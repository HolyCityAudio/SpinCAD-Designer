/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * LPF4PCADBlock.java
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

public class SVF2PVariableCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 240;
	double kql = 0.4;

	public SVF2PVariableCADBlock(int x, int y) {
		super(x, y);
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Low Pass");
		addOutputPin(this, "Band Pass");
		addOutputPin(this, "Hi Pass");
//		addOutputPin(this, "Notch");
		hasControlPanel = true;
		addControlInputPin(this, "Frequency");
		addControlInputPin(this, "Resonance");
		setName("SVF 2P");	
	}

	public void editBlock(){
		new SVF2PVariableControlPanel(this);
	}	

	public void generateCode(SpinFXBlock sfxb) {

		int input = -1;

		SpinCADPin p = this.getPin("Audio Input").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int kfl = sfxb.allocateReg();
			int lbyp = sfxb.allocateReg();
			int lp1bl = sfxb.allocateReg();
			int lp1al = sfxb.allocateReg();
			int hipass = sfxb.allocateReg();

			sfxb.comment("2 pole low pass");
			//			;prepare pot2 for low pass frequency control:
			p = this.getPin("Frequency").getPinConnection();
			int control1 = -1;
			if(p != null) {
				control1 = p.getRegister();
				sfxb.readRegister(control1,1);
				sfxb.scaleOffset(0.35,  -0.35);
				sfxb.exp(1, 0);
				sfxb.writeRegister(kfl, 0);
				//				;now derive filter bypass function (at open condition)
				sfxb.readRegister(control1,1);
				sfxb.scaleOffset(1,  -0.999);
				sfxb.exp(1, 0);
				sfxb.writeRegister(lbyp,  0);
			} else {
				sfxb.scaleOffset(0, 0.25);	// set dummy value
				sfxb.writeRegister(kfl,  0);
				sfxb.writeRegister(lbyp,  0);
			}
			// ------------- start of filter code
			sfxb.readRegister(lp1al,1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp1bl,1);
			sfxb.writeRegister(lp1bl, -1);
			p = this.getPin("Resonance").getPinConnection();
			int control2 = -1;
			int temp = -1;

			if(p != null) {
				control2 = p.getRegister();
				temp = sfxb.allocateReg();
				// we need to save this so we can multiply the next result by the control input
				// to get adjustable resonance
				sfxb.writeRegister(temp, 0);
				sfxb.readRegister(lp1al,-kql);
				mulx(control2);
				// then we add it back in later and everything's fine.
				sfxb.readRegister(temp,1.0);
			}
			else {
				sfxb.readRegister(lp1al,-kql);				
			}

			sfxb.readRegister(input,0.5);
			sfxb.writeRegister(hipass, 1.0);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp1al,1);
			sfxb.writeRegister(lp1al, 0);

			this.getPin("Band Pass").setRegister(lp1al);				
			this.getPin("Low Pass").setRegister(lp1bl);				
			this.getPin("Hi Pass").setRegister(hipass);				
		}
		System.out.println("LPF 2/4 pole code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
	}

	public void setQ(double value) {
		kql = 10/(value); 
	}

	public double getQ() {
		return kql/10.0;
	}
}