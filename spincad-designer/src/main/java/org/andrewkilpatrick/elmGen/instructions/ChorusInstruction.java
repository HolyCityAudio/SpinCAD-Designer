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

/**
 * This class represents the parent class of the CHO SOF and CHO RDA instructions.
 * Because they share a lot of code!  Added by GSW
 * @author andrew
 * 
 */
public class ChorusInstruction extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7762816709250158092L;
	int lfo;
	int flags;
	int addr;
	int lfoval;
	int lfoPos;
	boolean cos;
	boolean compc;
	boolean compa;
	boolean rptr2 = false;
	boolean na = false;

	int xfade;
	Reg tempReg;

	// GSW added for integration with SpinCAD Designer

	String readMode = "";

	/**
	 * Scale offset based on LFO position.
	 * 
	 * @param lfo the LFO to use (0 = SIN0, 1 = SIN1, 2 = RMP0, 3 = RMP1)
	 * @param flags the flags OR'd together
	 */
	public void SetFlags(int lfo, int flags) {
		if(lfo < 0 || lfo > 3) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 0, 1, 2 or 3 (SIN0, SIN1, RMP0 or RMP1)");
		}
		this.lfo = lfo;
		this.flags = (flags & 0x3f);	
		// GSW changed the names of LFO constants to be consistent with
		// Spin ASM, less confusing that way
		if((flags & ElmProgram.COS) != 0) {
			cos = true;
			if(lfo == 2 || lfo == 2) {
				throw new IllegalArgumentException("cos cannot be used for SIN LFOs");
			}
		}
		if((flags & ElmProgram.COMPC) != 0) {
			compc = true;
		}
		if((flags & ElmProgram.NA) != 0) {
			na = true;
		}
		if((flags & ElmProgram.COMPA) != 0) {
			compa = true;
		}
		if((flags & ElmProgram.RPTR2) != 0) {
			rptr2 = true;
		}

		// GSW added for integration with SpinCAD Designer
		readMode = new ChorusModeFlags().readMode(flags);
	}

	public void lfoPrepare(SimulatorState state) {
		// SIN LFOs
		if(lfo == 0 || lfo == 1) {
			if(cos) {
				lfoval = state.getSinLFOVal(2 + lfo);
			}
			else {
				lfoval = state.getSinLFOVal(lfo);
			}
			// GSW attempting to debug SIN LFO
			lfoPos = lfoval >> 9;
		}
		// RAMP LFOs
		else if(lfo == 2 || lfo == 3) {
			// do the ramp pointer magic
			if(rptr2) {
				lfoval = state.getRampLFORptr2Val(lfo - 2);
			}
			else {
				lfoval = state.getRampLFOVal(lfo - 2);
			}
			// GSW attempting to debug Ramp LFO
			lfoPos = lfoval >> 10;
		}

		// invert the waveform for COMPA
		if(compa) { 
			// for SIN LFOs, just flip the wave over
			if(lfo == 0 || lfo == 1) {
				lfoPos = -lfoPos; 
			}
			// for RAMP LFOs, i think we need to do maxval - offset
			else {
				lfoPos = state.getRampLFOAmp(lfo - 2) - lfoPos;
			}
		}
	}

	@Override
	public int getHexWord() {
		return 0;
	}

	@Override
	public String getInstructionString() {
		return null;
	}

	@Override
	public void simulate(SimulatorState state) {
	}
}
