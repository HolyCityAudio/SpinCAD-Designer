/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * servoCADBlock.java
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
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *     
 */ 
		package com.holycityaudio.SpinCAD.CADBlocks;

		import java.awt.Color;
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
 		import com.holycityaudio.SpinCAD.ControlPanel.servoControlPanel;
		
	    @SuppressWarnings("unused")
	    public class servoCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private servoControlPanel cp = null;
			
			private double inputGain = 1.0;
			private double fbkGain = 0.5;
			private double servoGain = 0.25;
			private double freq = 0.25;
			private double delayLength = 4096;
			private double tap1Ratio = 0.025;
			private double lfoSel = 0;
			private double delayOffset = -1;
			private int output;
			private int filt;
			private int output1;

			public servoCADBlock(int x, int y) {
				super(x, y);
				setName("Servo Flanger");					
			setBorderColor(new Color(0x243232));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Feedback In");
				addOutputPin(this, "Output");
				addOutputPin(this, "Tap Output");
				addControlInputPin(this, "Delay Time");
				addControlInputPin(this, "Feedback Gain");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new servoControlPanel(this);
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
			sp = this.getPin("Feedback In").getPinConnection();
			int feedback = -1;
			if(sp != null) {
				feedback = sp.getRegister();
			}
			sp = this.getPin("Delay Time").getPinConnection();
			int control1 = -1;
			if(sp != null) {
				control1 = sp.getRegister();
			}
			sp = this.getPin("Feedback Gain").getPinConnection();
			int fbk = -1;
			if(sp != null) {
				fbk = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input").isConnected() == true) {
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("moddel", 4096); 
			output = sfxb.allocateReg();
			if(this.getPin("Delay Time").isConnected() == true) {
			filt = sfxb.allocateReg();
			if(lfoSel == 0) {
			sfxb.skip(RUN, 1);
			sfxb.loadRampLFO((int) 0, (int) 0, (int) 4096);
			} else {
			sfxb.skip(RUN, 1);
			sfxb.loadRampLFO((int) 1, (int) 0, (int) 4096);
			}
			
			sfxb.clear();
			if(this.getPin("Feedback In").isConnected() == true) {
			sfxb.readRegister(feedback, fbkGain);
			if(this.getPin("Feedback Gain").isConnected() == true) {
			sfxb.mulx(fbk);
			}
			
			}
			
			sfxb.readRegister(input, inputGain);
			sfxb.FXwriteDelay("moddel", 0, 0);
			if(lfoSel == 0) {
			sfxb.chorusReadValue(RMP0);
			} else {
			sfxb.chorusReadValue(RMP1);
			}
			
			sfxb.readRegister(control1, -servoGain);
			if(lfoSel == 0) {
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.FXchorusReadDelay(RMP0, REG|COMPC, "moddel", 0);
			sfxb.FXchorusReadDelay(RMP0, 0, "moddel+", 1);
			} else {
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.FXchorusReadDelay(RMP1, REG|COMPC, "moddel", 0);
			sfxb.FXchorusReadDelay(RMP1, 0, "moddel+", 1);
			}
			
			sfxb.readRegisterFilter(filt, freq);
			sfxb.writeRegisterLowshelf(filt, -1);
			sfxb.writeRegister(output, 0);
			if(this.getPin("Tap Output").isConnected() == true) {
			output1 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			sfxb.scaleOffset((0.95 * tap1Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap1Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output1, 0.0);
			this.getPin("Tap Output").setRegister(output1);
			}
			
			this.getPin("Output").setRegister(output);
			} else {
			sfxb.readRegister(input, 1);
			sfxb.FXwriteDelay("moddel", 0, 0);
			sfxb.FXreadDelay("moddel^", 0, 1.0);
			sfxb.writeRegister(output, 0);
			this.getPin("Output").setRegister(output);
			}
			
			}
			

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
			public void setservoGain(double __param) {
				servoGain = __param;	
			}
			
			public double getservoGain() {
				return servoGain;
			}
			public void setfreq(double __param) {
				freq = __param;	
			}
			
			public double getfreq() {
				return freq;
			}
			public void settap1Ratio(double __param) {
				tap1Ratio = __param;	
			}
			
			public double gettap1Ratio() {
				return tap1Ratio;
			}
			public void setlfoSel(int __param) {
				lfoSel = (double) __param;	
			}
			
			public int getlfoSel() {
				return (int) lfoSel;
			}
		}	
