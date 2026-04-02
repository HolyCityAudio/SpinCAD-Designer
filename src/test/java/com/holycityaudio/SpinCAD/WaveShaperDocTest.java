package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Wave Shaper menu blocks.
 * Each block is fed a 440 Hz sine wave at three levels (0 dB, -6 dB, -18 dB)
 * and the output waveform is plotted.
 */
public class WaveShaperDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;
    private static final double FREQ = 440.0;
    // Show ~3 cycles at 440 Hz
    private static final int DISPLAY_SAMPLES = (int)(3.0 / FREQ * SAMPLE_RATE);
    // Skip initial samples for block to settle
    private static final int SKIP_SAMPLES = (int)(0.1 * SAMPLE_RATE);

    private static final double[] LEVELS_LINEAR = {1.0, 0.5, 0.125}; // 0, -6, -18 dB
    private static final String[] LEVEL_LABELS = {"0 dB", "-6 dB", "-18 dB"};

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateWaveShaperPlots() throws Exception {
        File docsDir = new File("docs");
        docsDir.mkdirs();

        // === Cube (CubeGain) ===
        plotWaveShaper("CubeGain", "Cube",
            (level) -> new CubeGainCADBlock(100, 100),
            null, "Audio Output 1", docsDir);

        // === Distortion ===
        plotWaveShaper("Distortion", "Distortion",
            (level) -> new DistortionCADBlock(100, 100),
            null, "Audio Output 1", docsDir);

        // === Overdrive ===
        plotWaveShaper("Overdrive", "Overdrive",
            (level) -> { OverdriveCADBlock b = new OverdriveCADBlock(100, 100); return b; },
            Map.of("Drive", 750), "Audio Output 1", docsDir);

        // === Octave Fuzz ===
        plotWaveShaper("OctaveFuzz", "Octave Fuzz",
            (level) -> new OctaveCADBlock(100, 100),
            null, "Audio_Output", docsDir);

        // === T/X ===
        plotWaveShaper("ToverX", "T/X",
            (level) -> new ToverXCADBlock(100, 100),
            null, "Audio_Output", docsDir);

        // === Aliaser ===
        plotWaveShaper("Aliaser", "Aliaser",
            (level) -> new AliaserCADBlock(100, 100),
            Map.of("Rip", 500), "Smooth", docsDir);

        // === Quantizer ===
        plotWaveShaper("Quantizer", "Quantizer",
            (level) -> new QuantizerCADBlock(100, 100),
            null, "Audio Output 1", docsDir);

        System.out.println("\nAll wave shaper PNGs written to docs/");
    }

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(double level); }

    private void plotWaveShaper(String fileBase, String title,
            BlockFactory factory, Map<String, Integer> controlInputs,
            String outputPin, File docsDir) throws Exception {

        double[][] curves = new double[LEVELS_LINEAR.length][];
        double[] timeMs = null;

        for (int li = 0; li < LEVELS_LINEAR.length; li++) {
            File sineWav = generateSineWav(SIM_DURATION, FREQ, LEVELS_LINEAR[li]);
            SpinCADBlock block = factory.create(LEVELS_LINEAR[li]);

            short[] stereo = simulate(block, sineWav, controlInputs,
                outputPin, null, tempDir);

            if (stereo == null) {
                System.err.println("  SKIP " + title + " at " + LEVEL_LABELS[li] + ": simulation failed");
                return;
            }

            short[] left = extractChannel(stereo, 0);
            double[] audio = toDouble(left);

            // Extract display window after settling
            int start = Math.min(SKIP_SAMPLES, audio.length - DISPLAY_SAMPLES - 1);
            int end = Math.min(start + DISPLAY_SAMPLES, audio.length);
            curves[li] = Arrays.copyOfRange(audio, start, end);

            if (timeMs == null) {
                timeMs = new double[end - start];
                for (int i = 0; i < timeMs.length; i++) {
                    timeMs[i] = 1000.0 * i / SAMPLE_RATE;
                }
            }
        }

        writePlot(new File(docsDir, "waveshaper-" + fileBase.toLowerCase() + ".png"),
            title, "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, curves, LEVEL_LABELS,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});
    }
}
