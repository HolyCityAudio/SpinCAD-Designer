/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * servoControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.servoCADBlock;

@SuppressWarnings("unused")
public class servoControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private servoCADBlock gCB;
	// declare the controls
	JSlider inputGainSlider;
	JLabel  inputGainLabel;	
	JSlider fbkGainSlider;
	JLabel  fbkGainLabel;	
	JSlider servoGainSlider;
	JLabel  servoGainLabel;	
	JSlider freqSlider;
	JLabel  freqLabel;	
	JSlider tap1RatioSlider;
	JLabel  tap1RatioLabel;	
	private JComboBox <String> lfoSelComboBox; 

public servoControlPanel(servoCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Servo Flanger");
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
						inputGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getinputGain())));
						inputGainSlider.addChangeListener(new servoListener());
						inputGainLabel = new JLabel();
						Border inputGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputGainLabel.setBorder(inputGainBorder1);
						updateinputGainLabel();
						
						Border inputGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel inputGaininnerPanel = new JPanel();
							
						inputGaininnerPanel.setLayout(new BoxLayout(inputGaininnerPanel, BoxLayout.Y_AXIS));
						inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						inputGaininnerPanel.add(inputGainLabel);
						inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						inputGaininnerPanel.add(inputGainSlider);		
						inputGaininnerPanel.setBorder(inputGainborder2);
			
						frame.add(inputGaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						fbkGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getfbkGain())));
						fbkGainSlider.addChangeListener(new servoListener());
						fbkGainLabel = new JLabel();
						Border fbkGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						fbkGainLabel.setBorder(fbkGainBorder1);
						updatefbkGainLabel();
						
						Border fbkGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel fbkGaininnerPanel = new JPanel();
							
						fbkGaininnerPanel.setLayout(new BoxLayout(fbkGaininnerPanel, BoxLayout.Y_AXIS));
						fbkGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						fbkGaininnerPanel.add(fbkGainLabel);
						fbkGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						fbkGaininnerPanel.add(fbkGainSlider);		
						fbkGaininnerPanel.setBorder(fbkGainborder2);
			
						frame.add(fbkGaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					servoGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.49 * 100.0), (int) (gCB.getservoGain() * 100.0));
						servoGainSlider.addChangeListener(new servoListener());
						servoGainLabel = new JLabel();
						Border servoGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						servoGainLabel.setBorder(servoGainBorder1);
						updateservoGainLabel();
						
						Border servoGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel servoGaininnerPanel = new JPanel();
							
						servoGaininnerPanel.setLayout(new BoxLayout(servoGaininnerPanel, BoxLayout.Y_AXIS));
						servoGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						servoGaininnerPanel.add(servoGainLabel);
						servoGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						servoGaininnerPanel.add(servoGainSlider);		
						servoGaininnerPanel.setBorder(servoGainborder2);
			
						frame.add(servoGaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						freqSlider = SpinCADBlock.LogSlider(500,7500,gCB.getfreq(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						freqSlider.addChangeListener(new servoListener());
						freqLabel = new JLabel();
						Border freqBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						freqLabel.setBorder(freqBorder1);
						updatefreqLabel();
						
						Border freqborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel freqinnerPanel = new JPanel();
							
						freqinnerPanel.setLayout(new BoxLayout(freqinnerPanel, BoxLayout.Y_AXIS));
						freqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						freqinnerPanel.add(freqLabel);
						freqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						freqinnerPanel.add(freqSlider);		
						freqinnerPanel.setBorder(freqborder2);
			
						frame.add(freqinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					tap1RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (0.05 * 1000.0), (int) (gCB.gettap1Ratio() * 1000.0));
						tap1RatioSlider.addChangeListener(new servoListener());
						tap1RatioLabel = new JLabel();
						Border tap1RatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap1RatioLabel.setBorder(tap1RatioBorder1);
						updatetap1RatioLabel();
						
						Border tap1Ratioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap1RatioinnerPanel = new JPanel();
							
						tap1RatioinnerPanel.setLayout(new BoxLayout(tap1RatioinnerPanel, BoxLayout.Y_AXIS));
						tap1RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap1RatioinnerPanel.add(tap1RatioLabel);
						tap1RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap1RatioinnerPanel.add(tap1RatioSlider);		
						tap1RatioinnerPanel.setBorder(tap1Ratioborder2);
			
						frame.add(tap1RatioinnerPanel);
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new servoActionListener());
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setAlwaysOnTop(true);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class servoListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/1.0));			    					
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/1.0));			    					
				updatefbkGainLabel();
			}
			if(ce.getSource() == servoGainSlider) {
			gCB.setservoGain((double) (servoGainSlider.getValue()/100.0));
				updateservoGainLabel();
			}
			if(ce.getSource() == freqSlider) {
			gCB.setfreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(freqSlider.getValue()), 100.0)));
				updatefreqLabel();
			}
			if(ce.getSource() == tap1RatioSlider) {
			gCB.settap1Ratio((double) (tap1RatioSlider.getValue()/1000.0));
				updatetap1RatioLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class servoItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class servoActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updateinputGainLabel() {
		inputGainLabel.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));		
		}		
		private void updatefbkGainLabel() {
		fbkGainLabel.setText("Feedback Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));		
		}		
		private void updateservoGainLabel() {
		servoGainLabel.setText("Servo Gain " + String.format("%4.2f", gCB.getservoGain()));		
		}		
		private void updatefreqLabel() {
		freqLabel.setText("Low_Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getfreq())) + " Hz");		
		}		
		private void updatetap1RatioLabel() {
		tap1RatioLabel.setText("Tap Time Ratio " + String.format("%4.3f", gCB.gettap1Ratio()));		
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
