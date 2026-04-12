package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Pitch menu blocks.
 * Each block shows stacked input/output waveforms to demonstrate
 * the pitch shift effect.
 */
public class PitchDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;
    private static final double FREQ = 440.0;
    private static final int DISPLAY_SAMPLES = (int)(0.025 * SAMPLE_RATE);
    private static final int SKIP_SAMPLES = (int)(0.2 * SAMPLE_RATE);

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generatePitchPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.5);

        // === Pitch Shift Fixed - Octave Up ===
        PitchShiftFixedCADBlock fixedUp = new PitchShiftFixedCADBlock(100, 100);
        fixedUp.setFreq(12);
        plotStacked("pitchshiftfixed-up", "Pitch Shift Fixed - Octave Up",
            fixedUp, sineWav, null, "Pitch Out", docsDir);

        // === Pitch Shift Fixed - Octave Down ===
        PitchShiftFixedCADBlock fixedDown = new PitchShiftFixedCADBlock(100, 100);
        fixedDown.setFreq(-12);
        plotStacked("pitchshiftfixed-down", "Pitch Shift Fixed - Octave Down",
            fixedDown, sineWav, null, "Pitch Out", docsDir);

        // === Pitch Shift Adjustable - Octave Up ===
        Pitch_shift_testCADBlock adjUp = new Pitch_shift_testCADBlock(100, 100);
        adjUp.setpitchSemitones(12);
        adjUp.setpitchCents(0);
        plotStacked("pitch_shift_test-up", "Pitch Shift Adjustable - Octave Up",
            adjUp, sineWav, null, "Pitch Out", docsDir);

        // === Pitch Shift Adjustable - Octave Down ===
        Pitch_shift_testCADBlock adjDown = new Pitch_shift_testCADBlock(100, 100);
        adjDown.setpitchSemitones(-12);
        adjDown.setpitchCents(0);
        plotStacked("pitch_shift_test-down", "Pitch Shift Adjustable - Octave Down",
            adjDown, sineWav, null, "Pitch Out", docsDir);

        // === Octave Up/Down (three-panel: input, down, up) ===
        plotTripleOutput("pitchupdown", "Octave Up/Down",
            new pitchupdownCADBlock(100, 100),
            sineWav, null, "Pitch_Down_Out", "Pitch_Up_Out", docsDir);

        // === Glitch Shift - Octave Up (longer window to show glitch) ===
        Glitch_shiftCADBlock glitchUp = new Glitch_shiftCADBlock(100, 100);
        glitchUp.setpitchSemitones(12);
        glitchUp.setpitchCents(0);
        plotStacked("glitch_shift-up", "Glitch Shift - Octave Up",
            glitchUp, sineWav, null, "Glitch Out",
            (int)(0.150 * SAMPLE_RATE), docsDir);

        // === Glitch Shift - Octave Down (longer window to show glitch) ===
        Glitch_shiftCADBlock glitchDown = new Glitch_shiftCADBlock(100, 100);
        glitchDown.setpitchSemitones(-12);
        glitchDown.setpitchCents(0);
        plotStacked("glitch_shift-down", "Glitch Shift - Octave Down",
            glitchDown, sineWav, null, "Glitch Out",
            (int)(0.150 * SAMPLE_RATE), docsDir);

        // === Pitch Four ===
        plotStacked("pitch_four", "Pitch Four",
            new pitch_fourCADBlock(100, 100),
            sineWav, null, "Pitch_Out", docsDir);

        // === Pitch Offset (needs control input for offset amount) ===
        Map<String, Integer> pitchOffsetCtrl = new HashMap<>();
        pitchOffsetCtrl.put("Pitch_Offset", 250);
        plotStacked("pitchoffset", "Pitch Offset",
            new pitchoffsetCADBlock(100, 100),
            sineWav, pitchOffsetCtrl, "Output", docsDir);

        // === Dual Output Pitch Offset (needs control inputs) ===
        Map<String, Integer> dualOffsetCtrl = new HashMap<>();
        dualOffsetCtrl.put("Offset 1", 250);
        dualOffsetCtrl.put("Offset 2", 500);
        plotTripleOutput("pitchoffset1_2", "Dual Output Pitch Offset",
            new pitchoffset1_2CADBlock(100, 100),
            sineWav, dualOffsetCtrl, "Output 1", "Output 2", docsDir);

        // === Arpeggiator ===
        plotStacked("arpeggiator", "Arpeggiator",
            new ArpeggiatorCADBlock(100, 100),
            sineWav, null, "Pitch Out", docsDir);

        // === Block Size Demo ===
        plotBlockSizeDemo(docsDir);

        // === Crossfade Phase Alignment Demo ===
        plotCrossfadeDemo(docsDir);

        System.out.println("\nAll pitch PNGs written to docs/images/");
    }

    private void plotStacked(String fileBase, String title,
            SpinCADBlock block, File inputWav,
            Map<String, Integer> controlInputs,
            String outputPin, File docsDir) throws Exception {
        plotStacked(fileBase, title, block, inputWav, controlInputs,
            outputPin, DISPLAY_SAMPLES, docsDir);
    }

    private void plotStacked(String fileBase, String title,
            SpinCADBlock block, File inputWav,
            Map<String, Integer> controlInputs,
            String outputPin, int displaySamples,
            File docsDir) throws Exception {

        short[] stereo;
        try {
            stereo = simulate(block, inputWav, controlInputs,
                outputPin, null, tempDir);
        } catch (Exception e) {
            System.err.println("  SKIP " + title + ": " + e.getMessage());
            return;
        }
        if (stereo == null) {
            System.err.println("  SKIP " + title + ": simulation returned null");
            return;
        }

        short[] left = extractChannel(stereo, 0);
        double[] output = toDouble(left);

        short[] inputStereo = readWavSamples(inputWav);
        double[] input = toDouble(extractChannel(inputStereo, 0));

        int start = Math.min(SKIP_SAMPLES, output.length - displaySamples - 1);
        if (start < 0) start = 0;
        int end = Math.min(start + displaySamples,
            Math.min(output.length, input.length));

        double[] outputSlice = Arrays.copyOfRange(output, start, end);
        double[] inputSlice = Arrays.copyOfRange(input, start, end);

        int len = Math.min(outputSlice.length, inputSlice.length);
        if (outputSlice.length != len) outputSlice = Arrays.copyOf(outputSlice, len);
        if (inputSlice.length != len) inputSlice = Arrays.copyOf(inputSlice, len);

        double[] timeMs = new double[len];
        for (int i = 0; i < len; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        writeStackedWaveformPlot(
            new File(docsDir, "pitch-" + fileBase + ".png"),
            title, timeMs, inputSlice, outputSlice,
            "Input (440 Hz)", "Output");

        System.out.println("  wrote pitch-" + fileBase + ".png");
    }

    private void plotTripleOutput(String fileBase, String title,
            SpinCADBlock block, File inputWav,
            Map<String, Integer> controlInputs,
            String outputPin1, String outputPin2,
            File docsDir) throws Exception {

        short[] stereo;
        try {
            stereo = simulate(block, inputWav, controlInputs,
                outputPin1, outputPin2, tempDir);
        } catch (Exception e) {
            System.err.println("  SKIP " + title + ": " + e.getMessage());
            return;
        }
        if (stereo == null) {
            System.err.println("  SKIP " + title + ": simulation returned null");
            return;
        }

        short[] left = extractChannel(stereo, 0);
        short[] right = extractChannel(stereo, 1);
        double[] out1 = toDouble(left);
        double[] out2 = toDouble(right);

        short[] inputStereo = readWavSamples(inputWav);
        double[] input = toDouble(extractChannel(inputStereo, 0));

        int start = Math.min(SKIP_SAMPLES, out1.length - DISPLAY_SAMPLES - 1);
        if (start < 0) start = 0;
        int minLen = Math.min(Math.min(out1.length, out2.length), input.length);
        int end = Math.min(start + DISPLAY_SAMPLES, minLen);

        double[] inputSlice = Arrays.copyOfRange(input, start, end);
        double[] slice1 = Arrays.copyOfRange(out1, start, end);
        double[] slice2 = Arrays.copyOfRange(out2, start, end);

        int len = Math.min(Math.min(inputSlice.length, slice1.length), slice2.length);
        if (inputSlice.length != len) inputSlice = Arrays.copyOf(inputSlice, len);
        if (slice1.length != len) slice1 = Arrays.copyOf(slice1, len);
        if (slice2.length != len) slice2 = Arrays.copyOf(slice2, len);

        double[] timeMs = new double[len];
        for (int i = 0; i < len; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        writeThreePanelWaveformPlot(
            new File(docsDir, "pitch-" + fileBase + ".png"),
            title, timeMs, inputSlice, slice1, slice2,
            "Input (440 Hz)", outputPin1, outputPin2);

        System.out.println("  wrote pitch-" + fileBase + ".png");
    }

    /**
     * Demonstrates the effect of buffer size on low-frequency pitch shifting.
     * Compares a 512-sample buffer (too small) against a 4096-sample buffer
     * for an octave-down shift of a 100 Hz input.
     */
    private void plotBlockSizeDemo(File docsDir) throws Exception {
        double lowFreq = 100.0;
        File lowWav = generateSineWav(SIM_DURATION, lowFreq, 0.5);
        int displaySamples = (int)(0.050 * SAMPLE_RATE); // 50 ms for low freq

        // Small buffer (512 samples) - octave down
        PitchShiftFixedCADBlock smallBlock = new PitchShiftFixedCADBlock(100, 100);
        smallBlock.setFreq(-12);
        smallBlock.setAmp(0); // index 0 = 512 samples

        // Large buffer (4096 samples) - octave down
        PitchShiftFixedCADBlock largeBlock = new PitchShiftFixedCADBlock(100, 100);
        largeBlock.setFreq(-12);
        // default amp = 4096

        short[] smallStereo, largeStereo;
        try {
            smallStereo = simulate(smallBlock, lowWav, null,
                "Pitch Out", null, tempDir);
            largeStereo = simulate(largeBlock, lowWav, null,
                "Pitch Out", null, tempDir);
        } catch (Exception e) {
            System.err.println("  SKIP block size demo: " + e.getMessage());
            return;
        }

        if (smallStereo == null || largeStereo == null) {
            System.err.println("  SKIP block size demo: simulation returned null");
            return;
        }

        short[] inputStereo = readWavSamples(lowWav);
        double[] input = toDouble(extractChannel(inputStereo, 0));
        double[] smallOut = toDouble(extractChannel(smallStereo, 0));
        double[] largeOut = toDouble(extractChannel(largeStereo, 0));

        int start = Math.min(SKIP_SAMPLES, smallOut.length - displaySamples - 1);
        if (start < 0) start = 0;
        int minLen = Math.min(Math.min(input.length, smallOut.length), largeOut.length);
        int end = Math.min(start + displaySamples, minLen);

        double[] inputSlice = Arrays.copyOfRange(input, start, end);
        double[] smallSlice = Arrays.copyOfRange(smallOut, start, end);
        double[] largeSlice = Arrays.copyOfRange(largeOut, start, end);

        int len = Math.min(Math.min(inputSlice.length, smallSlice.length),
            largeSlice.length);
        if (inputSlice.length != len) inputSlice = Arrays.copyOf(inputSlice, len);
        if (smallSlice.length != len) smallSlice = Arrays.copyOf(smallSlice, len);
        if (largeSlice.length != len) largeSlice = Arrays.copyOf(largeSlice, len);

        double[] timeMs = new double[len];
        for (int i = 0; i < len; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        writeThreePanelWaveformPlot(
            new File(docsDir, "pitch-blocksize.png"),
            "Block Size: 100 Hz Octave Down", timeMs,
            inputSlice, smallSlice, largeSlice,
            "Input (100 Hz)", "512 samples (too small)", "4096 samples");

        System.out.println("  wrote pitch-blocksize.png");
    }

    /**
     * Demonstrates crossfade phase alignment artifacts.
     * Compares worst case (440 Hz = 27.5 cycles per buffer half, odd
     * half-cycles cause phase cancellation) against best case (448 Hz =
     * 28 full cycles per buffer half, even half-cycles crossfade cleanly).
     * Uses a longer display window (150 ms) to show full envelope cycles.
     */
    private void plotCrossfadeDemo(File docsDir) throws Exception {
        int displaySamples = (int)(0.150 * SAMPLE_RATE); // 150 ms
        int skipSamples = (int)(0.25 * SAMPLE_RATE);

        // Worst case: 440 Hz = 55 * 8 Hz (27.5 cycles in 62.5 ms = odd half-cycles)
        double worstFreq = 440.0;
        File worstWav = generateSineWav(SIM_DURATION, worstFreq, 0.5);
        PitchShiftFixedCADBlock worstBlock = new PitchShiftFixedCADBlock(100, 100);
        worstBlock.setFreq(12);

        // Best case: 448 Hz = 28 * 16 Hz (28 full cycles in 62.5 ms = even half-cycles)
        double bestFreq = 448.0;
        File bestWav = generateSineWav(SIM_DURATION, bestFreq, 0.5);
        PitchShiftFixedCADBlock bestBlock = new PitchShiftFixedCADBlock(100, 100);
        bestBlock.setFreq(12);

        short[] worstStereo, bestStereo;
        try {
            worstStereo = simulate(worstBlock, worstWav, null,
                "Pitch Out", null, tempDir);
            bestStereo = simulate(bestBlock, bestWav, null,
                "Pitch Out", null, tempDir);
        } catch (Exception e) {
            System.err.println("  SKIP crossfade demo: " + e.getMessage());
            return;
        }

        if (worstStereo == null || bestStereo == null) {
            System.err.println("  SKIP crossfade demo: simulation returned null");
            return;
        }

        double[] worstOut = toDouble(extractChannel(worstStereo, 0));
        double[] bestOut = toDouble(extractChannel(bestStereo, 0));

        int start = Math.min(skipSamples,
            worstOut.length - displaySamples - 1);
        if (start < 0) start = 0;
        int end = Math.min(start + displaySamples,
            Math.min(worstOut.length, bestOut.length));

        double[] worstSlice = Arrays.copyOfRange(worstOut, start, end);
        double[] bestSlice = Arrays.copyOfRange(bestOut, start, end);

        int len = Math.min(worstSlice.length, bestSlice.length);
        if (worstSlice.length != len) worstSlice = Arrays.copyOf(worstSlice, len);
        if (bestSlice.length != len) bestSlice = Arrays.copyOf(bestSlice, len);

        double[] timeMs = new double[len];
        for (int i = 0; i < len; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        writeStackedWaveformPlot(
            new File(docsDir, "pitch-crossfade.png"),
            "Crossfade Artifacts (Octave Up, 4096 buffer)",
            timeMs, worstSlice, bestSlice,
            "440 Hz (worst case)", "448 Hz (best case)");

        System.out.println("  wrote pitch-crossfade.png");
    }
}
