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
 * This class represents the MULX instruction.
 * 
 * @author andrew
 */
public class Mulx extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1553893954601390192L;
	final int addr;
	
	/**
	 * Multiplies ACC by the value of a register.
	 * 
	 * @param addr the register address to multiply with ACC
	 */
	public Mulx(int addr) {
		/// GSW removing register limits
		// if(addr < 0 || addr > 63) {
		//	throw new IllegalArgumentException("addr out of range: " + addr +
		//			" - valid range: 0 - 63");
		// }
		this.addr = addr;
	}
	
	@Override
	public int getHexWord() {
		return ((addr & 0x3f) << 5) | 0x0a;
	}

	@Override
	public String getInstructionString() {
		return "Mulx(" + Util.getRegisterName(addr) + ")";
	}
	// GSW added for integration with SpinCAD Designer	
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "MULX " + Util.getRegisterName(addr);
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		state.getACC().scale(Util.regToDouble(state.getRegVal(addr)));		
	}
}
