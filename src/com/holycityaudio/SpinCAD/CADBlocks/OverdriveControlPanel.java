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
class OverdriveControlPanel extends JDialog implements ChangeListener, ActionListener {
	JSlider stagesSlider;
	JTextField stagesField;
	JSlider gainSlider;
	JTextField gainField;
	JSlider outputGainSlider;
	JTextField outputGainField;

	private OverdriveCADBlock oD;

	public OverdriveControlPanel(OverdriveCADBlock odCb) {
		super(SpinCADFrame.getInstance(), "Overdrive");
		this.oD = odCb;
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		stagesSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 3, 2);
		stagesSlider.setMajorTickSpacing(1);
		stagesSlider.setPaintTicks(true);
		stagesSlider.addChangeListener(this);
		stagesField = new JTextField();
		stagesField.setHorizontalAlignment(JTextField.CENTER);
		stagesField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int val = Integer.parseInt(stagesField.getText().replaceAll("[^0-9.\\-]", "").split("\\.")[0]);
					val = Math.max(1, Math.min(3, val));
					oD.setStages(val);
					stagesSlider.setValue(val);
					updateStagesLabel();
				} catch (NumberFormatException ex) {
					updateStagesLabel();
				}
			}
		});

		gainSlider = new FineControlSlider(JSlider.HORIZONTAL, 20, 190, (int) (odCb.getGain() * 100));
		gainSlider.addChangeListener(this);

		gainField = new JTextField();
		gainField.setHorizontalAlignment(JTextField.CENTER);
		gainField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(gainField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.20, Math.min(1.90, val));
					oD.setGain(val);
					gainSlider.setValue((int) Math.round(val * 100.0));
					updateGainLabel();
				} catch (NumberFormatException ex) {
					updateGainLabel();
				}
			}
		});

		outputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, 2, 100, (int) (odCb.getOutputGain() * 100));
		outputGainSlider.addChangeListener(this);

		outputGainField = new JTextField();
		outputGainField.setHorizontalAlignment(JTextField.CENTER);
		outputGainField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(outputGainField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.02, Math.min(1.0, val));
					oD.setOutputGain(val);
					outputGainSlider.setValue((int) Math.round(val * 100.0));
					updateOutputGainLabel();
				} catch (NumberFormatException ex) {
					updateOutputGainLabel();
				}
			}
		});

		this.getContentPane().add(stagesField);
		this.getContentPane().add(stagesSlider);

		this.getContentPane().add(gainField);
		this.getContentPane().add(gainSlider);

		this.getContentPane().add(outputGainField);
		this.getContentPane().add(outputGainSlider);

		gainSlider.setValue((int)Math.round(100.0 * odCb.getGain()));
		outputGainSlider.setValue((int)Math.round(100.0 * odCb.getOutputGain()));
		stagesSlider.setValue(odCb.getStages());

		updateStagesLabel();
		updateGainLabel();
		updateOutputGainLabel();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == stagesSlider) {
			oD.setStages(stagesSlider.getValue());
			updateStagesLabel();
		}
		else if(ce.getSource() == gainSlider) {
			oD.setGain(gainSlider.getValue()/100.0);
			updateGainLabel();
		}
		else if(ce.getSource() == outputGainSlider) {
				oD.setOutputGain(outputGainSlider.getValue()/100.0);
				updateOutputGainLabel();
		}
	}

	public void updateStagesLabel() {
		stagesField.setText("Stages: " + String.format("%d", oD.getStages()));
	}

	public void updateGainLabel() {
		gainField.setText("Input Gain: " + String.format("%4.2f", oD.getGain()));
	}

	public void updateOutputGainLabel() {
		outputGainField.setText("Output Gain: " + String.format("%4.2f", oD.getOutputGain()));
	}
}
