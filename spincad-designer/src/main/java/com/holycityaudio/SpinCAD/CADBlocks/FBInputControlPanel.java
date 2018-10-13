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

class FBInputControlPanel extends JFrame implements ChangeListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5986441333071288546L;
	JSlider lGainSlider;
	JLabel lGainLabel;
	
	private FBInputCADBlock inBlock;
	
	public FBInputControlPanel(FBInputCADBlock fbInputCADBlock) {
		inBlock = fbInputCADBlock;
		this.setTitle(inBlock.getName());
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		lGainSlider = new JSlider(JSlider.HORIZONTAL, -190, 190, 0);
		lGainSlider.addChangeListener(this);
		
		lGainLabel = new JLabel();
		
		this.getContentPane().add(lGainLabel);
		this.getContentPane().add(lGainSlider);

		lGainSlider.setValue((int)Math.round(100.0 * inBlock.getLGain()));
		this.pack();
		this.setAlwaysOnTop(true);	
		this.setVisible(true);
		this.setLocation(new Point(inBlock.getX() + 200, inBlock.getY() + 150));
		this.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---
		
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == lGainSlider) {
			inBlock.setLGain((double) lGainSlider.getValue()/100.0);
			lGainLabel.setText("Gain "
					+ String.format("%2.2f", inBlock.getLGain()));
		}
	}
}