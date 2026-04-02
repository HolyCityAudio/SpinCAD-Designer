package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;
import com.holycityaudio.SpinCAD.CADBlocks.peak_compressorCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.rms_compressorCADBlock;

/**
 * Compares FV-1 compressor implementations against Faust reference algorithms.
 *
 * Three reference curves per plot:
 *   - Faust reference: double-precision Java, proper dB math with threshold clamping
 *   - FV-1 ideal math: LOG/EXP formula in doubles (no quantization)
 *   - FV-1 simulator: actual SpinSimulator output
 *
 * Produces SVGs overlaying all three for visual comparison plus difference tables.
 *
 * Key analytical differences between Faust and FV-1:
 *
 *   1. NO TRUE THRESHOLD IN FV-1:
 *      FV-1 LOG C,D computes: gain = |env|^C * 2^(16*D)
 *      This is a power law applied to ALL signal levels.
 *      Faust clamps gain reduction to 0 below threshold: max(level-thresh, 0).
 *      Result: FV-1 attenuates signals below threshold; Faust passes them unchanged.
 *
 *   2. GAIN FORMULA:
 *      Faust: gainDb = max(levelDb - threshDb, 0) * (1/ratio - 1)
 *      FV-1:  gain = env^slope * 2^(16*threshZ)  where slope=-0.5*(1-1/ratio), threshZ=threshDb/100
 *      The FV-1 formula is a continuous power law, not a piecewise linear function in dB.
 *
 *   3. LOG/EXP PRECISION:
 *      FV-1 LOG/EXP instructions have ~20-bit effective resolution.
 *      Faust reference uses 64-bit IEEE doubles.
 *
 *   4. FIXED-POINT ARITHMETIC:
 *      FV-1 uses S.23 fixed-point; coefficients quantized to S1.14 (SOF) or S.10 (RDFX).
 *      Gain smoothing, envelope detection all subject to quantization noise.
 */
public class CompressorFaustComparisonTest {

    @TempDir
    File tempDir;

    private static final int SR = ElmProgram.SAMPLERATE;  // 32768
    private static final int NUM_LEVELS = 21;
    private static double[] INPUT_DB;
    private static File[] inputWavs;

    // Sweep parameters matching the individual compressor tests
    private static final double[] RATIOS = {2.0, 4.0, 6.0, 8.0};
    private static final double FIXED_THRESH = -25.0;
    private static final double FIXED_RATIO = 8.0;
    private static final double[] THRESH_DB = {-40.0, -30.0, -20.0, -10.0, 0.0};

    // RMS compressor uses strength instead of ratio
    private static final double[] STRENGTHS = {0.25, 0.5, 0.75, 1.0};
    private static final double FIXED_STRENGTH = 0.75;

    private static final String[] COLORS_4 = {"#2266cc", "#cc4422", "#22aa44", "#aa44cc"};
    private static final String[] COLORS_5 = {"#2266cc", "#cc4422", "#22aa44", "#aa44cc", "#cc8800"};

    // Fast attack/release for steady-state measurement (same as sweep tests)
    private static final double FAST_COEFF = 0.001;

    @BeforeAll
    static void setup() throws IOException {
        System.setProperty("java.awt.headless", "true");
        INPUT_DB = new double[NUM_LEVELS];
        inputWavs = new File[NUM_LEVELS];
        for (int i = 0; i < NUM_LEVELS; i++) {
            INPUT_DB[i] = -40.0 + i * 2.0;
            double amplitude = Math.pow(10.0, INPUT_DB[i] / 20.0);
            inputWavs[i] = generateTestWav(1.0, amplitude);
        }
    }

    // ========================================================================
    // Faust reference: compression_gain_mono (peak detection, hard knee)
    // ========================================================================
    private double faustPeakCompressorRms(double ratio, double threshDb,
            double attCoeff, double relCoeff, double amplitude) {
        int numSamples = SR;  // 1 second
        double freq = 1000.0;
        double envState = 0;
        double gainStateDb = 0;
        double smoothCoeff = attCoeff / 2.0;  // Faust kneesmooth uses att/2

        double sumSq = 0;
        int count = 0;
        int startSample = numSamples * 3 / 4;

        for (int i = 0; i < numSamples; i++) {
            double x = amplitude * Math.sin(2.0 * Math.PI * freq * i / SR);

            // Peak envelope follower with separate attack/release
            double absX = Math.abs(x);
            double diff = absX - envState;
            double coeff = (diff > 0) ? attCoeff : relCoeff;
            envState += coeff * diff;

            // Convert to dB
            double levelDb = 20.0 * Math.log10(Math.max(envState, 1e-30));

            // Gain reduction: only above threshold (Faust hard knee)
            double excessDb = Math.max(levelDb - threshDb, 0.0);
            double gainReductionDb = excessDb * (1.0 / ratio - 1.0);

            // Smooth the gain (Faust kneesmooth)
            gainStateDb += smoothCoeff * (gainReductionDb - gainStateDb);

            // Apply gain
            double gain = Math.pow(10.0, gainStateDb / 20.0);
            double output = x * gain;

            if (i >= startSample) {
                sumSq += output * output;
                count++;
            }
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    // ========================================================================
    // FV-1 ideal math: LOG/EXP with threshold clamping via SKP (in doubles)
    // Models the new .spincad algorithm: LOG 1,0 -> compare -> clamp -> EXP
    // ========================================================================
    private double fv1IdealPeakCompressorRms(double ratio, double threshDb,
            double attCoeff, double relCoeff, double amplitude) {
        int numSamples = SR;
        double freq = 1000.0;
        double envState = 0;
        double gainState = 0;  // in LOG domain (log2/16 scale)

        double ratioFactor = 1.0 / ratio - 1.0;
        double negThreshAcc = -threshDb / 96.33;

        double sumSq = 0;
        int count = 0;
        int startSample = numSamples * 3 / 4;

        for (int i = 0; i < numSamples; i++) {
            double x = amplitude * Math.sin(2.0 * Math.PI * freq * i / SR);

            // Peak envelope (same as Faust)
            double absX = Math.abs(x);
            double diff = absX - envState;
            double coeff = (diff > 0) ? attCoeff : relCoeff;
            envState += coeff * diff;

            // FV-1 LOG 1, 0: raw level in log2/16 domain
            double logVal;
            if (envState > 1e-30) {
                logVal = (Math.log(envState) / Math.log(2.0)) / 16.0;
            } else {
                logVal = -1.0;
            }

            // Threshold clamping (the SKP branch)
            double excess = logVal + negThreshAcc;  // level - threshold
            double gainReduction;
            if (excess >= 0) {
                gainReduction = excess * ratioFactor;  // above threshold: compress
            } else {
                gainReduction = 0;  // below threshold: unity gain
            }

            // RDFX smoothing in log domain
            gainState += attCoeff * (gainReduction - gainState);

            // FV-1 EXP: gain = 2^(ACC * 16)
            double gain = Math.pow(2.0, gainState * 16.0);
            gain = Math.min(gain, 1.0);

            double output = x * gain;

            if (i >= startSample) {
                sumSq += output * output;
                count++;
            }
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    // ========================================================================
    // Faust reference: RMS_compression_gain_mono_db (RMS, strength, post-LOG smoothing)
    // ========================================================================
    private double faustRmsCompressorRms(double strength, double threshDb,
            double attCoeff, double relCoeff, double amplitude) {
        int numSamples = SR;
        double freq = 1000.0;
        double rmsState = 0;
        double rmsCoeff = 0.005;
        double gainStateDb = 0;

        double sumSq = 0;
        int count = 0;
        int startSample = numSamples * 3 / 4;

        for (int i = 0; i < numSamples; i++) {
            double x = amplitude * Math.sin(2.0 * Math.PI * freq * i / SR);

            // RMS: square + short average
            double sq = x * x;
            rmsState += rmsCoeff * (sq - rmsState);
            double rms = Math.sqrt(Math.max(rmsState, 0));

            // Convert to dB
            double levelDb = 20.0 * Math.log10(Math.max(rms, 1e-30));

            // Gain computer (hard knee, strength-based)
            double excessDb = Math.max(levelDb - threshDb, 0.0);
            double gainReductionDb = -strength * excessDb;

            // Post-LOG attack/release smoothing
            double diff = gainReductionDb - gainStateDb;
            double coeff = (diff < 0) ? attCoeff : relCoeff;  // attacking = going more negative
            gainStateDb += coeff * diff;

            // Apply
            double gain = Math.pow(10.0, gainStateDb / 20.0);
            double output = x * gain;

            if (i >= startSample) {
                sumSq += output * output;
                count++;
            }
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    // ========================================================================
    // FV-1 ideal math for RMS compressor (with threshold clamping)
    // LOG 0.5, 0 undoes squaring; then same threshold/clamping as peak
    // ========================================================================
    private double fv1IdealRmsCompressorRms(double strength, double threshDb,
            double attCoeff, double relCoeff, double amplitude) {
        int numSamples = SR;
        double freq = 1000.0;
        double rmsState = 0;
        double rmsCoeff = 0.005;
        double gainState = 0;  // LOG domain

        double negThreshAcc = -threshDb / 96.33;
        double negStrength = -strength;

        double sumSq = 0;
        int count = 0;
        int startSample = numSamples * 3 / 4;

        for (int i = 0; i < numSamples; i++) {
            double x = amplitude * Math.sin(2.0 * Math.PI * freq * i / SR);

            // RMS: square + RDFX average
            double sq = x * x;
            rmsState += rmsCoeff * (sq - rmsState);

            // FV-1 LOG 0.5, 0: undoes squaring in log domain
            // ACC = 0.5 * log2(avg) / 16 ≈ log2(|A|) / 16
            double logVal;
            if (rmsState > 1e-30) {
                logVal = 0.5 * (Math.log(rmsState) / Math.log(2.0)) / 16.0;
            } else {
                logVal = -1.0;
            }

            // Threshold clamping
            double excess = logVal + negThreshAcc;
            double gainReduction;
            if (excess >= 0) {
                gainReduction = excess * negStrength;  // above: compress
            } else {
                gainReduction = 0;  // below: unity
            }

            // Post-LOG attack/release smoothing
            double diff = gainReduction - gainState;
            double coeff = (diff < 0) ? attCoeff : relCoeff;
            gainState += coeff * diff;

            // FV-1 EXP
            double gain = Math.pow(2.0, gainState * 16.0);
            gain = Math.min(gain, 1.0);

            double output = x * gain;

            if (i >= startSample) {
                sumSq += output * output;
                count++;
            }
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    // ========================================================================
    // FV-1 simulator measurement (same wiring as individual sweep tests)
    // ========================================================================
    private double measureFv1PeakOutput(double ratio, double threshDb,
            File wavFile, String tag) throws Exception {
        peak_compressorCADBlock comp = new peak_compressorCADBlock(100, 100);
        comp.setratio(ratio);
        comp.setthreshDb(threshDb);
        comp.setmakeupDb(0.0);
        comp.settrim(1.0);
        comp.setinGain(1.0);
        comp.setattTime(FAST_COEFF);
        comp.setrelTime(FAST_COEFF);
        return runSimulator(comp, wavFile, tag);
    }

    private double measureFv1RmsOutput(double strength, double threshDb,
            File wavFile, String tag) throws Exception {
        rms_compressorCADBlock comp = new rms_compressorCADBlock(100, 100);
        comp.setstrength(strength);
        comp.setthreshDb(threshDb);
        comp.setmakeupDb(0.0);
        comp.settrim(1.0);
        comp.setinGain(1.0);
        comp.setattTime(FAST_COEFF);
        comp.setrelTime(FAST_COEFF);
        return runSimulator(comp, wavFile, tag);
    }

    private double runSimulator(SpinCADBlock comp, File wavFile, String tag) throws Exception {
        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(comp);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        SpinCADPin inputL = inputBlock.getPin("Output 1");
        for (int p = 0; p < comp.pinList.size(); p++) {
            SpinCADPin pin = comp.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_IN) {
                pin.setConnection(inputBlock, inputL);
                break;
            }
        }

        List<SpinCADPin> outputPins = new ArrayList<>();
        for (int p = 0; p < comp.pinList.size(); p++) {
            SpinCADPin pin = comp.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_OUT) {
                outputPins.add(pin);
            }
        }
        assertFalse(outputPins.isEmpty(), "No audio output pins");
        outputBlock.getPin("Input 1").setConnection(comp, outputPins.get(0));
        outputBlock.getPin("Input 2").setConnection(comp, outputPins.get(0));

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        assertNotNull(renderBlock, "Render block null");

        File outFile = new File(tempDir, tag + ".wav");
        SpinSimulator sim = new SpinSimulator(
                renderBlock, wavFile.getAbsolutePath(), outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(30000);
        assertFalse(sim.isAlive(), "Simulator timed out");
        if (sim.getSimulationException() != null) {
            throw new RuntimeException("Sim error", sim.getSimulationException());
        }

        AudioInputStream ais = AudioSystem.getAudioInputStream(outFile);
        byte[] data = readAllBytes(ais);
        ais.close();

        int bytesPerFrame = 4;
        int totalFrames = data.length / bytesPerFrame;
        int startFrame = totalFrames * 3 / 4;
        double sumSq = 0;
        int count = 0;
        for (int f = startFrame; f < totalFrames; f++) {
            int offset = f * bytesPerFrame;
            if (offset + 1 >= data.length) break;
            short sample = (short) ((data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8));
            double normalized = sample / 32768.0;
            sumSq += normalized * normalized;
            count++;
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    // ========================================================================
    // Test: Peak compressor comparison
    // ========================================================================
    @Test
    void testPeakCompressorComparison() throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("PEAK COMPRESSOR: Faust vs FV-1 Ideal vs FV-1 Simulator");
        System.out.println("=".repeat(80));

        // Ratio sweep at fixed threshold
        double[][] faustRatio = new double[RATIOS.length][NUM_LEVELS];
        double[][] fv1IdealRatio = new double[RATIOS.length][NUM_LEVELS];
        double[][] fv1SimRatio = new double[RATIOS.length][NUM_LEVELS];

        for (int ri = 0; ri < RATIOS.length; ri++) {
            for (int li = 0; li < NUM_LEVELS; li++) {
                double amp = Math.pow(10.0, INPUT_DB[li] / 20.0);
                faustRatio[ri][li] = toDb(faustPeakCompressorRms(RATIOS[ri], FIXED_THRESH, FAST_COEFF, FAST_COEFF, amp));
                fv1IdealRatio[ri][li] = toDb(fv1IdealPeakCompressorRms(RATIOS[ri], FIXED_THRESH, FAST_COEFF, FAST_COEFF, amp));
                fv1SimRatio[ri][li] = toDb(measureFv1PeakOutput(RATIOS[ri], FIXED_THRESH, inputWavs[li],
                        String.format("pk_ratio_r%d_l%d", ri, li)));
            }
        }

        // Threshold sweep at fixed ratio
        double[][] faustThresh = new double[THRESH_DB.length][NUM_LEVELS];
        double[][] fv1IdealThresh = new double[THRESH_DB.length][NUM_LEVELS];
        double[][] fv1SimThresh = new double[THRESH_DB.length][NUM_LEVELS];

        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            for (int li = 0; li < NUM_LEVELS; li++) {
                double amp = Math.pow(10.0, INPUT_DB[li] / 20.0);
                faustThresh[ti][li] = toDb(faustPeakCompressorRms(FIXED_RATIO, THRESH_DB[ti], FAST_COEFF, FAST_COEFF, amp));
                fv1IdealThresh[ti][li] = toDb(fv1IdealPeakCompressorRms(FIXED_RATIO, THRESH_DB[ti], FAST_COEFF, FAST_COEFF, amp));
                fv1SimThresh[ti][li] = toDb(measureFv1PeakOutput(FIXED_RATIO, THRESH_DB[ti], inputWavs[li],
                        String.format("pk_thresh_t%d_l%d", ti, li)));
            }
        }

        // Print comparison table for ratio=4, thresh=-25
        printComparisonTable("Peak Compressor (ratio=4:1, thresh=-25 dB)",
                faustRatio[1], fv1IdealRatio[1], fv1SimRatio[1]);

        // Print comparison table for ratio=8, thresh=-20
        printComparisonTable("Peak Compressor (ratio=8:1, thresh=-20 dB)",
                faustThresh[2], fv1IdealThresh[2], fv1SimThresh[2]);

        // Generate SVG
        File svgFile = new File("build/peak_compressor_faust_comparison.svg");
        svgFile.getParentFile().mkdirs();
        writePeakSvg(svgFile, faustRatio, fv1IdealRatio, fv1SimRatio,
                faustThresh, fv1IdealThresh, fv1SimThresh);
        System.out.println("\nSVG: " + svgFile.getAbsolutePath());

        // Analytical summary
        System.out.println("\n--- Analytical Differences ---");
        System.out.println("1. THRESHOLD: Faust clamps gain reduction to 0 below threshold.");
        System.out.println("   FV-1 LOG/EXP applies gain = env^slope * 2^(16*threshZ) at ALL levels.");
        System.out.println("   -> FV-1 attenuates below threshold; Faust passes through unchanged.");
        System.out.println("2. GAIN CURVE: Faust is piecewise-linear in dB (hard knee).");
        System.out.println("   FV-1 is a continuous power law (smooth but no true breakpoint).");
        System.out.println("3. PRECISION: FV-1 LOG/EXP ~20-bit; Faust reference uses 64-bit doubles.");
        System.out.println("4. ENVELOPE: Both use same one-pole abs follower; should match closely.");
    }

    // ========================================================================
    // Test: RMS compressor comparison
    // ========================================================================
    @Test
    void testRmsCompressorComparison() throws Exception {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("RMS COMPRESSOR: Faust vs FV-1 Ideal vs FV-1 Simulator");
        System.out.println("=".repeat(80));

        // Strength sweep at fixed threshold
        double[][] faustStr = new double[STRENGTHS.length][NUM_LEVELS];
        double[][] fv1IdealStr = new double[STRENGTHS.length][NUM_LEVELS];
        double[][] fv1SimStr = new double[STRENGTHS.length][NUM_LEVELS];

        for (int si = 0; si < STRENGTHS.length; si++) {
            for (int li = 0; li < NUM_LEVELS; li++) {
                double amp = Math.pow(10.0, INPUT_DB[li] / 20.0);
                faustStr[si][li] = toDb(faustRmsCompressorRms(STRENGTHS[si], FIXED_THRESH, FAST_COEFF, FAST_COEFF, amp));
                fv1IdealStr[si][li] = toDb(fv1IdealRmsCompressorRms(STRENGTHS[si], FIXED_THRESH, FAST_COEFF, FAST_COEFF, amp));
                fv1SimStr[si][li] = toDb(measureFv1RmsOutput(STRENGTHS[si], FIXED_THRESH, inputWavs[li],
                        String.format("rms_str_s%d_l%d", si, li)));
            }
        }

        // Threshold sweep at fixed strength
        double[][] faustThresh = new double[THRESH_DB.length][NUM_LEVELS];
        double[][] fv1IdealThresh = new double[THRESH_DB.length][NUM_LEVELS];
        double[][] fv1SimThresh = new double[THRESH_DB.length][NUM_LEVELS];

        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            for (int li = 0; li < NUM_LEVELS; li++) {
                double amp = Math.pow(10.0, INPUT_DB[li] / 20.0);
                faustThresh[ti][li] = toDb(faustRmsCompressorRms(FIXED_STRENGTH, THRESH_DB[ti], FAST_COEFF, FAST_COEFF, amp));
                fv1IdealThresh[ti][li] = toDb(fv1IdealRmsCompressorRms(FIXED_STRENGTH, THRESH_DB[ti], FAST_COEFF, FAST_COEFF, amp));
                fv1SimThresh[ti][li] = toDb(measureFv1RmsOutput(FIXED_STRENGTH, THRESH_DB[ti], inputWavs[li],
                        String.format("rms_thresh_t%d_l%d", ti, li)));
            }
        }

        // Print comparison tables
        printComparisonTable("RMS Compressor (strength=0.5, thresh=-25 dB)",
                faustStr[1], fv1IdealStr[1], fv1SimStr[1]);

        printComparisonTable("RMS Compressor (strength=0.75, thresh=-20 dB)",
                faustThresh[2], fv1IdealThresh[2], fv1SimThresh[2]);

        // Generate SVG
        File svgFile = new File("build/rms_compressor_faust_comparison.svg");
        svgFile.getParentFile().mkdirs();
        writeRmsSvg(svgFile, faustStr, fv1IdealStr, fv1SimStr,
                faustThresh, fv1IdealThresh, fv1SimThresh);
        System.out.println("\nSVG: " + svgFile.getAbsolutePath());

        System.out.println("\n--- Analytical Differences ---");
        System.out.println("1. THRESHOLD: Same issue as peak — FV-1 has no true threshold clamping.");
        System.out.println("2. RMS DETECTION: Both use square + one-pole average.");
        System.out.println("   Faust takes sqrt before dB conversion; FV-1 feeds squared avg directly to LOG.");
        System.out.println("   LOG of squared signal = 2 * LOG of signal, absorbed into slope coefficient.");
        System.out.println("3. STRENGTH vs RATIO: Faust strength maps linearly to gain reduction.");
        System.out.println("   FV-1 logC = -0.5*strength maps to the LOG C coefficient (power law exponent).");
        System.out.println("4. POST-LOG SMOOTHING: Both apply attack/release after gain computation.");
    }

    // ========================================================================
    // Comparison table printer
    // ========================================================================
    private void printComparisonTable(String title, double[] faust, double[] fv1Ideal, double[] fv1Sim) {
        System.out.println("\n--- " + title + " ---");
        System.out.printf("%-8s  %-10s  %-10s  %-10s  %-10s  %-10s%n",
                "Input", "Faust", "FV1-Ideal", "FV1-Sim", "Ideal-Faust", "Sim-Faust");
        System.out.println("-".repeat(70));
        for (int li = 0; li < NUM_LEVELS; li += 2) {
            double diffIdeal = fv1Ideal[li] - faust[li];
            double diffSim = fv1Sim[li] - faust[li];
            System.out.printf("%5.0f dB  %7.1f dB  %7.1f dB  %7.1f dB  %+6.1f dB    %+6.1f dB%n",
                    INPUT_DB[li], faust[li], fv1Ideal[li], fv1Sim[li], diffIdeal, diffSim);
        }
    }

    private double toDb(double rms) {
        return 20.0 * Math.log10(rms + 1e-15);
    }

    // ========================================================================
    // SVG generation — overlays Faust (dashed), FV-1 ideal (dotted), FV-1 sim (solid)
    // ========================================================================
    private double[] computeYRange(double[][]... allResults) {
        double dataMin = Double.MAX_VALUE, dataMax = -Double.MAX_VALUE;
        for (double[][] results : allResults) {
            for (double[] curve : results) {
                for (double v : curve) {
                    if (v > -200) {
                        dataMin = Math.min(dataMin, v);
                        dataMax = Math.max(dataMax, v);
                    }
                }
            }
        }
        double yMin = Math.floor((dataMin - 2) / 5) * 5;
        double yMax = yMin + 40;
        if (dataMax > yMax - 2) {
            yMax = Math.ceil((dataMax + 2) / 5) * 5;
            yMin = yMax - 40;
        }
        return new double[]{yMin, yMax};
    }

    private void writePeakSvg(File file,
            double[][] faustRatio, double[][] fv1IdealRatio, double[][] fv1SimRatio,
            double[][] faustThresh, double[][] fv1IdealThresh, double[][] fv1SimThresh)
            throws IOException {
        int plotW = 400, plotH = 400;
        int padL = 55, padR = 30, padT = 40, padB = 50;
        int gapX = 80;
        int cellW = padL + plotW + padR;
        int totalW = cellW * 2 + gapX + 40;
        int totalH = padT + plotH + padB + 100;
        double xMin = -40, xMax = 0;
        double[] yL = computeYRange(faustRatio, fv1IdealRatio, fv1SimRatio);
        double[] yR = computeYRange(faustThresh, fv1IdealThresh, fv1SimThresh);

        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.printf("<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d' font-family='Arial, sans-serif'>%n", totalW, totalH);
        pw.printf("<rect width='%d' height='%d' fill='white'/>%n", totalW, totalH);

        int lx = 20, ly = 10;
        int lpx = lx + padL, lpy = ly + padT;

        drawPlot(pw, lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1],
                String.format("Peak: Vary Ratio (Thresh=%.0f dB)", FIXED_THRESH),
                "Input (dBFS)", "Output (dBFS)");
        for (int ri = 0; ri < RATIOS.length; ri++) {
            drawCurve(pw, fv1SimRatio[ri], lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1], COLORS_4[ri], "");
            drawCurve(pw, faustRatio[ri], lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1], COLORS_4[ri], "6,3");
            drawCurve(pw, fv1IdealRatio[ri], lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1], COLORS_4[ri], "2,2");
        }

        int legY = lpy + plotH + 25;
        for (int ri = 0; ri < RATIOS.length; ri++) {
            int legX = lpx + ri * 100;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS_4[ri]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>%.0f:1</text>%n",
                    legX + 25, legY, COLORS_4[ri], RATIOS[ri]);
        }
        // Line style legend
        int slegY = legY + 20;
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#333' stroke-width='2'/>%n", lpx, slegY-4, lpx+20, slegY-4);
        pw.printf("<text x='%d' y='%d' font-size='10' fill='#333'>FV-1 Simulator</text>%n", lpx+25, slegY);
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#333' stroke-width='2' stroke-dasharray='6,3'/>%n", lpx+140, slegY-4, lpx+160, slegY-4);
        pw.printf("<text x='%d' y='%d' font-size='10' fill='#333'>Faust Reference</text>%n", lpx+165, slegY);
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#333' stroke-width='2' stroke-dasharray='2,2'/>%n", lpx+290, slegY-4, lpx+310, slegY-4);
        pw.printf("<text x='%d' y='%d' font-size='10' fill='#333'>FV-1 Ideal Math</text>%n", lpx+315, slegY);

        // Right plot
        int rx = lx + cellW + gapX;
        int rpx = rx + padL, rpy = ly + padT;
        drawPlot(pw, rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1],
                String.format("Peak: Vary Threshold (Ratio=%.0f:1)", FIXED_RATIO),
                "Input (dBFS)", "Output (dBFS)");
        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            drawCurve(pw, fv1SimThresh[ti], rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1], COLORS_5[ti], "");
            drawCurve(pw, faustThresh[ti], rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1], COLORS_5[ti], "6,3");
            drawCurve(pw, fv1IdealThresh[ti], rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1], COLORS_5[ti], "2,2");
        }
        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            int legX = rpx + ti * 85;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS_5[ti]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>%.0f dB</text>%n",
                    legX + 25, legY, COLORS_5[ti], THRESH_DB[ti]);
        }

        pw.println("</svg>");
        pw.close();
    }

    private void writeRmsSvg(File file,
            double[][] faustStr, double[][] fv1IdealStr, double[][] fv1SimStr,
            double[][] faustThresh, double[][] fv1IdealThresh, double[][] fv1SimThresh)
            throws IOException {
        int plotW = 400, plotH = 400;
        int padL = 55, padR = 30, padT = 40, padB = 50;
        int gapX = 80;
        int cellW = padL + plotW + padR;
        int totalW = cellW * 2 + gapX + 40;
        int totalH = padT + plotH + padB + 100;
        double xMin = -40, xMax = 0;
        double[] yL = computeYRange(faustStr, fv1IdealStr, fv1SimStr);
        double[] yR = computeYRange(faustThresh, fv1IdealThresh, fv1SimThresh);

        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.printf("<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d' font-family='Arial, sans-serif'>%n", totalW, totalH);
        pw.printf("<rect width='%d' height='%d' fill='white'/>%n", totalW, totalH);

        int lx = 20, ly = 10;
        int lpx = lx + padL, lpy = ly + padT;

        drawPlot(pw, lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1],
                String.format("RMS: Vary Strength (Thresh=%.0f dB)", FIXED_THRESH),
                "Input (dBFS)", "Output (dBFS)");
        for (int si = 0; si < STRENGTHS.length; si++) {
            drawCurve(pw, fv1SimStr[si], lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1], COLORS_4[si], "");
            drawCurve(pw, faustStr[si], lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1], COLORS_4[si], "6,3");
            drawCurve(pw, fv1IdealStr[si], lpx, lpy, plotW, plotH, xMin, xMax, yL[0], yL[1], COLORS_4[si], "2,2");
        }

        int legY = lpy + plotH + 25;
        for (int si = 0; si < STRENGTHS.length; si++) {
            int legX = lpx + si * 100;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS_4[si]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>%.2f</text>%n",
                    legX + 25, legY, COLORS_4[si], STRENGTHS[si]);
        }
        int slegY = legY + 20;
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#333' stroke-width='2'/>%n", lpx, slegY-4, lpx+20, slegY-4);
        pw.printf("<text x='%d' y='%d' font-size='10' fill='#333'>FV-1 Simulator</text>%n", lpx+25, slegY);
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#333' stroke-width='2' stroke-dasharray='6,3'/>%n", lpx+140, slegY-4, lpx+160, slegY-4);
        pw.printf("<text x='%d' y='%d' font-size='10' fill='#333'>Faust Reference</text>%n", lpx+165, slegY);
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#333' stroke-width='2' stroke-dasharray='2,2'/>%n", lpx+290, slegY-4, lpx+310, slegY-4);
        pw.printf("<text x='%d' y='%d' font-size='10' fill='#333'>FV-1 Ideal Math</text>%n", lpx+315, slegY);

        int rx = lx + cellW + gapX;
        int rpx = rx + padL, rpy = ly + padT;
        drawPlot(pw, rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1],
                String.format("RMS: Vary Threshold (Strength=%.2f)", FIXED_STRENGTH),
                "Input (dBFS)", "Output (dBFS)");
        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            drawCurve(pw, fv1SimThresh[ti], rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1], COLORS_5[ti], "");
            drawCurve(pw, faustThresh[ti], rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1], COLORS_5[ti], "6,3");
            drawCurve(pw, fv1IdealThresh[ti], rpx, rpy, plotW, plotH, xMin, xMax, yR[0], yR[1], COLORS_5[ti], "2,2");
        }
        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            int legX = rpx + ti * 85;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS_5[ti]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>%.0f dB</text>%n",
                    legX + 25, legY, COLORS_5[ti], THRESH_DB[ti]);
        }

        pw.println("</svg>");
        pw.close();
    }

    // ========================================================================
    // Plot and curve drawing (shared)
    // ========================================================================
    private void drawPlot(PrintWriter pw, int px, int py, int plotW, int plotH,
            double xMin, double xMax, double yMin, double yMax,
            String title, String xLabel, String yLabel) {
        pw.printf("<rect x='%d' y='%d' width='%d' height='%d' fill='#f8f8f8' stroke='#ccc'/>%n",
                px, py, plotW, plotH);
        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='13' font-weight='bold'>%s</text>%n",
                px + plotW / 2, py - 10, title);

        for (double db = xMin; db <= xMax; db += 10) {
            double fx = (db - xMin) / (xMax - xMin);
            int gx = px + (int)(fx * plotW);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#ddd' stroke-width='0.5'/>%n",
                    gx, py, gx, py + plotH);
            pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='9' fill='#666'>%.0f</text>%n",
                    gx, py + plotH + 14, db);
        }
        for (double db = yMin; db <= yMax; db += 10) {
            double fy = 1.0 - (db - yMin) / (yMax - yMin);
            int gy = py + (int)(fy * plotH);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#ddd' stroke-width='0.5'/>%n",
                    px, gy, px + plotW, gy);
            pw.printf("<text x='%d' y='%d' text-anchor='end' font-size='9' fill='#666'>%.0f</text>%n",
                    px - 5, gy + 3, db);
        }

        // Unity gain line clipped to plot
        double uStart = Math.max(xMin, yMin);
        double uEnd = Math.min(xMax, yMax);
        if (uStart < uEnd) {
            double ux1 = (uStart - xMin) / (xMax - xMin);
            double ux2 = (uEnd - xMin) / (xMax - xMin);
            double uy1 = 1.0 - (uStart - yMin) / (yMax - yMin);
            double uy2 = 1.0 - (uEnd - yMin) / (yMax - yMin);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#aaa' stroke-width='1' stroke-dasharray='4,3'/>%n",
                    px + (int)(ux1 * plotW), py + (int)(uy1 * plotH),
                    px + (int)(ux2 * plotW), py + (int)(uy2 * plotH));
        }

        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='10' fill='#333'>%s</text>%n",
                px + plotW / 2, py + plotH + 35, xLabel);
        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='10' fill='#333' transform='rotate(-90,%d,%d)'>%s</text>%n",
                px - 40, py + plotH / 2, px - 40, py + plotH / 2, yLabel);
        pw.printf("<rect x='%d' y='%d' width='%d' height='%d' fill='none' stroke='#999'/>%n",
                px, py, plotW, plotH);
    }

    private void drawCurve(PrintWriter pw, double[] data, int px, int py,
            int plotW, int plotH, double xMin, double xMax, double yMin, double yMax,
            String color, String dashArray) {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < NUM_LEVELS; i++) {
            double inDb = INPUT_DB[i];
            double outDb = Math.max(yMin, Math.min(yMax, data[i]));
            double fx = (inDb - xMin) / (xMax - xMin);
            double fy = 1.0 - (outDb - yMin) / (yMax - yMin);
            fy = Math.max(0, Math.min(1, fy));
            int sx = px + (int)(fx * plotW);
            int sy = py + (int)(fy * plotH);
            if (i == 0) path.append(String.format("M%d,%d", sx, sy));
            else path.append(String.format(" L%d,%d", sx, sy));
        }
        if (dashArray.isEmpty()) {
            pw.printf("<path d='%s' fill='none' stroke='%s' stroke-width='2'/>%n", path, color);
        } else {
            pw.printf("<path d='%s' fill='none' stroke='%s' stroke-width='2' stroke-dasharray='%s'/>%n",
                    path, color, dashArray);
        }
    }

    // ========================================================================
    // WAV utilities
    // ========================================================================
    private static File generateTestWav(double durationSeconds, double amplitude) throws IOException {
        int sampleRate = SR;
        int numFrames = (int) (sampleRate * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        double freq = 1000.0;
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / sampleRate;
            short sample = (short) (Short.MAX_VALUE * amplitude * Math.sin(2.0 * Math.PI * freq * t));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }
        File wavFile = File.createTempFile("faust_comp_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    private byte[] readAllBytes(AudioInputStream ais) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }
}
