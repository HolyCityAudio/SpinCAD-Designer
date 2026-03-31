package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Parameterized GUI test that exercises every CADBlock in a single pass:
 *   1. Open and close the control panel 3 times
 *   2. Copy / paste / delete of the pasted copy
 *
 * Run via: ./gradlew guiTest
 */
@Tag("gui")
public class AllBlocksGUITest {

    private SpinCADFrame frame;
    private boolean savedAutoReload;

    static Stream<String> allBlockClassNames() {
        return BlockDiscovery.findAllBlockClassNames().stream();
    }

    @BeforeEach
    void setUp() throws Exception {
        // Disable auto-reload of last file to avoid loading a large patch on every test
        SpinCADFile scf = new SpinCADFile();
        savedAutoReload = scf.getAutoReloadLastFile();
        if (savedAutoReload) {
            scf.setAutoReloadLastFile(false);
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<SpinCADFrame> ref = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            SpinCADFrame f = new SpinCADFrame();
            f.setVisible(true);
            // Clean patch each test
            f.setPatch(new SpinCADPatch());
            ref.set(f);
            latch.countDown();
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Frame creation timed out");
        frame = ref.get();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(() -> frame.dispose());
        }
        // Restore auto-reload preference
        if (savedAutoReload) {
            new SpinCADFile().setAutoReloadLastFile(true);
        }
    }

    // ------------------------------------------------------------------
    // Combined test: control panel x3 + copy-paste-delete per block
    // ------------------------------------------------------------------
    @ParameterizedTest(name = "gui-test {0}")
    @MethodSource("allBlockClassNames")
    void controlPanelAndCopyPasteDelete(String className) throws Exception {
        final AtomicReference<SpinCADBlock> blockRef = new AtomicReference<>();
        runOnEDTAndWait(() -> {
            SpinCADBlock block = createBlock(className, 100, 100);
            frame.getPatch().patchModel.addBlock(block);
            blockRef.set(block);
        });

        SpinCADBlock block = blockRef.get();

        // --- Phase 1: open/close control panel 3 times ---
        if (block.hasControlPanel()) {
            for (int i = 1; i <= 3; i++) {
                final int iteration = i;

                runOnEDTAndWait(() -> {
                    block.controlPanelOpen = true;
                    block.openControlPanel();
                });

                drainEDT();

                runOnEDTAndWait(() -> {
                    assertTrue(block.controlPanelOpen,
                        simpleName(className) + " controlPanelOpen should be true (iteration " + iteration + ")");
                });

                runOnEDTAndWait(() -> {
                    block.deleteControlPanel();
                });

                drainEDT();

                runOnEDTAndWait(() -> {
                    assertFalse(block.controlPanelOpen,
                        simpleName(className) + " controlPanelOpen should be false after close (iteration " + iteration + ")");
                    assertNull(block.controlPanelFrame,
                        simpleName(className) + " controlPanelFrame should be null after close (iteration " + iteration + ")");
                });
            }
        }

        // --- Phase 2: copy-paste-delete ---
        runOnEDTAndWait(() -> {
            block.selected = true;
            frame.saveModelToPasteBuffer();
            frame.paste();
        });

        runOnEDTAndWait(() -> {
            List<SpinCADBlock> blocks = frame.getPatch().patchModel.blockList;
            assertEquals(2, blocks.size(),
                "Should have 2 blocks after paste of " + simpleName(className));

            for (SpinCADBlock b : blocks) b.selected = false;
            blocks.get(1).selected = true;
            frame.delete();
        });

        assertFrameAlive();

        runOnEDTAndWait(() -> {
            assertEquals(1, frame.getPatch().patchModel.blockList.size(),
                "Should have 1 block after deleting pasted copy of " + simpleName(className));
        });
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static SpinCADBlock createBlock(String className, int x, int y) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor(int.class, int.class);
            return (SpinCADBlock) ctor.newInstance(x, y);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create " + className, e);
        }
    }

    private static String simpleName(String className) {
        return className.substring(className.lastIndexOf('.') + 1);
    }

    private void assertFrameAlive() throws Exception {
        runOnEDTAndWait(() -> {
            assertTrue(frame.isDisplayable(), "Frame should still be displayable");
            assertTrue(frame.isVisible(), "Frame should still be visible");
        });
        CountDownLatch alive = new CountDownLatch(1);
        SwingUtilities.invokeLater(alive::countDown);
        assertTrue(alive.await(5, TimeUnit.SECONDS), "EDT should still be alive");
    }

    /** Flush pending EDT events (invokeLater callbacks etc.) */
    private void drainEDT() throws Exception {
        SwingUtilities.invokeAndWait(() -> { /* wait for queue to drain */ });
    }

    private void runOnEDTAndWait(Runnable action) throws Exception {
        AtomicReference<Throwable> error = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            try {
                action.run();
            } catch (Throwable t) {
                error.set(t);
            }
        });
        if (error.get() != null) {
            throw new RuntimeException("Action failed on EDT: " + error.get().getMessage(), error.get());
        }
    }
}
