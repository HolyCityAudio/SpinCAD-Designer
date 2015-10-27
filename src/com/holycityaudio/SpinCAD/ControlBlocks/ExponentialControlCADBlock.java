package com.holycityaudio.SpinCAD.ControlBlocks;

import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ExponentialControlCADBlock extends ControlCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4812040104612463732L;

	public ExponentialControlCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);	//	delay time
		addControlOutputPin(this);	//	feedback
		setName("Exp");
	}
	
	public void generateCode(SpinFXBlock sfxb) {

		int Exp = sfxb.allocateReg();			//
		sfxb.comment(getName());

		int input = this.getPin("Control Input 1").getPinConnection().getRegister();

//		rdax	pot1,1		;get pot1
		sfxb.readRegister(input, 1);
//		sof	0.5,-0.5	;ranges -0.5 to 0
		sfxb.scaleOffset(0.5, -0.5);
//		exp	1,0
		sfxb.exp(1,0);

		sfxb.writeRegister(Exp,0);

		this.getPin("Control Output 1").setRegister(Exp);
		System.out.println("Envelope control code gen!");

	}

}
