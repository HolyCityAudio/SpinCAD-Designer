/*
 * Gary Worsham attempt at a pitch up and down.	
 */

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class PitchUpCADBlock extends ModulationCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476502380095165941L;
	/**
	 * 
	 */

	public PitchUpCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);
		setName("Pitch Up");
	}
	
	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int controlInput = -1;	
			int pitch1 = sfxb.allocateReg();

			// delayd mem 4096 ; Down delay 
			sfxb.FXallocDelayMem("delayd", 4096);
			// temp mem 1 ; Temp location for partial calculations 
			sfxb.FXallocDelayMem("temp", 1); 
			sfxb.comment("Pitch Up");
			
			// skp run,START
			sfxb.skip(RUN, 1);
			// wldr RMP0, 0 ,4096 
			sfxb.loadRampLFO(0, 0, 4096);
			
			p = this.getPin("Control Input 1").getPinConnection();
			if(p != null) {
				int potfil = sfxb.allocateReg();
				controlInput = p.getRegister();
				sfxb.readRegister(controlInput, 1.0);
				sfxb.scaleOffset(0.25,  -0.125);
				sfxb.readRegister(potfil,  0.02);
				sfxb.writeRegister(potfil, 1.0);
				sfxb.writeRegister(RMP0_RATE, 0);				
			}
			// START: ldax ADCL
			sfxb.loadAccumulator(input);
			//; Write it to left delay and clear ACC 
			//wra delayd,0
			sfxb.FXwriteDelay("delayd", 0, 0.0);	
			
			//cho rda,RMP0,REG|CHO_COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP0, REG | COMPC, "delayd", 0);
			//cho rda,RMP0,,delayd+1 
			sfxb.FXchorusReadDelay(RMP0, 0, "delayd", 1);
			// wra temp,0 
//			sfxb.FXwriteDelay("temp", 0, 0.0);
			// cho rda,RMP0,RPTR2|COMPC,delayd 
//			sfxb.FXchorusReadDelay(CHO_LFO_RMP0, CHO_RPTR2 | CHO_COMPC, "delayd", 0);
			// cho rda,RMP0,RPTR2,delayd+1 
//			sfxb.FXchorusReadDelay(CHO_LFO_RMP0, CHO_RPTR2, "delayd", 1);
			// cho sof,RMP0,NA|COMPC,0 
//			sfxb.chorusScaleOffset(CHO_LFO_RMP0, CHO_NA | CHO_COMPC, 0);
			// cho rda,RMP0,NA,temp 
//			sfxb.FXchorusReadDelay(CHO_LFO_RMP0, CHO_NA, "temp", 0);
			//wrax pitch1,0
			sfxb.writeRegister(pitch1, 0);
	
			this.getPin("Audio Output 1").setRegister(pitch1);	
		}
	}
}