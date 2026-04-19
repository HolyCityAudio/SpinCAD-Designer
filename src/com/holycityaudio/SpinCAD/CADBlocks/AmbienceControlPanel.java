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
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

@SuppressWarnings("serial")
class AmbienceControlPanel extends JDialog implements ChangeListener, ActionListener {
	FineControlSlider inputGainSlider;
	JSlider toneSlider;
	JSlider decaySlider;
	JSlider filterFreqSlider;
	JTextField inputGainField;
	JTextField toneField;
	JTextField decayField;
	JTextField filterFreqField;

	private AmbienceCADBlock block;

	public AmbienceControlPanel(AmbienceCADBlock acb) {
		super(SpinCADFrame.getInstance(), "Ambience");
		this.block = acb;
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		inputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, -120, 0,
				(int) Math.round(acb.getInputGain() * 10));
		inputGainSlider.addChangeListener(this);

		toneSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		toneSlider.addChangeListener(this);

		decaySlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		decaySlider.addChangeListener(this);

		// slider range 2000-8000 Hz, step 100 Hz
		filterFreqSlider = new FineControlSlider(JSlider.HORIZONTAL, 2000, 8000, 4000);
		filterFreqSlider.addChangeListener(this);

		inputGainField = new JTextField();
		inputGainField.setHorizontalAlignment(JTextField.CENTER);
		inputGainField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(inputGainField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(-12.0, Math.min(0.0, val));
					block.setInputGain(val);
					inputGainSlider.setValue((int) Math.round(val * 10));
					updateInputGainLabel();
				} catch (NumberFormatException ex) {
					updateInputGainLabel();
				}
			}
		});

		toneField = new JTextField();
		toneField.setHorizontalAlignment(JTextField.CENTER);
		toneField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(toneField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(1.0, val));
					block.setTone(val);
					toneSlider.setValue((int) Math.round(val * 100.0));
					updateToneLabel();
				} catch (NumberFormatException ex) {
					updateToneLabel();
				}
			}
		});

		decayField = new JTextField();
		decayField.setHorizontalAlignment(JTextField.CENTER);
		decayField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(decayField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(1.0, val));
					block.setDecay(val);
					decaySlider.setValue((int) Math.round(val * 100.0));
					updateDecayLabel();
				} catch (NumberFormatException ex) {
					updateDecayLabel();
				}
			}
		});

		filterFreqField = new JTextField();
		filterFreqField.setHorizontalAlignment(JTextField.CENTER);
		filterFreqField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(filterFreqField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(2000.0, Math.min(8000.0, val));
					block.setFilterFreq(val);
					filterFreqSlider.setValue((int) Math.round(val));
					updateFilterFreqLabel();
				} catch (NumberFormatException ex) {
					updateFilterFreqLabel();
				}
			}
		});

		this.getContentPane().add(inputGainField);
		this.getContentPane().add(inputGainSlider);
		this.getContentPane().add(toneField);
		this.getContentPane().add(toneSlider);
		this.getContentPane().add(decayField);
		this.getContentPane().add(decaySlider);
		this.getContentPane().add(filterFreqField);
		this.getContentPane().add(filterFreqSlider);

		toneSlider.setValue((int) Math.round(acb.getTone() * 100.0));
		decaySlider.setValue((int) Math.round(acb.getDecay() * 100.0));
		filterFreqSlider.setValue((int) Math.round(acb.getFilterFreq()));

		updateInputGainLabel();
		updateToneLabel();
		updateDecayLabel();
		updateFilterFreqLabel();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == inputGainSlider) {
			block.setInputGain(inputGainSlider.getValue() / 10.0);
			updateInputGainLabel();
		} else if (ce.getSource() == toneSlider) {
			block.setTone((double) toneSlider.getValue() / 100.0);
			updateToneLabel();
		} else if (ce.getSource() == decaySlider) {
			block.setDecay((double) decaySlider.getValue() / 100.0);
			updateDecayLabel();
		} else if (ce.getSource() == filterFreqSlider) {
			block.setFilterFreq((double) filterFreqSlider.getValue());
			updateFilterFreqLabel();
		}
	}

	private void updateInputGainLabel() {
		inputGainField.setText(String.format("Input Gain: %4.1f dB", block.getInputGain()));
	}

	private void updateToneLabel() {
		toneField.setText("Tone " + String.format("%2.2f", block.getTone()));
	}

	private void updateDecayLabel() {
		decayField.setText("Decay " + String.format("%2.2f", block.getDecay()));
	}

	private void updateFilterFreqLabel() {
		filterFreqField.setText("Filter Freq " + String.format("%.0f", block.getFilterFreq()) + " Hz");
	}
}
