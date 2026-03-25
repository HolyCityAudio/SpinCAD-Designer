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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;

@SuppressWarnings("serial")
class PingPongControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider tapSlider0;
	JSlider tapSlider1;

	JSlider fbSlider;
	JSlider delayGainSlider;
	JSlider lengthSlider;

	JTextField tapField0;
	JTextField tapField1;


	JTextField fbField;
	JTextField delayGainField;
	JTextField lengthField;

	private PingPongCADBlock pong;

	public PingPongControlPanel(PingPongCADBlock ppcb) {
		this.pong = ppcb;
		this.setTitle("Ping Pong Delay");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		tapSlider0 = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider0.addChangeListener(this);

		tapSlider1 = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider1.addChangeListener(this);

		fbSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 90, 0);
		fbSlider.addChangeListener(this);

		delayGainSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 99, 0);
		delayGainSlider.addChangeListener(this);

		lengthSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 800, 0);
		lengthSlider.addChangeListener(this);

		tapField0 = new JTextField();
		tapField0.setHorizontalAlignment(JTextField.CENTER);
		tapField0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(tapField0.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(1.0, val));
					pong.setTapLevel(0, val);
					tapSlider0.setValue((int) Math.round(val * 100.0));
					updateTap0Label();
				} catch (NumberFormatException ex) {
					updateTap0Label();
				}
			}
		});

		tapField1 = new JTextField();
		tapField1.setHorizontalAlignment(JTextField.CENTER);
		tapField1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(tapField1.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(1.0, val));
					pong.setTapLevel(1, val);
					tapSlider1.setValue((int) Math.round(val * 100.0));
					updateTap1Label();
				} catch (NumberFormatException ex) {
					updateTap1Label();
				}
			}
		});

		fbField = new JTextField();
		fbField.setHorizontalAlignment(JTextField.CENTER);
		fbField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(fbField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(0.90, val));
					pong.setfbLevel(val);
					fbSlider.setValue((int) Math.round(val * 100.0));
					updateFbLabel();
				} catch (NumberFormatException ex) {
					updateFbLabel();
				}
			}
		});

		delayGainField = new JTextField();
		delayGainField.setHorizontalAlignment(JTextField.CENTER);
		delayGainField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(delayGainField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(0.99, val));
					pong.setDelayGain(val);
					delayGainSlider.setValue((int) Math.round(val * 100.0));
					updateDelayGainLabel();
				} catch (NumberFormatException ex) {
					updateDelayGainLabel();
				}
			}
		});

		lengthField = new JTextField();
		lengthField.setHorizontalAlignment(JTextField.CENTER);
		lengthField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(lengthField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(0.8, val));
					pong.setLength(val);
					lengthSlider.setValue((int) Math.round(val * 1000.0));
					updateLengthLabel();
				} catch (NumberFormatException ex) {
					updateLengthLabel();
				}
			}
		});

		this.getContentPane().add(tapField0);
		this.getContentPane().add(tapSlider0);

		this.getContentPane().add(tapField1);
		this.getContentPane().add(tapSlider1);

		this.getContentPane().add(fbField);
		this.getContentPane().add(fbSlider);
		this.getContentPane().add(delayGainField);
		this.getContentPane().add(delayGainSlider);

		this.getContentPane().add(lengthField);
		this.getContentPane().add(lengthSlider);

		tapSlider0.setValue((int)Math.round((ppcb.getTapLevel(0) * 100.0)));
		tapSlider1.setValue((int)Math.round((ppcb.getTapLevel(1) * 100.0)));

		fbSlider.setValue((int)Math.round((ppcb.getfbLevel() * 100.0)));
		delayGainSlider.setValue((int)Math.round((ppcb.getDelayGain() * 100.0)));
		lengthSlider.setValue((int)Math.round((ppcb.getLength() * 1000.0)));

		updateTap0Label();
		updateTap1Label();
		updateFbLabel();
		updateDelayGainLabel();
		updateLengthLabel();

		this.setVisible(true);
		this.pack();
		this.setLocation(new Point(pong.getX() + 200, pong.getY() + 150));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == tapSlider0) {
			pong.setTapLevel(0, (double)tapSlider0.getValue() / 100.0);
			updateTap0Label();
		}
		else if(ce.getSource() == tapSlider1) {
			pong.setTapLevel(1, (double)tapSlider1.getValue() / 100.0);
			updateTap1Label();
		}
		else if(ce.getSource() == fbSlider) {
			pong.setfbLevel((double)fbSlider.getValue() / 100.0);
			updateFbLabel();
		}
		else if(ce.getSource() == delayGainSlider) {
			pong.setDelayGain((double)delayGainSlider.getValue() / 100.0);
			updateDelayGainLabel();
		}
		else if(ce.getSource() == lengthSlider) {
			pong.setLength((double)lengthSlider.getValue() / 1000.0);
			updateLengthLabel();
		}
	}

	private void updateTap0Label() {
		tapField0.setText("Tap 1 level " + String.format("%2.2f", pong.getTapLevel(0)));
	}

	private void updateTap1Label() {
		tapField1.setText("Tap 2 level " + String.format("%2.2f", pong.getTapLevel(1)));
	}

	private void updateFbLabel() {
		fbField.setText("Feedback level " + String.format("%2.2f", pong.getfbLevel()));
	}

	private void updateDelayGainLabel() {
		delayGainField.setText("Delay Gain " + String.format("%2.2f", pong.getDelayGain()));
	}

	private void updateLengthLabel() {
		lengthField.setText("Delay (sec) " + String.format("%1.3f", pong.getLength()));
	}
}
