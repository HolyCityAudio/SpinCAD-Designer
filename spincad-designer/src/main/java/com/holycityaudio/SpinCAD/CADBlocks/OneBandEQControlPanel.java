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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class OneBandEQControlPanel extends JFrame implements ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6306397702386815750L;
	
	JSlider eqSlider0;
	JLabel eqLabel0;
	
	JSlider qSlider;
	JLabel qLabel;

	JSlider freqSlider0;
	JLabel freqLabel;

	private OneBandEQCADBlock filter;

	public OneBandEQControlPanel(OneBandEQCADBlock b) {
		this.filter = b;
		this.setTitle("1-Band EQ");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		eqSlider0 = new JSlider(JSlider.HORIZONTAL, -100, 199, 0);
		eqSlider0.addChangeListener(this);

		freqSlider0 = new JSlider(JSlider.HORIZONTAL, 80, 3200, 440);
		freqSlider0.addChangeListener(this);

		qSlider = new JSlider(JSlider.HORIZONTAL, 100, 400, 100);
		qSlider.addChangeListener(this);

		eqLabel0 = new JLabel();
		qLabel = new JLabel();
		freqLabel = new JLabel();

		this.getContentPane().add(eqLabel0);
		this.getContentPane().add(eqSlider0);

		this.getContentPane().add(freqLabel);
		this.getContentPane().add(freqSlider0);

		this.getContentPane().add(qLabel);
		this.getContentPane().add(qSlider);

		this.setVisible(true);
		this.setResizable(false);

		eqSlider0.setValue((int) Math.round(((b.getEqLevel()) * 100.0)));
		eqLabel0.setText("Level " + String.format("%2.2f", b.getEqLevel()));
		
		freqSlider0.setValue((int) Math.round(b.getFreq()));
		freqLabel.setText("Frequency " + String.format("%2.2f", b.getFreq()));
				
		qSlider.setValue((int) Math.round((b.getQLevel() * 100.0)));
		qLabel.setText("Resonance "	+ String.format("%2.2f", b.getQLevel()));
		this.setAlwaysOnTop(true);
		this.setLocation(new Point(filter.getX() + 200, filter.getY() + 150));
		this.pack();
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == eqSlider0) {
			filter.setEqLevel((double) eqSlider0.getValue() / 100.0);
			eqLabel0.setText("Level "
					+ String.format("%2.2f", filter.getEqLevel()));
		} else if (ce.getSource() == freqSlider0) {
			filter.setFreq((double) freqSlider0.getValue());
			freqLabel.setText("Frequency "
					+ String.format("%2.2f", filter.getFreq()));
		}  else if (ce.getSource() == qSlider) {
			filter.setqLevel((double) qSlider.getValue() / 100.0);
			qLabel.setText("Resonance "
					+ String.format("%2.2f", filter.getQLevel()));
		}
	}
}