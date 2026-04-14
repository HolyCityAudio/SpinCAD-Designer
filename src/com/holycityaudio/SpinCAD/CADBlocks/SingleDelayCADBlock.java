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
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

@SuppressWarnings("unused")
public class SingleDelayCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;
	private SingleDelayControlPanel cp = null;

	private double inputGain = 1.0;
	private double fbkGain = 0.5;
	private double delayLength = 32767;
	private double delayOffset = -1;
	private int output;

	public SingleDelayCADBlock(int x, int y) {
		super(x, y);
		setName("Single Delay");
		setBorderColor(new Color(0x6060c4));
		addInputPin(this, "Input");
		addInputPin(this, "Feedback");
		addOutputPin(this, "Output");
		addControlInputPin(this, "Delay Time");
		addControlInputPin(this, "Feedback Gain");
		hasControlPanel = true;
	}

	public void editBlock() {
		if(cp == null) {
			if(hasControlPanel == true) {
				cp = new SingleDelayControlPanel(this);
			}
		}
	}

	public void clearCP() {
		cp = null;
	}

	public void generateCode(SpinFXBlock sfxb) {

		sfxb.comment(getName());

		SpinCADPin sp = null;

		sp = this.getPin("Input").getPinConnection();
		int adcl = -1;
		if(sp != null) {
			adcl = sp.getRegister();
		}
		sp = this.getPin("Feedback").getPinConnection();
		int feedback = -1;
		if(sp != null) {
			feedback = sp.getRegister();
		}
		sp = this.getPin("Delay Time").getPinConnection();
		int cIn = -1;
		if(sp != null) {
			cIn = sp.getRegister();
		}
		sp = this.getPin("Feedback Gain").getPinConnection();
		int fbk = -1;
		if(sp != null) {
			fbk = sp.getRegister();
		}

		// calculate delay offset for ADDR_PTR based addressing
		int delayOffsetVal = sfxb.getDelayMemAllocated() + 1;
		sfxb.FXallocDelayMem("singleDelay", delayLength);

		if(this.getPin("Input").isConnected() == true) {
			// process feedback: read feedback audio input scaled by panel fbkGain,
			// then optionally multiply by external feedback gain control
			if(this.getPin("Feedback").isConnected() == true) {
				sfxb.readRegister(feedback, fbkGain);
				if(this.getPin("Feedback Gain").isConnected() == true) {
					sfxb.mulx(fbk);
				}
			}

			// add audio input and write to head of delay line
			sfxb.readRegister(adcl, inputGain);
			sfxb.FXwriteDelay("singleDelay", 0, 0.0);

			// read from delay output
			if(this.getPin("Output").isConnected() == true) {
				output = sfxb.allocateReg();
				sfxb.clear();
				sfxb.or(0x7FFF00);
				if(this.getPin("Delay Time").isConnected() == true) {
					sfxb.mulx(cIn);
				}
				sfxb.scaleOffset((0.95 * delayLength) / 32768.0, (delayOffsetVal + (0.05 * delayLength)) / 32768.0);
				sfxb.writeRegister(ADDR_PTR, 0);
				sfxb.readDelayPointer(1.0);
				sfxb.writeRegister(output, 0.0);
				this.getPin("Output").setRegister(output);
			}
		}
	}

	// control panel getters and setters
	public void setinputGain(double __param) {
		inputGain = Math.pow(10.0, __param / 20.0);
	}

	public double getinputGain() {
		return inputGain;
	}

	public void setfbkGain(double __param) {
		fbkGain = Math.pow(10.0, __param / 20.0);
	}

	public double getfbkGain() {
		return fbkGain;
	}

	public void setdelayLength(double __param) {
		delayLength = __param;
	}

	public double getdelayLength() {
		return delayLength;
	}
}
