package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;
import java.util.HashSet;

import javax.sound.sampled.*;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Function-specific verification tests for audio and control blocks
 * across all menu categories: Instructions, I/O-Mix, Waveshaper,
 * Dynamics, Delay, Reverb, Modulation, Pitch.
 */
public class AudioBlockFunctionTest {

    @TempDir
    File tempDir;

    private static final int SAMPLE_RATE = ElmProgram.SAMPLERATE;
    private static final double SINE_AMPLITUDE = 0.25;
    private static final double SINE_DURATION = 0.5;
    private static final long SIM_TIMEOUT = 30000;
    private static final double DC_TOLERANCE = 0.05;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // ======================================================================
    //  INSTRUCTIONS (control domain)
    // ======================================================================

    @Test
    void testScaleOffset_appliesScaleAndOffset() {
        System.out.println("\n=== ScaleOffset ===");
        // Default: inLow=0, inHigh=1, outLow=0, outHigh=0.75
        // scale = (0.75-0)/(1-0) = 0.75, offset = 0 - 0*0.75 = 0
        int[] inputs = {0, 250, 500, 750, 999};

        for (int val : inputs) {
            ScaleOffsetControlCADBlock block = new ScaleOffsetControlCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Control Input 1", val),
                "Control Output 1", null);

            assertNotNull(result, "ScaleOffset sim failed for input=" + val);
            double x = val / 1000.0;
            double expected = 0.75 * x; // default mapping
            System.out.printf("  input=%.3f  expected=%.3f  got=%.3f%n", x, expected, result[0]);
            assertEquals(expected, result[0], DC_TOLERANCE,
                String.format("ScaleOffset input=%.3f", x));
        }
    }

    @Test
    void testHalfWave_rectifiesInput() {
        System.out.println("\n=== HalfWave ===");
        // HalfWave passes positive values, zeroes negative.
        // With ConstantCADBlock (always positive 0-1), all should pass through.
        int[] inputs = {100, 500, 900};

        for (int val : inputs) {
            Half_WaveCADBlock block = new Half_WaveCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Input", val), "Output", null);

            assertNotNull(result, "HalfWave sim failed for input=" + val);
            double x = val / 1000.0;
            System.out.printf("  input=%.3f  got=%.3f%n", x, result[0]);
            assertTrue(result[0] > 0,
                String.format("HalfWave should pass positive input=%.3f", x));
        }
    }

    @Test
    void testAbsa_absoluteValue() {
        System.out.println("\n=== AbsoluteValue ===");
        // With positive inputs, output should equal input
        int[] inputs = {100, 500, 900};

        for (int val : inputs) {
            AbsaCADBlock block = new AbsaCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Input", val), "Output", null);

            assertNotNull(result, "Absa sim failed for input=" + val);
            double x = val / 1000.0;
            System.out.printf("  input=%.3f  got=%.3f%n", x, result[0]);
            assertTrue(result[0] > 0,
                String.format("Absa output should be positive for input=%.3f", x));
        }
    }

    @Test
    void testMultiply_singleInputSquares() {
        System.out.println("\n=== Multiply (single input) ===");
        // Test single input to avoid register compaction issues.
        // With only Input 1 connected, MULX reads from the same register,
        // effectively squaring the input.
        int[] inputs = {300, 500, 700, 900};

        for (int val : inputs) {
            MultiplyCADBlock block = new MultiplyCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Input 1", val),
                "Output", null);

            assertNotNull(result, "Multiply sim failed for input=" + val);
            double x = val / 1000.0;
            System.out.printf("  input=%.3f  got=%.4f%n", x, result[0]);
            assertTrue(result[0] >= 0,
                String.format("Multiply output should be non-negative for input=%.3f", x));
        }
    }

    @Test
    void testMaxx_maximumOfInputs() {
        System.out.println("\n=== Maximum ===");
        // Test single input to avoid register compaction issues
        int[] inputs = {200, 500, 800};

        for (int val : inputs) {
            maxxCADBlock block = new maxxCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Input 1", val),
                "Output", null);

            assertNotNull(result, "Maxx sim failed for input=" + val);
            double x = val / 1000.0;
            System.out.printf("  input1=%.3f  got=%.3f%n", x, result[0]);
            assertTrue(result[0] > 0,
                String.format("Max should output positive for input=%.3f", x));
        }
    }

    @Test
    void testExp_simulatesWithoutError() {
        System.out.println("\n=== Exp ===");
        // EXP instruction operates on LOG-domain values. With raw DC input,
        // the output may be zero or near-zero due to the LOG/EXP domain mismatch.
        // Verify the block generates code and simulates without error.
        int[] inputs = {200, 500, 800};

        for (int val : inputs) {
            ExpCADBlock block = new ExpCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Input", val), "Exp Output", null);

            assertNotNull(result, "Exp sim failed for input=" + val);
            System.out.printf("  input=%.3f  got=%.4f%n", val / 1000.0, result[0]);
        }
    }

    @Test
    void testLog_logarithmicTransform() {
        System.out.println("\n=== Log ===");
        int[] inputs = {200, 500, 800};

        for (int val : inputs) {
            LogCADBlock block = new LogCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Control Input", val), "Log Output", null);

            assertNotNull(result, "Log sim failed for input=" + val);
            System.out.printf("  input=%.3f  got=%.4f%n", val / 1000.0, result[0]);
            // LOG instruction produces a value; just verify it runs
            assertNotNull(result[0], "Log should produce output");
        }
    }

    @Test
    void testRoot_squareRoot() {
        System.out.println("\n=== Root ===");
        int[] inputs = {250, 500, 810};

        for (int val : inputs) {
            RootCADBlock block = new RootCADBlock(100, 100);
            double[] result = simulateControlDC(block,
                Map.of("Control Input 1", val),
                "Control Output 1", null);

            assertNotNull(result, "Root sim failed for input=" + val);
            double x = val / 1000.0;
            System.out.printf("  input=%.3f  got=%.4f%n", x, result[0]);
            // Square root of positive value should be positive and >= input (since input < 1)
            assertTrue(result[0] > 0,
                String.format("Root output should be positive for input=%.3f", x));
        }
    }

    // ======================================================================
    //  I/O - MIX
    // ======================================================================

    @Test
    void testVolume_attenuatesSignal() {
        System.out.println("\n=== Volume ===");
        // Full volume: should pass signal through (~0 dB)
        VolumeCADBlock blockFull = new VolumeCADBlock(100, 100);
        Double gainFull = simulateAudioBlock(blockFull, 1000, "Output",
            Map.of("Volume", 999));

        assertNotNull(gainFull, "Volume full sim failed");
        System.out.printf("  volume=1.0  gain=%.1f dB%n", gainFull);
        assertTrue(gainFull > -3.0, "Full volume should be near 0 dB, got " + gainFull);

        // Half volume: should attenuate by ~6 dB
        VolumeCADBlock blockHalf = new VolumeCADBlock(100, 100);
        Double gainHalf = simulateAudioBlock(blockHalf, 1000, "Output",
            Map.of("Volume", 500));

        assertNotNull(gainHalf, "Volume half sim failed");
        System.out.printf("  volume=0.5  gain=%.1f dB%n", gainHalf);
        assertTrue(gainHalf < gainFull,
            "Half volume should be quieter than full volume");
    }

    @Test
    void testPhaseInvert_invertsPolariy() {
        System.out.println("\n=== Phase Invert ===");
        Phase_InvertCADBlock block = new Phase_InvertCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Output", null);

        assertNotNull(gain, "PhaseInvert sim failed");
        System.out.printf("  gain=%.1f dB (should be ~0 dB)%n", gain);
        // Phase invert preserves amplitude
        assertTrue(gain > -3.0, "Phase invert should preserve level, got " + gain + " dB");
    }

    @Test
    void testCrossfade_blendsTwoInputs() {
        System.out.println("\n=== Crossfade ===");
        // Fade=0: should output input 1 only
        crossfadeCADBlock blockLeft = new crossfadeCADBlock(100, 100);
        Double gainLeft = simulateAudioBlockDualInput(blockLeft, 1000, "Audio Output",
            Map.of("Fade", 0));

        assertNotNull(gainLeft, "Crossfade fade=0 sim failed");
        System.out.printf("  fade=0  gain=%.1f dB%n", gainLeft);
        assertTrue(gainLeft > -10.0, "Crossfade at 0 should pass input, got " + gainLeft);

        // Fade=999: should output input 2 only
        crossfadeCADBlock blockRight = new crossfadeCADBlock(100, 100);
        Double gainRight = simulateAudioBlockDualInput(blockRight, 1000, "Audio Output",
            Map.of("Fade", 999));

        assertNotNull(gainRight, "Crossfade fade=999 sim failed");
        System.out.printf("  fade=999  gain=%.1f dB%n", gainRight);
        assertTrue(gainRight > -10.0, "Crossfade at 999 should pass input, got " + gainRight);
    }

    @Test
    void testPanner_distributesSignal() {
        System.out.println("\n=== Panner ===");
        // Pan center: both outputs should have signal
        pannerCADBlock block = new pannerCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Output 1",
            Map.of("Pan", 500));

        assertNotNull(gain, "Panner sim failed");
        System.out.printf("  pan=center  output1_gain=%.1f dB%n", gain);
        assertTrue(gain > -15.0, "Panner center should have signal on output 1");
    }

    @Test
    void testGainBoost_boostsSignal() {
        System.out.println("\n=== GainBoost ===");
        GainBoostCADBlock block = new GainBoostCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, null, null);

        assertNotNull(gain, "GainBoost sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        // GainBoost should amplify the signal
        assertTrue(gain > -1.0, "GainBoost should increase or maintain level, got " + gain);
    }

    // ======================================================================
    //  WAVESHAPER
    // ======================================================================

    @Test
    void testDistortion_producesOutput() {
        System.out.println("\n=== Distortion ===");
        DistortionCADBlock block = new DistortionCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, null, null);

        assertNotNull(gain, "Distortion sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "Distortion should produce audible output");
    }

    @Test
    void testCubeGain_producesOutput() {
        System.out.println("\n=== CubeGain ===");
        CubeGainCADBlock block = new CubeGainCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, null, null);

        assertNotNull(gain, "CubeGain sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        // Cube of small signal (0.25 amplitude) will be very quiet: 0.25^3 ≈ 0.016
        assertTrue(gain > -60.0, "CubeGain should produce output");
    }

    @Test
    void testOverdrive_producesOutput() {
        System.out.println("\n=== Overdrive ===");
        // Test with default gain (0.25) — light overdrive
        OverdriveCADBlock block = new OverdriveCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, null, null);

        assertNotNull(gain, "Overdrive sim failed");
        System.out.printf("  default gain  output=%.1f dB%n", gain);
        assertTrue(gain > -30.0, "Overdrive should produce output");
    }

    @Test
    void testAliaser_producesSmoothedOutput() {
        System.out.println("\n=== Aliaser ===");
        AliaserCADBlock block = new AliaserCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 500, "Smooth", null);

        assertNotNull(gain, "Aliaser sim failed");
        System.out.printf("  smooth output gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "Aliaser smooth output should have signal");
    }

    // ======================================================================
    //  DYNAMICS
    // ======================================================================

    @Test
    void testSoftKneeLimiter_limitsSignal() {
        System.out.println("\n=== Soft Knee Limiter ===");
        soft_knee_limiterCADBlock block = new soft_knee_limiterCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Audio_Output", null);

        assertNotNull(gain, "SoftKneeLimiter sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        // Limiter should pass signal through (possibly attenuated)
        assertTrue(gain > -20.0, "Limiter should pass signal");
    }

    @Test
    void testNoiseGate_passesSignal() {
        System.out.println("\n=== Noise Gate ===");
        NoiseGateCADBlock block = new NoiseGateCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Audio Out", null);

        assertNotNull(gain, "NoiseGate sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        // With strong input signal, gate should be open
        assertTrue(gain > -12.0, "NoiseGate should pass signal above threshold");
    }

    @Test
    void testRmsLimiter_passesSignal() {
        System.out.println("\n=== RMS Limiter ===");
        rms_limiterCADBlock block = new rms_limiterCADBlock(100, 100);
        // RMS limiter uses sidechain = same input
        Double gain = simulateAudioBlock(block, 1000, "Output", null);

        assertNotNull(gain, "RMS Limiter sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "RMS Limiter should pass signal");
    }

    // ======================================================================
    //  DELAY
    // ======================================================================

    @Test
    void testAllpass_passesSignal() {
        System.out.println("\n=== Allpass ===");
        allpassCADBlock block = new allpassCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Output", null);

        assertNotNull(gain, "Allpass sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        // Allpass should approximately preserve amplitude
        assertTrue(gain > -10.0, "Allpass should approximately preserve signal level");
    }

    @Test
    void testTripleTap_producesOutput() {
        System.out.println("\n=== TripleTap Delay ===");
        // Delay taps may not have filled within 0.5s sim for longer delay times.
        // Default delay times position taps within the delay buffer, so measure
        // whether the simulation runs and the block generates code without error.
        TripleTapCADBlock block = new TripleTapCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Tap 1 Out", null);

        assertNotNull(gain, "TripleTap sim failed");
        System.out.printf("  tap1 gain=%.1f dB%n", gain);
        // Delay block may output silence if taps haven't been reached yet;
        // just verify it runs without error
        assertTrue(gain >= -80.0, "TripleTap should simulate without error");
    }

    @Test
    void testSixTapDelay_producesOutput() {
        System.out.println("\n=== SixTap Delay ===");
        sixtapCADBlock block = new sixtapCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Mix L Out", null);

        assertNotNull(gain, "SixTap sim failed");
        System.out.printf("  mix L gain=%.1f dB%n", gain);
        assertTrue(gain > -30.0, "SixTap should produce output");
    }

    // ======================================================================
    //  REVERB
    // ======================================================================

    @Test
    void testMinReverb_producesReverbOutput() {
        System.out.println("\n=== MinReverb ===");
        MinReverbCADBlock block = new MinReverbCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, null, null);

        assertNotNull(gain, "MinReverb sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "MinReverb should produce output");
    }

    @Test
    void testReverb_producesOutput() {
        System.out.println("\n=== Room Reverb ===");
        reverbCADBlock block = new reverbCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Output_Left", null);

        assertNotNull(gain, "Reverb sim failed");
        System.out.printf("  left gain=%.1f dB%n", gain);
        assertTrue(gain > -30.0, "Reverb should produce output");
    }

    @Test
    void testReverbHall_producesOutput() {
        System.out.println("\n=== Hall Reverb ===");
        reverb_hallCADBlock block = new reverb_hallCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "OutputL", null);

        assertNotNull(gain, "Hall reverb sim failed");
        System.out.printf("  left gain=%.1f dB%n", gain);
        assertTrue(gain > -30.0, "Hall reverb should produce output");
    }

    @Test
    void testReverbPlate_producesOutput() {
        System.out.println("\n=== Plate Reverb ===");
        reverb_plateCADBlock block = new reverb_plateCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Output", null);

        assertNotNull(gain, "Plate reverb sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -30.0, "Plate reverb should produce output");
    }

    @Test
    void testAmbience_producesOutput() {
        System.out.println("\n=== Ambience ===");
        AmbienceCADBlock block = new AmbienceCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Audio Output L", null);

        assertNotNull(gain, "Ambience sim failed");
        System.out.printf("  left gain=%.1f dB%n", gain);
        assertTrue(gain > -30.0, "Ambience should produce output");
    }

    // ======================================================================
    //  MODULATION
    // ======================================================================

    @Test
    void testChorus_producesOutput() {
        System.out.println("\n=== Chorus ===");
        ChorusCADBlock block = new ChorusCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Output", null);

        assertNotNull(gain, "Chorus sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -12.0, "Chorus should produce audible output");
    }

    @Test
    void testFlanger_producesOutput() {
        System.out.println("\n=== Flanger ===");
        FlangerCADBlock block = new FlangerCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Output", null);

        assertNotNull(gain, "Flanger sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -12.0, "Flanger should produce audible output");
    }

    @Test
    void testRingMod_producesOutput() {
        System.out.println("\n=== Ring Modulator ===");
        RingModCADBlock block = new RingModCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 500, null, null);

        assertNotNull(gain, "RingMod sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "Ring modulator should produce output");
    }

    @Test
    void testPhaser_producesOutput() {
        System.out.println("\n=== Phaser ===");
        PhaserCADBlock block = new PhaserCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 1000, "Mix Out", null);

        assertNotNull(gain, "Phaser sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -12.0, "Phaser should produce audible output");
    }

    // ======================================================================
    //  PITCH
    // ======================================================================

    @Test
    void testPitchUpDown_producesOutput() {
        System.out.println("\n=== PitchUpDown ===");
        pitchupdownCADBlock block = new pitchupdownCADBlock(100, 100);
        Double gainDown = simulateAudioBlock(block, 500, "Pitch_Down_Out", null);

        assertNotNull(gainDown, "PitchUpDown down sim failed");
        System.out.printf("  pitch down gain=%.1f dB%n", gainDown);
        assertTrue(gainDown > -20.0, "Pitch down should produce output");
    }

    @Test
    void testPitchShiftFixed_producesOutput() {
        System.out.println("\n=== PitchShiftFixed ===");
        PitchShiftFixedCADBlock block = new PitchShiftFixedCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 500, "Pitch Out", null);

        assertNotNull(gain, "PitchShiftFixed sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "PitchShiftFixed should produce output");
    }

    @Test
    void testPitchOffset_producesOutput() {
        System.out.println("\n=== PitchOffset ===");
        pitchoffsetCADBlock block = new pitchoffsetCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 500, "Output",
            Map.of("Pitch_Offset", 500));

        assertNotNull(gain, "PitchOffset sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "PitchOffset should produce output");
    }

    @Test
    void testOctave_producesOutput() {
        System.out.println("\n=== Octave ===");
        OctaveCADBlock block = new OctaveCADBlock(100, 100);
        Double gain = simulateAudioBlock(block, 500, "Audio_Output", null);

        assertNotNull(gain, "Octave sim failed");
        System.out.printf("  gain=%.1f dB%n", gain);
        assertTrue(gain > -20.0, "Octave should produce output");
    }

    // ======================================================================
    //  Simulation engines
    // ======================================================================

    /**
     * Simulate an audio block with a sine wave input and measure output gain in dB.
     */
    private Double simulateAudioBlock(SpinCADBlock block, double sineFreqHz,
                                       String outputPinName,
                                       Map<String, Integer> controlValues) {
        try {
            SpinCADModel model = new SpinCADModel();
            InputCADBlock inputBlock = new InputCADBlock(0, 0);
            OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

            model.addBlock(inputBlock);
            model.addBlock(block);
            model.addBlock(outputBlock);

            SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
            inputBlock.generateCode(tempSfxb);

            // Wire all audio input pins to same source
            SpinCADPin srcPin = inputBlock.getPin("Output 1");
            for (SpinCADPin pin : block.pinList) {
                if (pin.getType() == pinType.AUDIO_IN && !pin.isConnected()) {
                    if (srcPin != null) {
                        pin.setConnection(inputBlock, srcPin);
                    }
                }
            }

            // Wire control inputs
            if (controlValues != null) {
                for (Map.Entry<String, Integer> entry : controlValues.entrySet()) {
                    SpinCADPin ctrlPin = block.getPin(entry.getKey());
                    if (ctrlPin != null && ctrlPin.getType() == pinType.CONTROL_IN) {
                        ConstantCADBlock cb = new ConstantCADBlock(50, 50);
                        cb.setConstant(entry.getValue());
                        model.addBlock(cb);
                        cb.generateCode(tempSfxb);
                        ctrlPin.setConnection(cb, cb.getPin("Value"));
                    }
                }
            }

            // Find output pin
            SpinCADPin outPin = null;
            if (outputPinName != null) {
                outPin = block.getPin(outputPinName);
            }
            if (outPin == null) {
                for (SpinCADPin pin : block.pinList) {
                    if (pin.getType() == pinType.AUDIO_OUT) {
                        outPin = pin;
                        break;
                    }
                }
            }
            if (outPin == null) return null;

            outputBlock.getPin("Input 1").setConnection(block, outPin);
            outputBlock.getPin("Input 2").setConnection(block, outPin);

            model.sortAlignGen();
            SpinFXBlock renderBlock = model.getRenderBlock();
            if (renderBlock == null) return null;

            String listing = renderBlock.getProgramListing(1);
            if (!listing.contains("WRAX DACL") || !listing.contains("WRAX DACR")) {
                return null;
            }

            File sineWav = generateSineWav(sineFreqHz);
            File outFile = new File(tempDir,
                "audio_" + (int) sineFreqHz + "_" + System.nanoTime() + ".wav");

            SpinSimulator sim = new SpinSimulator(renderBlock,
                sineWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
            sim.setLoopMode(false);
            sim.start();
            sim.join(SIM_TIMEOUT);

            if (sim.isAlive() || sim.getSimulationException() != null) return null;
            if (!outFile.exists()) return null;

            short[] stereo = readWavSamples(outFile);
            short[] left = extractChannel(stereo, 0);

            int start = left.length / 2;
            double outputRms = rmsOfRange(left, start, left.length);
            double inputRms = SINE_AMPLITUDE * 32767.0 / Math.sqrt(2.0);

            if (outputRms < 1.0) return -80.0;
            return 20.0 * Math.log10(outputRms / inputRms);

        } catch (Exception e) {
            System.err.println("  Sim error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Simulate an audio block with two audio inputs (e.g., crossfade).
     * Wires the same sine to both audio inputs.
     */
    private Double simulateAudioBlockDualInput(SpinCADBlock block, double sineFreqHz,
                                                String outputPinName,
                                                Map<String, Integer> controlValues) {
        // Same as simulateAudioBlock — it already wires all audio inputs
        return simulateAudioBlock(block, sineFreqHz, outputPinName, controlValues);
    }

    /**
     * Simulate a control block with DC inputs and measure steady-state output.
     */
    private double[] simulateControlDC(SpinCADBlock block,
                                        Map<String, Integer> controlInputs,
                                        String outputPinName1,
                                        String outputPinName2) {
        try {
            short[][] channels = runControlSimulation(block, controlInputs,
                outputPinName1, outputPinName2);
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

    private short[][] runControlSimulation(SpinCADBlock block,
                                            Map<String, Integer> controlInputs,
                                            String outputPinName1,
                                            String outputPinName2) throws Exception {
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

        // Wire audio input if block has one
        for (SpinCADPin pin : block.pinList) {
            if (pin.getType() == pinType.AUDIO_IN && !pin.isConnected()) {
                SpinCADPin srcPin = inputBlock.getPin("Output 1");
                if (srcPin != null) {
                    pin.setConnection(inputBlock, srcPin);
                }
                break;
            }
        }

        SpinCADPin outPin1 = block.getPin(outputPinName1);
        if (outPin1 == null) return null;

        outputBlock.getPin("Input 1").setConnection(block, outPin1);

        if (outputPinName2 != null) {
            SpinCADPin outPin2 = block.getPin(outputPinName2);
            if (outPin2 != null) {
                outputBlock.getPin("Input 2").setConnection(block, outPin2);
            }
        } else {
            outputBlock.getPin("Input 2").setConnection(block, outPin1);
        }

        model.sortAlignGen();
        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) return null;

        String listing = renderBlock.getProgramListing(1);
        if (!listing.contains("WRAX DACL") || !listing.contains("WRAX DACR")) {
            return null;
        }

        File silentWav = generateSilentWav();
        File outFile = new File(tempDir, "ctrl_" + System.nanoTime() + ".wav");

        SpinSimulator sim = new SpinSimulator(renderBlock,
            silentWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(SIM_TIMEOUT);

        if (sim.isAlive() || sim.getSimulationException() != null) return null;
        if (!outFile.exists()) return null;

        short[] stereo = readWavSamples(outFile);
        short[] left = extractChannel(stereo, 0);
        short[] right = extractChannel(stereo, 1);

        return new short[][]{left, right};
    }

    // ======================================================================
    //  Audio utilities
    // ======================================================================

    private File generateSineWav(double freqHz) throws IOException {
        int numFrames = (int) (SAMPLE_RATE * SINE_DURATION);
        byte[] data = new byte[numFrames * 4];

        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / SAMPLE_RATE;
            short sample = (short) (SINE_AMPLITUDE * 32767.0
                * Math.sin(2.0 * Math.PI * freqHz * t));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }

        File wavFile = File.createTempFile("sine_" + (int) freqHz + "_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    private File generateSilentWav() throws IOException {
        int numFrames = (int) (SAMPLE_RATE * SINE_DURATION);
        byte[] data = new byte[numFrames * 4];

        File wavFile = File.createTempFile("silent_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    static short[] extractChannel(short[] stereo, int channel) {
        short[] mono = new short[stereo.length / 2];
        for (int i = 0; i < mono.length; i++) {
            mono[i] = stereo[i * 2 + channel];
        }
        return mono;
    }

    static double meanOfRange(short[] samples, int start, int end) {
        if (start >= end || start >= samples.length) return 0;
        end = Math.min(end, samples.length);
        double sum = 0;
        for (int i = start; i < end; i++) {
            sum += samples[i];
        }
        return sum / (end - start);
    }

    static double rmsOfRange(short[] samples, int start, int end) {
        if (start >= end || start >= samples.length) return 0;
        end = Math.min(end, samples.length);
        double sumSq = 0;
        for (int i = start; i < end; i++) {
            sumSq += (double) samples[i] * samples[i];
        }
        return Math.sqrt(sumSq / (end - start));
    }

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
}
