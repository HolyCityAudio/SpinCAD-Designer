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

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class PhaserControlPanel implements ChangeListener, ActionListener {
	JSlider stagesSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 4);
	JLabel stagesLabel = new JLabel();

	private JLabel controlTypeLabel = new JLabel("Control Type");
	private JComboBox<String> controlType;
	private String listOptions[] = {
			"Internal LFO",
			"Direct Input"
	};


	private JFrame frame;

	private PhaserCADBlock pong;

	public PhaserControlPanel(PhaserCADBlock swoosh) {

		stagesSlider.addChangeListener(this);
		controlType = new JComboBox<String>(listOptions);
		controlType.addActionListener(this);

		pong = swoosh;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Ramp LFO");
				frame.setTitle("Phaser");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				frame.setResizable(false);

				stagesSlider.setMajorTickSpacing(1);
				stagesSlider.setPaintTicks(true);

				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.add(stagesLabel);
				updateFreqLabel();
				frame.add(stagesSlider);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			

				frame.add(controlTypeLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.add(controlType);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				controlType.setSelectedIndex(pong.getControlMode());

				stagesSlider.setValue((pong.getStages()));

				frame.addWindowListener(new MyWindowListener());
				frame.setVisible(true);
				frame.setAlwaysOnTop(true);
				frame.pack();
				frame.setLocation(new Point(pong.getX() + 200, pong.getY() + 150));			}
		});
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == stagesSlider) {
			pong.setStages(stagesSlider.getValue());
			updateFreqLabel();
		}
	}

	public void updateFreqLabel() {
		stagesLabel.setText("Stages " + String.format("%d", 2 * pong.getStages()));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == controlType) {
			JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
			String range = (String)cb.getSelectedItem();
			if (range == listOptions[0]) {
				pong.setControlMode(0);
			} else if (range == listOptions[1]) {
				pong.setControlMode(1);
			} 
		}
		pong.setupControls();
	}	
	
	class MyWindowListener implements WindowListener
	{
	@Override
		public void windowActivated(WindowEvent arg0) {
		}

	@Override
		public void windowClosed(WindowEvent arg0) {
		}

	@Override
		public void windowClosing(WindowEvent arg0) {
			pong.clearCP();
		}

	@Override
		public void windowDeactivated(WindowEvent arg0) {
		}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {

	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}
}

}