/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * FreeverbControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.FreeverbCADBlock;

@SuppressWarnings("unused")
public class FreeverbControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private FreeverbCADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider krtSlider;
	JTextField  krtField;
	FineControlSlider dampingSlider;
	JTextField  dampingField;

public FreeverbControlPanel(FreeverbCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Freeverb");
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
						gainSlider.addChangeListener(new FreeverbListener());
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
					krtSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.2 * 100.0),(int) (0.57 * 100.0), (int) (gCB.getkrt() * 100.0));
						krtSlider.addChangeListener(new FreeverbListener());
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
					dampingSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.1 * 100.0),(int) (0.9 * 100.0), (int) (gCB.getdamping() * 100.0));
						dampingSlider.addChangeListener(new FreeverbListener());
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
		class FreeverbListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == krtSlider) {
			gCB.setkrt((double) (krtSlider.getValue()/100.0));
				updatekrtLabel();
			}
			if(ce.getSource() == dampingSlider) {
			gCB.setdamping((double) (dampingSlider.getValue()/100.0));
				updatedampingLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class FreeverbItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class FreeverbActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatekrtLabel() {
		krtField.setText("Reverb Time " + String.format("%4.2f", gCB.getkrt()));		
		}		
		private void updatedampingLabel() {
		dampingField.setText("HF Damping " + String.format("%4.2f", gCB.getdamping()));		
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
