/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * control_smootherControlPanel.java
 * Copyright (C)2013 - Gary Worsham 
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
package com.holycityaudio.SpinCAD.CADBlocks;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ItemEvent;

import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JCheckBox;


public class control_smootherControlPanel {
	private JFrame frame;

	private control_smootherCADBlockA gCB;
	// declare the controls
	JSlider filtSlider;
	JLabel  filtLabel;	

	public control_smootherControlPanel(control_smootherCADBlockA genericCADBlock) {

		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Smoother");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				// filtSlider = new JSlider(JSlider.HORIZONTAL, (int)(0),(int) (100), (int) (gCB.getfilt() * 100000.0));
				filtSlider = new JSlider(JSlider.HORIZONTAL, (int)(-29),(int) (100), gCB.logvalToSlider(gCB.filtToFreq(gCB.getfilt()), 100.0));
				filtSlider.addChangeListener(new control_smootherSliderListener());
				filtLabel = new JLabel();
				updatefiltLabel();
				frame.getContentPane().add(filtLabel);
				frame.getContentPane().add(filtSlider);		

				frame.setVisible(true);		
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setAlwaysOnTop(true);
			}
		});
	}

	// add change listener for Sliders 
	class control_smootherSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == filtSlider) {
				gCB.setfilt(gCB.freqToFilt(gCB.sliderToLogval(filtSlider.getValue(), 100.0)));
				updatefiltLabel();
			}
		}
	}
	// add item listener for Bool (CheckbBox) 
	class control_smootherItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
		}
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			// TODO Auto-generated method stub
		}
	}

	private void updatefiltLabel() {
				filtLabel.setText(String.format("%3.2f", gCB.filtToFreq(gCB.getfilt())) + " Hz");		
			}
}
