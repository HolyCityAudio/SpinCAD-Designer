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

public class VolumeCADBlock_A extends MixCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4676526418848384621L;
	private double defaultGain = 1.0;

	public VolumeCADBlock_A(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this);
		setName("Volume");
	}

	public void generateCode(SpinFXBlock sfxb)
	{
		int left = sfxb.allocateReg();
//		int right = sfxb.allocateReg();

		int leftIn = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			leftIn = p.getRegister();			
		}

		p = this.getPin("Control Input 1").getPinConnection();

		sfxb.comment(getName());
		// generate left channel mix code only if left input has a pin connected.
		if(leftIn != -1) {
			if(p == null) {	// there's no pin attached! (redundant check, but who cares)
					sfxb.readRegister(leftIn, defaultGain);  // read left 100%\
			}
			else {
				int controlInput = p.getRegister();
				sfxb.readRegister(leftIn, defaultGain);
				sfxb.mulx(controlInput);
			}
			sfxb.writeRegister(left, 0);	
		}
		// generate right channel mix code only if right input has a pin connected.
		else {
			sfxb.clear();
		}
		// at this point, if there is no right input, we clear accumulator
		// if there is a right input but no right control input, then ACC holds rightIn * defaultGain
		// if there is a right input and right control input, then ACC holds right input * right control input

		this.getPin("Audio Output 1").setRegister(left);
		System.out.println("Volume code gen!");
	}

	public void setGain(int d) {
		defaultGain = Math.pow(10, (double) d/20);
						
	}

	public int getGain() {
		return (int) (20 * Math.log10((double)defaultGain));
	}
	
	public void editBlock(){
		new VolumeControlPanel_A(this);
	}

}
