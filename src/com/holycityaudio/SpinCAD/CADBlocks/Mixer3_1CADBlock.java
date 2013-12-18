package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class Mixer3_1CADBlock extends MixCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676526418848384621L;
	private double gain1 = 1.0/3;
	private double gain2 = 1.0/3;
	private double gain3 = 1.0/3;

	// three to one mixer, default 0.33 per input no controls
	public Mixer3_1CADBlock(int x, int y) {
		super(x, y);
		addInputPin(this);
		addInputPin(this);
		setName("Mixer 3-1");
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int mix = sfxb.allocateReg();

		int leftIn = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
		}

		int middleIn = -1;
		p = this.getPin("Audio Input 2").getPinConnection();
		if (p != null) {
			middleIn = p.getRegister();			
		}

		int rightIn = -1;
		p = this.getPin("Audio Input 3").getPinConnection();
		if (p != null) {
			rightIn = p.getRegister();			
		}
		sfxb.comment("Mixer 3-1");
		// generate left channel mix code only if left input has a pin connected.
		if(leftIn != -1) {
			sfxb.readRegister(leftIn, gain1);  // read left 100%\
		}

		// generate middle channel mix code only if right input has a pin connected.
		if(middleIn != -1) {
			sfxb.readRegister(middleIn, gain2);  // read left 100%\
		}

		// generate right channel mix code only if right input has a pin connected.
		if(rightIn != -1) {
			sfxb.readRegister(rightIn, gain3);  // read left 100%\
		}
		sfxb.writeRegister(mix, 0.0);	// dry signal, for later

		this.getPin("Audio Output 1").setRegister(mix);
		System.out.println("Mixer 3-1 code gen!");
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

	public void setGain3(double d) {
		gain3 = d;

	}

	public double getGain3() {
		return gain3;
	}

	public void editBlock(){
		new Mixer3_1ControlPanel(this);
	}
}
