/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * OutputControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;

@SuppressWarnings("unused")
public class OutputControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private OutputCADBlock gCB;
	// declare the controls
	FineControlSlider gain1Slider;
	JTextField  gain1Field;
	FineControlSlider gain2Slider;
	JTextField  gain2Field;
	JCheckBox monoCheckBox;
	JCheckBox offset0CheckBox;

public OutputControlPanel(OutputCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Output");
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
						gain1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getgain1()) * 10.0));
						gain1Slider.addChangeListener(new OutputListener());
						gain1Field = new JTextField();
						gain1Field.setHorizontalAlignment(JTextField.CENTER);
						Border gain1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gain1Field.setBorder(gain1Border1);
						gain1Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(gain1Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(gain1Slider.getMinimum(), Math.min(gain1Slider.getMaximum(), sliderVal));
						gain1Slider.setValue(sliderVal);
						gCB.setgain1((double) sliderVal / 10.0);
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
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider: multiplier sets steps per dB (e.g. 10 = 0.1 dB steps)
						gain2Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getgain2()) * 10.0));
						gain2Slider.addChangeListener(new OutputListener());
						gain2Field = new JTextField();
						gain2Field.setHorizontalAlignment(JTextField.CENTER);
						Border gain2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gain2Field.setBorder(gain2Border1);
						gain2Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(gain2Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(gain2Slider.getMinimum(), Math.min(gain2Slider.getMaximum(), sliderVal));
						gain2Slider.setValue(sliderVal);
						gCB.setgain2((double) sliderVal / 10.0);
									updategain2Label();
								} catch (NumberFormatException ex) {
									updategain2Label();
								}
							}
						});
						updategain2Label();
			
						Border gain2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel gain2innerPanel = new JPanel();
			
						gain2innerPanel.setLayout(new BoxLayout(gain2innerPanel, BoxLayout.Y_AXIS));
						gain2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						gain2innerPanel.add(gain2Field);
						gain2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						gain2innerPanel.add(gain2Slider);
						gain2innerPanel.setBorder(gain2border2);
			
						frame.add(gain2innerPanel);
			
			monoCheckBox = new JCheckBox("Mono", gCB.getmono());
			monoCheckBox.addItemListener(new OutputItemListener());
			frame.getContentPane().add(monoCheckBox);		
			
			offset0CheckBox = new JCheckBox("Offset", gCB.getoffset0());
			offset0CheckBox.addItemListener(new OutputItemListener());
			frame.getContentPane().add(offset0CheckBox);		
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class OutputListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gain1Slider) {
			gCB.setgain1((double) (gain1Slider.getValue()/10.0));			    					
				updategain1Label();
			}
			if(ce.getSource() == gain2Slider) {
			gCB.setgain2((double) (gain2Slider.getValue()/10.0));			    					
				updategain2Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class OutputItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			if(arg0.getSource() == monoCheckBox) {
				gCB.setmono((boolean) (monoCheckBox.isSelected()));
			}
			if(arg0.getSource() == offset0CheckBox) {
				gCB.setoffset0((boolean) (offset0CheckBox.isSelected()));
			}
			}
		}
		
		// add action listener for Combo Box
		class OutputActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategain1Label() {
		gain1Field.setText("Input Gain 1 " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain1()))));
		}
		private void updategain2Label() {
		gain2Field.setText("Input Gain 2 " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain2()))));
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
