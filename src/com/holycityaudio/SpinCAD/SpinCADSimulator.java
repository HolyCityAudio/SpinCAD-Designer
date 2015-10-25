package com.holycityaudio.SpinCAD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.andrewkilpatrick.elmGen.simulator.AudioFileReader;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;

public class SpinCADSimulator {
	private boolean simRunning = false;
	public SpinSimulator sim;
	// simulator output file
	public String outputFile = null; // play out through the sound card

	private Preferences prefs;
	public JPanel levelMonitor = new JPanel();
	public JPanel loggerPanel = new JPanel();		// see if we can display the logger panel within the main frame

	public boolean loggerIsVisible = false;
	
	private SpinCADFrame frame;
	public simControlToolBar sctb;

	private SpinCADPatch patch;

	// constructor
	public SpinCADSimulator(SpinCADFrame f, SpinCADPatch p) {
		frame = f;
		patch = p;
		this.sctb = new simControlToolBar(frame);
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
		
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == pot0Slider) {
				patch.setPotVal(0,(double) pot0Slider.getValue());
				pot0Slider.setToolTipText("Pot 0: " + patch.getPotVal(0));
				//				System.out.println("Pot 0: " + pot0Level);
				if (sim != null)
					sim.setPot(0, patch.getPotVal(0));
			} else if (e.getSource() == pot1Slider) {
				patch.setPotVal(1,(double) pot1Slider.getValue());
				pot1Slider.setToolTipText("Pot 1: " + patch.getPotVal(1));
				//				System.out.println("Pot 1: " + pot1Level);
				if (sim != null)
					sim.setPot(1, patch.getPotVal(1));
			} else if (e.getSource() == pot2Slider) {
				patch.setPotVal(2,(double) pot2Slider.getValue());
				pot2Slider.setToolTipText("Pot 2: " + patch.getPotVal(2));
				if (sim != null)
					sim.setPot(2, patch.getPotVal(2));
			}
		}

		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == btnStartSimulation) {
				if (isSimRunning() == true) {
					setSimRunning(false);
					loggerPanel.setVisible(false);
					levelMonitor.setVisible(false);;
					btnStartSimulation.setText("Start Simulator");
					sim.stopSimulator();
				} else {
					String testWavFileName = checkSimulatorFile();
					if(testWavFileName != "Not found!") {
						setSimRunning(true);
						// create file
						btnStartSimulation.setText("Stop Simulator");
						frame.updateAll();
						sim = new SpinSimulator(patch.patchModel.getRenderBlock(),
								testWavFileName, outputFile, patch.getPotVal(0), patch.getPotVal(1),
								patch.getPotVal(2));
						// loggerPanel.setVisible(loggerIsVisible);
						if(loggerIsVisible) {
							sim.showLevelLogger(loggerPanel);
							//							sim.showLevelMeter(levelMonitor);
						}
						//					sim.showLevelMeter();
						sim.start();
					}
					else { 
						frame.MessageBox("Simulator file not found.", "Please set a simulator source file.");
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
