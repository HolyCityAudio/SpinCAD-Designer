/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 */

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
	double gain = 0.25;

	public OverdriveCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
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
					int filt3 = sfxb.allocateReg();
					int filt4 = sfxb.allocateReg();
					int output = sfxb.allocateReg();
					
					sfxb.comment(getName());

					SpinCADPin p = this.getPin("Drive").getPinConnection();
					if(p == null) {	// there's no pin attached!
						sfxb.readRegister(input, gain);
					}
					else {
						int Control1 = p.getRegister();
						sfxb.readRegister(input, 1.0);
						sfxb.mulx(Control1);
					}

					if (stages > 2) {
						sfxb.scaleOffset(-2.0, 0.0);
						int filt = sfxb.allocateReg();
						sfxb.readRegister(filt, 0.9);
						sfxb.writeRegister(filt, 1.0);
					}
					if (stages > 1) {
						sfxb.scaleOffset(-2.0, 0.0);
						int filt2 = sfxb.allocateReg();
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
		return stages;
	}

	public void setStages(int value) {
		stages = value;
	}
	
	public double getGain() {
		return gain;
	}

	public void setGain(double value) {
		gain = value;
	}

	public double getOutputGain() {
		return outputGain;
	}

	public void setOutputGain(double value) {
		outputGain = value;
	}
}
