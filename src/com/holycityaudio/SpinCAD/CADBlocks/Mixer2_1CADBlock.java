package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class Mixer2_1CADBlock extends MixCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676526418848384621L;
	private double gain1 = 0.5;
	private double gain2 = 0.5;
	
	public Mixer2_1CADBlock(int x, int y) {
		super(x, y);
		addInputPin(this);
		addControlInputPin(this);
		addControlInputPin(this);
		setName("Mixer 2-1");
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int mix = sfxb.allocateReg();
		int left = sfxb.allocateReg();
//		int right = sfxb.allocateReg();

		int leftIn = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
		}

		int rightIn = -1;
		p = this.getPin("Audio Input 2").getPinConnection();
		if (p != null) {
			rightIn = p.getRegister();			
		}

		p = this.getPin("Control Input 1").getPinConnection();
		sfxb.comment(getName());

		// generate left channel mix code only if left input has a pin connected.
		if(leftIn != -1) {
			if(p == null) {	// there's no pin attached! (redundant check, but who cares)
					sfxb.readRegister(leftIn, gain1);  // read left 100%\
			}
			else {
				int controlInput = p.getRegister();
				sfxb.readRegister(leftIn, gain2);
				if(controlInput != -1)
					sfxb.mulx(controlInput);
			}
			sfxb.writeRegister(left, 0);	
		}

		p = this.getPin("Control Input 2").getPinConnection();

		// generate right channel mix code only if right input has a pin connected.
		if(rightIn != -1) {
			if(p == null) {	// there's no pin attached!
					sfxb.readRegister(rightIn, gain2);  // read left 100%\
			}
			else {
				int controlInput = p.getRegister();
				sfxb.readRegister(rightIn, gain2);
				if(controlInput != -1)
					sfxb.mulx(controlInput);
			}
//			sfxb.writeRegister(right, 0);	
		}
		else {
			sfxb.clear();
		}
		// at this point, if there is no right input, we clear accumulator
		// if there is a right input but no right control input, then ACC holds rightIn * defaultGain
		// if there is a right input and right control input, then ACC holds right input * right control input

		if(leftIn != -1) {
			sfxb.readRegister(left, 1.0);	// get left signal, add to register, scale by 1.0
		}
		sfxb.writeRegister(mix, 0.0);	// dry signal, for later

		this.getPin("Audio Output 1").setRegister(mix);
		System.out.println("Mixer 2_1 code gen!");
	}

	public double getGain1() {
		return gain1;
	}

	public double getGain2() {
		return gain2;
	}

	public void setGain1(double d) {
		gain1 = d;
		
	}

	public void setGain2(double d) {
		gain2 = d;
		
	}
	
	public void editBlock(){
		new Mixer2_1ControlPanel(this);
	}

}
