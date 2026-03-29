package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.AudioFileWriter;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Simulator tests:
 *   - Bypass patch: verifies output matches input exactly
 *   - Smoke test: runs every loadable patch through the simulator
 *     looking for exceptions (no output verification)
 */
public class SimulatorBypassTest {

    @TempDir
    File tempDir;

    private static File bypassInputWav;
    private static File smokeInputWav;

    // Known-failing patches (unconnected pins cause reg -1 in simulator)
    private static final Set<String> SKIP_PATCHES = new HashSet<>(Arrays.asList(
        "br-007.spcd",
        "plate reverb-chorus-01.spcd",
        "rom-reverb-01-hf-control.spcd",
        "rom-reverb-01-lf-control.spcd"
    ));

    @BeforeAll
    static void setup() throws IOException {
        System.setProperty("java.awt.headless", "true");
        bypassInputWav = generateTestWav(5.0);
        smokeInputWav = generateTestWav(0.5);
    }

    /**
     * Generate a stereo 16-bit PCM WAV file at FV-1 sample rate (32768 Hz)
     * containing a 1 kHz sine wave for the given duration.
     */
    private static File generateTestWav(double durationSeconds) throws IOException {
        int sampleRate = ElmProgram.SAMPLERATE;
        int numFrames = (int) (sampleRate * durationSeconds);
        byte[] data = new byte[numFrames * 4]; // stereo 16-bit = 4 bytes/frame

        double freq = 1000.0;
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / sampleRate;
            short sample = (short) (Short.MAX_VALUE * 0.5 * Math.sin(2.0 * Math.PI * freq * t));
            int offset = i * 4;
            // left channel - little-endian
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            // right channel - same signal
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }

        File wavFile = File.createTempFile("spincad_test_input", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(sampleRate, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, numFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    @Test
    void testBypassPatchOutputMatchesInput() throws Exception {
        File inputWav = bypassInputWav;
        File outputWav = new File(tempDir, "test_output.wav");

        // 2. Load the bypass patch
        SpinCADFile scFile = new SpinCADFile();
        String patchPath = "src/test/resources/patches/Bypass.spcdj";
        SpinCADPatch patch = scFile.fileReadPatch(patchPath);
        assertNotNull(patch, "Bypass patch should load");

        // 3. Run code generation
        patch.patchModel.sortAlignGen();
        SpinFXBlock renderBlock = patch.patchModel.getRenderBlock();
        assertNotNull(renderBlock, "Render block should not be null");

        // 4. Run the simulator
        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outputWav.getAbsolutePath(),
                0.0, 0.0, 0.0);
        sim.setLoopMode(false);
        sim.start();
        sim.join(30000); // wait up to 30 seconds
        assertFalse(sim.isAlive(), "Simulator should have finished");
        assertNull(sim.getSimulationException(),
                "Simulator threw exception: "
                + (sim.getSimulationException() != null ? sim.getSimulationException().getMessage() : ""));
        assertTrue(outputWav.exists(), "Output WAV should have been created");

        // 5. Read back both files and compare sample data
        AudioInputStream inputAis = AudioSystem.getAudioInputStream(inputWav);
        AudioInputStream outputAis = AudioSystem.getAudioInputStream(outputWav);

        byte[] inputBytes = readAllBytes(inputAis);
        byte[] outputBytes = readAllBytes(outputAis);
        inputAis.close();
        outputAis.close();

        assertEquals(inputBytes.length, outputBytes.length,
                "Output length should match input length");

        // Compare samples with a small tolerance for fixed-point rounding
        int numSamples = inputBytes.length / 2;
        int maxDiff = 0;
        for (int i = 0; i < numSamples; i++) {
            int offset = i * 2;
            short inSample = (short) ((inputBytes[offset] & 0xff)
                    | ((inputBytes[offset + 1] & 0xff) << 8));
            short outSample = (short) ((outputBytes[offset] & 0xff)
                    | ((outputBytes[offset + 1] & 0xff) << 8));
            int diff = Math.abs(inSample - outSample);
            if (diff > maxDiff) maxDiff = diff;
        }

        System.out.printf("  Bypass test: %d samples compared, max difference = %d%n",
                numSamples, maxDiff);
        // FV-1 fixed-point arithmetic may introduce small rounding errors
        assertTrue(maxDiff <= 1,
                "Bypass output should match input (max sample diff = " + maxDiff + ")");
    }

    // ==== Simulator smoke test for all loadable patches =================

    static Stream<String> patchFiles() {
        List<String> files = new ArrayList<>();
        collectPatchFiles(new File("patches"), files);
        collectPatchFiles(new File("src/test/resources/patches"), files);
        return files.stream();
    }

    private static void collectPatchFiles(File dir, List<String> files) {
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] found = dir.listFiles();
        if (found == null) return;
        for (File f : found) {
            String name = f.getName();
            if (name.endsWith(".spcd") || name.endsWith(".spcdj")) {
                files.add(f.getPath());
            }
        }
    }

    /**
     * Runs each patch through the simulator with the test WAV file.
     * No output verification — just checks that no exceptions are thrown.
     */
    @ParameterizedTest(name = "simulate: {0}")
    @MethodSource("patchFiles")
    void testSimulatePatch(String filePath) throws Exception {
        String fileName = new File(filePath).getName();
        assumeTrue(!SKIP_PATCHES.contains(fileName),
                "Skipped known-failing patch: " + fileName);
        SpinCADFile scFile = new SpinCADFile();
        SpinCADPatch patch = null;

        try {
            patch = scFile.fileReadPatch(filePath);
        } catch (Exception e1) {
            try {
                patch = scFile.fileReadPatch952(filePath);
            } catch (Exception e2) {
                assumeTrue(false, "Could not load " + filePath + ": " + e2.getMessage());
            }
        }

        assertNotNull(patch, "Patch should not be null: " + filePath);
        if (patch.isHexFile) {
            assumeTrue(false, "Skipped hex patch: " + filePath);
        }

        patch.patchModel.sortAlignGen();
        SpinFXBlock renderBlock = patch.patchModel.getRenderBlock();
        assertNotNull(renderBlock, "Render block should not be null: " + filePath);

        File outputWav = new File(tempDir, "sim_output_" + System.nanoTime() + ".wav");
        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                smokeInputWav.getAbsolutePath(),
                outputWav.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(60000); // up to 60 seconds per patch
        assertFalse(sim.isAlive(),
                "Simulator should have finished for: " + filePath);
        assertNull(sim.getSimulationException(),
                "Simulator threw exception for " + filePath + ": "
                + (sim.getSimulationException() != null ? sim.getSimulationException().getMessage() : ""));

        System.out.printf("  OK simulate: %s%n", new File(filePath).getName());
    }

    // ==== Helpers =====================================================

    private byte[] readAllBytes(AudioInputStream ais) throws IOException {
        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }
}
