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
 * This class represents the AND instruction.
 * 
 * @author andrew
 */
public class And extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3510166248326688669L;
	final int mask;
	
	/**
	 * ANDs the the ACC with mask.
	 * 
	 * @param mask the 24 bit mask
	 */
	public And(int mask) {
		this.mask = mask & 0xffffff;
	}
	
	@Override
	public int getHexWord() {
		return (mask  << 8) | 0x0e;
	}

	@Override
	public String getInstructionString() {
		return "And(" + String.format("%08X", mask) + ")";
	}
	// GSW added for integration with SpinCAD Designer
	// also added '$" to prefix hex values, which seems important
	public String getInstructionString(int mode) {
		if(mode == 1) {
		return "AND " + String.format("$%08X", mask);
		}
		else 
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		state.getACC().and(mask);
	}
}
