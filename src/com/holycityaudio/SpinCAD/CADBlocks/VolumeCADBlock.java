package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class VolumeCADBlock extends MixCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676526418848384621L;
	private double defaultGain = 1.0;

	public VolumeCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		addControlInputPin(this);
		setName("Volume");
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int left = sfxb.allocateReg();
//		int right = sfxb.allocateReg();

		int leftIn = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
		}

		p = this.getPin("Control Input 1").getPinConnection();

		sfxb.comment(getName());
		// generate left channel mix code only if left input has a pin connected.
		if(leftIn != -1) {
			if(p == null) {	// there's no pin attached! (redundant check, but who cares)
					sfxb.readRegister(leftIn, defaultGain);  // read left 100%\
			}
			else {
				int controlInput = p.getRegister();
				sfxb.readRegister(leftIn, defaultGain);
				sfxb.mulx(controlInput);
			}
			sfxb.writeRegister(left, 0);	
		}
		// generate right channel mix code only if right input has a pin connected.
		else {
			sfxb.clear();
		}
		// at this point, if there is no right input, we clear accumulator
		// if there is a right input but no right control input, then ACC holds rightIn * defaultGain
		// if there is a right input and right control input, then ACC holds right input * right control input

		this.getPin("Audio Output 1").setRegister(left);
		System.out.println("Volume code gen!");
	}

	public void setGain(double d) {
		defaultGain = d;		
	}

	public double getGain() {
		return defaultGain;
	}
	
	public void editBlock(){
		new VolumeControlPanel(this);
	}

}
