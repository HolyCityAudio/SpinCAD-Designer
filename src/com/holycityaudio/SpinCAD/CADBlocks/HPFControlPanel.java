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
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class HPFControlPanel extends JFrame implements ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8785303496392300373L;
	
	JSlider freqSlider;
	JSlider resSlider;

	JLabel freqLabel;
	JLabel resLabel;

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

		freqSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		freqSlider.addChangeListener(this);
		resSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 0);
		resSlider.addChangeListener(this);

		freqLabel = new JLabel();
		resLabel = new JLabel();

		this.getContentPane().add(freqLabel);
		this.getContentPane().add(freqSlider);

		this.getContentPane().add(resLabel);
		this.getContentPane().add(resSlider);

		this.pack();
		this.setVisible(true);
		this.setLocation(HPF.getX() + 200, HPF.getY() + 150);
		//		freqSlider.setValue((int)Math.round((b.getFreq() * 100.0)));
		//		resSlider.setValue((int)Math.round((b.getRes() * 100.0)));
	}

	public HPFControlPanel(HPFCADBlock b, JPanel p) {
		this.HPF = b;
		//		this.setTitle("High pass Filter");

		freqSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		freqSlider.addChangeListener(this);
		resSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 0);
		resSlider.addChangeListener(this);

		JLabel blockName = new JLabel("High Pass Filter");
		freqLabel = new JLabel();
		resLabel = new JLabel();

		p.add(blockName);

		p.add(freqLabel);
		p.add(freqSlider);

		p.add(resLabel);
		p.add(resSlider);

		freqSlider.setValue((int)Math.round((b.getFreq() * 100.0)));
		resSlider.setValue((int)Math.round((b.getRes() * 100.0)));
	}

	public void stateChanged(ChangeEvent ce) {
		if (ce.getSource() == freqSlider) {
			HPF.setFreq((double) freqSlider.getValue() / 100.0);
			freqLabel.setText("Frequency: "
					+ String.format("%2.2f", HPF.getFreq()));
		} else if (ce.getSource() == resSlider) {
			HPF.setRes((double) resSlider.getValue() / 100.0);
			resLabel.setText("Resonance: " + String.format("%2.2f", HPF.getRes()));
		}
	}
}