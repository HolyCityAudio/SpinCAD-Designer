/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C)2013 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick
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

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class EnvelopeControlControlPanel implements ChangeListener, ActionListener {

	private FineControlSlider gainSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 8, 2);
	private JTextField gainField = new JTextField();

	private FineControlSlider attackSlider = null;
	private JTextField attackField = new JTextField();

	private FineControlSlider decaySlider = null;
	private JTextField decayField = new JTextField();

	private JFrame frame;

	private EnvelopeControlCADBlock pC;

	public EnvelopeControlControlPanel(EnvelopeControlCADBlock envelopeControlCADBlock) {
		gainSlider.addChangeListener(this);

		this.pC = envelopeControlCADBlock;
		// JSlider value is converted to an exponent representing filter frequency, so
		// -29 => 10^(-29/100) = 0.5129 Hz which determined is the lowest practical frequency possible
		// with the FV-1's coefficient resolution.
		// 100 => 10^(100/100) = 10 Hz.
		attackSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-29),(int) (125), SpinCADBlock.logvalToSlider(SpinCADBlock.filtToFreq(pC.getAttack()), 100.0));
		decaySlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-29),(int) (50), SpinCADBlock.logvalToSlider(SpinCADBlock.filtToFreq(pC.getDecay()), 100.0));

		attackSlider.addChangeListener(this);
		decaySlider.addChangeListener(this);

		gainField.setHorizontalAlignment(JTextField.CENTER);
		gainField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = gainField.getText().replaceAll("[^\\d.\\-]", "");
					double dbVal = Double.parseDouble(text);
					// display shows pC.getGain() * 6 dB, so gain = dbVal / 6
					int sliderVal = (int) Math.round(dbVal / 6.0);
					sliderVal = Math.max(gainSlider.getMinimum(), Math.min(gainSlider.getMaximum(), sliderVal));
					pC.setGain(sliderVal);
					gainSlider.setValue(sliderVal);
					updateGainField();
				} catch (NumberFormatException ex) {
					updateGainField();
				}
			}
		});

		attackField.setHorizontalAlignment(JTextField.CENTER);
		attackField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = attackField.getText().replaceAll("[^\\d.\\-]", "");
					double freqVal = Double.parseDouble(text);
					// display shows filtToFreq(attack), so reverse: freq -> filt -> model
					// slider value = logvalToSlider(freq, 100.0)
					if (freqVal <= 0) freqVal = 0.01;
					int sliderVal = SpinCADBlock.logvalToSlider(freqVal, 100.0);
					sliderVal = Math.max(attackSlider.getMinimum(), Math.min(attackSlider.getMaximum(), sliderVal));
					pC.setAttack(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
					attackSlider.setValue(sliderVal);
					updateAttackField();
				} catch (NumberFormatException ex) {
					updateAttackField();
				}
			}
		});

		decayField.setHorizontalAlignment(JTextField.CENTER);
		decayField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = decayField.getText().replaceAll("[^\\d.\\-]", "");
					double freqVal = Double.parseDouble(text);
					if (freqVal <= 0) freqVal = 0.01;
					int sliderVal = SpinCADBlock.logvalToSlider(freqVal, 100.0);
					sliderVal = Math.max(decaySlider.getMinimum(), Math.min(decaySlider.getMaximum(), sliderVal));
					pC.setDecay(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
					decaySlider.setValue(sliderVal);
					updateDecayField();
				} catch (NumberFormatException ex) {
					updateDecayField();
				}
			}
		});

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Envelope");
				pC.controlPanelFrame = frame;
				frame.setTitle("Envelope");
				frame.setResizable(false);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gainSlider.setMajorTickSpacing(25);

				frame.add(gainField);
				frame.add(gainSlider);
				frame.add(attackField);
				frame.add(attackSlider);

				frame.add(decayField);
				frame.add(decaySlider);

				gainSlider.setValue((int) Math.round(pC.getGain()));
				updateGainField();

				attackSlider.setValue((int) SpinCADBlock.logvalToSlider(SpinCADBlock.filtToFreq(pC.getAttack()), 100.0));
				updateAttackField();

				decaySlider.setValue((int) SpinCADBlock.logvalToSlider(SpinCADBlock.filtToFreq(pC.getDecay()), 100.0));
				updateDecayField();

				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);
				frame.pack();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == gainSlider) {
			pC.setGain(gainSlider.getValue());
			updateGainField();
		}
		else if (e.getSource() == attackSlider) {
			pC.setAttack(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(attackSlider.getValue(), 100.0)));
//			pC.setAttack((double) attackSlider.getValue()/100000.0);
			updateAttackField();
		}
		else if (e.getSource() == decaySlider) {
			pC.setDecay(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(decaySlider.getValue(), 100.0)));
//			pC.setDecay((double) decaySlider.getValue()/1000000.0);
			updateDecayField();
		}
	}

	private void updateGainField() {
		gainField.setText(String.format("Gain: %2d dB", pC.getGain() * 6));
	}

	private void updateAttackField() {
		attackField.setText(String.format("Attack: %4.1f", SpinCADBlock.filtToFreq(pC.getAttack())));
	}

	private void updateDecayField() {
		decayField.setText(String.format("Decay: %4.1f", SpinCADBlock.filtToFreq(pC.getDecay())));
	}
}