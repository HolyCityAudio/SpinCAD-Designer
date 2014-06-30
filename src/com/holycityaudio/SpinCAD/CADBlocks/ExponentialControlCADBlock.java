/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * EnvelopeControlCADBlock.java
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

public class ExponentialControlCADBlock extends ControlCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4812040104612463732L;

	public ExponentialControlCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);	//	delay time
		addControlOutputPin(this);	//	feedback
		setName("Exp");
	}
	
	public void generateCode(SpinFXBlock sfxb) {

		int Exp = sfxb.allocateReg();			//
		sfxb.comment(getName());

		int input = this.getPin("Control Input 1").getPinConnection().getRegister();

//		rdax	pot1,1		;get pot1
		sfxb.readRegister(input, 1);
//		sof	0.5,-0.5	;ranges -0.5 to 0
		sfxb.scaleOffset(0.5, -0.5);
//		exp	1,0
		sfxb.exp(1,0);

		sfxb.writeRegister(Exp,0);

		this.getPin("Control Output 1").setRegister(Exp);
		System.out.println("Envelope control code gen!");

	}

}
