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

public class SVF2PCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 240;
	double q0 = 2;
	double fZ;
	double q1;
	
	public SVF2PCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addOutputPin(this);
		addOutputPin(this);
		setName("SVF 2P");	}

	public void editBlock(){
		new SVF2PControlPanel(this);
	}	

	public void generateCode(SpinFXBlock sfxb) {
		// coefficients
		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();

//			equ HP reg0
			int highPass = sfxb.allocateReg();
//			equ BP reg1 
			int bandPass = sfxb.allocateReg();
//			equ LP reg2 
			int lowPass = sfxb.allocateReg();
			
			sfxb.comment(getName());

//			sof 0,0
			sfxb.scaleOffset(0, 0);
			
//			ldax adcl ; read ADCL 
			sfxb.readRegister(input, 1.0);
			//			rdax LP, -1 ; -LP 
			sfxb.readRegister(lowPass,  -1);
			//			rdax BP, -1 ; -BP 
			sfxb.readRegister(bandPass,  -q1);
//			wrax HP, 0.1 ; Write high pass to HP, multiply acc by 0.1 
			sfxb.writeRegister(highPass, fZ);
			//			rdax BP, 1.0 ; add BP 
			sfxb.readRegister(bandPass, 1.0);
//			wrax BP, 0.1 ; Write band pass to ;BP, multiply acc by 0.1. 
			sfxb.writeRegister(bandPass, fZ);
//			rdax LP, 1.0 ; add LP 
			sfxb.readRegister(lowPass,  1);
//			wrax LP, 0 ; Write low pass to LP, acc x 0 
			sfxb.writeRegister(lowPass, fZ);
//			ldax HP ; read HP to ACC 
//			wrax dacl, 0 ; write to left and clear ACC 
//			ldax LP ; read LP to ACC 
//			wrax dacr, 0 ; write ro right anc clear ACC 
			this.getPin("Audio Output 1").setRegister(lowPass);	
			this.getPin("Audio Output 2").setRegister(bandPass);	
			this.getPin("Audio Output 3").setRegister(highPass);	
		}
		System.out.println("SVF code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
		setCoefficients();
	}

	public void setQ(double value) {
		q0 = value;
		setCoefficients();
	}

	public double getQ() {
		return q0;
	}
	
	public void setCoefficients() {
		q1 = 1.0/q0;
		fZ = 2 * Math.PI * Math.sin(f0/getSamplerate());
	}
}
