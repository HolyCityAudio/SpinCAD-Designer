/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BPFControlPanel.java
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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;

@SuppressWarnings("serial")
class BiQuadControlPanel {
	private JFrame frame;
	FineControlSlider freqSlider;
	FineControlSlider resSlider;

	JTextField freqField;
	JTextField resField;

	bqRadioButtons buttons;

	private BiQuadCADBlock spbBQF;

	public BiQuadControlPanel(BiQuadCADBlock b) {

		spbBQF = b;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("BiQuad Filter");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				freqSlider = new FineControlSlider(JSlider.HORIZONTAL, 120, 3600, (int) spbBQF.getFreq());
				resSlider = new FineControlSlider(JSlider.HORIZONTAL, 1, 100, (int) spbBQF.getQ());
				freqSlider.addChangeListener(new biquadSliderListener());
				resSlider.addChangeListener(new biquadSliderListener());

				freqField = new JTextField();
				freqField.setHorizontalAlignment(JTextField.CENTER);
				freqField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = freqField.getText().replaceAll("[^\\d.\\-]", "");
							double val = Double.parseDouble(text);
							int sliderVal = (int) Math.round(val);
							sliderVal = Math.max(freqSlider.getMinimum(), Math.min(freqSlider.getMaximum(), sliderVal));
							spbBQF.setFreq((double) sliderVal);
							freqSlider.setValue(sliderVal);
							updateFreqField();
						} catch (NumberFormatException ex) {
							updateFreqField();
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
							int sliderVal = (int) Math.round(val);
							sliderVal = Math.max(resSlider.getMinimum(), Math.min(resSlider.getMaximum(), sliderVal));
							spbBQF.setQ((double) sliderVal);
							resSlider.setValue(sliderVal);
							updateResField();
						} catch (NumberFormatException ex) {
							updateResField();
						}
					}
				});

				buttons = new bqRadioButtons();

				frame.getContentPane().add(buttons);
				buttons.setVisible(true);

				frame.getContentPane().add(freqField);
				updateFreqField();
				frame.getContentPane().add(freqSlider);

				frame.getContentPane().add(resField);
				updateResField();
				frame.getContentPane().add(resSlider);

				frame.setAlwaysOnTop(true);
				frame.setVisible(true);
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(spbBQF.getX() + 200, spbBQF.getY() + 150);
			}
		});
	}

	class biquadSliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqSlider) {
				spbBQF.setFreq((double) freqSlider.getValue());
				updateFreqField();
			}
			if(ce.getSource() == resSlider) {
				int resValue = resSlider.getValue();
				double res = (double) resValue;
				spbBQF.setQ(res);
				updateResField();
			}
		}
	}


	private void updateFreqField() {
		freqField.setText("Frequency " + String.format("%4.2f", spbBQF.getFreq()));
	}

	private void updateResField() {
		resField.setText("Resonance " + String.format("%4.2f", spbBQF.getQ()));
	}

	class bqRadioButtons extends JPanel implements ActionListener {
		JRadioButton lpfButton = new JRadioButton("Low pass");
		JRadioButton bpfButton = new JRadioButton("Band pass");
		JRadioButton hpfButton = new JRadioButton("High pass");

		public bqRadioButtons() {
			super(new BorderLayout());

			lpfButton.setActionCommand("Low pass");

			bpfButton.setActionCommand("Band pass");

			hpfButton.setActionCommand("High pass");
			hpfButton.setHorizontalAlignment(SwingConstants.CENTER);

			if(spbBQF.getFilterMode() == 1)
				lpfButton.setSelected(true);
			else if(spbBQF.getFilterMode() == 2)
				bpfButton.setSelected(true);
			if(spbBQF.getFilterMode() == 3)
				hpfButton.setSelected(true);

			//Group the radio buttons.
			ButtonGroup group = new ButtonGroup();
			group.add(lpfButton);
			group.add(bpfButton);
			group.add(hpfButton);

			//Register a listener for the radio buttons.
			lpfButton.addActionListener(this);
			bpfButton.addActionListener(this);
			hpfButton.addActionListener(this);

	        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
	        radioPanel.add(lpfButton);
	        radioPanel.add(bpfButton);
	        radioPanel.add(hpfButton);
	        add(radioPanel, BorderLayout.LINE_START);
		}
		@Override

		public void actionPerformed(ActionEvent arg0) {
			if(lpfButton.isSelected()) {
				spbBQF.setFilterMode(1);
			}
			else if(bpfButton.isSelected()) {
				spbBQF.setFilterMode(2);
			} else if (hpfButton.isSelected()) {
				spbBQF.setFilterMode(3);
			}

		}
	}
}
