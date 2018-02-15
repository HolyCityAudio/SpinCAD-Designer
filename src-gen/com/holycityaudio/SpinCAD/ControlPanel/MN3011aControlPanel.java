/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * MN3011aControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.MN3011aCADBlock;

@SuppressWarnings("unused")
public class MN3011aControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private MN3011aCADBlock gCB;
	// declare the controls
	JSlider inputGainSlider;
	JLabel  inputGainLabel;	
	JSlider fbkGainSlider;
	JLabel  fbkGainLabel;	
	JSlider delayLengthSlider;
	JLabel  delayLengthLabel;	
	JSlider tap1GainSlider;
	JLabel  tap1GainLabel;	
	JSlider tap2GainSlider;
	JLabel  tap2GainLabel;	
	JSlider tap3GainSlider;
	JLabel  tap3GainLabel;	
	JSlider tap4GainSlider;
	JLabel  tap4GainLabel;	
	JSlider tap5GainSlider;
	JLabel  tap5GainLabel;	
	JSlider tap6GainSlider;
	JLabel  tap6GainLabel;	

public MN3011aControlPanel(MN3011aCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("MN3011");
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
						inputGainSlider.addChangeListener(new MN3011aListener());
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
						fbkGainSlider.addChangeListener(new MN3011aListener());
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
					delayLengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (32767 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						delayLengthSlider.addChangeListener(new MN3011aListener());
						delayLengthLabel = new JLabel();
						Border delayLengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						delayLengthLabel.setBorder(delayLengthBorder1);
						updatedelayLengthLabel();
						
						Border delayLengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel delayLengthinnerPanel = new JPanel();
							
						delayLengthinnerPanel.setLayout(new BoxLayout(delayLengthinnerPanel, BoxLayout.Y_AXIS));
						delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						delayLengthinnerPanel.add(delayLengthLabel);
						delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						delayLengthinnerPanel.add(delayLengthSlider);		
						delayLengthinnerPanel.setBorder(delayLengthborder2);
			
						frame.add(delayLengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap1GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap1Gain())));
						tap1GainSlider.addChangeListener(new MN3011aListener());
						tap1GainLabel = new JLabel();
						Border tap1GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap1GainLabel.setBorder(tap1GainBorder1);
						updatetap1GainLabel();
						
						Border tap1Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap1GaininnerPanel = new JPanel();
							
						tap1GaininnerPanel.setLayout(new BoxLayout(tap1GaininnerPanel, BoxLayout.Y_AXIS));
						tap1GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap1GaininnerPanel.add(tap1GainLabel);
						tap1GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap1GaininnerPanel.add(tap1GainSlider);		
						tap1GaininnerPanel.setBorder(tap1Gainborder2);
			
						frame.add(tap1GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap2GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap2Gain())));
						tap2GainSlider.addChangeListener(new MN3011aListener());
						tap2GainLabel = new JLabel();
						Border tap2GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap2GainLabel.setBorder(tap2GainBorder1);
						updatetap2GainLabel();
						
						Border tap2Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap2GaininnerPanel = new JPanel();
							
						tap2GaininnerPanel.setLayout(new BoxLayout(tap2GaininnerPanel, BoxLayout.Y_AXIS));
						tap2GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap2GaininnerPanel.add(tap2GainLabel);
						tap2GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap2GaininnerPanel.add(tap2GainSlider);		
						tap2GaininnerPanel.setBorder(tap2Gainborder2);
			
						frame.add(tap2GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap3GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap3Gain())));
						tap3GainSlider.addChangeListener(new MN3011aListener());
						tap3GainLabel = new JLabel();
						Border tap3GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap3GainLabel.setBorder(tap3GainBorder1);
						updatetap3GainLabel();
						
						Border tap3Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap3GaininnerPanel = new JPanel();
							
						tap3GaininnerPanel.setLayout(new BoxLayout(tap3GaininnerPanel, BoxLayout.Y_AXIS));
						tap3GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap3GaininnerPanel.add(tap3GainLabel);
						tap3GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap3GaininnerPanel.add(tap3GainSlider);		
						tap3GaininnerPanel.setBorder(tap3Gainborder2);
			
						frame.add(tap3GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap4GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap4Gain())));
						tap4GainSlider.addChangeListener(new MN3011aListener());
						tap4GainLabel = new JLabel();
						Border tap4GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap4GainLabel.setBorder(tap4GainBorder1);
						updatetap4GainLabel();
						
						Border tap4Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap4GaininnerPanel = new JPanel();
							
						tap4GaininnerPanel.setLayout(new BoxLayout(tap4GaininnerPanel, BoxLayout.Y_AXIS));
						tap4GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap4GaininnerPanel.add(tap4GainLabel);
						tap4GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap4GaininnerPanel.add(tap4GainSlider);		
						tap4GaininnerPanel.setBorder(tap4Gainborder2);
			
						frame.add(tap4GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap5GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap5Gain())));
						tap5GainSlider.addChangeListener(new MN3011aListener());
						tap5GainLabel = new JLabel();
						Border tap5GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap5GainLabel.setBorder(tap5GainBorder1);
						updatetap5GainLabel();
						
						Border tap5Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap5GaininnerPanel = new JPanel();
							
						tap5GaininnerPanel.setLayout(new BoxLayout(tap5GaininnerPanel, BoxLayout.Y_AXIS));
						tap5GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap5GaininnerPanel.add(tap5GainLabel);
						tap5GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap5GaininnerPanel.add(tap5GainSlider);		
						tap5GaininnerPanel.setBorder(tap5Gainborder2);
			
						frame.add(tap5GaininnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						tap6GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap6Gain())));
						tap6GainSlider.addChangeListener(new MN3011aListener());
						tap6GainLabel = new JLabel();
						Border tap6GainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						tap6GainLabel.setBorder(tap6GainBorder1);
						updatetap6GainLabel();
						
						Border tap6Gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel tap6GaininnerPanel = new JPanel();
							
						tap6GaininnerPanel.setLayout(new BoxLayout(tap6GaininnerPanel, BoxLayout.Y_AXIS));
						tap6GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap6GaininnerPanel.add(tap6GainLabel);
						tap6GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						tap6GaininnerPanel.add(tap6GainSlider);		
						tap6GaininnerPanel.setBorder(tap6Gainborder2);
			
						frame.add(tap6GaininnerPanel);
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
		class MN3011aListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/1.0));			    					
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/1.0));			    					
				updatefbkGainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));			    					
				updatedelayLengthLabel();
			}
			if(ce.getSource() == tap1GainSlider) {
			gCB.settap1Gain((double) (tap1GainSlider.getValue()/1.0));			    					
				updatetap1GainLabel();
			}
			if(ce.getSource() == tap2GainSlider) {
			gCB.settap2Gain((double) (tap2GainSlider.getValue()/1.0));			    					
				updatetap2GainLabel();
			}
			if(ce.getSource() == tap3GainSlider) {
			gCB.settap3Gain((double) (tap3GainSlider.getValue()/1.0));			    					
				updatetap3GainLabel();
			}
			if(ce.getSource() == tap4GainSlider) {
			gCB.settap4Gain((double) (tap4GainSlider.getValue()/1.0));			    					
				updatetap4GainLabel();
			}
			if(ce.getSource() == tap5GainSlider) {
			gCB.settap5Gain((double) (tap5GainSlider.getValue()/1.0));			    					
				updatetap5GainLabel();
			}
			if(ce.getSource() == tap6GainSlider) {
			gCB.settap6Gain((double) (tap6GainSlider.getValue()/1.0));			    					
				updatetap6GainLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class MN3011aItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class MN3011aActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainLabel() {
		inputGainLabel.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));		
		}		
		private void updatefbkGainLabel() {
		fbkGainLabel.setText("Feedback Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthLabel.setText("Delay Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));		
		}		
		private void updatetap1GainLabel() {
		tap1GainLabel.setText("Tap 1 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap1Gain()))));		
		}		
		private void updatetap2GainLabel() {
		tap2GainLabel.setText("Tap 2 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap2Gain()))));		
		}		
		private void updatetap3GainLabel() {
		tap3GainLabel.setText("Tap 3 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap3Gain()))));		
		}		
		private void updatetap4GainLabel() {
		tap4GainLabel.setText("Tap 4 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap4Gain()))));		
		}		
		private void updatetap5GainLabel() {
		tap5GainLabel.setText("Tap 5 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap5Gain()))));		
		}		
		private void updatetap6GainLabel() {
		tap6GainLabel.setText("Tap 6 Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap6Gain()))));		
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
