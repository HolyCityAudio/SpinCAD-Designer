/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rms_compressorControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.rms_compressorCADBlock;

@SuppressWarnings("unused")
public class rms_compressorControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private rms_compressorCADBlock gCB;
	// declare the controls
	FineControlSlider inGainSlider;
	JTextField  inGainField;
	FineControlSlider strengthSlider;
	JTextField  strengthField;
	FineControlSlider threshDbSlider;
	JTextField  threshDbField;
	FineControlSlider attTimeSlider;
	JTextField  attTimeField;
	FineControlSlider relTimeSlider;
	JTextField  relTimeField;
	FineControlSlider makeupDbSlider;
	JTextField  makeupDbField;
	FineControlSlider trimSlider;
	JTextField  trimField;

public rms_compressorControlPanel(rms_compressorCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "RMS_Compressor");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					inGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.1 * 100.0),(int) (2.0 * 100.0), (int) (gCB.getinGain() * 100.0));
						inGainSlider.addChangeListener(new rms_compressorListener());
						inGainField = new JTextField();
						inGainField.setHorizontalAlignment(JTextField.CENTER);
						Border inGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inGainField.setBorder(inGainBorder1);
						inGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(inGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(inGainSlider.getMinimum(), Math.min(inGainSlider.getMaximum(), sliderVal));
						inGainSlider.setValue(sliderVal);
						gCB.setinGain((double) sliderVal / 100.0);
									updateinGainLabel();
								} catch (NumberFormatException ex) {
									updateinGainLabel();
								}
							}
						});
						updateinGainLabel();
			
						Border inGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel inGaininnerPanel = new JPanel();
			
						inGaininnerPanel.setLayout(new BoxLayout(inGaininnerPanel, BoxLayout.Y_AXIS));
						inGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inGaininnerPanel.add(inGainField);
						inGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inGaininnerPanel.add(inGainSlider);
						inGaininnerPanel.setBorder(inGainborder2);
			
						frame.add(inGaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					strengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (1.0 * 100.0), (int) (gCB.getstrength() * 100.0));
						strengthSlider.addChangeListener(new rms_compressorListener());
						strengthField = new JTextField();
						strengthField.setHorizontalAlignment(JTextField.CENTER);
						Border strengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						strengthField.setBorder(strengthBorder1);
						strengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(strengthField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(strengthSlider.getMinimum(), Math.min(strengthSlider.getMaximum(), sliderVal));
						strengthSlider.setValue(sliderVal);
						gCB.setstrength((double) sliderVal / 100.0);
									updatestrengthLabel();
								} catch (NumberFormatException ex) {
									updatestrengthLabel();
								}
							}
						});
						updatestrengthLabel();
			
						Border strengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel strengthinnerPanel = new JPanel();
			
						strengthinnerPanel.setLayout(new BoxLayout(strengthinnerPanel, BoxLayout.Y_AXIS));
						strengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						strengthinnerPanel.add(strengthField);
						strengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						strengthinnerPanel.add(strengthSlider);
						strengthinnerPanel.setBorder(strengthborder2);
			
						frame.add(strengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					threshDbSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-50.0 * 10.0),(int) (-5.0 * 10.0), (int) (gCB.getthreshDb() * 10.0));
						threshDbSlider.addChangeListener(new rms_compressorListener());
						threshDbField = new JTextField();
						threshDbField.setHorizontalAlignment(JTextField.CENTER);
						Border threshDbBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						threshDbField.setBorder(threshDbBorder1);
						threshDbField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(threshDbField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(threshDbSlider.getMinimum(), Math.min(threshDbSlider.getMaximum(), sliderVal));
						threshDbSlider.setValue(sliderVal);
						gCB.setthreshDb((double) sliderVal / 10.0);
									updatethreshDbLabel();
								} catch (NumberFormatException ex) {
									updatethreshDbLabel();
								}
							}
						});
						updatethreshDbLabel();
			
						Border threshDbborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel threshDbinnerPanel = new JPanel();
			
						threshDbinnerPanel.setLayout(new BoxLayout(threshDbinnerPanel, BoxLayout.Y_AXIS));
						threshDbinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						threshDbinnerPanel.add(threshDbField);
						threshDbinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						threshDbinnerPanel.add(threshDbSlider);
						threshDbinnerPanel.setBorder(threshDbborder2);
			
						frame.add(threshDbinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					attTimeSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1 * 10.0),(int) (50 * 10.0), (int) (SpinCADBlock.filtToTime(gCB.getattTime()) * 1000 * 10.0));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						attTimeSlider.addChangeListener(new rms_compressorListener());
						attTimeField = new JTextField();
						attTimeField.setHorizontalAlignment(JTextField.CENTER);
						Border attTimeBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						attTimeField.setBorder(attTimeBorder1);
						attTimeField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(attTimeField.getText().replaceAll("[^0-9.\\-]", ""));
						double filt = SpinCADBlock.timeToFilt(val / 1000.0);
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(attTimeSlider.getMinimum(), Math.min(attTimeSlider.getMaximum(), sliderVal));
						attTimeSlider.setValue(sliderVal);
						gCB.setattTime(filt);
									updateattTimeLabel();
								} catch (NumberFormatException ex) {
									updateattTimeLabel();
								}
							}
						});
						updateattTimeLabel();
			
						Border attTimeborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel attTimeinnerPanel = new JPanel();
			
						attTimeinnerPanel.setLayout(new BoxLayout(attTimeinnerPanel, BoxLayout.Y_AXIS));
						attTimeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						attTimeinnerPanel.add(attTimeField);
						attTimeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						attTimeinnerPanel.add(attTimeSlider);
						attTimeinnerPanel.setBorder(attTimeborder2);
			
						frame.add(attTimeinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					relTimeSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(50 * 10.0),(int) (500 * 10.0), (int) (SpinCADBlock.filtToTime(gCB.getrelTime()) * 1000 * 10.0));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						relTimeSlider.addChangeListener(new rms_compressorListener());
						relTimeField = new JTextField();
						relTimeField.setHorizontalAlignment(JTextField.CENTER);
						Border relTimeBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						relTimeField.setBorder(relTimeBorder1);
						relTimeField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(relTimeField.getText().replaceAll("[^0-9.\\-]", ""));
						double filt = SpinCADBlock.timeToFilt(val / 1000.0);
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(relTimeSlider.getMinimum(), Math.min(relTimeSlider.getMaximum(), sliderVal));
						relTimeSlider.setValue(sliderVal);
						gCB.setrelTime(filt);
									updaterelTimeLabel();
								} catch (NumberFormatException ex) {
									updaterelTimeLabel();
								}
							}
						});
						updaterelTimeLabel();
			
						Border relTimeborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel relTimeinnerPanel = new JPanel();
			
						relTimeinnerPanel.setLayout(new BoxLayout(relTimeinnerPanel, BoxLayout.Y_AXIS));
						relTimeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						relTimeinnerPanel.add(relTimeField);
						relTimeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						relTimeinnerPanel.add(relTimeSlider);
						relTimeinnerPanel.setBorder(relTimeborder2);
			
						frame.add(relTimeinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					makeupDbSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 10.0),(int) (30.0 * 10.0), (int) (gCB.getmakeupDb() * 10.0));
						makeupDbSlider.addChangeListener(new rms_compressorListener());
						makeupDbField = new JTextField();
						makeupDbField.setHorizontalAlignment(JTextField.CENTER);
						Border makeupDbBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						makeupDbField.setBorder(makeupDbBorder1);
						makeupDbField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(makeupDbField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(makeupDbSlider.getMinimum(), Math.min(makeupDbSlider.getMaximum(), sliderVal));
						makeupDbSlider.setValue(sliderVal);
						gCB.setmakeupDb((double) sliderVal / 10.0);
									updatemakeupDbLabel();
								} catch (NumberFormatException ex) {
									updatemakeupDbLabel();
								}
							}
						});
						updatemakeupDbLabel();
			
						Border makeupDbborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel makeupDbinnerPanel = new JPanel();
			
						makeupDbinnerPanel.setLayout(new BoxLayout(makeupDbinnerPanel, BoxLayout.Y_AXIS));
						makeupDbinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						makeupDbinnerPanel.add(makeupDbField);
						makeupDbinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						makeupDbinnerPanel.add(makeupDbSlider);
						makeupDbinnerPanel.setBorder(makeupDbborder2);
			
						frame.add(makeupDbinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					trimSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.25 * 100.0),(int) (2.0 * 100.0), (int) (gCB.gettrim() * 100.0));
						trimSlider.addChangeListener(new rms_compressorListener());
						trimField = new JTextField();
						trimField.setHorizontalAlignment(JTextField.CENTER);
						Border trimBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						trimField.setBorder(trimBorder1);
						trimField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(trimField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(trimSlider.getMinimum(), Math.min(trimSlider.getMaximum(), sliderVal));
						trimSlider.setValue(sliderVal);
						gCB.settrim((double) sliderVal / 100.0);
									updatetrimLabel();
								} catch (NumberFormatException ex) {
									updatetrimLabel();
								}
							}
						});
						updatetrimLabel();
			
						Border trimborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel triminnerPanel = new JPanel();
			
						triminnerPanel.setLayout(new BoxLayout(triminnerPanel, BoxLayout.Y_AXIS));
						triminnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						triminnerPanel.add(trimField);
						triminnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						triminnerPanel.add(trimSlider);
						triminnerPanel.setBorder(trimborder2);
			
						frame.add(triminnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class rms_compressorListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inGainSlider) {
			gCB.setinGain((double) (inGainSlider.getValue()/100.0));
				updateinGainLabel();
			}
			if(ce.getSource() == strengthSlider) {
			gCB.setstrength((double) (strengthSlider.getValue()/100.0));
				updatestrengthLabel();
			}
			if(ce.getSource() == threshDbSlider) {
			gCB.setthreshDb((double) (threshDbSlider.getValue()/10.0));
				updatethreshDbLabel();
			}
			if(ce.getSource() == attTimeSlider) {
			gCB.setattTime((double) SpinCADBlock.timeToFilt(attTimeSlider.getValue()/10.0/1000.0));
				updateattTimeLabel();
			}
			if(ce.getSource() == relTimeSlider) {
			gCB.setrelTime((double) SpinCADBlock.timeToFilt(relTimeSlider.getValue()/10.0/1000.0));
				updaterelTimeLabel();
			}
			if(ce.getSource() == makeupDbSlider) {
			gCB.setmakeupDb((double) (makeupDbSlider.getValue()/10.0));
				updatemakeupDbLabel();
			}
			if(ce.getSource() == trimSlider) {
			gCB.settrim((double) (trimSlider.getValue()/100.0));
				updatetrimLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class rms_compressorItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class rms_compressorActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinGainLabel() {
		inGainField.setText("Input_Gain " + String.format("%4.2f", gCB.getinGain()));		
		}		
		private void updatestrengthLabel() {
		strengthField.setText("Strength " + String.format("%4.2f", gCB.getstrength()));		
		}		
		private void updatethreshDbLabel() {
		threshDbField.setText("Threshold (dB) " + String.format("%4.1f", gCB.getthreshDb()));		
		}		
		private void updateattTimeLabel() {
		attTimeField.setText("Attack " + String.format("%4.1f", SpinCADBlock.filtToTime(gCB.getattTime()) * 1000) + " ms");		
		}		
		private void updaterelTimeLabel() {
		relTimeField.setText("Release " + String.format("%4.1f", SpinCADBlock.filtToTime(gCB.getrelTime()) * 1000) + " ms");		
		}		
		private void updatemakeupDbLabel() {
		makeupDbField.setText("Makeup (dB) " + String.format("%4.1f", gCB.getmakeupDb()));		
		}		
		private void updatetrimLabel() {
		trimField.setText("Output_Trim " + String.format("%4.2f", gCB.gettrim()));		
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
