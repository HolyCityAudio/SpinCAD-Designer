/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * LPF1PControlPanel.java
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADFrame;


class LPF4PControlPanel extends JFrame implements ActionListener {
	/**
	 *
	 */
	private static final long serialVersionUID = -2288952347754535913L;

	FineControlSlider freqSlider;
	JTextField freqField;

	FineControlSlider qSlider;
	JTextField qField;

	private JComboBox<Object> nPoles;

	private LPF4PCADBlock LPF;

	private String listOptions[] = {
			" 2 poles ",
			" 4 poles "
	};


	public LPF4PControlPanel(LPF4PCADBlock lpf1pcadBlock) {
		this.LPF = lpf1pcadBlock;
		nPoles = new JComboBox<Object>(listOptions);
		nPoles.addActionListener(this);
		createAndShowUI();
	}

	private void createAndShowUI() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if(LPF.getIs4Pole()) {
					setTitle("Low pass 4 pole");
					nPoles.setSelectedIndex(1);
				} else {
					setTitle("Low pass 2 pole");
					nPoles.setSelectedIndex(0);
				}
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				freqSlider = SpinCADBlock.LogSlider(20, 2500, SpinCADBlock.freqToFilt(LPF.getFreq()), "LOGFREQ", 100.0);
				freqSlider.addChangeListener(new LPF1PChangeListener());

				freqField = new JTextField();
				freqField.setHorizontalAlignment(JTextField.CENTER);
				freqField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = freqField.getText().replaceAll("[^\\d.\\-]", "");
							double val = Double.parseDouble(text);
							int sliderVal = SpinCADBlock.logvalToSlider(val, 100.0);
							sliderVal = Math.max(freqSlider.getMinimum(), Math.min(freqSlider.getMaximum(), sliderVal));
							freqSlider.setValue(sliderVal);
							LPF.setFreq(SpinCADBlock.sliderToLogval(sliderVal, 100.0));
							updateFreqLabel();
						} catch (NumberFormatException ex) {
							updateFreqLabel();
						}
					}
				});

				int qSliderPosition = (int)(1/LPF.getQ());
				qSlider = new FineControlSlider(JSlider.HORIZONTAL, 10, 200, qSliderPosition);
				qSlider.addChangeListener(new LPF1PChangeListener());

				qField = new JTextField();
				qField.setHorizontalAlignment(JTextField.CENTER);
				qField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							String text = qField.getText().replaceAll("[^\\d.\\-]", "");
							double displayedQ = Double.parseDouble(text);
							// display = 0.1/q_slider_value, so q_slider_value = 0.1/displayedQ
							int sliderVal = (int)(0.1 / displayedQ);
							sliderVal = Math.max(10, Math.min(200, sliderVal));
							LPF.setQ((double) sliderVal);
							qSlider.setValue(sliderVal);
							updateQLabel();
						} catch (NumberFormatException ex) {
							updateQLabel();
						}
					}
				});

				updateQLabel();

				getContentPane().add(freqField);
				getContentPane().add(freqSlider);
				getContentPane().add(Box.createRigidArea(new Dimension(250,4)));
				getContentPane().add(qField);
				getContentPane().add(Box.createRigidArea(new Dimension(250,4)));
				getContentPane().add(qSlider);
				getContentPane().add(Box.createRigidArea(new Dimension(250,7)));
				getContentPane().add(nPoles);
				getContentPane().add(Box.createRigidArea(new Dimension(250,4)));

				freqSlider.setValue(SpinCADBlock.logvalToSlider(LPF.getFreq(), 100.0));
				updateFreqLabel();
				setVisible(true);
				setLocationRelativeTo(SpinCADFrame.getInstance());
				pack();
				setResizable(true);
			}
		});
	}

	class LPF1PChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqSlider) {
				LPF.setFreq(SpinCADBlock.sliderToLogval((int) freqSlider.getValue(), 100.0));
				updateFreqLabel();
			}
			else if(ce.getSource() == qSlider) {
				LPF.setQ((double) qSlider.getValue());
				updateQLabel();
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == nPoles) {
	        JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
	        String range = (String)cb.getSelectedItem();
	        if (range == listOptions[0]) {
	        	LPF.setIs4Pole(false);
	        } else if (range == listOptions[1]) {
	        	LPF.setIs4Pole(true);
	        }
		}
	}

	public void updateQLabel() {
//		qField.setText(" Resonance " + String.format(new DecimalFormat("#.##").format(0.1/LPF.getQ())));
		qField.setText(" Resonance " + String.format("%3.1f",(0.1/LPF.getQ())));
	}

	private void updateFreqLabel() {
		freqField.setText("Frequency " + String.format("%4.1f", LPF.getFreq()));
	}

}
