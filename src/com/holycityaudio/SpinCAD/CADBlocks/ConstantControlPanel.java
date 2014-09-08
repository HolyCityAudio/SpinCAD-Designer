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

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ConstantControlPanel extends JFrame implements ChangeListener {
	JSlider constantSlider;
	JLabel constantLabel;

	private ConstantCADBlock sof;

	public ConstantControlPanel(final ConstantCADBlock cCB) {
		constantSlider = new JSlider(JSlider.HORIZONTAL, 0, 999, 0);
		constantSlider.addChangeListener(this);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sof = cCB;
				setTitle("Constant");
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				constantLabel = new JLabel();

				getContentPane().add(constantLabel);
				getContentPane().add(constantSlider);		

				constantSlider.setValue((int)Math.round((cCB.getConstant())));
				updateConstantLabel();

				setVisible(true);
				pack();
				setLocation(new Point(cCB.getX() + 200, cCB.getY() + 150));
				setResizable(false);
				setAlwaysOnTop(true);
			}
		});
	}

	@Override

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == constantSlider) {
			sof.setConstant(constantSlider.getValue());
			updateConstantLabel();
		}

	}

	private void updateConstantLabel() {
		constantLabel.setText("Value: " + String.format("%3.3f", sof.getConstant()/1000.0));
	}
}