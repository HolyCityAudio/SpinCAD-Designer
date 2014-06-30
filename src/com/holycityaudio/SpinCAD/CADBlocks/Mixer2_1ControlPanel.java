/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2014 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code. 
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

import java.awt.Point;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class Mixer2_1ControlPanel {
	private JFrame frame;

	JSlider gain1Slider;
	JLabel gain1Label;
	JSlider gain2Slider;
	JLabel gain2Label;

	private Mixer2_1CADBlock spbMix;

	public Mixer2_1ControlPanel(Mixer2_1CADBlock b) {

		spbMix = b;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Mixer 2-1");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gain1Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain1() * 100));
				gain1Slider.addChangeListener(new volumeSliderListener());
				gain1Label = new JLabel();
				
				gain2Slider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain2() * 100));
				gain2Slider.addChangeListener(new volumeSliderListener());
				gain2Label = new JLabel();
				
				frame.getContentPane().add(gain1Label);
				frame.getContentPane().add(gain1Slider);
				frame.getContentPane().add(gain2Label);
				frame.getContentPane().add(gain2Slider);
				
				updateGainLabels();
				frame.setVisible(true);	
				frame.pack();
				frame.setLocation(new Point(spbMix.getX() + 200, spbMix.getY() + 150));
				frame.setResizable(false);
			}
		});
	}

	class volumeSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gain1Slider) {
				spbMix.setGain1((double) gain1Slider.getValue()/100.0);
				updateGainLabels();
			}
			else if(ce.getSource() == gain2Slider) {
				spbMix.setGain2((double) gain2Slider.getValue()/100.0);
				updateGainLabels();
			}
		}
	}

	public void updateGainLabels() {
		gain1Label.setText("Gain " + String.format("%4.2f", spbMix.getGain1()));		
		gain2Label.setText("Gain " + String.format("%4.2f", spbMix.getGain2()));		
	}
}
