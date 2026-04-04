package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Sweeps DC-like ramp through ToverX and Distortion blocks to plot
 * the static transfer function (input amplitude vs output amplitude).
 * Also plots the Distortion block for comparison.
 */
public class ToverXTransferTest {

    @TempDir
    File tempDir;

    private static final int SAMPLE_RATE_LOCAL = PlotUtils.SAMPLE_RATE;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    /**
     * Generate a stereo WAV containing a slow linear ramp from -1 to +1.
     * The ramp is slow enough that the block sees it as quasi-DC.
     */
    private File generateRampWav(double duration) throws IOException {
        int numFrames = (int)(SAMPLE_RATE_LOCAL * duration);
        byte[] data = new byte[numFrames * 4];
        for (int i = 0; i < numFrames; i++) {
            // ramp from -1.0 to +1.0 over the duration
            double val = -1.0 + 2.0 * i / (numFrames - 1);
            short sample = (short)(val * Short.MAX_VALUE);
            int offset = i * 4;
            data[offset]     = (byte)(sample & 0xff);
            data[offset + 1] = (byte)((sample >> 8) & 0xff);
            data[offset + 2] = (byte)(sample & 0xff);
            data[offset + 3] = (byte)((sample >> 8) & 0xff);
        }
        File wavFile = File.createTempFile("spincad_ramp_", ".wav");
        wavFile.deleteOnExit();
        javax.sound.sampled.AudioFormat format =
            new javax.sound.sampled.AudioFormat(SAMPLE_RATE_LOCAL, 16, 2, true, false);
        javax.sound.sampled.AudioInputStream ais =
            new javax.sound.sampled.AudioInputStream(
                new ByteArrayInputStream(data), format, numFrames);
        javax.sound.sampled.AudioSystem.write(ais,
            javax.sound.sampled.AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    @Test
    void plotTransferFunctions() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        double duration = 2.0; // long ramp so it's quasi-DC
        File rampWav = generateRampWav(duration);
        int totalFrames = (int)(SAMPLE_RATE_LOCAL * duration);

        // --- ToverX (1 stage) ---
        double[] toverxOut = simulateBlock(new ToverXCADBlock(100, 100),
            rampWav, "Audio_Output");

        // --- ToverX (4 stages) ---
        ToverXCADBlock toverx4 = new ToverXCADBlock(100, 100);
        toverx4.setnStages(4);
        double[] toverx4Out = simulateBlock(toverx4, rampWav, "Audio_Output");

        // --- Distortion block (hand-written, for comparison) ---
        double[] distOut = simulateBlock(new DistortionCADBlock(100, 100),
            rampWav, "Audio Output 1");

        // Build input axis: -1 to +1
        int len = Math.min(toverxOut.length, totalFrames);
        double[] inputAxis = new double[len];
        for (int i = 0; i < len; i++) {
            inputAxis[i] = -1.0 + 2.0 * i / (len - 1);
        }

        // Trim all arrays to same length
        len = Math.min(len, Math.min(
            toverxOut != null ? toverxOut.length : len,
            distOut != null ? distOut.length : len));
        if (toverx4Out != null) len = Math.min(len, toverx4Out.length);

        inputAxis = Arrays.copyOf(inputAxis, len);
        if (toverxOut != null) toverxOut = Arrays.copyOf(toverxOut, len);
        if (toverx4Out != null) toverx4Out = Arrays.copyOf(toverx4Out, len);
        if (distOut != null) distOut = Arrays.copyOf(distOut, len);

        // Build curves list
        List<double[]> curveList = new ArrayList<>();
        List<String> labelList = new ArrayList<>();
        List<String> colorList = new ArrayList<>();

        if (toverxOut != null) {
            curveList.add(toverxOut);
            labelList.add("T/X 1-stage");
            colorList.add(COLORS[0]);
        }
        if (toverx4Out != null) {
            curveList.add(toverx4Out);
            labelList.add("T/X 4-stage");
            colorList.add(COLORS[1]);
        }
        if (distOut != null) {
            curveList.add(distOut);
            labelList.add("Distortion");
            colorList.add(COLORS[2]);
        }

        double[][] curves = curveList.toArray(new double[0][]);
        String[] labels = labelList.toArray(new String[0]);
        String[] colors = colorList.toArray(new String[0]);

        writePlot(new File(docsDir, "toverx-transfer.png"),
            "Transfer Function (Input vs Output)",
            "Input", "Output",
            -1.0, 1.0, -1.0, 1.0,
            inputAxis, curves, labels, colors);

        // Also plot a zoomed view near zero crossing (-0.25 to +0.25)
        int startIdx = 0, endIdx = len;
        for (int i = 0; i < len; i++) {
            if (inputAxis[i] >= -0.25 && startIdx == 0) startIdx = i;
            if (inputAxis[i] > 0.25) { endIdx = i; break; }
        }
        int zoomLen = endIdx - startIdx;
        double[] zoomInput = Arrays.copyOfRange(inputAxis, startIdx, endIdx);
        double[][] zoomCurves = new double[curves.length][];
        for (int c = 0; c < curves.length; c++) {
            zoomCurves[c] = Arrays.copyOfRange(curves[c], startIdx, endIdx);
        }

        // Find y range for zoomed view
        double yMin = -0.5, yMax = 0.5;

        writePlot(new File(docsDir, "toverx-transfer-zoom.png"),
            "Transfer Function (Zoomed)",
            "Input", "Output",
            -0.25, 0.25, yMin, yMax,
            zoomInput, zoomCurves, labels, colors);

        System.out.println("Transfer function plots written to docs/images/");
        System.out.println("  toverx-transfer.png      (full range)");
        System.out.println("  toverx-transfer-zoom.png (near zero)");
    }

    /**
     * Plot time-domain waveforms of ToverX vs Distortion with a sine input,
     * zoomed to show zero-crossing behavior.
     */
    @Test
    void plotZeroCrossingDetail() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        double duration = 0.5;
        double freq = 440.0;
        double[] amplitudes = {1.0, 0.5, 0.125}; // 0 dB, -6 dB, -18 dB
        String[] ampLabels = {"0 dB", "-6 dB", "-18 dB"};

        for (int ai = 0; ai < amplitudes.length; ai++) {
            double amp = amplitudes[ai];
            File sineWav = generateSineWav(duration, freq, amp);

            // ToverX 1-stage
            double[] toverxOut = simulateBlock(new ToverXCADBlock(100, 100),
                sineWav, "Audio_Output");

            // Distortion (reference)
            double[] distOut = simulateBlock(new DistortionCADBlock(100, 100),
                sineWav, "Audio Output 1");

            // Generate input reference
            int totalFrames = (int)(SAMPLE_RATE_LOCAL * duration);
            double[] inputSig = new double[totalFrames];
            for (int i = 0; i < totalFrames; i++) {
                inputSig[i] = amp * Math.sin(2 * Math.PI * freq * i / SAMPLE_RATE_LOCAL);
            }

            // Show 3 cycles, skip first 0.05s for settling
            int skipSamples = (int)(0.05 * SAMPLE_RATE_LOCAL);
            int displaySamples = (int)(3.0 / freq * SAMPLE_RATE_LOCAL);
            int start = skipSamples;
            int end = Math.min(start + displaySamples, totalFrames);
            int len = end - start;

            // Trim to common length
            if (toverxOut != null) len = Math.min(len, toverxOut.length - start);
            if (distOut != null) len = Math.min(len, distOut.length - start);
            end = start + len;

            double[] timeMs = new double[len];
            for (int i = 0; i < len; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE_LOCAL;

            double[] inputSlice = Arrays.copyOfRange(inputSig, start, end);
            double[] toverxSlice = toverxOut != null ? Arrays.copyOfRange(toverxOut, start, end) : null;
            double[] distSlice = distOut != null ? Arrays.copyOfRange(distOut, start, end) : null;

            List<double[]> curveList = new ArrayList<>();
            List<String> labelList = new ArrayList<>();
            List<String> colorList = new ArrayList<>();

            curveList.add(inputSlice);
            labelList.add("Input");
            colorList.add(COLORS[4]); // grey/brown

            if (toverxSlice != null) {
                curveList.add(toverxSlice);
                labelList.add("T/X 1-stage");
                colorList.add(COLORS[0]);
            }
            if (distSlice != null) {
                curveList.add(distSlice);
                labelList.add("Distortion");
                colorList.add(COLORS[2]);
            }

            writePlot(new File(docsDir, "toverx-sine-" + ampLabels[ai].replace(" ", "") + ".png"),
                "ToverX vs Distortion @ " + ampLabels[ai],
                "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1.0, 1.0,
                timeMs, curveList.toArray(new double[0][]),
                labelList.toArray(new String[0]),
                colorList.toArray(new String[0]));
        }

        System.out.println("Sine waveform plots written to docs/images/");
    }

    private double[] simulateBlock(SpinCADBlock block, File inputWav,
            String outputPinName) throws Exception {
        short[] stereo = simulate(block, inputWav, null,
            outputPinName, null, tempDir);
        if (stereo == null) {
            System.err.println("  Simulation failed for " + block.getName());
            return null;
        }
        short[] left = extractChannel(stereo, 0);
        return toDouble(left);
    }
}
