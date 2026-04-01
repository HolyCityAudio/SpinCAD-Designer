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

import org.andrewkilpatrick.elmGen.ElmProgram;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class LongDelayCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	private static final int DELAY_SIZE = 32767;
	// Read offset chosen so that pass_length (offset+1 = 32761 = 181^2)
	// is coprime to all interleave factors 2-16, guaranteeing exactly N
	// passes through the buffer for interleave factor N.
	private static final int READ_OFFSET = 32760;

	private int interleave = 8;
	private double feedbackLevel = 0.5;  // linear, set via dB
	private double inputGain = 1.0;     // linear, set via dB
	private boolean filterEnabled = true;

	public LongDelayCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio Input");
		addInputPin(this, "Feedback");
		addControlInputPin(this, "Feedback Gain");
		addOutputPin(this, "Audio Output");
		setBorderColor(new Color(0x6060c4));
		setName("Long Delay");
	}

	public void editBlock() {
		new LongDelayControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin p = this.getPin("Audio Input").getPinConnection();
		if (p == null) {
			return;
		}
		int input = p.getRegister();

		// External feedback audio input
		int feedback = -1;
		p = this.getPin("Feedback").getPinConnection();
		if (p != null) {
			feedback = p.getRegister();
		}

		// Feedback gain control pin
		int fbGainControl = -1;
		p = this.getPin("Feedback Gain").getPinConnection();
		if (p != null) {
			fbGainControl = p.getRegister();
		}

		sfxb.comment(getName());

		// Allocate full delay buffer
		sfxb.FXallocDelayMem("longdel", DELAY_SIZE);

		// Registers
		int inputReg = sfxb.allocateReg();
		int loopReg = sfxb.allocateReg();
		int tempReg = sfxb.allocateReg();
		int outputReg = sfxb.allocateReg();

		// Anti-aliasing filter registers (2-pole input + 2-pole output)
		int inFilt1 = -1, inFilt2 = -1;
		int outFilt1 = -1, outFilt2 = -1;
		double filterCoeff = 0;
		if (filterEnabled) {
			inFilt1 = sfxb.allocateReg();
			inFilt2 = sfxb.allocateReg();
			outFilt1 = sfxb.allocateReg();
			outFilt2 = sfxb.allocateReg();
			// Cutoff at effective Nyquist: samplerate / (2 * interleave)
			double cutoffHz = ElmProgram.getSamplerate() / (2.0 * interleave);
			filterCoeff = freqToFilt(cutoffHz);
		}

		// Interleave timing: choose step/threshold that are exact in
		// 24-bit fixed-point so that exactly N steps reach the threshold.
		// REG_SCALE = 0x800000 = 8388608 (the 24-bit fractional unit).
		int stepInt = 0x800000 / (2 * interleave);  // integer division, exact in fixed-point
		double stepSize = stepInt / 8388608.0;
		double threshold = (stepInt * interleave) / 8388608.0;

		// Mix external feedback (scaled by feedbackLevel) and input
		// Clear ACC first to avoid DC offset from prior block
		sfxb.clear();
		if (feedback != -1) {
			sfxb.readRegister(feedback, feedbackLevel);
			if (fbGainControl != -1) {
				sfxb.mulx(fbGainControl);
			}
		}
		// Add input signal
		sfxb.readRegister(input, inputGain);
		// Store combined signal
		sfxb.writeRegister(inputReg, 0);

		// Input anti-aliasing filter (2-pole lowpass tracking interleave)
		int captureReg = inputReg;
		if (filterEnabled) {
			sfxb.loadAccumulator(inputReg);
			sfxb.readRegisterFilter(inFilt1, filterCoeff);
			sfxb.writeRegister(inFilt1, 1);  // save state, keep ACC
			sfxb.readRegisterFilter(inFilt2, filterCoeff);
			sfxb.writeRegister(inFilt2, 0);  // save state, clear ACC
			captureReg = inFilt2;
		}

		// IF loop == 0 THEN capture input to temp
		// ACC = -loop; if loop > 0 then ACC < 0
		sfxb.readRegister(loopReg, -1);
		// skip 2 instructions if loop > 0 (ACC is negative)
		sfxb.skip(NEG, 2);
		sfxb.loadAccumulator(captureReg);  // ACC = filtered (or raw) input
		sfxb.writeRegister(tempReg, 0);    // temp = signal, ACC = 0

		// Write temp to delay head (every cycle)
		sfxb.loadAccumulator(tempReg);
		sfxb.FXwriteDelay("longdel", 0, 0);

		// Read delay tail into temp (every cycle)
		// Use "+" suffix so getAddrFromSpinMem applies the offset.
		// READ_OFFSET chosen so pass_length (offset+1) is coprime to all N.
		sfxb.FXreadDelay("longdel+", READ_OFFSET, 1.0);
		sfxb.writeRegister(tempReg, 0);

		// Increment loop counter: loop = loop + stepSize
		sfxb.loadAccumulator(loopReg);
		sfxb.scaleOffset(1, stepSize);
		sfxb.writeRegister(loopReg, 1);  // write loop, keep ACC

		// IF loop >= threshold THEN write output and reset loop
		sfxb.scaleOffset(1, -threshold);   // ACC = loop_new - threshold
		sfxb.skip(NEG, 3);                 // skip 3 if not yet at threshold
		sfxb.loadAccumulator(tempReg);     // ACC = delayed sample
		sfxb.writeRegister(outputReg, 0);  // output = temp, ACC = 0
		sfxb.writeRegister(loopReg, 0);    // loop = 0 (ACC was 0), reset counter
		// Both paths converge here — clear ACC so following blocks see 0
		sfxb.clear();

		// Output reconstruction filter (2-pole lowpass tracking interleave)
		if (filterEnabled) {
			sfxb.loadAccumulator(outputReg);
			sfxb.readRegisterFilter(outFilt1, filterCoeff);
			sfxb.writeRegister(outFilt1, 1);  // save state, keep ACC
			sfxb.readRegisterFilter(outFilt2, filterCoeff);
			sfxb.writeRegister(outFilt2, 0);  // save state, ACC = 0
			this.getPin("Audio Output").setRegister(outFilt2);
		} else {
			this.getPin("Audio Output").setRegister(outputReg);
		}
	}

	public int getInterleave() {
		return interleave;
	}

	public void setInterleave(int interleave) {
		if (interleave >= 2 && interleave <= 16) {
			this.interleave = interleave;
		}
	}

	public double getFeedbackLevel() {
		return feedbackLevel;
	}

	public void setFeedbackLevel(double dB) {
		this.feedbackLevel = Math.pow(10.0, dB / 20.0);
	}

	public double getInputGain() {
		return inputGain;
	}

	public void setInputGain(double dB) {
		this.inputGain = Math.pow(10.0, dB / 20.0);
	}

	public boolean isFilterEnabled() {
		return filterEnabled;
	}

	public void setFilterEnabled(boolean enabled) {
		this.filterEnabled = enabled;
	}

	public double getDelaySeconds() {
		// Each pass takes READ_OFFSET+1 samples (travel + 1 cycle in temp).
		// Signal makes exactly 'interleave' passes through the buffer.
		return (double) interleave * (READ_OFFSET + 1) / ElmProgram.getSamplerate();
	}
}
