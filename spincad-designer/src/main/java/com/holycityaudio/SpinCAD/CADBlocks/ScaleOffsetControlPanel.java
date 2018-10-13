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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



@SuppressWarnings("serial")
public class ScaleOffsetControlPanel extends JFrame implements ChangeListener{
	JSlider inLowSlider;
	JSlider inHighSlider;
	JSlider outLowSlider;
	JSlider outHighSlider;


	JLabel inLowLabel;
	JLabel inHighLabel;
	JLabel outLowLabel;
	JLabel outHighLabel;
	
	JLabel scaleLabel;
	JLabel offsetLabel;
	
	double scale;
	double offset;
	
	private ScaleOffsetControlCADBlock sof;
	
	public ScaleOffsetControlPanel(ScaleOffsetControlCADBlock scaleOffsetControlCADBlock) {
		this.sof = scaleOffsetControlCADBlock;
		this.setTitle("Scale Offset");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
		
	}

	private void createAndShowGUI() {
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		inLowSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		inLowSlider.addChangeListener(this);
		inHighSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		inHighSlider.addChangeListener(this);
		outLowSlider = new JSlider(JSlider.HORIZONTAL, -200, 100, 0);
		outLowSlider.addChangeListener(this);
		outHighSlider = new JSlider(JSlider.HORIZONTAL, -200, 100, 0);
		outHighSlider.addChangeListener(this);
		
		inLowLabel = new JLabel();
		inHighLabel = new JLabel();
		outLowLabel = new JLabel();
		outHighLabel = new JLabel();
		scaleLabel = new JLabel();
		offsetLabel = new JLabel();
		
		this.add(Box.createRigidArea(new Dimension(5,4)));			
		this.getContentPane().add(inLowLabel);
		this.getContentPane().add(inLowSlider);
		this.getContentPane().add(inHighLabel);
		this.getContentPane().add(inHighSlider);
		this.add(Box.createRigidArea(new Dimension(5,4)));			
		this.getContentPane().add(outLowLabel);
		this.getContentPane().add(outLowSlider);
		this.getContentPane().add(outHighLabel);
		this.getContentPane().add(outHighSlider);		
		this.add(Box.createRigidArea(new Dimension(5,4)));			
		this.getContentPane().add(scaleLabel);
		this.add(Box.createRigidArea(new Dimension(5,4)));			
		this.getContentPane().add(offsetLabel);		
		this.add(Box.createRigidArea(new Dimension(5,4)));			
		
		inLowSlider.setValue((int)Math.round((sof.getInLow() * 100.0)));
		inHighSlider.setValue((int)Math.round((sof.getInHigh() * 100.0)));
		outLowSlider.setValue((int)Math.round((sof.getOutLow() * 100.0)));
		outHighSlider.setValue((int)Math.round((sof.getOutHigh() * 100.0)));
		inLowLabel.setText("Input Low " + String.format("%2.2f", sof.getInLow()));
		inHighLabel.setText("Input High " + String.format("%2.2f", sof.getInHigh()));
		outLowLabel.setText("Output Low " + String.format("%2.2f", sof.getOutLow()));
		outHighLabel.setText("Output High " + String.format("%2.2f", sof.getOutHigh()));

		this.setVisible(true);
		this.pack();
		this.setLocation(new Point(sof.getX() + 200, sof.getY() + 150));
		this.setResizable(false);
		this.setAlwaysOnTop(true);
		
	}
	
	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == inLowSlider) {
			sof.setInLow((double)inLowSlider.getValue() / 100.0);
			inLowLabel.setText("Input Low " + String.format("%2.2f", sof.getInLow()));
			updateScaleOffsetLabels();
		}
		else if(ce.getSource() == inHighSlider) {
			sof.setInHigh((double)inHighSlider.getValue() / 100.0);
			inHighLabel.setText("Input High " + String.format("%2.2f", sof.getInHigh()));
			updateScaleOffsetLabels();
		}
		else if(ce.getSource() == outLowSlider) {
			sof.setOutLow((double)outLowSlider.getValue() / 100.0);
			outLowLabel.setText("Output Low " + String.format("%2.2f", sof.getOutLow()));
			updateScaleOffsetLabels();
		}
		else if(ce.getSource() == outHighSlider) {
			sof.setOutHigh((double)outHighSlider.getValue() / 100.0);
			outHighLabel.setText("Output High " + String.format("%2.2f", sof.getOutHigh()));
			updateScaleOffsetLabels();
		}
	}

	public int checkValuesInRange() {
		scale = (sof.getOutHigh() - sof.getOutLow())/(sof.getInHigh() - sof.getInLow());
		offset = sof.getOutLow() - (sof.getInLow() * scale);
		if((scale < -2.0) || (scale > 1.99993896484)) {
			return -1;			
		}
		else if((offset < -1.0) || (offset > 1.0)) {
			return -2;
		}
		else {
			return 0;
		}
	}
	
	private void updateScaleOffsetLabels() {
		int value = checkValuesInRange(); 
		if(value == 0) {
			scaleLabel.setForeground(Color.BLACK);
			scaleLabel.setOpaque(false);		
			offsetLabel.setForeground(Color.BLACK);
			offsetLabel.setOpaque(false);		
		} else if (value == -1) {
			scaleLabel.setBackground(Color.RED);
			scaleLabel.setForeground(Color.WHITE);
			scaleLabel.setOpaque(true);		
		} else if (value == -2) {
			offsetLabel.setBackground(Color.RED);
			offsetLabel.setForeground(Color.WHITE);
			offsetLabel.setOpaque(true);		
		} 
		scaleLabel.setText("Scale " + String.format("%3.3f", scale));
		offsetLabel.setText("Offset " + String.format("%3.3f", offset));
	}
}