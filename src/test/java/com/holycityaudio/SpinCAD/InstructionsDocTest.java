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
 * - ABSA, Half Wave: show effect on a full sine wave (time-domain)
 * - Exp: DC sweep over useful range per Spin ASM manual
 * - Log: DC sweep with log-scaled x-axis
 * - Root: DC sweep 0-1 with note about behavior at 0
 * - Maximum: two sine waves at different frequencies, output shows max
 */
public class InstructionsDocTest {

    @TempDir
    File tempDir;

    private static final int NUM_POINTS = 201;
    private static final double SIM_DURATION = 0.25;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateInstructionPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        plotAbsa(docsDir);
        plotHalfWave(docsDir);
        plotExp(docsDir);
        plotLog(docsDir);
        plotRoot(docsDir);
        plotMaxx(docsDir);

        System.out.println("\nAll instruction PNGs written to docs/images/");
    }

    // ================ ABSA — show effect on -1 to +1 sine wave ================

    private void plotAbsa(File docsDir) throws IOException {
        // Mathematical plot: 2 cycles of sine wave and its absolute value
        int pts = 400;
        double cycles = 2.0;
        double[] timeMs = new double[pts];
        double[] sine = new double[pts];
        double[] absa = new double[pts];

        double periodMs = 10.0 / cycles;  // 10 ms total
        for (int i = 0; i < pts; i++) {
            timeMs[i] = 10.0 * i / (pts - 1);
            sine[i] = Math.sin(2 * Math.PI * cycles * i / (pts - 1));
            absa[i] = Math.abs(sine[i]);
        }

        writePlot(new File(docsDir, "instructions-absa.png"),
            "Absolute Value — Effect on Sine Wave", "Time (ms)", "Amplitude",
            0, 10.0, -1.0, 1.0,
            timeMs, new double[][]{sine, absa},
            new String[]{"Input Sine", "|Input|"},
            new String[]{"#aaaaaa", COLORS[0]});
        System.out.println("  wrote instructions-absa.png");
    }

    // ================ Half Wave — show effect on -1 to +1 sine wave ================

    private void plotHalfWave(File docsDir) throws IOException {
        int pts = 400;
        double cycles = 2.0;
        double[] timeMs = new double[pts];
        double[] sine = new double[pts];
        double[] halfWave = new double[pts];

        for (int i = 0; i < pts; i++) {
            timeMs[i] = 10.0 * i / (pts - 1);
            sine[i] = Math.sin(2 * Math.PI * cycles * i / (pts - 1));
            halfWave[i] = Math.max(0, sine[i]);
        }

        writePlot(new File(docsDir, "instructions-halfwave.png"),
            "Half Wave Rectifier — Effect on Sine Wave", "Time (ms)", "Amplitude",
            0, 10.0, -1.0, 1.0,
            timeMs, new double[][]{sine, halfWave},
            new String[]{"Input Sine", "max(0, Input)"},
            new String[]{"#aaaaaa", COLORS[0]});
        System.out.println("  wrote instructions-halfwave.png");
    }

    // ================ Exp — DC sweep over useful input range ================
    // FV-1 EXP instruction: output = C * 2^(ACC * 16) + D
    // Default: C=0.5, D=-0.5
    // At ACC=0: 0.5 * 2^0 - 0.5 = 0
    // At ACC=-0.0625 (= -1/16): 0.5 * 2^(-1) - 0.5 = -0.25
    // At ACC=+0.0625 (= +1/16): 0.5 * 2^(1) - 0.5 = 0.5
    // Useful range: roughly -0.1 to +0.06 before output clips

    private void plotExp(File docsDir) throws Exception {
        // Show three curves with different C,D settings
        // Curve 1: Default C=0.5, D=-0.5 (standard pot-to-frequency mapping)
        // Curve 2: C=1.0, D=0  (pure exponential)
        // Curve 3: C=0.5, D=0  (half-scale exponential)

        // Sweep input from -0.1 to +0.06 (where the interesting behavior is)
        double xMin = -0.10;
        double xMax = 0.06;
        double[] xData = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            xData[i] = xMin + (xMax - xMin) * i / (NUM_POINTS - 1);
        }

        // Compute mathematically (the simulation DC sweep is too coarse)
        double[] curve1 = new double[NUM_POINTS]; // C=0.5, D=-0.5
        double[] curve2 = new double[NUM_POINTS]; // C=1.0, D=0
        double[] curve3 = new double[NUM_POINTS]; // C=0.5, D=0

        for (int i = 0; i < NUM_POINTS; i++) {
            double x = xData[i];
            double exp16 = Math.pow(2, x * 16);
            curve1[i] = Math.max(-1, Math.min(1, 0.5 * exp16 - 0.5));
            curve2[i] = Math.max(-1, Math.min(1, 1.0 * exp16 + 0));
            curve3[i] = Math.max(-1, Math.min(1, 0.5 * exp16 + 0));
        }

        writePlot(new File(docsDir, "instructions-exp.png"),
            "EXP Instruction — C * 2^(x*16) + D", "Input", "Output",
            xMin, xMax, -1.0, 1.0,
            xData, new double[][]{curve1, curve2, curve3},
            new String[]{"C=0.5 D=-0.5 (default)", "C=1.0 D=0", "C=0.5 D=0"},
            new String[]{COLORS[0], COLORS[1], COLORS[2]});
        System.out.println("  wrote instructions-exp.png");
    }

    // ================ Log — sweep with log-scaled x-axis ================
    // FV-1 LOG: output = C * log2(|ACC|) / 16 + D
    // Default: C=0.5, D=0.5/16=0.03125
    // Show log response from small values to ~1.0

    private void plotLog(File docsDir) throws Exception {
        // Log-spaced x-axis from 0.001 to 1.0
        double[] xData = new double[NUM_POINTS];
        double logMin = Math.log10(0.001);
        double logMax = Math.log10(1.0);
        for (int i = 0; i < NUM_POINTS; i++) {
            double logVal = logMin + (logMax - logMin) * i / (NUM_POINTS - 1);
            xData[i] = Math.pow(10, logVal);
        }

        // Compute LOG instruction output mathematically
        // LOG: output = C * log2(|x|) / 16 + D
        double C = 0.5;
        double D = 0.5 / 16.0;
        double[] logOut = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            double x = xData[i];
            double log2x = Math.log(Math.abs(x)) / Math.log(2);
            logOut[i] = Math.max(-1, Math.min(1, C * log2x / 16.0 + D));
        }

        // For log-scale x-axis, plot against log10(x) and relabel
        double[] xPlot = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            xPlot[i] = Math.log10(xData[i]);
        }

        writePlot(new File(docsDir, "instructions-log.png"),
            "LOG Instruction — C=0.5 D=0.5/16", "Input (log scale)", "Output",
            logMin, logMax, -0.5, 0.1,
            xPlot, new double[][]{logOut},
            new String[]{"C*log2(|x|)/16 + D"},
            new String[]{COLORS[0]});
        System.out.println("  wrote instructions-log.png");
    }

    // ================ Root — DC sweep 0 to 1, note behavior near 0 ================
    // Root uses LOG/EXP pair: log(x)/N then exp
    // At x=0, log(0) = -infinity → saturates to -1 → exp clips

    private void plotRoot(File docsDir) throws Exception {
        // Sweep from small positive values to 1.0
        // Show sqrt (N=2), cbrt (N=3), and 4th root (N=4)
        double[] xData = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            xData[i] = (double) i / (NUM_POINTS - 1);
        }

        // Simulate sqrt via the actual block
        double[] sqrtSim = sweepBlock(
            () -> new RootCADBlock(100, 100),
            "Control Input 1", "Control Output 1",
            0.0, 1.0);

        // Mathematical reference curves
        double[] sqrtRef = new double[NUM_POINTS];
        double[] cbrtRef = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            double x = xData[i];
            sqrtRef[i] = Math.sqrt(x);
            cbrtRef[i] = Math.cbrt(x);
        }

        writePlot(new File(docsDir, "instructions-root.png"),
            "Root Instruction", "Input", "Output",
            0, 1.0, 0, 1.0,
            xData, new double[][]{sqrtSim, sqrtRef, cbrtRef},
            new String[]{"FV-1 sqrt (simulated)", "sqrt(x) reference", "cbrt(x) reference"},
            new String[]{COLORS[0], "#aaaaaa", COLORS[2]});
        System.out.println("  wrote instructions-root.png");
    }

    // ================ Maximum — show MAXX behavior on sine wave ================
    // MAXX compares |accumulator| vs |register * gain|, keeps larger absolute value.
    // Show a sine wave and a fixed threshold — output shows the envelope-like behavior.

    private void plotMaxx(File docsDir) throws IOException {
        int pts = 400;
        double cycles = 2.0;
        double[] timeMs = new double[pts];
        double[] sine = new double[pts];
        double[] ref = new double[pts];
        double[] maxxOut = new double[pts];

        for (int i = 0; i < pts; i++) {
            timeMs[i] = 10.0 * i / (pts - 1);
            sine[i] = 0.9 * Math.sin(2 * Math.PI * cycles * i / (pts - 1));
            ref[i] = 0.4;
            // MAXX keeps the larger of |accumulator| and |register*gain|
            maxxOut[i] = Math.max(Math.abs(sine[i]), 0.4);
        }

        writePlot(new File(docsDir, "instructions-maximum.png"),
            "MAXX — max(|Input 1|, |Input 2|)", "Time (ms)", "Amplitude",
            0, 10.0, -1.0, 1.0,
            timeMs, new double[][]{sine, maxxOut, ref},
            new String[]{"Input 1 (sine)", "MAXX output", "Input 2 (0.4)"},
            new String[]{"#aaaaaa", COLORS[0], COLORS[1]});
        System.out.println("  wrote instructions-maximum.png");
    }

    // ================ Helpers ================

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(); }

    /** Sweep a block over [min, max] range using DC simulation. */
    private double[] sweepBlock(BlockFactory factory, String inputPin, String outputPin,
            double min, double max) {
        double[] out = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            double frac = (double) i / (NUM_POINTS - 1);
            int val = (int) Math.round(frac * 999);
            SpinCADBlock block = factory.create();
            double[] r = simulateControlDC(block, Map.of(inputPin, val), outputPin);
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
