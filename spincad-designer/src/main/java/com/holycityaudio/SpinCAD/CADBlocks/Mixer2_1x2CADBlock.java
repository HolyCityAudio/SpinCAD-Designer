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

public class Mixer2_1x2CADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676526418848384621L;
	private double gain1a = 1.0;
	private double gain1b = 1.0;
	private double gain2a = 1.0;
	private double gain2b = 1.0;

	// three to one mixer, default 0.33 per input no controls
	public Mixer2_1x2CADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio In 1a");
		addControlInputPin(this, "Level 1a");
		addInputPin(this, "Audio In 1b");
		addControlInputPin(this, "Level 1b");
		addInputPin(this, "Audio In 2a");
		addControlInputPin(this, "Level 2a");
		addInputPin(this, "Audio In 2b");
		addControlInputPin(this, "Level 2b");
		
		addOutputPin(this, "Audio Out 1");
		addOutputPin(this, "Audio Out 2");
		setName("Mixer 2-1x2"); 
		setBorderColor(new Color(0x2468f2));
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int mix1 = -1;
		int mix2 = -1;
		int leftIn = -1;
		int centerIn = -1;
		int rightIn = -1;
		sfxb.comment(getName());
		
		SpinCADPin p = this.getPin("Audio In 1a").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
			sfxb.readRegister(leftIn, gain1a);  // read input 1 scaled by control panel value
			p = this.getPin("Level 1a").getPinConnection();
			if(p != null) {
				int controlInput = p.getRegister();
				if(controlInput != -1)
					sfxb.mulx(controlInput);				
			}
			mix1 = sfxb.allocateReg();
			sfxb.writeRegister(mix1, 0);	
		}

		p = this.getPin("Audio In 1b").getPinConnection();
		if (p != null) {
			centerIn = p.getRegister();			
			sfxb.readRegister(centerIn, gain1b);  // read input 2 scaled by control panel value
			p = this.getPin("Level 1b").getPinConnection();
			if(p != null) {
				int controlInput = p.getRegister();
				if(controlInput != -1)
					sfxb.mulx(controlInput);
			}
			if(mix1 == -1) {
				mix1 = sfxb.allocateReg();			
				sfxb.writeRegister(mix1, 0);	
			}
			else {
				sfxb.readRegister(mix1, 1.0);
				sfxb.writeRegister(mix1, 0);	
			}
		}

		p = this.getPin("Audio In 2a").getPinConnection();
		if (p != null) {
			rightIn = p.getRegister();			
			sfxb.readRegister(rightIn, gain2a);  // read input 3 scaled by control panel value
			p = this.getPin("Level 2a").getPinConnection();
			if(p != null) {
				int controlInput = p.getRegister();
				if(controlInput != -1)
					sfxb.mulx(controlInput);
			}
			if(mix2 == -1) {
				mix2 = sfxb.allocateReg();			
				sfxb.writeRegister(mix2, 0);	
			}
			else {
				sfxb.readRegister(mix2, 1.0);
				sfxb.writeRegister(mix2, 0);	
			}
		}

		p = this.getPin("Audio In 2b").getPinConnection();
		if (p != null) {
			rightIn = p.getRegister();			
			sfxb.readRegister(rightIn, gain2b);  // read input 3 scaled by control panel value
			p = this.getPin("Level 2b").getPinConnection();
			if(p != null) {
				int controlInput = p.getRegister();
				if(controlInput != -1)
					sfxb.mulx(controlInput);
			}
			if(mix2 == -1) {
				mix2 = sfxb.allocateReg();			
				sfxb.writeRegister(mix2, 0);	
			}
			else {
				sfxb.readRegister(mix2, 1.0);
				sfxb.writeRegister(mix2, 0);	
			}
		}

		this.getPin("Audio Out 1").setRegister(mix1);
		this.getPin("Audio Out 2").setRegister(mix2);
		System.out.println("Mixer 2_1x2 code gen!");
	}

	public void setGain1a(double d) {
		gain1a = d;
	}

	public double getGain1a() {
		return gain1a;
	}

	public void setGain2a(double d) {
		gain2a = d;
	}

	public double getGain2a() {
		return gain2a;
	}

	public void setGain1b(double d) {
		gain1b = d;

	}

	public double getGain1b() {
		return gain1b;
	}

	public void setGain2b(double d) {
		gain2b = d;
	}

	public double getGain2b() {
		return gain2b;
	}

	public void editBlock(){
		new Mixer2_1x2ControlPanel(this);
	}
}
