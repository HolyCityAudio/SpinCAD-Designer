package org.andrewkilpatrick.elmGen.simulator;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.prefs.Preferences;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;

import org.andrewkilpatrick.elmGen.util.Util;

/**
 * Two-channel meter display for the SpinCAD simulator.
 * Three modes selected by radio buttons:
 *   VU     — 300 ms RMS integration, dB vertical scale, peak hold indicator
 *   Peak   — fast attack / slow decay, dB vertical scale, peak hold indicator
 *   Linear — instantaneous absolute value, linear 0–1 vertical scale, no dynamics
 *
 * Can run as a floating window or docked inside the main frame.
 * Window position and mode are saved/restored via Preferences.
 */
public class VUMeterDisplay {

	private javax.swing.JDialog frame;
	private MeterPanel meterPanel;
	private JPanel containerPanel;  // holds meterPanel + mode buttons (reusable for dock/undock)
	private Preferences prefs;

	// Mode: 0 = VU, 1 = Peak, 2 = Linear
	private volatile int mode = 0;

	// Meter state (updated from audio thread)
	private volatile double level1 = 0;
	private volatile double level2 = 0;
	private volatile double peak1 = 0;
	private volatile double peak2 = 0;

	// Per-sample coefficients at 32768 Hz
	private static final double SAMPLE_RATE = 32768.0;
	private static final double VU_COEFF = 1.0 - Math.exp(-1.0 / (SAMPLE_RATE * 0.300));
	private static final double PEAK_DECAY = 1.0 - Math.exp(-1.0 / (SAMPLE_RATE * 0.300));
	private static final double PEAK_HOLD_DECAY = 1.0 - Math.exp(-1.0 / (SAMPLE_RATE * 2.0));

	// Accumulators
	private double accum1 = 0;
	private double accum2 = 0;
	private double peakHold1 = 0;
	private double peakHold2 = 0;

	// Buffer for VU meter register data
	private volatile int[] vuRawBuf;
	private volatile int vuRawLen;

	// Docking support
	private volatile boolean docked = false;
	private DockHost dockHost;
	private java.awt.Window ownerWindow;  // parent window for floating dialog

	/** Interface for the main frame to provide docking. */
	public interface DockHost {
		void dockVUMeter(JPanel panel);
		void undockVUMeter();
		java.awt.Window getOwnerWindow();
	}

	public VUMeterDisplay() {
		prefs = Preferences.userNodeForPackage(VUMeterDisplay.class);
		mode = prefs.getInt("VU_MODE", 0);
		docked = prefs.getBoolean("VU_DOCKED", true);  // docked by default

		SwingUtilities.invokeLater(() -> {
			containerPanel = buildContainerPanel();

			if (docked && dockHost != null) {
				dockHost.dockVUMeter(containerPanel);
			} else {
				showFloating();
			}
		});
	}

	public void setDockHost(DockHost host) {
		this.dockHost = host;
		// If we should be docked but weren't yet because host wasn't set,
		// dock now on the EDT
		if (docked && host != null && containerPanel != null) {
			SwingUtilities.invokeLater(() -> {
				if (frame != null) {
					saveWindowPosition();
					frame.setVisible(false);
					frame.dispose();
					frame = null;
				}
				host.dockVUMeter(containerPanel);
			});
		}
	}

	private JPanel buildContainerPanel() {
		JPanel container = new JPanel(new java.awt.BorderLayout());
		container.setBackground(Color.BLACK);

		meterPanel = new MeterPanel();
		container.add(meterPanel, java.awt.BorderLayout.CENTER);

		// Radio buttons
		JPanel modePanel = new JPanel();
		modePanel.setBackground(new Color(30, 30, 30));
		JRadioButton rbVU = new JRadioButton("VU");
		JRadioButton rbPeak = new JRadioButton("Peak");
		JRadioButton rbLinear = new JRadioButton("Linear");
		JRadioButton rbDock = new JRadioButton("Dock");

		Color bgColor = new Color(30, 30, 30);
		for (JRadioButton rb : new JRadioButton[]{rbVU, rbPeak, rbLinear}) {
			rb.setForeground(Color.WHITE);
			rb.setBackground(bgColor);
		}
		rbDock.setForeground(new Color(100, 200, 255));
		rbDock.setBackground(bgColor);

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbVU); bg.add(rbPeak); bg.add(rbLinear);
		modePanel.add(rbVU); modePanel.add(rbPeak); modePanel.add(rbLinear);
		modePanel.add(javax.swing.Box.createHorizontalStrut(10));
		modePanel.add(rbDock);

		// Select initial mode
		switch (mode) {
			case 1: rbPeak.setSelected(true); break;
			case 2: rbLinear.setSelected(true); break;
			default: rbVU.setSelected(true); break;
		}
		rbDock.setSelected(docked);

		rbVU.addActionListener(e -> { mode = 0; prefs.putInt("VU_MODE", 0); resetAccumulators(); });
		rbPeak.addActionListener(e -> { mode = 1; prefs.putInt("VU_MODE", 1); resetAccumulators(); });
		rbLinear.addActionListener(e -> { mode = 2; prefs.putInt("VU_MODE", 2); resetAccumulators(); });
		rbDock.addActionListener(e -> toggleDock());
		container.add(modePanel, java.awt.BorderLayout.SOUTH);

		return container;
	}

	private void showFloating() {
		if (frame != null) return;
		// Use JDialog with owner so it stays on top of SpinCAD but not other apps
		java.awt.Window owner = (dockHost != null) ? dockHost.getOwnerWindow() : null;
		frame = new javax.swing.JDialog(owner, "VU Meter");
		frame.setDefaultCloseOperation(javax.swing.JDialog.HIDE_ON_CLOSE);
		frame.setContentPane(containerPanel);

		// Restore saved position/size
		int x = prefs.getInt("VU_X", -1);
		int y = prefs.getInt("VU_Y", -1);
		int w = prefs.getInt("VU_W", 280);
		int h = prefs.getInt("VU_H", 400);
		frame.setSize(w, h);
		if (x >= 0 && y >= 0) {
			frame.setLocation(x, y);
		} else {
			frame.setLocationByPlatform(true);
		}

		// Save position on move/resize
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent e) { saveWindowPosition(); }
			@Override
			public void componentResized(ComponentEvent e) { saveWindowPosition(); }
		});

		frame.setVisible(true);
	}

	private void saveWindowPosition() {
		if (frame != null && frame.isVisible()) {
			prefs.putInt("VU_X", frame.getX());
			prefs.putInt("VU_Y", frame.getY());
			prefs.putInt("VU_W", frame.getWidth());
			prefs.putInt("VU_H", frame.getHeight());
		}
	}

	private void toggleDock() {
		docked = !docked;
		prefs.putBoolean("VU_DOCKED", docked);

		if (docked && dockHost != null) {
			// Move from floating to docked
			saveWindowPosition();
			if (frame != null) {
				frame.setVisible(false);
				frame.dispose();
				frame = null;
			}
			dockHost.dockVUMeter(containerPanel);
		} else {
			// Move from docked to floating
			if (dockHost != null) {
				dockHost.undockVUMeter();
			}
			showFloating();
		}
	}

	private void resetAccumulators() {
		accum1 = 0; accum2 = 0;
		peakHold1 = 0; peakHold2 = 0;
		level1 = 0; level2 = 0;
		peak1 = 0; peak2 = 0;
	}

	/** Called from audio thread with VU register data before update(). */
	public void writeVUData(int[] buf, int len) {
		vuRawBuf = buf;
		vuRawLen = len;
	}

	/** Called from audio thread to update meter levels. */
	public void update(int[] dacBuf, int len) {
		int[] vuBuf = vuRawBuf;
		int vuLen = vuRawLen;
		boolean hasVU = (vuBuf != null && vuLen >= len);
		int currentMode = mode;

		for (int i = 0; i < len; i += 2) {
			double s1, s2;
			if (hasVU) {
				s1 = Math.abs(Util.regToDouble(vuBuf[i]));
				s2 = Math.abs(Util.regToDouble(vuBuf[i + 1]));
			} else {
				s1 = Math.abs(Util.regToDouble(dacBuf[i]));
				s2 = Math.abs(Util.regToDouble(dacBuf[i + 1]));
			}

			if (currentMode == 0) {
				accum1 += VU_COEFF * (s1 * s1 - accum1);
				accum2 += VU_COEFF * (s2 * s2 - accum2);
			} else if (currentMode == 1) {
				if (s1 > accum1) accum1 = s1;
				else accum1 *= (1.0 - PEAK_DECAY);
				if (s2 > accum2) accum2 = s2;
				else accum2 *= (1.0 - PEAK_DECAY);
			} else {
				accum1 = s1;
				accum2 = s2;
			}

			if (currentMode < 2) {
				if (s1 > peakHold1) peakHold1 = s1;
				else peakHold1 *= (1.0 - PEAK_HOLD_DECAY);
				if (s2 > peakHold2) peakHold2 = s2;
				else peakHold2 *= (1.0 - PEAK_HOLD_DECAY);
			}
		}

		if (currentMode == 0) {
			level1 = Math.sqrt(accum1);
			level2 = Math.sqrt(accum2);
		} else {
			level1 = accum1;
			level2 = accum2;
		}
		peak1 = peakHold1;
		peak2 = peakHold2;

		vuRawBuf = null;
		vuRawLen = 0;

		if (meterPanel != null) {
			meterPanel.repaint();
		}
	}

	public void close() {
		if (frame != null) {
			saveWindowPosition();
			SwingUtilities.invokeLater(() -> {
				frame.setVisible(false);
				frame.dispose();
				frame = null;
			});
		}
		if (docked && dockHost != null) {
			SwingUtilities.invokeLater(() -> dockHost.undockVUMeter());
		}
	}

	// =========================================================================
	private class MeterPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		private final double DB_MIN = -60.0;
		private final double DB_MAX = 3.0;
		private final double DB_RANGE = DB_MAX - DB_MIN;
		private final int[] DB_TICKS = {0, -3, -6, -10, -20, -30, -40, -50, -60};
		private final double[] LIN_TICKS = {0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0};

		private final int TOP_PAD = 30;
		private final int BOTTOM_PAD = 10;
		private final int SIDE_PAD = 15;
		private final int BAR_GAP = 20;
		private final int LABEL_WIDTH = 35;

		private final Font labelFont = new Font("SansSerif", Font.PLAIN, 10);
		private final Font titleFont = new Font("SansSerif", Font.BOLD, 12);

		MeterPanel() {
			setBackground(Color.BLACK);
			setPreferredSize(new Dimension(200, 350));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();
			int meterH = h - TOP_PAD - BOTTOM_PAD;
			if (meterH < 10) return;

			int currentMode = mode;
			boolean isLinear = (currentMode == 2);

			// Title
			g2.setColor(Color.WHITE);
			g2.setFont(titleFont);
			String title;
			switch (currentMode) {
				case 0: title = "VU Meter"; break;
				case 1: title = "Peak Meter"; break;
				default: title = "Linear Meter"; break;
			}
			FontMetrics fm = g2.getFontMetrics();
			g2.drawString(title, (w - fm.stringWidth(title)) / 2, 18);

			// Bar positions
			int barAreaLeft = SIDE_PAD + LABEL_WIDTH;
			int barAreaRight = w - SIDE_PAD;
			int barAreaWidth = barAreaRight - barAreaLeft;
			int barWidth = (barAreaWidth - BAR_GAP) / 2;
			if (barWidth < 8) barWidth = 8;
			int bar1X = barAreaLeft;
			int bar2X = barAreaLeft + barWidth + BAR_GAP;

			// Scale labels and grid
			g2.setFont(labelFont);
			fm = g2.getFontMetrics();
			if (isLinear) {
				for (double tick : LIN_TICKS) {
					int y = linToY(tick, meterH);
					g2.setColor(new Color(60, 60, 60));
					g2.drawLine(barAreaLeft, y, barAreaRight, y);
					g2.setColor(new Color(160, 160, 160));
					String label = (tick == (int) tick)
							? String.valueOf((int) tick)
							: String.format("%.1f", tick);
					int labelW = fm.stringWidth(label);
					g2.drawString(label, barAreaLeft - labelW - 4, y + fm.getAscent() / 2 - 1);
				}
			} else {
				for (int db : DB_TICKS) {
					int y = dbToY(db, meterH);
					g2.setColor(new Color(60, 60, 60));
					g2.drawLine(barAreaLeft, y, barAreaRight, y);
					g2.setColor(new Color(160, 160, 160));
					String label = (db == 0) ? " 0" : String.valueOf(db);
					int labelW = fm.stringWidth(label);
					g2.drawString(label, barAreaLeft - labelW - 4, y + fm.getAscent() / 2 - 1);
				}
			}

			// Bars
			drawBar(g2, bar1X, barWidth, meterH, level1, peak1, isLinear);
			drawBar(g2, bar2X, barWidth, meterH, level2, peak2, isLinear);

			// Channel labels
			g2.setColor(Color.WHITE);
			g2.setFont(titleFont);
			fm = g2.getFontMetrics();
			g2.drawString("L", bar1X + (barWidth - fm.stringWidth("L")) / 2, h - 2);
			g2.drawString("R", bar2X + (barWidth - fm.stringWidth("R")) / 2, h - 2);
		}

		private void drawBar(Graphics2D g2, int x, int barW, int meterH,
				double level, double peak, boolean isLinear) {
			int barBottom = TOP_PAD + meterH;

			g2.setColor(new Color(20, 20, 20));
			g2.fillRect(x, TOP_PAD, barW, meterH);
			g2.setColor(new Color(60, 60, 60));
			g2.drawRect(x, TOP_PAD, barW, meterH);

			int levelY;
			if (isLinear) {
				levelY = linToY(level, meterH);
			} else {
				levelY = dbToY(linearToDb(level), meterH);
			}

			if (levelY < barBottom) {
				if (isLinear) {
					g2.setColor(new Color(0, 180, 0));
					g2.fillRect(x + 2, levelY, barW - 4, barBottom - levelY);
				} else {
					int yGreen = dbToY(-10, meterH);
					int yYellow = dbToY(-3, meterH);

					int top = Math.max(levelY, yGreen);
					if (top < barBottom) {
						g2.setColor(new Color(0, 180, 0));
						g2.fillRect(x + 2, top, barW - 4, barBottom - top);
					}
					if (levelY < yGreen) {
						top = Math.max(levelY, yYellow);
						if (top < yGreen) {
							g2.setColor(new Color(200, 200, 0));
							g2.fillRect(x + 2, top, barW - 4, yGreen - top);
						}
					}
					if (levelY < yYellow) {
						g2.setColor(new Color(220, 0, 0));
						g2.fillRect(x + 2, levelY, barW - 4, yYellow - levelY);
					}

					// Peak hold indicator
					int peakY = dbToY(linearToDb(peak), meterH);
					if (peakY >= TOP_PAD && peakY < barBottom) {
						g2.setColor(Color.WHITE);
						g2.setStroke(new BasicStroke(2f));
						g2.drawLine(x + 2, peakY, x + barW - 2, peakY);
						g2.setStroke(new BasicStroke(1f));
					}
				}
			}
		}

		private int dbToY(double db, int meterH) {
			db = Math.max(DB_MIN, Math.min(DB_MAX, db));
			double fraction = (db - DB_MIN) / DB_RANGE;
			return TOP_PAD + (int) ((1.0 - fraction) * meterH);
		}

		private int linToY(double val, int meterH) {
			val = Math.max(0.0, Math.min(1.0, val));
			return TOP_PAD + (int) ((1.0 - val) * meterH);
		}

		private double linearToDb(double linear) {
			if (linear < 1e-10) return DB_MIN;
			return 20.0 * Math.log10(linear);
		}
	}
}
