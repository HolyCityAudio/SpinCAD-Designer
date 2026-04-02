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
 * Compressor blocks are tested at three threshold settings (-6, -12, -18 dB)
 * with a 0 dB sine input. The noise gate is tested with a decaying sine.
 * Limiters are tested with a 0 dB sine showing their limiting behavior.
 */
public class DynamicsDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;
    private static final double FREQ = 440.0;
    // Show ~3 cycles at 440 Hz
    private static final int DISPLAY_SAMPLES = (int)(3.0 / FREQ * SAMPLE_RATE);
    // Skip initial samples for block to settle
    private static final int SKIP_SAMPLES = (int)(0.25 * SAMPLE_RATE);

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // ==================== Noise Gate ====================

    @Test
    void generateNoiseGatePlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // Use three threshold settings to show gating behavior
        double[] thresholds = {0.005, 0.02, 0.08};
        String[] labels = {"thresh=0.005", "thresh=0.02", "thresh=0.08"};

        // Generate a decaying sine: starts loud and fades to silence
        File inputWav = generateDecayingSineWav(SIM_DURATION, FREQ, 1.0, 4.0);

        double[][] curves = new double[thresholds.length][];
        double[] timeMs = null;

        for (int ti = 0; ti < thresholds.length; ti++) {
            NoiseGateCADBlock block = new NoiseGateCADBlock(100, 100);
            block.setthresh(thresholds[ti]);

            short[] stereo = simulate(block, inputWav, null,
                "Audio Out", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP NoiseGate at " + labels[ti] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            // Show a longer window to see the decay and gating
            int displayLen = Math.min(audio.length - 100, (int)(0.3 * SAMPLE_RATE));
            int start = Math.min((int)(0.05 * SAMPLE_RATE), audio.length - displayLen - 1);
            int end = start + displayLen;
            curves[ti] = Arrays.copyOfRange(audio, start, end);

            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int i = 0; i < timeMs.length; i++) {
                    timeMs[i] = 1000.0 * i / SAMPLE_RATE;
                }
            }
        }

        writePlot(new File(docsDir, "dynamics-noisegate.png"),
            "Noise Gate", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});

        System.out.println("  wrote dynamics-noisegate.png");
    }

    // ==================== Peak Compressor ====================

    @Test
    void generatePeakCompressorPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        double[] thresholds = {-6.0, -12.0, -18.0};
        String[] labels = {"thresh=-6 dB", "thresh=-12 dB", "thresh=-18 dB"};

        File sineWav = generateSineWav(SIM_DURATION, FREQ, 1.0);

        double[][] curves = new double[thresholds.length][];
        double[] timeMs = null;

        for (int ti = 0; ti < thresholds.length; ti++) {
            peak_compressorCADBlock block = new peak_compressorCADBlock(100, 100);
            block.setthreshDb(thresholds[ti]);
            block.setratio(4.0);
            block.setinGain(1.0);
            block.setmakeupDb(0.0);
            block.settrim(1.0);

            short[] stereo = simulate(block, sineWav, null,
                "Audio_Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP peak_compressor at " + labels[ti] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
            curves[ti] = Arrays.copyOfRange(audio, start, end);

            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int i = 0; i < timeMs.length; i++) {
                    timeMs[i] = 1000.0 * i / SAMPLE_RATE;
                }
            }
        }

        writePlot(new File(docsDir, "dynamics-peak_compressor.png"),
            "Peak Compressor (ratio=4:1)", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});

        System.out.println("  wrote dynamics-peak_compressor.png");
    }

    // ==================== RMS Compressor ====================

    @Test
    void generateRmsCompressorPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        double[] thresholds = {-6.0, -12.0, -18.0};
        String[] labels = {"thresh=-6 dB", "thresh=-12 dB", "thresh=-18 dB"};

        File sineWav = generateSineWav(SIM_DURATION, FREQ, 1.0);

        double[][] curves = new double[thresholds.length][];
        double[] timeMs = null;

        for (int ti = 0; ti < thresholds.length; ti++) {
            rms_compressorCADBlock block = new rms_compressorCADBlock(100, 100);
            block.setthreshDb(thresholds[ti]);
            block.setstrength(0.5);
            block.setinGain(1.0);
            block.setmakeupDb(0.0);
            block.settrim(1.0);

            short[] stereo = simulate(block, sineWav, null,
                "Audio_Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP rms_compressor at " + labels[ti] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
            curves[ti] = Arrays.copyOfRange(audio, start, end);

            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int i = 0; i < timeMs.length; i++) {
                    timeMs[i] = 1000.0 * i / SAMPLE_RATE;
                }
            }
        }

        writePlot(new File(docsDir, "dynamics-rms_compressor.png"),
            "RMS Compressor (strength=0.5)", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});

        System.out.println("  wrote dynamics-rms_compressor.png");
    }

    // ==================== RMS Limiter/Expander ====================

    @Test
    void generateRmsLimExpPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // No setters -- show output at three input levels
        double[] levels = {1.0, 0.5, 0.125};
        String[] labels = {"0 dB in", "-6 dB in", "-18 dB in"};

        double[][] curves = new double[levels.length][];
        double[] timeMs = null;

        for (int li = 0; li < levels.length; li++) {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, levels[li]);
            rms_lim_expCADBlock block = new rms_lim_expCADBlock(100, 100);

            short[] stereo = simulate(block, sineWav, null,
                "Audio_Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP rms_lim_exp at " + labels[li] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
            curves[li] = Arrays.copyOfRange(audio, start, end);

            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int i = 0; i < timeMs.length; i++) {
                    timeMs[i] = 1000.0 * i / SAMPLE_RATE;
                }
            }
        }

        writePlot(new File(docsDir, "dynamics-rms_lim_exp.png"),
            "RMS Limiter/Expander", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});

        System.out.println("  wrote dynamics-rms_lim_exp.png");
    }

    // ==================== RMS Limiter ====================

    @Test
    void generateRmsLimiterPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // Show output at three input levels; side chain is wired to same input
        double[] levels = {1.0, 0.5, 0.125};
        String[] labels = {"0 dB in", "-6 dB in", "-18 dB in"};

        double[][] curves = new double[levels.length][];
        double[] timeMs = null;

        for (int li = 0; li < levels.length; li++) {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, levels[li]);
            rms_limiterCADBlock block = new rms_limiterCADBlock(100, 100);
            block.setinGain(0.5);

            // Wire Side Chain to the same ADC input via simulate()
            // simulate() auto-wires all audio inputs to ADC channels
            short[] stereo = simulate(block, sineWav, null,
                "Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP rms_limiter at " + labels[li] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
            curves[li] = Arrays.copyOfRange(audio, start, end);

            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int i = 0; i < timeMs.length; i++) {
                    timeMs[i] = 1000.0 * i / SAMPLE_RATE;
                }
            }
        }

        writePlot(new File(docsDir, "dynamics-rms_limiter.png"),
            "RMS Limiter", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});

        System.out.println("  wrote dynamics-rms_limiter.png");
    }

    // ==================== Soft Knee Limiter ====================

    @Test
    void generateSoftKneeLimiterPlot() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // No setters -- show output at three input levels
        double[] levels = {1.0, 0.5, 0.125};
        String[] labels = {"0 dB in", "-6 dB in", "-18 dB in"};

        double[][] curves = new double[levels.length][];
        double[] timeMs = null;

        for (int li = 0; li < levels.length; li++) {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, levels[li]);
            soft_knee_limiterCADBlock block = new soft_knee_limiterCADBlock(100, 100);

            short[] stereo = simulate(block, sineWav, null,
                "Audio_Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP soft_knee_limiter at " + labels[li] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
            curves[li] = Arrays.copyOfRange(audio, start, end);

            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int i = 0; i < timeMs.length; i++) {
                    timeMs[i] = 1000.0 * i / SAMPLE_RATE;
                }
            }
        }

        writePlot(new File(docsDir, "dynamics-soft_knee_limiter.png"),
            "Soft Knee Limiter", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});

        System.out.println("  wrote dynamics-soft_knee_limiter.png");
    }

    // ==================== Helper: decaying sine WAV ====================

    /**
     * Generate a stereo sine wave with exponential decay envelope.
     * Useful for testing noise gates.
     */
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
