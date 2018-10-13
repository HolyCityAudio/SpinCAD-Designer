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
 * This class represents the JAM instruction.
 * 
 * @author andrew
 */
public class Jam extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4055052897428773349L;
	final int lfo;
	
	/**
	 * Reset a RAMP LFO to the starting point.
	 * 
	 * @param lfo the LFO to reset (0 or 1)
	 */
	public Jam(int lfo) {
		// GSW corrected LFO values, it was a bug, pretty sure
		if(lfo < 2 || lfo > 3) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 2 or 3");
		}
		this.lfo = lfo;
	}
	
	@Override
	public int getHexWord() {
		// GSW corrected LFO values, it was a bug, pretty sure
		return 0x80 | (((lfo - 2) & 0x01) << 6) | 0x13;	// this may be incorrect if lfo needs to be 2 or 3
	}

	@Override
	public String getInstructionString() {
		return "Jam(" + lfo + ")";
	}
	// GSW added for integration with SpinCAD Designer	
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "JAM " + lfo;
		}
		else
			return "Error! Invalid mode.";
	}
	
	@Override
	public void simulate(SimulatorState state) {
		// GSW corrected LFO values, it was a bug, pretty sure
		state.jamRampLFO(lfo - 2);
	}
}
