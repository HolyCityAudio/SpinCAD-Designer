/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * DrumDelayControlPanel.java
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
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.holycityaudio.SpinCAD.CADBlocks;

import org.andrewkilpatrick.elmGen.ElmProgram;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ItemEvent;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.Dimension;
import java.text.DecimalFormat;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.spinCADControlPanel;
import com.holycityaudio.SpinCAD.FineControlSlider;

@SuppressWarnings("unused")
public class DrumDelayControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private DrumDelayCADBlock gCB;
	// declare the controls
	FineControlSlider inputGainSlider;
	JTextField  inputGainField;
	FineControlSlider fbkGainSlider;
	JTextField  fbkGainField;
	FineControlSlider delayLengthSlider;
	JTextField  delayLengthField;
	FineControlSlider tap1RatioSlider;
	JTextField  tap1RatioField;
	FineControlSlider tap2RatioSlider;
	JTextField  tap2RatioField;
	FineControlSlider tap3RatioSlider;
	JTextField  tap3RatioField;
	FineControlSlider tap4RatioSlider;
	JTextField  tap4RatioField;
	private JComboBox<String> subdivisionComboBox;
	private static final int[] SUBDIVISION_STEPS = {0, 750, 1000, 1500, 2000};

public DrumDelayControlPanel(DrumDelayCADBlock genericCADBlock) {

		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				gCB.controlPanelFrame = frame;
				frame.setTitle("Drum Delay");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// dB level slider: 1 dB normal drag, 0.1 dB fine (Ctrl+drag)
						inputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-240),(int) (0), (int) (20 * Math.log10(gCB.getinputGain()) * 10));
						inputGainSlider.setSubdivision(10);
						inputGainSlider.addChangeListener(new DrumDelayListener());
						inputGainField = new JTextField();
						inputGainField.setHorizontalAlignment(JTextField.CENTER);
						inputGainField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String text = inputGainField.getText().replaceAll("[^\\d.\\-]", "");
									double dbVal = Double.parseDouble(text);
									int sliderVal = (int) Math.round(dbVal * 10);
									sliderVal = Math.max(inputGainSlider.getMinimum(), Math.min(inputGainSlider.getMaximum(), sliderVal));
									gCB.setinputGain((double) (sliderVal/10.0));
									inputGainSlider.setValue(sliderVal);
									updateinputGainField();
								} catch (NumberFormatException ex) {
									updateinputGainField();
								}
							}
						});
						Border inputGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputGainField.setBorder(inputGainBorder1);
						updateinputGainField();

						Border inputGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel inputGaininnerPanel = new JPanel();

						inputGaininnerPanel.setLayout(new BoxLayout(inputGaininnerPanel, BoxLayout.Y_AXIS));
						inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputGaininnerPanel.add(inputGainField);
						inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputGaininnerPanel.add(inputGainSlider);
						inputGaininnerPanel.setBorder(inputGainborder2);

						frame.add(inputGaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// dB level slider: 1 dB normal drag, 0.1 dB fine (Ctrl+drag)
						fbkGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-240),(int) (0), (int) (20 * Math.log10(gCB.getfbkGain()) * 10));
						fbkGainSlider.setSubdivision(10);
						fbkGainSlider.addChangeListener(new DrumDelayListener());
						fbkGainField = new JTextField();
						fbkGainField.setHorizontalAlignment(JTextField.CENTER);
						fbkGainField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String text = fbkGainField.getText().replaceAll("[^\\d.\\-]", "");
									double dbVal = Double.parseDouble(text);
									int sliderVal = (int) Math.round(dbVal * 10);
									sliderVal = Math.max(fbkGainSlider.getMinimum(), Math.min(fbkGainSlider.getMaximum(), sliderVal));
									gCB.setfbkGain((double) (sliderVal/10.0));
									fbkGainSlider.setValue(sliderVal);
									updatefbkGainField();
								} catch (NumberFormatException ex) {
									updatefbkGainField();
								}
							}
						});
						Border fbkGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						fbkGainField.setBorder(fbkGainBorder1);
						updatefbkGainField();

						Border fbkGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel fbkGaininnerPanel = new JPanel();

						fbkGaininnerPanel.setLayout(new BoxLayout(fbkGaininnerPanel, BoxLayout.Y_AXIS));
						fbkGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						fbkGaininnerPanel.add(fbkGainField);
						fbkGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						fbkGaininnerPanel.add(fbkGainSlider);
						fbkGaininnerPanel.setBorder(fbkGainborder2);

						frame.add(fbkGaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					delayLengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (32767 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
						delayLengthSlider.addChangeListener(new DrumDelayListener());
						delayLengthField = new JTextField();
						delayLengthField.setHorizontalAlignment(JTextField.CENTER);
						delayLengthField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String text = delayLengthField.getText().replaceAll("[^\\d.\\-]", "");
									double msVal = Double.parseDouble(text);
									// display shows (1000 * delayLength)/samplerate as ms
									// so delayLength = msVal * samplerate / 1000
									int sliderVal = (int) Math.round(msVal * ElmProgram.getSamplerate() / 1000.0);
									sliderVal = Math.max(delayLengthSlider.getMinimum(), Math.min(delayLengthSlider.getMaximum(), sliderVal));
									gCB.setdelayLength((double) (sliderVal/1));
									delayLengthSlider.setValue(sliderVal);
									updatedelayLengthField();
								} catch (NumberFormatException ex) {
									updatedelayLengthField();
								}
							}
						});
						Border delayLengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						delayLengthField.setBorder(delayLengthBorder1);
						updatedelayLengthField();

						Border delayLengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel delayLengthinnerPanel = new JPanel();

						delayLengthinnerPanel.setLayout(new BoxLayout(delayLengthinnerPanel, BoxLayout.Y_AXIS));
						delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						delayLengthinnerPanel.add(delayLengthField);
						delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						delayLengthinnerPanel.add(delayLengthSlider);
						delayLengthinnerPanel.setBorder(delayLengthborder2);

						frame.add(delayLengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap1RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 6000.0),(int) (1.0 * 6000.0), (int) (gCB.gettap1Ratio() * 6000.0));
						tap1RatioSlider.addChangeListener(new DrumDelayListener());
						tap1RatioField = new JTextField();
						tap1RatioField.setHorizontalAlignment(JTextField.CENTER);
						tap1RatioField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String text = tap1RatioField.getText().replaceAll("[^\\d.\\-]", "");
									double val = Double.parseDouble(text);
									int sliderVal = (int) Math.round(val * 6000.0);
									sliderVal = Math.max(tap1RatioSlider.getMinimum(), Math.min(tap1RatioSlider.getMaximum(), sliderVal));
									gCB.settap1Ratio((double) (sliderVal/6000.0));
									tap1RatioSlider.setValue(sliderVal);
									updatetap1RatioField();
								} catch (NumberFormatException ex) {
									updatetap1RatioField();
								}
							}
						});
						Border tap1RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap1RatioField.setBorder(tap1RatioBorder1);
						updatetap1RatioField();

						Border tap1Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap1RatioinnerPanel = new JPanel();

						tap1RatioinnerPanel.setLayout(new BoxLayout(tap1RatioinnerPanel, BoxLayout.Y_AXIS));
						tap1RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap1RatioinnerPanel.add(tap1RatioField);
						tap1RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap1RatioinnerPanel.add(tap1RatioSlider);
						tap1RatioinnerPanel.setBorder(tap1Ratioborder2);

						frame.add(tap1RatioinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap2RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 6000.0),(int) (1.0 * 6000.0), (int) (gCB.gettap2Ratio() * 6000.0));
						tap2RatioSlider.addChangeListener(new DrumDelayListener());
						tap2RatioField = new JTextField();
						tap2RatioField.setHorizontalAlignment(JTextField.CENTER);
						tap2RatioField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String text = tap2RatioField.getText().replaceAll("[^\\d.\\-]", "");
									double val = Double.parseDouble(text);
									int sliderVal = (int) Math.round(val * 6000.0);
									sliderVal = Math.max(tap2RatioSlider.getMinimum(), Math.min(tap2RatioSlider.getMaximum(), sliderVal));
									gCB.settap2Ratio((double) (sliderVal/6000.0));
									tap2RatioSlider.setValue(sliderVal);
									updatetap2RatioField();
								} catch (NumberFormatException ex) {
									updatetap2RatioField();
								}
							}
						});
						Border tap2RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap2RatioField.setBorder(tap2RatioBorder1);
						updatetap2RatioField();

						Border tap2Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap2RatioinnerPanel = new JPanel();

						tap2RatioinnerPanel.setLayout(new BoxLayout(tap2RatioinnerPanel, BoxLayout.Y_AXIS));
						tap2RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap2RatioinnerPanel.add(tap2RatioField);
						tap2RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap2RatioinnerPanel.add(tap2RatioSlider);
						tap2RatioinnerPanel.setBorder(tap2Ratioborder2);

						frame.add(tap2RatioinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap3RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 6000.0),(int) (1.0 * 6000.0), (int) (gCB.gettap3Ratio() * 6000.0));
						tap3RatioSlider.addChangeListener(new DrumDelayListener());
						tap3RatioField = new JTextField();
						tap3RatioField.setHorizontalAlignment(JTextField.CENTER);
						tap3RatioField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String text = tap3RatioField.getText().replaceAll("[^\\d.\\-]", "");
									double val = Double.parseDouble(text);
									int sliderVal = (int) Math.round(val * 6000.0);
									sliderVal = Math.max(tap3RatioSlider.getMinimum(), Math.min(tap3RatioSlider.getMaximum(), sliderVal));
									gCB.settap3Ratio((double) (sliderVal/6000.0));
									tap3RatioSlider.setValue(sliderVal);
									updatetap3RatioField();
								} catch (NumberFormatException ex) {
									updatetap3RatioField();
								}
							}
						});
						Border tap3RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap3RatioField.setBorder(tap3RatioBorder1);
						updatetap3RatioField();

						Border tap3Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap3RatioinnerPanel = new JPanel();

						tap3RatioinnerPanel.setLayout(new BoxLayout(tap3RatioinnerPanel, BoxLayout.Y_AXIS));
						tap3RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap3RatioinnerPanel.add(tap3RatioField);
						tap3RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap3RatioinnerPanel.add(tap3RatioSlider);
						tap3RatioinnerPanel.setBorder(tap3Ratioborder2);

						frame.add(tap3RatioinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap4RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 6000.0),(int) (1.0 * 6000.0), (int) (gCB.gettap4Ratio() * 6000.0));
						tap4RatioSlider.addChangeListener(new DrumDelayListener());
						tap4RatioField = new JTextField();
						tap4RatioField.setHorizontalAlignment(JTextField.CENTER);
						tap4RatioField.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									String text = tap4RatioField.getText().replaceAll("[^\\d.\\-]", "");
									double val = Double.parseDouble(text);
									int sliderVal = (int) Math.round(val * 6000.0);
									sliderVal = Math.max(tap4RatioSlider.getMinimum(), Math.min(tap4RatioSlider.getMaximum(), sliderVal));
									gCB.settap4Ratio((double) (sliderVal/6000.0));
									tap4RatioSlider.setValue(sliderVal);
									updatetap4RatioField();
								} catch (NumberFormatException ex) {
									updatetap4RatioField();
								}
							}
						});
						Border tap4RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap4RatioField.setBorder(tap4RatioBorder1);
						updatetap4RatioField();

						Border tap4Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap4RatioinnerPanel = new JPanel();

						tap4RatioinnerPanel.setLayout(new BoxLayout(tap4RatioinnerPanel, BoxLayout.Y_AXIS));
						tap4RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4RatioinnerPanel.add(tap4RatioField);
						tap4RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4RatioinnerPanel.add(tap4RatioSlider);
						tap4RatioinnerPanel.setBorder(tap4Ratioborder2);

						frame.add(tap4RatioinnerPanel);

				// Subdivision combo box
				subdivisionComboBox = new JComboBox<>(new String[]{"None", "1/8", "1/6", "1/4", "1/3"});
				subdivisionComboBox.setSelectedIndex(gCB.getsubdivision());
				subdivisionComboBox.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						int idx = subdivisionComboBox.getSelectedIndex();
						gCB.setsubdivision(idx);
						int step = SUBDIVISION_STEPS[idx];
						tap1RatioSlider.setSubdivision(step);
						tap2RatioSlider.setSubdivision(step);
						tap3RatioSlider.setSubdivision(step);
						tap4RatioSlider.setSubdivision(step);
					}
				});

				Border subdivisionBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel subdivisionPanel = new JPanel();
				subdivisionPanel.setLayout(new BoxLayout(subdivisionPanel, BoxLayout.Y_AXIS));
				JTextField subdivisionLabel = new JTextField("Subdivision");
				subdivisionLabel.setHorizontalAlignment(JTextField.CENTER);
				subdivisionLabel.setEditable(false);
				Border subdivisionLabelBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				subdivisionLabel.setBorder(subdivisionLabelBorder);
				subdivisionPanel.add(Box.createRigidArea(new Dimension(5,4)));
				subdivisionPanel.add(subdivisionLabel);
				subdivisionPanel.add(Box.createRigidArea(new Dimension(5,4)));
				subdivisionPanel.add(subdivisionComboBox);
				subdivisionPanel.add(Box.createRigidArea(new Dimension(5,4)));
				subdivisionPanel.setBorder(subdivisionBorder);
				frame.add(subdivisionPanel);

				// restore saved subdivision state
				{
					int savedStep = SUBDIVISION_STEPS[gCB.getsubdivision()];
					tap1RatioSlider.setSubdivision(savedStep);
					tap2RatioSlider.setSubdivision(savedStep);
					tap3RatioSlider.setSubdivision(savedStep);
					tap4RatioSlider.setSubdivision(savedStep);
				}

				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getControlPanelLocation(100, 100));
				frame.setVisible(true);
			}
		});
		}

		// add change listener for Sliders, Spinners
		class DrumDelayListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/10.0));
				updateinputGainField();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/10.0));
				updatefbkGainField();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));
				updatedelayLengthField();
			}
			if(ce.getSource() == tap1RatioSlider) {
			gCB.settap1Ratio((double) (tap1RatioSlider.getValue()/6000.0));
				updatetap1RatioField();
			}
			if(ce.getSource() == tap2RatioSlider) {
			gCB.settap2Ratio((double) (tap2RatioSlider.getValue()/6000.0));
				updatetap2RatioField();
			}
			if(ce.getSource() == tap3RatioSlider) {
			gCB.settap3Ratio((double) (tap3RatioSlider.getValue()/6000.0));
				updatetap3RatioField();
			}
			if(ce.getSource() == tap4RatioSlider) {
			gCB.settap4Ratio((double) (tap4RatioSlider.getValue()/6000.0));
				updatetap4RatioField();
			}
			}
		}

		// add item state changed listener for Checkbox
		class DrumDelayItemListener implements java.awt.event.ItemListener {

		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}

		// add action listener for Combo Box
		class DrumDelayActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainField() {
		inputGainField.setText("Input Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));
		}
		private void updatefbkGainField() {
		fbkGainField.setText("Feedback Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));
		}
		private void updatedelayLengthField() {
		delayLengthField.setText("Delay Time (ms):  " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));
		}
		private void updatetap1RatioField() {
		tap1RatioField.setText("Tap 1 Time (%):  " + String.format("%4.2f", gCB.gettap1Ratio()));
		}
		private void updatetap2RatioField() {
		tap2RatioField.setText("Tap 2 Time (%):  " + String.format("%4.2f", gCB.gettap2Ratio()));
		}
		private void updatetap3RatioField() {
		tap3RatioField.setText("Tap 3 Time (%):  " + String.format("%4.2f", gCB.gettap3Ratio()));
		}
		private void updatetap4RatioField() {
		tap4RatioField.setText("Tap 4 Time (%):  " + String.format("%4.2f", gCB.gettap4Ratio()));
		}

		class MyWindowListener implements WindowListener
		{
		@Override
			public void windowActivated(WindowEvent arg0) {
			}

		@Override
			public void windowClosed(WindowEvent arg0) {
			}

		@Override
			public void windowClosing(WindowEvent arg0) {
				gCB.clearCP();
			}

		@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {

		}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		}

	}
