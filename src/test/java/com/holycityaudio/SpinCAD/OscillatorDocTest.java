package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates documentation plots for Oscillator menu blocks.
 * Each LFO/oscillator is simulated and its output waveform is captured.
 */
public class OscillatorDocTest {

    @TempDir
    File tempDir;

    private static final double SIM_DURATION = 0.5;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateOscillatorPlots() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // === Sin/Cos LFO (~4 Hz, 500ms display, max width) ===
        plotSinCosLFO(docsDir);

        // === Ramp LFO (width=4096 waveform chart) ===
        plotRampLFO(docsDir);

        // === Oscillator (~4 Hz, 500ms display) ===
        plotOscillator("oscillator", "Oscillator",
            () -> {
                OscillatorCADBlock b = new OscillatorCADBlock(100, 100);
                b.setLFO(0.000767); // ~4 Hz
                return b;
            },
            new String[]{"Sine Out", "Cosine Out"}, 500, docsDir);

        // === Sine/Square (default freq, auto-scale to 2 cycles) ===
        plotOscillator("sine-square", "Sine/Square",
            () -> {
                New_OscillatorCADBlock b = new New_OscillatorCADBlock(100, 100);
                return b;
            },
            new String[]{"Sine Output", "Square Output"}, 0, docsDir);

        System.out.println("\nAll oscillator PNGs written to docs/");
    }

    @Test
    void generateOscillatorFreqVsControl() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // Base frequency = 1000 Hz
        double baseLfo = SpinCADBlock.freqToFilt(1000.0);

        // Sweep control input from 0.05 to 0.999 (max for ConstantCADBlock)
        double[] controlValues = {0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.999};
        double[] measuredFreqs = new double[controlValues.length];

        for (int i = 0; i < controlValues.length; i++) {
            OscillatorCADBlock block = new OscillatorCADBlock(100, 100);
            block.setLFO(baseLfo);

            double[] sineData = simulateOscillatorWithControl(block,
                "LFO Speed", Math.min((int)(controlValues[i] * 1000), 999),
                new String[]{"Sine Out", "Cosine Out"});
            if (sineData == null) {
                System.err.println("  SKIP oscillator freq at control=" + controlValues[i]);
                measuredFreqs[i] = 0;
                continue;
            }

            int skip = SAMPLE_RATE / 10;
            measuredFreqs[i] = measureFrequencyHz(sineData, skip);
        }

        writePlot(new File(docsDir, "oscillator-freq-vs-control.png"),
            "Oscillator: Freq vs Control (base 1000 Hz)", "Control Input", "Frequency (Hz)",
            0, 1.0, 0, measuredFreqs[measuredFreqs.length - 1] * 1.1,
            controlValues, new double[][]{measuredFreqs},
            new String[]{"Measured Hz"},
            new String[]{COLORS[0]});

        System.out.println("\nOscillator freq vs control PNG written to docs/images/");
    }

    @Test
    void generateNewOscillatorFrequencyResponse() throws Exception {
        File docsDir = new File("docs/images");
        docsDir.mkdirs();

        // Sweep frequency control from 0 to ~1.0 in 10 steps
        double[] controlValues = {0.01, 0.05, 0.1, 0.15, 0.2, 0.3, 0.4, 0.5, 0.7, 0.9};
        double[] measuredFreqs = new double[controlValues.length];

        for (int i = 0; i < controlValues.length; i++) {
            New_OscillatorCADBlock block = new New_OscillatorCADBlock(100, 100);
            block.setfreqVar(controlValues[i]);

            double[] leftD = simulateOscillator(block, new String[]{"Sine Output", "Square Output"});
            if (leftD == null) {
                System.err.println("  SKIP freq response at " + controlValues[i]);
                measuredFreqs[i] = 0;
                continue;
            }

            // Measure frequency via zero-crossings (skip settling)
            int skip = SAMPLE_RATE / 10;
            measuredFreqs[i] = measureFrequencyHz(leftD, skip);
        }

        writePlot(new File(docsDir, "oscillator-sine-square-freq-response.png"),
            "Sine/Square Frequency Response", "Freq Control", "Frequency (Hz)",
            0, 1.0, 0, measuredFreqs[measuredFreqs.length - 1] * 1.1,
            controlValues, new double[][]{measuredFreqs},
            new String[]{"Measured Hz"},
            new String[]{COLORS[0]});

        System.out.println("\nSine/Square frequency response PNG written to docs/images/");
    }

    @Test
    void generateRampLFOTable() throws Exception {
        String[] pinNames = {"Ramp LFO", "Triangle LFO"};
        int[] widthIndices = {0, 1, 2, 3};
        int[] widthValues = {512, 1024, 2048, 4096};

        System.out.println("\n| Width | Ramp Peak | Triangle Peak | Frequency (Hz) |");
        System.out.println("|-------|-----------|---------------|----------------|");

        for (int wi = 0; wi < widthIndices.length; wi++) {
            double[][] ch = simRampLFO(widthIndices[wi], pinNames);
            if (ch == null) {
                System.out.printf("| %d | FAIL | FAIL | FAIL |%n", widthValues[wi]);
                continue;
            }

            int skip = Math.min(SAMPLE_RATE / 10, ch[0].length / 4);
            double rampPeak = peak(Arrays.copyOfRange(ch[0], skip, ch[0].length));
            double triPeak = peak(Arrays.copyOfRange(ch[1], skip, ch[1].length));

            double freq = measureRampFrequencyHz(ch[0], skip);

            System.out.printf("| %d | %.4f | %.4f | %.1f |%n",
                widthValues[wi], rampPeak, triPeak, freq);
        }
    }

    /** Measure ramp LFO frequency by detecting wrap-around jumps. */
    private static double measureRampFrequencyHz(double[] data, int skipSamples) {
        int start = Math.min(skipSamples, data.length / 4);
        double peakVal = 0;
        for (int i = start; i < data.length; i++) {
            if (data[i] > peakVal) peakVal = data[i];
        }
        if (peakVal < 1e-6) return 0;

        // Ramp decrements smoothly; resets are large upward jumps
        double jumpThreshold = peakVal * 0.3;
        int resets = 0;
        for (int i = start + 1; i < data.length; i++) {
            double jump = data[i] - data[i - 1];
            if (jump > jumpThreshold) {
                resets++;
                i += 3; // avoid double-counting
            }
        }
        double durationSec = (double)(data.length - start) / SAMPLE_RATE;
        return resets / durationSec;
    }

    /** Measure frequency in Hz via zero-crossing detection. */
    private static double measureFrequencyHz(double[] audio, int skipSamples) {
        int crossings = 0;
        int start = Math.min(skipSamples, audio.length / 4);
        for (int i = start + 1; i < audio.length; i++) {
            if (audio[i - 1] <= 0 && audio[i] > 0) crossings++;
        }
        double durationSec = (double)(audio.length - start) / SAMPLE_RATE;
        return crossings / durationSec;
    }

    @FunctionalInterface
    interface BlockFactory { SpinCADBlock create(); }

    /** Sin/Cos LFO: max width, gridlines every 0.25. */
    private void plotSinCosLFO(File docsDir) throws Exception {
        SinCosLFOACADBlock block = new SinCosLFOACADBlock(100, 100);
        block.setLFORate(100);     // ~4 Hz
        block.setLFOWidth(32767);  // max width
        String[] outputPinNames = {"Sine", "Cosine"};

        double[][] channels = simulateOscillatorStereo(block, outputPinNames);
        if (channels == null) {
            System.err.println("  SKIP Sin/Cos LFO: simulation failed");
            return;
        }

        int skip = Math.min(SAMPLE_RATE / 10, channels[0].length / 4);
        int displayLen = Math.min((int)(500 / 1000.0 * SAMPLE_RATE), channels[0].length - skip);

        double[] leftSlice = Arrays.copyOfRange(channels[0], skip, skip + displayLen);
        double[] rightSlice = Arrays.copyOfRange(channels[1], skip, skip + displayLen);
        double[] timeMs = new double[displayLen];
        for (int i = 0; i < displayLen; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        // 8 Y-axis divisions = gridlines every 0.25
        writePlot(new File(docsDir, "oscillator-sincos-lfo.png"),
            "Sin/Cos LFO", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], -1.0, 1.0,
            timeMs, new double[][]{leftSlice, rightSlice},
            outputPinNames,
            new String[]{COLORS[0], COLORS[1]},
            8);
    }

    /** Ramp LFO: single chart at width=4096. */
    private void plotRampLFO(File docsDir) throws Exception {
        int displayMs = 500;
        String[] pinNames = {"Ramp LFO", "Triangle LFO"};

        double[][] ch = simRampLFO(3, pinNames); // width index 3 = 4096
        if (ch == null) {
            System.err.println("  SKIP Ramp LFO: simulation failed");
            return;
        }

        int skip = Math.min(SAMPLE_RATE / 10, ch[0].length / 4);
        int displayLen = Math.min((int)(displayMs / 1000.0 * SAMPLE_RATE), ch[0].length - skip);

        double[] rampSlice = Arrays.copyOfRange(ch[0], skip, skip + displayLen);
        double[] triSlice = Arrays.copyOfRange(ch[1], skip, skip + displayLen);
        double[] timeMs = new double[displayLen];
        for (int i = 0; i < displayLen; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        double yMax = Math.ceil(Math.max(peak(rampSlice), peak(triSlice)) * 10) / 10.0;
        if (yMax < 0.1) yMax = 0.1;

        writePlot(new File(docsDir, "oscillator-ramp-lfo.png"),
            "Ramp LFO (Width = 4096)", "Time (ms)", "Amplitude",
            0, timeMs[timeMs.length - 1], 0, yMax,
            timeMs, new double[][]{rampSlice, triSlice},
            pinNames,
            new String[]{COLORS[0], COLORS[1]},
            5);
    }

    /** Simulate a RampLFO with the given width index, return [ramp, triangle] channels. */
    private double[][] simRampLFO(int widthIndex, String[] outputPinNames) throws Exception {
        RampLFOCADBlock block = new RampLFOCADBlock(100, 100);
        block.setLFORate(8192);
        block.setLFOWidth(widthIndex);
        return simulateOscillatorStereo(block, outputPinNames);
    }

    /**
     * @param displayMs display window in ms; 0 = auto-detect 2 cycles from frequency
     */
    private void plotOscillator(String fileBase, String title,
            BlockFactory factory, String[] outputPinNames, int displayMs,
            File docsDir) throws Exception {

        SpinCADBlock block = factory.create();
        double[] leftD = simulateOscillator(block, outputPinNames);
        if (leftD == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }

        block = factory.create();
        double[][] channels = simulateOscillatorStereo(block, outputPinNames);
        if (channels == null) {
            System.err.println("  SKIP " + title + ": simulation failed");
            return;
        }
        leftD = channels[0];
        double[] rightD = channels[1];

        int skip = Math.min(SAMPLE_RATE / 10, leftD.length / 4);
        int displayLen;

        if (displayMs > 0) {
            displayLen = Math.min((int)(displayMs / 1000.0 * SAMPLE_RATE), leftD.length - skip);
        } else {
            // Auto-detect: measure frequency, show 2 cycles
            double freqHz = measureFrequencyHz(leftD, skip);
            if (freqHz > 0) {
                displayLen = Math.min((int)(2.0 / freqHz * SAMPLE_RATE), leftD.length - skip);
            } else {
                displayLen = Math.min(leftD.length - skip, SAMPLE_RATE / 5);
            }
        }

        double[] leftSlice = Arrays.copyOfRange(leftD, skip, skip + displayLen);
        double[] rightSlice = Arrays.copyOfRange(rightD, skip, skip + displayLen);
        double[] timeMs = new double[displayLen];
        for (int i = 0; i < displayLen; i++) timeMs[i] = 1000.0 * i / SAMPLE_RATE;

        if (outputPinNames.length > 1) {
            writePlot(new File(docsDir, "oscillator-" + fileBase + ".png"),
                title, "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1.0, 1.0,
                timeMs, new double[][]{leftSlice, rightSlice},
                outputPinNames,
                new String[]{COLORS[0], COLORS[1]});
        } else {
            writePlot(new File(docsDir, "oscillator-" + fileBase + ".png"),
                title, "Time (ms)", "Amplitude",
                0, timeMs[timeMs.length - 1], -1.0, 1.0,
                timeMs, new double[][]{leftSlice},
                outputPinNames,
                new String[]{COLORS[0]});
        }
    }

    /** Simulate an oscillator block, return left channel audio as doubles. */
    private double[] simulateOscillator(SpinCADBlock block, String[] outputPinNames) throws Exception {
        double[][] channels = simulateOscillatorStereo(block, outputPinNames);
        return channels != null ? channels[0] : null;
    }

    /** Simulate an oscillator with a constant wired to a control input pin. */
    private double[] simulateOscillatorWithControl(SpinCADBlock block,
            String controlPinName, int constantValue,
            String[] outputPinNames) throws Exception {
        File silentWav = generateSilentWav(SIM_DURATION);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);
        ConstantCADBlock constBlock = new ConstantCADBlock(50, 50);
        constBlock.setConstant(constantValue);

        model.addBlock(inputBlock);
        model.addBlock(constBlock);
        model.addBlock(block);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);
        constBlock.generateCode(tempSfxb);

        // Wire control input
        SpinCADPin ctrlPin = block.getPin(controlPinName);
        if (ctrlPin != null) {
            ctrlPin.setConnection(constBlock, constBlock.getPin("Value"));
        }

        // Wire outputs
        SpinCADPin outPin1 = block.getPin(outputPinNames[0]);
        if (outPin1 == null) return null;
        outputBlock.getPin("Input 1").setConnection(block, outPin1);

        if (outputPinNames.length > 1) {
            SpinCADPin outPin2 = block.getPin(outputPinNames[1]);
            if (outPin2 != null)
                outputBlock.getPin("Input 2").setConnection(block, outPin2);
            else
                outputBlock.getPin("Input 2").setConnection(block, outPin1);
        } else {
            outputBlock.getPin("Input 2").setConnection(block, outPin1);
        }

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) return null;

        File outFile = new File(tempDir, "osc_ctrl_" + System.nanoTime() + ".wav");
        org.andrewkilpatrick.elmGen.simulator.SpinSimulator sim =
            new org.andrewkilpatrick.elmGen.simulator.SpinSimulator(renderBlock,
                silentWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) return null;
        if (!outFile.exists()) return null;

        short[] stereo = readWavSamples(outFile);
        return toDouble(extractChannel(stereo, 0));
    }

    /** Simulate an oscillator block, return [left, right] channel audio as doubles. */
    private double[][] simulateOscillatorStereo(SpinCADBlock block, String[] outputPinNames) throws Exception {
        File silentWav = generateSilentWav(SIM_DURATION);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);
        model.addBlock(inputBlock);
        model.addBlock(block);
        model.addBlock(outputBlock);

        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        SpinCADPin outPin1 = block.getPin(outputPinNames[0]);
        if (outPin1 == null) return null;
        outputBlock.getPin("Input 1").setConnection(block, outPin1);

        if (outputPinNames.length > 1) {
            SpinCADPin outPin2 = block.getPin(outputPinNames[1]);
            if (outPin2 != null)
                outputBlock.getPin("Input 2").setConnection(block, outPin2);
            else
                outputBlock.getPin("Input 2").setConnection(block, outPin1);
        } else {
            outputBlock.getPin("Input 2").setConnection(block, outPin1);
        }

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) return null;

        File outFile = new File(tempDir, "osc_" + System.nanoTime() + ".wav");
        org.andrewkilpatrick.elmGen.simulator.SpinSimulator sim =
            new org.andrewkilpatrick.elmGen.simulator.SpinSimulator(renderBlock,
                silentWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) return null;
        if (!outFile.exists()) return null;

        short[] stereo = readWavSamples(outFile);
        return new double[][]{
            toDouble(extractChannel(stereo, 0)),
            toDouble(extractChannel(stereo, 1))
        };
    }
}
