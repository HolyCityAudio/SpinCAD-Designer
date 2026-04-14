/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * pitch_fourControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.pitch_fourCADBlock;

@SuppressWarnings("unused")
public class pitch_fourControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private pitch_fourCADBlock gCB;
	// declare the controls
	FineControlSlider pitch1Slider;
	JTextField  pitch1Field;
	FineControlSlider cents1Slider;
	JTextField  cents1Field;
	FineControlSlider pitch2Slider;
	JTextField  pitch2Field;
	FineControlSlider cents2Slider;
	JTextField  cents2Field;
	FineControlSlider pitch3Slider;
	JTextField  pitch3Field;
	FineControlSlider cents3Slider;
	JTextField  cents3Field;
	FineControlSlider pitch4Slider;
	JTextField  pitch4Field;
	FineControlSlider cents4Slider;
	JTextField  cents4Field;
	private JComboBox <String> lfoSelComboBox; 

public pitch_fourControlPanel(pitch_fourCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Pitch_Four");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch1() * 1.0));
						pitch1Slider.addChangeListener(new pitch_fourListener());
						pitch1Field = new JTextField();
						pitch1Field.setHorizontalAlignment(JTextField.CENTER);
						Border pitch1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch1Field.setBorder(pitch1Border1);
						pitch1Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pitch1Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(pitch1Slider.getMinimum(), Math.min(pitch1Slider.getMaximum(), sliderVal));
						pitch1Slider.setValue(sliderVal);
						gCB.setpitch1((double) sliderVal / 1.0);
									updatepitch1Label();
								} catch (NumberFormatException ex) {
									updatepitch1Label();
								}
							}
						});
						updatepitch1Label();
			
						Border pitch1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch1innerPanel = new JPanel();
			
						pitch1innerPanel.setLayout(new BoxLayout(pitch1innerPanel, BoxLayout.Y_AXIS));
						pitch1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch1innerPanel.add(pitch1Field);
						pitch1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch1innerPanel.add(pitch1Slider);
						pitch1innerPanel.setBorder(pitch1border2);
			
						frame.add(pitch1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					cents1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-99 * 1.0),(int) (99 * 1.0), (int) (gCB.getcents1() * 1.0));
						cents1Slider.addChangeListener(new pitch_fourListener());
						cents1Field = new JTextField();
						cents1Field.setHorizontalAlignment(JTextField.CENTER);
						Border cents1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						cents1Field.setBorder(cents1Border1);
						cents1Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(cents1Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(cents1Slider.getMinimum(), Math.min(cents1Slider.getMaximum(), sliderVal));
						cents1Slider.setValue(sliderVal);
						gCB.setcents1((double) sliderVal / 1.0);
									updatecents1Label();
								} catch (NumberFormatException ex) {
									updatecents1Label();
								}
							}
						});
						updatecents1Label();
			
						Border cents1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel cents1innerPanel = new JPanel();
			
						cents1innerPanel.setLayout(new BoxLayout(cents1innerPanel, BoxLayout.Y_AXIS));
						cents1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents1innerPanel.add(cents1Field);
						cents1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents1innerPanel.add(cents1Slider);
						cents1innerPanel.setBorder(cents1border2);
			
						frame.add(cents1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch2Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch2() * 1.0));
						pitch2Slider.addChangeListener(new pitch_fourListener());
						pitch2Field = new JTextField();
						pitch2Field.setHorizontalAlignment(JTextField.CENTER);
						Border pitch2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch2Field.setBorder(pitch2Border1);
						pitch2Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pitch2Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(pitch2Slider.getMinimum(), Math.min(pitch2Slider.getMaximum(), sliderVal));
						pitch2Slider.setValue(sliderVal);
						gCB.setpitch2((double) sliderVal / 1.0);
									updatepitch2Label();
								} catch (NumberFormatException ex) {
									updatepitch2Label();
								}
							}
						});
						updatepitch2Label();
			
						Border pitch2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch2innerPanel = new JPanel();
			
						pitch2innerPanel.setLayout(new BoxLayout(pitch2innerPanel, BoxLayout.Y_AXIS));
						pitch2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch2innerPanel.add(pitch2Field);
						pitch2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch2innerPanel.add(pitch2Slider);
						pitch2innerPanel.setBorder(pitch2border2);
			
						frame.add(pitch2innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					cents2Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-99 * 1.0),(int) (99 * 1.0), (int) (gCB.getcents2() * 1.0));
						cents2Slider.addChangeListener(new pitch_fourListener());
						cents2Field = new JTextField();
						cents2Field.setHorizontalAlignment(JTextField.CENTER);
						Border cents2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						cents2Field.setBorder(cents2Border1);
						cents2Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(cents2Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(cents2Slider.getMinimum(), Math.min(cents2Slider.getMaximum(), sliderVal));
						cents2Slider.setValue(sliderVal);
						gCB.setcents2((double) sliderVal / 1.0);
									updatecents2Label();
								} catch (NumberFormatException ex) {
									updatecents2Label();
								}
							}
						});
						updatecents2Label();
			
						Border cents2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel cents2innerPanel = new JPanel();
			
						cents2innerPanel.setLayout(new BoxLayout(cents2innerPanel, BoxLayout.Y_AXIS));
						cents2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents2innerPanel.add(cents2Field);
						cents2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents2innerPanel.add(cents2Slider);
						cents2innerPanel.setBorder(cents2border2);
			
						frame.add(cents2innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch3Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch3() * 1.0));
						pitch3Slider.addChangeListener(new pitch_fourListener());
						pitch3Field = new JTextField();
						pitch3Field.setHorizontalAlignment(JTextField.CENTER);
						Border pitch3Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch3Field.setBorder(pitch3Border1);
						pitch3Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pitch3Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(pitch3Slider.getMinimum(), Math.min(pitch3Slider.getMaximum(), sliderVal));
						pitch3Slider.setValue(sliderVal);
						gCB.setpitch3((double) sliderVal / 1.0);
									updatepitch3Label();
								} catch (NumberFormatException ex) {
									updatepitch3Label();
								}
							}
						});
						updatepitch3Label();
			
						Border pitch3border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch3innerPanel = new JPanel();
			
						pitch3innerPanel.setLayout(new BoxLayout(pitch3innerPanel, BoxLayout.Y_AXIS));
						pitch3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch3innerPanel.add(pitch3Field);
						pitch3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch3innerPanel.add(pitch3Slider);
						pitch3innerPanel.setBorder(pitch3border2);
			
						frame.add(pitch3innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					cents3Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-99 * 1.0),(int) (99 * 1.0), (int) (gCB.getcents3() * 1.0));
						cents3Slider.addChangeListener(new pitch_fourListener());
						cents3Field = new JTextField();
						cents3Field.setHorizontalAlignment(JTextField.CENTER);
						Border cents3Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						cents3Field.setBorder(cents3Border1);
						cents3Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(cents3Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(cents3Slider.getMinimum(), Math.min(cents3Slider.getMaximum(), sliderVal));
						cents3Slider.setValue(sliderVal);
						gCB.setcents3((double) sliderVal / 1.0);
									updatecents3Label();
								} catch (NumberFormatException ex) {
									updatecents3Label();
								}
							}
						});
						updatecents3Label();
			
						Border cents3border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel cents3innerPanel = new JPanel();
			
						cents3innerPanel.setLayout(new BoxLayout(cents3innerPanel, BoxLayout.Y_AXIS));
						cents3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents3innerPanel.add(cents3Field);
						cents3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents3innerPanel.add(cents3Slider);
						cents3innerPanel.setBorder(cents3border2);
			
						frame.add(cents3innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch4Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch4() * 1.0));
						pitch4Slider.addChangeListener(new pitch_fourListener());
						pitch4Field = new JTextField();
						pitch4Field.setHorizontalAlignment(JTextField.CENTER);
						Border pitch4Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch4Field.setBorder(pitch4Border1);
						pitch4Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(pitch4Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(pitch4Slider.getMinimum(), Math.min(pitch4Slider.getMaximum(), sliderVal));
						pitch4Slider.setValue(sliderVal);
						gCB.setpitch4((double) sliderVal / 1.0);
									updatepitch4Label();
								} catch (NumberFormatException ex) {
									updatepitch4Label();
								}
							}
						});
						updatepitch4Label();
			
						Border pitch4border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch4innerPanel = new JPanel();
			
						pitch4innerPanel.setLayout(new BoxLayout(pitch4innerPanel, BoxLayout.Y_AXIS));
						pitch4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch4innerPanel.add(pitch4Field);
						pitch4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						pitch4innerPanel.add(pitch4Slider);
						pitch4innerPanel.setBorder(pitch4border2);
			
						frame.add(pitch4innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					cents4Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-99 * 1.0),(int) (99 * 1.0), (int) (gCB.getcents4() * 1.0));
						cents4Slider.addChangeListener(new pitch_fourListener());
						cents4Field = new JTextField();
						cents4Field.setHorizontalAlignment(JTextField.CENTER);
						Border cents4Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						cents4Field.setBorder(cents4Border1);
						cents4Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(cents4Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(cents4Slider.getMinimum(), Math.min(cents4Slider.getMaximum(), sliderVal));
						cents4Slider.setValue(sliderVal);
						gCB.setcents4((double) sliderVal / 1.0);
									updatecents4Label();
								} catch (NumberFormatException ex) {
									updatecents4Label();
								}
							}
						});
						updatecents4Label();
			
						Border cents4border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel cents4innerPanel = new JPanel();
			
						cents4innerPanel.setLayout(new BoxLayout(cents4innerPanel, BoxLayout.Y_AXIS));
						cents4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents4innerPanel.add(cents4Field);
						cents4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						cents4innerPanel.add(cents4Slider);
						cents4innerPanel.setBorder(cents4border2);
			
						frame.add(cents4innerPanel);
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new pitch_fourActionListener());
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class pitch_fourListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == pitch1Slider) {
			gCB.setpitch1((double) (pitch1Slider.getValue()/1.0));
				updatepitch1Label();
			}
			if(ce.getSource() == cents1Slider) {
			gCB.setcents1((double) (cents1Slider.getValue()/1.0));
				updatecents1Label();
			}
			if(ce.getSource() == pitch2Slider) {
			gCB.setpitch2((double) (pitch2Slider.getValue()/1.0));
				updatepitch2Label();
			}
			if(ce.getSource() == cents2Slider) {
			gCB.setcents2((double) (cents2Slider.getValue()/1.0));
				updatecents2Label();
			}
			if(ce.getSource() == pitch3Slider) {
			gCB.setpitch3((double) (pitch3Slider.getValue()/1.0));
				updatepitch3Label();
			}
			if(ce.getSource() == cents3Slider) {
			gCB.setcents3((double) (cents3Slider.getValue()/1.0));
				updatecents3Label();
			}
			if(ce.getSource() == pitch4Slider) {
			gCB.setpitch4((double) (pitch4Slider.getValue()/1.0));
				updatepitch4Label();
			}
			if(ce.getSource() == cents4Slider) {
			gCB.setcents4((double) (cents4Slider.getValue()/1.0));
				updatecents4Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class pitch_fourItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class pitch_fourActionListener implements java.awt.event.ActionListener {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatepitch1Label() {
		pitch1Field.setText("Pitch 1 " + String.format("%4.0f", gCB.getpitch1()));
		}
		private void updatecents1Label() {
		cents1Field.setText("Cents 1 " + String.format("%4.0f", gCB.getcents1()));
		}
		private void updatepitch2Label() {
		pitch2Field.setText("Pitch 2 " + String.format("%4.0f", gCB.getpitch2()));
		}
		private void updatecents2Label() {
		cents2Field.setText("Cents 2 " + String.format("%4.0f", gCB.getcents2()));
		}
		private void updatepitch3Label() {
		pitch3Field.setText("Pitch 3 " + String.format("%4.0f", gCB.getpitch3()));
		}
		private void updatecents3Label() {
		cents3Field.setText("Cents 3 " + String.format("%4.0f", gCB.getcents3()));
		}
		private void updatepitch4Label() {
		pitch4Field.setText("Pitch 4 " + String.format("%4.0f", gCB.getpitch4()));
		}
		private void updatecents4Label() {
		cents4Field.setText("Cents 4 " + String.format("%4.0f", gCB.getcents4()));
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
