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

import java.awt.Color;

import org.andrewkilpatrick.elmGen.Debug;

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

	@SuppressWarnings("unused")
	public PitchShiftFixedCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio In");
		addOutputPin(this, "Pitch Out");
		setBorderColor(new Color(0x111111));
		if(Debug.DEBUG== true) {
			addOutputPin(this, "CHO RDA delayd");
			addOutputPin(this, "CHO RDA delayd+1");
			addOutputPin(this, "CHO RDA RMP2 delayd");
			addOutputPin(this, "CHO RDA RMP2 delayd+1");
		}
		setName("Pitch Shift " + lfoSel);
	}

	public void editBlock(){
		new PitchShiftFixedControlPanel(this);
	}

	@SuppressWarnings("unused")
	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;
		int coefficient = 0;
		double octaves = 0.0;

		// pitchBufferSize should be 512, 1024, 2048, or 4096
		int pitchBufferSize = amp;
		sfxb.comment(getName());

		// XXX debug delete the following 4 lines when done debugging ramp pitch shift stuff
		int chordadelayd = sfxb.allocateReg();
		int chordadelaydplusone = sfxb.allocateReg();
		int chordarmp2delayd = sfxb.allocateReg();
		int chordarmp2delaydplusone = sfxb.allocateReg(); 

		if(Debug.DEBUG== true) {
			this.getPin("CHO RDA delayd").setRegister(chordadelayd);	
			this.getPin("CHO RDA delayd+1").setRegister(chordadelaydplusone);	
			this.getPin("CHO RDA RMP2 delayd").setRegister(chordarmp2delayd);	
			this.getPin("CHO RDA RMP2 delayd+1").setRegister(chordarmp2delaydplusone);	
		}

		SpinCADPin p = this.getPin("Audio In").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int pitch1 = sfxb.allocateReg();

			// delayd mem 4096 ; Down delay 
			sfxb.FXallocDelayMem("delayd", pitchBufferSize);
			// temp mem 1 ; Temp location for partial calculations 
			sfxb.FXallocDelayMem("temp", 1); 

//			;For pitch shifting up: 
//				;C = 2^14 * (2^N-1) 
//				;where 
//				; (2^(1/12))^S) - 1 
//				;N = Desired amount of pitch shift in octaves 
//				;S is the number of semitones

			octaves = (double) freq/12.0;
			if(freq > 0) {
				coefficient = (int)(16384 * (Math.pow(2.0, octaves) - 1));
			} else if (freq < 0) {
				coefficient = (int)(-16384 * (1 - (Math.pow(2.0, octaves))));
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
			if(Debug.DEBUG== true) {
				sfxb.writeRegister(chordadelayd, 0.0);
			}
			//cho rda,RMP0,,delayd+1 
			sfxb.FXchorusReadDelay(lfoFlag, 0, "delayd+", 1);
			if(Debug.DEBUG== true) {
				sfxb.writeRegister(chordadelaydplusone, 0.0);
			}			
			// wra temp,0 
			sfxb.FXwriteDelay("temp", 0, 0.0);
			// cho rda,RMP0,RPTR2|COMPC,delayd 
			sfxb.FXchorusReadDelay(lfoFlag, RPTR2 | COMPC, "delayd", 0);
			// cho rda,RMP0,RPTR2,delayd+1 
			sfxb.FXchorusReadDelay(lfoFlag, RPTR2, "delayd+", 1);
			// cho sof,RMP0,NA|COMPC,0 
			sfxb.chorusScaleOffset(lfoFlag, NA | COMPC, 0);
			// cho rda,RMP0,NA,temp debug, trying to isolate the xfade aspect
			sfxb.FXchorusReadDelay(lfoFlag, NA, "temp", 0);
			//wrax pitch1,0
			sfxb.writeRegister(pitch1,  0.0);		
			this.getPin("Pitch Out").setRegister(pitch1);	
		}
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


	public int getLFOSel() {
		return lfoSel;
	}

	public void setLFOSel(int r) {
		lfoSel = r;
	}
}