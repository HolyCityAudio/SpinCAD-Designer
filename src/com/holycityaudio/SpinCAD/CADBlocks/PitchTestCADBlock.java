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

/*
 * Gary Worsham attempt at a pitch up and down.	
 */

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;
import com.holycityaudio.SpinCAD.SpinCADBlock;

public class PitchTestCADBlock extends SpinCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476502380095165941L;
	private int freq = 0;
	private int amp = 0;
	/**
	 * 
	 */

	public PitchTestCADBlock(int x, int y) {
		super(x, y);
		addInputPin(this, "Audio In");
		addOutputPin(this, "Pitch Out 1");
		addOutputPin(this, "Pitch Out 2");
		setName("Pitch Test");
	}
	
	public void editBlock(){
		new PitchTestControlPanel(this);
	}

	//====================================================
	public int getFreq() {
		return freq;
	}

	public void setFreq(int d) {
		freq = d;
	}
	//====================================================
	public void setAmp(int d) {
		int LFO_Freqs[] = {512, 1024, 2048, 4096};
		amp = LFO_Freqs[d];
	}

	public int getAmp() {
		return amp;
	}


	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;
		// pitchBufferSize should be 512, 1024, 2048, or 4096
		int pitchBufferSize = amp;

		SpinCADPin p = this.getPin("Audio In").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int controlInput = -1;	
			int pitch1 = sfxb.allocateReg();
			int pitch2 = sfxb.allocateReg();

			// delayd mem 4096 ; Down delay 
			sfxb.FXallocDelayMem("delayd", pitchBufferSize);
			// temp mem 1 ; Temp location for partial calculations 
			sfxb.FXallocDelayMem("temp", 1); 
			sfxb.FXallocDelayMem("temp2", 1); 
			// skp run,START
			sfxb.skip(RUN, 1);
			// wldr RMP0, 0 ,4096 
			// configure Ramp LFO 0 buffer, one octave up (16384)
			sfxb.loadRampLFO(0, freq, pitchBufferSize);
				
			// START: ldax ADCL
			sfxb.loadAccumulator(input);
			//; Write it to left delay and clear ACC 
			//wra delayd,0
			sfxb.FXwriteDelay("delayd", 0, 0.0);	
			
			//cho rda,RMP0,REG|COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP0, REG | COMPC, "delayd", 0);
			//cho rda,RMP0,,delayd+1 
			sfxb.FXchorusReadDelay(RMP0, 0, "delayd+", 1);
//			sfxb.writeRegister(pitch1,  0.0);
			// wra temp,0 
			sfxb.FXwriteDelay("temp", 0, 0.0);
			sfxb.clear();
			// cho rda,RMP0,RPTR2|COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP0, RPTR2 | COMPC, "delayd", 0);
			// cho rda,RMP0,RPTR2,delayd+1 
			sfxb.FXchorusReadDelay(RMP0, RPTR2, "delayd+", 1);
			sfxb.FXwriteDelay("temp2", 0, 0.0);
			// cho sof,RMP0,NA|COMPC,0 
//			sfxb.chorusScaleOffset(RMP0, NA | COMPC, 0);
			// cho rda,RMP0,NA,temp debug, trying to isolate the xfade aspect
			sfxb.clear();	// TODO de
			sfxb.FXchorusReadDelay(RMP0, NA, "temp", 0);
			//wrax pitch1,0
			sfxb.writeRegister(pitch1,  0.0);

			sfxb.clear();	// TODO de
//			sfxb.FXchorusReadDelay(RMP0, NA | RPTR2 | COMPA, "temp2", 0);
			//wrax pitch1,0
			sfxb.writeRegister(pitch2,  0.0);
			
			this.getPin("Pitch Out 1").setRegister(pitch1);	
			this.getPin("Pitch Out 2").setRegister(pitch2);	
		}
	}
}