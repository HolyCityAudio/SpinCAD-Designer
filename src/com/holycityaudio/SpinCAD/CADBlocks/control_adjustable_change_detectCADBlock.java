/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * control_adjustable_change_detectCADBlock.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
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

import java.awt.Color;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class control_adjustable_change_detectCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	private double filt = 0.00015;	// default value is ~1.0 Hz
	private control_adjustable_change_detectControlPanel cp = null;

	public control_adjustable_change_detectCADBlock(int x, int y) {
		super(x, y);
		setName("Adj Change Detect");
		setBorderColor(new Color(0xf2f224));
		addControlInputPin(this, "Control Input");
		addControlInputPin(this, "Speed CV");
		addControlOutputPin(this, "Control Output");
		hasControlPanel = true;
	}

	public void editBlock(){
		if(cp == null) {
			if(hasControlPanel == true) {
				cp = new control_adjustable_change_detectControlPanel(this);
			}
		}
	}

	public void clearCP() {
		cp = null;
	}

	public void generateCode(SpinFXBlock sfxb) {

		int filtReg = sfxb.allocateReg();
		int output = sfxb.allocateReg();

		sfxb.comment(getName());

		SpinCADPin sp = null;

		sp = this.getPin("Control Input").getPinConnection();
		int input = -1;
		if(sp != null) {
			input = sp.getRegister();
		}

		sp = this.getPin("Speed CV").getPinConnection();
		int speedCV = -1;
		if(sp != null) {
			speedCV = sp.getRegister();
		}

		if(this.getPin("Control Input").getPinConnection() != null) {
			if(this.getPin("Speed CV").getPinConnection() != null) {
				// Adjustable coefficient path using LPF 1P pattern:
				// ACC = input * filt
				sfxb.readRegister(input, filt);
				// ACC = (input - filtReg) * filt
				sfxb.readRegister(filtReg, -filt);
				// ACC = (input - filtReg) * filt * speedCV
				sfxb.mulx(speedCV);
				// ACC = (input - filtReg) * filt * speedCV + filtReg
				sfxb.readRegister(filtReg, 1.0);
				// store LPF result
				sfxb.writeRegister(filtReg, -1.0);
			} else {
				// No speed CV connected: use standard RDFX smoother
				sfxb.readRegister(input, 1.0);
				sfxb.readRegisterFilter(filtReg, filt);
				sfxb.writeRegister(filtReg, -1.0);
			}
			// HPF = input - LPF: ACC has -filtReg, add input
			sfxb.readRegister(input, 1.0);
			sfxb.writeRegister(output, 0.0);
			this.getPin("Control Output").setRegister(output);
		}
	}

	public void setfilt(double __param) {
		filt = __param;
	}

	public double getfilt() {
		return filt;
	}
}
