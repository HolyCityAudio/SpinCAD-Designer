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
class ParkerSpringReverbControlPanel extends JFrame implements ChangeListener, ActionListener {

	private FineControlSlider gainSlider;
	private JSlider reverbTimeSlider;
	private JSlider dampingSlider;
	private JSlider dispersionSlider;

	private JTextField gainField;
	private JTextField reverbTimeField;
	private JTextField dampingField;
	private JTextField dispersionField;

	private ParkerSpringReverbCADBlock block;

	public ParkerSpringReverbControlPanel(ParkerSpringReverbCADBlock blk) {
		this.block = blk;
		this.setTitle("Parker Spring Reverb");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		// Gain: -24 to 0 dB, 1 dB normal drag, 0.1 dB fine (Ctrl+drag)
		gainSlider = new FineControlSlider(JSlider.HORIZONTAL, -240, 0, 0);
		gainSlider.setSubdivision(10);
		gainSlider.addChangeListener(this);

		// Reverb Time (feedback gain): 0.10 to 0.95 (slider 10-95)
		reverbTimeSlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 95, 70);
		reverbTimeSlider.addChangeListener(this);

		// Damping (LPF coeff): 0.01 to 0.70 (slider 1-70)
		dampingSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 70, 25);
		dampingSlider.addChangeListener(this);

		// Dispersion (AP coeff): 0.30 to 0.80 (slider 30-80)
		dispersionSlider = new FineControlSlider(JSlider.HORIZONTAL, 30, 80, 60);
		dispersionSlider.addChangeListener(this);

		gainField = makeField();
		reverbTimeField = makeField();
		dampingField = makeField();
		dispersionField = makeField();

		addRow(gainField, gainSlider);
		addRow(reverbTimeField, reverbTimeSlider);
		addRow(dampingField, dampingSlider);
		addRow(dispersionField, dispersionSlider);

		// Initialize slider positions from block
		gainSlider.setValue((int) Math.round(blk.getGain() * 10.0));
		reverbTimeSlider.setValue((int) Math.round(blk.getReverbTime() * 100.0));
		dampingSlider.setValue((int) Math.round(blk.getDamping() * 100.0));
		dispersionSlider.setValue((int) Math.round(blk.getDispersion() * 100.0));

		updateAllLabels();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
	}

	private JTextField makeField() {
		JTextField f = new JTextField();
		f.setHorizontalAlignment(JTextField.CENTER);
		return f;
	}

	private void addRow(JTextField field, JSlider slider) {
		this.getContentPane().add(field);
		this.getContentPane().add(slider);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == gainSlider) {
			block.setGain(gainSlider.getValue() / 10.0);
		} else if (ce.getSource() == reverbTimeSlider) {
			block.setReverbTime(reverbTimeSlider.getValue() / 100.0);
		} else if (ce.getSource() == dampingSlider) {
			block.setDamping(dampingSlider.getValue() / 100.0);
		} else if (ce.getSource() == dispersionSlider) {
			block.setDispersion(dispersionSlider.getValue() / 100.0);
		}
		updateAllLabels();
	}

	private void updateAllLabels() {
		gainField.setText("Input Gain " + String.format("%.1f", block.getGain()) + " dB");
		reverbTimeField.setText("Reverb Time " + String.format("%.2f", block.getReverbTime()));
		dampingField.setText("Damping " + String.format("%.2f", block.getDamping()));
		dispersionField.setText("Dispersion " + String.format("%.2f", block.getDispersion()));
	}
}
