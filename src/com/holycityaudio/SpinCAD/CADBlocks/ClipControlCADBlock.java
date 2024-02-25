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

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ClipControlCADBlock extends ControlCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7025985649946130854L;
	double gain = 3;
	boolean invert = false;
	boolean flip = false;

	public ClipControlCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this);
		addControlOutputPin(this);	//	feedback
		setName("Clip");
	}

	public void generateCode(SpinFXBlock sfxb) {

		int control = -1;
		double scaledGain = gain;
		boolean flipped = false;	// keep track of inversions from SOF
		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		sfxb.comment(getName());
		if (p != null ) {
			control = p.getRegister();
			int lbyp = sfxb.allocateReg();
			sfxb.readRegister(control, 1.0);
			if(flip == true) {
				sfxb.scaleOffset(-0.999, 0.999);
			}
			if(scaledGain > 8.0)	// 10.0 is the max
			{
				sfxb.scaleOffset(-2.0,  0);
				scaledGain = scaledGain/2.0;
				flipped = true;
			}
			if(scaledGain > 4.0)	// 10.0 is the max
			{
				sfxb.scaleOffset(-2.0,  0);
				scaledGain = scaledGain/2.0;
				if(flipped == true)	// it was flipped in the previous stage
					flipped = false;
				else
					flipped = true;	// it wasn't flipped previously
			}
			if(scaledGain > 2.0)	// 4.0 would be the max here
			{
				sfxb.scaleOffset(-2.0,  0);
				scaledGain = scaledGain/2.0;
				if(flipped == true)	// it was flipped in the previous stage
					flipped = false;
				else
					flipped = true;	// it wasn't flipped previously
			}
			if(scaledGain > 1.0) {
				sfxb.scaleOffset(-scaledGain,  0);				
				if(flipped == true)	// it was flipped in the previous stage
					flipped = false;
				else
					flipped = true;	// it wasn't flipped previously
			}
			
			if(flipped == true)
				sfxb.scaleOffset(-1.0, 0);
			
			if(invert == true) {
				sfxb.scaleOffset(-0.999, 0.999);
			}
			sfxb.writeRegister(lbyp, 0);
			this.getPin("Control Output 1").setRegister(lbyp);
		}
		System.out.println("Clip control code gen! Clip:" + gain);
	}

	public void editBlock(){
		new ClipControlControlPanel(this);
	}
	//====================================================
	public double getGain() {
		return gain;
	}

	public void setGain(double d) {
		gain = d;
	}

	public boolean getInvert() {
		return invert;
		}

	public void setInvert(boolean b) {
		invert = b;
	}

	public boolean getFlip() {
		return flip;
		}

	public void setFlip(boolean b) {
		flip = b;
	}

}
