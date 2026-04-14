/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * sixtapControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.sixtapCADBlock;

@SuppressWarnings("unused")
public class sixtapControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private sixtapCADBlock gCB;
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
	FineControlSlider tap5RatioSlider;
	JTextField  tap5RatioField;
	FineControlSlider tap6RatioSlider;
	JTextField  tap6RatioField;
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

public sixtapControlPanel(sixtapCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Six Tap");
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
						inputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getinputGain()) * 10.0));
						inputGainSlider.addChangeListener(new sixtapListener());
						inputGainField = new JTextField();
						inputGainField.setHorizontalAlignment(JTextField.CENTER);
						Border inputGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputGainField.setBorder(inputGainBorder1);
						inputGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(inputGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(inputGainSlider.getMinimum(), Math.min(inputGainSlider.getMaximum(), sliderVal));
						inputGainSlider.setValue(sliderVal);
						gCB.setinputGain((double) sliderVal / 10.0);
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
						fbkGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getfbkGain()) * 10.0));
						fbkGainSlider.addChangeListener(new sixtapListener());
						fbkGainField = new JTextField();
						fbkGainField.setHorizontalAlignment(JTextField.CENTER);
						Border fbkGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						fbkGainField.setBorder(fbkGainBorder1);
						fbkGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(fbkGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(fbkGainSlider.getMinimum(), Math.min(fbkGainSlider.getMaximum(), sliderVal));
						fbkGainSlider.setValue(sliderVal);
						gCB.setfbkGain((double) sliderVal / 10.0);
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
						delayLengthSlider.addChangeListener(new sixtapListener());
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
						tap1RatioSlider.addChangeListener(new sixtapListener());
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
						tap2RatioSlider.addChangeListener(new sixtapListener());
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
						tap3RatioSlider.addChangeListener(new sixtapListener());
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
						tap4RatioSlider.addChangeListener(new sixtapListener());
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
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap5RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap5Ratio() * 1000.0));
						tap5RatioSlider.addChangeListener(new sixtapListener());
						tap5RatioField = new JTextField();
						tap5RatioField.setHorizontalAlignment(JTextField.CENTER);
						Border tap5RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap5RatioField.setBorder(tap5RatioBorder1);
						tap5RatioField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap5RatioField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap5RatioSlider.getMinimum(), Math.min(tap5RatioSlider.getMaximum(), sliderVal));
						tap5RatioSlider.setValue(sliderVal);
						gCB.settap5Ratio((double) sliderVal / 1000.0);
									updatetap5RatioLabel();
								} catch (NumberFormatException ex) {
									updatetap5RatioLabel();
								}
							}
						});
						updatetap5RatioLabel();
			
						Border tap5Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap5RatioinnerPanel = new JPanel();
			
						tap5RatioinnerPanel.setLayout(new BoxLayout(tap5RatioinnerPanel, BoxLayout.Y_AXIS));
						tap5RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap5RatioinnerPanel.add(tap5RatioField);
						tap5RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap5RatioinnerPanel.add(tap5RatioSlider);
						tap5RatioinnerPanel.setBorder(tap5Ratioborder2);
			
						frame.add(tap5RatioinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap6RatioSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap6Ratio() * 1000.0));
						tap6RatioSlider.addChangeListener(new sixtapListener());
						tap6RatioField = new JTextField();
						tap6RatioField.setHorizontalAlignment(JTextField.CENTER);
						Border tap6RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap6RatioField.setBorder(tap6RatioBorder1);
						tap6RatioField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap6RatioField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap6RatioSlider.getMinimum(), Math.min(tap6RatioSlider.getMaximum(), sliderVal));
						tap6RatioSlider.setValue(sliderVal);
						gCB.settap6Ratio((double) sliderVal / 1000.0);
									updatetap6RatioLabel();
								} catch (NumberFormatException ex) {
									updatetap6RatioLabel();
								}
							}
						});
						updatetap6RatioLabel();
			
						Border tap6Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap6RatioinnerPanel = new JPanel();
			
						tap6RatioinnerPanel.setLayout(new BoxLayout(tap6RatioinnerPanel, BoxLayout.Y_AXIS));
						tap6RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap6RatioinnerPanel.add(tap6RatioField);
						tap6RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap6RatioinnerPanel.add(tap6RatioSlider);
						tap6RatioinnerPanel.setBorder(tap6Ratioborder2);
			
						frame.add(tap6RatioinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						tap1GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.gettap1Gain()) * 10.0));
						tap1GainSlider.addChangeListener(new sixtapListener());
						tap1GainField = new JTextField();
						tap1GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap1GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap1GainField.setBorder(tap1GainBorder1);
						tap1GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap1GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(tap1GainSlider.getMinimum(), Math.min(tap1GainSlider.getMaximum(), sliderVal));
						tap1GainSlider.setValue(sliderVal);
						gCB.settap1Gain((double) sliderVal / 10.0);
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
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						tap2GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.gettap2Gain()) * 10.0));
						tap2GainSlider.addChangeListener(new sixtapListener());
						tap2GainField = new JTextField();
						tap2GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap2GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap2GainField.setBorder(tap2GainBorder1);
						tap2GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap2GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(tap2GainSlider.getMinimum(), Math.min(tap2GainSlider.getMaximum(), sliderVal));
						tap2GainSlider.setValue(sliderVal);
						gCB.settap2Gain((double) sliderVal / 10.0);
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
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						tap3GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.gettap3Gain()) * 10.0));
						tap3GainSlider.addChangeListener(new sixtapListener());
						tap3GainField = new JTextField();
						tap3GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap3GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap3GainField.setBorder(tap3GainBorder1);
						tap3GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap3GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(tap3GainSlider.getMinimum(), Math.min(tap3GainSlider.getMaximum(), sliderVal));
						tap3GainSlider.setValue(sliderVal);
						gCB.settap3Gain((double) sliderVal / 10.0);
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
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						tap4GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.gettap4Gain()) * 10.0));
						tap4GainSlider.addChangeListener(new sixtapListener());
						tap4GainField = new JTextField();
						tap4GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap4GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap4GainField.setBorder(tap4GainBorder1);
						tap4GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap4GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(tap4GainSlider.getMinimum(), Math.min(tap4GainSlider.getMaximum(), sliderVal));
						tap4GainSlider.setValue(sliderVal);
						gCB.settap4Gain((double) sliderVal / 10.0);
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
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						tap5GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.gettap5Gain()) * 10.0));
						tap5GainSlider.addChangeListener(new sixtapListener());
						tap5GainField = new JTextField();
						tap5GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap5GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap5GainField.setBorder(tap5GainBorder1);
						tap5GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap5GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(tap5GainSlider.getMinimum(), Math.min(tap5GainSlider.getMaximum(), sliderVal));
						tap5GainSlider.setValue(sliderVal);
						gCB.settap5Gain((double) sliderVal / 10.0);
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
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						tap6GainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.gettap6Gain()) * 10.0));
						tap6GainSlider.addChangeListener(new sixtapListener());
						tap6GainField = new JTextField();
						tap6GainField.setHorizontalAlignment(JTextField.CENTER);
						Border tap6GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap6GainField.setBorder(tap6GainBorder1);
						tap6GainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap6GainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(tap6GainSlider.getMinimum(), Math.min(tap6GainSlider.getMaximum(), sliderVal));
						tap6GainSlider.setValue(sliderVal);
						gCB.settap6Gain((double) sliderVal / 10.0);
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
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class sixtapListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/10.0));			    					
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/10.0));			    					
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
			if(ce.getSource() == tap5RatioSlider) {
			gCB.settap5Ratio((double) (tap5RatioSlider.getValue()/1000.0));
				updatetap5RatioLabel();
			}
			if(ce.getSource() == tap6RatioSlider) {
			gCB.settap6Ratio((double) (tap6RatioSlider.getValue()/1000.0));
				updatetap6RatioLabel();
			}
			if(ce.getSource() == tap1GainSlider) {
			gCB.settap1Gain((double) (tap1GainSlider.getValue()/10.0));			    					
				updatetap1GainLabel();
			}
			if(ce.getSource() == tap2GainSlider) {
			gCB.settap2Gain((double) (tap2GainSlider.getValue()/10.0));			    					
				updatetap2GainLabel();
			}
			if(ce.getSource() == tap3GainSlider) {
			gCB.settap3Gain((double) (tap3GainSlider.getValue()/10.0));			    					
				updatetap3GainLabel();
			}
			if(ce.getSource() == tap4GainSlider) {
			gCB.settap4Gain((double) (tap4GainSlider.getValue()/10.0));			    					
				updatetap4GainLabel();
			}
			if(ce.getSource() == tap5GainSlider) {
			gCB.settap5Gain((double) (tap5GainSlider.getValue()/10.0));			    					
				updatetap5GainLabel();
			}
			if(ce.getSource() == tap6GainSlider) {
			gCB.settap6Gain((double) (tap6GainSlider.getValue()/10.0));			    					
				updatetap6GainLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class sixtapItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class sixtapActionListener implements java.awt.event.ActionListener {
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
		private void updatetap1RatioLabel() {
		tap1RatioField.setText("Tap 1 Time " + String.format("%4.2f", gCB.gettap1Ratio()));
		}
		private void updatetap2RatioLabel() {
		tap2RatioField.setText("Tap 2 Time " + String.format("%4.2f", gCB.gettap2Ratio()));
		}
		private void updatetap3RatioLabel() {
		tap3RatioField.setText("Tap 3 Time " + String.format("%4.2f", gCB.gettap3Ratio()));
		}
		private void updatetap4RatioLabel() {
		tap4RatioField.setText("Tap 4 Time " + String.format("%4.2f", gCB.gettap4Ratio()));
		}
		private void updatetap5RatioLabel() {
		tap5RatioField.setText("Tap 5 Time " + String.format("%4.2f", gCB.gettap5Ratio()));
		}
		private void updatetap6RatioLabel() {
		tap6RatioField.setText("Tap_6_Time " + String.format("%4.2f", gCB.gettap6Ratio()));
		}
		private void updatetap1GainLabel() {
		tap1GainField.setText("Tap 1 Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap1Gain()))));
		}
		private void updatetap2GainLabel() {
		tap2GainField.setText("Tap 2 Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap2Gain()))));
		}
		private void updatetap3GainLabel() {
		tap3GainField.setText("Tap 3 Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap3Gain()))));
		}
		private void updatetap4GainLabel() {
		tap4GainField.setText("Tap 4 Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap4Gain()))));
		}
		private void updatetap5GainLabel() {
		tap5GainField.setText("Tap 5 Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap5Gain()))));
		}
		private void updatetap6GainLabel() {
		tap6GainField.setText("Tap 6 Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap6Gain()))));
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
