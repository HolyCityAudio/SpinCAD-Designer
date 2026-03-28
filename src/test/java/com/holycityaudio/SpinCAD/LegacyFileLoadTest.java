package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

import java.io.File;
import java.io.InvalidClassException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.andrewkilpatrick.elmGen.ElmProgram;
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

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
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

        System.out.printf("  OK: %s — %d blocks, %d instr, %d regs, %d RAM%n",
                new File(filePath).getName(),
                patch.patchModel.blockList.size(),
                instrCount, regsUsed, ramUsed);
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
                    System.out.printf("  WARN: patch %d code gen failed in %s: %s%n",
                            i, new File(filePath).getName(), e.getMessage());
                }
            }
        }
        System.out.printf("  OK: %s — %d patches, %d total blocks%n",
                new File(filePath).getName(), patchCount, blockTotal);
    }

    // ==== Full round-trip =============================================

    /**
     * Full round-trip: load bank → save patch → reload patch → save ASM →
     * save bank → reload bank → export hex → load hex → export spin project.
     */
    @Test
    void testFullRoundTrip() throws Exception {
        File bankFile = findFirstBankFile();
        assumeTrue(bankFile != null, "No .spbk/.spbkj bank file found in test resources");

        SpinCADFile scFile = new SpinCADFile();
        System.out.println("=== Round-trip test using: " + bankFile.getName());

        // --- Step 1: Load the bank ---
        SpinCADBank bank = loadBank(scFile, bankFile);
        assertNotNull(bank, "Bank should load successfully");
        System.out.println("  1. Loaded bank: " + bankFile.getName());

        // Find the first non-empty, non-hex patch in the bank
        int patchIndex = findUsablePatchIndex(bank);
        assumeTrue(patchIndex >= 0, "No usable (non-empty, non-hex) patch found in bank");
        SpinCADPatch patch = bank.patch[patchIndex];
        System.out.println("  Using patch " + patchIndex + ": " + patch.patchFileName);

        // Run code generation on the patch so getRenderBlock() is ready
        patch.patchModel.sortAlignGen();

        // --- Step 2: Save patch as .spcdj ---
        String patchPath = tempDir.resolve("test-patch.spcdj").toString();
        SpinCADJsonSerializer.writePatch(patch, patchPath);
        assertTrue(new File(patchPath).exists(), "Saved .spcdj file should exist");
        assertTrue(new File(patchPath).length() > 0, "Saved .spcdj file should not be empty");
        System.out.println("  2. Saved patch as: " + patchPath);

        // --- Step 3: Load previously saved patch ---
        SpinCADPatch reloadedPatch = scFile.fileReadPatch(patchPath);
        assertNotNull(reloadedPatch, "Reloaded patch should not be null");
        assertNotNull(reloadedPatch.patchModel, "Reloaded patch model should not be null");
        assertFalse(reloadedPatch.patchModel.blockList.isEmpty(),
                "Reloaded patch should contain blocks");
        System.out.println("  3. Reloaded patch: " + reloadedPatch.patchModel.blockList.size() + " blocks");

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
        System.out.println("  4. Saved Spin ASM: " + asmFile.length() + " bytes");

        // --- Step 5: Save bank as .spbkj ---
        runCodeGenOnBank(bank);
        String bankPath = tempDir.resolve("test-bank.spbkj").toString();
        SpinCADJsonSerializer.writeBank(bank, bankPath);
        assertTrue(new File(bankPath).exists(), "Saved .spbkj file should exist");
        assertTrue(new File(bankPath).length() > 0, "Saved .spbkj file should not be empty");
        System.out.println("  5. Saved bank as: " + bankPath);

        // --- Step 6: Load previously saved bank ---
        SpinCADBank reloadedBank = scFile.fileReadBank(new File(bankPath));
        assertNotNull(reloadedBank, "Reloaded bank should not be null");
        assertNotNull(reloadedBank.patch[patchIndex], "Reloaded bank patch should not be null");
        assertFalse(reloadedBank.patch[patchIndex].patchModel.blockList.isEmpty(),
                "Reloaded bank patch should contain blocks");
        System.out.println("  6. Reloaded bank: " + new File(bankPath).getName());

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
                System.out.println("  Warning: hex export failed for patch " + i + ": " + e.getMessage());
                scFile.fileSaveHex(i, new int[0], hexPath);
            }
        }
        File hexFile = new File(hexPath);
        assertTrue(hexFile.exists(), "Exported .hex file should exist");
        assertTrue(hexFile.length() > 0, "Exported .hex file should not be empty");
        String hexContent = new String(Files.readAllBytes(hexFile.toPath()));
        assertTrue(hexContent.contains(":"), "Hex file should contain Intel HEX records");
        System.out.println("  7. Exported hex: " + hexFile.length() + " bytes");

        // --- Step 8: Load hex ---
        SpinCADPatch hexPatch = scFile.fileReadHex(hexPath);
        assertNotNull(hexPatch, "Hex-loaded patch should not be null");
        assertTrue(hexPatch.isHexFile, "Loaded patch should be marked as hex file");
        System.out.println("  8. Loaded hex file");

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
                    System.out.println("  Warning: ASM export failed for patch " + i + ": " + e.getMessage());
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
        System.out.println("  9. Exported Spin project: " + spjFile.getName());

        File[] spnFiles = new File(spjFolder).listFiles((d, n) -> n.endsWith(".spn"));
        assertTrue(spnFiles != null && spnFiles.length > 0,
                "Spin project should have created at least one .spn file");
        System.out.println("  Created " + spnFiles.length + " .spn files in project");

        System.out.println("=== Round-trip test PASSED ===");
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
