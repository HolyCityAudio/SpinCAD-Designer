/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.andrewkilpatrick.elmGen.simulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.util.Util;


public class LevelLogger implements AudioSink {
	JFrame frame;
	public LoggerPanel panel;
	int windowCount = 0;
	double maxL = 0.0;
	double maxR = 0.0;
	double decayval = 0.999;
	int xPos = 0;
	int oldL = -96;
	int oldR = -96;
	double refLeft  = -0.5;
	double refRight =  0.5;
	double vDivLeft  = 1.0;
	double vDivRight = 1.0;
	private int scopeCh1Gain = 16;
	private int scopeCh2Gain = 16;

	public enum triggerMode { AUTO, NORMAL, SINGLE };
	public triggerMode tm = triggerMode.AUTO;
	boolean triggered = false;

	private double[] filter = new double[3];

	private int logMode = 1;  // 0 = scope, 1 = log
	double left  = -1;
	double right = -1;
	int windowRatio = 512;

	// scope display controls
	private boolean ch1Enabled    = true;
	private boolean ch2Enabled    = true;
	private boolean frozen         = false;
	private boolean pendingFreeze  = false;
	private volatile boolean paused = false;

	// ms per division for time grid
	private double msPerDivision = 10.0;

	AudioDelay delay;

	private static final double FV1_FULL_SCALE = 8388608.0;
	private static final double[] GRID_LEVELS = { 1.0, 0.5, 0.25, 0.1 };

	// -------------------------------------------------------------------------
	// ScopePanel: a JPanel subclass that renders from a shared BufferedImage.
	// SpinCADSimulator declares scopePanel as this type so that showScope() can
	// use the same object for both layout and double-buffered drawing.
	// -------------------------------------------------------------------------
	public static class ScopePanel extends JPanel {
		private static final long serialVersionUID = 1L;
		// The back-buffer is written by the audio thread and read on the EDT.
		// volatile reference ensures the EDT always sees the latest buffer.
		volatile BufferedImage backBuffer = null;

		public ScopePanel() {
			setBackground(Color.BLACK);
			setOpaque(true);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			BufferedImage buf = backBuffer;   // single volatile read
			if(buf != null) {
				g.drawImage(buf, 0, 0, null);
			}
		}
	}

	// -------------------------------------------------------------------------
	// LoggerBufferPanel: buffered JPanel for the level logger, eliminates the
	// white-flash that occurs when Swing repaints a plain JPanel.
	// -------------------------------------------------------------------------
	public static class LoggerBufferPanel extends JPanel {
		private static final long serialVersionUID = 2L;
		volatile BufferedImage backBuffer = null;

		public LoggerBufferPanel() {
			setBackground(Color.BLACK);
			setOpaque(true);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			BufferedImage buf = backBuffer;
			if(buf != null) {
				g.drawImage(buf, 0, 0, null);
			}
		}
	}

	// -------------------------------------------------------------------------
	// Amplitude-label separator panel — placed between logger and scope.
	// -------------------------------------------------------------------------
	public static class AmplitudeLabelPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		private static final double[] LEVELS = { 1.0, 0.5, 0.25, 0.1 };
		private static final String[] LABELS = { "0dBFS", "-6dB", "-12dB", "-20dB" };
		private static final Color BG    = new Color(20, 20, 20);
		private static final Color LINE  = new Color(60, 90, 60);
		private static final Color LABEL = new Color(100, 170, 100);

		public AmplitudeLabelPanel() {
			setPreferredSize(new Dimension(52, 200));
			setMinimumSize(new Dimension(52, 50));
			setBackground(BG);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			int w = getWidth();
			int h = getHeight();
			int centerY = h / 2;
			int halfH   = centerY - 4;

			g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
			g2.setColor(LINE);
			g2.drawLine(w - 1, 0, w - 1, h);

			for(int gi = 0; gi < LEVELS.length; gi++) {
				int yPos = centerY - (int)(LEVELS[gi] * halfH);
				int yNeg = centerY + (int)(LEVELS[gi] * halfH);
				g2.setColor(LINE);
				g2.drawLine(w - 5, yPos, w - 1, yPos);
				g2.drawLine(w - 5, yNeg, w - 1, yNeg);
				g2.setColor(LABEL);
				g2.drawString("+" + LABELS[gi], 2, yPos + 4);
				if(gi > 0) g2.drawString("-" + LABELS[gi], 2, yNeg + 4);
			}
			g2.setColor(LINE);
			g2.drawLine(w - 5, centerY, w - 1, centerY);
			g2.setColor(LABEL);
			g2.drawString("0", 2, centerY + 4);
		}
	}

	// =========================================================================
	public LevelLogger() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("SpinCAD Designer - Level Logger");
				frame.setLocation(300, 0);
				panel = new LoggerPanel();
				panel.getPanel().setPreferredSize(new Dimension(600, 200));
				frame.getContentPane().add(panel.getPanel());
				frame.pack();
				frame.setVisible(true);
			}
		});
		delay = new AudioDelay();
	}

	/**
	 * Constructor used when the panel is a ScopePanel (double-buffered subclass).
	 * p MUST be a ScopePanel for flicker-free rendering; if it is a plain JPanel
	 * the display will still work but may flicker.
	 */
	public LevelLogger(final JPanel p) {
		panel = new LoggerPanel(p);  // set synchronously so audio thread never sees null
		delay = new AudioDelay();
		filter[0] = 0.0;
		filter[1] = 0.0;
		filter[2] = 0.0;
	}

	public void close() {}
	public void writeAdc(int[] buf, int len) {}

	public void writeDac(int[] buf, int len) {
		if(paused) return;
		int dbuf[] = delay.process(buf, 50000);
		for(int i = 0; i < len; i += 2) {
			if(logMode == 1) {
				left  = Math.abs(Util.regToDouble(dbuf[i])     + 0.00001);
				right = Math.abs(Util.regToDouble(dbuf[i + 1]) + 0.00001);
				if(left  > maxL) maxL = left;
				if(right > maxR) maxR = right;
				maxL *= decayval;
				maxR *= decayval;
			} else if(logMode == 0) {
				left  = (double) Util.regToInt(dbuf[i]);
				right = (double) Util.regToInt(dbuf[i + 1]);
			}
			windowCount++;
			if(windowCount >= windowRatio) {
				if(!frozen) panel.updateLevels();
				windowCount = 0;
			}
		}
	}

	// =========================================================================
	public class LoggerPanel {

		JPanel panel;

		// Back buffer — only used when panel is a ScopePanel
		private BufferedImage backBuffer = null;
		private int bufWidth  = 0;
		private int bufHeight = 0;

		public LoggerPanel() {
			panel = new JPanel();
		}

		public Component getPanel() {
			return panel;
		}

		public LoggerPanel(JPanel p) {
			panel = p;
		}

		/**
		 * Returns a Graphics2D onto the back buffer, recreating it if the panel
		 * has been resized.  Returns null if the panel has zero size.
		 * When the panel is a ScopePanel the finished buffer is published to it
		 * via the volatile backBuffer field so paintComponent can read it safely.
		 */
		private Graphics2D getBufferGraphics() {
			int w = panel.getWidth();
			int h = panel.getHeight();
			if(w < 1 || h < 1) return null;

			if(backBuffer == null || w != bufWidth || h != bufHeight) {
				bufWidth  = w;
				bufHeight = h;
				backBuffer = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				Graphics2D g2init = backBuffer.createGraphics();
				g2init.setColor(Color.BLACK);
				g2init.fillRect(0, 0, w, h);
				g2init.dispose();
				// Reset sweep so the full grid is redrawn at new size
				xPos      = 0;
				triggered = false;
				oldL = h / 2;
				oldR = h / 2;
				// Publish initial black frame to the panel
				if(panel instanceof ScopePanel) {
					((ScopePanel) panel).backBuffer = backBuffer;
				} else if(panel instanceof LoggerBufferPanel) {
					((LoggerBufferPanel) panel).backBuffer = backBuffer;
				}
			}
			return backBuffer.createGraphics();
		}

		/** Publishes the finished back buffer to the panel and schedules a repaint. */
		private void publish() {
			if(panel instanceof ScopePanel) {
				((ScopePanel) panel).backBuffer = backBuffer;
			} else if(panel instanceof LoggerBufferPanel) {
				((LoggerBufferPanel) panel).backBuffer = backBuffer;
			}
			panel.repaint();
		}

		protected void updateLevels() {
			if(!panel.isShowing()) return;  // hidden card — skip rendering
			int panelWidth  = panel.getWidth();
			int panelHeight = panel.getHeight();
			if(panelWidth < 1 || panelHeight < 1) return;

			int centerY = panelHeight / 2;
			int halfH   = centerY - 4;

			if(logMode == 1) {
				// ---- LOG / LEVEL-METER MODE — draw to back buffer to prevent white flash ----
				Graphics2D g2 = getBufferGraphics();
				if(g2 == null) return;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				int newL = (int) sampleToDB(maxL);
				int newR = (int) sampleToDB(maxR);

				filter[2] = filter[1];
				filter[1] = filter[0];
				filter[0] = newL;
				if(filter[0] - filter[2] > 0.0) triggered = true;

				if(triggered && (tm == triggerMode.AUTO) || (tm == triggerMode.SINGLE)) {
					if(xPos < 1) {
						g2.setColor(Color.BLACK);
						g2.fillRect(0, 0, panelWidth, panelHeight);
						drawLoggerGrid(g2, panelWidth, panelHeight);
					}
					// Erase slice ahead of cursor, restore grid lines
					g2.setColor(Color.BLACK);
					g2.fillRect(xPos + 1, 0, 3, panelHeight);
					redrawLoggerGridSlice(g2, xPos + 1, 3, panelHeight);
					g2.setColor(Color.MAGENTA);
					g2.drawLine(xPos, (oldL * -2), xPos + 1, -(newL * 2));
					g2.setColor(Color.CYAN);
					g2.drawLine(xPos, (oldR * -2), xPos + 1, -(newR * 2));
					oldL = newL;
					oldR = newR;
					xPos++;
					if(xPos == panelWidth) {
						switch(tm) {
						case AUTO:   xPos = 0; triggered = false; break;
						case NORMAL: xPos = 0; triggered = false; break;
						case SINGLE: triggered = false; break;
						}
					}
				}
				g2.dispose();
				publish();

			} else if(logMode == 0) {
				// ---- SCOPE MODE — all drawing to back buffer ----
				Graphics2D g2 = getBufferGraphics();
				if(g2 == null) return;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				double normL = left  / FV1_FULL_SCALE;
				double normR = right / FV1_FULL_SCALE;
				double gainMultL = Math.pow(2.0, 16 - scopeCh1Gain);
				double gainMultR = Math.pow(2.0, 16 - scopeCh2Gain);
				normL *= gainMultL;
				normR *= gainMultR;

				int newL = centerY - (int)(normL * halfH);
				int newR = centerY - (int)(normR * halfH);

				filter[2] = filter[1];
				filter[1] = filter[0];
				filter[0] = newL;
				if(filter[0] - filter[2] > 0.0) triggered = true;

				if(triggered && (tm == triggerMode.AUTO) || (tm == triggerMode.SINGLE)) {

					if(xPos < 1) {
						// New sweep: clear and draw full grid
						g2.setColor(Color.BLACK);
						g2.fillRect(0, 0, panelWidth, panelHeight);
						drawGrid(g2, panelWidth, panelHeight, centerY, halfH);
						oldL = centerY;
						oldR = centerY;
					}

					// Erase the thin slice ahead of the cursor, restore grid
					g2.setColor(Color.BLACK);
					g2.fillRect(xPos + 1, 0, 3, panelHeight);
					redrawGridSlice(g2, xPos + 1, 3, centerY, halfH, panelWidth);

					if(ch1Enabled) {
						g2.setColor(new Color(0, 210, 80));
						g2.drawLine(xPos, oldL, xPos + 1, newL);
					}
					if(ch2Enabled) {
						g2.setColor(new Color(210, 170, 0));
						g2.drawLine(xPos, oldR, xPos + 1, newR);
					}

					oldL = newL;
					oldR = newR;
					xPos++;

					if(xPos >= panelWidth) {
						if(pendingFreeze) {
							frozen = true;
							pendingFreeze = false;
						}
						switch(tm) {
						case AUTO:   xPos = 0; triggered = false; break;
						case NORMAL: xPos = 0; triggered = false; break;
						case SINGLE: triggered = false; break;
						}
					}
				}
				g2.dispose();
				publish();
			}
		}

		private void drawGrid(Graphics2D g2, int panelWidth, int panelHeight,
							  int centerY, int halfH) {
			Font labelFont = new Font("Monospaced", Font.PLAIN, 9);
			g2.setFont(labelFont);
			float[] dash = { 4f, 4f };
			BasicStroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT,
								 BasicStroke.JOIN_MITER, 1f, dash, 0f);
			BasicStroke solid  = new BasicStroke(1f);

			Color gridColor      = new Color(70, 120, 70);
			Color timeColor      = new Color(60, 90, 130);
			for(int gi = 0; gi < GRID_LEVELS.length; gi++) {
				int yPos = centerY - (int)(GRID_LEVELS[gi] * halfH);
				int yNeg = centerY + (int)(GRID_LEVELS[gi] * halfH);
				g2.setStroke(gi == 0 ? solid : dashed);
				g2.setColor(gridColor);
				g2.drawLine(0, yPos, panelWidth, yPos);
				g2.drawLine(0, yNeg, panelWidth, yNeg);
			}
			g2.setStroke(solid);
			g2.setColor(new Color(100, 160, 100));
			g2.drawLine(0, centerY, panelWidth, centerY);

			double samplesPerMs = ElmProgram.SAMPLERATE / 1000.0;
			double pixelsPerDiv = (msPerDivision * samplesPerMs) / windowRatio;
			if(pixelsPerDiv < 1.0) pixelsPerDiv = 1.0;

			g2.setStroke(dashed);
			double xDiv = pixelsPerDiv;
			while(xDiv < panelWidth) {
				int xi = (int) Math.round(xDiv);
				g2.setColor(timeColor);
				g2.drawLine(xi, 0, xi, panelHeight);
				xDiv += pixelsPerDiv;
			}
			g2.setStroke(solid);
		}

		private void redrawGridSlice(Graphics2D g2, int x, int w,
									 int centerY, int halfH, int panelWidth) {
			g2.setStroke(new BasicStroke(1f));
			for(int gi = 0; gi < GRID_LEVELS.length; gi++) {
				int yPos = centerY - (int)(GRID_LEVELS[gi] * halfH);
				int yNeg = centerY + (int)(GRID_LEVELS[gi] * halfH);
				g2.setColor(new Color(70, 120, 70));
				g2.drawLine(x, yPos, x + w, yPos);
				g2.drawLine(x, yNeg, x + w, yNeg);
			}
			g2.setColor(new Color(100, 160, 100));
			g2.drawLine(x, centerY, x + w, centerY);

			double samplesPerMs = ElmProgram.SAMPLERATE / 1000.0;
			double pixelsPerDiv = (msPerDivision * samplesPerMs) / windowRatio;
			if(pixelsPerDiv < 1.0) pixelsPerDiv = 1.0;
			double xDiv = pixelsPerDiv;
			while(xDiv < panelWidth) {
				int xi = (int) Math.round(xDiv);
				if(xi >= x && xi <= x + w) {
					g2.setColor(new Color(60, 90, 130));
					g2.drawLine(xi, 0, xi, centerY * 2);
				}
				xDiv += pixelsPerDiv;
			}
		}

		private void drawLoggerGrid(Graphics2D g2, int w, int h) {
			Color gridColor  = new Color(70, 100, 70);
			Color labelColor = new Color(100, 160, 100);
			float[] dash = { 4f, 4f };
			BasicStroke dashed = new BasicStroke(1f, BasicStroke.CAP_BUTT,
								 BasicStroke.JOIN_MITER, 1f, dash, 0f);
			BasicStroke solid  = new BasicStroke(1f);
			g2.setFont(new Font("Monospaced", Font.PLAIN, 9));
			// 2 pixels per dB; grid line every 12 dB from 0 to -96
			for(int db = 0; db <= 96; db += 12) {
				int y = db * 2;
				if(y >= h) break;
				g2.setStroke(db == 0 ? solid : dashed);
				g2.setColor(gridColor);
				g2.drawLine(0, y, w, y);
				g2.setColor(labelColor);
				String label = (db == 0) ? "0dBFS" : ("-" + db + "dB");
				g2.drawString(label, 2, y < 12 ? y + 10 : y - 2);
			}
			g2.setStroke(solid);
		}

		private void redrawLoggerGridSlice(Graphics2D g2, int x, int sliceW, int h) {
			g2.setStroke(new BasicStroke(1f));
			g2.setColor(new Color(70, 100, 70));
			for(int db = 0; db <= 96; db += 12) {
				int y = db * 2;
				if(y >= h) break;
				g2.drawLine(x, y, x + sliceW, y);
			}
		}
	}

	/**
	 * When frozen, force an immediate redraw of the back buffer from current state.
	 * This allows ch1/ch2 enable toggles to take effect without waiting for a new sweep.
	 * No new sample data is consumed — we just re-render the last frame.
	 * Performance impact: negligible (one off-screen paint on button press).
	 */
	public void redrawFrozen() {
		if(frozen && panel != null) {
			// Trigger a sweep restart so the full grid + traces are redrawn
			xPos      = 0;
			triggered = false;
			// Temporarily unfreeze to allow one updateLevels pass
			boolean wasFrozen = frozen;
			frozen = false;
			panel.updateLevels();
			frozen = wasFrozen;
		}
	}

	private double sampleToDB(double sampleLevel) {
		return 20 * Math.log10(sampleLevel);
	}

	public void setLogMode(int mode) {
		if((logMode == 0) || (logMode == 1)) logMode = mode;
	}

	public void setPaused(boolean p) { paused = p; }
	public boolean isPaused() { return paused; }

	public void setScopeCh1Gain(int gain) { scopeCh1Gain = gain; }
	public void setScopeCh2Gain(int gain) { scopeCh2Gain = gain; }
	public void setWindowRatio(int ratio) { windowRatio = ratio; }
	public void setMsPerDivision(double ms) { msPerDivision = ms; }
	public double getMsPerDivision() { return msPerDivision; }
	public double getSignalSlope(int channel) { return right; }

	public void setCh1Enabled(boolean enabled) { ch1Enabled = enabled; }
	public void setCh2Enabled(boolean enabled) { ch2Enabled = enabled; }
	public boolean isCh1Enabled() { return ch1Enabled; }
	public boolean isCh2Enabled() { return ch2Enabled; }

	public void requestFreeze() { pendingFreeze = true; }
	public void unfreeze()      { frozen = false; pendingFreeze = false; }
	public void setFrozen(boolean f) { if(f) requestFreeze(); else unfreeze(); }
	public boolean isFrozen() { return frozen || pendingFreeze; }
}
