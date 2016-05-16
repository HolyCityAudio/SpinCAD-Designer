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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
class BPFControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider freqSlider;
	JSlider resSlider;

	JLabel freqLabel;
	JLabel resLabel;
	
	private BPFCADBlock BPF;
	
	public BPFControlPanel(BPFCADBlock b) {
		this.BPF = b;
		this.setTitle("Band pass Filter");
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
		this.setLocation(BPF.getX() + 200, BPF.getY() + 150);
		}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---
		
	}

	public void stateChanged(ChangeEvent ce) {
	}
}