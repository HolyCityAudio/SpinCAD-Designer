/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BassManEQCADBlock.java
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

// this block is experimental and needs adjustment to work with the FV-1

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class BassmanEQCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	// tone control component values
	double c1 = 0.25e-9;
	double c2 = 20e-9;
	double c3 = 20e-9;
	double r1 = 250e+03;
	double r2 = 1e+06;
	double r3 = 25e+03; 
	double r4 = 56e+03;
	//===================================
	double m = 0.5;	// mid setting
	double t = 0.5;	// treble setting
	double l = 0.1;	// low setting (log sweep)

	public BassmanEQCADBlock(int x, int y) {
		super(x, y);
		setName("Bassman '59 EQ");	
	}

	public void editBlock(){
		new BassmanEQControlPanel(this);
	}	

	public void generateCode(SpinFXBlock sfxb) {

		// ==================================
		// http://www.docstoc.com/docs/53837224/Discretization-of-the-59-Fender-Bassman-Tone-Stack

		// double b0 = 1.0;
		double b1 = t * c1 * r1 + m * c3 * r3 + l * (c1 * r2 + c2 * r2) + (c1 * r3 + c2 * r3);
		double b2 = t * (c1 * c2 * r1 *r4 + c1 * c3 * r1 * r4) - m * m * (c1 *c3 * r3 * r3 + c2 * c3 * r3 * r3) +
				m * (c1 * c3 * r1 *r3 + c1 * c3* r3 * r3 + c2 * c3 * r2 *r2);
		double b3 = (c1 * c2 * c3) * (l * r2  - m * r3 + r3) * ((m * r3) * (r1 + r4)  + (t * r1 * r4));
		
		double a0 = 1.0;
		double a1 = (c1 * r1 + c1 * r3 + c2 * r3 + c2 * r4 + c3 * r4) + m * c3 * r3 + l * r2 * (c1 + c2);
		double a2 = m * c3 * r3 * ( c1 * ( r1 + r3) + c2 * (r3 -r4)) -
				m * m* c3 * r3 * r3 * (c1 + c2) +
				l * r2 * ( c1 * c2 * (r2 + r4) + c3 * r4 * (c1 + c2)) +
				c1 * r1 * r4 * (c1 + c3) + c1 * c2 * r3 * (r1 + r4) + c3 * r3 * r4 * (c1 + c2);
		double a3 = 0.03;
		
		double c = 2.0 * getSamplerate();
		double cSquared = c * c;
		double cCubed = cSquared * c;
		
		double bee0 = -(b1 * c) - (b2 * cSquared) - (b3 * cCubed);
		double bee1 = -(b1 * c) + (b2 * cSquared) + (3 * b3 * cCubed);
		double bee2 = (b1 * c) + (b2 * cSquared) - (3 * b3 * cCubed);
		double bee3 = (b1 * c) - (b2 * cSquared) + (b3 * cCubed);
		
		double ayy0 = -a0 - (a1 * c) - (a2 * cSquared) - (a3 * cCubed);
		double ayy1 = -(3 * a0) - (a1 * c) + (a2 * cSquared) + (3 * a3 * cCubed);
		double ayy2 = -(3 * a0) + (a1 * c) + (a2 * cSquared) - (3 * a3 * cCubed);
		
		double inputGain = 0.5;

		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int d0 = sfxb.allocateReg();
			int d1 = sfxb.allocateReg();
			int d2 = sfxb.allocateReg();
			int output = sfxb.allocateReg();
			
			sfxb.comment("Bassman 59 EQ");
			
			sfxb.scaleOffset(0, 0);
			sfxb.readRegister(input, inputGain * bee0/ayy0);

			sfxb.readRegister(d0, 1.0);
			sfxb.writeRegister(output,0);

			sfxb.readRegister(input, bee1/ayy0);
			
			sfxb.readRegister(output, -ayy1/ayy0);
			sfxb.readRegister(d1, 1.0);
			sfxb.writeRegister(d0,0);

			sfxb.readRegister(input, bee2/ayy0);
			sfxb.readRegister(output, -ayy2/ayy0);
			sfxb.writeRegister(d1,0);

			sfxb.readRegister(input, bee3/ayy0);
			sfxb.readRegister(output, -a3/ayy0);
			sfxb.writeRegister(d2,0);

			this.getPin("Audio Output 1").setRegister(output);	
		}
		System.out.println("Bassman 59 code gen!");
	}

	public double getBass() {
		return l;
	}

	public void setBass(double f) {
		l = f;
	}

	public double getMid() {
		return m;
	}

	public void setMid(double q) {
		m = q;
	}

	public void setTreble(double i) {
		t = i;
	}

	public double getTreble() {
		return t;
	}
}
