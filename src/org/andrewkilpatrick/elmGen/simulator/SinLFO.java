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

public class SinLFO {
	final SimulatorState state;
	final int freqReg;
	final int ampReg;
	long sin = 0;  // initial value
	long cos = -0x7fff00l;  // peak amplitude	
	int amp;
	int unit;
	
	public SinLFO(SimulatorState state, int unit) {
		this.state = state;
		this.unit = unit;
		if(unit == 0) {
			freqReg = ElmProgram.SIN0_RATE;
			ampReg = ElmProgram.SIN0_RANGE;
		}
		else if(unit == 1) {
			freqReg = ElmProgram.SIN1_RATE;
			ampReg = ElmProgram.SIN1_RANGE;			
		}
		else {
			throw new IllegalArgumentException("bad unit: " + unit);
		}
	}

	public void increment() {
		int coeff = state.getRegVal(freqReg) >> 14;
		amp = state.getRegVal(ampReg) >> 8;
		// calculate next value
		// GSW added saturation logic here to avoid glitches at peak.
		long acc = sin;			
		acc = (acc * coeff) >> 17;
		if(acc + cos > 0x7fffff) {
			acc = 0x7ffffff;
		}
		else if(acc + cos < -0x7fffff) {
			acc = -0x7fffff;
		}
		else
			acc = acc + cos;
		cos = acc;
		acc = -acc;
		acc = (acc * coeff) >> 17;
		if(acc + sin > 0x7fffff) {
			acc = 0x7fffff;
		}
		else if(acc + sin < -0x7fffff) {
			acc = -0x7fffff;
		}
		else 
			acc = acc + sin;
		sin = acc;
	}

	public void jam() {
		sin = 0;  // initial value
		cos = -0x7fff00l;  // peak amplitude	
	}

	public int getSinValue() {
		long val = (sin * amp) >> 15;
		if(val < 0) {
			return -(int)(0x7fffff - (val & 0x7fffff));
		}
		return (int)(val & 0x7fffff);
	}
	
	public int getCosValue() {
		long val = (cos * amp) >> 15;
		if(val < 0) {
//			return -(int)(val & 0x7fffff);
			return -(int)(0x7fffff - (val & 0x7fffff));
		}
		return (int)(val & 0x7fffff);
	}
// GSW added this for debugging of Sin LFO - not yet successful!	
	public int getAmp() {
		return amp;
	}

}
