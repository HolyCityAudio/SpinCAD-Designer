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
 * This class represents the WLDR instruction.
 * 
 * @author andrew
 */
public class LoadRampLFO extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8115761130650996548L;
	final int lfo;
	final int freq;
	final int amp;
	// GSW added for integration with SpinCAD Designer
	final int spinASMAmp;
	
	/**
	 * Loads a RAMP LFO frequency and amplitude values.
	 * 
	 * @param lfo the LFO to load (0 or 1)
	 * @param freq the frequency (-16384 - 32767)
	 */
	public LoadRampLFO(int lfo, int freq, int amp) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 0 or 1");
		}
		this.lfo = lfo;
		if(freq < -16384 || freq > 32767) {
			throw new IllegalArgumentException("frequency out of range: " + freq +
					" - valid range: -16384 to 32767");
		}
		this.freq = freq;
		if(amp != 512 && amp != 1024 && amp != 2048 && amp != 4096) {
			throw new IllegalArgumentException("amplitude invalid: " + amp +
					" - must be: 512, 1024, 2048 or 4096");
		}
	// GSW added for integration with SpinCAD Designer
		spinASMAmp = amp;
		if(amp == 1024) {
			this.amp = 0x02;
		}
		else if(amp == 2048) {
			this.amp = 0x01;
		}
		else if(amp == 4096) {
			this.amp = 0x00;
		}
		else { // 512
			this.amp = 0x03;
		}
	}
	
	@Override
	public int getHexWord() {
		return ((0x01 & 0xff) << 30) |  ((lfo & 0x01) << 29) |
			((freq & 0xffff) << 13) | ((amp & 0x03) << 5) | 0x012;
	}

	@Override
	public String getInstructionString() {
		return "LoadRampLFO(" + lfo + "," + freq + "," + amp + ")";
	}
	// GSW added for integration with SpinCAD Designer
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "WLDR " + lfo + ", " + freq + ", " + spinASMAmp;
		}
		else
			return "Error! Invalid mode.";
	}
	
	@Override
	public void simulate(SimulatorState state) {
		int regFreq = -1;
		regFreq = (freq & 0x7fff) << 8;
		// XXX debug GSW
		if(freq < 0) {
			regFreq |= 0xFF80_0000l;
		}
		if(lfo == 1) {
			state.setRegVal(ElmProgram.RMP1_RATE, regFreq);
			state.setRegVal(ElmProgram.RMP1_RANGE, amp);
			state.jamRampLFO(1);
		}
		else {
			state.setRegVal(ElmProgram.RMP0_RATE, regFreq);
			state.setRegVal(ElmProgram.RMP0_RANGE, amp);			
			state.jamRampLFO(0);
		}
	}

}
