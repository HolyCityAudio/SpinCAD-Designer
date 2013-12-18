/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * DiscoReverbCADBlock.java
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

import com.holycityaudio.SpinCAD.SpinFXBlock;

public class DiscoReverbCADBlock extends ReverbCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7759922095081133268L;

	public DiscoReverbCADBlock(int x, int y) {
		super(x, y);
		// add another input and output pin for stereo
		addInputPin(this);
		addOutputPin(this);
		setName("Disco Reverb");
	}

	public void generateCode(SpinFXBlock sfxb) {
		//		;dance patchfor disco mixers: 
		//			;pot1 = Reverb to infinite RT, scales in and out levels
		// TODO clean up for proper behavior with no inputs etc
		double inputGain = 0.25;
		//			;reveb can capture tonality for filter manipulation.
		//			;beware, infinite reverb turns off input!

		//			equ	krt	reg0
		int krt = sfxb.allocateReg();
		//			equ	kin	reg1
		int kin = sfxb.allocateReg();
		//			equ	kmix	reg2
		int kmix = sfxb.allocateReg();
		//			equ	temp	reg13
		int temp = sfxb.allocateReg();
		//			equ	rmixl	reg14
		int rmixl = sfxb.allocateReg();
		//			equ	rmixr	reg15
		int rmixr = sfxb.allocateReg();

		//			mem	ap1	202
		sfxb.FXallocDelayMem("ap1", 202);
		//			mem	ap2	541
		sfxb.FXallocDelayMem("ap2", 541);
		//			mem	ap3	1157
		sfxb.FXallocDelayMem("ap3", 1157);
		//			mem	ap4	1903
		sfxb.FXallocDelayMem("ap4", 1903);

		//			mem	dap1a	2204
		sfxb.FXallocDelayMem("dap1a", 2204);
		//			mem	dap1b	3301
		sfxb.FXallocDelayMem("dap1b", 3301);
		//			mem	del1	4456
		sfxb.FXallocDelayMem("del1", 4456);
		//			mem	dap2a	3532
		sfxb.FXallocDelayMem("dap2a", 3532);
		//			mem	dap2b	3201
		sfxb.FXallocDelayMem("dap2b", 3201);
		//			mem	del2	6325
		sfxb.FXallocDelayMem("del2", 6325);

		//			equ	kap	0.6
		double kap = 0.7;
		sfxb.comment("Disco reverb");
		
		int revControl = this.getPin("Control Input 1").getPinConnection().getRegister();
		int inputL = this.getPin("Audio Input 1").getPinConnection().getRegister();
		int inputR = this.getPin("Audio Input 2").getPinConnection().getRegister();
		this.getPin("Audio Output 1").setRegister(rmixl);
		this.getPin("Audio Output 2").setRegister(rmixr);
		

		//			;prepare pots to affect control variables:
		//			;pot0 controls reverb time, but also affects input drive level;
		//			;reveb time is moderate up to about mid position, then increases
		//			;to infinity (or nearly) at full position.
		//			;input drive is constant, but decreases at the full pot0 position.
		//			;output mix is varied over the first half of pot0, then remains
		//			;high to the end of pot0's range.

		//			rdax	pot0,1.999	;get pot0, clip the upper half of pot0's range.
		sfxb.readRegister(revControl, 1.999);
		//			wrax	kmix,0		;write the output mix value
		sfxb.writeRegister(kmix,  0);

		//			rdax	pot0,-1		;get pot0 again, 0 to -1
		sfxb.readRegister(revControl, -1.0);
		//			sof	1,0.999		;now +1 to 0
		sfxb.scaleOffset(1, 0.999);
		//			sof	1.999,0		;now +1 until midpint, then decreases to 0
		sfxb.scaleOffset(1.999, 0);
		//			wrax	kin,0		;write the input attenuator value
		sfxb.writeRegister(kin,  0);

		//			rdax	pot0,1		;get pot0 again
		sfxb.readRegister(revControl, 1);
		//			wrax	krt,1		;save in krt, keep in ACC
		sfxb.writeRegister(krt,  0);
		//			sof	1,-0.5		;subtract 1/2
		sfxb.scaleOffset(1.0, -0.5);
		//			skp	gez,2		;skp if pot is in upper half of range
		sfxb.skip(GEZ, 2);
		//			sof	0,0.5		;load accumulator with +0.5
		sfxb.scaleOffset(0, 0.5);
		//			wrax	krt,0		;overwrite if pot is in lower half of range
		sfxb.writeRegister(krt,  0);


//				;now do reverb, simple, twin loop, mono drive:

//					rdax	adcl,0.25
		sfxb.readRegister(inputL, inputGain);
//					rdax	adcr,0.25	;get inputs, leave headroom
		sfxb.readRegister(inputR, inputGain);
//					mulx	kin		;scale by input attenuator
		sfxb.mulx(kin);
//					rda	ap1#,kap	;4 all passes:
		sfxb.FXreadDelay("ap1#", 0, kap);
//						wrap	ap1,-kap
		sfxb.FXwriteAllpass("ap1", 0, -kap);
//						rda	ap2#,kap
		sfxb.FXreadDelay("ap2#", 0, kap);
//						wrap	ap2,-kap
		sfxb.FXwriteAllpass("ap2", 0, -kap);
//						rda	ap3#,kap
		sfxb.FXreadDelay("ap3#", 0, kap);
//						wrap	ap3,-kap
		sfxb.FXwriteAllpass("ap3", 0, -kap);
//						rda	ap4#,kap
		sfxb.FXreadDelay("ap4#", 0, kap);
//						wrap	ap4,-kap
		sfxb.FXwriteAllpass("ap4", 0, -kap);
//						wrax	temp,0		;write ap output to temp reg
		sfxb.writeRegister(temp,  0);

//						rda	del2#,1
		sfxb.FXreadDelay("del2", 1.0, 1);
//						mulx	krt;
		sfxb.mulx(krt);
//						rdax	temp,1
		sfxb.readRegister(temp, 1.0);
//						rda	dap1a#,kap
		sfxb.FXreadDelay("dap1a", 1.0, kap);
//						wrap	dap1a,-kap
		sfxb.FXwriteAllpass("dap1a", 0, -kap);
//						rda	dap1b#,kap
		sfxb.FXreadDelay("dap1b#", 0, kap);
//						wrap	dap1b,-kap
		sfxb.FXwriteAllpass("dap1b", 0, -kap);
//						wra	del1,0
		sfxb.FXwriteDelay("del1", 0, 0);
//						rda	del1#,1
		sfxb.FXreadDelay("del1#", 0, 1);
//						mulx	krt
		sfxb.mulx(krt);
//						rdax	temp,1
		sfxb.readRegister(temp,  1);
//						rda	dap2a#,kap
		sfxb.FXreadDelay("dap2a#", 0, kap);
//						wrap	dap2a,-kap
		sfxb.FXwriteAllpass("dap2a", 0, -kap);
//						rda	dap2b#,kap
		sfxb.FXreadDelay("dap2b#", 0, kap);
//						wrap	dap2b,-kap
		sfxb.FXwriteAllpass("dap2b", 0, -kap);
//						wra	del2,0
		sfxb.FXwriteDelay("del2", 0, 0);

//						;now mix the inputs with the reverb:

//							rdax	adcl,-1
		sfxb.readRegister(inputL, -1.0);
//							rda	del1,1.5
		sfxb.FXreadDelay("del1", 0.0, 1.5);
//							mulx	pot0
		sfxb.mulx(revControl);
//							rdax	adcl,1
		sfxb.readRegister(inputL, 1.0);
//							wrax	rmixl,0
		sfxb.writeRegister(rmixl, 0);
//							rdax	adcr,-1
		sfxb.readRegister(inputR, -1.0);
//							rda	del2,1.5
		sfxb.FXreadDelay("del2", 0.0, 1.5);
//							mulx	pot0
		sfxb.mulx(revControl);
//							rdax	adcr,1
		sfxb.readRegister(inputR, 1.0);
//							wrax	rmixr,0
		sfxb.writeRegister(rmixr, 0);

//							;Reverb outputs are at rmixl and rmixr.
	}
}
