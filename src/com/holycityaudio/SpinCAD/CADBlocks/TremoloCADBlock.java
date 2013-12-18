package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class TremoloCADBlock extends ModulationCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7695385445625428287L;
	/**
	 * 
	 */
	private double lfo = 0.02;

	public TremoloCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this, "LFO Speed");
		setName("Tremolo");
	}
	
	public void editBlock(){
		new TremoloControlPanel(this);
	}

	public void generateCode(SpinFXBlock b) {

		int input;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			int Control1 = -1;
			
			p = this.getPin("Control Input 1").getPinConnection();
			
			if (p != null)
				Control1 = p.getRegister();
			
			//		;POT0 : Control frequency 
			//		; 
			//		equ s reg0
			int s = b.allocateReg();
			//		equ c reg1
			int c = b.allocateReg();
			int output = b.allocateReg();
			b.comment(getName());

			//		;Then initialize the oscillator by setting one to xero and the other to -1 
			//		;we do this just once, during the first cycle of operation 

			//		skp run,endset ;do not execute if already running 
			b.skip(RUN, 3);
			//		wrax s,0 ;set s to 0, (acc should be zero) 
			b.writeRegister(s, 0.0);
			//		sof 0,-1 ;set accum to -1
			b.scaleOffset(0, -1);
			//		wrax c,0 ;write to c 
			b.writeRegister(c, 0);
			//		endset: ;jump-to label 

			//		;Now do the LFO, using pot0 as a control for frequency 

			//		rdax s,0.02 ;read the s register, change this value between  0.001 and 1.0
			b.readRegister(s, lfo);
			//		mulx pot0 ;multiply by pot value 
			if(Control1 != -1)
				b.mulx(Control1);
			//		rdax c,1 ;read the c register
			b.readRegister(c, 1);
			//		wrax c,-0.02 ;integrate the c value, this value MUST be the negative of
			b.writeRegister(c,  -lfo);
			//		;what ever you set the value in 'rdax s,X' to above 
			//		mulx pot0 ;multiply by pot value
			if(Control1 != -1)
				b.mulx(Control1);
			//		rdax s,1 ;read s reg
			b.readRegister(s, 1);
			//		wrax s,1 ;integrate the s value 
			b.writeRegister(s, 1);
			//		;Either the s or c register will be producing s waveforms (just shifted in 
			//		;phase), so either can be used as a modulation source. The maximum 
			//		;frequency of this LFO is Fs/2pi, which should be high enough! 

			//		mulx adcl 
			b.mulx(input);
			//		;and output the result 
			//		wrax dacl,0 
			b.writeRegister(output, 0);
			this.getPin("Audio Output 1").setRegister(output);
		}
	}
	//====================================================
	public double getLFO() {
		return lfo;
	}

	public void setLFO(double d) {
		lfo = d;
	}
}
