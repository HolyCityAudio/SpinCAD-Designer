/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ModDelayCADBLock.java
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

public class StraightDelayCADBlock extends ModulationCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 695539935034103396L;
	int delayLength = -1;
	int delayTime = 250;

	public StraightDelayCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		// editPanel.add();
		addControlInputPin(this,"Modulation In");
		setName("Straight Delay");
	}
	
	private void modDelay(SpinFXBlock sfxb, int chorusLength) {
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			int out = sfxb.allocateReg();
			
			int Control1 = -1;

			p = this.getPin("Modulation In").getPinConnection();
			if (p != null) {
				Control1 = p.getRegister();
			}
			sfxb.comment(getName());

//			;Single delay line dry only
//			equ smooth 0.00125
			double smooth = 0.00125;
//			equ del_read reg0
			int del_read = sfxb.allocateReg();
//			mem delay 32767*maxlength/1000
//			equ control pot0
		
//			equ maxlength 1000; max length of delay in milli seconds 0 - 1000
			delayLength = (int)((sfxb.getSamplerate() * delayTime)/1000.0);
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
			sfxb.mulx(Control1);
//			sof maxlength/1000,0
			sfxb.scaleOffset(delayTime/1000.0, 0);
//			rdfx del_read, smooth
			sfxb.readRegisterFilter(del_read, smooth);
//			wrax del_read,1
			sfxb.writeRegister(del_read, 1.0);
//			wrax addr_ptr,0
			sfxb.writeRegister(ADDR_PTR, 0);
//			rmpa 1
			sfxb.readDelayPointer(1.0);
//			wrax dacl,0
			sfxb.writeRegister(out, 0);
			this.getPin("Audio Output 1").setRegister(out);
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
		new StraightDelayControlPanel(this);
	}

	public int getDelayTime () {
		return delayTime;
	}

	public void setDelayTime (int l) {
		delayTime = l;
	}
}
