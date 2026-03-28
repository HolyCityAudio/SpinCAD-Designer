/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * eighttapControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.eighttapCADBlock;

@SuppressWarnings("unused")
public class eighttapControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private eighttapCADBlock gCB;
	// declare the controls
	FineControlSlider inputGainSlider;
	JTextField  inputGainField;
	FineControlSlider fbkGainSlider;
	JTextField  fbkGainField;
	FineControlSlider delayLengthSlider;
	JTextField  delayLengthField;
	FineControlSlider tap1GainSlider;
	JTextField  tap1GainField;
	FineControlSlider tap2GainSlider;
	JTextField  tap2GainField;
	FineControlSlider tap3GainSlider;
	JTextField  tap3GainField;
	FineControlSlider tap4GainSlider;
	JTextField  tap4GainField;
	FineControlSlider tap5GainSlider;
	JTextField  tap5GainField;
	FineControlSlider tap6GainSlider;
	JTextField  tap6GainField;
	FineControlSlider tap7GainSlider;
	JTextField  tap7GainField;
	FineControlSlider tap8GainSlider;
	JTextField  tap8GainField;

public eighttapControlPanel(eighttapCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Eight Tap");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						inputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getinputGain())));
						inputGainSlider.addChangeListener(new eighttapListener());
						inputGainField = new JTextField();
						inputGainField.setHorizontalAlignment(JTextField.CENTER);
						Border inputGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputGainField.setBorder(inputGainBorder1);
						inputGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(inputGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(inputGainSlider.getMinimum(), Math.min(inputGainSlider.getMaximum(), sliderVal));
						inputGainSlider.setValue(sliderVal);
						gCB.setinputGain((double) sliderVal);
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
					// dB level slider goes in steps of 1 dB
						fbkGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getfbkGain())));
						fbkGainSlider.addChangeListener(new eighttapListener());
						fbkGainField = new JTextField();
						fbkGainField.setHorizontalAlignment(JTextField.CENTER);
						Border fbkGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						fbkGainField.setBorder(fbkGainBorder1);
						fbkGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(fbkGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(fbkGainSlider.getMinimum(), Math.min(fbkGainSlider.getMaximum(), sliderVal));
						fbkGainSlider.setValue(sliderVal);
						gCB.setfbkGain((double) sliderVal);
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
						delayLengthSlider.addChangeListener(new eighttapListener());
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
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap1GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap1Gain())));
						tap1GainSlider.addChangeListener(new eighttapListener());
						tap1GainField = new JTextField();
						tap1GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap1GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap1GainField.setBorder(tap1GainBorder1);
						tap1GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap1GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap1GainSlider.getMinimum(), Math.min(tap1GainSlider.getMaximum(), sliderVal));
						tap1GainSlider.setValue(sliderVal);
						gCB.settap1Gain((double) sliderVal);
									updatetap1GainLabel();
								} catch (NumberFormatException ex) {
									updatetap1GainLabel();
								}
							}
						});
						updatetap1GainLabel();
			
						Border tap1Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap1GaininnerPanel = new JPanel();
			
						tap1GaininnerPanel.setLayout(new BoxLayout(tap1GaininnerPanel, BoxLayout.Y_AXIS));
						tap1GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap1GaininnerPanel.add(tap1GainField);
						tap1GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap1GaininnerPanel.add(tap1GainSlider);
						tap1GaininnerPanel.setBorder(tap1Gainborder2);
			
						frame.add(tap1GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap2GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap2Gain())));
						tap2GainSlider.addChangeListener(new eighttapListener());
						tap2GainField = new JTextField();
						tap2GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap2GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap2GainField.setBorder(tap2GainBorder1);
						tap2GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap2GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap2GainSlider.getMinimum(), Math.min(tap2GainSlider.getMaximum(), sliderVal));
						tap2GainSlider.setValue(sliderVal);
						gCB.settap2Gain((double) sliderVal);
									updatetap2GainLabel();
								} catch (NumberFormatException ex) {
									updatetap2GainLabel();
								}
							}
						});
						updatetap2GainLabel();
			
						Border tap2Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap2GaininnerPanel = new JPanel();
			
						tap2GaininnerPanel.setLayout(new BoxLayout(tap2GaininnerPanel, BoxLayout.Y_AXIS));
						tap2GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap2GaininnerPanel.add(tap2GainField);
						tap2GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap2GaininnerPanel.add(tap2GainSlider);
						tap2GaininnerPanel.setBorder(tap2Gainborder2);
			
						frame.add(tap2GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap3GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap3Gain())));
						tap3GainSlider.addChangeListener(new eighttapListener());
						tap3GainField = new JTextField();
						tap3GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap3GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap3GainField.setBorder(tap3GainBorder1);
						tap3GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap3GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap3GainSlider.getMinimum(), Math.min(tap3GainSlider.getMaximum(), sliderVal));
						tap3GainSlider.setValue(sliderVal);
						gCB.settap3Gain((double) sliderVal);
									updatetap3GainLabel();
								} catch (NumberFormatException ex) {
									updatetap3GainLabel();
								}
							}
						});
						updatetap3GainLabel();
			
						Border tap3Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap3GaininnerPanel = new JPanel();
			
						tap3GaininnerPanel.setLayout(new BoxLayout(tap3GaininnerPanel, BoxLayout.Y_AXIS));
						tap3GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap3GaininnerPanel.add(tap3GainField);
						tap3GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap3GaininnerPanel.add(tap3GainSlider);
						tap3GaininnerPanel.setBorder(tap3Gainborder2);
			
						frame.add(tap3GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap4GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap4Gain())));
						tap4GainSlider.addChangeListener(new eighttapListener());
						tap4GainField = new JTextField();
						tap4GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap4GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap4GainField.setBorder(tap4GainBorder1);
						tap4GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap4GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap4GainSlider.getMinimum(), Math.min(tap4GainSlider.getMaximum(), sliderVal));
						tap4GainSlider.setValue(sliderVal);
						gCB.settap4Gain((double) sliderVal);
									updatetap4GainLabel();
								} catch (NumberFormatException ex) {
									updatetap4GainLabel();
								}
							}
						});
						updatetap4GainLabel();
			
						Border tap4Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap4GaininnerPanel = new JPanel();
			
						tap4GaininnerPanel.setLayout(new BoxLayout(tap4GaininnerPanel, BoxLayout.Y_AXIS));
						tap4GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4GaininnerPanel.add(tap4GainField);
						tap4GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4GaininnerPanel.add(tap4GainSlider);
						tap4GaininnerPanel.setBorder(tap4Gainborder2);
			
						frame.add(tap4GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap5GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap5Gain())));
						tap5GainSlider.addChangeListener(new eighttapListener());
						tap5GainField = new JTextField();
						tap5GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap5GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap5GainField.setBorder(tap5GainBorder1);
						tap5GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap5GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap5GainSlider.getMinimum(), Math.min(tap5GainSlider.getMaximum(), sliderVal));
						tap5GainSlider.setValue(sliderVal);
						gCB.settap5Gain((double) sliderVal);
									updatetap5GainLabel();
								} catch (NumberFormatException ex) {
									updatetap5GainLabel();
								}
							}
						});
						updatetap5GainLabel();
			
						Border tap5Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap5GaininnerPanel = new JPanel();
			
						tap5GaininnerPanel.setLayout(new BoxLayout(tap5GaininnerPanel, BoxLayout.Y_AXIS));
						tap5GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap5GaininnerPanel.add(tap5GainField);
						tap5GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap5GaininnerPanel.add(tap5GainSlider);
						tap5GaininnerPanel.setBorder(tap5Gainborder2);
			
						frame.add(tap5GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap6GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap6Gain())));
						tap6GainSlider.addChangeListener(new eighttapListener());
						tap6GainField = new JTextField();
						tap6GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap6GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap6GainField.setBorder(tap6GainBorder1);
						tap6GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap6GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap6GainSlider.getMinimum(), Math.min(tap6GainSlider.getMaximum(), sliderVal));
						tap6GainSlider.setValue(sliderVal);
						gCB.settap6Gain((double) sliderVal);
									updatetap6GainLabel();
								} catch (NumberFormatException ex) {
									updatetap6GainLabel();
								}
							}
						});
						updatetap6GainLabel();
			
						Border tap6Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap6GaininnerPanel = new JPanel();
			
						tap6GaininnerPanel.setLayout(new BoxLayout(tap6GaininnerPanel, BoxLayout.Y_AXIS));
						tap6GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap6GaininnerPanel.add(tap6GainField);
						tap6GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap6GaininnerPanel.add(tap6GainSlider);
						tap6GaininnerPanel.setBorder(tap6Gainborder2);
			
						frame.add(tap6GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap7GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap7Gain())));
						tap7GainSlider.addChangeListener(new eighttapListener());
						tap7GainField = new JTextField();
						tap7GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap7GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap7GainField.setBorder(tap7GainBorder1);
						tap7GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap7GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap7GainSlider.getMinimum(), Math.min(tap7GainSlider.getMaximum(), sliderVal));
						tap7GainSlider.setValue(sliderVal);
						gCB.settap7Gain((double) sliderVal);
									updatetap7GainLabel();
								} catch (NumberFormatException ex) {
									updatetap7GainLabel();
								}
							}
						});
						updatetap7GainLabel();
			
						Border tap7Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap7GaininnerPanel = new JPanel();
			
						tap7GaininnerPanel.setLayout(new BoxLayout(tap7GaininnerPanel, BoxLayout.Y_AXIS));
						tap7GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap7GaininnerPanel.add(tap7GainField);
						tap7GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap7GaininnerPanel.add(tap7GainSlider);
						tap7GaininnerPanel.setBorder(tap7Gainborder2);
			
						frame.add(tap7GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap8GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap8Gain())));
						tap8GainSlider.addChangeListener(new eighttapListener());
						tap8GainField = new JTextField();
						tap8GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap8GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap8GainField.setBorder(tap8GainBorder1);
						tap8GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap8GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(tap8GainSlider.getMinimum(), Math.min(tap8GainSlider.getMaximum(), sliderVal));
						tap8GainSlider.setValue(sliderVal);
						gCB.settap8Gain((double) sliderVal);
									updatetap8GainLabel();
								} catch (NumberFormatException ex) {
									updatetap8GainLabel();
								}
							}
						});
						updatetap8GainLabel();
			
						Border tap8Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap8GaininnerPanel = new JPanel();
			
						tap8GaininnerPanel.setLayout(new BoxLayout(tap8GaininnerPanel, BoxLayout.Y_AXIS));
						tap8GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap8GaininnerPanel.add(tap8GainField);
						tap8GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap8GaininnerPanel.add(tap8GainSlider);
						tap8GaininnerPanel.setBorder(tap8Gainborder2);
			
						frame.add(tap8GaininnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class eighttapListener implements ChangeListener { 
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
			if(ce.getSource() == tap1GainSlider) {
			gCB.settap1Gain((double) (tap1GainSlider.getValue()/1.0));			    					
				updatetap1GainLabel();
			}
			if(ce.getSource() == tap2GainSlider) {
			gCB.settap2Gain((double) (tap2GainSlider.getValue()/1.0));			    					
				updatetap2GainLabel();
			}
			if(ce.getSource() == tap3GainSlider) {
			gCB.settap3Gain((double) (tap3GainSlider.getValue()/1.0));			    					
				updatetap3GainLabel();
			}
			if(ce.getSource() == tap4GainSlider) {
			gCB.settap4Gain((double) (tap4GainSlider.getValue()/1.0));			    					
				updatetap4GainLabel();
			}
			if(ce.getSource() == tap5GainSlider) {
			gCB.settap5Gain((double) (tap5GainSlider.getValue()/1.0));			    					
				updatetap5GainLabel();
			}
			if(ce.getSource() == tap6GainSlider) {
			gCB.settap6Gain((double) (tap6GainSlider.getValue()/1.0));			    					
				updatetap6GainLabel();
			}
			if(ce.getSource() == tap7GainSlider) {
			gCB.settap7Gain((double) (tap7GainSlider.getValue()/1.0));			    					
				updatetap7GainLabel();
			}
			if(ce.getSource() == tap8GainSlider) {
			gCB.settap8Gain((double) (tap8GainSlider.getValue()/1.0));			    					
				updatetap8GainLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class eighttapItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class eighttapActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainLabel() {
		inputGainField.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));		
		}		
		private void updatefbkGainLabel() {
		fbkGainField.setText("Feedback Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthField.setText("Delay Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));		
		}		
		private void updatetap1GainLabel() {
		tap1GainField.setText("Tap 1 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap1Gain()))));		
		}		
		private void updatetap2GainLabel() {
		tap2GainField.setText("Tap 2 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap2Gain()))));		
		}		
		private void updatetap3GainLabel() {
		tap3GainField.setText("Tap 3 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap3Gain()))));		
		}		
		private void updatetap4GainLabel() {
		tap4GainField.setText("Tap 4 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap4Gain()))));		
		}		
		private void updatetap5GainLabel() {
		tap5GainField.setText("Tap 5 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap5Gain()))));		
		}		
		private void updatetap6GainLabel() {
		tap6GainField.setText("Tap 6 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap6Gain()))));		
		}		
		private void updatetap7GainLabel() {
		tap7GainField.setText("Tap 7 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap7Gain()))));		
		}		
		private void updatetap8GainLabel() {
		tap8GainField.setText("Tap 8 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap8Gain()))));		
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
