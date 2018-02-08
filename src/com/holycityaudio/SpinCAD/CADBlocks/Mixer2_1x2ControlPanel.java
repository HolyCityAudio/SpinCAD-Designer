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

class Mixer2_1x2ControlPanel {
	private JFrame frame;

	JSlider gain1aSlider;
	JLabel gain1aLabel;
	JSlider gain1bSlider;
	JLabel gain1bLabel;
	JSlider gain2aSlider;
	JLabel gain2aLabel;
	JSlider gain2bSlider;
	JLabel gain2bLabel;

	private Mixer2_1x2CADBlock spbMix;

	public Mixer2_1x2ControlPanel(Mixer2_1x2CADBlock mixer2_1x2cadBlock) {

		spbMix = mixer2_1x2cadBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Mixer 2-1 (x2)");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gain1aSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain1a() * 100));
				gain1aSlider.addChangeListener(new volumeSliderListener());
				gain1aLabel = new JLabel();
				
				gain1bSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain1b() * 100));
				gain1bSlider.addChangeListener(new volumeSliderListener());
				gain1bLabel = new JLabel();

				gain2aSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain2a() * 100));
				gain2aSlider.addChangeListener(new volumeSliderListener());
				gain2aLabel = new JLabel();
				
				gain2bSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain2b() * 100));
				gain2bSlider.addChangeListener(new volumeSliderListener());
				gain2bLabel = new JLabel();
				
				frame.getContentPane().add(gain1aLabel);
				frame.getContentPane().add(gain1aSlider);
				frame.getContentPane().add(gain1bLabel);
				frame.getContentPane().add(gain1bSlider);
				frame.getContentPane().add(gain2aLabel);
				frame.getContentPane().add(gain2aSlider);
				frame.getContentPane().add(gain2bLabel);
				frame.getContentPane().add(gain2bSlider);
				
				updateGainLabels();
				frame.setVisible(true);	
				frame.setAlwaysOnTop(true);	
				frame.setResizable(false);
				frame.setLocation(new Point(spbMix.getX() + 200, spbMix.getY() + 150));
				frame.pack();
			}
		});
	}

	class volumeSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gain1aSlider) {
				spbMix.setGain1a((double) gain1aSlider.getValue()/100.0);
				updateGainLabels();
			}
			else if(ce.getSource() == gain1bSlider) {
				spbMix.setGain1b((double) gain1bSlider.getValue()/100.0);
				updateGainLabels();
			}
			else if(ce.getSource() == gain2aSlider) {
				spbMix.setGain2a((double) gain2aSlider.getValue()/100.0);
				updateGainLabels();
			}
			else if(ce.getSource() == gain2bSlider) {
				spbMix.setGain2b((double) gain2bSlider.getValue()/100.0);
				updateGainLabels();
			}
		}
	}

	public void updateGainLabels() {
		gain1aLabel.setText("Gain 1a " + String.format("%4.2f", spbMix.getGain1a()));		
		gain1bLabel.setText("Gain 1b " + String.format("%4.2f", spbMix.getGain1b()));		
		gain2aLabel.setText("Gain 2a " + String.format("%4.2f", spbMix.getGain2a()));		
		gain2bLabel.setText("Gain 2b " + String.format("%4.2f", spbMix.getGain2b()));		

	}
}
