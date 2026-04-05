package com.holycityaudio.SpinCAD;

import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Generates crossfade curve plots for documentation.
 * These are pure math plots (no simulation needed).
 */
public class CrossfadeDocTest {

    private static final int NUM_POINTS = 200;
    private static final int PLOT_W = 360, PLOT_H = 280;
    private static final int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 85;

    @Test
    void generateCrossfadeCurvePlots() throws Exception {
        double[] ctrl = new double[NUM_POINTS];
        for (int i = 0; i < NUM_POINTS; i++) ctrl[i] = (double) i / (NUM_POINTS - 1);

        double[][] midpoints = {
            {0.25, 0.25},
            {0.5, 0.5},
            {0.707, 0.707},
            {1.0, 1.0}
        };
        String[] names = {"0.25", "0.50 (linear)", "0.707 (equal-power)", "1.00 (max overlap)"};
        String[] fileSuffixes = {"025", "050", "0707", "100"};

        for (int mi = 0; mi < midpoints.length; mi++) {
            double m = midpoints[mi][0];
            double[] gain1Curve = new double[NUM_POINTS];
            double[] gain2Curve = new double[NUM_POINTS];

            for (int i = 0; i < NUM_POINTS; i++) {
                double c = ctrl[i];
                if (Math.abs(m - 0.5) < 0.001) {
                    gain1Curve[i] = 1.0 - c;
                    gain2Curve[i] = c;
                } else if (c < 0.5) {
                    gain2Curve[i] = c * 2.0 * m;
                    gain1Curve[i] = 1.0 - c * 2.0 * (1.0 - m);
                } else {
                    gain2Curve[i] = c * 2.0 * (1.0 - m) + (2.0 * m - 1.0);
                    gain1Curve[i] = 2.0 * m * (1.0 - c);
                }
            }

            String title = "Crossfade Adj (midpoint=" + names[mi] + ")";
            double yMin = 0, yMax = 1.2;

            int totalW = PAD_L + PLOT_W + PAD_R;
            int totalH = PAD_T + PLOT_H + PAD_B;
            BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = PlotUtils.createGraphics(img, totalW, totalH);
            int px = PAD_L, py = PAD_T;

            // Draw plot frame with 6 Y ticks (0, 0.2, 0.4, 0.6, 0.8, 1.0, 1.2)
            PlotUtils.drawPlot(g, px, py, PLOT_W, PLOT_H, title,
                "Control (0=In1, 1=In2)", "Gain",
                0, 1, yMin, yMax, 5, 6);

            // Darker gridline at gain = 1.0
            double frac10 = (1.0 - yMin) / (yMax - yMin);
            int gy10 = py + (int)((1.0 - frac10) * PLOT_H);
            g.setColor(new Color(0x99, 0x99, 0x99));
            g.setStroke(new BasicStroke(1.0f));
            g.drawLine(px, gy10, px + PLOT_W, gy10);

            // Draw curves
            String[] colors = { PlotUtils.COLORS[0], PlotUtils.COLORS[2] };
            PlotUtils.drawCurve(g, ctrl, gain1Curve, px, py, PLOT_W, PLOT_H, 0, 1, yMin, yMax, colors[0]);
            PlotUtils.drawCurve(g, ctrl, gain2Curve, px, py, PLOT_W, PLOT_H, 0, 1, yMin, yMax, colors[1]);

            // Legend
            PlotUtils.drawLegend(g, px, py + PLOT_H + 52, new String[] { "Input 1", "Input 2" }, colors);

            g.dispose();
            File outFile = new File("docs/images/mix-crossfadeadj-" + fileSuffixes[mi] + ".png");
            ImageIO.write(img, "png", outFile);
            System.out.println("Wrote " + outFile.getPath());
        }
    }
}
