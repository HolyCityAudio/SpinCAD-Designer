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
 * This class represents the WLDS instruction.
 * 
 * @author andrew
 */
public class LoadSinLFO extends Instruction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4087410593486074646L;
	final int lfo;
	final int freq;
	final int amp;
	
	/**
	 * Loads a SIN LFO frequency and amplitude values.
	 * 
	 * @param lfo the LFO to load (0 or 1)
	 * @param freq the frequency in Hz (0.0 to 20.33)
	 * @param amp the amplitude (0.0 to 1.0)
	 */
	public LoadSinLFO(int lfo, double freq, double amp) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 0 or 1");
		}
		this.lfo = lfo;
		this.freq = (int)(Math.pow(2.0, 17.0) * ((2.0 * Math.PI * freq) / 
				(double)ElmProgram.SAMPLERATE));
		if(this.freq > 511 || this.freq < 0) {
			throw new IllegalArgumentException("frequency out of range: " + freq +
					" - valid range: 0 - 20.33");
		}
		if(amp < 0 || amp > 1.0) {
			throw new IllegalArgumentException("amplitude out of range: " + amp);
		}
		this.amp = (int)(32768.0 * amp);
	}
	
	/**
	 * Loads a SIN LFO frequency and amplitude values.
	 * 
	 * @param lfo the LFO to load (0 or 1)
	 * @param freq the frequency in Hz (0 to 511)
	 * @param amp the amplitude (0 to 32767)
	 */
	public LoadSinLFO(int lfo, int freq, int amp) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("lfo out of range: " + lfo +
					" - valid values: 0 or 1");
		}
		this.lfo = lfo;
		if(freq > 511 || freq < 0) {
			throw new IllegalArgumentException("frequency out of range: " + freq +
					" - valid range: 0 - 511");
		}
		this.freq = freq;
		if(amp < 0 || amp > 32767) {
			throw new IllegalArgumentException("amplitude out of range: " + amp);
		}
		this.amp = amp;
	}
	
	@Override
	public int getHexWord() {
		return ((lfo & 0x01) << 29) | ((freq & 0x1ff) << 20) | 
			((amp & 0x7fff) << 5) | 0x12;
	}

	@Override
	public String getInstructionString() {
		return "LoadSinLFO(" + lfo + "," + freq + "," + amp + ")"; 
	}
	// GSW added for integration with SpinCAD Designer	
	public String getInstructionString(int mode) {
		if (mode == 1) {
			return "WLDS " + lfo + "," + freq + "," + amp; 
		}
		else
			return "Error! Invalid mode.";
	}

	@Override
	public void simulate(SimulatorState state) {
		if(lfo == 1) {
			state.setRegVal(ElmProgram.SIN1_RATE, (freq & 0x1ff) << 14);
			state.setRegVal(ElmProgram.SIN1_RANGE, (amp & 0x7fff) << 8);
			state.jamSinLFO(1);			
		}
		else {
			state.setRegVal(ElmProgram.SIN0_RATE, (freq & 0x1ff) << 14);
			state.setRegVal(ElmProgram.SIN0_RANGE, (amp & 0x7fff) << 8);
			state.jamSinLFO(0);	
		}
	}
}
