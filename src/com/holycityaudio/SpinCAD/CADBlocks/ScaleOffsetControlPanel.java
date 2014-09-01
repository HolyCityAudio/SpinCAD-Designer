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
public class ScaleOffsetControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider inLowSlider;
	JSlider inHighSlider;
	JSlider outLowSlider;
	JSlider outHighSlider;


	JLabel inLowLabel;
	JLabel inHighLabel;
	JLabel outLowLabel;
	JLabel outHighLabel;
	
	private ScaleOffsetControlCADBlock sof;
	
	public ScaleOffsetControlPanel(ScaleOffsetControlCADBlock scaleOffsetControlCADBlock) {
		this.sof = scaleOffsetControlCADBlock;
		this.setTitle("Scale Offset");
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		inLowSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		inLowSlider.addChangeListener(this);
		inHighSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		inHighSlider.addChangeListener(this);
		outLowSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		outLowSlider.addChangeListener(this);
		outHighSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		outHighSlider.addChangeListener(this);
		
		inLowLabel = new JLabel();
		inHighLabel = new JLabel();
		outLowLabel = new JLabel();
		outHighLabel = new JLabel();
		
		this.getContentPane().add(inLowLabel);
		this.getContentPane().add(inLowSlider);
		this.getContentPane().add(inHighLabel);
		this.getContentPane().add(inHighSlider);
		this.getContentPane().add(outLowLabel);
		this.getContentPane().add(outLowSlider);
		this.getContentPane().add(outHighLabel);
		this.getContentPane().add(outHighSlider);		
		
		inLowSlider.setValue((int)Math.round((scaleOffsetControlCADBlock.getInLow() * 100.0)));
		inHighSlider.setValue((int)Math.round((scaleOffsetControlCADBlock.getInHigh() * 100.0)));
		outLowSlider.setValue((int)Math.round((scaleOffsetControlCADBlock.getOutLow() * 100.0)));
		outHighSlider.setValue((int)Math.round((scaleOffsetControlCADBlock.getOutHigh() * 100.0)));
		inLowLabel.setText("Input Low " + String.format("%2.2f", sof.getInLow()));
		inHighLabel.setText("Input High " + String.format("%2.2f", sof.getInHigh()));
		outLowLabel.setText("Output Low " + String.format("%2.2f", sof.getOutLow()));
		outHighLabel.setText("Output High " + String.format("%2.2f", sof.getOutHigh()));

		this.setVisible(true);
		this.pack();
		this.setLocation(new Point(scaleOffsetControlCADBlock.getX() + 200, scaleOffsetControlCADBlock.getY() + 150));
		this.setResizable(false);
		this.setAlwaysOnTop(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == inLowSlider) {
			sof.setInLow((double)inLowSlider.getValue() / 100.0);
			inLowLabel.setText("Input Low " + String.format("%2.2f", sof.getInLow()));
			System.out.println("Input Low " + String.format("%2.2f", sof.getInLow()));
		}
		else if(ce.getSource() == inHighSlider) {
			sof.setInHigh((double)inHighSlider.getValue() / 100.0);
			inHighLabel.setText("Input High " + String.format("%2.2f", sof.getInHigh()));
			System.out.println("Input High " + String.format("%2.2f", sof.getInHigh()));
		}
		else if(ce.getSource() == outLowSlider) {
			sof.setOutLow((double)outLowSlider.getValue() / 100.0);
			outLowLabel.setText("Output Low " + String.format("%2.2f", sof.getOutLow()));
			System.out.println("Output Low " + String.format("%2.2f", sof.getOutLow()));
		}
		else if(ce.getSource() == outHighSlider) {
			sof.setOutHigh((double)outHighSlider.getValue() / 100.0);
			outHighLabel.setText("Output High " + String.format("%2.2f", sof.getOutHigh()));
			System.out.println("Output High " + String.format("%2.2f", sof.getOutHigh()));
		}

	}
}