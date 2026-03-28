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
class SingleDelayControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider fbSlider;
	JSlider lengthSlider;

	JTextField fbField;
	JTextField lengthField;

	private SingleDelayCADBlock delay;

	public SingleDelayControlPanel(SingleDelayCADBlock singleDelayCADBlock) {
		this.delay = singleDelayCADBlock;
		this.setTitle("Single Delay");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);


		fbSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 800, 0);
		fbSlider.addChangeListener(this);

		lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 800, 0);
		lengthSlider.addChangeListener(this);

		fbField = new JTextField();
		fbField.setHorizontalAlignment(JTextField.CENTER);
		fbField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(fbField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(8.0, val));
					delay.setfbLevel(val);
					fbSlider.setValue((int) Math.round(val * 100.0));
					updateFbField();
				} catch (NumberFormatException ex) {
					updateFbField();
				}
			}
		});

		lengthField = new JTextField();
		lengthField.setHorizontalAlignment(JTextField.CENTER);
		lengthField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int val = Integer.parseInt(lengthField.getText().replaceAll("[^0-9.\\-]", "").split("\\.")[0]);
					val = Math.max(0, Math.min(800, val));
					delay.setDelayTime(val);
					lengthSlider.setValue(val);
					updateLengthField();
				} catch (NumberFormatException ex) {
					updateLengthField();
				}
			}
		});

		this.getContentPane().add(fbField);
		this.getContentPane().add(fbSlider);

		this.getContentPane().add(lengthField);
		this.getContentPane().add(lengthSlider);

		fbSlider.setValue((int)Math.round((singleDelayCADBlock.getfbLevel() * 100.0)));
		lengthSlider.setValue(singleDelayCADBlock.getDelayTime());

		updateFbField();
		updateLengthField();

		this.setVisible(true);
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
		this.setAlwaysOnTop(true);
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == fbSlider) {
			delay.setfbLevel((double)fbSlider.getValue() / 100.0);
			updateFbField();
		}
		else if(ce.getSource() == lengthSlider) {
			delay.setDelayTime(lengthSlider.getValue());
			updateLengthField();
		}
	}

	private void updateFbField() {
		fbField.setText("Feedback level " + String.format("%2.2f", delay.getfbLevel()));
	}

	private void updateLengthField() {
		lengthField.setText("Delay (msec) " + String.format("%3d", delay.getDelayTime()));
	}
}