package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Verifies pitch shift blocks produce the expected frequency shift.
 * Feeds a 1 kHz sine wave and checks that:
 *   - Octave up  (+12 semitones) → dominant output near 2 kHz
 *   - Octave down (-12 semitones) → dominant output near 500 Hz
 *
 * Uses Goertzel single-frequency DFT to measure energy at candidate
 * frequencies in the steady-state portion of the output.
 */
public class PitchShiftFrequencyTest {

    @TempDir
    File tempDir;

    private static final double INPUT_FREQ = 1000.0;
    private static final double SIM_DURATION = 0.5;
    private static final double AMPLITUDE = 0.25;
    /** Skip first 0.25 s to let ramp LFO settle. */
    private static final int SKIP_SAMPLES = (int)(0.25 * SAMPLE_RATE);
    /** Tolerance: measured peak frequency must be within this ratio of expected. */
    private static final double FREQ_TOLERANCE = 0.15;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // ===================== Pitch Shift Fixed =====================

    @Test
    void pitchShiftFixed_octaveUp() throws Exception {
        PitchShiftFixedCADBlock block = new PitchShiftFixedCADBlock(100, 100);
        block.setFreq(12);   // +12 semitones = octave up
        block.setCents(0);
        assertDominantFrequency(block, "Pitch Out", 2000.0,
                "PitchShiftFixed octave up");
    }

    @Test
    void pitchShiftFixed_octaveDown() throws Exception {
        PitchShiftFixedCADBlock block = new PitchShiftFixedCADBlock(100, 100);
        block.setFreq(-12);  // -12 semitones = octave down
        block.setCents(0);
        assertDominantFrequency(block, "Pitch Out", 500.0,
                "PitchShiftFixed octave down");
    }

    // ===================== Pitch Shift Adjustable =====================

    @Test
    void pitchShiftAdj_octaveUp() throws Exception {
        Pitch_shift_testCADBlock block = new Pitch_shift_testCADBlock(100, 100);
        block.setpitchSemitones(12);
        block.setpitchCents(0);
        assertDominantFrequency(block, "Pitch Out", 2000.0,
                "PitchShiftAdj octave up");
    }

    @Test
    void pitchShiftAdj_octaveDown() throws Exception {
        Pitch_shift_testCADBlock block = new Pitch_shift_testCADBlock(100, 100);
        block.setpitchSemitones(-12);
        block.setpitchCents(0);
        assertDominantFrequency(block, "Pitch Out", 500.0,
                "PitchShiftAdj octave down");
    }

    // ===================== Glitch Shift =====================

    @Test
    void glitchShift_octaveUp() throws Exception {
        Glitch_shiftCADBlock block = new Glitch_shiftCADBlock(100, 100);
        block.setpitchSemitones(12);
        block.setpitchCents(0);
        assertDominantFrequency(block, "Glitch Out", 2000.0,
                "GlitchShift octave up");
    }

    @Test
    void glitchShift_octaveDown() throws Exception {
        Glitch_shiftCADBlock block = new Glitch_shiftCADBlock(100, 100);
        block.setpitchSemitones(-12);
        block.setpitchCents(0);
        assertDominantFrequency(block, "Glitch Out", 500.0,
                "GlitchShift octave down");
    }

    // ===================== Helper methods =====================

    /**
     * Simulate the block with a 1 kHz sine input, measure the dominant
     * frequency, and assert it is within tolerance of the expected value.
     */
    private void assertDominantFrequency(SpinCADBlock block,
            String outputPin, double expectedFreq, String label) throws Exception {

        File inputWav = generateSineWav(SIM_DURATION, INPUT_FREQ, AMPLITUDE);

        short[] stereo = simulate(block, inputWav, null,
                outputPin, null, tempDir);
        assertNotNull(stereo, label + ": simulation returned null");

        short[] left = extractChannel(stereo, 0);
        assertTrue(left.length > SKIP_SAMPLES + SAMPLE_RATE / 10,
                label + ": output too short");

        // Measure in steady-state region (skip transient)
        double dominant = findDominantFrequency(left, SKIP_SAMPLES, left.length);

        double ratio = dominant / expectedFreq;
        System.out.printf("  %s: expected %.0f Hz, measured %.0f Hz (ratio %.3f)%n",
                label, expectedFreq, dominant, ratio);

        assertTrue(ratio > (1.0 - FREQ_TOLERANCE) && ratio < (1.0 + FREQ_TOLERANCE),
                String.format("%s: expected ~%.0f Hz but measured %.0f Hz (ratio %.3f)",
                        label, expectedFreq, dominant, ratio));
    }

    /**
     * Find the dominant frequency in a signal using Goertzel DFT probes.
     * Scans from 100 Hz to 8 kHz in 25 Hz steps and returns the frequency
     * with the highest magnitude.
     */
    static double findDominantFrequency(short[] samples, int start, int end) {
        double bestFreq = 0;
        double bestMag = 0;
        int n = end - start;

        for (double freq = 100; freq <= 8000; freq += 25) {
            double mag = goertzelMagnitude(samples, start, n, freq);
            if (mag > bestMag) {
                bestMag = mag;
                bestFreq = freq;
            }
        }

        // Refine with 1 Hz steps around the peak
        double lo = Math.max(100, bestFreq - 30);
        double hi = Math.min(8000, bestFreq + 30);
        for (double freq = lo; freq <= hi; freq += 1) {
            double mag = goertzelMagnitude(samples, start, n, freq);
            if (mag > bestMag) {
                bestMag = mag;
                bestFreq = freq;
            }
        }

        return bestFreq;
    }

    /**
     * Goertzel single-frequency DFT magnitude.
     */
    static double goertzelMagnitude(short[] samples, int start, int n, double freq) {
        double omega = 2.0 * Math.PI * freq / SAMPLE_RATE;
        double cosSum = 0, sinSum = 0;
        for (int i = 0; i < n; i++) {
            double s = samples[start + i];
            cosSum += s * Math.cos(omega * i);
            sinSum += s * Math.sin(omega * i);
        }
        return Math.sqrt(cosSum * cosSum + sinSum * sinSum) / n;
    }
}
