/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ControlMixer_2_to_1ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.ControlMixer_2_to_1CADBlock;

@SuppressWarnings("unused")
public class ControlMixer_2_to_1ControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private ControlMixer_2_to_1CADBlock gCB;
	// declare the controls
	JSlider gain1Slider;
	JLabel  gain1Label;	
	JSlider gain2Slider;
	JLabel  gain2Label;	

public ControlMixer_2_to_1ControlPanel(ControlMixer_2_to_1CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Mixer 2:1");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					gain1Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.getgain1() * 1000.0));
						gain1Slider.addChangeListener(new ControlMixer_2_to_1Listener());
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
					gain2Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.getgain2() * 1000.0));
						gain2Slider.addChangeListener(new ControlMixer_2_to_1Listener());
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
		class ControlMixer_2_to_1Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gain1Slider) {
			gCB.setgain1((double) (gain1Slider.getValue()/1000.0));
				updategain1Label();
			}
			if(ce.getSource() == gain2Slider) {
			gCB.setgain2((double) (gain2Slider.getValue()/1000.0));
				updategain2Label();
			}
			}
		}

		// add item state changed listener for Checkbox
		class ControlMixer_2_to_1ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ControlMixer_2_to_1ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategain1Label() {
		gain1Label.setText("Gain 1 " + String.format("%4.3f", gCB.getgain1()));		
		}		
		private void updategain2Label() {
		gain2Label.setText("Gain 2 " + String.format("%4.3f", gCB.getgain2()));		
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
