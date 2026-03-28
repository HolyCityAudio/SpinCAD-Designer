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

class Mixer2_1x2ControlPanel {
	private JFrame frame;

	FineControlSlider gain1aSlider;
	JTextField gain1aField;
	FineControlSlider gain1bSlider;
	JTextField gain1bField;
	FineControlSlider gain2aSlider;
	JTextField gain2aField;
	FineControlSlider gain2bSlider;
	JTextField gain2bField;

	private Mixer2_1x2CADBlock spbMix;

	public Mixer2_1x2ControlPanel(Mixer2_1x2CADBlock mixer2_1x2cadBlock) {

		spbMix = mixer2_1x2cadBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Mixer 2-1 (x2)");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gain1aSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain1a() * 100));
				gain1aSlider.addChangeListener(new volumeSliderListener());
				gain1aField = new JTextField();
				gain1aField.setHorizontalAlignment(JTextField.CENTER);
				gain1aField.addActionListener(new GainFieldListener());

				gain1bSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain1b() * 100));
				gain1bSlider.addChangeListener(new volumeSliderListener());
				gain1bField = new JTextField();
				gain1bField.setHorizontalAlignment(JTextField.CENTER);
				gain1bField.addActionListener(new GainFieldListener());

				gain2aSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain2a() * 100));
				gain2aSlider.addChangeListener(new volumeSliderListener());
				gain2aField = new JTextField();
				gain2aField.setHorizontalAlignment(JTextField.CENTER);
				gain2aField.addActionListener(new GainFieldListener());

				gain2bSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) (spbMix.getGain2b() * 100));
				gain2bSlider.addChangeListener(new volumeSliderListener());
				gain2bField = new JTextField();
				gain2bField.setHorizontalAlignment(JTextField.CENTER);
				gain2bField.addActionListener(new GainFieldListener());

				frame.getContentPane().add(gain1aField);
				frame.getContentPane().add(gain1aSlider);
				frame.getContentPane().add(gain1bField);
				frame.getContentPane().add(gain1bSlider);
				frame.getContentPane().add(gain2aField);
				frame.getContentPane().add(gain2aSlider);
				frame.getContentPane().add(gain2bField);
				frame.getContentPane().add(gain2bSlider);

				updateGainLabels();
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
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

	class GainFieldListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				String text = ((JTextField) e.getSource()).getText().replaceAll("[^\\d.\\-]", "");
				double val = Double.parseDouble(text);
				int sliderVal = (int) Math.round(val * 100.0);
				sliderVal = Math.max(1, Math.min(100, sliderVal));
				if (e.getSource() == gain1aField) {
					spbMix.setGain1a(sliderVal / 100.0);
					gain1aSlider.setValue(sliderVal);
				} else if (e.getSource() == gain1bField) {
					spbMix.setGain1b(sliderVal / 100.0);
					gain1bSlider.setValue(sliderVal);
				} else if (e.getSource() == gain2aField) {
					spbMix.setGain2a(sliderVal / 100.0);
					gain2aSlider.setValue(sliderVal);
				} else if (e.getSource() == gain2bField) {
					spbMix.setGain2b(sliderVal / 100.0);
					gain2bSlider.setValue(sliderVal);
				}
				updateGainLabels();
			} catch (NumberFormatException ex) {
				updateGainLabels();
			}
		}
	}

	public void updateGainLabels() {
		gain1aField.setText("Gain 1a " + String.format("%4.2f", spbMix.getGain1a()));
		gain1bField.setText("Gain 1b " + String.format("%4.2f", spbMix.getGain1b()));
		gain2aField.setText("Gain 2a " + String.format("%4.2f", spbMix.getGain2a()));
		gain2bField.setText("Gain 2b " + String.format("%4.2f", spbMix.getGain2b()));

	}
}
