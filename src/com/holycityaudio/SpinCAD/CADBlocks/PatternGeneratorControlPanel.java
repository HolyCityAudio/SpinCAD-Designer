/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * PatternGeneratorControlPanel.java
 * Copyright (C) 2015 - Gary Worsham
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

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class PatternGeneratorControlPanel implements ChangeListener {

	private static final int MAX_STEPS = 12;

	private JDialog frame;
	private PatternGeneratorCADBlock gCB;

	private FineControlSlider numStepsSlider;
	private JTextField numStepsField;

	private FineControlSlider thresholdSlider;
	private JTextField thresholdField;

	private FineControlSlider[] stepSliders = new FineControlSlider[MAX_STEPS];
	private JTextField[] stepFields = new JTextField[MAX_STEPS];
	private JPanel[] stepPanels = new JPanel[MAX_STEPS];

	public PatternGeneratorControlPanel(PatternGeneratorCADBlock block) {
		gCB = block;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JDialog(SpinCADFrame.getInstance(), "Pattern Generator");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				// Number of Steps slider (at the top)
				numStepsSlider = new FineControlSlider(JSlider.HORIZONTAL, 3, 12, (int) gCB.getnumSteps());
				numStepsSlider.addChangeListener(PatternGeneratorControlPanel.this);
				numStepsField = new JTextField();
				numStepsField.setHorizontalAlignment(JTextField.CENTER);
				numStepsField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				numStepsField.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							double val = Double.parseDouble(numStepsField.getText().replaceAll("[^0-9.\\-]", ""));
							int sliderVal = (int) Math.round(val);
							sliderVal = Math.max(3, Math.min(12, sliderVal));
							numStepsSlider.setValue(sliderVal);
							gCB.setnumSteps(sliderVal);
							updateNumStepsLabel();
							updateStepVisibility();
						} catch (NumberFormatException ex) {
							updateNumStepsLabel();
						}
					}
				});
				updateNumStepsLabel();
				frame.add(buildSliderPanel(numStepsField, numStepsSlider));

				// Threshold slider
				thresholdSlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 90, (int) (gCB.getthreshold() * 100.0));
				thresholdSlider.addChangeListener(PatternGeneratorControlPanel.this);
				thresholdField = new JTextField();
				thresholdField.setHorizontalAlignment(JTextField.CENTER);
				thresholdField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				thresholdField.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							double val = Double.parseDouble(thresholdField.getText().replaceAll("[^0-9.\\-]", ""));
							int sliderVal = (int) Math.round(val * 100.0);
							sliderVal = Math.max(10, Math.min(90, sliderVal));
							thresholdSlider.setValue(sliderVal);
							gCB.setthreshold(sliderVal / 100.0);
							updateThresholdLabel();
						} catch (NumberFormatException ex) {
							updateThresholdLabel();
						}
					}
				});
				updateThresholdLabel();
				frame.add(buildSliderPanel(thresholdField, thresholdSlider));

				// Slope (trigger edge) selector
				String[] slopeNames = { "Positive", "Negative", "Both" };
				JComboBox<String> slopeCombo = new JComboBox<>(slopeNames);
				slopeCombo.setSelectedIndex(gCB.getSlope());
				slopeCombo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gCB.setSlope(slopeCombo.getSelectedIndex());
					}
				});
				JPanel slopePanel = new JPanel();
				slopePanel.setLayout(new BoxLayout(slopePanel, BoxLayout.X_AXIS));
				slopePanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				slopePanel.add(Box.createRigidArea(new Dimension(5, 4)));
				slopePanel.add(new JLabel("Trigger Slope: "));
				slopePanel.add(slopeCombo);
				slopePanel.add(Box.createRigidArea(new Dimension(5, 4)));
				frame.add(slopePanel);

				// Step sliders
				for (int i = 0; i < MAX_STEPS; i++) {
					final int idx = i;
					stepSliders[i] = new FineControlSlider(JSlider.HORIZONTAL, 0, 999, (int) (gCB.getstep(i) * 1000.0));
					stepSliders[i].addChangeListener(PatternGeneratorControlPanel.this);
					stepFields[i] = new JTextField();
					stepFields[i].setHorizontalAlignment(JTextField.CENTER);
					stepFields[i].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					stepFields[i].addActionListener(new java.awt.event.ActionListener() {
						@Override
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								double val = Double.parseDouble(stepFields[idx].getText().replaceAll("[^0-9.\\-]", ""));
								int sliderVal = (int) Math.round(val * 1000.0);
								sliderVal = Math.max(0, Math.min(999, sliderVal));
								stepSliders[idx].setValue(sliderVal);
								gCB.setstep(idx, sliderVal / 1000.0);
								updateStepLabel(idx);
							} catch (NumberFormatException ex) {
								updateStepLabel(idx);
							}
						}
					});
					updateStepLabel(i);
					stepPanels[i] = buildSliderPanel(stepFields[i], stepSliders[i]);
					frame.add(stepPanels[i]);
				}

				updateStepVisibility();

				frame.addWindowListener(new WindowListener() {
					public void windowClosing(WindowEvent e) { gCB.clearCP(); }
					public void windowOpened(WindowEvent e) {}
					public void windowClosed(WindowEvent e) {}
					public void windowIconified(WindowEvent e) {}
					public void windowDeiconified(WindowEvent e) {}
					public void windowActivated(WindowEvent e) {}
					public void windowDeactivated(WindowEvent e) {}
				});

				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);
			}
		});
	}

	private JPanel buildSliderPanel(JTextField field, FineControlSlider slider) {
		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(Box.createRigidArea(new Dimension(5, 4)));
		panel.add(field);
		panel.add(Box.createRigidArea(new Dimension(5, 4)));
		panel.add(slider);
		panel.setBorder(border);
		return panel;
	}

	private void updateStepVisibility() {
		int nSteps = (int) gCB.getnumSteps();
		for (int i = 0; i < MAX_STEPS; i++) {
			stepPanels[i].setVisible(i < nSteps);
		}
		if (frame != null) {
			frame.pack();
		}
	}

	@Override
	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == numStepsSlider) {
			gCB.setnumSteps(numStepsSlider.getValue());
			updateNumStepsLabel();
			updateStepVisibility();
		} else if (ce.getSource() == thresholdSlider) {
			gCB.setthreshold(thresholdSlider.getValue() / 100.0);
			updateThresholdLabel();
		} else {
			for (int i = 0; i < MAX_STEPS; i++) {
				if (ce.getSource() == stepSliders[i]) {
					gCB.setstep(i, stepSliders[i].getValue() / 1000.0);
					updateStepLabel(i);
					break;
				}
			}
		}
	}

	private void updateNumStepsLabel() {
		numStepsField.setText("Number of Steps " + String.format("%d", (int) gCB.getnumSteps()));
	}

	private void updateThresholdLabel() {
		thresholdField.setText("Threshold " + String.format("%4.2f", gCB.getthreshold()));
	}

	private void updateStepLabel(int i) {
		stepFields[i].setText("Step " + (i + 1) + " " + String.format("%4.3f", gCB.getstep(i)));
	}
}
