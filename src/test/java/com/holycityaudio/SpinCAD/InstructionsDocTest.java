package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Instructions menu blocks.
 * These are math/utility blocks that operate on control or audio signals.
 * Shows DC transfer functions (input vs output) similar to control block plots.
 */
public class InstructionsDocTest {

    @TempDir
    File tempDir;

    private static final int NUM_POINTS = 51;
    private static final double SIM_DURATION = 0.25;
    private static final double[] SWEEP_X = new double[NUM_POINTS];
    private static final int[] SWEEP_VALS = new int[NUM_POINTS];

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
        for (int i = 0; i < NUM_POINTS; i++) {
            SWEEP_X[i] = i / (double)(NUM_POINTS - 1);
            SWEEP_VALS[i] = (int) Math.round(SWEEP_X[i] * 999);
        }
    }

    @Test
    void generateInstructionPlots() throws Exception {
        File docsDir = new File("docs");
        docsDir.mkdirs();

        // === Absolute Value ===
        double[] absOut = sweepBlock(
            () -> new AbsaCADBlock(100, 100),
            "Input", "Output");
        writePlot01(new File(docsDir, "instructions-absa.png"),
            "Absolute Value", "Input", "Output", -1, 1,
            SWEEP_X, new double[][]{absOut},
            new String[]{"abs(x)"}, new String[]{COLORS[0]});

        // === Half Wave ===
        double[] hwOut = sweepBlock(
            () -> new Half_WaveCADBlock(100, 100),
            "Input", "Output");
        writePlot01(new File(docsDir, "instructions-halfwave.png"),
            "Half Wave", "Input", "Output", 0, 1,
            SWEEP_X, new double[][]{hwOut},
            new String[]{"max(0, x)"}, new String[]{COLORS[0]});

        // === Exp ===
        double[] expOut = sweepBlock(
            () -> new ExpCADBlock(100, 100),
            "Input", "Exp Output");
        writePlot01(new File(docsDir, "instructions-exp.png"),
            "Exp", "Input", "Output", 0, 1,
            SWEEP_X, new double[][]{expOut},
            new String[]{"exp(x)"}, new String[]{COLORS[0]});

        // === Log ===
        double[] logOut = sweepBlock(
            () -> new LogCADBlock(100, 100),
            "Control Input", "Log Output");
        writePlot01(new File(docsDir, "instructions-log.png"),
            "Log", "Input", "Output", 0, 1,
            SWEEP_X, new double[][]{logOut},
            new String[]{"log(x)"}, new String[]{COLORS[0]});

        // === Root ===
        double[] rootOut = sweepBlock(
            () -> new RootCADBlock(100, 100),
            "Control Input 1", "Control Output 1");
        writePlot01(new File(docsDir, "instructions-root.png"),
            "Root", "Input", "Output", 0, 1,
            SWEEP_X, new double[][]{rootOut},
            new String[]{"sqrt(x)"}, new String[]{COLORS[0]});

        // === Maximum ===
        // Sweep two inputs: one fixed at 0.5, one sweeping 0-1
        double[] maxOut = sweepMax();
        double[] ref = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) ref[i] = 0.5;
        writePlot01(new File(docsDir, "instructions-maximum.png"),
            "Maximum (Input 2 = 0.5)", "Input 1", "Output", 0, 1,
            SWEEP_X, new double[][]{maxOut, ref},
            new String[]{"max(in1, 0.5)", "Reference (0.5)"},
            new String[]{COLORS[0], "#aaa"});

        System.out.println("\nAll instruction PNGs written to docs/");
    }

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(); }

    private double[] sweepBlock(BlockFactory factory, String inputPin, String outputPin) {
        double[] out = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            SpinCADBlock block = factory.create();
            double[] r = simulateControlDC(block, Map.of(inputPin, SWEEP_VALS[i]), outputPin);
            out[i] = r != null ? r[0] : Double.NaN;
        }
        return out;
    }

    private double[] sweepMax() {
        double[] out = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            SpinCADBlock block = new maxxCADBlock(100, 100);
            Map<String, Integer> inputs = new HashMap<>();
            inputs.put("Input 1", SWEEP_VALS[i]);
            inputs.put("Input 2", 500);
            double[] r = simulateControlDC(block, inputs, "Output");
            out[i] = r != null ? r[0] : Double.NaN;
        }
        return out;
    }

    private double[] simulateControlDC(SpinCADBlock block,
            Map<String, Integer> controlInputs, String outputPinName) {
        try {
            short[] stereo = simulate(block,
                generateSilentWav(SIM_DURATION), controlInputs,
                outputPinName, null, tempDir);
            if (stereo == null) return null;
            short[] left = extractChannel(stereo, 0);
            int start = left.length / 2;
            double sum = 0;
            for (int i = start; i < left.length; i++) sum += left[i];
            double dc = sum / (left.length - start) / 32767.0;
            return new double[]{dc};
        } catch (Exception e) {
            System.err.println("  Sim error: " + e.getMessage());
            return null;
        }
    }
}
