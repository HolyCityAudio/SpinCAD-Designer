/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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
package org.andrewkilpatrick.elmGen.simulator;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// Added by GSW to integrate into SpinCAD Designer
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;

@SuppressWarnings("serial")
class ControlPanel extends JFrame implements ChangeListener, ActionListener {
	JSlider potSlider0;
	JSlider potSlider1;
	JSlider potSlider2;
	JLabel potLabel0;
	JLabel potLabel1;
	JLabel potLabel2;
	JButton stopSimButton;
	SpinSimulator sim;
	
	public ControlPanel(SpinSimulator sim) {
		this.sim = sim;
		this.setTitle("SpinSimulator");
		this.setPreferredSize(new Dimension(300, 200));
		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		potSlider0 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		potSlider0.addChangeListener(this);
		potSlider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		potSlider1.addChangeListener(this);
		potSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		potSlider2.addChangeListener(this);
		stopSimButton = new JButton("Stop Simulator");
		stopSimButton.addActionListener(this);
		potLabel0 = new JLabel();
		potLabel1 = new JLabel();
		potLabel2 = new JLabel();
		
		this.getContentPane().add(potLabel0);
		this.getContentPane().add(potSlider0);
		this.getContentPane().add(potLabel1);
		this.getContentPane().add(potSlider1);
		this.getContentPane().add(potLabel2);
		this.getContentPane().add(potSlider2);
		this.getContentPane().add(stopSimButton);
		
		this.pack();
		this.setVisible(true);
		
		potSlider0.setValue((int)Math.round((sim.getPot(0) * 100.0)));
		potSlider1.setValue((int)Math.round((sim.getPot(1) * 100.0)));
		potSlider2.setValue((int)Math.round((sim.getPot(2) * 100.0)));
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == potSlider0) {
			sim.setPot(0, (double)potSlider0.getValue() / 100.0);
			potLabel0.setText("Pot 0 - " + String.format("%2.2f", sim.getPot(0)));
			System.out.println("pot0: " + String.format("%2.2f", sim.getPot(0)));
		}
		else if(ce.getSource() == potSlider1) {
			sim.setPot(1, (double)potSlider1.getValue() / 100.0);
			potLabel1.setText("Pot 1 - " + String.format("%2.2f", sim.getPot(1)));
			System.out.println("pot1: " + String.format("%2.2f", sim.getPot(1)));
		}
		else if(ce.getSource() == potSlider2) {
			sim.setPot(2, (double)potSlider2.getValue() / 100.0);
			potLabel2.setText("Pot 2 - " + String.format("%2.2f", sim.getPot(2)));
			System.out.println(potLabel2.getText());
		}
	}

	public void actionPerformed(ActionEvent al) {
		if(al.getSource() == stopSimButton) {
			sim.stopSimulator();
			System.out.println("stopping simulator");
		}
	}
}