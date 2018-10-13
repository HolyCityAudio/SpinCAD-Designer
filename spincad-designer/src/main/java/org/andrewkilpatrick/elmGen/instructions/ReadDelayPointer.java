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

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.Reg;
import org.andrewkilpatrick.elmGen.simulator.SimulatorState;
import org.andrewkilpatrick.elmGen.util.Util;


/**
 * This class represents the RMPA instruction.
 * 
 * @author andrew
 */
public class ReadDelayPointer extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6429963690910982586L;
	final double scale;
	
	/**
	 * Reads the delay RAM location pointed to by ADDR_PTR
	 * and multiplies it by scale. The result is added to ACC.
	 * 
	 * @param scale the amount to scale the result before adding it to ACC
	 */
	public ReadDelayPointer(double scale) {
		checkS19(scale);
		this.scale = scale;
	}
	
	@Override
	public int getHexWord() {
//		return ((convS19(scale) & 0x7ff) << 21) | 
//			((0x18 & 0xff) << 5) | 0x01;
		return ((convS19(scale) & 0x7ff) << 21) | 0x01;	
	}

	@Override
	public String getInstructionString() {
		return "ReadDelayPointer(" + scale + ")";
	}
	// GSW added for integration with SpinCAD Designer	
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "RMPA " + Util.removeComma(scale);
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		Reg reg = new Reg(state.getDelayVal(state.getRegVal(ElmProgram.ADDR_PTR) >> 8));
		reg.scale(scale);
		state.getACC().add(reg.getValue());
	}
}
