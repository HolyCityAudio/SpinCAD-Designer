package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinFXBlock;

public class HPFCADBlock extends FilterCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4606131434210775273L;
	double freq = 1250.0;
	double resonance = 0.1;

	public HPFCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this);
		addOutputPin(this);
		setName("High Pass");	
	}
	
	public void editBlock(){
		new HPFControlPanel(this);
	}
	
	public void generateCode(SpinFXBlock sfxb) {
		
		// as written this is just a copy of the LPF code, so it ain't an HPF yet

			// coefficients
			double kfl = 1.0 - Math.exp((-6.283 * freq)/getSamplerate());
			double kql = -0.13;
			
			int input = this.getPin("Audio Input 1").getPinConnection().getRegister();
			
			// we could look for an assigned control input and make the filter sweepable if there is one.
			// otherwise it would be a fixed freq.
			
			int lpal = sfxb.allocateReg();
			int lpbl = sfxb.allocateReg();
			int lpoutl = sfxb.allocateReg();
			sfxb.comment("High pass filter");

			//		int rmixl = sfxb.allocateReg();
//			int kfx = sfxb.allocateReg();	
			
			//		;now do the low pass.
			
			sfxb.skip(RUN, 3);
			sfxb.clear();
			sfxb.writeRegister(lpal,  0);
			sfxb.writeRegister(lpbl,  0);
					
			// ------------- start of filter code
		 	//		rdax	lpal,1
			sfxb.readRegister(lpal, kfl);
			//		mulx	kfl
//			sfxb.mulx(kfl);
			//		rdax	lpbl,1
			sfxb.readRegister(lpbl, 1.0);
			//		wrax	lpbl,-1
			sfxb.writeRegister(lpbl, -1.0);
			//		rdax	lpal,kql
			sfxb.readRegister(lpal, kql);
			//		rdax	input,1
			sfxb.readRegister(input, 1.0);
			//		wrax	lpoutl,1	;lp output
			sfxb.writeRegister(lpoutl, kfl);
			//		mulx	kfl
//			sfxb.mulx(kfl);
			//		rdax	lpal,1
			sfxb.readRegister(lpal, 1.0);
			//		wrax	lpal,0
			sfxb.writeRegister(lpal, 0);

			//		rdax	lpbl,-1
			sfxb.readRegister(lpbl, -1.0);
			//		rdax	rmixl,1
			sfxb.readRegister(input, 1.0);
			//		rdax	lpbl,1
			sfxb.readRegister(lpbl, 1.0);
			
			this.getPin("Audio Output 1").setRegister(lpoutl);	
			System.out.println("HPF-2P-Fixed code gen!");
	}

	public double getFreq() {
		// ---
		return freq;
	}
	
	public void setFreq(double f) {
		// ---
		freq = f;
	}

	public double getRes() {
		// ---
		return resonance;
	}

	public void setRes(double f) {
		// ---
		resonance = f;
	}
}
