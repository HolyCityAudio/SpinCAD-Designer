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
	public LPF4PCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);
		setName("Low Pass 4P");	}

	public void editBlock(){
		//		new LPF1PControlPanel(this);
	}	

	public void generateCode(SpinFXBlock sfxb) {
		// coefficients

		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int kfl = sfxb.allocateReg();
			int lbyp = sfxb.allocateReg();
			int lp1bl = sfxb.allocateReg();
			int lp1al = sfxb.allocateReg();
			int lp2bl = sfxb.allocateReg();
			int lp2al = sfxb.allocateReg();
			double kql = -0.4;
			
			sfxb.comment("4 pole low pass");

			sfxb.skip(RUN, 5);
			sfxb.clear();
			sfxb.writeRegister(lp1al,  0);
			sfxb.writeRegister(lp1bl,  0);
			sfxb.writeRegister(lp2al,  0);
			sfxb.writeRegister(lp2bl,  0);

			//			;prepare pot2 for low pass frequency control:
			p = this.getPin("Control Input 1").getPinConnection();
			int control1 = -1;
			if(p != null) {
				control1 = p.getRegister();
				//				rdax	pot2,1		;get pot2
				sfxb.readRegister(control1,1);
				//				sof	0.25,-0.25	;ranges -0.3 to 0
				sfxb.scaleOffset(0.35,  -0.35);
				//				exp	1,0
				sfxb.exp(1, 0);
				//				wrax	kfl,0		;write to LP filter control
				sfxb.writeRegister(kfl, 0);
				//				;now derive filter bypass function (at open condition)

				//				rdax	pot2,1		;read pot2 (LP) again
				sfxb.readRegister(control1,1);
				//				sof	1,-0.999
				sfxb.scaleOffset(1,  -0.999);
				//				exp	1,0
				sfxb.exp(1, 0);
				//				wrax	lbyp,0
				sfxb.writeRegister(lbyp,  0);
			} else {
				sfxb.scaleOffset(0, 0.25);	// set dummy value
				sfxb.writeRegister(kfl,  0);
				sfxb.writeRegister(lbyp,  0);
			}

			// ------------- start of filter code
//			rdax	lp1al,1
			sfxb.readRegister(lp1al,1);
//			mulx	kfl
			sfxb.mulx(kfl);
//			rdax	lp1bl,1
			sfxb.readRegister(lp1bl,1);
//			wrax	lp1bl,-1
			sfxb.writeRegister(lp1bl, -1);
//			rdax	lp1al,kql
			sfxb.readRegister(lp1al,kql);
//			rdax	fol,1
			sfxb.readRegister(input,0.25);
//			mulx	kfl
			sfxb.mulx(kfl);
//			rdax	lp1al,1
			sfxb.readRegister(lp1al,1);
//			wrax	lp1al,0
			sfxb.writeRegister(lp1al, 0);

//			rdax	lp2al,1
			sfxb.readRegister(lp2al,1);
//			mulx	kfl
			sfxb.mulx(kfl);
//			rdax	lp2bl,1
			sfxb.readRegister(lp2bl,1);
//			wrax	lp2bl,-1
			sfxb.writeRegister(lp2bl, -1);
//			rdax	lp2al,kql
			sfxb.readRegister(lp2al,kql);
//			rdax	lp1bl,1
			sfxb.readRegister(lp1bl,1);
//			mulx	kfl
			sfxb.mulx(kfl);
//;			rdax	lp2al,1
			sfxb.readRegister(lp2al,1);
//			wrax	lp2al,0
			sfxb.writeRegister(lp2al, 0);

			this.getPin("Audio Output 1").setRegister(lp2al);	
//			this.getPin("Audio Output 1").setRegister(lp2bl);	
		}
		System.out.println("LPF 4 pole code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
	}
}
