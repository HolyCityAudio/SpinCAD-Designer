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
import org.andrewkilpatrick.elmGen.simulator.SimulatorState;


/**
 * This class represents the SKP instruction.
 * 
 * @author andrew
 */
public class Skip extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2634092684212169338L;
	final int flags;
	final int nskip;
	
	/**
	 * Conditional program execution.
	 * 
	 * @param flags the flags
	 * @param nskip the number of instructions to skip (1-63)
	 */
	public Skip(int flags, int nskip) {
		this.flags = flags;
		if(nskip < 1 || nskip > 63) {
			throw new IllegalArgumentException("nskip invalid: " + nskip + 
					" - must be 1 - 63");
		}
		this.nskip = nskip;
	}
	
	@Override
	public int getHexWord() {
		return (int)(((flags & 0x1f) << 27) | ((nskip & 0x3f) << 21) | 0x11);
	}

	@Override
	public String getInstructionString() {
		return "Skip(" + String.format("%02X", flags) + "," + nskip + ")";
	}
	
	// this function call added by Gary Worsham for compatibility with SpinCAD Designer
	public String getInstructionString(int mode) {
		String skipMode = "";
		if (mode == 1) {
			if ((flags | 0x10) == 0x10){
				skipMode = "RUN ";
			}
			if ((flags | 0x08) == 0x08){
				if(skipMode != "")
					skipMode = skipMode + " | ";
				skipMode = skipMode + "ZRC";
			}
			if ((flags | 0x04) == 0x04){
				if(skipMode != "")
					skipMode = skipMode + " | ";
				skipMode = skipMode + "ZRO";
			}
			if ((flags | 0x02) == 0x02){
				if(skipMode != "")
					skipMode = skipMode + " | ";
				skipMode = skipMode + "GEZ";
			}
			if ((flags | 0x01) == 0x01){
				if(skipMode != "")
					skipMode = skipMode + " | ";
				skipMode = skipMode + "NEG";
			}
			return "SKP " + skipMode + "," + nskip;
		}
		else
			return "Error! Invalid mode.";
	}

	// GSW in this section I changed all constants to be consistent
	// with Spin ASM, less confusing that way
	// GSW 02/01/2015, looks like Skip instruction only handles one flag at a time!
	// Suggested remedy will be:
	// match flags with current bit. if true, clear that bit and continue.
	
	@Override
	public void simulate(SimulatorState state) {
		boolean skip = false;
		if((flags & ElmProgram.RUN) > 0) {
			if(state.isFirstRun()) {
				skip = false;
			}
			else {
				skip = true;
			}
		}
		else if((flags & ElmProgram.ZRC) > 0) {
			int accv = state.getACCVal();
			int paccv = state.getPACCVal();
			
			if((state.getACCVal() < 0 && state.getPACCVal() >= 0) ||
					state.getACCVal() >= 0 && state.getPACCVal() < 0) {
				skip = true;
			}
			else {
				skip = false;
			}
		}
		else if((flags & ElmProgram.ZRO) > 0) {
			if(state.getACCVal() == 0) {
				skip = true;
			}
			else {
				skip = false;
			}
		}
		else if((flags & ElmProgram.GEZ) > 0) {
			if(state.getACCVal() > 0) {
				skip = true;
			}
			else {
				skip = false;
			}
		}
		else if((flags & ElmProgram.NEG) > 0) {
			if(state.getACCVal() < 0) {
				skip = true;
			}
			else {
				skip = false;
			}
		}
		if(skip) {
			state.skipInst(nskip);
		}
	}
}
