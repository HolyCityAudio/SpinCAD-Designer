package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Sweep test for control-menu blocks.
 * Measures the DC transfer function of each block by sweeping a constant
 * control input from 0 to 1, then renders individual PNG plots per block
 * into docs/.
 */
public class ControlBlockSweepTest {

    @TempDir
    File tempDir;

    private static final int SAMPLE_RATE = ElmProgram.SAMPLERATE;
    private static final int NUM_POINTS = 51;
    private static final double SIM_DURATION = 0.25;
    private static final long SIM_TIMEOUT = 30000;

    private static final int[] SWEEP_VALS = new int[NUM_POINTS];
    private static final double[] SWEEP_X = new double[NUM_POINTS];

    private static final String[] COLORS = {
        "#2266cc", "#cc4422", "#22aa44", "#aa44cc", "#cc8800", "#8844cc", "#cc2288"
    };

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
        for (int i = 0; i < NUM_POINTS; i++) {
            SWEEP_X[i] = i / (double)(NUM_POINTS - 1);
            SWEEP_VALS[i] = (int) Math.round(SWEEP_X[i] * 999);
        }
    }

    @Test
    void sweepAllControlBlocks() throws Exception {
        File docsDir = new File("docs");
        docsDir.mkdirs();
        int sinePoints = 200;

        // === 1. Invert ===
        double[] invertOut = sweepControl(
            () -> new InvertControlCADBlock(100, 100),
            "Control Input 1", "Control Output 1", null);
        double[] unity = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) unity[i] = SWEEP_X[i];

        writeSinglePlot(new File(docsDir, "control-invert.png"),
            "Invert", "Control In", "Control Out", 0, 1,
            new double[][]{invertOut, unity},
            new String[]{"1 - x", "Unity (ref)"},
            new String[]{COLORS[0], "#aaa"}, null);

        // === 2. Power ===
        int[] powers = {2, 3, 4, 5};
        double[][] powerCurves = new double[4][NUM_POINTS];
        for (int pi = 0; pi < powers.length; pi++) {
            final int pw = powers[pi];
            powerCurves[pi] = sweepControl(
                () -> { PowerControlCADBlock b = new PowerControlCADBlock(100, 100); b.setPower(pw); return b; },
                "Control Input 1", "Control Output 1", null);
        }
        writeSinglePlot(new File(docsDir, "control-power.png"),
            "Power", "Control In", "Control Out", 0, 1,
            powerCurves,
            new String[]{"x\u00B2", "x\u00B3", "x\u2074", "x\u2075"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]}, null);

        // Power flip/invert — separate charts
        String[][] pwModes = {
            {"Normal: x\u00B3", null, null},
            {"Invert: (1-x)\u00B3", "invert", null},
            {"Flip: 1-x\u00B3", null, "flip"},
            {"Invert+Flip: 1-(1-x)\u00B3", "invert", "flip"}
        };
        for (int m = 0; m < pwModes.length; m++) {
            final boolean inv = pwModes[m][1] != null;
            final boolean flp = pwModes[m][2] != null;
            double[] curve = sweepControl(
                () -> {
                    PowerControlCADBlock b = new PowerControlCADBlock(100, 100);
                    b.setPower(3);
                    if (inv) b.setInvert(true);
                    if (flp) b.setFlip(true);
                    return b;
                }, "Control Input 1", "Control Output 1", null);
            writeSinglePlot(new File(docsDir, "control-power-" + m + ".png"),
                "Power (p=3): " + pwModes[m][0].split(":")[0].trim(), "Control In", "Control Out", 0, 1,
                new double[][]{curve},
                new String[]{pwModes[m][0]},
                new String[]{COLORS[m]}, null);
        }

        // === 3. Two Stage ===
        double[] ts1 = new double[NUM_POINTS];
        double[] ts2 = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            Two_StageCADBlock block = new Two_StageCADBlock(100, 100);
            double[] r = simulateControlDC(block,
                Map.of("Input", SWEEP_VALS[i]), "Stage 1", "Stage 2");
            ts1[i] = r != null ? r[0] : Double.NaN;
            ts2[i] = r != null ? r[1] : Double.NaN;
        }
        writeSinglePlot(new File(docsDir, "control-two-stage.png"),
            "Two Stage", "Control In", "Control Out", 0, 1,
            new double[][]{ts1, ts2},
            new String[]{"Stage 1", "Stage 2"},
            new String[]{COLORS[0], COLORS[1]}, null);

        // === 4. Vee (both outputs connected) ===
        double[] veeOut1 = new double[NUM_POINTS];
        double[] veeOut2 = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            VeeCADBlock block = new VeeCADBlock(100, 100);
            double[] r = simulateControlDC(block,
                Map.of("Input", SWEEP_VALS[i]), "Output 1", "Output 2");
            veeOut1[i] = r != null ? r[0] : Double.NaN;
            veeOut2[i] = r != null ? r[1] : Double.NaN;
        }
        writeSinglePlot(new File(docsDir, "control-vee.png"),
            "Vee (both outputs connected)", "Control In", "Control Out", 0, 1,
            new double[][]{veeOut1, veeOut2},
            new String[]{"Output 1", "Output 2"},
            new String[]{COLORS[0], COLORS[1]}, null);

        // Vee with only one output connected at a time
        double[] veeOnly1 = new double[NUM_POINTS];
        double[] veeOnly2 = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            VeeCADBlock b1 = new VeeCADBlock(100, 100);
            double[] r1 = simulateControlDC(b1,
                Map.of("Input", SWEEP_VALS[i]), "Output 1", null);
            veeOnly1[i] = r1 != null ? r1[0] : Double.NaN;

            VeeCADBlock b2 = new VeeCADBlock(100, 100);
            double[] r2 = simulateControlDC(b2,
                Map.of("Input", SWEEP_VALS[i]), "Output 2", null);
            veeOnly2[i] = r2 != null ? r2[0] : Double.NaN;
        }
        writeSinglePlot(new File(docsDir, "control-vee-single.png"),
            "Vee (single output connected)", "Control In", "Control Out", 0, 1,
            new double[][]{veeOnly1, veeOnly2},
            new String[]{"Output 1 only", "Output 2 only"},
            new String[]{COLORS[0], COLORS[1]}, null);

        // === 5. Ratio (sweep both outputs simultaneously) ===
        double[] ratioVals = {2, 5, 10, 50};
        double[][] ratioFullRange = new double[4][NUM_POINTS];
        double[][] ratioCurves = new double[4][NUM_POINTS];
        for (int ri = 0; ri < ratioVals.length; ri++) {
            final double rv = ratioVals[ri];
            for (int i = 0; i < NUM_POINTS; i++) {
                RatioCADBlock block = new RatioCADBlock(100, 100);
                block.setinvRatio(rv);
                double[] r = simulateControlDC(block,
                    Map.of("Input", SWEEP_VALS[i]), "FullRange", "Ratio");
                ratioFullRange[ri][i] = r != null ? r[0] : Double.NaN;
                ratioCurves[ri][i] = r != null ? r[1] : Double.NaN;
            }
        }
        writeSinglePlot(new File(docsDir, "control-ratio-fullrange.png"),
            "Ratio: FullRange Output", "Control In", "Control Out", 0, 1,
            ratioFullRange,
            new String[]{"1:2", "1:5", "1:10", "1:50"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]}, null);
        writeSinglePlot(new File(docsDir, "control-ratio-ratio.png"),
            "Ratio: Ratio Output", "Control In", "Control Out", 0, 1,
            ratioCurves,
            new String[]{"1:2", "1:5", "1:10", "1:50"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]}, null);

        double[][] ratioProduct = new double[4][NUM_POINTS];
        for (int ri = 0; ri < ratioVals.length; ri++) {
            for (int i = 0; i < NUM_POINTS; i++) {
                double fr = Double.isNaN(ratioFullRange[ri][i]) ? 0 : ratioFullRange[ri][i];
                double ra = Double.isNaN(ratioCurves[ri][i]) ? 0 : ratioCurves[ri][i];
                ratioProduct[ri][i] = fr * ra;
            }
        }
        writeSinglePlot(new File(docsDir, "control-ratio-product.png"),
            "Ratio: FullRange \u00D7 Ratio", "Control In", "Product", 0, 1,
            ratioProduct,
            new String[]{"1:2", "1:5", "1:10", "1:50"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]}, null);

        // === 6. Clip ===
        double[] gains = {1, 2, 4, 8};
        double[][] clipCurves = new double[4][NUM_POINTS];
        for (int gi = 0; gi < gains.length; gi++) {
            final double g = gains[gi];
            clipCurves[gi] = sweepControl(
                () -> { ClipControlCADBlock b = new ClipControlCADBlock(100, 100); b.setGain(g); return b; },
                "Control Input 1", "Control Output 1", null);
        }
        writeSinglePlot(new File(docsDir, "control-clip.png"),
            "Clip", "Control In", "Control Out", 0, 1,
            clipCurves,
            new String[]{"1x", "2x", "4x", "8x"},
            new String[]{COLORS[0], COLORS[1], COLORS[2], COLORS[3]}, null);

        // Clip flip/invert — separate charts
        String[][] clModes = {
            {"Normal", null, null},
            {"Flip", null, "flip"},
            {"Invert", "invert", null},
            {"Flip + Invert", "invert", "flip"}
        };
        for (int m = 0; m < clModes.length; m++) {
            final boolean inv = clModes[m][1] != null;
            final boolean flp = clModes[m][2] != null;
            double[] curve = sweepControl(
                () -> {
                    ClipControlCADBlock b = new ClipControlCADBlock(100, 100);
                    b.setGain(10);
                    if (inv) b.setInvert(true);
                    if (flp) b.setFlip(true);
                    return b;
                }, "Control Input 1", "Control Output 1", null);
            writeSinglePlot(new File(docsDir, "control-clip-" + m + ".png"),
                "Clip (gain=10): " + clModes[m][0], "Control In", "Control Out", 0, 1,
                new double[][]{curve},
                new String[]{clModes[m][0]},
                new String[]{COLORS[m]}, null);
        }

        // === 7. Half Wave ===
        double[] halfWaveOut = sweepControl(
            () -> new Half_WaveCADBlock(100, 100),
            "Input", "Output", null);
        writeSinglePlot(new File(docsDir, "control-halfwave.png"),
            "Half Wave", "Control In", "Control Out", 0, 1,
            new double[][]{halfWaveOut},
            new String[]{"max(0, input)"}, new String[]{COLORS[0]}, null);

        // Half Wave on sine (computed)
        double[] sineX = new double[sinePoints];
        double[] sineIn = new double[sinePoints];
        double[] sineHW = new double[sinePoints];
        for (int i = 0; i < sinePoints; i++) {
            sineX[i] = i / (double)(sinePoints - 1);
            sineIn[i] = Math.sin(2 * Math.PI * 2 * sineX[i]);
            sineHW[i] = Math.max(0, sineIn[i]);
        }
        writeTimeDomainPlot(new File(docsDir, "control-halfwave-sine.png"),
            "Half Wave on Sine", "Time", "Amplitude", -1, 1,
            sineX, new double[][]{sineIn, sineHW},
            new String[]{"Input", "Output"},
            new String[]{"#aaa", COLORS[0]}, null);

        // === 8. Slicer (DC transfer) ===
        double[] sliceLevels = {0.2, 0.5, 0.8};
        double[][] slicerCurves = new double[3][NUM_POINTS];
        for (int si = 0; si < sliceLevels.length; si++) {
            final double sl = sliceLevels[si];
            slicerCurves[si] = sweepControl(
                () -> { SlicerCADBlock b = new SlicerCADBlock(100, 100); b.setslice(sl); return b; },
                "Control In", "Slicer Out", null);
        }
        writeSinglePlot(new File(docsDir, "control-slicer.png"),
            "Slicer", "Control In", "Control Out", 0, 1,
            slicerCurves,
            new String[]{"slice=0.2", "slice=0.5", "slice=0.8"},
            new String[]{COLORS[0], COLORS[1], COLORS[2]}, null);

        // Slicer on sine — separate chart per slice level with threshold line
        double[] sliceSineX = new double[sinePoints];
        double[] sliceSineIn = new double[sinePoints];
        for (int i = 0; i < sinePoints; i++) {
            sliceSineX[i] = i / (double)(sinePoints - 1);
            sliceSineIn[i] = 0.5 + 0.5 * Math.sin(2 * Math.PI * 2 * sliceSineX[i]);
        }
        for (int si = 0; si < sliceLevels.length; si++) {
            double[] sliceOut = new double[sinePoints];
            for (int i = 0; i < sinePoints; i++) {
                sliceOut[i] = (sliceSineIn[i] < sliceLevels[si]) ? 0.999 : 0.0;
            }
            // Horizontal threshold lines as extra annotation
            HLine hline = new HLine(sliceLevels[si], COLORS[si]);
            writeTimeDomainPlot(new File(docsDir, "control-slicer-sine-" + si + ".png"),
                String.format("Slicer: slice=%.1f", sliceLevels[si]),
                "Time (2 cycles)", "Amplitude", 0, 1,
                sliceSineX, new double[][]{sliceSineIn, sliceOut},
                new String[]{"Sine input", String.format("Output (slice=%.1f)", sliceLevels[si])},
                new String[]{"#aaa", COLORS[si]}, hline);
        }

        // === 9. Tremolizer ===
        double[] tremDepths = {0.5, 0.75, 0.999};
        double[][] tremCurves = new double[3][NUM_POINTS];
        for (int di = 0; di < tremDepths.length; di++) {
            final double d = tremDepths[di];
            tremCurves[di] = sweepControl(
                () -> { tremolizerCADBlock b = new tremolizerCADBlock(100, 100); b.setdepth(d); return b; },
                "LFO Input", "Control Output", null);
        }
        writeSinglePlot(new File(docsDir, "control-tremolizer.png"),
            "Tremolizer", "LFO Input", "Control Out", 0, 1,
            tremCurves,
            new String[]{"depth=0.5", "depth=0.75", "depth=1.0"},
            new String[]{COLORS[0], COLORS[1], COLORS[2]}, null);

        // Tremolizer on sine (computed)
        double[] tremSineX = new double[sinePoints];
        double[] tremSineIn = new double[sinePoints];
        double[][] tremSineOut = new double[3][sinePoints];
        for (int i = 0; i < sinePoints; i++) {
            tremSineX[i] = i / (double)(sinePoints - 1);
            double lfo = 0.5 + 0.5 * Math.sin(2 * Math.PI * 2 * tremSineX[i]);
            tremSineIn[i] = lfo;
            for (int di = 0; di < tremDepths.length; di++) {
                tremSineOut[di][i] = Math.max(0, 1.0 - tremDepths[di] * lfo);
            }
        }
        double[][] tremSineAll = new double[4][sinePoints];
        tremSineAll[0] = tremSineIn;
        System.arraycopy(tremSineOut, 0, tremSineAll, 1, 3);
        writeTimeDomainPlot(new File(docsDir, "control-tremolizer-sine.png"),
            "Tremolizer: LFO \u2192 Volume Envelope", "Time (2 cycles)", "Amplitude", 0, 1,
            tremSineX, tremSineAll,
            new String[]{"LFO input", "depth=0.5", "depth=0.75", "depth=1.0"},
            new String[]{"#aaa", COLORS[0], COLORS[1], COLORS[2]}, null);

        System.out.println("\nAll PNGs written to docs/");
    }

    // ==================== Sweep helpers ====================

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(); }

    private double[] sweepControl(BlockFactory factory, String inputPin, String outputPin1, String outputPin2) {
        double[] out = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            SpinCADBlock block = factory.create();
            double[] r = simulateControlDC(block, Map.of(inputPin, SWEEP_VALS[i]), outputPin1, outputPin2);
            out[i] = r != null ? r[0] : Double.NaN;
        }
        return out;
    }

    // ==================== PNG rendering ====================

    private static final int PLOT_W = 360, PLOT_H = 280;
    private static final int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 85;

    /** Optional horizontal reference line annotation. */
    static class HLine {
        final double y;
        final String color;
        HLine(double y, String color) { this.y = y; this.color = color; }
    }

    private static Color parseColor(String hex) {
        return Color.decode(hex);
    }

    /** Write a single-plot PNG with 0-1 x-axis and overlaid curves. */
    private void writeSinglePlot(File file, String title, String xLabel, String yLabel,
            double yMin, double yMax,
            double[][] curves, String[] labels, String[] colors, HLine hline) throws IOException {
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + PAD_B;

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        int px = PAD_L, py = PAD_T;
        drawPlot(g, px, py, PLOT_W, PLOT_H, title, xLabel, yLabel, 0, 1, yMin, yMax);

        if (hline != null) drawHLine(g, px, py, PLOT_W, PLOT_H, yMin, yMax, hline);

        for (int ci = 0; ci < curves.length; ci++) {
            drawCurve(g, SWEEP_X, curves[ci], px, py, PLOT_W, PLOT_H, 0, 1, yMin, yMax, colors[ci]);
        }

        drawLegend(g, px, py + PLOT_H + 52, labels, colors);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    /** Write a time-domain plot PNG with arbitrary x/y data arrays. */
    private void writeTimeDomainPlot(File file, String title, String xLabel, String yLabel,
            double yMin, double yMax,
            double[] xData, double[][] curves,
            String[] labels, String[] colors, HLine hline) throws IOException {
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + PAD_B;

        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);

        int px = PAD_L, py = PAD_T;
        drawPlot(g, px, py, PLOT_W, PLOT_H, title, xLabel, yLabel, 0, 1, yMin, yMax);

        if (hline != null) drawHLine(g, px, py, PLOT_W, PLOT_H, yMin, yMax, hline);

        for (int ci = 0; ci < curves.length; ci++) {
            drawCurve(g, xData, curves[ci], px, py, PLOT_W, PLOT_H, 0, 1, yMin, yMax, colors[ci]);
        }

        drawLegend(g, px, py + PLOT_H + 52, labels, colors);

        g.dispose();
        ImageIO.write(img, "png", file);
    }

    /** Create a Graphics2D context with antialiasing and white background. */
    private Graphics2D createGraphics(BufferedImage img, int w, int h) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        return g;
    }

    /** Draw a horizontal dotted reference line with label. */
    private void drawHLine(Graphics2D g, int px, int py, int plotW, int plotH,
            double yMin, double yMax, HLine hline) {
        double frac = (hline.y - yMin) / (yMax - yMin);
        int gy = py + (int)((1.0 - frac) * plotH);
        Stroke old = g.getStroke();
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{6, 4}, 0));
        g.setColor(parseColor(hline.color));
        g.drawLine(px, gy, px + plotW, gy);
        g.setStroke(old);
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        g.drawString(String.format("slice=%.1f", hline.y), px + plotW + 2, gy + 3);
    }

    /** Draw plot frame with grid, labels, and optional unity diagonal. */
    private void drawPlot(Graphics2D g, int px, int py, int plotW, int plotH,
            String title, String xLabel, String yLabel,
            double xMin, double xMax, double yMin, double yMax) {
        // Plot background
        g.setColor(new Color(0xf8, 0xf8, 0xf8));
        g.fillRect(px, py, plotW, plotH);
        g.setColor(new Color(0xcc, 0xcc, 0xcc));
        g.drawRect(px, py, plotW, plotH);

        // Title
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, px + plotW / 2 - fm.stringWidth(title) / 2, py - 10);

        double yRange = yMax - yMin;
        int numTicks = 5;

        // X-axis grid and labels
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        for (int tick = 0; tick <= numTicks; tick++) {
            double frac = tick / (double) numTicks;
            double xVal = xMin + frac * (xMax - xMin);
            int gx = px + (int)(frac * plotW);
            g.setColor(new Color(0xdd, 0xdd, 0xdd));
            g.drawLine(gx, py, gx, py + plotH);
            g.setColor(new Color(0x66, 0x66, 0x66));
            String label = String.format("%.1f", xVal);
            fm = g.getFontMetrics();
            g.drawString(label, gx - fm.stringWidth(label) / 2, py + plotH + 13);
        }

        // Y-axis grid and labels
        for (int tick = 0; tick <= numTicks; tick++) {
            double frac = tick / (double) numTicks;
            double yVal = yMin + frac * yRange;
            int gy = py + (int)((1.0 - frac) * plotH);
            g.setColor(new Color(0xdd, 0xdd, 0xdd));
            g.drawLine(px, gy, px + plotW, gy);
            g.setColor(new Color(0x66, 0x66, 0x66));
            String label;
            if (yVal == (int) yVal) label = String.valueOf((int) yVal);
            else label = String.format("%.1f", yVal);
            fm = g.getFontMetrics();
            g.drawString(label, px - 4 - fm.stringWidth(label), gy + 3);
        }

        // Unity diagonal
        if (Math.abs(yMin - xMin) < 0.01 && Math.abs(yMax - xMax) < 0.01) {
            Stroke old = g.getStroke();
            g.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{4, 3}, 0));
            g.setColor(new Color(0xbb, 0xbb, 0xbb));
            g.drawLine(px, py + plotH, px + plotW, py);
            g.setStroke(old);
        }

        // X-axis label
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(0x33, 0x33, 0x33));
        fm = g.getFontMetrics();
        g.drawString(xLabel, px + plotW / 2 - fm.stringWidth(xLabel) / 2, py + plotH + 30);

        // Y-axis label (rotated)
        java.awt.geom.AffineTransform origTransform = g.getTransform();
        g.rotate(-Math.PI / 2, px - 35, py + plotH / 2);
        fm = g.getFontMetrics();
        g.drawString(yLabel, px - 35 - fm.stringWidth(yLabel) / 2, py + plotH / 2 + 4);
        g.setTransform(origTransform);

        // Plot border
        g.setColor(new Color(0x99, 0x99, 0x99));
        g.drawRect(px, py, plotW, plotH);
    }

    /** Draw a curve from x/y data arrays. */
    private void drawCurve(Graphics2D g, double[] xData, double[] yData,
            int px, int py, int plotW, int plotH,
            double xMin, double xMax, double yMin, double yMax, String color) {
        double xRange = xMax - xMin;
        double yRange = yMax - yMin;
        int[] xPoints = new int[xData.length];
        int[] yPoints = new int[xData.length];
        int count = 0;
        for (int i = 0; i < xData.length; i++) {
            if (i >= yData.length || Double.isNaN(yData[i])) continue;
            double fx = (xData[i] - xMin) / xRange;
            double fy = (yData[i] - yMin) / yRange;
            fy = Math.max(0, Math.min(1, fy));
            xPoints[count] = px + (int)(fx * plotW);
            yPoints[count] = py + (int)((1.0 - fy) * plotH);
            count++;
        }
        if (count > 1) {
            g.setColor(parseColor(color));
            g.setStroke(new BasicStroke(2.0f));
            g.drawPolyline(Arrays.copyOf(xPoints, count), Arrays.copyOf(yPoints, count), count);
        }
    }

    /** Draw legend below x-axis label. */
    private void drawLegend(Graphics2D g, int x, int y, String[] labels, String[] colors) {
        g.setFont(new Font("Arial", Font.PLAIN, 9));
        int offset = 0;
        for (int i = 0; i < labels.length; i++) {
            int lx = x + offset;
            g.setColor(parseColor(colors[i]));
            g.setStroke(new BasicStroke(2.5f));
            g.drawLine(lx, y - 3, lx + 15, y - 3);
            g.drawString(labels[i], lx + 18, y);
            FontMetrics fm = g.getFontMetrics();
            offset += 18 + fm.stringWidth(labels[i]) + 8;
        }
    }

    // ==================== Simulation engine ====================

    private double[] simulateControlDC(SpinCADBlock block,
            Map<String, Integer> controlInputs,
            String outputPinName1, String outputPinName2) {
        try {
            short[][] channels = runSimulation(block, controlInputs, outputPinName1, outputPinName2);
            if (channels == null) return null;
            int start = channels[0].length / 2;
            double dcLeft = meanOfRange(channels[0], start, channels[0].length) / 32767.0;
            double dcRight = channels.length > 1
                ? meanOfRange(channels[1], start, channels[1].length) / 32767.0
                : dcLeft;
            return new double[]{dcLeft, dcRight};
        } catch (Exception e) {
            System.err.println("  Sim error: " + e.getMessage());
            return null;
        }
    }

    private short[][] runSimulation(SpinCADBlock block,
            Map<String, Integer> controlInputs,
            String outputPinName1, String outputPinName2) throws Exception {
        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);
        model.addBlock(inputBlock);
        model.addBlock(block);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        for (Map.Entry<String, Integer> entry : controlInputs.entrySet()) {
            SpinCADPin ctrlPin = block.getPin(entry.getKey());
            if (ctrlPin != null) {
                ConstantCADBlock cb = new ConstantCADBlock(50, 50);
                cb.setConstant(entry.getValue());
                model.addBlock(cb);
                cb.generateCode(tempSfxb);
                ctrlPin.setConnection(cb, cb.getPin("Value"));
            }
        }

        for (SpinCADPin pin : block.pinList) {
            if (pin.getType() == pinType.AUDIO_IN && !pin.isConnected()) {
                SpinCADPin srcPin = inputBlock.getPin("Output 1");
                if (srcPin != null) pin.setConnection(inputBlock, srcPin);
                break;
            }
        }

        SpinCADPin outPin1 = block.getPin(outputPinName1);
        if (outPin1 == null) return null;
        outputBlock.getPin("Input 1").setConnection(block, outPin1);

        if (outputPinName2 != null) {
            SpinCADPin outPin2 = block.getPin(outputPinName2);
            if (outPin2 != null)
                outputBlock.getPin("Input 2").setConnection(block, outPin2);
        } else {
            outputBlock.getPin("Input 2").setConnection(block, outPin1);
        }

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) return null;

        String listing = renderBlock.getProgramListing(1);
        if (!listing.contains("WRAX DACL") || !listing.contains("WRAX DACR")) return null;

        File silentWav = generateSilentWav();
        File outFile = new File(tempDir, "sweep_" + System.nanoTime() + ".wav");
        SpinSimulator sim = new SpinSimulator(renderBlock,
            silentWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) return null;
        if (!outFile.exists()) return null;

        short[] stereo = readWavSamples(outFile);
        return new short[][]{extractChannel(stereo, 0), extractChannel(stereo, 1)};
    }

    // ==================== Audio utilities ====================

    private File generateSilentWav() throws IOException {
        int numFrames = (int) (SAMPLE_RATE * SIM_DURATION);
        byte[] data = new byte[numFrames * 4];
        File wavFile = File.createTempFile("ctrl_sweep_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    private static short[] extractChannel(short[] stereo, int channel) {
        short[] mono = new short[stereo.length / 2];
        for (int i = 0; i < mono.length; i++) mono[i] = stereo[i * 2 + channel];
        return mono;
    }

    private static double meanOfRange(short[] samples, int start, int end) {
        if (start >= end || start >= samples.length) return 0;
        end = Math.min(end, samples.length);
        double sum = 0;
        for (int i = start; i < end; i++) sum += samples[i];
        return sum / (end - start);
    }

    private static short[] readWavSamples(File wavFile) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) bos.write(buf, 0, n);
        ais.close();
        byte[] raw = bos.toByteArray();
        short[] samples = new short[raw.length / 2];
        for (int i = 0; i < samples.length; i++) {
            int offset = i * 2;
            samples[i] = (short) ((raw[offset] & 0xff) | ((raw[offset + 1] & 0xff) << 8));
        }
        return samples;
    }
}
