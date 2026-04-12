/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * OilCanDelayCADBlock.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.
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
 * Oil Can Delay - a modulated delay where the LFO rate synchronizes with the
 * delay time so that pitch-bend effects accumulate coherently through feedback.
 *
 * Uses ADDR_PTR + RMPA for the delay read (runtime-variable position) and the
 * hardware SIN LFO via CHO RDAL for the modulation waveform. When the Delay
 * Time control input is connected, LOG/EXP computes the inverse relationship
 * to keep the LFO rate locked to 1/(2*delay_time) * ratio.
 */
public class OilCanDelayCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	// Slider defaults
	private int delayLength = 8192;     // samples (100-500 ms at 32768 Hz)
	private int ratio = 1;              // sync ratio: LFO cycles per delay period
	private double modDepth = 5.0;      // modulation depth in milliseconds (±)
	private double fbkGain = 0.5;       // feedback gain (linear, 0-0.95)
	private double dampFreq = 2000.0;   // damping LP frequency in Hz
	private int lfoSel = 0;             // 0 = SIN0, 1 = SIN1

	// Fixed range
	private static final int MIN_DELAY = 3277;    // ~100 ms
	private static final int MAX_DELAY = 16384;   // ~500 ms
	private static final int SAMPLERATE = 32768;
	private static final int MARGIN = 64;
	private static final double SMOOTH_COEFF = 0.001;

	// LOG/EXP constants for dynamic rate sync.
	// After  sof 0.8, 0.2  the time control T is in [0.2, 1.0] proportional
	// to delay_samples.  log -1, C  /  exp 1, 0  produces (1/T)*2^(16C).
	// C = -0.16 keeps the LOG result safely negative across the full range.
	// rateScaleBase maps the EXP output to the SIN_RATE register fraction:
	//   rateScaleBase = 1 / (512 * 2^(16*C))  *  (2pi * 2^17 / Fs)
	//                 ≈ 0.2895   (for C = -0.16)
	private static final double LOG_OFFSET = -0.16;
	private static final double RATE_SCALE_BASE = 0.2895;

	public OilCanDelayCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio Input");
		addInputPin(this, "Feedback In");
		addOutputPin(this, "Audio Output");
		addControlInputPin(this, "Delay Time");
		addControlInputPin(this, "Mod Width");
		addControlInputPin(this, "Feedback Gain");
		setBorderColor(new Color(0xd0a020));
		setName("Oil Can Delay");
	}

	public void editBlock() {
		new OilCanDelayControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin sp = this.getPin("Audio Input").getPinConnection();
		if (sp == null) return;
		int input = sp.getRegister();
		if (input == -1) return;

		sfxb.comment(getName());

		// --- resolve input/control connections ---
		int fbkInput = -1;
		sp = this.getPin("Feedback In").getPinConnection();
		if (sp != null) fbkInput = sp.getRegister();

		int timeCtrl = -1;
		sp = this.getPin("Delay Time").getPinConnection();
		if (sp != null) timeCtrl = sp.getRegister();

		int widthCtrl = -1;
		sp = this.getPin("Mod Width").getPinConnection();
		if (sp != null) widthCtrl = sp.getRegister();

		int fbkCtrl = -1;
		sp = this.getPin("Feedback Gain").getPinConnection();
		if (sp != null) fbkCtrl = sp.getRegister();

		// --- compute buffer geometry ---
		// When time control is connected the delay sweeps the full range,
		// so the buffer must accommodate MAX_DELAY + modulation.
		int effectiveMaxDelay = (timeCtrl != -1) ? MAX_DELAY : delayLength;
		int modAmplitude = (int)(modDepth * SAMPLERATE / 1000.0);
		if (modAmplitude < 1) modAmplitude = 1;
		int bufferLength = effectiveMaxDelay + modAmplitude + MARGIN;

		int delayOffset = sfxb.getDelayMemAllocated() + 1;
		sfxb.FXallocDelayMem("oilcan", bufferLength);

		// --- allocate registers ---
		int output   = sfxb.allocateReg();
		int fbkReg   = sfxb.allocateReg();
		int dampReg  = sfxb.allocateReg();

		int timeSmooth = -1;
		if (timeCtrl != -1) {
			timeSmooth = sfxb.allocateReg();
		}

		// --- damping coefficient from frequency ---
		double dampCoeff = freqToFilt(dampFreq);

		// --- compute initial LFO rate ---
		double initDelay = (timeCtrl != -1)
			? (MIN_DELAY + MAX_DELAY) / 2.0   // mid-range; overridden dynamically
			: delayLength;
		double rateHz = ratio * (double)SAMPLERATE / (2.0 * initDelay);
		rateHz = Math.min(rateHz, 20.0);       // clamp to SIN LFO max

		// Convert Hz to WLDS register value (0-511)
		int freqReg = (int)(Math.pow(2.0, 17.0) * ((2.0 * Math.PI * rateHz) / SAMPLERATE));
		freqReg = Math.max(0, Math.min(511, freqReg));

		// --- initialise SIN LFO (first sample only) ---
		// Use int overload: amp=32767 (max 15-bit) gives RDAL peak ≈ ±1.
		// The double overload with amp=1.0 overflows the 15-bit field to zero.
		sfxb.skip(RUN, 1);
		sfxb.loadSinLFO(lfoSel, freqReg, 32767);

		// =============================================================
		//  Dynamic rate update  (only when Delay Time control connected)
		// =============================================================
		if (timeCtrl != -1) {
			double rateScale = ratio * RATE_SCALE_BASE;

			sfxb.readRegister(timeCtrl, 1.0);                // ACC = raw control
			sfxb.readRegisterFilter(timeSmooth, SMOOTH_COEFF);// smooth
			sfxb.writeRegister(timeSmooth, 1.0);              // store; ACC = smoothed

			// map 0..1 → 0.2..1.0 (T proportional to delay_samples)
			sfxb.scaleOffset(0.8, 0.2);
			sfxb.log(-1.0, LOG_OFFSET);                       // ≈ log2(1/T)/16 shifted
			sfxb.exp(1.0, 0);                                 // ≈ (1/T) * K
			sfxb.scaleOffset(rateScale, 0);                   // scale to SIN_RATE units
			sfxb.writeRegister(lfoSel == 0 ? SIN0_RATE : SIN1_RATE, 0);
		}

		// =============================================================
		//  Write input + feedback to delay head
		// =============================================================
		if (fbkInput != -1) {
			// External feedback path (e.g. routed through other blocks)
			sfxb.readRegister(fbkInput, fbkGain);
		} else {
			// Internal feedback from damped output
			sfxb.readRegister(fbkReg, fbkGain);
		}
		if (fbkCtrl != -1) {
			sfxb.mulx(fbkCtrl);
		}
		sfxb.readRegister(input, 1.0);
		sfxb.FXwriteDelay("oilcan", 0, 0);    // wra delay, 0

		// =============================================================
		//  Modulated delay read  (CHO RDAL → scale → ADDR_PTR → RMPA)
		// =============================================================

		// 1. Read SIN LFO waveform
		sfxb.chorusReadValue(lfoSel);          // ACC ≈ ±1.0

		// 2. Scale by Mod Width control (if connected)
		if (widthCtrl != -1) {
			sfxb.mulx(widthCtrl);
		}

		// 3. Build normalised position inside the buffer (0..1) then map
		//    to absolute ADDR_PTR value via  sof bufFrac, offFrac.
		double modFrac = (double)modAmplitude / bufferLength;
		double bufFrac = (double)bufferLength / 32768.0;
		double offFrac = (double)delayOffset / 32768.0;

		if (timeCtrl != -1) {
			// Dynamic delay: LFO*modFrac + timeCtrl*(MAX-MIN)/buf + MIN/buf
			double rangeFracBuf = (double)(MAX_DELAY - MIN_DELAY) / bufferLength;
			double minFracBuf   = (double)MIN_DELAY / bufferLength;

			sfxb.scaleOffset(modFrac, 0);                     // ACC = LFO*modFrac
			sfxb.readRegister(timeSmooth, rangeFracBuf);       // ACC += time*rangeFrac
			sfxb.scaleOffset(1.0, minFracBuf);                 // ACC += minFrac
		} else {
			// Fixed delay from slider
			double tapFrac = (double)delayLength / bufferLength;
			sfxb.scaleOffset(modFrac, tapFrac);                // ACC = LFO*mod + tap
		}

		// Map buffer-relative position → absolute address
		sfxb.scaleOffset(bufFrac, offFrac);

		sfxb.writeRegister(ADDR_PTR, 0);       // set read pointer
		sfxb.readDelayPointer(1.0);             // RMPA 1.0

		// =============================================================
		//  Output + feedback damping
		// =============================================================
		sfxb.writeRegister(output, 1.0);        // store to output, keep in ACC
		sfxb.readRegisterFilter(dampReg, dampCoeff); // first-order lowpass
		sfxb.writeRegister(dampReg, 1.0);       // update filter state
		sfxb.writeRegister(fbkReg, 0);          // store damped signal as feedback

		this.getPin("Audio Output").setRegister(output);
	}

	// ========================= getters / setters =========================

	public int getDelayLength() { return delayLength; }
	public void setDelayLength(int d) {
		delayLength = Math.max(MIN_DELAY, Math.min(MAX_DELAY, d));
	}

	public int getRatio() { return ratio; }
	public void setRatio(int r) {
		ratio = Math.max(1, Math.min(4, r));
	}

	public double getModDepth() { return modDepth; }
	public void setModDepth(double d) {
		modDepth = Math.max(0.5, Math.min(20.0, d));
	}

	public double getFbkGain() { return fbkGain; }
	public void setFbkGain(double g) {
		fbkGain = Math.max(0, Math.min(0.95, g));
	}

	public double getDampFreq() { return dampFreq; }
	public void setDampFreq(double f) {
		dampFreq = Math.max(200, Math.min(8000, f));
	}

	public int getLfoSel() { return lfoSel; }
	public void setLfoSel(int s) {
		lfoSel = (s == 0) ? 0 : 1;
	}

	public static int getMinDelay() { return MIN_DELAY; }
	public static int getMaxDelay() { return MAX_DELAY; }
}
