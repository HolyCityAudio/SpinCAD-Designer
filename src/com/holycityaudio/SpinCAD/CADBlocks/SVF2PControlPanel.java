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
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class SVF2PControlPanel extends JFrame implements ChangeListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2288952347754535913L;

	JSlider freqSlider;
	JLabel freqLabel;

	JSlider qSlider;
	JLabel qLabel;

	private SVF2PCADBlock cadBlock;

	public SVF2PControlPanel(SVF2PCADBlock svf2pcadBlock) {
		// TODO make this thread safe
		this.cadBlock = svf2pcadBlock;
		this.setTitle("State Variable Filter");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

		freqSlider = new JSlider(JSlider.HORIZONTAL, 80, 2500, 1000);
		freqSlider.addChangeListener(this);
		
		qSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
		qSlider.addChangeListener(this);

		freqLabel = new JLabel();
		qLabel = new JLabel();
		
		this.getContentPane().add(freqLabel);
		this.getContentPane().add(freqSlider);
		this.getContentPane().add(qLabel);
		this.getContentPane().add(qSlider);

		freqSlider.setValue((int)Math.round((svf2pcadBlock.getFreq())));
		updateFreqLabel();
		qSlider.setValue((int)Math.round(svf2pcadBlock.getQ()));
		updateQLabel();
		this.setLocation(new Point(cadBlock.getX() + 200, cadBlock.getY() + 150));

		this.setVisible(true);
		this.pack();
		this.setAlwaysOnTop(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == freqSlider) {
			cadBlock.setFreq((double) freqSlider.getValue());
			updateFreqLabel();
		}
		else if(ce.getSource() == qSlider) {
			cadBlock.setQ((double) qSlider.getValue());
			updateQLabel();
		}
	}

	private void updateQLabel() {
		qLabel.setText("Resonance " + String.format("%2.2f", cadBlock.getQ()));		
		
	}

	private void updateFreqLabel() {
		freqLabel.setText("Frequency " + String.format("%2.2f", cadBlock.getFreq()));		
	}
}