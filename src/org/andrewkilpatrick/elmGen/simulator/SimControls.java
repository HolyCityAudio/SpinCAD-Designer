package org.andrewkilpatrick.elmGen.simulator;

import org.andrewkilpatrick.elmGen.simulator.ControlPanel;

import javax.swing.JFrame;

public class SimControls {
	JFrame frame = null;

	public SimControls(final SpinSimulator ss) {
		// TODO Auto-generated constructor stub
			new ControlPanel(ss);
	}
}