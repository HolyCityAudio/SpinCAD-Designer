/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick
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
	JSlider stagesSlider0;
	JLabel stagesLabel0;


	private OverdriveCADBlock oD;

	public OverdriveControlPanel(OverdriveCADBlock odCb) {
		this.oD = odCb;
		this.setTitle("Overdrive");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);

		stagesSlider0 = new JSlider(JSlider.HORIZONTAL, 1, 3, 2);
		stagesSlider0.setMajorTickSpacing(1);
		stagesSlider0.setPaintTicks(true);
		stagesSlider0.addChangeListener(this);
		stagesLabel0 = new JLabel();

		this.getContentPane().add(stagesLabel0);
		this.getContentPane().add(stagesSlider0);

		stagesSlider0.setValue(odCb.getStages());
		updateStagesLabel();

		this.setVisible(true);
		this.pack();
		this.setLocation(oD.getX() + 200, oD.getY() + 150);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == stagesSlider0) {
			oD.setStages(stagesSlider0.getValue());
			updateStagesLabel();
		}
	}
	
	public void updateStagesLabel() {
		stagesLabel0.setText("Stages: " + String.format("%d", oD.getStages()));		
	}
}