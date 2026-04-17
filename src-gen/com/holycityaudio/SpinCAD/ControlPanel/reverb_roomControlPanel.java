/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_roomControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.reverb_roomCADBlock;

@SuppressWarnings("unused")
public class reverb_roomControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private reverb_roomCADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider krtSlider;
	JTextField  krtField;
	FineControlSlider hpdfSlider;
	JTextField  hpdfField;
	FineControlSlider inputkapSlider;
	JTextField  inputkapField;
	FineControlSlider dlkapSlider;
	JTextField  dlkapField;
	FineControlSlider rate1Slider;
	JTextField  rate1Field;

public reverb_roomControlPanel(reverb_roomCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Reverb_Room");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getgain()) * 10.0));
						gainSlider.setSubdivision((int) 10.0);
						gainSlider.addChangeListener(new reverb_roomListener());
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
					krtSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkrt() * 100.0));
						krtSlider.addChangeListener(new reverb_roomListener());
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
						hpdfSlider = SpinCADBlock.LogSlider(40,1000,gCB.gethpdf(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
						hpdfSlider.addChangeListener(new reverb_roomListener());
						hpdfField = new JTextField();
						hpdfField.setHorizontalAlignment(JTextField.CENTER);
						Border hpdfBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						hpdfField.setBorder(hpdfBorder1);
						hpdfField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(hpdfField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(hpdfSlider.getMinimum(), Math.min(hpdfSlider.getMaximum(), sliderVal));
						hpdfSlider.setValue(sliderVal);
						gCB.sethpdf(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatehpdfLabel();
								} catch (NumberFormatException ex) {
									updatehpdfLabel();
								}
							}
						});
						updatehpdfLabel();
			
						Border hpdfborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel hpdfinnerPanel = new JPanel();
			
						hpdfinnerPanel.setLayout(new BoxLayout(hpdfinnerPanel, BoxLayout.Y_AXIS));
						hpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						hpdfinnerPanel.add(hpdfField);
						hpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						hpdfinnerPanel.add(hpdfSlider);
						hpdfinnerPanel.setBorder(hpdfborder2);
			
						frame.add(hpdfinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					inputkapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getinputkap() * 100.0));
						inputkapSlider.addChangeListener(new reverb_roomListener());
						inputkapField = new JTextField();
						inputkapField.setHorizontalAlignment(JTextField.CENTER);
						Border inputkapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputkapField.setBorder(inputkapBorder1);
						inputkapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(inputkapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(inputkapSlider.getMinimum(), Math.min(inputkapSlider.getMaximum(), sliderVal));
						inputkapSlider.setValue(sliderVal);
						gCB.setinputkap((double) sliderVal / 100.0);
									updateinputkapLabel();
								} catch (NumberFormatException ex) {
									updateinputkapLabel();
								}
							}
						});
						updateinputkapLabel();
			
						Border inputkapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel inputkapinnerPanel = new JPanel();
			
						inputkapinnerPanel.setLayout(new BoxLayout(inputkapinnerPanel, BoxLayout.Y_AXIS));
						inputkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputkapinnerPanel.add(inputkapField);
						inputkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						inputkapinnerPanel.add(inputkapSlider);
						inputkapinnerPanel.setBorder(inputkapborder2);
			
						frame.add(inputkapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					dlkapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getdlkap() * 100.0));
						dlkapSlider.addChangeListener(new reverb_roomListener());
						dlkapField = new JTextField();
						dlkapField.setHorizontalAlignment(JTextField.CENTER);
						Border dlkapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						dlkapField.setBorder(dlkapBorder1);
						dlkapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(dlkapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(dlkapSlider.getMinimum(), Math.min(dlkapSlider.getMaximum(), sliderVal));
						dlkapSlider.setValue(sliderVal);
						gCB.setdlkap((double) sliderVal / 100.0);
									updatedlkapLabel();
								} catch (NumberFormatException ex) {
									updatedlkapLabel();
								}
							}
						});
						updatedlkapLabel();
			
						Border dlkapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel dlkapinnerPanel = new JPanel();
			
						dlkapinnerPanel.setLayout(new BoxLayout(dlkapinnerPanel, BoxLayout.Y_AXIS));
						dlkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						dlkapinnerPanel.add(dlkapField);
						dlkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						dlkapinnerPanel.add(dlkapSlider);
						dlkapinnerPanel.setBorder(dlkapborder2);
			
						frame.add(dlkapinnerPanel);
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
					rate1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (51.0 * 100.0), (int) ((gCB.getrate1()) * 100.0));
						rate1Slider.addChangeListener(new reverb_roomListener());
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
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class reverb_roomListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/10.0));
				updategainLabel();
			}
			if(ce.getSource() == krtSlider) {
			gCB.setkrt((double) (krtSlider.getValue()/100.0));
				updatekrtLabel();
			}
			if(ce.getSource() == hpdfSlider) {
			gCB.sethpdf((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(hpdfSlider.getValue()), 100.0)));
				updatehpdfLabel();
			}
			if(ce.getSource() == inputkapSlider) {
			gCB.setinputkap((double) (inputkapSlider.getValue()/100.0));
				updateinputkapLabel();
			}
			if(ce.getSource() == dlkapSlider) {
			gCB.setdlkap((double) (dlkapSlider.getValue()/100.0));
				updatedlkapLabel();
			}
			if(ce.getSource() == rate1Slider) {
			gCB.setrate1((double) (rate1Slider.getValue()/100.0));
				updaterate1Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class reverb_roomItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class reverb_roomActionListener implements java.awt.event.ActionListener {
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
		private void updatehpdfLabel() {
		hpdfField.setText("Damping Freq Low " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.gethpdf())) + " Hz");
		}
		private void updateinputkapLabel() {
		inputkapField.setText("Input All-Pass Coefficient " + String.format("%4.2f", gCB.getinputkap()));
		}
		private void updatedlkapLabel() {
		dlkapField.setText("Delay All-Pass Coefficient " + String.format("%4.2f", gCB.getdlkap()));
		}
		private void updaterate1Label() {
		rate1Field.setText("LFO_Rate_1 " + String.format("%4.2f", coeffToLFORate(gCB.getrate1())));
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
