package com.holycityaudio.SpinCAD;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;

import com.holycityaudio.SpinCAD.CADBlocks.*;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Shared plotting and simulation utilities for block documentation tests.
 */
public class PlotUtils {

    public static final int SAMPLE_RATE = ElmProgram.SAMPLERATE;
    public static final long SIM_TIMEOUT = 30000;

    public static final String[] COLORS = {
        "#2266cc", "#cc4422", "#22aa44", "#aa44cc", "#cc8800", "#8844cc", "#cc2288"
    };

    private static final int PLOT_W = 360, PLOT_H = 280;
    private static final int PAD_L = 50, PAD_R = 20, PAD_T = 35, PAD_B = 85;

    // ==================== PNG rendering ====================

    public static Color parseColor(String hex) {
        return Color.decode(hex);
    }

    public static Graphics2D createGraphics(BufferedImage img, int w, int h) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        return g;
    }

    /** Write a single-plot PNG with overlaid curves. */
    public static void writePlot(File file, String title, String xLabel, String yLabel,
            double xMin, double xMax, double yMin, double yMax,
            double[] xData, double[][] curves, String[] labels, String[] colors) throws IOException {
        int totalW = PAD_L + PLOT_W + PAD_R;
        int totalH = PAD_T + PLOT_H + PAD_B;
        BufferedImage img = new BufferedImage(totalW, totalH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = createGraphics(img, totalW, totalH);
        int px = PAD_L, py = PAD_T;
        drawPlot(g, px, py, PLOT_W, PLOT_H, title, xLabel, yLabel, xMin, xMax, yMin, yMax);
        for (int ci = 0; ci < curves.length; ci++) {
            drawCurve(g, xData, curves[ci], px, py, PLOT_W, PLOT_H, xMin, xMax, yMin, yMax, colors[ci]);
        }
        drawLegend(g, px, py + PLOT_H + 52, labels, colors);
        g.dispose();
        ImageIO.write(img, "png", file);
    }

    /** Write a plot with default 0-1 x-axis using SWEEP_X style data. */
    public static void writePlot01(File file, String title, String xLabel, String yLabel,
            double yMin, double yMax,
            double[] xData, double[][] curves, String[] labels, String[] colors) throws IOException {
        writePlot(file, title, xLabel, yLabel, 0, 1, yMin, yMax, xData, curves, labels, colors);
    }

    /** Draw plot frame with grid, labels. */
    public static void drawPlot(Graphics2D g, int px, int py, int plotW, int plotH,
            String title, String xLabel, String yLabel,
            double xMin, double xMax, double yMin, double yMax) {
        g.setColor(new Color(0xf8, 0xf8, 0xf8));
        g.fillRect(px, py, plotW, plotH);
        g.setColor(new Color(0xcc, 0xcc, 0xcc));
        g.drawRect(px, py, plotW, plotH);

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(title, px + plotW / 2 - fm.stringWidth(title) / 2, py - 10);

        double yRange = yMax - yMin;
        int numTicks = 5;

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

        if (Math.abs(yMin - xMin) < 0.01 && Math.abs(yMax - xMax) < 0.01) {
            Stroke old = g.getStroke();
            g.setStroke(new BasicStroke(0.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{4, 3}, 0));
            g.setColor(new Color(0xbb, 0xbb, 0xbb));
            g.drawLine(px, py + plotH, px + plotW, py);
            g.setStroke(old);
        }

        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.setColor(new Color(0x33, 0x33, 0x33));
        fm = g.getFontMetrics();
        g.drawString(xLabel, px + plotW / 2 - fm.stringWidth(xLabel) / 2, py + plotH + 30);

        AffineTransform origTransform = g.getTransform();
        g.rotate(-Math.PI / 2, px - 35, py + plotH / 2);
        fm = g.getFontMetrics();
        g.drawString(yLabel, px - 35 - fm.stringWidth(yLabel) / 2, py + plotH / 2 + 4);
        g.setTransform(origTransform);

        g.setColor(new Color(0x99, 0x99, 0x99));
        g.drawRect(px, py, plotW, plotH);
    }

    /** Draw a curve from x/y data arrays. */
    public static void drawCurve(Graphics2D g, double[] xData, double[] yData,
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
    public static void drawLegend(Graphics2D g, int x, int y, String[] labels, String[] colors) {
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

    // ==================== WAV generation ====================

    /** Generate a stereo sine wave WAV at given frequency and amplitude. */
    public static File generateSineWav(double durationSeconds, double freqHz, double amplitude) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        for (int i = 0; i < numFrames; i++) {
            short sample = (short) (amplitude * Short.MAX_VALUE * Math.sin(2 * Math.PI * freqHz * i / SAMPLE_RATE));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }
        return writeWav(data, numFrames, "sine");
    }

    /** Generate a stereo impulse WAV (single sample at given amplitude). */
    public static File generateImpulseWav(double durationSeconds, double amplitude) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        short impulse = (short) (amplitude * Short.MAX_VALUE);
        data[0] = (byte) (impulse & 0xff);
        data[1] = (byte) ((impulse >> 8) & 0xff);
        data[2] = (byte) (impulse & 0xff);
        data[3] = (byte) ((impulse >> 8) & 0xff);
        return writeWav(data, numFrames, "impulse");
    }

    /** Generate a stereo silent WAV. */
    public static File generateSilentWav(double durationSeconds) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        return writeWav(data, numFrames, "silent");
    }

    /** Generate a stereo white noise WAV. */
    public static File generateNoiseWav(double durationSeconds, double amplitude) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        Random rng = new Random(42);
        for (int i = 0; i < numFrames; i++) {
            short sample = (short) (rng.nextGaussian() * Short.MAX_VALUE * amplitude);
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }
        return writeWav(data, numFrames, "noise");
    }

    /** Generate a log-swept sine (chirp) WAV from f0 to f1 Hz. */
    public static File generateChirpWav(double durationSeconds, double f0, double f1, double amplitude) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        double T = durationSeconds;
        double k = Math.pow(f1 / f0, 1.0 / T);
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / SAMPLE_RATE;
            double phase = 2 * Math.PI * f0 * (Math.pow(k, t) - 1) / Math.log(k);
            short sample = (short) (amplitude * Short.MAX_VALUE * Math.sin(phase));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }
        return writeWav(data, numFrames, "chirp");
    }

    private static File writeWav(byte[] data, int numFrames, String prefix) throws IOException {
        File wavFile = File.createTempFile("spincad_" + prefix + "_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    // ==================== WAV reading ====================

    public static short[] readWavSamples(File wavFile) throws Exception {
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

    public static short[] extractChannel(short[] stereo, int channel) {
        short[] mono = new short[stereo.length / 2];
        for (int i = 0; i < mono.length; i++) mono[i] = stereo[i * 2 + channel];
        return mono;
    }

    public static double[] toDouble(short[] samples) {
        double[] d = new double[samples.length];
        for (int i = 0; i < samples.length; i++) d[i] = samples[i] / 32768.0;
        return d;
    }

    // ==================== Simulation ====================

    /**
     * Build a model with Input -> block -> Output, wire all audio inputs,
     * optionally wire control inputs, simulate and return stereo samples.
     *
     * @param block the block under test
     * @param inputWav input WAV file (stereo)
     * @param controlInputs map of pin name -> constant value (0-999), or null
     * @param outputPinName1 first output pin name (connected to DAC L)
     * @param outputPinName2 second output pin name (connected to DAC R), or null for mono
     * @param tempDir directory for temporary output files
     * @return interleaved stereo samples, or null on failure
     */
    public static short[] simulate(SpinCADBlock block, File inputWav,
            Map<String, Integer> controlInputs,
            String outputPinName1, String outputPinName2,
            File tempDir) throws Exception {
        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);
        model.addBlock(inputBlock);
        model.addBlock(block);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        // Wire control inputs
        if (controlInputs != null) {
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
        }

        // Wire audio inputs
        int audioIdx = 0;
        for (SpinCADPin pin : block.pinList) {
            if (pin.getType() == pinType.AUDIO_IN && !pin.isConnected()) {
                String outPinName = (audioIdx == 0) ? "Output 1" : "Output 2";
                SpinCADPin srcPin = inputBlock.getPin(outPinName);
                if (srcPin != null) pin.setConnection(inputBlock, srcPin);
                audioIdx++;
            }
        }

        // Wire output
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

        File outFile = new File(tempDir, "sim_" + System.nanoTime() + ".wav");
        SpinSimulator sim = new SpinSimulator(renderBlock,
            inputWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) return null;
        if (!outFile.exists()) return null;

        return readWavSamples(outFile);
    }

    /**
     * Simulate with automatic audio and output pin wiring.
     * Wires all audio inputs to ADC, first audio/control output to DAC L+R.
     */
    public static short[] simulateAuto(SpinCADBlock block, File inputWav,
            Map<String, Integer> controlInputs, File tempDir) throws Exception {
        // Find first output pin
        String outPin1 = null, outPin2 = null;
        for (SpinCADPin pin : block.pinList) {
            if (pin.getType() == pinType.AUDIO_OUT || pin.getType() == pinType.CONTROL_OUT) {
                if (outPin1 == null) outPin1 = pin.getName();
                else if (outPin2 == null) outPin2 = pin.getName();
            }
        }
        if (outPin1 == null) return null;
        return simulate(block, inputWav, controlInputs, outPin1, outPin2, tempDir);
    }

    // ==================== Analysis ====================

    /** Compute RMS of a double array in [start, end). */
    public static double rms(double[] data, int start, int end) {
        double sumSq = 0;
        int count = 0;
        for (int i = start; i < Math.min(end, data.length); i++) {
            sumSq += data[i] * data[i];
            count++;
        }
        return count > 0 ? Math.sqrt(sumSq / count) : 0;
    }

    /** Compute RMS in dB. */
    public static double rmsDb(double[] data, int start, int end) {
        double r = rms(data, start, end);
        return r > 0 ? 20 * Math.log10(r) : -100;
    }

    /** Find peak absolute value. */
    public static double peak(double[] data) {
        double max = 0;
        for (double v : data) {
            double abs = Math.abs(v);
            if (abs > max) max = abs;
        }
        return max;
    }

    /** Decimate array for plotting (take every nth sample). */
    public static double[] decimate(double[] data, int factor) {
        int len = data.length / factor;
        double[] result = new double[len];
        for (int i = 0; i < len; i++) result[i] = data[i * factor];
        return result;
    }

    /** Create a time axis array in seconds. */
    public static double[] timeAxis(int numSamples, int sampleRate) {
        double[] t = new double[numSamples];
        for (int i = 0; i < numSamples; i++) t[i] = (double) i / sampleRate;
        return t;
    }

    /** Create a time axis in milliseconds. */
    public static double[] timeAxisMs(int numSamples, int sampleRate) {
        double[] t = new double[numSamples];
        for (int i = 0; i < numSamples; i++) t[i] = 1000.0 * i / sampleRate;
        return t;
    }
}
