/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ReverseDelayCADBlock.java
 * Copyright (C) 2013 - 2026 - Gary Worsham 
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
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *     
 */ 
		package com.holycityaudio.SpinCAD.CADBlocks;

		import java.awt.Color;
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
 		import com.holycityaudio.SpinCAD.ControlPanel.ReverseDelayControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ReverseDelayCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ReverseDelayControlPanel cp = null;
			
			private double inputGain = 1.0;
			private double fbkGain = 0.5;
			private double memMode = 0;
			private double delayLen = 16383;
			private int phase1;
			private int output;
			private int ramp;
			private int ramp2;
			private int xfade;
			private int xfade2;

			public ReverseDelayCADBlock(int x, int y) {
				super(x, y);
				setName("Reverse Delay");					
			setBorderColor(new Color(0x6060c4));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Feedback");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Feedback Gain");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ReverseDelayControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly

			
			sfxb.comment(getName());
			
			SpinCADPin sp = null;
					
			// Iterate through pin definitions and connect or assign as needed
			sp = this.getPin("Input").getPinConnection();
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			sp = this.getPin("Feedback").getPinConnection();
			int feedback = -1;
			if(sp != null) {
				feedback = sp.getRegister();
			}
			sp = this.getPin("Feedback Gain").getPinConnection();
			int fbk = -1;
			if(sp != null) {
				fbk = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(memMode == 0) {
			delayLen = 16383;
			}
			
			if(memMode == 1) {
			delayLen = 32767;
			}
			
			sfxb.FXallocDelayMem("delay", delayLen); 
			phase1 = sfxb.allocateReg();
			output = sfxb.allocateReg();
			ramp = sfxb.allocateReg();
			ramp2 = sfxb.allocateReg();
			xfade = sfxb.allocateReg();
			xfade2 = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			if(memMode == 0) {
			sfxb.skip(RUN, 3);
			sfxb.scaleOffset(0, -0.25);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.writeRegister(RMP0_RANGE, 0);
			} else {
			sfxb.skip(RUN, 3);
			sfxb.scaleOffset(0, -0.125);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.writeRegister(RMP0_RANGE, 0);
			}
			
			if(this.getPin("Feedback").isConnected() == true) {
			sfxb.readRegister(feedback, fbkGain);
			if(this.getPin("Feedback Gain").isConnected() == true) {
			sfxb.mulx(fbk);
			}
			
			}
			
			sfxb.readRegister(input, inputGain);
			sfxb.FXwriteDelay("delay#", 0, 0.0);
			sfxb.chorusReadValue(RMP0);
			if(memMode == 0) {
			sfxb.writeRegister(ADDR_PTR, 1.0);
			sfxb.writeRegister(ramp, 1.0);
			} else {
			sfxb.writeRegister(ramp, 1.0);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(ADDR_PTR, 0.0);
			sfxb.readRegister(ramp, 1.0);
			}
			
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.absa();
			sfxb.scaleOffset(-2.0, 0.25);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(0.5, 0.5);
			sfxb.writeRegister(xfade, 0.0);
			sfxb.readDelayPointer(1.0);
			sfxb.mulx(xfade);
			sfxb.writeRegister(phase1, 0.0);
			sfxb.readRegister(ramp, 1.0);
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.writeRegister(ramp2, 1.0);
			if(memMode == 0) {
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(1.0, 0.5);
			sfxb.writeRegister(ADDR_PTR, 0.0);
			} else {
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(1.0, 0.5);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(ADDR_PTR, 0.0);
			}
			
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(xfade2, 0.0);
			sfxb.readRegister(xfade, 1.0);
			sfxb.scaleOffset(-1.0, 0.99);
			sfxb.mulx(xfade2);
			sfxb.readRegister(phase1, 1.0);
			sfxb.writeRegister(output, 0.0);
			}
			
			this.getPin("Output").setRegister(output);

			}
			
			// create setters and getter for control panel variables
			public void setinputGain(double __param) {
				inputGain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getinputGain() {
				return inputGain;
			}
			public void setfbkGain(double __param) {
				fbkGain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getfbkGain() {
				return fbkGain;
			}
			public void setmemMode(int __param) {
				memMode = (double) __param;	
			}
			
			public int getmemMode() {
				return (int) memMode;
			}
		}	
