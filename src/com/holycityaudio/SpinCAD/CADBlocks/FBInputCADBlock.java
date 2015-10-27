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

public class FBInputCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4680315672929089295L;
	
	private int register = -1;
	private double lGain = 1.0;

	public FBInputCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Feedback Input");
		setBorderColor(Color.orange);
		// change this to allocate letters A B C D E etc.
		// actually assigning registers can be done at code generation time
		setName("FB In " + getIndex());
	}
	
	public FBInputCADBlock(int x, int y, int ind) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Feedback Input");
		setBorderColor(Color.orange);
		setIndex(ind);
		// change this to allocate letters A B C D E etc.
		// actually assigning registers can be done at code generation time
		setName("FB In " + getIndex());
	}
	
	public void generateCode(SpinFXBlock sfxb) {
//		System.out.println("Output codegen!");
		sfxb.comment(getName());
		
		SpinCADPin p = getPin("Feedback Input");
		SpinCADPin pC = p.getPinConnection();
//		SpinCADBlock b = p.getBlockConnection();
		
		if (pC != null) {
			if(lGain <= 1.9) {
				int i = pC.getRegister();
				if( (i >= 32 && i <= 64) || i == ADCR || i == ADCL) {
					sfxb.readRegister(i,lGain);
					sfxb.writeRegister(register, 0.0);
				}			
			}
		}
	}

	public void setLGain(double d) {
		lGain = d;
	}

	public double getLGain() {
		return lGain;
	}

	public int getRegister() {
		return register;
	}
	
	public void setRegister(int val) {
		register = val;
	}
	
	public void editBlock(){
		new FBInputControlPanel(this);
	}

}
