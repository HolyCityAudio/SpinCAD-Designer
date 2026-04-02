package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Filter menu blocks.
 * Each block is fed a log-swept sine chirp at three frequency settings
 * and the output spectrum is plotted as a frequency response.
 */
public class FilterDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 2.0;
    private static final double CHIRP_F0 = 20.0;
    private static final double CHIRP_F1 = 15000.0;
    private static final double AMPLITUDE = 0.5;

    // Number of FFT bins for frequency response display
    private static final int FFT_SIZE = 4096;
    // Skip initial samples for filter settling
    private static final int SKIP_SAMPLES = (int)(0.05 * SAMPLE_RATE);

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateFilterPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        File chirpWav = generateChirpWav(SIM_DURATION, CHIRP_F0, CHIRP_F1, AMPLITUDE);

        // === LPF 1-Pole (RDFX) ===
        {
            double[] freqs = {0.05, 0.15, 0.4};
            String[] labels = {"Low", "Mid", "High"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                LPF_RDFXCADBlock block = new LPF_RDFXCADBlock(100, 100);
                block.setfreq(freqs[i]);

                short[] stereo = simulate(block, chirpWav, null,
                    "Output", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP LPF 1P at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-lpf1p.png"),
                "LPF 1-Pole", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-lpf1p.png");
        }

        // === HPF 1-Pole (RDFX) ===
        {
            double[] freqs = {0.005, 0.015, 0.06};
            String[] labels = {"Low", "Mid", "High"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                HPF_RDFXCADBlock block = new HPF_RDFXCADBlock(100, 100);
                block.setfreq(freqs[i]);

                short[] stereo = simulate(block, chirpWav, null,
                    "Output", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP HPF 1P at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-hpf1p.png"),
                "HPF 1-Pole", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-hpf1p.png");
        }

        // === SVF 2-Pole (lowpass output) ===
        {
            double[] freqs = {200, 740, 3000};
            String[] labels = {"200 Hz", "740 Hz", "3000 Hz"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                SVF2PCADBlock block = new SVF2PCADBlock(100, 100);
                block.setFreq(freqs[i]);
                block.setQ(1.0);

                short[] stereo = simulate(block, chirpWav, null,
                    "Lowpass Out", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP SVF 2P LP at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-svf2p.png"),
                "SVF 2-Pole (Lowpass)", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-svf2p.png");
        }

        // === SVF 2-Pole Adjustable (lowpass output) ===
        {
            double[] freqs = {0.05, 0.15, 0.4};
            String[] labels = {"Low", "Mid", "High"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                SVF_2P_adjustableCADBlock block = new SVF_2P_adjustableCADBlock(100, 100);
                block.setfreq(freqs[i]);
                block.setqMax(5);
                block.setqMin(1);

                short[] stereo = simulate(block, chirpWav, null,
                    "Low Pass Output", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP SVF 2P Adj at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-svf2padj.png"),
                "SVF 2-Pole Adjustable (LP)", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-svf2padj.png");
        }

        // === LPF 4-Pole (2-pole mode) ===
        {
            double[] freqs = {200, 880, 3000};
            String[] labels = {"200 Hz", "880 Hz", "3000 Hz"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                LPF4PCADBlock block = new LPF4PCADBlock(100, 100);
                block.setFreq(freqs[i]);
                block.setIs4Pole(false);
                block.setQ(0.2);

                short[] stereo = simulate(block, chirpWav, null,
                    "Low Pass", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP LPF 4P at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-lpf4p.png"),
                "LPF 2/4-Pole", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-lpf4p.png");
        }

        // === HPF 2-Pole ===
        {
            double[] freqs = {100, 880, 3000};
            String[] labels = {"100 Hz", "880 Hz", "3000 Hz"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                HPF2PCADBlock block = new HPF2PCADBlock(100, 100);
                block.setFreq(freqs[i]);
                block.setIs4Pole(false);
                block.setQ(0.2);

                short[] stereo = simulate(block, chirpWav, null,
                    "High Pass", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP HPF 2P at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-hpf2p.png"),
                "HPF 2/4-Pole", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-hpf2p.png");
        }

        // === Notch ===
        {
            double[] freqs = {0.05, 0.15, 0.4};
            String[] labels = {"Low", "Mid", "High"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                NotchCADBlock block = new NotchCADBlock(100, 100);
                block.setfreq(freqs[i]);
                block.setqMax(5);
                block.setqMin(1);

                short[] stereo = simulate(block, chirpWav, null,
                    "Output_Notch", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP Notch at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-notch.png"),
                "Notch Filter", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-notch.png");
        }

        // === Shelving Lowpass ===
        {
            double[] shelves = {0.2, 0.5, 0.8};
            String[] labels = {"Shelf 0.2", "Shelf 0.5", "Shelf 0.8"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < shelves.length; i++) {
                Shelving_lowpassCADBlock block = new Shelving_lowpassCADBlock(100, 100);
                block.setfreq(0.15);
                block.setshelf(shelves[i]);

                short[] stereo = simulate(block, chirpWav, null,
                    "Output", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP Shelving LP at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-shelvinglp.png"),
                "Shelving Lowpass", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-shelvinglp.png");
        }

        // === Shelving Highpass ===
        {
            double[] shelves = {0.2, 0.5, 0.8};
            String[] labels = {"Shelf 0.2", "Shelf 0.5", "Shelf 0.8"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < shelves.length; i++) {
                Shelving_HipassCADBlock block = new Shelving_HipassCADBlock(100, 100);
                block.setfreq(0.15);
                block.setshelf(shelves[i]);

                short[] stereo = simulate(block, chirpWav, null,
                    "Output", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP Shelving HP at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-shelvinghp.png"),
                "Shelving Highpass", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 6,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-shelvinghp.png");
        }

        // === 1-Band EQ ===
        {
            double[] eqLevels = {-0.5, 0.0, 0.5};
            String[] labels = {"Cut", "Flat", "Boost"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < eqLevels.length; i++) {
                OneBandEQCADBlock block = new OneBandEQCADBlock(100, 100);
                block.setFreq(800);
                block.setqLevel(1.2);
                block.setEqLevel(eqLevels[i]);

                short[] stereo = simulate(block, chirpWav, null,
                    "Audio Output 1", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP 1-Band EQ at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-1bandeq.png"),
                "1-Band EQ (800 Hz)", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -20, 20,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-1bandeq.png");
        }

        // === 6-Band EQ ===
        {
            // Show three different EQ curve shapes
            String[] labels = {"Low boost", "Mid scoop", "High boost"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            // Low boost: boost bands 0,1 (80, 160 Hz)
            {
                SixBandEQCADBlock block = new SixBandEQCADBlock(100, 100);
                block.setqLevel(1.2);
                block.seteqLevel(0, 0.5);
                block.seteqLevel(1, 0.3);
                block.seteqLevel(2, 0.0);
                block.seteqLevel(3, 0.0);
                block.seteqLevel(4, 0.0);
                block.seteqLevel(5, 0.0);

                short[] stereo = simulate(block, chirpWav, null,
                    "Audio Output 1", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP 6-Band EQ low boost"); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[0] = resp;
            }

            // Mid scoop: cut bands 2,3 (320, 640 Hz)
            {
                SixBandEQCADBlock block = new SixBandEQCADBlock(100, 100);
                block.setqLevel(1.2);
                block.seteqLevel(0, 0.0);
                block.seteqLevel(1, 0.0);
                block.seteqLevel(2, -0.4);
                block.seteqLevel(3, -0.4);
                block.seteqLevel(4, 0.0);
                block.seteqLevel(5, 0.0);

                short[] stereo = simulate(block, chirpWav, null,
                    "Audio Output 1", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP 6-Band EQ mid scoop"); return; }

                curves[1] = computeFrequencyResponse(chirpWav, stereo);
            }

            // High boost: boost bands 4,5 (1280, 2560 Hz)
            {
                SixBandEQCADBlock block = new SixBandEQCADBlock(100, 100);
                block.setqLevel(1.2);
                block.seteqLevel(0, 0.0);
                block.seteqLevel(1, 0.0);
                block.seteqLevel(2, 0.0);
                block.seteqLevel(3, 0.0);
                block.seteqLevel(4, 0.3);
                block.seteqLevel(5, 0.5);

                short[] stereo = simulate(block, chirpWav, null,
                    "Audio Output 1", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP 6-Band EQ high boost"); return; }

                curves[2] = computeFrequencyResponse(chirpWav, stereo);
            }

            writePlot(new File(docsDir, "filter-6bandeq.png"),
                "6-Band EQ", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -20, 20,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-6bandeq.png");
        }

        // === Comb Filter ===
        {
            double[] delays = {500, 1116, 2000};
            String[] labels = {"Short", "Medium", "Long"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < delays.length; i++) {
                CombFilterCADBlock block = new CombFilterCADBlock(100, 100);
                block.setgain(0.5);
                block.setdelayLength(delays[i]);
                block.setfeedback(0.7);
                block.setdamping(0.5);

                short[] stereo = simulate(block, chirpWav, null,
                    "Output", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP Comb at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-comb.png"),
                "Comb Filter", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 20,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-comb.png");
        }

        // === Resonator ===
        {
            double[] freqs = {0.05, 0.2, 0.5};
            String[] labels = {"Low", "Mid", "High"};
            double[][] curves = new double[3][];
            double[] freqAxis = null;

            for (int i = 0; i < freqs.length; i++) {
                ResonatorCADBlock block = new ResonatorCADBlock(100, 100);
                block.setfreq(freqs[i]);
                block.setreso(0.01);

                short[] stereo = simulate(block, chirpWav, null,
                    "Output", null, tempDir);
                if (stereo == null) { System.err.println("  SKIP Resonator at " + labels[i]); return; }

                double[] resp = computeFrequencyResponse(chirpWav, stereo);
                if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
                curves[i] = resp;
            }

            writePlot(new File(docsDir, "filter-resonator.png"),
                "Resonator", "Frequency (Hz)", "Gain (dB)",
                freqAxis[0], freqAxis[freqAxis.length - 1], -40, 20,
                freqAxis, curves, labels,
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
            System.out.println("  wrote filter-resonator.png");
        }

        System.out.println("\nAll filter PNGs written to docs/");
    }

    // ==================== Frequency response helpers ====================

    /**
     * Compute frequency response in dB by comparing output spectrum to input spectrum.
     * Uses a simple DFT magnitude approach with windowing and averaging.
     */
    private double[] computeFrequencyResponse(File inputWav, short[] outputStereo) throws Exception {
        short[] inputStereo = readWavSamples(inputWav);
        double[] inputMono = toDouble(extractChannel(inputStereo, 0));
        double[] outputMono = toDouble(extractChannel(outputStereo, 0));

        // Use data after settling
        int start = SKIP_SAMPLES;
        int usableInput = inputMono.length - start;
        int usableOutput = outputMono.length - start;
        int usable = Math.min(usableInput, usableOutput);

        // Average over multiple overlapping windows
        int hopSize = FFT_SIZE / 2;
        int numWindows = Math.max(1, (usable - FFT_SIZE) / hopSize);

        int numBins = FFT_SIZE / 2;
        double[] inputPower = new double[numBins];
        double[] outputPower = new double[numBins];

        for (int w = 0; w < numWindows; w++) {
            int offset = start + w * hopSize;

            double[] inWin = applyHannWindow(inputMono, offset, FFT_SIZE);
            double[] outWin = applyHannWindow(outputMono, offset, FFT_SIZE);

            double[] inMag = fftMagnitude(inWin);
            double[] outMag = fftMagnitude(outWin);

            for (int b = 0; b < numBins; b++) {
                inputPower[b] += inMag[b] * inMag[b];
                outputPower[b] += outMag[b] * outMag[b];
            }
        }

        // Compute gain in dB, smoothed
        double[] gainDb = new double[numBins];
        for (int b = 0; b < numBins; b++) {
            double inRms = Math.sqrt(inputPower[b] / numWindows);
            double outRms = Math.sqrt(outputPower[b] / numWindows);
            if (inRms > 1e-10) {
                gainDb[b] = 20 * Math.log10(outRms / inRms);
            } else {
                gainDb[b] = -100;
            }
            // Clamp
            gainDb[b] = Math.max(-60, Math.min(30, gainDb[b]));
        }

        // Smooth with a small moving average for cleaner plots
        return smooth(gainDb, 5);
    }

    /** Compute frequency axis in Hz for the bins. */
    private double[] computeFreqAxis(int numBins) {
        double[] freqs = new double[numBins];
        for (int i = 0; i < numBins; i++) {
            freqs[i] = (double) i * SAMPLE_RATE / FFT_SIZE;
        }
        return freqs;
    }

    /** Apply a Hann window to a segment of data. */
    private double[] applyHannWindow(double[] data, int offset, int size) {
        double[] windowed = new double[size];
        for (int i = 0; i < size; i++) {
            int idx = offset + i;
            double sample = (idx < data.length) ? data[idx] : 0;
            double window = 0.5 * (1 - Math.cos(2 * Math.PI * i / (size - 1)));
            windowed[i] = sample * window;
        }
        return windowed;
    }

    /**
     * Compute FFT magnitude spectrum (first half only).
     * Simple radix-2 DIT FFT.
     */
    private double[] fftMagnitude(double[] data) {
        int n = data.length;
        // Bit-reversal permutation
        double[] re = new double[n];
        double[] im = new double[n];
        int bits = Integer.numberOfTrailingZeros(n);

        for (int i = 0; i < n; i++) {
            int rev = Integer.reverse(i) >>> (32 - bits);
            re[rev] = data[i];
        }

        // FFT butterfly
        for (int size = 2; size <= n; size *= 2) {
            int halfSize = size / 2;
            double angle = -2 * Math.PI / size;
            double wRe = Math.cos(angle);
            double wIm = Math.sin(angle);

            for (int start = 0; start < n; start += size) {
                double curRe = 1.0, curIm = 0.0;
                for (int j = 0; j < halfSize; j++) {
                    int even = start + j;
                    int odd = start + j + halfSize;
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

        // Magnitude of first half
        int half = n / 2;
        double[] mag = new double[half];
        for (int i = 0; i < half; i++) {
            mag[i] = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
        }
        return mag;
    }

    /** Simple moving average smoothing. */
    private double[] smooth(double[] data, int windowSize) {
        double[] result = new double[data.length];
        int half = windowSize / 2;
        for (int i = 0; i < data.length; i++) {
            double sum = 0;
            int count = 0;
            for (int j = Math.max(0, i - half); j <= Math.min(data.length - 1, i + half); j++) {
                sum += data[j];
                count++;
            }
            result[i] = sum / count;
        }
        return result;
    }
}
