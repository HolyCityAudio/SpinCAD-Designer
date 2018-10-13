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

public class QuantizerCADBlock extends GainCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2552493950557066242L;
	int nBits = 3;
	int divisions = 6;	// # of divisions to use with control input

	public QuantizerCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);
		hasControlPanel = true;
		setName("Quantizer");
	}

	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if(p != null) {
			input = p.getRegister();
			int output = sfxb.allocateReg();
			int crush = -1;

			sfxb.comment(getName());
			p = this.getPin("Control Input 1").getPinConnection();
			if(p != null) {
				int depth = p.getRegister();
				crush = getCrush(nBits + divisions);
				sfxb.readRegister(depth, 1.0);
				for(int i = 0; i < (divisions - 1); i++) {
					sfxb.scaleOffset(1.0, (-1.0/divisions));
					sfxb.skip(NEG, 2 * (divisions - i - 2) + 10 * (i + 1));			
				}
				sfxb.clear();
				sfxb.readRegister(input, 1.0);
				sfxb.skip(NEG, 2);
				sfxb.and(crush << (divisions - 1));
				sfxb.skip(GEZ, 3);
				sfxb.scaleOffset(-1.0, 0);
				sfxb.and(crush << (divisions - 1));
				sfxb.scaleOffset(-1.0, 0);
				sfxb.writeRegister(output, 0);
				sfxb.skip(ZRO, 10 * (divisions - 1) - 1);

				for(int i = 0; i < (divisions - 1); i++) {
					sfxb.clear();
					sfxb.readRegister(input, 1.0);
					sfxb.skip(NEG, 2);
					sfxb.and(crush << i);
					sfxb.skip(GEZ, 3);
					sfxb.scaleOffset(-1.0, 0);
					sfxb.and(crush << i);
					sfxb.scaleOffset(-1.0, 0);
	//				sfxb.or(~crush);
					sfxb.writeRegister(output, 0);
					if (i < divisions - 2) {
						sfxb.skip(ZRO, 10 * (divisions - i -2) - 1);
					}
				}
				
			} else {
				crush = getCrush(nBits);
				sfxb.readRegister(input, 1.0);
				sfxb.skip(NEG, 2);
				sfxb.and(crush);
				sfxb.skip(GEZ, 3);
				sfxb.scaleOffset(-1.0, 0);
				sfxb.and(crush);
				sfxb.scaleOffset(-1.0, 0);
				sfxb.writeRegister(output, 0);
			}
			//		wrax dacr,1.0
			this.getPin("Audio Output 1").setRegister(output);
			System.out.println("Bit crusher code gen!");	
		}
	}
	
	private int getCrush(int index) {
		int crush = 0;
		
		switch(index) {	
		case 1:
			crush = 0xFF800000;
			break;
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
		case 7:
			crush = 0xFFFE0000;
			break;
		case 8:
			crush = 0xFFFF0000;
			break;
		case 9:
			crush = 0xFFFF8000;
			break;
		case 10:
			crush = 0xFFFFC000;
			break;
		case 11:
			crush = 0xFFFFE000;
			break;
		case 12:
			crush = 0xFFFFF000;
			break;
		case 13:
			crush = 0xFFFFF800;
			break;
		case 14:
			crush = 0xFFFFFC00;
			break;
		case 15:
			crush = 0xFFFFFE00;
			break;
		case 16:
			crush = 0xFFFFFF00;
			break;
		case 17:
			crush = 0xFFFFFF80;
			break;
		case 18:
			crush = 0xFFFFFFC0;
			break;
		case 19:
			crush = 0xFFFFFFE0;
			break;
		case 20:
			crush = 0xFFFFFFF0;
			break;

		default:
			break;		
		}
		return crush;
	}
	
	public void editBlock(){
		new QuantizerControlPanel(this);
	}

	public int getBits() {
		return nBits;
	}

	public void setBits(int value) {
		nBits = value;
	}	

}
