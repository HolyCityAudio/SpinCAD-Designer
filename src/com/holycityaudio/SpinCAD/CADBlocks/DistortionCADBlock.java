package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class DistortionCADBlock extends GainCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6410980346610553856L;

	public DistortionCADBlock(int x, int y) {
		super(x, y);
		setName("Distortion");
	}

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		int input = -1;
		if(p != null) {
			input = p.getRegister();
			if(input != -1) {
				int sign = sfxb.allocateReg();
				
				sfxb.comment("Distortion");
				//	sof	0,-1		;load ACC with -1;
				sfxb.scaleOffset(0,  -1);
				//	wrax	sign,0		;set sign to negative
				sfxb.writeRegister(sign,  0);
				
				sfxb.readRegister(input, 1.0);
				
//				skp	neg,sgnset	;skip if mono input is negative
				sfxb.skip(NEG, 2);
//				sof	0,0.999		;set sign to +1 if input is pos (close enough)
				sfxb.scaleOffset(0,  0.999);
//				wrax	sign,0		;set sign to pos if signal is pos.
				sfxb.writeRegister(sign,  0);
//				sgnset:			;jump here if negative input, sign is now set

//				ldax	mono		;get input signal again
				sfxb.loadAccumulator(input);
//				absa			;positive only
				sfxb.absa();
//				wrax	abs,1		;store absolute signal in abs, keep in ACC
				int abs = sfxb.allocateReg();
				sfxb.writeRegister(abs,  1);
//				sof	1,-0.125	;subtract 1/8
				sfxb.scaleOffset(1,  -1.0/8.0);
//				skp	neg,nodist	;if signal is below this limit, 
				sfxb.skip(NEG, 5);
//				ldax	abs
				sfxb.loadAccumulator(abs);
//				log	-1,-0.375	;1/x, displace result
				sfxb.log(-1.0, -3.0/8);
//				exp	1,0
				sfxb.exp(1, 0);
//				sof	-1,0.25		;subtract 1/x from 0.25
				sfxb.scaleOffset(-1,  0.25);
//				skp	run,distout	;skip always
				sfxb.skip(RUN, 1);

//				nodist:
//				sof	1,0.125		;add threshold value back
				sfxb.scaleOffset(1,  0.125);
//				distout:
//				mulx	sign		;put sign back
				sfxb.mulx(sign);
//				sof	-2,0
				sfxb.scaleOffset(-2, 0);
//				sof	-2,0		;bring up gain (12dB)
				sfxb.scaleOffset(-2, 0);
				int output = sfxb.allocateReg();
				sfxb.writeRegister(output, 0.0);	
				p = this.getPin("Audio Output 1");
				p.setRegister(output);
			}
			System.out.println("Distortion code gen!");
		}
	}
}
