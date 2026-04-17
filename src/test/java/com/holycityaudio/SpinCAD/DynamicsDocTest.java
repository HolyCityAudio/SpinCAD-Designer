package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Dynamics menu blocks.
 * <p>
 * Compressors: input-vs-output dB transfer curves varying ratio/strength
 * and threshold. Limiters and noise gate: input-vs-output dB transfer
 * curves across 0 to -80 dB range.
 */
public class DynamicsDocTest {

    @TempDir
    File tempDir;

    private static final double FREQ = 1000.0;
    private static final double SWEEP_DURATION = 0.5;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // ==================== Sweep helpers ====================

    /** Build an array of dB levels from minDb to maxDb inclusive. */
    private static double[] sweepLevels(double minDb, double maxDb, double stepDb) {
        int n = (int) Math.round((maxDb - minDb) / stepDb) + 1;
        double[] levels = new double[n];
        for (int i = 0; i < n; i++) levels[i] = minDb + i * stepDb;
        return levels;
    }

    /** Measure steady-state output peak in dB for a single input level. */
    private double measureDb(SpinCADBlock block, String outputPin, double inputDb)
            throws Exception {
        double amplitude = Math.pow(10, inputDb / 20.0);
        File sineWav = generateSineWav(SWEEP_DURATION, FREQ, amplitude);
        short[] stereo = simulate(block, sineWav, null, outputPin, null, tempDir);
        if (stereo == null) return Double.NaN;
        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);
        int start = audio.length * 3 / 4;
        double pk = 0;
        for (int i = start; i < audio.length; i++)
            pk = Math.max(pk, Math.abs(audio[i]));
        return pk > 0 ? 20 * Math.log10(pk) : -100;
    }

    // ==================== Noise Gate ====================

    @Test
    void generateNoiseGatePlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();
        double[] inputDb = sweepLevels(-80, 0, 10);

        double[] threshDb = {-40, -80};
        String[] labels = new String[threshDb.length];
        double[][] curves = new double[threshDb.length][];

        for (int ti = 0; ti < threshDb.length; ti++) {
            double threshLin = Math.pow(10, threshDb[ti] / 20.0);
            curves[ti] = new double[inputDb.length];
            labels[ti] = String.format("thresh=%.0f dB", threshDb[ti]);
            for (int li = 0; li < inputDb.length; li++) {
                NoiseGateCADBlock b = new NoiseGateCADBlock(100, 100);
                b.setthresh(threshLin);
                curves[ti][li] = measureDb(b, "Audio Out", inputDb[li]);
            }
        }

        // Print table
        System.out.println("\nNoise Gate transfer:");
        System.out.printf("  Input dB");
        for (String l : labels) System.out.printf("  %14s", l);
        System.out.println();
        for (int i = 0; i < inputDb.length; i++) {
            System.out.printf("  %6.0f  ", inputDb[i]);
            for (int ti = 0; ti < threshDb.length; ti++)
                System.out.printf("  %14.1f", curves[ti][i]);
            System.out.println();
        }

        writePlot(new File(docsDir, "dynamics-noisegate.png"),
            "Noise Gate", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb, curves, labels,
            Arrays.copyOf(COLORS, curves.length));
        System.out.println("  wrote dynamics-noisegate.png");
    }

    // ==================== Peak Compressor ====================

    @Test
    void generatePeakCompressorPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();
        double[] inputDb = sweepLevels(-80, 0, 5);

        // Plot 1: vary ratio at fixed threshold (-25 dB)
        double[] ratios = {2, 4, 8};
        String[] ratioLabels = {"2:1", "4:1", "8:1"};
        double[][] ratioCurves = new double[ratios.length][];
        for (int ri = 0; ri < ratios.length; ri++) {
            ratioCurves[ri] = new double[inputDb.length];
            for (int li = 0; li < inputDb.length; li++) {
                peak_compressorCADBlock b = new peak_compressorCADBlock(100, 100);
                b.setthreshDb(-25);
                b.setratio(ratios[ri]);
                b.setinGain(1);
                b.setmakeupDb(0);
                b.settrim(1);
                b.setattTime(0.01);
                b.setrelTime(0.001);
                ratioCurves[ri][li] = measureDb(b, "Audio_Output", inputDb[li]);
            }
        }
        writePlot(new File(docsDir, "dynamics-peak_compressor.png"),
            "Peak Compressor (thresh=-25 dB)", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb, ratioCurves, ratioLabels,
            Arrays.copyOf(COLORS, ratios.length));

        // Plot 2: vary threshold at fixed ratio (4:1)
        double[] thresholds = {-12, -25};
        String[] threshLabels = {"thresh=-12 dB", "thresh=-25 dB"};
        double[][] threshCurves = new double[thresholds.length][];
        for (int ti = 0; ti < thresholds.length; ti++) {
            threshCurves[ti] = new double[inputDb.length];
            for (int li = 0; li < inputDb.length; li++) {
                peak_compressorCADBlock b = new peak_compressorCADBlock(100, 100);
                b.setthreshDb(thresholds[ti]);
                b.setratio(4);
                b.setinGain(1);
                b.setmakeupDb(0);
                b.settrim(1);
                b.setattTime(0.01);
                b.setrelTime(0.001);
                threshCurves[ti][li] = measureDb(b, "Audio_Output", inputDb[li]);
            }
        }
        writePlot(new File(docsDir, "dynamics-peak_compressor_thresh.png"),
            "Peak Compressor (ratio=4:1)", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb, threshCurves, threshLabels,
            Arrays.copyOf(COLORS, thresholds.length));

        System.out.println("  wrote dynamics-peak_compressor.png, dynamics-peak_compressor_thresh.png");
    }

    // ==================== RMS Compressor ====================

    @Test
    void generateRmsCompressorPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();
        double[] inputDb = sweepLevels(-80, 0, 5);

        // Plot 1: vary strength at fixed threshold (-25 dB)
        double[] strengths = {0.25, 0.5, 1.0};
        String[] strLabels = {"strength=0.25", "strength=0.5", "strength=1.0"};
        double[][] strCurves = new double[strengths.length][];
        for (int si = 0; si < strengths.length; si++) {
            strCurves[si] = new double[inputDb.length];
            for (int li = 0; li < inputDb.length; li++) {
                rms_compressorCADBlock b = new rms_compressorCADBlock(100, 100);
                b.setthreshDb(-25);
                b.setstrength(strengths[si]);
                b.setinGain(1);
                b.setmakeupDb(0);
                b.settrim(1);
                b.setattTime(0.01);
                b.setrelTime(0.001);
                strCurves[si][li] = measureDb(b, "Audio_Output", inputDb[li]);
            }
        }
        writePlot(new File(docsDir, "dynamics-rms_compressor.png"),
            "RMS Compressor (thresh=-25 dB)", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb, strCurves, strLabels,
            Arrays.copyOf(COLORS, strengths.length));

        // Plot 2: vary threshold at fixed strength (0.75)
        double[] thresholds = {-12, -25};
        String[] threshLabels = {"thresh=-12 dB", "thresh=-25 dB"};
        double[][] threshCurves = new double[thresholds.length][];
        for (int ti = 0; ti < thresholds.length; ti++) {
            threshCurves[ti] = new double[inputDb.length];
            for (int li = 0; li < inputDb.length; li++) {
                rms_compressorCADBlock b = new rms_compressorCADBlock(100, 100);
                b.setthreshDb(thresholds[ti]);
                b.setstrength(0.75);
                b.setinGain(1);
                b.setmakeupDb(0);
                b.settrim(1);
                b.setattTime(0.01);
                b.setrelTime(0.001);
                threshCurves[ti][li] = measureDb(b, "Audio_Output", inputDb[li]);
            }
        }
        writePlot(new File(docsDir, "dynamics-rms_compressor_thresh.png"),
            "RMS Compressor (strength=0.75)", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb, threshCurves, threshLabels,
            Arrays.copyOf(COLORS, thresholds.length));

        System.out.println("  wrote dynamics-rms_compressor.png, dynamics-rms_compressor_thresh.png");
    }

    // ==================== RMS Limiter/Expander ====================

    @Test
    void generateRmsLimExpPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();
        double[] inputDb = sweepLevels(-80, 0, 10);

        double[] outputDb = new double[inputDb.length];
        for (int li = 0; li < inputDb.length; li++) {
            rms_lim_expCADBlock b = new rms_lim_expCADBlock(100, 100);
            outputDb[li] = measureDb(b, "Audio_Output", inputDb[li]);
        }

        // Print table
        System.out.println("\nRMS Limiter/Expander transfer:");
        System.out.println("  Input dB  Output dB");
        for (int i = 0; i < inputDb.length; i++)
            System.out.printf("  %6.0f    %6.1f%n", inputDb[i], outputDb[i]);

        writePlot(new File(docsDir, "dynamics-rms_lim_exp.png"),
            "RMS Limiter/Expander", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb,
            new double[][]{outputDb}, new String[]{"output"},
            new String[]{COLORS[0]});
        System.out.println("  wrote dynamics-rms_lim_exp.png");
    }

    // ==================== RMS Limiter ====================

    @Test
    void generateRmsLimiterPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();
        double[] inputDb = sweepLevels(-80, 0, 10);

        // Transfer curve
        double[] outputDb = new double[inputDb.length];
        for (int li = 0; li < inputDb.length; li++) {
            rms_limiterCADBlock b = new rms_limiterCADBlock(100, 100);
            b.setinGain(1.0);
            outputDb[li] = measureDb(b, "Output", inputDb[li]);
        }

        // Print table
        System.out.println("\nRMS Limiter transfer:");
        System.out.println("  Input dB  Output dB");
        for (int i = 0; i < inputDb.length; i++)
            System.out.printf("  %6.0f    %6.1f%n", inputDb[i], outputDb[i]);

        writePlot(new File(docsDir, "dynamics-rms_limiter.png"),
            "RMS Limiter", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb,
            new double[][]{inputDb, outputDb}, new String[]{"input", "output"},
            new String[]{"#aaaaaa", COLORS[0]});

        System.out.println("  wrote dynamics-rms_limiter.png");
    }

    // ==================== Soft Knee Limiter ====================

    @Test
    void generateSoftKneeLimiterPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();
        double[] inputDb = sweepLevels(-80, 0, 10);

        double[] outputDb = new double[inputDb.length];
        for (int li = 0; li < inputDb.length; li++) {
            soft_knee_limiterCADBlock b = new soft_knee_limiterCADBlock(100, 100);
            outputDb[li] = measureDb(b, "Audio_Output", inputDb[li]);
        }

        // Print table
        System.out.println("\nSoft Knee Limiter transfer:");
        System.out.println("  Input dB  Output dB");
        for (int i = 0; i < inputDb.length; i++)
            System.out.printf("  %6.0f    %6.1f%n", inputDb[i], outputDb[i]);

        writePlot(new File(docsDir, "dynamics-soft_knee_limiter.png"),
            "Soft Knee Limiter", "Input (dB)", "Output (dB)",
            -80, 0, -80, 0, inputDb,
            new double[][]{inputDb, outputDb}, new String[]{"input", "output"},
            new String[]{"#aaaaaa", COLORS[0]});
        System.out.println("  wrote dynamics-soft_knee_limiter.png");
    }

}
