/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChorusCADBLock.java
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

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ChorusPresetCADBlock extends ModulationCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 695539935034103396L;

	public ChorusPresetCADBlock(int x, int y) {
		super(x, y);
		// editPanel.add();
		addControlInputPin(this);
		addControlInputPin(this);
		setName("Preset Chorus");
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

			p = this.getPin("Control Input 1").getPinConnection();
			if (p != null) {
				Control1 = p.getRegister();
			}
			int Control2 = -1;
			p = this.getPin("Control Input 2").getPinConnection();
			if (p != null) {
				Control2 = p.getRegister();
			}

			sfxb.comment("Preset chorus");

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
				sfxb.scaleOffset(0, 0.9990); // just plug in some dummy value
			// sof 1.9999, 0 ;pot2 * approx. 4, saturation limits to approx. 1
			sfxb.scaleOffset(1.9999, 0);
			// sof 0.002, 0 ;scale limited value to 0.002 max as input to
			// smoothing filter
			sfxb.scaleOffset(0.002, 0);
			// rdax effmix, 0.998 ;effmix becomes smoothing register,
			// feedback=(1-input drive)
			sfxb.readRegister(effmix, 0.998);

			// wrax effmix, 0 ;effmix ranges 0 to 0.9999 during the first 1/4
			// turn of pot2
			sfxb.writeRegister(effmix, 0);

			// rdax pot2, 0.05 ;set maximum width value (sweeps most of delay
			// maximum)
			if (Control2 != -1) {
				sfxb.readRegister(Control2, 0.05);
			} else
				sfxb.scaleOffset(0, 0.05); // just plug in some dummy value

			// sof 0.002, 0 ;scale value as input to smoothing filter
			sfxb.scaleOffset(0.002, 0);
			// rdax width, 0.998 ;read smoothing filter times (1-input drive)
			sfxb.readRegister(width, 0.998);
			// wrax width, 0 ;store temporary width value (from pot 2), clear
			// accumulator
			sfxb.writeRegister(width, 0);

			// ;set up pot 1 to control chorus sinwave rate and modify width
			// control from pot2 :
			// rdax pot1, 1.0 ;read pot 1
			if (Control1 != -1) {
				sfxb.readRegister(Control1, 1.0);
				sfxb.mulx(Control1);
				// mulx pot1 ;cubed
				sfxb.mulx(Control1);
			} else
				sfxb.scaleOffset(0, 0.9); // just plug in some dummy value
			// mulx pot1 ;square value (acc=0 to 1.0)
			// sof 0.3, 0.01 ;set range of LFO1 frequency from 0.01 to 0.31
			sfxb.scaleOffset(0.30, 0.01);
			// wrax rmp1_rate,0 ;write value to set LFO1 rate, clear accumulator
			sfxb.writeRegister(SIN1_RATE, 0);

			// pot 1 function
			// rdax pot1, 1.0 ;generate function of pot1 that decreases width as
			// LFO frequency increases
			if (Control1 != -1) {
				sfxb.readRegister(Control1, 1.0);
				// sof -0.50, 0.999 ;multiply by -0.6, add 1. This makes output
				// function go roughly 1 to 0.5
				sfxb.scaleOffset(-0.50, 0.999);
			} else
				sfxb.scaleOffset(0.0, 0.75);
				
			// wrax temp, 1.0 ;write to temp and keep in accumulator
			sfxb.writeRegister(temp, 1.0);
			// mulx temp ;function squared, now ranges 1 to 0.25
			sfxb.mulx(temp);
			// mulx temp ;pot function cubed, now ranges 1 to 0.125
			sfxb.mulx(temp);
			// mulx temp ;pot function ^4, now ranges 1 to 0.0625
			sfxb.mulx(temp);
			// mulx width ;multiply by previous width value
			sfxb.mulx(width);
			// sof 0.002, 0 ;scale to input of smoothing filter
			sfxb.scaleOffset(0.002, 0);
			// rdax width2, 0.998 ;do filter
			sfxb.readRegister(width2, 0.998);
			// wrax width2, 1.0 ;store filter value, keep in acc
			sfxb.writeRegister(width2, 1.0);
			// wrax rmp1_range,0 ;control lfo width with result, clear acc
			sfxb.writeRegister(SIN1_RANGE, 0);

			// ; ========== audio block =============
			// ; do chorus:
			sfxb.readRegister(input, 1.0);
			// wra chordel, 0 ;write to delay input
			sfxb.FXwriteDelay("chordel", 0, 0.0);
			// cho RDA, sin1,0x06, chordel+1000 ;sweep about midpoint
			sfxb.FXchorusReadDelay(SIN1, 0x06, "chordel",
					chorusLength / 2);
			// cho RDA, sin1,0, chordel+1001 ;interpolate between adjacent
			// samples
			sfxb.FXchorusReadDelay(SIN1, 0, "chordel",
					1 + (chorusLength / 2));
			// mulx effmix ;multiply chorus delay output by effmix
			sfxb.mulx(effmix);
			// rdax sigin, 1.0 ;add to sigin
			sfxb.readRegister(input, 1.0);
			// wrax chorout, 0 ;write chorus out, clear accumulator
			sfxb.writeRegister(chorout, 1.0);
			// rdax chorout, 0.5 ;add chorus output * 0.5
			sfxb.readRegister(chorout, 0.5);
			// sof 1.999, 0 ;scale back up (lower internal levels keep from
			// clipping)
			sfxb.scaleOffset(0.002, 0);

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
