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
 * Generates documentation plots for Reverb menu blocks.
 * Each block is fed an impulse (0.5 amplitude, 1.0 s duration) and the
 * output envelope is plotted at three reverb-time settings (short/medium/long).
 * Blocks with a pre-delay parameter get an additional plot showing the
 * effect of pre-delay at three settings.
 *
 * Control pins are left DISCONNECTED; reverb time is set via the block's
 * setter method.
 */
public class ReverbDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 1.0;
    private static final double IMPULSE_AMP = 0.5;

    // Decimate for plotting: take every Nth sample so plots are not too dense
    private static final int DECIMATE = 32;

    private static final String[] RT_LABELS = {"Short", "Medium", "Long"};
    private static final String[] PD_LABELS = {"No pre-delay", "Medium pre-delay", "Long pre-delay"};

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // =========================================================================
    // Helper: simulate an impulse through a block, return left-channel envelope
    // =========================================================================

    private double[] impulseResponse(SpinCADBlock block,
            String outPin1, String outPin2) throws Exception {
        File impulseWav = generateImpulseWav(SIM_DURATION, IMPULSE_AMP);
        short[] stereo = simulate(block, impulseWav, null, outPin1, outPin2, tempDir);
        if (stereo == null) return null;
        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);
        // Compute envelope: peak absolute value within each window (not point-sample)
        int outLen = audio.length / DECIMATE;
        double[] env = new double[outLen];
        for (int i = 0; i < outLen; i++) {
            double peak = 0;
            for (int j = 0; j < DECIMATE && i * DECIMATE + j < audio.length; j++) {
                double abs = Math.abs(audio[i * DECIMATE + j]);
                if (abs > peak) peak = abs;
            }
            env[i] = peak;
        }
        return env;
    }

    // =========================================================================
    // Plot helper
    // =========================================================================

    private void plotIR(File outFile, String title, double[][] curves,
            String[] labels, int decimatedLen) throws IOException {
        plotIR(outFile, title, curves, labels, decimatedLen, -1);
    }

    private void plotIR(File outFile, String title, double[][] curves,
            String[] labels, int decimatedLen, double maxTimeMs) throws IOException {
        double[] timeMs = new double[decimatedLen];
        for (int i = 0; i < decimatedLen; i++) {
            timeMs[i] = 1000.0 * i * DECIMATE / SAMPLE_RATE;
        }

        // If maxTimeMs specified, trim data to that range
        double xMax;
        if (maxTimeMs > 0) {
            xMax = maxTimeMs;
            int trimLen = decimatedLen;
            for (int i = 0; i < decimatedLen; i++) {
                if (timeMs[i] > maxTimeMs) { trimLen = i; break; }
            }
            if (trimLen < decimatedLen) {
                timeMs = Arrays.copyOf(timeMs, trimLen);
                for (int c = 0; c < curves.length; c++) {
                    curves[c] = Arrays.copyOf(curves[c], trimLen);
                }
            }
        } else {
            xMax = timeMs[timeMs.length - 1];
        }

        double maxY = 0;
        for (double[] c : curves) {
            if (c == null) continue;
            for (double v : c) if (v > maxY) maxY = v;
        }
        if (maxY < 0.01) maxY = 0.5;
        maxY = Math.min(maxY * 1.1, 1.0);

        writePlot(outFile, title, "Time (ms)", "Amplitude",
            0, xMax, 0, maxY,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});
    }

    // =========================================================================
    // Main test
    // =========================================================================

    @Test
    void generateReverbPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // === Adjustable Reverb (reverbCADBlock) ===
        plotReverb("reverb", "Adjustable Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                reverbCADBlock b = new reverbCADBlock(100, 100);
                double[] klaps = {0.3, 0.6, 0.85};
                b.setklap(klaps[level]);
                return impulseResponse(b, "Output_Left", "Output_Right");
            }
        });

        // === Allpass (300 ms) ===
        plotReverb("allpass", "Allpass", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                allpassCADBlock b = new allpassCADBlock(100, 100);
                double[] kiaps = {0.3, 0.5, 0.7};
                b.setkiap(kiaps[level]);
                return impulseResponse(b, "Output", null);
            }
        }, 300);

        // === Ambience (200 ms) ===
        plotReverb("ambience", "Ambience", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                AmbienceCADBlock b = new AmbienceCADBlock(100, 100);
                double[] decays = {0.2, 0.5, 0.9};
                b.setDecay(decays[level]);
                return impulseResponse(b, "Audio Output L", "Audio Output R");
            }
        }, 200);

        // === Chirp (20 ms waveform + spectrogram) ===
        plotChirp(docsDir);

        // === Freeverb (500 ms) ===
        plotReverb("freeverb", "Freeverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                FreeverbCADBlock b = new FreeverbCADBlock(100, 100);
                double[] krts = {0.2, 0.42, 0.7};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        }, 500);

        // === Hall Reverb (has pre-delay) — 500 ms, higher krt to show tail ===
        plotReverb("hall", "Hall Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                reverb_hallCADBlock b = new reverb_hallCADBlock(100, 100);
                double[] krts = {0.5, 0.8, 0.95};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        }, 500);
        // Also plot raw waveform for Hall (Long) to see actual tail content
        plotHallWaveform(docsDir);
        // Hall pre-delay plot
        plotPreDelay("hall", "Hall Reverb Pre-Delay", docsDir, new PreDelayPlotter() {
            public double[] run(int level) throws Exception {
                reverb_hallCADBlock b = new reverb_hallCADBlock(100, 100);
                b.setkrt(0.5);
                double[] inputkaps = {0.0, 0.5, 0.9};
                b.setinputkap(inputkaps[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });

        // === Dattorro Plate Reverb ===
        plotReverb("dattorro", "Dattorro Plate Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                DattorroPlateReverbCADBlock b = new DattorroPlateReverbCADBlock(100, 100);
                double[] decays = {0.2, 0.5, 0.9};
                b.setDecay(decays[level]);
                return impulseResponse(b, "Audio Output L", "Audio Output R");
            }
        });

        // === Reverb Designer ===
        plotReverb("reverbdesigner", "Reverb Designer", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                ReverbDesignerCADBlock b = new ReverbDesignerCADBlock(100, 100);
                double[] rts = {0.2, 0.5, 0.9};
                b.setReverbTime(rts[level]);
                return impulseResponse(b, "Out L", "Out R");
            }
        });

        // === ROM Reverb 1 ===
        plotReverb("rom_rev1", "ROM Reverb 1", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                rom_rev1CADBlock b = new rom_rev1CADBlock(100, 100);
                double[] kapds = {0.3, 0.6, 0.85};
                b.setkapd1(kapds[level]);
                return impulseResponse(b, "Output_Left", "Output_Right");
            }
        });

        // === ROM Reverb 2 ===
        plotReverb("rom_rev2", "ROM Reverb 2", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                rom_rev2CADBlock b = new rom_rev2CADBlock(100, 100);
                double[] rts = {0.3, 0.6, 0.9};
                b.setrevTimeMax(rts[level]);
                return impulseResponse(b, "Output", null);
            }
        });

        // === Room Reverb (has pre-delay) ===
        plotReverb("room", "Room Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                reverb_roomCADBlock b = new reverb_roomCADBlock(100, 100);
                double[] krts = {0.25, 0.5, 0.8};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });
        // Room pre-delay plot
        plotPreDelay("room", "Room Reverb Pre-Delay", docsDir, new PreDelayPlotter() {
            public double[] run(int level) throws Exception {
                reverb_roomCADBlock b = new reverb_roomCADBlock(100, 100);
                b.setkrt(0.5);
                double[] inputkaps = {0.0, 0.5, 0.9};
                b.setinputkap(inputkaps[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });

        // === Min Reverb ===
        plotReverb("minreverb", "Min Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                // MinReverbCADBlock has no public setter for krt;
                // use a single default setting
                MinReverbCADBlock b = new MinReverbCADBlock(100, 100);
                return impulseResponse(b, "Audio Output 1", null);
            }
        });

        // === Min Reverb 2 (stereo) ===
        plotReverb("minreverb2", "Small Reverb (Stereo)", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                MinReverb2CADBlock b = new MinReverb2CADBlock(100, 100);
                double[] klaps = {0.3, 0.6, 0.85};
                b.setklap(klaps[level]);
                return impulseResponse(b, "Output_Left", "Output_Right");
            }
        });

        // === Spring Reverb ===
        plotReverb("spring", "Spring Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                SpringReverbCADBlock b = new SpringReverbCADBlock(100, 100);
                double[] krts = {0.5, 0.75, 0.95};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });

        // === Parker Spring Reverb (500 ms IR + spectrogram) ===
        plotParkerSpring(docsDir);

        System.out.println("\nAll reverb PNGs written to docs/");
    }

    // =========================================================================
    // Plot wrappers
    // =========================================================================

    // =========================================================================
    // Chirp: waveform (20ms) + spectrogram on same time scale
    // =========================================================================

    private void plotChirp(File docsDir) throws Exception {
        double[] apCoeffs = {-0.75, -0.5, 0, 0.5, 0.75};
        String[] apLabels = new String[apCoeffs.length];
        for (int a = 0; a < apCoeffs.length; a++) {
            apLabels[a] = String.format("AP=%.2f", apCoeffs[a]);
        }

        int samples20ms = (int)(SAMPLE_RATE * 0.020);

        // Collect waveforms for all AP coefficients
        double[][] waveforms = new double[apCoeffs.length][];
        double[][] audioFull = new double[apCoeffs.length][];
        double maxAmp = 0;

        for (int a = 0; a < apCoeffs.length; a++) {
            ChirpCADBlock block = new ChirpCADBlock(100, 100);
            block.setkiap(apCoeffs[a]);

            File impulseWav = generateImpulseWav(SIM_DURATION, IMPULSE_AMP);
            short[] stereo = simulate(block, impulseWav, null, "Output", null, tempDir);
            if (stereo == null) {
                System.err.println("  SKIP Chirp AP=" + apCoeffs[a] + ": simulation failed");
                continue;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);
            audioFull[a] = audio;
            int len = Math.min(audio.length, samples20ms);
            waveforms[a] = Arrays.copyOf(audio, len);

            for (double v : waveforms[a]) if (Math.abs(v) > maxAmp) maxAmp = Math.abs(v);
        }

        if (maxAmp < 0.01) maxAmp = 0.5;
        maxAmp = Math.min(maxAmp * 1.1, 1.0);

        // Build time axis
        int len = samples20ms;
        double[] timeMs = new double[len];
        for (int i = 0; i < len; i++) {
            timeMs[i] = 1000.0 * i / SAMPLE_RATE;
        }

        // Ensure all waveforms are same length
        for (int a = 0; a < apCoeffs.length; a++) {
            if (waveforms[a] == null) waveforms[a] = new double[len];
            if (waveforms[a].length < len) waveforms[a] = Arrays.copyOf(waveforms[a], len);
        }

        // Waveform plot with all AP coefficients overlaid
        String[] colors = {COLORS[0], COLORS[1], COLORS[2], COLORS[3], COLORS[4]};
        writePlot(new File(docsDir, "reverb-chirp.png"),
            "Chirp Reverb — Impulse Response", "Time (ms)", "Amplitude",
            0, 20.0, -maxAmp, maxAmp,
            timeMs, waveforms, apLabels, colors);
        System.out.println("  wrote reverb-chirp.png");

        // Spectrogram for each AP coefficient
        for (int a = 0; a < apCoeffs.length; a++) {
            if (audioFull[a] == null) continue;
            int specLen = Math.min(audioFull[a].length, samples20ms);
            String suffix = String.format("%.2f", apCoeffs[a]).replace("-", "neg");
            writeSpectrogram(
                new File(docsDir, "reverb-chirp-spec-" + suffix + ".png"),
                String.format("Chirp Spectrogram (AP=%.2f)", apCoeffs[a]),
                audioFull[a], specLen, SAMPLE_RATE);
            System.out.println("  wrote reverb-chirp-spec-" + suffix + ".png");
        }
    }

    // =========================================================================
    // Parker Spring: impulse response (500ms) + spectrogram
    // =========================================================================

    private void plotParkerSpring(File docsDir) throws Exception {
        // IR at three reverb time settings
        plotReverb("parker-spring", "Parker Spring Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                ParkerSpringReverbCADBlock b = new ParkerSpringReverbCADBlock(100, 100);
                double[] rts = {0.4, 0.7, 0.9};
                b.setReverbTime(rts[level]);
                return impulseResponse(b, "Output L", "Output R");
            }
        }, 500);

        // Spectrogram at default settings — shows chirp dispersion across echoes
        ParkerSpringReverbCADBlock b = new ParkerSpringReverbCADBlock(100, 100);
        File impulseWav = generateImpulseWav(SIM_DURATION, IMPULSE_AMP);
        short[] stereo = simulate(b, impulseWav, null, "Output L", "Output R", tempDir);
        if (stereo != null) {
            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);
            int samples500ms = (int)(SAMPLE_RATE * 0.500);
            int specLen = Math.min(audio.length, samples500ms);
            writeSpectrogram(
                new File(docsDir, "reverb-parker-spring-spec.png"),
                "Parker Spring — Spectrogram (default settings)",
                audio, specLen, SAMPLE_RATE);
            System.out.println("  wrote reverb-parker-spring-spec.png");
        }
    }

    /**
     * Write a spectrogram PNG using short-time FFT with dB magnitude scale.
     * X-axis: time (ms), Y-axis: frequency (Hz), color: magnitude in dB.
     * Uses small FFT window for good time resolution on chirp signals.
     */
    private void writeSpectrogram(File file, String title,
            double[] audio, int totalSamples, int sampleRate) throws IOException {
        int fftSize = 64;   // small window for ~2ms time resolution
        int hopSize = 8;    // ~0.25ms hop for smooth display
        int numFrames = (totalSamples - fftSize) / hopSize;
        if (numFrames < 1) return;

        int freqBins = fftSize / 2;
        double[][] magDb = new double[numFrames][freqBins];
        double maxDb = -200;
        double dbFloor = -60;  // dB floor for colormap

        // Hamming window
        double[] window = new double[fftSize];
        for (int i = 0; i < fftSize; i++) {
            window[i] = 0.54 - 0.46 * Math.cos(2 * Math.PI * i / (fftSize - 1));
        }

        for (int f = 0; f < numFrames; f++) {
            int offset = f * hopSize;
            double[] re = new double[fftSize];
            double[] im = new double[fftSize];
            for (int i = 0; i < fftSize; i++) {
                int idx = offset + i;
                re[i] = (idx < audio.length ? audio[idx] : 0) * window[i];
            }
            fft(re, im);
            for (int b = 0; b < freqBins; b++) {
                double m = Math.sqrt(re[b] * re[b] + im[b] * im[b]);
                double db = 20 * Math.log10(m + 1e-10);
                magDb[f][b] = db;
                if (db > maxDb) maxDb = db;
            }
        }

        // Render spectrogram image
        int padL = 50, padR = 20, padT = 35, padB = 50;
        int plotW = 360, plotH = 280;
        int totalW = padL + plotW + padR;
        int totalH = padT + plotH + padB;
        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, padL + plotW / 2 - fm.stringWidth(title) / 2, padT - 10);

        // Draw spectrogram pixels — dB scale relative to max
        for (int f = 0; f < numFrames; f++) {
            int x = padL + (int)((double) f / numFrames * plotW);
            int w = Math.max(1, (int)((double)(f + 1) / numFrames * plotW) - x);
            for (int b = 0; b < freqBins; b++) {
                int y = padT + plotH - (int)((double)(b + 1) / freqBins * plotH);
                int h = Math.max(1, (int)((double) 1 / freqBins * plotH) + 1);
                double dbRel = magDb[f][b] - maxDb;  // relative to peak
                double val = (dbRel - dbFloor) / (-dbFloor);  // 0 = floor, 1 = peak
                val = Math.max(0, Math.min(1, val));
                g.setColor(spectrogramColor(val));
                g.fillRect(x, y, w, h);
            }
        }

        // Axes
        g.setColor(new Color(0x99, 0x99, 0x99));
        g.drawRect(padL, padT, plotW, plotH);

        g.setFont(new Font("Arial", Font.PLAIN, 9));
        g.setColor(new Color(0x33, 0x33, 0x33));

        // X-axis labels (time in ms)
        double maxTimeMs = 1000.0 * totalSamples / sampleRate;
        for (int tick = 0; tick <= 4; tick++) {
            double frac = tick / 4.0;
            int gx = padL + (int)(frac * plotW);
            String label = String.format("%.0f", frac * maxTimeMs);
            fm = g.getFontMetrics();
            g.drawString(label, gx - fm.stringWidth(label) / 2, padT + plotH + 13);
        }

        // Y-axis labels (frequency in kHz)
        double nyquist = sampleRate / 2.0;
        for (int tick = 0; tick <= 4; tick++) {
            double frac = tick / 4.0;
            int gy = padT + (int)((1.0 - frac) * plotH);
            double freq = frac * nyquist;
            String label;
            if (freq >= 1000) label = String.format("%.0fk", freq / 1000);
            else label = String.format("%.0f", freq);
            fm = g.getFontMetrics();
            g.drawString(label, padL - 4 - fm.stringWidth(label), gy + 3);
        }

        // Axis labels
        g.setFont(new Font("Arial", Font.PLAIN, 10));
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

    /** Simple in-place radix-2 FFT. Arrays must be power-of-2 length. */
    private static void fft(double[] re, double[] im) {
        int n = re.length;
        // Bit-reversal
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >> 1;
            for (; (j & bit) != 0; bit >>= 1) j ^= bit;
            j ^= bit;
            if (i < j) {
                double tr = re[i]; re[i] = re[j]; re[j] = tr;
                double ti = im[i]; im[i] = im[j]; im[j] = ti;
            }
        }
        // FFT
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
                    double newCurRe = curRe * wRe - curIm * wIm;
                    curIm = curRe * wIm + curIm * wRe;
                    curRe = newCurRe;
                }
            }
        }
    }

    // =========================================================================
    // Hall waveform diagnostic — raw waveform to see tail
    // =========================================================================

    private void plotHallWaveform(File docsDir) throws Exception {
        reverb_hallCADBlock b = new reverb_hallCADBlock(100, 100);
        b.setkrt(0.8);

        File impulseWav = generateImpulseWav(SIM_DURATION, IMPULSE_AMP);
        short[] stereo = simulate(b, impulseWav, null, "OutputL", "OutputR", tempDir);
        if (stereo == null) {
            System.err.println("  SKIP Hall waveform: simulation failed");
            return;
        }

        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);

        // Trim to 500ms, decimate by 4 (not 32) for better detail
        int samples500ms = (int)(SAMPLE_RATE * 0.5);
        int len = Math.min(audio.length, samples500ms);
        int decFactor = 4;
        double[] decimated = new double[len / decFactor];
        double[] timeMs = new double[decimated.length];
        for (int i = 0; i < decimated.length; i++) {
            decimated[i] = audio[i * decFactor];
            timeMs[i] = 1000.0 * i * decFactor / SAMPLE_RATE;
        }

        double maxAmp = 0;
        for (double v : decimated) if (Math.abs(v) > maxAmp) maxAmp = Math.abs(v);
        if (maxAmp < 0.01) maxAmp = 0.1;
        maxAmp = Math.min(maxAmp * 1.1, 1.0);

        writePlot(new File(docsDir, "reverb-hall-waveform.png"),
            "Hall Reverb — Raw Waveform (krt=0.8)", "Time (ms)", "Amplitude",
            0, 500.0, -maxAmp, maxAmp,
            timeMs, new double[][]{decimated},
            new String[]{"Left Output"},
            new String[]{COLORS[0]});
        System.out.println("  wrote reverb-hall-waveform.png");
    }

    /** Blue-black-red-yellow colormap for spectrogram. */
    private static Color spectrogramColor(double val) {
        if (val < 0.25) {
            // Black to dark blue
            int b = (int)(val / 0.25 * 180);
            return new Color(0, 0, b);
        } else if (val < 0.5) {
            // Dark blue to red
            double t = (val - 0.25) / 0.25;
            return new Color((int)(t * 200), 0, (int)(180 * (1 - t)));
        } else if (val < 0.75) {
            // Red to orange
            double t = (val - 0.5) / 0.25;
            return new Color(200 + (int)(t * 55), (int)(t * 140), 0);
        } else {
            // Orange to yellow-white
            double t = (val - 0.75) / 0.25;
            return new Color(255, 140 + (int)(t * 115), (int)(t * 100));
        }
    }

    // =========================================================================
    // Plot wrappers
    // =========================================================================

    @FunctionalInterface
    interface ReverbPlotter { double[] run(int level) throws Exception; }

    @FunctionalInterface
    interface PreDelayPlotter { double[] run(int level) throws Exception; }

    private void plotReverb(String fileBase, String title, File docsDir,
            ReverbPlotter plotter) throws Exception {
        plotReverb(fileBase, title, docsDir, plotter, -1);
    }

    private void plotReverb(String fileBase, String title, File docsDir,
            ReverbPlotter plotter, double maxTimeMs) throws Exception {
        double[][] curves = new double[3][];
        int minLen = Integer.MAX_VALUE;
        boolean anySuccess = false;

        for (int i = 0; i < 3; i++) {
            try {
                curves[i] = plotter.run(i);
                if (curves[i] != null) {
                    anySuccess = true;
                    if (curves[i].length < minLen) minLen = curves[i].length;
                }
            } catch (Exception e) {
                System.err.println("  SKIP " + title + " level " + i + ": " + e.getMessage());
            }
        }

        if (!anySuccess) {
            System.err.println("  SKIP " + title + ": all simulations failed");
            return;
        }

        // If some levels failed (e.g. MinReverb with no setter), duplicate the
        // successful curve so the plot still renders three overlapping lines
        int firstGood = -1;
        for (int i = 0; i < 3; i++) {
            if (curves[i] != null) { firstGood = i; break; }
        }
        for (int i = 0; i < 3; i++) {
            if (curves[i] == null) curves[i] = curves[firstGood];
            if (curves[i].length < minLen) minLen = curves[i].length;
        }

        // Trim to common length
        for (int i = 0; i < 3; i++) {
            curves[i] = Arrays.copyOf(curves[i], minLen);
        }

        plotIR(new File(docsDir, "reverb-" + fileBase + ".png"),
            title, curves, RT_LABELS, minLen, maxTimeMs);
        System.out.println("  wrote reverb-" + fileBase + ".png");
    }

    private void plotPreDelay(String fileBase, String title, File docsDir,
            PreDelayPlotter plotter) throws Exception {
        double[][] curves = new double[3][];
        int minLen = Integer.MAX_VALUE;
        boolean anySuccess = false;

        for (int i = 0; i < 3; i++) {
            try {
                curves[i] = plotter.run(i);
                if (curves[i] != null) {
                    anySuccess = true;
                    if (curves[i].length < minLen) minLen = curves[i].length;
                }
            } catch (Exception e) {
                System.err.println("  SKIP " + title + " level " + i + ": " + e.getMessage());
            }
        }

        if (!anySuccess) {
            System.err.println("  SKIP " + title + " (pre-delay): all simulations failed");
            return;
        }

        int firstGood = -1;
        for (int i = 0; i < 3; i++) {
            if (curves[i] != null) { firstGood = i; break; }
        }
        for (int i = 0; i < 3; i++) {
            if (curves[i] == null) curves[i] = curves[firstGood];
            if (curves[i].length < minLen) minLen = curves[i].length;
        }
        for (int i = 0; i < 3; i++) {
            curves[i] = Arrays.copyOf(curves[i], minLen);
        }

        plotIR(new File(docsDir, "reverb-" + fileBase + "-predelay.png"),
            title, curves, PD_LABELS, minLen);
        System.out.println("  wrote reverb-" + fileBase + "-predelay.png");
    }
}
