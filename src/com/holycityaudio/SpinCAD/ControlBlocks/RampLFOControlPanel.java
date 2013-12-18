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

package com.holycityaudio.SpinCAD.ControlBlocks;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.CADBlocks.RampLFOCADBlock;

public class RampLFOControlPanel implements ChangeListener, ActionListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4717076120154216169L;
	
	private JSlider lfoWidthSlider = new JSlider(JSlider.HORIZONTAL, 0, 32767, 8192);
	private JLabel lfoWidthLabel = new JLabel("LFO Rate");
	
	private JSlider lfoRateSlider = new JSlider(JSlider.HORIZONTAL, 0, 511, 200);
	private JLabel lfoRateLabel = new JLabel("LFO Rate");
	
	private JFrame frame;
	private RampLFOCADBlock pC;

	public RampLFOControlPanel(RampLFOCADBlock rampLFOCADBlock) {
		lfoWidthSlider.addChangeListener(this);
		lfoRateSlider.addChangeListener(this);
		this.pC = rampLFOCADBlock;
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("LFO");
				frame.setTitle("");
				frame.setResizable(false);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				frame.setLocation(new Point(pC.getX() + 200, pC.getY() + 150));

				//				    graph.setBorder(BorderFactory.createEmptyBorder(0,10,10,10)); 

				lfoRateSlider.setMajorTickSpacing(25);

				frame.add(lfoRateLabel);
				frame.add(lfoRateSlider);
				frame.add(lfoWidthLabel);
				frame.add(lfoWidthSlider);

				lfoRateSlider.setValue(pC.getLFORate());
				lfoRateLabel.setText(String.format("%2d", pC.getLFORate()));
				lfoWidthSlider.setValue(pC.getLFOWidth());
				lfoWidthLabel.setText(String.format("%2d", pC.getLFOWidth()));
				frame.setVisible(true);
				frame.pack();
			}
		});
	}

		@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	public void itemStateChanged(ItemEvent arg0) {
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == lfoRateSlider) {
			pC.setLFORate( lfoRateSlider.getValue());
			lfoRateLabel.setText(String.format("%2d", pC.getLFORate()));
		}
		else if (e.getSource() == lfoWidthSlider) {
			pC.setLFOWidth(lfoWidthSlider.getValue());
			lfoWidthLabel.setText(String.format("%2d", pC.getLFOWidth()));
		}
	}	
}