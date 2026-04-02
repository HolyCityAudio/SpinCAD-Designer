/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * LogControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.LogCADBlock;

@SuppressWarnings("unused")
public class LogControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private LogCADBlock gCB;
	// declare the controls
	FineControlSlider multiplierSlider;
	JTextField  multiplierField;
	FineControlSlider log_offsetSlider;
	JTextField  log_offsetField;

public LogControlPanel(LogCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Log");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					multiplierSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-1.00 * 1000.0),(int) (0.99999 * 1000.0), (int) (gCB.getmultiplier() * 1000.0));
						multiplierSlider.addChangeListener(new LogListener());
						multiplierField = new JTextField();
						multiplierField.setHorizontalAlignment(JTextField.CENTER);
						Border multiplierBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						multiplierField.setBorder(multiplierBorder1);
						multiplierField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(multiplierField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(multiplierSlider.getMinimum(), Math.min(multiplierSlider.getMaximum(), sliderVal));
						multiplierSlider.setValue(sliderVal);
						gCB.setmultiplier((double) sliderVal / 1000.0);
									updatemultiplierLabel();
								} catch (NumberFormatException ex) {
									updatemultiplierLabel();
								}
							}
						});
						updatemultiplierLabel();
			
						Border multiplierborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel multiplierinnerPanel = new JPanel();
			
						multiplierinnerPanel.setLayout(new BoxLayout(multiplierinnerPanel, BoxLayout.Y_AXIS));
						multiplierinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						multiplierinnerPanel.add(multiplierField);
						multiplierinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						multiplierinnerPanel.add(multiplierSlider);
						multiplierinnerPanel.setBorder(multiplierborder2);
			
						frame.add(multiplierinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					log_offsetSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-16 * 100.0),(int) (15.99999 * 100.0), (int) (gCB.getlog_offset() * 100.0));
						log_offsetSlider.addChangeListener(new LogListener());
						log_offsetField = new JTextField();
						log_offsetField.setHorizontalAlignment(JTextField.CENTER);
						Border log_offsetBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						log_offsetField.setBorder(log_offsetBorder1);
						log_offsetField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(log_offsetField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(log_offsetSlider.getMinimum(), Math.min(log_offsetSlider.getMaximum(), sliderVal));
						log_offsetSlider.setValue(sliderVal);
						gCB.setlog_offset((double) sliderVal / 100.0);
									updatelog_offsetLabel();
								} catch (NumberFormatException ex) {
									updatelog_offsetLabel();
								}
							}
						});
						updatelog_offsetLabel();
			
						Border log_offsetborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel log_offsetinnerPanel = new JPanel();
			
						log_offsetinnerPanel.setLayout(new BoxLayout(log_offsetinnerPanel, BoxLayout.Y_AXIS));
						log_offsetinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						log_offsetinnerPanel.add(log_offsetField);
						log_offsetinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						log_offsetinnerPanel.add(log_offsetSlider);
						log_offsetinnerPanel.setBorder(log_offsetborder2);
			
						frame.add(log_offsetinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class LogListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == multiplierSlider) {
			gCB.setmultiplier((double) (multiplierSlider.getValue()/1000.0));
				updatemultiplierLabel();
			}
			if(ce.getSource() == log_offsetSlider) {
			gCB.setlog_offset((double) (log_offsetSlider.getValue()/100.0));
				updatelog_offsetLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class LogItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class LogActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatemultiplierLabel() {
		multiplierField.setText("Log Multiplier " + String.format("%4.3f", gCB.getmultiplier()));		
		}		
		private void updatelog_offsetLabel() {
		log_offsetField.setText("Log_Offset " + String.format("%4.2f", gCB.getlog_offset()));		
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
