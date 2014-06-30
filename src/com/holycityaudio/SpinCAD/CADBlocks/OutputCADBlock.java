/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
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
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class OutputCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2094768928842616450L;
	/**
	 * 
	 */
	static double lGain = 1.0;
	static double rGain = 1.0;

	public OutputCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this);
		addInputPin(this);
		setName("Output");
		setBorderColor(Color.darkGray);
		//		OutputBlock output = new OutputBlock(this, 32, 34);
	}

	public void generateCode(SpinFXBlock eP) {
//		System.out.println("Output codegen!");
		eP.comment(getName());
		
		SpinCADPin p = getPin("Audio Input 1");
		SpinCADPin pC = p.getPinConnection();
		SpinCADBlock b = p.getBlockConnection();
		
		if (pC != null) {
			// TODO debug this!
			// so that if gain == 1.0 then the output register of the previous block becomes e.g. DACL
			if(lGain < 2.0) {
				int i = pC.getRegister();
				if( (i >= 32 && i <= 64) || i == ADCR || i == ADCL || i == POT0 || i == POT1 || i == POT2) {
					eP.readRegister(i,lGain);
					eP.writeRegister(DACL, 0.0);
				}			
			}
			else {
				p.setRegister(DACL);	// default, just set register of previous block to DACL
			}
		}

		p = getPin("Audio Input 2").getPinConnection();
		if (p != null) {
			if(rGain < 2.0) {
				int i = p.getRegister();
				if(( i >= 32 && i <= 64) || i == ADCR || i == ADCL || i == POT0 || i == POT1 || i == POT2) {
					eP.readRegister(i,rGain);
					eP.writeRegister(DACR, 0.0);
				}
			}
			else {
				p.setRegister(DACR);	// default, just set register of previous block to DACL
			}
		}
	}

	public void editBlock(){
		new OutputControlPanel(this);
	}

	public void setLGain(double d) {
		lGain = d;
	}

	public void setRGain(double d) {
		rGain = d;
	}

	public static double getLGain() {
		return lGain;
	}

	public static double getRGain() {
		return rGain;
	}

}
