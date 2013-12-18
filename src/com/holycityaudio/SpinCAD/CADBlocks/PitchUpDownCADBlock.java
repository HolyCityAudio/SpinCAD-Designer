/*
 * Gary Worsham attempt at a pitch up and down.	
 */

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class PitchUpDownCADBlock extends ModulationCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476502380095165941L;
	/**
	 * 
	 */

	public PitchUpDownCADBlock(int x, int y) {
		super(x, y);
		setName("Pitch Up/Down");
		addOutputPin(this);
	}
	
	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {

			input = p.getRegister();
			int pitch1 = sfxb.allocateReg();
			int pitch2 = sfxb.allocateReg();

			sfxb.clear();
			// delayd mem 4096 ; Down delay 
			sfxb.FXallocDelayMem("delayd", 4096);
			// temp mem 1 ; Temp location for partial calculations 
			sfxb.FXallocDelayMem("temp", 1); 
			// skp run,START
			sfxb.skip(RUN, 2);
			// wldr RMP0,16384,4096 
			sfxb.loadRampLFO(0, 16384, 4096);
			// wldr RMP1,-8192,4096 
			sfxb.loadRampLFO(1, -8192, 4096);
			// START: ldax ADCL
			sfxb.loadAccumulator(input);
			//; Write it to left delay and clear ACC 
			//wra delayd,0
			sfxb.FXwriteDelay("delayd", 0, 0.0);	
			//; Read in left 
			//ldax ADCL 
			sfxb.loadAccumulator(input);
			// wra delayd,0
			sfxb.FXwriteDelay("delayd", 0, 0.0);
			// not sure why we did the same thing twice			
			
			//cho rda,RMP0,REG|COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP0, REG | COMPC, "delayd", 0);
			//cho rda,RMP0,,delayd+1 
			sfxb.FXchorusReadDelay(RMP0, 0, "delayd", 1);
			// wra temp,0 
			sfxb.FXwriteDelay("temp", 0, 0.0);
			// cho rda,RMP0,RPTR2|COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP0, RPTR2 | COMPC, "delayd", 0);
			// cho rda,RMP0,RPTR2,delayd+1 
			sfxb.FXchorusReadDelay(RMP0, RPTR2, "delayd", 1);
			// cho sof,RMP0,NA|COMPC,0 
			sfxb.chorusScaleOffset(RMP0, NA | COMPC, 0);
			// cho rda,RMP0,NA,temp 
			sfxb.FXchorusReadDelay(RMP0, NA, "temp", 0);
			// mulx POT1
			sfxb.mulx(POT1);
			//wrax pitch1,0
			sfxb.writeRegister(pitch1, 0);
	
			// second pitch shift
			// cho rda,RMP1,REG|COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP1, REG | COMPC, "delayd", 0);
			// cho rda,RMP1,,delayd+1 
			sfxb.FXchorusReadDelay(RMP1, 0, "delayd", 1);
			// wra temp,0 
			sfxb.FXwriteDelay("temp", 0, 0.0);
			//cho rda,RMP1,RPTR2|COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP1, RPTR2 | COMPC, "delayd", 0);
			//cho rda,RMP1,RPTR2,delayd+1 
			sfxb.FXchorusReadDelay(RMP1, RPTR2, "delayd", 1);
			//cho sof,RMP1,NA|COMPC,0 
			sfxb.chorusScaleOffset(RMP1, NA | COMPC, 0);
			//cho rda,RMP1,NA,temp
			sfxb.FXchorusReadDelay(RMP1, NA, "temp", 0);

			//MULX POT2 
			sfxb.mulx(POT2);
			//wrax pitch2,0 
			sfxb.writeRegister(pitch2, 0);
			this.getPin("Audio Output 1").setRegister(pitch1);	
			this.getPin("Audio Output 2").setRegister(pitch2);	
		}
	}
}