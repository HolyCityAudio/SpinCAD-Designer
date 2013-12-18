package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class LPF1PCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 240;
	public LPF1PCADBlock(int x, int y) {
		super(x, y);
		setName("Low Pass 1P");	}

	public void editBlock(){
		new LPF1PControlPanel(this);
	}	

	public void generateCode(SpinFXBlock sfxb) {
		// coefficients

		double k1 = Math.exp((-6.283 * f0)/getSamplerate());
		double k2 = 1.0 - k1;

		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int filt = sfxb.allocateReg();
			
			sfxb.comment("1 pole low pass");

			sfxb.skip(RUN, 2);
			sfxb.clear();
			sfxb.writeRegister(filt,  0);

			// ------------- start of filter code
//			sfxb.loadAccumulator(input);
			sfxb.readRegister(filt, k1);
			sfxb.readRegister(input, k2);
			sfxb.writeRegister(filt, 0.0);

			this.getPin("Audio Output 1").setRegister(filt);	
		}
		System.out.println("LPF code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
	}
}
