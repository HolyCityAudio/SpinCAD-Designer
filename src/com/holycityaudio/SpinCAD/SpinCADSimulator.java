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
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andrewkilpatrick.elmGen.simulator.AudioFileReader;
import org.andrewkilpatrick.elmGen.simulator.LevelLogger.ScopePanel;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;

public class SpinCADSimulator {
	private boolean simRunning = false;
	public SpinSimulator sim;
	public String outputFile = null;

	private Preferences prefs;
	public JPanel levelMonitor = new JPanel();

	// scopePanel is declared as ScopePanel so the double-buffered paintComponent
	// is in effect from the moment it is added to the layout.
	public ScopePanel scopePanel = new ScopePanel();

	// displayColumn: container panel — switchDisplay() swaps in the active child.
	public javax.swing.JComponent displayColumn = null;  // set by SpinCADFrame after layout
	// displayIsVisible: show the logger+scope split pane when simulator runs
	public boolean displayIsVisible = false;

	// CardLayout for swapping Y-axis labels between scope and logger modes
	public java.awt.CardLayout labelCardLayout = new java.awt.CardLayout();
	public JPanel labelCards = new JPanel(labelCardLayout);

	// Current display mode: 0 = scope, 1 = logger
	private int displayMode = 0;

	// Reference to scope amplitude label panel for Lin/dB toggle
	public org.andrewkilpatrick.elmGen.simulator.LevelLogger.AmplitudeLabelPanel ampLabelPanel;

	// Lin/dB toggle button — placed under the label panel by SpinCADFrame
	public TraceButton btnLinDb;

	private SpinCADFrame frame;

	public simControlToolBar sctb;
	public ScopeToolBar stb;

	private SpinCADPatch patch;

	public SpinCADSimulator(SpinCADFrame f, SpinCADPatch p) {
		frame = f;
		patch = p;
		this.sctb = new simControlToolBar(frame);
		this.stb  = new ScopeToolBar();
		this.btnLinDb = new TraceButton("dB", new java.awt.Color(80, 80, 90), java.awt.Color.WHITE);
		btnLinDb.setToolTipText("Toggle between linear and dB scale labels");
		btnLinDb.setPreferredSize(new Dimension(58, 32));
		btnLinDb.setMinimumSize(new Dimension(58, 32));
		btnLinDb.setMaximumSize(new Dimension(58, 32));
		btnLinDb.addActionListener(e -> {
			if(ampLabelPanel != null) {
				boolean nowDB = !ampLabelPanel.isDBMode();
				ampLabelPanel.setDBMode(nowDB);
				btnLinDb.setLabel(nowDB ? "dB" : "Lin");
				if(sim != null && sim.scope != null) {
					sim.scope.setLinMode(!nowDB);
				}
			}
		});
		prefs = Preferences.userNodeForPackage(this.getClass());
		// Restore output file mode from preferences
		if (prefs.getBoolean("OUTPUT_FILE_MODE", false)) {
			setOutputFileMode(true);
		}
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
		if (state) {
			String path = prefs.get("SIMULATOR_OUT_FILE", "");
			outputFile = (path != null && !path.isEmpty()) ? path : null;
		} else {
			outputFile = null;
		}
	}

	// Track whether scope probes are active for toolbar button visibility
	boolean probe1Active = false;
	boolean probe2Active = false;

	private void scanForScopeProbe() {
		probe1Active = false;
		probe2Active = false;
		if (patch == null || patch.patchModel == null) return;
		java.util.Iterator<SpinCADBlock> itr = patch.patchModel.blockList.iterator();
		while (itr.hasNext()) {
			SpinCADBlock b = itr.next();
			if (b instanceof com.holycityaudio.SpinCAD.CADBlocks.ScopeProbeCADBlock) {
				com.holycityaudio.SpinCAD.CADBlocks.ScopeProbeCADBlock probe =
						(com.holycityaudio.SpinCAD.CADBlocks.ScopeProbeCADBlock) b;
				int reg1 = probe.getScope1Reg();
				int reg2 = probe.getScope2Reg();
				probe1Active = (reg1 >= 0);
				probe2Active = (reg2 >= 0);
				sim.setScopeRegisters(reg1, reg2);
				if (sim.scope != null) {
					sim.scope.setProbe1Active(probe1Active);
					sim.scope.setProbe2Active(probe2Active);
				}
				break;  // only use the first probe block
			}
		}
		stb.updateProbeButtons(probe1Active, probe2Active);
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
					frame.etb.statusMessage.setText("");
				} else {
					// --- Start ---
					String testWavFileName = checkSimulatorFile();
					if(testWavFileName != "Not found!") {
						// Ensure any previous simulator thread is fully stopped
						if (sim != null) {
							sim.stopSimulator();
							try { sim.join(2000); } catch (InterruptedException ignored) {}
						}
						setSimRunning(true);
						btnStartSimulation.setText(" Stop Simulator ");
						frame.updateAll();
						sim = new SpinSimulator(
								patch.patchModel.getRenderBlock(),
								testWavFileName, outputFile,
								patch.getPotVal(0), patch.getPotVal(1), patch.getPotVal(2));

						// Apply loop mode and real-time mode from preferences
						boolean realTime = prefs.getBoolean("REALTIME_FILE_SIM", false);
						if (outputFile != null && realTime) {
							sim.setLoopMode(true);
							sim.setRealTimeMode(true);
							System.out.println("Simulator: File mode (real-time), loop=true, outputFile=" + outputFile);
						} else if (outputFile != null) {
							sim.setLoopMode(false);
							sim.setRealTimeMode(false);
							System.out.println("Simulator: File mode (fast), loop=false, outputFile=" + outputFile);
						} else {
							// Sound Card mode: always loop (sound card blocks, needs continuous audio)
							sim.setLoopMode(true);
							sim.setRealTimeMode(false);
							System.out.println("Simulator: Sound Card mode, loop=true");
						}

						sim.showDisplay(scopePanel);

						// Scan for ScopeProbeCADBlock and configure scope registers
						// (must be after showDisplay which creates the LevelLogger)
						scanForScopeProbe();

						// Apply current display mode (scope or logger)
						if(displayMode == 1) {
							sim.setDisplayMode(1);
						}
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
					frame.etb.statusMessage.setText("");
				} else {
					setSimRunning(true);
					btnSigGen.setText("Stop Signal");
				}
			}
		}

		private void restoreScopePrefs() {
			if(sim == null || sim.scope == null) return;

			// Restore ch1 gain — update both the scope and the combo box display
			String gain1 = prefs.get("CH1_VERT_GAIN", "1x");
			switch(gain1) {
			case "1x":  sim.scope.setScopeCh1Gain(16); break;
			case "2x":  sim.scope.setScopeCh1Gain(15); break;
			case "4x":  sim.scope.setScopeCh1Gain(14); break;
			case "8x":  sim.scope.setScopeCh1Gain(13); break;
			case "16x": sim.scope.setScopeCh1Gain(12); break;
			default:    sim.scope.setScopeCh1Gain(16); gain1 = "1x"; break;
			}
			// Suppress the ActionEvent so we don't double-apply on combo change
			stb.ch1_Vertical_Gain.removeActionListener(stb);
			stb.ch1_Vertical_Gain.setSelectedItem(gain1);
			stb.ch1_Vertical_Gain.addActionListener(stb);

			// Restore ch2 gain
			String gain2 = prefs.get("CH2_VERT_GAIN", "1x");
			switch(gain2) {
			case "1x":  sim.scope.setScopeCh2Gain(16); break;
			case "2x":  sim.scope.setScopeCh2Gain(15); break;
			case "4x":  sim.scope.setScopeCh2Gain(14); break;
			case "8x":  sim.scope.setScopeCh2Gain(13); break;
			case "16x": sim.scope.setScopeCh2Gain(12); break;
			default:    sim.scope.setScopeCh2Gain(16); gain2 = "1x"; break;
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
			setMinimumSize(new Dimension(64, 24));
			setMaximumSize(new Dimension(64, 24));
			setAlignmentY(CENTER_ALIGNMENT);
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

			// Background fill — dim when logically or Swing disabled, lighten on hover
			Color fill = (enabled2 && isEnabled()) ? bg : bg.darker().darker();
			if(hovered && isEnabled()) fill = fill.brighter();
			g2.setColor(fill);
			g2.fillRoundRect(1, 1, w - 2, h - 2, ARC, ARC);

			// Subtle border
			g2.setColor(fill.darker());
			g2.drawRoundRect(1, 1, w - 2, h - 2, ARC, ARC);

			// Label
			g2.setColor((enabled2 && isEnabled()) ? fg : fg.darker());
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

		final Color MODE_SCOPE_COLOR  = new Color(60, 80, 130);
		final Color MODE_LOGGER_COLOR = new Color(100, 60, 120);

		final Color PROBE1_COLOR = new Color(0, 160, 180);    // cyan
		final Color PROBE2_COLOR = new Color(180, 0, 180);    // magenta

		// Custom-painted buttons — immune to LAF background overrides
		final TraceButton btnModeToggle = new TraceButton("Scope", MODE_SCOPE_COLOR, Color.WHITE);
		final TraceButton btnCh1Enable = new TraceButton("● Ch 1", CH1_COLOR, Color.WHITE);
		final TraceButton btnCh2Enable = new TraceButton("● Ch 2", CH2_COLOR, Color.BLACK);
		final TraceButton btnFreeze    = new TraceButton("❚❚ Freeze", FREEZE_OFF_COLOR, Color.WHITE);

		// Probe buttons — created once, shown/hidden based on ScopeProbeCADBlock presence
		final TraceButton btnProbe1 = new TraceButton("● Probe 1", PROBE1_COLOR, Color.WHITE);
		final TraceButton btnProbe2 = new TraceButton("● Probe 2", PROBE2_COLOR, Color.WHITE);

		final JLabel ch1_Vertical_Gain_Label = new JLabel(" Ch 1 Gain: ");
		String[] gainLabels = new String[] {"1x", "2x", "4x", "8x", "16x"};
		JComboBox<String> ch1_Vertical_Gain = new JComboBox<>(gainLabels);

		final JLabel ch2_Vertical_Gain_Label = new JLabel(" Ch 2 Gain: ");
		JComboBox<String> ch2_Vertical_Gain = new JComboBox<>(gainLabels);

		String[] timebaseLabels = new String[] {"1", "2", "5", "10", "20", "50", "100", "200", "500"};
		final JLabel timebaseLabel = new JLabel(" Time/div (ms): ");
		JComboBox<String> timebase = new JComboBox<>(timebaseLabels);

		// Separators between scope-specific controls (hidden in logger mode)
		final java.util.List<javax.swing.JToolBar.Separator> scopeSeparators = new java.util.ArrayList<>();

		public ScopeToolBar() {
			super();
			setBackground(new Color(45, 45, 50));
			setOpaque(true);

			setPreferredSize(new Dimension(800, 32));
			setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

			Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
			Dimension cbDim = new Dimension(80, 24);

			btnModeToggle.setToolTipText("Switch between Scope and Level Logger");
			btnModeToggle.addActionListener(this);
			add(btnModeToggle);

			addSeparator(new Dimension(8, 24));

			btnCh1Enable.setToolTipText("Toggle Channel 1 trace on/off");
			btnCh1Enable.addActionListener(this);
			add(btnCh1Enable);

			addSeparator(new Dimension(6, 24));

			btnCh2Enable.setToolTipText("Toggle Channel 2 trace on/off");
			btnCh2Enable.addActionListener(this);
			add(btnCh2Enable);

			addSeparator(new Dimension(6, 24));

			btnFreeze.setToolTipText("Freeze display at end of current sweep");
			btnFreeze.addActionListener(this);
			add(btnFreeze);

			addSeparator(new Dimension(6, 24));

			btnProbe1.setToolTipText("Toggle Probe 1 trace on/off");
			btnProbe1.addActionListener(this);
			btnProbe1.setVisible(false);
			add(btnProbe1);

			addSeparator(new Dimension(6, 24));

			btnProbe2.setToolTipText("Toggle Probe 2 trace on/off");
			btnProbe2.addActionListener(this);
			btnProbe2.setVisible(false);
			add(btnProbe2);

			scopeSeparators.add(addScopeSeparator(8));

			ch1_Vertical_Gain.setToolTipText("Ch 1 vertical gain");
			ch1_Vertical_Gain.setPreferredSize(cbDim);
			ch1_Vertical_Gain.setMaximumSize(cbDim);
			ch1_Vertical_Gain.setBorder(border);
			ch1_Vertical_Gain.setSelectedItem("1x");
			ch1_Vertical_Gain.addActionListener(this);
			ch1_Vertical_Gain_Label.setForeground(new Color(200, 200, 200));
			add(ch1_Vertical_Gain_Label);
			add(ch1_Vertical_Gain);

			ch2_Vertical_Gain.setToolTipText("Ch 2 vertical gain");
			ch2_Vertical_Gain.setPreferredSize(cbDim);
			ch2_Vertical_Gain.setMaximumSize(cbDim);
			ch2_Vertical_Gain.setBorder(border);
			ch2_Vertical_Gain.setSelectedItem("1x");
			ch2_Vertical_Gain.addActionListener(this);
			ch2_Vertical_Gain_Label.setForeground(new Color(200, 200, 200));
			add(ch2_Vertical_Gain_Label);
			add(ch2_Vertical_Gain);

			scopeSeparators.add(addScopeSeparator(8));

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
			if(running) {
				refreshButtonStates();
				setScopeControlsVisible(displayMode == 0);
			}
		}

		public void updateProbeButtons(boolean p1Active, boolean p2Active) {
			btnProbe1.setVisible(p1Active && displayMode == 0);
			btnProbe2.setVisible(p2Active && displayMode == 0);
		}

		/** Add a separator and return it so it can be tracked for visibility. */
		private javax.swing.JToolBar.Separator addScopeSeparator(int width) {
			javax.swing.JToolBar.Separator sep = new javax.swing.JToolBar.Separator(new Dimension(width, 24));
			add(sep);
			return sep;
		}

		/** Show/hide scope-specific controls (gain, timebase, ch enable, freeze, separators). */
		void setScopeControlsVisible(boolean visible) {
			ch1_Vertical_Gain_Label.setVisible(visible);
			ch1_Vertical_Gain.setVisible(visible);
			ch2_Vertical_Gain_Label.setVisible(visible);
			ch2_Vertical_Gain.setVisible(visible);
			timebaseLabel.setVisible(visible);
			timebase.setVisible(visible);
			for(javax.swing.JToolBar.Separator sep : scopeSeparators) {
				sep.setVisible(visible);
			}
			// Update mode toggle button
			btnModeToggle.setLabel(visible ? "Scope" : "Levels");
			btnModeToggle.setBgColor(visible ? MODE_SCOPE_COLOR : MODE_LOGGER_COLOR);
			// Show/hide probe buttons based on scope mode and probe presence
			btnProbe1.setVisible(visible && probe1Active);
			btnProbe2.setVisible(visible && probe2Active);
			// Disable Lin/dB toggle in logger mode — logger is always dB
			btnLinDb.setEnabled(visible);
			if(!visible) {
				if(ampLabelPanel != null) ampLabelPanel.setDBMode(true);
				btnLinDb.setLabel("dB");
				if(sim != null && sim.scope != null) sim.scope.setLinMode(false);
			}
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
			if(arg0.getSource() == btnModeToggle) {
				// Toggle between scope (0) and logger (1)
				displayMode = (displayMode == 0) ? 1 : 0;
				if(sim != null) {
					sim.setDisplayMode(displayMode);
					if(displayMode == 0) {
						// Switching back to scope — restore timebase-based windowRatio
						String val = (String) timebase.getSelectedItem();
						try { applyMsPerDiv(Double.parseDouble(val)); }
						catch(NumberFormatException ignored) {}
					}
				}
				setScopeControlsVisible(displayMode == 0);
				refreshButtonStates();
				labelCardLayout.show(labelCards, displayMode == 0 ? "scope" : "logger");
			} else if(arg0.getSource() == btnCh1Enable) {
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
					case "1x":  sim.scope.setScopeCh1Gain(16); break;
					case "2x":  sim.scope.setScopeCh1Gain(15); break;
					case "4x":  sim.scope.setScopeCh1Gain(14); break;
					case "8x":  sim.scope.setScopeCh1Gain(13); break;
					case "16x": sim.scope.setScopeCh1Gain(12); break;
					}
					prefs.put("CH1_VERT_GAIN", gain);
				}
			} else if(arg0.getSource() == ch2_Vertical_Gain) {
				String gain = (String) ch2_Vertical_Gain.getSelectedItem();
				if(sim != null && sim.scope != null) {
					switch(gain) {
					case "1x":  sim.scope.setScopeCh2Gain(16); break;
					case "2x":  sim.scope.setScopeCh2Gain(15); break;
					case "4x":  sim.scope.setScopeCh2Gain(14); break;
					case "8x":  sim.scope.setScopeCh2Gain(13); break;
					case "16x": sim.scope.setScopeCh2Gain(12); break;
					}
					prefs.put("CH2_VERT_GAIN", gain);
				}
			} else if(arg0.getSource() == timebase) {
				String val = (String) timebase.getSelectedItem();
				try { applyMsPerDiv(Double.parseDouble(val)); }
				catch(NumberFormatException ignored) {}
			} else if(arg0.getSource() == btnProbe1) {
				if(sim != null && sim.scope != null) {
					sim.scope.setProbe1Enabled(!sim.scope.isProbe1Enabled());
					btnProbe1.setEnabled2(sim.scope.isProbe1Enabled());
				}
			} else if(arg0.getSource() == btnProbe2) {
				if(sim != null && sim.scope != null) {
					sim.scope.setProbe2Enabled(!sim.scope.isProbe2Enabled());
					btnProbe2.setEnabled2(sim.scope.isProbe2Enabled());
				}
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
