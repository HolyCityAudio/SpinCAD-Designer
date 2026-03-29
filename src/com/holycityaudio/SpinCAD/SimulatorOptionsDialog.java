package com.holycityaudio.SpinCAD;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import org.andrewkilpatrick.elmGen.Debug;
import org.andrewkilpatrick.elmGen.ElmProgram;

public class SimulatorOptionsDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	private final SpinCADSimulator simX;
	private final SpinCADFrame frame;
	private final Preferences prefs;

	private JRadioButton rbSoundCard;
	private JRadioButton rbFile;
	private JTextField tfSourceFile;
	private JTextField tfOutputFile;
	private JComboBox<String> cbSampleRate;
	private JCheckBox cbEnableDisplay;
	private JCheckBox cbLoopMode;
	private JCheckBox cbRealTime;

	public SimulatorOptionsDialog(SpinCADFrame owner, SpinCADSimulator simX) {
		super(owner, "Simulator Options", true);
		this.frame = owner;
		this.simX = simX;
		this.prefs = Preferences.userNodeForPackage(SpinCADSimulator.class);

		JPanel content = new JPanel(new GridBagLayout());
		content.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		int row = 0;

		// --- Output Destination ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
		content.add(new JLabel("Output Destination:"), gbc);
		row++;

		boolean isFileMode = prefs.getBoolean("OUTPUT_FILE_MODE", simX.outputFile != null);

		rbSoundCard = new JRadioButton("Sound Card", !isFileMode);
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
		content.add(rbSoundCard, gbc);

		rbFile = new JRadioButton("File", isFileMode);
		gbc.gridx = 1; gbc.gridwidth = 2;
		content.add(rbFile, gbc);
		row++;

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbSoundCard);
		bg.add(rbFile);

		// --- Separator ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
		content.add(new JSeparator(), gbc);
		row++;

		// --- Source File ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		content.add(new JLabel("Source File:"), gbc);

		tfSourceFile = new JTextField(prefs.get("SIMULATOR_FILE", ""));
		tfSourceFile.setEditable(false);
		tfSourceFile.setPreferredSize(new Dimension(300, 24));
		gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
		content.add(tfSourceFile, gbc);

		JButton btnBrowseSource = new JButton("Browse...");
		gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
		content.add(btnBrowseSource, gbc);
		row++;

		// --- Output File ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
		content.add(new JLabel("Output File:"), gbc);

		tfOutputFile = new JTextField(prefs.get("SIMULATOR_OUT_FILE", ""));
		tfOutputFile.setEditable(false);
		tfOutputFile.setPreferredSize(new Dimension(300, 24));
		gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
		content.add(tfOutputFile, gbc);

		JButton btnBrowseOutput = new JButton("Browse...");
		gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
		content.add(btnBrowseOutput, gbc);
		row++;

		// --- Debug File (only in debug mode) ---
		JTextField tfDebugFile = null;
		if (Debug.DEBUG) {
			gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
			content.add(new JLabel("Debug File:"), gbc);

			tfDebugFile = new JTextField(prefs.get("SIMULATOR_DEBUG_FILE", ""));
			tfDebugFile.setEditable(false);
			tfDebugFile.setPreferredSize(new Dimension(300, 24));
			gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
			content.add(tfDebugFile, gbc);

			JButton btnBrowseDebug = new JButton("Browse...");
			gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
			content.add(btnBrowseDebug, gbc);
			row++;

			final JTextField tfDebugRef = tfDebugFile;
			btnBrowseDebug.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						simX.setSimulatorDebugFile();
						tfDebugRef.setText(prefs.get("SIMULATOR_DEBUG_FILE", ""));
					} catch (IOException ex) {
						SpinCADDialogs.MessageBox(frame, "Simulator Debug File Error", "Uhmmmm....");
					}
				}
			});
		}

		// --- Separator ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		content.add(new JSeparator(), gbc);
		row++;

		// --- Sample Rate ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		content.add(new JLabel("Sample Rate:"), gbc);

		String[] rateStrings = { "32768", "44100", "48000" };
		cbSampleRate = new JComboBox<String>(rateStrings);
		if (ElmProgram.SAMPLERATE == 44100) {
			cbSampleRate.setSelectedIndex(1);
		} else if (ElmProgram.SAMPLERATE == 48000) {
			cbSampleRate.setSelectedIndex(2);
		} else {
			cbSampleRate.setSelectedIndex(0);
		}
		gbc.gridx = 1; gbc.gridwidth = 2;
		gbc.fill = GridBagConstraints.NONE;
		content.add(cbSampleRate, gbc);
		row++;

		// --- Separator ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		content.add(new JSeparator(), gbc);
		row++;

		// --- Checkboxes ---
		cbEnableDisplay = new JCheckBox("Enable Display",
				prefs.getBoolean("ENABLE_DISPLAY", false));
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
		content.add(cbEnableDisplay, gbc);
		row++;

		cbLoopMode = new JCheckBox("Loop Mode",
				prefs.getBoolean("LOOP_MODE", true));
		gbc.gridy = row;
		content.add(cbLoopMode, gbc);
		row++;

		cbRealTime = new JCheckBox("Simulate to File in Real Time",
				prefs.getBoolean("REALTIME_FILE_SIM", false));
		gbc.gridy = row;
		content.add(cbRealTime, gbc);
		row++;

		// --- Separator + Close ---
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		content.add(new JSeparator(), gbc);
		row++;

		JButton btnClose = new JButton("Close");
		gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.NONE;
		content.add(btnClose, gbc);

		// --- Wire up enable/disable logic ---
		ActionListener updateState = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateControlStates();
			}
		};
		rbSoundCard.addActionListener(updateState);
		rbFile.addActionListener(updateState);
		cbRealTime.addActionListener(updateState);
		updateControlStates();

		// --- Button actions ---
		btnBrowseSource.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					simX.getSimulatorFile();
					tfSourceFile.setText(prefs.get("SIMULATOR_FILE", ""));
				} catch (UnsupportedAudioFileException ex) {
					SpinCADDialogs.MessageBox(frame, "Simulator File Error",
							"Make sure that your simulator source\n"
							+ "file is a stereo 16 bit WAV file sampled \nat 32768, 44100, or 48000 Hz.");
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		btnBrowseOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				simX.setSimulatorOutputFile();
				tfOutputFile.setText(prefs.get("SIMULATOR_OUT_FILE", ""));
			}
		});

		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				applySettings();
				dispose();
			}
		});

		setContentPane(content);
		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
	}

	private void updateControlStates() {
		boolean fileMode = rbFile.isSelected();
		cbRealTime.setEnabled(fileMode);
		if (!fileMode) {
			cbRealTime.setSelected(false);
			// Sound Card mode always loops
			cbLoopMode.setSelected(true);
			cbLoopMode.setEnabled(false);
		} else if (!cbRealTime.isSelected()) {
			// File mode without real-time: no loop
			cbLoopMode.setSelected(false);
			cbLoopMode.setEnabled(false);
		} else {
			// File mode with real-time: user chooses loop
			cbLoopMode.setEnabled(true);
		}
	}

	private void applySettings() {
		// Output destination
		boolean fileMode = rbFile.isSelected();
		prefs.putBoolean("OUTPUT_FILE_MODE", fileMode);
		if (fileMode) {
			simX.setOutputFileMode(true);
		} else {
			simX.outputFile = null;
		}

		// Sample rate
		String rate = (String) cbSampleRate.getSelectedItem();
		if ("32768".equals(rate)) {
			ElmProgram.setSamplerate(32768);
		} else if ("44100".equals(rate)) {
			ElmProgram.setSamplerate(44100);
		} else if ("48000".equals(rate)) {
			ElmProgram.setSamplerate(48000);
		}

		// Display
		simX.displayIsVisible = cbEnableDisplay.isSelected();
		prefs.putBoolean("ENABLE_DISPLAY", cbEnableDisplay.isSelected());

		// Loop mode
		boolean loopMode = cbLoopMode.isSelected();
		prefs.putBoolean("LOOP_MODE", loopMode);
		if (simX.sim != null) {
			simX.sim.setLoopMode(loopMode);
		}

		// Real-time file sim
		boolean realTime = cbRealTime.isSelected();
		prefs.putBoolean("REALTIME_FILE_SIM", realTime);
		if (simX.sim != null) {
			simX.sim.setRealTimeMode(realTime);
		}

		// If real-time file mode, also enable loop
		if (rbFile.isSelected() && realTime) {
			if (simX.sim != null) {
				simX.sim.setLoopMode(true);
			}
		}

		// Update display if sim is running
		if (simX.isSimRunning()) {
			simX.switchDisplay();
		}
	}
}
