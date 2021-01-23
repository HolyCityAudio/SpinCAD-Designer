package com.holycityaudio.SpinCAD;

import java.awt.Dimension;
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
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andrewkilpatrick.elmGen.simulator.AudioFileReader;
import org.andrewkilpatrick.elmGen.simulator.LevelLogger.triggerMode;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;

public class SpinCADSimulator {
	private boolean simRunning = false;
	public SpinSimulator sim;
	// simulator output file
	public String outputFile = null; 				// play out through the sound card

	private Preferences prefs;
	public JPanel levelMonitor = new JPanel();
	public JPanel loggerPanel = new JPanel();		// ame
	public JPanel scopePanel = new JPanel();		// e

	public boolean loggerIsVisible = false;
	public boolean scopeIsVisible = false;

	private SpinCADFrame frame;

	public simControlToolBar sctb;
	public ScopeToolBar stb;

	private SpinCADPatch patch;

	// constructor
	public SpinCADSimulator(SpinCADFrame f, SpinCADPatch p) {
		frame = f;
		patch = p;
		this.sctb = new simControlToolBar(frame);
		this.stb = new ScopeToolBar();
		prefs = Preferences.userNodeForPackage(this.getClass());
	}

	// check whether simulator is currently running
	public boolean isSimRunning() {
		return simRunning;
	}

	// start or stop simulator
	public boolean setSimRunning(boolean simRunning) {
		this.simRunning = simRunning;
		return simRunning;
	}

	public void updateSliders(SpinCADPatch p) {
		this.patch = p;
		sctb.updateSimSliders();
	}

	// if outputFile = null, then simulator goes thru speakers
	public void setOutputFileMode(Boolean state) {

		if(state == true) {
			outputFile = prefs.get("SIMULATOR_OUT_FILE", "");
		}
		else {
			outputFile = null;
		}
	}

	// ======================================================================================================
	class simControlToolBar extends JToolBar implements ActionListener, ChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7552645224196206164L;
		/**
		 * 
		 */
		final JButton btnStartSimulation = new JButton("Start Simulation");
		final JButton btnSigGen = new JButton("Sig Gen Sim");

		final JSlider pot0Slider = new JSlider(0, 100, 1);
		final JSlider pot1Slider = new JSlider(0, 100, 1);
		final JSlider pot2Slider = new JSlider(0, 100, 1);

		public simControlToolBar(JFrame frame) {
			super();
			this.add(btnStartSimulation);
			btnStartSimulation.addActionListener(this);
			// this.add(btnSigGen);
			// btnSigGen.addActionListener(this);

			this.add(pot0Slider);
			pot0Slider.addChangeListener(this);
			pot0Slider.setToolTipText("Pot 0");

			this.add(pot1Slider);
			pot1Slider.addChangeListener(this);
			pot1Slider.setToolTipText("Pot 1");

			this.add(pot2Slider);
			pot2Slider.addChangeListener(this);
			pot2Slider.setToolTipText("Pot 2");

			// this.setVisible(true);
			if (sim != null) {
				updateSimSliders();
			}
		}

		public void updateSimSliders() {	
			pot0Slider.setValue((int) patch.getPotVal(0));
			pot1Slider.setValue((int) patch.getPotVal(1));
			pot2Slider.setValue((int) patch.getPotVal(2));
		}

		public void setSimPotValues() {
			double simPot0 = patch.getPotVal(0) / 100.0;
			double simPot1 = patch.getPotVal(1) / 100.0;
			double simPot2 = patch.getPotVal(2) / 100.0;

			if (sim != null) {
				sim.setPot(0, simPot0);
				sim.setPot(1, simPot1);
				sim.setPot(2, simPot2);
			}
		}

		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == pot0Slider) {
				patch.setPotVal(0,(double) pot0Slider.getValue());
				double simPot0 = patch.getPotVal(0) / 100.0;
				pot0Slider.setToolTipText("Pot 0: " + simPot0);
				if (sim != null)
					sim.setPot(0, simPot0);
			} else if (e.getSource() == pot1Slider) {
				patch.setPotVal(1,(double) pot1Slider.getValue());
				double simPot1 = patch.getPotVal(1) / 100.0;
				pot1Slider.setToolTipText("Pot 1: " + simPot1);
				if (sim != null)
					sim.setPot(1, patch.getPotVal(1) / 100.0);
			} else if (e.getSource() == pot2Slider) {
				patch.setPotVal(2,(double) pot2Slider.getValue());
				double simPot2 = patch.getPotVal(2) / 100.0;
				pot2Slider.setToolTipText("Pot 2: " + simPot2);
				if (sim != null)
					sim.setPot(2, simPot2);
			}
		}

		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == btnStartSimulation) {
				if (isSimRunning() == true) {
					setSimRunning(false);
					loggerPanel.setVisible(false);
					scopePanel.setVisible(false);
					levelMonitor.setVisible(false);;
					btnStartSimulation.setText(" Start Simulator");
					sim.stopSimulator();
				} else {
					String testWavFileName = checkSimulatorFile();
					if(testWavFileName != "Not found!") {
						setSimRunning(true);
						// create file
						btnStartSimulation.setText(" Stop Simulator ");
						frame.updateAll();
						sim = new SpinSimulator(patch.patchModel.getRenderBlock(),
								testWavFileName, outputFile, patch.getPotVal(0), patch.getPotVal(1),
								patch.getPotVal(2));
						// loggerPanel.setVisible(loggerIsVisible);
						if(loggerIsVisible) {
							sim.showLevelLogger(loggerPanel);
							//							sim.showLevelMeter(levelMonitor);
						}
						if(scopeIsVisible) {
							sim.showScope(scopePanel);
							//							sim.showLevelMeter(levelMonitor);
						}
						//					sim.showLevelMeter();
						// restore scope settings
						String gain = prefs.get("CH2_VERT_GAIN", "");
						switch(gain) {
						case "1x":
							if(sim != null) {
								sim.scope.setScopeCh2Gain(19);
							}
							break;
						case "2x":
							if(sim != null) {
								sim.scope.setScopeCh2Gain(18);
							}
							break;
						case "4x":
							if(sim != null) {
								sim.scope.setScopeCh2Gain(17);
							}
							break;
						case "8x":
							if(sim != null) {
								sim.scope.setScopeCh2Gain(16);
							}
						case "16x":
							if(sim != null) {
								sim.scope.setScopeCh2Gain(15);
							}
							break;
						}	

						gain = prefs.get("CH1_VERT_GAIN", "");
						switch(gain) {
						case "1x":
							if(sim != null) {
								sim.scope.setScopeCh1Gain(19);
							}
							break;
						case "2x":
							if(sim != null) {
								sim.scope.setScopeCh1Gain(18);
							}
							break;
						case "4x":
							if(sim != null) {
								sim.scope.setScopeCh1Gain(17);
							}
							break;
						case "8x":
							if(sim != null) {
								sim.scope.setScopeCh1Gain(16);
							}
						case "16x":
							if(sim != null) {
								sim.scope.setScopeCh1Gain(15);
							}
							break;
						}	
						

						String timeBase = prefs.get("TimeBase", "");

						switch(timeBase) {
						case "8":
							if(sim != null) {
								sim.scope.setWindowRatio(8);
							}
							break;
						case "16":
							if(sim != null) {
								sim.scope.setWindowRatio(16);
							}
							break;
						case "32":
							if(sim != null) {
								sim.scope.setWindowRatio(32);
							}
							break;
						case "64":
							if(sim != null) {
								sim.scope.setWindowRatio(64);
							}
							break;
						case "128":
							if(sim != null) {
								sim.scope.setWindowRatio(128);
							}
							break;
						case "256":
							if(sim != null) {
								sim.scope.setWindowRatio(256);
							}
							break;
						case "512":
							if(sim != null) {
								sim.scope.setWindowRatio(512);
							}
							break;
						case "1024":
							if(sim != null) {
								sim.scope.setWindowRatio(1024);
							}
							break;
						}
						setSimPotValues();
						sim.start();
					}
					else { 
						SpinCADDialogs.MessageBox("Simulator file not found.", "Please set a simulator source file.");
					} 
				}
			} else if (arg0.getSource() == btnSigGen) {
				if (isSimRunning() == true) {
					setSimRunning(false);
					btnSigGen.setText("Start Signal");
					sim.stopSimulator();
				} else {
					//					String outputFile = null; // play out through the sound card
					setSimRunning(true);
					btnSigGen.setText("Stop Signal");
					//					SignalGenerator SigGen = new SignalGenerator();
				}
			}
		} 
	}
	
	

	public class ScopeToolBar extends JToolBar implements ActionListener, ChangeListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3040642773216953900L;
		final JLabel ch1_Vertical_Gain_Label = new JLabel(" Ch 1 Gain: ");

		String[] gainLabels = new String[] {"1x", "2x", "4x", "8x", "16x"};
		JComboBox<String> ch1_Vertical_Gain = new JComboBox<>(gainLabels);

		final JLabel ch2_Vertical_Gain_Label = new JLabel(" Ch 2 Gain: ");
		JComboBox<String> ch2_Vertical_Gain = new JComboBox<>(gainLabels);

		String[] timebaseLabels = new String[] {"8", "16", "32", "64", "128", "256", "512", "1024"};
		final JLabel timebaseLabel = new JLabel(" Time Base: ");
		JComboBox<String> timebase = new JComboBox<>(timebaseLabels);

		String[] triggerModeLabels = new String[] {"Auto", "Normal", "Single"};
		final JLabel triggerModeLabel = new JLabel(" Trigger Mode: ");
		final JComboBox<String> triggerModeCB = new JComboBox<String>(triggerModeLabels);

		final JLabel triggerLevelLabel = new JLabel(" Trigger Level: ");
		final JSpinner triggerLevel = new JSpinner();

		String[] triggerSlopeLabels = new String[] {"Pos", "Neg"};
		final JLabel triggerSlopeLabel = new JLabel(" Trigger Slope: ");
		final JComboBox<String> triggerSlope = new JComboBox<String>(triggerSlopeLabels);

		class Task extends SwingWorker<Void, Void> {
			/*
			 * Main task. Executed in background thread.
			 */
			@Override
			public Void doInBackground() {
				// Sleep for at least one second to simulate "startup".
				try {
					Thread.sleep(200);
				} catch (InterruptedException ignore) {
				}
				done();
				return null;
			}

			/*
			 * Executed in event dispatch thread
			 */
			public void done() {
			}
		}

		// ==============================================================
		// == Resources toolbar
		public ScopeToolBar() {
			super();

			// Call setStringPainted now so that the progress bar height
			// stays the same whether or not the string is shown.

			ch1_Vertical_Gain.setToolTipText(" Ch 1 Gain ");
			//			ch1_Vertical_Gain.setPreferredSize(new Dimension(20,10));
			ch1_Vertical_Gain.setMinimumSize(new Dimension(100,120));
			ch1_Vertical_Gain.setMaximumSize(new Dimension(120,140));
			ch1_Vertical_Gain.addActionListener(this);

			Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
			ch1_Vertical_Gain.setBorder(border);

			ch2_Vertical_Gain.setToolTipText(" Ch 2 Gain ");
			//			ch2_Vertical_Gain.setPreferredSize(new Dimension(100,40));
			ch2_Vertical_Gain.setMinimumSize(new Dimension(100,120));
			ch2_Vertical_Gain.setMaximumSize(new Dimension(120,140));
			//			ch2_Vertical_Gain.setMaximumSize(ch2_Vertical_Gain.getPreferredSize());
			ch2_Vertical_Gain.setBorder(border);
			ch2_Vertical_Gain.addActionListener(this);

			triggerModeCB.add(new JLabel("Auto"));
			triggerModeCB.add(new JLabel("Normal"));
			triggerModeCB.add(new JLabel("Single"));

			add(ch1_Vertical_Gain_Label);
			add(ch1_Vertical_Gain);
			add(ch2_Vertical_Gain_Label);
			add(ch2_Vertical_Gain);

			add(timebaseLabel);

			timebase.setMinimumSize(new Dimension(100,120));
			timebase.setMaximumSize(new Dimension(120,140));
			timebase.setBorder(border);
			timebase.addActionListener(this);
			add(timebase);

			add(triggerModeLabel);
			triggerModeCB.setMinimumSize(new Dimension(100,120));
			triggerModeCB.setMaximumSize(new Dimension(120,140));
			triggerModeCB.setBorder(border);
			triggerModeCB.addActionListener(this);
			add(triggerModeCB);

			add(triggerLevelLabel);
			triggerLevel.setMinimumSize(new Dimension(100,120));
			triggerLevel.setMaximumSize(new Dimension(120,140));
			triggerLevel.setBorder(border);
			triggerLevel.addChangeListener(this);
			add(triggerLevel);

			add(triggerSlopeLabel);
			triggerSlope.setMinimumSize(new Dimension(100,120));
			triggerSlope.setMaximumSize(new Dimension(120,140));
			triggerSlope.setBorder(border);
			triggerSlope.addActionListener(this);
			add(triggerSlope);
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == ch1_Vertical_Gain) {
				JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
				String gain = (String)cb.getSelectedItem();
				switch(gain) {
				case "1x":
					if(sim != null) {
						sim.scope.setScopeCh1Gain(19);
						prefs.put("CH1_VERT_GAIN", "1x");
					}
					break;
				case "2x":
					if(sim != null) {
						sim.scope.setScopeCh1Gain(18);
						prefs.put("CH1_VERT_GAIN", "2x");
					}
					break;
				case "4x":
					if(sim != null) {
						sim.scope.setScopeCh1Gain(17);
						prefs.put("CH1_VERT_GAIN", "4x");
					}
					break;
				case "8x":
					if(sim != null) {
						sim.scope.setScopeCh1Gain(16);
						prefs.put("CH1_VERT_GAIN", "8x");
					}
				case "16":
					if(sim != null) {
						sim.scope.setScopeCh1Gain(15);
						prefs.put("CH1_VERT_GAIN", "16x");
					}
					break;
				}
			} else if (arg0.getSource() == ch2_Vertical_Gain) {
				JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
				String gain = (String)cb.getSelectedItem();
				switch(gain) {
				case "1x":
					if(sim != null) {
						sim.scope.setScopeCh2Gain(19);
						prefs.put("CH2_VERT_GAIN", "1x");
					}
					break;
				case "2x":
					if(sim != null) {
						sim.scope.setScopeCh2Gain(18);
						prefs.put("CH2_VERT_GAIN", "2x");
					}
					break;
				case "4x":
					if(sim != null) {
						sim.scope.setScopeCh2Gain(17);
						prefs.put("CH2_VERT_GAIN", "4x");
					}
					break;
				case "8x":
					if(sim != null) {
						sim.scope.setScopeCh2Gain(16);
						prefs.put("CH2_VERT_GAIN", "8x");
					}
				case "16x":
					if(sim != null) {
						sim.scope.setScopeCh2Gain(15);
						prefs.put("CH2_VERT_GAIN", "16x");
					}
					break;
				}
			} else if (arg0.getSource() == timebase) {
				JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
				String gain = (String)cb.getSelectedItem();
				setTimeBase(gain);
				
			} else if (arg0.getSource() == triggerSlope) {
				int j = 1;
			} else if (arg0.getSource() == triggerModeCB) {
				JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
				String gain = (String)cb.getSelectedItem();
				switch(gain) {
				case "Auto":
					if(sim != null) {
					sim.scope.tm = triggerMode.AUTO;
					prefs.put("TRIGGER_MODE", "Auto");
					}
					break;
				case "Normal":
					if(sim != null) {
						prefs.put("TRIGGER_MODE", "Normal");
					}
					break;
				case "Single":
					if(sim != null) {
						sim.scope.tm = triggerMode.SINGLE;
						prefs.put("TRIGGER_MODE", "Single");
					}
					break;
				}
			}

		}
		
		void setTimeBase(String gain) {
			switch(gain) {
			case "8":
				if(sim != null) {
					sim.scope.setWindowRatio(8);
					prefs.put("TimeBase", "8");
				}
				break;
			case "16":
				if(sim != null) {
					sim.scope.setWindowRatio(16);
					prefs.put("TimeBase", "16");
				}
				break;
			case "32":
				if(sim != null) {
					sim.scope.setWindowRatio(32);
					prefs.put("TimeBase", "32");
				}
				break;
			case "64":
				if(sim != null) {
					sim.scope.setWindowRatio(64);
					prefs.put("TimeBase", "64");
				}
				break;
			case "128":
				if(sim != null) {
					sim.scope.setWindowRatio(128);
					prefs.put("TimeBase", "128");
				}
				break;
			case "256":
				if(sim != null) {
					sim.scope.setWindowRatio(256);
					prefs.put("TimeBase", "256");
				}
				break;
			case "512":
				if(sim != null) {
					sim.scope.setWindowRatio(512);
					prefs.put("TimeBase", "512");
				}
				break;
			case "1024":
				if(sim != null) {
					sim.scope.setWindowRatio(1024);
					prefs.put("TimeBase", "1024");
				}
				break;
			}
			
		}

		@Override
		public void stateChanged(ChangeEvent e) {
			// ---
			if (e.getSource() == triggerLevel) {
				int j = 1;
			}		
		}
	}


	public String checkSimulatorFile() {
		File f = null;
		String testWavFileName = prefs.get("SIMULATOR_FILE", "");
		if(testWavFileName == "") {
			System.out.println("Simulator file name blank!");
			return "Not found!";
		} else {
			try {
				f = new File(testWavFileName);
			} 
			catch(Exception e) {
				System.out.println("Exception opening file!");
				return "Not found!";
			}
			finally {
			}
			if(f.exists() && !f.isDirectory()) {
				return testWavFileName;	
			}
		}
		return "Not found!";
	}

	public void getSimulatorFile() throws UnsupportedAudioFileException, IOException {
		String testWavFileName = prefs.get("SIMULATOR_FILE", "");
		final JFileChooser fc = new JFileChooser(testWavFileName);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"WAV files", "wav");
		fc.setSelectedFile(new File(testWavFileName));
		fc.setFileFilter(filter);

		final String newline = "\n";

		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			testWavFileName = fc.getSelectedFile().getPath();
			prefs.put("SIMULATOR_FILE", testWavFileName);
			System.out.println("Opening: " + testWavFileName + "." + newline);
			// do this just to check the file format, exceptions will be thrown if any
			new AudioFileReader(testWavFileName, false);
		} else {
			System.out.println("Command cancelled by user." + newline);
		}
	}

	public void setSimulatorDebugFile() throws IOException {
		String debugFileName = prefs.get("SIMULATOR_DEBUG_FILE", "");
		final JFileChooser fc = new JFileChooser(debugFileName);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"txt files", "txt");
		fc.setSelectedFile(new File(debugFileName));
		fc.setFileFilter(filter);

		final String newline = "\n";

		int returnVal = fc.showSaveDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			debugFileName = fc.getSelectedFile().getPath();
			prefs.put("SIMULATOR_DEBUG_FILE", debugFileName);
		} else {
			System.out.println("Command cancelled by user." + newline);
		}
	}

	public void setSimulatorOutputFile() {
		String simWavOutFileName = prefs.get("SIMULATOR_OUT_FILE", "");
		final JFileChooser fc = new JFileChooser(simWavOutFileName);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"WAV files", "wav");
		fc.setSelectedFile(new File(simWavOutFileName));

		fc.setFileFilter(filter);

		final String newline = "\n";

		int returnVal = fc.showSaveDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			simWavOutFileName = fc.getSelectedFile().getPath();
			prefs.put("SIMULATOR_OUT_FILE", simWavOutFileName);

			System.out.println("Simulator output file: " + simWavOutFileName);
		} else {
			System.out.println("Command cancelled by user." + newline);
		}
	}
}
