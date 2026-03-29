package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.holycityaudio.SpinCAD.CADBlocks.SinCosLFOACADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.GainCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;

/**
 * GUI integration tests for copy/paste/delete operations.
 * These require a real display (non-headless) — run via: ./gradlew guiTest
 */
@Tag("gui")
public class CopyPasteDeleteTest {

    private SpinCADFrame frame;

    @BeforeEach
    void setUp() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<SpinCADFrame> ref = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            SpinCADFrame f = new SpinCADFrame();
            f.setVisible(true);
            // Start each test with a clean patch (like Ctrl-N)
            f.setPatch(new SpinCADPatch());
            ref.set(f);
            latch.countDown();
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS), "Frame should be created within 10s");
        frame = ref.get();
        assertNotNull(frame);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (frame != null) {
            SwingUtilities.invokeAndWait(() -> frame.dispose());
        }
    }

    private static void select(SpinCADBlock b, boolean sel) {
        b.selected = sel;
    }

    private void selectAll(boolean sel) {
        for (SpinCADBlock b : blockList()) b.selected = sel;
    }

    private List<SpinCADBlock> blockList() {
        return frame.getPatch().patchModel.blockList;
    }

    private void addBlock(SpinCADBlock b) {
        frame.getPatch().patchModel.addBlock(b);
    }

    /**
     * Reproduces the crash: paste blocks including SinCosLFO, then delete
     * the pasted SinCosLFO. Before the fix, this disposed the main frame.
     */
    @Test
    void deletePastedSinCosLFO_shouldNotCrash() throws Exception {
        runOnEDTAndWait(() -> {
            addBlock(new SinCosLFOACADBlock(100, 100));
            addBlock(new InputCADBlock(100, 200));
            selectAll(true);
            frame.saveModelToPasteBuffer();
            frame.paste();
        });

        // Find and delete only the pasted SinCosLFO
        runOnEDTAndWait(() -> {
            selectAll(false);
            List<SpinCADBlock> lfos = new ArrayList<>();
            for (SpinCADBlock b : blockList()) {
                if (b instanceof SinCosLFOACADBlock) lfos.add(b);
            }
            assertEquals(2, lfos.size(), "Should have 2 SinCosLFO blocks after paste");
            select(lfos.get(1), true);
            frame.delete();
        });

        assertFrameAlive();

        runOnEDTAndWait(() -> {
            int lfoCount = 0;
            for (SpinCADBlock b : blockList()) {
                if (b instanceof SinCosLFOACADBlock) lfoCount++;
            }
            assertEquals(1, lfoCount, "Should have 1 SinCosLFO after deleting the copy");
        });
    }

    /**
     * Tests deleting a pasted block that was the FIRST selected block in the
     * paste operation (the one assigned to SpinCADFrame.blk). This is the
     * specific scenario that triggered the main-frame disposal bug.
     */
    @Test
    void deletePastedBlock_firstInSelection_shouldNotCrash() throws Exception {
        runOnEDTAndWait(() -> {
            addBlock(new SinCosLFOACADBlock(50, 50));
            addBlock(new InputCADBlock(50, 150));
            addBlock(new OutputCADBlock(50, 250));
            selectAll(true);
            frame.saveModelToPasteBuffer();
            frame.paste();
        });

        runOnEDTAndWait(() -> {
            selectAll(false);
            List<SpinCADBlock> lfos = new ArrayList<>();
            for (SpinCADBlock b : blockList()) {
                if (b instanceof SinCosLFOACADBlock) lfos.add(b);
            }
            select(lfos.get(1), true);
            frame.delete();
        });

        assertFrameAlive();
    }

    /**
     * Tests that deleting ALL pasted blocks one-by-one doesn't crash.
     */
    @Test
    void deleteAllPastedBlocks_oneByOne_shouldNotCrash() throws Exception {
        runOnEDTAndWait(() -> {
            addBlock(new SinCosLFOACADBlock(100, 100));
            addBlock(new InputCADBlock(100, 200));
            addBlock(new OutputCADBlock(100, 300));
            selectAll(true);
            frame.saveModelToPasteBuffer();
            frame.paste();
        });

        final int[] originalCount = new int[1];
        runOnEDTAndWait(() -> {
            // Half the blocks are originals, half are pasted copies
            originalCount[0] = blockList().size() / 2;
        });

        // Delete pasted blocks one at a time
        for (int i = 0; i < 3; i++) {
            runOnEDTAndWait(() -> {
                List<SpinCADBlock> blocks = blockList();
                selectAll(false);
                if (blocks.size() > originalCount[0]) {
                    select(blocks.get(originalCount[0]), true);
                    frame.delete();
                }
            });
        }

        assertFrameAlive();

        runOnEDTAndWait(() -> {
            assertEquals(originalCount[0], blockList().size(),
                "Should have only the original blocks left");
        });
    }

    private void assertFrameAlive() throws Exception {
        runOnEDTAndWait(() -> {
            assertTrue(frame.isDisplayable(), "Frame should still be displayable");
            assertTrue(frame.isVisible(), "Frame should still be visible");
        });
        CountDownLatch edtAlive = new CountDownLatch(1);
        SwingUtilities.invokeLater(edtAlive::countDown);
        assertTrue(edtAlive.await(5, TimeUnit.SECONDS), "EDT should still be alive");
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
            throw new RuntimeException("Action failed on EDT", error.get());
        }
    }
}
