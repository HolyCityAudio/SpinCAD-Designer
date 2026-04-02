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

        // === Sin/Cos LFO ===
        plotOscillator("sincos-lfo", "Sin/Cos LFO",
            () -> {
                SinCosLFOACADBlock b = new SinCosLFOACADBlock(100, 100);
                b.setLFORate(20);
                b.setLFOWidth(4096);
                return b;
            },
            new String[]{"Sine", "Cosine"}, docsDir);

        // === Ramp LFO ===
        plotOscillator("ramp-lfo", "Ramp LFO",
            () -> {
                RampLFOCADBlock b = new RampLFOCADBlock(100, 100);
                b.setLFORate(20);
                b.setLFOWidth(2048);
                return b;
            },
            new String[]{"Ramp LFO", "Triangle LFO"}, docsDir);

        // === Oscillator (Sin/Cos) ===
        plotOscillator("oscillator", "Oscillator",
            () -> {
                OscillatorCADBlock b = new OscillatorCADBlock(100, 100);
                b.setLFO(20);
                return b;
            },
            new String[]{"Sine Out", "Cosine Out"}, docsDir);

        // === New Oscillator ===
        plotOscillator("new-oscillator", "New Oscillator",
            () -> {
                New_OscillatorCADBlock b = new New_OscillatorCADBlock(100, 100);
                return b;
            },
            new String[]{"Sine Output", "Square Output"}, docsDir);

        // === LFO Value ===
        plotOscillator("lfo-value", "LFO Value",
            () -> {
                LFO_ValueCADBlock b = new LFO_ValueCADBlock(100, 100);
                return b;
            },
            new String[]{"Output"}, docsDir);

        System.out.println("\nAll oscillator PNGs written to docs/");
    }

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(); }

    private void plotOscillator(String fileBase, String title,
            BlockFactory factory, String[] outputPinNames, File docsDir) throws Exception {

        SpinCADBlock block = factory.create();
        File silentWav = generateSilentWav(SIM_DURATION);

        // Build model manually to wire control outputs to audio DAC
        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);
        model.addBlock(inputBlock);
        model.addBlock(block);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        // Wire first output pin to DAC L
        SpinCADPin outPin1 = block.getPin(outputPinNames[0]);
        if (outPin1 == null) {
            System.err.println("  SKIP " + title + ": output pin '" + outputPinNames[0] + "' not found");
            return;
        }
        outputBlock.getPin("Input 1").setConnection(block, outPin1);

        // Wire second output pin to DAC R (or same as L if only one)
        if (outputPinNames.length > 1) {
            SpinCADPin outPin2 = block.getPin(outputPinNames[1]);
            if (outPin2 != null) {
                outputBlock.getPin("Input 2").setConnection(block, outPin2);
            } else {
                outputBlock.getPin("Input 2").setConnection(block, outPin1);
            }
        } else {
            outputBlock.getPin("Input 2").setConnection(block, outPin1);
        }

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) {
            System.err.println("  SKIP " + title + ": code generation failed");
            return;
        }

        String listing = renderBlock.getProgramListing(1);
        System.out.println("  " + title + " listing:\n" + listing);

        File outFile = new File(tempDir, "osc_" + System.nanoTime() + ".wav");
        org.andrewkilpatrick.elmGen.simulator.SpinSimulator sim =
            new org.andrewkilpatrick.elmGen.simulator.SpinSimulator(renderBlock,
                silentWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) {
            System.err.println("  SKIP " + title + ": simulation timeout or error");
            return;
        }
        if (!outFile.exists()) {
            System.err.println("  SKIP " + title + ": output file not found");
            return;
        }

        short[] stereo = readWavSamples(outFile);
        short[] left = extractChannel(stereo, 0);
        short[] right = extractChannel(stereo, 1);
        double[] leftD = toDouble(left);
        double[] rightD = toDouble(right);

        // Show a portion of the waveform (skip initial settling)
        int skip = Math.min(SAMPLE_RATE / 10, leftD.length / 4);
        int displayLen = Math.min(leftD.length - skip, SAMPLE_RATE / 5); // ~200ms
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
}
