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

public class SixBandEQControlPanel extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6306397702386815750L;

	JSlider eqSlider0;
	JSlider eqSlider1;
	JSlider eqSlider2;
	JSlider eqSlider3;
	JSlider eqSlider4;
	JSlider eqSlider5;
	JSlider qSlider;

	JLabel eqLabel0;
	JLabel eqLabel1;
	JLabel eqLabel2;
	JLabel eqLabel3;
	JLabel eqLabel4;
	JLabel eqLabel5;
	JLabel qLabel;

	private SixBandEQCADBlock filter;

	public SixBandEQControlPanel(SixBandEQCADBlock b) {
		this.filter = b;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("6-Band EQ");
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
				
				SixBandChangeListener sixCL = new SixBandChangeListener();

				eqSlider0 = new JSlider(JSlider.HORIZONTAL, -100, 199, 0);
				eqSlider0.addChangeListener(sixCL);
				eqSlider1 = new JSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider1.addChangeListener(sixCL);
				eqSlider2 = new JSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider2.addChangeListener(sixCL);
				eqSlider3 = new JSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider3.addChangeListener(sixCL);
				eqSlider4 = new JSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider4.addChangeListener(sixCL);
				eqSlider5 = new JSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider5.addChangeListener(sixCL);

				qSlider = new JSlider(JSlider.HORIZONTAL, 100, 400, 100);
				qSlider.addChangeListener(sixCL);

				eqLabel0 = new JLabel();
				eqLabel1 = new JLabel();
				eqLabel2 = new JLabel();
				eqLabel3 = new JLabel();
				eqLabel4 = new JLabel();
				eqLabel5 = new JLabel();
				qLabel = new JLabel();

				getContentPane().add(eqLabel0);
				getContentPane().add(eqSlider0);
				getContentPane().add(eqLabel1);
				getContentPane().add(eqSlider1);
				getContentPane().add(eqLabel2);
				getContentPane().add(eqSlider2);
				getContentPane().add(eqLabel3);
				getContentPane().add(eqSlider3);
				getContentPane().add(eqLabel4);
				getContentPane().add(eqSlider4);
				getContentPane().add(eqLabel5);
				getContentPane().add(eqSlider5);

				getContentPane().add(qLabel);
				getContentPane().add(qSlider);


				eqSlider0.setValue((int) Math.round(((filter.geteqLevel(0)) * 100.0)));
				eqLabel0.setText("80 Hz level " + String.format("%2.2f", filter.geteqLevel(0)));

				eqSlider1.setValue((int) Math.round((filter.geteqLevel(1) * 100.0)));
				eqLabel1.setText("160 Hz level " + String.format("%2.2f", filter.geteqLevel(1)));

				eqSlider2.setValue((int) Math.round((filter.geteqLevel(2) * 100.0)));
				eqLabel2.setText("320 Hz level " + String.format("%2.2f", filter.geteqLevel(2)));

				eqSlider3.setValue((int) Math.round((filter.geteqLevel(3) * 100.0)));
				eqLabel3.setText("640 Hz level " + String.format("%2.2f", filter.geteqLevel(3)));

				eqSlider4.setValue((int) Math.round((filter.geteqLevel(4) * 100.0)));
				eqLabel4.setText("1280 Hz level " + String.format("%2.2f", filter.geteqLevel(4)));

				eqSlider5.setValue((int) Math.round((filter.geteqLevel(5) * 100.0)));
				eqLabel5.setText("2560 Hz level " + String.format("%2.2f", filter.geteqLevel(5)));

				qSlider.setValue((int) Math.round((filter.getQLevel() * 100.0)));
				setVisible(true);
				setLocation(new Point(filter.getX() + 200, filter.getY() + 150));
				pack();
				setResizable(false);
				setAlwaysOnTop(true);
			}
		});
	}

	class SixBandChangeListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if (ce.getSource() == eqSlider0) {
				filter.seteqLevel(0, (double) eqSlider0.getValue() / 100.0);
				eqLabel0.setText("80 Hz level "
						+ String.format("%2.2f", filter.geteqLevel(0)));
			} else if (ce.getSource() == eqSlider1) {
				filter.seteqLevel(1, (double) eqSlider1.getValue() / 100.0);
				eqLabel1.setText("160 Hz level "
						+ String.format("%2.2f", filter.geteqLevel(1)));
			} else if (ce.getSource() == eqSlider2) {
				filter.seteqLevel(2, (double) eqSlider2.getValue() / 100.0);
				eqLabel2.setText("320 Hz level "
						+ String.format("%2.2f", filter.geteqLevel(2)));
			} else if (ce.getSource() == eqSlider3) {
				filter.seteqLevel(3, (double) eqSlider3.getValue() / 100.0);
				eqLabel3.setText("640 Hz level "
						+ String.format("%2.2f", filter.geteqLevel(3)));
			} else if (ce.getSource() == eqSlider4) {
				filter.seteqLevel(4, (double) eqSlider4.getValue() / 100.0);
				eqLabel4.setText("1280 Hz level "
						+ String.format("%2.2f", filter.geteqLevel(4)));
			} else if (ce.getSource() == eqSlider5) {
				filter.seteqLevel(5, (double) eqSlider5.getValue() / 100.0);
				eqLabel5.setText("2560 Hz level "
						+ String.format("%2.2f", filter.geteqLevel(5)));
			} else if (ce.getSource() == qSlider) {
				filter.setqLevel((double) qSlider.getValue() / 100.0);
				qLabel.setText("Resonance "
						+ String.format("%2.2f", filter.getQLevel()));
			}
		}
	}
}