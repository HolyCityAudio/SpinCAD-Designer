package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.ChirpCADBlock;

/**
 * Module test for the Chirp Delay (allpass cascade) block.
 *
 * Tests nstages=50 (effectively 30, the block maximum) at stretch values
 * of 5, 10, and 20 with AP coefficients +0.65 and -0.65.
 *
 * For each configuration:
 * <ul>
 *   <li>Impulse response spectrogram at 0.5 ms temporal resolution</li>
 *   <li>Group delay measurement at the spectrogram's first maximum</li>
 *   <li>100 ms tone burst (5-cycle linear fade in/out) at the peak frequency,
 *       shown as stacked input/output waveform</li>
 * </ul>
 */
public class ChirpDelayDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;
    private static final double IMPULSE_AMP = 0.5;
    private static final int N_STAGES = 50; // effectively 30 (block max)
    private static final double BURST_DURATION_MS = 100.0;
    private static final double BURST_AMP = 0.5;

    // Spectrogram: 0.5 ms hop (~16 samples at 32768 Hz), 128-point FFT
    private static final int FFT_SIZE = 128;
    private static final int HOP_SIZE = 16;
    private static final double DB_FLOOR = -60;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateChirpDelayPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        int[] stretches = {5, 10, 20};
        double[] apCoeffs = {0.65, -0.65};

        for (int stretch : stretches) {
            for (double ap : apCoeffs) {
                System.out.printf("%n=== Chirp Delay: nstages=%d, stretch=%d, AP=%.2f ===%n",
                    N_STAGES, stretch, ap);
                testChirpConfig(docsDir, stretch, ap);
            }
        }

        System.out.println("\nAll chirp delay PNGs written to docs/images/");
    }

    private void testChirpConfig(File docsDir, int stretch, double apCoeff)
            throws Exception {
        String tag = String.format("s%d_ap%s", stretch, fmtCoeff(apCoeff));

        // --- 1. Impulse response ---
        ChirpCADBlock block = new ChirpCADBlock(100, 100);
        block.setnAPs(N_STAGES);
        block.setstretch(stretch);
        block.setkiap(apCoeff);

        File impulseWav = generateImpulseWav(SIM_DURATION, IMPULSE_AMP);
        short[] stereo = simulate(block, impulseWav, null, "Output", null, tempDir);
        if (stereo == null) {
            System.err.println("  SKIP chirp " + tag + ": impulse sim failed");
            return;
        }

        double[] audio = toDouble(extractChannel(stereo, 0));

        // Adaptive display: 2.5x estimated max group delay, at least 50 ms
        double absK = Math.abs(apCoeff);
        double estMaxDelayMs = 1000.0 * 30 * stretch * (1 + absK)
            / ((1 - absK) * SAMPLE_RATE);
        double displayMs = Math.max(50, estMaxDelayMs * 2.5);
        int displaySamples = Math.min((int)(displayMs / 1000.0 * SAMPLE_RATE),
            audio.length);

        // --- 2. Measure group delay via cross-spectral method ---
        // τ(f) = Re(H'(f)·H*(f)) / |H(f)|² where H'(f) = FFT(n·h[n])
        int gdFftSize = 8192; // ~4 Hz resolution at 32768 Hz
        double[] groupDelaySamples = computeGroupDelay(audio, gdFftSize);
        int gdBins = gdFftSize / 2;

        // The allpass group delay is periodic in frequency — peaks at DC
        // and multiples of fs/D all share the same value.  Skip the DC
        // lobe by starting from fs/(4·stretch) and find the first
        // non-DC resonance peak.
        // Skip DC lobe: start search at fs/(4·stretch)
        int minBin = Math.max(1,
            (int)((double) SAMPLE_RATE / (4.0 * stretch) * gdFftSize / SAMPLE_RATE));

        // First pass: find the overall max above the floor
        double maxGdSamples = 0;
        for (int b = minBin; b < gdBins; b++) {
            if (groupDelaySamples[b] > maxGdSamples)
                maxGdSamples = groupDelaySamples[b];
        }

        // Second pass: find the first local maximum that reaches at least
        // 50% of the global peak — this gives the fundamental resonance
        // rather than a high-frequency harmonic
        int peakGdBin = minBin;
        for (int b = minBin + 1; b < gdBins - 1; b++) {
            if (groupDelaySamples[b] >= groupDelaySamples[b - 1]
                    && groupDelaySamples[b] >= groupDelaySamples[b + 1]
                    && groupDelaySamples[b] >= maxGdSamples * 0.5) {
                peakGdBin = b;
                maxGdSamples = groupDelaySamples[b];
                break;
            }
        }
        double groupDelayMs = 1000.0 * maxGdSamples / SAMPLE_RATE;
        double peakFreqHz = (double) peakGdBin * SAMPLE_RATE / gdFftSize;

        System.out.printf("  Group delay at first maximum: %.2f ms at %.0f Hz%n",
            groupDelayMs, peakFreqHz);

        // --- 3. Compute spectrogram for visualization ---
        int numFrames = (displaySamples - FFT_SIZE) / HOP_SIZE;
        if (numFrames < 1) {
            System.err.println("  SKIP chirp " + tag + ": too few frames");
            return;
        }
        int freqBins = FFT_SIZE / 2;

        double[][] magDb = new double[numFrames][freqBins];
        double globalMaxDb = -200;
        double[] window = hammingWindow(FFT_SIZE);

        for (int f = 0; f < numFrames; f++) {
            int offset = f * HOP_SIZE;
            double[] re = new double[FFT_SIZE];
            double[] im = new double[FFT_SIZE];
            for (int i = 0; i < FFT_SIZE; i++) {
                int idx = offset + i;
                re[i] = (idx < audio.length ? audio[idx] : 0) * window[i];
            }
            fft(re, im);
            for (int b = 0; b < freqBins; b++) {
                double mag = Math.sqrt(re[b] * re[b] + im[b] * im[b]);
                double db = 20 * Math.log10(mag + 1e-10);
                magDb[f][b] = db;
                if (db > globalMaxDb) globalMaxDb = db;
            }
        }

        // --- Write impulse spectrogram ---
        File specFile = new File(docsDir, "chirp-delay-impulse-" + tag + ".png");
        writeChirpSpectrogram(specFile,
            String.format("Chirp Impulse (stretch=%d, AP=%.2f)", stretch, apCoeff),
            String.format("Peak GD: %.1f ms @ %.0f Hz", groupDelayMs, peakFreqHz),
            magDb, numFrames, freqBins, displaySamples, globalMaxDb);
        System.out.println("  wrote " + specFile.getName());

        // --- 3. Tone burst at peak frequency ---
        File burstWav = generateFadedToneBurst(peakFreqHz);

        ChirpCADBlock block2 = new ChirpCADBlock(100, 100);
        block2.setnAPs(N_STAGES);
        block2.setstretch(stretch);
        block2.setkiap(apCoeff);

        short[] burstStereo = simulate(block2, burstWav, null, "Output", null, tempDir);
        if (burstStereo == null) {
            System.err.println("  SKIP chirp " + tag + " burst: sim failed");
            return;
        }

        double[] burstOut = toDouble(extractChannel(burstStereo, 0));
        double[] burstIn = toDouble(extractChannel(readWavSamples(burstWav), 0));

        // Display: burst duration + 200 ms for group delay tail
        int showSamples = (int)((BURST_DURATION_MS / 1000.0 + 0.200) * SAMPLE_RATE);
        showSamples = Math.min(showSamples, Math.min(burstOut.length, burstIn.length));

        double[] inSlice = Arrays.copyOf(burstIn, showSamples);
        double[] outSlice = Arrays.copyOf(burstOut, showSamples);
        double[] timeMs = timeAxisMs(showSamples, SAMPLE_RATE);

        File burstFile = new File(docsDir, "chirp-delay-burst-" + tag + ".png");
        writeStackedWaveformPlot(burstFile,
            String.format("%.0f Hz Burst (stretch=%d, AP=%.2f)",
                peakFreqHz, stretch, apCoeff),
            timeMs, inSlice, outSlice,
            String.format("Input (%.0f Hz, 100 ms)", peakFreqHz), "Output");
        System.out.println("  wrote " + burstFile.getName());
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static String fmtCoeff(double c) {
        return String.format("%+.2f", c)
            .replace("+", "pos").replace("-", "neg").replace(".", "");
    }

    private static double[] hammingWindow(int size) {
        double[] w = new double[size];
        for (int i = 0; i < size; i++)
            w[i] = 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (size - 1));
        return w;
    }

    /** Generate a stereo tone burst with linear 5-cycle fade-in and fade-out. */
    private File generateFadedToneBurst(double freqHz) throws IOException {
        int numFrames = (int)(SAMPLE_RATE * SIM_DURATION);
        int burstFrames = (int)(SAMPLE_RATE * BURST_DURATION_MS / 1000.0);
        int fadeSamples = (int)(5.0 / freqHz * SAMPLE_RATE);
        fadeSamples = Math.min(fadeSamples, burstFrames / 2);

        byte[] data = new byte[numFrames * 4];
        for (int i = 0; i < Math.min(burstFrames, numFrames); i++) {
            double env = 1.0;
            if (i < fadeSamples) {
                env = (double) i / fadeSamples;
            } else if (i >= burstFrames - fadeSamples) {
                env = (double)(burstFrames - 1 - i) / fadeSamples;
            }
            short sample = (short)(BURST_AMP * env * Short.MAX_VALUE *
                Math.sin(2 * Math.PI * freqHz * i / SAMPLE_RATE));
            int offset = i * 4;
            data[offset] = (byte)(sample & 0xff);
            data[offset + 1] = (byte)((sample >> 8) & 0xff);
            data[offset + 2] = (byte)(sample & 0xff);
            data[offset + 3] = (byte)((sample >> 8) & 0xff);
        }

        File wavFile = File.createTempFile("spincad_fadedburst_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    // =========================================================================
    // Spectrogram rendering
    // =========================================================================

    private void writeChirpSpectrogram(File file, String title, String annotation,
            double[][] magDb, int numFrames, int freqBins,
            int totalSamples, double maxDb) throws IOException {
        int padL = 50, padR = 20, padT = 35, padB = 50;
        int plotW = 400, plotH = 280;
        int totalW = padL + plotW + padR;
        int totalH = padT + plotH + padB;

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(0x1a, 0x1a, 0x1a));
        g.fillRect(0, 0, totalW, totalH);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(0xee, 0xee, 0xee));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, padL + plotW / 2 - fm.stringWidth(title) / 2, padT - 12);

        // Annotation (peak info)
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(0xaa, 0xdd, 0xaa));
        fm = g.getFontMetrics();
        g.drawString(annotation, padL + plotW / 2 - fm.stringWidth(annotation) / 2, padT - 1);

        // Draw spectrogram pixels
        for (int f = 0; f < numFrames; f++) {
            int x = padL + (int)((double) f / numFrames * plotW);
            int w = Math.max(1, padL + (int)((double)(f + 1) / numFrames * plotW) - x);
            for (int b = 0; b < freqBins; b++) {
                int y = padT + plotH - (int)((double)(b + 1) / freqBins * plotH);
                int h = Math.max(1, (int)((double) 1 / freqBins * plotH) + 1);
                double dbRel = magDb[f][b] - maxDb;
                double val = Math.max(0, Math.min(1, (dbRel - DB_FLOOR) / (-DB_FLOOR)));
                g.setColor(spectrogramColor(val));
                g.fillRect(x, y, w, h);
            }
        }

        // Border
        g.setColor(new Color(0x66, 0x66, 0x66));
        g.drawRect(padL, padT, plotW, plotH);

        // X-axis labels (time in ms)
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        g.setColor(new Color(0xbb, 0xbb, 0xbb));
        double maxTimeMs = 1000.0 * totalSamples / SAMPLE_RATE;
        fm = g.getFontMetrics();
        for (int tick = 0; tick <= 4; tick++) {
            double frac = tick / 4.0;
            int gx = padL + (int)(frac * plotW);
            String label = String.format("%.0f", frac * maxTimeMs);
            g.drawString(label, gx - fm.stringWidth(label) / 2, padT + plotH + 13);
        }

        // Y-axis labels (frequency)
        double nyquist = SAMPLE_RATE / 2.0;
        for (int tick = 0; tick <= 4; tick++) {
            double frac = tick / 4.0;
            int gy = padT + (int)((1.0 - frac) * plotH);
            double freq = frac * nyquist;
            String label = freq >= 1000
                ? String.format("%.0fk", freq / 1000)
                : String.format("%.0f", freq);
            fm = g.getFontMetrics();
            g.drawString(label, padL - 4 - fm.stringWidth(label), gy + 3);
        }

        // Axis labels
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(0xcc, 0xcc, 0xcc));
        fm = g.getFontMetrics();
        String xLabel = "Time (ms)";
        g.drawString(xLabel, padL + plotW / 2 - fm.stringWidth(xLabel) / 2, padT + plotH + 30);

        java.awt.geom.AffineTransform origT = g.getTransform();
        String yLabel = "Frequency";
        g.rotate(-Math.PI / 2, padL - 35, padT + plotH / 2);
        fm = g.getFontMetrics();
        g.drawString(yLabel, padL - 35 - fm.stringWidth(yLabel) / 2, padT + plotH / 2 + 4);
        g.setTransform(origT);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    /** Blue-black-red-yellow colormap for spectrogram. */
    private static Color spectrogramColor(double val) {
        if (val < 0.25) {
            return new Color(0, 0, (int)(val / 0.25 * 180));
        } else if (val < 0.5) {
            double t = (val - 0.25) / 0.25;
            return new Color((int)(t * 200), 0, (int)(180 * (1 - t)));
        } else if (val < 0.75) {
            double t = (val - 0.5) / 0.25;
            return new Color(200 + (int)(t * 55), (int)(t * 140), 0);
        } else {
            double t = (val - 0.75) / 0.25;
            return new Color(255, 140 + (int)(t * 115), (int)(t * 100));
        }
    }

    /**
     * Compute group delay from impulse response using the cross-spectral method:
     * τ(f) = Re(H'(f)·H*(f)) / |H(f)|²  where H'(f) = FFT(n·h[n]).
     * Returns group delay in samples at each frequency bin (0..fftSize/2-1).
     */
    private double[] computeGroupDelay(double[] impulseResponse, int fftSize) {
        double[] hRe = new double[fftSize];
        double[] hIm = new double[fftSize];
        double[] nhRe = new double[fftSize];
        double[] nhIm = new double[fftSize];

        int len = Math.min(impulseResponse.length, fftSize);
        for (int i = 0; i < len; i++) {
            hRe[i] = impulseResponse[i];
            nhRe[i] = i * impulseResponse[i];
        }

        fft(hRe, hIm);
        fft(nhRe, nhIm);

        int freqBins = fftSize / 2;
        double[] gd = new double[freqBins];
        for (int b = 0; b < freqBins; b++) {
            double magSq = hRe[b] * hRe[b] + hIm[b] * hIm[b];
            if (magSq > 1e-20) {
                double crossReal = nhRe[b] * hRe[b] + nhIm[b] * hIm[b];
                gd[b] = crossReal / magSq;
            }
        }
        return gd;
    }

    /** In-place radix-2 FFT. Arrays must be power-of-2 length. */
    private static void fft(double[] re, double[] im) {
        int n = re.length;
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >> 1;
            for (; (j & bit) != 0; bit >>= 1) j ^= bit;
            j ^= bit;
            if (i < j) {
                double t = re[i]; re[i] = re[j]; re[j] = t;
                t = im[i]; im[i] = im[j]; im[j] = t;
            }
        }
        for (int len = 2; len <= n; len <<= 1) {
            double ang = -2 * Math.PI / len;
            double wRe = Math.cos(ang), wIm = Math.sin(ang);
            for (int i = 0; i < n; i += len) {
                double curRe = 1, curIm = 0;
                for (int j = 0; j < len / 2; j++) {
                    double uRe = re[i + j], uIm = im[i + j];
                    double vRe = re[i + j + len / 2] * curRe - im[i + j + len / 2] * curIm;
                    double vIm = re[i + j + len / 2] * curIm + im[i + j + len / 2] * curRe;
                    re[i + j] = uRe + vRe;
                    im[i + j] = uIm + vIm;
                    re[i + j + len / 2] = uRe - vRe;
                    im[i + j + len / 2] = uIm - vIm;
                    double t = curRe * wRe - curIm * wIm;
                    curIm = curRe * wIm + curIm * wRe;
                    curRe = t;
                }
            }
        }
    }
}
