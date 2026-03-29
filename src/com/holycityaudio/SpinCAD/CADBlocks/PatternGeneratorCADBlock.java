/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * PatternGeneratorCADBlock.java
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
public class PatternGeneratorCADBlock extends SpinCADBlock {
	private static final long serialVersionUID = 1L;

	private static final int MAX_STEPS = 12;
	private static final int MIN_STEPS = 3;

	private transient PatternGeneratorControlPanel cp = null;

	private double threshold = 0.25;
	private double numSteps = 8;
	private double[] steps = { 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5 };

	public PatternGeneratorCADBlock(int x, int y) {
		super(x, y);
		setName("Pattern Gen");
		setBorderColor(new Color(0xf2f224));
		addControlInputPin(this, "Trigger");
		addControlInputPin(this, "Range");
		addControlOutputPin(this, "Level");
		hasControlPanel = true;
	}

	public void editBlock() {
		if (cp == null) {
			if (hasControlPanel) {
				cp = new PatternGeneratorControlPanel(this);
			}
		}
	}

	public void clearCP() {
		cp = null;
	}

	public void generateCode(SpinFXBlock sfxb) {
		sfxb.comment(getName());

		SpinCADPin sp = null;

		sp = this.getPin("Trigger").getPinConnection();
		int trigger = -1;
		if (sp != null) {
			trigger = sp.getRegister();
		}

		sp = this.getPin("Range").getPinConnection();
		int range = -1;
		if (sp != null) {
			range = sp.getRegister();
		}

		int output = sfxb.allocateReg();
		int gate = sfxb.allocateReg();
		int counter = sfxb.allocateReg();
		int hold = sfxb.allocateReg();
		int rangeVal = sfxb.allocateReg();

		int nSteps = (int) numSteps;
		if (nSteps < MIN_STEPS) nSteps = MIN_STEPS;
		if (nSteps > MAX_STEPS) nSteps = MAX_STEPS;

		double stepWidth = 1.0 / nSteps;
		if (stepWidth > 0.9990234375) stepWidth = 0.9990234375;

		// === Precompute range value into register ===
		if (this.getPin("Range").isConnected()) {
			sfxb.loadAccumulator(range);
			sfxb.writeRegister(rangeVal, 0);
		} else {
			sfxb.scaleOffset(0.0, 0.9990234375);
			sfxb.writeRegister(rangeVal, 0);
		}

		if (this.getPin("Trigger").isConnected()) {
			// === Edge detection ===
			// Instruction layout after "skp NEG, gateOff":
			//   [1]  rdax gate, -1.0
			//   [2]  skp NEG, gateHigh
			//   [3]  ldax counter
			//   [4..3+2*(n-1)]  (n-1) x {sof, skp NEG}    = 2*(n-1) instr
			//   [+1] sof 0.0, step[n] (default)
			//   [+2] skp RUN, gotStep
			//   sel labels: (n-2) x {sof, skp RUN} + 1 sof = 2*(n-2)+1 = 2n-3 instr
			//   [+1] wrax hold, 0  (gotStep)
			//   [+3] rdax counter / sof stepWidth / wrax counter
			//   [+3] sof -wrapBase / rdax rangeVal / skp NEG
			//   [+2] clr / wrax counter
			//   [+3] sof 0.999 / wrax gate / skp RUN  (gateHigh)
			//
			// Totals (instructions between skip and target):
			//   cascade = 1 + 2*(n-1) + 2 = 2n+1
			//   sel     = 2n-3
			//   skipToGateOff  = 2 + cascade + sel + 1 + 3 + 3 + 2 + 3 = 4n+12
			//   skipToGateHigh = cascade + sel + 1 + 3 + 3 + 2 = 4n+7
			// Verified: n=8 → skipToGateOff=44, skipToGateHigh=39 (matches known-good values)

			sfxb.loadAccumulator(trigger);
			sfxb.scaleOffset(1.0, -threshold);
			sfxb.skip(NEG, 4 * nSteps + 12);               // → gateOff

			sfxb.readRegister(gate, -1.0);
			sfxb.skip(NEG, 4 * nSteps + 7);                 // → gateHigh

			// === Step selection cascade ===
			// All cascade SKP NEGs have the same skip distance: 2*(n-1)
			// Verified: n=8 → cascadeSkip=14 (matches known-good value)
			sfxb.loadAccumulator(counter);
			for (int i = 0; i < nSteps - 1; i++) {
				sfxb.scaleOffset(1.0, -stepWidth);
				sfxb.skip(NEG, 2 * nSteps - 2);             // → sel[i]
			}

			// Default: last step value
			sfxb.scaleOffset(0.0, steps[nSteps - 1]);
			sfxb.skip(RUN, 2 * nSteps - 3);                 // → gotStep

			// === Sel labels ===
			for (int i = 0; i < nSteps - 1; i++) {
				sfxb.scaleOffset(0.0, steps[i]);
				if (i < nSteps - 2) {
					sfxb.skip(RUN, 2 * (nSteps - i - 3) + 1); // → gotStep
				}
				// Last sel falls through to gotStep
			}

			// === gotStep ===
			sfxb.writeRegister(hold, 0);

			// === Counter advance ===
			sfxb.readRegister(counter, 1.0);
			sfxb.scaleOffset(1.0, stepWidth);
			sfxb.writeRegister(counter, 1.0);

			// === Wrap check ===
			// Range=0 → wrap at MIN_STEPS*stepWidth, Range=1 → wrap at nSteps*stepWidth
			double wrapBase = MIN_STEPS * stepWidth;
			if (wrapBase > 0.9990234375) wrapBase = 0.9990234375;
			double wrapScale = (nSteps - MIN_STEPS) * stepWidth * 0.999;

			sfxb.scaleOffset(1.0, -wrapBase);
			sfxb.readRegister(rangeVal, -wrapScale);
			sfxb.skip(NEG, 2);                               // → gateHigh
			sfxb.clear();
			sfxb.writeRegister(counter, 0);

			// === gateHigh ===
			sfxb.scaleOffset(0.0, 0.9990234375);
			sfxb.writeRegister(gate, 0);
			sfxb.skip(RUN, 2);                               // → done

			// === gateOff ===
			sfxb.clear();
			sfxb.writeRegister(gate, 0);

			// === done ===
			sfxb.readRegister(hold, 1.0);
			sfxb.writeRegister(output, 0);

			this.getPin("Level").setRegister(output);
		}
	}

	// Getters and setters for control panel
	public void setthreshold(double val) { threshold = val; }
	public double getthreshold() { return threshold; }

	public void setnumSteps(double val) { numSteps = val; }
	public double getnumSteps() { return numSteps; }

	public void setstep(int index, double val) {
		if (index >= 0 && index < MAX_STEPS) steps[index] = val;
	}
	public double getstep(int index) {
		if (index >= 0 && index < MAX_STEPS) return steps[index];
		return 0.5;
	}

	// Legacy getters/setters for serialization compatibility with old 8-step patches
	public void setstep1(double v) { steps[0] = v; }
	public double getstep1() { return steps[0]; }
	public void setstep2(double v) { steps[1] = v; }
	public double getstep2() { return steps[1]; }
	public void setstep3(double v) { steps[2] = v; }
	public double getstep3() { return steps[2]; }
	public void setstep4(double v) { steps[3] = v; }
	public double getstep4() { return steps[3]; }
	public void setstep5(double v) { steps[4] = v; }
	public double getstep5() { return steps[4]; }
	public void setstep6(double v) { steps[5] = v; }
	public double getstep6() { return steps[5]; }
	public void setstep7(double v) { steps[6] = v; }
	public double getstep7() { return steps[6]; }
	public void setstep8(double v) { steps[7] = v; }
	public double getstep8() { return steps[7]; }
	public void setstep9(double v) { steps[8] = v; }
	public double getstep9() { return steps[8]; }
	public void setstep10(double v) { steps[9] = v; }
	public double getstep10() { return steps[9]; }
	public void setstep11(double v) { steps[10] = v; }
	public double getstep11() { return steps[10]; }
	public void setstep12(double v) { steps[11] = v; }
	public double getstep12() { return steps[11]; }
}
