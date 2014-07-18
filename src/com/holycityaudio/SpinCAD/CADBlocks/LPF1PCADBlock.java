/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * LPF1PCADBlock.java
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

public class LPF1PCADBlock extends FilterCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5711126291575876825L;
	double f0 = 240;
	public LPF1PCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Audio Output");
		setName("Low Pass 1P");	}

	public void editBlock(){
		new LPF1PControlPanel(this);
	}	

	public void generateCode(SpinFXBlock sfxb) {
		// coefficients

		double k1 = Math.exp((-6.283 * f0)/getSamplerate());
		double k2 = 1.0 - k1;

		int input = -1;

		SpinCADPin p = this.getPin("Audio Input").getPinConnection();

		if(p != null) {
			input = p.getRegister();

			int filt = sfxb.allocateReg();
			
			sfxb.comment("1 pole low pass");
			// ------------- start of filter code
//			sfxb.loadAccumulator(input);
			sfxb.readRegister(filt, k1);
			sfxb.readRegister(input, k2);
			sfxb.writeRegister(filt, 0.0);

			this.getPin("Audio Output").setRegister(filt);	
		}
		System.out.println("LPF 1P code gen!");
	}

	public double getFreq() {
		return f0;
	}

	public void setFreq(double f) {
		f0 = f;
	}
}
