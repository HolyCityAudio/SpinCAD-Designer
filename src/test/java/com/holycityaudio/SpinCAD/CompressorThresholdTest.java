package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;
import com.holycityaudio.SpinCAD.CADBlocks.var_slope_compressorCADBlock;

/**
 * Diagnostic test for compressor threshold behavior.
 * Measures gain during silence and at various input levels
 * for different threshold settings.
 */
public class CompressorThresholdTest {

    @TempDir
    File tempDir;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void testThresholdBehavior() throws Exception {
        // Threshold in dB (converted to Z = threshDb/100 internally)
        double[] thresholds = {-5.0, -10.0, -15.0, -20.0, -25.0, -35.0, -50.0};
        double[] inputDbLevels = {-40.0, -30.0, -24.0, -18.0, -12.0, -6.0, 0.0};
        double ratio = 4.0;
        double knee = 0.0625;

        System.out.println("\n=== Compressor Threshold Diagnostic (direct Z coefficient) ===");
        System.out.println("Ratio: " + ratio + ":1, Knee: " + knee);

        double oneMinusInv = 1.0 - 1.0 / ratio;
        double slope = -0.5 * oneMinusInv;
        System.out.println("slope = " + slope);
        System.out.println();

        // Show gain during silence for each threshold
        System.out.println("--- Gain during silence for each threshold ---");
        for (double threshDb : thresholds) {
            double z = threshDb / 100.0;
            double logSilence = slope * (Math.log(knee) / Math.log(2.0)) / 16.0 + z;
            double gainSilence;
            if (logSilence >= 0) {
                gainSilence = 1.0;
            } else {
                gainSilence = Math.pow(2.0, logSilence * 16.0);
            }
            double gainDb = 20.0 * Math.log10(gainSilence);
            System.out.printf("thresh=%4.0f dB  Z=%+.2f  silence_gain=%.4f  (%.1f dB)%n",
                    threshDb, z, gainSilence, gainDb);
        }
        System.out.println();

        // Now run actual simulator
        System.out.println("--- Simulator measurements ---");
        System.out.printf("%-10s", "ThreshDb");
        for (double inputDb : inputDbLevels) {
            System.out.printf("  %6.0f dB", inputDb);
        }
        System.out.println("  | silence");
        System.out.println("-".repeat(10 + inputDbLevels.length * 10 + 12));

        for (double threshDb : thresholds) {
            System.out.printf("%-10.0f", threshDb);

            for (double inputDb : inputDbLevels) {
                double amplitude = Math.pow(10.0, inputDb / 20.0);
                double outputRms = measureOutput(ratio, threshDb, knee, amplitude, 0.5);
                double outputDb = 20.0 * Math.log10(outputRms + 1e-15);
                System.out.printf("  %6.1f dB", outputDb);
            }

            // Measure silence
            double silenceRms = measureOutput(ratio, threshDb, knee, 0.0, 0.5);
            double silenceDb = 20.0 * Math.log10(silenceRms + 1e-15);
            System.out.printf("  | %6.1f dB", silenceDb);

            System.out.println();
        }

        // Also print gain (output/input ratio in dB) to see compression curve
        System.out.println();
        System.out.println("--- Gain (output - input) in dB ---");
        System.out.printf("%-10s", "ThreshDb");
        for (double inputDb : inputDbLevels) {
            System.out.printf("  %6.0f dB", inputDb);
        }
        System.out.println();
        System.out.println("-".repeat(10 + inputDbLevels.length * 10));

        for (double threshDb : thresholds) {
            System.out.printf("%-10.0f", threshDb);

            for (double inputDb : inputDbLevels) {
                double amplitude = Math.pow(10.0, inputDb / 20.0);
                double outputRms = measureOutput(ratio, threshDb, knee, amplitude, 0.5);
                double outputDb = 20.0 * Math.log10(outputRms + 1e-15);
                double gainDb = outputDb - inputDb;
                System.out.printf("  %+6.1f dB", gainDb);
            }
            System.out.println();
        }

        // Verify: during silence, output should be near zero
        // (gain < 1.0 is expected with app note approach, but silence * gain = 0)
        System.out.println();
        for (double threshDb : thresholds) {
            double silenceRms = measureOutput(ratio, threshDb, knee, 0.0, 0.5);
            assertTrue(silenceRms < 0.001,
                    "Silence output should be near zero at thresh=" + threshDb + " dB"
                    + " but got RMS=" + silenceRms);
        }
    }

    private double measureOutput(double ratio, double threshDb, double knee,
            double inputAmplitude, double durationSec) throws Exception {

        var_slope_compressorCADBlock comp = new var_slope_compressorCADBlock(100, 100);
        comp.setratio(ratio);
        comp.setthreshDb(threshDb);
        comp.setmakeupDb(0.0);
        comp.settrim(1.0);
        comp.setinGain(1.0);
        comp.setknee(knee);
        comp.setavgTime(0.013333);
        comp.setrelTime(0.000447);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(comp);
        model.addBlock(outputBlock);

        // Generate code for input block to assign output registers
        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        // Wire audio input
        SpinCADPin inputL = inputBlock.getPin("Output 1");
        for (int p = 0; p < comp.pinList.size(); p++) {
            SpinCADPin pin = comp.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_IN) {
                pin.setConnection(inputBlock, inputL);
                break;
            }
        }

        // Wire audio output
        List<SpinCADPin> outputPins = new ArrayList<>();
        for (int p = 0; p < comp.pinList.size(); p++) {
            SpinCADPin pin = comp.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_OUT) {
                outputPins.add(pin);
            }
        }
        assertFalse(outputPins.isEmpty(), "No audio output pins");
        outputBlock.getPin("Input 1").setConnection(comp, outputPins.get(0));
        outputBlock.getPin("Input 2").setConnection(comp, outputPins.get(0));

        // Generate
        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        assertNotNull(renderBlock, "Render block null");

        // Create input WAV
        File inputWav = generateTestWav(durationSec, inputAmplitude);
        String tag = String.format("thresh%.0f_in%.0f", threshDb, inputAmplitude * 1000);
        File outFile = new File(tempDir, tag + ".wav");

        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(30000);

        assertFalse(sim.isAlive(), "Simulator timed out");
        if (sim.getSimulationException() != null) {
            throw new RuntimeException("Simulator error", sim.getSimulationException());
        }
        assertTrue(outFile.exists(), "No output WAV");

        // Read output, measure RMS of last quarter
        AudioInputStream ais = AudioSystem.getAudioInputStream(outFile);
        byte[] data = readAllBytes(ais);
        ais.close();

        int bytesPerFrame = 4;
        int totalFrames = data.length / bytesPerFrame;
        int startFrame = totalFrames * 3 / 4;  // last quarter only
        double sumSq = 0;
        int count = 0;
        for (int f = startFrame; f < totalFrames; f++) {
            int offset = f * bytesPerFrame;
            if (offset + 1 >= data.length) break;
            short sample = (short) ((data[offset] & 0xff)
                    | ((data[offset + 1] & 0xff) << 8));
            double normalized = sample / 32768.0;
            sumSq += normalized * normalized;
            count++;
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    private static File generateTestWav(double durationSeconds, double amplitude)
            throws IOException {
        int sampleRate = ElmProgram.SAMPLERATE;
        int numFrames = (int) (sampleRate * durationSeconds);
        byte[] data = new byte[numFrames * 4];

        double freq = 1000.0;
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / sampleRate;
            short sample = (short) (Short.MAX_VALUE * amplitude
                    * Math.sin(2.0 * Math.PI * freq * t));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }

        File wavFile = File.createTempFile("compressor_thresh_test", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    private byte[] readAllBytes(AudioInputStream ais) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }
}
