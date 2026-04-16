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
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADFrame;

class OneBandEQControlPanel extends JDialog implements ChangeListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 6306397702386815750L;

	JSlider eqSlider0;
	JTextField eqField0;

	JSlider qSlider;
	JTextField qField;

	JSlider freqSlider0;
	JTextField freqField;

	private OneBandEQCADBlock filter;

	public OneBandEQControlPanel(OneBandEQCADBlock b) {
		super(SpinCADFrame.getInstance(), "1-Band EQ");
		this.filter = b;
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		eqSlider0 = new FineControlSlider(JSlider.HORIZONTAL, -100, 199, 0);
		eqSlider0.addChangeListener(this);

		freqSlider0 = SpinCADBlock.LogSlider(20, 3200, SpinCADBlock.freqToFilt(b.getFreq()), "LOGFREQ", 100.0);
		freqSlider0.addChangeListener(this);

		qSlider = new FineControlSlider(JSlider.HORIZONTAL, 50, 1000, 120);
		qSlider.addChangeListener(this);

		eqField0 = new JTextField();
		eqField0.setHorizontalAlignment(JTextField.CENTER);
		eqField0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(eqField0.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(-1.0, Math.min(1.99, val));
					filter.setEqLevel(val);
					eqSlider0.setValue((int) Math.round(val * 100.0));
					updateEqLabel();
				} catch (NumberFormatException ex) {
					updateEqLabel();
				}
			}
		});

		qField = new JTextField();
		qField.setHorizontalAlignment(JTextField.CENTER);
		qField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(qField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.5, Math.min(10.0, val));
					filter.setqLevel(val);
					qSlider.setValue((int) Math.round(val * 100.0));
					updateQLabel();
				} catch (NumberFormatException ex) {
					updateQLabel();
				}
			}
		});

		freqField = new JTextField();
		freqField.setHorizontalAlignment(JTextField.CENTER);
		freqField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(freqField.getText().replaceAll("[^0-9.\\-]", ""));
					int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
					sliderVal = Math.max(freqSlider0.getMinimum(), Math.min(freqSlider0.getMaximum(), sliderVal));
					freqSlider0.setValue(sliderVal);
					filter.setFreq(SpinCADBlock.sliderToLogval(sliderVal, 100.0));
					updateFreqLabel();
				} catch (NumberFormatException ex) {
					updateFreqLabel();
				}
			}
		});

		this.getContentPane().add(eqField0);
		this.getContentPane().add(eqSlider0);

		this.getContentPane().add(freqField);
		this.getContentPane().add(freqSlider0);

		this.getContentPane().add(qField);
		this.getContentPane().add(qSlider);

		this.setVisible(true);
		this.setResizable(false);

		eqSlider0.setValue((int) Math.round(((b.getEqLevel()) * 100.0)));
		updateEqLabel();

		freqSlider0.setValue(SpinCADBlock.logvalToSlider(b.getFreq(), 100.0));
		updateFreqLabel();

		qSlider.setValue((int) Math.round((b.getQLevel() * 100.0)));
		updateQLabel();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
		this.pack();
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == eqSlider0) {
			filter.setEqLevel((double) eqSlider0.getValue() / 100.0);
			updateEqLabel();
		} else if (ce.getSource() == freqSlider0) {
			filter.setFreq(SpinCADBlock.sliderToLogval((int) freqSlider0.getValue(), 100.0));
			updateFreqLabel();
		}  else if (ce.getSource() == qSlider) {
			filter.setqLevel((double) qSlider.getValue() / 100.0);
			updateQLabel();
		}
	}

	private void updateEqLabel() {
		eqField0.setText("Level "
				+ String.format("%2.2f", filter.getEqLevel()));
	}

	private void updateFreqLabel() {
		freqField.setText("Frequency "
				+ String.format("%2.2f", filter.getFreq()));
	}

	private void updateQLabel() {
		qField.setText("Q " + String.format("%2.1f", filter.getQLevel()));
	}
}
