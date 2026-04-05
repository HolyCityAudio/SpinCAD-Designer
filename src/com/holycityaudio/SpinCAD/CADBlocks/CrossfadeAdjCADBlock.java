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

/**
 * Adjustable crossfade with configurable midpoint gain.
 * <p>
 * At control = 0: Input 1 full, Input 2 silent.
 * At control = 0.5: both inputs at midpoint gain level.
 * At control = 1: Input 1 silent, Input 2 full.
 * <p>
 * midpoint = 0.5 gives linear crossfade (Crossfade 1 equivalent).
 * midpoint = 0.707 gives constant-power crossfade (Crossfade 3 equivalent).
 * midpoint = 1.0 gives maximum overlap crossfade (Crossfade 2 equivalent).
 * <p>
 * When midpoint = 0.5, uses a simpler straight-line path (fewer instructions).
 * Otherwise uses a piecewise linear curve with breakpoint at ctrl = 0.5.
 */
public class CrossfadeAdjCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	private double gain1 = 0.5;   // linear gain, set via dB slider
	private double gain2 = 0.5;
	private double midpoint = 0.707;

	public CrossfadeAdjCADBlock(int x, int y) {
		super(x, y);
		setName("Crossfade Adj");
		setBorderColor(new Color(0x2468f2));
		addInputPin(this, "Audio In 1");
		addInputPin(this, "Audio In 2");
		addControlInputPin(this, "Control Input");
		addOutputPin(this, "Audio Output");
		hasControlPanel = true;
	}

	public void editBlock() {
		new CrossfadeAdjControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		sfxb.comment(getName());

		SpinCADPin sp = null;

		sp = this.getPin("Audio In 1").getPinConnection();
		int inputOne = -1;
		if (sp != null) inputOne = sp.getRegister();

		sp = this.getPin("Audio In 2").getPinConnection();
		int inputTwo = -1;
		if (sp != null) inputTwo = sp.getRegister();

		sp = this.getPin("Control Input").getPinConnection();
		int controlIn = -1;
		if (sp != null) controlIn = sp.getRegister();

		int output = sfxb.allocateReg();
		int temp = sfxb.allocateReg();

		if (this.getPin("Audio In 1").isConnected()) {
			if (this.getPin("Audio In 2").isConnected()) {
				// Both audio inputs connected
				if (this.getPin("Control Input").isConnected()) {
					if (Math.abs(midpoint - 0.5) < 0.001) {
						// Linear crossfade: gain2 = ctrl, gain1 = 1 - ctrl
						generateLinearCrossfade(sfxb, inputOne, inputTwo, controlIn, temp);
					} else {
						// Piecewise crossfade with adjustable midpoint
						generatePiecewiseCrossfade(sfxb, inputOne, inputTwo, controlIn, temp);
					}
				} else {
					// No control input: mix at gain levels
					sfxb.readRegister(inputOne, gain1);
					sfxb.readRegister(inputTwo, gain2);
				}
				sfxb.writeRegister(output, 0);
			} else {
				// Only Audio In 1 connected
				if (this.getPin("Control Input").isConnected()) {
					sfxb.readRegister(inputOne, 1.0);
					sfxb.mulx(controlIn);
					sfxb.scaleOffset(gain1, 0);
				} else {
					sfxb.readRegister(inputOne, gain1);
				}
				sfxb.writeRegister(output, 0);
			}
		} else {
			if (this.getPin("Audio In 2").isConnected()) {
				// Only Audio In 2 connected
				if (this.getPin("Control Input").isConnected()) {
					sfxb.readRegister(inputTwo, 1.0);
					sfxb.mulx(controlIn);
					sfxb.scaleOffset(gain2, 0);
				} else {
					sfxb.readRegister(inputTwo, gain2);
				}
				sfxb.writeRegister(output, 0);
			}
		}

		this.getPin("Audio Output").setRegister(output);
	}

	/**
	 * Linear crossfade (midpoint = 0.5): constant slope, no branching.
	 * gain2 = ctrl, gain1 = 1 - ctrl
	 * 9 instructions.
	 */
	private void generateLinearCrossfade(SpinFXBlock sfxb,
			int inputOne, int inputTwo, int controlIn, int temp) {
		// gain2 path: ctrl * inputTwo * gain2
		sfxb.readRegister(controlIn, 1.0);
		sfxb.mulx(inputTwo);
		sfxb.scaleOffset(gain2, 0);
		sfxb.writeRegister(temp, 0);
		// gain1 path: (1 - ctrl) * inputOne * gain1
		sfxb.readRegister(controlIn, -1.0);
		sfxb.scaleOffset(0.999, 0.999);
		sfxb.mulx(inputOne);
		sfxb.scaleOffset(gain1, 0);
		sfxb.readRegister(temp, 1.0);
	}

	/**
	 * Piecewise crossfade with adjustable midpoint.
	 * Two linear segments joined at ctrl = 0.5 where both gains = midpoint.
	 * Same instruction count as Crossfade 3.
	 */
	private void generatePiecewiseCrossfade(SpinFXBlock sfxb,
			int inputOne, int inputTwo, int controlIn, int temp) {

		double m = midpoint;
		double twoM = 2.0 * m;
		double twoOneMinusM = 2.0 * (1.0 - m);
		double twoMMinusOne = 2.0 * m - 1.0;

		// Test: is ctrl >= 0.5?
		sfxb.readRegister(controlIn, 1.0);
		sfxb.scaleOffset(1.0, -0.5);
		sfxb.skip(NEG, 12);

		// HIGH HALF (ctrl >= 0.5): 12 instructions
		// gain2 = ctrl * 2*(1-m) + (2m-1)  =>  ranges from m at 0.5 to 1.0 at 1.0
		sfxb.readRegister(controlIn, twoOneMinusM);
		sfxb.scaleOffset(1.0, twoMMinusOne);
		sfxb.mulx(inputTwo);
		sfxb.scaleOffset(gain2, 0);
		sfxb.writeRegister(temp, 0);
		// gain1 = 2m*(1 - ctrl)  =>  ranges from m at 0.5 to 0.0 at 1.0
		sfxb.readRegister(controlIn, 1.0);
		sfxb.scaleOffset(m, -m);
		sfxb.scaleOffset(-2.0, 0);
		sfxb.mulx(inputOne);
		sfxb.scaleOffset(gain1, 0);
		sfxb.readRegister(temp, 1.0);
		sfxb.skip(RUN, 10);

		// LOW HALF (ctrl < 0.5): 10 instructions
		// gain2 = ctrl * 2m  =>  ranges from 0.0 at 0.0 to m at 0.5
		sfxb.clear();
		sfxb.readRegister(controlIn, twoM);
		sfxb.mulx(inputTwo);
		sfxb.scaleOffset(gain2, 0);
		sfxb.writeRegister(temp, 0);
		// gain1 = 1 - ctrl * 2*(1-m)  =>  ranges from 1.0 at 0.0 to m at 0.5
		sfxb.readRegister(controlIn, -twoOneMinusM);
		sfxb.scaleOffset(0.999, 0.999);
		sfxb.mulx(inputOne);
		sfxb.scaleOffset(gain1, 0);
		sfxb.readRegister(temp, 1.0);
	}

	// Getters and setters

	public double getGain1() { return gain1; }
	public void setGain1(double dB) { gain1 = Math.pow(10.0, dB / 20.0); }

	public double getGain2() { return gain2; }
	public void setGain2(double dB) { gain2 = Math.pow(10.0, dB / 20.0); }

	public double getMidpoint() { return midpoint; }
	public void setMidpoint(double value) { midpoint = value; }
}
