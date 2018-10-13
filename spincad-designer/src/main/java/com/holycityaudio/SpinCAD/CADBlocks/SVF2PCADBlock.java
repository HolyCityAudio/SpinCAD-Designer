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

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class SVF2PCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 740.0;
	double q0 = 1;
	double fZ;
	double q1;
	
	public SVF2PCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		setBorderColor(new Color(0x24f26f));
		addInputPin(this, "Audio Input");
		addControlInputPin(this, "Frequency");
		addOutputPin(this, "Lowpass Out");
		addOutputPin(this, "Bandpass Out");
		addOutputPin(this, "Hipass Out");
		setCoefficients();
		setName("SVF 2P");	
	}

	public void editBlock(){
		new SVF2PControlPanel(this);
	}	

	public void generateCode(SpinFXBlock sfxb) {
		// coefficients
		int input = -1;

		SpinCADPin p = this.getPin("Audio Input").getPinConnection();

		if(p != null) {
			setCoefficients();
			input = p.getRegister();

			int highPass = sfxb.allocateReg();
			int bandPass = sfxb.allocateReg();
			int lowPass = sfxb.allocateReg();
			
			sfxb.comment(getName());

			sfxb.scaleOffset(0, 0);
	
			sfxb.readRegister(input, 1.0);
			sfxb.readRegister(lowPass,  -1);

			sfxb.readRegister(bandPass,  -q1);

			sfxb.writeRegister(highPass, fZ);
			
			SpinCADPin p2 = this.getPin("Frequency").getPinConnection();

			if(p2 != null) {
				sfxb.mulx(p2.getRegister());
			}

			sfxb.readRegister(bandPass, 1.0);
			sfxb.writeRegister(bandPass, fZ);
			if(p2 != null) {
				sfxb.mulx(p2.getRegister());
			}
			
			sfxb.readRegister(lowPass,  1);
			sfxb.writeRegister(lowPass, 0);
			this.getPin("Lowpass Out").setRegister(lowPass);	
			this.getPin("Bandpass Out").setRegister(bandPass);	
			this.getPin("Hipass Out").setRegister(highPass);	
		}
		System.out.println("SVF 2P code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
//		setCoefficients();
	}

	public void setQ(double value) {
		q0 = value;
//		setCoefficients();
	}

	public double getQ() {
		return q0;
	}
	
	public void setCoefficients() {
		q1 = 1.0/q0;
		fZ = Math.sin(2 * Math.PI * f0/getSamplerate());
	}
}
