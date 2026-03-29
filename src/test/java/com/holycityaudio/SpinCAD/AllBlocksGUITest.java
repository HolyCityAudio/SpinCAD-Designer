package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
 * Parameterized GUI tests that exercise every CADBlock for:
 *   1. Copy / paste / delete of the pasted copy (the crash-fix regression test)
 *   2. Opening and closing the control panel 3 times
 *
 * Run via: ./gradlew guiTest
 */
@Tag("gui")
public class AllBlocksGUITest {

    private SpinCADFrame frame;

    static Stream<String> allBlockClassNames() {
        return BlockDiscovery.findAllBlockClassNames().stream();
    }

    @BeforeEach
    void setUp() throws Exception {
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
    }

    // ------------------------------------------------------------------
    // Test 1: copy-paste-delete for every block type
    // ------------------------------------------------------------------
    @ParameterizedTest(name = "copy-paste-delete {0}")
    @MethodSource("allBlockClassNames")
    void copyPasteDelete(String className) throws Exception {
        runOnEDTAndWait(() -> {
            SpinCADBlock block = createBlock(className, 100, 100);
            frame.getPatch().patchModel.addBlock(block);
            block.selected = true;

            frame.saveModelToPasteBuffer();
            frame.paste();
        });

        // Should now have 2 instances
        runOnEDTAndWait(() -> {
            List<SpinCADBlock> blocks = frame.getPatch().patchModel.blockList;
            assertEquals(2, blocks.size(),
                "Should have 2 blocks after paste of " + simpleName(className));

            // Unselect all, then select the pasted copy (second one) and delete
            for (SpinCADBlock b : blocks) b.selected = false;
            blocks.get(1).selected = true;
            frame.delete();
        });

        // Frame must survive
        assertFrameAlive();

        runOnEDTAndWait(() -> {
            assertEquals(1, frame.getPatch().patchModel.blockList.size(),
                "Should have 1 block after deleting pasted copy of " + simpleName(className));
        });
    }

    // ------------------------------------------------------------------
    // Test 2: open and close control panel 3 times for every block
    // ------------------------------------------------------------------
    @ParameterizedTest(name = "control-panel x3 {0}")
    @MethodSource("allBlockClassNames")
    void openCloseControlPanel3Times(String className) throws Exception {
        final AtomicReference<SpinCADBlock> blockRef = new AtomicReference<>();
        runOnEDTAndWait(() -> {
            SpinCADBlock block = createBlock(className, 100, 100);
            frame.getPatch().patchModel.addBlock(block);
            blockRef.set(block);
        });

        SpinCADBlock block = blockRef.get();
        if (!block.hasControlPanel()) {
            // No control panel to test — pass automatically
            return;
        }

        for (int i = 1; i <= 3; i++) {
            final int iteration = i;

            // Open the control panel (uses the same path as the UI)
            runOnEDTAndWait(() -> {
                block.controlPanelOpen = true;
                block.openControlPanel();
            });

            // Let invokeLater callbacks (positioning, listener attachment) execute
            drainEDT();

            // Verify control panel opened
            runOnEDTAndWait(() -> {
                assertTrue(block.controlPanelOpen,
                    simpleName(className) + " controlPanelOpen should be true (iteration " + iteration + ")");
            });

            // Close the control panel
            runOnEDTAndWait(() -> {
                block.deleteControlPanel();
            });

            drainEDT();

            // Verify it closed cleanly
            runOnEDTAndWait(() -> {
                assertFalse(block.controlPanelOpen,
                    simpleName(className) + " controlPanelOpen should be false after close (iteration " + iteration + ")");
                assertNull(block.controlPanelFrame,
                    simpleName(className) + " controlPanelFrame should be null after close (iteration " + iteration + ")");
            });
        }

        assertFrameAlive();
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
