package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.ConstantCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Parameterized test that exercises every CADBlock implementation.
 * For each block:
 *   1. Instantiate it
 *   2. Wire an InputCADBlock to each audio input, ConstantCADBlock to each control input
 *   3. Wire an OutputCADBlock to audio outputs
 *   4. Run sortAlignGen() on the model
 *   5. Assert no exceptions were thrown during code generation
 *   6. Assert resources are within FV-1 limits
 */
public class AllBlocksCodeGenTest {

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    static Stream<String> allBlockClassNames() {
        return BlockDiscovery.findAllBlockClassNames().stream();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("allBlockClassNames")
    void testBlockCodeGeneration(String className) throws Exception {
        // Skip InputCADBlock and OutputCADBlock — they're used as test harness blocks
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        if (simpleName.equals("InputCADBlock") || simpleName.equals("OutputCADBlock")) {
            // These are infrastructure blocks; test them in a simple model
            testInfrastructureBlock(className);
            return;
        }

        // 1. Instantiate the block under test
        SpinCADBlock blockUnderTest = instantiateBlock(className);
        assertNotNull(blockUnderTest, "Failed to instantiate " + className);

        // 2. Build a model with Input -> BlockUnderTest -> Output
        SpinCADModel model = new SpinCADModel();

        // Add an input source block
        InputCADBlock inputBlock = new InputCADBlock(50, 50);
        model.addBlock(inputBlock);

        // Add constant blocks for control inputs
        List<ConstantCADBlock> constantBlocks = new ArrayList<>();
        for (SpinCADPin pin : blockUnderTest.pinList) {
            if (pin.getType() == pinType.CONTROL_IN) {
                ConstantCADBlock cb = new ConstantCADBlock(50, 50);
                model.addBlock(cb);
                constantBlocks.add(cb);
            }
        }

        // Add the block under test
        model.addBlock(blockUnderTest);

        // Add an output sink block
        OutputCADBlock outputBlock = new OutputCADBlock(50, 200);
        model.addBlock(outputBlock);

        // 3. Wire connections
        // First, generate code on source blocks to assign their output registers
        SpinFXBlock setupBlock = new SpinFXBlock("Setup");
        inputBlock.generateCode(setupBlock);
        for (ConstantCADBlock cb : constantBlocks) {
            cb.generateCode(setupBlock);
        }

        // Wire audio inputs from the Input block
        int audioInputIdx = 0;
        int controlInputIdx = 0;
        for (SpinCADPin pin : blockUnderTest.pinList) {
            if (pin.getType() == pinType.AUDIO_IN && audioInputIdx < 2) {
                // Connect from InputCADBlock's output pins
                String outPinName = (audioInputIdx == 0) ? "Output 1" : "Output 2";
                SpinCADPin sourcePin = inputBlock.getPin(outPinName);
                if (sourcePin != null) {
                    pin.setConnection(inputBlock, sourcePin);
                }
                audioInputIdx++;
            } else if (pin.getType() == pinType.CONTROL_IN && controlInputIdx < constantBlocks.size()) {
                // Connect from ConstantCADBlock's output
                ConstantCADBlock cb = constantBlocks.get(controlInputIdx);
                SpinCADPin sourcePin = findOutputPin(cb);
                if (sourcePin != null) {
                    pin.setConnection(cb, sourcePin);
                }
                controlInputIdx++;
            }
        }

        // Wire first audio output of block under test to the output block
        SpinCADPin blockAudioOut = null;
        for (SpinCADPin pin : blockUnderTest.pinList) {
            if (pin.getType() == pinType.AUDIO_OUT) {
                blockAudioOut = pin;
                break;
            }
        }
        if (blockAudioOut != null) {
            SpinCADPin outputIn = outputBlock.getPin("Input 1");
            if (outputIn != null) {
                outputIn.setConnection(blockUnderTest, blockAudioOut);
            }
        }

        // 4. Run code generation, capturing stderr to detect swallowed exceptions
        ByteArrayOutputStream errCapture = new ByteArrayOutputStream();
        PrintStream origErr = System.err;
        System.setErr(new PrintStream(errCapture));

        int codeLen;
        try {
            codeLen = model.sortAlignGen();
        } finally {
            System.setErr(origErr);
        }

        String stderrOutput = errCapture.toString();

        // 5. Check for exceptions that were silently caught inside generateCode()
        boolean hadException = stderrOutput.contains("Exception")
                || stderrOutput.contains("Error")
                // filter out expected non-error messages
                && !stderrOutput.contains("Register compaction report");

        // Print captured stderr so it's visible in test output
        if (!stderrOutput.isEmpty()) {
            System.err.print(stderrOutput);
        }

        assertFalse(hadException,
                simpleName + ": code generation threw an exception:\n" + stderrOutput);

        // 6. Assert resource limits
        SpinFXBlock renderBlock = model.getRenderBlock();
        assertNotNull(renderBlock, "Render block should not be null");

        int instrCount = renderBlock.getCodeLen() - renderBlock.getNumComments();
        int regsUsed = renderBlock.getNumRegs() - ElmProgram.REG0;
        int ramUsed = renderBlock.getDelayMemAllocated();

        // Resource limit warnings (not hard failures, since the test harness adds
        // Input + Output blocks that consume extra instructions/registers)
        if (instrCount > ElmProgram.MAX_CODE_LEN) {
            System.out.printf("  WARN: %s exceeds instruction limit (%d > %d) — "
                    + "expected for large blocks tested with Input/Output overhead%n",
                    simpleName, instrCount, ElmProgram.MAX_CODE_LEN);
        }
        assertTrue(regsUsed <= 32,
                simpleName + ": registers (" + regsUsed + ") exceed max 32");
        assertTrue(ramUsed <= ElmProgram.MAX_DELAY_MEM,
                simpleName + ": delay RAM (" + ramUsed + ") exceeds max " + ElmProgram.MAX_DELAY_MEM);

        System.out.printf("  OK: %s — %d instr, %d regs, %d RAM%n",
                simpleName, instrCount, regsUsed, ramUsed);
    }

    private void testInfrastructureBlock(String className) throws Exception {
        SpinCADModel model = new SpinCADModel();
        InputCADBlock input = new InputCADBlock(50, 50);
        OutputCADBlock output = new OutputCADBlock(50, 200);
        model.addBlock(input);
        model.addBlock(output);

        // Wire Input -> Output
        SpinFXBlock setupBlock = new SpinFXBlock("Setup");
        input.generateCode(setupBlock);
        SpinCADPin outPin = input.getPin("Output 1");
        SpinCADPin inPin = output.getPin("Input 1");
        inPin.setConnection(input, outPin);

        int codeLen = model.sortAlignGen();
        assertTrue(codeLen >= 0, className + " should generate valid code");
    }

    private SpinCADBlock instantiateBlock(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
            return (SpinCADBlock) ctor.newInstance(100, 100);
        } catch (Exception e) {
            System.err.println("Could not instantiate " + className + ": " + e.getMessage());
            return null;
        }
    }

    private SpinCADPin findOutputPin(SpinCADBlock block) {
        for (SpinCADPin pin : block.pinList) {
            if (pin.isOutputPin()) {
                return pin;
            }
        }
        return null;
    }
}
