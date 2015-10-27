/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * BPFCADBlock.java
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

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class BPFCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2168695413490384727L;
	double f0 = 1200;

	public BPFCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Audio Output");
		setName("Band Pass");
		setBorderColor(Color.PINK);
	}
	
	public void editBlock(){
		new BPFControlPanel(this);
	}
	
	public void generateCode(SpinFXBlock sfxb) {
		// at this moment, code implements a low pass.
		// and not a very good one at that!
		// coefficients
		
		// need to figure out how to map these coefficients to freq/resonance
		sfxb.comment(sfxb.getName());
		double kfl = 1.0 - Math.exp((-6.283 * f0)/getSamplerate());
		double kql = -0.13;
		
		int input = this.getPin("Audio Input").getPinConnection().getRegister();
		
		int lpal = sfxb.allocateReg();
		int lpbl = sfxb.allocateReg();
		int lpoutl = sfxb.allocateReg();

		//		int rmixl = sfxb.allocateReg();
//		int kfx = sfxb.allocateReg();	
		
		//		;now do the low pass.
		
		sfxb.skip(RUN, 3);
		sfxb.clear();
		sfxb.writeRegister(lpal,  0);
		sfxb.writeRegister(lpbl,  0);
				
		// ------------- start of filter code
	 	//		rdax	lpal,1
		sfxb.readRegister(lpal, kfl);
		//		mulx	kfl
//		sfxb.mulx(kfl);
		//		rdax	lpbl,1
		sfxb.readRegister(lpbl, 1.0);
		//		wrax	lpbl,-1
		sfxb.writeRegister(lpbl, -1.0);
		//		rdax	lpal,kql
		sfxb.readRegister(lpal, kql);
		//		rdax	input,1
		sfxb.readRegister(input, 1.0);
		//		wrax	lpoutl,1	;lp output
		sfxb.writeRegister(lpoutl, kfl);
		//		mulx	kfl
//		sfxb.mulx(kfl);
		//		rdax	lpal,1
		sfxb.readRegister(lpal, 1.0);
		//		wrax	lpal,0
		sfxb.writeRegister(lpal, 0);

		//		rdax	lpbl,-1
		sfxb.readRegister(lpbl, -1.0);
		//		rdax	rmixl,1
		sfxb.readRegister(input, 1.0);
		//		rdax	lpbl,1
		sfxb.readRegister(lpbl, 1.0);
		
		this.getPin("Audio Output").setRegister(lpoutl);	
		System.out.println("BPF code gen!");

	}
}
