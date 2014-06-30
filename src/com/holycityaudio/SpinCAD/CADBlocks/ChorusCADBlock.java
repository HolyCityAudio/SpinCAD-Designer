/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChorusCADBLock.java
 * Copyright (C) 2013 - 2014 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code. 
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

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ChorusCADBlock extends ModulationCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 695539935034103396L;

	public ChorusCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this,"LFO Speed");
		addControlInputPin(this,"LFO Width");
		setName("Chorus");
	}

	private void doChorus(SpinFXBlock sfxb, int chorusLength) {
		// ;development program: copy to target when done
		// ;10/3/05
		// ;Guitar reverb/chorus
		// ;pot0 = reverb mix (mix from zero to 50% mix)
		// ;pot1 = chorus rate (scales width with frequency to approximate
		// constant pitch bend)
		// ;pot2 = chorus mix/width (0 to 100% mix in first 25% of pot swing,
		// control sweep width over entire range)
		// ;
		// mem chordel 2000
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();

			int Control1 = -1;

			p = this.getPin("LFO Speed").getPinConnection();
			if (p != null) {
				Control1 = p.getRegister();
			}
			int Control2 = -1;
			p = this.getPin("LFO Width").getPinConnection();
			if (p != null) {
				Control2 = p.getRegister();
			}

			sfxb.FXallocDelayMem("chordel", chorusLength);
			// ;
			// ;
			// equ sigin reg0 ;sum of inputs, mono
			// equ chorout reg1 ;output of chorus process, input to reverb
			// allpass filters
			int chorout = sfxb.allocateReg();
			// output_left = chorout;
			// equ width reg3 ;chorus width from pot2
			int width = sfxb.allocateReg();
			// equ width2 reg4 ;smoothed value of total chorus width
			int width2 = sfxb.allocateReg();
			// equ effmix reg5 ;value for mixing chorus from pot2
			int effmix = sfxb.allocateReg();
			// equ temp reg6 ;temp value for calculations
			int temp = sfxb.allocateReg();
			// ;initialize lfos for reverb and chorus:
			sfxb.comment("Chorus");

			// skp RUN, LOOP
			sfxb.skip(RUN, 1);
			// wlds 1, 12, 1500 ;lfo for chorus delay modulation (will be
			// modified in loop)
			sfxb.loadSinLFO(1, 12, (chorusLength / 2) - 1);

			// ;Pot1 will control both LFO1 rate and amplitude, pot2 will
			// control mix and delay sweep width:
			// ;set up pot 2 to control mix and scale width:
			// ;

			sfxb.clear();
			// rdax pot2, 1.9999 ;read pot2 times approx. 2.0
			if (Control2 != -1) {
				sfxb.readRegister(Control2, 1.999);
			} else
				sfxb.scaleOffset(0, 0.99); // just plug in some dummy value
			// rdax pot2, 0.05 ;set maximum width value (sweeps most of delay
			// maximum)
			
			if (Control1 != -1) {
				sfxb.readRegister(Control1, 0.05);
			} else
				sfxb.scaleOffset(0, 0.05); // just plug in some dummy value

			sfxb.writeRegister(SIN1_RATE, 0);

			// pot 1 function
			// rdax pot1, 1.0 ;generate function of pot1 that decreases width as
			// LFO frequency increases
			if (Control2 != -1) {
				sfxb.readRegister(Control2, 1.0);
			} else
				sfxb.scaleOffset(0, 0.5); // just plug in some dummy value

			// wrax rmp1_range,0 ;control lfo width with result, clear acc
			sfxb.writeRegister(SIN1_RANGE, 0);

			// ; ========== audio block =============
			// ; do chorus:
			sfxb.readRegister(input, 1.0);
			// wra chordel, 0 ;write to delay input
			sfxb.FXwriteDelay("chordel", 0, 0.0);
			// cho RDA, sin1,0x06, chordel+1000 ;sweep about midpoint
			sfxb.FXchorusReadDelay(SIN1, 0x06, "chordel+",
					(chorusLength / 2));
			// cho RDA, sin1,0, chordel+1001 ;interpolate between adjacent
			// samples
			sfxb.FXchorusReadDelay(SIN1, 0, "chordel+",
					1 + (chorusLength / 2));
			// mulx effmix ;multiply chorus delay output by effmix
			sfxb.mulx(effmix);
			// rdax sigin, 1.0 ;add to sigin
			sfxb.readRegister(input, 1.0);
			// wrax chorout, 0 ;write chorus out, clear accumulator
			sfxb.writeRegister(chorout, 1.0);
			// rdax chorout, 0.5 ;add chorus output * 0.5
			sfxb.readRegister(chorout, 0.5);
			this.getPin("Audio Output 1").setRegister(chorout);
			System.out.println("Chorus code gen!");
		}
	}

	public void generateCode(SpinFXBlock sfxb) {
		/**
		 * @param sfxb
		 *            is the handle of the calling program
		 * @param input
		 *            is the input register from a previously defined block
		 */
		doChorus(sfxb, 2000);
	}
}
