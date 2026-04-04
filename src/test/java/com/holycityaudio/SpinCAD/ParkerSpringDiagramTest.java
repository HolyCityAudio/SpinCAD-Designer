package com.holycityaudio.SpinCAD;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Generates a block diagram PNG for the Parker Spring Reverb.
 */
public class ParkerSpringDiagramTest {

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    @Test
    void generateBlockDiagram() throws Exception {
        int W = 800, H = 340;
        BufferedImage img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, W, H);

        // Title
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Parker Spring Reverb — Block Diagram (Parker, DAFx-11, 2011)", 130, 22);

        // Layout
        int loopY = 110;   // main loop center
        int boxH = 36;
        int boxHalf = boxH / 2;

        Font labelFont = new Font("Arial", Font.PLAIN, 11);
        Font smallFont = new Font("Arial", Font.PLAIN, 9);
        Font boldFont = new Font("Arial", Font.BOLD, 11);

        Color loopColor = new Color(0x22, 0x66, 0xcc);

        // ========== INPUT ==========
        int inputX = 30;
        g.setFont(boldFont);
        g.setColor(Color.BLACK);
        g.drawString("Input", inputX, loopY - 5);
        int inputLineX = inputX + 38;

        // ========== SUMMING JUNCTION ==========
        int sumX = inputLineX + 30;
        drawSumCircle(g, sumX, loopY);
        drawArrow(g, inputLineX, loopY, sumX - 12, loopY, true);

        // ========== DELAY ==========
        int delX = sumX + 45;
        drawBox(g, delX, loopY - boxHalf, 100, boxH, "Delay", "2048 smp (~62ms)", loopColor, labelFont, smallFont);
        drawArrow(g, sumX + 12, loopY, delX, loopY, true);

        // ========== ALLPASS CASCADE ==========
        int apX = delX + 120;
        drawBox(g, apX, loopY - boxHalf, 140, boxH, "53× Allpass", "k=4 (stretch), a=0.60", loopColor, labelFont, smallFont);
        drawArrow(g, delX + 100, loopY, apX, loopY, true);

        // fC annotation
        g.setFont(smallFont);
        g.setColor(new Color(0x66, 0x66, 0x66));
        g.drawString("fC ≈ 4 kHz", apX + 35, loopY + boxHalf + 14);

        // ========== LPF ==========
        int lpfX = apX + 160;
        drawBox(g, lpfX, loopY - boxHalf, 70, boxH, "LPF", "1-pole", loopColor, labelFont, smallFont);
        drawArrow(g, apX + 140, loopY, lpfX, loopY, true);

        // ========== OUTPUT POINT ==========
        int outX = lpfX + 90;
        g.setColor(Color.BLACK);
        fillDot(g, outX, loopY, 4);
        drawArrow(g, lpfX + 70, loopY, outX, loopY, true);
        g.setFont(labelFont);
        g.drawString("fb", outX + 8, loopY - 5);

        // ========== FEEDBACK PATH ==========
        int fbY = loopY + 60;
        g.setColor(loopColor);
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                10f, new float[]{6f, 4f}, 0f));
        g.drawLine(outX, loopY, outX, fbY);
        g.drawLine(outX, fbY, sumX, fbY);
        g.drawLine(sumX, fbY, sumX, loopY + 12);
        g.setStroke(new BasicStroke(1.5f));

        g.setFont(smallFont);
        g.setColor(loopColor);
        g.drawString("× g (Reverb Time)", sumX + 5, fbY - 3);

        // ========== OUTPUT MIXING ==========
        int mixY = loopY + 105;
        g.setFont(boldFont);
        g.setColor(Color.BLACK);
        g.drawString("Output:", 40, mixY);
        g.setFont(labelFont);
        g.drawString("L = R = Input×gain + fb", 110, mixY);

        // Line from fb down to output
        g.setColor(loopColor);
        g.setStroke(new BasicStroke(1.0f));
        g.drawLine(outX, loopY + 4, outX, mixY - 15);
        drawArrow(g, outX, mixY - 15, 230, mixY - 8, true);

        // ========== CONTROL PANEL ==========
        int ctrlY = mixY + 30;
        g.setFont(boldFont);
        g.setColor(Color.BLACK);
        g.drawString("Control Panel", 40, ctrlY);

        g.setFont(smallFont);
        g.setColor(new Color(0x44, 0x44, 0x44));
        String[] params = {
            "Gain: -24..0 dB (input level)",
            "Reverb Time: 0.10..0.95 (feedback gain — echo decay)",
            "Damping: 0.01..0.70 (LPF coeff — darker = more damped)",
            "Dispersion: 0.30..0.80 (allpass coeff — chirp amount)",
        };
        for (int i = 0; i < params.length; i++) {
            g.drawString(params[i], 50, ctrlY + 16 + i * 13);
        }

        // Resource note
        g.drawString("Resources: 119 instructions, ~2260 delay samples", 450, ctrlY + 16);
        g.drawString("53 allpasses × 4 samples + 2048 delay", 450, ctrlY + 29);

        g.dispose();

        File docsDir = new File("docs/images");
        docsDir.mkdirs();
        File out = new File(docsDir, "reverb-parker-spring-diagram.png");
        ImageIO.write(img, "png", out);
        System.out.println("Wrote " + out.getPath());
    }

    // ========== Drawing helpers ==========

    private void drawBox(Graphics2D g, int x, int y, int w, int h,
            String line1, String line2, Color borderColor, Font mainFont, Font subFont) {
        g.setColor(new Color(0xf0, 0xf4, 0xfa));
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(borderColor);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, h, 8, 8);
        g.setStroke(new BasicStroke(1.5f));

        g.setFont(mainFont);
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(line1, x + (w - fm.stringWidth(line1)) / 2, y + 15);

        g.setFont(subFont);
        g.setColor(new Color(0x66, 0x66, 0x66));
        fm = g.getFontMetrics();
        g.drawString(line2, x + (w - fm.stringWidth(line2)) / 2, y + 28);
    }

    private void drawSumCircle(Graphics2D g, int cx, int cy) {
        int r = 11;
        g.setColor(Color.WHITE);
        g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(cx - r, cy - r, 2 * r, 2 * r);
        g.drawLine(cx - 5, cy, cx + 5, cy);
        g.drawLine(cx, cy - 5, cx, cy + 5);
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2, boolean withHead) {
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(x1, y1, x2, y2);
        if (withHead) {
            double angle = Math.atan2(y2 - y1, x2 - x1);
            int sz = 7;
            int ax1 = (int) (x2 - sz * Math.cos(angle - Math.PI / 6));
            int ay1 = (int) (y2 - sz * Math.sin(angle - Math.PI / 6));
            int ax2 = (int) (x2 - sz * Math.cos(angle + Math.PI / 6));
            int ay2 = (int) (y2 - sz * Math.sin(angle + Math.PI / 6));
            g.fillPolygon(new int[]{x2, ax1, ax2}, new int[]{y2, ay1, ay2}, 3);
        }
    }

    private void fillDot(Graphics2D g, int cx, int cy, int r) {
        g.fillOval(cx - r, cy - r, 2 * r, 2 * r);
    }
}
