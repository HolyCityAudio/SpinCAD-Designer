/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * var_slope_compressorControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.var_slope_compressorCADBlock;

@SuppressWarnings("unused")
public class var_slope_compressorControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private var_slope_compressorCADBlock gCB;
	// declare the controls
	FineControlSlider inGainSlider;
	JTextField  inGainField;
	FineControlSlider avgTimeSlider;
	JTextField  avgTimeField;
	FineControlSlider kneeSlider;
	JTextField  kneeField;
	FineControlSlider ratioSlider;
	JTextField  ratioField;
	FineControlSlider threshSlider;
	JTextField  threshField;
	FineControlSlider makeupGainSlider;
	JTextField  makeupGainField;

public var_slope_compressorControlPanel(var_slope_compressorCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Var_Slope_Compressor");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					inGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.1 * 100.0),(int) (2.0 * 100.0), (int) (gCB.getinGain() * 100.0));
						inGainSlider.addChangeListener(new var_slope_compressorListener());
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
					avgTimeSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1 * 10.0),(int) (100 * 10.0), (int) (SpinCADBlock.filtToTime(gCB.getavgTime()) * 1000 * 10.0));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						avgTimeSlider.addChangeListener(new var_slope_compressorListener());
						avgTimeField = new JTextField();
						avgTimeField.setHorizontalAlignment(JTextField.CENTER);
						Border avgTimeBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						avgTimeField.setBorder(avgTimeBorder1);
						avgTimeField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(avgTimeField.getText().replaceAll("[^0-9.\\-]", ""));
						double filt = SpinCADBlock.timeToFilt(val / 1000.0);
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(avgTimeSlider.getMinimum(), Math.min(avgTimeSlider.getMaximum(), sliderVal));
						avgTimeSlider.setValue(sliderVal);
						gCB.setavgTime(filt);
									updateavgTimeLabel();
								} catch (NumberFormatException ex) {
									updateavgTimeLabel();
								}
							}
						});
						updateavgTimeLabel();
			
						Border avgTimeborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel avgTimeinnerPanel = new JPanel();
			
						avgTimeinnerPanel.setLayout(new BoxLayout(avgTimeinnerPanel, BoxLayout.Y_AXIS));
						avgTimeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						avgTimeinnerPanel.add(avgTimeField);
						avgTimeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						avgTimeinnerPanel.add(avgTimeSlider);
						avgTimeinnerPanel.setBorder(avgTimeborder2);
			
						frame.add(avgTimeinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kneeSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (0.25 * 1000.0), (int) (gCB.getknee() * 1000.0));
						kneeSlider.addChangeListener(new var_slope_compressorListener());
						kneeField = new JTextField();
						kneeField.setHorizontalAlignment(JTextField.CENTER);
						Border kneeBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kneeField.setBorder(kneeBorder1);
						kneeField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kneeField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(kneeSlider.getMinimum(), Math.min(kneeSlider.getMaximum(), sliderVal));
						kneeSlider.setValue(sliderVal);
						gCB.setknee((double) sliderVal / 1000.0);
									updatekneeLabel();
								} catch (NumberFormatException ex) {
									updatekneeLabel();
								}
							}
						});
						updatekneeLabel();
			
						Border kneeborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kneeinnerPanel = new JPanel();
			
						kneeinnerPanel.setLayout(new BoxLayout(kneeinnerPanel, BoxLayout.Y_AXIS));
						kneeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kneeinnerPanel.add(kneeField);
						kneeinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kneeinnerPanel.add(kneeSlider);
						kneeinnerPanel.setBorder(kneeborder2);
			
						frame.add(kneeinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ratioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1.0 * 10.0),(int) (8.0 * 10.0), (int) (gCB.getratio() * 10.0));
						ratioSlider.addChangeListener(new var_slope_compressorListener());
						ratioField = new JTextField();
						ratioField.setHorizontalAlignment(JTextField.CENTER);
						Border ratioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ratioField.setBorder(ratioBorder1);
						ratioField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ratioField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(ratioSlider.getMinimum(), Math.min(ratioSlider.getMaximum(), sliderVal));
						ratioSlider.setValue(sliderVal);
						gCB.setratio((double) sliderVal / 10.0);
									updateratioLabel();
								} catch (NumberFormatException ex) {
									updateratioLabel();
								}
							}
						});
						updateratioLabel();
			
						Border ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ratioinnerPanel = new JPanel();
			
						ratioinnerPanel.setLayout(new BoxLayout(ratioinnerPanel, BoxLayout.Y_AXIS));
						ratioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ratioinnerPanel.add(ratioField);
						ratioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ratioinnerPanel.add(ratioSlider);
						ratioinnerPanel.setBorder(ratioborder2);
			
						frame.add(ratioinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					threshSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.05 * 1000.0),(int) (0.5 * 1000.0), (int) (gCB.getthresh() * 1000.0));
						threshSlider.addChangeListener(new var_slope_compressorListener());
						threshField = new JTextField();
						threshField.setHorizontalAlignment(JTextField.CENTER);
						Border threshBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						threshField.setBorder(threshBorder1);
						threshField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(threshField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(threshSlider.getMinimum(), Math.min(threshSlider.getMaximum(), sliderVal));
						threshSlider.setValue(sliderVal);
						gCB.setthresh((double) sliderVal / 1000.0);
									updatethreshLabel();
								} catch (NumberFormatException ex) {
									updatethreshLabel();
								}
							}
						});
						updatethreshLabel();
			
						Border threshborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel threshinnerPanel = new JPanel();
			
						threshinnerPanel.setLayout(new BoxLayout(threshinnerPanel, BoxLayout.Y_AXIS));
						threshinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						threshinnerPanel.add(threshField);
						threshinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						threshinnerPanel.add(threshSlider);
						threshinnerPanel.setBorder(threshborder2);
			
						frame.add(threshinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					makeupGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.5 * 100.0),(int) (6.0 * 100.0), (int) (gCB.getmakeupGain() * 100.0));
						makeupGainSlider.addChangeListener(new var_slope_compressorListener());
						makeupGainField = new JTextField();
						makeupGainField.setHorizontalAlignment(JTextField.CENTER);
						Border makeupGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						makeupGainField.setBorder(makeupGainBorder1);
						makeupGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(makeupGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(makeupGainSlider.getMinimum(), Math.min(makeupGainSlider.getMaximum(), sliderVal));
						makeupGainSlider.setValue(sliderVal);
						gCB.setmakeupGain((double) sliderVal / 100.0);
									updatemakeupGainLabel();
								} catch (NumberFormatException ex) {
									updatemakeupGainLabel();
								}
							}
						});
						updatemakeupGainLabel();
			
						Border makeupGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel makeupGaininnerPanel = new JPanel();
			
						makeupGaininnerPanel.setLayout(new BoxLayout(makeupGaininnerPanel, BoxLayout.Y_AXIS));
						makeupGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						makeupGaininnerPanel.add(makeupGainField);
						makeupGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						makeupGaininnerPanel.add(makeupGainSlider);
						makeupGaininnerPanel.setBorder(makeupGainborder2);
			
						frame.add(makeupGaininnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class var_slope_compressorListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inGainSlider) {
			gCB.setinGain((double) (inGainSlider.getValue()/100.0));
				updateinGainLabel();
			}
			if(ce.getSource() == avgTimeSlider) {
			gCB.setavgTime((double) SpinCADBlock.timeToFilt(avgTimeSlider.getValue()/10.0/1000.0));
				updateavgTimeLabel();
			}
			if(ce.getSource() == kneeSlider) {
			gCB.setknee((double) (kneeSlider.getValue()/1000.0));
				updatekneeLabel();
			}
			if(ce.getSource() == ratioSlider) {
			gCB.setratio((double) (ratioSlider.getValue()/10.0));
				updateratioLabel();
			}
			if(ce.getSource() == threshSlider) {
			gCB.setthresh((double) (threshSlider.getValue()/1000.0));
				updatethreshLabel();
			}
			if(ce.getSource() == makeupGainSlider) {
			gCB.setmakeupGain((double) (makeupGainSlider.getValue()/100.0));
				updatemakeupGainLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class var_slope_compressorItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class var_slope_compressorActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinGainLabel() {
		inGainField.setText("Input_Gain " + String.format("%4.2f", gCB.getinGain()));		
		}		
		private void updateavgTimeLabel() {
		avgTimeField.setText("Attack " + String.format("%4.1f", SpinCADBlock.filtToTime(gCB.getavgTime()) * 1000) + " ms");		
		}		
		private void updatekneeLabel() {
		kneeField.setText("Knee " + String.format("%4.4f", gCB.getknee()));		
		}		
		private void updateratioLabel() {
		ratioField.setText("Ratio " + String.format("%4.1f", gCB.getratio()));		
		}		
		private void updatethreshLabel() {
		threshField.setText("Threshold " + String.format("%4.3f", gCB.getthresh()));		
		}		
		private void updatemakeupGainLabel() {
		makeupGainField.setText("Makeup_Gain " + String.format("%4.2f", gCB.getmakeupGain()));		
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
