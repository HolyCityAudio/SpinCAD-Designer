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
import org.andrewkilpatrick.elmGen.util.Util;

/**
 * This class represents the WRAP instruction.
 * 
 * @author andrew
 */
public class WriteAllpass extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1609543469507398104L;
	final int addr;
	final double scale;
	
	/**
	 * Stores ACC in the register addr. Multiplies ACC by scale
	 * and adds the contents of the last delay RAM read (LR) to
	 * the result. This instruction is typically used for allpass
	 * filters in a reverb program. 
	 * 
	 * @param addr the delay RAM address (0-32767)
	 * @param scale the amount to scale the result before adding it to ACC
	 */
	public WriteAllpass(int addr, double scale) {
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
			((addr & 0xffff) << 5) | 0x03;
	}

	@Override
	public String getInstructionString() {
		return "WriteAllpass(" + addr + "," + scale + ")";
	}
	
	// this function call added by Gary Worsham for compatibility with SpinCAD Designer
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "WRAP " + addr + "," + Util.removeComma(scale);
		}
		else
			return "Error! Invalid mode.";
	}

	
	@Override
	public void simulate(SimulatorState state) {
		state.setDelayVal(addr, state.getACCVal());
		state.getACC().scale(scale);
		state.getACC().add(state.getLRVal());
	}
}
