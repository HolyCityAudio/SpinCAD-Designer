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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

class Mixer4_1ControlPanel {
	private JFrame frame;

	FineControlSlider gain1Slider;
	JTextField gain1Field;
	FineControlSlider gain2Slider;
	JTextField gain2Field;
	FineControlSlider gain3Slider;
	JTextField gain3Field;
	FineControlSlider gain4Slider;
	JTextField gain4Field;

	private Mixer4_1CADBlock spbMix;

	public Mixer4_1ControlPanel(Mixer4_1CADBlock mixer4_1cadBlock) {

		spbMix = mixer4_1cadBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				spbMix.controlPanelFrame = frame;
				frame.setTitle("Mixer 4-1");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gain1Slider = new FineControlSlider(JSlider.HORIZONTAL, 1, 50, (int) (spbMix.getGain1() * 100));
				gain1Slider.addChangeListener(new volumeSliderListener());
				gain1Field = new JTextField();
				gain1Field.setHorizontalAlignment(JTextField.CENTER);
				gain1Field.addActionListener(new GainFieldListener());

				gain2Slider = new FineControlSlider(JSlider.HORIZONTAL, 1, 50, (int) (spbMix.getGain2() * 100));
				gain2Slider.addChangeListener(new volumeSliderListener());
				gain2Field = new JTextField();
				gain2Field.setHorizontalAlignment(JTextField.CENTER);
				gain2Field.addActionListener(new GainFieldListener());

				gain3Slider = new FineControlSlider(JSlider.HORIZONTAL, 1, 50, (int) (spbMix.getGain3() * 100));
				gain3Slider.addChangeListener(new volumeSliderListener());
				gain3Field = new JTextField();
				gain3Field.setHorizontalAlignment(JTextField.CENTER);
				gain3Field.addActionListener(new GainFieldListener());

				gain4Slider = new FineControlSlider(JSlider.HORIZONTAL, 1, 50, (int) (spbMix.getGain4() * 100));
				gain4Slider.addChangeListener(new volumeSliderListener());
				gain4Field = new JTextField();
				gain4Field.setHorizontalAlignment(JTextField.CENTER);
				gain4Field.addActionListener(new GainFieldListener());

				frame.getContentPane().add(gain1Field);
				frame.getContentPane().add(gain1Slider);
				frame.getContentPane().add(gain2Field);
				frame.getContentPane().add(gain2Slider);
				frame.getContentPane().add(gain3Field);
				frame.getContentPane().add(gain3Slider);
				frame.getContentPane().add(gain4Field);
				frame.getContentPane().add(gain4Slider);

				updateGainLabels();
				frame.setVisible(true);
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.pack();
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
			else if(ce.getSource() == gain3Slider) {
				spbMix.setGain3((double) gain3Slider.getValue()/100.0);
				updateGainLabels();
			}
			else if(ce.getSource() == gain4Slider) {
				spbMix.setGain4((double) gain4Slider.getValue()/100.0);
				updateGainLabels();
			}
		}
	}

	class GainFieldListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String text = ((JTextField) e.getSource()).getText().replaceAll("[^\\d.\\-]", "");
				double val = Double.parseDouble(text);
				int sliderVal = (int) Math.round(val * 100.0);
				sliderVal = Math.max(1, Math.min(50, sliderVal));
				if (e.getSource() == gain1Field) {
					spbMix.setGain1(sliderVal / 100.0);
					gain1Slider.setValue(sliderVal);
				} else if (e.getSource() == gain2Field) {
					spbMix.setGain2(sliderVal / 100.0);
					gain2Slider.setValue(sliderVal);
				} else if (e.getSource() == gain3Field) {
					spbMix.setGain3(sliderVal / 100.0);
					gain3Slider.setValue(sliderVal);
				} else if (e.getSource() == gain4Field) {
					spbMix.setGain4(sliderVal / 100.0);
					gain4Slider.setValue(sliderVal);
				}
				updateGainLabels();
			} catch (NumberFormatException ex) {
				updateGainLabels();
			}
		}
	}

	public void updateGainLabels() {
		gain1Field.setText("Gain " + String.format("%4.2f", spbMix.getGain1()));
		gain2Field.setText("Gain " + String.format("%4.2f", spbMix.getGain2()));
		gain3Field.setText("Gain " + String.format("%4.2f", spbMix.getGain3()));
		gain4Field.setText("Gain " + String.format("%4.2f", spbMix.getGain4()));

	}
}
