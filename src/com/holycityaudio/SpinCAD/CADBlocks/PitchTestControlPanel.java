/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2026.  Look for GSW in code.
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

@SuppressWarnings("serial")
class PitchTestControlPanel extends JFrame implements ChangeListener {
	JSlider freqSlider;
	JSlider ampSlider;

	JTextField freqField;
	JTextField ampField;

	private PitchTestCADBlock pong;

	public PitchTestControlPanel(PitchTestCADBlock ppcb) {
		this.pong = ppcb;
		this.setTitle("Pitch Shift Test");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		freqSlider = new FineControlSlider(JSlider.HORIZONTAL, -16383, 32767, 16384);
		freqSlider.addChangeListener(this);
		ampSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 3, 2);
		ampSlider.addChangeListener(this);

		freqField = new JTextField();
		freqField.setHorizontalAlignment(JTextField.CENTER);
		freqField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int val = Integer.parseInt(freqField.getText().replaceAll("[^0-9.\\-]", "").split("\\.")[0]);
					val = Math.max(-16383, Math.min(32767, val));
					pong.setFreq(val);
					freqSlider.setValue(val);
					updateFreqLabel();
				} catch (NumberFormatException ex) {
					updateFreqLabel();
				}
			}
		});

		ampField = new JTextField();
		ampField.setHorizontalAlignment(JTextField.CENTER);
		ampField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// User types a buffer depth value: 512, 1024, 2048, 4096
					int typed = Integer.parseInt(ampField.getText().replaceAll("[^0-9.\\-]", "").split("\\.")[0]);
					int idx;
					if (typed <= 512) idx = 0;
					else if (typed <= 1024) idx = 1;
					else if (typed <= 2048) idx = 2;
					else idx = 3;
					idx = Math.max(0, Math.min(3, idx));
					pong.setAmp(idx);
					ampSlider.setValue(idx);
					updateAmpLabel(idx);
				} catch (NumberFormatException ex) {
					updateAmpLabel(ampSlider.getValue());
				}
			}
		});

		this.getContentPane().add(ampField);
		updateAmpLabel(2);
		this.getContentPane().add(ampSlider);

		this.getContentPane().add(freqField);
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
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
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
		freqField.setText("Freq coefficient " + String.format("%d", pong.getFreq()));

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
		ampField.setText("Amplitude " + label);
	}
}
