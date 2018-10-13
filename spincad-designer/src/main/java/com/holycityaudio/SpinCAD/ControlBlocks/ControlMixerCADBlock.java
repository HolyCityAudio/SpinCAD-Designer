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

public class ControlMixerCADBlock extends ControlCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676526418848384621L;

	public ControlMixerCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);
		addControlInputPin(this);
		addControlOutputPin(this);
		setName("Control Mixer");
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int mix = sfxb.allocateReg();
		int leftIn = -1;
		sfxb.comment(getName());
		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
			sfxb.readRegister(leftIn, 1.0);	// get left signal, add to register, scale by 1.0
		}

		int rightIn = -1;
		p = this.getPin("Control Input 2").getPinConnection();
		if (p != null) {
			rightIn = p.getRegister();			
			sfxb.readRegister(rightIn,  1.0);
		}

		// at this point, if there is no right input, we clear accumulator
		// if there is a right input but no right control input, then ACC holds rightIn * defaultGain
		// if there is a right input and right control input, then ACC holds right input * right control input

		if(leftIn + rightIn != -2) {
			sfxb.writeRegister(mix, 0.0);	// dry signal, for later
			this.getPin("Control Output 1").setRegister(mix);
			System.out.println("Control Mixer code gen!");
		}
	}
}
