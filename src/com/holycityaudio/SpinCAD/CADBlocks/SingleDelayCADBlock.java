package com.holycityaudio.SpinCAD.CADBlocks;

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class SingleDelayCADBlock extends DelayCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3997234959654893065L;

	// coefficients

	private static double tap0level = 0.35; // tap1 level
	private static double tap1level = 0.35; // tap1 level

	// the following tap parameters are percentage of total, to be used with
	// readDelay
	// equally spaced for 16th note resolution

	private double length = 0.8;
	private double fbLevel = 0.05;
	private double defaultGain = 0.5;
	private double defaultFeedback = 0.45;
	private double delayFactor = 0.999;

	public SingleDelayCADBlock(int x, int y) {
		// super("MultiTap");
		super(x, y);
		// TODO Auto-generated constructor stub
		addControlInputPin(this, "Gain");	//	delay time
		addControlInputPin(this, "Feedback");	//	feedback
		setName("Single Delay");
	}

	public void editBlock() {
		new SingleDelayControlPanel(this);
	}

	public void generateCode(SpinFXBlock sfxb) {
		// only mono input supported
		// TODO Auto-generated constructor stub
		int input;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			int Control1 = -1;

			p = this.getPin("Feedback").getPinConnection();
			if (p == null) { // there's no pin attached!
				defaultFeedback = 0.45;
			} else {
				Control1 = p.getRegister();
			}

			int Control2 = -1;
			sfxb.comment(getName());
			
			p = this.getPin("Gain").getPinConnection();
			if (p == null) { // there's no pin attached!
				sfxb.readRegister(input, defaultGain);
			} else {
				Control2 = p.getRegister();
			}

			sfxb.FXallocDelayMem("Delay", (int) (length * getSamplerate() + 1));
			int output = sfxb.allocateReg();

			// ; Guitar Echo
			// ; HK July 2009
			// ; version 2.0
			;
			// ; mono in mono out
			// ; pot0 = feedback amount (from no feedback = slap back to infinite
			// feedback)
			// ; pot1 = delay time (50 ms to 1 second with 32 kHz x-tal)
			// ; pot2 = dry - wet mix
			;
			// ; only 20 thicks

			// ;declare memory spaces:

			// mem del 32767

			// ;declare register equates:

			// equ dout reg0
			int dout = sfxb.allocateReg();

			// equ kfbk reg1
			int feedback = sfxb.allocateReg();
			int dry_in = sfxb.allocateReg();

			// equ dry_in reg2

			// ;get feedback value from pot0:

			// wrax kfbk,0
			sfxb.writeRegister(feedback, 0);
			//
			// rdax pot0,1
			if(Control1 == -1) {
				sfxb.scaleOffset(0, defaultFeedback);
			}
			else
				sfxb.readRegister(Control1, 1);
			// wrax kfbk,0
			sfxb.writeRegister(feedback, 0);

			// ;get address pointer from pot1:

			// rdax pot1,1
			if(Control2 == -1) {
				sfxb.scaleOffset(0, delayFactor);
			}
			else {
				sfxb.readRegister(Control2, 1);
			}
			// and %01111110_00000000_00000000 ;don't make jumps too small
//			sfxb.and(0b011111100000000000000000);
			// sof 61/64,3/64 ;50 ms to 1 second
			sfxb.scaleOffset(61 / 64.0, 3 / 64.0);
			// wrax addr_ptr,0
			sfxb.writeRegister(ADDR_PTR, 0);

			// ;get output from delay:

			// rmpa 1
			sfxb.readDelayPointer(1);
			// TODO debug remove the line bloew it doesn't belowng here
			sfxb.FXreadDelay("delay^+", 25, 0.5);
			// wrax dout,0
			sfxb.writeRegister(dout, 1);

			// ;put input signals into delay, allowing for feedback:

			// rdax dout,1
			//		sfxb.readRegister(dout, 1);
			// mulx kfbk
			sfxb.mulx(feedback);
			// rdax adcl,0.5
			sfxb.readRegister(input, 0.5);
			// rdax adcr,0.5
			// wrax dry_in, 1
			//		sfxb.writeRegister(input, 1);
			// wra del,0
			sfxb.FXwriteDelay("delay", 0, 0);

			// ; mix dry and wet using pot2

			// rdax dout,1
			sfxb.readRegister(dout, 1.0);
			// write output
			sfxb.writeRegister(dout, 0.0); // write ACC to DACL and clear ACC
			p = this.getPin("Audio Output 1");
			p.setRegister(dout);
		}
	}

	// ====================================================
	public double getfbLevel() {
		// TODO Auto-generated method stub
		return fbLevel;
	}

	public void setfbLevel(double d) {
		// TODO Auto-generated method stub
		fbLevel = d;
	}

	// ====================================================
	public void setLength(double d) {
		// TODO Auto-generated method stub
		length = d;
	}

	public double getLength() {
		// TODO Auto-generated method stub
		return length;
	}

	// ====================================================
	public void setTapLevel(int i, double value) {
		// TODO Auto-generated method stub
		if (value < 0.0) {
			value = 0.0;
		}
		if (i == 0) {
			tap0level = value;
		} else if (i == 1) {
			tap1level = value;
		} else {
			System.err.println("Tap # out of range: " + i);
			return;
		}
	}

	public double getTapLevel(int i) {
		// TODO Auto-generated method stub
		if (i == 0) {
			return tap0level;
		} else if (i == 1) {
			return tap1level;
		} else {
			System.err.println("Tap # out of range: " + i);
			return -1.0;
		}
	}
}
