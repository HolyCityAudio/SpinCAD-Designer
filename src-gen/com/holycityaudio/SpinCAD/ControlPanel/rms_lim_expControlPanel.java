/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rms_lim_expControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.rms_lim_expCADBlock;

@SuppressWarnings("unused")
public class rms_lim_expControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private rms_lim_expCADBlock gCB;
	// declare the controls
	FineControlSlider makeupGainSlider;
	JTextField  makeupGainField;

public rms_lim_expControlPanel(rms_lim_expCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "RMS_Lim_Exp");
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
						makeupGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 10.0),(int) (6 * 10.0), (int) (20 * Math.log10(gCB.getmakeupGain()) * 10.0));
						makeupGainSlider.setSubdivision((int) 10.0);
						makeupGainSlider.addChangeListener(new rms_lim_expListener());
						makeupGainField = new JTextField();
						makeupGainField.setHorizontalAlignment(JTextField.CENTER);
						Border makeupGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						makeupGainField.setBorder(makeupGainBorder1);
						makeupGainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(makeupGainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(makeupGainSlider.getMinimum(), Math.min(makeupGainSlider.getMaximum(), sliderVal));
						makeupGainSlider.setValue(sliderVal);
						gCB.setmakeupGain((double) sliderVal / 10.0);
									updatemakeupGainLabel();
								} catch (NumberFormatException ex) {
									updatemakeupGainLabel();
								}
							}
						});
						updatemakeupGainLabel();
			
						Border makeupGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel makeupGaininnerPanel = new JPanel();
			
						makeupGaininnerPanel.setLayout(new BoxLayout(makeupGaininnerPanel, BoxLayout.Y_AXIS));
						makeupGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						makeupGaininnerPanel.add(makeupGainField);
						makeupGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						makeupGaininnerPanel.add(makeupGainSlider);
						makeupGaininnerPanel.setBorder(makeupGainborder2);
			
						frame.add(makeupGaininnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class rms_lim_expListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == makeupGainSlider) {
			gCB.setmakeupGain((double) (makeupGainSlider.getValue()/10.0));
				updatemakeupGainLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class rms_lim_expItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class rms_lim_expActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatemakeupGainLabel() {
		makeupGainField.setText("Makeup (dB) " + String.format("%4.1f dB", (20 * Math.log10(gCB.getmakeupGain()))));
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
