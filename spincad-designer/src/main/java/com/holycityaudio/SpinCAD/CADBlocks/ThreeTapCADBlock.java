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


public class ThreeTapCADBlock extends DelayCADBlock{

	/**
	 * 
	Control 1, if connected, controls gain of input before going to delay line. 
	If Control 1 not connected, input comes in at 100% gain.
	Control 2, if connected, controls delay line length, from 25% of max to 100% of max
	If Control 2 not connected, delay length is 100%.
	Control 3, if connected, controls feedback.
	If Control 3 not connected, delay feedback s 100% of control panel setting.
	 */

	private static final long serialVersionUID = 3997234959654893065L;
	private static double tap1level = 0.65;	// tap1 level
	private static double tap2level = 0.65;	// tap1 level
	private static double tap3level = 0.65;	// tap1 level

	// the following tap parameters are percentage of total, to be used with readDelay
	// equally spaced for ping pong
	private double tap1time = 0.0;
	private double tap2time = 0.0;
	private double tap3time = 0.0;

	private double length = 0.9;
	private double fbLevel = 0.05;
	private double defaultGain = 1.0;
	private double delayLineGain = 0.85;
	private double delayFactor = 1.0;

	public ThreeTapCADBlock(int x, int y) {
		//		super("MultiTap");
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this, "Delay gain");	//	delay time
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Delay End Out");
		addOutputPin(this, "Tap 1 Out");
		addOutputPin(this, "Tap 2 Out");
		addOutputPin(this, "Tap 3 Out");
		addControlInputPin(this,"Delay Time 1");
		addControlInputPin(this,"Delay Time 2");
		addControlInputPin(this,"Delay Time 3");
		setName("3-Tap Delay");
	}

	public void editBlock(){
		new ThreeTapControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int input = -1; 
		int leftOut = -1;
		int rightOut = -1;


		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			if(input != -1) {		// don't generate code if there's no input connected
				sfxb.FXallocDelayMem("PingPongDelay", (int)(length * getSamplerate() + 1));
				sfxb.readRegister(input, defaultGain);
				sfxb.comment(getName());

				sfxb.readRegister(rightOut, fbLevel);  // feedback from end

				sfxb.FXwriteDelay("PingPongDelay", 0, delayLineGain);
				// rdax pot2,1
				int Control2 = -1;

				p = this.getPin("Control Input 2").getPinConnection();
				if(p == null) {	// there's no pin attached!
					sfxb.readRegister(input, defaultGain);
				}
				else {
					Control2 = p.getRegister();
				}
				// Control Input 1 scales input level to delay line.
				p = this.getPin("Delay Time 1").getPinConnection();
				int control1 = -1;
				if(p == null) {	// there's no pin attached!
					// use control panel setting for delay
				}
				else {
					control1 = p.getRegister();
					sfxb.readRegister(input, 1.0);
					sfxb.mulx(control1);
				}
				int output1 = sfxb.allocateReg();
				sfxb.FXreadDelay("PingPongDelay", tap1time, tap1level);  //
				sfxb.writeRegister(output1, 0);

				int output2 = sfxb.allocateReg();
				sfxb.FXreadDelay("PingPongDelay", tap2time, tap2level);  //
				sfxb.writeRegister(output2, 0);

				int output3 = sfxb.allocateReg();
				sfxb.FXreadDelay("PingPongDelay", tap2time, tap2level);  //
				sfxb.writeRegister(output3, 0);

				p = this.getPin("Audio Output 1");
				p.setRegister(leftOut);
				p = this.getPin("Audio Output 2");
				p.setRegister(rightOut);

			}
		}

	}
	//====================================================
	public double getfbLevel() {
		// ---
		return fbLevel;
	}

	public void setfbLevel(double d) {
		// ---
		fbLevel = d;
	}
	//====================================================
	public void setDelayGain(double d) {
		// ---
		delayLineGain = d;
	}

	public double getDelayGain() {
		// ---
		return delayLineGain;
	}

	//====================================================
	public void setLength(double d) {
		// ---
		length = d;
	}

	public double getLength() {
		// ---
		return length;
	}

	//====================================================
	public void setTapLevel(int i, double value) {
		// ---
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
		else {
			System.err.println("Tap # out of range: " + i);
			return;
		}	
	}

	public double getTapLevel(int i) {
		// ---
		if(i == 0) {
			return tap1level;
		}
		else if(i == 1) {
			return tap2level;
		}
		else if(i == 2) {
			return tap3level;
		}
		else {
			System.err.println("Tap # out of range: " + i);
			return -1.0;
		}
	}
}
