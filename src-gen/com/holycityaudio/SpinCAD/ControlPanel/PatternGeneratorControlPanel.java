/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * PatternGeneratorControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.PatternGeneratorCADBlock;

@SuppressWarnings("unused")
public class PatternGeneratorControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private PatternGeneratorCADBlock gCB;
	// declare the controls
	FineControlSlider thresholdSlider;
	JTextField  thresholdField;
	FineControlSlider step1Slider;
	JTextField  step1Field;
	FineControlSlider step2Slider;
	JTextField  step2Field;
	FineControlSlider step3Slider;
	JTextField  step3Field;
	FineControlSlider step4Slider;
	JTextField  step4Field;
	FineControlSlider step5Slider;
	JTextField  step5Field;
	FineControlSlider step6Slider;
	JTextField  step6Field;
	FineControlSlider step7Slider;
	JTextField  step7Field;
	FineControlSlider step8Slider;
	JTextField  step8Field;

public PatternGeneratorControlPanel(PatternGeneratorCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "PatternGenerator");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					thresholdSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.5 * 100.0), (int) (gCB.getthreshold() * 100.0));
						thresholdSlider.addChangeListener(new PatternGeneratorListener());
						thresholdField = new JTextField();
						thresholdField.setHorizontalAlignment(JTextField.CENTER);
						Border thresholdBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						thresholdField.setBorder(thresholdBorder1);
						thresholdField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(thresholdField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 100.0);
						sliderVal = Math.max(thresholdSlider.getMinimum(), Math.min(thresholdSlider.getMaximum(), sliderVal));
						thresholdSlider.setValue(sliderVal);
						gCB.setthreshold((double) sliderVal / 100.0);
									updatethresholdLabel();
								} catch (NumberFormatException ex) {
									updatethresholdLabel();
								}
							}
						});
						updatethresholdLabel();
			
						Border thresholdborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel thresholdinnerPanel = new JPanel();
			
						thresholdinnerPanel.setLayout(new BoxLayout(thresholdinnerPanel, BoxLayout.Y_AXIS));
						thresholdinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						thresholdinnerPanel.add(thresholdField);
						thresholdinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						thresholdinnerPanel.add(thresholdSlider);
						thresholdinnerPanel.setBorder(thresholdborder2);
			
						frame.add(thresholdinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step1Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep1() * 1000.0));
						step1Slider.addChangeListener(new PatternGeneratorListener());
						step1Field = new JTextField();
						step1Field.setHorizontalAlignment(JTextField.CENTER);
						Border step1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step1Field.setBorder(step1Border1);
						step1Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step1Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step1Slider.getMinimum(), Math.min(step1Slider.getMaximum(), sliderVal));
						step1Slider.setValue(sliderVal);
						gCB.setstep1((double) sliderVal / 1000.0);
									updatestep1Label();
								} catch (NumberFormatException ex) {
									updatestep1Label();
								}
							}
						});
						updatestep1Label();
			
						Border step1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step1innerPanel = new JPanel();
			
						step1innerPanel.setLayout(new BoxLayout(step1innerPanel, BoxLayout.Y_AXIS));
						step1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step1innerPanel.add(step1Field);
						step1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step1innerPanel.add(step1Slider);
						step1innerPanel.setBorder(step1border2);
			
						frame.add(step1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step2Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep2() * 1000.0));
						step2Slider.addChangeListener(new PatternGeneratorListener());
						step2Field = new JTextField();
						step2Field.setHorizontalAlignment(JTextField.CENTER);
						Border step2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step2Field.setBorder(step2Border1);
						step2Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step2Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step2Slider.getMinimum(), Math.min(step2Slider.getMaximum(), sliderVal));
						step2Slider.setValue(sliderVal);
						gCB.setstep2((double) sliderVal / 1000.0);
									updatestep2Label();
								} catch (NumberFormatException ex) {
									updatestep2Label();
								}
							}
						});
						updatestep2Label();
			
						Border step2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step2innerPanel = new JPanel();
			
						step2innerPanel.setLayout(new BoxLayout(step2innerPanel, BoxLayout.Y_AXIS));
						step2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step2innerPanel.add(step2Field);
						step2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step2innerPanel.add(step2Slider);
						step2innerPanel.setBorder(step2border2);
			
						frame.add(step2innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step3Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep3() * 1000.0));
						step3Slider.addChangeListener(new PatternGeneratorListener());
						step3Field = new JTextField();
						step3Field.setHorizontalAlignment(JTextField.CENTER);
						Border step3Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step3Field.setBorder(step3Border1);
						step3Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step3Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step3Slider.getMinimum(), Math.min(step3Slider.getMaximum(), sliderVal));
						step3Slider.setValue(sliderVal);
						gCB.setstep3((double) sliderVal / 1000.0);
									updatestep3Label();
								} catch (NumberFormatException ex) {
									updatestep3Label();
								}
							}
						});
						updatestep3Label();
			
						Border step3border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step3innerPanel = new JPanel();
			
						step3innerPanel.setLayout(new BoxLayout(step3innerPanel, BoxLayout.Y_AXIS));
						step3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step3innerPanel.add(step3Field);
						step3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step3innerPanel.add(step3Slider);
						step3innerPanel.setBorder(step3border2);
			
						frame.add(step3innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step4Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep4() * 1000.0));
						step4Slider.addChangeListener(new PatternGeneratorListener());
						step4Field = new JTextField();
						step4Field.setHorizontalAlignment(JTextField.CENTER);
						Border step4Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step4Field.setBorder(step4Border1);
						step4Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step4Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step4Slider.getMinimum(), Math.min(step4Slider.getMaximum(), sliderVal));
						step4Slider.setValue(sliderVal);
						gCB.setstep4((double) sliderVal / 1000.0);
									updatestep4Label();
								} catch (NumberFormatException ex) {
									updatestep4Label();
								}
							}
						});
						updatestep4Label();
			
						Border step4border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step4innerPanel = new JPanel();
			
						step4innerPanel.setLayout(new BoxLayout(step4innerPanel, BoxLayout.Y_AXIS));
						step4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step4innerPanel.add(step4Field);
						step4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step4innerPanel.add(step4Slider);
						step4innerPanel.setBorder(step4border2);
			
						frame.add(step4innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step5Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep5() * 1000.0));
						step5Slider.addChangeListener(new PatternGeneratorListener());
						step5Field = new JTextField();
						step5Field.setHorizontalAlignment(JTextField.CENTER);
						Border step5Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step5Field.setBorder(step5Border1);
						step5Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step5Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step5Slider.getMinimum(), Math.min(step5Slider.getMaximum(), sliderVal));
						step5Slider.setValue(sliderVal);
						gCB.setstep5((double) sliderVal / 1000.0);
									updatestep5Label();
								} catch (NumberFormatException ex) {
									updatestep5Label();
								}
							}
						});
						updatestep5Label();
			
						Border step5border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step5innerPanel = new JPanel();
			
						step5innerPanel.setLayout(new BoxLayout(step5innerPanel, BoxLayout.Y_AXIS));
						step5innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step5innerPanel.add(step5Field);
						step5innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step5innerPanel.add(step5Slider);
						step5innerPanel.setBorder(step5border2);
			
						frame.add(step5innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step6Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep6() * 1000.0));
						step6Slider.addChangeListener(new PatternGeneratorListener());
						step6Field = new JTextField();
						step6Field.setHorizontalAlignment(JTextField.CENTER);
						Border step6Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step6Field.setBorder(step6Border1);
						step6Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step6Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step6Slider.getMinimum(), Math.min(step6Slider.getMaximum(), sliderVal));
						step6Slider.setValue(sliderVal);
						gCB.setstep6((double) sliderVal / 1000.0);
									updatestep6Label();
								} catch (NumberFormatException ex) {
									updatestep6Label();
								}
							}
						});
						updatestep6Label();
			
						Border step6border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step6innerPanel = new JPanel();
			
						step6innerPanel.setLayout(new BoxLayout(step6innerPanel, BoxLayout.Y_AXIS));
						step6innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step6innerPanel.add(step6Field);
						step6innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step6innerPanel.add(step6Slider);
						step6innerPanel.setBorder(step6border2);
			
						frame.add(step6innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step7Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep7() * 1000.0));
						step7Slider.addChangeListener(new PatternGeneratorListener());
						step7Field = new JTextField();
						step7Field.setHorizontalAlignment(JTextField.CENTER);
						Border step7Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step7Field.setBorder(step7Border1);
						step7Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step7Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step7Slider.getMinimum(), Math.min(step7Slider.getMaximum(), sliderVal));
						step7Slider.setValue(sliderVal);
						gCB.setstep7((double) sliderVal / 1000.0);
									updatestep7Label();
								} catch (NumberFormatException ex) {
									updatestep7Label();
								}
							}
						});
						updatestep7Label();
			
						Border step7border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step7innerPanel = new JPanel();
			
						step7innerPanel.setLayout(new BoxLayout(step7innerPanel, BoxLayout.Y_AXIS));
						step7innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step7innerPanel.add(step7Field);
						step7innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step7innerPanel.add(step7Slider);
						step7innerPanel.setBorder(step7border2);
			
						frame.add(step7innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					step8Slider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.999 * 1000.0), (int) (gCB.getstep8() * 1000.0));
						step8Slider.addChangeListener(new PatternGeneratorListener());
						step8Field = new JTextField();
						step8Field.setHorizontalAlignment(JTextField.CENTER);
						Border step8Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						step8Field.setBorder(step8Border1);
						step8Field.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(step8Field.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1000.0);
						sliderVal = Math.max(step8Slider.getMinimum(), Math.min(step8Slider.getMaximum(), sliderVal));
						step8Slider.setValue(sliderVal);
						gCB.setstep8((double) sliderVal / 1000.0);
									updatestep8Label();
								} catch (NumberFormatException ex) {
									updatestep8Label();
								}
							}
						});
						updatestep8Label();
			
						Border step8border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel step8innerPanel = new JPanel();
			
						step8innerPanel.setLayout(new BoxLayout(step8innerPanel, BoxLayout.Y_AXIS));
						step8innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step8innerPanel.add(step8Field);
						step8innerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						step8innerPanel.add(step8Slider);
						step8innerPanel.setBorder(step8border2);
			
						frame.add(step8innerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class PatternGeneratorListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == thresholdSlider) {
			gCB.setthreshold((double) (thresholdSlider.getValue()/100.0));
				updatethresholdLabel();
			}
			if(ce.getSource() == step1Slider) {
			gCB.setstep1((double) (step1Slider.getValue()/1000.0));
				updatestep1Label();
			}
			if(ce.getSource() == step2Slider) {
			gCB.setstep2((double) (step2Slider.getValue()/1000.0));
				updatestep2Label();
			}
			if(ce.getSource() == step3Slider) {
			gCB.setstep3((double) (step3Slider.getValue()/1000.0));
				updatestep3Label();
			}
			if(ce.getSource() == step4Slider) {
			gCB.setstep4((double) (step4Slider.getValue()/1000.0));
				updatestep4Label();
			}
			if(ce.getSource() == step5Slider) {
			gCB.setstep5((double) (step5Slider.getValue()/1000.0));
				updatestep5Label();
			}
			if(ce.getSource() == step6Slider) {
			gCB.setstep6((double) (step6Slider.getValue()/1000.0));
				updatestep6Label();
			}
			if(ce.getSource() == step7Slider) {
			gCB.setstep7((double) (step7Slider.getValue()/1000.0));
				updatestep7Label();
			}
			if(ce.getSource() == step8Slider) {
			gCB.setstep8((double) (step8Slider.getValue()/1000.0));
				updatestep8Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class PatternGeneratorItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class PatternGeneratorActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatethresholdLabel() {
		thresholdField.setText("Threshold " + String.format("%4.2f", gCB.getthreshold()));		
		}		
		private void updatestep1Label() {
		step1Field.setText("Step 1 " + String.format("%4.3f", gCB.getstep1()));		
		}		
		private void updatestep2Label() {
		step2Field.setText("Step 2 " + String.format("%4.3f", gCB.getstep2()));		
		}		
		private void updatestep3Label() {
		step3Field.setText("Step 3 " + String.format("%4.3f", gCB.getstep3()));		
		}		
		private void updatestep4Label() {
		step4Field.setText("Step 4 " + String.format("%4.3f", gCB.getstep4()));		
		}		
		private void updatestep5Label() {
		step5Field.setText("Step 5 " + String.format("%4.3f", gCB.getstep5()));		
		}		
		private void updatestep6Label() {
		step6Field.setText("Step 6 " + String.format("%4.3f", gCB.getstep6()));		
		}		
		private void updatestep7Label() {
		step7Field.setText("Step 7 " + String.format("%4.3f", gCB.getstep7()));		
		}		
		private void updatestep8Label() {
		step8Field.setText("Step 8 " + String.format("%4.3f", gCB.getstep8()));		
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
