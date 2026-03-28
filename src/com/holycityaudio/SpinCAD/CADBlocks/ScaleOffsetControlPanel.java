/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;


@SuppressWarnings("serial")
public class ScaleOffsetControlPanel extends JFrame implements ChangeListener{
	JSlider inLowSlider;
	JSlider inHighSlider;
	JSlider outLowSlider;
	JSlider outHighSlider;


	JTextField inLowField;
	JTextField inHighField;
	JTextField outLowField;
	JTextField outHighField;

	JLabel scaleLabel;
	JLabel offsetLabel;

	double scale;
	double offset;

	private ScaleOffsetControlCADBlock sof;

	public ScaleOffsetControlPanel(ScaleOffsetControlCADBlock scaleOffsetControlCADBlock) {
		this.sof = scaleOffsetControlCADBlock;
		this.setTitle("Scale Offset");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

	private void createAndShowGUI() {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		inLowSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		inLowSlider.addChangeListener(this);
		inHighSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		inHighSlider.addChangeListener(this);
		outLowSlider = new FineControlSlider(JSlider.HORIZONTAL, -200, 100, 0);
		outLowSlider.addChangeListener(this);
		outHighSlider = new FineControlSlider(JSlider.HORIZONTAL, -200, 100, 0);
		outHighSlider.addChangeListener(this);

		inLowField = new JTextField();
		inLowField.setHorizontalAlignment(JTextField.CENTER);
		inLowField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(inLowField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(1.0, val));
					sof.setInLow(val);
					inLowSlider.setValue((int) Math.round(val * 100.0));
					updateInLowField();
				} catch (NumberFormatException ex) {
					updateInLowField();
				}
				updateScaleOffsetLabels();
			}
		});

		inHighField = new JTextField();
		inHighField.setHorizontalAlignment(JTextField.CENTER);
		inHighField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(inHighField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(0.0, Math.min(1.0, val));
					sof.setInHigh(val);
					inHighSlider.setValue((int) Math.round(val * 100.0));
					updateInHighField();
				} catch (NumberFormatException ex) {
					updateInHighField();
				}
				updateScaleOffsetLabels();
			}
		});

		outLowField = new JTextField();
		outLowField.setHorizontalAlignment(JTextField.CENTER);
		outLowField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(outLowField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(-2.0, Math.min(1.0, val));
					sof.setOutLow(val);
					outLowSlider.setValue((int) Math.round(val * 100.0));
					updateOutLowField();
				} catch (NumberFormatException ex) {
					updateOutLowField();
				}
				updateScaleOffsetLabels();
			}
		});

		outHighField = new JTextField();
		outHighField.setHorizontalAlignment(JTextField.CENTER);
		outHighField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(outHighField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(-2.0, Math.min(1.0, val));
					sof.setOutHigh(val);
					outHighSlider.setValue((int) Math.round(val * 100.0));
					updateOutHighField();
				} catch (NumberFormatException ex) {
					updateOutHighField();
				}
				updateScaleOffsetLabels();
			}
		});

		scaleLabel = new JLabel();
		offsetLabel = new JLabel();

		this.add(Box.createRigidArea(new Dimension(5,4)));
		this.getContentPane().add(inLowField);
		this.getContentPane().add(inLowSlider);
		this.getContentPane().add(inHighField);
		this.getContentPane().add(inHighSlider);
		this.add(Box.createRigidArea(new Dimension(5,4)));
		this.getContentPane().add(outLowField);
		this.getContentPane().add(outLowSlider);
		this.getContentPane().add(outHighField);
		this.getContentPane().add(outHighSlider);
		this.add(Box.createRigidArea(new Dimension(5,4)));
		this.getContentPane().add(scaleLabel);
		this.add(Box.createRigidArea(new Dimension(5,4)));
		this.getContentPane().add(offsetLabel);
		this.add(Box.createRigidArea(new Dimension(5,4)));

		inLowSlider.setValue((int)Math.round((sof.getInLow() * 100.0)));
		inHighSlider.setValue((int)Math.round((sof.getInHigh() * 100.0)));
		outLowSlider.setValue((int)Math.round((sof.getOutLow() * 100.0)));
		outHighSlider.setValue((int)Math.round((sof.getOutHigh() * 100.0)));
		updateInLowField();
		updateInHighField();
		updateOutLowField();
		updateOutHighField();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
		this.setResizable(false);
		this.setAlwaysOnTop(true);

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == inLowSlider) {
			sof.setInLow((double)inLowSlider.getValue() / 100.0);
			updateInLowField();
			updateScaleOffsetLabels();
		}
		else if(ce.getSource() == inHighSlider) {
			sof.setInHigh((double)inHighSlider.getValue() / 100.0);
			updateInHighField();
			updateScaleOffsetLabels();
		}
		else if(ce.getSource() == outLowSlider) {
			sof.setOutLow((double)outLowSlider.getValue() / 100.0);
			updateOutLowField();
			updateScaleOffsetLabels();
		}
		else if(ce.getSource() == outHighSlider) {
			sof.setOutHigh((double)outHighSlider.getValue() / 100.0);
			updateOutHighField();
			updateScaleOffsetLabels();
		}
	}

	private void updateInLowField() {
		inLowField.setText("Input Low " + String.format("%2.2f", sof.getInLow()));
	}

	private void updateInHighField() {
		inHighField.setText("Input High " + String.format("%2.2f", sof.getInHigh()));
	}

	private void updateOutLowField() {
		outLowField.setText("Output Low " + String.format("%2.2f", sof.getOutLow()));
	}

	private void updateOutHighField() {
		outHighField.setText("Output High " + String.format("%2.2f", sof.getOutHigh()));
	}

	public int checkValuesInRange() {
		scale = (sof.getOutHigh() - sof.getOutLow())/(sof.getInHigh() - sof.getInLow());
		offset = sof.getOutLow() - (sof.getInLow() * scale);
		if((scale < -2.0) || (scale > 1.99993896484)) {
			return -1;
		}
		else if((offset < -1.0) || (offset > 1.0)) {
			return -2;
		}
		else {
			return 0;
		}
	}

	private void updateScaleOffsetLabels() {
		int value = checkValuesInRange();
		if(value == 0) {
			scaleLabel.setForeground(Color.BLACK);
			scaleLabel.setOpaque(false);
			offsetLabel.setForeground(Color.BLACK);
			offsetLabel.setOpaque(false);
		} else if (value == -1) {
			scaleLabel.setBackground(Color.RED);
			scaleLabel.setForeground(Color.WHITE);
			scaleLabel.setOpaque(true);
		} else if (value == -2) {
			offsetLabel.setBackground(Color.RED);
			offsetLabel.setForeground(Color.WHITE);
			offsetLabel.setOpaque(true);
		}
		scaleLabel.setText("Scale " + String.format("%3.3f", scale));
		offsetLabel.setText("Offset " + String.format("%3.3f", offset));
	}
}