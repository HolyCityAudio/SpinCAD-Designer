/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * control_smootherCADBlock.java
 * Copyright (C)2013 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
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

public class control_smootherACADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	private int filtReg;
	private double filt = 0.00015;	// default value is 1.0 Hz
	private control_smootherControlPanelA cp = null;

	public control_smootherACADBlock(int x, int y) {
		super(x, y);
		setName("Smoother A");	
		// Iterate through pin definitions and allocate or assign as needed
		addControlInputPin(this, "Control_Input");
		addControlOutputPin(this, "Control_Output");
		// if any control panel elements declared, set hasControlPanel to true
		hasControlPanel = true;
	}

	// In the event there are parameters editable by control panel
	public void editBlock(){ 
		if(cp == null) {
			if(hasControlPanel == true) {
				cp = new control_smootherControlPanelA(this);
			}
		}
	}
	
	public void clearCP() {
		cp = null;
	}

	public void generateCode(SpinFXBlock sfxb) {

		// Iterate through mem and equ statements, allocate accordingly
		filtReg = sfxb.allocateReg();


		sfxb.comment(getName());

		SpinCADPin sp = null;

		// Iterate through pin definitions and connect or assign as needed
		sp = this.getPin("Control_Input").getPinConnection();
		int input = -1;
		if(sp != null) {
			input = sp.getRegister();
		}

		// finally, generate the instructions
		filtReg = sfxb.allocateReg();
		if(this.getPin("Input").getPinConnection() != null) {
			sfxb.readRegister(input, 1.0);
			sfxb.readRegisterFilter(filtReg, filt);
			sfxb.writeRegister(filtReg, 0.0);
			this.getPin("Control_Output").setRegister(filtReg);
		}
	}

	// create setters and getter for control panel variables
	public void setfilt(double __param) {
		filt = __param;	
	}

	public double getfilt() {
		return filt;
	}
}	
