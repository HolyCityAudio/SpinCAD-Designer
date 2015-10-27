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

public class OneBandEQCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7536335478681336314L;

	double q = 1.2;
	// band 0 -------------------------------------------------
	double fZero = 80;

	double kg0;
	double kp0;
	double kq0;
	double kf0;

	public OneBandEQCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addOutputPin(this);
		addInputPin(this);
		setName("1-Band EQ");
		setBorderColor(new Color(0x24f26f));
	}

	public void generateCode(SpinFXBlock sfxb) {
		// this code from Spin DSP crossover example by Keith Barr
		//		;EQ params will depend on driver set.

		int input;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			int eqin = sfxb.allocateReg();
			int temp = sfxb.allocateReg();

			sfxb.comment(getName());

			//		;sum inputs to temp register:

			//			rdax	adcr,0.5
			sfxb.readRegister(input, 1.0);
			//			rdax	adcl,0.5
			//			wrax	eqin,0

			//		;Equalizer to correct amplitude variations.
			//		;input to filter bank is in toeq, output will be input
			//		;plus fractions of each band filter:

			//			;EQ band 1:

			if(kp0 != 0) {
				sfxb.writeRegister(eqin, 0);

				int b0a = sfxb.allocateReg();
				int b0b = sfxb.allocateReg();

				sfxb.readRegister(eqin, kg0);
				sfxb.readRegister(b0b,-kf0);
				sfxb.readRegister(b0a,1);
				sfxb.writeRegister(temp, kq0);

				sfxb.readRegister(eqin,kg0);
				sfxb.writeRegister(b0a, 0);

				sfxb.readRegister(b0a,kf0);
				sfxb.readRegister(b0b,1);
				sfxb.writeRegister(b0b, 0);

				sfxb.readRegister(eqin,1);
				sfxb.readRegister(temp,kp0);
			}
			else {
				sfxb.writeRegister(eqin, 1);			
			}

			sfxb.writeRegister(eqin,0);
			this.getPin("Audio Output 1").setRegister(eqin);
		}
		// maybe it's better to set output register to eqin?
		// also double check case where all bands are set to 0
		System.out.println("One Band EQ code gen!");	
	}

	//-----------------------control panel functions

	public void editBlock(){
		new OneBandEQControlPanel(this);
	}
	//==============================================
	public double getEqLevel() {
		return kp0;
	}

	public void setEqLevel(double d) {
		kp0 = d;
		setCoefficients();
	}
//==============================================
	public double getQLevel() {
		return q;
	}

	public void setqLevel(double d) {
		q = d;
		setCoefficients();
	}
	//==============================================
	public double getFreq() {
		return fZero;
	}

	void setFreq(double f) {
		fZero = f;
		setCoefficients();
	}
	//==============================================
		//;Equations for setting EQ bands:
	//;kp(x) = peak/dip; range from -1 (inf notch) to +1.9999 (+6dB)
	//;where:
	//;kt=tan(pi*f/Fs)
	//;kts=kt^2
	//;f=center frequency
	//;Fs=sample rate
	//;q=Q of filter peak

	//;kf(x) = sqrt((4*kts)/(1+(kt/q)+kts))
	//;kq(x) = (1-(kt/q)+kts)/(1+(kt/q)+kts)
	//;kg(x) = (kt/q)/(1+(kt/q)+kts)

	private void setCoefficients() {
		double kt = Math.tan(Math.PI * fZero/getSamplerate());
		double kts = Math.pow(kt, 2);
		kg0 = (kt/q)/(1 + (kt/q) + kts);
		kf0 = Math.sqrt((4 * kts)/(1 + (kt/q) + kts));	
		kq0 =  (1 - (kt/q) + kts)/(1 + (kt/q) + kts);
	}
}


