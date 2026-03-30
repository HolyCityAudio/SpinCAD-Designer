/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * ArpeggiatorCADBlock.java
 * Copyright (C) 2015 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

@SuppressWarnings("unused")
public class ArpeggiatorCADBlock extends SpinCADBlock {
	private static final long serialVersionUID = 1L;

	private static final int MAX_STEPS = 12;
	private static final int MIN_STEPS = 3;
	private static final int MIN_SEMITONES = -12;
	private static final int MAX_SEMITONES = 19;

	private transient ArpeggiatorControlPanel cp = null;

	public static final int SLOPE_POSITIVE = 0;
	public static final int SLOPE_NEGATIVE = 1;
	public static final int SLOPE_BOTH = 2;

	private double threshold = 0.25;
	private double numSteps = 8;
	private int slope = SLOPE_POSITIVE;
	private int[] semitones = { 0, 4, 7, 12, 7, 4, 0, -12, 0, 0, 0, 0 };
	private int lfoSel = 0;
	private int bufferSize = 4096;

	public ArpeggiatorCADBlock(int x, int y) {
		super(x, y);
		setName("Arpeggiator");
		setBorderColor(new Color(0xd050d0));
		addInputPin(this, "Audio In");
		addControlInputPin(this, "Trigger");
		addOutputPin(this, "Pitch Out");
		hasControlPanel = true;
	}

	public void editBlock() {
		if (cp == null) {
			if (hasControlPanel) {
				cp = new ArpeggiatorControlPanel(this);
			}
		}
	}

	public void clearCP() {
		cp = null;
	}

	// Convert semitone offset to ramp LFO rate value.
	// Formula from AN-0001: coefficient = 2^14 * (2^(N) - 1) where N = semitones/12
	// Rate value = coefficient / 32768 for writing to RMP_RATE register.
	private double semitoneToRate(int semi) {
		if (semi == 0) return 0.0;
		double octaves = semi / 12.0;
		double coefficient;
		if (semi > 0) {
			coefficient = 16384.0 * (Math.pow(2.0, octaves) - 1.0);
		} else {
			coefficient = -16384.0 * (1.0 - Math.pow(2.0, octaves));
		}
		double rate = coefficient / 32768.0;
		if (rate > 0.9990234375) rate = 0.9990234375;
		if (rate < -1.0) rate = -1.0;
		return rate;
	}

	public void generateCode(SpinFXBlock sfxb) {
		sfxb.comment(getName());

		SpinCADPin sp = this.getPin("Audio In").getPinConnection();
		if (sp == null) return;
		int audioIn = sp.getRegister();

		sp = this.getPin("Trigger").getPinConnection();
		int trigger = -1;
		if (sp != null) {
			trigger = sp.getRegister();
		}

		int output = sfxb.allocateReg();
		int hold = sfxb.allocateReg();

		// Allocate pitch shift delay memory
		sfxb.FXallocDelayMem("delayd", bufferSize);
		sfxb.FXallocDelayMem("temp", 1);

		// Initialize ramp LFO with rate 0 (no shift until sequencer sets it)
		int lfoFlag = (lfoSel == 0) ? RMP0 : RMP1;
		sfxb.skip(RUN, 1);
		sfxb.loadRampLFO(lfoSel, 0, bufferSize);

		// === Step sequencer (generates rate values from semitone settings) ===
		if (trigger >= 0) {
			int gate = sfxb.allocateReg();
			int counter = sfxb.allocateReg();

			int nSteps = (int) numSteps;
			if (nSteps < MIN_STEPS) nSteps = MIN_STEPS;
			if (nSteps > MAX_STEPS) nSteps = MAX_STEPS;

			double stepWidth = 1.0 / nSteps;
			if (stepWidth > 0.9990234375) stepWidth = 0.9990234375;

			double[] rateValues = new double[nSteps];
			for (int i = 0; i < nSteps; i++) {
				rateValues[i] = semitoneToRate(semitones[i]);
			}

			boolean isBoth = (slope == SLOPE_BOTH);

			if (isBoth) {
				// Trigger on any threshold crossing (rising or falling edge).
				// Cascade(4n-1) + Counter(7) + UpdateGate(3) = skipToNoEdge = 4n+9
				int tempState = sfxb.allocateReg();
				sfxb.loadAccumulator(trigger);
				sfxb.scaleOffset(1.0, -threshold);
				sfxb.skip(NEG, 2);
				sfxb.scaleOffset(0.0, 0.9990234375);
				sfxb.skip(RUN, 1);
				sfxb.clear();
				sfxb.writeRegister(tempState, 1.0);
				sfxb.readRegister(gate, -1.0);
				sfxb.absa();
				sfxb.scaleOffset(1.0, -0.5);
				sfxb.skip(NEG, 4 * nSteps + 9);

				generateCascade(sfxb, nSteps, stepWidth, counter, hold, rateValues);
				generateCounterAdvance(sfxb, nSteps, stepWidth, counter);

				// Edge path: update gate
				sfxb.loadAccumulator(tempState);
				sfxb.writeRegister(gate, 0);
				sfxb.skip(RUN, 2);

				// noEdge: update gate
				sfxb.loadAccumulator(tempState);
				sfxb.writeRegister(gate, 0);
			} else {
				// Positive or Negative edge detection.
				// skipToGateOff = 2 + cascade(4n-1) + counter(7) + gateHigh(3) = 4n+11
				// skipToGateHigh = cascade(4n-1) + counter(7) = 4n+6
				sfxb.loadAccumulator(trigger);
				if (slope == SLOPE_NEGATIVE) {
					sfxb.scaleOffset(-1.0, threshold);
				} else {
					sfxb.scaleOffset(1.0, -threshold);
				}
				sfxb.skip(NEG, 4 * nSteps + 11);

				sfxb.readRegister(gate, -1.0);
				sfxb.skip(NEG, 4 * nSteps + 6);

				generateCascade(sfxb, nSteps, stepWidth, counter, hold, rateValues);
				generateCounterAdvance(sfxb, nSteps, stepWidth, counter);

				// gateHigh
				sfxb.scaleOffset(0.0, 0.9990234375);
				sfxb.writeRegister(gate, 0);
				sfxb.skip(RUN, 2);

				// gateOff
				sfxb.clear();
				sfxb.writeRegister(gate, 0);
			}

			// done: write current step's rate to LFO rate register
			sfxb.readRegister(hold, 1.0);
			sfxb.writeRegister(lfoSel == 0 ? RMP0_RATE : RMP1_RATE, 0);
		}

		// === Pitch shift audio path (runs every sample) ===
		sfxb.loadAccumulator(audioIn);
		sfxb.FXwriteDelay("delayd", 0, 0.0);

		sfxb.FXchorusReadDelay(lfoFlag, REG | COMPC, "delayd", 0);
		sfxb.FXchorusReadDelay(lfoFlag, 0, "delayd+", 1);
		sfxb.FXwriteDelay("temp", 0, 0.0);
		sfxb.FXchorusReadDelay(lfoFlag, RPTR2 | COMPC, "delayd", 0);
		sfxb.FXchorusReadDelay(lfoFlag, RPTR2, "delayd+", 1);
		sfxb.chorusScaleOffset(lfoFlag, NA | COMPC, 0);
		sfxb.FXchorusReadDelay(lfoFlag, NA, "temp", 0);
		sfxb.writeRegister(output, 0.0);

		this.getPin("Pitch Out").setRegister(output);
	}

	// Step selection cascade: loads counter, subtracts stepWidth per step,
	// branches to the matching step's rate value.
	// Total instructions: 4n - 1
	private void generateCascade(SpinFXBlock sfxb, int nSteps, double stepWidth,
			int counter, int hold, double[] rateValues) {
		sfxb.loadAccumulator(counter);
		for (int i = 0; i < nSteps - 1; i++) {
			sfxb.scaleOffset(1.0, -stepWidth);
			sfxb.skip(NEG, 2 * nSteps - 2);
		}

		// Default: last step's rate value
		sfxb.scaleOffset(0.0, rateValues[nSteps - 1]);
		sfxb.skip(RUN, 2 * nSteps - 3);

		// Sel labels: one per step (except last, which is the default above)
		for (int i = 0; i < nSteps - 1; i++) {
			sfxb.scaleOffset(0.0, rateValues[i]);
			if (i < nSteps - 2) {
				sfxb.skip(RUN, 2 * (nSteps - i - 3) + 1);
			}
		}

		// gotStep: store selected rate value
		sfxb.writeRegister(hold, 0);
	}

	// Counter advance: increments counter by stepWidth, wraps at nSteps.
	// Total instructions: 7
	private void generateCounterAdvance(SpinFXBlock sfxb, int nSteps, double stepWidth,
			int counter) {
		sfxb.readRegister(counter, 1.0);
		sfxb.scaleOffset(1.0, stepWidth);
		sfxb.writeRegister(counter, 1.0);

		double wrapPoint = nSteps * stepWidth;
		if (wrapPoint > 0.9990234375) wrapPoint = 0.9990234375;

		sfxb.scaleOffset(1.0, -wrapPoint);
		sfxb.skip(NEG, 2);
		sfxb.clear();
		sfxb.writeRegister(counter, 0);
	}

	// --- Getters and setters ---
	public void setthreshold(double val) { threshold = val; }
	public double getthreshold() { return threshold; }

	public void setnumSteps(double val) { numSteps = val; }
	public double getnumSteps() { return numSteps; }

	public void setSlope(int val) { slope = val; }
	public int getSlope() { return slope; }

	public void setSemitone(int index, int val) {
		if (index >= 0 && index < MAX_STEPS) {
			semitones[index] = Math.max(MIN_SEMITONES, Math.min(MAX_SEMITONES, val));
		}
	}
	public int getSemitone(int index) {
		if (index >= 0 && index < MAX_STEPS) return semitones[index];
		return 0;
	}

	public int getLFOSel() { return lfoSel; }
	public void setLFOSel(int val) { lfoSel = val; }

	public int getBufferSize() { return bufferSize; }
	public void setBufferSize(int val) { bufferSize = val; }

	// Interval name for UI display
	public static String intervalName(int semi) {
		if (semi == 0) return "Unison";
		String[] names = { "", "m2", "M2", "m3", "M3", "P4", "TT", "P5", "m6", "M6", "m7", "M7", "Oct" };
		int abs = Math.abs(semi);
		String dir = semi > 0 ? "" : "-";
		if (abs <= 12) return dir + names[abs];
		int remainder = abs - 12;
		return dir + "Oct+" + names[remainder];
	}

	// Legacy per-step getters/setters for serialization
	public void setsemitone1(int v) { semitones[0] = v; }
	public int getsemitone1() { return semitones[0]; }
	public void setsemitone2(int v) { semitones[1] = v; }
	public int getsemitone2() { return semitones[1]; }
	public void setsemitone3(int v) { semitones[2] = v; }
	public int getsemitone3() { return semitones[2]; }
	public void setsemitone4(int v) { semitones[3] = v; }
	public int getsemitone4() { return semitones[3]; }
	public void setsemitone5(int v) { semitones[4] = v; }
	public int getsemitone5() { return semitones[4]; }
	public void setsemitone6(int v) { semitones[5] = v; }
	public int getsemitone6() { return semitones[5]; }
	public void setsemitone7(int v) { semitones[6] = v; }
	public int getsemitone7() { return semitones[6]; }
	public void setsemitone8(int v) { semitones[7] = v; }
	public int getsemitone8() { return semitones[7]; }
	public void setsemitone9(int v) { semitones[8] = v; }
	public int getsemitone9() { return semitones[8]; }
	public void setsemitone10(int v) { semitones[9] = v; }
	public int getsemitone10() { return semitones[9]; }
	public void setsemitone11(int v) { semitones[10] = v; }
	public int getsemitone11() { return semitones[10]; }
	public void setsemitone12(int v) { semitones[11] = v; }
	public int getsemitone12() { return semitones[11]; }
}
