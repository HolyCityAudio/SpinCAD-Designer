/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Copyright (C)2013 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
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

package com.holycityaudio.SpinCAD.ControlBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class TapTempoCADBlock extends ControlCADBlock{

	/**
	 * @author slacker
	 */
	private static final long serialVersionUID = 4676526418848384621L;

	public TapTempoCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);
		addControlOutputPin(this, "Latch");
		addControlOutputPin(this, "Ramp");
		addControlOutputPin(this, "Tap Tempo");
		setName("Tap-Tempo");
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int latch = -1;
		int ramp = -1;
		int taptempo = -1;

		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		if (p != null) {
			// A pot is used as a tap tempo switch input. This should be a momentary switch, transition can be high to low or low to high.
			// see guitar amp application note for examples of switch hookup. 
			int ttpot = p.getRegister();

			// set up registers and equates
			int db = sfxb.allocateReg();
			int mom = sfxb.allocateReg();
			latch = sfxb.allocateReg();
			ramp = sfxb.allocateReg();
			taptempo = sfxb.allocateReg();
			sfxb.comment(getName());

			double maxtime = 1;
			double deftime = 0.33;
			double ramprate = 1/maxtime/16;
			double count = 0.01;
			
			sfxb.skip(RUN,5);
			sfxb.loadRampLFO(0,0,4096);
			sfxb.scaleOffset(0,0.99);
			sfxb.writeRegister(latch,1);
			sfxb.scaleOffset(0,deftime/maxtime);
			sfxb.writeRegister(ramp,0);

//			START:

			// Switch Debouncing and pot filtering work around

			sfxb.loadAccumulator(ttpot);
			sfxb.scaleOffset(1,-0.5 );
			sfxb.skip(NEG,4 );
			sfxb.loadAccumulator(db);
			sfxb.scaleOffset(1,count );
			sfxb.writeRegister(db,0 );
			sfxb.skip(ZRO,3 );
//			DOWN:
			sfxb.loadAccumulator(db);
			sfxb.scaleOffset(1,-count );
			sfxb.writeRegister(db,0 );

//			ENDDB:

			// latching switch, falling edge triggered flipflop
			// Output of debounce routine of < -0.9 is low, > 0.9 is high, values in between
			// are ignored and the switch does nothing, Schmitt trigger action.

			sfxb.loadAccumulator(db);
			sfxb.absa();
			sfxb.scaleOffset(1,-0.9 );
			sfxb.skip(NEG, 13 );
			sfxb.loadAccumulator(db);
			sfxb.scaleOffset(1,-0.9 );
			sfxb.skip(NEG, 3);
			sfxb.scaleOffset(0,0.999 );
			sfxb.writeRegister(mom,0 );
			sfxb.skip(ZRO,7 );
//			LO:
			sfxb.loadAccumulator(mom);
			sfxb.skip(NEG, 5 );
			sfxb.scaleOffset(0,-0.999 );
			sfxb.writeRegister(mom,0 );
			sfxb.loadAccumulator(latch);
			sfxb.scaleOffset(-1,0 );
			sfxb.writeRegister(latch,0 );

//			ENDSWITCH:

			// tap tempo, uses rmp0 as a 1 Hz rising ramp, runs whilst latch is low and is sampled and held when latch is high

			sfxb.loadAccumulator(latch);
			sfxb.skip(NEG,4);
			sfxb.jam(RMP0);
			sfxb.loadAccumulator(ramp);
			sfxb.writeRegister(taptempo,0 );
			sfxb.skip(ZRO, 12);
			//LOW:
			sfxb.scaleOffset(0,ramprate );
			sfxb.writeRegister(RMP0_RATE,0 );
			sfxb.chorusReadValue(RMP0);
			sfxb.scaleOffset(-2,0.999);
			sfxb.scaleOffset(1,0.001 );
			sfxb.writeRegister(ramp,1 );
			sfxb.scaleOffset(1,-0.999 );
			sfxb.skip(NEG,4 );
			sfxb.loadAccumulator(taptempo);
			sfxb.writeRegister(ramp,0 );
			sfxb.scaleOffset(0,0.999 );
			sfxb.writeRegister(latch,0);
//			ENDTT:

			System.out.println("Tap Tempo code gen!");
		}
		this.getPin("Latch").setRegister(latch);
		this.getPin("Tap Tempo").setRegister(taptempo);
		this.getPin("Ramp").setRegister(ramp);
	}
}
