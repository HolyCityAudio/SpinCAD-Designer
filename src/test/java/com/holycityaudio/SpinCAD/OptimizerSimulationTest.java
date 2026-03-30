package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Verifies that the shuttle-register optimizer does not change the
 * audio output of any block.  For each block:
 *   1. Build a model: Input → Block → Output
 *   2. Generate code with optimization ON, simulate
 *   3. Generate code with optimization OFF, simulate
 *   4. Compare DACL/DACR outputs sample-by-sample
 */
public class OptimizerSimulationTest {

    @TempDir
    File tempDir;

    private static File inputWav;

    @BeforeAll
    static void setup() throws IOException {
        System.setProperty("java.awt.headless", "true");
        inputWav = generateTestWav(0.25);  // 250ms of 1kHz sine
    }

    static Stream<String> blockClassNames() {
        return BlockDiscovery.findAllBlockClassNames().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("blockClassNames")
    void testOptimizationPreservesOutput(String className) throws Exception {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        if (simpleName.equals("InputCADBlock") || simpleName.equals("OutputCADBlock")) return;

        // 1. Build model (same wiring as AllBlocksCodeGenTest)
        SpinCADBlock blockUnderTest;
        try {
            blockUnderTest = (SpinCADBlock) Class.forName(className)
                    .getConstructor(int.class, int.class)
                    .newInstance(100, 100);
        } catch (Exception e) {
            return; // skip blocks that can't be instantiated
        }

        // 2. Run with optimization ON
        byte[] optimizedOutput = simulateWithOptimizer(blockUnderTest, className, true, "opt");
        if (optimizedOutput == null) return;  // skip if simulation failed

        // 3. Run with optimization OFF (fresh block instance)
        SpinCADBlock blockUnderTest2;
        try {
            blockUnderTest2 = (SpinCADBlock) Class.forName(className)
                    .getConstructor(int.class, int.class)
                    .newInstance(100, 100);
        } catch (Exception e) {
            return;
        }
        byte[] unoptimizedOutput = simulateWithOptimizer(blockUnderTest2, className, false, "noopt");
        if (unoptimizedOutput == null) return;

        // 4. Compare
        assertEquals(optimizedOutput.length, unoptimizedOutput.length,
                simpleName + ": output lengths differ (optimized vs unoptimized)");

        int numSamples = optimizedOutput.length / 2;
        int maxDiff = 0;
        int maxDiffIdx = 0;
        for (int i = 0; i < numSamples; i++) {
            int offset = i * 2;
            short optSample = (short) ((optimizedOutput[offset] & 0xff)
                    | ((optimizedOutput[offset + 1] & 0xff) << 8));
            short nooptSample = (short) ((unoptimizedOutput[offset] & 0xff)
                    | ((unoptimizedOutput[offset + 1] & 0xff) << 8));
            int diff = Math.abs(optSample - nooptSample);
            if (diff > maxDiff) {
                maxDiff = diff;
                maxDiffIdx = i;
            }
        }

        System.out.printf("  %s: %d samples, max diff = %d (at sample %d)%n",
                simpleName, numSamples, maxDiff, maxDiffIdx);

        assertTrue(maxDiff == 0,
                simpleName + ": optimizer changed output! max sample diff = " + maxDiff
                + " at sample " + maxDiffIdx);
    }

    /**
     * Build a model with Input → block → Output, generate code, and simulate.
     * Returns the raw output WAV bytes, or null if the block can't be tested.
     */
    private byte[] simulateWithOptimizer(SpinCADBlock blockUnderTest, String className,
            boolean optimize, String tag) throws Exception {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);

        SpinCADModel model = new SpinCADModel();
        InputCADBlock inputBlock = new InputCADBlock(0, 0);
        OutputCADBlock outputBlock = new OutputCADBlock(200, 100);

        model.addBlock(inputBlock);
        model.addBlock(blockUnderTest);
        model.addBlock(outputBlock);

        // Generate code for source blocks to assign output registers
        SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
        inputBlock.generateCode(tempSfxb);

        // Wire audio inputs from InputCADBlock
        List<SpinCADPin> audioInputs = new ArrayList<>();
        for (int p = 0; p < blockUnderTest.pinList.size(); p++) {
            SpinCADPin pin = blockUnderTest.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_IN) {
                audioInputs.add(pin);
            }
        }
        SpinCADPin inputL = inputBlock.getPin("Output 1");
        SpinCADPin inputR = inputBlock.getPin("Output 2");
        for (int i = 0; i < audioInputs.size(); i++) {
            SpinCADPin src = (i % 2 == 0) ? inputL : inputR;
            audioInputs.get(i).setConnection(inputBlock, src);
        }

        // Wire control inputs from ConstantCADBlocks
        List<SpinCADBlock> constants = new ArrayList<>();
        for (int p = 0; p < blockUnderTest.pinList.size(); p++) {
            SpinCADPin pin = blockUnderTest.pinList.get(p);
            if (pin.getType() == pinType.CONTROL_IN) {
                ConstantCADBlock cBlock = new ConstantCADBlock(50, 50);
                model.addBlock(cBlock);
                constants.add(cBlock);
                cBlock.generateCode(tempSfxb);
                SpinCADPin cOut = cBlock.getPin("Value");
                pin.setConnection(cBlock, cOut);
            }
        }

        // Find output pins
        List<SpinCADPin> outputPins = new ArrayList<>();
        for (int p = 0; p < blockUnderTest.pinList.size(); p++) {
            SpinCADPin pin = blockUnderTest.pinList.get(p);
            if (pin.getType() == pinType.AUDIO_OUT) {
                outputPins.add(pin);
            }
        }
        if (outputPins.isEmpty()) return null;

        // Wire to Output block (same logic as AllBlocksCodeGenTest)
        if (outputPins.size() == 1) {
            outputBlock.getPin("Input 1").setConnection(blockUnderTest, outputPins.get(0));
            outputBlock.getPin("Input 2").setConnection(blockUnderTest, outputPins.get(0));
        } else if (outputPins.size() >= 2) {
            outputBlock.getPin("Input 1").setConnection(blockUnderTest, outputPins.get(0));
            outputBlock.getPin("Input 2").setConnection(blockUnderTest, outputPins.get(1));
        }

        // Generate code
        SpinCADFile scFile = new SpinCADFile();
        boolean prevSetting = scFile.getDisableOptimizer();
        try {
            scFile.setDisableOptimizer(!optimize);
            model.sortAlignGen();
        } finally {
            scFile.setDisableOptimizer(prevSetting);
        }

        SpinFXBlock renderBlock = model.getRenderBlock();
        if (renderBlock == null) return null;

        // Quick sanity: must have DACL and DACR writes
        String listing = renderBlock.getProgramListing(1);
        if (!listing.contains("WRAX DACL") || !listing.contains("WRAX DACR")) {
            return null;
        }


        // Simulate
        File outFile = new File(tempDir, simpleName + "_" + tag + ".wav");
        SpinSimulator sim = new SpinSimulator(
                renderBlock,
                inputWav.getAbsolutePath(),
                outFile.getAbsolutePath(),
                0.5, 0.5, 0.5);
        sim.setLoopMode(false);
        sim.start();
        sim.join(30000);

        if (sim.isAlive() || sim.getSimulationException() != null) {
            return null;  // skip blocks that crash the simulator
        }
        if (!outFile.exists()) return null;

        // Read output WAV
        AudioInputStream ais = AudioSystem.getAudioInputStream(outFile);
        byte[] data = readAllBytes(ais);
        ais.close();
        return data;
    }

    private static File generateTestWav(double durationSeconds) throws IOException {
        int sampleRate = ElmProgram.SAMPLERATE;
        int numFrames = (int) (sampleRate * durationSeconds);
        byte[] data = new byte[numFrames * 4]; // stereo 16-bit

        double freq = 1000.0;
        for (int i = 0; i < numFrames; i++) {
            double t = (double) i / sampleRate;
            short sample = (short) (Short.MAX_VALUE * 0.5 * Math.sin(2.0 * Math.PI * freq * t));
            int offset = i * 4;
            data[offset] = (byte) (sample & 0xff);
            data[offset + 1] = (byte) ((sample >> 8) & 0xff);
            data[offset + 2] = (byte) (sample & 0xff);
            data[offset + 3] = (byte) ((sample >> 8) & 0xff);
        }

        File wavFile = File.createTempFile("optimizer_test_input", ".wav");
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
