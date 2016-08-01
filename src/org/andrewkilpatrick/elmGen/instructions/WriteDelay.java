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
 * This class represents the WRA instruction.
 * 
 * @author andrew
 */
public class WriteDelay extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7594162401835224762L;
	final int addr;
	final double scale;
	
	/**
	 * Writes ACC to a delay RAM location and then multiplies ACC by scale. 
	 * 
	 * @param addr the delay RAM address (0-32767)
	 * @param scale the amount to scale the result before adding it to ACC
	 */
	public WriteDelay(int addr, double scale) {
		if(addr < 0 || addr > 32767) {
			throw new IllegalArgumentException("address out of range: " + addr +
					" - must be: 0 - 32767");
		}
		checkS19(scale);
		this.addr = addr;
		this.scale = scale;
	}
	
	@Override
	public int getHexWord() {
		return ((convS19(scale) & 0x7ff) << 21) | 
			((addr & 0xffff) << 5) | 0x02;
	}

	@Override
	public String getInstructionString() {
		return "WriteDelay(" + addr + "," + scale + ")";
	}

	// this function call added by Gary Worsham for compatibility with SpinCAD Designer
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "WRA " + addr + "," + scale;
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		state.setDelayVal(addr, state.getACCVal());
		state.getACC().scale(scale);
	}
}
