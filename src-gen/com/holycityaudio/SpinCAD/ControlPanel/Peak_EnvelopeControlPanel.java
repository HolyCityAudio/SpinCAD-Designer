/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Peak_EnvelopeControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.Peak_EnvelopeCADBlock;

@SuppressWarnings("unused")
public class Peak_EnvelopeControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private Peak_EnvelopeCADBlock gCB;
	// declare the controls
	FineControlSlider attackFreqSlider;
	JTextField  attackFreqField;
	FineControlSlider decayFreqSlider;
	JTextField  decayFreqField;

public Peak_EnvelopeControlPanel(Peak_EnvelopeCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Peak/Envelope");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						attackFreqSlider = SpinCADBlock.LogSlider(0.51,20,gCB.getattackFreq(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
						attackFreqSlider.addChangeListener(new Peak_EnvelopeListener());
						attackFreqField = new JTextField();
						attackFreqField.setHorizontalAlignment(JTextField.CENTER);
						Border attackFreqBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						attackFreqField.setBorder(attackFreqBorder1);
						attackFreqField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(attackFreqField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(attackFreqSlider.getMinimum(), Math.min(attackFreqSlider.getMaximum(), sliderVal));
						attackFreqSlider.setValue(sliderVal);
						gCB.setattackFreq(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updateattackFreqLabel();
								} catch (NumberFormatException ex) {
									updateattackFreqLabel();
								}
							}
						});
						updateattackFreqLabel();
			
						Border attackFreqborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel attackFreqinnerPanel = new JPanel();
			
						attackFreqinnerPanel.setLayout(new BoxLayout(attackFreqinnerPanel, BoxLayout.Y_AXIS));
						attackFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						attackFreqinnerPanel.add(attackFreqField);
						attackFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						attackFreqinnerPanel.add(attackFreqSlider);
						attackFreqinnerPanel.setBorder(attackFreqborder2);
			
						frame.add(attackFreqinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						decayFreqSlider = SpinCADBlock.LogSlider(0.51,10,gCB.getdecayFreq(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
						decayFreqSlider.addChangeListener(new Peak_EnvelopeListener());
						decayFreqField = new JTextField();
						decayFreqField.setHorizontalAlignment(JTextField.CENTER);
						Border decayFreqBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						decayFreqField.setBorder(decayFreqBorder1);
						decayFreqField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(decayFreqField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(decayFreqSlider.getMinimum(), Math.min(decayFreqSlider.getMaximum(), sliderVal));
						decayFreqSlider.setValue(sliderVal);
						gCB.setdecayFreq(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatedecayFreqLabel();
								} catch (NumberFormatException ex) {
									updatedecayFreqLabel();
								}
							}
						});
						updatedecayFreqLabel();
			
						Border decayFreqborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel decayFreqinnerPanel = new JPanel();
			
						decayFreqinnerPanel.setLayout(new BoxLayout(decayFreqinnerPanel, BoxLayout.Y_AXIS));
						decayFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						decayFreqinnerPanel.add(decayFreqField);
						decayFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						decayFreqinnerPanel.add(decayFreqSlider);
						decayFreqinnerPanel.setBorder(decayFreqborder2);
			
						frame.add(decayFreqinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class Peak_EnvelopeListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == attackFreqSlider) {
			gCB.setattackFreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(attackFreqSlider.getValue()), 100.0)));
				updateattackFreqLabel();
			}
			if(ce.getSource() == decayFreqSlider) {
			gCB.setdecayFreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(decayFreqSlider.getValue()), 100.0)));
				updatedecayFreqLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class Peak_EnvelopeItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class Peak_EnvelopeActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateattackFreqLabel() {
		attackFreqField.setText("Attack Freq " + String.format("%4.5f", SpinCADBlock.filtToFreq(gCB.getattackFreq())) + " Hz");
		}
		private void updatedecayFreqLabel() {
		decayFreqField.setText("Decay Freq " + String.format("%4.5f", SpinCADBlock.filtToFreq(gCB.getdecayFreq())) + " Hz");
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
