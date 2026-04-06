/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * EnvelopeFollowerCADBlock.java
 * Copyright (C) 2024-2026 - Gary Worsham
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
 * General-purpose envelope follower with control voltage output.
 *
 * Detects the amplitude envelope of an audio signal and produces a
 * smoothed control voltage (0..1) suitable for modulating any parameter.
 *
 * Detection modes:
 *   AVERAGE  - rectify (ABSA) then lowpass filter
 *   RMS      - square (MULX self), lowpass filter
 *   PEAK     - peak-hold with decay (MAXX-based follower)
 *
 * Features:
 *   - Separate attack and release controls
 *   - Adjustable threshold/sensitivity (via panel or CV input)
 *   - Configurable output range (min/max)
 *   - Output smoothing filter
 *   - Side-chain input for detecting envelope from a different signal
 *   - Raw envelope output for monitoring
 */
public class EnvelopeFollowerCADBlock extends ControlCADBlock {
	private static final long serialVersionUID = 2L;

	// Detection mode constants
	public static final int DETECT_AVERAGE = 0;
	public static final int DETECT_RMS = 1;
	public static final int DETECT_PEAK = 2;

	// Maximum number of SOF doubling pairs for threshold scaling
	private static final int MAX_THRESHOLD_DOUBLINGS = 3; // 2^3 = 8x max gain

	// Parameters
	private int detectMode = DETECT_AVERAGE;
	private int gain = 2;                    // input gain in 6dB steps
	private double attackCoeff = 0.001;      // attack filter coefficient
	private double releaseCoeff = 0.0003;    // release filter coefficient
	private double threshold = 0.1;          // envelope threshold (0..1)
	private double outputMin = 0.0;          // minimum output level (0..1)
	private double outputMax = 1.0;          // maximum output level (0..1)
	private double smoothCoeff = 0.0002;     // output smoother coefficient

	private transient EnvelopeFollowerControlPanel cp = null;

	public EnvelopeFollowerCADBlock(int x, int y) {
		super(x, y);
		setBorderColor(new Color(0x02f27f));
		hasControlPanel = true;
		setName("Env Follower");
		addInputPin(this);                          // Audio Input 1 (detection source)
		addInputPin(this, "Side Chain");            // optional side-chain
		addControlInputPin(this, "Threshold CV");   // optional threshold modulation
		addControlOutputPin(this, "CV Out");        // scaled control voltage output
		addControlOutputPin(this, "Envelope");      // raw envelope for monitoring
	}

	public void editBlock() {
		if (cp == null) {
			if (hasControlPanel) {
				cp = new EnvelopeFollowerControlPanel(this);
			}
		}
	}

	public void clearCP() {
		cp = null;
	}

	public void generateCode(SpinFXBlock sfxb) {
		// Determine detection source: use side-chain if connected, else main input
		SpinCADPin p = this.getPin("Side Chain").getPinConnection();
		if (p == null) {
			p = this.getPin("Audio Input 1").getPinConnection();
		}
		if (p == null) return;
		int input = p.getRegister();

		sfxb.comment("=== Envelope Follower ===");
		sfxb.comment("Mode: " + getDetectModeName()
				+ "  Threshold: " + String.format("%.3f", threshold));

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

		// === Step 2: Envelope detection ===
		switch (detectMode) {
		case DETECT_AVERAGE:
			sfxb.absa();
			sfxb.readRegisterFilter(envReg, attackCoeff);
			sfxb.writeRegister(envReg, 0.0);
			break;

		case DETECT_RMS:
			sfxb.writeRegister(temp, 1.0);
			sfxb.mulx(temp);
			sfxb.readRegisterFilter(envReg, attackCoeff);
			sfxb.writeRegister(envReg, 0.0);
			break;

		case DETECT_PEAK:
			sfxb.absa();
			sfxb.writeRegister(temp, 0.0);
			// Decay the current envelope
			sfxb.readRegister(envReg, 1.0);
			sfxb.scaleOffset(-releaseCoeff, 0.0);
			sfxb.readRegister(envReg, 1.0);
			// Take the max of decayed envelope and new peak
			sfxb.maxx(temp, 1.0);
			sfxb.writeRegister(envReg, 0.0);
			break;
		}

		// === Step 3: Threshold scaling and CV output ===
		// Only generate CV processing code when CV Out is connected
		if (this.getPin("CV Out").isConnected()) {
			sfxb.comment("--- Threshold and CV output ---");
			sfxb.readRegister(envReg, 1.0);

			// Apply threshold CV modulation if connected
			SpinCADPin threshPin = this.getPin("Threshold CV").getPinConnection();
			if (threshPin != null) {
				// Multiply envelope by threshold CV (higher CV = more sensitive)
				sfxb.mulx(threshPin.getRegister());
			}

			// Scale envelope so that threshold level maps to ~1.0
			// Cap at MAX_THRESHOLD_DOUBLINGS to keep instruction count predictable
			double effectiveThreshold = threshold;
			if (detectMode == DETECT_RMS) {
				effectiveThreshold = threshold * threshold;
			}

			if (effectiveThreshold > 0.001) {
				double scaleFactor = 1.0 / effectiveThreshold;
				int doublings = 0;
				while (scaleFactor > 2.0 && doublings < MAX_THRESHOLD_DOUBLINGS) {
					sfxb.scaleOffset(-2.0, 0.0);
					sfxb.scaleOffset(-1.0, 0.0);
					scaleFactor /= 2.0;
					doublings++;
				}
				if (scaleFactor > 1.001) {
					sfxb.scaleOffset(-scaleFactor, 0.0);
					sfxb.scaleOffset(-1.0, 0.0);
				}
			}
			// ACC is now envelope/threshold, naturally clipped at ~0.999

			// Map to output range: output = outputMin + (outputMax - outputMin) * crossfade
			double outputRange = outputMax - outputMin;
			sfxb.scaleOffset(outputRange, outputMin);

			// Smooth the output
			sfxb.readRegisterFilter(cvReg, smoothCoeff);
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

	public double getOutputMin() { return outputMin; }
	public void setOutputMin(double m) { this.outputMin = m; }

	public double getOutputMax() { return outputMax; }
	public void setOutputMax(double m) { this.outputMax = m; }

	public double getSmoothCoeff() { return smoothCoeff; }
	public void setSmoothCoeff(double s) { this.smoothCoeff = s; }
}
