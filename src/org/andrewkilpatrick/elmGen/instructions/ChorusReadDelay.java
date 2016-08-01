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

import org.andrewkilpatrick.elmGen.simulator.Reg;
import org.andrewkilpatrick.elmGen.simulator.SimulatorState;


/**
 * This class represents the CHO RDA instruction.
 * 
 * @author andrew
 */
public class ChorusReadDelay extends ChorusInstruction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4557383048214102025L;

	/**
	 * Read delay RAM based on chorus position.
	 * 
	 * @param lfo the LFO to use (0 = SIN0, 1 = SIN1, 2 = RMP0, 3 = RMP1)
	 * @param flags the flags OR'd together
	 * @param addr the address in delay memory (0-32767)
	 */
	public ChorusReadDelay(int lfo, int flags, int addr) {
		SetFlags(lfo, flags);
		// GSW added for integration with SpinCAD Designer
		// there was also an issue here somewhere with LFO value

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
	// GSW added for integration with SpinCAD Designer
	public String getInstructionString(int mode) {
		if(mode == 1) {
			return "CHO RDA," + lfo + "," + readMode + "," + addr;			
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		lfoPrepare(state);

		// do crossfading only = GSW this is oversimplified but might work for simulation
		if(na) {
			int fadeVal = state.getRampXfadeVal(lfo - 2);
			if(compc) {
				fadeVal = 16384 - fadeVal;
			}
			if(fadeVal != 0) {
				@SuppressWarnings("unused")
				int iopl = 346;
			}
			// do the crossfade
			int delayPos = addr;

			tempReg.setValue(state.getDelayVal(delayPos));
			int value = tempReg.getValue();
			tempReg.mult(fadeVal);
			value = tempReg.getValue();
			if(value != 0) {
				@SuppressWarnings("unused")
				int iopl = 345;
			}
			state.getACC().add(value);
		}
		// do delay offset lookup
		else {
			int delayPos = addr + lfoPos;
			int inter = -1;
			if(lfo == 0 || lfo == 1) {
				inter = lfoval & 0xff;
				// get the delay memory value and scale it by the interpolation amount
				if(compc) {
					tempReg.setValue(state.getDelayVal(delayPos));
					tempReg.mult((255 - inter) << 6);
					state.getACC().add(tempReg.getValue());
				}
				else {
					tempReg.setValue(state.getDelayVal(delayPos));
					tempReg.mult(inter << 6);
					state.getACC().add(tempReg.getValue());
				}	
			}
			// for RAMP LFOs, use bottom 14 (fractional) bits for inter-sample interpolation
			else {
				inter = (lfoval & 0x3fff);
				// get the delay memory value and scale it by the interpolation amount
				if(compc) {
					tempReg.setValue(state.getDelayVal(delayPos));
					tempReg.mult((16383 - inter));
					state.getACC().add(tempReg.getValue());
				}
				else {
					tempReg.setValue(state.getDelayVal(delayPos));
					tempReg.mult(inter);
					state.getACC().add(tempReg.getValue());
				}			
			}
		}
	}
}
