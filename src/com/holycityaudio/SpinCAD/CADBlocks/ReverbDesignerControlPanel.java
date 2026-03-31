/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * ReverbDesignerControlPanel.java
 * Copyright (C) 2024 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class ReverbDesignerControlPanel implements ChangeListener, ActionListener, WindowListener {

	private JDialog frame;
	private ReverbDesignerCADBlock gCB;

	// Design-time controls
	private JComboBox<String> topologyCombo;
	private JComboBox<String> sizeCombo;
	private JCheckBox stereoCheck;
	private JComboBox<String> lfoCombo;
	private JComboBox<String> shimmerCombo;
	private JComboBox<String> shimmerPitchCombo;
	private JCheckBox preDelayCheck;

	// Runtime parameter sliders
	private FineControlSlider reverbTimeSlider;
	private JTextField reverbTimeField;
	private FineControlSlider hfDampingSlider;
	private JTextField hfDampingField;
	private FineControlSlider lfDampingSlider;
	private JTextField lfDampingField;
	private FineControlSlider dryWetSlider;
	private JTextField dryWetField;
	private FineControlSlider preDelaySlider;
	private JTextField preDelayField;
	private JPanel preDelayPanel;
	private FineControlSlider inputBwSlider;
	private JTextField inputBwField;
	private FineControlSlider diffusionSlider;
	private JTextField diffusionField;
	private FineControlSlider inputGainSlider;
	private JTextField inputGainField;
	private FineControlSlider shimmerLevelSlider;
	private JTextField shimmerLevelField;
	private JPanel shimmerLevelPanel;

	// Resource display
	private JLabel resourceLabel;

	public ReverbDesignerControlPanel(ReverbDesignerCADBlock block) {
		gCB = block;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JDialog(SpinCADFrame.getInstance(), "Reverb Designer");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				frame.setResizable(false);
				frame.addWindowListener(ReverbDesignerControlPanel.this);

				buildDesignSection();
				buildParameterSection();
				buildResourceSection();

				frame.pack();
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);
			}
		});
	}

	private void buildDesignSection() {
		JPanel designPanel = new JPanel();
		designPanel.setLayout(new BoxLayout(designPanel, BoxLayout.Y_AXIS));
		designPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Design",
				TitledBorder.LEFT, TitledBorder.TOP));

		// Row 1: Topology, Size, LFO
		JPanel row1 = new JPanel(new GridLayout(1, 3, 5, 0));
		JPanel topPanel = labeledCombo("Topology");
		topologyCombo = new JComboBox<String>(new String[] { "Two-Loop", "Dattorro Plate", "Ring FDN" });
		topologyCombo.setSelectedIndex(gCB.getTopology());
		topologyCombo.addActionListener(this);
		topPanel.add(topologyCombo);
		row1.add(topPanel);

		JPanel sizePanel = labeledCombo("Size");
		sizeCombo = new JComboBox<String>(new String[] { "Small", "Medium", "Large" });
		sizeCombo.setSelectedIndex(gCB.getSizePreset());
		sizeCombo.addActionListener(this);
		sizePanel.add(sizeCombo);
		row1.add(sizePanel);

		JPanel lfoPanel = labeledCombo("LFO Modulation");
		lfoCombo = new JComboBox<String>(new String[] { "None", "Subtle", "Wide" });
		lfoCombo.setSelectedIndex(gCB.getLfoDepth());
		lfoCombo.addActionListener(this);
		lfoPanel.add(lfoCombo);
		row1.add(lfoPanel);

		designPanel.add(row1);
		designPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// Row 2: Stereo, Shimmer, Shimmer Pitch
		JPanel row2 = new JPanel(new GridLayout(1, 3, 5, 0));
		stereoCheck = new JCheckBox("Stereo Output", gCB.isStereoOutput());
		stereoCheck.addActionListener(this);
		row2.add(stereoCheck);

		JPanel shimPanel = labeledCombo("Shimmer");
		shimmerCombo = new JComboBox<String>(new String[] { "Off", "Input Only", "Input + Feedback" });
		shimmerCombo.setSelectedIndex(gCB.getShimmerMode());
		shimmerCombo.addActionListener(this);
		shimPanel.add(shimmerCombo);
		row2.add(shimPanel);

		JPanel pitchPanel = labeledCombo("Shimmer Pitch");
		shimmerPitchCombo = new JComboBox<String>(new String[] {
			"Octave Up (+12)", "Fifth Up (+7)", "Fourth Up (+5)", "Maj Third (+4)", "Min Third (+3)"
		});
		selectShimmerPitch(gCB.getShimmerPitchSemitones());
		shimmerPitchCombo.setEnabled(gCB.getShimmerMode() != ReverbDesignerCADBlock.SHIMMER_OFF);
		shimmerPitchCombo.addActionListener(this);
		pitchPanel.add(shimmerPitchCombo);
		row2.add(pitchPanel);

		designPanel.add(row2);
		designPanel.add(Box.createRigidArea(new Dimension(0, 5)));

		// Row 3: Pre-Delay checkbox
		preDelayCheck = new JCheckBox("Pre-Delay", gCB.isPreDelayEnabled());
		preDelayCheck.addActionListener(this);
		designPanel.add(preDelayCheck);

		frame.add(designPanel);
	}

	private void buildParameterSection() {
		JPanel paramPanel = new JPanel();
		paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.Y_AXIS));
		paramPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Parameters (defaults when not pot-controlled)",
				TitledBorder.LEFT, TitledBorder.TOP));

		// Reverb Time
		reverbTimeSlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 95, (int)(gCB.getReverbTime() * 100));
		reverbTimeSlider.addChangeListener(this);
		reverbTimeField = new JTextField();
		reverbTimeField.setEditable(false);
		reverbTimeField.setHorizontalAlignment(JTextField.CENTER);
		paramPanel.add(makeSliderRow("Reverb Time", reverbTimeField, reverbTimeSlider));
		updateReverbTimeLabel();

		// HF Damping
		hfDampingSlider = new FineControlSlider(JSlider.HORIZONTAL, 5, 90, (int)(gCB.getHfDamping() * 100));
		hfDampingSlider.addChangeListener(this);
		hfDampingField = new JTextField();
		hfDampingField.setEditable(false);
		hfDampingField.setHorizontalAlignment(JTextField.CENTER);
		paramPanel.add(makeSliderRow("HF Damping", hfDampingField, hfDampingSlider));
		updateHfDampingLabel();

		// LF Damping
		lfDampingSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int)(gCB.getLfDamping() * 1000));
		lfDampingSlider.addChangeListener(this);
		lfDampingField = new JTextField();
		lfDampingField.setEditable(false);
		lfDampingField.setHorizontalAlignment(JTextField.CENTER);
		paramPanel.add(makeSliderRow("LF Damping", lfDampingField, lfDampingSlider));
		updateLfDampingLabel();

		// Dry/Wet Mix
		dryWetSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, (int)(gCB.getDryWet() * 100));
		dryWetSlider.addChangeListener(this);
		dryWetField = new JTextField();
		dryWetField.setEditable(false);
		dryWetField.setHorizontalAlignment(JTextField.CENTER);
		paramPanel.add(makeSliderRow("Dry/Wet Mix", dryWetField, dryWetSlider));
		updateDryWetLabel();

		// Pre-Delay Amount
		preDelaySlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, (int)(gCB.getPreDelayAmount() * 100));
		preDelaySlider.addChangeListener(this);
		preDelayField = new JTextField();
		preDelayField.setEditable(false);
		preDelayField.setHorizontalAlignment(JTextField.CENTER);
		preDelayPanel = makeSliderRow("Pre-Delay", preDelayField, preDelaySlider);
		preDelayPanel.setVisible(gCB.isPreDelayEnabled());
		paramPanel.add(preDelayPanel);
		updatePreDelayLabel();

		// Input Bandwidth
		inputBwSlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 90, (int)(gCB.getInputBandwidth() * 100));
		inputBwSlider.addChangeListener(this);
		inputBwField = new JTextField();
		inputBwField.setEditable(false);
		inputBwField.setHorizontalAlignment(JTextField.CENTER);
		paramPanel.add(makeSliderRow("Input Bandwidth", inputBwField, inputBwSlider));
		updateInputBwLabel();

		// Input Gain: -24 to 0 in slider units = -12.0 to 0.0 dB in 0.5 dB steps
		inputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, -24, 0,
				(int)(gCB.getInputGain() * 2));
		inputGainSlider.addChangeListener(this);
		inputGainField = new JTextField();
		inputGainField.setEditable(false);
		inputGainField.setHorizontalAlignment(JTextField.CENTER);
		paramPanel.add(makeSliderRow("Input Gain", inputGainField, inputGainSlider));
		updateInputGainLabel();

		// Diffusion
		diffusionSlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 75, (int)(gCB.getDiffusion() * 100));
		diffusionSlider.addChangeListener(this);
		diffusionField = new JTextField();
		diffusionField.setEditable(false);
		diffusionField.setHorizontalAlignment(JTextField.CENTER);
		paramPanel.add(makeSliderRow("Diffusion", diffusionField, diffusionSlider));
		updateDiffusionLabel();

		// Shimmer Level
		shimmerLevelSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 70, (int)(gCB.getShimmerLevel() * 100));
		shimmerLevelSlider.addChangeListener(this);
		shimmerLevelField = new JTextField();
		shimmerLevelField.setEditable(false);
		shimmerLevelField.setHorizontalAlignment(JTextField.CENTER);
		shimmerLevelPanel = makeSliderRow("Shimmer Level", shimmerLevelField, shimmerLevelSlider);
		shimmerLevelPanel.setVisible(gCB.getShimmerMode() != ReverbDesignerCADBlock.SHIMMER_OFF);
		paramPanel.add(shimmerLevelPanel);
		updateShimmerLevelLabel();

		frame.add(paramPanel);
	}

	private void buildResourceSection() {
		JPanel resPanel = new JPanel(new BorderLayout());
		resPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Resources",
				TitledBorder.LEFT, TitledBorder.TOP));
		resourceLabel = new JLabel();
		resourceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		resPanel.add(resourceLabel, BorderLayout.CENTER);
		frame.add(resPanel);
		updateResourceLabel();
	}

	// =====================================================
	// Slider row builder
	// =====================================================
	private JPanel makeSliderRow(String label, JTextField field, FineControlSlider slider) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		field.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
		slider.setMaximumSize(new Dimension(Short.MAX_VALUE, 30));
		field.setText(label);
		panel.add(field);
		panel.add(slider);
		panel.add(Box.createRigidArea(new Dimension(0, 3)));
		return panel;
	}

	private JPanel labeledCombo(String label) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		JLabel lbl = new JLabel(label);
		lbl.setAlignmentX(0.0f);
		p.add(lbl);
		return p;
	}

	// =====================================================
	// Update label methods
	// =====================================================
	private void updateReverbTimeLabel() {
		reverbTimeField.setText(String.format("Reverb Time: %.2f", gCB.getReverbTime()));
	}

	private void updateHfDampingLabel() {
		hfDampingField.setText(String.format("HF Damping: %.2f", gCB.getHfDamping()));
	}

	private void updateLfDampingLabel() {
		lfDampingField.setText(String.format("LF Damping: %.3f", gCB.getLfDamping()));
	}

	private void updateDryWetLabel() {
		dryWetField.setText(String.format("Dry/Wet Mix: %d%%", (int)(gCB.getDryWet() * 100)));
	}

	private void updatePreDelayLabel() {
		double ms = gCB.getPreDelayAmount() * 100.0; // 0-100ms
		preDelayField.setText(String.format("Pre-Delay: %.0f ms", ms));
	}

	private void updateInputBwLabel() {
		inputBwField.setText(String.format("Input Bandwidth: %.2f", gCB.getInputBandwidth()));
	}

	private void updateDiffusionLabel() {
		diffusionField.setText(String.format("Diffusion: %.2f", gCB.getDiffusion()));
	}

	private void updateInputGainLabel() {
		inputGainField.setText(String.format("Input Gain: %.1f dB", gCB.getInputGain()));
	}

	private void updateShimmerLevelLabel() {
		shimmerLevelField.setText(String.format("Shimmer Level: %.2f", gCB.getShimmerLevel()));
	}

	private void updateResourceLabel() {
		int instr = gCB.estimateInstructions();
		int mem = gCB.estimateMemory();
		String warning = "";
		if (instr > 110) warning = " [WARNING: high instruction count]";
		else if (mem > 30000) warning = " [WARNING: high memory usage]";
		resourceLabel.setText(String.format("Est. ~%d instructions, ~%,d samples delay memory%s", instr, mem, warning));
	}

	private void selectShimmerPitch(int semi) {
		switch (semi) {
		case 12: shimmerPitchCombo.setSelectedIndex(0); break;
		case 7: shimmerPitchCombo.setSelectedIndex(1); break;
		case 5: shimmerPitchCombo.setSelectedIndex(2); break;
		case 4: shimmerPitchCombo.setSelectedIndex(3); break;
		case 3: shimmerPitchCombo.setSelectedIndex(4); break;
		default: shimmerPitchCombo.setSelectedIndex(0); break;
		}
	}

	private int getShimmerPitchFromCombo() {
		int[] semis = { 12, 7, 5, 4, 3 };
		int idx = shimmerPitchCombo.getSelectedIndex();
		return (idx >= 0 && idx < semis.length) ? semis[idx] : 12;
	}

	// =====================================================
	// Event handlers
	// =====================================================
	public void stateChanged(ChangeEvent e) {
		Object src = e.getSource();
		if (src == reverbTimeSlider) {
			gCB.setReverbTime(reverbTimeSlider.getValue() / 100.0);
			updateReverbTimeLabel();
		} else if (src == hfDampingSlider) {
			gCB.setHfDamping(hfDampingSlider.getValue() / 100.0);
			updateHfDampingLabel();
		} else if (src == lfDampingSlider) {
			gCB.setLfDamping(lfDampingSlider.getValue() / 1000.0);
			updateLfDampingLabel();
		} else if (src == dryWetSlider) {
			gCB.setDryWet(dryWetSlider.getValue() / 100.0);
			updateDryWetLabel();
		} else if (src == preDelaySlider) {
			gCB.setPreDelayAmount(preDelaySlider.getValue() / 100.0);
			updatePreDelayLabel();
		} else if (src == inputBwSlider) {
			gCB.setInputBandwidth(inputBwSlider.getValue() / 100.0);
			updateInputBwLabel();
		} else if (src == diffusionSlider) {
			gCB.setDiffusion(diffusionSlider.getValue() / 100.0);
			updateDiffusionLabel();
		} else if (src == inputGainSlider) {
			gCB.setInputGain(inputGainSlider.getValue() / 2.0);
			updateInputGainLabel();
		} else if (src == shimmerLevelSlider) {
			gCB.setShimmerLevel(shimmerLevelSlider.getValue() / 100.0);
			updateShimmerLevelLabel();
		}
		updateResourceLabel();
	}

	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == topologyCombo) {
			gCB.setTopology(topologyCombo.getSelectedIndex());
		} else if (src == sizeCombo) {
			gCB.setSizePreset(sizeCombo.getSelectedIndex());
		} else if (src == stereoCheck) {
			gCB.setStereoOutput(stereoCheck.isSelected());
		} else if (src == lfoCombo) {
			gCB.setLfoDepth(lfoCombo.getSelectedIndex());
		} else if (src == shimmerCombo) {
			gCB.setShimmerMode(shimmerCombo.getSelectedIndex());
			boolean shimOn = shimmerCombo.getSelectedIndex() != 0;
			shimmerPitchCombo.setEnabled(shimOn);
			shimmerLevelPanel.setVisible(shimOn);
			frame.pack();
		} else if (src == shimmerPitchCombo) {
			gCB.setShimmerPitchSemitones(getShimmerPitchFromCombo());
		} else if (src == preDelayCheck) {
			gCB.setPreDelayEnabled(preDelayCheck.isSelected());
			preDelayPanel.setVisible(preDelayCheck.isSelected());
			frame.pack();
		}
		updateResourceLabel();
	}

	// WindowListener implementation
	public void windowClosing(WindowEvent e) {
		gCB.clearCP();
		frame.dispose();
	}
	public void windowOpened(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
}
