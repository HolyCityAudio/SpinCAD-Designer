package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Delay menu blocks.
 * Each block is fed an impulse and the output waveform is plotted
 * to reveal tap positions and echo patterns.
 */
public class DelayDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 1.0;
    private static final double IMPULSE_AMPLITUDE = 0.9;
    private static final int DECIMATE_FACTOR = 8;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateDelayPlots() throws Exception {
        File docsDir = new File("docs");
        docsDir.mkdirs();

        // === Six Tap ===
        plotDelay("sixtap", "Six Tap Delay",
            () -> new sixtapCADBlock(100, 100),
            "Mix L Out", null, docsDir);

        // === Eight Tap ===
        plotDelay("eighttap", "Eight Tap Delay",
            () -> new eighttapCADBlock(100, 100),
            "Mix 1 Out", null, docsDir);

        // === Drum Delay ===
        plotDelay("drumdelay", "Drum Delay",
            () -> new DrumDelayCADBlock(100, 100),
            "Tap 1 Out", null, docsDir);

        // === Long Delay ===
        try {
            plotDelay("longdelay", "Long Delay",
                () -> new LongDelayCADBlock(100, 100),
                "Audio Output", null, docsDir);
        } catch (Exception e) {
            System.err.println("  SKIP Long Delay: " + e.getMessage());
        }

        // === MN3011 ===
        plotDelay("mn3011a", "MN3011 BBD Emulation",
            () -> new MN3011aCADBlock(100, 100),
            "Mix Out", null, docsDir);

        // === Reverse Delay ===
        plotDelay("reversedelay", "Reverse Delay",
            () -> new ReverseDelayCADBlock(100, 100),
            "Output", null, docsDir);

        // === Stutter ===
        plotDelay("stutter", "Stutter",
            () -> new StutterCADBlock(100, 100),
            "Output", null, docsDir);

        // === Triple Tap ===
        plotDelay("tripletap", "Triple Tap Delay",
            () -> new TripleTapCADBlock(100, 100),
            "Tap 1 Out", null, docsDir);

        System.out.println("\nAll delay PNGs written to docs/");
    }

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(); }

    private void plotDelay(String fileBase, String title,
            BlockFactory factory, String outputPin1, String outputPin2,
            File docsDir) throws Exception {

        File impulseWav = generateImpulseWav(SIM_DURATION, IMPULSE_AMPLITUDE);
        SpinCADBlock block = factory.create();

        short[] stereo = simulate(block, impulseWav, null,
            outputPin1, outputPin2, tempDir);

        if (stereo == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }

        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);

        // Decimate for plotting
        double[] plotAudio = decimate(audio, DECIMATE_FACTOR);
        double[] timeMs = timeAxisMs(plotAudio.length, SAMPLE_RATE / DECIMATE_FACTOR);

        double maxTime = timeMs[timeMs.length - 1];

        writePlot(new File(docsDir, "delay-" + fileBase + ".png"),
            title, "Time (ms)", "Amplitude",
            0, maxTime, -1.0, 1.0,
            timeMs, new double[][]{plotAudio},
            new String[]{"Impulse Response"},
            new String[]{COLORS[0]});
    }
}
