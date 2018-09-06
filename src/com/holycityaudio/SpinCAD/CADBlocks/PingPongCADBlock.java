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


public class PingPongCADBlock extends DelayCADBlock{

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
	private static double tap0level = 0.65;	// tap1 level
	private static double tap1level = 0.65;	// tap1 level

	// the following tap parameters are percentage of total, to be used with readDelay
	// equally spaced for ping pong
	private double tap0 = (4/8.0);
	private double tap1 = (8/8.0);

	private double length = 0.9;
	private double fbLevel = 0.05;
	private double defaultGain = 1.0;
	private double delayLineGain = 0.85;

	public PingPongCADBlock(int x, int y) {
		//		super("MultiTap");
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this, "Delay gain");	//	delay time
		addControlInputPin(this, "Not assigned");	//	feedback
		addOutputPin(this);
		setName("Ping Pong");
	}

	public void editBlock(){
		new PingPongControlPanel(this);
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
				int Control1 = -1;
				sfxb.comment(getName());

				// Control Input 1 scales input level to delay line.
				p = this.getPin("Control Input 1").getPinConnection();
				if(p == null) {	// there's no pin attached!
					sfxb.readRegister(input, defaultGain);
				}
				else {
					Control1 = p.getRegister();
					sfxb.readRegister(input, 1.0);
					sfxb.mulx(Control1);
				}

				leftOut = sfxb.allocateReg();
				rightOut = sfxb.allocateReg();

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
/*				
				if(Control2 == -1) {
					sfxb.scaleOffset(0, delayFactor);
				}
				else {
					sfxb.readRegister(Control2, 1);
				}
				// and %01111110_00000000_00000000 ;don't make jumps too small
				sfxb.and(0b011111100000000000000000);
				// sof 61/64,3/64 ;50 ms to 1 second
				sfxb.scaleOffset(61 / 64.0, 3 / 64.0);
				// wrax addr_ptr,0
				sfxb.writeRegister(ADDR_PTR, 0);

				// ;get output from delay:

				// rmpa 1
				sfxb.readDelayPointer(1);
*/				
				sfxb.FXreadDelay("PingPongDelay", tap0, tap0level);  //
				sfxb.writeRegister(leftOut, 0);
				sfxb.FXreadDelay("PingPongDelay", tap1, tap1level);  //
				sfxb.writeRegister(rightOut, 0);

				// write output
				if(Control1 == -1)	{ // nothing connected to control input
					sfxb.readRegister(rightOut, defaultGain);
				}
				else {
					sfxb.readRegister(rightOut, 1.0);
					sfxb.mulx(Control1);			
				}
				//				sfxb.readRegister(input_dry, 1.0);

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
			tap0level = value;
		}
		else if(i == 1) {
			tap1level = value;
		}
		else {
			System.err.println("Tap # out of range: " + i);
			return;
		}	
	}

	public double getTapLevel(int i) {
		// ---
		if(i == 0) {
			return tap0level;
		}
		else if(i == 1) {
			return tap1level;
		}
		else {
			System.err.println("Tap # out of range: " + i);
			return -1.0;
		}
	}
}
