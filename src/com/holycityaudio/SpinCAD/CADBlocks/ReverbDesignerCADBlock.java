/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * ReverbDesignerCADBlock.java
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

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

@SuppressWarnings("unused")
public class ReverbDesignerCADBlock extends SpinCADBlock {
	private static final long serialVersionUID = 1L;

	// Topology constants
	public static final int TOPOLOGY_TWO_LOOP = 0;
	public static final int TOPOLOGY_DATTORRO = 1;
	public static final int TOPOLOGY_RING_FDN = 2;

	// Size constants
	public static final int SIZE_SMALL = 0;
	public static final int SIZE_MEDIUM = 1;
	public static final int SIZE_LARGE = 2;

	// Shimmer constants
	public static final int SHIMMER_OFF = 0;
	public static final int SHIMMER_INPUT_ONLY = 1;
	public static final int SHIMMER_INPUT_AND_FEEDBACK = 2;

	// LFO depth constants
	public static final int LFO_NONE = 0;
	public static final int LFO_SUBTLE = 1;
	public static final int LFO_WIDE = 2;

	// Design-time parameters
	private int topology = TOPOLOGY_DATTORRO;
	private int sizePreset = SIZE_MEDIUM;
	private boolean stereoOutput = true;
	private int shimmerMode = SHIMMER_OFF;
	private int shimmerPitchSemitones = 12; // octave up
	private int lfoDepth = LFO_SUBTLE;
	private boolean preDelayEnabled = false;

	// Runtime parameter defaults (used when not pot-controlled)
	private double reverbTime = 0.7;
	private double hfDamping = 0.4;
	private double lfDamping = 0.02;
	private double dryWet = 0.5;
	private double preDelayAmount = 0.3;
	private double inputBandwidth = 0.5; // filter coefficient
	private double diffusion = 0.5;
	private double shimmerLevel = 0.3;

	private transient ReverbDesignerControlPanel cp = null;

	public ReverbDesignerCADBlock(int x, int y) {
		super(x, y);
		setName("Reverb Designer");
		setBorderColor(new Color(0x7100fc));
		addInputPin(this, "Audio In");
		addOutputPin(this, "Out L");
		addOutputPin(this, "Out R");
		addControlInputPin(this, "Reverb_Time");
		addControlInputPin(this, "HF_Damping");
		addControlInputPin(this, "Mix");
		hasControlPanel = true;
	}

	public void editBlock() {
		if (cp == null) {
			if (hasControlPanel) {
				cp = new ReverbDesignerControlPanel(this);
			}
		}
	}

	public void clearCP() {
		cp = null;
	}

	// =====================================================
	// Pitch shift coefficient calculation (from ArpeggiatorCADBlock)
	// =====================================================
	private int semitoneToCoefficient(int semi) {
		if (semi == 0) return 0;
		double octaves = semi / 12.0;
		int coefficient;
		if (semi > 0) {
			coefficient = (int) (16384 * (Math.pow(2.0, octaves) - 1));
		} else {
			coefficient = (int) (-16384 * (1 - Math.pow(2.0, octaves)));
		}
		return coefficient;
	}

	// =====================================================
	// Delay length tables
	// =====================================================

	// Size scaling factors: Small=0.6, Medium=1.0, Large=1.3
	private double getSizeScale() {
		switch (sizePreset) {
		case SIZE_SMALL: return 0.6;
		case SIZE_LARGE: return 1.3;
		default: return 1.0;
		}
	}

	private int scaled(int base) {
		return Math.max(1, (int) (base * getSizeScale()));
	}

	// Two-Loop delay lengths (medium base)
	private static final int[] TL_INPUT_AP = { 122, 303, 553, 922 };
	private static final int[] TL_LOOP_AP = { 3823, 4732 };
	private static final int[] TL_LOOP_DEL = { 6512, 5016 };

	// Dattorro delay lengths (medium base, from the paper scaled for 32768Hz)
	private static final int[] DAT_INPUT_AP = { 156, 117, 417, 305 };
	private static final double DAT_INPUT_DIFF_1 = 0.75;
	private static final double DAT_INPUT_DIFF_2 = 0.625;
	// Tank: 2 sides, each with 2 APs and 2 delays
	private static final int[] DAT_TANK_AP = { 740, 1982, 1000, 2924 }; // side A ap1, ap2, side B ap1, ap2
	private static final int[] DAT_TANK_DEL = { 4903, 4096, 4643, 3483 }; // side A d1, d2, side B d1, d2
	private static final double DAT_DECAY_DIFF_1 = 0.70;

	// Ring FDN delay lengths (medium base, from rev_pl_1)
	private static final int[] RING_INPUT_AP = { 156, 223, 332, 448 };
	private static final int[] RING_STAGE_AP = { 1251, 1443, 1582, 1274 };
	private static final int[] RING_STAGE_DEL = { 3620, 4591, 4387, 3679 };

	// Number of ring stages (auto-reduced when shimmer enabled)
	private int getRingStages() {
		if (shimmerMode != SHIMMER_OFF) return 3;
		return 4;
	}

	// =====================================================
	// Resource estimation
	// =====================================================
	public int estimateInstructions() {
		int count = 12; // input section always
		count += 2; // DC blocker (RDFX + WRHX)
		switch (topology) {
		case TOPOLOGY_TWO_LOOP:
			count += 20; // tank + output base
			break;
		case TOPOLOGY_DATTORRO:
			count += 42; // tank + output base
			break;
		case TOPOLOGY_RING_FDN:
			count += (getRingStages() == 3) ? 32 : 42;
			break;
		}
		if (stereoOutput) count += 5;
		if (shimmerMode != SHIMMER_OFF) count += 14;
		if (preDelayEnabled) count += 2;
		if (lfoDepth == LFO_SUBTLE) count += 9;
		else if (lfoDepth == LFO_WIDE) count += 15;
		// dry/wet mix
		count += 4;
		return count;
	}

	public int estimateMemory() {
		int mem = 0;
		switch (topology) {
		case TOPOLOGY_TWO_LOOP:
			for (int v : TL_INPUT_AP) mem += scaled(v);
			for (int v : TL_LOOP_AP) mem += scaled(v);
			for (int v : TL_LOOP_DEL) mem += scaled(v);
			break;
		case TOPOLOGY_DATTORRO:
			for (int v : DAT_INPUT_AP) mem += scaled(v);
			for (int v : DAT_TANK_AP) mem += scaled(v);
			for (int v : DAT_TANK_DEL) mem += scaled(v);
			break;
		case TOPOLOGY_RING_FDN:
			int stages = getRingStages();
			for (int v : RING_INPUT_AP) mem += scaled(v);
			for (int i = 0; i < stages; i++) mem += scaled(RING_STAGE_AP[i]);
			for (int i = 0; i < stages; i++) mem += scaled(RING_STAGE_DEL[i]);
			break;
		}
		if (shimmerMode != SHIMMER_OFF) mem += 4097; // 4096 + 1 temp
		if (preDelayEnabled) mem += (int)(3276 * preDelayAmount) + 1;
		return mem;
	}

	// =====================================================
	// Code generation
	// =====================================================
	private String getTopologyName() {
		switch (topology) {
		case TOPOLOGY_TWO_LOOP: return "Two-Loop";
		case TOPOLOGY_DATTORRO: return "Dattorro Plate";
		case TOPOLOGY_RING_FDN: return "Ring FDN";
		default: return "Unknown";
		}
	}

	private String getSizeName() {
		switch (sizePreset) {
		case SIZE_SMALL: return "Small";
		case SIZE_MEDIUM: return "Medium";
		case SIZE_LARGE: return "Large";
		default: return "Unknown";
		}
	}

	private String getShimmerName() {
		switch (shimmerMode) {
		case SHIMMER_OFF: return "Off";
		case SHIMMER_INPUT_ONLY: return "Input Only";
		case SHIMMER_INPUT_AND_FEEDBACK: return "Input + Feedback";
		default: return "Unknown";
		}
	}

	private String getLfoName() {
		switch (lfoDepth) {
		case LFO_NONE: return "None";
		case LFO_SUBTLE: return "Subtle";
		case LFO_WIDE: return "Wide";
		default: return "Unknown";
		}
	}

	private void emitSettingsComments(SpinFXBlock sfxb, int rtPin, int hfPin, int mixPin) {
		sfxb.comment("=== Reverb Designer Settings ===");
		sfxb.comment("Topology: " + getTopologyName());
		sfxb.comment("Size: " + getSizeName() + " (scale=" + getSizeScale() + ")");
		sfxb.comment("Stereo: " + stereoOutput);
		sfxb.comment("LFO Mod: " + getLfoName());
		sfxb.comment("Shimmer: " + getShimmerName()
				+ (shimmerMode != SHIMMER_OFF ? " pitch=" + shimmerPitchSemitones + " semi" : ""));
		sfxb.comment("Pre-Delay: " + (preDelayEnabled ? "On (" + String.format("%.0f", preDelayAmount * 100) + " ms)" : "Off"));
		sfxb.comment("--- Runtime Params ---");
		sfxb.comment("Reverb Time: " + (rtPin >= 0 ? "POT" : String.format("%.2f", reverbTime)));
		sfxb.comment("HF Damping: " + (hfPin >= 0 ? "POT" : String.format("%.2f", hfDamping)));
		sfxb.comment("LF Damping: " + String.format("%.3f", lfDamping));
		sfxb.comment("Dry/Wet: " + (mixPin >= 0 ? "POT" : String.format("%.2f", dryWet)));
		sfxb.comment("Input BW: " + String.format("%.2f", inputBandwidth));
		sfxb.comment("Diffusion: " + String.format("%.2f", diffusion));
		if (shimmerMode != SHIMMER_OFF) {
			sfxb.comment("Shimmer Level: " + String.format("%.2f", shimmerLevel));
		}
		sfxb.comment("Est. instr: ~" + estimateInstructions() + "  mem: ~" + estimateMemory());
		sfxb.comment("================================");
	}

	public void generateCode(SpinFXBlock sfxb) {
		sfxb.comment("Reverb Designer");

		SpinCADPin sp = this.getPin("Audio In").getPinConnection();
		if (sp == null) return;
		int audioIn = sp.getRegister();

		// Resolve control input pins
		sp = this.getPin("Reverb_Time").getPinConnection();
		int rtPin = (sp != null) ? sp.getRegister() : -1;

		sp = this.getPin("HF_Damping").getPinConnection();
		int hfPin = (sp != null) ? sp.getRegister() : -1;

		sp = this.getPin("Mix").getPinConnection();
		int mixPin = (sp != null) ? sp.getRegister() : -1;

		emitSettingsComments(sfxb, rtPin, hfPin, mixPin);

		switch (topology) {
		case TOPOLOGY_TWO_LOOP:
			generateTwoLoop(sfxb, audioIn, rtPin, hfPin, mixPin);
			break;
		case TOPOLOGY_DATTORRO:
			generateDattorro(sfxb, audioIn, rtPin, hfPin, mixPin);
			break;
		case TOPOLOGY_RING_FDN:
			generateRingFDN(sfxb, audioIn, rtPin, hfPin, mixPin);
			break;
		}
	}

	// =====================================================
	// Two-Loop Topology
	// =====================================================
	private void generateTwoLoop(SpinFXBlock sfxb, int audioIn, int rtPin, int hfPin, int mixPin) {
		// Allocate delay memory
		sfxb.FXallocDelayMem("api1", scaled(TL_INPUT_AP[0]));
		sfxb.FXallocDelayMem("api2", scaled(TL_INPUT_AP[1]));
		sfxb.FXallocDelayMem("api3", scaled(TL_INPUT_AP[2]));
		sfxb.FXallocDelayMem("api4", scaled(TL_INPUT_AP[3]));
		sfxb.FXallocDelayMem("lap1", scaled(TL_LOOP_AP[0]));
		sfxb.FXallocDelayMem("del1", scaled(TL_LOOP_DEL[0]));
		sfxb.FXallocDelayMem("lap2", scaled(TL_LOOP_AP[1]));
		sfxb.FXallocDelayMem("del2", scaled(TL_LOOP_DEL[1]));

		// Shimmer delay if needed
		if (shimmerMode != SHIMMER_OFF) {
			sfxb.FXallocDelayMem("shim", 4096);
			sfxb.FXallocDelayMem("stimp", 1);
		}

		// Pre-delay: fixed delay line sized to slider setting
		int preDelaySamples = preDelayEnabled ? Math.max(1, (int)(3276 * preDelayAmount)) : 0;
		if (preDelayEnabled) {
			sfxb.FXallocDelayMem("pdel", preDelaySamples);
		}

		// Allocate registers
		int apout = sfxb.allocateReg();
		int outputL = sfxb.allocateReg();
		int outputR = stereoOutput ? sfxb.allocateReg() : -1;
		int lp1 = sfxb.allocateReg();
		int lp2 = sfxb.allocateReg();
		int bwReg = sfxb.allocateReg();
		int dry = sfxb.allocateReg();
		int temp = sfxb.allocateReg();
		int shimLp = (shimmerMode != SHIMMER_OFF) ? sfxb.allocateReg() : -1;
		int shimOut = (shimmerMode != SHIMMER_OFF) ? sfxb.allocateReg() : -1;
		int dcBlock = sfxb.allocateReg(); // DC blocker filter state

		// === Init section (LFOs) ===
		int skipCount = 0;
		if (shimmerMode != SHIMMER_OFF) skipCount += 1; // loadRampLFO
		if (lfoDepth != LFO_NONE) skipCount += 2; // 2x loadSinLFO
		if (skipCount > 0) {
			sfxb.skip(RUN, skipCount);
			if (shimmerMode != SHIMMER_OFF) {
				sfxb.loadRampLFO(0, semitoneToCoefficient(shimmerPitchSemitones), 4096);
			}
			if (lfoDepth != LFO_NONE) {
				int exc = (lfoDepth == LFO_SUBTLE) ? 20 : 50;
				sfxb.loadSinLFO(0, 12, exc);
				sfxb.loadSinLFO(1, 15, exc);
			}
		}

		// === Save dry signal for mix ===
		sfxb.comment("--- Save dry signal ---");
		sfxb.readRegister(audioIn, 1.0);
		sfxb.writeRegister(dry, 0.0);

		// === Pre-delay (fixed delay line, read from end) ===
		if (preDelayEnabled) {
			sfxb.comment("--- Pre-delay ---");
			sfxb.readRegister(audioIn, 0.25);
			sfxb.FXwriteDelay("pdel", 0, 0.0);    // write input to delay start
			sfxb.FXreadDelay("pdel#", 0, 1.0);     // read from delay end = pre-delayed signal
		} else {
			sfxb.readRegister(audioIn, 0.25);
		}

		// === Input bandwidth filter ===
		sfxb.comment("--- Input bandwidth filter + 4 diffusion APs ---");
		sfxb.readRegisterFilter(bwReg, inputBandwidth);
		sfxb.writeRegister(bwReg, 1.0);  // keep filtered output in ACC

		// === 4 input diffusion allpasses ===
		sfxb.FXreadDelay("api1#", 0, diffusion);
		sfxb.FXwriteAllpass("api1", 0, -diffusion);
		sfxb.FXreadDelay("api2#", 0, diffusion);
		sfxb.FXwriteAllpass("api2", 0, -diffusion);
		sfxb.FXreadDelay("api3#", 0, diffusion);
		sfxb.FXwriteAllpass("api3", 0, -diffusion);
		sfxb.FXreadDelay("api4#", 0, diffusion);
		sfxb.FXwriteAllpass("api4", 0, -diffusion);
		sfxb.writeRegister(apout, 0.0);

		// === Shimmer processing ===
		if (shimmerMode != SHIMMER_OFF) {
			generateShimmerSection(sfxb, apout, shimLp, shimOut,
					shimmerMode == SHIMMER_INPUT_AND_FEEDBACK);
		}

		// === Loop 1: read del2 end * RT, add apout, AP, HF damp, write del1 ===
		sfxb.comment("--- Tank Loop 1 (feedback from del2 -> del1) ---");
		if (rtPin >= 0) {
			sfxb.FXreadDelay("del2#", 0, 1.0);
			sfxb.mulx(rtPin);
		} else {
			sfxb.FXreadDelay("del2#", 0, reverbTime);
		}
		// DC blocker (~5 Hz highpass) prevents DC offset buildup in feedback
		sfxb.readRegisterFilter(dcBlock, 0.001);
		sfxb.writeRegisterHighshelf(dcBlock, -1.0);
		sfxb.readRegister(apout, 1.0);
		if (shimmerMode != SHIMMER_OFF) {
			sfxb.readRegister(shimOut, 1.0); // add shimmer
		}
		sfxb.FXreadDelay("lap1#", 0, -0.6);
		sfxb.FXwriteAllpass("lap1", 0, 0.6);
		// HF damping: lowpass filter in feedback path
		if (hfPin >= 0) {
			// Pot-controlled: crossfade between unfiltered and filtered
			// When pot=0: no damping. When pot=1: full LP filtering.
			sfxb.writeRegister(temp, 1.0);           // save unfiltered, keep in ACC
			sfxb.readRegisterFilter(lp1, 0.5);       // 1-pole LPF
			sfxb.writeRegister(lp1, 1.0);            // save filter state, keep filtered in ACC
			sfxb.readRegister(temp, -1.0);            // ACC = filtered - unfiltered
			sfxb.mulx(hfPin);                         // ACC = (filtered - unfiltered) * pot
			sfxb.readRegister(temp, 1.0);             // ACC = unfiltered + (filtered - unfiltered) * pot
		} else {
			// Fixed coefficient LPF
			sfxb.readRegisterFilter(lp1, hfDamping);  // 1-pole LPF
			sfxb.writeRegister(lp1, 1.0);             // save state, keep filtered output in ACC
		}
		sfxb.FXwriteDelay("del1", 0, 1.0);   // write to delay, keep ACC for output
		sfxb.writeRegister(outputL, 0.0);

		// === Loop 2: read del1 end * RT, add apout, AP, HF damp, write del2 ===
		sfxb.comment("--- Tank Loop 2 (feedback from del1 -> del2) ---");
		if (rtPin >= 0) {
			sfxb.FXreadDelay("del1#", 0, 1.0);
			sfxb.mulx(rtPin);
		} else {
			sfxb.FXreadDelay("del1#", 0, reverbTime);
		}
		sfxb.readRegister(apout, 1.0);
		sfxb.FXreadDelay("lap2#", 0, -0.6);
		sfxb.FXwriteAllpass("lap2", 0, 0.6);
		// HF damping: lowpass filter in feedback path
		if (hfPin >= 0) {
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lp2, 0.5);
			sfxb.writeRegister(lp2, 1.0);
			sfxb.readRegister(temp, -1.0);
			sfxb.mulx(hfPin);
			sfxb.readRegister(temp, 1.0);
		} else {
			sfxb.readRegisterFilter(lp2, hfDamping);
			sfxb.writeRegister(lp2, 1.0);             // keep filtered output in ACC
		}
		sfxb.FXwriteDelay("del2", 0, 1.0);   // write to delay, keep ACC for output
		if (stereoOutput) {
			sfxb.writeRegister(outputR, 0.0);
		} else {
			sfxb.readRegister(outputL, 1.0); // sum into outputL
			sfxb.writeRegister(outputL, 0.0);
		}

		// === LFO modulation of loop allpasses ===
		if (lfoDepth != LFO_NONE) {
			sfxb.comment("--- LFO modulation ---");
			int exc = (lfoDepth == LFO_SUBTLE) ? 20 : 50;
			sfxb.FXchorusReadDelay(SIN0, SIN | COMPC, "lap1+", exc);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap1+", exc + 1);
			sfxb.FXwriteDelay("lap1+", exc * 2, 0.0);
			sfxb.FXchorusReadDelay(SIN1, COS | COMPC, "lap2+", exc);
			sfxb.FXchorusReadDelay(SIN1, COS, "lap2+", exc + 1);
			sfxb.FXwriteDelay("lap2+", exc * 2, 0.0);
		}

		// === Dry/Wet mix and output ===
		generateMixOutput(sfxb, dry, outputL, outputR, mixPin);
	}

	// =====================================================
	// Dattorro Plate Topology
	// =====================================================
	private void generateDattorro(SpinFXBlock sfxb, int audioIn, int rtPin, int hfPin, int mixPin) {
		int lfoExcursion = 0;
		if (lfoDepth == LFO_SUBTLE) lfoExcursion = 8;
		else if (lfoDepth == LFO_WIDE) lfoExcursion = 64;

		// Allocate delay memory: 4 input APs + 4 tank APs + 4 tank delays
		sfxb.FXallocDelayMem("dapi1", scaled(DAT_INPUT_AP[0]));
		sfxb.FXallocDelayMem("dapi2", scaled(DAT_INPUT_AP[1]));
		sfxb.FXallocDelayMem("dapi3", scaled(DAT_INPUT_AP[2]));
		sfxb.FXallocDelayMem("dapi4", scaled(DAT_INPUT_AP[3]));
		// Tank side A
		sfxb.FXallocDelayMem("tap1", scaled(DAT_TANK_AP[0]) + lfoExcursion);
		sfxb.FXallocDelayMem("tdl1", scaled(DAT_TANK_DEL[0]));
		sfxb.FXallocDelayMem("tap2", scaled(DAT_TANK_AP[1]) + lfoExcursion);
		sfxb.FXallocDelayMem("tdl2", scaled(DAT_TANK_DEL[1]));
		// Tank side B
		sfxb.FXallocDelayMem("tap3", scaled(DAT_TANK_AP[2]) + lfoExcursion);
		sfxb.FXallocDelayMem("tdl3", scaled(DAT_TANK_DEL[2]));
		sfxb.FXallocDelayMem("tap4", scaled(DAT_TANK_AP[3]) + lfoExcursion);
		sfxb.FXallocDelayMem("tdl4", scaled(DAT_TANK_DEL[3]));

		// Shimmer delay if needed
		if (shimmerMode != SHIMMER_OFF) {
			sfxb.FXallocDelayMem("shim", 4096);
			sfxb.FXallocDelayMem("stimp", 1);
		}

		// Pre-delay: fixed delay line sized to slider setting
		int dPreDelaySamples = preDelayEnabled ? Math.max(1, (int)(3276 * preDelayAmount)) : 0;
		if (preDelayEnabled) {
			sfxb.FXallocDelayMem("pdel", dPreDelaySamples);
		}

		// Allocate registers
		int apout = sfxb.allocateReg();
		int outputL = sfxb.allocateReg();
		int outputR = stereoOutput ? sfxb.allocateReg() : -1;
		int lpA = sfxb.allocateReg(); // HF damp side A
		int lpB = sfxb.allocateReg(); // HF damp side B
		int bwReg = sfxb.allocateReg();
		int dry = sfxb.allocateReg();
		int temp = sfxb.allocateReg();
		int shimLp = (shimmerMode != SHIMMER_OFF) ? sfxb.allocateReg() : -1;
		int shimOut = (shimmerMode != SHIMMER_OFF) ? sfxb.allocateReg() : -1;
		int dcBlock = sfxb.allocateReg(); // DC blocker filter state

		// === Init section ===
		int skipCount = 0;
		if (shimmerMode != SHIMMER_OFF) skipCount += 1;
		if (lfoDepth != LFO_NONE) skipCount += 2;
		if (skipCount > 0) {
			sfxb.skip(RUN, skipCount);
			if (shimmerMode != SHIMMER_OFF) {
				sfxb.loadRampLFO(0, semitoneToCoefficient(shimmerPitchSemitones), 4096);
			}
			if (lfoDepth != LFO_NONE) {
				sfxb.loadSinLFO(0, 27, lfoExcursion);
				sfxb.loadSinLFO(1, 23, lfoExcursion);
			}
		}

		// === Save dry signal ===
		sfxb.comment("--- Save dry signal ---");
		sfxb.readRegister(audioIn, 1.0);
		sfxb.writeRegister(dry, 0.0);

		// === Pre-delay (fixed delay line, read from end) ===
		if (preDelayEnabled) {
			sfxb.comment("--- Pre-delay ---");
			sfxb.readRegister(audioIn, 0.25);
			sfxb.FXwriteDelay("pdel", 0, 0.0);
			sfxb.FXreadDelay("pdel#", 0, 1.0);
		} else {
			sfxb.readRegister(audioIn, 0.25);
		}

		// === Input bandwidth filter ===
		sfxb.comment("--- Input bandwidth filter + 4 diffusion APs ---");
		sfxb.readRegisterFilter(bwReg, inputBandwidth);
		sfxb.writeRegister(bwReg, 1.0);  // keep filtered output in ACC

		// === 4 input diffusion allpasses ===
		sfxb.FXreadDelay("dapi1#", 0, DAT_INPUT_DIFF_1);
		sfxb.FXwriteAllpass("dapi1", 0, -DAT_INPUT_DIFF_1);
		sfxb.FXreadDelay("dapi2#", 0, DAT_INPUT_DIFF_1);
		sfxb.FXwriteAllpass("dapi2", 0, -DAT_INPUT_DIFF_1);
		sfxb.FXreadDelay("dapi3#", 0, DAT_INPUT_DIFF_2);
		sfxb.FXwriteAllpass("dapi3", 0, -DAT_INPUT_DIFF_2);
		sfxb.FXreadDelay("dapi4#", 0, DAT_INPUT_DIFF_2);
		sfxb.FXwriteAllpass("dapi4", 0, -DAT_INPUT_DIFF_2);
		sfxb.writeRegister(apout, 0.0);

		// === Shimmer processing ===
		if (shimmerMode != SHIMMER_OFF) {
			generateShimmerSection(sfxb, apout, shimLp, shimOut,
					shimmerMode == SHIMMER_INPUT_AND_FEEDBACK);
		}

		// === Tank Side A ===
		sfxb.comment("--- Tank Side A (feedback from tdl4 -> tdl1 -> tdl2) ---");
		// Read cross-feedback from side B end
		if (rtPin >= 0) {
			sfxb.FXreadDelay("tdl4#", 0, 1.0);
			sfxb.mulx(rtPin);
		} else {
			sfxb.FXreadDelay("tdl4#", 0, reverbTime);
		}
		// DC blocker (~5 Hz highpass) prevents DC offset buildup in feedback
		sfxb.readRegisterFilter(dcBlock, 0.001);
		sfxb.writeRegisterHighshelf(dcBlock, -1.0);
		sfxb.readRegister(apout, 1.0);
		if (shimmerMode != SHIMMER_OFF) {
			sfxb.readRegister(shimOut, 1.0);
		}
		// Tank AP1 (modulated via LFO if enabled)
		sfxb.FXreadDelay("tap1#", 0, DAT_DECAY_DIFF_1);
		sfxb.FXwriteAllpass("tap1", 0, -DAT_DECAY_DIFF_1);
		// Delay 1
		sfxb.FXwriteDelay("tdl1", 0, 0.0);
		sfxb.FXreadDelay("tdl1#", 0, 1.0);
		// HF damping side A
		if (hfPin >= 0) {
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpA, 0.5);
			sfxb.writeRegister(lpA, 1.0);
			sfxb.readRegister(temp, -1.0);
			sfxb.mulx(hfPin);
			sfxb.readRegister(temp, 1.0);
		} else {
			sfxb.readRegisterFilter(lpA, hfDamping);
			sfxb.writeRegister(lpA, 1.0);
		}
		// Tank AP2
		sfxb.FXreadDelay("tap2#", 0, 0.5);
		sfxb.FXwriteAllpass("tap2", 0, -0.5);
		// Delay 2
		sfxb.FXwriteDelay("tdl2", 0, 0.0);

		// === Tank Side B ===
		sfxb.comment("--- Tank Side B (feedback from tdl2 -> tdl3 -> tdl4) ---");
		// Read cross-feedback from side A end
		if (rtPin >= 0) {
			sfxb.FXreadDelay("tdl2#", 0, 1.0);
			sfxb.mulx(rtPin);
		} else {
			sfxb.FXreadDelay("tdl2#", 0, reverbTime);
		}
		sfxb.readRegister(apout, 1.0);
		// Tank AP3 (modulated)
		sfxb.FXreadDelay("tap3#", 0, DAT_DECAY_DIFF_1);
		sfxb.FXwriteAllpass("tap3", 0, -DAT_DECAY_DIFF_1);
		// Delay 3
		sfxb.FXwriteDelay("tdl3", 0, 0.0);
		sfxb.FXreadDelay("tdl3#", 0, 1.0);
		// HF damping side B
		if (hfPin >= 0) {
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpB, 0.5);
			sfxb.writeRegister(lpB, 1.0);
			sfxb.readRegister(temp, -1.0);
			sfxb.mulx(hfPin);
			sfxb.readRegister(temp, 1.0);
		} else {
			sfxb.readRegisterFilter(lpB, hfDamping);
			sfxb.writeRegister(lpB, 1.0);
		}
		// Tank AP4
		sfxb.FXreadDelay("tap4#", 0, 0.5);
		sfxb.FXwriteAllpass("tap4", 0, -0.5);
		// Delay 4
		sfxb.FXwriteDelay("tdl4", 0, 0.0);

		// === Output taps (4 per channel from different delays) ===
		sfxb.comment("--- Output taps ---");
		sfxb.FXreadDelay("tdl1+", (int)(scaled(DAT_TANK_DEL[0]) * 0.15), 0.6);
		sfxb.FXreadDelay("tdl2+", (int)(scaled(DAT_TANK_DEL[1]) * 0.4), 0.6);
		sfxb.FXreadDelay("tdl3+", (int)(scaled(DAT_TANK_DEL[2]) * 0.3), -0.6);
		sfxb.FXreadDelay("tdl4+", (int)(scaled(DAT_TANK_DEL[3]) * 0.6), 0.6);
		sfxb.writeRegister(outputL, 0.0);

		if (stereoOutput) {
			sfxb.FXreadDelay("tdl1+", (int)(scaled(DAT_TANK_DEL[0]) * 0.6), 0.6);
			sfxb.FXreadDelay("tdl2+", (int)(scaled(DAT_TANK_DEL[1]) * 0.7), -0.6);
			sfxb.FXreadDelay("tdl3+", (int)(scaled(DAT_TANK_DEL[2]) * 0.5), 0.6);
			sfxb.FXreadDelay("tdl4+", (int)(scaled(DAT_TANK_DEL[3]) * 0.2), 0.6);
			sfxb.writeRegister(outputR, 0.0);
		}

		// === LFO modulation of tank APs ===
		if (lfoDepth != LFO_NONE) {
			sfxb.comment("--- LFO modulation ---");
			// Modulate tap1 with SIN0/SIN
			sfxb.FXchorusReadDelay(SIN0, SIN | COMPC, "tap1+", lfoExcursion);
			sfxb.FXchorusReadDelay(SIN0, SIN, "tap1+", lfoExcursion + 1);
			sfxb.FXwriteDelay("tap1+", lfoExcursion * 2, 0.0);
			// Modulate tap3 with SIN0/COS (decorrelated)
			sfxb.FXchorusReadDelay(SIN0, COS | COMPC, "tap3+", lfoExcursion);
			sfxb.FXchorusReadDelay(SIN0, COS, "tap3+", lfoExcursion + 1);
			sfxb.FXwriteDelay("tap3+", lfoExcursion * 2, 0.0);

			if (lfoDepth == LFO_WIDE) {
				// Also modulate tap2 and tap4 with SIN1
				sfxb.FXchorusReadDelay(SIN1, COS | COMPC, "tap2+", lfoExcursion);
				sfxb.FXchorusReadDelay(SIN1, COS, "tap2+", lfoExcursion + 1);
				sfxb.FXwriteDelay("tap2+", lfoExcursion * 2, 0.0);
				sfxb.FXchorusReadDelay(SIN1, SIN | COMPC, "tap4+", lfoExcursion);
				sfxb.FXchorusReadDelay(SIN1, SIN, "tap4+", lfoExcursion + 1);
				sfxb.FXwriteDelay("tap4+", lfoExcursion * 2, 0.0);
			}
		}

		// === Dry/Wet mix and output ===
		generateMixOutput(sfxb, dry, outputL, outputR, mixPin);
	}

	// =====================================================
	// Ring FDN Topology
	// =====================================================
	private void generateRingFDN(SpinFXBlock sfxb, int audioIn, int rtPin, int hfPin, int mixPin) {
		int stages = getRingStages();
		int lfoExcursion = 0;
		if (lfoDepth == LFO_SUBTLE) lfoExcursion = 20;
		else if (lfoDepth == LFO_WIDE) lfoExcursion = 40;

		// Allocate delay memory: 4 input APs + N ring stages (AP + delay each)
		sfxb.FXallocDelayMem("rapi1", scaled(RING_INPUT_AP[0]));
		sfxb.FXallocDelayMem("rapi2", scaled(RING_INPUT_AP[1]));
		sfxb.FXallocDelayMem("rapi3", scaled(RING_INPUT_AP[2]));
		sfxb.FXallocDelayMem("rapi4", scaled(RING_INPUT_AP[3]));

		for (int i = 0; i < stages; i++) {
			sfxb.FXallocDelayMem("rap" + i, scaled(RING_STAGE_AP[i]) + lfoExcursion);
			sfxb.FXallocDelayMem("rdl" + i, scaled(RING_STAGE_DEL[i]));
		}

		// Shimmer delay if needed
		if (shimmerMode != SHIMMER_OFF) {
			sfxb.FXallocDelayMem("shim", 4096);
			sfxb.FXallocDelayMem("stimp", 1);
		}

		// Pre-delay: fixed delay line sized to slider setting
		int rPreDelaySamples = preDelayEnabled ? Math.max(1, (int)(3276 * preDelayAmount)) : 0;
		if (preDelayEnabled) {
			sfxb.FXallocDelayMem("pdel", rPreDelaySamples);
		}

		// Allocate registers
		int apout = sfxb.allocateReg();
		int outputL = sfxb.allocateReg();
		int outputR = stereoOutput ? sfxb.allocateReg() : -1;
		int temp = sfxb.allocateReg();
		int bwReg = sfxb.allocateReg();
		int dry = sfxb.allocateReg();
		// HP and LP filter state per stage
		int[] hpRegs = new int[stages];
		int[] lpRegs = new int[stages];
		for (int i = 0; i < stages; i++) {
			hpRegs[i] = sfxb.allocateReg();
			lpRegs[i] = sfxb.allocateReg();
		}
		int shimLp = (shimmerMode != SHIMMER_OFF) ? sfxb.allocateReg() : -1;
		int shimOut = (shimmerMode != SHIMMER_OFF) ? sfxb.allocateReg() : -1;
		int dcBlock = sfxb.allocateReg(); // DC blocker filter state

		// === Init section ===
		int skipCount = 0;
		if (shimmerMode != SHIMMER_OFF) skipCount += 1;
		if (lfoDepth != LFO_NONE) skipCount += 2;
		if (skipCount > 0) {
			sfxb.skip(RUN, skipCount);
			if (shimmerMode != SHIMMER_OFF) {
				sfxb.loadRampLFO(0, semitoneToCoefficient(shimmerPitchSemitones), 4096);
			}
			if (lfoDepth != LFO_NONE) {
				sfxb.loadSinLFO(0, 12, lfoExcursion);
				sfxb.loadSinLFO(1, 15, lfoExcursion);
			}
		}

		// === Save dry signal ===
		sfxb.comment("--- Save dry signal ---");
		sfxb.readRegister(audioIn, 1.0);
		sfxb.writeRegister(dry, 0.0);

		// === Pre-delay (fixed delay line, read from end) ===
		if (preDelayEnabled) {
			sfxb.comment("--- Pre-delay ---");
			sfxb.readRegister(audioIn, 0.25);
			sfxb.FXwriteDelay("pdel", 0, 0.0);
			sfxb.FXreadDelay("pdel#", 0, 1.0);
		} else {
			sfxb.readRegister(audioIn, 0.25);
		}

		// === Input bandwidth filter ===
		sfxb.comment("--- Input bandwidth filter + 4 diffusion APs ---");
		sfxb.readRegisterFilter(bwReg, inputBandwidth);
		sfxb.writeRegister(bwReg, 1.0);  // keep filtered output in ACC

		// === 4 input diffusion allpasses ===
		sfxb.FXreadDelay("rapi1#", 0, diffusion);
		sfxb.FXwriteAllpass("rapi1", 0, -diffusion);
		sfxb.FXreadDelay("rapi2#", 0, diffusion);
		sfxb.FXwriteAllpass("rapi2", 0, -diffusion);
		sfxb.FXreadDelay("rapi3#", 0, diffusion);
		sfxb.FXwriteAllpass("rapi3", 0, -diffusion);
		sfxb.FXreadDelay("rapi4#", 0, diffusion);
		sfxb.FXwriteAllpass("rapi4", 0, -diffusion);
		sfxb.writeRegister(apout, 0.0);

		// === Shimmer processing ===
		if (shimmerMode != SHIMMER_OFF) {
			generateShimmerSection(sfxb, apout, shimLp, shimOut,
					shimmerMode == SHIMMER_INPUT_AND_FEEDBACK);
		}

		// === Ring stages ===
		// Each stage reads from previous stage's delay end, multiplies by RT,
		// adds input (apout), runs through AP, applies HP/LP shelving filters,
		// then writes the filtered result to the delay (single write).
		String lastDel = "rdl" + (stages - 1); // ring wraps around
		for (int i = 0; i < stages; i++) {
			String curAp = "rap" + i;
			String curDel = "rdl" + i;
			String prevDel = (i == 0) ? lastDel : "rdl" + (i - 1);

			sfxb.comment("--- Ring stage " + i + " (" + prevDel + " -> " + curDel + ") ---");
			// Read previous delay end * RT
			if (rtPin >= 0) {
				sfxb.FXreadDelay(prevDel + "#", 0, 1.0);
				sfxb.mulx(rtPin);
			} else {
				sfxb.FXreadDelay(prevDel + "#", 0, reverbTime);
			}
			// DC blocker at ring wrap point (~5 Hz highpass)
			if (i == 0) {
				sfxb.readRegisterFilter(dcBlock, 0.001);
				sfxb.writeRegisterHighshelf(dcBlock, -1.0);
			}
			// Add input
			sfxb.readRegister(apout, 1.0);
			if (i == 0 && shimmerMode != SHIMMER_OFF) {
				sfxb.readRegister(shimOut, 1.0);
			}
			// Ring allpass
			sfxb.FXreadDelay(curAp + "#", 0, 0.6);
			sfxb.FXwriteAllpass(curAp, 0, -0.6);
			// HP shelf (LF damping) — applied before delay write
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpRegs[i], lfDamping);
			sfxb.writeRegisterLowshelf(hpRegs[i], -1.0);
			sfxb.scaleOffset(-0.5, 0.0);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			// LP filter (HF damping) — applied before delay write
			if (hfPin >= 0) {
				// Pot-controlled: crossfade between unfiltered and filtered
				// When pot=0: no damping. When pot=1: full LP filtering.
				sfxb.writeRegister(temp, 1.0);           // save unfiltered
				sfxb.readRegisterFilter(lpRegs[i], 0.5); // 1-pole LPF
				sfxb.writeRegister(lpRegs[i], 1.0);      // save filter state, keep filtered
				sfxb.readRegister(temp, -1.0);            // ACC = filtered - unfiltered
				sfxb.mulx(hfPin);                         // ACC = (filtered - unfiltered) * pot
				sfxb.readRegister(temp, 1.0);             // ACC = unfiltered + (filtered-unfiltered)*pot
			} else {
				// Fixed coefficient LPF
				sfxb.readRegisterFilter(lpRegs[i], hfDamping);
				sfxb.writeRegister(lpRegs[i], 1.0);      // save state, keep filtered output
			}
			// Write filtered result to delay (single write)
			sfxb.FXwriteDelay(curDel, 0, 0.0);
		}

		// === Output taps (one from each stage delay) ===
		sfxb.comment("--- Output taps ---");
		double[] tapGains = { 0.8, 0.7, 0.6, 0.5 };
		// Left channel: taps at varying positions
		for (int i = 0; i < stages; i++) {
			int tapOffset = (int)(scaled(RING_STAGE_DEL[i]) * (0.2 + i * 0.15));
			sfxb.FXreadDelay("rdl" + i + "+", tapOffset, tapGains[i]);
		}
		sfxb.writeRegister(outputL, 0.0);

		if (stereoOutput) {
			// Right channel: different tap positions for decorrelation
			for (int i = 0; i < stages; i++) {
				int tapOffset = (int)(scaled(RING_STAGE_DEL[i]) * (0.5 + i * 0.1));
				sfxb.FXreadDelay("rdl" + i + "+", tapOffset, ((i % 2 == 0) ? 1.0 : -1.0) * tapGains[i]);
			}
			sfxb.writeRegister(outputR, 0.0);
		}

		// === LFO modulation of ring APs ===
		if (lfoDepth != LFO_NONE) {
			sfxb.comment("--- LFO modulation ---");
			// Modulate first 2 ring APs
			sfxb.FXchorusReadDelay(SIN0, SIN | COMPC, "rap0+", lfoExcursion);
			sfxb.FXchorusReadDelay(SIN0, SIN, "rap0+", lfoExcursion + 1);
			sfxb.FXwriteDelay("rap0+", lfoExcursion * 2, 0.0);
			sfxb.FXchorusReadDelay(SIN0, COS | COMPC, "rap1+", lfoExcursion);
			sfxb.FXchorusReadDelay(SIN0, COS, "rap1+", lfoExcursion + 1);
			sfxb.FXwriteDelay("rap1+", lfoExcursion * 2, 0.0);

			if (lfoDepth == LFO_WIDE && stages >= 3) {
				sfxb.FXchorusReadDelay(SIN1, SIN | COMPC, "rap2+", lfoExcursion);
				sfxb.FXchorusReadDelay(SIN1, SIN, "rap2+", lfoExcursion + 1);
				sfxb.FXwriteDelay("rap2+", lfoExcursion * 2, 0.0);
				if (stages >= 4) {
					sfxb.FXchorusReadDelay(SIN1, COS | COMPC, "rap3+", lfoExcursion);
					sfxb.FXchorusReadDelay(SIN1, COS, "rap3+", lfoExcursion + 1);
					sfxb.FXwriteDelay("rap3+", lfoExcursion * 2, 0.0);
				}
			}
		}

		// === Dry/Wet mix and output ===
		generateMixOutput(sfxb, dry, outputL, outputR, mixPin);
	}

	// =====================================================
	// Shimmer section (shared by all topologies)
	// =====================================================
	private void generateShimmerSection(SpinFXBlock sfxb, int apout, int shimLp, int shimOut, boolean useFeedback) {
		sfxb.comment("--- Shimmer pitch shift ---");
		// Read input from diffused signal
		sfxb.readRegister(apout, shimmerLevel);
		// Self-feedback: read previous shimmer output for cascading pitch shifts
		// Each recirculation shifts up another octave, LPF tames brightness buildup
		if (useFeedback) {
			sfxb.readRegister(shimOut, shimmerLevel * 0.6);
		}
		// Write to shimmer delay
		sfxb.FXwriteDelay("shim", 0, 0.0);
		// Pitch shift via RMP0 crossfade
		sfxb.FXchorusReadDelay(RMP0, REG | COMPC, "shim", 0);
		sfxb.FXchorusReadDelay(RMP0, 0, "shim+", 1);
		sfxb.FXwriteDelay("stimp", 0, 0.0);
		sfxb.FXchorusReadDelay(RMP0, RPTR2 | COMPC, "shim", 0);
		sfxb.FXchorusReadDelay(RMP0, RPTR2, "shim+", 1);
		sfxb.chorusScaleOffset(RMP0, NA | COMPC, 0);
		sfxb.FXchorusReadDelay(RMP0, NA, "stimp", 0);
		// Post-shift LPF to tame brightness
		sfxb.readRegisterFilter(shimLp, 0.4);
		sfxb.writeRegister(shimLp, 1.0);      // save filter state, keep filtered in ACC
		// Store shimmer output
		sfxb.writeRegister(shimOut, 0.0);
	}

	// =====================================================
	// Dry/Wet mix and output assignment (shared)
	// =====================================================
	private void generateMixOutput(SpinFXBlock sfxb, int dry, int outputL, int outputR, int mixPin) {
		sfxb.comment("--- Dry/Wet mix ---");
		int finalL = sfxb.allocateReg();
		int finalR = stereoOutput ? sfxb.allocateReg() : -1;

		if (mixPin >= 0) {
			// Crossfade: output = dry + (wet - dry) * mix
			sfxb.readRegister(outputL, 1.0);
			sfxb.readRegister(dry, -1.0);
			sfxb.mulx(mixPin);
			sfxb.readRegister(dry, 1.0);
			sfxb.writeRegister(finalL, 0.0);

			if (stereoOutput) {
				sfxb.readRegister(outputR, 1.0);
				sfxb.readRegister(dry, -1.0);
				sfxb.mulx(mixPin);
				sfxb.readRegister(dry, 1.0);
				sfxb.writeRegister(finalR, 0.0);
			}
		} else {
			// Fixed mix: output = dry * (1-mix) + wet * mix
			sfxb.readRegister(dry, 1.0 - dryWet);
			sfxb.readRegister(outputL, dryWet);
			sfxb.writeRegister(finalL, 0.0);

			if (stereoOutput) {
				sfxb.readRegister(dry, 1.0 - dryWet);
				sfxb.readRegister(outputR, dryWet);
				sfxb.writeRegister(finalR, 0.0);
			}
		}

		// Set output pins
		this.getPin("Out L").setRegister(finalL);
		if (stereoOutput) {
			this.getPin("Out R").setRegister(finalR);
		} else {
			this.getPin("Out R").setRegister(finalL); // mono to both
		}
	}

	// =====================================================
	// Getters and setters for serialization and control panel
	// =====================================================
	public int getTopology() { return topology; }
	public void setTopology(int v) { topology = v; }

	public int getSizePreset() { return sizePreset; }
	public void setSizePreset(int v) { sizePreset = v; }

	public boolean isStereoOutput() { return stereoOutput; }
	public void setStereoOutput(boolean v) { stereoOutput = v; }

	public int getShimmerMode() { return shimmerMode; }
	public void setShimmerMode(int v) {
		shimmerMode = v;
	}

	public int getShimmerPitchSemitones() { return shimmerPitchSemitones; }
	public void setShimmerPitchSemitones(int v) { shimmerPitchSemitones = v; }

	public int getLfoDepth() { return lfoDepth; }
	public void setLfoDepth(int v) { lfoDepth = v; }

	public boolean isPreDelayEnabled() { return preDelayEnabled; }
	public void setPreDelayEnabled(boolean v) {
		preDelayEnabled = v;
	}

	public double getReverbTime() { return reverbTime; }
	public void setReverbTime(double v) { reverbTime = v; }

	public double getHfDamping() { return hfDamping; }
	public void setHfDamping(double v) { hfDamping = v; }

	public double getLfDamping() { return lfDamping; }
	public void setLfDamping(double v) { lfDamping = v; }

	public double getDryWet() { return dryWet; }
	public void setDryWet(double v) { dryWet = v; }

	public double getPreDelayAmount() { return preDelayAmount; }
	public void setPreDelayAmount(double v) { preDelayAmount = v; }

	public double getInputBandwidth() { return inputBandwidth; }
	public void setInputBandwidth(double v) { inputBandwidth = v; }

	public double getDiffusion() { return diffusion; }
	public void setDiffusion(double v) { diffusion = v; }

	public double getShimmerLevel() { return shimmerLevel; }
	public void setShimmerLevel(double v) { shimmerLevel = v; }
}
