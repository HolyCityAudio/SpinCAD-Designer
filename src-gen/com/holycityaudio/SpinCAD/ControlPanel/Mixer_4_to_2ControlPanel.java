/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Mixer_4_to_2ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.Mixer_4_to_2CADBlock;

@SuppressWarnings("unused")
public class Mixer_4_to_2ControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private Mixer_4_to_2CADBlock gCB;
	// declare the controls
	JSlider gain1Slider;
	JLabel  gain1Label;	
	JSlider gain2Slider;
	JLabel  gain2Label;	
	JSlider gain3Slider;
	JLabel  gain3Label;	
	JSlider gain4Slider;
	JLabel  gain4Label;	

public Mixer_4_to_2ControlPanel(Mixer_4_to_2CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Mixer 4:2");
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
						gain1Slider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain1())));
						gain1Slider.addChangeListener(new Mixer_4_to_2Listener());
						gain1Label = new JLabel();
						Border gain1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gain1Label.setBorder(gain1Border1);
						updategain1Label();
						
						Border gain1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel gain1innerPanel = new JPanel();
							
						gain1innerPanel.setLayout(new BoxLayout(gain1innerPanel, BoxLayout.Y_AXIS));
						gain1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain1innerPanel.add(gain1Label);
						gain1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain1innerPanel.add(gain1Slider);		
						gain1innerPanel.setBorder(gain1border2);
			
						frame.add(gain1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						gain2Slider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain2())));
						gain2Slider.addChangeListener(new Mixer_4_to_2Listener());
						gain2Label = new JLabel();
						Border gain2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gain2Label.setBorder(gain2Border1);
						updategain2Label();
						
						Border gain2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel gain2innerPanel = new JPanel();
							
						gain2innerPanel.setLayout(new BoxLayout(gain2innerPanel, BoxLayout.Y_AXIS));
						gain2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain2innerPanel.add(gain2Label);
						gain2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain2innerPanel.add(gain2Slider);		
						gain2innerPanel.setBorder(gain2border2);
			
						frame.add(gain2innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						gain3Slider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain3())));
						gain3Slider.addChangeListener(new Mixer_4_to_2Listener());
						gain3Label = new JLabel();
						Border gain3Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gain3Label.setBorder(gain3Border1);
						updategain3Label();
						
						Border gain3border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel gain3innerPanel = new JPanel();
							
						gain3innerPanel.setLayout(new BoxLayout(gain3innerPanel, BoxLayout.Y_AXIS));
						gain3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain3innerPanel.add(gain3Label);
						gain3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain3innerPanel.add(gain3Slider);		
						gain3innerPanel.setBorder(gain3border2);
			
						frame.add(gain3innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						gain4Slider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain4())));
						gain4Slider.addChangeListener(new Mixer_4_to_2Listener());
						gain4Label = new JLabel();
						Border gain4Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						gain4Label.setBorder(gain4Border1);
						updategain4Label();
						
						Border gain4border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel gain4innerPanel = new JPanel();
							
						gain4innerPanel.setLayout(new BoxLayout(gain4innerPanel, BoxLayout.Y_AXIS));
						gain4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain4innerPanel.add(gain4Label);
						gain4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						gain4innerPanel.add(gain4Slider);		
						gain4innerPanel.setBorder(gain4border2);
			
						frame.add(gain4innerPanel);
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
		class Mixer_4_to_2Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gain1Slider) {
			gCB.setgain1((double) (gain1Slider.getValue()/1.0));			    					
				updategain1Label();
			}
			if(ce.getSource() == gain2Slider) {
			gCB.setgain2((double) (gain2Slider.getValue()/1.0));			    					
				updategain2Label();
			}
			if(ce.getSource() == gain3Slider) {
			gCB.setgain3((double) (gain3Slider.getValue()/1.0));			    					
				updategain3Label();
			}
			if(ce.getSource() == gain4Slider) {
			gCB.setgain4((double) (gain4Slider.getValue()/1.0));			    					
				updategain4Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class Mixer_4_to_2ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class Mixer_4_to_2ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategain1Label() {
		gain1Label.setText("Input Gain 1 " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain1()))));		
		}		
		private void updategain2Label() {
		gain2Label.setText("Input Gain 2 " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain2()))));		
		}		
		private void updategain3Label() {
		gain3Label.setText("Input Gain 3 " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain3()))));		
		}		
		private void updategain4Label() {
		gain4Label.setText("Input Gain 4 " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain4()))));		
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
