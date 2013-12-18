package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class OverdriveCADBlock extends GainCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6410980346610553856L;
	int stages = 2;
	double outputGain = 0.3;
	double defaultGain = 0.25;

	public OverdriveCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		addControlInputPin(this, "Drive");
		setName("Overdrive");
	}

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin inputPin = this.getPin("Audio Input 1").getPinConnection();
		int input = -1;
		if(inputPin != null) {
			input = inputPin.getRegister();
			if(input != -1) {
				if(stages > 0) {
					int filt = sfxb.allocateReg();
					int filt2 = sfxb.allocateReg();
					int filt3 = sfxb.allocateReg();
					int filt4 = sfxb.allocateReg();
					int output = sfxb.allocateReg();
					
					sfxb.comment(getName());

					SpinCADPin p = this.getPin("Drive").getPinConnection();
					if(p == null) {	// there's no pin attached!
						sfxb.readRegister(input, defaultGain);
					}
					else {
						int Control1 = p.getRegister();
						sfxb.readRegister(input, 1.0);
						sfxb.mulx(Control1);
					}

					if (stages > 2) {
						sfxb.scaleOffset(-2.0, 0.0);
						sfxb.readRegister(filt, 0.9);
						sfxb.writeRegister(filt, 1.0);
					}
					if (stages > 1) {
						sfxb.scaleOffset(-2.0, 0.0);
						sfxb.readRegister(filt2, 0.3);
						sfxb.writeRegister(filt2, 1.0);
					}
					sfxb.scaleOffset(-2.0, 0.0);
					// here are some low pass filters, it would be good to understand them better
					sfxb.readRegister(filt3, 0.7);
					sfxb.writeRegister(filt3, 1.0);
					sfxb.readRegister(filt4, -0.3);
					// scale filt4 output before saving in output register
					sfxb.writeRegister(filt4, outputGain);	
					sfxb.writeRegister(output, 0.0);	

					p = this.getPin("Audio Output 1");
					p.setRegister(output);
				}
			}
			System.out.println("Overdrive code gen!");
		}
	}
	
	public void editBlock(){
		new OverdriveControlPanel(this);
	}

	public int getStages() {
		// TODO Auto-generated method stub
		return stages;
	}

	public void setStages(int value) {
		// TODO Auto-generated method stub
		stages = value;
	}

}
