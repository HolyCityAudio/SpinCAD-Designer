/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChirpControlPanel.java
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
import javax.swing.JFrame;
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
import com.holycityaudio.SpinCAD.CADBlocks.ChirpCADBlock;

@SuppressWarnings("unused")
public class ChirpControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private ChirpCADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider nAPsSlider;
	JTextField  nAPsField;
	FineControlSlider stretchSlider;
	JTextField  stretchField;
	FineControlSlider kiapSlider;
	JTextField  kiapField;

public ChirpControlPanel(ChirpCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Chirp");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain())));
						gainSlider.addChangeListener(new ChirpListener());
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
					nAPsSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(2 * 1.0),(int) (30 * 1.0), (int) (gCB.getnAPs() * 1.0));
						nAPsSlider.addChangeListener(new ChirpListener());
						nAPsField = new JTextField();
						nAPsField.setHorizontalAlignment(JTextField.CENTER);
						Border nAPsBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						nAPsField.setBorder(nAPsBorder1);
						nAPsField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(nAPsField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(nAPsSlider.getMinimum(), Math.min(nAPsSlider.getMaximum(), sliderVal));
						nAPsSlider.setValue(sliderVal);
						gCB.setnAPs((double) sliderVal / 1.0);
									updatenAPsLabel();
								} catch (NumberFormatException ex) {
									updatenAPsLabel();
								}
							}
						});
						updatenAPsLabel();
			
						Border nAPsborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel nAPsinnerPanel = new JPanel();
			
						nAPsinnerPanel.setLayout(new BoxLayout(nAPsinnerPanel, BoxLayout.Y_AXIS));
						nAPsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						nAPsinnerPanel.add(nAPsField);
						nAPsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						nAPsinnerPanel.add(nAPsSlider);
						nAPsinnerPanel.setBorder(nAPsborder2);
			
						frame.add(nAPsinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					stretchSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1 * 1.0),(int) (50 * 1.0), (int) (gCB.getstretch() * 1.0));
						stretchSlider.addChangeListener(new ChirpListener());
						stretchField = new JTextField();
						stretchField.setHorizontalAlignment(JTextField.CENTER);
						Border stretchBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						stretchField.setBorder(stretchBorder1);
						stretchField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(stretchField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(stretchSlider.getMinimum(), Math.min(stretchSlider.getMaximum(), sliderVal));
						stretchSlider.setValue(sliderVal);
						gCB.setstretch((double) sliderVal / 1.0);
									updatestretchLabel();
								} catch (NumberFormatException ex) {
									updatestretchLabel();
								}
							}
						});
						updatestretchLabel();
			
						Border stretchborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel stretchinnerPanel = new JPanel();
			
						stretchinnerPanel.setLayout(new BoxLayout(stretchinnerPanel, BoxLayout.Y_AXIS));
						stretchinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						stretchinnerPanel.add(stretchField);
						stretchinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						stretchinnerPanel.add(stretchSlider);
						stretchinnerPanel.setBorder(stretchborder2);
			
						frame.add(stretchinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kiapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-0.98 * 100.0),(int) (0.98 * 100.0), (int) (gCB.getkiap() * 100.0));
						kiapSlider.addChangeListener(new ChirpListener());
						kiapField = new JTextField();
						kiapField.setHorizontalAlignment(JTextField.CENTER);
						Border kiapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kiapField.setBorder(kiapBorder1);
						kiapField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kiapField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(kiapSlider.getMinimum(), Math.min(kiapSlider.getMaximum(), sliderVal));
						kiapSlider.setValue(sliderVal);
						gCB.setkiap((double) sliderVal / 100.0);
									updatekiapLabel();
								} catch (NumberFormatException ex) {
									updatekiapLabel();
								}
							}
						});
						updatekiapLabel();
			
						Border kiapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kiapinnerPanel = new JPanel();
			
						kiapinnerPanel.setLayout(new BoxLayout(kiapinnerPanel, BoxLayout.Y_AXIS));
						kiapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kiapinnerPanel.add(kiapField);
						kiapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kiapinnerPanel.add(kiapSlider);
						kiapinnerPanel.setBorder(kiapborder2);
			
						frame.add(kiapinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class ChirpListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == nAPsSlider) {
			gCB.setnAPs((double) (nAPsSlider.getValue()/1.0));
				updatenAPsLabel();
			}
			if(ce.getSource() == stretchSlider) {
			gCB.setstretch((double) (stretchSlider.getValue()/1.0));
				updatestretchLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class ChirpItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ChirpActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatenAPsLabel() {
		nAPsField.setText("Stages " + String.format("%4.1f", gCB.getnAPs()));		
		}		
		private void updatestretchLabel() {
		stretchField.setText("Stretch " + String.format("%4.1f", gCB.getstretch()));		
		}		
		private void updatekiapLabel() {
		kiapField.setText("All Pass " + String.format("%4.2f", gCB.getkiap()));		
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
