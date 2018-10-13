/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ModDelayControlPanel.java
 * Copyright (C) 2013 - 2014 - Gary Worsham 
 * SpinCAD Designer is based on ElmGenby Andrew Kilpatrick.  
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

import org.andrewkilpatrick.elmGen.ElmProgram;

class CoarseDelayControlPanel {

	private CoarseDelayCADBlock mD;
	private JSlider delaySliderCoarse;
	private JSlider delaySliderFine;
	private JLabel delayLabelCoarse;
	private JLabel delayLabelFine;
	private JFrame frame;

	public CoarseDelayControlPanel(CoarseDelayCADBlock sDCB) {
		this.mD = sDCB;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame();
				frame.setTitle("Servo Delay");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				// XXX debug, this may not be correct
				int timeCoarse = calcDelayTimeCoarse(mD.getDelayLength());
				delaySliderCoarse = new JSlider(JSlider.HORIZONTAL, 0, calcDelayTimeCoarse(32767), timeCoarse);
				
				delaySliderCoarse.addChangeListener(new bitSliderListener());
				delayLabelCoarse = new JLabel();
				frame.add(delayLabelCoarse);
				frame.add(delaySliderCoarse);
				
				updateDelayLabelCoarse();
				
				int timeFine = calcDelayTimeFine(mD.getDelayLength());
				delaySliderFine = new JSlider(JSlider.HORIZONTAL, 0, 25, timeFine);
				delaySliderFine.addChangeListener(new bitSliderListener());
				delayLabelFine = new JLabel();
				frame.add(delayLabelFine);
				frame.add(delaySliderFine);
				updateDelayLabelFine();
				
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
				frame.pack();
				frame.setLocation(mD.getX() + 200, mD.getY() + 150);
			}
		});

	}
	
	class bitSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
// XXX this needs to be reworks, it is not accurate
			int totalDelay = (int)(((delaySliderCoarse.getValue()+ delaySliderFine.getValue()) * ElmProgram.getSamplerate())/1000.0);
			if(ce.getSource() == delaySliderCoarse) {
				mD.setDelayLength(totalDelay);
				updateDelayLabelCoarse();
			}
			if(ce.getSource() == delaySliderFine) {
				mD.setDelayLength(totalDelay);
				updateDelayLabelFine();
			}
		}
	}

	public void updateDelayLabelCoarse() {
		// ---
		delayLabelCoarse.setText("Delay (coarse): " + String.format("%d ms", calcDelayTimeCoarse(mD.getDelayLength())));		

	}
	
	public void updateDelayLabelFine() {
		// ---
		delayLabelFine.setText("Delay (fine): " + String.format("%d ms", calcDelayTimeFine(mD.getDelayLength())));		

	}
	
	private int calcDelayTimeCoarse(int length) {
		int l = (int) ((length * 1000)/ElmProgram.getSamplerate()/25) * 25;
		return l;
	}
	
	private int calcDelayTimeFine(int length) {
		int l = (int) (((length * 1000)/ElmProgram.getSamplerate()) % 25);
		return l;
	}
}