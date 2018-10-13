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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


@SuppressWarnings("serial")
class PingPongControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider tapSlider0;
	JSlider tapSlider1;

	JSlider fbSlider;
	JSlider delayGainSlider;
	JSlider lengthSlider;

	JLabel tapLabel0;
	JLabel tapLabel1;


	JLabel fbLabel;
	JLabel delayGainLabel;
	JLabel lengthLabel;
	
	private PingPongCADBlock pong;
	
	public PingPongControlPanel(PingPongCADBlock ppcb) {
		this.pong = ppcb;
		this.setTitle("Ping Pong Delay");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		this.setResizable(false);
		
		tapSlider0 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider0.addChangeListener(this);
		
		tapSlider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider1.addChangeListener(this);
		
		fbSlider = new JSlider(JSlider.HORIZONTAL, 0, 90, 0);
		fbSlider.addChangeListener(this);
	
		delayGainSlider = new JSlider(JSlider.HORIZONTAL, 0, 99, 0);
		delayGainSlider.addChangeListener(this);

		lengthSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 0);
		lengthSlider.addChangeListener(this);
		
		tapLabel0 = new JLabel();
		tapLabel1 = new JLabel();

		fbLabel = new JLabel();
		delayGainLabel = new JLabel();
		lengthLabel = new JLabel();
		
		this.getContentPane().add(tapLabel0);
		this.getContentPane().add(tapSlider0);
		
		this.getContentPane().add(tapLabel1);
		this.getContentPane().add(tapSlider1);
			
		this.getContentPane().add(fbLabel);
		this.getContentPane().add(fbSlider);
		this.getContentPane().add(delayGainLabel);
		this.getContentPane().add(delayGainSlider);

		this.getContentPane().add(lengthLabel);
		this.getContentPane().add(lengthSlider);
		
		tapSlider0.setValue((int)Math.round((ppcb.getTapLevel(0) * 100.0)));
		tapSlider1.setValue((int)Math.round((ppcb.getTapLevel(1) * 100.0)));

		fbSlider.setValue((int)Math.round((ppcb.getfbLevel() * 100.0)));
		delayGainSlider.setValue((int)Math.round((ppcb.getDelayGain() * 100.0)));
		lengthSlider.setValue((int)Math.round((ppcb.getLength() * 1000.0)));
		
		this.setVisible(true);
		this.pack();
		this.setLocation(new Point(pong.getX() + 200, pong.getY() + 150));
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// ---
		
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == tapSlider0) {
			pong.setTapLevel(0, (double)tapSlider0.getValue() / 100.0);
			tapLabel0.setText("Tap 1 level " + String.format("%2.2f", pong.getTapLevel(0)));
		}
		else if(ce.getSource() == tapSlider1) {
			pong.setTapLevel(1, (double)tapSlider1.getValue() / 100.0);
			tapLabel1.setText("Tap 2 level " + String.format("%2.2f", pong.getTapLevel(1)));
		}
		else if(ce.getSource() == fbSlider) {
			pong.setfbLevel((double)fbSlider.getValue() / 100.0);
			fbLabel.setText("Feedback level " + String.format("%2.2f", pong.getfbLevel()));
		}
		else if(ce.getSource() == delayGainSlider) {
			pong.setDelayGain((double)delayGainSlider.getValue() / 100.0);
			delayGainLabel.setText("Delay Gain " + String.format("%2.2f", pong.getDelayGain()));
		}
		else if(ce.getSource() == lengthSlider) {
			pong.setLength((double)lengthSlider.getValue() / 1000.0);
			lengthLabel.setText("Delay (sec) " + String.format("%1.3f", pong.getLength()));
		}
	}
}