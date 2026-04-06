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
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

@SuppressWarnings("serial")
class MinReverbControlPanel extends JFrame implements ChangeListener {
	FineControlSlider inputGainSlider;
	JTextField inputGainField;

	private MinReverbCADBlock block;

	public MinReverbControlPanel(MinReverbCADBlock mrb) {
		this.block = mrb;
		this.setTitle("Min Reverb");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		inputGainSlider = new FineControlSlider(FineControlSlider.HORIZONTAL, -120, 0,
				(int) Math.round(block.getInputGain() * 10));
		inputGainSlider.addChangeListener(this);

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

		this.getContentPane().add(inputGainField);
		this.getContentPane().add(inputGainSlider);

		updateInputGainLabel();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == inputGainSlider) {
			block.setInputGain(inputGainSlider.getValue() / 10.0);
			updateInputGainLabel();
		}
	}

	private void updateInputGainLabel() {
		inputGainField.setText(String.format("Input Gain: %4.1f dB", block.getInputGain()));
	}
}
