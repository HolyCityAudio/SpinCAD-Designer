package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Reverb menu blocks.
 * Each block is fed an impulse (0.5 amplitude, 1.0 s duration) and the
 * output envelope is plotted at three reverb-time settings (short/medium/long).
 * Blocks with a pre-delay parameter get an additional plot showing the
 * effect of pre-delay at three settings.
 *
 * Control pins are left DISCONNECTED; reverb time is set via the block's
 * setter method.
 */
public class ReverbDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 1.0;
    private static final double IMPULSE_AMP = 0.5;

    // Decimate for plotting: take every Nth sample so plots are not too dense
    private static final int DECIMATE = 32;

    private static final String[] RT_LABELS = {"Short", "Medium", "Long"};
    private static final String[] PD_LABELS = {"No pre-delay", "Medium pre-delay", "Long pre-delay"};

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // =========================================================================
    // Helper: simulate an impulse through a block, return left-channel envelope
    // =========================================================================

    private double[] impulseResponse(SpinCADBlock block,
            String outPin1, String outPin2) throws Exception {
        File impulseWav = generateImpulseWav(SIM_DURATION, IMPULSE_AMP);
        short[] stereo = simulate(block, impulseWav, null, outPin1, outPin2, tempDir);
        if (stereo == null) return null;
        short[] left = extractChannel(stereo, 0);
        double[] audio = toDouble(left);
        // Compute envelope: absolute value, then decimate
        double[] env = new double[audio.length];
        for (int i = 0; i < audio.length; i++) env[i] = Math.abs(audio[i]);
        return decimate(env, DECIMATE);
    }

    // =========================================================================
    // Plot helper
    // =========================================================================

    private void plotIR(File outFile, String title, double[][] curves,
            String[] labels, int decimatedLen) throws IOException {
        double[] timeMs = new double[decimatedLen];
        for (int i = 0; i < decimatedLen; i++) {
            timeMs[i] = 1000.0 * i * DECIMATE / SAMPLE_RATE;
        }
        double maxY = 0;
        for (double[] c : curves) {
            if (c == null) continue;
            for (double v : c) if (v > maxY) maxY = v;
        }
        if (maxY < 0.01) maxY = 0.5;
        maxY = Math.min(maxY * 1.1, 1.0);

        writePlot(outFile, title, "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], 0, maxY,
            timeMs, curves, labels,
            new String[]{COLORS[0], COLORS[1], COLORS[2]});
    }

    // =========================================================================
    // Main test
    // =========================================================================

    @Test
    void generateReverbPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // === Adjustable Reverb (reverbCADBlock) ===
        plotReverb("reverb", "Adjustable Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                reverbCADBlock b = new reverbCADBlock(100, 100);
                double[] klaps = {0.3, 0.6, 0.85};
                b.setklap(klaps[level]);
                return impulseResponse(b, "Output_Left", "Output_Right");
            }
        });

        // === Allpass ===
        plotReverb("allpass", "Allpass", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                allpassCADBlock b = new allpassCADBlock(100, 100);
                double[] kiaps = {0.3, 0.5, 0.7};
                b.setkiap(kiaps[level]);
                return impulseResponse(b, "Output", null);
            }
        });

        // === Ambience ===
        plotReverb("ambience", "Ambience", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                AmbienceCADBlock b = new AmbienceCADBlock(100, 100);
                double[] decays = {0.2, 0.5, 0.9};
                b.setDecay(decays[level]);
                return impulseResponse(b, "Audio Output L", "Audio Output R");
            }
        });

        // === Chirp ===
        plotReverb("chirp", "Chirp Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                ChirpCADBlock b = new ChirpCADBlock(100, 100);
                double[] kiaps = {0.3, 0.5, 0.7};
                b.setkiap(kiaps[level]);
                return impulseResponse(b, "Output", null);
            }
        });

        // === Freeverb ===
        plotReverb("freeverb", "Freeverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                FreeverbCADBlock b = new FreeverbCADBlock(100, 100);
                double[] krts = {0.2, 0.42, 0.7};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });

        // === Hall Reverb (has pre-delay) ===
        plotReverb("hall", "Hall Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                reverb_hallCADBlock b = new reverb_hallCADBlock(100, 100);
                double[] krts = {0.25, 0.5, 0.8};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });
        // Hall pre-delay plot
        plotPreDelay("hall", "Hall Reverb Pre-Delay", docsDir, new PreDelayPlotter() {
            public double[] run(int level) throws Exception {
                reverb_hallCADBlock b = new reverb_hallCADBlock(100, 100);
                b.setkrt(0.5);
                double[] inputkaps = {0.0, 0.5, 0.9};
                b.setinputkap(inputkaps[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });

        // === Dattorro Plate Reverb ===
        plotReverb("dattorro", "Dattorro Plate Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                DattorroPlateReverbCADBlock b = new DattorroPlateReverbCADBlock(100, 100);
                double[] decays = {0.2, 0.5, 0.9};
                b.setDecay(decays[level]);
                return impulseResponse(b, "Audio Output L", "Audio Output R");
            }
        });

        // === Reverb Designer ===
        plotReverb("reverbdesigner", "Reverb Designer", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                ReverbDesignerCADBlock b = new ReverbDesignerCADBlock(100, 100);
                double[] rts = {0.2, 0.5, 0.9};
                b.setReverbTime(rts[level]);
                return impulseResponse(b, "Out L", "Out R");
            }
        });

        // === ROM Reverb 1 ===
        plotReverb("rom_rev1", "ROM Reverb 1", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                rom_rev1CADBlock b = new rom_rev1CADBlock(100, 100);
                double[] kapds = {0.3, 0.6, 0.85};
                b.setkapd1(kapds[level]);
                return impulseResponse(b, "Output_Left", "Output_Right");
            }
        });

        // === ROM Reverb 2 ===
        plotReverb("rom_rev2", "ROM Reverb 2", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                rom_rev2CADBlock b = new rom_rev2CADBlock(100, 100);
                double[] rts = {0.3, 0.6, 0.9};
                b.setrevTimeMax(rts[level]);
                return impulseResponse(b, "Output", null);
            }
        });

        // === Room Reverb (has pre-delay) ===
        plotReverb("room", "Room Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                reverb_roomCADBlock b = new reverb_roomCADBlock(100, 100);
                double[] krts = {0.25, 0.5, 0.8};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });
        // Room pre-delay plot
        plotPreDelay("room", "Room Reverb Pre-Delay", docsDir, new PreDelayPlotter() {
            public double[] run(int level) throws Exception {
                reverb_roomCADBlock b = new reverb_roomCADBlock(100, 100);
                b.setkrt(0.5);
                double[] inputkaps = {0.0, 0.5, 0.9};
                b.setinputkap(inputkaps[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });

        // === Min Reverb ===
        plotReverb("minreverb", "Min Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                // MinReverbCADBlock has no public setter for krt;
                // use a single default setting
                MinReverbCADBlock b = new MinReverbCADBlock(100, 100);
                return impulseResponse(b, "Audio Output 1", null);
            }
        });

        // === Min Reverb 2 (stereo) ===
        plotReverb("minreverb2", "Small Reverb (Stereo)", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                MinReverb2CADBlock b = new MinReverb2CADBlock(100, 100);
                double[] klaps = {0.3, 0.6, 0.85};
                b.setklap(klaps[level]);
                return impulseResponse(b, "Output_Left", "Output_Right");
            }
        });

        // === Spring Reverb ===
        plotReverb("spring", "Spring Reverb", docsDir, new ReverbPlotter() {
            public double[] run(int level) throws Exception {
                SpringReverbCADBlock b = new SpringReverbCADBlock(100, 100);
                double[] krts = {0.5, 0.75, 0.95};
                b.setkrt(krts[level]);
                return impulseResponse(b, "OutputL", "OutputR");
            }
        });

        System.out.println("\nAll reverb PNGs written to docs/");
    }

    // =========================================================================
    // Plot wrappers
    // =========================================================================

    @FunctionalInterface
    interface ReverbPlotter { double[] run(int level) throws Exception; }

    @FunctionalInterface
    interface PreDelayPlotter { double[] run(int level) throws Exception; }

    private void plotReverb(String fileBase, String title, File docsDir,
            ReverbPlotter plotter) throws Exception {
        double[][] curves = new double[3][];
        int minLen = Integer.MAX_VALUE;
        boolean anySuccess = false;

        for (int i = 0; i < 3; i++) {
            try {
                curves[i] = plotter.run(i);
                if (curves[i] != null) {
                    anySuccess = true;
                    if (curves[i].length < minLen) minLen = curves[i].length;
                }
            } catch (Exception e) {
                System.err.println("  SKIP " + title + " level " + i + ": " + e.getMessage());
            }
        }

        if (!anySuccess) {
            System.err.println("  SKIP " + title + ": all simulations failed");
            return;
        }

        // If some levels failed (e.g. MinReverb with no setter), duplicate the
        // successful curve so the plot still renders three overlapping lines
        int firstGood = -1;
        for (int i = 0; i < 3; i++) {
            if (curves[i] != null) { firstGood = i; break; }
        }
        for (int i = 0; i < 3; i++) {
            if (curves[i] == null) curves[i] = curves[firstGood];
            if (curves[i].length < minLen) minLen = curves[i].length;
        }

        // Trim to common length
        for (int i = 0; i < 3; i++) {
            curves[i] = Arrays.copyOf(curves[i], minLen);
        }

        plotIR(new File(docsDir, "reverb-" + fileBase + ".png"),
            title, curves, RT_LABELS, minLen);
        System.out.println("  wrote reverb-" + fileBase + ".png");
    }

    private void plotPreDelay(String fileBase, String title, File docsDir,
            PreDelayPlotter plotter) throws Exception {
        double[][] curves = new double[3][];
        int minLen = Integer.MAX_VALUE;
        boolean anySuccess = false;

        for (int i = 0; i < 3; i++) {
            try {
                curves[i] = plotter.run(i);
                if (curves[i] != null) {
                    anySuccess = true;
                    if (curves[i].length < minLen) minLen = curves[i].length;
                }
            } catch (Exception e) {
                System.err.println("  SKIP " + title + " level " + i + ": " + e.getMessage());
            }
        }

        if (!anySuccess) {
            System.err.println("  SKIP " + title + " (pre-delay): all simulations failed");
            return;
        }

        int firstGood = -1;
        for (int i = 0; i < 3; i++) {
            if (curves[i] != null) { firstGood = i; break; }
        }
        for (int i = 0; i < 3; i++) {
            if (curves[i] == null) curves[i] = curves[firstGood];
            if (curves[i].length < minLen) minLen = curves[i].length;
        }
        for (int i = 0; i < 3; i++) {
            curves[i] = Arrays.copyOf(curves[i], minLen);
        }

        plotIR(new File(docsDir, "reverb-" + fileBase + "-predelay.png"),
            title, curves, PD_LABELS, minLen);
        System.out.println("  wrote reverb-" + fileBase + "-predelay.png");
    }
}
