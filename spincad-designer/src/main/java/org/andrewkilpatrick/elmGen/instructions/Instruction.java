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
 * This class is the superclass that all instructions extend.
 * 
 * @author andrew
 */
public abstract class Instruction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7070499813585484961L;
	/**
	 * Creates an instruction.
	 */
	public Instruction() {
	}
	
	public abstract int getHexWord();
	
	public abstract String getInstructionString();
	
	public abstract void simulate(SimulatorState state);
	
	/**
	 * Checks the range of a double to be converted to S1.14 (16 bit) format.
	 * The valid range is -2.0 to +1.99993896484.
	 * 
	 * @param num the number to check
	 * @throws IllegalArgumentException if the number is out of range
	 */
	protected void checkS114(double num) {
		if(num < -2.0 || num > 1.99993896484) {
			throw new IllegalArgumentException("S1.14 argument out of range: " +
					num + " - valid range: -2.0 - 1.99993896484");
		}
	}
	
	/**
	 * Checks the range of a double to be converted to S1.9 (11 bit) format.
	 * The valid range is -2.0 to +1.998046875.
	 * 
	 * @param num the number to check
	 * @throws IllegalArgumentException if the number is out of range
	 */	
	protected void checkS19(double num) {
		if(num < -2.0 || num > 1.998046875) {
			throw new IllegalArgumentException("S1.9 argument out of range: " +
					num + " - valid range: -2.0 - 1.998046875");
		}
	}
	
	/**
	 * Checks the range of a double to be converted to S.10 (11 bit) format.
	 * The valid range is -1.0 to +0.9990234375.
	 * 
	 * @param num the number to check
	 * @throws IllegalArgumentException if the number is out of range
	 */	
	protected void checkS10(double num) {
		if(num < -1.0 || num > 0.9990234375) {
			throw new IllegalArgumentException("S.10 argument out of range: " +
					num + " - valid range: -1.0 - 0.9990234375");
		}
	}

	/**
	 * Checks the range of a double to be converted to S.15 (16 bit) format.
	 * The valid range is -1.0 to +0.999969482421875.
	 * 
	 * @param num the number to check
	 * @throws IllegalArgumentException if the number is out of range
	 */	
	protected void checkS15(double num) {
		if(num < -1.0 || num > 0.999969482421875) {
			throw new IllegalArgumentException("S.15 argument out of range: " +
					num + " - valid range: -1.0 - 0.999969482421875");
		}
	}

	/**
	 * Converts a double into the S1.14 (16 bit) binary format.
	 * 
	 * @param num the number to convert
	 * @return the binary formatted number
	 */
	protected int convS114(double num) {
//		return Math.round(num * (32768.0 / 2.0)) & 0xffff;
		return (int)(num * (32768.0 / 2.0)) & 0xffff;  // SpinASM compatibility
	}
	
	/**
	 * Converts a double into the S1.9 (11 bit) binary format.
	 * 
	 * @param num the number to convert
	 * @return the binary formatted number
	 */
	protected int convS19(double num) {
//		return Math.round(num * (1024.0 / 2.0)) & 0x7ff;
		return (int)(num * (1024.0 / 2.0)) & 0x7ff;  // SpinASM compatibility
	}
	
	/**
	 * Converts a double into the S.10 (11 bit) binary format.
	 * 
	 * @param num the number to convert
	 * @return the binary formatted number
	 */
	protected int convS10(double num) {
//		return Math.round(num * 1024.0) & 0x7ff;
		return (int)(num * 1024.0) & 0x7ff;  // SpinASM compatibility
	}

	/**
	 * Converts a double into the S.15 (16 bit) binary format.
	 * 
	 * @param num the number to convert
	 * @return the binary formatted number
	 */
	protected int convS15(double num) {
//		return Math.round(num * 32768.0) & 0xffff;
		return (int)(num * 32768.0) & 0x7fff;  // SpinASM compatibility
	}
	// GSW added for integration with SpinCAD Designer
	public String getInstructionString(int i) {
		// ---
		return null;
	}
}
