/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BPFControlPanel.java 
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

class VolumeControlPanel_A {
	private JFrame frame;
	JSlider gainSlider;
	JLabel gainLabel;

	private VolumeCADBlock_A spbVol;

	public VolumeControlPanel_A(VolumeCADBlock_A b) {

		spbVol = b;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Volume");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gainSlider = new JSlider(JSlider.HORIZONTAL, -48, 0, spbVol.getGain());
				gainSlider.addChangeListener(new volumeSliderListener());

				gainLabel = new JLabel();
				frame.getContentPane().add(gainLabel);
				frame.getContentPane().add(gainSlider);
				updateGainLabel();
				frame.setVisible(true);		
				frame.setLocation(new Point(spbVol.getX() + 200, spbVol.getY() + 150));
				frame.pack();
				frame.setResizable(false);
				frame.setAlwaysOnTop(true);
			}
		});
	}

	class volumeSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
				spbVol.setGain(gainSlider.getValue());
				updateGainLabel();
			}
		}
	}

	public void updateGainLabel() {
		gainLabel.setText("Gain " + String.format("%d dB", spbVol.getGain()));		
	}
}
