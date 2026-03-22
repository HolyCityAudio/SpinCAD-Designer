/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ControlPanelTestControlPanel.java
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
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.spinCADControlPanel;
import com.holycityaudio.SpinCAD.CADBlocks.ControlPanelTestCADBlock;

@SuppressWarnings("unused")
public class ControlPanelTestControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private ControlPanelTestCADBlock gCB;
	// declare the controls
	JSlider pitchCoeffSlider;
	JLabel  pitchCoeffLabel;	
	JSlider pitchCoeff1Slider;
	JLabel  pitchCoeff1Label;	
	JSlider pitchCoeff2Slider;
	JLabel  pitchCoeff2Label;	
	private JComboBox <String> controlRangeComboBox; 
	private JComboBox <String> lfoSelComboBox; 
	private JComboBox <String> lfoWidthComboBox; 

public ControlPanelTestControlPanel(ControlPanelTestCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				gCB.controlPanelFrame = frame;
				frame.setTitle("Control Panel Test");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitchCoeffSlider = new JSlider(JSlider.HORIZONTAL, (int)(-16384 * 1.0),(int) (32767 * 1.0), (int) (gCB.getpitchCoeff() * 1.0));
						pitchCoeffSlider.addChangeListener(new ControlPanelTestListener());
						pitchCoeffLabel = new JLabel();
						Border pitchCoeffBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitchCoeffLabel.setBorder(pitchCoeffBorder1);
						updatepitchCoeffLabel();
						
						Border pitchCoeffborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitchCoeffinnerPanel = new JPanel();
							
						pitchCoeffinnerPanel.setLayout(new BoxLayout(pitchCoeffinnerPanel, BoxLayout.Y_AXIS));
						pitchCoeffinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeffinnerPanel.add(pitchCoeffLabel);
						pitchCoeffinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeffinnerPanel.add(pitchCoeffSlider);		
						pitchCoeffinnerPanel.setBorder(pitchCoeffborder2);
			
						frame.add(pitchCoeffinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitchCoeff1Slider = new JSlider(JSlider.HORIZONTAL, (int)(-16384 * 1.0),(int) (32767 * 1.0), (int) (gCB.getpitchCoeff1() * 1.0));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						pitchCoeff1Slider.addChangeListener(new ControlPanelTestListener());
						pitchCoeff1Label = new JLabel();
						Border pitchCoeff1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitchCoeff1Label.setBorder(pitchCoeff1Border1);
						updatepitchCoeff1Label();
						
						Border pitchCoeff1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitchCoeff1innerPanel = new JPanel();
							
						pitchCoeff1innerPanel.setLayout(new BoxLayout(pitchCoeff1innerPanel, BoxLayout.Y_AXIS));
						pitchCoeff1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeff1innerPanel.add(pitchCoeff1Label);
						pitchCoeff1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeff1innerPanel.add(pitchCoeff1Slider);		
						pitchCoeff1innerPanel.setBorder(pitchCoeff1border2);
			
						frame.add(pitchCoeff1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					pitchCoeff2Slider = new JSlider(JSlider.HORIZONTAL, (int)(-16384 * 1.0),(int) (32767 * 1.0), (int) ((gCB.getpitchCoeff2()) * 1.0));
						pitchCoeff2Slider.addChangeListener(new ControlPanelTestListener());
						pitchCoeff2Label = new JLabel();
						Border pitchCoeff2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitchCoeff2Label.setBorder(pitchCoeff2Border1);
						updatepitchCoeff2Label();
						
						Border pitchCoeff2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitchCoeff2innerPanel = new JPanel();
							
						pitchCoeff2innerPanel.setLayout(new BoxLayout(pitchCoeff2innerPanel, BoxLayout.Y_AXIS));
						pitchCoeff2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeff2innerPanel.add(pitchCoeff2Label);
						pitchCoeff2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeff2innerPanel.add(pitchCoeff2Slider);		
						pitchCoeff2innerPanel.setBorder(pitchCoeff2border2);
			
						frame.add(pitchCoeff2innerPanel);
				controlRangeComboBox = new JComboBox <String> ();
				controlRangeComboBox.addItem("0 -> +1");
				controlRangeComboBox.addItem("-1 -> +1");
				controlRangeComboBox.setSelectedIndex(gCB.getcontrolRange());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(controlRangeComboBox);
				controlRangeComboBox.addActionListener(new ControlPanelTestActionListener());
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new ControlPanelTestActionListener());
				lfoWidthComboBox = new JComboBox <String> ();
				lfoWidthComboBox.addItem("4096");
				lfoWidthComboBox.addItem("2048");
			lfoWidthComboBox.addItem("1024");
			lfoWidthComboBox.addItem("512");
				lfoWidthComboBox.setSelectedIndex(gCB.getlfoWidth());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoWidthComboBox);
				lfoWidthComboBox.addActionListener(new ControlPanelTestActionListener());
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getControlPanelLocation(100, 100));
				frame.setAlwaysOnTop(true);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class ControlPanelTestListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == pitchCoeffSlider) {
			gCB.setpitchCoeff((double) (pitchCoeffSlider.getValue()/1.0));
				updatepitchCoeffLabel();
			}
			if(ce.getSource() == pitchCoeff1Slider) {
			gCB.setpitchCoeff1((double) (pitchCoeff1Slider.getValue()/1.0));			    					
				updatepitchCoeff1Label();
			}
			if(ce.getSource() == pitchCoeff2Slider) {
			gCB.setpitchCoeff2((double) (pitchCoeff2Slider.getValue()/1.0));			    					
				updatepitchCoeff2Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class ControlPanelTestItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ControlPanelTestActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == controlRangeComboBox) {
				gCB.setcontrolRange((controlRangeComboBox.getSelectedIndex()));
			}
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			if(arg0.getSource() == lfoWidthComboBox) {
				gCB.setlfoWidth((lfoWidthComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatepitchCoeffLabel() {
		pitchCoeffLabel.setText("Pitch Coefficient " + String.format("%4.0f", gCB.getpitchCoeff()));		
		}		
		private void updatepitchCoeff1Label() {
		pitchCoeff1Label.setText("Pitch Coefficient " + String.format("%4.0f", (1000 * gCB.getpitchCoeff1())/ElmProgram.getSamplerate()));		
		}		
		private void updatepitchCoeff2Label() {
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
