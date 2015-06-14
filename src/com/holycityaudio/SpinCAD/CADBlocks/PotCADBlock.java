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
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class PotCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6291190763689134131L;
	
	private boolean speedup = false;	// whether to implement the shelving high-pass speedup code
	int potRegister = -1;				// Register value for POT input
	
	public PotCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlOutputPin(this);	//	feedback
		setBorderColor(Color.BLUE);
	}
	
	// POT speedup code taken from Spin forum
	public void generateCode(SpinFXBlock eP) {
		eP.comment(getName());
		if(getSpeedup() == true) {
			eP.comment(" pot speedup high shelf filter");
			// equ   potfilt   reg0 
			int potfilt = eP.allocateReg();
			// equ   fastpot reg1    
			int fastpot = eP.allocateReg();

			// rdax   pot0, 1 
			eP.readRegister(potRegister,  1.0);
			// rdfx   potfilt, 0.001    ; this is the shelving highpass filter 
			eP.readRegisterFilter(potfilt, 0.001);
			// wrhx   potfilt, -0.75    ; it cuts lower freqs by factor of 4 
			eP.writeRegisterHighshelf(potfilt, -0.75);
			// rdax   fastpot, 0.75     ; this gives 4X recursive gain 
			eP.readRegister(fastpot, 0.75);
			// wrax   fastpot, 1        ; to recover full range
			eP.writeRegister(fastpot,  0.0);
			this.getPin("Control Output 1").setRegister(fastpot);
		} else {
			this.getPin("Control Output 1").setRegister(potRegister);
		}
	}
	
	void setSpeedup(boolean s) {
		speedup = s;
	}
	
	boolean getSpeedup() {
		return speedup;
	}
	
	int getPotNum() {
		return potRegister - POT0;
	}
	
	public void editBlock(){
		new PotControlPanel(this);
	}
}
