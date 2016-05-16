/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ModDelayCADBLock.java
 * Copyright (C) 2013 - 2014 - Gary Worsham 
 * SpinCAD Designer is based on ElmGen by Andrew Kilpatrick.  
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
// this code was supplied by holycityaudio.com/forum member slacker.
// http://holycityaudio.com/forum/viewtopic.php?f=31&t=1272
// First a simple delay, this is just a delay line, any mixing 
// and feedback to make an effect would be done with other blocks. 
// The maximum length can be set anywhere up to 1 second. The control 
// signal from a pot or whatever is then scaled accordingly so the range 0 - 1 
// always sweeps the whole delay line irrespective of it's length. The control 
// signal is smoothed to prevent zipper noise. 
// GSW had to adjust this so that the offset of the statement prior to RMPA
// corresponds to the delay buffer offset of this block within the model

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class StraightDelayCADBlock extends SpinCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 695539935034103396L;
	int delayLength = 8192;	// default delay length = 25% of buffer
	int delayOffset = -1;	// this is the offset due to allocations by other blocks

	public StraightDelayCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Audio Output");
		addControlInputPin(this,"Delay Time");
		setBorderColor(new Color(0x6060c4));
		setName("Straight Delay");
	}
	
	private void modDelay(SpinFXBlock sfxb, int chorusLength) {
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			int out = sfxb.allocateReg();
			
			sfxb.comment(getName());

//			;Single delay line dry only
//			equ smooth 0.00125
			double smooth = 0.00125;
//			equ del_read reg0
			int del_read = sfxb.allocateReg();
//			mem delay 32767*maxlength/1000
//			equ control pot0
		
			// delay offset is the starting location of the memory segment in this block
			delayOffset = sfxb.getDelayMemAllocated() + 1;
//			equ maxlength 1000; max length of delay in milli seconds 0 - 1000
			sfxb.FXallocDelayMem("moddel", delayLength);

//			rdax adcl,1
			sfxb.readRegister(input,  1.0);
//			wra delay,0
			sfxb.FXwriteDelay("moddel", 0, 0);
//			clr
			sfxb.clear();
//			or 32767*256
			sfxb.or(32767 * 256);
//			mulx control
			int Control1 = -1;

			p = this.getPin("Delay Time").getPinConnection();
			
			if (p != null) {
				Control1 = p.getRegister();
				sfxb.mulx(Control1);
				// only filter control input when the control input is connected!	
//				rdfx del_read, smooth
				sfxb.readRegisterFilter(del_read, smooth);
//				wrax del_read,1
				sfxb.writeRegister(del_read, 1.0);
			} else {
				// TODO add default time control input
				// since default would be full delay length, no need to scale it
				// haha but I just did anyway - or not
//				sfxb.scaleOffset(1.0, 0);
			}
//			sof maxlength/1000, buffer
			sfxb.scaleOffset((double)(delayLength/32768.0), (double) (delayOffset/32768.0));
//			wrax addr_ptr,0
			sfxb.writeRegister(ADDR_PTR, 0);
//			rmpa 1
			sfxb.readDelayPointer(1.0);
//			wrax dacl,0
			sfxb.writeRegister(out, 0);
			this.getPin("Audio Output").setRegister(out);
			System.out.println("Straight Delay code gen!");
		}
	}

	public void generateCode(SpinFXBlock sfxb) {
		/**
		 * @param sfxb
		 *            is the handle of the calling program
		 * @param input
		 *            is the input register from a previously defined block
		 */
		modDelay(sfxb, delayLength);
	}

// control panel functions
	
	public void editBlock(){
//		new StraightDelayControlPanel(this);
	}

	public int getDelayLength () {
		return delayLength;
	}

	public void setDelayLength (int l) {
		if(l <= 32767) {
			delayLength = l;
		}
	}
}
