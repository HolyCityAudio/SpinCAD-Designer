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


@SuppressWarnings("serial")
class SingleDelayControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider fbSlider;
	JSlider lengthSlider;

	JLabel fbLabel;
	JLabel lengthLabel;
	
	private SingleDelayCADBlock delay;
	
	public SingleDelayControlPanel(SingleDelayCADBlock singleDelayCADBlock) {
		this.delay = singleDelayCADBlock;
		this.setTitle("Single Delay");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);
		
		
		fbSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 0);
		fbSlider.addChangeListener(this);

		lengthSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 0);
		lengthSlider.addChangeListener(this);
		
		fbLabel = new JLabel();
		lengthLabel = new JLabel();
			
		this.getContentPane().add(fbLabel);
		this.getContentPane().add(fbSlider);

		this.getContentPane().add(lengthLabel);
		this.getContentPane().add(lengthSlider);
		
		fbSlider.setValue((int)Math.round((singleDelayCADBlock.getfbLevel() * 100.0)));
		lengthSlider.setValue(singleDelayCADBlock.getDelayTime());

		this.setVisible(true);
		this.setLocation(new Point(delay.getX() + 200, delay.getY() + 150));
		this.setAlwaysOnTop(true);
		this.pack();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---
		
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == fbSlider) {
			delay.setfbLevel((double)fbSlider.getValue() / 100.0);
			fbLabel.setText("Feedback level " + String.format("%2.2f", delay.getfbLevel()));
		}
		else if(ce.getSource() == lengthSlider) {
			delay.setDelayTime(lengthSlider.getValue());
			lengthLabel.setText("Delay (msec) " + String.format("%3d", delay.getDelayTime()));
		}
	}
}