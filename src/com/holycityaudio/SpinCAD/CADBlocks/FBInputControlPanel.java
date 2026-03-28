/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
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
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

class FBInputControlPanel extends JFrame implements ChangeListener, ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -5986441333071288546L;
	FineControlSlider lGainSlider;
	JTextField lGainField;

	private FBInputCADBlock inBlock;

	public FBInputControlPanel(FBInputCADBlock fbInputCADBlock) {
		inBlock = fbInputCADBlock;
		this.setTitle(inBlock.getName());
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		lGainSlider = new FineControlSlider(JSlider.HORIZONTAL, -190, 190, 0);
		lGainSlider.addChangeListener(this);

		lGainField = new JTextField();
		lGainField.setHorizontalAlignment(JTextField.CENTER);
		lGainField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = lGainField.getText().replaceAll("[^\\d.\\-]", "");
					double val = Double.parseDouble(text);
					// display shows inBlock.getLGain() which is slider/100.0
					int sliderVal = (int) Math.round(val * 100.0);
					sliderVal = Math.max(lGainSlider.getMinimum(), Math.min(lGainSlider.getMaximum(), sliderVal));
					inBlock.setLGain(sliderVal / 100.0);
					lGainSlider.setValue(sliderVal);
					updateLGainField();
				} catch (NumberFormatException ex) {
					updateLGainField();
				}
			}
		});

		this.getContentPane().add(lGainField);
		this.getContentPane().add(lGainSlider);

		lGainSlider.setValue((int)Math.round(100.0 * inBlock.getLGain()));
		updateLGainField();
		this.pack();
		this.setAlwaysOnTop(true);
		this.setVisible(true);
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
		this.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == lGainSlider) {
			inBlock.setLGain((double) lGainSlider.getValue()/100.0);
			updateLGainField();
		}
	}

	private void updateLGainField() {
		lGainField.setText("Gain "
				+ String.format("%2.2f", inBlock.getLGain()));
	}
}