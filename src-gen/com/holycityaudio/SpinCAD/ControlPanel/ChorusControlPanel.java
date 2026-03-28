/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChorusControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.ChorusCADBlock;

@SuppressWarnings("unused")
public class ChorusControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private ChorusCADBlock gCB;
	// declare the controls
	FineControlSlider delayLengthSlider;
	JTextField  delayLengthField;
	FineControlSlider rateSlider;
	JTextField  rateField;
	FineControlSlider widthSlider;
	JTextField  widthField;
	private JComboBox <String> lfoSelComboBox; 

public ChorusControlPanel(ChorusCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Chorus");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					delayLengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(128 * 1),(int) (2048 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						delayLengthSlider.addChangeListener(new ChorusListener());
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
					rateSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (511.0 * 100.0), (int) ((gCB.getrate()) * 100.0));
						rateSlider.addChangeListener(new ChorusListener());
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
					widthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(5.0 * 100.0),(int) (100.0 * 100.0), (int) (gCB.getwidth() * 100.0));
						widthSlider.addChangeListener(new ChorusListener());
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
				lfoSelComboBox.addActionListener(new ChorusActionListener());
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class ChorusListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));			    					
				updatedelayLengthLabel();
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
		class ChorusItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ChorusActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatedelayLengthLabel() {
		delayLengthField.setText("Chorus Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));		
		}		
		private void updaterateLabel() {
		rateField.setText("LFO Rate " + String.format("%4.2f", coeffToLFORate(gCB.getrate())));		
		}		
		private void updatewidthLabel() {
		widthField.setText("LFO Width " + String.format("%4.1f", gCB.getwidth()));		
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
