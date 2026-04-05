package com.holycityaudio.SpinCAD;

import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * Generates crossfade curve plots for documentation.
 * These are pure math plots (no simulation needed).
 */
public class CrossfadeDocTest {

    private static final int NUM_POINTS = 200;

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

            File outFile = new File("docs/images/mix-crossfadeadj-" + fileSuffixes[mi] + ".png");
            PlotUtils.writePlot(outFile,
                "Crossfade Adj (midpoint=" + names[mi] + ")",
                "Control (0=In1, 1=In2)", "Gain",
                0, 1, 0, 1.1,
                ctrl,
                new double[][] { gain1Curve, gain2Curve },
                new String[] { "Input 1", "Input 2" },
                new String[] { PlotUtils.COLORS[0], PlotUtils.COLORS[2] },
                5);

            System.out.println("Wrote " + outFile.getPath());
        }
    }
}
