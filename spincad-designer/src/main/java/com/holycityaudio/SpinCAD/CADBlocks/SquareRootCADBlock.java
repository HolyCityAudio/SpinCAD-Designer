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
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class SquareRootCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 343880108475812086L;

	public SquareRootCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this);
		addControlOutputPin(this);
		setName("Square Root");
	}

	public void generateCode(SpinFXBlock sfxb) {

		int control1 = -1;
		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		sfxb.comment(getName());

		if(p != null) {
			control1 = p.getRegister();
			int output = sfxb.allocateReg();
			sfxb.readRegister(control1, 1.0);
			sfxb.log(0.5, 0);
			sfxb.exp(1,0);
			sfxb.scaleOffset(1.0, -0.5);
			sfxb.scaleOffset(1.9990, 0);
			sfxb.writeRegister(output, 0);

			// last instruction clears accumulator
			p = this.getPin("Control Output 1");
			p.setRegister(output);
		}
		System.out.println("Square Root code gen!");
	}
}
