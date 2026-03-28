/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BPFControlPanel.java
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * SpinCAD Designer is based on ElmGenby Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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

class BassmanEQControlPanel {
	private JFrame frame;

	FineControlSlider lSlider;	// "low"
	JTextField lField;

	FineControlSlider mSlider;	// "mid"
	JTextField mField;

	FineControlSlider tSlider;	// "treble"
	JTextField tField;

	private BassmanEQCADBlock spbBMEQ;

	public BassmanEQControlPanel(BassmanEQCADBlock bassmanEQCADBlock) {

		spbBMEQ = bassmanEQCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Bassman '59 EQ");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				lSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) (100 * spbBMEQ.getBass()));
				mSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) (100 * spbBMEQ.getMid()));
				tSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) (100 * spbBMEQ.getTreble()));

				BassmanEQSliderListener bEQSL = new BassmanEQSliderListener();

				lSlider.addChangeListener(bEQSL);
				mSlider.addChangeListener(bEQSL);
				tSlider.addChangeListener(bEQSL);

				lField = new JTextField();
				lField.setHorizontalAlignment(JTextField.CENTER);
				lField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = lField.getText().replaceAll("[^\\d.\\-]", "");
							double displayVal = Double.parseDouble(text);
							// display shows value*10, so real = displayVal/10, slider = real*100
							int sliderVal = (int) Math.round(displayVal * 10.0);
							sliderVal = Math.max(lSlider.getMinimum(), Math.min(lSlider.getMaximum(), sliderVal));
							spbBMEQ.setBass(sliderVal / 100.0);
							lSlider.setValue(sliderVal);
							updateBassField();
						} catch (NumberFormatException ex) {
							updateBassField();
						}
					}
				});

				mField = new JTextField();
				mField.setHorizontalAlignment(JTextField.CENTER);
				mField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = mField.getText().replaceAll("[^\\d.\\-]", "");
							double displayVal = Double.parseDouble(text);
							int sliderVal = (int) Math.round(displayVal * 10.0);
							sliderVal = Math.max(mSlider.getMinimum(), Math.min(mSlider.getMaximum(), sliderVal));
							spbBMEQ.setMid(sliderVal / 100.0);
							mSlider.setValue(sliderVal);
							updateMidField();
						} catch (NumberFormatException ex) {
							updateMidField();
						}
					}
				});

				tField = new JTextField();
				tField.setHorizontalAlignment(JTextField.CENTER);
				tField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = tField.getText().replaceAll("[^\\d.\\-]", "");
							double displayVal = Double.parseDouble(text);
							int sliderVal = (int) Math.round(displayVal * 10.0);
							sliderVal = Math.max(tSlider.getMinimum(), Math.min(tSlider.getMaximum(), sliderVal));
							spbBMEQ.setTreble(sliderVal / 100.0);
							tSlider.setValue(sliderVal);
							updateTrebleField();
						} catch (NumberFormatException ex) {
							updateTrebleField();
						}
					}
				});

				frame.getContentPane().add(lField);
				updateBassField();
				frame.getContentPane().add(lSlider);

				frame.getContentPane().add(mField);
				updateMidField();
				frame.getContentPane().add(mSlider);

				frame.getContentPane().add(tField);
				updateTrebleField();
				frame.getContentPane().add(tSlider);

				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
			}
		});
	}

	class BassmanEQSliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == lSlider) {
				spbBMEQ.setBass((double) (lSlider.getValue()/100.0));
				updateBassField();
			}
			else if(ce.getSource() == mSlider) {
				int resValue = mSlider.getValue();
				double mid = (double) (resValue/100.0);
				spbBMEQ.setMid(mid);
				updateMidField();
			}
			else if(ce.getSource() == tSlider) {
				int trebValue = tSlider.getValue();
				double treble = (double) (trebValue/100.0);
				spbBMEQ.setTreble(treble);
				updateTrebleField();
			}
		}
	}

	private void updateBassField() {
		lField.setText("Bass " + String.format("%4.2f", spbBMEQ.getBass() * 10));
	}

	private void updateMidField() {
		mField.setText("Mid " + String.format("%4.2f", spbBMEQ.getMid() * 10));
	}

	private void updateTrebleField() {
		tField.setText("Treble " + String.format("%4.2f", spbBMEQ.getTreble() * 10));
	}
}
