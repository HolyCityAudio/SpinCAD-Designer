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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
class OverdriveControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider stagesSlider;
	JLabel stagesLabel;
	JSlider gainSlider;
	JLabel gainLabel;
	JSlider outputGainSlider;
	JLabel outputGainLabel;

	private OverdriveCADBlock oD;

	public OverdriveControlPanel(OverdriveCADBlock odCb) {
		this.oD = odCb;
		this.setTitle("Overdrive");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		stagesSlider = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);
		stagesSlider.setMajorTickSpacing(1);
		stagesSlider.setPaintTicks(true);
		stagesSlider.addChangeListener(this);
		stagesLabel = new JLabel();

		gainSlider = new JSlider(JSlider.HORIZONTAL, 20, 190, (int) (odCb.getGain() * 100));
		gainSlider.addChangeListener(this);
		
		gainLabel = new JLabel();
		
		outputGainSlider = new JSlider(JSlider.HORIZONTAL, 2, 100, (int) (odCb.getOutputGain() * 100));
		outputGainSlider.addChangeListener(this);
		
		outputGainLabel = new JLabel();
		
		this.getContentPane().add(stagesLabel);
		this.getContentPane().add(stagesSlider);

		this.getContentPane().add(gainLabel);
		this.getContentPane().add(gainSlider);

		this.getContentPane().add(outputGainLabel);
		this.getContentPane().add(outputGainSlider);

		gainSlider.setValue((int)Math.round(100.0 * odCb.getGain()));
		outputGainSlider.setValue((int)Math.round(100.0 * odCb.getOutputGain()));
		stagesSlider.setValue(odCb.getStages());

		updateStagesLabel();
		updateGainLabel();
		updateOutputGainLabel();

		this.setVisible(true);
		this.pack();
		this.setLocation(oD.getX() + 200, oD.getY() + 150);
		this.setAlwaysOnTop(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == stagesSlider) {
			oD.setStages(stagesSlider.getValue());
			updateStagesLabel();
		}
		else if(ce.getSource() == gainSlider) {
			oD.setGain(gainSlider.getValue()/100.0);
			updateGainLabel();
		}
		else if(ce.getSource() == outputGainSlider) {
				oD.setOutputGain(outputGainSlider.getValue()/100.0);
				updateOutputGainLabel();
		}
	}
	
	public void updateStagesLabel() {
		stagesLabel.setText("Stages: " + String.format("%d", oD.getStages()));		
	}	
	
	public void updateGainLabel() {
		gainLabel.setText("Input Gain: " + String.format("%4.2f", oD.getGain()));		
	}

	public void updateOutputGainLabel() {
		outputGainLabel.setText("Output Gain: " + String.format("%4.2f", oD.getOutputGain()));		
	}
}