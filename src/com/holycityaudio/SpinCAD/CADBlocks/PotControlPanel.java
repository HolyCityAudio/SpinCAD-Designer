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

import com.holycityaudio.SpinCAD.CADBlocks.ClipControlCADBlock;

public class PotControlPanel implements ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4717076120154216169L;

	private JCheckBox speedupCB = new JCheckBox();
	private JFrame frame;
	private PotCADBlock pC;

	public PotControlPanel(final PotCADBlock pCB) {
		speedupCB.addItemListener(this);
		pC = pCB;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String panelName = String.format("Pot %d response", pCB.getPotNum());
				frame = new JFrame(panelName);
				frame.setTitle(panelName);
				frame.setResizable(false);

				speedupCB.setText("Speed Up");

				frame.add(speedupCB);

				speedupCB.setSelected(pCB.getSpeedup());
				frame.setVisible(true);
				frame.setSize(180, 50);
				frame.setLocation(new Point(pCB.getX() + 200, pCB.getY() + 150));
				frame.setAlwaysOnTop(true);

			}
		});
	}

	@Override

	public void itemStateChanged(ItemEvent arg0) {
		Object source = arg0.getItemSelectable();

		if (source == speedupCB) {
			if(pC.getSpeedup() == true) {
				pC.setSpeedup(false);
			}
			else
				pC.setSpeedup(true);	    		
		} 
	}
}
	