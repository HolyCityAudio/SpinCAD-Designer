/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ModDelayControlPanel.java
 * Copyright (C)2013 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
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

class ModDelayControlPanel {

	private ModDelayCADBlock mD;
	private JSlider delaySlider;
	private JLabel delayLabel;
	private JFrame frame;

	public ModDelayControlPanel(ModDelayCADBlock modDelayCADBlock) {
		this.mD = modDelayCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setTitle("Straight\nDelay");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				delaySlider = new JSlider(JSlider.HORIZONTAL, 4096, 16384, mD.getDelayLength());
				delaySlider.addChangeListener(new bitSliderListener());
				delayLabel = new JLabel();
				frame.add(delayLabel);
				frame.add(delaySlider);
				updateDelayLabel();
				frame.setVisible(true);
				frame.pack();
				frame.setLocation(mD.getX() + 200, mD.getY() + 150);
			}
		});

	}
	
	class bitSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == delaySlider) {
				mD.setDelayLength(delaySlider.getValue());
				updateDelayLabel();
			}
		}
	}

	public void updateDelayLabel() {
		// TODO Auto-generated method stub
		delayLabel.setText("Delay length: " + String.format("%d", mD.getDelayLength()));		

	}
}