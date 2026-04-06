/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * servoControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.servoCADBlock;

@SuppressWarnings("unused")
public class servoControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private servoCADBlock gCB;
	// declare the controls
	FineControlSlider inputGainSlider;
	JTextField  inputGainField;
	FineControlSlider fbkGainSlider;
	JTextField  fbkGainField;
	FineControlSlider servoGainSlider;
	JTextField  servoGainField;
	FineControlSlider freqSlider;
	JTextField  freqField;
	FineControlSlider tap1RatioSlider;
	JTextField  tap1RatioField;
	private JComboBox <String> lfoSelComboBox; 

public servoControlPanel(servoCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Servo Flanger");
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
						inputGainSlider.addChangeListener(new servoListener());
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
						fbkGainSlider.addChangeListener(new servoListener());
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
					servoGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.49 * 100.0), (int) (gCB.getservoGain() * 100.0));
						servoGainSlider.addChangeListener(new servoListener());
						servoGainField = new JTextField();
						servoGainField.setHorizontalAlignment(JTextField.CENTER);
						Border servoGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						servoGainField.setBorder(servoGainBorder1);
						servoGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(servoGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(servoGainSlider.getMinimum(), Math.min(servoGainSlider.getMaximum(), sliderVal));
						servoGainSlider.setValue(sliderVal);
						gCB.setservoGain((double) sliderVal / 100.0);
									updateservoGainLabel();
								} catch (NumberFormatException ex) {
									updateservoGainLabel();
								}
							}
						});
						updateservoGainLabel();
			
						Border servoGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel servoGaininnerPanel = new JPanel();
			
						servoGaininnerPanel.setLayout(new BoxLayout(servoGaininnerPanel, BoxLayout.Y_AXIS));
						servoGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						servoGaininnerPanel.add(servoGainField);
						servoGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						servoGaininnerPanel.add(servoGainSlider);
						servoGaininnerPanel.setBorder(servoGainborder2);
			
						frame.add(servoGaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						freqSlider = SpinCADBlock.LogSlider(500,7500,gCB.getfreq(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						freqSlider.addChangeListener(new servoListener());
						freqField = new JTextField();
						freqField.setHorizontalAlignment(JTextField.CENTER);
						Border freqBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						freqField.setBorder(freqBorder1);
						freqField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(freqField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(freqSlider.getMinimum(), Math.min(freqSlider.getMaximum(), sliderVal));
						freqSlider.setValue(sliderVal);
						gCB.setfreq(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatefreqLabel();
								} catch (NumberFormatException ex) {
									updatefreqLabel();
								}
							}
						});
						updatefreqLabel();
			
						Border freqborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel freqinnerPanel = new JPanel();
			
						freqinnerPanel.setLayout(new BoxLayout(freqinnerPanel, BoxLayout.Y_AXIS));
						freqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						freqinnerPanel.add(freqField);
						freqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						freqinnerPanel.add(freqSlider);
						freqinnerPanel.setBorder(freqborder2);
			
						frame.add(freqinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap1RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (0.05 * 1000.0), (int) (gCB.gettap1Ratio() * 1000.0));
						tap1RatioSlider.addChangeListener(new servoListener());
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
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new servoActionListener());
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class servoListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/1.0));			    					
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/1.0));			    					
				updatefbkGainLabel();
			}
			if(ce.getSource() == servoGainSlider) {
			gCB.setservoGain((double) (servoGainSlider.getValue()/100.0));
				updateservoGainLabel();
			}
			if(ce.getSource() == freqSlider) {
			gCB.setfreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(freqSlider.getValue()), 100.0)));
				updatefreqLabel();
			}
			if(ce.getSource() == tap1RatioSlider) {
			gCB.settap1Ratio((double) (tap1RatioSlider.getValue()/1000.0));
				updatetap1RatioLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class servoItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class servoActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updateinputGainLabel() {
		inputGainField.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));		
		}		
		private void updatefbkGainLabel() {
		fbkGainField.setText("Feedback Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));		
		}		
		private void updateservoGainLabel() {
		servoGainField.setText("Servo Gain " + String.format("%4.2f", gCB.getservoGain()));		
		}		
		private void updatefreqLabel() {
		freqField.setText("Low_Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getfreq())) + " Hz");		
		}		
		private void updatetap1RatioLabel() {
		tap1RatioField.setText("Tap Time Ratio " + String.format("%4.3f", gCB.gettap1Ratio()));		
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
