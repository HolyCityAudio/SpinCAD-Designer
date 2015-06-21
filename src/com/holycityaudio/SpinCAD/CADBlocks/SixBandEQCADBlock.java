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

public class SixBandEQCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7536335478681336314L;

	double q = 1.2;

	// band 0 -------------------------------------------------
	double fZero0 = 80;
	double q0 = q;

	double kf0 = setFreq(fZero0, q0);
	double kq0 = setQ(fZero0, q0);
	double kp0 = 0.250;
	double kg0 = setGain(fZero0, q0);

	// band 1 -------------------------------------------------
	double fZero1 = 160;
	double q1 = q;

	double kf1 = setFreq(fZero1, q1);
	double kq1 = setQ(fZero1, q1);
	double kp1 = 0.10;
	double kg1 = setGain(fZero1, q1);

	// band 2 -------------------------------------------------
	double fZero2 = 320;
	double q2 = q;

	double kf2 = setFreq(fZero2, q2);
	double kq2 = setQ(fZero2, q2);
	double kp2 = 0.25;
	double kg2 = setGain(fZero2, q2);

	// band 3 -------------------------------------------------
	double fZero3 = 640;
	double q3 = q;

	double kf3 = setFreq(fZero3, q3);
	double kq3 = setQ(fZero3, q3);
	double kp3 = 0.750;
	double kg3 = setGain(fZero3, q3);

	// band 4 -------------------------------------------------
	double fZero4 = 1280;
	double q4 = q;

	double kf4 = setFreq(fZero4, q4);
	double kq4 = setQ(fZero4, q4);
	double kp4 = 0.990;
	double kg4 = setGain(fZero4, q4);

	// band 5 -------------------------------------------------
	double fZero5 = 2560;
	double q5 = q;

	double kf5 = setFreq(fZero5, q5);
	double kq5 = setQ(fZero5, q5);
	double kp5 = 1.990;
	double kg5 = setGain(fZero5, q5);

	enum filterType { LOWPASS, BANDPASS, HIGHPASS };

	public SixBandEQCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addOutputPin(this);
		addInputPin(this);
		setName("6-Band EQ");
		setBorderColor(new Color(0x24f26f));
	}

	public void generateCode(SpinFXBlock sfxb) {
		// this code from Spin DSP crossover example by Keith Barr
		//		;EQ params will depend on driver set.

		int input;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();

			int output = sfxb.allocateReg();

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

			if(kp1 != 0) {
				//equ	b1a	reg11
				int b1a = sfxb.allocateReg();
				//equ	b1b	reg12
				int b1b = sfxb.allocateReg();
				//			rdax	eqin,kg1

				sfxb.writeRegister(eqin,kg1);
				//			rdax	b1b,-kf1
				sfxb.readRegister(b1b,-kf1);
				//			rdax	b1a,1
				sfxb.readRegister(b1a,1);
				//			wrax	temp,kq1
				sfxb.writeRegister(temp, kq1);
				//			rdax	eqin,kg1
				sfxb.readRegister(eqin,kg1);
				//			wrax	b1a,0
				sfxb.writeRegister(b1a, 0);
				//			rdax	b1a,kf1
				sfxb.readRegister(b1a,kf1);
				//			rdax	b1b,1
				sfxb.readRegister(b1b,1);
				//			wrax	b1b,0
				sfxb.writeRegister(b1b, 0);
				//			rdax	eqin,1
				sfxb.readRegister(eqin,1);
				//			rdax	temp,kp1
				sfxb.readRegister(temp,kp1);
				//			wrax	eqin,kg2
			}

			if(kp2 != 0) {
				//equ	b2a	reg13
				int b2a = sfxb.allocateReg();
				//equ	b2b	reg14
				int b2b = sfxb.allocateReg();

				sfxb.writeRegister(eqin, kg2);
				sfxb.readRegister(b2b,-kf2);
				sfxb.readRegister(b2a,1);
				sfxb.writeRegister(temp, kq2);
				sfxb.readRegister(eqin,kg2);
				sfxb.writeRegister(b2a, 0);
				sfxb.readRegister(b2a,kf2);
				sfxb.readRegister(b2b,1);
				sfxb.writeRegister(b2b, 0);
				sfxb.readRegister(eqin,1);
				sfxb.readRegister(temp,kp2);
			}

			if(kp3 != 0) {
				int b3a = sfxb.allocateReg();
				int b3b = sfxb.allocateReg();

				sfxb.writeRegister(eqin,kg3);
				sfxb.readRegister(b3b,-kf3);
				sfxb.readRegister(b3a,1);
				sfxb.writeRegister(temp, kq3);
				sfxb.readRegister(eqin,kg3);
				sfxb.writeRegister(b3a, 0);
				sfxb.readRegister(b3a,kf3);
				sfxb.readRegister(b3b,1);
				sfxb.writeRegister(b3b, 0);
				sfxb.readRegister(eqin,1);
				sfxb.readRegister(temp,kp3);
			}

			if(kp4 != 0) {
				int b4a = sfxb.allocateReg();
				int b4b = sfxb.allocateReg();

				sfxb.writeRegister(eqin,kg4);
				sfxb.readRegister(b4b,-kf4);
				sfxb.readRegister(b4a,1);
				sfxb.writeRegister(temp, kq4);
				sfxb.readRegister(eqin,kg4);
				sfxb.writeRegister(b4a, 0);
				sfxb.readRegister(b4a,kf4);
				sfxb.readRegister(b4b,1);
				sfxb.writeRegister(b4b, 0);
				sfxb.readRegister(eqin,1);
				sfxb.readRegister(temp,kp4);
			}

			if(kp5 != 0) {
				int b5a = sfxb.allocateReg();
				int b5b = sfxb.allocateReg();

				sfxb.writeRegister(eqin,kg5);
				sfxb.readRegister(b5b,-kf5);
				sfxb.readRegister(b5a,1);
				sfxb.writeRegister(temp, kq5);
				sfxb.readRegister(eqin,kg5);
				sfxb.writeRegister(b5a, 0);
				sfxb.readRegister(b5a,kf4);
				sfxb.readRegister(b5b,1);
				sfxb.writeRegister(b5b, 0);
				sfxb.readRegister(eqin,1);
				sfxb.readRegister(temp,kp5);
			}
			sfxb.writeRegister(eqin,1);
			sfxb.writeRegister(output, 0);
			this.getPin("Audio Output 1").setRegister(output);
		}
		// maybe it's better to set output register to eqin?
		// also double check case where all bands are set to 0
		System.out.println("Filter EQ code gen!");	
	}

	//;Equations for setting EQ bands:

	//;kp(x) = peak/dip; range from -1 (inf notch) to +1.9999 (+6dB)

	//;where:
	//;kt=tan(pi*f/Fs)
	//;kts=kt^2
	//;f=center frequency
	//;Fs=sample rate
	//;q=Q of filter peak

	//;kf(x) = sqrt((4*kts)/(1+(kt/q)+kts))
	double setFreq(double fZero, double q) {
		double kt = Math.tan(Math.PI * fZero/getSamplerate());
		double kts = Math.pow(kt, 2);
		return Math.sqrt((4 * kts)/(1 + (kt/q) + kts));	
	}

	double setFreq(double fZero) {
		double q = 1.0;
		double kt = Math.tan(Math.PI * fZero/getSamplerate());
		double kts = Math.pow(kt, 2);
		return Math.sqrt((4 * kts)/(1 + (kt/q) + kts));	
	}

	//;kq(x) = (1-(kt/q)+kts)/(1+(kt/q)+kts)
	double setQ(double fZero, double q) {
		double kt = Math.tan(Math.PI * fZero/getSamplerate());
		double kts = Math.pow(kt, 2);
		return (1 - (kt/q) + kts)/(1 + (kt/q) + kts);
	}

	double setQ(double fZero) {
		double q = 1.0;
		double kt = Math.tan(Math.PI * fZero/getSamplerate());
		double kts = Math.pow(kt, 2);
		return (1 - (kt/q) + kts)/(1 + (kt/q) + kts);
	}

	//;kg(x) = (kt/q)/(1+(kt/q)+kts)
	double setGain(double fZero, double q) {
		double kt = Math.tan(Math.PI * fZero/getSamplerate());
		double kts = Math.pow(kt, 2);
		return (kt/q)/(1 + (kt/q) + kts);
	}


	//-----------------------control panel functions

	public void editBlock(){
		new SixBandEQControlPanel(this);
	}

	public double geteqLevel(int i) {
		if(i == 0) {
			return kp0;
		} else if(i == 1) {
			return kp1;
		} else if(i == 2) {
			return kp2;
		}
		else 		if(i == 3) {
			return kp3;

		} else 		if(i == 4) {
			return kp4;
		}
		else 		if(i == 5) {
			return kp5;
		}
		return 0;
	}

	public double getQLevel() {
		return q;
	}
	public double getFreq() {
		return kf1;
	}

	public double getRes() {
		return kq1;
	}

	public void seteqLevel(int i, double d) {
		if(i == 0) {
			kp0 = d;
		} else if(i == 1) {
			kp1 = d;
		} else if(i == 2) {
			kp2 = d;
		}
		else 		if(i == 3) {
			kp3 = d;

		} else 		if(i == 4) {
			kp4 = d;
		}
		else 		if(i == 5) {
			kp5 = d;
		}
	}

	public void setqLevel(double d) {
		q = d;
	}
}


