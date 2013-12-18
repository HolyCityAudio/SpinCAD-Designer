/*
 * Gary Worsham attempt at a pitch up and down.	
 */

package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class PitchTestCADBlock extends ModulationCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3476502380095165941L;
	/**
	 * 
	 */

	public PitchTestCADBlock(int x, int y) {
		super(x, y);
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
			// skp run,START
			sfxb.skip(RUN, 1);
			// wldr RMP0, 0 ,4096 
			sfxb.loadRampLFO(0, 0, 4096);
				
			sfxb.clear();
			sfxb.scaleOffset(0, -1.0);
			sfxb.writeRegister(RMP0_RATE, 0);				
			// START: ldax ADCL
			sfxb.loadAccumulator(input);
			//; Write it to left delay and clear ACC 
			//wra delayd,0
			sfxb.FXwriteDelay("delayd", 0, 0.0);	
			
			//cho rda,RMP0,REG|CHO_COMPC,delayd 
			sfxb.FXchorusReadDelay(RMP0, REG | COMPC, "delayd", 0);
			//cho rda,RMP0,,delayd+1 
//			sfxb.FXchorusReadDelay(CHO_LFO_RMP0, 0, "delayd", 1);
			// wra temp,0 
	//		sfxb.FXwriteDelay("temp", 0, 0.0);
			// cho rda,RMP0,RPTR2|COMPC,delayd 
	//		sfxb.FXchorusReadDelay(CHO_LFO_RMP0, CHO_RPTR2 | CHO_COMPC, "delayd", 0);
			// cho rda,RMP0,RPTR2,delayd+1 
	//		sfxb.FXchorusReadDelay(CHO_LFO_RMP0, CHO_RPTR2, "delayd", 1);
			// cho sof,RMP0,NA|COMPC,0 
	//		sfxb.chorusScaleOffset(CHO_LFO_RMP0, CHO_NA | CHO_COMPC, 0);
			// cho rda,RMP0,NA,temp 
	//		sfxb.FXchorusReadDelay(CHO_LFO_RMP0, CHO_NA, "temp", 0);
			//wrax pitch1,0
			sfxb.writeRegister(pitch1, 0);
	
			this.getPin("Audio Output 1").setRegister(pitch1);	
		}
	}
}