/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick
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
 * This class represents the LDAX instruction.
 * 
 * @author andrew
 */
public class LoadAccumulator extends Instruction {
	final int addr;
	
	/**
	 * Loads ACC with reg addr.
	 * 
	 * @param addr the address to load into ACC (0-63)
	 */
	public LoadAccumulator(int addr) {
		if(addr < 0 || addr > 63) {
			throw new IllegalArgumentException("addr out of range: " + addr +
					" - valid range: 0 - 63");
		}
		this.addr = addr;
	}
	
	@Override
	public int getHexWord() {
		return ((addr & 0x3f) << 5) | 0x05;
	}

	@Override
	public String getInstructionString() {
		return "LoadAccumulator(" + addr + ")";
	}
	
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "LDAX " + addr;
		}
		else
			return "Error! Invalid mode.";
	}
	
	@Override
	public void simulate(SimulatorState state) {
		state.setACCVal(state.getRegVal(addr));
	}
}
