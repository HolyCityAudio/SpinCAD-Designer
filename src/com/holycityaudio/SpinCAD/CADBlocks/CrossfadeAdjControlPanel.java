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
class CrossfadeAdjControlPanel extends JFrame implements ChangeListener, ActionListener {
	private FineControlSlider midpointSlider;
	private JTextField midpointField;
	private FineControlSlider gain1Slider;
	private JTextField gain1Field;
	private FineControlSlider gain2Slider;
	private JTextField gain2Field;

	private CrossfadeAdjCADBlock gCB;

	public CrossfadeAdjControlPanel(CrossfadeAdjCADBlock block) {
		this.gCB = block;
		this.setTitle("Crossfade Adj");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		// Midpoint slider: 25-90 (displayed as 0.25-0.90)
		midpointSlider = new FineControlSlider(JSlider.HORIZONTAL, 25, 90,
			(int) Math.round(gCB.getMidpoint() * 100));
		midpointSlider.addChangeListener(this);
		midpointField = new JTextField();
		midpointField.setHorizontalAlignment(JTextField.CENTER);
		midpointField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(midpointField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.25, Math.min(0.90, val));
					gCB.setMidpoint(val);
					midpointSlider.setValue((int) Math.round(val * 100));
					updateMidpointLabel();
				} catch (NumberFormatException ex) {
					updateMidpointLabel();
				}
			}
		});

		// Gain 1 slider: -12 to 0 dB
		gain1Slider = new FineControlSlider(JSlider.HORIZONTAL, -12, 0,
			(int) Math.round(20 * Math.log10(gCB.getGain1())));
		gain1Slider.addChangeListener(this);
		gain1Field = new JTextField();
		gain1Field.setHorizontalAlignment(JTextField.CENTER);
		gain1Field.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(gain1Field.getText().replaceAll("[^0-9.\\-]", ""));
					int sliderVal = (int) Math.round(val);
					sliderVal = Math.max(-12, Math.min(0, sliderVal));
					gain1Slider.setValue(sliderVal);
					gCB.setGain1(sliderVal);
					updateGain1Label();
				} catch (NumberFormatException ex) {
					updateGain1Label();
				}
			}
		});

		// Gain 2 slider: -12 to 0 dB
		gain2Slider = new FineControlSlider(JSlider.HORIZONTAL, -12, 0,
			(int) Math.round(20 * Math.log10(gCB.getGain2())));
		gain2Slider.addChangeListener(this);
		gain2Field = new JTextField();
		gain2Field.setHorizontalAlignment(JTextField.CENTER);
		gain2Field.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(gain2Field.getText().replaceAll("[^0-9.\\-]", ""));
					int sliderVal = (int) Math.round(val);
					sliderVal = Math.max(-12, Math.min(0, sliderVal));
					gain2Slider.setValue(sliderVal);
					gCB.setGain2(sliderVal);
					updateGain2Label();
				} catch (NumberFormatException ex) {
					updateGain2Label();
				}
			}
		});

		this.getContentPane().add(midpointField);
		this.getContentPane().add(midpointSlider);
		this.getContentPane().add(gain1Field);
		this.getContentPane().add(gain1Slider);
		this.getContentPane().add(gain2Field);
		this.getContentPane().add(gain2Slider);

		updateMidpointLabel();
		updateGain1Label();
		updateGain2Label();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == midpointSlider) {
			gCB.setMidpoint(midpointSlider.getValue() / 100.0);
			updateMidpointLabel();
		} else if (ce.getSource() == gain1Slider) {
			gCB.setGain1(gain1Slider.getValue());
			updateGain1Label();
		} else if (ce.getSource() == gain2Slider) {
			gCB.setGain2(gain2Slider.getValue());
			updateGain2Label();
		}
	}

	private void updateMidpointLabel() {
		midpointField.setText("Midpoint: " + String.format("%.2f", gCB.getMidpoint()));
	}

	private void updateGain1Label() {
		gain1Field.setText("Input 1 Gain " + String.format("%4.1f dB", 20 * Math.log10(gCB.getGain1())));
	}

	private void updateGain2Label() {
		gain2Field.setText("Input 2 Gain " + String.format("%4.1f dB", 20 * Math.log10(gCB.getGain2())));
	}
}
