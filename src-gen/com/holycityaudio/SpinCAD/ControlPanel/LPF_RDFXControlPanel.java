/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * LPF_RDFXControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.LPF_RDFXCADBlock;

@SuppressWarnings("unused")
public class LPF_RDFXControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private LPF_RDFXCADBlock gCB;
	// declare the controls
	FineControlSlider freqSlider;
	JTextField  freqField;

public LPF_RDFXControlPanel(LPF_RDFXCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "LPF 1P");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						freqSlider = SpinCADBlock.LogSlider(80,5000,gCB.getfreq(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						freqSlider.addChangeListener(new LPF_RDFXListener());
						freqField = new JTextField();
						freqField.setHorizontalAlignment(JTextField.CENTER);
						Border freqBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						freqField.setBorder(freqBorder1);
						freqField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(freqField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(freqSlider.getMinimum(), Math.min(freqSlider.getMaximum(), sliderVal));
						freqSlider.setValue(sliderVal);
						gCB.setfreq(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatefreqLabel();
								} catch (NumberFormatException ex) {
									updatefreqLabel();
								}
							}
						});
						updatefreqLabel();
			
						Border freqborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel freqinnerPanel = new JPanel();
			
						freqinnerPanel.setLayout(new BoxLayout(freqinnerPanel, BoxLayout.Y_AXIS));
						freqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						freqinnerPanel.add(freqField);
						freqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						freqinnerPanel.add(freqSlider);
						freqinnerPanel.setBorder(freqborder2);
			
						frame.add(freqinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class LPF_RDFXListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqSlider) {
			gCB.setfreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(freqSlider.getValue()), 100.0)));
				updatefreqLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class LPF_RDFXItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class LPF_RDFXActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatefreqLabel() {
		freqField.setText("Frequency (Hz) " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getfreq())) + " Hz");		
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
