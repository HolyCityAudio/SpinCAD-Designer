/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * OilCanDelayControlPanel.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

@SuppressWarnings("serial")
class OilCanDelayControlPanel extends JFrame implements ChangeListener, ActionListener {

	private OilCanDelayCADBlock block;

	// sliders (int range, mapped to real values via scale factors)
	private JSlider delaySlider;      // 3277 .. 16384 (samples)
	private JSlider ratioSlider;      // 1 .. 4
	private JSlider modDepthSlider;   // 5 .. 200 (tenths of ms → 0.5..20 ms)
	private JSlider fbkSlider;        // 0 .. 95  (%)
	private JSlider dampSlider;       // 200 .. 8000 (Hz)

	private JTextField delayField;
	private JTextField ratioField;
	private JTextField modDepthField;
	private JTextField fbkField;
	private JTextField dampField;

	private JComboBox<String> lfoCombo;

	private static final int SAMPLERATE = 32768;

	public OilCanDelayControlPanel(OilCanDelayCADBlock b) {
		this.block = b;
		this.setTitle("Oil Can Delay");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		// --- Delay Time ---
		delayField = makeField();
		delaySlider = new FineControlSlider(JSlider.HORIZONTAL,
				OilCanDelayCADBlock.getMinDelay(),
				OilCanDelayCADBlock.getMaxDelay(),
				block.getDelayLength());
		delaySlider.addChangeListener(this);
		this.getContentPane().add(delayField);
		this.getContentPane().add(delaySlider);

		// --- Sync Ratio ---
		ratioField = makeField();
		ratioSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 4, block.getRatio());
		ratioSlider.addChangeListener(this);
		this.getContentPane().add(ratioField);
		this.getContentPane().add(ratioSlider);

		// --- Mod Depth (slider in tenths of ms: 5..200 → 0.5..20.0 ms) ---
		modDepthField = makeField();
		modDepthSlider = new FineControlSlider(JSlider.HORIZONTAL, 5, 200,
				(int)(block.getModDepth() * 10));
		modDepthSlider.addChangeListener(this);
		this.getContentPane().add(modDepthField);
		this.getContentPane().add(modDepthSlider);

		// --- Feedback ---
		fbkField = makeField();
		fbkSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 95,
				(int)(block.getFbkGain() * 100));
		fbkSlider.addChangeListener(this);
		this.getContentPane().add(fbkField);
		this.getContentPane().add(fbkSlider);

		// --- Damping ---
		dampField = makeField();
		dampSlider = new FineControlSlider(JSlider.HORIZONTAL, 200, 8000,
				(int) block.getDampFreq());
		dampSlider.addChangeListener(this);
		this.getContentPane().add(dampField);
		this.getContentPane().add(dampSlider);

		// --- LFO selector ---
		String[] lfoChoices = { "SIN LFO 0", "SIN LFO 1" };
		lfoCombo = new JComboBox<>(lfoChoices);
		lfoCombo.setSelectedIndex(block.getLfoSel());
		lfoCombo.addActionListener(this);
		this.getContentPane().add(lfoCombo);

		updateAllLabels();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
	}

	private JTextField makeField() {
		JTextField f = new JTextField();
		f.setHorizontalAlignment(JTextField.CENTER);
		f.setEditable(false);
		return f;
	}

	// ---- slider changes ----

	@Override
	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == delaySlider) {
			block.setDelayLength(delaySlider.getValue());
			updateDelayLabel();
		} else if (ce.getSource() == ratioSlider) {
			block.setRatio(ratioSlider.getValue());
			updateRatioLabel();
		} else if (ce.getSource() == modDepthSlider) {
			block.setModDepth(modDepthSlider.getValue() / 10.0);
			updateModDepthLabel();
		} else if (ce.getSource() == fbkSlider) {
			block.setFbkGain(fbkSlider.getValue() / 100.0);
			updateFbkLabel();
		} else if (ce.getSource() == dampSlider) {
			block.setDampFreq(dampSlider.getValue());
			updateDampLabel();
		}
	}

	// ---- combo box changes ----

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == lfoCombo) {
			block.setLfoSel(lfoCombo.getSelectedIndex());
		}
	}

	// ---- label helpers ----

	private void updateAllLabels() {
		updateDelayLabel();
		updateRatioLabel();
		updateModDepthLabel();
		updateFbkLabel();
		updateDampLabel();
	}

	private void updateDelayLabel() {
		double ms = block.getDelayLength() * 1000.0 / SAMPLERATE;
		double syncHz = block.getRatio() * SAMPLERATE / (2.0 * block.getDelayLength());
		delayField.setText(String.format("Delay Time  %.0f ms  (LFO %.1f Hz)", ms, syncHz));
	}

	private void updateRatioLabel() {
		ratioField.setText(String.format("Sync Ratio  %d", block.getRatio()));
		updateDelayLabel(); // LFO Hz changes too
	}

	private void updateModDepthLabel() {
		modDepthField.setText(String.format("Mod Depth  \u00b1%.1f ms", block.getModDepth()));
	}

	private void updateFbkLabel() {
		double dB = 20.0 * Math.log10(Math.max(block.getFbkGain(), 0.001));
		fbkField.setText(String.format("Feedback  %.0f%%  (%.1f dB)", block.getFbkGain() * 100, dB));
	}

	private void updateDampLabel() {
		dampField.setText(String.format("Damping  %.0f Hz", block.getDampFreq()));
	}
}
