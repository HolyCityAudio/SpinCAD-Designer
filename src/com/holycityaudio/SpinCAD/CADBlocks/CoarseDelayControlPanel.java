/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * ModDelayControlPanel.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * SpinCAD Designer is based on ElmGenby Andrew Kilpatrick.
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

import org.andrewkilpatrick.elmGen.ElmProgram;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

class CoarseDelayControlPanel {

	private CoarseDelayCADBlock mD;
	private FineControlSlider delaySliderCoarse;
	private FineControlSlider delaySliderFine;
	private JTextField delayFieldCoarse;
	private JTextField delayFieldFine;
	private JDialog frame;

	public CoarseDelayControlPanel(CoarseDelayCADBlock sDCB) {
		this.mD = sDCB;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JDialog(SpinCADFrame.getInstance(), "Servo Delay");
				mD.controlPanelFrame = frame;
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				// XXX debug, this may not be correct
				int timeCoarse = calcDelayTimeCoarse(mD.getDelayLength());
				delaySliderCoarse = new FineControlSlider(JSlider.HORIZONTAL, 0, calcDelayTimeCoarse(32767), timeCoarse);

				delaySliderCoarse.addChangeListener(new bitSliderListener());
				delayFieldCoarse = new JTextField();
				delayFieldCoarse.setHorizontalAlignment(JTextField.CENTER);
				delayFieldCoarse.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = delayFieldCoarse.getText().replaceAll("[^\\d.\\-]", "");
							int val = (int) Math.round(Double.parseDouble(text));
							val = Math.max(delaySliderCoarse.getMinimum(), Math.min(delaySliderCoarse.getMaximum(), val));
							delaySliderCoarse.setValue(val);
							int totalDelay = (int)(((delaySliderCoarse.getValue() + delaySliderFine.getValue()) * ElmProgram.getSamplerate())/1000.0);
							mD.setDelayLength(totalDelay);
							updateDelayFieldCoarse();
						} catch (NumberFormatException ex) {
							updateDelayFieldCoarse();
						}
					}
				});
				frame.add(delayFieldCoarse);
				frame.add(delaySliderCoarse);

				updateDelayFieldCoarse();

				int timeFine = calcDelayTimeFine(mD.getDelayLength());
				delaySliderFine = new FineControlSlider(JSlider.HORIZONTAL, 0, 25, timeFine);
				delaySliderFine.addChangeListener(new bitSliderListener());
				delayFieldFine = new JTextField();
				delayFieldFine.setHorizontalAlignment(JTextField.CENTER);
				delayFieldFine.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = delayFieldFine.getText().replaceAll("[^\\d.\\-]", "");
							int val = (int) Math.round(Double.parseDouble(text));
							val = Math.max(delaySliderFine.getMinimum(), Math.min(delaySliderFine.getMaximum(), val));
							delaySliderFine.setValue(val);
							int totalDelay = (int)(((delaySliderCoarse.getValue() + delaySliderFine.getValue()) * ElmProgram.getSamplerate())/1000.0);
							mD.setDelayLength(totalDelay);
							updateDelayFieldFine();
						} catch (NumberFormatException ex) {
							updateDelayFieldFine();
						}
					}
				});
				frame.add(delayFieldFine);
				frame.add(delaySliderFine);
				updateDelayFieldFine();

				frame.setVisible(true);
				frame.pack();
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
			}
		});

	}

	class bitSliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
// XXX this needs to be reworks, it is not accurate
			int totalDelay = (int)(((delaySliderCoarse.getValue()+ delaySliderFine.getValue()) * ElmProgram.getSamplerate())/1000.0);
			if(ce.getSource() == delaySliderCoarse) {
				mD.setDelayLength(totalDelay);
				updateDelayFieldCoarse();
			}
			if(ce.getSource() == delaySliderFine) {
				mD.setDelayLength(totalDelay);
				updateDelayFieldFine();
			}
		}
	}

	public void updateDelayFieldCoarse() {
		// ---
		delayFieldCoarse.setText("Delay (coarse): " + String.format("%d ms", calcDelayTimeCoarse(mD.getDelayLength())));

	}

	public void updateDelayFieldFine() {
		// ---
		delayFieldFine.setText("Delay (fine): " + String.format("%d ms", calcDelayTimeFine(mD.getDelayLength())));

	}

	private int calcDelayTimeCoarse(int length) {
		int l = (int) ((length * 1000)/ElmProgram.getSamplerate()/25) * 25;
		return l;
	}

	private int calcDelayTimeFine(int length) {
		int l = (int) (((length * 1000)/ElmProgram.getSamplerate()) % 25);
		return l;
	}
}