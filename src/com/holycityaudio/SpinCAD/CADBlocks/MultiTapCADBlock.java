/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Copyright (C)2013 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
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


public class MultiTapCADBlock extends DelayCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3997234959654893065L;
	private int sum_of_taps = -1;
	@SuppressWarnings("unused")
	private int filt = -1;
	// coefficients

	private static double tap0level = 0.35;	// tap1 level
	private static double tap1level = 0.35;	// tap1 level
	private static double tap2level = 0.35;	// tap2 level
	private static double tap3level = 0.35;	// tap3 level
	private static double tap4level = 0.051;	// tap4 level
	private static double tap5level = 0.051;	// tap5 level
	private static double tap6level = 0.051;	// tap6 level

	private static double tap7level = 0.35;	// tap2 level

	// the following tap parameters are percentage of total, to be used with readDelay
	// equally spaced for 16th note resolution
	private double tap0 = (1/8.0);
	private double tap1 = (2/8.0);
	private double tap2 = (3/8.0);
	private double tap3 = (4/8.0);
	private double tap4 = (5/8.0);
	private double tap5 = (6/8.0);
	private double tap6 = (7/8.0);
	private double tap7 = (8/8.0);

	private double length = 0.9;
	private double fbLevel = 0.05;
	private double defaultGain = 0.5;
	private double delayLineGain = 0.85;

	public MultiTapCADBlock(int x, int y) {
		//		super("MultiTap");
		super(x, y);
		addControlInputPin(this, "Delay Gain");
		setName("MultiTap");
	}

	public void editBlock(){
		new MultiTapControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		// only mono input supported
		int input;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if(p != null) {
			input = p.getRegister();

			int Control1 = -1;

			p = this.getPin("Delay Gain").getPinConnection();

			sfxb.comment(getName());

			if(p == null) {	// there's no pin attached!
				sfxb.readRegister(input, defaultGain);
			}
			else {
				Control1 = p.getRegister();
				sfxb.readRegister(input, 1.0);
				sfxb.mulx(Control1);
			}

			sfxb.FXallocDelayMem("Multidelay", (int)(length * getSamplerate() + 1));

			sum_of_taps = sfxb.allocateReg();
			filt = sfxb.allocateReg();
			int leftOut = sfxb.allocateReg();
			int feedback = sfxb.allocateReg();

			// read left and right input
			sfxb.readRegister(input, 0.45);  // read left 55%
			sfxb.readRegister(feedback, fbLevel);  // sum of tap delays
			//	writeRegister(REG1, 1.0);	// write input to REG1, scale ACC by 1.0  REG1 = INPUT+FB (not used currently)
			// low pass filter
			//		sfxb.readRegisterFilter(filt, 1.25);
			//		sfxb.writeRegisterHighshelf(filt, 1.0);
			// write to delay line
			sfxb.FXwriteDelay("Multidelay", 0, delayLineGain);
			sfxb.FXreadDelay("Multidelay", tap0, tap0level);  //
			sfxb.FXreadDelay("Multidelay", tap1, tap1level);  //
			sfxb.FXreadDelay("Multidelay", tap2, tap2level);  //
			sfxb.FXreadDelay("Multidelay", tap3, tap3level);  //
			sfxb.FXreadDelay("Multidelay", tap4, tap4level);  //
			sfxb.FXreadDelay("Multidelay", tap5, tap5level);  //
			sfxb.FXreadDelay("Multidelay", tap6, tap6level);  //
			sfxb.FXreadDelay("Multidelay", tap7, tap7level);  //

			sfxb.writeRegister(sum_of_taps, 0.0);	// write sum of taps to REG3, REG3 = SUM
			sfxb.FXreadDelay("Multidelay", tap7, 1.0);  //
			sfxb.writeRegister(feedback,  0);

			// write output
			if(Control1 == -1)	{ // nothing connected to control input
				sfxb.readRegister(sum_of_taps, defaultGain);
			}
			else {
				sfxb.readRegister(sum_of_taps, 1.0);
				sfxb.mulx(Control1);			
			}

			sfxb.writeRegister(leftOut, 0.0);	// write ACC to DACL and clear ACC
			p = this.getPin("Audio Output 1");
			p.setRegister(leftOut);
		}
	}
	//====================================================
	public double getfbLevel() {
		// TODO Auto-generated method stub
		return fbLevel;
	}

	public void setfbLevel(double d) {
		// TODO Auto-generated method stub
		fbLevel = d;
	}
	//====================================================
	public void setDelayGain(double d) {
		// TODO Auto-generated method stub
		delayLineGain = d;
	}

	public double getDelayGain() {
		// TODO Auto-generated method stub
		return delayLineGain;
	}

	//====================================================
	public void setLength(double d) {
		// TODO Auto-generated method stub
		length = d;
	}

	public double getLength() {
		// TODO Auto-generated method stub
		return length;
	}

	//====================================================
	public void setTapLevel(int i, double value) {
		// TODO Auto-generated method stub
		if(value < 0.0) {
			value = 0.0;
		}
		if(i == 0) {
			tap0level = value;
		}
		else if(i == 1) {
			tap1level = value;
		}
		else if(i == 2) {
			tap2level = value;
		}
		else if(i == 3) {
			tap3level = value;
		}
		else if(i == 4) {
			tap4level = value;
		}
		else if(i == 5) {
			tap5level = value;
		}
		else if(i == 6) {
			tap6level = value;
		}
		else if(i == 7) {
			tap7level = value;
		}
		else {
			System.err.println("Tap # out of range: " + i);
			return;
		}	
	}

	public double getTapLevel(int i) {
		// TODO Auto-generated method stub
		if(i == 0) {
			return tap0level;
		}
		else if(i == 1) {
			return tap1level;
		}
		else if(i == 2) {
			return tap2level;
		}
		else if(i == 3) {
			return tap3level;
		}
		else if(i == 4) {
			return tap4level;
		}
		else if(i == 5) {
			return tap5level;
		}
		else if(i == 6) {
			return tap6level;
		}
		else if(i == 7) {
			return tap7level;
		}
		else {
			System.err.println("Tap # out of range: " + i);
			return -1.0;
		}
	}
}
