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
 * Generates documentation plots for Modulation menu blocks.
 * Each block is fed a 440 Hz sine wave and the output waveform is plotted
 * over ~50 ms to show the modulation effect at default settings.
 */
public class ModulationDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;
    private static final double FREQ = 440.0;
    // ~50 ms display window
    private static final int DISPLAY_SAMPLES = (int)(0.050 * SAMPLE_RATE);
    // Skip initial samples for block to settle
    private static final int SKIP_SAMPLES = (int)(0.2 * SAMPLE_RATE);

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateModulationPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        plotChorus(docsDir);
        plotChorusQuad(docsDir);
        plotChorusQuadLfo(docsDir);
        plotFlanger(docsDir);
        plotPhaser(docsDir);
        plotRingMod(docsDir);
        plotServo(docsDir);

        System.out.println("\nAll modulation PNGs written to docs/");
    }

    // === Chorus ===
    // Synthetic illustration: 10 cycles of input vs. chorused output.
    // The simulator's WLDS Ka=64 produces too little detuning for a visible
    // cycle mismatch, so we compute the chorus output analytically:
    //   output(t) = input(t - d(t))  where d(t) = center + A*sin(2*pi*f_lfo*t)
    // Pitch shift = 1 - d'(t)/Fs, proportional to LFO slope.
    private void plotChorus(File docsDir) throws Exception {
        int cycles = 10;
        double lfoHz = 2.0;         // typical chorus LFO rate
        double delayCenterMs = 8.0; // 8 ms base delay
        double sweepMs = 1.5;       // ±1.5 ms modulation depth
        double amp = 0.8;

        // generate enough to show 10 cycles with visible mismatch
        int displaySamples = (int)(cycles / FREQ * SAMPLE_RATE);
        double[] inputWave = new double[displaySamples];
        double[] outputWave = new double[displaySamples];
        double[] timeMs = new double[displaySamples];

        for (int i = 0; i < displaySamples; i++) {
            double t = (double) i / SAMPLE_RATE;
            timeMs[i] = t * 1000.0;
            inputWave[i] = amp * Math.sin(2 * Math.PI * FREQ * t);
            // chorus reads from a modulated delay tap
            double delaySec = (delayCenterMs + sweepMs * Math.sin(2 * Math.PI * lfoHz * t)) / 1000.0;
            double tDelayed = t - delaySec;
            outputWave[i] = amp * Math.sin(2 * Math.PI * FREQ * tDelayed);
        }

        writeStackedWaveformPlot(
            new File(docsDir, "modulation-chorus.png"),
            "Chorus", timeMs, inputWave, outputWave, "Input", "Output");
    }

    // === Chorus Quad (4-voice) ===
    private void plotChorusQuad(File docsDir) throws Exception {
        try {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.8);
            ChorusQuadCADBlock block = new ChorusQuadCADBlock(100, 100);

            // Wire Voice_1 output only
            short[] stereo = simulate(block, sineWav, null,
                "Voice_1", "Voice_2", tempDir);

            if (stereo == null) {
                System.err.println("  SKIP ChorusQuad: simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            short[] right = extractChannel(stereo, 1);
            double[] audioL = toDouble(left);
            double[] audioR = toDouble(right);
            double[] inputAudio = getInputAudio(sineWav);

            int start = Math.min(SKIP_SAMPLES, audioL.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audioL.length);
            double[] curveInput = Arrays.copyOfRange(inputAudio, start, Math.min(end, inputAudio.length));
            double[] curveL = Arrays.copyOfRange(audioL, start, end);
            double[] curveR = Arrays.copyOfRange(audioR, start, end);

            double[] timeMs = new double[end - start];
            for (int i = 0; i < timeMs.length; i++) {
                timeMs[i] = 1000.0 * i / SAMPLE_RATE;
            }

            writePlot(new File(docsDir, "modulation-chorusquad.png"),
                "4-Voice Chorus", "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1.0, 1.0,
                timeMs, new double[][]{curveInput, curveL, curveR},
                new String[]{"Input", "Voice 1", "Voice 2"},
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
        } catch (Exception e) {
            System.err.println("  SKIP ChorusQuad: " + e.getMessage());
        }
    }

    // === 4-Voice Chorus LFO Phase Diagram ===
    // Conceptual diagram: 4 LFO phases and corresponding pitch shift.
    // Pitch shift is proportional to slope (derivative) of the LFO waveform.
    private void plotChorusQuadLfo(File docsDir) throws Exception {
        int N = 200;  // points per cycle, show one full LFO cycle
        double[] phase = new double[N];
        double[] sin0 = new double[N];
        double[] sin90 = new double[N];
        double[] sin180 = new double[N];
        double[] sin270 = new double[N];
        double[] pitch0 = new double[N];
        double[] pitch90 = new double[N];
        double[] pitch180 = new double[N];
        double[] pitch270 = new double[N];

        for (int i = 0; i < N; i++) {
            double angle = 2 * Math.PI * i / N;
            phase[i] = 360.0 * i / N;  // degrees
            sin0[i]   = Math.sin(angle);
            sin90[i]  = Math.sin(angle + Math.PI / 2);
            sin180[i] = Math.sin(angle + Math.PI);
            sin270[i] = Math.sin(angle + 3 * Math.PI / 2);
            // Pitch shift = derivative of LFO = cos at same phase
            pitch0[i]   = Math.cos(angle);
            pitch90[i]  = Math.cos(angle + Math.PI / 2);
            pitch180[i] = Math.cos(angle + Math.PI);
            pitch270[i] = Math.cos(angle + 3 * Math.PI / 2);
        }

        int PLOT_W = 360, PLOT_H = 140;
        int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 15;
        int GAP = 55, LEGEND_H = 40;
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + GAP + PLOT_H + PAD_B + LEGEND_H;

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        String title = "4-Voice Chorus: LFO Phase & Pitch Shift";
        g.drawString(title, PAD_L + PLOT_W / 2 - fm.stringWidth(title) / 2, 14);

        int py1 = PAD_T;
        drawPlot(g, PAD_L, py1, PLOT_W, PLOT_H, "LFO Delay Modulation",
            "LFO Phase (degrees)", "Amplitude", 0, 360, -1.0, 1.0);
        drawCurve(g, phase, sin0, PAD_L, py1, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[0]);
        drawCurve(g, phase, sin90, PAD_L, py1, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[1]);
        drawCurve(g, phase, sin180, PAD_L, py1, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[2]);
        drawCurve(g, phase, sin270, PAD_L, py1, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[3]);

        int py2 = PAD_T + PLOT_H + GAP;
        drawPlot(g, PAD_L, py2, PLOT_W, PLOT_H, "Pitch Shift (LFO slope)",
            "LFO Phase (degrees)", "Shift", 0, 360, -1.0, 1.0);
        drawCurve(g, phase, pitch0, PAD_L, py2, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[0]);
        drawCurve(g, phase, pitch90, PAD_L, py2, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[1]);
        drawCurve(g, phase, pitch180, PAD_L, py2, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[2]);
        drawCurve(g, phase, pitch270, PAD_L, py2, PLOT_W, PLOT_H, 0, 360, -1.0, 1.0, COLORS[3]);

        drawLegend2x2(g, PAD_L, py2 + PLOT_H + 42,
            new String[]{"Voice 1 (0\u00B0)", "Voice 2 (90\u00B0)", "Voice 3 (180\u00B0)", "Voice 4 (270\u00B0)"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]});
        g.dispose();
        ImageIO.write(img, "png", new File(docsDir, "modulation-chorusquad-lfo.png"));
        System.out.println("  wrote modulation-chorusquad-lfo.png");
    }

    // === Flanger ===
    // Stacked input/output like chorus
    private void plotFlanger(File docsDir) throws Exception {
        try {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.8);
            FlangerCADBlock block = new FlangerCADBlock(100, 100);

            short[] stereo = simulate(block, sineWav, null,
                "Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP Flanger: simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] output = toDouble(left);
            double[] input = getInputAudio(sineWav);

            int start = Math.min(SKIP_SAMPLES, output.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, output.length);
            double[] inputSlice = Arrays.copyOfRange(input, start, Math.min(end, input.length));
            double[] outputSlice = Arrays.copyOfRange(output, start, end);
            double[] timeMs = new double[end - start];
            for (int i = 0; i < timeMs.length; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

            writeStackedWaveformPlot(
                new File(docsDir, "modulation-flanger.png"),
                "Flanger", timeMs, inputSlice, outputSlice, "Input", "Output");
        } catch (Exception e) {
            System.err.println("  SKIP Flanger: " + e.getMessage());
        }
    }

    // === Phaser ===
    // 3-panel stacked: Input, Mix Out, Wet Out on separate graphs
    private void plotPhaser(File docsDir) throws Exception {
        try {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.8);
            PhaserCADBlock block = new PhaserCADBlock(100, 100);

            // Use Mix Out which combines wet + dry
            short[] stereo = simulate(block, sineWav, null,
                "Mix Out", "Wet Out", tempDir);

            if (stereo == null) {
                System.err.println("  SKIP Phaser: simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            short[] right = extractChannel(stereo, 1);
            double[] mixAudio = toDouble(left);
            double[] wetAudio = toDouble(right);
            double[] inputAudio = getInputAudio(sineWav);

            int start = Math.min(SKIP_SAMPLES, mixAudio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, mixAudio.length);
            double[] curveInput = Arrays.copyOfRange(inputAudio, start, Math.min(end, inputAudio.length));
            double[] curveMix = Arrays.copyOfRange(mixAudio, start, end);
            double[] curveWet = Arrays.copyOfRange(wetAudio, start, end);

            double[] timeMs = new double[end - start];
            for (int i = 0; i < timeMs.length; i++) {
                timeMs[i] = 1000.0 * i / SAMPLE_RATE;
            }

            writeThreePanelPlot(
                new File(docsDir, "modulation-phaser.png"),
                "Phaser", timeMs,
                curveInput, curveMix, curveWet,
                "Input", "Mix Out", "Wet Out");
        } catch (Exception e) {
            System.err.println("  SKIP Phaser: " + e.getMessage());
        }
    }

    /** Write a 3-panel stacked waveform plot (input, mix, wet). */
    private void writeThreePanelPlot(File file, String title,
            double[] timeMs, double[] data1, double[] data2, double[] data3,
            String label1, String label2, String label3) throws IOException {
        int PLOT_W = 360, PLOT_H = 120;
        int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 15;
        int GAP = 45, LEGEND_H = 40;
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + GAP + PLOT_H + GAP + PLOT_H + PAD_B + LEGEND_H;
        double xMin = timeMs[0], xMax = timeMs[timeMs.length - 1];

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, PAD_L + PLOT_W / 2 - fm.stringWidth(title) / 2, 14);

        int py1 = PAD_T;
        drawPlot(g, PAD_L, py1, PLOT_W, PLOT_H, label1,
            "Time (ms)", "Amplitude", xMin, xMax, -1.0, 1.0);
        drawCurve(g, timeMs, data1, PAD_L, py1, PLOT_W, PLOT_H,
            xMin, xMax, -1.0, 1.0, COLORS[0]);

        int py2 = PAD_T + PLOT_H + GAP;
        drawPlot(g, PAD_L, py2, PLOT_W, PLOT_H, label2,
            "Time (ms)", "Amplitude", xMin, xMax, -1.0, 1.0);
        drawCurve(g, timeMs, data2, PAD_L, py2, PLOT_W, PLOT_H,
            xMin, xMax, -1.0, 1.0, COLORS[1]);

        int py3 = PAD_T + 2 * (PLOT_H + GAP);
        drawPlot(g, PAD_L, py3, PLOT_W, PLOT_H, label3,
            "Time (ms)", "Amplitude", xMin, xMax, -1.0, 1.0);
        drawCurve(g, timeMs, data3, PAD_L, py3, PLOT_W, PLOT_H,
            xMin, xMax, -1.0, 1.0, COLORS[2]);

        drawLegend(g, PAD_L, py3 + PLOT_H + 42,
            new String[]{label1, label2, label3},
            new String[]{COLORS[0], COLORS[1], COLORS[2]});
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // === Ring Mod — input/output over/under + spectrum over/under ===
    private void plotRingMod(File docsDir) throws Exception {
        try {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.8);
            RingModCADBlock block = new RingModCADBlock(100, 100);

            short[] stereo = simulate(block, sineWav,
                Map.of("Carrier Frequency", 500),
                "Audio Output 1", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP RingMod: simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] output = toDouble(left);
            double[] input = getInputAudio(sineWav);

            int start = Math.min(SKIP_SAMPLES, output.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, output.length);
            double[] inputSlice = Arrays.copyOfRange(input, start, Math.min(end, input.length));
            double[] outputSlice = Arrays.copyOfRange(output, start, end);
            double[] timeMs = new double[end - start];
            for (int i = 0; i < timeMs.length; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

            // Stacked waveform: input over output
            writeStackedWaveformPlot(
                new File(docsDir, "modulation-ringmod.png"),
                "Ring Modulator", timeMs, inputSlice, outputSlice, "Input", "Output");

            // Stacked spectrum: input over output
            int fftSize = 8192;
            double[] inputSpectrum = computeSpectrumDb(input, start, fftSize);
            double[] outputSpectrum = computeSpectrumDb(output, start, fftSize);
            double[] freqAxis = new double[fftSize / 2];
            for (int i = 0; i < freqAxis.length; i++)
                freqAxis[i] = (double) i * SAMPLE_RATE / fftSize;

            // Trim to 0-5 kHz
            double fMax = 5000;
            int binMax = Math.min(freqAxis.length - 1, (int)(fMax * fftSize / SAMPLE_RATE));
            int binMin = 1;
            int displayLen = binMax - binMin + 1;
            double[] trimFreq = new double[displayLen];
            double[] trimInput = new double[displayLen];
            double[] trimOutput = new double[displayLen];
            for (int i = 0; i < displayLen; i++) {
                trimFreq[i] = freqAxis[binMin + i];
                trimInput[i] = inputSpectrum[binMin + i];
                trimOutput[i] = outputSpectrum[binMin + i];
            }

            writeStackedSpectrumPlot(
                new File(docsDir, "modulation-ringmod-spectrum.png"),
                "Ring Modulator Spectrum", trimFreq, trimInput, trimOutput);

            System.out.println("  wrote modulation-ringmod.png + spectrum");
        } catch (Exception e) {
            System.err.println("  SKIP RingMod: " + e.getMessage());
        }
    }

    private double[] computeSpectrumDb(double[] data, int offset, int fftSize) {
        double[] windowed = new double[fftSize];
        for (int i = 0; i < fftSize; i++) {
            int idx = offset + i;
            double sample = (idx < data.length) ? data[idx] : 0;
            double window = 0.5 * (1 - Math.cos(2 * Math.PI * i / (fftSize - 1)));
            windowed[i] = sample * window;
        }
        double[] re = new double[fftSize], im = new double[fftSize];
        int bits = Integer.numberOfTrailingZeros(fftSize);
        for (int i = 0; i < fftSize; i++) re[Integer.reverse(i) >>> (32 - bits)] = windowed[i];
        for (int size = 2; size <= fftSize; size *= 2) {
            int half = size / 2;
            double ang = -2 * Math.PI / size;
            double wR = Math.cos(ang), wI = Math.sin(ang);
            for (int s = 0; s < fftSize; s += size) {
                double cR = 1, cI = 0;
                for (int j = 0; j < half; j++) {
                    int e = s + j, o = s + j + half;
                    double tR = cR * re[o] - cI * im[o], tI = cR * im[o] + cI * re[o];
                    re[o] = re[e] - tR; im[o] = im[e] - tI;
                    re[e] += tR; im[e] += tI;
                    double nR = cR * wR - cI * wI; cI = cR * wI + cI * wR; cR = nR;
                }
            }
        }
        double[] db = new double[fftSize / 2];
        for (int i = 0; i < db.length; i++) {
            double mag = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
            db[i] = (mag > 1e-10) ? 20 * Math.log10(mag) : -100;
            db[i] = Math.max(-80, Math.min(10, db[i]));
        }
        return db;
    }

    private void writeStackedWaveformPlot(File file, String title,
            double[] timeMs, double[] inputData, double[] outputData,
            String inputLabel, String outputLabel) throws IOException {
        int PLOT_W = 360, PLOT_H = 160;
        int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 15;
        int GAP = 55, LEGEND_H = 40;
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + GAP + PLOT_H + PAD_B + LEGEND_H;
        double xMin = timeMs[0], xMax = timeMs[timeMs.length - 1];

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, PAD_L + PLOT_W / 2 - fm.stringWidth(title) / 2, 14);

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

        drawLegend(g, PAD_L, py2 + PLOT_H + 52,
            new String[]{inputLabel, outputLabel},
            new String[]{COLORS[0], COLORS[1]});
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    private void writeStackedSpectrumPlot(File file, String title,
            double[] freqAxis, double[] inputDb, double[] outputDb) throws IOException {
        int PLOT_W = 360, PLOT_H = 160;
        int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 15;
        int GAP = 55, LEGEND_H = 40;
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + GAP + PLOT_H + PAD_B + LEGEND_H;
        double yMin = -80, yMax = 10;
        double xMin = freqAxis[0], xMax = freqAxis[freqAxis.length - 1];

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        int py1 = PAD_T;
        drawPlot(g, PAD_L, py1, PLOT_W, PLOT_H, "Input Spectrum",
            "Frequency (Hz)", "dB", xMin, xMax, yMin, yMax);
        drawCurve(g, freqAxis, inputDb, PAD_L, py1, PLOT_W, PLOT_H,
            xMin, xMax, yMin, yMax, COLORS[0]);

        int py2 = PAD_T + PLOT_H + GAP;
        drawPlot(g, PAD_L, py2, PLOT_W, PLOT_H, "Output Spectrum",
            "Frequency (Hz)", "dB", xMin, xMax, yMin, yMax);
        drawCurve(g, freqAxis, outputDb, PAD_L, py2, PLOT_W, PLOT_H,
            xMin, xMax, yMin, yMax, COLORS[1]);

        drawLegend(g, PAD_L, py2 + PLOT_H + 52,
            new String[]{"Input", "Output"},
            new String[]{COLORS[0], COLORS[1]});
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // === Servo Flanger ===
    // Stacked input/output
    private void plotServo(File docsDir) throws Exception {
        try {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.8);
            servoCADBlock block = new servoCADBlock(100, 100);

            short[] stereo = simulate(block, sineWav, null,
                "Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP Servo: simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] output = toDouble(left);
            double[] input = getInputAudio(sineWav);

            int start = Math.min(SKIP_SAMPLES, output.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, output.length);
            double[] inputSlice = Arrays.copyOfRange(input, start, Math.min(end, input.length));
            double[] outputSlice = Arrays.copyOfRange(output, start, end);
            double[] timeMs = new double[end - start];
            for (int i = 0; i < timeMs.length; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

            writeStackedWaveformPlot(
                new File(docsDir, "modulation-servo.png"),
                "Servo Flanger", timeMs, inputSlice, outputSlice, "Input", "Output");
        } catch (Exception e) {
            System.err.println("  SKIP Servo: " + e.getMessage());
        }
    }

    // ==================== Helpers ====================

    /** Extract the left channel of the input WAV as doubles. */
    private double[] getInputAudio(File sineWav) throws Exception {
        short[] inStereo = readWavSamples(sineWav);
        short[] inLeft = extractChannel(inStereo, 0);
        return toDouble(inLeft);
    }

    /** Plot input vs output for a single-output modulation block. */
    private void plotInputOutput(String title, String fileBase,
            File sineWav, short[] stereo, File docsDir) throws Exception {

        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);
        double[] inputAudio = getInputAudio(sineWav);

        int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
        int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
        double[] curveInput = Arrays.copyOfRange(inputAudio, start, Math.min(end, inputAudio.length));
        double[] curveOutput = Arrays.copyOfRange(audio, start, end);

        double[] timeMs = new double[end - start];
        for (int i = 0; i < timeMs.length; i++) {
            timeMs[i] = 1000.0 * i / SAMPLE_RATE;
        }

        writePlot(new File(docsDir, "modulation-" + fileBase + ".png"),
            title, "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, new double[][]{curveInput, curveOutput},
            new String[]{"Input", "Output"},
            new String[]{COLORS[0], COLORS[1]});
    }
}
