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

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SimulatorState;
import org.andrewkilpatrick.elmGen.util.Util;


/**
 * This class represents the CHO SOF instruction.
 * 
 * @author andrew
 */
public class ChorusScaleOffset extends Instruction {
	final int lfo;
	final int flags;
	final double offset;
	private boolean cos;
	private boolean compc;
	private boolean compa;
	String readMode = "";
	
	/**
	 * Scale offset based on LFO position.
	 * 
	 * @param lfo the LFO to use (0 = SIN0, 1 = SIN1, 2 = RMP0, 3 = RMP1)
	 * @param flags the flags OR'd together
	 */
	public ChorusScaleOffset(int lfo, int flags, double offset) {
		if(lfo < 0 || lfo > 3) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 0, 1, 2 or 3 (SIN0, SIN1, RMP0 or RMP1)");
		}
		this.lfo = lfo;
		this.flags = (flags & 0x3f);	
		if((flags & ElmProgram.COS) != 0) {
			cos = true;
			if(lfo == 2 || lfo == 2) {
				throw new IllegalArgumentException("cos cannot be used for SIN LFOs");
			}
		}
		if((flags & ElmProgram.COMPC) != 0) {
			compc = true;
		}
		if((flags & ElmProgram.COMPA) != 0) {
			compa = true;
		}
		
		readMode = new ChorusModeFlags().readMode(flags);
		checkS15(offset);
		this.offset = offset;
	}
	
	@Override
	public int getHexWord() {
		int ret = 0x80000014;
		ret |= (flags << 24);
		ret |= (lfo & 0x03) << 21;
		ret |= (convS15(offset) & 0xffff) << 5;
		return ret;
	}

	@Override
	public String getInstructionString() {
		return "ChorusScaleOffset(" + lfo + "," + 
			String.format("%02X", flags) + "," + offset + ")";
	}
	
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "CHO SOF," + lfo + "," + 
					readMode + "," + offset;
		}
		else
			return "Error! Invalid mode.";
	}
	
	@Override
	public void simulate(SimulatorState state) {
		// XXX - finish/test ChorusScaleOffset simulation
		int lfoval = 0;
		// SIN LFOs
		if(lfo == 0 || lfo == 1) {
			if(cos) {
				lfoval = state.getSinLFOVal(2 + lfo);
			}
			else {
				lfoval = state.getSinLFOVal(lfo);
			}
		}
		// RAMP LFOs
		else if(lfo == 2 || lfo == 3) {
			lfoval = state.getRampLFOVal(lfo - 2);
		}

		int lfoPos = lfoval;
		
		// possibly invert the waveform - is this also where compc goes?
		if(compa || compc) {
			// for SIN LFOs, just flip the wave over
			if(lfo == 0 || lfo == 1) {
				lfoPos = -lfoPos;
			}
			// for RAMP LFOs, i think we need to do maxval - offset
			else {
				lfoPos = state.getRampLFOAmp(lfo - 2) - lfoPos;
			}
		}

		double scale = Util.regToDouble(lfoPos);
		state.getACC().scale(scale);
		state.getACC().add(offset);		
	}
}
