/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * CubeGainCADBlock.java
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

public class CubeGainCADBlock extends GainCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7292138111130632016L;

	public CubeGainCADBlock(int x, int y) {
		super(x, y);
		setName("Cubed");
	}
	
	public void generateCode(SpinFXBlock sfxb) {
		int input = this.getPin("Audio Input 1").getPinConnection().getRegister();
//		int Control1 = -1;
		
		int temp = sfxb.allocateReg();
		int output = sfxb.allocateReg();
		sfxb.comment("Cube gain");

		sfxb.readRegister(input,1.0);  // read left 100%

//		wrax    temp,-0.33333
		sfxb.writeRegister(temp, -0.93333);
//		mulx    temp
		sfxb.mulx(temp);
//		mulx    temp
		sfxb.mulx(temp);
//		rdax     temp,1
		sfxb.readRegister(temp, 1);
		//		sof       1.5,0
		sfxb.scaleOffset(1.5, 0);
		sfxb.writeRegister(output, 0);
		
		SpinCADPin p = this.getPin("Audio Output 1");
		p.setRegister(output);
	}
}
