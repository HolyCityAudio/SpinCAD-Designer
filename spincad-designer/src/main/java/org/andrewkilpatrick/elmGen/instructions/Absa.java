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
package org.andrewkilpatrick.elmGen.instructions;

import org.andrewkilpatrick.elmGen.simulator.SimulatorState;

/**
 * This class represents the ABSA instruction.
 * 
 * @author andrew
 */
public class Absa extends Instruction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7441163369408334307L;

	/**
	 * Makes ACC positive.
	 */
	public Absa() {
		// nothing to do
	}

	@Override
	public int getHexWord() {
		return 0x09;
	}

	@Override
	public String getInstructionString() {
		return "Absa()";
	}
	// GSW added for integration with SpinCAD Designer
	public String getInstructionString(int mode) {
		if(mode ==1) {
			return "ABSA";
		}
		else {
			return "Error! Invalid mode.";
		}
	}

	@Override
	public void simulate(SimulatorState state) {
		state.getACC().abs();
	}
}
