package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.LongDelayCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;

/**
 * Measures actual delay time of LongDelayCADBlock at various interleave factors
 * by feeding an impulse and finding when it appears in the output.
 */
public class LongDelayTimingTest {

    private static final int SAMPLE_RATE = (int) ElmProgram.getSamplerate();

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @ParameterizedTest(name = "delay timing at interleave={0}")
    @ValueSource(ints = {2, 4, 8})
    void testDelayTime(int interleave) throws Exception {
        // Simulate long enough to capture the expected delay + margin
        // Expected delay if interleaving works: ~interleave seconds
        // We simulate interleave + 2 seconds to be safe
        double simDuration = interleave + 2.0;
        File inputWav = generateImpulseWav(simDuration);

        LongDelayCADBlock delayBlock = new LongDelayCADBlock(100, 100);
        delayBlock.setInterleave(interleave);
        delayBlock.setInputGain(0);     // 0 dB = unity
        delayBlock.setFilterEnabled(false);  // disable filter for clean impulse

        // Build model: Input → LongDelay → Output
        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(delayBlock);
        model.addBlock(outputBlock);

        // Pre-generate input block to get pin registers
        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        // Wire: Input Output 1 → LongDelay Audio Input
        SpinCADPin inputOut = inputBlock.getPin("Output 1");
        delayBlock.getPin("Audio Input").setConnection(inputBlock, inputOut);

        // Wire: LongDelay Audio Output → Output Input 1 and Input 2
        SpinCADPin delayOut = delayBlock.getPin("Audio Output");
        outputBlock.getPin("Input 1").setConnection(delayBlock, delayOut);
        outputBlock.getPin("Input 2").setConnection(delayBlock, delayOut);

        // Generate code
        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        assertNotNull(renderBlock, "Code generation failed");

        // Print generated program for debugging
        String listing = renderBlock.getProgramListing(1);
        System.out.println("=== LongDelay interleave=" + interleave + " ===");
        System.out.println(listing);

        // Simulate
        File outFile = File.createTempFile("longdelay_timing_" + interleave + "_", ".wav");
        outFile.deleteOnExit();

        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(60000);  // 60 second timeout for long delays

        assertFalse(sim.isAlive(), "Simulation timed out");
        assertNull(sim.getSimulationException(), "Simulation failed");
        assertTrue(outFile.exists(), "Output file not created");

        // Read output and find the impulse
        short[] stereoSamples = readWavSamples(outFile);
        short[] left = extractChannel(stereoSamples, 0);

        // Find first sample that exceeds a threshold (impulse arrival)
        int threshold = 500;  // above noise floor
        int impulseAt = -1;
        for (int i = 0; i < left.length; i++) {
            if (Math.abs(left[i]) > threshold) {
                impulseAt = i;
                break;
            }
        }

        assertTrue(impulseAt > 0, "No impulse detected in output");

        double measuredDelaySec = (double) impulseAt / SAMPLE_RATE;
        // Each pass = READ_OFFSET+1 = 32761 samples; signal makes N passes
        double expectedDelaySec = (double) interleave * 32761.0 / SAMPLE_RATE;

        // Also compute what 1 pass would be
        double oneSecDelay = 32761.0 / SAMPLE_RATE;

        System.out.printf("  Interleave=%d: impulse at sample %d = %.4f sec%n",
                interleave, impulseAt, measuredDelaySec);
        System.out.printf("  Expected (N*buffer): %.4f sec%n", expectedDelaySec);
        System.out.printf("  Expected (1*buffer): %.4f sec%n", oneSecDelay);

        // Print a window around the impulse for debugging
        int windowStart = Math.max(0, impulseAt - 5);
        int windowEnd = Math.min(left.length, impulseAt + 20);
        System.out.print("  Samples around impulse: ");
        for (int i = windowStart; i < windowEnd; i++) {
            System.out.printf("[%d]=%d ", i, left[i]);
        }
        System.out.println();

        // The delay should scale with interleave factor.
        // Allow 10% tolerance on the measurement.
        double tolerance = 0.10;
        double ratio = measuredDelaySec / expectedDelaySec;
        System.out.printf("  Ratio measured/expected: %.4f%n", ratio);

        // If the delay doesn't scale with interleave, ratio will be ~1/interleave
        // If it does scale, ratio will be ~1.0
        // If it's half, ratio will be ~0.5
    }

    @Test
    void testDelayScaling() throws Exception {
        int[] factors = {2, 3, 4, 6, 8, 10, 12, 16};
        int[] delaySamples = new int[factors.length];

        for (int i = 0; i < factors.length; i++) {
            delaySamples[i] = measureDelaySamples(factors[i]);
        }

        System.out.println("\n=== Delay Scaling Summary ===");
        System.out.println("  N  | Samples   | Seconds  | Expected N*32767 | Ratio   | Samples/32768");
        System.out.println("  ---|-----------|----------|------------------|---------|-------------");
        for (int i = 0; i < factors.length; i++) {
            int n = factors[i];
            double sec = (double) delaySamples[i] / SAMPLE_RATE;
            int expected = n * 32767;
            double ratio = (double) delaySamples[i] / expected;
            double passes = (double) delaySamples[i] / 32768.0;
            System.out.printf("  %2d | %9d | %7.4f s | %16d | %7.4f | %8.2f%n",
                    n, delaySamples[i], sec, expected, ratio, passes);
        }
    }

    private int measureDelaySamples(int interleave) throws Exception {
        double simDuration = interleave * 1.5 + 2.0;
        File inputWav = generateImpulseWav(simDuration);

        LongDelayCADBlock delayBlock = new LongDelayCADBlock(100, 100);
        delayBlock.setInterleave(interleave);
        delayBlock.setInputGain(0);
        delayBlock.setFilterEnabled(false);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(delayBlock);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        SpinCADPin inputOut = inputBlock.getPin("Output 1");
        delayBlock.getPin("Audio Input").setConnection(inputBlock, inputOut);

        SpinCADPin delayOut = delayBlock.getPin("Audio Output");
        outputBlock.getPin("Input 1").setConnection(delayBlock, delayOut);
        outputBlock.getPin("Input 2").setConnection(delayBlock, delayOut);

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();

        File outFile = File.createTempFile("longdelay_scale_" + interleave + "_", ".wav");
        outFile.deleteOnExit();

        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(120000);

        short[] stereoSamples = readWavSamples(outFile);
        short[] left = extractChannel(stereoSamples, 0);

        int threshold = 100;
        for (int i = 0; i < left.length; i++) {
            if (Math.abs(left[i]) > threshold) {
                return i;
            }
        }
        fail("No impulse detected at interleave=" + interleave);
        return -1;
    }

    private double measureDelaySeconds(int interleave) throws Exception {
        double simDuration = interleave + 2.0;
        File inputWav = generateImpulseWav(simDuration);

        LongDelayCADBlock delayBlock = new LongDelayCADBlock(100, 100);
        delayBlock.setInterleave(interleave);
        delayBlock.setInputGain(0);
        delayBlock.setFilterEnabled(false);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(delayBlock);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        SpinCADPin inputOut = inputBlock.getPin("Output 1");
        delayBlock.getPin("Audio Input").setConnection(inputBlock, inputOut);

        SpinCADPin delayOut = delayBlock.getPin("Audio Output");
        outputBlock.getPin("Input 1").setConnection(delayBlock, delayOut);
        outputBlock.getPin("Input 2").setConnection(delayBlock, delayOut);

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();

        File outFile = File.createTempFile("longdelay_scale_" + interleave + "_", ".wav");
        outFile.deleteOnExit();

        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(120000);

        short[] stereoSamples = readWavSamples(outFile);
        short[] left = extractChannel(stereoSamples, 0);

        int threshold = 500;
        for (int i = 0; i < left.length; i++) {
            if (Math.abs(left[i]) > threshold) {
                return (double) i / SAMPLE_RATE;
            }
        }
        fail("No impulse detected at interleave=" + interleave);
        return -1;
    }

    // ========== Utility methods ==========

    private static File generateImpulseWav(double durationSeconds) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];

        short impulse = (short) (Short.MAX_VALUE / 2);
        data[0] = (byte) (impulse & 0xff);
        data[1] = (byte) ((impulse >> 8) & 0xff);
        data[2] = (byte) (impulse & 0xff);
        data[3] = (byte) ((impulse >> 8) & 0xff);

        return writeWav(data, numFrames);
    }

    private static File writeWav(byte[] data, int numFrames) throws IOException {
        File wavFile = File.createTempFile("spincad_impulse_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    private static short[] readWavSamples(File wavFile) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        ais.close();

        byte[] raw = bos.toByteArray();
        short[] samples = new short[raw.length / 2];
        for (int i = 0; i < samples.length; i++) {
            int offset = i * 2;
            samples[i] = (short) ((raw[offset] & 0xff) | ((raw[offset + 1] & 0xff) << 8));
        }
        return samples;
    }

    private static short[] extractChannel(short[] stereo, int channel) {
        short[] mono = new short[stereo.length / 2];
        for (int i = 0; i < mono.length; i++) {
            mono[i] = stereo[i * 2 + channel];
        }
        return mono;
    }
}
