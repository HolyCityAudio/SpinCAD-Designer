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

public class PitchShiftFixedCADBlock extends SpinCADBlock {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3476502380095165941L;
	private int freq = 0;
	private int amp = 0;
	private int lfoSel = 0;
	/**
	 * 
	 */

	public PitchShiftFixedCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio In");
		addOutputPin(this, "Pitch Out 1");
		setName("Pitch Shift " + lfoSel);
	}
	
	public void editBlock(){
		new PitchShiftFixedControlPanel(this);
	}

	//====================================================
	// freq parameter is the shift in semitones,  This needs to get converted to 
	// the proper pitch shift coefficient inline during code generation
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
		int coefficient = 0;
		double octaves = 0.0;
		
		// pitchBufferSize should be 512, 1024, 2048, or 4096
		int pitchBufferSize = amp;
		sfxb.comment(getName());
		SpinCADPin p = this.getPin("Audio In").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int pitch1 = sfxb.allocateReg();
			int pitch2 = sfxb.allocateReg();

			// delayd mem 4096 ; Down delay 
			sfxb.FXallocDelayMem("delayd", pitchBufferSize);
			// temp mem 1 ; Temp location for partial calculations 
			sfxb.FXallocDelayMem("temp", 1); 
			sfxb.FXallocDelayMem("temp2", 1); 
			
			octaves = (double) freq/12.0;
			
			if(freq > 0) {
				coefficient = (int) (16384 * (Math.pow(2.0, octaves - 1.0)) - 1);
			} else if (freq < 0) {
				
			} else {
				coefficient = 0;
			}
			
			// skp run,START
			sfxb.skip(RUN, 1);
			// configure Ramp LFO 0 buffer, per semitone setting
			// freq below is pitch shift in octaves.  
			// for pitch shift UP, coefficient = 2^14 * (2^freq - 1) - which makes no sense
			// wldr RMP0, 0 ,4096 
			sfxb.loadRampLFO(lfoSel, coefficient, pitchBufferSize);
				
			// START: ldax ADCL
			sfxb.loadAccumulator(input);
			//; Write it to left delay and clear ACC 
			//wra delayd,0
			sfxb.FXwriteDelay("delayd", 0, 0.0);	
			
			int lfoFlag = -1;
			if(lfoSel == 0) {
				lfoFlag = RMP0;
			} else
			{
				lfoFlag = RMP1;
			}
			//cho rda,RMP0,REG|COMPC,delayd 
			sfxb.FXchorusReadDelay(lfoFlag, REG | COMPC, "delayd", 0);
			//cho rda,RMP0,,delayd+1 
			sfxb.FXchorusReadDelay(lfoFlag, 0, "delayd+", 1);
//			sfxb.writeRegister(pitch1,  0.0);
			// wra temp,0 
			sfxb.FXwriteDelay("temp", 0, 0.0);
			sfxb.clear();
			// cho rda,RMP0,RPTR2|COMPC,delayd 
			sfxb.FXchorusReadDelay(lfoFlag, RPTR2 | COMPC, "delayd", 0);
			// cho rda,RMP0,RPTR2,delayd+1 
			sfxb.FXchorusReadDelay(lfoFlag, RPTR2, "delayd+", 1);
			sfxb.FXwriteDelay("temp2", 0, 0.0);
			// cho sof,RMP0,NA|COMPC,0 
//			sfxb.chorusScaleOffset(RMP0, NA | COMPC, 0);
			// cho rda,RMP0,NA,temp debug, trying to isolate the xfade aspect
			sfxb.clear();	// TODO de
			sfxb.FXchorusReadDelay(lfoFlag, NA, "temp", 0);
			//wrax pitch1,0
			sfxb.writeRegister(pitch1,  0.0);

			sfxb.clear();	// TODO de
//			sfxb.FXchorusReadDelay(RMP0, NA | RPTR2 | COMPA, "temp2", 0);
			//wrax pitch1,0
			sfxb.writeRegister(pitch2,  0.0);
			
			this.getPin("Pitch Out 1").setRegister(pitch1);	
		}
	}
	
	public int getLFOSel() {
		return lfoSel;
	}

	public void setLFOSel(int r) {
		lfoSel = r;
	}
}