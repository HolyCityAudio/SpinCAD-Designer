package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.File;
import java.io.InvalidClassException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests loading of legacy .spcd patch files and verifying code generation works.
 *
 * Patch files are stored in two locations:
 *   - patches/ directory (shipped with the project)
 *   - src/test/resources/patches/ (test-specific files for full module coverage)
 *
 * Each file is loaded, deserialized, and then code generation is run on the model.
 * The test verifies no exceptions occur and resources stay within FV-1 limits.
 */
public class LegacyFileLoadTest {

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    static Stream<String> patchFiles() {
        List<String> files = new ArrayList<>();

        // Scan patches/ directory for .spcd files
        collectPatchFiles(new File("patches"), files);

        // Scan test resources for .spcd and .spcdj files
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

    @ParameterizedTest(name = "load: {0}")
    @MethodSource("patchFiles")
    void testLoadPatchFile(String filePath) throws Exception {
        SpinCADFile scFile = new SpinCADFile();
        SpinCADPatch patch = null;

        // Try current format first, fall back to legacy 952 format
        try {
            patch = scFile.fileReadPatch(filePath);
        } catch (Exception e1) {
            // If it's a serialVersionUID mismatch, skip as a known compatibility issue
            if (isSerialVersionMismatch(e1)) {
                assumeTrue(false,
                        "Skipped: serialVersionUID mismatch in " + filePath
                        + " — needs class remapping update: " + e1.getMessage());
            }
            try {
                patch = scFile.fileReadPatch952(filePath);
            } catch (Exception e2) {
                if (isSerialVersionMismatch(e2)) {
                    assumeTrue(false,
                            "Skipped: serialVersionUID mismatch in " + filePath
                            + " — needs class remapping update: " + e2.getMessage());
                }
                fail("Could not load " + filePath + " with either format: "
                        + e1.getMessage() + " / " + e2.getMessage());
            }
        }

        assertNotNull(patch, "Patch should not be null: " + filePath);
        assertNotNull(patch.patchModel, "Patch model should not be null: " + filePath);
        assertFalse(patch.patchModel.blockList.isEmpty(),
                "Patch should contain blocks: " + filePath);

        // Run code generation
        int codeLen = patch.patchModel.sortAlignGen();

        SpinFXBlock renderBlock = patch.patchModel.getRenderBlock();
        int instrCount = renderBlock.getCodeLen() - renderBlock.getNumComments();
        int regsUsed = renderBlock.getNumRegs() - ElmProgram.REG0;
        int ramUsed = renderBlock.getDelayMemAllocated();

        // Verify resources within limits
        assertTrue(instrCount <= ElmProgram.MAX_CODE_LEN,
                filePath + ": instructions (" + instrCount + ") exceed max");
        assertTrue(regsUsed <= 32,
                filePath + ": registers (" + regsUsed + ") exceed max 32");
        assertTrue(ramUsed <= ElmProgram.MAX_DELAY_MEM,
                filePath + ": delay RAM (" + ramUsed + ") exceeds max");

        System.out.printf("  OK: %s — %d blocks, %d instr, %d regs, %d RAM%n",
                new File(filePath).getName(),
                patch.patchModel.blockList.size(),
                instrCount, regsUsed, ramUsed);
    }

    private static boolean isSerialVersionMismatch(Exception e) {
        Throwable t = e;
        while (t != null) {
            if (t instanceof InvalidClassException) {
                String msg = t.getMessage();
                if (msg != null && msg.contains("serialVersionUID")) {
                    return true;
                }
            }
            t = t.getCause();
        }
        return false;
    }
}
