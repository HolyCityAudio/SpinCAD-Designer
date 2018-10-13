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
package org.andrewkilpatrick.elmGen.simulator;

import org.andrewkilpatrick.elmGen.ElmProgram;

// GSW I would like to revisit resolution and rounding and clipping and 
// number of bits used where

public class RampLFO {
	public static final int AMP_4096 = 0x3ffffff;
	public static final int AMP_2048 = 0x1ffffff;
	public static final int AMP_1024 = 0x0ffffff;
	public static final int AMP_512 =  0x07fffff;

	final SimulatorState state;
	final int unit;
	final int freqReg;
	final int ampReg;

	int pos = 0;
	int amp = 0;
	long xfade = 0;
	int xFadeScale = 1;
	long xFadeMax = 0;
	long xFadeMin = 16384;

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
			throw new IllegalArgumentException("Ramp LFO: bad unit: " + unit);
		}
	}

	public void increment() {
		int sign = 1;
		int freq = state.getRegVal(freqReg);
		freq = freq >> 8;

		if((freq & 0x80000) != 0) {
			sign = -1;
			freq = ~((-1 ^ 0x7FFFF) | freq) + 1;
		}

		int regAmp = state.getRegVal(ampReg);
		// default values
		amp = AMP_4096;
		xFadeScale = 16;

		if(regAmp == 0x03) {
			amp = AMP_512;
			xFadeScale = 128;
		}
		else if(regAmp == 0x02) {
			amp = AMP_1024;
			xFadeScale = 64;
		}
		else if(regAmp == 0x01) {
			amp = AMP_2048;
			xFadeScale = 32;
		}
		// taking freq at full resolution for pointer increment
		int increment = freq * sign; 
		pos = (pos - increment) & amp;

		// divide windows into eighths
		int eighthAmp = amp >> 3;

		if(sign == 1) {
			if(pos > eighthAmp * 7) {
				xfade = 0;
			}
			else if (pos > eighthAmp * 5) {
				xfade += increment;
			}
			else if (pos > eighthAmp * 3) {
				xfade = xfade * 1;
			}
			else if ((pos > eighthAmp * 1) && (xfade > 0)) {
				xfade -= increment;
			}
			else {
				xfade = 0;
			}
		}
		if(sign == -1) {
			if(pos > eighthAmp * 7) {
				xfade = 0;
			}
			else if (pos > eighthAmp * 5) {
				xfade += increment;
			}
			else if (pos > eighthAmp * 3) {
				xfade = xfade * 1;
			}
			else if ((pos > eighthAmp * 1)) {
				xfade -= increment;
			}
			else {
				xfade = 0;
			}
		}
	}

	public void jam() {
		pos = 0;  // this should be ok for both pos and neg frequencies
	}

	public int getValue() {
		// shift right 4 places before returning value
		return (pos >> 4);
	}

	public int getRptr2Value() {
		// shift right 4 places before returning value
		return ((pos + (amp >> 1)) & amp) >> 4;
	}

	public int getXfade() {
		return (int) (xfade * xFadeScale)/16384;
	}

	public int getAmp() {
		return (amp >> 4);
	}
}
