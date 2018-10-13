/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * BitCrusherControlPanel.java
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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class QuantizerControlPanel {

	private QuantizerCADBlock BC;
	private JSlider bitSlider;
	private JLabel bitLabel;
	private JFrame frame;

	public QuantizerControlPanel(QuantizerCADBlock b) {
		this.BC = b;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setTitle("Quantizer");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				bitSlider = new JSlider(JSlider.HORIZONTAL, 1, 16, BC.getBits());
				bitSlider.addChangeListener(new bitSliderListener());
				bitLabel = new JLabel();
				frame.add(bitLabel);
				frame.add(bitSlider);
				updateBitLabel();
				frame.setAlwaysOnTop(true);	
				frame.setVisible(true);
				frame.pack();
				frame.setLocation(BC.getX() + 200, BC.getY() + 150);
			}
		});

	}
	class bitSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == bitSlider) {
				BC.setBits(bitSlider.getValue());
				updateBitLabel();
			}
		}
	}

	public void updateBitLabel() {
		// ---
		bitLabel.setText("Bits: " + String.format("%d", BC.getBits()));		

	}

	public void stateChanged(ChangeEvent ce) {
	}
}