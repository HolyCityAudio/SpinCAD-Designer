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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;


public class PotControlPanel implements ItemListener {

	/**
	 * 
	 */

	private JCheckBox speedupCB = new JCheckBox();
	private JFrame frame;
	private PotCADBlock pC;

	public PotControlPanel(final PotCADBlock pCB) {
		pC = pCB;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				String panelName = String.format("Pot %d response", pCB.getPotNum());
				frame = new JFrame(panelName);
				frame.setTitle(panelName);
				frame.setResizable(false);

				speedupCB.setText("Speed Up");
				speedupCB.setSelected(pCB.getSpeedup());

				frame.add(speedupCB);

				frame.setVisible(true);
				frame.setSize(180, 50);
				frame.setLocation(new Point(pCB.getX() + 200, pCB.getY() + 150));
				frame.setAlwaysOnTop(true);

			}
		});
		speedupCB.addItemListener(this);
	}

	@Override

	public void itemStateChanged(ItemEvent arg0) {
		Object source = arg0.getItemSelectable();

		if (source == speedupCB) {
	        if (arg0.getStateChange() == ItemEvent.DESELECTED) {
				pC.setSpeedup(false);
			}
			else
				pC.setSpeedup(true);	    		
		} 
	}
}
	