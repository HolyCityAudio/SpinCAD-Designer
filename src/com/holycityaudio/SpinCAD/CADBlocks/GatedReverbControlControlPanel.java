/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * GatedReverbControlControlPanel.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class GatedReverbControlControlPanel implements ChangeListener, ActionListener {

	private GatedReverbControlCADBlock block;
	private JDialog frame;

	// Detection mode selector
	private JComboBox<String> detectModeCombo;

	// Sliders
	private FineControlSlider gainSlider;
	private FineControlSlider attackSlider;    // rise time in ms (log scale)
	private FineControlSlider releaseSlider;   // rise time in ms (log scale)
	private FineControlSlider thresholdSlider;
	private FineControlSlider activeRTSlider;
	private FineControlSlider idleRTSlider;
	private FineControlSlider smoothSlider;    // rise time in ms (log scale)

	// Text fields
	private JTextField gainField = new JTextField();
	private JTextField attackField = new JTextField();
	private JTextField releaseField = new JTextField();
	private JTextField thresholdField = new JTextField();
	private JTextField activeRTField = new JTextField();
	private JTextField idleRTField = new JTextField();
	private JTextField smoothField = new JTextField();

	// Time slider range: log10(ms) * 100
	// 1 ms = 0, 10 ms = 100, 100 ms = 200, 1000 ms = 300, 2000 ms = 330
	private static final int TIME_SLIDER_MIN = 0;     // 1 ms
	private static final int TIME_SLIDER_MAX = 269;    // 500 ms

	public GatedReverbControlControlPanel(GatedReverbControlCADBlock block) {
		this.block = block;

		// Detection mode combo
		detectModeCombo = new JComboBox<>(new String[]{"Average", "RMS", "Peak"});
		detectModeCombo.setSelectedIndex(block.getDetectMode());
		detectModeCombo.addActionListener(this);

		// Gain: 0-8 in 6dB steps
		gainSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 8, block.getGain());
		gainSlider.addChangeListener(this);

		// Attack time (ms, log scale, left=fast right=slow)
		attackSlider = new FineControlSlider(JSlider.HORIZONTAL, TIME_SLIDER_MIN, TIME_SLIDER_MAX,
				clampSlider(coeffToTimeSlider(block.getAttack()), TIME_SLIDER_MIN, TIME_SLIDER_MAX));
		attackSlider.addChangeListener(this);

		// Release time (ms, log scale, left=fast right=slow)
		releaseSlider = new FineControlSlider(JSlider.HORIZONTAL, TIME_SLIDER_MIN, TIME_SLIDER_MAX,
				clampSlider(coeffToTimeSlider(block.getRelease()), TIME_SLIDER_MIN, TIME_SLIDER_MAX));
		releaseSlider.addChangeListener(this);

		// Threshold: 1 to 100 (displayed as 0.01 to 1.00)
		thresholdSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100,
				Math.max(1, Math.min(100, (int)(block.getThreshold() * 100))));
		thresholdSlider.addChangeListener(this);

		// Active RT: 0 to 100 (displayed as 0.00 to 1.00)
		activeRTSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100,
				Math.max(0, Math.min(100, (int)(block.getActiveRT() * 100))));
		activeRTSlider.addChangeListener(this);

		// Idle RT: 0 to 100 (displayed as 0.00 to 1.00)
		idleRTSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100,
				Math.max(0, Math.min(100, (int)(block.getIdleRT() * 100))));
		idleRTSlider.addChangeListener(this);

		// Smooth time (ms, log scale, left=fast right=slow)
		smoothSlider = new FineControlSlider(JSlider.HORIZONTAL, TIME_SLIDER_MIN, TIME_SLIDER_MAX,
				clampSlider(coeffToTimeSlider(block.getSmoothCoeff()), TIME_SLIDER_MIN, TIME_SLIDER_MAX));
		smoothSlider.addChangeListener(this);

		// Set up text field alignment
		gainField.setHorizontalAlignment(JTextField.CENTER);
		attackField.setHorizontalAlignment(JTextField.CENTER);
		releaseField.setHorizontalAlignment(JTextField.CENTER);
		thresholdField.setHorizontalAlignment(JTextField.CENTER);
		activeRTField.setHorizontalAlignment(JTextField.CENTER);
		idleRTField.setHorizontalAlignment(JTextField.CENTER);
		smoothField.setHorizontalAlignment(JTextField.CENTER);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JDialog(SpinCADFrame.getInstance(), "Gate CV");
				block.controlPanelFrame = frame;
				frame.setResizable(false);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				frame.add(new JLabel("Detection Mode:"));
				frame.add(detectModeCombo);

				frame.add(gainField);
				frame.add(gainSlider);

				frame.add(attackField);
				frame.add(attackSlider);

				frame.add(releaseField);
				frame.add(releaseSlider);

				frame.add(thresholdField);
				frame.add(thresholdSlider);

				frame.add(activeRTField);
				frame.add(activeRTSlider);

				frame.add(idleRTField);
				frame.add(idleRTSlider);

				frame.add(smoothField);
				frame.add(smoothSlider);

				updateAllFields();

				frame.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						block.clearCP();
					}
				});

				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);
				frame.pack();
			}
		});
	}

	// === Time <-> slider conversion ===
	// Slider value = log10(time_ms) * 100, so slider increases left to right
	// with increasing time (slower response).

	private static int coeffToTimeSlider(double coeff) {
		double timeMs = SpinCADBlock.filtToTime(coeff) * 1000.0;
		if (timeMs < 1.0) timeMs = 1.0;
		return (int)(Math.log10(timeMs) * 100.0);
	}

	private static double timeSliderToCoeff(int sliderVal) {
		double timeMs = Math.pow(10.0, sliderVal / 100.0);
		double timeSec = timeMs / 1000.0;
		return SpinCADBlock.timeToFilt(timeSec);
	}

	private static double coeffToTimeMs(double coeff) {
		return SpinCADBlock.filtToTime(coeff) * 1000.0;
	}

	private static int clampSlider(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == detectModeCombo) {
			block.setDetectMode(detectModeCombo.getSelectedIndex());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == gainSlider) {
			block.setGain(gainSlider.getValue());
			updateGainField();
		} else if (e.getSource() == attackSlider) {
			block.setAttack(timeSliderToCoeff(attackSlider.getValue()));
			updateAttackField();
		} else if (e.getSource() == releaseSlider) {
			block.setRelease(timeSliderToCoeff(releaseSlider.getValue()));
			updateReleaseField();
		} else if (e.getSource() == thresholdSlider) {
			block.setThreshold(thresholdSlider.getValue() / 100.0);
			updateThresholdField();
		} else if (e.getSource() == activeRTSlider) {
			block.setActiveRT(activeRTSlider.getValue() / 100.0);
			updateActiveRTField();
		} else if (e.getSource() == idleRTSlider) {
			block.setIdleRT(idleRTSlider.getValue() / 100.0);
			updateIdleRTField();
		} else if (e.getSource() == smoothSlider) {
			block.setSmoothCoeff(timeSliderToCoeff(smoothSlider.getValue()));
			updateSmoothField();
		}
	}

	private void updateAllFields() {
		updateGainField();
		updateAttackField();
		updateReleaseField();
		updateThresholdField();
		updateActiveRTField();
		updateIdleRTField();
		updateSmoothField();
	}

	private void updateGainField() {
		gainField.setText(String.format("Input Gain: %d dB", block.getGain() * 6));
	}

	private void updateAttackField() {
		double ms = coeffToTimeMs(block.getAttack());
		if (ms >= 100) {
			attackField.setText(String.format("Attack: %.0f ms", ms));
		} else {
			attackField.setText(String.format("Attack: %.1f ms", ms));
		}
	}

	private void updateReleaseField() {
		double ms = coeffToTimeMs(block.getRelease());
		if (ms >= 100) {
			releaseField.setText(String.format("Release: %.0f ms", ms));
		} else {
			releaseField.setText(String.format("Release: %.1f ms", ms));
		}
	}

	private void updateThresholdField() {
		thresholdField.setText(String.format("Threshold: %.2f", block.getThreshold()));
	}

	private void updateActiveRTField() {
		activeRTField.setText(String.format("Active RT: %.2f", block.getActiveRT()));
	}

	private void updateIdleRTField() {
		idleRTField.setText(String.format("Idle RT: %.2f", block.getIdleRT()));
	}

	private void updateSmoothField() {
		double ms = coeffToTimeMs(block.getSmoothCoeff());
		if (ms >= 100) {
			smoothField.setText(String.format("Smooth: %.0f ms", ms));
		} else {
			smoothField.setText(String.format("Smooth: %.1f ms", ms));
		}
	}
}
