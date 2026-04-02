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

// Dattorro plate reverb from "Effect Design" paper (Jon Dattorro, 1997).
// Input diffusers -> cross-coupled tank with modulated allpasses -> multi-tap stereo output.
// Hand-written because the SpinCAD Builder parser cannot handle CHO RDA flag expressions
// (e.g., sin|reg|compc) or memory offset arithmetic (e.g., tap23#-excursion-1).

package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class DattorroPlateReverbCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	// Fixed coefficients from the Dattorro paper
	private static final double INPUT_DIFFUSION_1 = 0.75;
	private static final double INPUT_DIFFUSION_2 = 0.625;
	private static final double DECAY_DIFFUSION_1 = 0.70;
	private static final int EXCURSION = 8;

	// Slider values
	private double gain = -6.0;       // dB, range -24 to 0
	private double decay = 0.5;       // range 0.1 to 0.95
	private double damping = 0.5;     // range 0.0 to 0.95
	private double bandwidth = 0.32;  // range 0.1 to 0.7

	public DattorroPlateReverbCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		setBorderColor(new Color(0x7100fc));
		addInputPin(this, "Audio Input L");
		addInputPin(this, "Audio Input R");
		addOutputPin(this, "Audio Output L");
		addOutputPin(this, "Audio Output R");
		addControlInputPin(this, "Reverb Time");
		addControlInputPin(this, "HF Loss");
		setName("Dattorro Plate");
	}

	public void editBlock() {
		new DattorroPlateReverbControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		// Either L or R input (or both) must be connected
		SpinCADPin pL = this.getPin("Audio Input L").getPinConnection();
		SpinCADPin pR = this.getPin("Audio Input R").getPinConnection();
		if (pL == null && pR == null) return;
		int inputL = (pL != null) ? pL.getRegister() : -1;
		int inputR = (pR != null) ? pR.getRegister() : -1;

		sfxb.comment(getName());

		// Check control inputs
		SpinCADPin rtPin = this.getPin("Reverb Time").getPinConnection();
		int rtInput = (rtPin != null) ? rtPin.getRegister() : -1;

		SpinCADPin hfPin = this.getPin("HF Loss").getPinConnection();
		int hfInput = (hfPin != null) ? hfPin.getRegister() : -1;

		// --- Allocate delay memory ---
		// Delay lengths scaled for 32768 Hz (1.1x from original 29761 Hz)
		sfxb.FXallocDelayMem("predelay", 655);
		// Input diffusers
		sfxb.FXallocDelayMem("iap1", 156);
		sfxb.FXallocDelayMem("iap2", 117);
		sfxb.FXallocDelayMem("iap3", 417);
		sfxb.FXallocDelayMem("iap4", 305);
		// Left tank
		sfxb.FXallocDelayMem("tap23", 748);
		sfxb.FXallocDelayMem("del24", 4903);
		sfxb.FXallocDelayMem("tap31", 1990);
		sfxb.FXallocDelayMem("del33", 4096);
		// Right tank
		sfxb.FXallocDelayMem("tap46", 1008);
		sfxb.FXallocDelayMem("del48", 4643);
		sfxb.FXallocDelayMem("tap55", 2932);
		sfxb.FXallocDelayMem("del59", 3483);

		// --- Allocate registers ---
		int outputL = sfxb.allocateReg();
		int outputR = sfxb.allocateReg();
		int rt = sfxb.allocateReg();
		int dd2 = sfxb.allocateReg();
		int damp = sfxb.allocateReg();
		int oneminusdamp = sfxb.allocateReg();
		int bwFilter = sfxb.allocateReg();
		int tankLp1 = sfxb.allocateReg();
		int tankLp2 = sfxb.allocateReg();
		int diffin = sfxb.allocateReg();
		int temp = sfxb.allocateReg();
		int temp2 = sfxb.allocateReg();

		// --- Initialize LFOs for tank modulation ---
		sfxb.skip(RUN, 2);
		sfxb.loadSinLFO(0, 27, EXCURSION);
		sfxb.loadSinLFO(1, 23, EXCURSION);

		// --- Derive decay coefficient (rt) ---
		sfxb.scaleOffset(0, decay);
		if (rtInput >= 0) {
			sfxb.mulx(rtInput);
		}
		sfxb.writeRegister(rt, 1.0);

		// --- Compute decay_diffusion_2 = clamp(rt + 0.15, 0.25, 0.5) ---
		// ACC still holds rt from writeRegister(..., 1.0)
		sfxb.scaleOffset(1.0, -0.35);   // ACC = rt - 0.35
		sfxb.skip(NEG, 1);               // if rt < 0.35, skip clr
		sfxb.clear();                     // clamp high: ACC = 0
		sfxb.scaleOffset(1.0, 0.35);    // ACC = min(rt, 0.35)
		sfxb.scaleOffset(1.0, -0.10);   // ACC = min(rt, 0.35) - 0.10
		sfxb.skip(GEZ, 1);               // if >= 0, skip clr
		sfxb.clear();                     // clamp low: ACC = 0
		sfxb.scaleOffset(1.0, 0.25);    // ACC = clamp(rt+0.15, 0.25, 0.5)
		sfxb.writeRegister(dd2, 0);

		// --- Derive damping coefficients ---
		sfxb.scaleOffset(0, damping);
		if (hfInput >= 0) {
			sfxb.mulx(hfInput);
		}
		sfxb.writeRegister(damp, -1.0);      // damp = ACC; ACC = -damp
		sfxb.scaleOffset(1.0, 0.999);        // ACC ~ 1 - damp
		sfxb.writeRegister(oneminusdamp, 0);

		// --- Input to pre-delay (mix L+R to mono) ---
		double linearGain = Math.pow(10.0, gain / 20.0);
		if (inputL >= 0) {
			sfxb.readRegister(inputL, linearGain);
		}
		if (inputR >= 0) {
			sfxb.readRegister(inputR, linearGain);
		}
		sfxb.FXwriteDelay("predelay", 0, 0);

		// --- Input bandwidth LP (one-pole) ---
		sfxb.FXreadDelay("predelay#", 0, 1.0);
		sfxb.readRegisterFilter(bwFilter, bandwidth);
		sfxb.writeRegister(bwFilter, 1.0);

		// --- Input diffusers (4 allpasses in series) ---
		sfxb.FXreadDelay("iap1#", 0, -INPUT_DIFFUSION_1);
		sfxb.FXwriteAllpass("iap1", 0, INPUT_DIFFUSION_1);
		sfxb.FXreadDelay("iap2#", 0, -INPUT_DIFFUSION_1);
		sfxb.FXwriteAllpass("iap2", 0, INPUT_DIFFUSION_1);
		sfxb.FXreadDelay("iap3#", 0, -INPUT_DIFFUSION_2);
		sfxb.FXwriteAllpass("iap3", 0, INPUT_DIFFUSION_2);
		sfxb.FXreadDelay("iap4#", 0, -INPUT_DIFFUSION_2);
		sfxb.FXwriteAllpass("iap4", 0, INPUT_DIFFUSION_2);
		sfxb.writeRegister(diffin, 0);

		// ============================================================
		// LEFT TANK: del59 -> decay -> add diffin -> modAP -> delay -> LP -> modAP -> delay
		// ============================================================
		sfxb.FXreadDelay("del59#", 0, 1.0);
		sfxb.mulx(rt);
		sfxb.readRegister(diffin, 1.0);
		sfxb.writeRegister(temp, 0);

		// Modulated AP (tap23) — type 1: write = kd1*v + x, output = v(1-kd1²) - x*kd1
		generateModAP_Type1(sfxb, SIN0, SIN, "tap23", DECAY_DIFFUSION_1, temp, temp2);

		sfxb.FXwriteDelay("del24", 0, 0);
		sfxb.FXreadDelay("del24#", 0, 1.0);

		// LP damping
		generateTankLP(sfxb, oneminusdamp, damp, tankLp1, temp);

		sfxb.mulx(rt);
		sfxb.writeRegister(temp, 0);

		// Modulated AP (tap31) — type 2: write = -dd2*v + x, output = v(1-dd2²) + x*dd2
		generateModAP_Type2(sfxb, SIN1, COS, "tap31", dd2, temp, temp2);

		sfxb.FXwriteDelay("del33", 0, 0);
		sfxb.FXreadDelay("del33#", 0, 1.0);

		// ============================================================
		// RIGHT TANK: del33 -> decay -> add diffin -> modAP -> delay -> LP -> modAP -> delay
		// ============================================================
		sfxb.mulx(rt);
		sfxb.readRegister(diffin, 1.0);
		sfxb.writeRegister(temp, 0);

		// Modulated AP (tap46) — type 1, cosine phase of SIN0
		generateModAP_Type1(sfxb, SIN0, COS, "tap46", DECAY_DIFFUSION_1, temp, temp2);

		sfxb.FXwriteDelay("del48", 0, 0);
		sfxb.FXreadDelay("del48#", 0, 1.0);

		// LP damping
		generateTankLP(sfxb, oneminusdamp, damp, tankLp2, temp);

		sfxb.mulx(rt);
		sfxb.writeRegister(temp, 0);

		// Modulated AP (tap55) — type 2, sine phase of SIN1
		generateModAP_Type2(sfxb, SIN1, SIN, "tap55", dd2, temp, temp2);

		sfxb.FXwriteDelay("del59", 0, 0);

		// ============================================================
		// STEREO OUTPUT — multi-tap from tank delay lines
		// ============================================================
		// Left output (5 taps — reduced from 7, gain scaled 0.6->0.84 to compensate)
		sfxb.FXreadDelay("del48+", 292, 0.84);
		sfxb.FXreadDelay("tap55+", 2107, -0.84);
		sfxb.FXreadDelay("del59+", 2198, 0.84);
		sfxb.FXreadDelay("del24+", 2192, -0.84);
		sfxb.FXreadDelay("tap31+", 205, -0.84);
		sfxb.writeRegister(outputL, 0);

		// Right output (5 taps — reduced from 7, gain scaled 0.6->0.84 to compensate)
		sfxb.FXreadDelay("del24+", 389, 0.84);
		sfxb.FXreadDelay("tap31+", 1352, -0.84);
		sfxb.FXreadDelay("del33+", 2943, 0.84);
		sfxb.FXreadDelay("del48+", 2325, -0.84);
		sfxb.FXreadDelay("tap55+", 369, -0.84);
		sfxb.writeRegister(outputR, 0);

		// Set output pins
		this.getPin("Audio Output L").setRegister(outputL);
		this.getPin("Audio Output R").setRegister(outputR);
	}

	// Type 1 modulated allpass: used for first AP in each tank half.
	// cho rda pair → wrax temp2, kd → rdax temp, 1 → wra delay, -kd → rdax temp2, 1
	private void generateModAP_Type1(SpinFXBlock sfxb, int lfo, int lfoFlags,
			String delayName, double kd, int temp, int temp2) {
		sfxb.FXchorusReadDelay(lfo, lfoFlags | REG | COMPC, delayName + "#-", EXCURSION + 1);
		sfxb.FXchorusReadDelay(lfo, lfoFlags, delayName + "#-", EXCURSION);
		sfxb.writeRegister(temp2, kd);
		sfxb.readRegister(temp, 1.0);
		sfxb.FXwriteDelay(delayName, 0, -kd);
		sfxb.readRegister(temp2, 1.0);
	}

	// Type 2 modulated allpass: used for second AP in each tank half.
	// cho rda pair → wrax temp2, -1 → mulx dd2 → rdax temp, 1 → wra delay, 1 → mulx dd2 → rdax temp2, 1
	private void generateModAP_Type2(SpinFXBlock sfxb, int lfo, int lfoFlags,
			String delayName, int dd2, int temp, int temp2) {
		sfxb.FXchorusReadDelay(lfo, lfoFlags | REG | COMPC, delayName + "#-", EXCURSION + 1);
		sfxb.FXchorusReadDelay(lfo, lfoFlags, delayName + "#-", EXCURSION);
		sfxb.writeRegister(temp2, -1.0);
		sfxb.mulx(dd2);
		sfxb.readRegister(temp, 1.0);
		sfxb.FXwriteDelay(delayName, 0, 1.0);
		sfxb.mulx(dd2);
		sfxb.readRegister(temp2, 1.0);
	}

	// One-pole LP filter for tank damping.
	// filtered = input*(1-damp) + prev*damp
	private void generateTankLP(SpinFXBlock sfxb, int oneminusdamp, int damp,
			int lpReg, int temp) {
		sfxb.mulx(oneminusdamp);
		sfxb.writeRegister(temp, 0);
		sfxb.readRegister(lpReg, 1.0);
		sfxb.mulx(damp);
		sfxb.readRegister(temp, 1.0);
		sfxb.writeRegister(lpReg, 1.0);
	}

	// --- Getters/setters for control panel ---

	public double getGain() {
		return gain;
	}

	public void setGain(double gain) {
		this.gain = gain;
	}

	public double getDecay() {
		return decay;
	}

	public void setDecay(double decay) {
		this.decay = decay;
	}

	public double getDamping() {
		return damping;
	}

	public void setDamping(double damping) {
		this.damping = damping;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
}
