/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * GatedReverbControlCADBlock.java
 * Copyright (C) 2024 - Gary Worsham
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

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

/**
 * Gated Reverb Controller - Envelope-following control voltage generator.
 *
 * Listens to the same audio signal as a reverb block's input and produces
 * a smoothed control voltage output that can modulate reverb time (or any
 * other parameter) based on signal activity.
 *
 * Detection modes:
 *   AVERAGE  - rectify (ABSA) then lowpass filter
 *   RMS      - square (MULX self), lowpass filter (no LOG/EXP needed;
 *              threshold is squared internally to match)
 *   PEAK     - peak-hold with decay (MAXX-based follower)
 *
 * Operation:
 *   The detected envelope is scaled relative to the threshold so that
 *   the threshold maps to a crossfade of ~1.0. The crossfade smoothly
 *   interpolates between idleRT and activeRT. A final output smoother
 *   ensures glitch-free transitions.
 *
 * Connect the "CV Out" to the Reverb Designer's "Reverb_Time" control input.
 */
public class GatedReverbControlCADBlock extends ControlCADBlock {
	private static final long serialVersionUID = 1L;

	// Detection mode constants
	public static final int DETECT_AVERAGE = 0;
	public static final int DETECT_RMS = 1;
	public static final int DETECT_PEAK = 2;

	// Parameters
	private int detectMode = DETECT_AVERAGE;
	private int gain = 2;                    // input gain in 6dB steps
	private double attackCoeff = 0.001;      // attack filter coefficient
	private double releaseCoeff = 0.0003;    // release filter coefficient
	private double threshold = 0.1;          // envelope threshold (0..1)
	private double activeRT = 0.9;           // reverb time when signal present (0..1)
	private double idleRT = 0.1;             // reverb time when signal absent (0..1)
	private double smoothCoeff = 0.0002;     // output smoother coefficient

	private transient GatedReverbControlControlPanel cp = null;

	public GatedReverbControlCADBlock(int x, int y) {
		super(x, y);
		setBorderColor(new Color(0x009595));
		hasControlPanel = true;
		setName("Gate CV");
		addInputPin(this);                          // Audio Input 1
		addControlOutputPin(this, "CV Out");        // control voltage output
		addControlOutputPin(this, "Envelope");      // raw envelope for monitoring
	}

	public void editBlock() {
		if (cp == null) {
			if (hasControlPanel) {
				cp = new GatedReverbControlControlPanel(this);
			}
		}
	}

	public void clearCP() {
		cp = null;
	}

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p == null) return;
		int input = p.getRegister();

		sfxb.comment("=== Gated Reverb Controller ===");
		sfxb.comment("Mode: " + getDetectModeName()
				+ "  Threshold: " + String.format("%.3f", threshold));
		sfxb.comment("Active RT: " + String.format("%.2f", activeRT)
				+ "  Idle RT: " + String.format("%.2f", idleRT));

		// Allocate registers
		int envReg = sfxb.allocateReg();     // envelope follower state
		int cvReg = sfxb.allocateReg();      // smoothed CV output
		int temp = sfxb.allocateReg();       // scratch register

		// === Step 1: Read input and apply gain ===
		sfxb.comment("--- Signal detection ---");
		sfxb.readRegister(input, 1.0);
		for (int i = 0; i < gain; i++) {
			sfxb.scaleOffset(-2.0, 0.0);     // ACC *= -2
		}
		if ((gain & 1) == 1) {
			sfxb.scaleOffset(-1.0, 0.0);     // fix sign for odd gain count
		}
		// ACC now holds the gained input signal

		// === Step 2: Envelope detection ===
		switch (detectMode) {
		case DETECT_AVERAGE:
			// Rectify then lowpass filter
			sfxb.absa();                             // ACC = |gained input|
			sfxb.readRegisterFilter(envReg, attackCoeff); // RDFX: 1-pole LPF
			sfxb.writeRegister(envReg, 0.0);         // save envelope, clear ACC
			break;

		case DETECT_RMS:
			// Square the signal: save gained input, then multiply by itself.
			// No LOG/EXP sqrt needed — we compare against threshold^2 instead.
			sfxb.writeRegister(temp, 1.0);           // temp = gained, ACC = gained
			sfxb.mulx(temp);                         // ACC = gained^2 (always positive)
			sfxb.readRegisterFilter(envReg, attackCoeff); // smooth the squared signal
			sfxb.writeRegister(envReg, 0.0);         // save envelope, clear ACC
			break;

		case DETECT_PEAK:
			// Peak detection: MAXX of new peak vs decayed previous envelope
			sfxb.absa();                             // ACC = |gained input|
			sfxb.writeRegister(temp, 0.0);           // temp = |gained|, clear ACC
			// Decay the current envelope: env = env * (1 - releaseCoeff)
			sfxb.readRegister(envReg, 1.0);          // ACC = envReg
			sfxb.scaleOffset(-releaseCoeff, 0.0);    // ACC = env * (-releaseCoeff)
			sfxb.readRegister(envReg, 1.0);          // ACC = env - env*releaseCoeff
			// Take the max of decayed envelope and new absolute peak
			sfxb.maxx(temp, 1.0);                    // ACC = max(decayed_env, |new_peak|)
			sfxb.writeRegister(envReg, 0.0);         // save envelope, clear ACC
			break;
		}

		// === Step 3: Threshold comparison and CV crossfade ===
		// Only needed if CV Out is connected
		if (this.getPin("CV Out").isConnected()) {
			// The envelope is positive (0 to ~1). We scale it so that the
			// threshold level maps to ~1.0, then use it as a crossfade between
			// idleRT and activeRT. The FV-1 clips at ~0.999 which provides
			// natural saturation above threshold.
			//
			// For RMS mode, the envelope is squared, so we compare against
			// threshold^2 to match.

			sfxb.comment("--- Threshold and CV crossfade ---");
			sfxb.readRegister(envReg, 1.0);              // ACC = envelope

			// Compute effective threshold for scaling
			double effectiveThreshold = threshold;
			if (detectMode == DETECT_RMS) {
				effectiveThreshold = threshold * threshold; // match squared domain
			}

			// Scale envelope so that threshold level -> ~1.0
			// Use SOF for multiplication: each SOF(-2,0) + SOF(-1,0) = *2
			if (effectiveThreshold > 0.001) {
				double scaleFactor = 1.0 / effectiveThreshold;
				while (scaleFactor > 2.0) {
					sfxb.scaleOffset(-2.0, 0.0);
					sfxb.scaleOffset(-1.0, 0.0);
					scaleFactor /= 2.0;
				}
				if (scaleFactor > 1.001) {
					sfxb.scaleOffset(-scaleFactor, 0.0);
					sfxb.scaleOffset(-1.0, 0.0);
				}
			}
			// ACC is now envelope/threshold, naturally clipped at ~0.999

			// Crossfade: output = idleRT + (activeRT - idleRT) * crossfade
			double rtRange = activeRT - idleRT;
			sfxb.scaleOffset(rtRange, idleRT);           // ACC = rtRange * crossfade + idleRT

			// Smooth the output with a lowpass filter for glitch-free transitions
			sfxb.readRegisterFilter(cvReg, smoothCoeff); // RDFX
			sfxb.writeRegister(cvReg, 0.0);

			this.getPin("CV Out").setRegister(cvReg);
		}

		// === Set output pins ===
		this.getPin("Envelope").setRegister(envReg);
	}

	// =====================================================
	// Getters/Setters
	// =====================================================
	public String getDetectModeName() {
		switch (detectMode) {
		case DETECT_AVERAGE: return "Average";
		case DETECT_RMS: return "RMS";
		case DETECT_PEAK: return "Peak";
		default: return "Unknown";
		}
	}

	public int getDetectMode() { return detectMode; }
	public void setDetectMode(int mode) { this.detectMode = mode; }

	public int getGain() { return gain; }
	public void setGain(int g) { this.gain = g; }

	public double getAttack() { return attackCoeff; }
	public void setAttack(double a) { this.attackCoeff = a; }

	public double getRelease() { return releaseCoeff; }
	public void setRelease(double r) { this.releaseCoeff = r; }

	public double getThreshold() { return threshold; }
	public void setThreshold(double t) { this.threshold = t; }

	public double getActiveRT() { return activeRT; }
	public void setActiveRT(double rt) { this.activeRT = rt; }

	public double getIdleRT() { return idleRT; }
	public void setIdleRT(double rt) { this.idleRT = rt; }

	public double getSmoothCoeff() { return smoothCoeff; }
	public void setSmoothCoeff(double s) { this.smoothCoeff = s; }
}
