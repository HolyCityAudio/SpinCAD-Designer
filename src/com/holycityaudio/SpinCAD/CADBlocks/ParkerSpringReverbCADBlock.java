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

// Parker spring reverb based on "Efficient Dispersion Generation Structures
// for Spring Reverb Emulation" (Julian Parker, DAFx-11, 2011).
//
// Single feedback loop: delay -> 53 stretched allpass stages -> LPF -> feedback
// The stretched allpasses (k=4) produce chirped echoes whose dispersion
// accumulates with each pass through the loop.
//
// Hand-written because stretched allpass cascades with feedback loops
// cannot be expressed in the SpinCAD Builder grammar.

package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ParkerSpringReverbCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	// Stretched allpass cascade for chirped echoes
	// k=4 stretch -> transition freq fC = fs/(2*4) = 4096 Hz at 32768 Hz
	private static final int AP_STRETCH = 4;
	private static final int NUM_APS = 52;
	// Main echo delay ~62 ms at 32768 Hz
	private static final int DELAY_LEN = 2048;

	// Control panel parameters
	private double gain = -6.0;         // dB, range -24 to 0
	private double reverbTime = 0.70;   // feedback gain, range 0.1 to 0.95
	private double damping = 0.25;      // LPF coefficient, range 0.01 to 0.7
	private double dispersion = 0.60;   // allpass coefficient, range 0.3 to 0.8

	public ParkerSpringReverbCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		setBorderColor(new Color(0x7100fc));
		addInputPin(this, "Input");
		addOutputPin(this, "Output L");
		addOutputPin(this, "Output R");
		addControlInputPin(this, "Reverb Time");
		addControlInputPin(this, "Damping");
		setName("Parker Spring");
	}

	public void editBlock() {
		new ParkerSpringReverbControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin sp = this.getPin("Input").getPinConnection();
		if (sp == null) return;
		int input = sp.getRegister();

		sfxb.comment(getName());

		// Control inputs
		SpinCADPin rtPin = this.getPin("Reverb Time").getPinConnection();
		int rtInput = (rtPin != null) ? rtPin.getRegister() : -1;
		SpinCADPin dampPin = this.getPin("Damping").getPinConnection();
		int dampInput = (dampPin != null) ? dampPin.getRegister() : -1;

		// --- Allocate delay memory ---
		sfxb.FXallocDelayMem("sp_del", DELAY_LEN);
		for (int i = 0; i < NUM_APS; i++) {
			sfxb.FXallocDelayMem("sp_ap" + i, AP_STRETCH);
		}

		// --- Allocate registers ---
		int outputL = sfxb.allocateReg();
		int outputR = sfxb.allocateReg();
		int fb = sfxb.allocateReg();      // feedback from end of loop
		int lpfTrans = sfxb.allocateReg(); // LPF at transition freq (fC ≈ 4 kHz)
		int lpfReg = sfxb.allocateReg();  // LPF state for damping

		double linearGain = Math.pow(10.0, gain / 20.0);

		// FV-1 allpass: H(z) = (z^-N - kap) / (1 - kap*z^-N)
		// Paper's allpass: H(z) = (a + z^-k) / (1 + a*z^-k)
		// So FV-1 kap = -a
		double kap = -dispersion;

		// ============================================================
		// Feedback loop: fb -> delay -> 53× stretched allpass -> LPF -> fb
		// Dispersion accumulates with each echo pass.
		// ============================================================

		// Feedback from processed output (previous cycle)
		sfxb.readRegister(fb, reverbTime);       // 1
		if (rtInput >= 0) {
			sfxb.mulx(rtInput);
		}
		// Add input
		sfxb.readRegister(input, linearGain);     // 2
		// Write into delay head
		sfxb.FXwriteDelay("sp_del", 0, 0);       // 3

		// Read from delay end into allpass cascade
		sfxb.FXreadDelay("sp_del#", 0, 1.0);     // 4

		// 52 stretched allpass stages (k=4 each)
		for (int i = 0; i < NUM_APS; i++) {       // 52 × 2 = 104
			sfxb.FXreadDelay("sp_ap" + i + "#", 0, kap);
			sfxb.FXwriteAllpass("sp_ap" + i, 0, -kap);
		}

		// Low-pass filter at transition frequency fC = fs/(2k) ≈ 4096 Hz
		// Cuts off at the first point of maximum group delay in the
		// stretched allpass cascade, removing HF content above fC.
		// kf = 1 - exp(-2π·fC/fs) ≈ 0.54
		double kfTransition = 1.0 - Math.exp(-2.0 * Math.PI * 4096.0 / 32768.0);
		sfxb.readRegisterFilter(lpfTrans, kfTransition); // 109
		sfxb.writeRegister(lpfTrans, 1.0);               // 110

		// Low-pass filter (1-pole) for additional HF damping
		sfxb.readRegisterFilter(lpfReg, damping); // 111
		sfxb.writeRegister(lpfReg, 1.0);          // 112
		if (dampInput >= 0) {
			sfxb.mulx(dampInput);
		}

		// Save output — this is the feedback source for next cycle
		sfxb.writeRegister(fb, 0);                // 113

		// ============================================================
		// Output: dry + wet to both channels
		// ============================================================
		sfxb.readRegister(input, linearGain);     // 114
		sfxb.readRegister(fb, 1.0);               // 115
		sfxb.writeRegister(outputL, 0);           // 116

		sfxb.readRegister(input, linearGain);     // 117
		sfxb.readRegister(fb, 1.0);               // 118
		sfxb.writeRegister(outputR, 0);           // 119
		// Total: 119 instructions (without control pin mulx)

		this.getPin("Output L").setRegister(outputL);
		this.getPin("Output R").setRegister(outputR);
	}

	// --- Getters/setters for control panel ---

	public double getGain() { return gain; }
	public void setGain(double v) { this.gain = v; }

	public double getReverbTime() { return reverbTime; }
	public void setReverbTime(double v) { this.reverbTime = v; }

	public double getDamping() { return damping; }
	public void setDamping(double v) { this.damping = v; }

	public double getDispersion() { return dispersion; }
	public void setDispersion(double v) { this.dispersion = v; }
}
