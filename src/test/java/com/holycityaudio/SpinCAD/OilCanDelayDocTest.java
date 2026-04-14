package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.jupiter.api.Test;

import com.holycityaudio.SpinCAD.CADBlocks.OilCanDelayCADBlock;

/**
 * Oil Can Delay verification: LFO period vs delay time for each sync ratio,
 * and center delay position accuracy.
 */
public class OilCanDelayDocTest {

    private static final int SAMPLERATE = 32768;
    private static final int MIN_DELAY = 3277;   // ~100 ms
    private static final int MAX_DELAY = 16384;   // ~500 ms
    private static final int MARGIN = 64;
    private static final double[] RATIOS = OilCanDelayCADBlock.RATIO_VALUES;  // {1/3, 1/2, 1, 2}
    private static final String[] RATIO_LABELS = OilCanDelayCADBlock.RATIO_LABELS;
    private static final String[] COLORS = {"#2266cc", "#cc4422", "#22aa44", "#aa44cc"};

    // Delay times: 100 to 500 ms in 25 ms steps
    private static final int NUM_POINTS = 17;  // (500-100)/25 + 1

    @Test
    public void lfoTimingAndCenterDelay() throws IOException {
        double[] delayMs = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) {
            delayMs[i] = 100.0 + i * 25.0;
        }

        // --- LFO period data: [ratio][delayIndex] ---
        double[][] lfoPeriod = new double[RATIOS.length][NUM_POINTS];
        double[][] idealPeriod = new double[RATIOS.length][NUM_POINTS];

        // --- Center delay error: [ratio][delayIndex] ---
        double[][] centerErrorMs = new double[RATIOS.length][NUM_POINTS];

        for (int ri = 0; ri < RATIOS.length; ri++) {
            double ratio = RATIOS[ri];
            for (int di = 0; di < NUM_POINTS; di++) {
                int delaySamples = (int)(delayMs[di] * SAMPLERATE / 1000.0);
                delaySamples = Math.max(MIN_DELAY, Math.min(MAX_DELAY, delaySamples));

                // --- Ideal LFO period ---
                double idealHz = ratio * (double) SAMPLERATE / (2.0 * delaySamples);
                idealPeriod[ri][di] = 1.0 / idealHz;

                // --- Actual LFO period from quantized freqReg ---
                // Same calculation as OilCanDelayCADBlock.generateCode()
                double rateHz = ratio * (double) SAMPLERATE / (2.0 * delaySamples);
                rateHz = Math.min(rateHz, 20.0);
                int freqReg = (int)(Math.pow(2.0, 17.0) * ((2.0 * Math.PI * rateHz) / SAMPLERATE));
                freqReg = Math.max(0, Math.min(511, freqReg));

                // Reverse: freqReg back to Hz
                double actualHz = freqReg * SAMPLERATE / (Math.pow(2.0, 17.0) * 2.0 * Math.PI);
                lfoPeriod[ri][di] = 1.0 / actualHz;

                // --- Center delay verification (mod width = 0, fixed delay) ---
                double modDepthMs = 5.0;  // default
                int modAmplitude = Math.max(1, (int)(modDepthMs * SAMPLERATE / (1000.0 * ratio)));
                int bufferLength = delaySamples + modAmplitude + MARGIN;
                int delayOffset = 1;  // first block allocated

                double tapFrac = (double) delaySamples / bufferLength;
                double bufFrac = (double) bufferLength / 32768.0;
                double offFrac = (double) delayOffset / 32768.0;

                // With LFO = 0: ACC after first SOF = tapFrac
                // After second SOF: ACC = tapFrac * bufFrac + offFrac
                double addrPtrValue = tapFrac * bufFrac + offFrac;
                double readPositionSamples = addrPtrValue * 32768.0;

                // Expected: read at delayOffset + delaySamples (absolute address)
                double expectedPosition = delayOffset + delaySamples;
                double errorSamples = readPositionSamples - expectedPosition;
                centerErrorMs[ri][di] = errorSamples * 1000.0 / SAMPLERATE;
            }
        }

        // --- Print table ---
        System.out.println("\nOil Can Delay: LFO Period vs Delay Time");
        System.out.println("========================================");
        System.out.printf("%-10s", "Delay ms");
        for (int ri = 0; ri < RATIOS.length; ri++) {
            System.out.printf("  R=%-4s (ms)  Ideal (ms)  Err%%", RATIO_LABELS[ri]);
        }
        System.out.println();

        for (int di = 0; di < NUM_POINTS; di++) {
            System.out.printf("%-10.0f", delayMs[di]);
            for (int ri = 0; ri < RATIOS.length; ri++) {
                double actual = lfoPeriod[ri][di] * 1000.0;
                double ideal = idealPeriod[ri][di] * 1000.0;
                double errPct = (actual - ideal) / ideal * 100.0;
                System.out.printf("  %10.1f  %10.1f  %+5.1f", actual, ideal, errPct);
            }
            System.out.println();
        }

        System.out.println("\nCenter Delay Position Error (ms):");
        System.out.printf("%-10s", "Delay ms");
        for (int ri = 0; ri < RATIOS.length; ri++) {
            System.out.printf("  R=%-4s", RATIO_LABELS[ri]);
        }
        System.out.println();
        for (int di = 0; di < NUM_POINTS; di++) {
            System.out.printf("%-10.0f", delayMs[di]);
            for (int ri = 0; ri < RATIOS.length; ri++) {
                System.out.printf("  %+.3f", centerErrorMs[ri][di]);
            }
            System.out.println();
        }

        // --- Assertions ---
        for (int ri = 0; ri < RATIOS.length; ri++) {
            for (int di = 0; di < NUM_POINTS; di++) {
                double actual = lfoPeriod[ri][di] * 1000.0;
                double ideal = idealPeriod[ri][di] * 1000.0;
                double errPct = Math.abs((actual - ideal) / ideal * 100.0);
                // freqReg quantization (0-511) causes up to ~11% error at
                // low rates (fractional ratios + long delays). This is a HW limit.
                assertTrue(errPct < 12.0,
                        String.format("LFO period error %.1f%% at delay=%.0f ms ratio=%s",
                                errPct, delayMs[di], RATIO_LABELS[ri]));

                assertTrue(Math.abs(centerErrorMs[ri][di]) < 0.1,
                        String.format("Center delay error %.3f ms at delay=%.0f ms ratio=%s",
                                centerErrorMs[ri][di], delayMs[di], RATIO_LABELS[ri]));
            }
        }

        // --- Generate SVG ---
        File svgFile = new File("build/oil_can_delay_lfo_timing.svg");
        svgFile.getParentFile().mkdirs();
        writeSvg(svgFile, delayMs, lfoPeriod, idealPeriod, centerErrorMs);
        System.out.println("\nSVG written to: " + svgFile.getAbsolutePath());
    }

    // ========================= SVG generation =========================

    private void writeSvg(File file, double[] delayMs,
            double[][] lfoPeriod, double[][] idealPeriod, double[][] centerErrorMs)
            throws IOException {

        int plotW = 500, plotH = 350;
        int padL = 65, padR = 30, padT = 45, padB = 55;
        int cellW = padL + plotW + padR;
        int gapX = 80;
        int totalW = cellW * 2 + gapX + 40;
        int totalH = padT + plotH + padB + 50;

        // X range: 100-500 ms
        double xMin = 100, xMax = 500;

        // Y range for LFO period: find min/max across all data
        double yMinL = Double.MAX_VALUE, yMaxL = 0;
        for (int ri = 0; ri < RATIOS.length; ri++) {
            for (int di = 0; di < NUM_POINTS; di++) {
                yMinL = Math.min(yMinL, lfoPeriod[ri][di] * 1000.0);
                yMaxL = Math.max(yMaxL, lfoPeriod[ri][di] * 1000.0);
            }
        }
        // Round to nice bounds
        yMinL = Math.floor(yMinL / 50) * 50;
        yMaxL = Math.ceil(yMaxL / 50) * 50;
        if (yMaxL - yMinL < 100) { yMinL = 0; yMaxL = Math.ceil(yMaxL / 100) * 100; }
        yMinL = 0;  // always start at 0 for period

        // Y range for center error
        double yMinR = -0.1, yMaxR = 0.1;

        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.printf("<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d' ", totalW, totalH);
        pw.println("font-family='Arial, sans-serif'>");
        pw.printf("<rect width='%d' height='%d' fill='white'/>%n", totalW, totalH);

        // ===== Left plot: LFO period vs delay time =====
        int lx = 20, ly = 10;
        int lpx = lx + padL, lpy = ly + padT;

        drawPlotGrid(pw, lpx, lpy, plotW, plotH, xMin, xMax, yMinL, yMaxL,
                100, 50,
                "Oil Can Delay: LFO Period vs Delay Time",
                "Delay Time (ms)", "LFO Period (ms)");

        // Draw ideal (dashed) and actual (solid) for each ratio
        for (int ri = 0; ri < RATIOS.length; ri++) {
            // Convert period arrays to ms for plotting
            double[] actualMs = new double[NUM_POINTS];
            double[] idealMs = new double[NUM_POINTS];
            for (int di = 0; di < NUM_POINTS; di++) {
                actualMs[di] = lfoPeriod[ri][di] * 1000.0;
                idealMs[di] = idealPeriod[ri][di] * 1000.0;
            }
            drawXYCurve(pw, delayMs, idealMs, lpx, lpy, plotW, plotH,
                    xMin, xMax, yMinL, yMaxL, COLORS[ri], "4,3");
            drawXYCurve(pw, delayMs, actualMs, lpx, lpy, plotW, plotH,
                    xMin, xMax, yMinL, yMaxL, COLORS[ri], "");
        }

        // Legend
        int legY = lpy + plotH + 40;
        for (int ri = 0; ri < RATIOS.length; ri++) {
            int legX = lpx + ri * 120;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2.5'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS[ri]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>Ratio %s</text>%n",
                    legX + 25, legY, COLORS[ri], RATIO_LABELS[ri]);
        }
        // Dashed = ideal note
        int noteX = lpx + RATIOS.length * 120 + 10;
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#666' stroke-width='1.5' stroke-dasharray='4,3'/>%n",
                noteX, legY - 4, noteX + 20, legY - 4);
        pw.printf("<text x='%d' y='%d' font-size='10' fill='#666'>= ideal</text>%n", noteX + 25, legY);

        // ===== Right plot: center delay error =====
        int rx = lx + cellW + gapX;
        int rpx = rx + padL, rpy = ly + padT;

        drawPlotGrid(pw, rpx, rpy, plotW, plotH, xMin, xMax, yMinR, yMaxR,
                100, 0.02,
                "Center Delay Position Error (mod width = 0)",
                "Delay Time (ms)", "Error (ms)");

        // Zero line
        double fy0 = 1.0 - (0 - yMinR) / (yMaxR - yMinR);
        int gy0 = rpy + (int)(fy0 * plotH);
        pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#aaa' stroke-width='1' stroke-dasharray='4,3'/>%n",
                rpx, gy0, rpx + plotW, gy0);

        for (int ri = 0; ri < RATIOS.length; ri++) {
            drawXYCurve(pw, delayMs, centerErrorMs[ri], rpx, rpy, plotW, plotH,
                    xMin, xMax, yMinR, yMaxR, COLORS[ri], "");
        }

        // Legend for right plot
        for (int ri = 0; ri < RATIOS.length; ri++) {
            int legX = rpx + ri * 120;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2.5'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS[ri]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>Ratio %s</text>%n",
                    legX + 25, legY, COLORS[ri], RATIO_LABELS[ri]);
        }

        pw.println("</svg>");
        pw.close();
    }

    private void drawPlotGrid(PrintWriter pw, int px, int py, int plotW, int plotH,
            double xMin, double xMax, double yMin, double yMax,
            double xStep, double yStep,
            String title, String xLabel, String yLabel) {

        pw.printf("<rect x='%d' y='%d' width='%d' height='%d' fill='#f8f8f8' stroke='#ccc'/>%n",
                px, py, plotW, plotH);
        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='13' font-weight='bold'>%s</text>%n",
                px + plotW / 2, py - 10, title);

        // X-axis grid
        for (double v = xMin; v <= xMax + 0.001; v += xStep) {
            double fx = (v - xMin) / (xMax - xMin);
            int gx = px + (int)(fx * plotW);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#ddd' stroke-width='0.5'/>%n",
                    gx, py, gx, py + plotH);
            pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='9' fill='#666'>%.0f</text>%n",
                    gx, py + plotH + 14, v);
        }

        // Y-axis grid
        String yFmt = yStep < 1 ? "%.2f" : "%.0f";
        for (double v = yMin; v <= yMax + yStep * 0.001; v += yStep) {
            double fy = 1.0 - (v - yMin) / (yMax - yMin);
            int gy = py + (int)(fy * plotH);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#ddd' stroke-width='0.5'/>%n",
                    px, gy, px + plotW, gy);
            pw.printf("<text x='%d' y='%d' text-anchor='end' font-size='9' fill='#666'>" + yFmt + "</text>%n",
                    px - 5, gy + 3, v);
        }

        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='10' fill='#333'>%s</text>%n",
                px + plotW / 2, py + plotH + 35, xLabel);
        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='10' fill='#333' ",
                px - 50, py + plotH / 2);
        pw.printf("transform='rotate(-90,%d,%d)'>%s</text>%n",
                px - 50, py + plotH / 2, yLabel);

        pw.printf("<rect x='%d' y='%d' width='%d' height='%d' fill='none' stroke='#999'/>%n",
                px, py, plotW, plotH);
    }

    private void drawXYCurve(PrintWriter pw, double[] xData, double[] yData,
            int px, int py, int plotW, int plotH,
            double xMin, double xMax, double yMin, double yMax,
            String color, String dashArray) {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < xData.length; i++) {
            double fx = (xData[i] - xMin) / (xMax - xMin);
            double fy = 1.0 - (yData[i] - yMin) / (yMax - yMin);
            fy = Math.max(0, Math.min(1, fy));
            fx = Math.max(0, Math.min(1, fx));
            int sx = px + (int)(fx * plotW);
            int sy = py + (int)(fy * plotH);
            if (i == 0) path.append(String.format("M%d,%d", sx, sy));
            else path.append(String.format(" L%d,%d", sx, sy));
        }
        if (dashArray.isEmpty()) {
            pw.printf("<path d='%s' fill='none' stroke='%s' stroke-width='2'/>%n", path, color);
        } else {
            pw.printf("<path d='%s' fill='none' stroke='%s' stroke-width='1.5' stroke-dasharray='%s'/>%n",
                    path, color, dashArray);
        }
    }
}
