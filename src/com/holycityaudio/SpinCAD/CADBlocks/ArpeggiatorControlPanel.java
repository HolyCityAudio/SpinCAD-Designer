/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * ArpeggiatorControlPanel.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
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
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class ArpeggiatorControlPanel implements ChangeListener {

	private static final int MAX_STEPS = 12;

	private JDialog frame;
	private ArpeggiatorCADBlock gCB;

	private FineControlSlider numStepsSlider;
	private JTextField numStepsField;

	private FineControlSlider thresholdSlider;
	private JTextField thresholdField;

	private FineControlSlider[] stepSliders = new FineControlSlider[MAX_STEPS];
	private JTextField[] stepFields = new JTextField[MAX_STEPS];
	private JPanel[] stepPanels = new JPanel[MAX_STEPS];

	public ArpeggiatorControlPanel(ArpeggiatorCADBlock block) {
		gCB = block;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JDialog(SpinCADFrame.getInstance(), "Arpeggiator");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				// Number of Steps slider
				numStepsSlider = new FineControlSlider(JSlider.HORIZONTAL, 3, 12, (int) gCB.getnumSteps());
				numStepsSlider.addChangeListener(ArpeggiatorControlPanel.this);
				numStepsField = new JTextField();
				numStepsField.setHorizontalAlignment(JTextField.CENTER);
				numStepsField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				numStepsField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
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
				thresholdSlider.addChangeListener(ArpeggiatorControlPanel.this);
				thresholdField = new JTextField();
				thresholdField.setHorizontalAlignment(JTextField.CENTER);
				thresholdField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
				thresholdField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
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

				// Trigger slope selector
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

				// Buffer depth selector
				String[] bufferNames = { "512", "1024", "2048", "4096" };
				int[] bufferValues = { 512, 1024, 2048, 4096 };
				JComboBox<String> bufferCombo = new JComboBox<>(bufferNames);
				int currentBuf = gCB.getBufferSize();
				for (int i = 0; i < bufferValues.length; i++) {
					if (bufferValues[i] == currentBuf) {
						bufferCombo.setSelectedIndex(i);
						break;
					}
				}
				bufferCombo.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						gCB.setBufferSize(bufferValues[bufferCombo.getSelectedIndex()]);
					}
				});
				JPanel bufferPanel = new JPanel();
				bufferPanel.setLayout(new BoxLayout(bufferPanel, BoxLayout.X_AXIS));
				bufferPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				bufferPanel.add(Box.createRigidArea(new Dimension(5, 4)));
				bufferPanel.add(new JLabel("Buffer Depth: "));
				bufferPanel.add(bufferCombo);
				bufferPanel.add(Box.createRigidArea(new Dimension(5, 4)));
				frame.add(bufferPanel);

				// LFO selection radio buttons
				JRadioButton lfo0 = new JRadioButton("LFO 0");
				JRadioButton lfo1 = new JRadioButton("LFO 1");
				if (gCB.getLFOSel() == 0) lfo0.setSelected(true);
				else lfo1.setSelected(true);

				ButtonGroup lfoGroup = new ButtonGroup();
				lfoGroup.add(lfo0);
				lfoGroup.add(lfo1);

				lfo0.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) { gCB.setLFOSel(0); }
				});
				lfo1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) { gCB.setLFOSel(1); }
				});

				JPanel lfoPanel = new JPanel(new GridLayout(1, 2));
				lfoPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
				lfoPanel.add(lfo0);
				lfoPanel.add(lfo1);
				frame.add(lfoPanel);

				// Semitone step sliders (-12 to +19)
				for (int i = 0; i < MAX_STEPS; i++) {
					final int idx = i;
					stepSliders[i] = new FineControlSlider(JSlider.HORIZONTAL, -12, 19, gCB.getSemitone(i));
					stepSliders[i].addChangeListener(ArpeggiatorControlPanel.this);
					stepFields[i] = new JTextField();
					stepFields[i].setHorizontalAlignment(JTextField.CENTER);
					stepFields[i].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
					stepFields[i].addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							try {
								int val = Integer.parseInt(
									stepFields[idx].getText().replaceAll("[^0-9\\-]", ""));
								val = Math.max(-12, Math.min(19, val));
								stepSliders[idx].setValue(val);
								gCB.setSemitone(idx, val);
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
					gCB.setSemitone(i, stepSliders[i].getValue());
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
		int semi = gCB.getSemitone(i);
		String sign = semi > 0 ? "+" : "";
		String interval = ArpeggiatorCADBlock.intervalName(semi);
		stepFields[i].setText("Step " + (i + 1) + ": " + sign + semi + " (" + interval + ")");
	}
}
