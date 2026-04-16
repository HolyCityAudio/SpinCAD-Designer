/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * CombFilterControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.CombFilterCADBlock;

@SuppressWarnings("unused")
public class CombFilterControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private CombFilterCADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider delayLengthSlider;
	JTextField  delayLengthField;
	FineControlSlider feedbackSlider;
	JTextField  feedbackField;
	FineControlSlider dampingSlider;
	JTextField  dampingField;

public CombFilterControlPanel(CombFilterCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Comb_Filter");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-18 * 1.0),(int) (0 * 1.0), (int) (20 * Math.log10(gCB.getgain()) * 1.0));
						gainSlider.setSubdivision((int) 1.0);
						gainSlider.addChangeListener(new CombFilterListener());
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
					delayLengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(100 * 1),(int) (4095 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
						delayLengthSlider.addChangeListener(new CombFilterListener());
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
					feedbackSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getfeedback() * 100.0));
						feedbackSlider.addChangeListener(new CombFilterListener());
						feedbackField = new JTextField();
						feedbackField.setHorizontalAlignment(JTextField.CENTER);
						Border feedbackBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						feedbackField.setBorder(feedbackBorder1);
						feedbackField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(feedbackField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(feedbackSlider.getMinimum(), Math.min(feedbackSlider.getMaximum(), sliderVal));
						feedbackSlider.setValue(sliderVal);
						gCB.setfeedback((double) sliderVal / 100.0);
									updatefeedbackLabel();
								} catch (NumberFormatException ex) {
									updatefeedbackLabel();
								}
							}
						});
						updatefeedbackLabel();
			
						Border feedbackborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel feedbackinnerPanel = new JPanel();
			
						feedbackinnerPanel.setLayout(new BoxLayout(feedbackinnerPanel, BoxLayout.Y_AXIS));
						feedbackinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						feedbackinnerPanel.add(feedbackField);
						feedbackinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						feedbackinnerPanel.add(feedbackSlider);
						feedbackinnerPanel.setBorder(feedbackborder2);
			
						frame.add(feedbackinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					dampingSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.99 * 100.0), (int) (gCB.getdamping() * 100.0));
						dampingSlider.addChangeListener(new CombFilterListener());
						dampingField = new JTextField();
						dampingField.setHorizontalAlignment(JTextField.CENTER);
						Border dampingBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						dampingField.setBorder(dampingBorder1);
						dampingField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(dampingField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(dampingSlider.getMinimum(), Math.min(dampingSlider.getMaximum(), sliderVal));
						dampingSlider.setValue(sliderVal);
						gCB.setdamping((double) sliderVal / 100.0);
									updatedampingLabel();
								} catch (NumberFormatException ex) {
									updatedampingLabel();
								}
							}
						});
						updatedampingLabel();
			
						Border dampingborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel dampinginnerPanel = new JPanel();
			
						dampinginnerPanel.setLayout(new BoxLayout(dampinginnerPanel, BoxLayout.Y_AXIS));
						dampinginnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						dampinginnerPanel.add(dampingField);
						dampinginnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						dampinginnerPanel.add(dampingSlider);
						dampinginnerPanel.setBorder(dampingborder2);
			
						frame.add(dampinginnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class CombFilterListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));
				updategainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));
				updatedelayLengthLabel();
			}
			if(ce.getSource() == feedbackSlider) {
			gCB.setfeedback((double) (feedbackSlider.getValue()/100.0));
				updatefeedbackLabel();
			}
			if(ce.getSource() == dampingSlider) {
			gCB.setdamping((double) (dampingSlider.getValue()/100.0));
				updatedampingLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class CombFilterItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class CombFilterActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));
		}
		private void updatedelayLengthLabel() {
		delayLengthField.setText("Delay Length " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));
		}
		private void updatefeedbackLabel() {
		feedbackField.setText("Feedback " + String.format("%4.2f", gCB.getfeedback()));
		}
		private void updatedampingLabel() {
		dampingField.setText("LP Damping " + String.format("%4.2f", gCB.getdamping()));
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
