/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BitCrusherControlPanel.java
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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

class QuantizerControlPanel {

	private QuantizerCADBlock BC;
	private JSlider bitSlider;
	private JTextField bitField;
	private JFrame frame;

	public QuantizerControlPanel(QuantizerCADBlock b) {
		this.BC = b;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setTitle("Quantizer");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				bitSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 16, BC.getBits());
				bitSlider.addChangeListener(new bitSliderListener());
				bitField = new JTextField();
				bitField.setHorizontalAlignment(JTextField.CENTER);
				bitField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							int val = Integer.parseInt(bitField.getText().replaceAll("[^0-9.\\-]", "").split("\\.")[0]);
							val = Math.max(1, Math.min(16, val));
							BC.setBits(val);
							bitSlider.setValue(val);
							updateBitLabel();
						} catch (NumberFormatException ex) {
							updateBitLabel();
						}
					}
				});
				frame.add(bitField);
				frame.add(bitSlider);
				updateBitLabel();
				frame.setAlwaysOnTop(true);
				frame.setVisible(true);
				frame.pack();
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
			}
		});

	}
	class bitSliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == bitSlider) {
				BC.setBits(bitSlider.getValue());
				updateBitLabel();
			}
		}
	}

	public void updateBitLabel() {
		// ---
		bitField.setText("Bits: " + String.format("%d", BC.getBits()));

	}

	public void stateChanged(ChangeEvent ce) {
	}
}
