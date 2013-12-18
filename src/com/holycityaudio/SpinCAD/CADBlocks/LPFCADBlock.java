package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinFXBlock;

public class LPFCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 240;
	public LPFCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		addInputPin(this);
		addOutputPin(this);
		setName("Low Pass");	}
	
	public void editBlock(){
		new LPFControlPanel(this);
	}	
	
	public void generateCode(SpinFXBlock sfxb) {

		// coefficients

		double kfl = 1.0 - Math.exp((-6.283 * f0)/getSamplerate());
		double kql = -0.13;
		
		int input = this.getPin("Audio Input 1").getPinConnection().getRegister();
		
		// TODO Auto-generated constructor stub
		int lpal = sfxb.allocateReg();
		int lpbl = sfxb.allocateReg();
		int lpoutl = sfxb.allocateReg();

		//		int rmixl = sfxb.allocateReg();
//		int kfx = sfxb.allocateReg();	
		
		//		;now do the low pass.
		
		sfxb.skip(RUN, 3);
		sfxb.clear();
		sfxb.writeRegister(lpal,  0);
		sfxb.writeRegister(lpbl,  0);
				
		// ------------- start of filter code
	 	//		rdax	lpal,1
		sfxb.readRegister(lpal, kfl);
		// ACC = lpal * kfl
		
		//		mulx	kfl
//		sfxb.mulx(kfl);
		//		rdax	lpbl,1
		sfxb.readRegister(lpbl, 1.0);
		// ACC = lpal * kfl + lpbl
		
		//		wrax	lpbl,-1
		sfxb.writeRegister(lpbl, -1.0);
		// lpbl = (lpal * kfl) + lpbl
		// ACC = -(lpal * kfl) - lpbl		
		
		//		rdax	lpal,kql
		sfxb.readRegister(lpal, kql);
		// ACC = kql * (lpal  * (1 - kfl) - lpbl)
		
		//		rdax	input,1
		sfxb.readRegister(input, 1.0);
		// ACC = x[0] + kql * (lpal * (1 - kfl) - lpbl)
		//		wrax	lpoutl,1	;lp output
		sfxb.writeRegister(lpoutl, kfl);
		// y[0] = lpoutl = x[0] + kql * (lpal * (1 - kfl) - lpbl)
		// ACC = kfl * ( x[0] + kql * (lpal * (1 - kfl) - lpbl)) 
		//		mulx	kfl
//		sfxb.mulx(kfl);
		//		rdax	lpal,1
		sfxb.readRegister(lpal, 1.0);
		// ACC = lpal + kfl * ( x[0] + kql * (lpal * (1 - kfl) - lpbl))
		
		//		wrax	lpal,0
		sfxb.writeRegister(lpal, 0);
		// lpal = lpal + kfl * ( x[0] + kql * (lpal * (1 - kfl) - lpbl))
		// ACC = 0
		//		rdax	lpbl,-1
		sfxb.readRegister(lpbl, -1.0);
		//		rdax	rmixl,1
		sfxb.readRegister(input, 1.0);
		//		rdax	lpbl,1
		sfxb.readRegister(lpbl, 1.0);
		// hmmm this is a little weird, what happened?  ACC is not used for anything....
		this.getPin("Audio Output 1").setRegister(lpoutl);	
		System.out.println("LPF code gen!");

	}
}
