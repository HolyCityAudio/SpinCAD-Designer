/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2026.  Look for GSW in code.
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

import org.andrewkilpatrick.elmGen.ElmProgram;
import javax.swing.JDialog;
import com.holycityaudio.SpinCAD.SpinCADFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.BoxLayout;
import javax.swing.JSlider;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.Dimension;
import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.spinCADControlPanel;

@SuppressWarnings("unused")
class SingleDelayControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private SingleDelayCADBlock gCB;
	// declare the controls
	FineControlSlider inputGainSlider;
	JTextField inputGainField;
	FineControlSlider fbkGainSlider;
	JTextField fbkGainField;
	FineControlSlider delayLengthSlider;
	JTextField delayLengthField;

	public SingleDelayControlPanel(SingleDelayCADBlock genericCADBlock) {

		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "Single Delay");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				// Input Gain slider (dB): -24 to 0 dB, 1 dB normal drag, 0.1 dB fine (Ctrl+drag)
				inputGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getinputGain()) * 10.0));
				inputGainSlider.setSubdivision(10);
				inputGainSlider.addChangeListener(new SingleDelayListener());
				inputGainField = new JTextField();
				inputGainField.setHorizontalAlignment(JTextField.CENTER);
				Border inputGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				inputGainField.setBorder(inputGainBorder1);
				inputGainField.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							double val = Double.parseDouble(inputGainField.getText().replaceAll("[^0-9.\\-]", ""));
							int sliderVal = (int) Math.round(val * 10.0);
							sliderVal = Math.max(inputGainSlider.getMinimum(), Math.min(inputGainSlider.getMaximum(), sliderVal));
							inputGainSlider.setValue(sliderVal);
							gCB.setinputGain((double) sliderVal / 10.0);
							updateinputGainLabel();
						} catch (NumberFormatException ex) {
							updateinputGainLabel();
						}
					}
				});
				updateinputGainLabel();

				Border inputGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel inputGaininnerPanel = new JPanel();

				inputGaininnerPanel.setLayout(new BoxLayout(inputGaininnerPanel, BoxLayout.Y_AXIS));
				inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				inputGaininnerPanel.add(inputGainField);
				inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				inputGaininnerPanel.add(inputGainSlider);
				inputGaininnerPanel.setBorder(inputGainborder2);

				frame.add(inputGaininnerPanel);

				// Feedback Gain slider (dB): -24 to 0 dB, 1 dB normal drag, 0.1 dB fine (Ctrl+drag)
				fbkGainSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(-24 * 10.0),(int) (0 * 10.0), (int) (20 * Math.log10(gCB.getfbkGain()) * 10.0));
				fbkGainSlider.setSubdivision(10);
				fbkGainSlider.addChangeListener(new SingleDelayListener());
				fbkGainField = new JTextField();
				fbkGainField.setHorizontalAlignment(JTextField.CENTER);
				Border fbkGainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				fbkGainField.setBorder(fbkGainBorder1);
				fbkGainField.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							double val = Double.parseDouble(fbkGainField.getText().replaceAll("[^0-9.\\-]", ""));
							int sliderVal = (int) Math.round(val * 10.0);
							sliderVal = Math.max(fbkGainSlider.getMinimum(), Math.min(fbkGainSlider.getMaximum(), sliderVal));
							fbkGainSlider.setValue(sliderVal);
							gCB.setfbkGain((double) sliderVal / 10.0);
							updatefbkGainLabel();
						} catch (NumberFormatException ex) {
							updatefbkGainLabel();
						}
					}
				});
				updatefbkGainLabel();

				Border fbkGainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel fbkGaininnerPanel = new JPanel();

				fbkGaininnerPanel.setLayout(new BoxLayout(fbkGaininnerPanel, BoxLayout.Y_AXIS));
				fbkGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				fbkGaininnerPanel.add(fbkGainField);
				fbkGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				fbkGaininnerPanel.add(fbkGainSlider);
				fbkGaininnerPanel.setBorder(fbkGainborder2);

				frame.add(fbkGaininnerPanel);

				// Delay Length slider: 0 to 32767 samples
				delayLengthSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (32767 * 1), (int) (gCB.getdelayLength() * 1));
				delayLengthSlider.addChangeListener(new SingleDelayListener());
				delayLengthField = new JTextField();
				delayLengthField.setHorizontalAlignment(JTextField.CENTER);
				Border delayLengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				delayLengthField.setBorder(delayLengthBorder1);
				delayLengthField.addActionListener(new java.awt.event.ActionListener() {
					@Override
					public void actionPerformed(java.awt.event.ActionEvent e) {
						try {
							double val = Double.parseDouble(delayLengthField.getText().replaceAll("[^0-9.\\-]", ""));
							double samples = val * ElmProgram.getSamplerate() / 1000.0;
							int sliderVal = (int) Math.round(samples * 1);
							sliderVal = Math.max(delayLengthSlider.getMinimum(), Math.min(delayLengthSlider.getMaximum(), sliderVal));
							delayLengthSlider.setValue(sliderVal);
							gCB.setdelayLength((double) sliderVal / 1);
							updatedelayLengthLabel();
						} catch (NumberFormatException ex) {
							updatedelayLengthLabel();
						}
					}
				});
				updatedelayLengthLabel();

				Border delayLengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel delayLengthinnerPanel = new JPanel();

				delayLengthinnerPanel.setLayout(new BoxLayout(delayLengthinnerPanel, BoxLayout.Y_AXIS));
				delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				delayLengthinnerPanel.add(delayLengthField);
				delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
				delayLengthinnerPanel.add(delayLengthSlider);
				delayLengthinnerPanel.setBorder(delayLengthborder2);

				frame.add(delayLengthinnerPanel);

				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);
			}
		});
	}

	// change listener for sliders
	class SingleDelayListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
				gCB.setinputGain((double) (inputGainSlider.getValue()/10.0));
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
				gCB.setfbkGain((double) (fbkGainSlider.getValue()/10.0));
				updatefbkGainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
				gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));
				updatedelayLengthLabel();
			}
		}
	}

	private void updateinputGainLabel() {
		inputGainField.setText("Input Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));
	}

	private void updatefbkGainLabel() {
		fbkGainField.setText("Feedback Gain:  " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));
	}

	private void updatedelayLengthLabel() {
		delayLengthField.setText("Delay Time (ms):  " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));
	}

	class MyWindowListener implements WindowListener {
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
