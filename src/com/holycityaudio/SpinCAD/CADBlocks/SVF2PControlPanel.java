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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

class SVF2PControlPanel extends JFrame implements ChangeListener, ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -2288952347754535913L;

	JSlider freqSlider;
	JTextField freqField;

	JSlider qSlider;
	JTextField qField;

	private SVF2PCADBlock cadBlock;

	public SVF2PControlPanel(SVF2PCADBlock svf2pcadBlock) {
		// TODO make this thread safe
		this.cadBlock = svf2pcadBlock;
		this.setTitle("State Variable Filter");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		freqSlider = new FineControlSlider(JSlider.HORIZONTAL, 800, 25000, 10000);
		freqSlider.addChangeListener(this);

		qSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, 1);
		qSlider.addChangeListener(this);

		freqField = new JTextField();
		freqField.setHorizontalAlignment(JTextField.CENTER);
		Border freqBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		freqField.setBorder(freqBorder);
		freqField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(freqField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(80.0, Math.min(2500.0, val));
					cadBlock.setFreq(val);
					freqSlider.setValue((int) Math.round(val * 10));
					updateFreqField();
				} catch (NumberFormatException ex) {
					updateFreqField();
				}
			}
		});

		qField = new JTextField();
		qField.setHorizontalAlignment(JTextField.CENTER);
		Border qBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
		qField.setBorder(qBorder);
		qField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					double val = Double.parseDouble(qField.getText().replaceAll("[^0-9.\\-]", ""));
					val = Math.max(1.0, Math.min(100.0, val));
					cadBlock.setQ(val);
					qSlider.setValue((int) Math.round(val));
					updateQField();
				} catch (NumberFormatException ex) {
					updateQField();
				}
			}
		});

		Border freqOuterBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		JPanel freqPanel = new JPanel();
		freqPanel.setLayout(new BoxLayout(freqPanel, BoxLayout.Y_AXIS));
		freqPanel.add(Box.createRigidArea(new Dimension(5, 4)));
		freqPanel.add(freqField);
		freqPanel.add(Box.createRigidArea(new Dimension(5, 4)));
		freqPanel.add(freqSlider);
		freqPanel.setBorder(freqOuterBorder);

		Border qOuterBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		JPanel qPanel = new JPanel();
		qPanel.setLayout(new BoxLayout(qPanel, BoxLayout.Y_AXIS));
		qPanel.add(Box.createRigidArea(new Dimension(5, 4)));
		qPanel.add(qField);
		qPanel.add(Box.createRigidArea(new Dimension(5, 4)));
		qPanel.add(qSlider);
		qPanel.setBorder(qOuterBorder);

		this.getContentPane().add(freqPanel);
		this.getContentPane().add(qPanel);

		freqSlider.setValue((int)Math.round(svf2pcadBlock.getFreq() * 10));
		updateFreqField();
		qSlider.setValue((int)Math.round(svf2pcadBlock.getQ()));
		updateQField();
		this.setLocationRelativeTo(SpinCADFrame.getInstance());

		this.setVisible(true);
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == freqSlider) {
			cadBlock.setFreq((double) freqSlider.getValue() / 10.0);
			updateFreqField();
		}
		else if(ce.getSource() == qSlider) {
			cadBlock.setQ((double) qSlider.getValue());
			updateQField();
		}
	}

	private void updateQField() {
		qField.setText("Resonance " + String.format("%2.1f", cadBlock.getQ()));
	}

	private void updateFreqField() {
		freqField.setText("Frequency " + String.format("%4.1f", cadBlock.getFreq()));
	}
}
