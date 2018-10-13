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
 * This class represents the SOF instruction.
 * 
 * @author andrew
 */
public class ScaleOffset extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1901768076087913530L;
	final double scale;
	final double offset;
	
	/**
	 * Scales the ACC by scale and add offset.
	 * 
	 * @param scale the value to scale the ACC by
	 * @param offset the amount to add to the result
	 */
	public ScaleOffset(double scale, double offset) {
		checkS114(scale);
		checkS10(offset);
		this.scale = scale;
		this.offset = offset;		
	}
	
	@Override
	public int getHexWord() {
		return ((convS114(scale) & 0xffff) << 16) | 
			((convS10(offset) & 0x7ff) << 5) | 0x0d;
	}

	@Override
	public String getInstructionString() {
		return "ScaleOffset(" + scale + "," + offset + ")";
	}
	// GSW added for integration with SpinCAD Designer
	public String getInstructionString(int mode) {
		if(mode == 1) {
			return "SOF " + Util.removeComma(String.format("%6.10f",scale)) + "," + Util.removeComma(String.format("%6.10f",offset));		
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		state.getACC().scale(scale);
		state.getACC().add(offset);
	}
}
