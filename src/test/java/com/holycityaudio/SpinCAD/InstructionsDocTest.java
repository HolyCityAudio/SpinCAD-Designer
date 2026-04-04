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
    // At ACC=0: output = C + D.  At ACC=1/16: output = 2C + D.
    // The curve grows very fast, so D must offset C to keep output in range.
    // For C=1: at x=0, output = 1+D, so D=-1 keeps output = 0 at x=0
    //          at x=1/16, output = 2+(-1) = 1 (just reaches full scale)

    private void plotExp(File docsDir) throws Exception {
        // Sweep input from -0.1 to +0.07 (where the interesting behavior is)
        double xMin = -0.10;
        double xMax = 0.07;
        double[] xData = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            xData[i] = xMin + (xMax - xMin) * i / (NUM_POINTS - 1);
        }

        // Curve 1: Default C=0.5, D=-0.5 (standard pot-to-frequency mapping)
        //   At x=0: 0.5-0.5=0.  At x=1/16: 1.0-0.5=0.5.  Stays in range.
        // Curve 2: C=1.0, D=-1.0 (full-range exponential, no clipping)
        //   At x=0: 1-1=0.  At x=1/16: 2-1=1.  Perfect 0-to-1 mapping.
        // Curve 3: C=1.0, D=0 (clips almost immediately)
        //   At x=0: 1+0=1 already at max!  Shows why D matters.
        // Curve 4: C=0.5, D=0 (half-scale, clips above x≈1/16)

        double[] curve1 = new double[NUM_POINTS];
        double[] curve2 = new double[NUM_POINTS];
        double[] curve3 = new double[NUM_POINTS];
        double[] curve4 = new double[NUM_POINTS];

        for (int i = 0; i < NUM_POINTS; i++) {
            double x = xData[i];
            double exp16 = Math.pow(2, x * 16);
            curve1[i] = Math.max(-1, Math.min(1, 0.5 * exp16 - 0.5));
            curve2[i] = Math.max(-1, Math.min(1, 1.0 * exp16 - 1.0));
            curve3[i] = Math.max(-1, Math.min(1, 1.0 * exp16 + 0));
            curve4[i] = Math.max(-1, Math.min(1, 0.5 * exp16 + 0));
        }

        writePlot2x2(new File(docsDir, "instructions-exp.png"),
            "EXP Instruction — C * 2^(x*16) + D", "Input", "Output",
            xMin, xMax, -1.0, 1.0,
            xData, new double[][]{curve1, curve2, curve3, curve4},
            new String[]{"C=0.5 D=-0.5 (default)", "C=1.0 D=-1.0 (full range)",
                          "C=1.0 D=0 (clips!)", "C=0.5 D=0"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]});
        System.out.println("  wrote instructions-exp.png");
    }

    // ================ Log — sweep with log-scaled x-axis ================
    // FV-1 LOG: output = C * log2(|ACC|) / 16 + D
    // C scales the slope (steepness of the log curve).
    // D shifts the output vertically (DC offset).
    // Show multiple C/D combinations to illustrate their effect.

    private void plotLog(File docsDir) throws Exception {
        // Log-spaced x-axis from 0.001 to 1.0
        double[] xData = new double[NUM_POINTS];
        double logMin = Math.log10(0.001);
        double logMax = Math.log10(1.0);
        for (int i = 0; i < NUM_POINTS; i++) {
            double logVal = logMin + (logMax - logMin) * i / (NUM_POINTS - 1);
            xData[i] = Math.pow(10, logVal);
        }

        // Curve 1: C=0.5, D=0.5/16 (default) — moderate slope, small offset
        // Curve 2: C=1.0, D=0.5/16 — steeper slope (C doubles sensitivity)
        // Curve 3: C=0.5, D=0.25 — same slope, shifted up (D adds DC offset)
        // Curve 4: C=-0.5, D=0.5/16 — inverted slope (C negative flips curve)

        double[][] curves = new double[4][NUM_POINTS];
        double[][] params = {
            {0.5,  0.5/16.0},   // default
            {1.0,  0.5/16.0},   // double C
            {0.5,  0.25},       // raised D
            {-0.5, 0.5/16.0},   // negative C
        };

        for (int i = 0; i < NUM_POINTS; i++) {
            double x = xData[i];
            double log2x = Math.log(Math.abs(x)) / Math.log(2);
            for (int c = 0; c < 4; c++) {
                curves[c][i] = Math.max(-1, Math.min(1,
                    params[c][0] * log2x / 16.0 + params[c][1]));
            }
        }

        // Plot against log10(x) for log-scaled x-axis
        double[] xPlot = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            xPlot[i] = Math.log10(xData[i]);
        }

        writePlot2x2(new File(docsDir, "instructions-log.png"),
            "LOG Instruction — C * log2(|x|) / 16 + D", "Input (log scale)", "Output",
            logMin, logMax, -0.7, 0.5,
            xPlot, curves,
            new String[]{"C=0.5 D=0.03 (default)", "C=1.0 D=0.03 (steeper)",
                          "C=0.5 D=0.25 (shifted up)", "C=-0.5 D=0.03 (inverted)"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]});
        System.out.println("  wrote instructions-log.png");
    }

    // ================ Root — DC sweep 0 to 1, note behavior near 0 ================
    // Root uses LOG/EXP pair: log(x)/N then exp
    // At x=0, log(0) = -infinity → saturates to -1 → exp clips

    private void plotRoot(File docsDir) throws Exception {
        double[] xData = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            xData[i] = (double) i / (NUM_POINTS - 1);
        }

        // Simulate sqrt (N=2) and cbrt (N=3) via the actual block
        double[] sqrtSim = sweepBlock(
            () -> new RootCADBlock(100, 100),
            "Control Input 1", "Control Output 1",
            0.0, 1.0);

        double[] cbrtSim = sweepBlock(
            () -> { RootCADBlock b = new RootCADBlock(100, 100); b.setRoot(3); return b; },
            "Control Input 1", "Control Output 1",
            0.0, 1.0);

        // Mathematical reference: dashed identity line (drawn by writePlot when ranges match)
        // Plot simulated curves only — the identity line provides context
        writePlot(new File(docsDir, "instructions-root.png"),
            "Root Instruction (FV-1 Simulated)", "Input", "Output",
            0, 1.0, 0, 1.0,
            xData, new double[][]{sqrtSim, cbrtSim},
            new String[]{"sqrt (N=2)", "cbrt (N=3)"},
            new String[]{COLORS[0], COLORS[2]});
        System.out.println("  wrote instructions-root.png");
    }

    // ================ Maximum — show MAXX behavior on sine wave ================
    // MAXX keeps the larger of accumulator and register*gain.
    // Show a sine wave and a fixed threshold — output follows sine when
    // above threshold, holds at threshold otherwise.

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
            // MAXX: output = max(input1, input2)
            maxxOut[i] = Math.max(sine[i], 0.4);
        }

        writePlot(new File(docsDir, "instructions-maximum.png"),
            "MAXX — max(Input 1, Input 2)", "Time (ms)", "Amplitude",
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
