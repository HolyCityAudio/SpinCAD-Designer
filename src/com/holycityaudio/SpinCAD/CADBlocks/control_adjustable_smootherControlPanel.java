/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * control_adjustable_smootherControlPanel.java
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
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class control_adjustable_smootherControlPanel {
	private JDialog frame;

	private control_adjustable_smootherCADBlock gCB;

	FineControlSlider filtSlider;
	JTextField filtField;

	public control_adjustable_smootherControlPanel(control_adjustable_smootherCADBlock genericCADBlock) {

		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Adjustable Smoother");
				gCB.controlPanelFrame = frame;
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				// Slider: log scale for rise time in ms
				double maxFreq = 35.0;  // fast end (~10 ms)
				double minCoeff = 1.0 / 16384.0;  // smallest S1.14 coefficient
				double minFreq = SpinCADBlock.filtToFreq(minCoeff);  // slow end
				double timeLowMs = 350.0 / maxFreq;
				double timeHighMs = 350.0 / minFreq;
				double initTimeMs = filtToRiseTimeMs();
				int leftLimit = SpinCADBlock.logvalToSlider(timeLowMs, 100.0);
				int rightLimit = SpinCADBlock.logvalToSlider(timeHighMs, 100.0);
				int initial = SpinCADBlock.logvalToSlider(initTimeMs, 100.0);
				initial = Math.max(leftLimit, Math.min(rightLimit, initial));
				filtSlider = new FineControlSlider(JSlider.HORIZONTAL, leftLimit, rightLimit, initial);

				filtSlider.addChangeListener(new filtChangeListener());
				filtField = new JTextField();
				filtField.setHorizontalAlignment(JTextField.CENTER);
				Border filtBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				filtField.setBorder(filtBorder1);
				filtField.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							double val = Double.parseDouble(filtField.getText().replaceAll("[^0-9.\\-]", ""));
							int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
							sliderVal = Math.max(filtSlider.getMinimum(), Math.min(filtSlider.getMaximum(), sliderVal));
							filtSlider.setValue(sliderVal);
							double timeMs = SpinCADBlock.sliderToLogval(sliderVal, 100.0);
							gCB.setfilt(SpinCADBlock.freqToFilt(350.0 / timeMs));
							updatefiltLabel();
						} catch (NumberFormatException ex) {
							updatefiltLabel();
						}
					}
				});
				updatefiltLabel();

				Border filtBorder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel filtInnerPanel = new JPanel();

				filtInnerPanel.setLayout(new BoxLayout(filtInnerPanel, BoxLayout.Y_AXIS));
				filtInnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				filtInnerPanel.add(filtField);
				filtInnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				filtInnerPanel.add(filtSlider);
				filtInnerPanel.setBorder(filtBorder2);

				frame.add(filtInnerPanel);

				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				gCB.positionControlPanel(frame);
				frame.setVisible(true);
			}
		});
	}

	class filtChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == filtSlider) {
				double timeMs = SpinCADBlock.sliderToLogval(filtSlider.getValue(), 100.0);
				gCB.setfilt(SpinCADBlock.freqToFilt(350.0 / timeMs));
				updatefiltLabel();
			}
		}
	}

	private double filtToRiseTimeMs() {
		return 350.0 / SpinCADBlock.filtToFreq(gCB.getfilt());
	}

	private void updatefiltLabel() {
		filtField.setText("Smoothing Time (ms) " + String.format("%4.1f", filtToRiseTimeMs()));
	}

	class MyWindowListener implements WindowListener
	{
		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			gCB.clearCP();
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {

		}

		@Override
		public void windowOpened(WindowEvent arg0) {
		}
	}
}
