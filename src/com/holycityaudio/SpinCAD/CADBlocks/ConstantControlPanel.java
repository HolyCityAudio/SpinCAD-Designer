/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
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
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

@SuppressWarnings("serial")
public class ConstantControlPanel extends JDialog implements ChangeListener {
	FineControlSlider constantSlider;
	JTextField constantField;

	private ConstantCADBlock sof;

	public ConstantControlPanel(final ConstantCADBlock cCB) {
		super(SpinCADFrame.getInstance(), "Constant");
		constantSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 999, 0);
		constantSlider.addChangeListener(this);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sof = cCB;
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				constantField = new JTextField();
				constantField.setHorizontalAlignment(JTextField.CENTER);
				constantField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = constantField.getText().replaceAll("[^\\d.\\-]", "");
							double displayVal = Double.parseDouble(text);
							// display shows sof.getConstant()/1000.0, so constant = displayVal * 1000
							int sliderVal = (int) Math.round(displayVal * 1000.0);
							sliderVal = Math.max(constantSlider.getMinimum(), Math.min(constantSlider.getMaximum(), sliderVal));
							sof.setConstant(sliderVal);
							constantSlider.setValue(sliderVal);
							updateConstantField();
						} catch (NumberFormatException ex) {
							updateConstantField();
						}
					}
				});

				getContentPane().add(constantField);
				getContentPane().add(constantSlider);

				constantSlider.setValue((int)Math.round((cCB.getConstant())));
				updateConstantField();

				setVisible(true);
				pack();
				setLocationRelativeTo(SpinCADFrame.getInstance());
				setResizable(false);
			}
		});
	}

	@Override

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == constantSlider) {
			sof.setConstant(constantSlider.getValue());
			updateConstantField();
		}

	}

	private void updateConstantField() {
		constantField.setText("Value: " + String.format("%3.3f", sof.getConstant()/1000.0));
	}
}