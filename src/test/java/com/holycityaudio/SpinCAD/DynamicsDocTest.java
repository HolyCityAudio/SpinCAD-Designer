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
 * and threshold. Limiters: input-vs-output dB transfer curves across
 * 0 to -80 dB range. Noise gate: envelope-in-dB showing gating at two
 * threshold settings.
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

    /** Measure steady-state output RMS in dB for a single input level. */
    private double measureDb(SpinCADBlock block, String outputPin, double inputDb)
            throws Exception {
        double amplitude = Math.pow(10, inputDb / 20.0);
        File sineWav = generateSineWav(SWEEP_DURATION, FREQ, amplitude);
        short[] stereo = simulate(block, sineWav, null, outputPin, null, tempDir);
        if (stereo == null) return Double.NaN;
        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);
        int start = audio.length * 3 / 4;
        return rmsDb(audio, start, audio.length);
    }

    /** Compute windowed RMS envelope in dB. */
    private static double[] envelopeDb(double[] audio, int windowSize) {
        int n = audio.length / windowSize;
        double[] env = new double[n];
        for (int i = 0; i < n; i++) {
            double sumSq = 0;
            for (int j = 0; j < windowSize; j++) {
                double s = audio[i * windowSize + j];
                sumSq += s * s;
            }
            double r = Math.sqrt(sumSq / windowSize);
            env[i] = r > 0 ? 20 * Math.log10(r) : -100;
        }
        return env;
    }

    // ==================== Noise Gate ====================

    @Test
    void generateNoiseGatePlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // Two thresholds: -40 dB and -80 dB (converted to linear for the block)
        double[] threshDb = {-40, -80};
        double duration = 0.5;
        // Decay rate 20: reaches -80 dB at ~0.46s
        File inputWav = generateDecayingSineWav(duration, 440, 1.0, 20.0);

        short[] inputSamples = readWavSamples(inputWav);
        double[] inputAudio = toDouble(extractChannel(inputSamples, 0));

        int windowSize = 256;
        int numWindows = inputAudio.length / windowSize;
        double[] timeMs = new double[numWindows];
        for (int i = 0; i < numWindows; i++)
            timeMs[i] = 1000.0 * (i * windowSize + windowSize / 2) / SAMPLE_RATE;

        double[][] curves = new double[1 + threshDb.length][];
        String[] labels = new String[1 + threshDb.length];
        curves[0] = envelopeDb(inputAudio, windowSize);
        labels[0] = "input";

        for (int ti = 0; ti < threshDb.length; ti++) {
            double threshLin = Math.pow(10, threshDb[ti] / 20.0);
            NoiseGateCADBlock block = new NoiseGateCADBlock(100, 100);
            block.setthresh(threshLin);
            short[] stereo = simulate(block, inputWav, null,
                "Audio Out", null, tempDir);
            if (stereo == null) {
                System.err.println("  SKIP NoiseGate at " + threshDb[ti]);
                return;
            }
            double[] audio = toDouble(extractChannel(stereo, 0));
            curves[ti + 1] = envelopeDb(audio, windowSize);
            labels[ti + 1] = String.format("thresh=%.0f dB", threshDb[ti]);
        }

        writePlot(new File(docsDir, "dynamics-noisegate.png"),
            "Noise Gate", "Time (ms)", "Level (dB)",
            0, timeMs[timeMs.length - 1], -100, 0,
            timeMs, curves, labels,
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
            b.setinGain(0.5);
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
            new double[][]{outputDb}, new String[]{"output"},
            new String[]{COLORS[0]});

        // Waveform at 0 dB to show distortion
        File sineWav = generateSineWav(0.5, 440, 1.0);
        rms_limiterCADBlock b0 = new rms_limiterCADBlock(100, 100);
        b0.setinGain(0.5);
        short[] stereo = simulate(b0, sineWav, null, "Output", null, tempDir);
        if (stereo != null) {
            double[] audio = toDouble(extractChannel(stereo, 0));
            // Measure distortion: peak vs RMS ratio (pure sine = sqrt(2))
            int qStart = audio.length * 3 / 4;
            double outRms = rms(audio, qStart, audio.length);
            double outPeak = 0;
            for (int i = qStart; i < audio.length; i++)
                outPeak = Math.max(outPeak, Math.abs(audio[i]));
            double crestFactor = outPeak / outRms;
            System.out.printf("%nRMS Limiter at 0 dB input:%n");
            System.out.printf("  Output peak: %.4f  RMS: %.4f  Crest: %.3f (sine=%.3f)%n",
                outPeak, outRms, crestFactor, Math.sqrt(2));
            if (outPeak > 0.99)
                System.out.println("  ** Output is clipping — the 1.5x output "
                    + "scale factor causes hard clipping at 0 dB input");

            int skipSamples = (int) (0.25 * SAMPLE_RATE);
            int displaySamples = (int) (3.0 / 440 * SAMPLE_RATE);
            int start = Math.min(skipSamples, audio.length - displaySamples - 1);
            int end = start + displaySamples;
            double[] wave = Arrays.copyOfRange(audio, start, end);
            double[] timeMs = new double[wave.length];
            for (int i = 0; i < timeMs.length; i++)
                timeMs[i] = 1000.0 * i / SAMPLE_RATE;

            writePlot(new File(docsDir, "dynamics-rms_limiter_0db.png"),
                "RMS Limiter (0 dB input)", "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1, 1, timeMs,
                new double[][]{wave}, new String[]{"0 dB in"},
                new String[]{COLORS[0]});
        }

        System.out.println("  wrote dynamics-rms_limiter.png, dynamics-rms_limiter_0db.png");
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
            new double[][]{outputDb}, new String[]{"output"},
            new String[]{COLORS[0]});
        System.out.println("  wrote dynamics-soft_knee_limiter.png");
    }

    // ==================== Helper: decaying sine WAV ====================

    private static File generateDecayingSineWav(double durationSeconds, double freqHz,
            double amplitude, double decayRate) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / SAMPLE_RATE;
            double envelope = amplitude * Math.exp(-decayRate * t);
            short sample = (short) (envelope * Short.MAX_VALUE *
                Math.sin(2 * Math.PI * freqHz * i / SAMPLE_RATE));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }
        File wavFile = File.createTempFile("spincad_decay_", ".wav");
        wavFile.deleteOnExit();
        javax.sound.sampled.AudioFormat format =
            new javax.sound.sampled.AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data);
        javax.sound.sampled.AudioInputStream ais =
            new javax.sound.sampled.AudioInputStream(bais, format, numFrames);
        javax.sound.sampled.AudioSystem.write(ais,
            javax.sound.sampled.AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }
}
