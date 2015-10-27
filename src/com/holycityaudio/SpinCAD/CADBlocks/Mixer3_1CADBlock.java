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

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class Mixer3_1CADBlock extends SpinCADBlock{

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
		hasControlPanel = true;
		addInputPin(this);
		addControlInputPin(this, "Level 1");
		addInputPin(this);
		addControlInputPin(this, "Level 2");
		addInputPin(this);
		addControlInputPin(this, "Level 3");
		addOutputPin(this);
		setName("Mixer 3-1");
		setBorderColor(Color.YELLOW);
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int mix = -1;
		int leftIn = -1;
		int centerIn = -1;
		int rightIn = -1;
		sfxb.comment(getName());
		
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
			sfxb.readRegister(leftIn, gain1);  // read input 1 scaled by control panel value
			p = this.getPin("Level 1").getPinConnection();
			if(p != null) {
				int controlInput = p.getRegister();
				if(controlInput != -1)
					sfxb.mulx(controlInput);				
			}
			mix = sfxb.allocateReg();
			sfxb.writeRegister(mix, 0);	
		}

		p = this.getPin("Audio Input 2").getPinConnection();
		if (p != null) {
			centerIn = p.getRegister();			
			sfxb.readRegister(centerIn, gain2);  // read input 2 scaled by control panel value
			p = this.getPin("Level 2").getPinConnection();
			if(p != null) {
				int controlInput = p.getRegister();
				if(controlInput != -1)
					sfxb.mulx(controlInput);
			}
			if(mix == -1) {
				mix = sfxb.allocateReg();			
				sfxb.writeRegister(mix, 0);	
			}
			else {
				sfxb.readRegister(mix, 1.0);
				sfxb.writeRegister(mix, 0);	
			}
		}

		p = this.getPin("Audio Input 3").getPinConnection();
		if (p != null) {
			rightIn = p.getRegister();			
			sfxb.readRegister(rightIn, gain3);  // read input 3 scaled by control panel value
			p = this.getPin("Level 3").getPinConnection();
			if(p != null) {
				int controlInput = p.getRegister();
				if(controlInput != -1)
					sfxb.mulx(controlInput);
			}
			if(mix == -1) {
				mix = sfxb.allocateReg();			
				sfxb.writeRegister(mix, 0);	
			}
			else {
				sfxb.readRegister(mix, 1.0);
				sfxb.writeRegister(mix, 0);	
			}
		}

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
