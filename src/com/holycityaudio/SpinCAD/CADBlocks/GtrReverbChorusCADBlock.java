/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * BitCrusherCADBlock.java
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

public class GtrReverbChorusCADBlock extends ReverbCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2552493950557066242L;
	int nBits = 3;

	public GtrReverbChorusCADBlock(int x, int y) {
		super(x, y);
		//		editPanel.add();
		setName("Guitar Rev/Cho");
	}

	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;
		sfxb.comment(getName());
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if(p != null) {
			input = p.getRegister();
			// development program: copy to target when done
			// 10/3/05
			// Guitar reverb/chorus
			// POT0 = reverb mix (mix from zero to 50% mix)
			// POT1 = chorus rate (scales width with frequency to approximate constant pitch bend)
			// POT2 = chorus mix/width (0 to 100% mix in first 25% of pot swing, control sweep width over entire range)
			// 
			int chordelLength = 2000;
			sfxb.FXallocDelayMem("chordel", chordelLength);
			// 
			int ap1Length = 150;
			sfxb.FXallocDelayMem("ap1", ap1Length);
			int ap2Length = 221;
			sfxb.FXallocDelayMem("ap2", ap2Length);
			int ap3Length = 345;
			sfxb.FXallocDelayMem("ap3", ap3Length);
			int ap4Length = 431;
			sfxb.FXallocDelayMem("ap4", ap4Length);
			int rap1Length = 1157;
			sfxb.FXallocDelayMem("rap1", rap1Length);
			int rap1bLength = 2257;
			sfxb.FXallocDelayMem("rap1b", rap1bLength);
			int rap2Length = 1978;
			sfxb.FXallocDelayMem("rap2", rap2Length);
			int rap2bLength = 1678;
			sfxb.FXallocDelayMem("rap2b", rap2bLength);
			int rap3Length = 1850;
			sfxb.FXallocDelayMem("rap3", rap3Length);
			int rap3bLength = 2456;
			sfxb.FXallocDelayMem("rap3b", rap3bLength);
			int rap4Length = 1234;
			sfxb.FXallocDelayMem("rap4", rap4Length);
			int rap4bLength = 1567;
			sfxb.FXallocDelayMem("rap4b", rap4bLength);
			int d1Length = 2180;
			sfxb.FXallocDelayMem("d1", d1Length);
			int d2Length = 3956;
			sfxb.FXallocDelayMem("d2", d2Length);
			int d3Length = 4165;
			sfxb.FXallocDelayMem("d3", d3Length);
			int d4Length = 3456;
			sfxb.FXallocDelayMem("d4", d4Length);
			// 
			int sigin = sfxb.allocateReg();
			int chorout = sfxb.allocateReg();
			int revin = sfxb.allocateReg();
			int width = sfxb.allocateReg();
			int width2 = sfxb.allocateReg();
			int effmix = sfxb.allocateReg();
			int temp = sfxb.allocateReg();
			int rfil = sfxb.allocateReg();
			int output = sfxb.allocateReg();
			// 

			// initialize lfos for reverb and chorus:

			sfxb.skip(RUN,  2);
			sfxb.loadSinLFO(0,  12,  160 );
			sfxb.loadSinLFO(1,  12,  1500 );
//			LOOP:

			// 
			// Pot0 will be used directly in reverb mix, but the other pots control multiple values and
			// require value modifications.
			// 
			// Pot1 will control both LFO1 rate and amplitude, POT2 will control mix and delay sweep width:
			// set up pot 2 to control mix and scale width:
			// 

			sfxb.readRegister(POT2,  1.9999 );
			sfxb.scaleOffset(1.9999,  0 );
			sfxb.scaleOffset(0.002,  0 );
			sfxb.readRegister(effmix,  0.998 );
			sfxb.writeRegister(effmix,  0 );
			sfxb.readRegister(POT2,  0.05 );
			sfxb.scaleOffset(0.002,  0 );
			sfxb.readRegister(width,  0.998 );
			sfxb.writeRegister(width,  0 );

			// 
			// set up pot 1 to control chorus sinwave rate and modify width control from POT2 :

			sfxb.readRegister(POT1,  1.0 );
			sfxb.mulx(POT1 );
			sfxb.mulx(POT1 );
			sfxb.scaleOffset(0.3,  0.01 );
			sfxb.writeRegister(RMP1_RATE, 0 );
			sfxb.readRegister(POT1,  1.0 );
			sfxb.scaleOffset(-0.50,  0.999 );
			sfxb.writeRegister(temp,  1.0 );
			sfxb.mulx(temp );
			sfxb.mulx(temp );
			sfxb.mulx(temp );
			sfxb.mulx(width );
			sfxb.scaleOffset(0.002,  0 );
			sfxb.readRegister(width2,  0.998 );
			sfxb.writeRegister(width2,  1.0 );
			sfxb.writeRegister(RMP1_RANGE, 0 );

			// do chorus:
			sfxb.readRegister(input,  1.0 );
//			sfxb.readRegister(adcr,  0.5);
			sfxb.writeRegister(sigin,  1.0 );
			sfxb.FXwriteDelay("chordel", 0, 0.0);	//write to delay input
			// cho RDA, sin1,0x06, chordel+1000 ;sweep about midpoint
			sfxb.FXchorusReadDelay(SIN1,0x06, "chordel", 1000);
			// cho RDA, sin1,0, chordel+1001 ;interpolate between ajacent samples
			sfxb.FXchorusReadDelay(SIN1,0, "chordel", 1001);
			sfxb.mulx(effmix );
			sfxb.readRegister(sigin,  1.0 );
			sfxb.writeRegister(chorout,  0 );
			// 
			// Now do reverb using chorout as an input signal
			// first, do ap smearing to loop aps. LFO0 is used to vary ap lengths.
			// four aps in reverb ring, use sin, cos, inv sin and inv cos as modulating values:
			// 
			// cho RDA, sin0, 0x06, rap1+50 
			// TODO regenerate from assembler
			sfxb.FXchorusReadDelay(SIN0, 0x06, "rap1", 50);
			// cho RDA, sin0, 0, rap1+51 
			sfxb.FXchorusReadDelay(SIN0, 0, "rap1", 51);
			sfxb.FXwriteDelay("rap1", 51, 0.0);
			// cho RDA, sin0, 0x07, rap2+50 
			sfxb.FXchorusReadDelay(SIN0, 0x07, "rap2", 50);
			// cho RDA, sin0, 1, rap2+51 
			sfxb.FXchorusReadDelay(SIN0, 1, "rap2", 51);
			sfxb.FXwriteDelay("rap2", 52, 0.0);
			// cho RDA, sin0, 0x0E, rap3+50 
			sfxb.FXchorusReadDelay(SIN0, 0x0E, "rap3", 50);
			// cho RDA, sin0, 0x08, rap3+51 
			sfxb.FXchorusReadDelay(SIN0, 0x08, "rap3", 51);
			sfxb.FXwriteDelay("rap3", 52, 0.0);
			// cho RDA, sin0, 0x0F, rap4+50 
			sfxb.FXchorusReadDelay(SIN0, 0x0F, "rap4", 50);
			// cho RDA, sin0, 0x09, rap4+51 
			sfxb.FXchorusReadDelay(SIN0, 0x09, "rap4", 51);
			sfxb.FXwriteDelay("rap4", 51, 0.0);
			// 
			sfxb.readRegister(chorout,  0.5 );
			sfxb.FXreadDelay("ap1", 1.0, -0.6);	//do 4 series allpass filters 
			sfxb.FXwriteAllpass("ap1", 0, 0.6);
			sfxb.FXreadDelay("ap2", 1.0, -0.6);
			sfxb.FXwriteAllpass("ap2", 0, 0.6);
			sfxb.FXreadDelay("ap3", 1.0, -0.6);
			sfxb.FXwriteAllpass("ap3", 0, 0.6);
			sfxb.FXreadDelay("ap4", 1.0, -0.6);
			sfxb.FXwriteAllpass("ap4", 0, 0.6);
			sfxb.writeRegister(revin,  0.0 );
			// 
			sfxb.FXreadDelay("d4", 1.0, 0.4);	//read last delay times RT setting coefficient
			sfxb.FXreadDelay("rap1", 1.0, -0.6);	//do a ring allpass
			sfxb.FXwriteAllpass("rap1", 0, 0.6);
			sfxb.FXreadDelay("rap1b", 1.0, -0.7);	//do second reverb ring allpass
			sfxb.FXwriteAllpass("rap1b", 0, 0.7);
			sfxb.readRegister(revin,  0.5 );
			sfxb.FXwriteDelay("d1", 0, 0.0);	//write output to next delay
			// 
			sfxb.FXreadDelay("d1", 1.0, 0.4);	//do as above 3 more times
			sfxb.FXreadDelay("rap2", 1.0, -0.6);
			sfxb.FXwriteAllpass("rap2", 0, 0.6);
			sfxb.FXreadDelay("rap2b", 1.0, -0.7);
			sfxb.FXwriteAllpass("rap2b", 0, 0.7);
			sfxb.readRegister(revin,  0.5);
			sfxb.FXwriteDelay("d2", 0, 0.0);
			// 
			sfxb.FXreadDelay("d2", 1.0, 0.4);
			sfxb.FXreadDelay("rap3", 1.0, -0.6);
			sfxb.FXwriteAllpass("rap3", 0, 0.6);
			sfxb.FXreadDelay("rap3b", 1.0, -0.7);
			sfxb.FXwriteAllpass("rap3b", 0, 0.7);
			sfxb.readRegister(revin,  0.5);
			sfxb.FXwriteDelay("d3", 0, 0.0);
			// 
			sfxb.FXreadDelay("d3", 1.0, 0.4);
			sfxb.FXreadDelay("rap4", 1.0, -0.6);
			sfxb.FXwriteAllpass("rap4", 0, 0.6);
			sfxb.FXreadDelay("rap4b", 1.0, -0.7);
			sfxb.FXwriteAllpass("rap4b", 0, 0.7);
			sfxb.readRegister(revin,  0.7);
			sfxb.FXwriteDelay("d4", 0, 0.0);
			// 
			sfxb.FXreadDelay("d1", 0.3761467889908257, 1.0);	//sum outpts from taps on the 4 reverb ring delays
			sfxb.FXreadDelay("d2", 0.3131951466127401, 0.8);
			sfxb.FXreadDelay("d3", 0.5620648259303721, 0.7);
			sfxb.FXreadDelay("d4", 0.08738425925925926, 0.6);
			sfxb.readRegisterFilter(rfil,  0.5 );
			// 
			sfxb.mulx(POT0 );
			sfxb.mulx(POT0 );
			sfxb.readRegister(chorout,  0.5 );
			sfxb.scaleOffset(1.999,  0 );
			sfxb.writeRegister(output,  0 );
			
			this.getPin("Audio Output 1").setRegister(output);
			System.out.println("Reverb/chorus code gen!");	
		}
	}
	
	public void editBlock(){
//		new BitCrusherControlPanel(this);
	}
}
