/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EnvelopeControlControlPanel implements ChangeListener, ActionListener {

	private JSlider gainSlider = new JSlider(JSlider.HORIZONTAL, 1, 8, 2);
	private JLabel gainLabel = new JLabel("Hi");
	private JSlider filterSlider = new JSlider(JSlider.HORIZONTAL, 10, 100, 20);
	private JLabel filterLabel = new JLabel("Hi");
	private JFrame frame;

	private EnvelopeControlCADBlock pC;

	public EnvelopeControlControlPanel(EnvelopeControlCADBlock envelopeControlCADBlock) {
		gainSlider.addChangeListener(this);
		filterSlider.addChangeListener(this);
		
		this.pC = envelopeControlCADBlock;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Envelope");
				frame.setTitle("Envelope");
				frame.setResizable(false);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				gainSlider.setMajorTickSpacing(25);

				frame.add(gainLabel);
				frame.add(gainSlider);
				frame.add(filterLabel);
				frame.add(filterSlider);

				gainSlider.setValue((int) Math.round(pC.getGain()));
				updateGainLabel();

				filterSlider.setValue((int) Math.round(pC.getFilter() * 10000));
				filterLabel.setText(String.format("Filter: %4.4f", pC.getFilter()));

				frame.setLocation(pC.getX() + 200, pC.getY() + 150);
				frame.setVisible(true);
				frame.pack();
				frame.setAlwaysOnTop(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == gainSlider) {
			pC.setGain(gainSlider.getValue());
			updateGainLabel();
		}
		else if (e.getSource() == filterSlider) {
			pC.setFilter((double) filterSlider.getValue()/10000.0);
			filterLabel.setText(String.format("Filter: %4.4f", pC.getFilter()));
		}
	}	
	
	private void updateGainLabel() {
		gainLabel.setText(String.format("Gain: %2d dB", pC.getGain() * 6));				
	}
}