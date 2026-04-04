/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * SVF_2P_adjustableControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.SVF_2P_adjustableCADBlock;

@SuppressWarnings("unused")
public class SVF_2P_adjustableControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private SVF_2P_adjustableCADBlock gCB;
	// declare the controls
	FineControlSlider freqSlider;
	JTextField  freqField;
	FineControlSlider qMaxSlider;
	JTextField  qMaxField;
	FineControlSlider qMinSlider;
	JTextField  qMinField;

public SVF_2P_adjustableControlPanel(SVF_2P_adjustableCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "SVF 2 Pole");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// multiplier is points per decade here
						freqSlider = SpinCADBlock.LogSlider(20,5000,gCB.getfreq(), "LOGFREQ2", 100.0);
					// ---------------------------------------------						
						freqSlider.addChangeListener(new SVF_2P_adjustableListener());
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
						gCB.setfreq(SpinCADBlock.freqToFiltSVF(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
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
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					qMaxSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1.0 * 1000.0),(int) (10.0 * 1000.0), (int) (gCB.getqMax() * 1000.0));
						qMaxSlider.addChangeListener(new SVF_2P_adjustableListener());
						qMaxField = new JTextField();
						qMaxField.setHorizontalAlignment(JTextField.CENTER);
						Border qMaxBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						qMaxField.setBorder(qMaxBorder1);
						qMaxField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(qMaxField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(qMaxSlider.getMinimum(), Math.min(qMaxSlider.getMaximum(), sliderVal));
						qMaxSlider.setValue(sliderVal);
						gCB.setqMax((double) sliderVal / 1000.0);
									updateqMaxLabel();
								} catch (NumberFormatException ex) {
									updateqMaxLabel();
								}
							}
						});
						updateqMaxLabel();
			
						Border qMaxborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel qMaxinnerPanel = new JPanel();
			
						qMaxinnerPanel.setLayout(new BoxLayout(qMaxinnerPanel, BoxLayout.Y_AXIS));
						qMaxinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						qMaxinnerPanel.add(qMaxField);
						qMaxinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						qMaxinnerPanel.add(qMaxSlider);
						qMaxinnerPanel.setBorder(qMaxborder2);
			
						frame.add(qMaxinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					qMinSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1.0 * 1000.0),(int) (10.0 * 1000.0), (int) (gCB.getqMin() * 1000.0));
						qMinSlider.addChangeListener(new SVF_2P_adjustableListener());
						qMinField = new JTextField();
						qMinField.setHorizontalAlignment(JTextField.CENTER);
						Border qMinBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						qMinField.setBorder(qMinBorder1);
						qMinField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(qMinField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(qMinSlider.getMinimum(), Math.min(qMinSlider.getMaximum(), sliderVal));
						qMinSlider.setValue(sliderVal);
						gCB.setqMin((double) sliderVal / 1000.0);
									updateqMinLabel();
								} catch (NumberFormatException ex) {
									updateqMinLabel();
								}
							}
						});
						updateqMinLabel();
			
						Border qMinborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel qMininnerPanel = new JPanel();
			
						qMininnerPanel.setLayout(new BoxLayout(qMininnerPanel, BoxLayout.Y_AXIS));
						qMininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						qMininnerPanel.add(qMinField);
						qMininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						qMininnerPanel.add(qMinSlider);
						qMininnerPanel.setBorder(qMinborder2);
			
						frame.add(qMininnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class SVF_2P_adjustableListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqSlider) {
			gCB.setfreq((double) SpinCADBlock.freqToFiltSVF(SpinCADBlock.sliderToLogval((int)(freqSlider.getValue()), 100.0)));
				updatefreqLabel();
			}
			if(ce.getSource() == qMaxSlider) {
			gCB.setqMax((double) (qMaxSlider.getValue()/1000.0));
				updateqMaxLabel();
			}
			if(ce.getSource() == qMinSlider) {
			gCB.setqMin((double) (qMinSlider.getValue()/1000.0));
				updateqMinLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class SVF_2P_adjustableItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class SVF_2P_adjustableActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatefreqLabel() {
		freqField.setText("Frequency (Hz) " + String.format("%4.1f", SpinCADBlock.filtToFreqSVF(gCB.getfreq())) + " Hz");		
		}		
		private void updateqMaxLabel() {
		qMaxField.setText("Max Resonance " + String.format("%4.1f", gCB.getqMax()));		
		}		
		private void updateqMinLabel() {
		qMinField.setText("Min Resonance " + String.format("%4.1f", gCB.getqMin()));		
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
