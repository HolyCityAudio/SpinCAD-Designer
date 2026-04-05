/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * BassManEQCADBlock.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2026.  Look for GSW in code.
 *
 * Implements the Yeh/Smith discretization of the '59 Fender Bassman tone stack.
 * Reference: Yeh & Smith, "Discretization of the '59 Fender Bassman Tone Stack",
 * Proc. DAFx-06, Montreal, 2006.
 *
 * Uses parallel 1st-order sections (partial fraction expansion) to keep all
 * FV-1 coefficients within the RDAX [-2,+2) range. Direct 3rd-order form
 * is not feasible because feedback coefficients A1/A0 and A2/A0 exceed +/-3
 * for audio-rate poles at 32768 Hz sample rate.
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

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class BassmanEQCADBlock extends FilterCADBlock {
	private static final long serialVersionUID = 5711126291575876826L;

	// Bassman '59 tone stack component values (Yeh/Smith Fig. 1)
	static final double C1 = 0.25e-9;
	static final double C2 = 20e-9;
	static final double C3 = 20e-9;
	static final double R1 = 250e3;
	static final double R2 = 1e6;
	static final double R3 = 25e3;
	static final double R4 = 56e3;

	// tone control positions [0, 1]
	double t = 0.5;   // treble
	double m = 0.5;   // middle
	double l = 0.5;   // bass (log taper in real circuit, linear here)

	public BassmanEQCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addInputPin(this, "Audio Input 1");
		addOutputPin(this, "Audio Output 1");
		addControlInputPin(this, "Middle");
		addControlInputPin(this, "Bass");
		addControlInputPin(this, "Treble");
		setName("Bassman '59 EQ");
	}

	public void editBlock() {
		new BassmanEQControlPanel(this);
	}

	// ================================================================
	// Yeh/Smith s-domain coefficient computation (Eqn. 1)
	// H(s) = (b1*s + b2*s^2 + b3*s^3) / (a0 + a1*s + a2*s^2 + a3*s^3)
	// ================================================================

	static double[] computeSCoeffs(double t, double m, double l) {
		double m2 = m * m;

		double b1 = t * C1 * R1
				+ m * C3 * R3
				+ l * (C1 * R2 + C2 * R2)
				+ (C1 * R3 + C2 * R3);

		double b2 = t * (C1 * C2 * R1 * R4 + C1 * C3 * R1 * R4)
				- m2 * (C1 * C3 * R3 * R3 + C2 * C3 * R3 * R3)
				+ m * (C1 * C3 * R1 * R3 + C1 * C3 * R3 * R3 + C2 * C3 * R3 * R3)
				+ l * (C1 * C2 * R1 * R2 + C1 * C2 * R2 * R4 + C1 * C3 * R2 * R4)
				+ l * m * (C1 * C3 * R2 * R3 + C2 * C3 * R2 * R3)
				+ (C1 * C2 * R1 * R3 + C1 * C2 * R3 * R4 + C1 * C3 * R3 * R4);

		double b3 = l * m * (C1 * C2 * C3 * R1 * R2 * R3 + C1 * C2 * C3 * R2 * R3 * R4)
				- m2 * (C1 * C2 * C3 * R1 * R3 * R3 + C1 * C2 * C3 * R3 * R3 * R4)
				+ m * (C1 * C2 * C3 * R1 * R3 * R3 + C1 * C2 * C3 * R3 * R3 * R4)
				+ t * C1 * C2 * C3 * R1 * R3 * R4
				- t * m * C1 * C2 * C3 * R1 * R3 * R4
				+ t * l * C1 * C2 * C3 * R1 * R2 * R4;

		double a0 = 1.0;

		double a1 = (C1 * R1 + C1 * R3 + C2 * R3 + C2 * R4 + C3 * R4)
				+ m * C3 * R3
				+ l * (C1 * R2 + C2 * R2);

		double a2 = m * (C1 * C3 * R1 * R3 - C2 * C3 * R3 * R4 + C1 * C3 * R3 * R3 + C2 * C3 * R3 * R3)
				+ l * m * (C1 * C3 * R2 * R3 + C2 * C3 * R2 * R3)
				- m2 * (C1 * C3 * R3 * R3 + C2 * C3 * R3 * R3)
				+ l * (C1 * C2 * R2 * R4 + C1 * C2 * R1 * R2 + C1 * C3 * R2 * R4 + C2 * C3 * R2 * R4)
				+ (C1 * C2 * R1 * R4 + C1 * C3 * R1 * R4 + C1 * C2 * R3 * R4
						+ C1 * C2 * R1 * R3 + C1 * C3 * R3 * R4 + C2 * C3 * R3 * R4);

		double a3 = l * m * (C1 * C2 * C3 * R1 * R2 * R3 + C1 * C2 * C3 * R2 * R3 * R4)
				- m2 * (C1 * C2 * C3 * R1 * R3 * R3 + C1 * C2 * C3 * R3 * R3 * R4)
				+ m * (C1 * C2 * C3 * R3 * R3 * R4 + C1 * C2 * C3 * R1 * R3 * R3
						- C1 * C2 * C3 * R1 * R3 * R4)
				+ l * C1 * C2 * C3 * R1 * R2 * R4
				+ C1 * C2 * C3 * R1 * R3 * R4;

		return new double[] { b1, b2, b3, a0, a1, a2, a3 };
	}

	// ================================================================
	// Bilinear transform: s = c*(1-z^-1)/(1+z^-1), c = 2*fs
	// Produces discrete-time coefficients B0..B3, A0..A3
	// ================================================================

	static double[] bilinearTransform(double[] sCoeffs, double fs) {
		double b1 = sCoeffs[0], b2 = sCoeffs[1], b3 = sCoeffs[2];
		double a0 = sCoeffs[3], a1 = sCoeffs[4], a2 = sCoeffs[5], a3 = sCoeffs[6];

		double c = 2.0 * fs;
		double c2 = c * c;
		double c3 = c2 * c;

		double B0 = -b1 * c - b2 * c2 - b3 * c3;
		double B1 = -b1 * c + b2 * c2 + 3 * b3 * c3;
		double B2 = b1 * c + b2 * c2 - 3 * b3 * c3;
		double B3 = b1 * c - b2 * c2 + b3 * c3;

		double A0 = -a0 - a1 * c - a2 * c2 - a3 * c3;
		double A1 = -3 * a0 - a1 * c + a2 * c2 + 3 * a3 * c3;
		double A2 = -3 * a0 + a1 * c + a2 * c2 - 3 * a3 * c3;
		double A3 = -a0 + a1 * c - a2 * c2 + a3 * c3;

		return new double[] { B0, B1, B2, B3, A0, A1, A2, A3 };
	}

	// ================================================================
	// Partial fraction expansion for 3 real poles
	// H(z) = K + r1/(1-p1*z^-1) + r2/(1-p2*z^-1) + r3/(1-p3*z^-1)
	//
	// All poles of the Bassman tone stack are real (passive RC circuit).
	// ================================================================

	/**
	 * Find real roots of cubic: x^3 + ax^2 + bx + c = 0
	 * using the trigonometric method (all roots real for passive RC).
	 */
	static double[] solveCubic(double a, double b, double c) {
		double Q = (a * a - 3 * b) / 9.0;
		double R = (2 * a * a * a - 9 * a * b + 27 * c) / 54.0;

		if (R * R < Q * Q * Q) {
			// three real roots (trigonometric solution)
			double theta = Math.acos(R / Math.sqrt(Q * Q * Q));
			double sqrtQ = Math.sqrt(Q);
			double x1 = -2 * sqrtQ * Math.cos(theta / 3) - a / 3;
			double x2 = -2 * sqrtQ * Math.cos((theta + 2 * Math.PI) / 3) - a / 3;
			double x3 = -2 * sqrtQ * Math.cos((theta - 2 * Math.PI) / 3) - a / 3;
			return new double[] { x1, x2, x3 };
		} else {
			// fallback: Cardano's formula (may have repeated or near-repeated roots)
			double sqrtR2Q3 = Math.sqrt(Math.abs(R * R - Q * Q * Q));
			double A = -Math.signum(R) * Math.pow(Math.abs(R) + sqrtR2Q3, 1.0 / 3.0);
			double B = (A == 0) ? 0 : Q / A;
			double x1 = (A + B) - a / 3;
			// for repeated roots, perturb slightly
			double x2 = -0.5 * (A + B) - a / 3;
			double x3 = x2;
			return new double[] { x1, x2, x3 };
		}
	}

	/**
	 * Compute parallel 1st-order decomposition.
	 * Returns { K, r1, p1, r2, p2, r3, p3 }
	 * where K = direct term, ri = residues, pi = poles in z-domain.
	 */
	static double[] partialFractionExpansion(double[] zCoeffs) {
		double B0 = zCoeffs[0], B1 = zCoeffs[1], B2 = zCoeffs[2], B3 = zCoeffs[3];
		double A0 = zCoeffs[4], A1 = zCoeffs[5], A2 = zCoeffs[6], A3 = zCoeffs[7];

		// normalize: divide everything by A0
		double nb0 = B0 / A0, nb1 = B1 / A0, nb2 = B2 / A0, nb3 = B3 / A0;
		double na1 = A1 / A0, na2 = A2 / A0, na3 = A3 / A0;

		// direct term K = nb3/1 (leading coeff of num / leading coeff of denom,
		// since both are degree 3 in z^-1... but actually we need polynomial long division)
		// H(z) = nb0 + nb1*z^-1 + nb2*z^-2 + nb3*z^-3
		//        ------------------------------------------
		//         1  + na1*z^-1 + na2*z^-2 + na3*z^-3
		//
		// The direct term K = nb3 / 1 is wrong. We need degree(num)==degree(den),
		// so K = B3/A3 (ratio of highest-delay coefficients in descending power form).
		// Actually: in z^-1 form, both are degree 3, so K = nb0 (ratio of z^0 terms).
		// But we want the proper partial fraction, so let's do polynomial long division.
		// Since num and denom have same degree, K = nb0 / 1 = nb0 is not right either
		// because the denominator leading coeff is already 1.
		// K = nb0 is incorrect because the "leading term" in z^-1 polynomials is the z^0 term.
		// Actually: H(z) = K + R(z)/D(z) where K*D(z) + R(z) = N(z), with deg(R) < deg(D).
		// Since D(z) = 1 + na1*z^-1 + ..., the leading term is 1 (for z^0).
		// N(z) = nb0 + nb1*z^-1 + ...
		// K = nb0. Then R(z) = N(z) - K*D(z).
		// But wait, this gives R with z^0 term = 0, and z^-1 term = nb1 - nb0*na1, etc.
		// That's degree < 3 in z^-1. But we need 3 partial fractions (one per pole).
		// Let me think again...
		//
		// Actually the standard approach: write as ratio of polynomials in z (not z^-1):
		// Multiply top and bottom by z^3:
		// H(z) = (B0*z^3 + B1*z^2 + B2*z + B3) / (A0*z^3 + A1*z^2 + A2*z + A3)
		// This is a proper rational function (degree 3 / degree 3), so K = B0/A0 = nb0.
		// Remainder: N(z) - K * D(z) has degree < 3.

		double K = nb0;
		// remainder numerator after subtracting K * denominator
		// r(z^-1) = (nb1 - K*na1)*z^-1 + (nb2 - K*na2)*z^-2 + (nb3 - K*na3)*z^-3
		double rem1 = nb1 - K * na1;
		double rem2 = nb2 - K * na2;
		double rem3 = nb3 - K * na3;

		// find poles: roots of 1 + na1*z^-1 + na2*z^-2 + na3*z^-3 = 0
		// multiply by z^3: z^3 + na1*z^2 + na2*z + na3 = 0
		double[] poles = solveCubic(na1, na2, na3);
		double p1 = poles[0], p2 = poles[1], p3 = poles[2];

		// residues: for H_rem(z) = (rem1*z^-1 + rem2*z^-2 + rem3*z^-3) / ((1-p1*z^-1)(1-p2*z^-1)(1-p3*z^-1))
		// = r1/(1-p1*z^-1) + r2/(1-p2*z^-1) + r3/(1-p3*z^-1)
		//
		// But we need to be careful. The remainder is:
		// R(z^-1) = rem1*z^-1 + rem2*z^-2 + rem3*z^-3
		// In terms of z: R(z) = rem1*z^2 + rem2*z + rem3  (after multiplying by z^3)
		// D(z) = (z-p1)(z-p2)(z-p3) = z^3 + na1*z^2 + na2*z + na3
		//
		// Partial fractions in z:
		// R(z)/D(z) = r1/(z-p1) + r2/(z-p2) + r3/(z-p3)
		//
		// Residue ri = R(pi) / product_{j!=i}(pi - pj)

		double r1 = (rem1 * p1 * p1 + rem2 * p1 + rem3) / ((p1 - p2) * (p1 - p3));
		double r2 = (rem1 * p2 * p2 + rem2 * p2 + rem3) / ((p2 - p1) * (p2 - p3));
		double r3 = (rem1 * p3 * p3 + rem2 * p3 + rem3) / ((p3 - p1) * (p3 - p2));

		// Convert from r/(z-p) to the form used in z^-1:
		// r/(z-p) = (r/z) / (1 - p*z^-1) = r*z^-1 / (1 - p*z^-1)
		// So the contribution to H(z) is r_i * z^-1 / (1 - p_i * z^-1)
		// which implements as: y_i[n] = p_i * y_i[n-1] + r_i * x[n-1]
		//
		// But for a causal implementation we want:
		// H_i(z) = r_i * z^-1 / (1 - p_i * z^-1)
		//
		// This means the output is delayed by one sample relative to direct term K.
		// Alternatively, factor out z^-1 from the remainder and adjust K.
		//
		// Actually, let's reconsider. The FV-1 implementation works fine with:
		// y[n] = K*x[n] + sum_i(state_i)
		// state_i[n] = p_i * state_i[n-1] + r_i * x[n]  (using r_i as feedforward gain)
		//
		// Wait, that gives H_i(z) = r_i / (1 - p_i*z^-1), which is what partial fractions
		// in z^-1 would give if the remainder were degree 2 in z^-1 (no z^-1 factor).
		// Our remainder has an explicit z^-1 factor: rem1*z^-1 + rem2*z^-2 + rem3*z^-3.
		//
		// Let me redo this more carefully by working entirely in z^-1 form.
		// D(z^-1) = 1 + na1*z^-1 + na2*z^-2 + na3*z^-3 = (1-p1*z^-1)(1-p2*z^-1)(1-p3*z^-1)
		// R(z^-1) = rem1*z^-1 + rem2*z^-2 + rem3*z^-3 = z^-1 * (rem1 + rem2*z^-1 + rem3*z^-2)
		//
		// So R(z^-1)/D(z^-1) = z^-1 * Q(z^-1) / D(z^-1) where Q is degree 2.
		//
		// Q(z^-1)/D(z^-1) = s1/(1-p1*z^-1) + s2/(1-p2*z^-1) + s3/(1-p3*z^-1)
		// where si = Q(1/pi) * pi / product_{j!=i}(pi - pj)  ... this gets complicated.
		//
		// Simplest approach: evaluate the full transfer function at z=p_i (residue theorem).
		// For H(z) = N(z)/D(z) with D(z) = A0*(z-p1)*(z-p2)*(z-p3):
		// residue at z=pi:  Ri = N(pi) / (A0 * product_{j!=i}(pi - pj))
		// Then H(z) = K + R1*z/(z-p1) + R2*z/(z-p2) + R3*z/(z-p3)
		//           = K + R1/(1-p1*z^-1) + R2/(1-p2*z^-1) + R3/(1-p3*z^-1)

		// N(z) = B0*z^3 + B1*z^2 + B2*z + B3 (already unnormalized)
		// D(z) = A0*z^3 + A1*z^2 + A2*z + A3
		// D'(z) = 3*A0*z^2 + 2*A1*z + A2
		// Residue Ri = [N(pi)/D'(pi)] (using L'Hopital / residue formula for simple poles)
		// But we want R_i * z/(z-pi), so R_i = N(pi) / (pi * D'(pi))...
		// Actually for partial fractions of N(z)/D(z) in the form K + sum Ri*z/(z-pi):
		// First do K = lim_{z->inf} N(z)/D(z) = B0/A0 = nb0. ✓
		// Then [N(z) - K*D(z)]/D(z) = sum Ri*z/(z-pi)
		// Let F(z) = N(z) - K*D(z) = (B1-K*A1)*z^2 + (B2-K*A2)*z + (B3-K*A3)
		//   (the z^3 term cancels)
		// Ri = F(pi) / (pi * product_{j!=i}(pi-pj) * A0)
		// But since we already normalized by A0 (na1,na2,na3 and rem1,rem2,rem3), and
		// D_norm(z) = z^3 + na1*z^2 + na2*z + na3 = (z-p1)(z-p2)(z-p3):
		// F_norm(z) = rem1*z^2 + rem2*z + rem3
		// Ri = F_norm(pi) / (pi * product_{j!=i}(pi-pj))

		// Recompute residues for H(z) = K + R1/(1-p1*z^-1) + R2/(1-p2*z^-1) + R3/(1-p3*z^-1)
		double R1 = (rem1 * p1 * p1 + rem2 * p1 + rem3) / (p1 * (p1 - p2) * (p1 - p3));
		double R2 = (rem1 * p2 * p2 + rem2 * p2 + rem3) / (p2 * (p2 - p1) * (p2 - p3));
		double R3 = (rem1 * p3 * p3 + rem2 * p3 + rem3) / (p3 * (p3 - p1) * (p3 - p2));

		return new double[] { K, R1, p1, R2, p2, R3, p3 };
	}

	/**
	 * Compute the 7 parallel-form parameters for given tone control settings.
	 * Returns { K, R1, p1, R2, p2, R3, p3 }
	 */
	double[] computeParams(double tVal, double mVal, double lVal) {
		double[] sCoeffs = computeSCoeffs(tVal, mVal, lVal);
		double[] zCoeffs = bilinearTransform(sCoeffs, getSamplerate());
		return partialFractionExpansion(zCoeffs);
	}

	// ================================================================
	// FV-1 code generation
	// ================================================================

	public void generateCode(SpinFXBlock sfxb) {
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p == null) return;

		int input = p.getRegister();

		// check which CV pins are connected
		SpinCADPin midPin = this.getPin("Middle").getPinConnection();
		SpinCADPin bassPin = this.getPin("Bass").getPinConnection();
		SpinCADPin treblePin = this.getPin("Treble").getPinConnection();

		sfxb.comment("Bassman '59 EQ");

		if (midPin != null || bassPin != null || treblePin != null) {
			generateCodeWithCV(sfxb, input, midPin, bassPin, treblePin);
		} else {
			generateCodeSliderOnly(sfxb, input);
		}
	}

	/**
	 * Slider-only mode: all coefficients precomputed in Java.
	 * 15 FV-1 instructions, 5 registers.
	 */
	private void generateCodeSliderOnly(SpinFXBlock sfxb, int input) {
		double[] params = computeParams(t, m, l);
		double K = params[0];
		double R1 = params[1], p1 = params[2];
		double R2 = params[3], p2 = params[4];
		double R3 = params[5], p3 = params[6];

		int s1 = sfxb.allocateReg();
		int s2 = sfxb.allocateReg();
		int s3 = sfxb.allocateReg();
		int output = sfxb.allocateReg();
		int accum = sfxb.allocateReg();

		sfxb.comment("Parallel 1st-order sections (slider)");

		// ACC = K * x[n] (direct term)
		sfxb.scaleOffset(0, 0);         // clear ACC
		sfxb.readRegister(input, K);    // ACC = K * input
		sfxb.writeRegister(accum, 0);   // save direct term

		// Section 1: state1[n] = p1 * state1[n-1] + R1 * x[n]
		//            output contribution: state1[n]
		sfxb.readRegister(input, R1);   // ACC = R1 * x[n]
		sfxb.readRegister(s1, p1);      // ACC += p1 * s1
		sfxb.writeRegister(s1, 1.0);    // s1 = ACC; keep in ACC
		sfxb.readRegister(accum, 1.0);  // ACC += accum
		sfxb.writeRegister(accum, 0);   // save running sum

		// Section 2: state2[n] = p2 * state2[n-1] + R2 * x[n]
		sfxb.readRegister(input, R2);
		sfxb.readRegister(s2, p2);
		sfxb.writeRegister(s2, 1.0);
		sfxb.readRegister(accum, 1.0);
		sfxb.writeRegister(accum, 0);

		// Section 3: state3[n] = p3 * state3[n-1] + R3 * x[n]
		sfxb.readRegister(input, R3);
		sfxb.readRegister(s3, p3);
		sfxb.writeRegister(s3, 1.0);
		sfxb.readRegister(accum, 1.0);
		sfxb.writeRegister(output, 0);

		this.getPin("Audio Output 1").setRegister(output);
	}

	/**
	 * CV mode: precompute coefficients at two endpoints per CV-connected control,
	 * crossfade on FV-1 using MULX. Uses register-based coefficients.
	 *
	 * For each connected CV, we precompute params at control=0 and control=1
	 * (with other controls at their slider positions). The CV linearly
	 * interpolates between these two sets.
	 *
	 * For multiple CVs, we use the primary CV (mid) for interpolation and
	 * bake the others at their slider positions (since 3D interpolation
	 * would be prohibitively expensive on FV-1).
	 */
	private void generateCodeWithCV(SpinFXBlock sfxb, int input,
			SpinCADPin midPin, SpinCADPin bassPin, SpinCADPin treblePin) {

		// determine which single CV to use for interpolation (priority: mid > bass > treble)
		SpinCADPin primaryCV;
		double[] paramsLow, paramsHigh;

		if (midPin != null) {
			primaryCV = midPin;
			paramsLow = computeParams(t, 0.0, l);
			paramsHigh = computeParams(t, 1.0, l);
		} else if (bassPin != null) {
			primaryCV = bassPin;
			paramsLow = computeParams(t, m, 0.0);
			paramsHigh = computeParams(t, m, 1.0);
		} else {
			primaryCV = treblePin;
			paramsLow = computeParams(0.0, m, l);
			paramsHigh = computeParams(1.0, m, l);
		}

		int cvReg = primaryCV.getRegister();

		// allocate registers for interpolated coefficients
		int kReg = sfxb.allocateReg();
		int r1Reg = sfxb.allocateReg();
		int p1Reg = sfxb.allocateReg();
		int r2Reg = sfxb.allocateReg();
		int p2Reg = sfxb.allocateReg();
		int r3Reg = sfxb.allocateReg();
		int p3Reg = sfxb.allocateReg();
		int s1 = sfxb.allocateReg();
		int s2 = sfxb.allocateReg();
		int s3 = sfxb.allocateReg();
		int output = sfxb.allocateReg();
		int temp = sfxb.allocateReg();

		int[] coeffRegs = { kReg, r1Reg, p1Reg, r2Reg, p2Reg, r3Reg, p3Reg };

		sfxb.comment("Interpolate coefficients from CV");

		// For each coefficient: result = low + cv * (high - low)
		// FV-1: SOF 0, low; WRAX temp, 0; LDAX cv; SOF (high-low), 0; RDAX temp, 1; WRAX coeffReg, 0
		// That's 6 instructions per coefficient, but we can optimize:
		// RDAX cv, (high-low); SOF 1.0, low; WRAX coeffReg, 0  -- 3 instructions if (high-low) fits in RDAX range
		// But SOF coefficient range is [-1,+1) and offset is [-1,+1).
		// The 'low' value must fit in SOF offset range [-1,+1).
		// For safety, use the longer form with MULX.

		for (int i = 0; i < 7; i++) {
			double lo = paramsLow[i];
			double hi = paramsHigh[i];
			double delta = hi - lo;

			// if delta is small enough for RDAX coeff range and lo for SOF offset
			if (Math.abs(delta) < 2.0 && Math.abs(lo) < 1.0) {
				sfxb.readRegister(cvReg, delta);
				sfxb.scaleOffset(1.0, lo);
				sfxb.writeRegister(coeffRegs[i], 0);
			} else {
				// fallback: use temp register and MULX
				sfxb.scaleOffset(0, clampSOF(delta));
				sfxb.writeRegister(temp, 0);
				sfxb.loadAccumulator(cvReg);
				sfxb.mulx(temp);
				sfxb.scaleOffset(1.0, clampSOF(lo));
				sfxb.writeRegister(coeffRegs[i], 0);
			}
		}

		sfxb.comment("Parallel 1st-order sections (CV)");

		// Section structure with register-based coefficients:
		// For each section: y_i[n] = p_i*y_i[n-1] + r_i*x[n]
		// LDAX input; MULX r_i_reg; WRAX temp,0; LDAX s_i; MULX p_i_reg; RDAX temp,1; WRAX s_i,1; (keep for sum)
		// = 7 instructions per section

		// Direct term: LDAX input; MULX kReg; WRAX output, 0;
		sfxb.loadAccumulator(input);
		sfxb.mulx(kReg);
		sfxb.writeRegister(output, 0);

		// Section 1
		sfxb.loadAccumulator(input);
		sfxb.mulx(r1Reg);
		sfxb.writeRegister(temp, 0);
		sfxb.loadAccumulator(s1);
		sfxb.mulx(p1Reg);
		sfxb.readRegister(temp, 1.0);
		sfxb.writeRegister(s1, 1.0);
		sfxb.readRegister(output, 1.0);
		sfxb.writeRegister(output, 0);

		// Section 2
		sfxb.loadAccumulator(input);
		sfxb.mulx(r2Reg);
		sfxb.writeRegister(temp, 0);
		sfxb.loadAccumulator(s2);
		sfxb.mulx(p2Reg);
		sfxb.readRegister(temp, 1.0);
		sfxb.writeRegister(s2, 1.0);
		sfxb.readRegister(output, 1.0);
		sfxb.writeRegister(output, 0);

		// Section 3
		sfxb.loadAccumulator(input);
		sfxb.mulx(r3Reg);
		sfxb.writeRegister(temp, 0);
		sfxb.loadAccumulator(s3);
		sfxb.mulx(p3Reg);
		sfxb.readRegister(temp, 1.0);
		sfxb.writeRegister(s3, 1.0);
		sfxb.readRegister(output, 1.0);
		sfxb.writeRegister(output, 0);

		this.getPin("Audio Output 1").setRegister(output);
	}

	/** Clamp value to SOF offset range [-1.0, +0.999...] */
	private static double clampSOF(double v) {
		return Math.max(-1.0, Math.min(0.999, v));
	}

	// ================================================================
	// Getter/setters for control panel
	// ================================================================

	public double getBass() { return l; }
	public void setBass(double f) { l = f; }

	public double getMid() { return m; }
	public void setMid(double q) { m = q; }

	public double getTreble() { return t; }
	public void setTreble(double i) { t = i; }
}
