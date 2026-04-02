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
 * Measures output level of the Var_Slope_Compressor across different
 * threshold/ratio settings to evaluate auto-makeup gain effectiveness.
 *
 * "Before" levels are derived by dividing out the auto-makeup factor,
 * showing what the output would be with makeupGain=1.0.
 * "After" levels are the actual measured outputs with auto-makeup active.
 */
public class CompressorLevelTest {

    @TempDir
    File tempDir;

    private static File inputWav;

    @BeforeAll
    static void setup() throws IOException {
        System.setProperty("java.awt.headless", "true");
        inputWav = generateTestWav(0.5);  // 500ms of 1kHz sine at -6dBFS
    }

    /** Compute the auto-makeup value using the same Taylor approximation as the .spincad */
    private static double computeAutoMakeup(double ratio, double thresh) {
        double exponent = thresh - ratio * 0.0625;
        double xln2 = exponent * 0.6931;
        return 1.0 + xln2 + (xln2 * xln2) / 2.0;
    }

    @Test
    void testOutputLevelConsistency() throws Exception {
        double[] ratios = {1.0, 2.0, 4.0, 8.0};
        double[] thresholds = {0.05, 0.15, 0.25, 0.5};

        System.out.println("\n=== Compressor Output Level Test ===");
        System.out.println(String.format("%-8s %-10s %-12s %-12s %-12s",
                "Ratio", "Threshold", "Before(raw)", "After(auto)", "AutoMakeup"));
        System.out.println("--------------------------------------------------------------");

        List<Double> beforeLevels = new ArrayList<>();
        List<Double> afterLevels = new ArrayList<>();

        for (double ratio : ratios) {
            for (double thresh : thresholds) {
                double autoMakeup = computeAutoMakeup(ratio, thresh);

                // Measure with auto-makeup active (trim=1.0)
                double rmsAfter = measureOutputRms(ratio, thresh, 1.0);

                // Derive "before" by dividing out the auto-makeup
                double rmsBefore = rmsAfter / autoMakeup;

                beforeLevels.add(rmsBefore);
                afterLevels.add(rmsAfter);

                System.out.println(String.format("%-8.1f %-10.3f %-12.4f %-12.4f %-12.4f",
                        ratio, thresh, rmsBefore, rmsAfter, autoMakeup));
            }
        }

        // Compute spread (max - min in dB) for before and after
        double beforeMin = beforeLevels.stream().mapToDouble(d -> d).min().orElse(0);
        double beforeMax = beforeLevels.stream().mapToDouble(d -> d).max().orElse(0);
        double afterMin = afterLevels.stream().mapToDouble(d -> d).min().orElse(0);
        double afterMax = afterLevels.stream().mapToDouble(d -> d).max().orElse(0);

        double beforeSpreadDb = 20.0 * Math.log10(beforeMax / (beforeMin + 1e-10));
        double afterSpreadDb = 20.0 * Math.log10(afterMax / (afterMin + 1e-10));

        System.out.println("--------------------------------------------------------------");
        System.out.println(String.format("Before spread: %.1f dB  (min=%.4f, max=%.4f)",
                beforeSpreadDb, beforeMin, beforeMax));
        System.out.println(String.format("After spread:  %.1f dB  (min=%.4f, max=%.4f)",
                afterSpreadDb, afterMin, afterMax));
        System.out.println();

        // Auto-makeup should reduce the spread
        assertTrue(afterSpreadDb < beforeSpreadDb,
                "Auto-makeup should reduce output level variation. Before: "
                + String.format("%.1f", beforeSpreadDb)
                + " dB, After: " + String.format("%.1f", afterSpreadDb) + " dB");
    }

    /**
     * Build a model with Input → Var_Slope_Compressor → Output,
     * run the simulator, and return the output RMS level (0.0 to 1.0).
     */
    private double measureOutputRms(double ratio, double thresh, double trim)
            throws Exception {
        var_slope_compressorCADBlock comp = new var_slope_compressorCADBlock(100, 100);
        comp.setratio(ratio);
        comp.setthreshDb(thresh);
        comp.setmakeupDb(0.0);
        comp.settrim(trim);
        comp.setinGain(1.0);
        comp.setknee(0.0625);
        comp.setavgTime(0.001);
        comp.setrelTime(0.001);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(comp);
        model.addBlock(outputBlock);

        // Generate code for input to assign output registers
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

        // Wire audio output → Output block
        List<SpinCADPin> outputPins = new ArrayList<>();
        for (int p = 0; p < comp.pinList.size(); p++) {
            SpinCADPin pin = comp.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_OUT) {
                outputPins.add(pin);
            }
        }
        if (outputPins.isEmpty()) {
            fail("No audio output pins on compressor");
        }
        outputBlock.getPin("Input 1").setConnection(comp, outputPins.get(0));
        outputBlock.getPin("Input 2").setConnection(comp, outputPins.get(0));

        // Generate and simulate
        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        assertNotNull(renderBlock, "Render block should not be null");

        File outFile = new File(tempDir,
                String.format("comp_r%.0f_t%.0f_tr%.0f.wav",
                        ratio * 10, thresh * 100, trim * 100));
        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(30000);

        assertFalse(sim.isAlive(), "Simulator should have finished");
        assertNull(sim.getSimulationException(), "Simulator should not throw");
        assertTrue(outFile.exists(), "Output WAV should exist");

        // Read output and compute RMS of second half (skip transient)
        AudioInputStream ais = AudioSystem.getAudioInputStream(outFile);
        byte[] data = readAllBytes(ais);
        ais.close();

        // Data is stereo 16-bit: 4 bytes per frame (L,R)
        int bytesPerFrame = 4;
        int totalFrames = data.length / bytesPerFrame;
        int startFrame = totalFrames / 2;  // skip first half (transient settling)
        double sumSq = 0;
        int count = 0;
        for (int f = startFrame; f < totalFrames; f++) {
            int offset = f * bytesPerFrame;  // left channel
            if (offset + 1 >= data.length) break;
            short sample = (short) ((data[offset] & 0xff)
                    | ((data[offset + 1] & 0xff) << 8));
            double normalized = sample / 32768.0;
            sumSq += normalized * normalized;
            count++;
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    private static File generateTestWav(double durationSeconds) throws IOException {
        int sampleRate = ElmProgram.SAMPLERATE;
        int numFrames = (int) (sampleRate * durationSeconds);
        byte[] data = new byte[numFrames * 4]; // stereo 16-bit

        double freq = 1000.0;
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / sampleRate;
            short sample = (short) (Short.MAX_VALUE * 0.5 * Math.sin(2.0 * Math.PI * freq * t));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }

        File wavFile = File.createTempFile("compressor_level_test", ".wav");
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
