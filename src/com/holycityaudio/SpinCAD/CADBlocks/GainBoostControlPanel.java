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

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class GainBoostControlPanel implements ChangeListener {

	private FineControlSlider gainSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 8, 1);
	private JTextField gainField = new JTextField();
	private JFrame frame;

	private GainBoostCADBlock pC;

	public GainBoostControlPanel(GainBoostCADBlock gainBoostCADBlock) {
		gainSlider.addChangeListener(this);

		this.pC = gainBoostCADBlock;

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

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Gain Boost");
				pC.controlPanelFrame = frame;
				frame.setTitle("Gain Boost");
				frame.setResizable(false);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gainSlider.setMajorTickSpacing(25);

				frame.add(gainField);
				frame.add(gainSlider);

				gainSlider.setValue((int) Math.round(pC.getGain()));
				updateGainField();

				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);
				frame.pack();
			}
		});
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == gainSlider) {
			pC.setGain(gainSlider.getValue());
			updateGainField();
		}
	}

	public void updateGainField() {
		gainField.setText(String.format("Gain: %2d dB", pC.getGain() * 6));
	}
}