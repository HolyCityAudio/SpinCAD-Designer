package com.holycityaudio.SpinCAD;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

/**
 * Discovers all SpinCADBlock subclasses in the CADBlocks package
 * by scanning both src/ and src-gen/ compiled classes.
 */
public class BlockDiscovery {

    private static final String PACKAGE = "com.holycityaudio.SpinCAD.CADBlocks.";

    // Blocks to skip in automated testing (abstract, base, or test-only)
    private static final List<String> SKIP_LIST = new ArrayList<>();
    static {
        SKIP_LIST.add("ControlCADBlock");      // abstract base for control blocks
        SKIP_LIST.add("ControlPanelTestCADBlock"); // GUI test only
        SKIP_LIST.add("InstructionTestCADBlock");  // manual test block
        SKIP_LIST.add("DelayRamTestCADBlock");     // manual test block
        // BassmanEQCADBlock now uses parallel 1st-order sections with coefficients in range
    }

    /**
     * Returns the fully qualified class names of all CADBlock implementations.
     */
    public static List<String> findAllBlockClassNames() {
        TreeSet<String> names = new TreeSet<>();

        // Scan src/ CADBlocks
        scanDirectory(new File("src/com/holycityaudio/SpinCAD/CADBlocks"), names);
        // Scan src-gen/ CADBlocks
        scanDirectory(new File("src-gen/com/holycityaudio/SpinCAD/CADBlocks"), names);

        List<String> result = new ArrayList<>();
        for (String name : names) {
            if (!SKIP_LIST.contains(name)) {
                result.add(PACKAGE + name);
            }
        }
        return result;
    }

    private static void scanDirectory(File dir, TreeSet<String> names) {
        if (!dir.exists() || !dir.isDirectory()) return;
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File f : files) {
            String fname = f.getName();
            if (fname.endsWith("CADBlock.java")
                    && !fname.equals("SpinCADBlock.java")
                    && !fname.contains("ControlPanel")) {
                names.add(fname.replace(".java", ""));
            }
        }
    }

    /**
     * Returns just the simple class names (without package).
     */
    public static List<String> findAllBlockSimpleNames() {
        List<String> fqns = findAllBlockClassNames();
        List<String> simple = new ArrayList<>();
        for (String fqn : fqns) {
            simple.add(fqn.substring(fqn.lastIndexOf('.') + 1));
        }
        return simple;
    }
}
