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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
class PitchTestControlPanel extends JFrame implements ChangeListener {
	JSlider freqSlider;
	JSlider ampSlider;

	JLabel freqLabel;
	JLabel ampLabel;

	private PitchTestCADBlock pong;

	public PitchTestControlPanel(PitchTestCADBlock ppcb) {
		this.pong = ppcb;
		this.setTitle("Pitch Shift Test");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		freqSlider = new JSlider(JSlider.HORIZONTAL, -16383, 32767, 16384);
		freqSlider.addChangeListener(this);
		ampSlider = new JSlider(JSlider.HORIZONTAL, 0, 3, 2);
		ampSlider.addChangeListener(this);

		freqLabel = new JLabel();
		ampLabel = new JLabel();

		this.getContentPane().add(ampLabel);
		updateAmpLabel(2);
		this.getContentPane().add(ampSlider);

		this.getContentPane().add(freqLabel);
		updateFreqLabel();
		this.getContentPane().add(freqSlider);

		int i = ppcb.getAmp();
		int j = 0;
		if(i == 512) {
			j = 0;
		}
		if(i == 1024) {
			j = 1;
		}
		if(i == 2048) {
			j = 2;
		}
		if(i == 4096) {
			j = 3;
		}

		ampSlider.setValue(j);
		freqSlider.setValue((ppcb.getFreq()));

		this.setVisible(true);
		this.setAlwaysOnTop(true);
		this.pack();
		this.setLocation(new Point(pong.getX() + 200, pong.getY() + 150));
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == ampSlider) {
			int i = ampSlider.getValue();
			pong.setAmp(i);
			updateAmpLabel(i);
		}
		else if(ce.getSource() == freqSlider) {
			pong.setFreq(freqSlider.getValue());
			updateFreqLabel();
		}
	}

	public void updateFreqLabel() {
		freqLabel.setText("Freq coefficient " + String.format("%d", pong.getFreq()));

	}
	public void updateAmpLabel(int i) {
		String label = "";
		if(i == 0) {
			label = "512";
		}
		if(i == 1) {
			label = "1024";
		}
		if(i == 2) {
			label = "2048";
		}
		if(i == 3) {
			label = "4096";
		}
		ampLabel.setText("Amplitude " + label);
	}	
}