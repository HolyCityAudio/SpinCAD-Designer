package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class BiQuadCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 1200.0;
	double q0 = 10.0;
	double w0 = (2.0 * Math.PI * f0)/getSamplerate();
	int filterMode = 1;
	double b0;
	double b1;
	double b2;

	public BiQuadCADBlock(int x, int y) {
		super(x, y);
		setName("BiQuad");	
	}

	public void editBlock(){
		new BiQuadControlPanel(this);
	}	

	@Override
	public void generateCode(SpinFXBlock sfxb) {
		// coefficients
		w0 = (2.0 * Math.PI * f0)/getSamplerate();	
		//	    alpha = sin(w0)/(2*Q) (case: Q)
		double alpha = Math.sin(w0)/(2.0 * q0);

		if(filterMode == 1) {
			// the following are for a low pass filter
			//        b0 =  (1 - cos(w0))/2
			b0 = (1.0 - Math.cos(w0))/2;
			//        b1 =   1 - cos(w0)
			b1 = 1.0 - Math.cos(w0);
			//        b2 =  (1 - cos(w0))/2
			b2 = (1.0 - Math.cos(w0))/2;
		} else if (filterMode == 2) {
			// band pass
			// b0 = Math.sin(w0)/2;
			b0 = alpha;		// correcting gain against Q
			b1 = 0;
			// b2 = -Math.sin(w0)/2;
			b2 = -alpha;	// correcting gain against Q
		} else if (filterMode == 3) {
			// high pass
			b0 = (1.0 + Math.cos(w0))/2;
			b1 = -(1.0 + Math.cos(w0));
			b2 = (1.0 + Math.cos(w0))/2;			
		}
		//        a0 =   1 + alpha;
		double a0 = 1 + alpha;
		//        a1 =  -2*cos(w0)
		double a1 =  -2.0 * Math.cos(w0);
		//        a2 =   1 - alpha
		double a2 = 1 - alpha;
		
		double inputGain = 0.25;

		int input = -1;

		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int d0 = sfxb.allocateReg();
			int d1 = sfxb.allocateReg();
			int output = sfxb.allocateReg();
			
			sfxb.comment("BiQuad filter");
			
			sfxb.scaleOffset(0, 0);
			sfxb.readRegister(input, inputGain * b0/a0);

			sfxb.readRegister(d0, 1.0);
			sfxb.writeRegister(output,0);

			sfxb.readRegister(input, b1/a0);
			sfxb.readRegister(output, -a1/a0);
			sfxb.readRegister(d1, 1.0);
			sfxb.writeRegister(d0,0);

			sfxb.readRegister(input, b2/a0);
			sfxb.readRegister(output, -a2/a0);
			sfxb.writeRegister(d1,0);

			this.getPin("Audio Output 1").setRegister(output);	
		}
		System.out.println("BiQuad code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
	}

	public double getQ() {
		return q0;
	}

	public void setQ(double q) {
		q0 = q;
	}

	public void setFilterMode(int i) {
		filterMode = i;
	}

	public int getFilterMode() {
		return filterMode;
	}
}
