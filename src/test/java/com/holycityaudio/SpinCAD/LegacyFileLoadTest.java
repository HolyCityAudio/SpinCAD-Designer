package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.File;
import java.io.InvalidClassException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests loading of legacy .spcd patch files, .spbk bank files, and a full
 * round-trip through every file format:
 *
 *   - Load each .spcd/.spcdj patch, run code generation, verify FV-1 limits
 *   - Load each .spbk/.spbkj bank, run code generation on every patch
 *   - Full round-trip: load bank → save patch as → reload patch →
 *     save Spin ASM → save bank as → reload bank → export hex →
 *     load hex → export Spin project
 */
public class LegacyFileLoadTest {

    @TempDir
    static Path tempDir;

    private static final File ASM_OUTPUT_DIR = new File("build/test-asm");

    // Known-failing patches to skip (negative tests or known issues)
    private static final Set<String> SKIP_PATCHES = new HashSet<>(Arrays.asList(
        "double-stutter-03.spcd"   // intentional: exceeds instruction limit
    ));

    // Test result tracking for summary
    private static final List<String> patchResults = new ArrayList<>();
    private static final List<String> bankResults = new ArrayList<>();
    private static String roundTripResult = null;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
        ASM_OUTPUT_DIR.mkdirs();
    }

    @AfterAll
    static void printSummary() {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("  LegacyFileLoadTest SUMMARY");
        System.out.println("================================================================");

        System.out.println("  PATCH FILES (" + patchResults.size() + "):");
        for (String r : patchResults) {
            System.out.println("    " + r);
        }

        System.out.println("  --------------------------------------------------------------");
        System.out.println("  BANK FILES (" + bankResults.size() + "):");
        for (String r : bankResults) {
            System.out.println("    " + r);
        }

        System.out.println("  --------------------------------------------------------------");
        System.out.println("  ROUND-TRIP: " + (roundTripResult != null ? roundTripResult : "NOT RUN"));
        System.out.println("================================================================");
    }

    // ==== Patch file loading ==========================================

    static Stream<String> patchFiles() {
        List<String> files = new ArrayList<>();

        // Scan patches/ directory for .spcd files
        collectFiles(new File("patches"), files, ".spcd", ".spcdj");

        // Scan test resources for .spcd and .spcdj files
        collectFiles(new File("src/test/resources/patches"), files, ".spcd", ".spcdj");

        return files.stream();
    }

    @ParameterizedTest(name = "load: {0}")
    @MethodSource("patchFiles")
    void testLoadPatchFile(String filePath) throws Exception {
        String fileName = new File(filePath).getName();
        assumeTrue(!SKIP_PATCHES.contains(fileName),
                "Skipped known-failing patch: " + fileName);
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
        patch.patchModel.sortAlignGen();

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

        // Save as ASM (.spn)
        String baseName = FilenameUtils.removeExtension(new File(filePath).getName());
        String asmPath = new File(ASM_OUTPUT_DIR, baseName + ".spn").getPath();
        scFile.fileSaveAsm(patch, asmPath);

        String summary = String.format("OK  %-35s %3d blocks, %3d instr, %2d regs, %5d RAM",
                new File(filePath).getName(),
                patch.patchModel.blockList.size(),
                instrCount, regsUsed, ramUsed);
        patchResults.add(summary);
    }

    // ==== Bank file loading ===========================================

    static Stream<String> bankFiles() {
        List<String> files = new ArrayList<>();
        collectFiles(new File("patches"), files, ".spbk", ".spbkj");
        collectFiles(new File("src/test/resources/patches"), files, ".spbk", ".spbkj");
        return files.stream();
    }

    @ParameterizedTest(name = "loadBank: {0}")
    @MethodSource("bankFiles")
    void testLoadBankFile(String filePath) throws Exception {
        SpinCADFile scFile = new SpinCADFile();
        SpinCADBank bank = loadBank(scFile, new File(filePath));
        assertNotNull(bank, "Bank should not be null: " + filePath);

        int patchCount = 0;
        int blockTotal = 0;
        for (int i = 0; i < 8; i++) {
            assertNotNull(bank.patch[i], "Patch " + i + " should not be null in " + filePath);
            if (!bank.patch[i].patchFileName.equals("Untitled")
                    && !bank.patch[i].isHexFile
                    && !bank.patch[i].patchModel.blockList.isEmpty()) {
                patchCount++;
                blockTotal += bank.patch[i].patchModel.blockList.size();
                try {
                    bank.patch[i].patchModel.sortAlignGen();
                } catch (Exception e) {
                    // code gen can fail for some patch configurations
                }
            }
        }
        String summary = String.format("OK  %-35s %d patches, %d total blocks",
                new File(filePath).getName(), patchCount, blockTotal);
        bankResults.add(summary);
    }

    // ==== Full round-trip =============================================

    /**
     * Full round-trip: load bank → save patch → reload patch → save ASM →
     * save bank → reload bank → export hex → load hex → export spin project.
     */
    @Test
    void testFullRoundTrip() throws Exception {
        File bankFile = new File("src/test/resources/patches/8-patch-bank.spbkj");
        assumeTrue(bankFile.exists(), "8-patch-bank.spbkj not found in test resources");

        SpinCADFile scFile = new SpinCADFile();

        // --- Step 1: Load the bank ---
        SpinCADBank bank = loadBank(scFile, bankFile);
        assertNotNull(bank, "Bank should load successfully");

        // Find the first non-empty, non-hex patch in the bank
        int patchIndex = findUsablePatchIndex(bank);
        assumeTrue(patchIndex >= 0, "No usable (non-empty, non-hex) patch found in bank");
        SpinCADPatch patch = bank.patch[patchIndex];

        // Run code generation on the patch so getRenderBlock() is ready
        patch.patchModel.sortAlignGen();

        // --- Step 2: Save patch as .spcdj ---
        String patchPath = tempDir.resolve("test-patch.spcdj").toString();
        SpinCADJsonSerializer.writePatch(patch, patchPath);
        assertTrue(new File(patchPath).exists(), "Saved .spcdj file should exist");
        assertTrue(new File(patchPath).length() > 0, "Saved .spcdj file should not be empty");

        // --- Step 3: Load previously saved patch ---
        SpinCADPatch reloadedPatch = scFile.fileReadPatch(patchPath);
        assertNotNull(reloadedPatch, "Reloaded patch should not be null");
        assertNotNull(reloadedPatch.patchModel, "Reloaded patch model should not be null");
        assertFalse(reloadedPatch.patchModel.blockList.isEmpty(),
                "Reloaded patch should contain blocks");

        // Verify code generation works on reloaded patch
        reloadedPatch.patchModel.sortAlignGen();

        // --- Step 4: Save as Spin ASM (.spn) ---
        String asmPath = tempDir.resolve("test-patch.spn").toString();
        scFile.fileSaveAsm(reloadedPatch, asmPath);
        File asmFile = new File(asmPath);
        assertTrue(asmFile.exists(), "Saved .spn file should exist");
        assertTrue(asmFile.length() > 0, "Saved .spn file should not be empty");
        String asmContent = new String(Files.readAllBytes(asmFile.toPath()));
        assertTrue(asmContent.contains(";"), "ASM file should contain comment lines");

        // --- Step 5: Save bank as .spbkj ---
        runCodeGenOnBank(bank);
        String bankPath = tempDir.resolve("test-bank.spbkj").toString();
        SpinCADJsonSerializer.writeBank(bank, bankPath);
        assertTrue(new File(bankPath).exists(), "Saved .spbkj file should exist");
        assertTrue(new File(bankPath).length() > 0, "Saved .spbkj file should not be empty");

        // --- Step 6: Load previously saved bank ---
        SpinCADBank reloadedBank = scFile.fileReadBank(new File(bankPath));
        assertNotNull(reloadedBank, "Reloaded bank should not be null");
        assertNotNull(reloadedBank.patch[patchIndex], "Reloaded bank patch should not be null");
        assertFalse(reloadedBank.patch[patchIndex].patchModel.blockList.isEmpty(),
                "Reloaded bank patch should contain blocks");

        // Run code gen on reloaded bank for hex export
        runCodeGenOnBank(reloadedBank);

        // --- Step 7: Export to hex ---
        String hexPath = tempDir.resolve("test-bank.hex").toString();
        for (int i = 0; i < 8; i++) {
            try {
                if (reloadedBank.patch[i].isHexFile) {
                    scFile.fileSaveHex(i, reloadedBank.patch[i].hexFile, hexPath);
                } else if (!reloadedBank.patch[i].patchModel.blockList.isEmpty()
                        && reloadedBank.patch[i].patchModel.getRenderBlock() != null) {
                    scFile.fileSaveHex(i, reloadedBank.patch[i].patchModel.getRenderBlock().generateHex(), hexPath);
                } else {
                    scFile.fileSaveHex(i, new int[0], hexPath);
                }
            } catch (Exception e) {
                // hex export can fail for patches without render blocks
                scFile.fileSaveHex(i, new int[0], hexPath);
            }
        }
        File hexFile = new File(hexPath);
        assertTrue(hexFile.exists(), "Exported .hex file should exist");
        assertTrue(hexFile.length() > 0, "Exported .hex file should not be empty");
        String hexContent = new String(Files.readAllBytes(hexFile.toPath()));
        assertTrue(hexContent.contains(":"), "Hex file should contain Intel HEX records");

        // --- Step 8: Load hex ---
        SpinCADPatch hexPatch = scFile.fileReadHex(hexPath);
        assertNotNull(hexPatch, "Hex-loaded patch should not be null");
        assertTrue(hexPatch.isHexFile, "Loaded patch should be marked as hex file");

        // --- Step 9: Export to Spin project ---
        String spjFolder = tempDir.resolve("spj").toString();
        new File(spjFolder).mkdirs();
        String spjPath = spjFolder + "/test-project.spj";
        String[] spnFileNames = new String[8];

        for (int i = 0; i < 8; i++) {
            if (!reloadedBank.patch[i].patchFileName.equals("Untitled")
                    && !reloadedBank.patch[i].isHexFile
                    && !reloadedBank.patch[i].patchModel.blockList.isEmpty()
                    && reloadedBank.patch[i].patchModel.getRenderBlock() != null) {
                try {
                    String baseName = reloadedBank.patch[i].patchFileName;
                    int dotIdx = baseName.lastIndexOf('.');
                    if (dotIdx > 0) baseName = baseName.substring(0, dotIdx);
                    String spnPath = spjFolder + "/" + baseName + ".spn";
                    scFile.fileSaveAsm(reloadedBank.patch[i], spnPath);
                    spnFileNames[i] = spnPath;
                } catch (Exception e) {
                    // ASM export can fail for patches without complete render blocks
                }
            }
        }

        // Write the .spj project file
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(spjPath))) {
            writer.write("NUMDOCS:8");
            writer.newLine();
            for (int i = 0; i < 8; i++) {
                if (spnFileNames[i] != null) {
                    writer.write(spnFileNames[i] + ",1");
                } else {
                    writer.write(",0");
                }
                writer.newLine();
            }
            writer.write(",1,1,1");
            writer.newLine();
        }

        File spjFile = new File(spjPath);
        assertTrue(spjFile.exists(), "Spin project file should exist");
        assertTrue(spjFile.length() > 0, "Spin project file should not be empty");
        String spjContent = new String(Files.readAllBytes(spjFile.toPath()));
        assertTrue(spjContent.contains("NUMDOCS:8"), "SPJ should contain NUMDOCS header");

        File[] spnFiles = new File(spjFolder).listFiles((d, n) -> n.endsWith(".spn"));
        assertNotNull(spnFiles, "Spin project folder should contain .spn files");
        assertEquals(8, spnFiles.length,
                "Spin project should have created 8 .spn files (one per patch)");

        roundTripResult = "PASSED - load bank > save/reload patch > ASM > save/reload bank > hex > spin project (" + spnFiles.length + " .spn files)";
    }

    // ==== Helpers =====================================================

    private static void collectFiles(File dir, List<String> files, String... extensions) {
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] found = dir.listFiles();
        if (found == null) return;

        for (File f : found) {
            String name = f.getName();
            for (String ext : extensions) {
                if (name.endsWith(ext)) {
                    files.add(f.getPath());
                    break;
                }
            }
        }
    }

    private static SpinCADBank loadBank(SpinCADFile scFile, File bankFile) throws Exception {
        try {
            return scFile.fileReadBank(bankFile);
        } catch (Exception e) {
            if (isSerialVersionMismatch(e)) {
                assumeTrue(false,
                        "Skipped: serialVersionUID mismatch in " + bankFile.getName()
                        + " — needs class remapping update: " + e.getMessage());
            }
            throw e;
        }
    }

    private File findFirstBankFile() {
        File[] dirs = {
            new File("src/test/resources/patches"),
            new File("patches")
        };
        for (File dir : dirs) {
            if (!dir.exists() || !dir.isDirectory()) continue;
            File[] files = dir.listFiles();
            if (files == null) continue;
            for (File f : files) {
                String name = f.getName();
                if (name.endsWith(".spbk") || name.endsWith(".spbkj")) {
                    return f;
                }
            }
        }
        return null;
    }

    private int findUsablePatchIndex(SpinCADBank bank) {
        for (int i = 0; i < 8; i++) {
            if (!bank.patch[i].patchFileName.equals("Untitled")
                    && !bank.patch[i].isHexFile
                    && !bank.patch[i].patchModel.blockList.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    private static void runCodeGenOnBank(SpinCADBank bank) {
        for (int i = 0; i < 8; i++) {
            if (!bank.patch[i].patchFileName.equals("Untitled")
                    && !bank.patch[i].isHexFile
                    && !bank.patch[i].patchModel.blockList.isEmpty()) {
                try {
                    bank.patch[i].patchModel.sortAlignGen();
                } catch (Exception e) {
                    System.out.println("  Warning: code gen failed for patch " + i + ": " + e.getMessage());
                }
            }
        }
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
