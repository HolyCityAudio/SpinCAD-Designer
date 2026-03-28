/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * LPF1PControlPanel.java
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

import java.awt.Point;
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


class HPF1PControlPanel extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = -2288952347754535913L;

	FineControlSlider freqSlider;
	JTextField freqField;

	private HPF1PCADBlock HPF;

	public HPF1PControlPanel(HPF1PCADBlock hpf1pcadBlock) {
		this.HPF = hpf1pcadBlock;
		createAndShowUI();
	}

	private void createAndShowUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("High pass 1 pole");
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				freqSlider = new FineControlSlider(JSlider.HORIZONTAL, 800, 25000, 10000);
				freqSlider.addChangeListener(new LPF1PChangeListener());

				freqField = new JTextField();
				freqField.setHorizontalAlignment(JTextField.CENTER);
				freqField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = freqField.getText().replaceAll("[^\\d.\\-]", "");
							double val = Double.parseDouble(text);
							val = Math.max(80.0, Math.min(2500.0, val));
							HPF.setFreq(val);
							freqSlider.setValue((int) Math.round(val * 10));
							updateFreqLabel();
						} catch (NumberFormatException ex) {
							updateFreqLabel();
						}
					}
				});

				getContentPane().add(freqField);
				getContentPane().add(freqSlider);

				freqSlider.setValue((int)Math.round(HPF.getFreq() * 10));
				updateFreqLabel();
				setAlwaysOnTop(true);
				setVisible(true);
				setLocationRelativeTo(SpinCADFrame.getInstance());
				pack();
				setResizable(false);
			}
		});
	}

	class LPF1PChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqSlider) {
				HPF.setFreq((double) freqSlider.getValue() / 10.0);
				updateFreqLabel();
			}
		}
	}

	private void updateFreqLabel() {
		freqField.setText("Frequency " + String.format("%4.1f", HPF.getFreq()));
	}

}
