/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Pitch_shift_testControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.Pitch_shift_testCADBlock;

@SuppressWarnings("unused")
public class Pitch_shift_testControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private Pitch_shift_testCADBlock gCB;
	// declare the controls
	FineControlSlider pitchSemitonesSlider;
	JTextField  pitchSemitonesField;
	FineControlSlider pitchCentsSlider;
	JTextField  pitchCentsField;
	private JComboBox <String> controlRangeComboBox; 
	private JComboBox <String> lfoSelComboBox; 
	private JComboBox <String> lfoWidthComboBox; 

public Pitch_shift_testControlPanel(Pitch_shift_testCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Pitch Shift");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitchSemitonesSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (19 * 1.0), (int) (gCB.getpitchSemitones() * 1.0));
						pitchSemitonesSlider.addChangeListener(new Pitch_shift_testListener());
						pitchSemitonesField = new JTextField();
						pitchSemitonesField.setHorizontalAlignment(JTextField.CENTER);
						Border pitchSemitonesBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitchSemitonesField.setBorder(pitchSemitonesBorder1);
						pitchSemitonesField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pitchSemitonesField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(pitchSemitonesSlider.getMinimum(), Math.min(pitchSemitonesSlider.getMaximum(), sliderVal));
						pitchSemitonesSlider.setValue(sliderVal);
						gCB.setpitchSemitones((double) sliderVal / 1.0);
									updatepitchSemitonesLabel();
								} catch (NumberFormatException ex) {
									updatepitchSemitonesLabel();
								}
							}
						});
						updatepitchSemitonesLabel();
			
						Border pitchSemitonesborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitchSemitonesinnerPanel = new JPanel();
			
						pitchSemitonesinnerPanel.setLayout(new BoxLayout(pitchSemitonesinnerPanel, BoxLayout.Y_AXIS));
						pitchSemitonesinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitchSemitonesinnerPanel.add(pitchSemitonesField);
						pitchSemitonesinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitchSemitonesinnerPanel.add(pitchSemitonesSlider);
						pitchSemitonesinnerPanel.setBorder(pitchSemitonesborder2);
			
						frame.add(pitchSemitonesinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitchCentsSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-99 * 1.0),(int) (99 * 1.0), (int) (gCB.getpitchCents() * 1.0));
						pitchCentsSlider.addChangeListener(new Pitch_shift_testListener());
						pitchCentsField = new JTextField();
						pitchCentsField.setHorizontalAlignment(JTextField.CENTER);
						Border pitchCentsBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitchCentsField.setBorder(pitchCentsBorder1);
						pitchCentsField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pitchCentsField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(pitchCentsSlider.getMinimum(), Math.min(pitchCentsSlider.getMaximum(), sliderVal));
						pitchCentsSlider.setValue(sliderVal);
						gCB.setpitchCents((double) sliderVal / 1.0);
									updatepitchCentsLabel();
								} catch (NumberFormatException ex) {
									updatepitchCentsLabel();
								}
							}
						});
						updatepitchCentsLabel();
			
						Border pitchCentsborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitchCentsinnerPanel = new JPanel();
			
						pitchCentsinnerPanel.setLayout(new BoxLayout(pitchCentsinnerPanel, BoxLayout.Y_AXIS));
						pitchCentsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitchCentsinnerPanel.add(pitchCentsField);
						pitchCentsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitchCentsinnerPanel.add(pitchCentsSlider);
						pitchCentsinnerPanel.setBorder(pitchCentsborder2);
			
						frame.add(pitchCentsinnerPanel);
				controlRangeComboBox = new JComboBox <String> ();
				controlRangeComboBox.addItem("0 -> +1");
				controlRangeComboBox.addItem("-1 -> +1");
				controlRangeComboBox.setSelectedIndex(gCB.getcontrolRange());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(controlRangeComboBox);
				controlRangeComboBox.addActionListener(new Pitch_shift_testActionListener());
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new Pitch_shift_testActionListener());
				lfoWidthComboBox = new JComboBox <String> ();
				lfoWidthComboBox.addItem("4096");
				lfoWidthComboBox.addItem("2048");
			lfoWidthComboBox.addItem("1024");
			lfoWidthComboBox.addItem("512");
				lfoWidthComboBox.setSelectedIndex(gCB.getlfoWidth());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoWidthComboBox);
				lfoWidthComboBox.addActionListener(new Pitch_shift_testActionListener());
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class Pitch_shift_testListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == pitchSemitonesSlider) {
			gCB.setpitchSemitones((double) (pitchSemitonesSlider.getValue()/1.0));
				updatepitchSemitonesLabel();
			}
			if(ce.getSource() == pitchCentsSlider) {
			gCB.setpitchCents((double) (pitchCentsSlider.getValue()/1.0));
				updatepitchCentsLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class Pitch_shift_testItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class Pitch_shift_testActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == controlRangeComboBox) {
				gCB.setcontrolRange((controlRangeComboBox.getSelectedIndex()));
			}
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			if(arg0.getSource() == lfoWidthComboBox) {
				gCB.setlfoWidth((lfoWidthComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatepitchSemitonesLabel() {
		pitchSemitonesField.setText("Pitch (semitones) " + String.format("%4.0f", gCB.getpitchSemitones()));
		}
		private void updatepitchCentsLabel() {
		pitchCentsField.setText("Cents " + String.format("%4.0f", gCB.getpitchCents()));
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
