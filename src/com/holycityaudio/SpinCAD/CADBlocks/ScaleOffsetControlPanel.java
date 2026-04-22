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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;


@SuppressWarnings("serial")
public class ScaleOffsetControlPanel extends JDialog implements ChangeListener{

	// Mapped mode sliders (0.001 resolution)
	JSlider inLowSlider;
	JSlider inHighSlider;
	JSlider outLowSlider;
	JSlider outHighSlider;

	JTextField inLowField;
	JTextField inHighField;
	JTextField outLowField;
	JTextField outHighField;

	// Direct mode sliders (0.001 resolution)
	JSlider scaleSlider;
	JSlider offsetSlider;

	JTextField scaleField;
	JTextField offsetField;

	// Shared labels showing computed/actual scale and offset
	JLabel scaleLabel;
	JLabel offsetLabel;

	double scale;
	double offset;

	private JComboBox<String> modeCombo;
	private JPanel mappedPanel;
	private JPanel directPanel;
	private JPanel activePanel;

	private ScaleOffsetControlCADBlock sof;

	public ScaleOffsetControlPanel(ScaleOffsetControlCADBlock scaleOffsetControlCADBlock) {
		super(SpinCADFrame.getInstance(), "Scale Offset");
		this.sof = scaleOffsetControlCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private void createAndShowGUI() {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		// Mode combo box
		modeCombo = new JComboBox<String>(new String[] { "Mapped", "Direct" });
		modeCombo.setSelectedIndex(sof.isDirectMode() ? 1 : 0);
		modeCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		modeCombo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean wasDirect = sof.isDirectMode();
				boolean nowDirect = modeCombo.getSelectedIndex() == 1;
				if (wasDirect == nowDirect) return;

				if (nowDirect) {
					// Mapped -> Direct: transfer computed scale/offset
					double s = (sof.getOutHigh() - sof.getOutLow()) / (sof.getInHigh() - sof.getInLow());
					double o = sof.getOutLow() - (sof.getInLow() * s);
					sof.setDirectScale(s);
					sof.setDirectOffset(o);
					scaleSlider.setValue((int) Math.round(s * 1000.0));
					offsetSlider.setValue((int) Math.round(o * 1000.0));
					updateScaleField();
					updateOffsetField();
				} else {
					// Direct -> Mapped: reset to defaults
					sof.setInLow(0.0);
					sof.setInHigh(1.0);
					sof.setOutLow(0.0);
					sof.setOutHigh(0.75);
					inLowSlider.setValue(0);
					inHighSlider.setValue(1000);
					outLowSlider.setValue(0);
					outHighSlider.setValue(750);
					updateInLowField();
					updateInHighField();
					updateOutLowField();
					updateOutHighField();
				}
				sof.setDirectMode(nowDirect);
				swapPanel(nowDirect ? directPanel : mappedPanel);
				updateScaleOffsetLabels();
				pack();
			}
		});
		this.getContentPane().add(modeCombo);
		this.add(Box.createRigidArea(new Dimension(5, 4)));

		// === Mapped mode panel ===
		mappedPanel = new JPanel();
		mappedPanel.setLayout(new BoxLayout(mappedPanel, BoxLayout.Y_AXIS));

		inLowSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 1000, 0);
		inLowSlider.addChangeListener(this);
		inHighSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 1000, 0);
		inHighSlider.addChangeListener(this);
		outLowSlider = new FineControlSlider(JSlider.HORIZONTAL, -2000, 1000, 0);
		outLowSlider.addChangeListener(this);
		outHighSlider = new FineControlSlider(JSlider.HORIZONTAL, -2000, 1000, 0);
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
					inLowSlider.setValue((int) Math.round(val * 1000.0));
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
					inHighSlider.setValue((int) Math.round(val * 1000.0));
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
					outLowSlider.setValue((int) Math.round(val * 1000.0));
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
					outHighSlider.setValue((int) Math.round(val * 1000.0));
					updateOutHighField();
				} catch (NumberFormatException ex) {
					updateOutHighField();
				}
				updateScaleOffsetLabels();
			}
		});

		mappedPanel.add(Box.createRigidArea(new Dimension(5, 4)));
		mappedPanel.add(inLowField);
		mappedPanel.add(inLowSlider);
		mappedPanel.add(inHighField);
		mappedPanel.add(inHighSlider);
		mappedPanel.add(Box.createRigidArea(new Dimension(5, 4)));
		mappedPanel.add(outLowField);
		mappedPanel.add(outLowSlider);
		mappedPanel.add(outHighField);
		mappedPanel.add(outHighSlider);

		// === Direct mode panel ===
		directPanel = new JPanel();
		directPanel.setLayout(new BoxLayout(directPanel, BoxLayout.Y_AXIS));

		scaleSlider = new FineControlSlider(JSlider.HORIZONTAL, -2000, 1999, 1000);
		scaleSlider.addChangeListener(this);
		offsetSlider = new FineControlSlider(JSlider.HORIZONTAL, -1000, 1000, 0);
		offsetSlider.addChangeListener(this);

		scaleField = new JTextField();
		scaleField.setHorizontalAlignment(JTextField.CENTER);
		scaleField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		scaleField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(scaleField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(-2.0, Math.min(1.999, val));
					sof.setDirectScale(val);
					scaleSlider.setValue((int) Math.round(val * 1000.0));
					updateScaleField();
				} catch (NumberFormatException ex) {
					updateScaleField();
				}
				updateScaleOffsetLabels();
			}
		});

		offsetField = new JTextField();
		offsetField.setHorizontalAlignment(JTextField.CENTER);
		offsetField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
		offsetField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(offsetField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(-1.0, Math.min(1.0, val));
					sof.setDirectOffset(val);
					offsetSlider.setValue((int) Math.round(val * 1000.0));
					updateOffsetField();
				} catch (NumberFormatException ex) {
					updateOffsetField();
				}
				updateScaleOffsetLabels();
			}
		});

		directPanel.add(Box.createRigidArea(new Dimension(5, 4)));
		directPanel.add(scaleField);
		directPanel.add(scaleSlider);
		directPanel.add(offsetField);
		directPanel.add(offsetSlider);

		// Add the active panel
		activePanel = sof.isDirectMode() ? directPanel : mappedPanel;
		this.getContentPane().add(activePanel);

		// Scale/offset readout labels (shown in both modes)
		scaleLabel = new JLabel();
		offsetLabel = new JLabel();
		this.add(Box.createRigidArea(new Dimension(5, 4)));
		this.getContentPane().add(scaleLabel);
		this.add(Box.createRigidArea(new Dimension(5, 4)));
		this.getContentPane().add(offsetLabel);
		this.add(Box.createRigidArea(new Dimension(5, 4)));

		// Initialize slider positions
		inLowSlider.setValue((int) Math.round(sof.getInLow() * 1000.0));
		inHighSlider.setValue((int) Math.round(sof.getInHigh() * 1000.0));
		outLowSlider.setValue((int) Math.round(sof.getOutLow() * 1000.0));
		outHighSlider.setValue((int) Math.round(sof.getOutHigh() * 1000.0));
		scaleSlider.setValue((int) Math.round(sof.getDirectScale() * 1000.0));
		offsetSlider.setValue((int) Math.round(sof.getDirectOffset() * 1000.0));

		updateInLowField();
		updateInHighField();
		updateOutLowField();
		updateOutHighField();
		updateScaleField();
		updateOffsetField();
		updateScaleOffsetLabels();

		this.setVisible(true);
		this.pack();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
		this.setResizable(false);
	}

	private void swapPanel(JPanel newPanel) {
		int idx = getContentPane().getComponentZOrder(activePanel);
		getContentPane().remove(activePanel);
		getContentPane().add(newPanel, idx);
		activePanel = newPanel;
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == inLowSlider) {
			sof.setInLow(inLowSlider.getValue() / 1000.0);
			updateInLowField();
			updateScaleOffsetLabels();
		} else if (ce.getSource() == inHighSlider) {
			sof.setInHigh(inHighSlider.getValue() / 1000.0);
			updateInHighField();
			updateScaleOffsetLabels();
		} else if (ce.getSource() == outLowSlider) {
			sof.setOutLow(outLowSlider.getValue() / 1000.0);
			updateOutLowField();
			updateScaleOffsetLabels();
		} else if (ce.getSource() == outHighSlider) {
			sof.setOutHigh(outHighSlider.getValue() / 1000.0);
			updateOutHighField();
			updateScaleOffsetLabels();
		} else if (ce.getSource() == scaleSlider) {
			sof.setDirectScale(scaleSlider.getValue() / 1000.0);
			updateScaleField();
			updateScaleOffsetLabels();
		} else if (ce.getSource() == offsetSlider) {
			sof.setDirectOffset(offsetSlider.getValue() / 1000.0);
			updateOffsetField();
			updateScaleOffsetLabels();
		}
	}

	private void updateInLowField() {
		inLowField.setText("Input Low " + String.format("%1.3f", sof.getInLow()));
	}

	private void updateInHighField() {
		inHighField.setText("Input High " + String.format("%1.3f", sof.getInHigh()));
	}

	private void updateOutLowField() {
		outLowField.setText("Output Low " + String.format("%1.3f", sof.getOutLow()));
	}

	private void updateOutHighField() {
		outHighField.setText("Output High " + String.format("%1.3f", sof.getOutHigh()));
	}

	private void updateScaleField() {
		scaleField.setText("Scale " + String.format("%1.3f", sof.getDirectScale()));
	}

	private void updateOffsetField() {
		offsetField.setText("Offset " + String.format("%1.3f", sof.getDirectOffset()));
	}

	public int checkValuesInRange() {
		if (sof.isDirectMode()) {
			scale = sof.getDirectScale();
			offset = sof.getDirectOffset();
		} else {
			scale = (sof.getOutHigh() - sof.getOutLow()) / (sof.getInHigh() - sof.getInLow());
			offset = sof.getOutLow() - (sof.getInLow() * scale);
		}
		if ((scale < -2.0) || (scale > 1.99993896484)) {
			return -1;
		} else if ((offset < -1.0) || (offset > 1.0)) {
			return -2;
		} else {
			return 0;
		}
	}

	private void updateScaleOffsetLabels() {
		int value = checkValuesInRange();
		if (value == 0) {
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
