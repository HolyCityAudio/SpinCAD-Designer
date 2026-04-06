/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_delay_line_01ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.reverb_delay_line_01CADBlock;

@SuppressWarnings("unused")
public class reverb_delay_line_01ControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private reverb_delay_line_01CADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider delayLengthSlider;
	JTextField  delayLengthField;
	FineControlSlider krtSlider;
	JTextField  krtField;
	FineControlSlider lpdfSlider;
	JTextField  lpdfField;
	FineControlSlider ap1LengthSlider;
	JTextField  ap1LengthField;
	FineControlSlider ap1kapSlider;
	JTextField  ap1kapField;
	FineControlSlider ap2LengthSlider;
	JTextField  ap2LengthField;
	FineControlSlider ap2kapSlider;
	JTextField  ap2kapField;

public reverb_delay_line_01ControlPanel(reverb_delay_line_01CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Reverb_Delay_Line_01");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 1.0),(int) (0 * 1.0), (int) (20 * Math.log10(gCB.getgain()) * 1.0));
						gainSlider.addChangeListener(new reverb_delay_line_01Listener());
						gainField = new JTextField();
						gainField.setHorizontalAlignment(JTextField.CENTER);
						Border gainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gainField.setBorder(gainBorder1);
						gainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(gainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(gainSlider.getMinimum(), Math.min(gainSlider.getMaximum(), sliderVal));
						gainSlider.setValue(sliderVal);
						gCB.setgain((double) sliderVal / 1.0);
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
					delayLengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (5000 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						delayLengthSlider.addChangeListener(new reverb_delay_line_01Listener());
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
					krtSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkrt() * 100.0));
						krtSlider.addChangeListener(new reverb_delay_line_01Listener());
						krtField = new JTextField();
						krtField.setHorizontalAlignment(JTextField.CENTER);
						Border krtBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						krtField.setBorder(krtBorder1);
						krtField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(krtField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(krtSlider.getMinimum(), Math.min(krtSlider.getMaximum(), sliderVal));
						krtSlider.setValue(sliderVal);
						gCB.setkrt((double) sliderVal / 100.0);
									updatekrtLabel();
								} catch (NumberFormatException ex) {
									updatekrtLabel();
								}
							}
						});
						updatekrtLabel();
			
						Border krtborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel krtinnerPanel = new JPanel();
			
						krtinnerPanel.setLayout(new BoxLayout(krtinnerPanel, BoxLayout.Y_AXIS));
						krtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						krtinnerPanel.add(krtField);
						krtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						krtinnerPanel.add(krtSlider);
						krtinnerPanel.setBorder(krtborder2);
			
						frame.add(krtinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						lpdfSlider = SpinCADBlock.LogSlider(500,2500,gCB.getlpdf(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						lpdfSlider.addChangeListener(new reverb_delay_line_01Listener());
						lpdfField = new JTextField();
						lpdfField.setHorizontalAlignment(JTextField.CENTER);
						Border lpdfBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						lpdfField.setBorder(lpdfBorder1);
						lpdfField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(lpdfField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(lpdfSlider.getMinimum(), Math.min(lpdfSlider.getMaximum(), sliderVal));
						lpdfSlider.setValue(sliderVal);
						gCB.setlpdf(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatelpdfLabel();
								} catch (NumberFormatException ex) {
									updatelpdfLabel();
								}
							}
						});
						updatelpdfLabel();
			
						Border lpdfborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel lpdfinnerPanel = new JPanel();
			
						lpdfinnerPanel.setLayout(new BoxLayout(lpdfinnerPanel, BoxLayout.Y_AXIS));
						lpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						lpdfinnerPanel.add(lpdfField);
						lpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						lpdfinnerPanel.add(lpdfSlider);
						lpdfinnerPanel.setBorder(lpdfborder2);
			
						frame.add(lpdfinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap1LengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (1500 * 1), (int) (gCB.getap1Length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap1LengthSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap1LengthField = new JTextField();
						ap1LengthField.setHorizontalAlignment(JTextField.CENTER);
						Border ap1LengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap1LengthField.setBorder(ap1LengthBorder1);
						ap1LengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap1LengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(ap1LengthSlider.getMinimum(), Math.min(ap1LengthSlider.getMaximum(), sliderVal));
						ap1LengthSlider.setValue(sliderVal);
						gCB.setap1Length((double) sliderVal / 1);
									updateap1LengthLabel();
								} catch (NumberFormatException ex) {
									updateap1LengthLabel();
								}
							}
						});
						updateap1LengthLabel();
			
						Border ap1Lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap1LengthinnerPanel = new JPanel();
			
						ap1LengthinnerPanel.setLayout(new BoxLayout(ap1LengthinnerPanel, BoxLayout.Y_AXIS));
						ap1LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap1LengthinnerPanel.add(ap1LengthField);
						ap1LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap1LengthinnerPanel.add(ap1LengthSlider);
						ap1LengthinnerPanel.setBorder(ap1Lengthborder2);
			
						frame.add(ap1LengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap1kapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getap1kap() * 100.0));
						ap1kapSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap1kapField = new JTextField();
						ap1kapField.setHorizontalAlignment(JTextField.CENTER);
						Border ap1kapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap1kapField.setBorder(ap1kapBorder1);
						ap1kapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap1kapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(ap1kapSlider.getMinimum(), Math.min(ap1kapSlider.getMaximum(), sliderVal));
						ap1kapSlider.setValue(sliderVal);
						gCB.setap1kap((double) sliderVal / 100.0);
									updateap1kapLabel();
								} catch (NumberFormatException ex) {
									updateap1kapLabel();
								}
							}
						});
						updateap1kapLabel();
			
						Border ap1kapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap1kapinnerPanel = new JPanel();
			
						ap1kapinnerPanel.setLayout(new BoxLayout(ap1kapinnerPanel, BoxLayout.Y_AXIS));
						ap1kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap1kapinnerPanel.add(ap1kapField);
						ap1kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap1kapinnerPanel.add(ap1kapSlider);
						ap1kapinnerPanel.setBorder(ap1kapborder2);
			
						frame.add(ap1kapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap2LengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (1500 * 1), (int) (gCB.getap2Length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap2LengthSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap2LengthField = new JTextField();
						ap2LengthField.setHorizontalAlignment(JTextField.CENTER);
						Border ap2LengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap2LengthField.setBorder(ap2LengthBorder1);
						ap2LengthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap2LengthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(ap2LengthSlider.getMinimum(), Math.min(ap2LengthSlider.getMaximum(), sliderVal));
						ap2LengthSlider.setValue(sliderVal);
						gCB.setap2Length((double) sliderVal / 1);
									updateap2LengthLabel();
								} catch (NumberFormatException ex) {
									updateap2LengthLabel();
								}
							}
						});
						updateap2LengthLabel();
			
						Border ap2Lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap2LengthinnerPanel = new JPanel();
			
						ap2LengthinnerPanel.setLayout(new BoxLayout(ap2LengthinnerPanel, BoxLayout.Y_AXIS));
						ap2LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap2LengthinnerPanel.add(ap2LengthField);
						ap2LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap2LengthinnerPanel.add(ap2LengthSlider);
						ap2LengthinnerPanel.setBorder(ap2Lengthborder2);
			
						frame.add(ap2LengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap2kapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getap2kap() * 100.0));
						ap2kapSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap2kapField = new JTextField();
						ap2kapField.setHorizontalAlignment(JTextField.CENTER);
						Border ap2kapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap2kapField.setBorder(ap2kapBorder1);
						ap2kapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ap2kapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(ap2kapSlider.getMinimum(), Math.min(ap2kapSlider.getMaximum(), sliderVal));
						ap2kapSlider.setValue(sliderVal);
						gCB.setap2kap((double) sliderVal / 100.0);
									updateap2kapLabel();
								} catch (NumberFormatException ex) {
									updateap2kapLabel();
								}
							}
						});
						updateap2kapLabel();
			
						Border ap2kapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap2kapinnerPanel = new JPanel();
			
						ap2kapinnerPanel.setLayout(new BoxLayout(ap2kapinnerPanel, BoxLayout.Y_AXIS));
						ap2kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap2kapinnerPanel.add(ap2kapField);
						ap2kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ap2kapinnerPanel.add(ap2kapSlider);
						ap2kapinnerPanel.setBorder(ap2kapborder2);
			
						frame.add(ap2kapinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class reverb_delay_line_01Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));			    					
				updatedelayLengthLabel();
			}
			if(ce.getSource() == krtSlider) {
			gCB.setkrt((double) (krtSlider.getValue()/100.0));
				updatekrtLabel();
			}
			if(ce.getSource() == lpdfSlider) {
			gCB.setlpdf((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(lpdfSlider.getValue()), 100.0)));
				updatelpdfLabel();
			}
			if(ce.getSource() == ap1LengthSlider) {
			gCB.setap1Length((double) (ap1LengthSlider.getValue()/1));			    					
				updateap1LengthLabel();
			}
			if(ce.getSource() == ap1kapSlider) {
			gCB.setap1kap((double) (ap1kapSlider.getValue()/100.0));
				updateap1kapLabel();
			}
			if(ce.getSource() == ap2LengthSlider) {
			gCB.setap2Length((double) (ap2LengthSlider.getValue()/1));			    					
				updateap2LengthLabel();
			}
			if(ce.getSource() == ap2kapSlider) {
			gCB.setap2kap((double) (ap2kapSlider.getValue()/100.0));
				updateap2kapLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class reverb_delay_line_01ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class reverb_delay_line_01ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthField.setText("Delay Line Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));		
		}		
		private void updatekrtLabel() {
		krtField.setText("Reverb Time Coefficient " + String.format("%4.2f", gCB.getkrt()));		
		}		
		private void updatelpdfLabel() {
		lpdfField.setText("Damping Freq Hi " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getlpdf())) + " Hz");		
		}		
		private void updateap1LengthLabel() {
		ap1LengthField.setText("Allpass #1 Time " + String.format("%4.0f", (1000 * gCB.getap1Length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap1kapLabel() {
		ap1kapField.setText("All-Pass #1 Coefficient " + String.format("%4.2f", gCB.getap1kap()));		
		}		
		private void updateap2LengthLabel() {
		ap2LengthField.setText("Allpass #2 Time " + String.format("%4.0f", (1000 * gCB.getap2Length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap2kapLabel() {
		ap2kapField.setText("All-Pass #2 Coefficient " + String.format("%4.2f", gCB.getap2kap()));		
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
