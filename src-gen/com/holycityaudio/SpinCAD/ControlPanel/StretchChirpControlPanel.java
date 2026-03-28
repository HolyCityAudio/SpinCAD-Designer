/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * StretchChirpControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.StretchChirpCADBlock;

@SuppressWarnings("unused")
public class StretchChirpControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private StretchChirpCADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider stretchSlider;
	JTextField  stretchField;
	FineControlSlider ap01Slider;
	JTextField  ap01Field;

public StretchChirpControlPanel(StretchChirpCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "StretchChirp");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain())));
						gainSlider.addChangeListener(new StretchChirpListener());
						gainField = new JTextField();
						gainField.setHorizontalAlignment(JTextField.CENTER);
						Border gainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gainField.setBorder(gainBorder1);
						gainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(gainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val);
						sliderVal = Math.max(gainSlider.getMinimum(), Math.min(gainSlider.getMaximum(), sliderVal));
						gainSlider.setValue(sliderVal);
						gCB.setgain((double) sliderVal);
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
					stretchSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1 * 1.0),(int) (100 * 1.0), (int) (gCB.getstretch() * 1.0));
						stretchSlider.addChangeListener(new StretchChirpListener());
						stretchField = new JTextField();
						stretchField.setHorizontalAlignment(JTextField.CENTER);
						Border stretchBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						stretchField.setBorder(stretchBorder1);
						stretchField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(stretchField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(stretchSlider.getMinimum(), Math.min(stretchSlider.getMaximum(), sliderVal));
						stretchSlider.setValue(sliderVal);
						gCB.setstretch((double) sliderVal / 1.0);
									updatestretchLabel();
								} catch (NumberFormatException ex) {
									updatestretchLabel();
								}
							}
						});
						updatestretchLabel();
			
						Border stretchborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel stretchinnerPanel = new JPanel();
			
						stretchinnerPanel.setLayout(new BoxLayout(stretchinnerPanel, BoxLayout.Y_AXIS));
						stretchinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						stretchinnerPanel.add(stretchField);
						stretchinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						stretchinnerPanel.add(stretchSlider);
						stretchinnerPanel.setBorder(stretchborder2);
			
						frame.add(stretchinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap01Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-0.98 * 100.0),(int) (0.98 * 100.0), (int) (gCB.getap01() * 100.0));
						ap01Slider.addChangeListener(new StretchChirpListener());
						ap01Field = new JTextField();
						ap01Field.setHorizontalAlignment(JTextField.CENTER);
						Border ap01Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap01Field.setBorder(ap01Border1);
						ap01Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap01Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(ap01Slider.getMinimum(), Math.min(ap01Slider.getMaximum(), sliderVal));
						ap01Slider.setValue(sliderVal);
						gCB.setap01((double) sliderVal / 100.0);
									updateap01Label();
								} catch (NumberFormatException ex) {
									updateap01Label();
								}
							}
						});
						updateap01Label();
			
						Border ap01border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap01innerPanel = new JPanel();
			
						ap01innerPanel.setLayout(new BoxLayout(ap01innerPanel, BoxLayout.Y_AXIS));
						ap01innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap01innerPanel.add(ap01Field);
						ap01innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap01innerPanel.add(ap01Slider);
						ap01innerPanel.setBorder(ap01border2);
			
						frame.add(ap01innerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class StretchChirpListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == stretchSlider) {
			gCB.setstretch((double) (stretchSlider.getValue()/1.0));
				updatestretchLabel();
			}
			if(ce.getSource() == ap01Slider) {
			gCB.setap01((double) (ap01Slider.getValue()/100.0));
				updateap01Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class StretchChirpItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class StretchChirpActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatestretchLabel() {
		stretchField.setText("Stretch " + String.format("%4.1f", gCB.getstretch()));		
		}		
		private void updateap01Label() {
		ap01Field.setText("All Pass " + String.format("%4.2f", gCB.getap01()));		
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
