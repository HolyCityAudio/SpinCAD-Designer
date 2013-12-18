/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * distControlPanel.java
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
		package com.holycityaudio.SpinCAD.ControlPanel;
		import javax.swing.JFrame;
		import javax.swing.SwingUtilities;
		import javax.swing.event.ChangeEvent;
		import javax.swing.event.ChangeListener;
		import javax.swing.BoxLayout;
		import javax.swing.JSlider;
		import com.holycityaudio.SpinCAD.CADBlocks.distCADBlock;

		public class distControlPanel {
		private JFrame frame;

		private distCADBlock spbCB;

		public distControlPanel(distCADBlock genericCADBlock) {
		
		spbCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Distortion");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				frame.setVisible(true);		
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(spbCB.getX() + 100, spbCB.getY() + 100);
			}
		});
		}

		class distSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			/*
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
			*/
		}
		
		/*		
		private void updateBassLabel() {
			lLabel.setText("Bass " + String.format("%4.2f", spbBMEQ.getBass() * 10));		
		}

		private void updateMidLabel() {
			mLabel.setText("Mid " + String.format("%4.2f", spbBMEQ.getMid() * 10));		
		}

		private void updateTrebleLabel() {
			tLabel.setText("Treble " + String.format("%4.2f", spbBMEQ.getTreble() * 10));		
		}
		*/
		} 
		}
