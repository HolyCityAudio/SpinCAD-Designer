/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChorusQuadControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.ChorusQuadCADBlock;

@SuppressWarnings("unused")
public class ChorusQuadControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private ChorusQuadCADBlock gCB;
	// declare the controls
	FineControlSlider gain1Slider;
	JTextField  gain1Field;
	FineControlSlider delayLengthSlider;
	JTextField  delayLengthField;
	FineControlSlider tap1CenterSlider;
	JTextField  tap1CenterField;
	FineControlSlider tap2CenterSlider;
	JTextField  tap2CenterField;
	FineControlSlider tap3CenterSlider;
	JTextField  tap3CenterField;
	FineControlSlider tap4CenterSlider;
	JTextField  tap4CenterField;
	FineControlSlider rateSlider;
	JTextField  rateField;
	FineControlSlider widthSlider;
	JTextField  widthField;
	private JComboBox <String> lfoSelComboBox; 

public ChorusQuadControlPanel(ChorusQuadCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "4-voice Chorus");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						gain1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 1.0),(int) (0 * 1.0), (int) (20 * Math.log10(gCB.getgain1()) * 1.0));
						gain1Slider.setSubdivision((int) 1.0);
						gain1Slider.addChangeListener(new ChorusQuadListener());
						gain1Field = new JTextField();
						gain1Field.setHorizontalAlignment(JTextField.CENTER);
						Border gain1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gain1Field.setBorder(gain1Border1);
						gain1Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(gain1Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(gain1Slider.getMinimum(), Math.min(gain1Slider.getMaximum(), sliderVal));
						gain1Slider.setValue(sliderVal);
						gCB.setgain1((double) sliderVal / 1.0);
									updategain1Label();
								} catch (NumberFormatException ex) {
									updategain1Label();
								}
							}
						});
						updategain1Label();
			
						Border gain1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel gain1innerPanel = new JPanel();
			
						gain1innerPanel.setLayout(new BoxLayout(gain1innerPanel, BoxLayout.Y_AXIS));
						gain1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						gain1innerPanel.add(gain1Field);
						gain1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						gain1innerPanel.add(gain1Slider);
						gain1innerPanel.setBorder(gain1border2);
			
						frame.add(gain1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					delayLengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (2048 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
						delayLengthSlider.addChangeListener(new ChorusQuadListener());
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
					tap1CenterSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap1Center() * 1000.0));
						tap1CenterSlider.addChangeListener(new ChorusQuadListener());
						tap1CenterField = new JTextField();
						tap1CenterField.setHorizontalAlignment(JTextField.CENTER);
						Border tap1CenterBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap1CenterField.setBorder(tap1CenterBorder1);
						tap1CenterField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap1CenterField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap1CenterSlider.getMinimum(), Math.min(tap1CenterSlider.getMaximum(), sliderVal));
						tap1CenterSlider.setValue(sliderVal);
						gCB.settap1Center((double) sliderVal / 1000.0);
									updatetap1CenterLabel();
								} catch (NumberFormatException ex) {
									updatetap1CenterLabel();
								}
							}
						});
						updatetap1CenterLabel();
			
						Border tap1Centerborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap1CenterinnerPanel = new JPanel();
			
						tap1CenterinnerPanel.setLayout(new BoxLayout(tap1CenterinnerPanel, BoxLayout.Y_AXIS));
						tap1CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap1CenterinnerPanel.add(tap1CenterField);
						tap1CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap1CenterinnerPanel.add(tap1CenterSlider);
						tap1CenterinnerPanel.setBorder(tap1Centerborder2);
			
						frame.add(tap1CenterinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap2CenterSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap2Center() * 1000.0));
						tap2CenterSlider.addChangeListener(new ChorusQuadListener());
						tap2CenterField = new JTextField();
						tap2CenterField.setHorizontalAlignment(JTextField.CENTER);
						Border tap2CenterBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap2CenterField.setBorder(tap2CenterBorder1);
						tap2CenterField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap2CenterField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap2CenterSlider.getMinimum(), Math.min(tap2CenterSlider.getMaximum(), sliderVal));
						tap2CenterSlider.setValue(sliderVal);
						gCB.settap2Center((double) sliderVal / 1000.0);
									updatetap2CenterLabel();
								} catch (NumberFormatException ex) {
									updatetap2CenterLabel();
								}
							}
						});
						updatetap2CenterLabel();
			
						Border tap2Centerborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap2CenterinnerPanel = new JPanel();
			
						tap2CenterinnerPanel.setLayout(new BoxLayout(tap2CenterinnerPanel, BoxLayout.Y_AXIS));
						tap2CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap2CenterinnerPanel.add(tap2CenterField);
						tap2CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap2CenterinnerPanel.add(tap2CenterSlider);
						tap2CenterinnerPanel.setBorder(tap2Centerborder2);
			
						frame.add(tap2CenterinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap3CenterSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap3Center() * 1000.0));
						tap3CenterSlider.addChangeListener(new ChorusQuadListener());
						tap3CenterField = new JTextField();
						tap3CenterField.setHorizontalAlignment(JTextField.CENTER);
						Border tap3CenterBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap3CenterField.setBorder(tap3CenterBorder1);
						tap3CenterField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap3CenterField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap3CenterSlider.getMinimum(), Math.min(tap3CenterSlider.getMaximum(), sliderVal));
						tap3CenterSlider.setValue(sliderVal);
						gCB.settap3Center((double) sliderVal / 1000.0);
									updatetap3CenterLabel();
								} catch (NumberFormatException ex) {
									updatetap3CenterLabel();
								}
							}
						});
						updatetap3CenterLabel();
			
						Border tap3Centerborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap3CenterinnerPanel = new JPanel();
			
						tap3CenterinnerPanel.setLayout(new BoxLayout(tap3CenterinnerPanel, BoxLayout.Y_AXIS));
						tap3CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap3CenterinnerPanel.add(tap3CenterField);
						tap3CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap3CenterinnerPanel.add(tap3CenterSlider);
						tap3CenterinnerPanel.setBorder(tap3Centerborder2);
			
						frame.add(tap3CenterinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap4CenterSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap4Center() * 1000.0));
						tap4CenterSlider.addChangeListener(new ChorusQuadListener());
						tap4CenterField = new JTextField();
						tap4CenterField.setHorizontalAlignment(JTextField.CENTER);
						Border tap4CenterBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap4CenterField.setBorder(tap4CenterBorder1);
						tap4CenterField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(tap4CenterField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(tap4CenterSlider.getMinimum(), Math.min(tap4CenterSlider.getMaximum(), sliderVal));
						tap4CenterSlider.setValue(sliderVal);
						gCB.settap4Center((double) sliderVal / 1000.0);
									updatetap4CenterLabel();
								} catch (NumberFormatException ex) {
									updatetap4CenterLabel();
								}
							}
						});
						updatetap4CenterLabel();
			
						Border tap4Centerborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap4CenterinnerPanel = new JPanel();
			
						tap4CenterinnerPanel.setLayout(new BoxLayout(tap4CenterinnerPanel, BoxLayout.Y_AXIS));
						tap4CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4CenterinnerPanel.add(tap4CenterField);
						tap4CenterinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						tap4CenterinnerPanel.add(tap4CenterSlider);
						tap4CenterinnerPanel.setBorder(tap4Centerborder2);
			
						frame.add(tap4CenterinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
					rateSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (511.0 * 100.0), (int) ((gCB.getrate()) * 100.0));
						rateSlider.addChangeListener(new ChorusQuadListener());
						rateField = new JTextField();
						rateField.setHorizontalAlignment(JTextField.CENTER);
						Border rateBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						rateField.setBorder(rateBorder1);
						rateField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(rateField.getText().replaceAll("[^0-9.\\-]", ""));
						double coeff = val * 2.0 * Math.PI * Math.pow(2.0, 17) / (ElmProgram.getSamplerate() * 511.0);
						int sliderVal = (int) Math.round(coeff * 100.0);
						sliderVal = Math.max(rateSlider.getMinimum(), Math.min(rateSlider.getMaximum(), sliderVal));
						rateSlider.setValue(sliderVal);
						gCB.setrate((double) sliderVal / 100.0);
									updaterateLabel();
								} catch (NumberFormatException ex) {
									updaterateLabel();
								}
							}
						});
						updaterateLabel();
			
						Border rateborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel rateinnerPanel = new JPanel();
			
						rateinnerPanel.setLayout(new BoxLayout(rateinnerPanel, BoxLayout.Y_AXIS));
						rateinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						rateinnerPanel.add(rateField);
						rateinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						rateinnerPanel.add(rateSlider);
						rateinnerPanel.setBorder(rateborder2);
			
						frame.add(rateinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					widthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (200.0 * 100.0), (int) (gCB.getwidth() * 100.0));
						widthSlider.addChangeListener(new ChorusQuadListener());
						widthField = new JTextField();
						widthField.setHorizontalAlignment(JTextField.CENTER);
						Border widthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						widthField.setBorder(widthBorder1);
						widthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(widthField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(widthSlider.getMinimum(), Math.min(widthSlider.getMaximum(), sliderVal));
						widthSlider.setValue(sliderVal);
						gCB.setwidth((double) sliderVal / 100.0);
									updatewidthLabel();
								} catch (NumberFormatException ex) {
									updatewidthLabel();
								}
							}
						});
						updatewidthLabel();
			
						Border widthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel widthinnerPanel = new JPanel();
			
						widthinnerPanel.setLayout(new BoxLayout(widthinnerPanel, BoxLayout.Y_AXIS));
						widthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						widthinnerPanel.add(widthField);
						widthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						widthinnerPanel.add(widthSlider);
						widthinnerPanel.setBorder(widthborder2);
			
						frame.add(widthinnerPanel);
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("SIN LFO 0");
				lfoSelComboBox.addItem("SIN LFO 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new ChorusQuadActionListener());
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class ChorusQuadListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gain1Slider) {
			gCB.setgain1((double) (gain1Slider.getValue()/1.0));
				updategain1Label();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));
				updatedelayLengthLabel();
			}
			if(ce.getSource() == tap1CenterSlider) {
			gCB.settap1Center((double) (tap1CenterSlider.getValue()/1000.0));
				updatetap1CenterLabel();
			}
			if(ce.getSource() == tap2CenterSlider) {
			gCB.settap2Center((double) (tap2CenterSlider.getValue()/1000.0));
				updatetap2CenterLabel();
			}
			if(ce.getSource() == tap3CenterSlider) {
			gCB.settap3Center((double) (tap3CenterSlider.getValue()/1000.0));
				updatetap3CenterLabel();
			}
			if(ce.getSource() == tap4CenterSlider) {
			gCB.settap4Center((double) (tap4CenterSlider.getValue()/1000.0));
				updatetap4CenterLabel();
			}
			if(ce.getSource() == rateSlider) {
			gCB.setrate((double) (rateSlider.getValue()/100.0));
				updaterateLabel();
			}
			if(ce.getSource() == widthSlider) {
			gCB.setwidth((double) (widthSlider.getValue()/100.0));
				updatewidthLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class ChorusQuadItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ChorusQuadActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updategain1Label() {
		gain1Field.setText("Input Gain 1 " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain1()))));
		}
		private void updatedelayLengthLabel() {
		delayLengthField.setText("Chorus_Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));
		}
		private void updatetap1CenterLabel() {
		tap1CenterField.setText("Tap_1_Center " + String.format("%4.3f", gCB.gettap1Center()));
		}
		private void updatetap2CenterLabel() {
		tap2CenterField.setText("Tap_2_Center " + String.format("%4.3f", gCB.gettap2Center()));
		}
		private void updatetap3CenterLabel() {
		tap3CenterField.setText("Tap_3_Center " + String.format("%4.3f", gCB.gettap3Center()));
		}
		private void updatetap4CenterLabel() {
		tap4CenterField.setText("Tap_4_Center " + String.format("%4.3f", gCB.gettap4Center()));
		}
		private void updaterateLabel() {
		rateField.setText("LFO_Rate " + String.format("%4.1f", coeffToLFORate(gCB.getrate())));
		}
		private void updatewidthLabel() {
		widthField.setText("LFO_Width " + String.format("%4.1f", gCB.getwidth()));
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
