/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * DrumDelaySrcControlPanel.java
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
package com.holycityaudio.SpinCAD.ControlPanel;

import org.andrewkilpatrick.elmGen.ElmProgram;
import javax.swing.JDialog;
import com.holycityaudio.SpinCAD.SpinCADFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ItemEvent;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JTextField;
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
import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.spinCADControlPanel;
import com.holycityaudio.SpinCAD.CADBlocks.DrumDelaySrcCADBlock;

@SuppressWarnings("unused")
public class DrumDelaySrcControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private DrumDelaySrcCADBlock gCB;
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

public DrumDelaySrcControlPanel(DrumDelaySrcCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Drum Delay");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						inputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 1.0),(int) (0 * 1.0), (int) (20 * Math.log10(gCB.getinputGain()) * 1.0));
						inputGainSlider.addChangeListener(new DrumDelaySrcListener());
						inputGainField = new JTextField();
						inputGainField.setHorizontalAlignment(JTextField.CENTER);
						Border inputGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputGainField.setBorder(inputGainBorder1);
						inputGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(inputGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(inputGainSlider.getMinimum(), Math.min(inputGainSlider.getMaximum(), sliderVal));
						inputGainSlider.setValue(sliderVal);
						gCB.setinputGain((double) sliderVal / 1.0);
									updateinputGainLabel();
								} catch (NumberFormatException ex) {
									updateinputGainLabel();
								}
							}
						});
						updateinputGainLabel();
			
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
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						fbkGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 1.0),(int) (0 * 1.0), (int) (20 * Math.log10(gCB.getfbkGain()) * 1.0));
						fbkGainSlider.addChangeListener(new DrumDelaySrcListener());
						fbkGainField = new JTextField();
						fbkGainField.setHorizontalAlignment(JTextField.CENTER);
						Border fbkGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						fbkGainField.setBorder(fbkGainBorder1);
						fbkGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(fbkGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(fbkGainSlider.getMinimum(), Math.min(fbkGainSlider.getMaximum(), sliderVal));
						fbkGainSlider.setValue(sliderVal);
						gCB.setfbkGain((double) sliderVal / 1.0);
									updatefbkGainLabel();
								} catch (NumberFormatException ex) {
									updatefbkGainLabel();
								}
							}
						});
						updatefbkGainLabel();
			
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
						delayLengthSlider.addChangeListener(new DrumDelaySrcListener());
						delayLengthField = new JTextField();
						delayLengthField.setHorizontalAlignment(JTextField.CENTER);
						Border delayLengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						delayLengthField.setBorder(delayLengthBorder1);
						delayLengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(delayLengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(delayLengthSlider.getMinimum(), Math.min(delayLengthSlider.getMaximum(), sliderVal));
						delayLengthSlider.setValue(sliderVal);
						gCB.setdelayLength((double) sliderVal / 1);
									updatedelayLengthLabel();
								} catch (NumberFormatException ex) {
									updatedelayLengthLabel();
								}
							}
						});
						updatedelayLengthLabel();
			
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
					tap1RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap1Ratio() * 1000.0));
						tap1RatioSlider.addChangeListener(new DrumDelaySrcListener());
						tap1RatioField = new JTextField();
						tap1RatioField.setHorizontalAlignment(JTextField.CENTER);
						Border tap1RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap1RatioField.setBorder(tap1RatioBorder1);
						tap1RatioField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap1RatioField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap1RatioSlider.getMinimum(), Math.min(tap1RatioSlider.getMaximum(), sliderVal));
						tap1RatioSlider.setValue(sliderVal);
						gCB.settap1Ratio((double) sliderVal / 1000.0);
									updatetap1RatioLabel();
								} catch (NumberFormatException ex) {
									updatetap1RatioLabel();
								}
							}
						});
						updatetap1RatioLabel();
			
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
					tap2RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap2Ratio() * 1000.0));
						tap2RatioSlider.addChangeListener(new DrumDelaySrcListener());
						tap2RatioField = new JTextField();
						tap2RatioField.setHorizontalAlignment(JTextField.CENTER);
						Border tap2RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap2RatioField.setBorder(tap2RatioBorder1);
						tap2RatioField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap2RatioField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap2RatioSlider.getMinimum(), Math.min(tap2RatioSlider.getMaximum(), sliderVal));
						tap2RatioSlider.setValue(sliderVal);
						gCB.settap2Ratio((double) sliderVal / 1000.0);
									updatetap2RatioLabel();
								} catch (NumberFormatException ex) {
									updatetap2RatioLabel();
								}
							}
						});
						updatetap2RatioLabel();
			
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
					tap3RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap3Ratio() * 1000.0));
						tap3RatioSlider.addChangeListener(new DrumDelaySrcListener());
						tap3RatioField = new JTextField();
						tap3RatioField.setHorizontalAlignment(JTextField.CENTER);
						Border tap3RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap3RatioField.setBorder(tap3RatioBorder1);
						tap3RatioField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap3RatioField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap3RatioSlider.getMinimum(), Math.min(tap3RatioSlider.getMaximum(), sliderVal));
						tap3RatioSlider.setValue(sliderVal);
						gCB.settap3Ratio((double) sliderVal / 1000.0);
									updatetap3RatioLabel();
								} catch (NumberFormatException ex) {
									updatetap3RatioLabel();
								}
							}
						});
						updatetap3RatioLabel();
			
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
					tap4RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap4Ratio() * 1000.0));
						tap4RatioSlider.addChangeListener(new DrumDelaySrcListener());
						tap4RatioField = new JTextField();
						tap4RatioField.setHorizontalAlignment(JTextField.CENTER);
						Border tap4RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap4RatioField.setBorder(tap4RatioBorder1);
						tap4RatioField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap4RatioField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap4RatioSlider.getMinimum(), Math.min(tap4RatioSlider.getMaximum(), sliderVal));
						tap4RatioSlider.setValue(sliderVal);
						gCB.settap4Ratio((double) sliderVal / 1000.0);
									updatetap4RatioLabel();
								} catch (NumberFormatException ex) {
									updatetap4RatioLabel();
								}
							}
						});
						updatetap4RatioLabel();
			
						Border tap4Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap4RatioinnerPanel = new JPanel();
			
						tap4RatioinnerPanel.setLayout(new BoxLayout(tap4RatioinnerPanel, BoxLayout.Y_AXIS));
						tap4RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4RatioinnerPanel.add(tap4RatioField);
						tap4RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4RatioinnerPanel.add(tap4RatioSlider);
						tap4RatioinnerPanel.setBorder(tap4Ratioborder2);
			
						frame.add(tap4RatioinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class DrumDelaySrcListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/1.0));			    					
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/1.0));			    					
				updatefbkGainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));			    					
				updatedelayLengthLabel();
			}
			if(ce.getSource() == tap1RatioSlider) {
			gCB.settap1Ratio((double) (tap1RatioSlider.getValue()/1000.0));
				updatetap1RatioLabel();
			}
			if(ce.getSource() == tap2RatioSlider) {
			gCB.settap2Ratio((double) (tap2RatioSlider.getValue()/1000.0));
				updatetap2RatioLabel();
			}
			if(ce.getSource() == tap3RatioSlider) {
			gCB.settap3Ratio((double) (tap3RatioSlider.getValue()/1000.0));
				updatetap3RatioLabel();
			}
			if(ce.getSource() == tap4RatioSlider) {
			gCB.settap4Ratio((double) (tap4RatioSlider.getValue()/1000.0));
				updatetap4RatioLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class DrumDelaySrcItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class DrumDelaySrcActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainLabel() {
		inputGainField.setText("Input Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));		
		}		
		private void updatefbkGainLabel() {
		fbkGainField.setText("Feedback Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthField.setText("Delay Time (ms):  " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));		
		}		
		private void updatetap1RatioLabel() {
		tap1RatioField.setText("Tap 1 Time (%):  " + String.format("%4.2f", gCB.gettap1Ratio()));		
		}		
		private void updatetap2RatioLabel() {
		tap2RatioField.setText("Tap 2 Time (%):  " + String.format("%4.2f", gCB.gettap2Ratio()));		
		}		
		private void updatetap3RatioLabel() {
		tap3RatioField.setText("Tap 3 Time (%):  " + String.format("%4.2f", gCB.gettap3Ratio()));		
		}		
		private void updatetap4RatioLabel() {
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
