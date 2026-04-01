/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * control_adjustable_change_detectControlPanel.java
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

public class control_adjustable_change_detectControlPanel {
	private JDialog frame;

	private control_adjustable_change_detectCADBlock gCB;

	FineControlSlider filtSlider;
	JTextField filtField;

	public control_adjustable_change_detectControlPanel(control_adjustable_change_detectCADBlock genericCADBlock) {

		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Adjustable Change Detect");
				gCB.controlPanelFrame = frame;
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				// Slider: log scale for cutoff frequency in Hz
				double maxFreq = 35.0;  // fast end
				double minCoeff = 1.0 / 16384.0;  // smallest S1.14 coefficient
				double minFreq = SpinCADBlock.filtToFreq(minCoeff);  // slow end
				int leftLimit = SpinCADBlock.logvalToSlider(minFreq, 100.0);
				int rightLimit = SpinCADBlock.logvalToSlider(maxFreq, 100.0);
				double initFreq = SpinCADBlock.filtToFreq(gCB.getfilt());
				int initial = SpinCADBlock.logvalToSlider(initFreq, 100.0);
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
							gCB.setfilt(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(sliderVal, 100.0)));
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
				double freq = SpinCADBlock.sliderToLogval(filtSlider.getValue(), 100.0);
				gCB.setfilt(SpinCADBlock.freqToFilt(freq));
				updatefiltLabel();
			}
		}
	}

	private void updatefiltLabel() {
		filtField.setText("Cutoff Frequency (Hz) " + String.format("%4.2f", SpinCADBlock.filtToFreq(gCB.getfilt())));
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
