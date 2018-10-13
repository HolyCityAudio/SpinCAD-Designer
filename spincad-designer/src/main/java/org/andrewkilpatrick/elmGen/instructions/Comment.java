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

import java.io.Serializable;

import org.andrewkilpatrick.elmGen.simulator.SimulatorState;

/**
 * This class represents the AND instruction.
 * 
 * @author andrew
 */
public class Comment extends Instruction implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7982679767433623680L;
	final String remark;
	
	/**
	 * ANDs the the ACC with mask.
	 * 
	 * @param mask the 24 bit mask
	 */
	public Comment(String r) {
		remark = r;
	}
	
	@Override
	public int getHexWord() {
		return (-1);
	}

	@Override
	public String getInstructionString() {
		return ";------ " + remark;
	}

	public String getInstructionString(int mode) {
		if(mode == 1) {
			return ";------ " + remark;
		}
		else 
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
	}
}
