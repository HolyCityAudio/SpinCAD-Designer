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
 * This class represents the CHO SOF instruction.
 * 
 * @author andrew
 */
public class ChorusScaleOffset extends ChorusInstruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6563114802960302412L;
	final double offset;
	
	/**
	 * Scale offset based on LFO position.
	 * 
	 * @param lfo the LFO to use (0 = SIN0, 1 = SIN1, 2 = RMP0, 3 = RMP1)
	 * @param flags the flags OR'd together
	 */
	public ChorusScaleOffset(int lfo, int flags, double offset) {
		SetFlags(lfo, flags);

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
	// GSW added for integration with SpinCAD Designer
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
		int fadeVal = 0;
		lfoPrepare(state);
		if(na) {
			fadeVal = (int) state.getRampXfadeVal(lfo - 2);
			if(compc) {
				fadeVal = 16384 - fadeVal;
			}
		}

		state.getACC().mult(fadeVal);
		state.getACC().add(offset);		
	}
}
