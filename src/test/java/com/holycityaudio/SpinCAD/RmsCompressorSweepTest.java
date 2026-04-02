package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;
import com.holycityaudio.SpinCAD.CADBlocks.rms_compressorCADBlock;

/**
 * RMS compressor sweep test (Faust RMS_compression_gain_mono_db style).
 * Produces SVG with two overlay plots:
 *   Left:  fixed threshold -25 dB, overlay strengths 0.25/0.5/0.75/1.0
 *   Right: fixed strength 0.75, overlay thresholds -40/-30/-20/-10/0
 * Input and output axes span -40 to 0 dBFS.
 */
public class RmsCompressorSweepTest {

    @TempDir
    File tempDir;

    private static final int NUM_LEVELS = 21;
    private static double[] INPUT_DB;
    private static File[] inputWavs;

    // Chart 1: vary strength at fixed threshold
    private static final double[] STRENGTHS = {0.25, 0.5, 0.75, 1.0};
    private static final double FIXED_THRESH = -25.0;

    // Chart 2: vary threshold at fixed strength
    private static final double[] THRESH_DB = {-40.0, -30.0, -20.0, -10.0, 0.0};
    private static final double FIXED_STRENGTH = 0.75;

    private static final String[] COLORS_4 = {"#2266cc", "#cc4422", "#22aa44", "#aa44cc"};
    private static final String[] COLORS_5 = {"#2266cc", "#cc4422", "#22aa44", "#aa44cc", "#cc8800"};

    @BeforeAll
    static void setup() throws IOException {
        System.setProperty("java.awt.headless", "true");
        INPUT_DB = new double[NUM_LEVELS];
        inputWavs = new File[NUM_LEVELS];
        for (int i = 0; i < NUM_LEVELS; i++) {
            INPUT_DB[i] = -40.0 + i * 2.0;
            double amplitude = Math.pow(10.0, INPUT_DB[i] / 20.0);
            inputWavs[i] = generateTestWav(1.0, amplitude);
        }
    }

    @Test
    void testSweepAndRenderSvg() throws Exception {
        // --- Chart 1: vary strength at fixed threshold ---
        double[][] strengthResults = new double[STRENGTHS.length][NUM_LEVELS];
        for (int si = 0; si < STRENGTHS.length; si++) {
            for (int li = 0; li < NUM_LEVELS; li++) {
                double rms = measureOutput(STRENGTHS[si], FIXED_THRESH, 0.0,
                        inputWavs[li], String.format("str_s%d_l%d", si, li));
                strengthResults[si][li] = 20.0 * Math.log10(rms + 1e-15);
            }
        }

        // --- Chart 2: vary threshold at fixed strength ---
        double[][] threshResults = new double[THRESH_DB.length][NUM_LEVELS];
        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            for (int li = 0; li < NUM_LEVELS; li++) {
                double rms = measureOutput(FIXED_STRENGTH, THRESH_DB[ti], 0.0,
                        inputWavs[li], String.format("thresh_t%d_l%d", ti, li));
                threshResults[ti][li] = 20.0 * Math.log10(rms + 1e-15);
            }
        }

        // Print tables
        System.out.println("\n=== RMS Compressor: Strength sweep (threshold=" + FIXED_THRESH + " dB) ===");
        for (int si = 0; si < STRENGTHS.length; si++) {
            System.out.printf("Strength %4.2f: ", STRENGTHS[si]);
            for (int li = 0; li < NUM_LEVELS; li += 4) {
                System.out.printf("[%3.0f->%5.1f] ", INPUT_DB[li], strengthResults[si][li]);
            }
            System.out.println();
        }

        System.out.println("\n=== RMS Compressor: Threshold sweep (strength=" + FIXED_STRENGTH + ") ===");
        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            System.out.printf("Thresh %3.0f: ", THRESH_DB[ti]);
            for (int li = 0; li < NUM_LEVELS; li += 4) {
                System.out.printf("[%3.0f->%5.1f] ", INPUT_DB[li], threshResults[ti][li]);
            }
            System.out.println();
        }

        // Generate SVG
        File svgFile = new File("build/rms_compressor_sweep.svg");
        svgFile.getParentFile().mkdirs();
        writeSvg(svgFile, strengthResults, threshResults);
        System.out.println("\nSVG written to: " + svgFile.getAbsolutePath());
    }

    private double measureOutput(double strength, double threshDb, double makeupDb,
            File wavFile, String tag) throws Exception {

        rms_compressorCADBlock comp = new rms_compressorCADBlock(100, 100);
        comp.setstrength(strength);
        comp.setthreshDb(threshDb);
        comp.setmakeupDb(makeupDb);
        comp.settrim(1.0);
        comp.setinGain(1.0);
        comp.setattTime(0.001);   // fast attack for steady-state measurement
        comp.setrelTime(0.001);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(comp);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        // Wire audio input
        SpinCADPin inputL = inputBlock.getPin("Output 1");
        for (int p = 0; p < comp.pinList.size(); p++) {
            SpinCADPin pin = comp.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_IN) {
                pin.setConnection(inputBlock, inputL);
                break;
            }
        }

        // Wire audio output
        List<SpinCADPin> outputPins = new ArrayList<>();
        for (int p = 0; p < comp.pinList.size(); p++) {
            SpinCADPin pin = comp.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_OUT) {
                outputPins.add(pin);
            }
        }
        assertFalse(outputPins.isEmpty(), "No audio output pins");
        outputBlock.getPin("Input 1").setConnection(comp, outputPins.get(0));
        outputBlock.getPin("Input 2").setConnection(comp, outputPins.get(0));

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        assertNotNull(renderBlock, "Render block null");

        File outFile = new File(tempDir, tag + ".wav");
        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                wavFile.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(30000);

        assertFalse(sim.isAlive(), "Simulator timed out");
        if (sim.getSimulationException() != null) {
            throw new RuntimeException("Sim error", sim.getSimulationException());
        }
        assertTrue(outFile.exists(), "No output WAV");

        AudioInputStream ais = AudioSystem.getAudioInputStream(outFile);
        byte[] data = readAllBytes(ais);
        ais.close();

        int bytesPerFrame = 4;
        int totalFrames = data.length / bytesPerFrame;
        int startFrame = totalFrames * 3 / 4;
        double sumSq = 0;
        int count = 0;
        for (int f = startFrame; f < totalFrames; f++) {
            int offset = f * bytesPerFrame;
            if (offset + 1 >= data.length) break;
            short sample = (short) ((data[offset] & 0xff)
                    | ((data[offset + 1] & 0xff) << 8));
            double normalized = sample / 32768.0;
            sumSq += normalized * normalized;
            count++;
        }
        return Math.sqrt(sumSq / Math.max(count, 1));
    }

    /** Compute a 40 dB output axis range that best frames the data. */
    private double[] computeYRange(double[][]... allResults) {
        double dataMin = Double.MAX_VALUE, dataMax = -Double.MAX_VALUE;
        for (double[][] results : allResults) {
            for (double[] curve : results) {
                for (double v : curve) {
                    if (v > -200) { // ignore silence floor
                        dataMin = Math.min(dataMin, v);
                        dataMax = Math.max(dataMax, v);
                    }
                }
            }
        }
        // Round to nearest 5 dB, pad by 2 dB, enforce 40 dB span
        double yMin = Math.floor((dataMin - 2) / 5) * 5;
        double yMax = yMin + 40;
        // If data exceeds top, shift up
        if (dataMax > yMax - 2) {
            yMax = Math.ceil((dataMax + 2) / 5) * 5;
            yMin = yMax - 40;
        }
        return new double[]{yMin, yMax};
    }

    private void writeSvg(File file, double[][] strengthResults, double[][] threshResults)
            throws IOException {
        int plotW = 400, plotH = 400;
        int padL = 55, padR = 30, padT = 40, padB = 50;
        int gapX = 80;
        int cellW = padL + plotW + padR;
        int totalW = cellW * 2 + gapX + 40;
        int totalH = padT + plotH + padB + 80;

        double xMin = -40, xMax = 0;
        double[] yRangeL = computeYRange(strengthResults);
        double[] yRangeR = computeYRange(threshResults);

        PrintWriter pw = new PrintWriter(new FileWriter(file));
        pw.printf("<svg xmlns='http://www.w3.org/2000/svg' width='%d' height='%d' ", totalW, totalH);
        pw.println("font-family='Arial, sans-serif'>");
        pw.printf("<rect width='%d' height='%d' fill='white'/>%n", totalW, totalH);

        // --- Left plot: vary strength ---
        int lx = 20, ly = 10;
        int lpx = lx + padL, lpy = ly + padT;

        drawPlot(pw, lpx, lpy, plotW, plotH, xMin, xMax, yRangeL[0], yRangeL[1],
                String.format("RMS Comp: Vary Strength (Thresh = %.0f dB)", FIXED_THRESH),
                "Input (dBFS)", "Output (dBFS)");

        for (int si = 0; si < STRENGTHS.length; si++) {
            drawCurve(pw, strengthResults[si], lpx, lpy, plotW, plotH, xMin, xMax, yRangeL[0], yRangeL[1], COLORS_4[si]);
        }

        int legY = lpy + plotH + 35;
        for (int si = 0; si < STRENGTHS.length; si++) {
            int legX = lpx + si * 100;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2.5'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS_4[si]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>%.2f</text>%n",
                    legX + 25, legY, COLORS_4[si], STRENGTHS[si]);
        }

        // --- Right plot: vary threshold ---
        int rx = lx + cellW + gapX;
        int rpx = rx + padL, rpy = ly + padT;

        drawPlot(pw, rpx, rpy, plotW, plotH, xMin, xMax, yRangeR[0], yRangeR[1],
                String.format("RMS Comp: Vary Threshold (Strength = %.2f)", FIXED_STRENGTH),
                "Input (dBFS)", "Output (dBFS)");

        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            drawCurve(pw, threshResults[ti], rpx, rpy, plotW, plotH, xMin, xMax, yRangeR[0], yRangeR[1], COLORS_5[ti]);
        }

        for (int ti = 0; ti < THRESH_DB.length; ti++) {
            int legX = rpx + ti * 85;
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='%s' stroke-width='2.5'/>%n",
                    legX, legY - 4, legX + 20, legY - 4, COLORS_5[ti]);
            pw.printf("<text x='%d' y='%d' font-size='11' fill='%s'>%.0f dB</text>%n",
                    legX + 25, legY, COLORS_5[ti], THRESH_DB[ti]);
        }

        pw.println("</svg>");
        pw.close();
    }

    private void drawPlot(PrintWriter pw, int px, int py, int plotW, int plotH,
            double xMin, double xMax, double yMin, double yMax,
            String title, String xLabel, String yLabel) {
        pw.printf("<rect x='%d' y='%d' width='%d' height='%d' fill='#f8f8f8' stroke='#ccc'/>%n",
                px, py, plotW, plotH);
        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='13' font-weight='bold'>%s</text>%n",
                px + plotW / 2, py - 10, title);

        // X-axis grid
        for (double db = xMin; db <= xMax; db += 10) {
            double fx = (db - xMin) / (xMax - xMin);
            int gx = px + (int)(fx * plotW);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#ddd' stroke-width='0.5'/>%n",
                    gx, py, gx, py + plotH);
            pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='9' fill='#666'>%.0f</text>%n",
                    gx, py + plotH + 14, db);
        }
        // Y-axis grid
        for (double db = yMin; db <= yMax; db += 10) {
            double fy = 1.0 - (db - yMin) / (yMax - yMin);
            int gy = py + (int)(fy * plotH);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#ddd' stroke-width='0.5'/>%n",
                    px, gy, px + plotW, gy);
            pw.printf("<text x='%d' y='%d' text-anchor='end' font-size='9' fill='#666'>%.0f</text>%n",
                    px - 5, gy + 3, db);
        }

        // Unity gain line (output = input) clipped to plot area
        double uStartDb = Math.max(xMin, yMin);
        double uEndDb = Math.min(xMax, yMax);
        if (uStartDb < uEndDb) {
            double ux1 = (uStartDb - xMin) / (xMax - xMin);
            double ux2 = (uEndDb - xMin) / (xMax - xMin);
            double uy1 = 1.0 - (uStartDb - yMin) / (yMax - yMin);
            double uy2 = 1.0 - (uEndDb - yMin) / (yMax - yMin);
            pw.printf("<line x1='%d' y1='%d' x2='%d' y2='%d' stroke='#aaa' stroke-width='1' stroke-dasharray='4,3'/>%n",
                    px + (int)(ux1 * plotW), py + (int)(uy1 * plotH),
                    px + (int)(ux2 * plotW), py + (int)(uy2 * plotH));
        }

        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='10' fill='#333'>%s</text>%n",
                px + plotW / 2, py + plotH + 35, xLabel);
        pw.printf("<text x='%d' y='%d' text-anchor='middle' font-size='10' fill='#333' ",
                px - 40, py + plotH / 2);
        pw.printf("transform='rotate(-90,%d,%d)'>%s</text>%n",
                px - 40, py + plotH / 2, yLabel);

        pw.printf("<rect x='%d' y='%d' width='%d' height='%d' fill='none' stroke='#999'/>%n",
                px, py, plotW, plotH);
    }

    private void drawCurve(PrintWriter pw, double[] data, int px, int py,
            int plotW, int plotH, double xMin, double xMax, double yMin, double yMax, String color) {
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < NUM_LEVELS; i++) {
            double inDb = INPUT_DB[i];
            double outDb = Math.max(yMin, Math.min(yMax, data[i]));
            double fx = (inDb - xMin) / (xMax - xMin);
            double fy = 1.0 - (outDb - yMin) / (yMax - yMin);
            fy = Math.max(0, Math.min(1, fy));
            int sx = px + (int)(fx * plotW);
            int sy = py + (int)(fy * plotH);
            if (i == 0) path.append(String.format("M%d,%d", sx, sy));
            else path.append(String.format(" L%d,%d", sx, sy));
        }
        pw.printf("<path d='%s' fill='none' stroke='%s' stroke-width='2'/>%n", path, color);
    }

    private static File generateTestWav(double durationSeconds, double amplitude) throws IOException {
        int sampleRate = ElmProgram.SAMPLERATE;
        int numFrames = (int) (sampleRate * durationSeconds);
        byte[] data = new byte[numFrames * 4];
        double freq = 1000.0;
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / sampleRate;
            short sample = (short) (Short.MAX_VALUE * amplitude * Math.sin(2.0 * Math.PI * freq * t));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }
        File wavFile = File.createTempFile("rms_comp_sweep_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    private byte[] readAllBytes(AudioInputStream ais) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }
}
