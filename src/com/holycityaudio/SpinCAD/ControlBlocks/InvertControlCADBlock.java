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

public class InvertControlCADBlock extends ControlCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2532916136606805308L;

	public InvertControlCADBlock(int x, int y) {
		super(x, y);
		// ---
		addControlInputPin(this);	//	delay time
		addControlOutputPin(this);	//	feedback
		setName("Invert");
	}

	//	takes a 0 to 1 input and makes it 1 to 0
	public void generateCode(SpinFXBlock sfxb) {
		int controlInput;
		int controlOutput = sfxb.allocateReg();
		sfxb.comment(getName());

		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		if(p == null) {	// there's no pin attached!
		}
		else {
			controlInput = p.getRegister();
			sfxb.readRegister(controlInput, 1.0);
			sfxb.scaleOffset(-0.999, 0.999);
			sfxb.writeRegister(controlOutput,  0);
			p = this.getPin("Control Output 1");
			p.setRegister(controlOutput);
		}
		System.out.println("Invert code gen!");
	}
}