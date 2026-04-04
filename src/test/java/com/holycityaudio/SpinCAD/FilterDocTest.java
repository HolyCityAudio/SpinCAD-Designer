package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Filter menu blocks.
 * Each block is fed a log-swept sine chirp and the output spectrum is
 * plotted as a frequency response.
 */
public class FilterDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 6.0;  // 3x longer for settling
    private static final double CHIRP_F0 = 20.0;
    // Extend chirp to Nyquist so there's energy across the full display range
    private static final double CHIRP_F1 = SAMPLE_RATE / 2.0;
    private static final double AMPLITUDE = 0.5;

    private static final int FFT_SIZE = 8192;
    private static final int SKIP_SAMPLES = (int)(0.2 * SAMPLE_RATE);

    // Default display range: 20 Hz to 15 kHz
    private static final double DISPLAY_F_MIN = 10;
    private static final double DISPLAY_F_MAX = 5000;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateFilterPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        File chirpWav = generateChirpWav(SIM_DURATION, CHIRP_F0, CHIRP_F1, AMPLITUDE);

        plotLPF1P(docsDir, chirpWav);
        plotHPF1P(docsDir, chirpWav);
        plotSVF2P_LP(docsDir, chirpWav);
        plotSVF2P_BP(docsDir, chirpWav);
        plotSVF2P_HP(docsDir, chirpWav);
        plotSVF2P_Q(docsDir, chirpWav);
        plotSVF2PAdj_LP(docsDir, chirpWav);
        plotSVF2PAdj_BP(docsDir, chirpWav);
        plotSVF2PAdj_HP(docsDir, chirpWav);
        plotSVF2PAdj_Q(docsDir, chirpWav);
        plotLPF4P(docsDir, chirpWav);
        plotHPF2P(docsDir, chirpWav);
        plotNotch(docsDir, chirpWav);
        plotShelvingLP(docsDir, chirpWav);
        plotShelvingHP(docsDir, chirpWav);
        plot1BandEQ(docsDir, chirpWav);
        plot6BandEQ(docsDir, chirpWav);
        plotComb(docsDir, chirpWav);
        plotResonator(docsDir, chirpWav);

        System.out.println("\nAll filter PNGs written to docs/images/");
    }

    // === LPF 1-Pole ===
    private void plotLPF1P(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {0.05, 0.15, 0.4};
        String[] labels = {"Low", "Mid", "High"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            LPF_RDFXCADBlock block = new LPF_RDFXCADBlock(100, 100);
            block.setfreq(freqs[i]);
            short[] stereo = simulate(block, chirpWav, null, "Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP LPF 1P at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-lpf1p.png"),
            "LPF 1-Pole", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-lpf1p.png");
    }

    // === HPF 1-Pole — display 20 Hz to 1 kHz ===
    private void plotHPF1P(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {0.005, 0.015, 0.06};
        String[] labels = {"Low", "Mid", "High"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            HPF_RDFXCADBlock block = new HPF_RDFXCADBlock(100, 100);
            block.setfreq(freqs[i]);
            short[] stereo = simulate(block, chirpWav, null, "Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP HPF 1P at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-hpf1p.png"),
            "HPF 1-Pole", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-hpf1p.png");
    }

    // === SVF 2-Pole Lowpass ===
    private void plotSVF2P_LP(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {200, 740, 3000};
        String[] labels = {"200 Hz", "740 Hz", "3000 Hz"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            SVF2PCADBlock block = new SVF2PCADBlock(100, 100);
            block.setFreq(freqs[i]);
            block.setQ(1.0);
            short[] stereo = simulate(block, chirpWav, null, "Lowpass Out", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P LP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2p.png"),
            "SVF 2-Pole (Lowpass)", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6});
        System.out.println("  wrote filter-svf2p.png");
    }

    // === SVF 2-Pole Bandpass ===
    private void plotSVF2P_BP(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {200, 740, 3000};
        String[] labels = {"200 Hz", "740 Hz", "3000 Hz"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            SVF2PCADBlock block = new SVF2PCADBlock(100, 100);
            block.setFreq(freqs[i]);
            block.setQ(1.0);
            short[] stereo = simulate(block, chirpWav, null, "Bandpass Out", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P BP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2p-bp.png"),
            "SVF 2-Pole (Bandpass)", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6});
        System.out.println("  wrote filter-svf2p-bp.png");
    }

    // === SVF 2-Pole Highpass ===
    private void plotSVF2P_HP(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {200, 740, 3000};
        String[] labels = {"200 Hz", "740 Hz", "3000 Hz"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            SVF2PCADBlock block = new SVF2PCADBlock(100, 100);
            block.setFreq(freqs[i]);
            block.setQ(1.0);
            short[] stereo = simulate(block, chirpWav, null, "Hipass Out", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P HP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2p-hp.png"),
            "SVF 2-Pole (Highpass)", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6});
        System.out.println("  wrote filter-svf2p-hp.png");
    }

    // === SVF 2-Pole varying Q at 1 kHz ===
    // Low amplitude chirp prevents FV-1 internal clipping at high Q
    // (bandpass peak ≈ Q × input_amplitude, must stay < 1.0)
    private void plotSVF2P_Q(File docsDir, File chirpWav) throws Exception {
        File lowChirp = generateChirpWav(SIM_DURATION, CHIRP_F0, CHIRP_F1, 0.05);
        double[] qVals = {0.5, 1.0, 5.0, 10.0};
        String[] labels = {"Q=0.5", "Q=1", "Q=5", "Q=10"};
        double[][] curves = new double[4][];
        double[] freqAxis = null;

        for (int i = 0; i < qVals.length; i++) {
            SVF2PCADBlock block = new SVF2PCADBlock(100, 100);
            block.setFreq(1000);
            block.setQ(qVals[i]);
            short[] stereo = simulate(block, lowChirp, null, "Lowpass Out", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P Q at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(lowChirp, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2p-q.png"),
            "SVF 2-Pole LP (Q variation at 1 kHz)", freqAxis, curves, labels, -40, 20,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]});
        System.out.println("  wrote filter-svf2p-q.png");
    }

    // === SVF 2-Pole Adjustable Lowpass ===
    private void plotSVF2PAdj_LP(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {0.05, 0.15, 0.4};
        String[] labels = {"Low", "Mid", "High"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            SVF_2P_adjustableCADBlock block = new SVF_2P_adjustableCADBlock(100, 100);
            block.setfreq(freqs[i]);
            block.setqMax(2);
            block.setqMin(1);
            short[] stereo = simulate(block, chirpWav, null, "Low Pass Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P Adj LP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2padj.png"),
            "SVF 2-Pole Adj (Lowpass)", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6});
        System.out.println("  wrote filter-svf2padj.png");
    }

    // === SVF 2-Pole Adjustable Bandpass ===
    private void plotSVF2PAdj_BP(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {0.05, 0.15, 0.4};
        String[] labels = {"Low", "Mid", "High"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            SVF_2P_adjustableCADBlock block = new SVF_2P_adjustableCADBlock(100, 100);
            block.setfreq(freqs[i]);
            block.setqMax(2);
            block.setqMin(1);
            short[] stereo = simulate(block, chirpWav, null, "Band Pass Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P Adj BP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2padj-bp.png"),
            "SVF 2-Pole Adj (Bandpass)", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6});
        System.out.println("  wrote filter-svf2padj-bp.png");
    }

    // === SVF 2-Pole Adjustable Highpass ===
    private void plotSVF2PAdj_HP(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {0.05, 0.15, 0.4};
        String[] labels = {"Low", "Mid", "High"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            SVF_2P_adjustableCADBlock block = new SVF_2P_adjustableCADBlock(100, 100);
            block.setfreq(freqs[i]);
            block.setqMax(2);
            block.setqMin(1);
            short[] stereo = simulate(block, chirpWav, null, "High Pass Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P Adj HP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2padj-hp.png"),
            "SVF 2-Pole Adj (Highpass)", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6});
        System.out.println("  wrote filter-svf2padj-hp.png");
    }

    // === SVF 2-Pole Adjustable varying Q at 1 kHz ===
    // Low amplitude chirp prevents FV-1 internal clipping at high Q
    private void plotSVF2PAdj_Q(File docsDir, File chirpWav) throws Exception {
        File lowChirp = generateChirpWav(SIM_DURATION, CHIRP_F0, CHIRP_F1, 0.05);
        double freqCoeff = 0.19;
        double[] qMaxVals = {1.5, 3, 8, 15};
        String[] labels = {"Q=1.5", "Q=3", "Q=8", "Q=15"};
        double[][] curves = new double[4][];
        double[] freqAxis = null;

        for (int i = 0; i < qMaxVals.length; i++) {
            SVF_2P_adjustableCADBlock block = new SVF_2P_adjustableCADBlock(100, 100);
            block.setfreq(freqCoeff);
            block.setqMax(qMaxVals[i]);
            block.setqMin(qMaxVals[i]);
            short[] stereo = simulate(block, lowChirp, null, "Low Pass Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP SVF 2P Adj Q at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(lowChirp, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-svf2padj-q.png"),
            "SVF 2-Pole Adj LP (Q variation ~1 kHz)", freqAxis, curves, labels, -40, 20,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0, 6, -6},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]});
        System.out.println("  wrote filter-svf2padj-q.png");
    }

    // === LPF 2/4-Pole ===
    private void plotLPF4P(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {500, 880, 3000};
        String[] labels = {"500 Hz", "880 Hz", "3000 Hz"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            LPF4PCADBlock block = new LPF4PCADBlock(100, 100);
            block.setFreq(freqs[i]);
            block.setIs4Pole(false);
            block.setQ(10.0);  // kql = 10/10 = 1.0, safely in S1.14 range
            short[] stereo = simulate(block, chirpWav, null, "Low Pass", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP LPF 4P at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-lpf4p.png"),
            "LPF 2/4-Pole", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-lpf4p.png");
    }

    // === HPF 2/4-Pole ===
    private void plotHPF2P(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {100, 880, 3000};
        String[] labels = {"100 Hz", "880 Hz", "3000 Hz"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            HPF2PCADBlock block = new HPF2PCADBlock(100, 100);
            block.setFreq(freqs[i]);
            block.setIs4Pole(false);
            block.setQ(10.0);
            short[] stereo = simulate(block, chirpWav, null, "High Pass", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP HPF 2P at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-hpf2p.png"),
            "HPF 2/4-Pole", freqAxis, curves, labels, -40, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-hpf2p.png");
    }

    // === Notch ===
    private void plotNotch(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {0.05, 0.15, 0.4};
        String[] labels = {"Low", "Mid", "High"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            NotchCADBlock block = new NotchCADBlock(100, 100);
            block.setfreq(freqs[i]);
            block.setqMax(5);
            block.setqMin(1);
            short[] stereo = simulate(block, chirpWav, null, "Output_Notch", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP Notch at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-notch.png"),
            "Notch Filter", freqAxis, curves, labels, -12, 6,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-notch.png");
    }

    // === Shelving Lowpass (corner ~200 Hz, display to 500 Hz) ===
    private void plotShelvingLP(File docsDir, File chirpWav) throws Exception {
        double[] shelvesDb = {-12, -6, 6};
        String[] labels = {"-12 dB", "-6 dB", "+6 dB"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < shelvesDb.length; i++) {
            Shelving_lowpassCADBlock block = new Shelving_lowpassCADBlock(100, 100);
            block.setfreq(0.038);  // ~200 Hz corner
            block.setshelf(shelvesDb[i]);
            short[] stereo = simulate(block, chirpWav, null, "Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP Shelving LP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-shelvinglp.png"),
            "Shelving Lowpass (~200 Hz)", freqAxis, curves, labels, -20, 10,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-shelvinglp.png");
    }

    // === Shelving Highpass (corner ~200 Hz, display to 500 Hz) ===
    private void plotShelvingHP(File docsDir, File chirpWav) throws Exception {
        double[] shelvesDb = {-12, -6, 6};
        String[] labels = {"-12 dB", "-6 dB", "+6 dB"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < shelvesDb.length; i++) {
            Shelving_HipassCADBlock block = new Shelving_HipassCADBlock(100, 100);
            block.setfreq(0.038);  // ~200 Hz corner
            block.setshelf(shelvesDb[i]);
            short[] stereo = simulate(block, chirpWav, null, "Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP Shelving HP at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-shelvinghp.png"),
            "Shelving Highpass (~200 Hz)", freqAxis, curves, labels, -20, 10,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-shelvinghp.png");
    }

    // === 1-Band EQ ===
    private void plot1BandEQ(File docsDir, File chirpWav) throws Exception {
        double[] eqLevels = {-0.5, 0.0, 0.5};
        String[] labels = {"Cut", "Flat", "Boost"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < eqLevels.length; i++) {
            OneBandEQCADBlock block = new OneBandEQCADBlock(100, 100);
            block.setFreq(800);
            block.setqLevel(1.2);
            block.setEqLevel(eqLevels[i]);
            short[] stereo = simulate(block, chirpWav, null, "Audio Output 1", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP 1-Band EQ at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-1bandeq.png"),
            "1-Band EQ (800 Hz)", freqAxis, curves, labels, -12, 12,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-1bandeq.png");
    }

    // === 6-Band EQ ===
    private void plot6BandEQ(File docsDir, File chirpWav) throws Exception {
        String[] labels = {"Low boost", "Mid scoop", "High boost"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        // Low boost
        {
            SixBandEQCADBlock block = new SixBandEQCADBlock(100, 100);
            block.setqLevel(1.2);
            block.seteqLevel(0, 0.5); block.seteqLevel(1, 0.3);
            block.seteqLevel(2, 0.0); block.seteqLevel(3, 0.0);
            block.seteqLevel(4, 0.0); block.seteqLevel(5, 0.0);
            short[] stereo = simulate(block, chirpWav, null, "Audio Output 1", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP 6-Band EQ low boost"); return; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[0] = resp;
        }
        // Mid scoop
        {
            SixBandEQCADBlock block = new SixBandEQCADBlock(100, 100);
            block.setqLevel(1.2);
            block.seteqLevel(0, 0.0); block.seteqLevel(1, 0.0);
            block.seteqLevel(2, -0.4); block.seteqLevel(3, -0.4);
            block.seteqLevel(4, 0.0); block.seteqLevel(5, 0.0);
            short[] stereo = simulate(block, chirpWav, null, "Audio Output 1", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP 6-Band EQ mid scoop"); return; }
            curves[1] = computeFrequencyResponse(chirpWav, stereo);
        }
        // High boost
        {
            SixBandEQCADBlock block = new SixBandEQCADBlock(100, 100);
            block.setqLevel(1.2);
            block.seteqLevel(0, 0.0); block.seteqLevel(1, 0.0);
            block.seteqLevel(2, 0.0); block.seteqLevel(3, 0.0);
            block.seteqLevel(4, 0.3); block.seteqLevel(5, 0.5);
            short[] stereo = simulate(block, chirpWav, null, "Audio Output 1", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP 6-Band EQ high boost"); return; }
            curves[2] = computeFrequencyResponse(chirpWav, stereo);
        }
        writeFilterPlot(new File(docsDir, "filter-6bandeq.png"),
            "6-Band EQ", freqAxis, curves, labels, -12, 12,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-6bandeq.png");
    }

    // === Comb Filter (Long only) ===
    private void plotComb(File docsDir, File chirpWav) throws Exception {
        CombFilterCADBlock block = new CombFilterCADBlock(100, 100);
        block.setgain(0.5);
        block.setdelayLength(2000);
        block.setfeedback(0.7);
        block.setdamping(0.5);
        short[] stereo = simulate(block, chirpWav, null, "Output", null, tempDir);
        if (stereo == null) { System.err.println("  SKIP Comb Long"); return; }
        double[] resp = computeFrequencyResponse(chirpWav, stereo);
        double[] freqAxis = computeFreqAxis(resp.length);

        writeFilterPlot(new File(docsDir, "filter-comb.png"),
            "Comb Filter (Long)", freqAxis, new double[][]{resp}, new String[]{"Long"}, -40, 20,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0},
            new String[]{COLORS[0]});
        System.out.println("  wrote filter-comb.png");
    }

    // === Resonator ===
    private void plotResonator(File docsDir, File chirpWav) throws Exception {
        double[] freqs = {0.05, 0.2, 0.5};
        String[] labels = {"Low", "Mid", "High"};
        double[][] curves = new double[3][];
        double[] freqAxis = null;

        for (int i = 0; i < freqs.length; i++) {
            ResonatorCADBlock block = new ResonatorCADBlock(100, 100);
            block.setfreq(freqs[i]);
            block.setreso(0.01);
            short[] stereo = simulate(block, chirpWav, null, "Output", null, tempDir);
            if (stereo == null) { System.err.println("  SKIP Resonator at " + labels[i]); continue; }
            double[] resp = computeFrequencyResponse(chirpWav, stereo);
            if (freqAxis == null) freqAxis = computeFreqAxis(resp.length);
            curves[i] = resp;
        }
        if (freqAxis == null) return;
        writeFilterPlot(new File(docsDir, "filter-resonator.png"),
            "Resonator", freqAxis, curves, labels, -40, 20,
            DISPLAY_F_MIN, DISPLAY_F_MAX, new double[]{0});
        System.out.println("  wrote filter-resonator.png");
    }

    // ==================== Plot helper with reference lines ====================

    private void writeFilterPlot(File file, String title,
            double[] freqAxis, double[][] curves, String[] labels,
            double yMin, double yMax, double fMin, double fMax,
            double[] refLines) throws IOException {
        writeFilterPlot(file, title, freqAxis, curves, labels, yMin, yMax, fMin, fMax, refLines,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});
    }

    private void writeFilterPlot(File file, String title,
            double[] freqAxis, double[][] curves, String[] labels,
            double yMin, double yMax, double fMin, double fMax,
            double[] refLines, String[] colors) throws IOException {

        // Trim data to display range
        int binMin = Math.max(1, (int)(fMin * FFT_SIZE / SAMPLE_RATE));
        int binMax = Math.min(freqAxis.length - 1, (int)(fMax * FFT_SIZE / SAMPLE_RATE));
        int displayLen = binMax - binMin + 1;

        double[] trimmedFreq = new double[displayLen];
        double[][] trimmedCurves = new double[curves.length][displayLen];
        for (int i = 0; i < displayLen; i++) {
            trimmedFreq[i] = freqAxis[binMin + i];
            for (int c = 0; c < curves.length; c++) {
                if (curves[c] != null) {
                    trimmedCurves[c][i] = curves[c][binMin + i];
                }
            }
        }

        int PLOT_W = 360, PLOT_H = 280;
        int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 85;
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + PAD_B;
        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);
        int px = PAD_L, py = PAD_T;

        double logMin = Math.log10(fMin);
        double logMax = Math.log10(fMax);
        double logRange = logMax - logMin;
        double yRange = yMax - yMin;

        // Background and border
        g.setColor(new Color(0xf8, 0xf8, 0xf8));
        g.fillRect(px, py, PLOT_W, PLOT_H);
        g.setColor(new Color(0xcc, 0xcc, 0xcc));
        g.drawRect(px, py, PLOT_W, PLOT_H);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, px + PLOT_W / 2 - fm.stringWidth(title) / 2, py - 10);

        // Log-scale x-axis grid and ticks
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        double[] majorTicks = {10, 100, 1000, 5000};
        for (double tick : majorTicks) {
            if (tick < fMin || tick > fMax) continue;
            double fx = (Math.log10(tick) - logMin) / logRange;
            int gx = px + (int)(fx * PLOT_W);
            g.setColor(new Color(0xdd, 0xdd, 0xdd));
            g.drawLine(gx, py, gx, py + PLOT_H);
            g.setColor(new Color(0x66, 0x66, 0x66));
            String label;
            if (tick >= 1000) label = String.format("%.0fk", tick / 1000);
            else label = String.format("%.0f", tick);
            fm = g.getFontMetrics();
            g.drawString(label, gx - fm.stringWidth(label) / 2, py + PLOT_H + 13);
        }
        // Minor grid lines at 20,30,...,90, 200,300,...,900, 2000,3000,4000
        g.setColor(new Color(0xee, 0xee, 0xee));
        for (int decade = (int)Math.floor(logMin); decade <= (int)Math.floor(logMax); decade++) {
            for (int m = 2; m <= 9; m++) {
                double freq = m * Math.pow(10, decade);
                if (freq <= fMin || freq >= fMax) continue;
                double fx = (Math.log10(freq) - logMin) / logRange;
                int gx = px + (int)(fx * PLOT_W);
                g.drawLine(gx, py, gx, py + PLOT_H);
            }
        }

        // Y-axis grid at fixed dB intervals: 3 dB for ranges <= 24 dB, 6 dB otherwise
        int dbStep = (yRange <= 24) ? 3 : 6;
        // Start from the first multiple of dbStep >= yMin
        int firstDb = (int)(Math.ceil(yMin / dbStep) * dbStep);
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        Stroke normalStroke = g.getStroke();
        Stroke dashedStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
            BasicStroke.JOIN_MITER, 10, new float[]{6, 4}, 0);
        for (int db = firstDb; db <= (int) yMax; db += dbStep) {
            double frac = (db - yMin) / yRange;
            int gy = py + (int)((1.0 - frac) * PLOT_H);
            if (db == 0) {
                // 0 dB reference: darker dashed line
                g.setStroke(dashedStroke);
                g.setColor(new Color(0x44, 0x44, 0x44));
                g.drawLine(px + 1, gy, px + PLOT_W - 1, gy);
                g.setStroke(normalStroke);
            } else {
                g.setColor(new Color(0xdd, 0xdd, 0xdd));
                g.drawLine(px, gy, px + PLOT_W, gy);
            }
            // Tick label
            g.setColor(new Color(0x66, 0x66, 0x66));
            String label = String.valueOf(db);
            fm = g.getFontMetrics();
            g.drawString(label, px - 4 - fm.stringWidth(label), gy + 3);
        }

        // Draw curves with log x mapping
        for (int ci = 0; ci < trimmedCurves.length; ci++) {
            if (trimmedCurves[ci] == null) continue;
            int[] xPoints = new int[displayLen];
            int[] yPoints = new int[displayLen];
            int count = 0;
            for (int i = 0; i < displayLen; i++) {
                if (trimmedFreq[i] <= 0 || Double.isNaN(trimmedCurves[ci][i])) continue;
                double fx = (Math.log10(trimmedFreq[i]) - logMin) / logRange;
                double fy = (trimmedCurves[ci][i] - yMin) / yRange;
                fy = Math.max(0, Math.min(1, fy));
                xPoints[count] = px + (int)(fx * PLOT_W);
                yPoints[count] = py + (int)((1.0 - fy) * PLOT_H);
                count++;
            }
            if (count > 1) {
                g.setColor(parseColor(colors[ci % colors.length]));
                g.setStroke(new BasicStroke(2.0f));
                g.drawPolyline(Arrays.copyOf(xPoints, count), Arrays.copyOf(yPoints, count), count);
            }
        }

        // Axis labels
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(0x33, 0x33, 0x33));
        fm = g.getFontMetrics();
        String xLabel = "Frequency (Hz)";
        g.drawString(xLabel, px + PLOT_W / 2 - fm.stringWidth(xLabel) / 2, py + PLOT_H + 30);
        AffineTransform origT = g.getTransform();
        String yLabel = "Gain (dB)";
        g.rotate(-Math.PI / 2, px - 35, py + PLOT_H / 2);
        fm = g.getFontMetrics();
        g.drawString(yLabel, px - 35 - fm.stringWidth(yLabel) / 2, py + PLOT_H / 2 + 4);
        g.setTransform(origT);

        // Border
        g.setColor(new Color(0x99, 0x99, 0x99));
        g.drawRect(px, py, PLOT_W, PLOT_H);

        drawLegend(g, px, py + PLOT_H + 52, labels, colors);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    // ==================== Frequency response helpers ====================

    private double[] computeFrequencyResponse(File inputWav, short[] outputStereo) throws Exception {
        short[] inputStereo = readWavSamples(inputWav);
        double[] inputMono = toDouble(extractChannel(inputStereo, 0));
        double[] outputMono = toDouble(extractChannel(outputStereo, 0));

        int start = SKIP_SAMPLES;
        int usable = Math.min(inputMono.length - start, outputMono.length - start);

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

        double[] gainDb = new double[numBins];
        for (int b = 0; b < numBins; b++) {
            double inRms = Math.sqrt(inputPower[b] / numWindows);
            double outRms = Math.sqrt(outputPower[b] / numWindows);
            if (inRms > 1e-10) {
                gainDb[b] = 20 * Math.log10(outRms / inRms);
            } else {
                gainDb[b] = -100;
            }
            gainDb[b] = Math.max(-60, Math.min(30, gainDb[b]));
        }
        return smooth(gainDb, 3);
    }

    private double[] computeFreqAxis(int numBins) {
        double[] freqs = new double[numBins];
        for (int i = 0; i < numBins; i++) {
            freqs[i] = (double) i * SAMPLE_RATE / FFT_SIZE;
        }
        return freqs;
    }

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

        int half = n / 2;
        double[] mag = new double[half];
        for (int i = 0; i < half; i++) {
            mag[i] = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
        }
        return mag;
    }

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
