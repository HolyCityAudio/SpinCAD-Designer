package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

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
        File docsDir = new File("docs");
        docsDir.mkdirs();

        plotChorus(docsDir);
        plotChorusQuad(docsDir);
        plotFlanger(docsDir);
        plotPhaser(docsDir);
        plotRingMod(docsDir);
        plotServo(docsDir);

        System.out.println("\nAll modulation PNGs written to docs/");
    }

    // === Chorus ===
    private void plotChorus(File docsDir) throws Exception {
        try {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.8);
            ChorusCADBlock block = new ChorusCADBlock(100, 100);

            short[] stereo = simulate(block, sineWav, null,
                "Output", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP Chorus: simulation failed");
                return;
            }

            plotInputOutput("Chorus", "chorus", sineWav, stereo, docsDir);
        } catch (Exception e) {
            System.err.println("  SKIP Chorus: " + e.getMessage());
        }
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

    // === Flanger ===
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

            plotInputOutput("Flanger", "flanger", sineWav, stereo, docsDir);
        } catch (Exception e) {
            System.err.println("  SKIP Flanger: " + e.getMessage());
        }
    }

    // === Phaser ===
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

            writePlot(new File(docsDir, "modulation-phaser.png"),
                "Phaser", "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1.0, 1.0,
                timeMs, new double[][]{curveInput, curveMix, curveWet},
                new String[]{"Input", "Mix Out", "Wet Out"},
                new String[]{COLORS[0], COLORS[1], COLORS[2]});
        } catch (Exception e) {
            System.err.println("  SKIP Phaser: " + e.getMessage());
        }
    }

    // === Ring Mod ===
    private void plotRingMod(File docsDir) throws Exception {
        try {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, 0.8);
            RingModCADBlock block = new RingModCADBlock(100, 100);

            // Carrier Frequency control drives the internal oscillator speed
            short[] stereo = simulate(block, sineWav,
                Map.of("Carrier Frequency", 500),
                "Audio Output 1", null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP RingMod: simulation failed");
                return;
            }

            plotInputOutput("Ring Modulator", "ringmod", sineWav, stereo, docsDir);
        } catch (Exception e) {
            System.err.println("  SKIP RingMod: " + e.getMessage());
        }
    }

    // === Servo Flanger ===
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

            plotInputOutput("Servo Flanger", "servo", sineWav, stereo, docsDir);
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
