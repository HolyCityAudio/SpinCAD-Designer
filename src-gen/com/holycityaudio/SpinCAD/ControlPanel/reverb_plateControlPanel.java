/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_plateControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.reverb_plateCADBlock;

@SuppressWarnings("unused")
public class reverb_plateControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private reverb_plateCADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider krtSlider;
	JTextField  krtField;
	FineControlSlider kfhSlider;
	JTextField  kfhField;
	FineControlSlider kflSlider;
	JTextField  kflField;
	FineControlSlider kapiSlider;
	JTextField  kapiField;
	FineControlSlider kapSlider;
	JTextField  kapField;
	FineControlSlider rate1Slider;
	JTextField  rate1Field;
	FineControlSlider rate2Slider;
	JTextField  rate2Field;

public reverb_plateControlPanel(reverb_plateCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Reverb_Plate");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getgain())));
						gainSlider.addChangeListener(new reverb_plateListener());
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
					krtSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.15 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkrt() * 100.0));
						krtSlider.addChangeListener(new reverb_plateListener());
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
						kfhSlider = SpinCADBlock.LogSlider(40,500,gCB.getkfh(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						kfhSlider.addChangeListener(new reverb_plateListener());
						kfhField = new JTextField();
						kfhField.setHorizontalAlignment(JTextField.CENTER);
						Border kfhBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kfhField.setBorder(kfhBorder1);
						kfhField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kfhField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(kfhSlider.getMinimum(), Math.min(kfhSlider.getMaximum(), sliderVal));
						kfhSlider.setValue(sliderVal);
						gCB.setkfh(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatekfhLabel();
								} catch (NumberFormatException ex) {
									updatekfhLabel();
								}
							}
						});
						updatekfhLabel();
			
						Border kfhborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kfhinnerPanel = new JPanel();
			
						kfhinnerPanel.setLayout(new BoxLayout(kfhinnerPanel, BoxLayout.Y_AXIS));
						kfhinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kfhinnerPanel.add(kfhField);
						kfhinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kfhinnerPanel.add(kfhSlider);
						kfhinnerPanel.setBorder(kfhborder2);
			
						frame.add(kfhinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						kflSlider = SpinCADBlock.LogSlider(1000,8000,gCB.getkfl(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						kflSlider.addChangeListener(new reverb_plateListener());
						kflField = new JTextField();
						kflField.setHorizontalAlignment(JTextField.CENTER);
						Border kflBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kflField.setBorder(kflBorder1);
						kflField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kflField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(kflSlider.getMinimum(), Math.min(kflSlider.getMaximum(), sliderVal));
						kflSlider.setValue(sliderVal);
						gCB.setkfl(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatekflLabel();
								} catch (NumberFormatException ex) {
									updatekflLabel();
								}
							}
						});
						updatekflLabel();
			
						Border kflborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kflinnerPanel = new JPanel();
			
						kflinnerPanel.setLayout(new BoxLayout(kflinnerPanel, BoxLayout.Y_AXIS));
						kflinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kflinnerPanel.add(kflField);
						kflinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kflinnerPanel.add(kflSlider);
						kflinnerPanel.setBorder(kflborder2);
			
						frame.add(kflinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kapiSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-0.90 * 100.0),(int) (0.90 * 100.0), (int) (gCB.getkapi() * 100.0));
						kapiSlider.addChangeListener(new reverb_plateListener());
						kapiField = new JTextField();
						kapiField.setHorizontalAlignment(JTextField.CENTER);
						Border kapiBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kapiField.setBorder(kapiBorder1);
						kapiField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kapiField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(kapiSlider.getMinimum(), Math.min(kapiSlider.getMaximum(), sliderVal));
						kapiSlider.setValue(sliderVal);
						gCB.setkapi((double) sliderVal / 100.0);
									updatekapiLabel();
								} catch (NumberFormatException ex) {
									updatekapiLabel();
								}
							}
						});
						updatekapiLabel();
			
						Border kapiborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kapiinnerPanel = new JPanel();
			
						kapiinnerPanel.setLayout(new BoxLayout(kapiinnerPanel, BoxLayout.Y_AXIS));
						kapiinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kapiinnerPanel.add(kapiField);
						kapiinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kapiinnerPanel.add(kapiSlider);
						kapiinnerPanel.setBorder(kapiborder2);
			
						frame.add(kapiinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-0.90 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkap() * 100.0));
						kapSlider.addChangeListener(new reverb_plateListener());
						kapField = new JTextField();
						kapField.setHorizontalAlignment(JTextField.CENTER);
						Border kapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kapField.setBorder(kapBorder1);
						kapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(kapSlider.getMinimum(), Math.min(kapSlider.getMaximum(), sliderVal));
						kapSlider.setValue(sliderVal);
						gCB.setkap((double) sliderVal / 100.0);
									updatekapLabel();
								} catch (NumberFormatException ex) {
									updatekapLabel();
								}
							}
						});
						updatekapLabel();
			
						Border kapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kapinnerPanel = new JPanel();
			
						kapinnerPanel.setLayout(new BoxLayout(kapinnerPanel, BoxLayout.Y_AXIS));
						kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kapinnerPanel.add(kapField);
						kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kapinnerPanel.add(kapSlider);
						kapinnerPanel.setBorder(kapborder2);
			
						frame.add(kapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					rate1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (51.0 * 100.0), (int) ((gCB.getrate1()) * 100.0));
						rate1Slider.addChangeListener(new reverb_plateListener());
						rate1Field = new JTextField();
						rate1Field.setHorizontalAlignment(JTextField.CENTER);
						Border rate1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						rate1Field.setBorder(rate1Border1);
						rate1Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(rate1Field.getText().replaceAll("[^0-9.\\-]", ""));
						double coeff = val * 2.0 * Math.PI * Math.pow(2.0, 17) / (ElmProgram.getSamplerate() * 511.0);
						int sliderVal = (int) Math.round(coeff * 100.0);
						sliderVal = Math.max(rate1Slider.getMinimum(), Math.min(rate1Slider.getMaximum(), sliderVal));
						rate1Slider.setValue(sliderVal);
						gCB.setrate1((double) sliderVal / 100.0);
									updaterate1Label();
								} catch (NumberFormatException ex) {
									updaterate1Label();
								}
							}
						});
						updaterate1Label();
			
						Border rate1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel rate1innerPanel = new JPanel();
			
						rate1innerPanel.setLayout(new BoxLayout(rate1innerPanel, BoxLayout.Y_AXIS));
						rate1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						rate1innerPanel.add(rate1Field);
						rate1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						rate1innerPanel.add(rate1Slider);
						rate1innerPanel.setBorder(rate1border2);
			
						frame.add(rate1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					rate2Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (51.0 * 100.0), (int) ((gCB.getrate2()) * 100.0));
						rate2Slider.addChangeListener(new reverb_plateListener());
						rate2Field = new JTextField();
						rate2Field.setHorizontalAlignment(JTextField.CENTER);
						Border rate2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						rate2Field.setBorder(rate2Border1);
						rate2Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(rate2Field.getText().replaceAll("[^0-9.\\-]", ""));
						double coeff = val * 2.0 * Math.PI * Math.pow(2.0, 17) / (ElmProgram.getSamplerate() * 511.0);
						int sliderVal = (int) Math.round(coeff * 100.0);
						sliderVal = Math.max(rate2Slider.getMinimum(), Math.min(rate2Slider.getMaximum(), sliderVal));
						rate2Slider.setValue(sliderVal);
						gCB.setrate2((double) sliderVal / 100.0);
									updaterate2Label();
								} catch (NumberFormatException ex) {
									updaterate2Label();
								}
							}
						});
						updaterate2Label();
			
						Border rate2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel rate2innerPanel = new JPanel();
			
						rate2innerPanel.setLayout(new BoxLayout(rate2innerPanel, BoxLayout.Y_AXIS));
						rate2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						rate2innerPanel.add(rate2Field);
						rate2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						rate2innerPanel.add(rate2Slider);
						rate2innerPanel.setBorder(rate2border2);
			
						frame.add(rate2innerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class reverb_plateListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == krtSlider) {
			gCB.setkrt((double) (krtSlider.getValue()/100.0));
				updatekrtLabel();
			}
			if(ce.getSource() == kfhSlider) {
			gCB.setkfh((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kfhSlider.getValue()), 100.0)));
				updatekfhLabel();
			}
			if(ce.getSource() == kflSlider) {
			gCB.setkfl((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kflSlider.getValue()), 100.0)));
				updatekflLabel();
			}
			if(ce.getSource() == kapiSlider) {
			gCB.setkapi((double) (kapiSlider.getValue()/100.0));
				updatekapiLabel();
			}
			if(ce.getSource() == kapSlider) {
			gCB.setkap((double) (kapSlider.getValue()/100.0));
				updatekapLabel();
			}
			if(ce.getSource() == rate1Slider) {
			gCB.setrate1((double) (rate1Slider.getValue()/100.0));			    					
				updaterate1Label();
			}
			if(ce.getSource() == rate2Slider) {
			gCB.setrate2((double) (rate2Slider.getValue()/100.0));			    					
				updaterate2Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class reverb_plateItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class reverb_plateActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatekrtLabel() {
		krtField.setText("Reverb Time Coefficient " + String.format("%4.2f", gCB.getkrt()));		
		}		
		private void updatekfhLabel() {
		kfhField.setText("Low Freq Damping Frequency " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfh())) + " Hz");		
		}		
		private void updatekflLabel() {
		kflField.setText("High Freq Damping Frequency " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfl())) + " Hz");		
		}		
		private void updatekapiLabel() {
		kapiField.setText("Input All-Pass Coefficient " + String.format("%4.2f", gCB.getkapi()));		
		}		
		private void updatekapLabel() {
		kapField.setText("Loop All-Pass Coefficient " + String.format("%4.2f", gCB.getkap()));		
		}		
		private void updaterate1Label() {
		rate1Field.setText("LFO_Rate_1 " + String.format("%4.2f", coeffToLFORate(gCB.getrate1())));		
		}		
		private void updaterate2Label() {
		rate2Field.setText("LFO_Rate_2 " + String.format("%4.2f", coeffToLFORate(gCB.getrate2())));		
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
