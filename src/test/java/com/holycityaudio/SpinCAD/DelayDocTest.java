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
 * Each block is fed a 10 ms tone burst and the output waveform is plotted
 * to reveal tap positions and echo patterns.
 */
public class DelayDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 1.0;
    private static final double BURST_DURATION_MS = 10.0;
    private static final double BURST_FREQ = 1000.0;
    private static final double BURST_AMPLITUDE = 0.9;
    private static final int DECIMATE_FACTOR = 8;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateDelayPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        File burstWav = generateToneBurstWav(SIM_DURATION, BURST_DURATION_MS,
            BURST_FREQ, BURST_AMPLITUDE);

        // === Six Tap — distribute 6 taps evenly ===
        {
            sixtapCADBlock block = new sixtapCADBlock(100, 100);
            block.settap1Ratio(1.0 / 6);
            block.settap2Ratio(2.0 / 6);
            block.settap3Ratio(3.0 / 6);
            block.settap4Ratio(4.0 / 6);
            block.settap5Ratio(5.0 / 6);
            block.settap6Ratio(1.0);
            plotDelay("sixtap", "Six Tap Delay", block,
                "Mix L Out", null, burstWav, docsDir);
        }

        // === Eight Tap — taps already evenly spaced at 1/8 intervals ===
        {
            eighttapCADBlock block = new eighttapCADBlock(100, 100);
            plotDelay("eighttap", "Eight Tap Delay", block,
                "Mix 1 Out", null, burstWav, docsDir);
        }

        // === Drum Delay — distribute 4 taps evenly ===
        {
            DrumDelayCADBlock block = new DrumDelayCADBlock(100, 100);
            block.settap1Ratio(0.25);
            block.settap2Ratio(0.50);
            block.settap3Ratio(0.75);
            block.settap4Ratio(1.0);
            plotDelay("drumdelay", "Drum Delay", block,
                "Tap 1 Out", null, burstWav, docsDir);
        }

        // === Long Delay ===
        try {
            LongDelayCADBlock block = new LongDelayCADBlock(100, 100);
            plotDelay("longdelay", "Long Delay", block,
                "Audio Output", null, burstWav, docsDir);
        } catch (Exception e) {
            System.err.println("  SKIP Long Delay: " + e.getMessage());
        }

        // === MN3011 — taps at fixed BBD ratios ===
        {
            MN3011aCADBlock block = new MN3011aCADBlock(100, 100);
            plotDelay("mn3011a", "MN3011 BBD Emulation", block,
                "Mix Out", null, burstWav, docsDir);
        }

        // === Reverse Delay (no adjustable taps) ===
        {
            ReverseDelayCADBlock block = new ReverseDelayCADBlock(100, 100);
            plotDelay("reversedelay", "Reverse Delay", block,
                "Output", null, burstWav, docsDir);
        }

        // === Stutter (no adjustable taps) ===
        {
            StutterCADBlock block = new StutterCADBlock(100, 100);
            plotDelay("stutter", "Stutter", block,
                "Output", null, burstWav, docsDir);
        }

        // === Triple Tap — distribute 3 taps evenly ===
        {
            TripleTapCADBlock block = new TripleTapCADBlock(100, 100);
            block.settap1Ratio(1.0 / 3);
            block.settap2Ratio(2.0 / 3);
            block.settap3Ratio(1.0);
            plotDelay("tripletap", "Triple Tap Delay", block,
                "Tap 1 Out", null, burstWav, docsDir);
        }

        System.out.println("\nAll delay PNGs written to docs/images/");
    }

    private void plotDelay(String fileBase, String title,
            SpinCADBlock block, String outputPin1, String outputPin2,
            File burstWav, File docsDir) throws Exception {

        short[] stereo = simulate(block, burstWav, null,
            outputPin1, outputPin2, tempDir);

        if (stereo == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }

        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);

        double[] plotAudio = decimate(audio, DECIMATE_FACTOR);
        double[] timeMs = timeAxisMs(plotAudio.length, SAMPLE_RATE / DECIMATE_FACTOR);

        double maxTime = timeMs[timeMs.length - 1];

        writePlot(new File(docsDir, "delay-" + fileBase + ".png"),
            title, "Time (ms)", "Amplitude",
            0, maxTime, -1.0, 1.0,
            timeMs, new double[][]{plotAudio},
            new String[]{"Tone Burst Response"},
            new String[]{COLORS[0]});
        System.out.println("  wrote delay-" + fileBase + ".png");
    }
}
