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

public class PowerControlCADBlock extends ControlCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7025985649946130854L;
	double power = 3;
	boolean invert = false;
	boolean flip = false;

	public PowerControlCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this);	//	feedback
		addControlOutputPin(this);	//	feedback
		setName("Power");
	}

	public void generateCode(SpinFXBlock sfxb) {

		int control = -1;
		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		if (p != null ) {
			control = p.getRegister();
			int lbyp = sfxb.allocateReg();
			sfxb.comment(getName());
			//			rdax	pot2,-1
			sfxb.readRegister(control, 1.0);
			if(invert == true) {
				sfxb.scaleOffset(-0.9990234375, 0.9990234375);
			}
			for(int i = 0; i < (int) power - 1; i++) {
				//				mulx	pot2
				sfxb.mulx(control);		
			}
			//			wrax	lbyp,0
			if(flip == true) {
				sfxb.scaleOffset(-0.9990234375, 0.9990234375);
			}
			sfxb.writeRegister(lbyp, 0);
			this.getPin("Control Output 1").setRegister(lbyp);
		}
		System.out.println("Power control code gen! Power:" + power);
	}

	public void editBlock(){
		new PowerControlControlPanel(this);
	}
	//====================================================
	public double getPower() {
		return power;
	}

	public void setPower(double d) {
		power = d;
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
