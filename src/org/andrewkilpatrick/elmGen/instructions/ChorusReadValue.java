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

/**
 * This class represents the CHO RDAL instruction.
 * 
 * @author andrew
 */
public class ChorusReadValue extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6799053097694006359L;
	final int lfo;
	
	/**
	 * Scale offset based on LFO position.
	 * 
	 * @param lfo the LFO to use (0 = SIN0, 1 = SIN1, 2 = RMP0, 3 = RMP1, 8 = COS0, 9 = COS1)
	 */
	// GSW fixed some issues with the COS LFO values
	public ChorusReadValue(int lfo) {
		if(lfo < 0 || lfo > 9 || lfo == 4 || lfo == 5 ) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 0 = SIN0, 1 = SIN1, 2 = RMP0, 3 = RMP1, 8 = COS0, 9 = COS1)");
		}
		this.lfo = lfo;
	}
	
	@Override
	public int getHexWord() {
		int ret = 0xc2000014;
		ret |= (lfo & 0x03) << 21;
		ret |= (lfo & 0x04) << 22;
		return ret;
	}

	@Override
	public String getInstructionString() {
		return "ChorusReadValue(" + lfo + ")";
	}
	// GSW added for integration with SpinCAD Designer
	public String getInstructionString(int mode) {
		if (mode ==1) {
			return "CHO RDAL," + lfo;			
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		if(lfo == 0) { // sin 0
			state.setACCVal(state.getSinLFOVal(0));
		}
		else if(lfo == 1) {  // sin 1
			state.setACCVal(state.getSinLFOVal(1));
		}
		else if(lfo == 2) {  // ramp 0
			state.setACCVal(state.getRampLFOVal(0));
		}
		else if(lfo == 3) {  // ramp 1
			state.setACCVal(state.getRampLFOVal(1));
		}
		// GSW needed to change values for COS0 and COS1, it was a bug
		else if(lfo == 8) {  // cos 0
			state.setACCVal(state.getSinLFOVal(2));
		}
		else if(lfo == 9) {  // cos 1
			state.setACCVal(state.getSinLFOVal(3));
		}
	}
}
