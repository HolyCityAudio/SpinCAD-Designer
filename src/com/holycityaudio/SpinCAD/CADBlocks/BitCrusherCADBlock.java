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

public class BitCrusherCADBlock extends GainCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2552493950557066242L;
	int nBits = 3;

	public BitCrusherCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		setName("Bit Crusher");
	}

	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if(p != null) {
			input = p.getRegister();
			int output = sfxb.allocateReg();
			int crush = -1;
			
			sfxb.comment(getName());

			switch(nBits) {
			case 2:
				crush = 0xFFC00000;
				break;

			case 3:
				crush = 0xFFE00000;
				break;
			case 4:
				crush = 0xFFF00000;
				break;
			case 5:
				crush = 0xFFF80000;
				break;
			case 6:
				crush = 0xFFFC0000;
				break;
			default:
				break;

			}		
			//		clr 
			sfxb.clear();
			//		rdax adcl,0.5 
			sfxb.readRegister(input, 1.0);
			//		rdax adcr,0.5 
			//		skp neg,invit; working with 2's comp numbers, if negative skip down
			sfxb.skip(NEG, 2);
			//		and crush 
			sfxb.and(crush);
			//		skp gez,outter 
			sfxb.skip(GEZ, 3);
			//		invit: 
			//		sof -1.0,0 ; for negative samples, invert them, 
			sfxb.scaleOffset(-1.0, 0);
			//		and crush ; mask the data 
			sfxb.and(crush);
			//		sof -1.0,0 ; invert it back 
			sfxb.scaleOffset(-1.0, 0);
			//		outter: 
			//		wrax dacl,1.0 
			sfxb.writeRegister(output, 0);
			//		wrax dacr,1.0
			this.getPin("Audio Output 1").setRegister(output);
			System.out.println("Bit crusher code gen!");	
		}
	}
	public void editBlock(){
		new BitCrusherControlPanel(this);
	}

	public int getBits() {
		return nBits;
	}

	public void setBits(int value) {
		nBits = value;
	}	

}
