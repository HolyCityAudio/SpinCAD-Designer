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

class OutputControlPanel extends JFrame implements ChangeListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5986441333071288546L;
	JSlider lGainSlider;
	JSlider rGainSlider;

	JLabel lGainLabel;
	JLabel rGainLabel;
	
	private OutputCADBlock outBlock;
	
	public OutputControlPanel(OutputCADBlock b) {
		this.outBlock = b;
		this.setTitle("Output");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		lGainSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		lGainSlider.addChangeListener(this);
		rGainSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		rGainSlider.addChangeListener(this);
		
		lGainLabel = new JLabel();
		rGainLabel = new JLabel();
		
		this.getContentPane().add(lGainLabel);
		this.getContentPane().add(lGainSlider);

		this.getContentPane().add(rGainLabel);
		this.getContentPane().add(rGainSlider);
		
		lGainSlider.setValue((int)Math.round(100.0 * OutputCADBlock.getLGain()));
		rGainSlider.setValue((int)Math.round(100.0 * OutputCADBlock.getRGain()));
		this.pack();
		this.setVisible(true);
		this.setLocation(new Point(outBlock.getX() + 200, outBlock.getY() + 150));
		this.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == lGainSlider) {
			outBlock.setLGain((double) lGainSlider.getValue()/100.0);
			lGainLabel.setText("Left Gain "
					+ String.format("%2.2f", OutputCADBlock.getLGain()));
		}
		else if(ce.getSource() == rGainSlider) {
			outBlock.setRGain((double) rGainSlider.getValue()/100.0);
			rGainLabel.setText("Right Gain "
					+ String.format("%2.2f", OutputCADBlock.getRGain()));
		}
	}
}