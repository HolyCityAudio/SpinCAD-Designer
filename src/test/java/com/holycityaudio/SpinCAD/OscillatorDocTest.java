package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Oscillator menu blocks.
 * Each LFO/oscillator is simulated and its output waveform is captured.
 */
public class OscillatorDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateOscillatorPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // === Sin/Cos LFO (~4 Hz, 500ms display) ===
        plotOscillator("sincos-lfo", "Sin/Cos LFO",
            () -> {
                SinCosLFOACADBlock b = new SinCosLFOACADBlock(100, 100);
                b.setLFORate(100);   // ~4 Hz
                b.setLFOWidth(4096);
                return b;
            },
            new String[]{"Sine", "Cosine"}, 500, docsDir);

        // === Ramp LFO (~4 Hz, 500ms display) ===
        plotOscillator("ramp-lfo", "Ramp LFO",
            () -> {
                RampLFOCADBlock b = new RampLFOCADBlock(100, 100);
                b.setLFORate(8192);  // ~4 Hz
                b.setLFOWidth(3);    // width index 3 = 4096
                return b;
            },
            new String[]{"Ramp LFO", "Triangle LFO"}, 500, docsDir);

        // === Oscillator (~4 Hz, 500ms display) ===
        plotOscillator("oscillator", "Oscillator",
            () -> {
                OscillatorCADBlock b = new OscillatorCADBlock(100, 100);
                b.setLFO(0.000767); // ~4 Hz
                return b;
            },
            new String[]{"Sine Out", "Cosine Out"}, 500, docsDir);

        // === New Oscillator (default freq, auto-scale to 2 cycles) ===
        plotOscillator("new-oscillator", "New Oscillator",
            () -> {
                New_OscillatorCADBlock b = new New_OscillatorCADBlock(100, 100);
                return b;
            },
            new String[]{"Sine Output", "Square Output"}, 0, docsDir);

        System.out.println("\nAll oscillator PNGs written to docs/");
    }

    @Test
    void generateNewOscillatorFrequencyResponse() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // Sweep frequency control from 0 to ~1.0 in 10 steps
        double[] controlValues = {0.01, 0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5, 0.7, 0.9};
        double[] measuredFreqs = new double[controlValues.length];

        for (int i = 0; i < controlValues.length; i++) {
            New_OscillatorCADBlock block = new New_OscillatorCADBlock(100, 100);
            block.setfreqVar(controlValues[i]);

            double[] leftD = simulateOscillator(block, new String[]{"Sine Output", "Square Output"});
            if (leftD == null) {
                System.err.println("  SKIP freq response at " + controlValues[i]);
                measuredFreqs[i] = 0;
                continue;
            }

            // Measure frequency via zero-crossings (skip settling)
            int skip = SAMPLE_RATE / 10;
            measuredFreqs[i] = measureFrequencyHz(leftD, skip);
        }

        writePlot(new File(docsDir, "oscillator-new-oscillator-freq-response.png"),
            "New Oscillator Frequency Response", "Freq Control", "Frequency (Hz)",
            0, 1.0, 0, measuredFreqs[measuredFreqs.length - 1] * 1.1,
            controlValues, new double[][]{measuredFreqs},
            new String[]{"Measured Hz"},
            new String[]{COLORS[0]});

        System.out.println("\nNew Oscillator frequency response PNG written to docs/images/");
    }

    /** Measure frequency in Hz via zero-crossing detection. */
    private static double measureFrequencyHz(double[] audio, int skipSamples) {
        int crossings = 0;
        int start = Math.min(skipSamples, audio.length / 4);
        for (int i = start + 1; i < audio.length; i++) {
            if (audio[i - 1] <= 0 && audio[i] > 0) crossings++;
        }
        double durationSec = (double)(audio.length - start) / SAMPLE_RATE;
        return crossings / durationSec;
    }

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(); }

    /**
     * @param displayMs display window in ms; 0 = auto-detect 2 cycles from frequency
     */
    private void plotOscillator(String fileBase, String title,
            BlockFactory factory, String[] outputPinNames, int displayMs,
            File docsDir) throws Exception {

        SpinCADBlock block = factory.create();
        double[] leftD = simulateOscillator(block, outputPinNames);
        if (leftD == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }

        // Get right channel too
        // Re-simulate (block state may be consumed) — or extract from same sim
        // Actually, let's re-do with a shared sim method that returns both channels
        block = factory.create();
        double[][] channels = simulateOscillatorStereo(block, outputPinNames);
        if (channels == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }
        leftD = channels[0];
        double[] rightD = channels[1];

        int skip = Math.min(SAMPLE_RATE / 10, leftD.length / 4);
        int displayLen;

        if (displayMs > 0) {
            displayLen = Math.min((int)(displayMs / 1000.0 * SAMPLE_RATE), leftD.length - skip);
        } else {
            // Auto-detect: measure frequency, show 2 cycles
            double freqHz = measureFrequencyHz(leftD, skip);
            if (freqHz > 0) {
                displayLen = Math.min((int)(2.0 / freqHz * SAMPLE_RATE), leftD.length - skip);
            } else {
                displayLen = Math.min(leftD.length - skip, SAMPLE_RATE / 5);
            }
        }

        double[] leftSlice = Arrays.copyOfRange(leftD, skip, skip + displayLen);
        double[] rightSlice = Arrays.copyOfRange(rightD, skip, skip + displayLen);
        double[] timeMs = new double[displayLen];
        for (int i = 0; i < displayLen; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        if (outputPinNames.length > 1) {
            writePlot(new File(docsDir, "oscillator-" + fileBase + ".png"),
                title, "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1.0, 1.0,
                timeMs, new double[][]{leftSlice, rightSlice},
                outputPinNames,
                new String[]{COLORS[0], COLORS[1]});
        } else {
            writePlot(new File(docsDir, "oscillator-" + fileBase + ".png"),
                title, "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1.0, 1.0,
                timeMs, new double[][]{leftSlice},
                outputPinNames,
                new String[]{COLORS[0]});
        }
    }

    /** Simulate an oscillator block, return left channel audio as doubles. */
    private double[] simulateOscillator(SpinCADBlock block, String[] outputPinNames) throws Exception {
        double[][] channels = simulateOscillatorStereo(block, outputPinNames);
        return channels != null ? channels[0] : null;
    }

    /** Simulate an oscillator block, return [left, right] channel audio as doubles. */
    private double[][] simulateOscillatorStereo(SpinCADBlock block, String[] outputPinNames) throws Exception {
        File silentWav = generateSilentWav(SIM_DURATION);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);
        model.addBlock(inputBlock);
        model.addBlock(block);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        SpinCADPin outPin1 = block.getPin(outputPinNames[0]);
        if (outPin1 == null) return null;
        outputBlock.getPin("Input 1").setConnection(block, outPin1);

        if (outputPinNames.length > 1) {
            SpinCADPin outPin2 = block.getPin(outputPinNames[1]);
            if (outPin2 != null)
                outputBlock.getPin("Input 2").setConnection(block, outPin2);
            else
                outputBlock.getPin("Input 2").setConnection(block, outPin1);
        } else {
            outputBlock.getPin("Input 2").setConnection(block, outPin1);
        }

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) return null;

        File outFile = new File(tempDir, "osc_" + System.nanoTime() + ".wav");
        org.andrewkilpatrick.elmGen.simulator.SpinSimulator sim =
            new org.andrewkilpatrick.elmGen.simulator.SpinSimulator(renderBlock,
                silentWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) return null;
        if (!outFile.exists()) return null;

        short[] stereo = readWavSamples(outFile);
        return new double[][]{
            toDouble(extractChannel(stereo, 0)),
            toDouble(extractChannel(stereo, 1))
        };
    }
}
