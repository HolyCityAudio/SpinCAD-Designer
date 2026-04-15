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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.andrewkilpatrick.elmGen.ElmProgram;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADFrame;

@SuppressWarnings("serial")
class LongDelayControlPanel extends JFrame implements ChangeListener, ActionListener {
	FineControlSlider feedbackSlider;
	FineControlSlider gainSlider;
	JSlider interleaveSlider;
	JCheckBox filterCheck;

	JTextField feedbackField;
	JTextField gainField;
	JTextField interleaveField;

	private LongDelayCADBlock block;

	public LongDelayControlPanel(LongDelayCADBlock longDelayBlock) {
		this.block = longDelayBlock;
		this.setTitle("Long Delay");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		interleaveSlider = new JSlider(JSlider.HORIZONTAL, 2, 16, block.getInterleave());
		interleaveSlider.setMajorTickSpacing(2);
		interleaveSlider.setMinorTickSpacing(1);
		interleaveSlider.setSnapToTicks(true);
		interleaveSlider.setPaintTicks(true);
		interleaveSlider.setPaintLabels(true);
		interleaveSlider.addChangeListener(this);

		// dB sliders: -24 to 0 dB, 1 dB normal drag, 0.1 dB fine (Ctrl+drag)
		gainSlider = new FineControlSlider(JSlider.HORIZONTAL, -240, 0,
				(int) (20 * Math.log10(block.getInputGain()) * 10));
		gainSlider.setSubdivision(10);
		gainSlider.addChangeListener(this);

		feedbackSlider = new FineControlSlider(JSlider.HORIZONTAL, -240, 0,
				(int) (20 * Math.log10(block.getFeedbackLevel()) * 10));
		feedbackSlider.setSubdivision(10);
		feedbackSlider.addChangeListener(this);

		filterCheck = new JCheckBox("Anti-Aliasing Filter", block.isFilterEnabled());
		filterCheck.addActionListener(this);

		gainField = new JTextField();
		gainField.setHorizontalAlignment(JTextField.CENTER);

		feedbackField = new JTextField();
		feedbackField.setHorizontalAlignment(JTextField.CENTER);

		interleaveField = new JTextField();
		interleaveField.setHorizontalAlignment(JTextField.CENTER);

		this.getContentPane().add(interleaveField);
		this.getContentPane().add(interleaveSlider);

		this.getContentPane().add(gainField);
		this.getContentPane().add(gainSlider);

		this.getContentPane().add(feedbackField);
		this.getContentPane().add(feedbackSlider);

		this.getContentPane().add(filterCheck);

		updateAllFields();

		this.setVisible(true);
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == filterCheck) {
			block.setFilterEnabled(filterCheck.isSelected());
		}
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == gainSlider) {
			block.setInputGain(gainSlider.getValue() / 10.0);
			updateGainField();
		} else if (ce.getSource() == feedbackSlider) {
			block.setFeedbackLevel(feedbackSlider.getValue() / 10.0);
			updateFeedbackField();
		} else if (ce.getSource() == interleaveSlider) {
			block.setInterleave(interleaveSlider.getValue());
			updateInterleaveField();
		}
	}

	private void updateAllFields() {
		updateGainField();
		updateFeedbackField();
		updateInterleaveField();
	}

	private void updateGainField() {
		gainField.setText(String.format("Input Gain: %4.1f dB", 20 * Math.log10(block.getInputGain())));
	}

	private void updateFeedbackField() {
		feedbackField.setText(String.format("Feedback Gain: %4.1f dB", 20 * Math.log10(block.getFeedbackLevel())));
	}

	private void updateInterleaveField() {
		int n = block.getInterleave();
		double seconds = block.getDelaySeconds();
		double cutoffHz = ElmProgram.getSamplerate() / (2.0 * n);
		interleaveField.setText(String.format("Interleave: %dx (~%.1f sec, LP %.0f Hz)", n, seconds, cutoffHz));
	}
}
