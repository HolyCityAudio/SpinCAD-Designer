/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * AliaserControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.AliaserCADBlock;

@SuppressWarnings("unused")
public class AliaserControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private AliaserCADBlock gCB;
	// declare the controls
	FineControlSlider ripLowSlider;
	JTextField  ripLowField;
	FineControlSlider ripHighSlider;
	JTextField  ripHighField;

public AliaserControlPanel(AliaserCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Aliaser");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ripLowSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (0.015 * 1000.0), (int) (gCB.getripLow() * 1000.0));
						ripLowSlider.addChangeListener(new AliaserListener());
						ripLowField = new JTextField();
						ripLowField.setHorizontalAlignment(JTextField.CENTER);
						Border ripLowBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ripLowField.setBorder(ripLowBorder1);
						ripLowField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ripLowField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(ripLowSlider.getMinimum(), Math.min(ripLowSlider.getMaximum(), sliderVal));
						ripLowSlider.setValue(sliderVal);
						gCB.setripLow((double) sliderVal / 1000.0);
									updateripLowLabel();
								} catch (NumberFormatException ex) {
									updateripLowLabel();
								}
							}
						});
						updateripLowLabel();
			
						Border ripLowborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ripLowinnerPanel = new JPanel();
			
						ripLowinnerPanel.setLayout(new BoxLayout(ripLowinnerPanel, BoxLayout.Y_AXIS));
						ripLowinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ripLowinnerPanel.add(ripLowField);
						ripLowinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ripLowinnerPanel.add(ripLowSlider);
						ripLowinnerPanel.setBorder(ripLowborder2);
			
						frame.add(ripLowinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ripHighSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.02 * 1000.0),(int) (0.2 * 1000.0), (int) (gCB.getripHigh() * 1000.0));
						ripHighSlider.addChangeListener(new AliaserListener());
						ripHighField = new JTextField();
						ripHighField.setHorizontalAlignment(JTextField.CENTER);
						Border ripHighBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ripHighField.setBorder(ripHighBorder1);
						ripHighField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(ripHighField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(ripHighSlider.getMinimum(), Math.min(ripHighSlider.getMaximum(), sliderVal));
						ripHighSlider.setValue(sliderVal);
						gCB.setripHigh((double) sliderVal / 1000.0);
									updateripHighLabel();
								} catch (NumberFormatException ex) {
									updateripHighLabel();
								}
							}
						});
						updateripHighLabel();
			
						Border ripHighborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ripHighinnerPanel = new JPanel();
			
						ripHighinnerPanel.setLayout(new BoxLayout(ripHighinnerPanel, BoxLayout.Y_AXIS));
						ripHighinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ripHighinnerPanel.add(ripHighField);
						ripHighinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						ripHighinnerPanel.add(ripHighSlider);
						ripHighinnerPanel.setBorder(ripHighborder2);
			
						frame.add(ripHighinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class AliaserListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == ripLowSlider) {
			gCB.setripLow((double) (ripLowSlider.getValue()/1000.0));
				updateripLowLabel();
			}
			if(ce.getSource() == ripHighSlider) {
			gCB.setripHigh((double) (ripHighSlider.getValue()/1000.0));
				updateripHighLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class AliaserItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class AliaserActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateripLowLabel() {
		ripLowField.setText("Rip Low " + String.format("%4.3f", gCB.getripLow()));
		}
		private void updateripHighLabel() {
		ripHighField.setText("Rip High " + String.format("%4.3f", gCB.getripHigh()));
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
