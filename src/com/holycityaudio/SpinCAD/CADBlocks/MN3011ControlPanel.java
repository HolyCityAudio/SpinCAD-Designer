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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class MN3011ControlPanel extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4752954128794535487L;
	
	JSlider tapSlider0;
	JSlider tapSlider1;
	JSlider tapSlider2;
	JSlider tapSlider3;
	JSlider tapSlider4;
	JSlider tapSlider5;
	JSlider fbSlider;
	JSlider delayGainSlider;
	JSlider lengthSlider;

	JLabel tapLabel0;
	JLabel tapLabel1;
	JLabel tapLabel2;
	JLabel tapLabel3;
	JLabel tapLabel4;
	JLabel tapLabel5;
	JLabel fbLabel;
	JLabel delayGainLabel;
	JLabel lengthLabel;

	private MN3011CADBlock MN3011;

	public MN3011ControlPanel(MN3011CADBlock b) {
		this.MN3011 = b;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("MN3011 Block");
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				MN3011ChangeListener mnCL = new MN3011ChangeListener();
				
				tapSlider0 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider0.addChangeListener(mnCL);
				tapSlider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider1.addChangeListener(mnCL);
				tapSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider2.addChangeListener(mnCL);
				tapSlider3 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider3.addChangeListener(mnCL);
				tapSlider4 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider4.addChangeListener(mnCL);
				tapSlider5 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider5.addChangeListener(mnCL);

				fbSlider = new JSlider(JSlider.HORIZONTAL, 0, 80, 0);
				fbSlider.addChangeListener(mnCL);

				lengthSlider = new JSlider(JSlider.HORIZONTAL, 0, 1000, 0);
				lengthSlider.addChangeListener(mnCL);

				tapLabel0 = new JLabel();
				tapLabel1 = new JLabel();
				tapLabel2 = new JLabel();
				tapLabel3 = new JLabel();
				tapLabel4 = new JLabel();
				tapLabel5 = new JLabel();
				fbLabel = new JLabel();
				lengthLabel = new JLabel();

				getContentPane().add(tapLabel0);
				getContentPane().add(tapSlider0);
				getContentPane().add(tapLabel1);
				getContentPane().add(tapSlider1);
				getContentPane().add(tapLabel2);
				getContentPane().add(tapSlider2);
				getContentPane().add(tapLabel3);
				getContentPane().add(tapSlider3);
				getContentPane().add(tapLabel4);
				getContentPane().add(tapSlider4);
				getContentPane().add(tapLabel5);
				getContentPane().add(tapSlider5);

				getContentPane().add(fbLabel);
				getContentPane().add(fbSlider);

				getContentPane().add(lengthLabel);
				getContentPane().add(lengthSlider);

				tapSlider0.setValue((int)Math.round((MN3011.getTapLevel(0) * 100.0)));
				tapSlider1.setValue((int)Math.round((MN3011.getTapLevel(1) * 100.0)));
				tapSlider2.setValue((int)Math.round((MN3011.getTapLevel(2) * 100.0)));
				tapSlider3.setValue((int)Math.round((MN3011.getTapLevel(3) * 100.0)));
				tapSlider4.setValue((int)Math.round((MN3011.getTapLevel(4) * 100.0)));
				tapSlider5.setValue((int)Math.round((MN3011.getTapLevel(5) * 100.0)));
				fbSlider.setValue((int)Math.round((MN3011.getfbLevel() * 100.0)));
				lengthSlider.setValue((int)Math.round((MN3011.getLength() * 1000.0)));

				setVisible(true);
				setResizable(false);
				setLocation(new Point(MN3011.getX() + 200, MN3011.getY() + 150));
				pack();
			}
		});
	}

	class MN3011ChangeListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == tapSlider0) {
				MN3011.setTapLevel(0, (double)tapSlider0.getValue() / 100.0);
				tapLabel0.setText("Tap 1 level " + String.format("%2.2f", MN3011.getTapLevel(0)));
				System.out.println("Tap 1 level " + String.format("%2.2f", MN3011.getTapLevel(0)));
			}
			else if(ce.getSource() == tapSlider1) {
				MN3011.setTapLevel(1, (double)tapSlider1.getValue() / 100.0);
				tapLabel1.setText("Tap 2 level " + String.format("%2.2f", MN3011.getTapLevel(1)));
				System.out.println("Tap 2 level " + String.format("%2.2f", MN3011.getTapLevel(1)));
			}
			else if(ce.getSource() == tapSlider2) {
				MN3011.setTapLevel(2, (double)tapSlider2.getValue() / 100.0);
				tapLabel2.setText("Tap 3 level " + String.format("%2.2f", MN3011.getTapLevel(2)));
				System.out.println("Tap 3 level " + String.format("%2.2f", MN3011.getTapLevel(2)));
			}
			else if(ce.getSource() == tapSlider3) {
				MN3011.setTapLevel(3, (double)tapSlider3.getValue() / 100.0);
				tapLabel3.setText("Tap 4 level " + String.format("%2.2f", MN3011.getTapLevel(3)));
				System.out.println("Tap 4 level " + String.format("%2.2f", MN3011.getTapLevel(3)));
			}
			else if(ce.getSource() == tapSlider4) {
				MN3011.setTapLevel(4, (double)tapSlider4.getValue() / 100.0);
				tapLabel4.setText("Tap 5 level " + String.format("%2.2f", MN3011.getTapLevel(4)));
				System.out.println("Tap 5 level " + String.format("%2.2f", MN3011.getTapLevel(4)));
			}
			else if(ce.getSource() == tapSlider5) {
				MN3011.setTapLevel(5, (double)tapSlider5.getValue() / 100.0);
				tapLabel5.setText("Tap 6 level " + String.format("%2.2f", MN3011.getTapLevel(5)));
				System.out.println("Tap 6 level " + String.format("%2.2f", MN3011.getTapLevel(5)));
			}
			else if(ce.getSource() == fbSlider) {
				MN3011.setfbLevel((double)fbSlider.getValue() / 100.0);
				fbLabel.setText("Feedback level " + String.format("%2.2f", MN3011.getfbLevel()));
				System.out.println("Feedback level " + String.format("%2.2f", MN3011.getfbLevel()));
			}
			else if(ce.getSource() == lengthSlider) {
				MN3011.setLength((double)lengthSlider.getValue() / 1000.0);
				lengthLabel.setText("Delay (sec) " + String.format("%1.3f", MN3011.getLength()));
				System.out.println("Delay (sec) " + String.format("%1.3f", MN3011.getLength()));
			}
		}
	}
}