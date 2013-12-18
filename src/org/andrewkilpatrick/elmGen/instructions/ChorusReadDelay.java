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
import org.andrewkilpatrick.elmGen.simulator.Reg;
import org.andrewkilpatrick.elmGen.simulator.SimulatorState;



/**
 * This class represents the CHO RDA instruction.
 * 
 * @author andrew
 */
public class ChorusReadDelay extends Instruction {
	final int lfo;
	final int flags;
	final int addr;
	Reg tempReg;
	// simulator stuff
	boolean cos = false;
	boolean compc = false;
	boolean compa = false;
	boolean rptr2 = false;
	boolean na = false;
	String readMode = "";


	/**
	 * Read delay RAM based on chorus position.
	 * 
	 * @param lfo the LFO to use (0 = SIN0, 1 = SIN1, 2 = RMP0, 3 = RMP1)
	 * @param flags the flags OR'd together
	 * @param addr the address in delay memory (0-32767)
	 */
	public ChorusReadDelay(int lfo, int flags, int addr) {
		if(lfo < 0 || lfo > 3) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 0, 1, 2 or 3 (SIN0, SIN1, RMP0 or RMP1)");
		}
		this.lfo = lfo;
		this.flags = (flags & 0x3f);
		if((flags & ElmProgram.COS) != 0) {
			cos = true;
			if(lfo == 2 || lfo == 3) {
				throw new IllegalArgumentException("cos cannot be used for RAMP LFOs");
			}
		}
		if ((flags & ElmProgram.NA) != 0){
			na = true;
			if(lfo == 0 || lfo == 1) {
				throw new IllegalArgumentException("na cannot be used for SIN LFOs");
			}
		}
		if ((flags & ElmProgram.COMPA) != 0){
			compa = true;
		}
		if ((flags & ElmProgram.COMPC) != 0){
			compc = true;
		}
		if ((flags & ElmProgram.RPTR2) != 0){
			rptr2 = true;
			if(lfo == 0 || lfo == 1) {
				throw new IllegalArgumentException("rptr2 cannot be used for SIN LFOs");
			}
		}
		readMode = new ChorusModeFlags().readMode(flags);
		
		if(addr < 0 || addr > 32767) {
			throw new IllegalArgumentException("addr out of range: " + addr +
					" - valid range: 0 - 32767");
		}
		this.addr = addr;
		tempReg = new Reg();
	}
	

	@Override
	public int getHexWord() {
		int ret = 0x14;
		ret |= (flags << 24);
		ret |= (lfo & 0x03) << 21;
		ret |= (addr & 0x7fff) << 5;
		return ret;
	}

	@Override
	public String getInstructionString() {
		return "ChorusReadDelay(" + lfo + "," + String.format("%02X", flags) + "," + addr + ")";
	}

	public String getInstructionString(int mode) {
		if(mode == 1) {
			return "CHO RDA," + lfo + "," + readMode + "," + addr;			
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
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
			// do the ramp pointer magic
			if(rptr2) {
				lfoval = state.getRampLFORptr2Val(lfo - 2);
			}
			else {
				lfoval = state.getRampLFOVal(lfo - 2);
			}
		}

		int lfoPos = lfoval >> 10;

		// TODO debug!!!! GSW
		//		if(lfo == 2 && !rptr2)
		//			System.out.println("LFOPos " + lfo + " = " + lfoPos);

		// possibly invert the waveform
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

		// do crossfading only
		if(na) {
			int amp = state.getRampLFOAmp(lfo - 2);
			int halfAmp = amp >> 1;
		int xfade = lfoPos;
		// if we're beyond the middle, fade down
		if(lfoPos > halfAmp) {
			xfade = halfAmp - xfade;
		}
		// fix the xfade amplitude based on the ramp maxval
		if(amp == 0x3fffff) {
			xfade = xfade << 1;
		}
		else if(amp == 0x1fffff) {
			xfade = xfade << 2;
		}
		// TODO hmmm this doesn't look quite right
		else if(amp == 0x1fffff) {
			xfade = xfade << 3;
		}
		else {
			xfade = xfade << 4;
		}
		// do the crossfade
		tempReg.setValue(addr);
		tempReg.mult(xfade);
		state.getACC().add(tempReg.getValue());
		}
		// do delay offset lookup
		else {
			int delayPos = addr + lfoPos;
			int inter = lfoval & 0xff;
//TODO debug GSW
//			System.out.printf("lfoPos: %x delayPos: %x inter: %x\n", lfoPos, delayPos, inter);
			// get the delay memory value and scale it by the interpolation amount
			if(compc) {
				tempReg.setValue(state.getDelayVal(delayPos));
				tempReg.mult((255 - inter) << 7);
				state.getACC().add(tempReg.getValue());
			}
			else {
				tempReg.setValue(state.getDelayVal(delayPos));
				tempReg.mult(inter << 7);
				state.getACC().add(tempReg.getValue());
			}			
		}
	}
}
