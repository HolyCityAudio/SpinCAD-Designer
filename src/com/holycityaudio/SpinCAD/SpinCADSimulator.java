package com.holycityaudio.SpinCAD;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.RenderingHints;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andrewkilpatrick.elmGen.simulator.AudioFileReader;
import org.andrewkilpatrick.elmGen.simulator.LevelLogger;
import org.andrewkilpatrick.elmGen.simulator.LevelLogger.LoggerBufferPanel;
import org.andrewkilpatrick.elmGen.simulator.LevelLogger.ScopePanel;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;

public class SpinCADSimulator {
	private boolean simRunning = false;
	public SpinSimulator sim;
	public String outputFile = null;

	private Preferences prefs;
	public JPanel levelMonitor = new JPanel();
	public LoggerBufferPanel loggerPanel = new LoggerBufferPanel();

	// scopePanel is declared as ScopePanel so the double-buffered paintComponent
	// is in effect from the moment it is added to the layout.
	public ScopePanel scopePanel = new ScopePanel();

	// displayColumn: container panel — switchDisplay() swaps in the active child.
	public javax.swing.JComponent displayColumn = null;  // set by SpinCADFrame after layout
	// displayIsVisible: show the logger+scope split pane when simulator runs
	public boolean displayIsVisible = false;

	private SpinCADFrame frame;

	public simControlToolBar sctb;
	public ScopeToolBar stb;

	private SpinCADPatch patch;

	public SpinCADSimulator(SpinCADFrame f, SpinCADPatch p) {
		frame = f;
		patch = p;
		this.sctb = new simControlToolBar(frame);
		this.stb  = new ScopeToolBar();
		prefs = Preferences.userNodeForPackage(this.getClass());
	}

	public boolean isSimRunning() { return simRunning; }

	public boolean setSimRunning(boolean simRunning) {
		this.simRunning = simRunning;
		return simRunning;
	}

	/** Show the currently-selected display card (or hide if neither selected). */
	public void switchDisplay() {
		if(displayColumn == null) return;
		displayColumn.setVisible(displayIsVisible);
		stb.updateVisibility(displayIsVisible);
		frame.validate();
	}

	public void updateSliders(SpinCADPatch p) {
		this.patch = p;
		sctb.updateSimSliders();
	}

	public void setOutputFileMode(Boolean state) {
		outputFile = state ? prefs.get("SIMULATOR_OUT_FILE", "") : null;
	}

	// ======================================================================================================
	class simControlToolBar extends JToolBar implements ActionListener, ChangeListener {

		private static final long serialVersionUID = 7552645224196206164L;

		final JButton btnStartSimulation = new JButton("Start Simulation");
		final JButton btnSigGen = new JButton("Sig Gen Sim");

		final JSlider pot0Slider = new JSlider(0, 100, 1);
		final JSlider pot1Slider = new JSlider(0, 100, 1);
		final JSlider pot2Slider = new JSlider(0, 100, 1);

		public simControlToolBar(JFrame frame) {
			super();
			this.add(btnStartSimulation);
			btnStartSimulation.addActionListener(this);

			this.add(pot0Slider);
			pot0Slider.addChangeListener(this);
			pot0Slider.setMajorTickSpacing(10);
			pot0Slider.setMinorTickSpacing(5);
			pot0Slider.setPaintTicks(true);
			pot0Slider.setToolTipText("Pot 0");

			this.add(pot1Slider);
			pot1Slider.addChangeListener(this);
			pot1Slider.setMajorTickSpacing(10);
			pot1Slider.setMinorTickSpacing(5);
			pot1Slider.setPaintTicks(true);
			pot1Slider.setToolTipText("Pot 1");

			this.add(pot2Slider);
			pot2Slider.addChangeListener(this);
			pot2Slider.setMajorTickSpacing(10);
			pot2Slider.setMinorTickSpacing(5);
			pot2Slider.setPaintTicks(true);
			pot2Slider.setToolTipText("Pot 2");

			if(sim != null) updateSimSliders();
		}

		public void updateSimSliders() {
			pot0Slider.setValue((int) patch.getPotVal(0));
			pot1Slider.setValue((int) patch.getPotVal(1));
			pot2Slider.setValue((int) patch.getPotVal(2));
		}

		public void setSimPotValues() {
			if(sim != null) {
				sim.setPot(0, patch.getPotVal(0) / 100.0);
				sim.setPot(1, patch.getPotVal(1) / 100.0);
				sim.setPot(2, patch.getPotVal(2) / 100.0);
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			if(e.getSource() == pot0Slider) {
				patch.setPotVal(0, (double) pot0Slider.getValue());
				pot0Slider.setToolTipText("Pot 0: " + (patch.getPotVal(0) / 100.0));
				if(sim != null) sim.setPot(0, patch.getPotVal(0) / 100.0);
			} else if(e.getSource() == pot1Slider) {
				patch.setPotVal(1, (double) pot1Slider.getValue());
				pot1Slider.setToolTipText("Pot 1: " + (patch.getPotVal(1) / 100.0));
				if(sim != null) sim.setPot(1, patch.getPotVal(1) / 100.0);
			} else if(e.getSource() == pot2Slider) {
				patch.setPotVal(2, (double) pot2Slider.getValue());
				pot2Slider.setToolTipText("Pot 2: " + (patch.getPotVal(2) / 100.0));
				if(sim != null) sim.setPot(2, patch.getPotVal(2) / 100.0);
			}
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == btnStartSimulation) {
				if(isSimRunning()) {
					// --- Stop ---
					setSimRunning(false);
					if(displayColumn != null) displayColumn.setVisible(false);
					levelMonitor.setVisible(false);
					btnStartSimulation.setText(" Start Simulator");
					sim.stopSimulator();
					stb.updateVisibility(false);
				} else {
					// --- Start ---
					String testWavFileName = checkSimulatorFile();
					if(testWavFileName != "Not found!") {
						setSimRunning(true);
						btnStartSimulation.setText(" Stop Simulator ");
						frame.updateAll();
						sim = new SpinSimulator(
								patch.patchModel.getRenderBlock(),
								testWavFileName, outputFile,
								patch.getPotVal(0), patch.getPotVal(1), patch.getPotVal(2));

						sim.showLevelLogger(loggerPanel);
						sim.showScope(scopePanel);
						restoreScopePrefs();

						// Show/hide display and force layout before audio starts
						switchDisplay();
						setSimPotValues();
						sim.start();
					} else {
						SpinCADDialogs.MessageBox(frame, "Simulator file not found.",
								"Please set a simulator source file.");
					}
				}
			} else if(arg0.getSource() == btnSigGen) {
				if(isSimRunning()) {
					setSimRunning(false);
					btnSigGen.setText("Start Signal");
					sim.stopSimulator();
				} else {
					setSimRunning(true);
					btnSigGen.setText("Stop Signal");
				}
			}
		}

		private void restoreScopePrefs() {
			if(sim == null || sim.scope == null) return;

			// Restore ch1 gain — update both the scope and the combo box display
			String gain1 = prefs.get("CH1_VERT_GAIN", "8x");
			switch(gain1) {
			case "1x":  sim.scope.setScopeCh1Gain(19); break;
			case "2x":  sim.scope.setScopeCh1Gain(18); break;
			case "4x":  sim.scope.setScopeCh1Gain(17); break;
			case "8x":  sim.scope.setScopeCh1Gain(16); break;
			case "16x": sim.scope.setScopeCh1Gain(15); break;
			default:    sim.scope.setScopeCh1Gain(16); gain1 = "8x"; break;
			}
			// Suppress the ActionEvent so we don't double-apply on combo change
			stb.ch1_Vertical_Gain.removeActionListener(stb);
			stb.ch1_Vertical_Gain.setSelectedItem(gain1);
			stb.ch1_Vertical_Gain.addActionListener(stb);

			// Restore ch2 gain
			String gain2 = prefs.get("CH2_VERT_GAIN", "8x");
			switch(gain2) {
			case "1x":  sim.scope.setScopeCh2Gain(19); break;
			case "2x":  sim.scope.setScopeCh2Gain(18); break;
			case "4x":  sim.scope.setScopeCh2Gain(17); break;
			case "8x":  sim.scope.setScopeCh2Gain(16); break;
			case "16x": sim.scope.setScopeCh2Gain(15); break;
			default:    sim.scope.setScopeCh2Gain(16); gain2 = "8x"; break;
			}
			stb.ch2_Vertical_Gain.removeActionListener(stb);
			stb.ch2_Vertical_Gain.setSelectedItem(gain2);
			stb.ch2_Vertical_Gain.addActionListener(stb);

			// Restore timebase — normalise stored value to match combo item strings
			String msStr = prefs.get("MsPerDiv", "10");
			double msVal = 10.0;
			try {
				msVal = Double.parseDouble(msStr);
				// Normalise "10.0" -> "10" so setSelectedItem finds the combo entry
				msStr = (msVal == Math.floor(msVal)) ? String.valueOf((int) msVal) : msStr;
			} catch(NumberFormatException ignored) { msStr = "10"; msVal = 10.0; }
			stb.timebase.removeActionListener(stb);
			stb.timebase.setSelectedItem(msStr);
			stb.timebase.addActionListener(stb);
			sim.scope.setMsPerDivision(msVal);
			stb.applyMsPerDiv(msVal);
		}
	}



	// ======================================================================================================
	// TraceButton — a custom-painted button whose background is LAF-independent.
	// Swing's system LAF delegates ignore setBackground() on JButton on macOS/Windows;
	// this class owns its entire paint so colour is always exactly what we set.
	// ======================================================================================================
	public class TraceButton extends JComponent {
		private static final long serialVersionUID = 1L;

		private String  label;
		private Color   bg;
		private Color   fg;
		private boolean enabled2 = true;   // logical enable (separate from Swing enabled)
		private boolean hovered  = false;

		// Arc radius for the rounded rectangle
		private static final int ARC = 6;

		public TraceButton(String label, Color bg, Color fg) {
			this.label = label;
			this.bg    = bg;
			this.fg    = fg;
			setOpaque(false);   // we paint everything ourselves
			setPreferredSize(new Dimension(64, 24));
			setMaximumSize(new Dimension(64, 24));
			setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));

			addMouseListener(new MouseAdapter() {
				@Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
				@Override public void mouseExited (MouseEvent e) { hovered = false; repaint(); }
				@Override public void mouseClicked(MouseEvent e) {
					if(isEnabled()) fireActionPerformed();
				}
			});
		}

		/** Set the fill colour and repaint. */
		public void setBgColor(Color c) { bg = c; repaint(); }
		public void setFgColor(Color c) { fg = c; repaint(); }
		public void setLabel(String s)  { label = s; repaint(); }

		/** Logical dim — darkens the button without disabling Swing interaction. */
		public void setEnabled2(boolean on) { enabled2 = on; repaint(); }

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();

			// Background fill — dim when logically disabled, lighten on hover
			Color fill = enabled2 ? bg : bg.darker().darker();
			if(hovered && isEnabled()) fill = fill.brighter();
			g2.setColor(fill);
			g2.fillRoundRect(1, 1, w - 2, h - 2, ARC, ARC);

			// Subtle border
			g2.setColor(fill.darker());
			g2.drawRoundRect(1, 1, w - 2, h - 2, ARC, ARC);

			// Label
			g2.setColor(enabled2 ? fg : fg.darker());
			g2.setFont(getFont());
			FontMetrics fm = g2.getFontMetrics();
			int tx = (w - fm.stringWidth(label)) / 2;
			int ty = (h - fm.getHeight()) / 2 + fm.getAscent();
			g2.drawString(label, tx, ty);

			g2.dispose();
		}

		// Minimal action-listener support
		private java.util.List<ActionListener> listeners = new java.util.ArrayList<>();
		public void addActionListener(ActionListener l)    { listeners.add(l); }
		public void removeActionListener(ActionListener l) { listeners.remove(l); }
		private void fireActionPerformed() {
			ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, label);
			for(ActionListener l : listeners) l.actionPerformed(e);
		}
	}

	// ======================================================================================================
	// ScopeToolBar
	// ======================================================================================================
	public class ScopeToolBar extends JToolBar implements ActionListener, ChangeListener {

		private static final long serialVersionUID = -3040642773216953900L;

		// Slightly darker base colours than the traces (trace is 0,210,80 / 210,170,0)
		final Color CH1_COLOR = new Color(0, 175, 65);
		final Color CH2_COLOR = new Color(185, 145, 0);
		final Color FREEZE_OFF_COLOR = new Color(80, 80, 90);
		final Color FREEZE_ON_COLOR  = new Color(30, 30, 40);

		// Custom-painted buttons — immune to LAF background overrides
		final TraceButton btnCh1Enable = new TraceButton("● Ch 1", CH1_COLOR, Color.WHITE);
		final TraceButton btnCh2Enable = new TraceButton("● Ch 2", CH2_COLOR, Color.BLACK);
		final TraceButton btnFreeze    = new TraceButton("❚❚ Freeze", FREEZE_OFF_COLOR, Color.WHITE);

		final JLabel ch1_Vertical_Gain_Label = new JLabel(" Ch 1 Gain: ");
		String[] gainLabels = new String[] {"1x", "2x", "4x", "8x", "16x"};
		JComboBox<String> ch1_Vertical_Gain = new JComboBox<>(gainLabels);

		final JLabel ch2_Vertical_Gain_Label = new JLabel(" Ch 2 Gain: ");
		JComboBox<String> ch2_Vertical_Gain = new JComboBox<>(gainLabels);

		String[] timebaseLabels = new String[] {"1", "2", "5", "10", "20", "50", "100", "200", "500"};
		final JLabel timebaseLabel = new JLabel(" Time/div (ms): ");
		JComboBox<String> timebase = new JComboBox<>(timebaseLabels);

		public ScopeToolBar() {
			super();
			setBackground(new Color(45, 45, 50));
			setOpaque(true);

			setPreferredSize(new Dimension(800, 32));
			setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

			Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
			Dimension cbDim = new Dimension(80, 24);

			btnCh1Enable.setToolTipText("Toggle Channel 1 trace on/off");
			btnCh1Enable.addActionListener(this);
			add(btnCh1Enable);

			addSeparator(new Dimension(6, 24));

			btnCh2Enable.setToolTipText("Toggle Channel 2 trace on/off");
			btnCh2Enable.addActionListener(this);
			add(btnCh2Enable);

			addSeparator(new Dimension(6, 24));

			btnFreeze.setToolTipText("Freeze scope at end of current sweep");
			btnFreeze.addActionListener(this);
			add(btnFreeze);

			addSeparator(new Dimension(8, 24));

			ch1_Vertical_Gain.setToolTipText("Ch 1 vertical gain");
			ch1_Vertical_Gain.setPreferredSize(cbDim);
			ch1_Vertical_Gain.setMaximumSize(cbDim);
			ch1_Vertical_Gain.setBorder(border);
			ch1_Vertical_Gain.setSelectedItem("8x");
			ch1_Vertical_Gain.addActionListener(this);
			ch1_Vertical_Gain_Label.setForeground(new Color(200, 200, 200));
			add(ch1_Vertical_Gain_Label);
			add(ch1_Vertical_Gain);

			ch2_Vertical_Gain.setToolTipText("Ch 2 vertical gain");
			ch2_Vertical_Gain.setPreferredSize(cbDim);
			ch2_Vertical_Gain.setMaximumSize(cbDim);
			ch2_Vertical_Gain.setBorder(border);
			ch2_Vertical_Gain.setSelectedItem("8x");
			ch2_Vertical_Gain.addActionListener(this);
			ch2_Vertical_Gain_Label.setForeground(new Color(200, 200, 200));
			add(ch2_Vertical_Gain_Label);
			add(ch2_Vertical_Gain);

			addSeparator(new Dimension(8, 24));

			timebase.setToolTipText("Milliseconds per horizontal division");
			timebase.setPreferredSize(cbDim);
			timebase.setMaximumSize(cbDim);
			timebase.setBorder(border);
			timebase.setSelectedItem("10");
			timebase.addActionListener(this);
			timebaseLabel.setForeground(new Color(200, 200, 200));
			add(timebaseLabel);
			add(timebase);

			setVisible(false);
		}

		public void updateVisibility(boolean running) {
			setVisible(running);
			if(running) refreshButtonStates();
		}

		private void refreshButtonStates() {
			if(sim == null || sim.scope == null) return;

			boolean frozen = sim.scope.isFrozen();

			// Ch1 — enabled2=true (bright) when trace on, false (dim) when off.
			// Fully disabled (no clicks) when scope is frozen.
			boolean ch1on = sim.scope.isCh1Enabled();
			btnCh1Enable.setEnabled2(ch1on);
			btnCh1Enable.setEnabled(!frozen);

			// Ch2
			boolean ch2on = sim.scope.isCh2Enabled();
			btnCh2Enable.setEnabled2(ch2on);
			btnCh2Enable.setEnabled(!frozen);

			// Freeze
			btnFreeze.setBgColor(frozen ? FREEZE_ON_COLOR : FREEZE_OFF_COLOR);
			btnFreeze.setLabel(frozen ? "▶ Unfreeze" : "❚❚ Freeze");
		}

		void applyMsPerDiv(double ms) {
			if(sim == null || sim.scope == null) return;
			sim.scope.setMsPerDivision(ms);
			int sampleRate = org.andrewkilpatrick.elmGen.ElmProgram.SAMPLERATE;
			double samplesPerDiv = ms * sampleRate / 1000.0;
			int ratio = Math.max(1, (int) Math.round(samplesPerDiv / 100.0));
			sim.scope.setWindowRatio(ratio);
			// Store as a plain integer string so it matches the combo items exactly.
			// e.g. 10.0 -> "10", not "10.0" which would fail setSelectedItem lookup.
			String key = (ms == Math.floor(ms)) ? String.valueOf((int) ms) : String.valueOf(ms);
			prefs.put("MsPerDiv", key);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == btnCh1Enable) {
				if(sim != null && sim.scope != null) {
					sim.scope.setCh1Enabled(!sim.scope.isCh1Enabled());
					refreshButtonStates();
					sim.scope.redrawFrozen();
				}
			} else if(arg0.getSource() == btnCh2Enable) {
				if(sim != null && sim.scope != null) {
					sim.scope.setCh2Enabled(!sim.scope.isCh2Enabled());
					refreshButtonStates();
					sim.scope.redrawFrozen();
				}
			} else if(arg0.getSource() == btnFreeze) {
				if(sim != null && sim.scope != null) {
					sim.scope.setFrozen(!sim.scope.isFrozen());
					refreshButtonStates();
				}
			} else if(arg0.getSource() == ch1_Vertical_Gain) {
				String gain = (String) ch1_Vertical_Gain.getSelectedItem();
				if(sim != null && sim.scope != null) {
					switch(gain) {
					case "1x":  sim.scope.setScopeCh1Gain(19); break;
					case "2x":  sim.scope.setScopeCh1Gain(18); break;
					case "4x":  sim.scope.setScopeCh1Gain(17); break;
					case "8x":  sim.scope.setScopeCh1Gain(16); break;
					case "16x": sim.scope.setScopeCh1Gain(15); break;
					}
					prefs.put("CH1_VERT_GAIN", gain);
				}
			} else if(arg0.getSource() == ch2_Vertical_Gain) {
				String gain = (String) ch2_Vertical_Gain.getSelectedItem();
				if(sim != null && sim.scope != null) {
					switch(gain) {
					case "1x":  sim.scope.setScopeCh2Gain(19); break;
					case "2x":  sim.scope.setScopeCh2Gain(18); break;
					case "4x":  sim.scope.setScopeCh2Gain(17); break;
					case "8x":  sim.scope.setScopeCh2Gain(16); break;
					case "16x": sim.scope.setScopeCh2Gain(15); break;
					}
					prefs.put("CH2_VERT_GAIN", gain);
				}
			} else if(arg0.getSource() == timebase) {
				String val = (String) timebase.getSelectedItem();
				try { applyMsPerDiv(Double.parseDouble(val)); }
				catch(NumberFormatException ignored) {}
			}
		}

		@Override
		public void stateChanged(ChangeEvent e) {}
	}

	public String checkSimulatorFile() {
		String testWavFileName = prefs.get("SIMULATOR_FILE", "");
		if(testWavFileName.equals("")) {
			System.out.println("Simulator file name blank!");
			return "Not found!";
		}
		try {
			File f = new File(testWavFileName);
			if(f.exists() && !f.isDirectory()) return testWavFileName;
		} catch(Exception e) {
			System.out.println("Exception opening file!");
		}
		return "Not found!";
	}

	public void getSimulatorFile() throws UnsupportedAudioFileException, IOException {
		String testWavFileName = prefs.get("SIMULATOR_FILE", "");
		final JFileChooser fc = new JFileChooser(testWavFileName);
		fc.setSelectedFile(new File(testWavFileName));
		fc.setFileFilter(new FileNameExtensionFilter("WAV files", "wav"));
		int returnVal = fc.showOpenDialog(frame);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			testWavFileName = fc.getSelectedFile().getPath();
			prefs.put("SIMULATOR_FILE", testWavFileName);
			System.out.println("Opening: " + testWavFileName);
			new AudioFileReader(testWavFileName, false);
		}
	}

	public void setSimulatorDebugFile() throws IOException {
		String debugFileName = prefs.get("SIMULATOR_DEBUG_FILE", "");
		final JFileChooser fc = new JFileChooser(debugFileName);
		fc.setSelectedFile(new File(debugFileName));
		fc.setFileFilter(new FileNameExtensionFilter("txt files", "txt"));
		if(fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			prefs.put("SIMULATOR_DEBUG_FILE", fc.getSelectedFile().getPath());
		}
	}

	public void setSimulatorOutputFile() {
		String simWavOutFileName = prefs.get("SIMULATOR_OUT_FILE", "");
		final JFileChooser fc = new JFileChooser(simWavOutFileName);
		fc.setSelectedFile(new File(simWavOutFileName));
		fc.setFileFilter(new FileNameExtensionFilter("WAV files", "wav"));
		if(fc.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
			simWavOutFileName = fc.getSelectedFile().getPath();
			prefs.put("SIMULATOR_OUT_FILE", simWavOutFileName);
			System.out.println("Simulator output file: " + simWavOutFileName);
		}
	}
}
