package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Wave Shaper menu blocks.
 * Each block is fed a 440 Hz sine wave at three levels (0 dB, -6 dB, -18 dB)
 * and the output waveform is plotted.
 */
public class WaveShaperDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;
    private static final double FREQ = 440.0;
    // Show ~3 cycles at 440 Hz
    private static final int DISPLAY_SAMPLES = (int)(3.0 / FREQ * SAMPLE_RATE);
    // Skip initial samples for block to settle
    private static final int SKIP_SAMPLES = (int)(0.1 * SAMPLE_RATE);

    private static final double[] LEVELS_LINEAR = {1.0, 0.5, 0.125, 0.0625}; // 0, -6, -18, -24 dB
    private static final String[] LEVEL_LABELS = {"0 dB", "-6 dB", "-18 dB", "-24 dB"};

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateWaveShaperPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // === Cube (CubeGain) — 0 dB only ===
        plotSingleLevel("CubeGain", "Cube (0 dB)",
            new CubeGainCADBlock(100, 100), null, "Audio Output 1", docsDir);
        plotCubeGainTransfer(docsDir);

        // === Distortion ===
        plotWaveShaper("Distortion", "Distortion",
            (level) -> new DistortionCADBlock(100, 100),
            null, "Audio Output 1", docsDir);

        // === Overdrive ===
        plotWaveShaper("Overdrive", "Overdrive",
            (level) -> { OverdriveCADBlock b = new OverdriveCADBlock(100, 100); return b; },
            Map.of("Drive", 750), "Audio Output 1", docsDir);

        // === Octave Fuzz — 0 dB only with 80 Hz HPF ===
        plotOctaveFuzz(docsDir);

        // === T/X ===
        plotWaveShaper("ToverX", "T/X",
            (level) -> new ToverXCADBlock(100, 100),
            null, "Audio_Output", docsDir);

        // === Aliaser — separate over/under charts for Smooth and Raw, 500 Hz ===
        plotAliaserStacked(docsDir);

        // === Quantizer — 0 dB only, 3-bit vs 8-bit ===
        plotQuantizerComparison(docsDir);

        System.out.println("\nAll wave shaper PNGs written to docs/");
    }

    @Test
    void generateAliaserSpectrumPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // 500 Hz sine at full scale
        File sineWav = generateSineWav(SIM_DURATION, 500.0, 1.0);

        // Rip=500 for moderate aliasing
        int rip = 500;
        plotAliaserSpectrum(sineWav, rip, "Smooth", "Aliaser Smooth (Rip=" + rip + ")",
            "waveshaper-aliaser-smooth-spectrum.png", docsDir);
        plotAliaserSpectrum(sineWav, rip, "Raw", "Aliaser Raw (Rip=" + rip + ")",
            "waveshaper-aliaser-raw-spectrum.png", docsDir);

        // 2x Rip for more aggressive aliasing
        int rip2x = 900;
        plotAliaserSpectrum(sineWav, rip2x, "Smooth", "Aliaser Smooth (Rip=" + rip2x + ")",
            "waveshaper-aliaser-smooth-spectrum-2x.png", docsDir);
        plotAliaserSpectrum(sineWav, rip2x, "Raw", "Aliaser Raw (Rip=" + rip2x + ")",
            "waveshaper-aliaser-raw-spectrum-2x.png", docsDir);

        System.out.println("\nAliaser spectrum PNGs written to docs/images/");
    }

    private void plotAliaserSpectrum(File sineWav, int rip, String outputPin,
            String title, String fileName, File docsDir) throws Exception {

        AliaserCADBlock block = new AliaserCADBlock(100, 100);
        short[] stereo = simulate(block, sineWav, Map.of("Rip", rip),
            outputPin, null, tempDir);
        if (stereo == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }

        // Read input and output audio
        short[] inputStereo = readWavSamples(sineWav);
        double[] inputMono = toDouble(extractChannel(inputStereo, 0));
        double[] outputMono = toDouble(extractChannel(stereo, 0));

        // Compute spectra (skip settling time)
        int start = SKIP_SAMPLES;
        double[] inputSpectrum = computeSpectrumDb(inputMono, start, FFT_SIZE);
        double[] outputSpectrum = computeSpectrumDb(outputMono, start, FFT_SIZE);
        double[] freqAxis = computeFreqAxis(FFT_SIZE / 2);

        // Trim to display range 0-8 kHz
        double fMax = 8000;
        int binMax = Math.min(freqAxis.length - 1, (int)(fMax * FFT_SIZE / SAMPLE_RATE));
        int binMin = 1; // skip DC
        int displayLen = binMax - binMin + 1;

        double[] trimFreq = new double[displayLen];
        double[] trimInput = new double[displayLen];
        double[] trimOutput = new double[displayLen];
        for (int i = 0; i < displayLen; i++) {
            trimFreq[i] = freqAxis[binMin + i];
            trimInput[i] = inputSpectrum[binMin + i];
            trimOutput[i] = outputSpectrum[binMin + i];
        }

        // Write stacked 2-panel plot: input spectrum on top, output on bottom
        writeStackedSpectrumPlot(new File(docsDir, fileName), title,
            trimFreq, trimInput, trimOutput);
        System.out.println("  wrote " + fileName);
    }

    // ==================== Spectrum helpers ====================

    private static final int FFT_SIZE = 8192;

    private double[] computeSpectrumDb(double[] data, int offset, int fftSize) {
        double[] windowed = new double[fftSize];
        for (int i = 0; i < fftSize; i++) {
            int idx = offset + i;
            double sample = (idx < data.length) ? data[idx] : 0;
            double window = 0.5 * (1 - Math.cos(2 * Math.PI * i / (fftSize - 1)));
            windowed[i] = sample * window;
        }
        double[] mag = fftMagnitude(windowed);
        double[] db = new double[mag.length];
        for (int i = 0; i < mag.length; i++) {
            db[i] = (mag[i] > 1e-10) ? 20 * Math.log10(mag[i]) : -100;
            db[i] = Math.max(-80, Math.min(10, db[i]));
        }
        return db;
    }

    private double[] computeFreqAxis(int numBins) {
        double[] freqs = new double[numBins];
        for (int i = 0; i < numBins; i++) {
            freqs[i] = (double) i * SAMPLE_RATE / FFT_SIZE;
        }
        return freqs;
    }

    private double[] fftMagnitude(double[] data) {
        int n = data.length;
        double[] re = new double[n];
        double[] im = new double[n];
        int bits = Integer.numberOfTrailingZeros(n);
        for (int i = 0; i < n; i++) {
            int rev = Integer.reverse(i) >>> (32 - bits);
            re[rev] = data[i];
        }
        for (int size = 2; size <= n; size *= 2) {
            int halfSize = size / 2;
            double angle = -2 * Math.PI / size;
            double wRe = Math.cos(angle);
            double wIm = Math.sin(angle);
            for (int s = 0; s < n; s += size) {
                double curRe = 1.0, curIm = 0.0;
                for (int j = 0; j < halfSize; j++) {
                    int even = s + j;
                    int odd = s + j + halfSize;
                    double tRe = curRe * re[odd] - curIm * im[odd];
                    double tIm = curRe * im[odd] + curIm * re[odd];
                    re[odd] = re[even] - tRe;
                    im[odd] = im[even] - tIm;
                    re[even] = re[even] + tRe;
                    im[even] = im[even] + tIm;
                    double newRe = curRe * wRe - curIm * wIm;
                    curIm = curRe * wIm + curIm * wRe;
                    curRe = newRe;
                }
            }
        }
        int half = n / 2;
        double[] mag = new double[half];
        for (int i = 0; i < half; i++) {
            mag[i] = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
        }
        return mag;
    }

    private void writeStackedSpectrumPlot(File file, String title,
            double[] freqAxis, double[] inputDb, double[] outputDb) throws IOException {

        int PLOT_W = 360, PLOT_H = 160;
        int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 15;
        int GAP = 55; // gap between panels for x-axis ticks + title
        int LEGEND_H = 40;
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + GAP + PLOT_H + PAD_B + LEGEND_H;
        double yMin = -80, yMax = 10;
        double xMin = freqAxis[0], xMax = freqAxis[freqAxis.length - 1];

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        // Top panel: Input spectrum
        int py1 = PAD_T;
        drawPlot(g, PAD_L, py1, PLOT_W, PLOT_H, "Input Spectrum",
            "Frequency (Hz)", "dB", xMin, xMax, yMin, yMax);
        drawCurve(g, freqAxis, inputDb, PAD_L, py1, PLOT_W, PLOT_H,
            xMin, xMax, yMin, yMax, COLORS[0]);

        // Bottom panel: Output spectrum
        int py2 = PAD_T + PLOT_H + GAP;
        drawPlot(g, PAD_L, py2, PLOT_W, PLOT_H, title,
            "Frequency (Hz)", "dB", xMin, xMax, yMin, yMax);
        drawCurve(g, freqAxis, outputDb, PAD_L, py2, PLOT_W, PLOT_H,
            xMin, xMax, yMin, yMax, COLORS[1]);

        // Legend
        drawLegend(g, PAD_L, py2 + PLOT_H + 52,
            new String[]{"Input", "Output"},
            new String[]{COLORS[0], COLORS[1]});

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(double level); }

    private void plotWaveShaper(String fileBase, String title,
            BlockFactory factory, Map<String, Integer> controlInputs,
            String outputPin, File docsDir) throws Exception {

        double[][] curves = new double[LEVELS_LINEAR.length][];
        double[] timeMs = null;

        for (int li = 0; li < LEVELS_LINEAR.length; li++) {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, LEVELS_LINEAR[li]);
            SpinCADBlock block = factory.create(LEVELS_LINEAR[li]);

            short[] stereo = simulate(block, sineWav, controlInputs,
                outputPin, null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP " + title + " at " + LEVEL_LABELS[li] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            // Extract display window after settling
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

        writePlot(new File(docsDir, "waveshaper-" + fileBase.toLowerCase() + ".png"),
            title, "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, LEVEL_LABELS,
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]});
    }

    /** Plot a single block at 0 dB only. */
    private void plotSingleLevel(String fileBase, String title,
            SpinCADBlock block, Map<String, Integer> controlInputs,
            String outputPin, File docsDir) throws Exception {

        File sineWav = generateSineWav(SIM_DURATION, FREQ, 1.0);
        short[] stereo = simulate(block, sineWav, controlInputs, outputPin, null, tempDir);
        if (stereo == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }
        double[] audio = toDouble(extractChannel(stereo, 0));
        int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
        int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
        double[] curve = Arrays.copyOfRange(audio, start, end);
        double[] timeMs = new double[end - start];
        for (int i = 0; i < timeMs.length; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        writePlot(new File(docsDir, "waveshaper-" + fileBase.toLowerCase() + ".png"),
            title, "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, new double[][]{curve}, new String[]{"0 dB"},
            new String[]{COLORS[0]});
    }

    /** Plot Cube Gain static transfer curve: f(x) = -1.4x^3 + 1.5x for x in [-1,1]. */
    private void plotCubeGainTransfer(File docsDir) throws IOException {
        int N = 401;
        double[] x = new double[N];
        double[] y = new double[N];
        for (int i = 0; i < N; i++) {
            x[i] = -1.0 + 2.0 * i / (N - 1);
            y[i] = -1.4 * x[i] * x[i] * x[i] + 1.5 * x[i];
        }
        writePlot(new File(docsDir, "waveshaper-cubegain-transfer.png"),
            "Cube Gain Transfer", "Input", "Output",
            -1.0, 1.0, -0.7, 0.7,
            x, new double[][]{y}, new String[]{"f(x)"},
            new String[]{COLORS[0]});
    }

    /** Plot Octave Fuzz at 0 dB with one-pole HPF at 80 Hz to remove DC offset. */
    private void plotOctaveFuzz(File docsDir) throws Exception {
        File sineWav = generateSineWav(SIM_DURATION, FREQ, 1.0);
        OctaveCADBlock block = new OctaveCADBlock(100, 100);
        short[] stereo = simulate(block, sineWav, null, "Audio_Output", null, tempDir);
        if (stereo == null) {
            System.err.println("  SKIP Octave Fuzz: simulation failed");
            return;
        }
        double[] audio = toDouble(extractChannel(stereo, 0));

        // One-pole HPF at 80 Hz: alpha = RC/(RC+dt)
        double dt = 1.0 / SAMPLE_RATE;
        double RC = 1.0 / (2 * Math.PI * 80);
        double alpha = RC / (RC + dt);
        double[] filtered = new double[audio.length];
        filtered[0] = audio[0];
        for (int i = 1; i < audio.length; i++) {
            filtered[i] = alpha * (filtered[i - 1] + audio[i] - audio[i - 1]);
        }

        int start = Math.min(SKIP_SAMPLES, filtered.length - DISPLAY_SAMPLES - 1);
        int end = Math.min(start + DISPLAY_SAMPLES, filtered.length);
        double[] curve = Arrays.copyOfRange(filtered, start, end);
        double[] timeMs = new double[end - start];
        for (int i = 0; i < timeMs.length; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        writePlot(new File(docsDir, "waveshaper-octavefuzz.png"),
            "Octave Fuzz (0 dB)", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, new double[][]{curve}, new String[]{"0 dB"},
            new String[]{COLORS[0]});
    }

    /** Plot Aliaser: separate over/under charts for Smooth and Raw at 500 Hz. */
    private void plotAliaserStacked(File docsDir) throws Exception {
        double aliaserFreq = 500.0;
        int aliaserDisplay = (int)(3.0 / aliaserFreq * SAMPLE_RATE);
        File sineWav = generateSineWav(SIM_DURATION, aliaserFreq, 1.0);

        for (String outputPin : new String[]{"Smooth", "Raw"}) {
            AliaserCADBlock block = new AliaserCADBlock(100, 100);
            short[] stereo = simulate(block, sineWav, Map.of("Rip", 500),
                outputPin, null, tempDir);
            if (stereo == null) {
                System.err.println("  SKIP Aliaser " + outputPin + ": simulation failed");
                continue;
            }

            double[] output = toDouble(extractChannel(stereo, 0));
            double[] input = toDouble(extractChannel(readWavSamples(sineWav), 0));

            int start = Math.min(SKIP_SAMPLES, output.length - aliaserDisplay - 1);
            int end = Math.min(start + aliaserDisplay, output.length);
            double[] inputSlice = Arrays.copyOfRange(input, start, Math.min(end, input.length));
            double[] outputSlice = Arrays.copyOfRange(output, start, end);
            double[] timeMs = new double[end - start];
            for (int i = 0; i < timeMs.length; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

            writeStackedWaveformPlot(
                new File(docsDir, "waveshaper-aliaser-" + outputPin.toLowerCase() + ".png"),
                "Aliaser " + outputPin + " (500 Hz, Rip=500)",
                timeMs, inputSlice, outputSlice, "Input", outputPin);
            System.out.println("  wrote waveshaper-aliaser-" + outputPin.toLowerCase() + ".png");
        }
    }

    /** Plot Quantizer: 0 dB only, 3-bit vs 8-bit on single chart. */
    private void plotQuantizerComparison(File docsDir) throws Exception {
        File sineWav = generateSineWav(SIM_DURATION, FREQ, 1.0);
        int[] bitSettings = {3, 8};
        String[] labels = {"3-bit", "8-bit"};
        double[][] curves = new double[2][];
        double[] timeMs = null;

        for (int i = 0; i < bitSettings.length; i++) {
            QuantizerCADBlock block = new QuantizerCADBlock(100, 100);
            block.setBits(bitSettings[i]);
            short[] stereo = simulate(block, sineWav, null, "Audio Output 1", null, tempDir);
            if (stereo == null) {
                System.err.println("  SKIP Quantizer " + labels[i] + ": simulation failed");
                return;
            }
            double[] audio = toDouble(extractChannel(stereo, 0));
            int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
            curves[i] = Arrays.copyOfRange(audio, start, end);
            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int j = 0; j < timeMs.length; j++) timeMs[j] = 1000.0 * j / SAMPLE_RATE;
            }
        }

        writePlot(new File(docsDir, "waveshaper-quantizer.png"),
            "Quantizer (0 dB)", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1]});
    }

    /** Write a stacked 2-panel waveform plot: input on top, output on bottom. */
    private void writeStackedWaveformPlot(File file, String title,
            double[] timeMs, double[] inputData, double[] outputData,
            String inputLabel, String outputLabel) throws IOException {

        int PLOT_W = 360, PLOT_H = 160;
        int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 15;
        int GAP = 55;
        int LEGEND_H = 40;
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + GAP + PLOT_H + PAD_B + LEGEND_H;
        double xMin = timeMs[0], xMax = timeMs[timeMs.length - 1];

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        int py1 = PAD_T;
        drawPlot(g, PAD_L, py1, PLOT_W, PLOT_H, inputLabel,
            "Time (ms)", "Amplitude", xMin, xMax, -1.0, 1.0);
        drawCurve(g, timeMs, inputData, PAD_L, py1, PLOT_W, PLOT_H,
            xMin, xMax, -1.0, 1.0, COLORS[0]);

        int py2 = PAD_T + PLOT_H + GAP;
        drawPlot(g, PAD_L, py2, PLOT_W, PLOT_H, outputLabel,
            "Time (ms)", "Amplitude", xMin, xMax, -1.0, 1.0);
        drawCurve(g, timeMs, outputData, PAD_L, py2, PLOT_W, PLOT_H,
            xMin, xMax, -1.0, 1.0, COLORS[1]);

        // Overall title
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, PAD_L + PLOT_W / 2 - fm.stringWidth(title) / 2, 14);

        drawLegend(g, PAD_L, py2 + PLOT_H + 52,
            new String[]{inputLabel, outputLabel},
            new String[]{COLORS[0], COLORS[1]});

        g.dispose();
        ImageIO.write(img, "png", file);
    }
}
