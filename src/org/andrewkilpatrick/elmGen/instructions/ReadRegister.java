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

import org.andrewkilpatrick.elmGen.simulator.Reg;
import org.andrewkilpatrick.elmGen.simulator.SimulatorState;
import org.andrewkilpatrick.elmGen.util.Util;

/**
 * This class represents the RDAX instruction.
 * 
 * @author andrew
 */
public class ReadRegister extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5725695398631154403L;
	final int addr;
	final double scale;
	
	/**
	 * Reads a register file, multiplies by scale and
	 * adds to ACC.
	 * 
	 * @param addr the register address to read
	 * @param scale the amount to scale the register
	 */
	public ReadRegister(int addr, double scale) {
		if(addr < 0 || addr > 63) {
			throw new IllegalArgumentException("addr out of range: " + addr +
					" - valid range: 0 - 63");
		}
		checkS114(scale);
		this.addr = addr;
		this.scale = scale;
	}
	
	@Override
	public int getHexWord() {
		return ((convS114(scale) & 0xffff) << 16) | 
			((addr & 0x3f) << 5) | 0x04;
	}

	@Override
	public String getInstructionString() {
		return "ReadRegister(" + addr + "," + scale + ")";
	}
	// GSW added for integration with SpinCAD Designer
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "RDAX " + Util.getRegisterName(addr) + "," + Util.removeComma(String.format("%6.10f",scale));		
		}
		else
			return 
				"Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		Reg reg = new Reg(state.getRegVal(addr));
		reg.scale(scale);
		state.getACC().add(reg.getValue());
	}
}
