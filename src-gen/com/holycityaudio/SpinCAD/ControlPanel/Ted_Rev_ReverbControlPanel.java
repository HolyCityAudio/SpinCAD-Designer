/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Ted_Rev_ReverbControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.Ted_Rev_ReverbCADBlock;

@SuppressWarnings("unused")
public class Ted_Rev_ReverbControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private Ted_Rev_ReverbCADBlock gCB;
	// declare the controls
	FineControlSlider inputGainlSlider;
	JTextField  inputGainlField;
	FineControlSlider inputGainrSlider;
	JTextField  inputGainrField;
	FineControlSlider preSlider;
	JTextField  preField;
	FineControlSlider decaySlider;
	JTextField  decayField;

public Ted_Rev_ReverbControlPanel(Ted_Rev_ReverbCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Ted_Rev_Reverb");
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
						inputGainlSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 1.0),(int) (0 * 1.0), (int) (20 * Math.log10(gCB.getinputGainl()) * 1.0));
						inputGainlSlider.addChangeListener(new Ted_Rev_ReverbListener());
						inputGainlField = new JTextField();
						inputGainlField.setHorizontalAlignment(JTextField.CENTER);
						Border inputGainlBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputGainlField.setBorder(inputGainlBorder1);
						inputGainlField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(inputGainlField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(inputGainlSlider.getMinimum(), Math.min(inputGainlSlider.getMaximum(), sliderVal));
						inputGainlSlider.setValue(sliderVal);
						gCB.setinputGainl((double) sliderVal / 1.0);
									updateinputGainlLabel();
								} catch (NumberFormatException ex) {
									updateinputGainlLabel();
								}
							}
						});
						updateinputGainlLabel();
			
						Border inputGainlborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel inputGainlinnerPanel = new JPanel();
			
						inputGainlinnerPanel.setLayout(new BoxLayout(inputGainlinnerPanel, BoxLayout.Y_AXIS));
						inputGainlinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputGainlinnerPanel.add(inputGainlField);
						inputGainlinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputGainlinnerPanel.add(inputGainlSlider);
						inputGainlinnerPanel.setBorder(inputGainlborder2);
			
						frame.add(inputGainlinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						inputGainrSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 1.0),(int) (0 * 1.0), (int) (20 * Math.log10(gCB.getinputGainr()) * 1.0));
						inputGainrSlider.addChangeListener(new Ted_Rev_ReverbListener());
						inputGainrField = new JTextField();
						inputGainrField.setHorizontalAlignment(JTextField.CENTER);
						Border inputGainrBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputGainrField.setBorder(inputGainrBorder1);
						inputGainrField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(inputGainrField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(inputGainrSlider.getMinimum(), Math.min(inputGainrSlider.getMaximum(), sliderVal));
						inputGainrSlider.setValue(sliderVal);
						gCB.setinputGainr((double) sliderVal / 1.0);
									updateinputGainrLabel();
								} catch (NumberFormatException ex) {
									updateinputGainrLabel();
								}
							}
						});
						updateinputGainrLabel();
			
						Border inputGainrborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel inputGainrinnerPanel = new JPanel();
			
						inputGainrinnerPanel.setLayout(new BoxLayout(inputGainrinnerPanel, BoxLayout.Y_AXIS));
						inputGainrinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputGainrinnerPanel.add(inputGainrField);
						inputGainrinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputGainrinnerPanel.add(inputGainrSlider);
						inputGainrinnerPanel.setBorder(inputGainrborder2);
			
						frame.add(inputGainrinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					preSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (9900 * 1), (int) (gCB.getpre() * 1));
						preSlider.addChangeListener(new Ted_Rev_ReverbListener());
						preField = new JTextField();
						preField.setHorizontalAlignment(JTextField.CENTER);
						Border preBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						preField.setBorder(preBorder1);
						preField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(preField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1);
						sliderVal = Math.max(preSlider.getMinimum(), Math.min(preSlider.getMaximum(), sliderVal));
						preSlider.setValue(sliderVal);
						gCB.setpre((double) sliderVal / 1);
									updatepreLabel();
								} catch (NumberFormatException ex) {
									updatepreLabel();
								}
							}
						});
						updatepreLabel();
			
						Border preborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel preinnerPanel = new JPanel();
			
						preinnerPanel.setLayout(new BoxLayout(preinnerPanel, BoxLayout.Y_AXIS));
						preinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						preinnerPanel.add(preField);
						preinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						preinnerPanel.add(preSlider);
						preinnerPanel.setBorder(preborder2);
			
						frame.add(preinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					decaySlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (32767 * 1), (int) (gCB.getdecay() * 1));
						decaySlider.addChangeListener(new Ted_Rev_ReverbListener());
						decayField = new JTextField();
						decayField.setHorizontalAlignment(JTextField.CENTER);
						Border decayBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						decayField.setBorder(decayBorder1);
						decayField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(decayField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1);
						sliderVal = Math.max(decaySlider.getMinimum(), Math.min(decaySlider.getMaximum(), sliderVal));
						decaySlider.setValue(sliderVal);
						gCB.setdecay((double) sliderVal / 1);
									updatedecayLabel();
								} catch (NumberFormatException ex) {
									updatedecayLabel();
								}
							}
						});
						updatedecayLabel();
			
						Border decayborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel decayinnerPanel = new JPanel();
			
						decayinnerPanel.setLayout(new BoxLayout(decayinnerPanel, BoxLayout.Y_AXIS));
						decayinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						decayinnerPanel.add(decayField);
						decayinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						decayinnerPanel.add(decaySlider);
						decayinnerPanel.setBorder(decayborder2);
			
						frame.add(decayinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class Ted_Rev_ReverbListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainlSlider) {
			gCB.setinputGainl((double) (inputGainlSlider.getValue()/1.0));			    					
				updateinputGainlLabel();
			}
			if(ce.getSource() == inputGainrSlider) {
			gCB.setinputGainr((double) (inputGainrSlider.getValue()/1.0));			    					
				updateinputGainrLabel();
			}
			if(ce.getSource() == preSlider) {
			gCB.setpre((double) (preSlider.getValue()/1));
				updatepreLabel();
			}
			if(ce.getSource() == decaySlider) {
			gCB.setdecay((double) (decaySlider.getValue()/1));
				updatedecayLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class Ted_Rev_ReverbItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class Ted_Rev_ReverbActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainlLabel() {
		inputGainlField.setText("Input Gain L " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGainl()))));
		}
		private void updateinputGainrLabel() {
		inputGainrField.setText("Input Gain R " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGainr()))));
		}
		private void updatepreLabel() {
		preField.setText("Pre_Delay " + String.format("%4.0f", gCB.getpre()));
		}
		private void updatedecayLabel() {
		decayField.setText("Decay_Time " + String.format("%4.0f", gCB.getdecay()));
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
