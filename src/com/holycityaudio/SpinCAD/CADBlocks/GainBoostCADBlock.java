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

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class GainBoostCADBlock extends ControlCADBlock{

	/**
	 * 
	 */
	// gain is increments of 6 dB, or # of SOF -2.0, 0
	int gain = 1;
	
	private static final long serialVersionUID = -125887536230107216L;

	public GainBoostCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		setName("Gain Boost");
		setBorderColor(new Color(0x2468f2));
		addInputPin(this, "Audio Input");	
		addOutputPin(this, "Audio Output");	
	}

	public void generateCode(SpinFXBlock sfxb) {
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input").getPinConnection();
		sfxb.comment(getName());

		if(p != null) {
			input = p.getRegister();
			int AVG = sfxb.allocateReg();			//

			sfxb.readRegister(input, 1);
			for(int i = 0; i < gain; i++) {
				sfxb.scaleOffset(-2.0,  0.0);
			}
			if((gain & 1) == 1) {
				sfxb.scaleOffset(-1.0,  0.0);				
			}
			sfxb.writeRegister(AVG, 0);
			this.getPin("Audio Output").setRegister(AVG);
		}
		System.out.println("Gain boost code gen!");
	}
	
	public void editBlock(){
		new GainBoostControlPanel(this);
	}
	//====================================================
	public int getGain() {
		return gain;
	}

	public void setGain(int d) {
		gain = d;
	}
}

