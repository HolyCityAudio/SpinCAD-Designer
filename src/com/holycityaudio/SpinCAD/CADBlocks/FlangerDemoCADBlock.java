/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * FlangerCADBlock.java
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

import com.holycityaudio.SpinCAD.SpinFXBlock;

public class FlangerDemoCADBlock extends ModulationCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8818516263438874366L;

	public FlangerDemoCADBlock(int x, int y) {
		super(x, y);
		setName("Flanger");
	}
	
	public void generateCode(SpinFXBlock sfxb) {
//		;GA_DEMO	Flanger

//		;Pot0 = Reverb level
//		;Pot1 = Flange rate
//		;pot2 = Effect level w/ feedback

//		;memory declarations:
//		mem	fladel	1000
		sfxb.FXallocDelayMem("flanger", 1000);

//		;register equates:

//		equ	mono	reg0
		int mono = sfxb.allocateReg();
//		equ	flaout	reg5
		int flaout = sfxb.allocateReg();
//		equ	fbk	reg6
		int fbk = sfxb.allocateReg();
//		equ	flamix	reg7
		int flamix = sfxb.allocateReg();
//		equ	tri	reg8
		int tri = sfxb.allocateReg();
//		equ	fhp	reg9
		int fhp = sfxb.allocateReg();

		sfxb.comment("Flanger");
		
		int input = this.getPin("Audio Input 1").getPinConnection().getRegister();
		int Control1 = this.getPin("Control Input 1").getPinConnection().getRegister();
		int Control2 = this.getPin("Control Input 2").getPinConnection().getRegister();

//		;declare constants:

//		equ	fbkmax	0.9	;maximum flange feedback
		double fbkmax = 0.9;

//		;clear registers and initialize LFOs:

//		skp	run,endclr
		sfxb.skip(RUN, 3);
		//		wrax	fhp,0
		sfxb.writeRegister(fhp, 0);
//		wldr	rmp0,0,4096	;for making triangle
		sfxb.loadRampLFO(0, 0, 4096);
//		wldr	rmp1,0,512	;servo LFO
		sfxb.loadRampLFO(1, 0, 512);
//		endclr:

//		;sum inputs to mono:

//		rdax	adcl,0.5
		sfxb.readRegister(input, 0.5);
//		rdax	adcr,0.5
//		wrax	mono,0.5	;attenuate into reverb
		sfxb.writeRegister(mono, 0.5);
if (true) {
//		;now do flange, begin by adding feedabck to delay

//		rdax	flaout,fbkmax
		sfxb.readRegister(flaout, fbkmax);
//		mulx	fbk
		sfxb.mulx(fbk);
//		rdax	mono,1
		sfxb.readRegister(mono,  1);
//		wra	fladel,0
		sfxb.FXwriteDelay("flanger", 0, 0);

//		;prepare a flamix and fbk values from pot2:

//		rdax	pot2,1
		sfxb.readRegister(Control2, 1);
//		sof	1.99,0		;clip function so we mix from 0 to max in 1st half of pot rotation
		sfxb.scaleOffset(1.99, 0);
//		wrax	flamix,0		;write flange mix value
		sfxb.writeRegister(flamix,  0);

//		rdax	pot2,1
		sfxb.readRegister(Control2,  1);
//		sof	1,-1		;goes from -1 to 0
		sfxb.scaleOffset(1.0, -1.0);
		
//		sof	1.999,0		;goes -1 to -1 at half point, then on to 0
		sfxb.scaleOffset(1.999, 0);
//		sof	1,0.999		;goes from zero to 1 beginning at midpoint
		sfxb.scaleOffset(1.0, 0.999);
//		wrax	fbk,0		;wrtie to feedback register
		sfxb.writeRegister(fbk, 0);
//		;now prepare triangle with pot1:

//		rdax	pot1,1
		sfxb.readRegister(Control1, 1);
//		mulx	pot1
		sfxb.mulx(Control1);
//		mulx	pot1
		sfxb.mulx(Control1);
//		sof	0.4,0.01		;40:1 rate range
		sfxb.scaleOffset(0.4, 0.01);
//		wrax	rmp0_rate,0	;control rmp0 rate
		sfxb.writeRegister(RMP0_RATE, 0);
//		cho	rdal,rmp0
		sfxb.chorusReadValue(RMP0);
//		sof	1,-0.25
		sfxb.scaleOffset(1, -0.25);
//		absa
		sfxb.absa();
//		wrax	tri,0
		sfxb.writeRegister(tri,  0);
//		;now servo rmp1 with the tri signal:

//		cho	rdal,rmp1	;read ramp
		sfxb.chorusReadValue(RMP1);
//		rdax	tri,-0.06		;subtract tri
		sfxb.readRegister(tri,  -0.06);
//		sof	0.5,0
		sfxb.scaleOffset(0.5, 0);
//		wrax	rmp1_rate,0	;write ramp rate
		sfxb.writeRegister(RMP1_RATE, 0);

//		cho	rda,rmp1,reg|compc,fladel
		sfxb.FXchorusReadDelay(RMP1, REG | COMPC, "flanger", 0);
//		cho	rda,rmp1,0,fladel+1
		sfxb.FXchorusReadDelay(RMP1, 0, "flanger+", 1);
//		rdfx	fhp,0.02
		sfxb.readRegister(fhp, 0.02);

//		wrhx	fhp,-1
		sfxb.writeRegisterHighshelf(fhp, -1.0);
//		wrax	flaout,0
		sfxb.writeRegister(flaout, 0);

//		rdax	flaout,1
		sfxb.readRegister(flaout, 1);
//		mulx	flamix
		sfxb.mulx(flamix);
	}
//		rdax	mono,1
		sfxb.readRegister(mono, 1);
//		rdax	revout,1
//		sfxb.readRegister(input, 1); - this is probably not necessary
//		sof	1,0.02
		sfxb.scaleOffset(1, 0.02);
//		wrax	dacl,1
		sfxb.writeRegister(flaout, 0);
		this.getPin("Audio Output 1").setRegister(flaout);
		System.out.println("Flanger code gen!");
		
	}
}
