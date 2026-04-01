/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * PluckControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.PluckCADBlock;

@SuppressWarnings("unused")
public class PluckControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private PluckCADBlock gCB;
	// declare the controls
	FineControlSlider thresholdSlider;
	JTextField  thresholdField;
	FineControlSlider pulseLevelSlider;
	JTextField  pulseLevelField;
	FineControlSlider pulseWidthSlider;
	JTextField  pulseWidthField;

public PluckControlPanel(PluckCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Pluck");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					thresholdSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (0.5 * 1000.0), (int) (gCB.getthreshold() * 1000.0));
						thresholdSlider.addChangeListener(new PluckListener());
						thresholdField = new JTextField();
						thresholdField.setHorizontalAlignment(JTextField.CENTER);
						Border thresholdBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						thresholdField.setBorder(thresholdBorder1);
						thresholdField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(thresholdField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(thresholdSlider.getMinimum(), Math.min(thresholdSlider.getMaximum(), sliderVal));
						thresholdSlider.setValue(sliderVal);
						gCB.setthreshold((double) sliderVal / 1000.0);
									updatethresholdLabel();
								} catch (NumberFormatException ex) {
									updatethresholdLabel();
								}
							}
						});
						updatethresholdLabel();
			
						Border thresholdborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel thresholdinnerPanel = new JPanel();
			
						thresholdinnerPanel.setLayout(new BoxLayout(thresholdinnerPanel, BoxLayout.Y_AXIS));
						thresholdinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						thresholdinnerPanel.add(thresholdField);
						thresholdinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						thresholdinnerPanel.add(thresholdSlider);
						thresholdinnerPanel.setBorder(thresholdborder2);
			
						frame.add(thresholdinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pulseLevelSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-1.0 * 100.0),(int) (1.0 * 100.0), (int) (gCB.getpulseLevel() * 100.0));
						pulseLevelSlider.addChangeListener(new PluckListener());
						pulseLevelField = new JTextField();
						pulseLevelField.setHorizontalAlignment(JTextField.CENTER);
						Border pulseLevelBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pulseLevelField.setBorder(pulseLevelBorder1);
						pulseLevelField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pulseLevelField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(pulseLevelSlider.getMinimum(), Math.min(pulseLevelSlider.getMaximum(), sliderVal));
						pulseLevelSlider.setValue(sliderVal);
						gCB.setpulseLevel((double) sliderVal / 100.0);
									updatepulseLevelLabel();
								} catch (NumberFormatException ex) {
									updatepulseLevelLabel();
								}
							}
						});
						updatepulseLevelLabel();
			
						Border pulseLevelborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pulseLevelinnerPanel = new JPanel();
			
						pulseLevelinnerPanel.setLayout(new BoxLayout(pulseLevelinnerPanel, BoxLayout.Y_AXIS));
						pulseLevelinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pulseLevelinnerPanel.add(pulseLevelField);
						pulseLevelinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pulseLevelinnerPanel.add(pulseLevelSlider);
						pulseLevelinnerPanel.setBorder(pulseLevelborder2);
			
						frame.add(pulseLevelinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pulseWidthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (5000 * 1), (int) (gCB.getpulseWidth() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						pulseWidthSlider.addChangeListener(new PluckListener());
						pulseWidthField = new JTextField();
						pulseWidthField.setHorizontalAlignment(JTextField.CENTER);
						Border pulseWidthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pulseWidthField.setBorder(pulseWidthBorder1);
						pulseWidthField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pulseWidthField.getText().replaceAll("[^0-9.\\-]", ""));
						double samples = val * ElmProgram.getSamplerate() / 1000.0;
						int sliderVal = (int) Math.round(samples * 1);
						sliderVal = Math.max(pulseWidthSlider.getMinimum(), Math.min(pulseWidthSlider.getMaximum(), sliderVal));
						pulseWidthSlider.setValue(sliderVal);
						gCB.setpulseWidth((double) sliderVal / 1);
									updatepulseWidthLabel();
								} catch (NumberFormatException ex) {
									updatepulseWidthLabel();
								}
							}
						});
						updatepulseWidthLabel();
			
						Border pulseWidthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pulseWidthinnerPanel = new JPanel();
			
						pulseWidthinnerPanel.setLayout(new BoxLayout(pulseWidthinnerPanel, BoxLayout.Y_AXIS));
						pulseWidthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pulseWidthinnerPanel.add(pulseWidthField);
						pulseWidthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pulseWidthinnerPanel.add(pulseWidthSlider);
						pulseWidthinnerPanel.setBorder(pulseWidthborder2);
			
						frame.add(pulseWidthinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class PluckListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == thresholdSlider) {
			gCB.setthreshold((double) (thresholdSlider.getValue()/1000.0));
				updatethresholdLabel();
			}
			if(ce.getSource() == pulseLevelSlider) {
			gCB.setpulseLevel((double) (pulseLevelSlider.getValue()/100.0));
				updatepulseLevelLabel();
			}
			if(ce.getSource() == pulseWidthSlider) {
			gCB.setpulseWidth((double) (pulseWidthSlider.getValue()/1));			    					
				updatepulseWidthLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class PluckItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class PluckActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatethresholdLabel() {
		thresholdField.setText("Threshold " + String.format("%4.3f", gCB.getthreshold()));		
		}		
		private void updatepulseLevelLabel() {
		pulseLevelField.setText("Pulse Amplitude " + String.format("%4.2f", gCB.getpulseLevel()));		
		}		
		private void updatepulseWidthLabel() {
		pulseWidthField.setText("Pulse Width " + String.format("%4.0f", (1000 * gCB.getpulseWidth())/ElmProgram.getSamplerate()));		
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
