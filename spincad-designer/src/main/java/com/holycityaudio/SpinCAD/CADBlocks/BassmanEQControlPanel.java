/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BPFControlPanel.java 
 * Copyright (C) 2013 - 2014 - Gary Worsham 
 * SpinCAD Designer is based on ElmGenby Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code. 
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

class BassmanEQControlPanel {
	private JFrame frame;
	
	JSlider lSlider;	// "low"
	JLabel lLabel;

	JSlider mSlider;	// "mid"
	JLabel mLabel;

	JSlider tSlider;	// "treble"
	JLabel tLabel;

	private BassmanEQCADBlock spbBMEQ;

	public BassmanEQControlPanel(BassmanEQCADBlock bassmanEQCADBlock) {
		
		spbBMEQ = bassmanEQCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Bassman '59 EQ");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				lSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (100 * spbBMEQ.getBass()));
				mSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (100 * spbBMEQ.getMid()));
				tSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, (int) (100 * spbBMEQ.getTreble()));
				
				BassmanEQSliderListener bEQSL = new BassmanEQSliderListener();

				lSlider.addChangeListener(bEQSL);
				mSlider.addChangeListener(bEQSL);
				tSlider.addChangeListener(bEQSL);

				lLabel = new JLabel();
				mLabel = new JLabel();
				tLabel = new JLabel();

				frame.getContentPane().add(lLabel);
				updateBassLabel();
				frame.getContentPane().add(lSlider);

				frame.getContentPane().add(mLabel);
				updateMidLabel();
				frame.getContentPane().add(mSlider);

				frame.getContentPane().add(tLabel);
				updateTrebleLabel();
				frame.getContentPane().add(tSlider);

				frame.setVisible(true);		
				frame.setAlwaysOnTop(true);	
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(spbBMEQ.getX() + 100, spbBMEQ.getY() + 100);
			}
		});
	}

	class BassmanEQSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == lSlider) {
				spbBMEQ.setBass((double) (lSlider.getValue()/100.0));
				updateBassLabel();
			}
			else if(ce.getSource() == mSlider) {
				int resValue = mSlider.getValue();
				double mid = (double) (resValue/100.0);
				spbBMEQ.setMid(mid);
				updateMidLabel();
			}
			else if(ce.getSource() == tSlider) {
				int trebValue = tSlider.getValue();
				double treble = (double) (trebValue/100.0);
				spbBMEQ.setTreble(treble);
				updateTrebleLabel();
			}
		}
	}
	
	private void updateBassLabel() {
		lLabel.setText("Bass " + String.format("%4.2f", spbBMEQ.getBass() * 10));		
	}

	private void updateMidLabel() {
		mLabel.setText("Mid " + String.format("%4.2f", spbBMEQ.getMid() * 10));		
	}

	private void updateTrebleLabel() {
		tLabel.setText("Treble " + String.format("%4.2f", spbBMEQ.getTreble() * 10));		
	}
}
