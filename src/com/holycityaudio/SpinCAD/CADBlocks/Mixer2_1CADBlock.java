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

public class Mixer2_1CADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676526418848384621L;
	private double gain1 = 0.5;
	private double gain2 = 0.5;
	
	public Mixer2_1CADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this);
		addInputPin(this);
		addOutputPin(this);
		addControlInputPin(this, "Level 1");
		addControlInputPin(this, "Level 2");
		setName("Mixer 2-1");
		setBorderColor(Color.YELLOW);
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int mix = -1;
		int leftIn = -1;
		int rightIn = -1;
		
		sfxb.comment(getName());
		
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
			sfxb.readRegister(leftIn, gain1);  // read left 100%\
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
			rightIn = p.getRegister();			
			sfxb.readRegister(rightIn, gain2);  // read left 100%\
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
