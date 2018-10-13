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

import org.andrewkilpatrick.elmGen.ElmProgram;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class SingleDelayCADBlock extends SpinCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3997234959654893065L;

	// coefficients

	private static double tap0level = 0.35; // tap1 level
	private static double tap1level = 0.35; // tap1 level

	// the following tap parameters are percentage of total, to be used with
	// readDelay
	// equally spaced for 16th note resolution

	private double fbLevel = 0.05;
	private double defaultGain = 0.5;
	private double defaultFeedback = 0.45;
	private double delayFactor = 0.999;

	int delayLength = -1;
	int delayOffset = -1;	// this is the offset due to allocations by other blocks
	int delayTime = 250;	// milliseconds

	public SingleDelayCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this);
		addControlInputPin(this, "Time");	//	delay time
		addControlInputPin(this, "Feedback");	//	feedback
		addOutputPin(this);
		setBorderColor(new Color(0x6060c4));
		setName("Single Delay");
	}

	public void editBlock() {
		new SingleDelayControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		// only mono input supported
		int input;
		
		// Here we account for memory already allocated.  Since we are using RMPA
		// we have to calculate the pointer explicitly rather than using buffer names and
		// offsets, etc.
		
		delayOffset = sfxb.getDelayMemAllocated() + 1;
//		equ maxlength 1000; max length of delay in milli seconds 0 - 1000
		delayLength = (int)(((ElmProgram.getSamplerate() - 1) * delayTime)/1000.0);
		sfxb.FXallocDelayMem("moddel", delayLength);

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			int Control1 = -1;

			p = this.getPin("Feedback").getPinConnection();
			if (p == null) { // there's no pin attached!
				defaultFeedback = 0.45;
			} else {
				Control1 = p.getRegister();
			}

			int Control2 = -1;
			sfxb.comment(getName());
			
			p = this.getPin("Time").getPinConnection();
			if (p == null) { // there's no pin attached!
				sfxb.readRegister(input, defaultGain);
			} else {
				Control2 = p.getRegister();
			}
// output = sfxb.allocateReg();

			// ; Guitar Echo
			// ; HK July 2009
			// ; version 2.0
			;
			// ; mono in mono out
			// ; pot0 = feedback amount (from no feedback = slap back to infinite
			// feedback)
			// ; pot1 = delay time (50 ms to 1 second with 32 kHz x-tal)
			// ; pot2 = dry - wet mix
			;
			// ; only 20 thicks

			// ;declare memory spaces:

			// mem del 32767

			// ;declare register equates:

			// equ dout reg0
			int dout = sfxb.allocateReg();

			// equ kfbk reg1
			int feedback = sfxb.allocateReg();
//			int dry_in = sfxb.allocateReg();

			// equ dry_in reg2

			// ;get feedback value from pot0:

			// wrax kfbk,0
			sfxb.writeRegister(feedback, 0);
			//
			// rdax pot0,1
			if(Control1 == -1) {
				sfxb.scaleOffset(0, defaultFeedback);
			}
			else {
				sfxb.readRegister(Control1, defaultFeedback);
			}
			
			// wrax kfbk,0
			sfxb.writeRegister(feedback, 0);

			// ;get address pointer from pot1:

			// rdax pot1,1
			if(Control2 == -1) {
				sfxb.scaleOffset(0, delayFactor);
			}
			else {
				sfxb.readRegister(Control2, delayFactor);
			}
			// and %01111110_00000000_00000000 ;don't make jumps too small
//			sfxb.and(0b011111100000000000000000);
			// sof 61/64,3/64 ;50 ms to 1 second
			// this line was from original example
//			sfxb.scaleOffset(61 / 64.0, 3 / 64.0);
			// this compensates for the offset and length of the delay buffer
			sfxb.scaleOffset(delayTime/1000.0, (double) (delayOffset/32768.0));
			// wrax addr_ptr,0
			sfxb.writeRegister(ADDR_PTR, 0);

			// ;get output from delay:

			// rmpa 1
			sfxb.readDelayPointer(1);
			// wrax dout,0
			sfxb.writeRegister(dout, 1);

			// ;put input signals into delay, allowing for feedback:

			// rdax dout,1
			//		sfxb.readRegister(dout, 1);
			// mulx kfbk
			sfxb.mulx(feedback);
			// rdax adcl,0.5
			sfxb.readRegister(input, 0.5);
			// rdax adcr,0.5
			// wrax dry_in, 1
			//		sfxb.writeRegister(input, 1);
			// wra del,0
			sfxb.FXwriteDelay("moddel", 0, 0);
			// rdax dout,1
			sfxb.readRegister(dout, 1.0);
			// write output
			sfxb.writeRegister(dout, 0.0); // write ACC to DACL and clear ACC
			p = this.getPin("Audio Output 1");
			p.setRegister(dout);
		}
	}

	// ====================================================
	public double getfbLevel() {
		return fbLevel;
	}

	public void setfbLevel(double d) {
		fbLevel = d;
	}

	// ====================================================
	public void setDelayTime(int d) {
		delayTime = d;
	}

	public int getDelayTime() {
		return delayTime;
	}

	// ====================================================
	public void setTapLevel(int i, double value) {
		if (value < 0.0) {
			value = 0.0;
		}
		if (i == 0) {
			tap0level = value;
		} else if (i == 1) {
			tap1level = value;
		} else {
			System.err.println("Tap # out of range: " + i);
			return;
		}
	}

	public double getTapLevel(int i) {
		if (i == 0) {
			return tap0level;
		} else if (i == 1) {
			return tap1level;
		} else {
			System.err.println("Tap # out of range: " + i);
			return -1.0;
		}
	}
}
