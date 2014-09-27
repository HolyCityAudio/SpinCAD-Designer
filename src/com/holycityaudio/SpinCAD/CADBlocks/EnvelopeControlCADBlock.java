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

public class EnvelopeControlCADBlock extends ControlCADBlock{

	/**
	 * 
	 */
	private double attackCoeff = 0.00015;
	private int gain = 2;
	private double decayCoeff = 0.0001;
	
	private static final long serialVersionUID = -125887536230107216L;

	public EnvelopeControlCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		setName("Envelope");
		addInputPin(this);	//	delay time
		addControlInputPin(this, "Sensitivity");
		addControlOutputPin(this, "Single Slope");	//
		addControlOutputPin(this, "Dual Slope");	//
	}

	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();

		if(p != null) {
			input = p.getRegister();
			int AVG = sfxb.allocateReg();			//
			int LAVG = sfxb.allocateReg();			//
			int TEMP = sfxb.allocateReg();			//
			sfxb.comment(getName());
			sfxb.readRegister(input, 1);
			p = this.getPin("Sensitivity").getPinConnection();
			if(p != null) {
				int sens = p.getRegister();
				sfxb.mulx(sens);
			}
			sfxb.absa();
			
			for(int i = 0; i < gain; i++) {
				sfxb.scaleOffset(-2.0,  0.0);
			}
			if((gain & 1) == 1) {
				sfxb.scaleOffset(-1.0,  0.0);				
			}

			//				rdfx	avg,0.01		;average input level
			sfxb.readRegisterFilter(AVG, attackCoeff);
			//				wrax	avg,0		;write avg level, pass on
			sfxb.writeRegister(AVG, 0);
			//				rdax	lavg,0
			sfxb.readRegister(LAVG,1);
			//				sof	-0.01,0	
			sfxb.scaleOffset(-decayCoeff, 0);
			//				rdax	lavg,1	
			sfxb.readRegister(LAVG,1);
			//				wrax	temp,0
			sfxb.writeRegister(TEMP, 0);
			//				rdax	avg,1
			sfxb.readRegister(AVG,1);
			//				maxx	temp,1		;filter a long average
			sfxb.maxx(TEMP,1);
			//				wrax	lavg,0
			sfxb.writeRegister(LAVG,0);
			this.getPin("Single Slope").setRegister(AVG);
			this.getPin("Dual Slope").setRegister(LAVG);
		}

		System.out.println("Envelope control code gen!");

	}
	
	public void editBlock(){
		new EnvelopeControlControlPanel(this);
	}
	//====================================================
	public int getGain() {
		return gain;
	}

	public void setGain(int d) {
		gain = d;
	}

	public double getAttack() {
		return attackCoeff;
		}

	public void setAttack(double b) {
		attackCoeff = b;
	}

	public double getDecay() {
		return decayCoeff;
	}

	public void setDecay(double d) {
		decayCoeff = d;
	}

}

