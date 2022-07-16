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
public class ScaleOffsetControlCADBlock extends ControlCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2532916136606805308L;

	private double inLow = 0.0;
	private double inHigh = 1.0;
	private double outLow;
	private double outHigh;
	
	public ScaleOffsetControlCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this);	//	delay time
		addControlOutputPin(this);	//	feedback
		outLow = 0.00;
		outHigh = 0.75;
		setName("Scale/Offset");
	}

	//	takes a 0 to 1 input and makes it 1 to 0
	public void generateCode(SpinFXBlock sfxb) {
		int controlInput;

		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		if(p == null) {	// there's no pin attached!
		}
		else {
			int controlOutput = sfxb.allocateReg();
			controlInput = p.getRegister();
			sfxb.comment(getName());
			
			sfxb.readRegister(controlInput, 1.0);

			double scale = (outHigh - outLow)/(inHigh - inLow);
			double offset = outLow - (inLow * scale);
			if(offset > 0.999) {
				offset = 0.999;
			}
			sfxb.scaleOffset(scale, offset);
			sfxb.writeRegister(controlOutput,  0);
			p = this.getPin("Control Output 1");
			p.setRegister(controlOutput);
		}
		System.out.println("Scale Offset code gen!");
	}

	public void editBlock(){
		new ScaleOffsetControlPanel(this);
	}

	public void setInLow(double d) {
		inLow = d;
	}

	public double getInLow() {
		return inLow;
	}
	public void setInHigh(double d) {
		inHigh = d;
	}

	public double getOutHigh() {
		return outHigh;
	}
	public void setOutLow(double d) {
		outLow = d;
	}

	public double getOutLow() {
		return outLow;
	}
	public void setOutHigh(double d) {
		outHigh = d;
	}

	public double getInHigh() {
		return inHigh;
	}
}