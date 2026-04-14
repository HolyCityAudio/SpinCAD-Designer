/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * MinReverb2ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.MinReverb2CADBlock;

@SuppressWarnings("unused")
public class MinReverb2ControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private MinReverb2CADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider kiapSlider;
	JTextField  kiapField;
	FineControlSlider ap1lengthSlider;
	JTextField  ap1lengthField;
	FineControlSlider ap2lengthSlider;
	JTextField  ap2lengthField;
	FineControlSlider ap3lengthSlider;
	JTextField  ap3lengthField;
	FineControlSlider ap4lengthSlider;
	JTextField  ap4lengthField;
	FineControlSlider klapSlider;
	JTextField  klapField;
	FineControlSlider lap1lengthSlider;
	JTextField  lap1lengthField;
	FineControlSlider del1lengthSlider;
	JTextField  del1lengthField;
	FineControlSlider lap2lengthSlider;
	JTextField  lap2lengthField;
	FineControlSlider del2lengthSlider;
	JTextField  del2lengthField;

public MinReverb2ControlPanel(MinReverb2CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Small Reverb");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-18 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getgain()) * 10.0));
						gainSlider.addChangeListener(new MinReverb2Listener());
						gainField = new JTextField();
						gainField.setHorizontalAlignment(JTextField.CENTER);
						Border gainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gainField.setBorder(gainBorder1);
						gainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(gainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(gainSlider.getMinimum(), Math.min(gainSlider.getMaximum(), sliderVal));
						gainSlider.setValue(sliderVal);
						gCB.setgain((double) sliderVal / 10.0);
									updategainLabel();
								} catch (NumberFormatException ex) {
									updategainLabel();
								}
							}
						});
						updategainLabel();
			
						Border gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel gaininnerPanel = new JPanel();
			
						gaininnerPanel.setLayout(new BoxLayout(gaininnerPanel, BoxLayout.Y_AXIS));
						gaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						gaininnerPanel.add(gainField);
						gaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						gaininnerPanel.add(gainSlider);
						gaininnerPanel.setBorder(gainborder2);
			
						frame.add(gaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kiapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-0.90 * 100.0),(int) (0.90 * 100.0), (int) (gCB.getkiap() * 100.0));
						kiapSlider.addChangeListener(new MinReverb2Listener());
						kiapField = new JTextField();
						kiapField.setHorizontalAlignment(JTextField.CENTER);
						Border kiapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kiapField.setBorder(kiapBorder1);
						kiapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kiapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(kiapSlider.getMinimum(), Math.min(kiapSlider.getMaximum(), sliderVal));
						kiapSlider.setValue(sliderVal);
						gCB.setkiap((double) sliderVal / 100.0);
									updatekiapLabel();
								} catch (NumberFormatException ex) {
									updatekiapLabel();
								}
							}
						});
						updatekiapLabel();
			
						Border kiapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kiapinnerPanel = new JPanel();
			
						kiapinnerPanel.setLayout(new BoxLayout(kiapinnerPanel, BoxLayout.Y_AXIS));
						kiapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kiapinnerPanel.add(kiapField);
						kiapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kiapinnerPanel.add(kiapSlider);
						kiapinnerPanel.setBorder(kiapborder2);
			
						frame.add(kiapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap1lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap1length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap1lengthSlider.addChangeListener(new MinReverb2Listener());
						ap1lengthField = new JTextField();
						ap1lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border ap1lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap1lengthField.setBorder(ap1lengthBorder1);
						ap1lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap1lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(ap1lengthSlider.getMinimum(), Math.min(ap1lengthSlider.getMaximum(), sliderVal));
						ap1lengthSlider.setValue(sliderVal);
						gCB.setap1length((double) sliderVal / 1);
									updateap1lengthLabel();
								} catch (NumberFormatException ex) {
									updateap1lengthLabel();
								}
							}
						});
						updateap1lengthLabel();
			
						Border ap1lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap1lengthinnerPanel = new JPanel();
			
						ap1lengthinnerPanel.setLayout(new BoxLayout(ap1lengthinnerPanel, BoxLayout.Y_AXIS));
						ap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap1lengthinnerPanel.add(ap1lengthField);
						ap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap1lengthinnerPanel.add(ap1lengthSlider);
						ap1lengthinnerPanel.setBorder(ap1lengthborder2);
			
						frame.add(ap1lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap2lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap2length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap2lengthSlider.addChangeListener(new MinReverb2Listener());
						ap2lengthField = new JTextField();
						ap2lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border ap2lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap2lengthField.setBorder(ap2lengthBorder1);
						ap2lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap2lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(ap2lengthSlider.getMinimum(), Math.min(ap2lengthSlider.getMaximum(), sliderVal));
						ap2lengthSlider.setValue(sliderVal);
						gCB.setap2length((double) sliderVal / 1);
									updateap2lengthLabel();
								} catch (NumberFormatException ex) {
									updateap2lengthLabel();
								}
							}
						});
						updateap2lengthLabel();
			
						Border ap2lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap2lengthinnerPanel = new JPanel();
			
						ap2lengthinnerPanel.setLayout(new BoxLayout(ap2lengthinnerPanel, BoxLayout.Y_AXIS));
						ap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap2lengthinnerPanel.add(ap2lengthField);
						ap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap2lengthinnerPanel.add(ap2lengthSlider);
						ap2lengthinnerPanel.setBorder(ap2lengthborder2);
			
						frame.add(ap2lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap3lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap3length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap3lengthSlider.addChangeListener(new MinReverb2Listener());
						ap3lengthField = new JTextField();
						ap3lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border ap3lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap3lengthField.setBorder(ap3lengthBorder1);
						ap3lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap3lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(ap3lengthSlider.getMinimum(), Math.min(ap3lengthSlider.getMaximum(), sliderVal));
						ap3lengthSlider.setValue(sliderVal);
						gCB.setap3length((double) sliderVal / 1);
									updateap3lengthLabel();
								} catch (NumberFormatException ex) {
									updateap3lengthLabel();
								}
							}
						});
						updateap3lengthLabel();
			
						Border ap3lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap3lengthinnerPanel = new JPanel();
			
						ap3lengthinnerPanel.setLayout(new BoxLayout(ap3lengthinnerPanel, BoxLayout.Y_AXIS));
						ap3lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap3lengthinnerPanel.add(ap3lengthField);
						ap3lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap3lengthinnerPanel.add(ap3lengthSlider);
						ap3lengthinnerPanel.setBorder(ap3lengthborder2);
			
						frame.add(ap3lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap4lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap4length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap4lengthSlider.addChangeListener(new MinReverb2Listener());
						ap4lengthField = new JTextField();
						ap4lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border ap4lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap4lengthField.setBorder(ap4lengthBorder1);
						ap4lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap4lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(ap4lengthSlider.getMinimum(), Math.min(ap4lengthSlider.getMaximum(), sliderVal));
						ap4lengthSlider.setValue(sliderVal);
						gCB.setap4length((double) sliderVal / 1);
									updateap4lengthLabel();
								} catch (NumberFormatException ex) {
									updateap4lengthLabel();
								}
							}
						});
						updateap4lengthLabel();
			
						Border ap4lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap4lengthinnerPanel = new JPanel();
			
						ap4lengthinnerPanel.setLayout(new BoxLayout(ap4lengthinnerPanel, BoxLayout.Y_AXIS));
						ap4lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap4lengthinnerPanel.add(ap4lengthField);
						ap4lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap4lengthinnerPanel.add(ap4lengthSlider);
						ap4lengthinnerPanel.setBorder(ap4lengthborder2);
			
						frame.add(ap4lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					klapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-0.90 * 100.0),(int) (0.90 * 100.0), (int) (gCB.getklap() * 100.0));
						klapSlider.addChangeListener(new MinReverb2Listener());
						klapField = new JTextField();
						klapField.setHorizontalAlignment(JTextField.CENTER);
						Border klapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						klapField.setBorder(klapBorder1);
						klapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(klapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(klapSlider.getMinimum(), Math.min(klapSlider.getMaximum(), sliderVal));
						klapSlider.setValue(sliderVal);
						gCB.setklap((double) sliderVal / 100.0);
									updateklapLabel();
								} catch (NumberFormatException ex) {
									updateklapLabel();
								}
							}
						});
						updateklapLabel();
			
						Border klapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel klapinnerPanel = new JPanel();
			
						klapinnerPanel.setLayout(new BoxLayout(klapinnerPanel, BoxLayout.Y_AXIS));
						klapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						klapinnerPanel.add(klapField);
						klapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						klapinnerPanel.add(klapSlider);
						klapinnerPanel.setBorder(klapborder2);
			
						frame.add(klapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					lap1lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(3500 * 1),(int) (5000 * 1), (int) (gCB.getlap1length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						lap1lengthSlider.addChangeListener(new MinReverb2Listener());
						lap1lengthField = new JTextField();
						lap1lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border lap1lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						lap1lengthField.setBorder(lap1lengthBorder1);
						lap1lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(lap1lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(lap1lengthSlider.getMinimum(), Math.min(lap1lengthSlider.getMaximum(), sliderVal));
						lap1lengthSlider.setValue(sliderVal);
						gCB.setlap1length((double) sliderVal / 1);
									updatelap1lengthLabel();
								} catch (NumberFormatException ex) {
									updatelap1lengthLabel();
								}
							}
						});
						updatelap1lengthLabel();
			
						Border lap1lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel lap1lengthinnerPanel = new JPanel();
			
						lap1lengthinnerPanel.setLayout(new BoxLayout(lap1lengthinnerPanel, BoxLayout.Y_AXIS));
						lap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						lap1lengthinnerPanel.add(lap1lengthField);
						lap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						lap1lengthinnerPanel.add(lap1lengthSlider);
						lap1lengthinnerPanel.setBorder(lap1lengthborder2);
			
						frame.add(lap1lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					del1lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(5000 * 1),(int) (9000 * 1), (int) (gCB.getdel1length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						del1lengthSlider.addChangeListener(new MinReverb2Listener());
						del1lengthField = new JTextField();
						del1lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border del1lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						del1lengthField.setBorder(del1lengthBorder1);
						del1lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(del1lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(del1lengthSlider.getMinimum(), Math.min(del1lengthSlider.getMaximum(), sliderVal));
						del1lengthSlider.setValue(sliderVal);
						gCB.setdel1length((double) sliderVal / 1);
									updatedel1lengthLabel();
								} catch (NumberFormatException ex) {
									updatedel1lengthLabel();
								}
							}
						});
						updatedel1lengthLabel();
			
						Border del1lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel del1lengthinnerPanel = new JPanel();
			
						del1lengthinnerPanel.setLayout(new BoxLayout(del1lengthinnerPanel, BoxLayout.Y_AXIS));
						del1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						del1lengthinnerPanel.add(del1lengthField);
						del1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						del1lengthinnerPanel.add(del1lengthSlider);
						del1lengthinnerPanel.setBorder(del1lengthborder2);
			
						frame.add(del1lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					lap2lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(3500 * 1),(int) (5000 * 1), (int) (gCB.getlap2length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						lap2lengthSlider.addChangeListener(new MinReverb2Listener());
						lap2lengthField = new JTextField();
						lap2lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border lap2lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						lap2lengthField.setBorder(lap2lengthBorder1);
						lap2lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(lap2lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(lap2lengthSlider.getMinimum(), Math.min(lap2lengthSlider.getMaximum(), sliderVal));
						lap2lengthSlider.setValue(sliderVal);
						gCB.setlap2length((double) sliderVal / 1);
									updatelap2lengthLabel();
								} catch (NumberFormatException ex) {
									updatelap2lengthLabel();
								}
							}
						});
						updatelap2lengthLabel();
			
						Border lap2lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel lap2lengthinnerPanel = new JPanel();
			
						lap2lengthinnerPanel.setLayout(new BoxLayout(lap2lengthinnerPanel, BoxLayout.Y_AXIS));
						lap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						lap2lengthinnerPanel.add(lap2lengthField);
						lap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						lap2lengthinnerPanel.add(lap2lengthSlider);
						lap2lengthinnerPanel.setBorder(lap2lengthborder2);
			
						frame.add(lap2lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					del2lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(5000 * 1),(int) (9000 * 1), (int) (gCB.getdel2length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						del2lengthSlider.addChangeListener(new MinReverb2Listener());
						del2lengthField = new JTextField();
						del2lengthField.setHorizontalAlignment(JTextField.CENTER);
						Border del2lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						del2lengthField.setBorder(del2lengthBorder1);
						del2lengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(del2lengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(del2lengthSlider.getMinimum(), Math.min(del2lengthSlider.getMaximum(), sliderVal));
						del2lengthSlider.setValue(sliderVal);
						gCB.setdel2length((double) sliderVal / 1);
									updatedel2lengthLabel();
								} catch (NumberFormatException ex) {
									updatedel2lengthLabel();
								}
							}
						});
						updatedel2lengthLabel();
			
						Border del2lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel del2lengthinnerPanel = new JPanel();
			
						del2lengthinnerPanel.setLayout(new BoxLayout(del2lengthinnerPanel, BoxLayout.Y_AXIS));
						del2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						del2lengthinnerPanel.add(del2lengthField);
						del2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						del2lengthinnerPanel.add(del2lengthSlider);
						del2lengthinnerPanel.setBorder(del2lengthborder2);
			
						frame.add(del2lengthinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class MinReverb2Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/10.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			if(ce.getSource() == ap1lengthSlider) {
			gCB.setap1length((double) (ap1lengthSlider.getValue()/1));			    					
				updateap1lengthLabel();
			}
			if(ce.getSource() == ap2lengthSlider) {
			gCB.setap2length((double) (ap2lengthSlider.getValue()/1));			    					
				updateap2lengthLabel();
			}
			if(ce.getSource() == ap3lengthSlider) {
			gCB.setap3length((double) (ap3lengthSlider.getValue()/1));			    					
				updateap3lengthLabel();
			}
			if(ce.getSource() == ap4lengthSlider) {
			gCB.setap4length((double) (ap4lengthSlider.getValue()/1));			    					
				updateap4lengthLabel();
			}
			if(ce.getSource() == klapSlider) {
			gCB.setklap((double) (klapSlider.getValue()/100.0));
				updateklapLabel();
			}
			if(ce.getSource() == lap1lengthSlider) {
			gCB.setlap1length((double) (lap1lengthSlider.getValue()/1));			    					
				updatelap1lengthLabel();
			}
			if(ce.getSource() == del1lengthSlider) {
			gCB.setdel1length((double) (del1lengthSlider.getValue()/1));			    					
				updatedel1lengthLabel();
			}
			if(ce.getSource() == lap2lengthSlider) {
			gCB.setlap2length((double) (lap2lengthSlider.getValue()/1));			    					
				updatelap2lengthLabel();
			}
			if(ce.getSource() == del2lengthSlider) {
			gCB.setdel2length((double) (del2lengthSlider.getValue()/1));			    					
				updatedel2lengthLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class MinReverb2ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class MinReverb2ActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));
		}
		private void updatekiapLabel() {
		kiapField.setText("Input All Pass: " + String.format("%4.2f", gCB.getkiap()));
		}
		private void updateap1lengthLabel() {
		ap1lengthField.setText("Input AP1 (msec) " + String.format("%4.1f", (1000 * gCB.getap1length())/ElmProgram.getSamplerate()));		
		}
		private void updateap2lengthLabel() {
		ap2lengthField.setText("Input AP2 (msec) " + String.format("%4.1f", (1000 * gCB.getap2length())/ElmProgram.getSamplerate()));		
		}
		private void updateap3lengthLabel() {
		ap3lengthField.setText("Input AP3 (msec) " + String.format("%4.1f", (1000 * gCB.getap3length())/ElmProgram.getSamplerate()));		
		}
		private void updateap4lengthLabel() {
		ap4lengthField.setText("Input AP4 (msec) " + String.format("%4.1f", (1000 * gCB.getap4length())/ElmProgram.getSamplerate()));		
		}
		private void updateklapLabel() {
		klapField.setText("Loop All Pass " + String.format("%4.2f", gCB.getklap()));
		}
		private void updatelap1lengthLabel() {
		lap1lengthField.setText("Loop AP1 (msec) " + String.format("%4.0f", (1000 * gCB.getlap1length())/ElmProgram.getSamplerate()));		
		}
		private void updatedel1lengthLabel() {
		del1lengthField.setText("Loop Delay 1 (msec) " + String.format("%4.0f", (1000 * gCB.getdel1length())/ElmProgram.getSamplerate()));		
		}
		private void updatelap2lengthLabel() {
		lap2lengthField.setText("Loop AP2 (msec) " + String.format("%4.0f", (1000 * gCB.getlap2length())/ElmProgram.getSamplerate()));		
		}
		private void updatedel2lengthLabel() {
		del2lengthField.setText("Loop Delay 2 (msec) " + String.format("%4.0f", (1000 * gCB.getdel2length())/ElmProgram.getSamplerate()));		
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
