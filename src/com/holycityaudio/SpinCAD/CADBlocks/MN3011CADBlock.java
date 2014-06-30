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

public class MN3011CADBlock extends DelayCADBlock {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4339890908289994143L;
	private int feedback = -1;
	private int filt = -1;
	int output = -1;

	// coefficients
	private static double tap1level = 0.35;	// tap1 level
	private static double tap2level = 0.35;	// tap2 level
	private static double tap3level = 0.35;	// tap3 level
	private static double tap4level = 0.051;	// tap4 level
	private static double tap5level = 0.051;	// tap5 level
	private static double tap6level = 0.051;	// tap6 level

	// the following tap parameters are percentage of total, to be used with readDelay
	// these are based on the MN3011 BBD
	private double tap1 = (396/3328.0);
	private double tap2 = (662/3328.0);
	private double tap3 = (1194/3328.0);
	private double tap4 = (1726/3328.0);
	private double tap5 = (2790/3328.0);
	private double tap6 = (3328/3328.0);

	private double length = 0.3;
	private double fbLevel = 0.25;
	private double defaultGain = 1.0;
	private double delayLineGain = 0.75;

	public MN3011CADBlock(int x, int y) {
		//		super("MN3011");
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this);
		setName("MN3011");
	}

	public void editBlock(){
		new MN3011ControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		// only mono input supported
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if(p != null) {
			input = p.getRegister();

			int Control1 = -1;

			p = this.getPin("Control Input 1").getPinConnection();
			sfxb.comment(getName());
			if(p == null) {	// there's no pin attached!
				sfxb.readRegister(input, defaultGain);
			}
			else {
				Control1 = p.getRegister();
				sfxb.readRegister(input, 1.0);
				sfxb.mulx(Control1);
			}

			sfxb.FXallocDelayMem("3011delay", (int)(length * getSamplerate() + 1));

			feedback = sfxb.allocateReg();
			//		input_dry = sfxb.allocateReg();
			filt = sfxb.allocateReg();
			output = sfxb.allocateReg();

			// read left and right input
			sfxb.readRegister(input, 0.45);  // read left 55%
			//		sfxb.writeRegister(input_dry, 1.0);
			sfxb.readRegister(feedback, fbLevel);  // feedback from last tap only
			//	writeRegister(REG1, 1.0);	// write input to REG1, scale ACC by 1.0  REG1 = INPUT+FB (not used currently)
			// low pass filter
			sfxb.readRegisterFilter(filt, 1.25);
			sfxb.writeRegisterHighshelf(filt, 1.0);
			// write to delay line
			sfxb.FXwriteDelay("3011delay", 0, delayLineGain);
			sfxb.FXreadDelay("3011delay", tap1, tap1level);  //
			sfxb.FXreadDelay("3011delay", tap2, tap2level);  //
			sfxb.FXreadDelay("3011delay", tap3, tap3level);  //
			sfxb.FXreadDelay("3011delay", tap4, tap4level);  //
			sfxb.FXreadDelay("3011delay", tap5, tap5level);  //
			sfxb.FXreadDelay("3011delay", tap6, tap6level);  //

			sfxb.writeRegister(output, 0.0);	// write sum of taps to output

			sfxb.FXreadDelay("3011delay", tap6, fbLevel);  //
			sfxb.writeRegister(feedback, 0.0);	// feedback taken from last tap

			// write output
			if(Control1 == -1)	{ // nothing connected to control input
				sfxb.readRegister(output, defaultGain);
			}
			else {
				sfxb.readRegister(output, 1.0);
				sfxb.mulx(Control1);			
			}

			sfxb.writeRegister(output, 0.0);	// write ACC to DACL and clear ACC
			p = this.getPin("Audio Output 1");
			p.setRegister(output);
		}
	}

	public double getfbLevel() {
		return fbLevel;
	}

	public double getLength() {
		return length;
	}

	public double getTapLevel(int i) {
		if(i == 0) {
			return tap1level;
		}
		else if(i == 1) {
			return tap2level;
		}
		else if(i == 2) {
			return tap3level;
		}
		else if(i == 3) {
			return tap4level;
		}
		else if(i == 4) {
			return tap5level;
		}
		else if(i == 5) {
			return tap6level;
		}
		else {
			System.err.println("Tap # out of range: " + i);
			return -1.0;
		}
	}

	public void setfbLevel(double d) {
		fbLevel = d;
	}

	public void setDelayGain(double d) {
		delayLineGain = d;
	}

	public double getDelayGain() {
		return delayLineGain;
	}

	public void setLength(double d) {
		length = d;
	}

	public void setTapLevel(int i, double value) {
		if(value < 0.0) {
			value = 0.0;
		}
		if(i == 0) {
			tap1level = value;
		}
		else if(i == 1) {
			tap2level = value;
		}
		else if(i == 2) {
			tap3level = value;
		}
		else if(i == 3) {
			tap4level = value;
		}
		else if(i == 4) {
			tap5level = value;
		}
		else if(i == 5) {
			tap6level = value;
		}
		else {
			System.err.println("Tap # out of range: " + i);
			return;
		}	
	}
}

