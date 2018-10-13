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

public class RootCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 343880108475812086L;
	int root = 2;
	boolean invert = false;
	boolean flip = false;


	public RootCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this);
		addControlOutputPin(this);
		setName("Root");
	}

	public void generateCode(SpinFXBlock sfxb) {

		int control1 = -1;
		SpinCADPin p = this.getPin("Control Input 1").getPinConnection();
		sfxb.comment(getName());

		if(p != null) {
			control1 = p.getRegister();
			int output = sfxb.allocateReg();
			sfxb.readRegister(control1, 1.0);
			if(invert == true) {
				sfxb.scaleOffset(-0.9990234375, 0.9990234375);
			}
			sfxb.log(1.0/root, 0);
			sfxb.exp(1,0);
			if(flip == true) {
				sfxb.scaleOffset(-0.9990234375, 0.9990234375);
			}
			sfxb.writeRegister(output, 0);

			// last instruction clears accumulator
			p = this.getPin("Control Output 1");
			p.setRegister(output);
		}
		System.out.println("Root code gen!");
	}
	
	public void editBlock(){
		new RootControlControlPanel(this);
	}
	public int getRoot() {
		return root;
	}

	public void setRoot(int root) {
		this.root = root;
	}
	public boolean isInvert() {
		return invert;
	}

	public void setInvert(boolean invert) {
		this.invert = invert;
	}

	public boolean isFlip() {
		return flip;
	}

	public void setFlip(boolean flip) {
		this.flip = flip;
	}
}
