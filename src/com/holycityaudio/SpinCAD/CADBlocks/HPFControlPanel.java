/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * HPFControlPanel.java
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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;


public class HPFControlPanel extends JFrame implements ChangeListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 8785303496392300373L;

	FineControlSlider freqSlider;
	FineControlSlider resSlider;

	JTextField freqField;
	JTextField resField;

	private HPFCADBlock HPF;

	public HPFControlPanel(HPFCADBlock b) {
		this.HPF = b;

        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowUI();
            }
        });
    }

	private void createAndShowUI() {
		this.setTitle("High pass Filter");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		freqSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		freqSlider.addChangeListener(this);
		resSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 90, 0);
		resSlider.addChangeListener(this);

		freqField = new JTextField();
		freqField.setHorizontalAlignment(JTextField.CENTER);
		freqField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = freqField.getText().replaceAll("[^\\d.\\-]", "");
					double val = Double.parseDouble(text);
					int sliderVal = (int) Math.round(val * 100.0);
					sliderVal = Math.max(0, Math.min(100, sliderVal));
					freqSlider.setValue(sliderVal);
					HPF.setFreq((double) sliderVal / 100.0);
					updateFreqLabel();
				} catch (NumberFormatException ex) {
					updateFreqLabel();
				}
			}
		});

		resField = new JTextField();
		resField.setHorizontalAlignment(JTextField.CENTER);
		resField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String text = resField.getText().replaceAll("[^\\d.\\-]", "");
					double val = Double.parseDouble(text);
					int sliderVal = (int) Math.round(val * 100.0);
					sliderVal = Math.max(0, Math.min(90, sliderVal));
					resSlider.setValue(sliderVal);
					HPF.setRes((double) sliderVal / 100.0);
					updateResLabel();
				} catch (NumberFormatException ex) {
					updateResLabel();
				}
			}
		});

		this.getContentPane().add(freqField);
		this.getContentPane().add(freqSlider);

		this.getContentPane().add(resField);
		this.getContentPane().add(resSlider);

		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(SpinCADFrame.getInstance());
		//		freqSlider.setValue((int)Math.round((b.getFreq() * 100.0)));
		//		resSlider.setValue((int)Math.round((b.getRes() * 100.0)));
	}

	public HPFControlPanel(HPFCADBlock b, JPanel p) {
		this.HPF = b;
		//		this.setTitle("High pass Filter");

		freqSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 100, 0);
		freqSlider.addChangeListener(this);
		resSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 90, 0);
		resSlider.addChangeListener(this);

		JLabel blockName = new JLabel("High Pass Filter");
		freqField = new JTextField();
		freqField.setHorizontalAlignment(JTextField.CENTER);
		resField = new JTextField();
		resField.setHorizontalAlignment(JTextField.CENTER);

		p.add(blockName);

		p.add(freqField);
		p.add(freqSlider);

		p.add(resField);
		p.add(resSlider);

		freqSlider.setValue((int)Math.round((b.getFreq() * 100.0)));
		resSlider.setValue((int)Math.round((b.getRes() * 100.0)));
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == freqSlider) {
			HPF.setFreq((double) freqSlider.getValue() / 100.0);
			updateFreqLabel();
		} else if (ce.getSource() == resSlider) {
			HPF.setRes((double) resSlider.getValue() / 100.0);
			updateResLabel();
		}
	}

	private void updateFreqLabel() {
		freqField.setText("Frequency: "
				+ String.format("%4.1f", HPF.getFreq()));
	}

	private void updateResLabel() {
		resField.setText("Resonance: " + String.format("%2.1f", HPF.getRes()));
	}
}
