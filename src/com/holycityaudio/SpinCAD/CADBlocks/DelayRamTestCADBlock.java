/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BassManEQCADBlock.java
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

// this block is experimental and needs adjustment to work with the FV-1

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;
import com.holycityaudio.SpinCAD.SpinCADBlock;

public class DelayRamTestCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;

	public DelayRamTestCADBlock(int x, int y) {
		super(x, y);
		addInputPin(this);
		addOutputPin(this);
		setName("Delay RAM Test");	
	}

	public void editBlock(){

	}	

	public void generateCode(SpinFXBlock sfxb) {

		double inputGain = 0.5;

		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int output = sfxb.allocateReg();
			//			mem	ap1	202
			sfxb.FXallocDelayMem("ram", 1);

			sfxb.comment("Delay RAM Test");
			sfxb.readRegister(input, 1.0);
			sfxb.FXwriteDelay("ram", 0, 0.0);
			sfxb.FXreadDelay("ram", 0, 1.0);
			sfxb.writeRegister(output, 0.0);	
			
			this.getPin("Audio Output 1").setRegister(output);	
		}
		System.out.println("Delay Test code gen!");
	}
}
