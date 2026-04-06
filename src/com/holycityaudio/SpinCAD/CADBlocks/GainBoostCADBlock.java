/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Copyright (C) 2013 - 2026 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2026.  Look for GSW in code. 
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
	// gain in tenths of dB (e.g. 60 = 6.0 dB), 0.1 dB resolution
	int gain = 60;
	// version flag: false in old serialized data (was whole dB), true in new (tenths)
	boolean gainV2 = true;

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
			int AVG = sfxb.allocateReg();

			sfxb.readRegister(input, 1);

			double gainDb = getGain();
			int full6db = (int)(gainDb / 6.0);
			double remainderDb = gainDb - (full6db * 6.0);

			// Each SOF -2.0 doubles the signal (6 dB)
			for(int i = 0; i < full6db; i++) {
				sfxb.scaleOffset(-2.0, 0.0);
			}
			// Fix sign and apply remainder in one step if possible
			if(remainderDb > 0.001) {
				double linearGain = Math.pow(10.0, remainderDb / 20.0);
				if((full6db & 1) == 1) {
					sfxb.scaleOffset(-linearGain, 0.0);
				} else {
					sfxb.scaleOffset(linearGain, 0.0);
				}
			} else if((full6db & 1) == 1) {
				sfxb.scaleOffset(-1.0, 0.0);
			}

			sfxb.writeRegister(AVG, 0);
			this.getPin("Audio Output").setRegister(AVG);
		}
		System.out.println("Gain boost code gen!");
	}
	
	public void editBlock(){
		new GainBoostControlPanel(this);
	}
	// backward compat: old format stored whole dB, new stores tenths
	@Override
	protected Object readResolve() {
		super.readResolve();
		if (!gainV2) {
			gain = gain * 10;
			gainV2 = true;
		}
		return this;
	}

	//====================================================
	public double getGain() {
		return gain / 10.0;
	}

	public void setGain(double d) {
		gain = (int) Math.round(d * 10);
	}
}

