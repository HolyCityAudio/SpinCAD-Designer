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
 * Each block is fed a 440 Hz sine wave and the output waveform is plotted
 * to show the pitch shift effect.
 */
public class PitchDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;
    private static final double FREQ = 440.0;
    // Show ~25 ms of output to reveal pitch shift
    private static final int DISPLAY_SAMPLES = (int)(0.025 * SAMPLE_RATE);
    // Skip initial samples for block to settle
    private static final int SKIP_SAMPLES = (int)(0.2 * SAMPLE_RATE);

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generatePitchPlots() throws Exception {
        File docsDir = new File("docs");
        docsDir.mkdirs();

        File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.5);

        // === Pitch Shift Fixed ===
        plotBlock("pitchshiftfixed", "Pitch Shift Fixed",
            new PitchShiftFixedCADBlock(100, 100),
            sineWav, null, "Pitch Out", null, docsDir);

        // === Pitch Shift Adjustable ===
        plotBlock("pitch_shift_test", "Pitch Shift Adjustable",
            new Pitch_shift_testCADBlock(100, 100),
            sineWav, null, "Pitch Out", null, docsDir);

        // === Octave Up/Down (two outputs) ===
        plotDualOutput("pitchupdown", "Octave Up/Down",
            new pitchupdownCADBlock(100, 100),
            sineWav, "Pitch_Down_Out", "Pitch_Up_Out", docsDir);

        // === Glitch Shift Adjustable ===
        plotBlock("glitch_shift", "Glitch Shift Adjustable",
            new Glitch_shiftCADBlock(100, 100),
            sineWav, null, "Glitch Out", null, docsDir);

        // === Pitch Four ===
        plotBlock("pitch_four", "Pitch Four",
            new pitch_fourCADBlock(100, 100),
            sineWav, null, "Pitch_Out", null, docsDir);

        // === Pitch Offset ===
        plotBlock("pitchoffset", "Pitch Offset",
            new pitchoffsetCADBlock(100, 100),
            sineWav, null, "Output", null, docsDir);

        // === Dual Output Pitch Offset (two outputs) ===
        plotDualOutput("pitchoffset1_2", "Dual Output Pitch Offset",
            new pitchoffset1_2CADBlock(100, 100),
            sineWav, "Output 1", "Output 2", docsDir);

        // === Arpeggiator ===
        plotBlock("arpeggiator", "Arpeggiator",
            new ArpeggiatorCADBlock(100, 100),
            sineWav, null, "Pitch Out", null, docsDir);

        System.out.println("\nAll pitch PNGs written to docs/");
    }

    private void plotBlock(String fileBase, String title,
            SpinCADBlock block, File inputWav,
            Map<String, Integer> controlInputs,
            String outputPin1, String outputPin2,
            File docsDir) throws Exception {

        short[] stereo;
        try {
            stereo = simulate(block, inputWav, controlInputs,
                outputPin1, outputPin2, tempDir);
        } catch (Exception e) {
            System.err.println("  SKIP " + title + ": simulation error - " + e.getMessage());
            return;
        }

        if (stereo == null) {
            System.err.println("  SKIP " + title + ": simulation returned null");
            return;
        }

        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);

        // Also get input for comparison
        short[] inputStereo = readWavSamples(inputWav);
        short[] inputLeft = extractChannel(inputStereo, 0);
        double[] inputAudio = toDouble(inputLeft);

        int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
        if (start < 0) start = 0;
        int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
        int inputEnd = Math.min(start + DISPLAY_SAMPLES, inputAudio.length);

        double[] outputCurve = Arrays.copyOfRange(audio, start, end);
        double[] inputCurve = Arrays.copyOfRange(inputAudio, start, inputEnd);

        // Ensure both curves are same length
        int len = Math.min(outputCurve.length, inputCurve.length);
        if (outputCurve.length != len) outputCurve = Arrays.copyOf(outputCurve, len);
        if (inputCurve.length != len) inputCurve = Arrays.copyOf(inputCurve, len);

        double[] timeMs = new double[len];
        for (int i = 0; i < len; i++) {
            timeMs[i] = 1000.0 * i / SAMPLE_RATE;
        }

        writePlot(new File(docsDir, "pitch-" + fileBase + ".png"),
            title, "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs,
            new double[][]{inputCurve, outputCurve},
            new String[]{"Input", "Output"},
            new String[]{COLORS[0], COLORS[1]});

        System.out.println("  wrote pitch-" + fileBase + ".png");
    }

    private void plotDualOutput(String fileBase, String title,
            SpinCADBlock block, File inputWav,
            String outputPin1, String outputPin2,
            File docsDir) throws Exception {

        short[] stereo;
        try {
            stereo = simulate(block, inputWav, null,
                outputPin1, outputPin2, tempDir);
        } catch (Exception e) {
            System.err.println("  SKIP " + title + ": simulation error - " + e.getMessage());
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

        int start = Math.min(SKIP_SAMPLES, out1.length - DISPLAY_SAMPLES - 1);
        if (start < 0) start = 0;
        int end = Math.min(start + DISPLAY_SAMPLES, out1.length);

        double[] curve1 = Arrays.copyOfRange(out1, start, end);
        double[] curve2 = Arrays.copyOfRange(out2, start, end);

        int len = Math.min(curve1.length, curve2.length);
        if (curve1.length != len) curve1 = Arrays.copyOf(curve1, len);
        if (curve2.length != len) curve2 = Arrays.copyOf(curve2, len);

        double[] timeMs = new double[len];
        for (int i = 0; i < len; i++) {
            timeMs[i] = 1000.0 * i / SAMPLE_RATE;
        }

        writePlot(new File(docsDir, "pitch-" + fileBase + ".png"),
            title, "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs,
            new double[][]{curve1, curve2},
            new String[]{outputPin1, outputPin2},
            new String[]{COLORS[0], COLORS[1]});

        System.out.println("  wrote pitch-" + fileBase + ".png");
    }
}
