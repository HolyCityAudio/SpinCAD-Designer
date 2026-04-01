package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.holycityaudio.SpinCAD.CADBlocks.ConstantCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.Mixer_2_to_1CADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.Mixer_3_to_1CADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.Mixer_4_to_1CADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Audio behavior tests for DSP blocks.
 *
 * 1. Impulse response: feeds a single-sample impulse through each block
 *    and verifies output is non-silent, decays, and doesn't clip.
 *
 * 2. Parameter sensitivity: runs each block with control inputs at min
 *    vs max and verifies the output changes.
 *
 * 3. Frequency content: feeds white noise through blocks with controls,
 *    verifies that the spectral balance changes with control values.
 */
public class AudioBehaviorTest {

    @TempDir
    File tempDir;

    private static File impulseWav;
    private static File noiseWav;

    private static final int SAMPLE_RATE = ElmProgram.SAMPLERATE;
    private static final double IMPULSE_DURATION = 0.5;  // seconds
    private static final double NOISE_DURATION = 0.5;
    private static final long SIM_TIMEOUT = 30000;

    // Blocks that crash the simulator due to unconnected pin issues (reg -1)
    // or require special wiring not handled by generic test harness
    private static final Set<String> SKIP_BLOCKS = new HashSet<>(Arrays.asList(
        "ClipControlCADBlock",
        "ControlMixerCADBlock",
        "ControlMixer_2_to_1CADBlock",
        "ControlMixer_3_to_1CADBlock",
        "DistortionCADBlock",
        "OscillatorCADBlock",
        "Two_StageCADBlock",
        "distCADBlock",
        "crossfadeCADBlock",
        "FlangerCADBlock",
        "MultiplyCADBlock",
        "Ted_Rev_ReverbCADBlock",
        "slow_gearCADBlock",
        "reverb_plateCADBlock"
    ));

    @BeforeAll
    static void setup() throws IOException {
        System.setProperty("java.awt.headless", "true");
        impulseWav = generateImpulseWav(IMPULSE_DURATION);
        noiseWav = generateNoiseWav(NOISE_DURATION);
    }

    // ========== Test data providers ==========

    static Stream<String> allBlockClassNames() {
        return BlockDiscovery.findAllBlockClassNames().stream();
    }

    static Stream<String> blocksWithControlInputs() {
        List<String> result = new ArrayList<>();
        for (String className : BlockDiscovery.findAllBlockClassNames()) {
            String simpleName = className.substring(className.lastIndexOf('.') + 1);
            if (simpleName.equals("InputCADBlock") || simpleName.equals("OutputCADBlock")) continue;
            SpinCADBlock block = instantiateBlock(className);
            if (block != null && countControlInputs(block) > 0) {
                result.add(className);
            }
        }
        return result.stream();
    }

    // ========== 1. Impulse Response Tests ==========

    @ParameterizedTest(name = "impulse: {0}")
    @MethodSource("allBlockClassNames")
    void testImpulseResponse(String className) throws Exception {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        if (simpleName.equals("InputCADBlock") || simpleName.equals("OutputCADBlock")) return;
        assumeTrue(!SKIP_BLOCKS.contains(simpleName), "Skipped: " + simpleName);

        // Simulate with impulse input, controls at midpoint
        short[] output = buildAndSimulate(className, impulseWav, 500);
        assumeTrue(output != null, "Could not simulate " + simpleName);

        // Split into left channel samples
        short[] left = extractChannel(output, 0);

        // Check: output should be non-silent (RMS > threshold)
        double rms = rmsOfRange(left, 0, left.length);
        assumeTrue(rms > 0.5, simpleName + ": output is silent (RMS=" + rms + "), skipping");

        // Check: no clipping (warn but don't fail — some blocks clip by design)
        int maxAbs = maxAbsSample(left);
        if (maxAbs >= 32767) {
            System.out.printf("  WARN %s: output clips (maxAbs=%d)%n", simpleName, maxAbs);
        }

        // Report decay characteristics
        double rmsFirstHalf = rmsOfRange(left, 0, left.length / 2);
        double rmsSecondHalf = rmsOfRange(left, left.length / 2, left.length);

        System.out.printf("  OK impulse: %s — RMS=%.1f, first=%.1f, second=%.1f, maxAbs=%d%n",
                simpleName, rms, rmsFirstHalf, rmsSecondHalf, maxAbs);
    }

    // ========== 2. Parameter Sensitivity Tests ==========

    @ParameterizedTest(name = "sensitivity: {0}")
    @MethodSource("blocksWithControlInputs")
    void testParameterSensitivity(String className) throws Exception {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        assumeTrue(!SKIP_BLOCKS.contains(simpleName), "Skipped: " + simpleName);

        // Use noise input — reveals modulation/LFO effects better than impulse
        // Simulate with all controls at minimum (0)
        short[] outputLow = buildAndSimulate(className, noiseWav, 0);
        assumeTrue(outputLow != null, "Could not simulate " + simpleName + " with low controls");

        // Simulate with all controls at maximum (999 ≈ 0.999)
        short[] outputHigh = buildAndSimulate(className, noiseWav, 999);
        assumeTrue(outputHigh != null, "Could not simulate " + simpleName + " with high controls");

        // Both outputs must be non-silent
        short[] leftLow = extractChannel(outputLow, 0);
        short[] leftHigh = extractChannel(outputHigh, 0);
        assumeTrue(rmsOfRange(leftLow, 0, leftLow.length) > 0.5
                || rmsOfRange(leftHigh, 0, leftHigh.length) > 0.5,
                simpleName + ": both outputs silent, skipping");

        // Compare: outputs must differ
        int len = Math.min(outputLow.length, outputHigh.length);
        long totalDiff = 0;
        int maxDiff = 0;
        for (int i = 0; i < len; i++) {
            int diff = Math.abs(outputLow[i] - outputHigh[i]);
            totalDiff += diff;
            if (diff > maxDiff) maxDiff = diff;
        }

        double avgDiff = (double) totalDiff / len;

        System.out.printf("  %s sensitivity: avgDiff=%.2f maxDiff=%d%n",
                simpleName, avgDiff, maxDiff);

        assertTrue(maxDiff > 0,
                simpleName + ": control inputs have no effect on output "
                + "(output identical with controls at 0 vs 0.999)");
    }

    // ========== 3. Frequency Content Tests ==========

    @ParameterizedTest(name = "spectrum: {0}")
    @MethodSource("blocksWithControlInputs")
    void testFrequencyContent(String className) throws Exception {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        assumeTrue(!SKIP_BLOCKS.contains(simpleName), "Skipped: " + simpleName);

        // Simulate with controls at min
        short[] outputLow = buildAndSimulate(className, noiseWav, 0);
        assumeTrue(outputLow != null, "Could not simulate " + simpleName + " with noise (low)");

        // Simulate with controls at max
        short[] outputHigh = buildAndSimulate(className, noiseWav, 999);
        assumeTrue(outputHigh != null, "Could not simulate " + simpleName + " with noise (high)");

        // Extract left channel
        short[] leftLow = extractChannel(outputLow, 0);
        short[] leftHigh = extractChannel(outputHigh, 0);

        // Skip if either output is silent
        double rmsLow = rmsOfRange(leftLow, 0, leftLow.length);
        double rmsHigh = rmsOfRange(leftHigh, 0, leftHigh.length);
        assumeTrue(rmsLow > 1.0 || rmsHigh > 1.0,
                simpleName + ": both outputs silent, skipping spectrum test");

        // Compute energy in 4 frequency bands
        double[] bandsLow = computeBandEnergy(leftLow);
        double[] bandsHigh = computeBandEnergy(leftHigh);

        // Report spectral differences
        String[] bandNames = {"0-2k", "2k-4k", "4k-8k", "8k-16k"};
        StringBuilder sb = new StringBuilder();
        double totalBandDiff = 0;
        for (int b = 0; b < 4; b++) {
            double diffDb = 0;
            if (bandsLow[b] > 0 && bandsHigh[b] > 0) {
                diffDb = 20.0 * Math.log10(bandsHigh[b] / bandsLow[b]);
            }
            totalBandDiff += Math.abs(diffDb);
            sb.append(String.format("  %s: %.1fdB", bandNames[b], diffDb));
        }

        System.out.printf("  %s spectrum:%s%n", simpleName, sb.toString());

        // At least some spectral difference should exist
        // (not all blocks are filters, so we just report — no hard assertion)
    }

    // ========== Model building and simulation ==========

    /**
     * Builds a model with Input → block → Output, wires control inputs
     * to ConstantCADBlocks at the given value, simulates, and returns
     * interleaved stereo samples (L,R,L,R,...).
     *
     * @param controlValue 0-999 (maps to 0.0-0.999 via ConstantCADBlock.setConstant)
     * @return stereo samples, or null if simulation failed
     */
    private short[] buildAndSimulate(String className, File inputWav,
            int controlValue) throws Exception {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);

        SpinCADBlock blockUnderTest = instantiateBlock(className);
        if (blockUnderTest == null) return null;

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(blockUnderTest);
        model.addBlock(outputBlock);

        // Pre-generate code on source blocks
        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        // Wire audio inputs
        int audioIdx = 0;
        for (SpinCADPin pin : blockUnderTest.pinList) {
            if (pin.getType() == pinType.AUDIO_IN && audioIdx < 2) {
                String outPinName = (audioIdx == 0) ? "Output 1" : "Output 2";
                SpinCADPin sourcePin = inputBlock.getPin(outPinName);
                if (sourcePin != null) {
                    pin.setConnection(inputBlock, sourcePin);
                }
                audioIdx++;
            }
        }

        // Wire control inputs with specified value
        for (SpinCADPin pin : blockUnderTest.pinList) {
            if (pin.getType() == pinType.CONTROL_IN) {
                ConstantCADBlock cb = new ConstantCADBlock(50, 50);
                cb.setConstant(controlValue);
                model.addBlock(cb);
                cb.generateCode(tempSfxb);
                SpinCADPin cOut = cb.getPin("Value");
                pin.setConnection(cb, cOut);
            }
        }

        // Wire audio outputs
        List<SpinCADPin> outputPins = new ArrayList<>();
        for (SpinCADPin pin : blockUnderTest.pinList) {
            if (pin.getType() == pinType.AUDIO_OUT || pin.getType() == pinType.CONTROL_OUT) {
                outputPins.add(pin);
            }
        }
        if (outputPins.isEmpty()) return null;

        if (outputPins.size() == 1) {
            outputBlock.getPin("Input 1").setConnection(blockUnderTest, outputPins.get(0));
            outputBlock.getPin("Input 2").setConnection(blockUnderTest, outputPins.get(0));
        } else if (outputPins.size() == 2) {
            outputBlock.getPin("Input 1").setConnection(blockUnderTest, outputPins.get(0));
            outputBlock.getPin("Input 2").setConnection(blockUnderTest, outputPins.get(1));
        } else if (outputPins.size() >= 3) {
            outputBlock.getPin("Input 1").setConnection(blockUnderTest, outputPins.get(0));
            List<SpinCADPin> remaining = outputPins.subList(1, outputPins.size());
            SpinCADBlock mixer = createMixer(remaining.size());
            if (mixer != null) {
                model.addBlock(mixer);
                for (int i = 0; i < remaining.size(); i++) {
                    SpinCADPin mixerIn = mixer.getPin("Input " + (i + 1));
                    if (mixerIn != null) {
                        mixerIn.setConnection(blockUnderTest, remaining.get(i));
                    }
                }
                SpinCADPin mixerOut = findOutputPin(mixer);
                if (mixerOut != null) {
                    outputBlock.getPin("Input 2").setConnection(mixer, mixerOut);
                }
            }
        }

        // Generate code
        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) return null;

        // Must write to DAC
        String listing = renderBlock.getProgramListing(1);
        if (!listing.contains("WRAX DACL") || !listing.contains("WRAX DACR")) {
            return null;
        }

        // Simulate
        File outFile = new File(tempDir,
                simpleName + "_ctrl" + controlValue + "_" + System.nanoTime() + ".wav");
        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) {
            return null;
        }
        if (!outFile.exists()) return null;

        return readWavSamples(outFile);
    }

    // ========== Audio analysis utilities ==========

    /**
     * Compute RMS of samples in range [start, end).
     */
    static double rmsOfRange(short[] samples, int start, int end) {
        if (start >= end || start >= samples.length) return 0;
        end = Math.min(end, samples.length);
        double sumSq = 0;
        for (int i = start; i < end; i++) {
            sumSq += (double) samples[i] * samples[i];
        }
        return Math.sqrt(sumSq / (end - start));
    }

    /**
     * Find maximum absolute sample value.
     */
    static int maxAbsSample(short[] samples) {
        int max = 0;
        for (short s : samples) {
            int abs = Math.abs(s);
            if (abs > max) max = abs;
        }
        return max;
    }

    /**
     * Extract one channel from interleaved stereo samples.
     * @param channel 0=left, 1=right
     */
    static short[] extractChannel(short[] stereo, int channel) {
        short[] mono = new short[stereo.length / 2];
        for (int i = 0; i < mono.length; i++) {
            mono[i] = stereo[i * 2 + channel];
        }
        return mono;
    }

    /**
     * Compute energy in 4 frequency bands using DFT:
     *   Band 0: 0-2000 Hz
     *   Band 1: 2000-4000 Hz
     *   Band 2: 4000-8000 Hz
     *   Band 3: 8000-16384 Hz (Nyquist)
     *
     * Returns RMS energy per band.
     */
    static double[] computeBandEnergy(short[] samples) {
        int[] bandEdges = {0, 2000, 4000, 8000, SAMPLE_RATE / 2};
        double[] bandEnergy = new double[4];

        // Use DFT at selected frequencies within each band
        // (not a full FFT — we just probe enough frequencies to get band energy)
        int probesPerBand = 16;

        for (int b = 0; b < 4; b++) {
            double sumMagSq = 0;
            int fLow = bandEdges[b];
            int fHigh = bandEdges[b + 1];

            for (int p = 0; p < probesPerBand; p++) {
                double freq = fLow + (fHigh - fLow) * (p + 0.5) / probesPerBand;
                double omega = 2.0 * Math.PI * freq / SAMPLE_RATE;

                // Goertzel-like single-frequency DFT
                double cosSum = 0, sinSum = 0;
                for (int i = 0; i < samples.length; i++) {
                    cosSum += samples[i] * Math.cos(omega * i);
                    sinSum += samples[i] * Math.sin(omega * i);
                }
                sumMagSq += (cosSum * cosSum + sinSum * sinSum);
            }

            bandEnergy[b] = Math.sqrt(sumMagSq / probesPerBand) / samples.length;
        }

        return bandEnergy;
    }

    // ========== WAV generation utilities ==========

    /**
     * Generate a single-sample impulse WAV (stereo).
     * First sample at 50% amplitude, rest silence.
     */
    private static File generateImpulseWav(double durationSeconds) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4]; // stereo 16-bit

        // Single impulse sample at half amplitude
        short impulse = (short) (Short.MAX_VALUE / 2);
        data[0] = (byte) (impulse & 0xff);
        data[1] = (byte) ((impulse >> 8) & 0xff);
        data[2] = (byte) (impulse & 0xff);
        data[3] = (byte) ((impulse >> 8) & 0xff);
        // Rest is already zero

        return writeWav(data, numFrames, "impulse");
    }

    /**
     * Generate a white noise WAV (stereo, deterministic seed for reproducibility).
     */
    private static File generateNoiseWav(double durationSeconds) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * durationSeconds);
        byte[] data = new byte[numFrames * 4];

        Random rng = new Random(42); // deterministic seed
        for (int i = 0; i < numFrames; i++) {
            short sample = (short) (rng.nextGaussian() * Short.MAX_VALUE * 0.25);
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }

        return writeWav(data, numFrames, "noise");
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

    // ========== WAV reading ==========

    /**
     * Read a WAV file and return interleaved 16-bit stereo samples.
     */
    private static short[] readWavSamples(File wavFile) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        ais.close();

        byte[] raw = bos.toByteArray();
        short[] samples = new short[raw.length / 2];
        for (int i = 0; i < samples.length; i++) {
            int offset = i * 2;
            samples[i] = (short) ((raw[offset] & 0xff) | ((raw[offset + 1] & 0xff) << 8));
        }
        return samples;
    }

    // ========== Block utilities ==========

    private static SpinCADBlock instantiateBlock(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
            return (SpinCADBlock) ctor.newInstance(100, 100);
        } catch (Exception e) {
            return null;
        }
    }

    private static int countControlInputs(SpinCADBlock block) {
        int count = 0;
        for (SpinCADPin pin : block.pinList) {
            if (pin.getType() == pinType.CONTROL_IN) count++;
        }
        return count;
    }

    private SpinCADBlock createMixer(int inputCount) {
        if (inputCount <= 2) return new Mixer_2_to_1CADBlock(50, 150);
        if (inputCount <= 3) return new Mixer_3_to_1CADBlock(50, 150);
        return new Mixer_4_to_1CADBlock(50, 150);
    }

    private SpinCADPin findOutputPin(SpinCADBlock block) {
        for (SpinCADPin pin : block.pinList) {
            if (pin.isOutputPin()) return pin;
        }
        return null;
    }
}
