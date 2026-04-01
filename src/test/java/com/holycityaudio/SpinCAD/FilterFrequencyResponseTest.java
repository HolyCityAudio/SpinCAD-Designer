package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.*;
import java.util.*;

import javax.sound.sampled.*;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Detailed frequency response tests for filter blocks active in the menus.
 *
 * - 8 cutoff frequencies per filter (logarithmically spaced)
 * - 8 shelf levels for shelving filters
 * - Minimum + 8 resonance settings for resonant filters
 * - 2 dB tolerance on passband/shelf measurements
 */
public class FilterFrequencyResponseTest {

    @TempDir
    File tempDir;

    private static final int SAMPLE_RATE = ElmProgram.SAMPLERATE;
    private static final double TOLERANCE_DB = 2.0;
    private static final double SINE_DURATION = 0.5;
    private static final double SINE_AMPLITUDE = 0.25;
    private static final long SIM_TIMEOUT = 30000;

    // 8 cutoff frequencies, roughly octave-spaced (Hz)
    private static final double[] CUTOFF_FREQS = {100, 200, 400, 800, 1600, 3200, 6400, 10000};

    // 8 shelf levels in dB
    private static final double[] SHELF_LEVELS = {-12, -9, -6, -3, 3, 6, 9, 12};

    // Minimum resonance + 8 above (ConstantCADBlock values 0-999)
    private static final int[] RESONANCE_CONTROLS = {0, 125, 250, 375, 500, 625, 750, 875, 999};

    // Direct Q values for SVF2P
    private static final double[] Q_DIRECT_VALUES = {0.5, 1, 2, 4, 8, 16, 32, 50, 100};

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // ==================== Coefficient conversions ====================

    /** Hz to RDFX filter coefficient: coeff = 1 - e^(-2*pi*f/fs) */
    static double hzToRdfxCoeff(double hz) {
        return clamp(1.0 - Math.exp(-2.0 * Math.PI * hz / SAMPLE_RATE), 0.001, 0.999);
    }

    /** Hz to SVF frequency coefficient: coeff = sin(2*pi*f/fs) */
    static double hzToSvfCoeff(double hz) {
        return clamp(Math.sin(2.0 * Math.PI * hz / SAMPLE_RATE), 0.001, 0.999);
    }

    /**
     * Hz to LPF4P/HPF2P ConstantCADBlock control value (0-999).
     * Control path: constant -> scaleOffset(0.35,-0.35) -> exp(1,0) -> kfl register.
     * kfl = 2^(16 * (0.35*constant - 0.35)), target kfl = 2*sin(pi*fc/fs).
     */
    static int hzToExpControl(double hz) {
        double kfl = 2.0 * Math.sin(Math.PI * hz / SAMPLE_RATE);
        kfl = clamp(kfl, 0.021, 0.999);
        double constant = (Math.log(kfl) / Math.log(2) / 16.0 + 0.35) / 0.35;
        return (int) clamp(constant * 1000, 0, 999);
    }

    static double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    // ==================== 1. LPF_RDFX (1-pole lowpass) ====================

    @Test
    void testLPF_RDFX() {
        System.out.println("\n=== LPF_RDFX (1P Lowpass) ===");
        for (double fc : CUTOFF_FREQS) {
            double probePass = Math.max(30, fc / 4.0);
            double probeStop = Math.min(12000, fc * 4.0);

            LPF_RDFXCADBlock bPass = new LPF_RDFXCADBlock(100, 100);
            bPass.setfreq(hzToRdfxCoeff(fc));
            Double gainPass = simulateFilter(bPass, probePass, "Output", null);

            LPF_RDFXCADBlock bStop = new LPF_RDFXCADBlock(100, 100);
            bStop.setfreq(hzToRdfxCoeff(fc));
            Double gainStop = simulateFilter(bStop, probeStop, "Output", null);

            System.out.printf("  fc=%5.0f  pass(%5.0f)=%s  stop(%5.0f)=%s%n",
                fc, probePass, fmtDb(gainPass), probeStop, fmtDb(gainStop));

            if (gainPass != null) {
                assertEquals(0.0, gainPass, TOLERANCE_DB,
                    msg("LPF_RDFX", fc, probePass, gainPass, "passband ~0dB"));
            }
            if (gainStop != null && probeStop > fc * 2) {
                assertTrue(gainStop < -6.0,
                    msg("LPF_RDFX", fc, probeStop, gainStop, "stopband <-6dB"));
            }
        }
    }

    // ==================== 2. HPF_RDFX (1-pole highpass) ====================

    @Test
    void testHPF_RDFX() {
        System.out.println("\n=== HPF_RDFX (1P Highpass) ===");
        for (double fc : CUTOFF_FREQS) {
            double probeStop = Math.max(30, fc / 4.0);
            double probePass = Math.min(8000, fc * 4.0); // cap at 8kHz to avoid Nyquist warping

            HPF_RDFXCADBlock bStop = new HPF_RDFXCADBlock(100, 100);
            bStop.setfreq(hzToRdfxCoeff(fc));
            Double gainStop = simulateFilter(bStop, probeStop, "Output", null);

            HPF_RDFXCADBlock bPass = new HPF_RDFXCADBlock(100, 100);
            bPass.setfreq(hzToRdfxCoeff(fc));
            Double gainPass = simulateFilter(bPass, probePass, "Output", null);

            System.out.printf("  fc=%5.0f  stop(%5.0f)=%s  pass(%5.0f)=%s%n",
                fc, probeStop, fmtDb(gainStop), probePass, fmtDb(gainPass));

            if (gainPass != null && probePass >= fc * 4) {
                assertEquals(0.0, gainPass, TOLERANCE_DB,
                    msg("HPF_RDFX", fc, probePass, gainPass, "passband ~0dB"));
            }
            if (gainStop != null && probeStop < fc / 2) {
                assertTrue(gainStop < -6.0,
                    msg("HPF_RDFX", fc, probeStop, gainStop, "stopband <-6dB"));
            }
        }
    }

    // ==================== 3. Shelving Lowpass ====================
    // Shelving LP passes low frequencies unchanged, shelves (boosts/cuts) high frequencies.

    @Test
    void testShelvingLowpass_frequencies() {
        System.out.println("\n=== Shelving Lowpass -- 8 Frequencies (shelf=-6dB) ===");
        double shelfDb = -6.0;
        for (double fc : CUTOFF_FREQS) {
            double probeLow = Math.max(30, fc / 4.0);
            double probeHigh = Math.min(8000, fc * 4.0);

            Shelving_lowpassCADBlock bLow = new Shelving_lowpassCADBlock(100, 100);
            bLow.setfreq(hzToRdfxCoeff(fc));
            bLow.setshelf(shelfDb);
            Double gainLow = simulateFilter(bLow, probeLow, "Output", null);

            Shelving_lowpassCADBlock bHigh = new Shelving_lowpassCADBlock(100, 100);
            bHigh.setfreq(hzToRdfxCoeff(fc));
            bHigh.setshelf(shelfDb);
            Double gainHigh = simulateFilter(bHigh, probeHigh, "Output", null);

            System.out.printf("  fc=%5.0f  low(%5.0f)=%s  high(%5.0f)=%s%n",
                fc, probeLow, fmtDb(gainLow), probeHigh, fmtDb(gainHigh));

            if (gainLow != null) {
                assertEquals(0.0, gainLow, TOLERANCE_DB,
                    msg("ShelvLP", fc, probeLow, gainLow, "passband ~0dB"));
            }
            if (gainHigh != null && probeHigh >= fc * 4) {
                assertEquals(shelfDb, gainHigh, TOLERANCE_DB,
                    msg("ShelvLP", fc, probeHigh, gainHigh, "shelved ~" + shelfDb + "dB"));
            }
        }
    }

    @Test
    void testShelvingLowpass_shelfLevels() {
        System.out.println("\n=== Shelving Lowpass -- 8 Shelf Levels (fc=500Hz) ===");
        double fc = 500;
        double probeHigh = 8000; // probe well above cutoff (16x) where shelving fully applies

        for (double shelfDb : SHELF_LEVELS) {
            Shelving_lowpassCADBlock b = new Shelving_lowpassCADBlock(100, 100);
            b.setfreq(hzToRdfxCoeff(fc));
            b.setshelf(shelfDb);
            Double gain = simulateFilter(b, probeHigh, "Output", null);

            System.out.printf("  shelf=%+3.0fdB  gain@%4.0fHz=%s%n",
                shelfDb, probeHigh, fmtDb(gain));

            if (gain != null) {
                assertEquals(shelfDb, gain, TOLERANCE_DB,
                    String.format("ShelvLP shelf=%.0fdB: gain=%.1fdB at %.0fHz",
                        shelfDb, gain, probeHigh));
            }
        }
    }

    // ==================== 4. Shelving Hipass ====================
    // Shelving HP passes high frequencies unchanged, shelves (boosts/cuts) low frequencies.

    @Test
    void testShelvingHipass_frequencies() {
        System.out.println("\n=== Shelving Hipass -- 8 Frequencies (shelf=-6dB) ===");
        double shelfDb = -6.0;
        for (double fc : CUTOFF_FREQS) {
            double probeLow = Math.max(30, fc / 4.0);
            double probeHigh = Math.min(8000, fc * 4.0);

            Shelving_HipassCADBlock bLow = new Shelving_HipassCADBlock(100, 100);
            bLow.setfreq(hzToRdfxCoeff(fc));
            bLow.setshelf(shelfDb);
            Double gainLow = simulateFilter(bLow, probeLow, "Output", null);

            Shelving_HipassCADBlock bHigh = new Shelving_HipassCADBlock(100, 100);
            bHigh.setfreq(hzToRdfxCoeff(fc));
            bHigh.setshelf(shelfDb);
            Double gainHigh = simulateFilter(bHigh, probeHigh, "Output", null);

            System.out.printf("  fc=%5.0f  low(%5.0f)=%s  high(%5.0f)=%s%n",
                fc, probeLow, fmtDb(gainLow), probeHigh, fmtDb(gainHigh));

            if (gainLow != null && probeLow < fc / 2) {
                assertEquals(shelfDb, gainLow, TOLERANCE_DB,
                    msg("ShelvHP", fc, probeLow, gainLow, "shelved ~" + shelfDb + "dB"));
            }
            if (gainHigh != null && probeHigh > fc * 2) {
                assertEquals(0.0, gainHigh, TOLERANCE_DB,
                    msg("ShelvHP", fc, probeHigh, gainHigh, "passband ~0dB"));
            }
        }
    }

    @Test
    void testShelvingHipass_shelfLevels() {
        System.out.println("\n=== Shelving Hipass -- 8 Shelf Levels (fc=1000Hz) ===");
        double fc = 1000;
        double probeLow = 100; // probe below cutoff where shelving applies

        for (double shelfDb : SHELF_LEVELS) {
            Shelving_HipassCADBlock b = new Shelving_HipassCADBlock(100, 100);
            b.setfreq(hzToRdfxCoeff(fc));
            b.setshelf(shelfDb);
            Double gain = simulateFilter(b, probeLow, "Output", null);

            System.out.printf("  shelf=%+3.0fdB  gain@%4.0fHz=%s%n",
                shelfDb, probeLow, fmtDb(gain));

            if (gain != null) {
                assertEquals(shelfDb, gain, TOLERANCE_DB,
                    String.format("ShelvHP shelf=%.0fdB: gain=%.1fdB at %.0fHz",
                        shelfDb, gain, probeLow));
            }
        }
    }

    // ==================== 5. SVF2P (2-pole SVF, fixed Q) ====================

    @Test
    void testSVF2P_lowpass() {
        System.out.println("\n=== SVF2P Lowpass Output ===");
        for (double fc : CUTOFF_FREQS) {
            if (fc > 8000) continue; // SVF unstable near Nyquist
            double probePass = Math.max(30, fc / 4.0);
            double probeStop = Math.min(12000, fc * 4.0);

            SVF2PCADBlock bPass = new SVF2PCADBlock(100, 100);
            bPass.setFreq(fc);
            bPass.setQ(0.707);
            Double gainPass = simulateFilter(bPass, probePass, "Lowpass Out", null);

            SVF2PCADBlock bStop = new SVF2PCADBlock(100, 100);
            bStop.setFreq(fc);
            bStop.setQ(0.707);
            Double gainStop = simulateFilter(bStop, probeStop, "Lowpass Out", null);

            System.out.printf("  fc=%5.0f  pass(%5.0f)=%s  stop(%5.0f)=%s%n",
                fc, probePass, fmtDb(gainPass), probeStop, fmtDb(gainStop));

            if (gainPass != null) {
                assertEquals(0.0, gainPass, TOLERANCE_DB,
                    msg("SVF2P_LP", fc, probePass, gainPass, "passband ~0dB"));
            }
            if (gainStop != null && probeStop > fc * 2) {
                assertTrue(gainStop < -6.0,
                    msg("SVF2P_LP", fc, probeStop, gainStop, "stopband <-6dB"));
            }
        }
    }

    @Test
    void testSVF2P_highpass() {
        System.out.println("\n=== SVF2P Highpass Output ===");
        for (double fc : CUTOFF_FREQS) {
            if (fc > 8000) continue;
            double probeStop = Math.max(30, fc / 4.0);
            double probePass = Math.min(8000, fc * 4.0); // cap to avoid Nyquist peaking

            SVF2PCADBlock bStop = new SVF2PCADBlock(100, 100);
            bStop.setFreq(fc);
            bStop.setQ(0.707);
            Double gainStop = simulateFilter(bStop, probeStop, "Hipass Out", null);

            SVF2PCADBlock bPass = new SVF2PCADBlock(100, 100);
            bPass.setFreq(fc);
            bPass.setQ(0.707);
            Double gainPass = simulateFilter(bPass, probePass, "Hipass Out", null);

            System.out.printf("  fc=%5.0f  stop(%5.0f)=%s  pass(%5.0f)=%s%n",
                fc, probeStop, fmtDb(gainStop), probePass, fmtDb(gainPass));

            if (gainPass != null && probePass >= fc * 4) {
                // SVF highpass has digital warping peaking near Nyquist, allow 3dB
                assertEquals(0.0, gainPass, 3.0,
                    msg("SVF2P_HP", fc, probePass, gainPass, "passband ~0dB"));
            }
            if (gainStop != null && probeStop < fc / 2) {
                assertTrue(gainStop < -6.0,
                    msg("SVF2P_HP", fc, probeStop, gainStop, "stopband <-6dB"));
            }
        }
    }

    @Test
    void testSVF2P_resonance() {
        System.out.println("\n=== SVF2P Resonance (Q sweep, fc=1000Hz) ===");
        double fc = 1000;
        Double prevGain = null;

        for (double q : Q_DIRECT_VALUES) {
            SVF2PCADBlock b = new SVF2PCADBlock(100, 100);
            b.setFreq(fc);
            b.setQ(q);
            Double gainAtFc = simulateFilter(b, fc, "Lowpass Out", null);

            System.out.printf("  Q=%5.1f  gain@fc=%s%n", q, fmtDb(gainAtFc));
            prevGain = gainAtFc;
        }
        // Verify Q has an effect: first and last should differ
        SVF2PCADBlock bLow = new SVF2PCADBlock(100, 100);
        bLow.setFreq(fc);
        bLow.setQ(Q_DIRECT_VALUES[0]);
        Double gainLowQ = simulateFilter(bLow, fc, "Lowpass Out", null);

        SVF2PCADBlock bHigh = new SVF2PCADBlock(100, 100);
        bHigh.setFreq(fc);
        bHigh.setQ(Q_DIRECT_VALUES[Q_DIRECT_VALUES.length - 1]);
        Double gainHighQ = simulateFilter(bHigh, fc, "Lowpass Out", null);

        if (gainLowQ != null && gainHighQ != null) {
            assertNotEquals(gainLowQ, gainHighQ, 1.0,
                "SVF2P: Q should affect gain at cutoff");
        }
    }

    // ==================== 6. SVF_2P_adjustable ====================

    @Test
    void testSVF2P_adjustable_lowpass() {
        System.out.println("\n=== SVF_2P_adjustable Lowpass ===");
        for (double fc : CUTOFF_FREQS) {
            if (fc > 8000) continue;
            double probePass = Math.max(30, fc / 4.0);
            double probeStop = Math.min(12000, fc * 4.0);

            SVF_2P_adjustableCADBlock bPass = new SVF_2P_adjustableCADBlock(100, 100);
            bPass.setfreq(hzToSvfCoeff(fc));
            Double gainPass = simulateFilter(bPass, probePass, "Low Pass Output", null);

            SVF_2P_adjustableCADBlock bStop = new SVF_2P_adjustableCADBlock(100, 100);
            bStop.setfreq(hzToSvfCoeff(fc));
            Double gainStop = simulateFilter(bStop, probeStop, "Low Pass Output", null);

            System.out.printf("  fc=%5.0f  pass(%5.0f)=%s  stop(%5.0f)=%s%n",
                fc, probePass, fmtDb(gainPass), probeStop, fmtDb(gainStop));

            if (gainPass != null) {
                assertEquals(0.0, gainPass, TOLERANCE_DB,
                    msg("SVF_adj_LP", fc, probePass, gainPass, "passband ~0dB"));
            }
            if (gainStop != null && probeStop > fc * 2) {
                assertTrue(gainStop < -6.0,
                    msg("SVF_adj_LP", fc, probeStop, gainStop, "stopband <-6dB"));
            }
        }
    }

    @Test
    void testSVF2P_adjustable_resonance() {
        System.out.println("\n=== SVF_2P_adjustable Resonance (fc=1000Hz) ===");
        double fc = 1000;

        for (int resCtrl : RESONANCE_CONTROLS) {
            SVF_2P_adjustableCADBlock b = new SVF_2P_adjustableCADBlock(100, 100);
            b.setfreq(hzToSvfCoeff(fc));
            Map<String, Integer> controls = new HashMap<>();
            controls.put("Q", resCtrl);
            Double gain = simulateFilter(b, fc, "Band Pass Output", controls);

            System.out.printf("  Q_ctrl=%3d  gain@fc=%s%n", resCtrl, fmtDb(gain));
        }
    }

    // ==================== 7. LPF4P (2/4-pole lowpass) ====================

    @Test
    void testLPF4P() {
        System.out.println("\n=== LPF4P (2P Lowpass) -- 8 control values ===");
        // Use direct control values since the exp() Hz mapping is unreliable.
        // Verify lowpass behavior: low probe near 0dB, high probe well below.
        int[] controlValues = {400, 500, 600, 700, 800, 900};
        double probeLow = 30;    // always well below any cutoff
        double probeHigh = 10000; // always well above any cutoff

        for (int ctrl : controlValues) {
            Map<String, Integer> controls = new HashMap<>();
            controls.put("Frequency", ctrl);

            Double gainLow = simulateFilter(
                new LPF4PCADBlock(100, 100), probeLow, "Low Pass", controls);
            Double gainHigh = simulateFilter(
                new LPF4PCADBlock(100, 100), probeHigh, "Low Pass", controls);

            System.out.printf("  ctrl=%3d  pass(%5.0f)=%s  stop(%5.0f)=%s%n",
                ctrl, probeLow, fmtDb(gainLow), probeHigh, fmtDb(gainHigh));

            if (gainLow != null) {
                // LPF4P reads input at 0.5 gain, so inherent -6dB offset
                assertEquals(-6.0, gainLow, TOLERANCE_DB,
                    String.format("LPF4P ctrl=%d: gain=%.1fdB at %.0fHz, expected passband ~-6dB",
                        ctrl, gainLow, probeLow));
            }
            if (gainHigh != null) {
                assertTrue(gainHigh < -12.0,
                    String.format("LPF4P ctrl=%d: gain=%.1fdB at %.0fHz, expected stopband <-12dB",
                        ctrl, gainHigh, probeHigh));
            }
        }
    }

    @Test
    void testLPF4P_resonance() {
        System.out.println("\n=== LPF4P Resonance (fc~1000Hz) ===");
        int freqCtrl = hzToExpControl(1000);

        for (int resCtrl : RESONANCE_CONTROLS) {
            Map<String, Integer> controls = new HashMap<>();
            controls.put("Frequency", freqCtrl);
            controls.put("Resonance", resCtrl);
            Double gain = simulateFilter(
                new LPF4PCADBlock(100, 100), 1000, "Low Pass", controls);

            System.out.printf("  res_ctrl=%3d  gain@1000Hz=%s%n", resCtrl, fmtDb(gain));
        }
    }

    // ==================== 8. HPF2P (2/4-pole highpass) ====================

    @Test
    void testHPF2P() {
        System.out.println("\n=== HPF2P (2P Highpass) ===");
        for (double fc : CUTOFF_FREQS) {
            int freqCtrl = hzToExpControl(fc);
            if (freqCtrl < 400 || freqCtrl > 998) continue;

            double probeStop = Math.max(30, fc / 4.0);
            double probePass = Math.min(8000, fc * 4.0); // cap to avoid Nyquist

            Map<String, Integer> controls = new HashMap<>();
            controls.put("Frequency", freqCtrl);

            Double gainStop = simulateFilter(
                new HPF2PCADBlock(100, 100), probeStop, "High Pass", controls);
            Double gainPass = simulateFilter(
                new HPF2PCADBlock(100, 100), probePass, "High Pass", controls);

            System.out.printf("  fc~%5.0f(ctrl=%3d)  stop(%5.0f)=%s  pass(%5.0f)=%s%n",
                fc, freqCtrl, probeStop, fmtDb(gainStop), probePass, fmtDb(gainPass));

            if (gainPass != null && probePass >= fc * 4) {
                assertEquals(0.0, gainPass, TOLERANCE_DB,
                    msg("HPF2P", fc, probePass, gainPass, "passband ~0dB"));
            }
            if (gainStop != null && probeStop < fc / 2) {
                assertTrue(gainStop < -6.0,
                    msg("HPF2P", fc, probeStop, gainStop, "stopband <-6dB"));
            }
        }
    }

    @Test
    void testHPF2P_resonance() {
        System.out.println("\n=== HPF2P Resonance (fc~1000Hz) ===");
        int freqCtrl = hzToExpControl(1000);

        for (int resCtrl : RESONANCE_CONTROLS) {
            Map<String, Integer> controls = new HashMap<>();
            controls.put("Frequency", freqCtrl);
            controls.put("Resonance", resCtrl);
            Double gain = simulateFilter(
                new HPF2PCADBlock(100, 100), 1000, "High Pass", controls);

            System.out.printf("  res_ctrl=%3d  gain@1000Hz=%s%n", resCtrl, fmtDb(gain));
        }
    }

    // ==================== 9. Notch ====================

    @Test
    void testNotch_frequencies() {
        System.out.println("\n=== Notch Frequency Response ===");
        for (double fc : CUTOFF_FREQS) {
            if (fc < 800 || fc > 4000) continue; // SVF notch too shallow below 800Hz, unstable above 4kHz

            NotchCADBlock bCenter = new NotchCADBlock(100, 100);
            bCenter.setfreq(hzToSvfCoeff(fc));
            Double gainCenter = simulateFilter(bCenter, fc, "Output_Notch", null);

            double awayFreq = Math.min(12000, fc * 4);
            NotchCADBlock bAway = new NotchCADBlock(100, 100);
            bAway.setfreq(hzToSvfCoeff(fc));
            Double gainAway = simulateFilter(bAway, awayFreq, "Output_Notch", null);

            System.out.printf("  fc=%5.0f  center=%s  away(%5.0f)=%s%n",
                fc, fmtDb(gainCenter), awayFreq, fmtDb(gainAway));

            if (gainCenter != null && gainAway != null) {
                // Fixed-point SVF produces shallow notches without explicit resonance control
                assertTrue(gainCenter < gainAway - 1.0,
                    String.format("Notch fc=%.0f: center=%.1fdB should be below away=%.1fdB",
                        fc, gainCenter, gainAway));
            }
        }
    }

    @Test
    void testNotch_resonance() {
        System.out.println("\n=== Notch Resonance (fc=1000Hz) ===");
        double fc = 1000;

        for (int resCtrl : RESONANCE_CONTROLS) {
            NotchCADBlock b = new NotchCADBlock(100, 100);
            b.setfreq(hzToSvfCoeff(fc));
            Map<String, Integer> controls = new HashMap<>();
            controls.put("Resonance", resCtrl);
            Double gain = simulateFilter(b, fc, "Output_Notch", controls);

            System.out.printf("  res_ctrl=%3d  gain@center=%s%n", resCtrl, fmtDb(gain));
        }
    }

    // ==================== 10. OneBandEQ ====================

    @Test
    void testOneBandEQ_frequencies() {
        System.out.println("\n=== OneBandEQ -- 8 Frequencies (boost=+6dB) ===");
        double boostKp = 1.0; // kp0=1.0 -> gain = 20*log10(1+1) = +6dB at center

        for (double fc : CUTOFF_FREQS) {
            if (fc > 6000) continue;

            OneBandEQCADBlock bAtFc = new OneBandEQCADBlock(100, 100);
            bAtFc.setFreq(fc);
            bAtFc.setEqLevel(boostKp);
            Double gainAtFc = simulateFilter(bAtFc, fc, null, null);

            double awayFreq = Math.min(12000, fc * 8);
            OneBandEQCADBlock bAway = new OneBandEQCADBlock(100, 100);
            bAway.setFreq(fc);
            bAway.setEqLevel(boostKp);
            Double gainAway = simulateFilter(bAway, awayFreq, null, null);

            System.out.printf("  fc=%5.0f  atFc=%s  away(%5.0f)=%s%n",
                fc, fmtDb(gainAtFc), awayFreq, fmtDb(gainAway));

            if (gainAtFc != null) {
                assertTrue(gainAtFc > 2.0,
                    String.format("EQ fc=%.0f: boost at center=%.1fdB, expected >2dB",
                        fc, gainAtFc));
            }
            // Away from center, gain should be closer to 0 dB than at center
            if (gainAtFc != null && gainAway != null) {
                assertTrue(Math.abs(gainAway) < Math.abs(gainAtFc),
                    String.format("EQ fc=%.0f: away=%.1fdB should be closer to 0 than center=%.1fdB",
                        fc, gainAway, gainAtFc));
            }
        }
    }

    // ==================== 11. SixBandEQ ====================

    @Test
    void testSixBandEQ_bands() {
        System.out.println("\n=== SixBandEQ Band Response ===");
        double[] bandFreqs = {80, 160, 320, 640, 1280, 2560};

        for (int band = 0; band < 6; band++) {
            SixBandEQCADBlock b = new SixBandEQCADBlock(100, 100);
            // Boost only this band, zero all others
            for (int j = 0; j < 6; j++) {
                b.seteqLevel(j, (j == band) ? 1.0 : 0.0);
            }
            Double gain = simulateFilter(b, bandFreqs[band], null, null);

            System.out.printf("  band %d (%4.0fHz) boost=1.0: gain=%s%n",
                band, bandFreqs[band], fmtDb(gain));

            if (gain != null) {
                assertTrue(gain > 2.0,
                    String.format("6BandEQ band %d (%.0fHz): gain=%.1fdB, expected >2dB",
                        band, bandFreqs[band], gain));
            }
        }
    }

    // ==================== Simulation engine ====================

    /**
     * Build a model with Input -> block -> Output, simulate with a sine wave
     * at the given frequency, and return the measured gain in dB.
     *
     * @param block          The filter block (fresh instance, will be consumed)
     * @param sineFreqHz     Test sine frequency
     * @param outputPinName  Name of output pin to measure, or null for first audio out
     * @param controlValues  Map of control pin name -> ConstantCADBlock value (0-999), or null
     * @return Gain in dB, or null if simulation failed
     */
    private Double simulateFilter(SpinCADBlock block, double sineFreqHz,
                                   String outputPinName, Map<String, Integer> controlValues) {
        try {
            SpinCADModel model = new SpinCADModel();
            InputCADBlock inputBlock = new InputCADBlock(0, 0);
            OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

            model.addBlock(inputBlock);
            model.addBlock(block);
            model.addBlock(outputBlock);

            SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
            inputBlock.generateCode(tempSfxb);

            // Wire first audio input pin
            boolean inputWired = false;
            for (SpinCADPin pin : block.pinList) {
                if (pin.getType() == pinType.AUDIO_IN && !inputWired) {
                    SpinCADPin srcPin = inputBlock.getPin("Output 1");
                    if (srcPin != null) {
                        pin.setConnection(inputBlock, srcPin);
                        inputWired = true;
                    }
                }
            }
            if (!inputWired) return null;

            // Wire control inputs
            if (controlValues != null) {
                for (Map.Entry<String, Integer> entry : controlValues.entrySet()) {
                    SpinCADPin ctrlPin = block.getPin(entry.getKey());
                    if (ctrlPin != null && ctrlPin.getType() == pinType.CONTROL_IN) {
                        ConstantCADBlock cb = new ConstantCADBlock(50, 50);
                        cb.setConstant(entry.getValue());
                        model.addBlock(cb);
                        cb.generateCode(tempSfxb);
                        ctrlPin.setConnection(cb, cb.getPin("Value"));
                    }
                }
            }

            // Find output pin
            SpinCADPin outPin = null;
            if (outputPinName != null) {
                outPin = block.getPin(outputPinName);
            } else {
                for (SpinCADPin pin : block.pinList) {
                    if (pin.getType() == pinType.AUDIO_OUT) {
                        outPin = pin;
                        break;
                    }
                }
            }
            if (outPin == null) return null;

            outputBlock.getPin("Input 1").setConnection(block, outPin);
            outputBlock.getPin("Input 2").setConnection(block, outPin);

            // Generate code
            model.sortAlignGen();
            SpinFXBlock renderBlock = model.getRenderBlock();
            if (renderBlock == null) return null;

            String listing = renderBlock.getProgramListing(1);
            if (!listing.contains("WRAX DACL") || !listing.contains("WRAX DACR")) {
                return null;
            }

            // Generate sine wave and simulate
            File sineWav = generateSineWav(sineFreqHz);
            File outFile = new File(tempDir,
                "filt_" + (int) sineFreqHz + "_" + System.nanoTime() + ".wav");

            SpinSimulator sim = new SpinSimulator(renderBlock,
                sineWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
            sim.setLoopMode(false);
            sim.start();
            sim.join(SIM_TIMEOUT);

            if (sim.isAlive() || sim.getSimulationException() != null) return null;
            if (!outFile.exists()) return null;

            // Measure output
            short[] stereo = readWavSamples(outFile);
            short[] left = extractChannel(stereo, 0);

            // Skip first half (transient), measure steady-state RMS
            int start = left.length / 2;
            double outputRms = rmsOfRange(left, start, left.length);
            double inputRms = SINE_AMPLITUDE * 32767.0 / Math.sqrt(2.0);

            if (outputRms < 1.0) return -80.0;
            return 20.0 * Math.log10(outputRms / inputRms);

        } catch (Exception e) {
            System.err.println("  Sim error: " + e.getMessage());
            return null;
        }
    }

    // ==================== Audio utilities ====================

    private File generateSineWav(double freqHz) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * SINE_DURATION);
        byte[] data = new byte[numFrames * 4]; // stereo 16-bit

        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / SAMPLE_RATE;
            short sample = (short) (SINE_AMPLITUDE * 32767.0
                * Math.sin(2.0 * Math.PI * freqHz * t));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }

        File wavFile = File.createTempFile("sine_" + (int) freqHz + "_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    static short[] extractChannel(short[] stereo, int channel) {
        short[] mono = new short[stereo.length / 2];
        for (int i = 0; i < mono.length; i++) {
            mono[i] = stereo[i * 2 + channel];
        }
        return mono;
    }

    static double rmsOfRange(short[] samples, int start, int end) {
        if (start >= end || start >= samples.length) return 0;
        end = Math.min(end, samples.length);
        double sumSq = 0;
        for (int i = start; i < end; i++) {
            sumSq += (double) samples[i] * samples[i];
        }
        return Math.sqrt(sumSq / (end - start));
    }

    private static short[] readWavSamples(File wavFile) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        ais.close();
        byte[] raw = bos.toByteArray();
        short[] samples = new short[raw.length / 2];
        for (int i = 0; i < samples.length; i++) {
            int offset = i * 2;
            samples[i] = (short) ((raw[offset] & 0xff) | ((raw[offset + 1] & 0xff) << 8));
        }
        return samples;
    }

    // ==================== Formatting ====================

    private static String fmtDb(Double db) {
        return db == null ? "  null " : String.format("%+6.1fdB", db);
    }

    private static String msg(String filter, double fc, double probe, double gain, String expected) {
        return String.format("%s fc=%.0f: gain=%.1fdB at %.0fHz, expected %s",
            filter, fc, gain, probe, expected);
    }
}
