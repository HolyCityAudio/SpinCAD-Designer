package com.holycityaudio.SpinCAD;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.ResonatorCADBlock;

/**
 * Frequency response plots for Resonator block tuned to 440 Hz with Q=20 and Q=50.
 * Produces two PNGs in build/ showing magnitude response across 80-2000 Hz.
 */
public class ResonatorFrequencyResponseTest {

    @TempDir
    File tempDir;

    private static final int SAMPLE_RATE = ElmProgram.SAMPLERATE;
    private static final double FC = 440.0;
    // At resonance gain ≈ Q. For Q=50, input 0.005 -> peak output ~0.25 (safe).
    private static final double SINE_AMPLITUDE = 0.005;
    private static final double SINE_DURATION = 1.2;
    private static final long SIM_TIMEOUT = 60000;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void plotResonator_Q20() throws Exception {
        runSweep(20.0, new File("build/resonator_440Hz_Q20.png"),
            "Resonator fc=440 Hz  Q=20");
    }

    @Test
    void plotResonator_Q50() throws Exception {
        runSweep(50.0, new File("build/resonator_440Hz_Q50.png"),
            "Resonator fc=440 Hz  Q=50");
    }

    @Test
    void plotResonator_QOverlay() throws Exception {
        double[] qValues = {10.0, 20.0, 50.0};
        String[] colors = {"#1f9e3a", "#d97706", "#2563eb"}; // green, orange, blue
        double[] freqs = logSweep(80.0, 2000.0, 50);
        double[][] curves = new double[qValues.length][];
        double[] peakGains = new double[qValues.length];
        double[] peakFreqs = new double[qValues.length];

        for (int k = 0; k < qValues.length; k++) {
            double q = qValues[k];
            double[] gains = new double[freqs.length];
            System.out.printf("%n=== Q=%.0f sweep ===%n", q);
            for (int i = 0; i < freqs.length; i++) {
                gains[i] = measureGain(freqs[i], q);
                System.out.printf("  f=%7.1f Hz  gain=%+6.1f dB%n", freqs[i], gains[i]);
            }
            curves[k] = gains;
            int pi = 0;
            for (int i = 1; i < gains.length; i++) {
                if (!Double.isNaN(gains[i]) && (Double.isNaN(gains[pi]) || gains[i] > gains[pi])) pi = i;
            }
            peakGains[k] = gains[pi];
            peakFreqs[k] = freqs[pi];
        }

        File outFile = new File("build/resonator_440Hz_Q_overlay.png");
        writeOverlayPlot(outFile,
            "Resonator fc = 440 Hz — Q overlay (input -46 dBFS)",
            freqs, curves, qValues, peakGains, peakFreqs, colors);
        System.out.println("Wrote " + outFile.getAbsolutePath());
    }

    private void runSweep(double q, File outFile, String title) throws Exception {
        double[] freqs = logSweep(80.0, 2000.0, 50);
        double[] gainsDb = new double[freqs.length];
        System.out.printf("%n=== %s ===%n", title);
        for (int i = 0; i < freqs.length; i++) {
            gainsDb[i] = measureGain(freqs[i], q);
            System.out.printf("  f=%7.1f Hz  gain=%+6.1f dB%n", freqs[i], gainsDb[i]);
        }
        writeLogPlot(outFile, title, freqs, gainsDb);
        System.out.println("Wrote " + outFile.getAbsolutePath());
    }

    private double[] logSweep(double f0, double f1, int n) {
        double[] out = new double[n];
        double lo = Math.log10(f0), hi = Math.log10(f1);
        for (int i = 0; i < n; i++) {
            out[i] = Math.pow(10, lo + (hi - lo) * i / (n - 1));
        }
        return out;
    }

    private double measureGain(double sineFreq, double q) throws Exception {
        ResonatorCADBlock block = new ResonatorCADBlock(100, 100);
        block.setfreq(SpinCADBlock.freqToFiltSVF(FC));
        block.setreso(1.0 / q);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inBlk = new InputCADBlock(0, 0);
        OutputCADBlock outBlk = new OutputCADBlock(200, 100);
        model.addBlock(inBlk);
        model.addBlock(block);
        model.addBlock(outBlk);

        SpinFXBlock setup = new SpinFXBlock("Setup");
        inBlk.generateCode(setup);

        block.getPin("Input").setConnection(inBlk, inBlk.getPin("Output 1"));
        SpinCADPin outPin = block.getPin("Output");
        outBlk.getPin("Input 1").setConnection(block, outPin);
        outBlk.getPin("Input 2").setConnection(block, outPin);

        model.sortAlignGen();
        SpinFXBlock render = model.getRenderBlock();
        if (render == null) return Double.NaN;

        File sineWav = generateSineWav(sineFreq, SINE_AMPLITUDE);
        File outWav = new File(tempDir,
            "reso_" + (int) sineFreq + "_" + System.nanoTime() + ".wav");
        SpinSimulator sim = new SpinSimulator(render,
            sineWav.getAbsolutePath(), outWav.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);
        if (sim.isAlive() || sim.getSimulationException() != null) return Double.NaN;
        if (!outWav.exists()) return Double.NaN;

        short[] stereo = readWavSamples(outWav);
        short[] left = new short[stereo.length / 2];
        for (int i = 0; i < left.length; i++) left[i] = stereo[i * 2];

        // Measure steady-state RMS over last third, after resonator settles
        int start = (int) (left.length * 2.0 / 3.0);
        double outRms = rmsOfRange(left, start, left.length);
        double inRms = SINE_AMPLITUDE * 32767.0 / Math.sqrt(2.0);
        if (outRms < 1.0) return -80.0;
        return 20.0 * Math.log10(outRms / inRms);
    }

    private File generateSineWav(double freqHz, double amp) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * SINE_DURATION);
        byte[] data = new byte[numFrames * 4];
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / SAMPLE_RATE;
            short s = (short) (amp * 32767.0 * Math.sin(2 * Math.PI * freqHz * t));
            int off = i * 4;
            data[off] = (byte) (s & 0xff);
            data[off + 1] = (byte) ((s >> 8) & 0xff);
            data[off + 2] = (byte) (s & 0xff);
            data[off + 3] = (byte) ((s >> 8) & 0xff);
        }
        File f = File.createTempFile("reso_sine_" + (int) freqHz + "_", ".wav");
        f.deleteOnExit();
        AudioFormat fmt = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, fmt, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, f);
        ais.close();
        return f;
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
            int off = i * 2;
            samples[i] = (short) ((raw[off] & 0xff) | ((raw[off + 1] & 0xff) << 8));
        }
        return samples;
    }

    private static double rmsOfRange(short[] samples, int start, int end) {
        if (start >= end || start >= samples.length) return 0;
        end = Math.min(end, samples.length);
        double sumSq = 0;
        for (int i = start; i < end; i++) sumSq += (double) samples[i] * samples[i];
        return Math.sqrt(sumSq / (end - start));
    }

    private void writeLogPlot(File outFile, String title,
                              double[] freqs, double[] gainsDb) throws IOException {
        int W = 900, H = 520;
        int padL = 80, padR = 40, padT = 55, padB = 75;
        int plotW = W - padL - padR, plotH = H - padT - padB;

        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, W / 2 - fm.stringWidth(title) / 2, 28);

        g.setColor(new Color(0xfa, 0xfa, 0xfa));
        g.fillRect(padL, padT, plotW, plotH);

        double xMinLog = Math.log10(80.0);
        double xMaxLog = Math.log10(2000.0);
        double yMin = -40.0, yMax = 40.0;

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));

        // Minor gridlines
        double[] minor = {90, 110, 150, 250, 300, 350, 500, 600, 700, 800, 900, 1100, 1250, 1500, 1750};
        g.setColor(new Color(0xee, 0xee, 0xee));
        for (double f : minor) {
            int gx = padL + (int) (plotW * (Math.log10(f) - xMinLog) / (xMaxLog - xMinLog));
            g.drawLine(gx, padT, gx, padT + plotH);
        }

        // Major ticks: 100, 200, 440, 1000, 2000
        double[] major = {100, 200, 440, 1000, 2000};
        for (double f : major) {
            int gx = padL + (int) (plotW * (Math.log10(f) - xMinLog) / (xMaxLog - xMinLog));
            if (f == 440) {
                g.setColor(new Color(0xff, 0x66, 0x66));
                g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10, new float[]{4, 3}, 0));
                g.drawLine(gx, padT, gx, padT + plotH);
                g.setStroke(new BasicStroke(1.0f));
            } else {
                g.setColor(new Color(0xcc, 0xcc, 0xcc));
                g.drawLine(gx, padT, gx, padT + plotH);
            }
            g.setColor(f == 440 ? new Color(0xcc, 0x22, 0x22) : Color.DARK_GRAY);
            String lbl = String.format("%.0f", f);
            fm = g.getFontMetrics();
            g.drawString(lbl, gx - fm.stringWidth(lbl) / 2, padT + plotH + 16);
        }

        // Y gridlines every 10 dB
        for (int y = (int) yMin; y <= yMax; y += 10) {
            int gy = padT + (int) (plotH * (1.0 - (y - yMin) / (yMax - yMin)));
            g.setColor(y == 0 ? new Color(0xaa, 0xaa, 0xaa) : new Color(0xee, 0xee, 0xee));
            g.drawLine(padL, gy, padL + plotW, gy);
            g.setColor(Color.DARK_GRAY);
            String lbl = y + " dB";
            fm = g.getFontMetrics();
            g.drawString(lbl, padL - 6 - fm.stringWidth(lbl), gy + 4);
        }

        // Axis labels
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String xLbl = "Frequency (Hz) — log scale";
        fm = g.getFontMetrics();
        g.drawString(xLbl, padL + plotW / 2 - fm.stringWidth(xLbl) / 2, padT + plotH + 40);

        // Frame
        g.setColor(new Color(0x99, 0x99, 0x99));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRect(padL, padT, plotW, plotH);

        // Curve
        g.setColor(new Color(0x22, 0x66, 0xcc));
        g.setStroke(new BasicStroke(2.5f));
        int[] xp = new int[freqs.length];
        int[] yp = new int[freqs.length];
        int count = 0;
        for (int i = 0; i < freqs.length; i++) {
            if (Double.isNaN(gainsDb[i])) continue;
            double fx = (Math.log10(freqs[i]) - xMinLog) / (xMaxLog - xMinLog);
            double fy = (gainsDb[i] - yMin) / (yMax - yMin);
            fy = Math.max(0, Math.min(1, fy));
            xp[count] = padL + (int) (fx * plotW);
            yp[count] = padT + (int) ((1.0 - fy) * plotH);
            count++;
        }
        if (count > 1) g.drawPolyline(xp, yp, count);

        // Peak annotation
        int peakIdx = -1;
        for (int i = 0; i < gainsDb.length; i++) {
            if (Double.isNaN(gainsDb[i])) continue;
            if (peakIdx < 0 || gainsDb[i] > gainsDb[peakIdx]) peakIdx = i;
        }
        if (peakIdx >= 0) {
            String peakTxt = String.format("Peak: %+.1f dB at %.0f Hz",
                gainsDb[peakIdx], freqs[peakIdx]);
            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(peakTxt, padL + 8, padT + 18);

            // Dot on peak
            int px = padL + (int) (plotW * (Math.log10(freqs[peakIdx]) - xMinLog) / (xMaxLog - xMinLog));
            int py = padT + (int) (plotH * (1.0 - (gainsDb[peakIdx] - yMin) / (yMax - yMin)));
            g.setColor(new Color(0xcc, 0x22, 0x22));
            g.fillOval(px - 4, py - 4, 8, 8);
        }

        g.dispose();
        File parent = outFile.getParentFile();
        if (parent != null) parent.mkdirs();
        ImageIO.write(img, "png", outFile);
    }

    private void writeOverlayPlot(File outFile, String title,
                                  double[] freqs, double[][] curves,
                                  double[] qValues, double[] peakGains, double[] peakFreqs,
                                  String[] hexColors) throws IOException {
        int W = 900, H = 560;
        int padL = 80, padR = 40, padT = 55, padB = 95;
        int plotW = W - padL - padR, plotH = H - padT - padB;

        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, W / 2 - fm.stringWidth(title) / 2, 28);

        g.setColor(new Color(0xfa, 0xfa, 0xfa));
        g.fillRect(padL, padT, plotW, plotH);

        double xMinLog = Math.log10(80.0);
        double xMaxLog = Math.log10(2000.0);
        double yMin = -40.0, yMax = 40.0;

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        double[] minor = {90, 110, 150, 250, 300, 350, 500, 600, 700, 800, 900, 1100, 1250, 1500, 1750};
        g.setColor(new Color(0xee, 0xee, 0xee));
        for (double f : minor) {
            int gx = padL + (int) (plotW * (Math.log10(f) - xMinLog) / (xMaxLog - xMinLog));
            g.drawLine(gx, padT, gx, padT + plotH);
        }

        double[] major = {100, 200, 440, 1000, 2000};
        for (double f : major) {
            int gx = padL + (int) (plotW * (Math.log10(f) - xMinLog) / (xMaxLog - xMinLog));
            if (f == 440) {
                g.setColor(new Color(0xff, 0x88, 0x88));
                g.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                    10, new float[]{4, 3}, 0));
                g.drawLine(gx, padT, gx, padT + plotH);
                g.setStroke(new BasicStroke(1.0f));
            } else {
                g.setColor(new Color(0xcc, 0xcc, 0xcc));
                g.drawLine(gx, padT, gx, padT + plotH);
            }
            g.setColor(f == 440 ? new Color(0xcc, 0x22, 0x22) : Color.DARK_GRAY);
            String lbl = String.format("%.0f", f);
            fm = g.getFontMetrics();
            g.drawString(lbl, gx - fm.stringWidth(lbl) / 2, padT + plotH + 16);
        }

        for (int y = (int) yMin; y <= yMax; y += 10) {
            int gy = padT + (int) (plotH * (1.0 - (y - yMin) / (yMax - yMin)));
            g.setColor(y == 0 ? new Color(0xaa, 0xaa, 0xaa) : new Color(0xee, 0xee, 0xee));
            g.drawLine(padL, gy, padL + plotW, gy);
            g.setColor(Color.DARK_GRAY);
            String lbl = y + " dB";
            fm = g.getFontMetrics();
            g.drawString(lbl, padL - 6 - fm.stringWidth(lbl), gy + 4);
        }

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        String xLbl = "Frequency (Hz) — log scale";
        fm = g.getFontMetrics();
        g.drawString(xLbl, padL + plotW / 2 - fm.stringWidth(xLbl) / 2, padT + plotH + 40);

        g.setColor(new Color(0x99, 0x99, 0x99));
        g.setStroke(new BasicStroke(1.0f));
        g.drawRect(padL, padT, plotW, plotH);

        // Draw each curve
        for (int k = 0; k < curves.length; k++) {
            double[] gains = curves[k];
            Color c = Color.decode(hexColors[k]);
            g.setColor(c);
            g.setStroke(new BasicStroke(2.5f));
            int[] xp = new int[gains.length];
            int[] yp = new int[gains.length];
            int count = 0;
            for (int i = 0; i < gains.length; i++) {
                if (Double.isNaN(gains[i])) continue;
                double fx = (Math.log10(freqs[i]) - xMinLog) / (xMaxLog - xMinLog);
                double fy = (gains[i] - yMin) / (yMax - yMin);
                fy = Math.max(0, Math.min(1, fy));
                xp[count] = padL + (int) (fx * plotW);
                yp[count] = padT + (int) ((1.0 - fy) * plotH);
                count++;
            }
            if (count > 1) g.drawPolyline(xp, yp, count);

            // Dot on peak
            double pf = peakFreqs[k], pg = peakGains[k];
            int px = padL + (int) (plotW * (Math.log10(pf) - xMinLog) / (xMaxLog - xMinLog));
            double pyd = padT + plotH * (1.0 - (pg - yMin) / (yMax - yMin));
            pyd = Math.max(padT, Math.min(padT + plotH, pyd));
            int py = (int) pyd;
            g.fillOval(px - 4, py - 4, 8, 8);
        }

        // Legend row below x-axis
        int legendY = padT + plotH + 72;
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        String[] entries = new String[qValues.length];
        int totalW = 0;
        for (int k = 0; k < qValues.length; k++) {
            entries[k] = String.format("Q = %.0f   peak %+.1f dB @ %.0f Hz",
                qValues[k], peakGains[k], peakFreqs[k]);
            totalW += 22 + fm.stringWidth(entries[k]) + 24;
        }
        int lx = padL + (plotW - totalW) / 2;
        if (lx < padL) lx = padL;
        for (int k = 0; k < qValues.length; k++) {
            Color c = Color.decode(hexColors[k]);
            g.setColor(c);
            g.setStroke(new BasicStroke(3.0f));
            g.drawLine(lx, legendY - 4, lx + 18, legendY - 4);
            g.fillOval(lx + 6, legendY - 8, 8, 8);
            g.setColor(Color.BLACK);
            g.drawString(entries[k], lx + 22, legendY);
            lx += 22 + fm.stringWidth(entries[k]) + 24;
        }

        g.dispose();
        File parent = outFile.getParentFile();
        if (parent != null) parent.mkdirs();
        ImageIO.write(img, "png", outFile);
    }
}
