/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rom_rev1ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.rom_rev1CADBlock;

@SuppressWarnings("unused")
public class rom_rev1ControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private rom_rev1CADBlock gCB;
	// declare the controls
	FineControlSlider gainSlider;
	JTextField  gainField;
	FineControlSlider kiapSlider;
	JTextField  kiapField;
	FineControlSlider nDLsSlider;
	JTextField  nDLsField;
	FineControlSlider kapd1Slider;
	JTextField  kapd1Field;
	FineControlSlider kflSlider;
	JTextField  kflField;
	FineControlSlider kfhSlider;
	JTextField  kfhField;

public rom_rev1ControlPanel(rom_rev1CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "ROM_Reverb_1");
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
						gainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-18 * 10.0),(int) (0.0 * 10.0), (int) (20 * Math.log10(gCB.getgain()) * 10.0));
						gainSlider.setSubdivision((int) 10.0);
						gainSlider.addChangeListener(new rom_rev1Listener());
						gainField = new JTextField();
						gainField.setHorizontalAlignment(JTextField.CENTER);
						Border gainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gainField.setBorder(gainBorder1);
						gainField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(gainField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 10.0);
						sliderVal = Math.max(gainSlider.getMinimum(), Math.min(gainSlider.getMaximum(), sliderVal));
						gainSlider.setValue(sliderVal);
						gCB.setgain((double) sliderVal / 10.0);
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
					kiapSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.25 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkiap() * 100.0));
						kiapSlider.addChangeListener(new rom_rev1Listener());
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
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					nDLsSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(2 * 1.0),(int) (4 * 1.0), (int) (gCB.getnDLs() * 1.0));
						nDLsSlider.addChangeListener(new rom_rev1Listener());
						nDLsField = new JTextField();
						nDLsField.setHorizontalAlignment(JTextField.CENTER);
						Border nDLsBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						nDLsField.setBorder(nDLsBorder1);
						nDLsField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(nDLsField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(nDLsSlider.getMinimum(), Math.min(nDLsSlider.getMaximum(), sliderVal));
						nDLsSlider.setValue(sliderVal);
						gCB.setnDLs((double) sliderVal / 1.0);
									updatenDLsLabel();
								} catch (NumberFormatException ex) {
									updatenDLsLabel();
								}
							}
						});
						updatenDLsLabel();
			
						Border nDLsborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel nDLsinnerPanel = new JPanel();
			
						nDLsinnerPanel.setLayout(new BoxLayout(nDLsinnerPanel, BoxLayout.Y_AXIS));
						nDLsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						nDLsinnerPanel.add(nDLsField);
						nDLsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						nDLsinnerPanel.add(nDLsSlider);
						nDLsinnerPanel.setBorder(nDLsborder2);
			
						frame.add(nDLsinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kapd1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapd1() * 100.0));
						kapd1Slider.addChangeListener(new rom_rev1Listener());
						kapd1Field = new JTextField();
						kapd1Field.setHorizontalAlignment(JTextField.CENTER);
						Border kapd1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kapd1Field.setBorder(kapd1Border1);
						kapd1Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kapd1Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(kapd1Slider.getMinimum(), Math.min(kapd1Slider.getMaximum(), sliderVal));
						kapd1Slider.setValue(sliderVal);
						gCB.setkapd1((double) sliderVal / 100.0);
									updatekapd1Label();
								} catch (NumberFormatException ex) {
									updatekapd1Label();
								}
							}
						});
						updatekapd1Label();
			
						Border kapd1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kapd1innerPanel = new JPanel();
			
						kapd1innerPanel.setLayout(new BoxLayout(kapd1innerPanel, BoxLayout.Y_AXIS));
						kapd1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kapd1innerPanel.add(kapd1Field);
						kapd1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kapd1innerPanel.add(kapd1Slider);
						kapd1innerPanel.setBorder(kapd1border2);
			
						frame.add(kapd1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						kflSlider = SpinCADBlock.LogSlider(500,5000,gCB.getkfl(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
						kflSlider.addChangeListener(new rom_rev1Listener());
						kflField = new JTextField();
						kflField.setHorizontalAlignment(JTextField.CENTER);
						Border kflBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kflField.setBorder(kflBorder1);
						kflField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kflField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(kflSlider.getMinimum(), Math.min(kflSlider.getMaximum(), sliderVal));
						kflSlider.setValue(sliderVal);
						gCB.setkfl(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatekflLabel();
								} catch (NumberFormatException ex) {
									updatekflLabel();
								}
							}
						});
						updatekflLabel();
			
						Border kflborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kflinnerPanel = new JPanel();
			
						kflinnerPanel.setLayout(new BoxLayout(kflinnerPanel, BoxLayout.Y_AXIS));
						kflinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kflinnerPanel.add(kflField);
						kflinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kflinnerPanel.add(kflSlider);
						kflinnerPanel.setBorder(kflborder2);
			
						frame.add(kflinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						kfhSlider = SpinCADBlock.LogSlider(40,1000,gCB.getkfh(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------
					// QFACTOR is a log-scale Q slider; stored value = 1/Q
					// ---------------------------------------------
						kfhSlider.addChangeListener(new rom_rev1Listener());
						kfhField = new JTextField();
						kfhField.setHorizontalAlignment(JTextField.CENTER);
						Border kfhBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kfhField.setBorder(kfhBorder1);
						kfhField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(kfhField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
						sliderVal = Math.max(kfhSlider.getMinimum(), Math.min(kfhSlider.getMaximum(), sliderVal));
						kfhSlider.setValue(sliderVal);
						gCB.setkfh(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
									updatekfhLabel();
								} catch (NumberFormatException ex) {
									updatekfhLabel();
								}
							}
						});
						updatekfhLabel();
			
						Border kfhborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kfhinnerPanel = new JPanel();
			
						kfhinnerPanel.setLayout(new BoxLayout(kfhinnerPanel, BoxLayout.Y_AXIS));
						kfhinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kfhinnerPanel.add(kfhField);
						kfhinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						kfhinnerPanel.add(kfhSlider);
						kfhinnerPanel.setBorder(kfhborder2);
			
						frame.add(kfhinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class rom_rev1Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/10.0));
				updategainLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			if(ce.getSource() == nDLsSlider) {
			gCB.setnDLs((double) (nDLsSlider.getValue()/1.0));
				updatenDLsLabel();
			}
			if(ce.getSource() == kapd1Slider) {
			gCB.setkapd1((double) (kapd1Slider.getValue()/100.0));
				updatekapd1Label();
			}
			if(ce.getSource() == kflSlider) {
			gCB.setkfl((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kflSlider.getValue()), 100.0)));
				updatekflLabel();
			}
			if(ce.getSource() == kfhSlider) {
			gCB.setkfh((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kfhSlider.getValue()), 100.0)));
				updatekfhLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class rom_rev1ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class rom_rev1ActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainField.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));
		}
		private void updatekiapLabel() {
		kiapField.setText("Input_All_Pass " + String.format("%4.2f", gCB.getkiap()));
		}
		private void updatenDLsLabel() {
		nDLsField.setText("Delay_Stages " + String.format("%4.0f", gCB.getnDLs()));
		}
		private void updatekapd1Label() {
		kapd1Field.setText("Delay_All_Pass_1_Gain " + String.format("%4.2f", gCB.getkapd1()));
		}
		private void updatekflLabel() {
		kflField.setText("Low_Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfl())) + " Hz");
		}
		private void updatekfhLabel() {
		kfhField.setText("High_Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfh())) + " Hz");
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
