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

// AmbienceCADBlock.java
// Based on P16_V_Ambience by Spin Semiconductor.
// Multi-tapped delay with allpass diffusion, tone filters,
// and exponential decay for early reflection ambience.

package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import org.andrewkilpatrick.elmGen.ElmProgram;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class AmbienceCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	// allpass coefficient
	private static final double KAP = 0.6;

	// input gain in dB (-12 to 0)
	private double inputGain = -6.0;

	// default tone (0=dark, 1=bright) used when Tone CV not connected
	private double tone = 0.5;
	// default decay (0-1 mapped to krt 0.2-0.85) used when Decay CV not connected
	private double decay = 0.5;
	// filter corner frequency in Hz (2000-8000)
	private double filterFreq = 4000.0;

	// left channel tap offsets (from original P16_V_Ambience)
	private static final int[] TAPS_L = {
		5500, 5121, 4912, 4474, 4267, 3914, 3587, 3265, 2954,
		2532, 2262, 1994, 1712, 1475, 1159, 746, 652, 351, 187
	};

	// right channel tap offsets (slightly different for stereo spread)
	private static final int[] TAPS_R = {
		5520, 5129, 4908, 4460, 4269, 3914, 3583, 3265, 2957,
		2522, 2262, 1999, 1712, 1473, 1159, 747, 656, 348, 187
	};

	public AmbienceCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		setBorderColor(new Color(0x7100fc));
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Audio Output L");
		addOutputPin(this, "Audio Output R");
		addControlInputPin(this, "Tone");
		addControlInputPin(this, "Decay");
		setName("Ambience");
	}

	public void editBlock() {
		new AmbienceControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin p = this.getPin("Audio Input").getPinConnection();
		if (p == null) return;
		int input = p.getRegister();
		if (input == -1) return;

		sfxb.comment(getName());

		// allocate delay memory
		sfxb.FXallocDelayMem("del", 17000);
		sfxb.FXallocDelayMem("ap", 31);
		sfxb.FXallocDelayMem("ap1", 54);
		sfxb.FXallocDelayMem("ap2", 96);
		sfxb.FXallocDelayMem("ap3", 142);
		sfxb.FXallocDelayMem("ap4", 189);

		// allocate registers
		int krt = sfxb.allocateReg();
		int lf1 = sfxb.allocateReg();
		int lf2 = sfxb.allocateReg();
		int lf3 = sfxb.allocateReg();
		int lf4 = sfxb.allocateReg();
		int temp = sfxb.allocateReg();
		int outputL = sfxb.allocateReg();
		int outputR = sfxb.allocateReg();

		// --- prepare krt (decay coefficient) ---
		// Linear map: 0->0.2 (short ambience), 1->0.85 (long tail)
		SpinCADPin decayPin = this.getPin("Decay").getPinConnection();
		if (decayPin != null) {
			int decayReg = decayPin.getRegister();
			sfxb.readRegister(decayReg, 0.65);  // ACC = cv * 0.65
			sfxb.scaleOffset(1.0, 0.2);          // ACC = ACC + 0.2, range 0.2-0.85
		} else {
			double krtValue = 0.2 + decay * 0.65;
			sfxb.scaleOffset(0, krtValue);
		}
		sfxb.writeRegister(krt, 0);

		// --- get input into delay with an allpass ---
		double linearGain = Math.pow(10.0, inputGain / 20.0);
		sfxb.readRegister(input, linearGain);
		sfxb.FXreadDelay("ap#", 0, KAP);
		sfxb.FXwriteAllpass("ap", 0, -KAP);
		sfxb.FXwriteDelay("del", 0, 0.0);

		// --- compute filter coefficient from frequency ---
		double kfl = 1.0 - Math.exp(-2.0 * Math.PI * filterFreq / ElmProgram.getSamplerate());

		// --- check tone control input ---
		SpinCADPin tonePin = this.getPin("Tone").getPinConnection();
		int toneReg = -1;
		if (tonePin != null) {
			toneReg = tonePin.getRegister();
		}

		// --- install APs and filters into delay ---
		// Section 1: offset 600, positive phase
		generateDiffusionSection(sfxb, "del", 600, "ap1", lf1, temp, toneReg, 1.0, KAP, kfl);
		// Section 2: offset 1000, negative phase
		generateDiffusionSection(sfxb, "del", 1000, "ap2", lf2, temp, toneReg, -1.0, -KAP, kfl);
		// Section 3: offset 1641, positive phase
		generateDiffusionSection(sfxb, "del", 1641, "ap3", lf3, temp, toneReg, 1.0, KAP, kfl);
		// Section 4: offset 2456, negative phase
		generateDiffusionSection(sfxb, "del", 2456, "ap4", lf4, temp, toneReg, -1.0, -KAP, kfl);

		// --- derive left output as 19 exponentially decaying taps ---
		generateTapChain(sfxb, TAPS_L, krt);
		sfxb.scaleOffset(1.999, 0);    // makeup gain
		sfxb.writeRegister(outputL, 0);

		// --- derive right output ---
		generateTapChain(sfxb, TAPS_R, krt);
		sfxb.scaleOffset(1.999, 0);    // makeup gain
		sfxb.writeRegister(outputR, 0);

		// set output pins
		this.getPin("Audio Output L").setRegister(outputL);
		this.getPin("Audio Output R").setRegister(outputR);
	}

	private void generateDiffusionSection(SpinFXBlock sfxb, String delayName,
			int offset, String apName, int filterReg, int temp, int toneReg,
			double readScale, double apCoeff, double kfl) {
		// read from delay at offset
		sfxb.FXreadDelay(delayName + "+", offset, readScale);
		// allpass: read end, write start
		sfxb.FXreadDelay(apName + "#", 0, apCoeff);
		sfxb.FXwriteAllpass(apName, 0, -apCoeff);
		// store allpass output, negate for filter
		sfxb.writeRegister(temp, -1.0);
		// highshelf filter (RDFX + WRHX)
		sfxb.readRegisterFilter(filterReg, kfl);
		sfxb.writeRegisterHighshelf(filterReg, -1.0);
		// apply tone control
		if (toneReg >= 0) {
			sfxb.mulx(toneReg);
		} else {
			sfxb.scaleOffset(tone, 0);
		}
		// add back unfiltered signal and write back to delay
		sfxb.readRegister(temp, 1.0);
		sfxb.FXwriteDelay(delayName + "+", offset, 0.0);
	}

	private void generateTapChain(SpinFXBlock sfxb, int[] taps, int krt) {
		// First tap: just read
		sfxb.FXreadDelay("del+", taps[0], 1.0);
		// Remaining taps: multiply by krt, then add next tap
		for (int i = 1; i < taps.length; i++) {
			sfxb.mulx(krt);
			sfxb.FXreadDelay("del+", taps[i], 1.0);
		}
	}

	// --- getters/setters for control panel ---
	public double getTone() {
		return tone;
	}

	public void setTone(double tone) {
		this.tone = tone;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

	public double getFilterFreq() {
		return filterFreq;
	}

	public void setFilterFreq(double filterFreq) {
		this.filterFreq = filterFreq;
	}

	public double getInputGain() {
		return inputGain;
	}

	public void setInputGain(double inputGain) {
		this.inputGain = inputGain;
	}
}
