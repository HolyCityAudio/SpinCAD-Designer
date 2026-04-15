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
class DattorroPlateReverbControlPanel extends JFrame implements ChangeListener, ActionListener {

	private FineControlSlider gainSlider;
	private JSlider decaySlider;
	private JSlider dampingSlider;
	private JSlider bandwidthSlider;

	private JTextField gainField;
	private JTextField decayField;
	private JTextField dampingField;
	private JTextField bandwidthField;

	private DattorroPlateReverbCADBlock block;

	public DattorroPlateReverbControlPanel(DattorroPlateReverbCADBlock blk) {
		this.block = blk;
		this.setTitle("Dattorro Plate Reverb");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		// Gain slider: -24 to 0 dB, 1 dB normal drag, 0.1 dB fine (Ctrl+drag)
		gainSlider = new FineControlSlider(JSlider.HORIZONTAL, -240, 0, 0);
		gainSlider.setSubdivision(10);
		gainSlider.addChangeListener(this);

		// Decay slider: 0.10 to 0.95 (mapped 10-95)
		decaySlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 95, 50);
		decaySlider.addChangeListener(this);

		// Damping slider: 0.00 to 0.95 (mapped 0-95)
		dampingSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 95, 50);
		dampingSlider.addChangeListener(this);

		// Bandwidth slider: 0.10 to 0.70 (mapped 10-70)
		bandwidthSlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 70, 32);
		bandwidthSlider.addChangeListener(this);

		gainField = new JTextField();
		gainField.setHorizontalAlignment(JTextField.CENTER);
		gainField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(gainField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(-24.0, Math.min(0.0, val));
					block.setGain(val);
					gainSlider.setValue((int) Math.round(val * 10.0));
					updateGainLabel();
				} catch (NumberFormatException ex) {
					updateGainLabel();
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
					val = Math.max(0.1, Math.min(0.95, val));
					block.setDecay(val);
					decaySlider.setValue((int) Math.round(val * 100.0));
					updateDecayLabel();
				} catch (NumberFormatException ex) {
					updateDecayLabel();
				}
			}
		});

		dampingField = new JTextField();
		dampingField.setHorizontalAlignment(JTextField.CENTER);
		dampingField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(dampingField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(0.95, val));
					block.setDamping(val);
					dampingSlider.setValue((int) Math.round(val * 100.0));
					updateDampingLabel();
				} catch (NumberFormatException ex) {
					updateDampingLabel();
				}
			}
		});

		bandwidthField = new JTextField();
		bandwidthField.setHorizontalAlignment(JTextField.CENTER);
		bandwidthField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(bandwidthField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.1, Math.min(0.7, val));
					block.setBandwidth(val);
					bandwidthSlider.setValue((int) Math.round(val * 100.0));
					updateBandwidthLabel();
				} catch (NumberFormatException ex) {
					updateBandwidthLabel();
				}
			}
		});

		this.getContentPane().add(gainField);
		this.getContentPane().add(gainSlider);
		this.getContentPane().add(decayField);
		this.getContentPane().add(decaySlider);
		this.getContentPane().add(dampingField);
		this.getContentPane().add(dampingSlider);
		this.getContentPane().add(bandwidthField);
		this.getContentPane().add(bandwidthSlider);

		// Initialize slider positions from block values
		gainSlider.setValue((int) Math.round(blk.getGain() * 10.0));
		decaySlider.setValue((int) Math.round(blk.getDecay() * 100.0));
		dampingSlider.setValue((int) Math.round(blk.getDamping() * 100.0));
		bandwidthSlider.setValue((int) Math.round(blk.getBandwidth() * 100.0));

		updateGainLabel();
		updateDecayLabel();
		updateDampingLabel();
		updateBandwidthLabel();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == gainSlider) {
			block.setGain(gainSlider.getValue() / 10.0);
			updateGainLabel();
		} else if (ce.getSource() == decaySlider) {
			block.setDecay(decaySlider.getValue() / 100.0);
			updateDecayLabel();
		} else if (ce.getSource() == dampingSlider) {
			block.setDamping(dampingSlider.getValue() / 100.0);
			updateDampingLabel();
		} else if (ce.getSource() == bandwidthSlider) {
			block.setBandwidth(bandwidthSlider.getValue() / 100.0);
			updateBandwidthLabel();
		}
	}

	private void updateGainLabel() {
		gainField.setText("Input Gain " + String.format("%.1f", block.getGain()) + " dB");
	}

	private void updateDecayLabel() {
		decayField.setText("Decay " + String.format("%.2f", block.getDecay()));
	}

	private void updateDampingLabel() {
		dampingField.setText("HF Damping " + String.format("%.2f", block.getDamping()));
	}

	private void updateBandwidthLabel() {
		bandwidthField.setText("Bandwidth " + String.format("%.2f", block.getBandwidth()));
	}
}
