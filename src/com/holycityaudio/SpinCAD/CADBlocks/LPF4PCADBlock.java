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

public class LPF4PCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 240;
	double kql = 0.4;
	boolean is4Pole = false;

	public LPF4PCADBlock(int x, int y) {
		super(x, y);
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Audio Output");
		hasControlPanel = true;
		addControlInputPin(this);
		if(is4Pole == true) {
			setName("Low Pass 4P");	
		} else {
			setName("Low Pass 2P");	
		}
	}

	public void editBlock(){
				new LPF4PControlPanel(this);
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
			int lp2bl = -1;
			int lp2al = -1;

			if(is4Pole == true) {
				sfxb.comment("4 pole low pass");
				lp2bl = sfxb.allocateReg();
				lp2al = sfxb.allocateReg();
			} else {
				sfxb.comment("2 pole low pass");
			}
			//			;prepare pot2 for low pass frequency control:
			p = this.getPin("Control Input 1").getPinConnection();
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
			sfxb.readRegister(lp1al,-kql);
			sfxb.readRegister(input,0.25);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp1al,1);
			sfxb.writeRegister(lp1al, 0);

			if(is4Pole) {
				sfxb.readRegister(lp2al,1);
				sfxb.mulx(kfl);
				sfxb.readRegister(lp2bl,1);
				sfxb.writeRegister(lp2bl, -1);
				sfxb.readRegister(lp2al,-kql);
				sfxb.readRegister(lp1bl,1);
				sfxb.mulx(kfl);
				sfxb.readRegister(lp2al,1);
				sfxb.writeRegister(lp2al, 0);

				this.getPin("Audio Output").setRegister(lp2al);
			}
			else {
				this.getPin("Audio Output").setRegister(lp1al);				
			}
		}
		System.out.println("LPF 4 pole code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
	}

	public boolean getIs4Pole() {
		return is4Pole;
	}

	public void setIs4Pole(boolean r) {
		is4Pole = r;
	}

	public void setQ(double value) {
		kql = 10/(value); // TODO Auto-generated method stub
	}

	public double getQ() {
		// TODO Auto-generated method stub
		return kql/10.0;
	}
}
