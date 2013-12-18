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
package org.andrewkilpatrick.elmGen.simulator;

import org.andrewkilpatrick.elmGen.ElmProgram;

public class RampLFO {
	public static final int AMP_4096 = 0x3fffff;
	public static final int AMP_2048 = 0x1fffff;
	public static final int AMP_1024 = 0x0fffff;
	public static final int AMP_512 = 0x07ffff;

	final SimulatorState state;
	final int unit;
	final int freqReg;
	final int ampReg;
	int pos = 0;
	int amp = 0;

	public RampLFO(SimulatorState state, int unit) {
		this.state = state;
		this.unit = unit;
		if(unit == 0) {
			freqReg = ElmProgram.RMP0_RATE;
			ampReg = ElmProgram.RMP0_RANGE;
		}
		else if(unit == 1) {
			freqReg = ElmProgram.RMP1_RATE;
			ampReg = ElmProgram.RMP1_RANGE;			
		}
		else {
			throw new IllegalArgumentException("bad unit: " + unit);
		}
	}

	public void increment() {
		int sign = 1;
		if(unit == 0) {
//			System.out.printf("Ramp rate: %x\n", state.getRegVal(freqReg));
		}
		int freq = state.getRegVal(freqReg) >> 8;

		if((freq & 0x80) != 0) {
			sign = -1;
			freq = ~((-1 ^ 0x7F) | freq) + 1;
		}
		int regAmp = state.getRegVal(ampReg);
		amp = AMP_4096;
		if(regAmp == 0x03) {
			amp = AMP_512;
		}
		else if(regAmp == 0x02) {
			amp = AMP_1024;
		}
		else if(regAmp == 0x01) {
			amp = AMP_2048;
		}

		pos = (pos - (freq >> 4)) & amp;
//		if(unit == 1)
//			System.out.printf("%8x\n", pos);
	}

	public void jam() {
		pos = 0;  // this should be ok for both pos and neg frequencies
	}

	public int getValue() {
		return pos;
	}

	public int getRptr2Value() {
		return (pos + (amp >> 1)) & amp;
	}

	public int getAmp() {
		return amp;
	}
}
