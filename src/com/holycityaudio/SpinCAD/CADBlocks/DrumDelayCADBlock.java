/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * DrumDelayCADBlock.java
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
		
	    @SuppressWarnings("unused")
	    public class DrumDelayCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private DrumDelayControlPanel cp = null;
			
			private double inputGain = 1.0;
			private double fbkGain = 0.5;
			private double delayLength = 32767;
			private double tap1Ratio = 0.85;
			private double tap2Ratio = 0.60;
			private double tap3Ratio = 0.45;
			private double tap4Ratio = 0.25;
			private double heads = 1.0;
			private double delayOffset = -1;
			private int output1;
			private int output2;
			private int output3;
			private int output4;

			public DrumDelayCADBlock(int x, int y) {
				super(x, y);
				setName("Drum Delay");					
			setBorderColor(new Color(0x6060c4));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Feedback");
				addOutputPin(this, "Tap 1 Out");
				addOutputPin(this, "Tap 2 Out");
				addOutputPin(this, "Tap 3 Out");
				addOutputPin(this, "Tap 4 Out");
				addControlInputPin(this, "Delay Time");
				addControlInputPin(this, "Heads");
				addControlInputPin(this, "Feedback Gain");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
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
						cp = new DrumDelayControlPanel(this);
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
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			sp = this.getPin("Feedback").getPinConnection();
			int feedback = -1;
			if(sp != null) {
				feedback = sp.getRegister();
			}
			sp = this.getPin("Delay Time").getPinConnection();
			int cIn1 = -1;
			if(sp != null) {
				cIn1 = sp.getRegister();
			}
			sp = this.getPin("Heads").getPinConnection();
			int heads = -1;
			if(sp != null) {
				heads = sp.getRegister();
			}
			sp = this.getPin("Feedback Gain").getPinConnection();
			int fbk = -1;
			if(sp != null) {
				fbk = sp.getRegister();
			}
			
			// finally, generate the instructions
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("drumDelay", delayLength); 
			if(this.getPin("Input").isConnected() == true) {
			if(this.getPin("Feedback").isConnected() == true) {
			sfxb.readRegister(feedback, fbkGain);
			if(this.getPin("Feedback Gain").isConnected() == true) {
			sfxb.mulx(fbk);
			}
			
			}
			
			sfxb.readRegister(adcl, inputGain);
			sfxb.FXwriteDelay("drumDelay", 0, 0.0);
			if(this.getPin("Heads").isConnected() == true) {
			sfxb.readRegister(heads, 1.0);
			sfxb.scaleOffset(1.0, -0.25);
			if(this.getPin("Delay Time").isConnected() == true) {
			sfxb.skip(NEG, 24);
			} else {
			sfxb.skip(NEG, 21);
			}
			
			sfxb.scaleOffset(1.0, -0.25);
			if(this.getPin("Delay Time").isConnected() == true) {
			sfxb.skip(NEG, 16);
			} else {
			sfxb.skip(NEG, 14);
			}
			
			sfxb.scaleOffset(1.0, -0.25);
			if(this.getPin("Delay Time").isConnected() == true) {
			sfxb.skip(NEG, 7);
			} else {
			sfxb.skip(NEG, 6);
			}
			
			} else {
			}
			
			output4 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			if(this.getPin("Delay Time").isConnected() == true) {
			sfxb.mulx(cIn1);
			}
			
			sfxb.scaleOffset((0.95 * tap4Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap4Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output4, 0.0);
			this.getPin("Tap 4 Out").setRegister(output4);
			output3 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			if(this.getPin("Delay Time").isConnected() == true) {
			sfxb.mulx(cIn1);
			}
			
			sfxb.scaleOffset((0.95 * tap3Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap3Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output3, 0.0);
			this.getPin("Tap 3 Out").setRegister(output3);
			output2 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			if(this.getPin("Delay Time").isConnected() == true) {
			sfxb.mulx(cIn1);
			}
			
			sfxb.scaleOffset((0.95 * tap2Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap2Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output2, 0.0);
			this.getPin("Tap 2 Out").setRegister(output2);
			output1 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			if(this.getPin("Delay Time").isConnected() == true) {
			sfxb.mulx(cIn1);
			}
			
			sfxb.scaleOffset((0.95 * tap1Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap1Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output1, 0.0);
			this.getPin("Tap 1 Out").setRegister(output1);
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
			public void setdelayLength(double __param) {
				delayLength = __param;	
			}
			
			public double getdelayLength() {
				return delayLength;
			}
			public void settap1Ratio(double __param) {
				tap1Ratio = __param;	
			}
			
			public double gettap1Ratio() {
				return tap1Ratio;
			}
			public void settap2Ratio(double __param) {
				tap2Ratio = __param;	
			}
			
			public double gettap2Ratio() {
				return tap2Ratio;
			}
			public void settap3Ratio(double __param) {
				tap3Ratio = __param;	
			}
			
			public double gettap3Ratio() {
				return tap3Ratio;
			}
			public void settap4Ratio(double __param) {
				tap4Ratio = __param;	
			}
			
			public double gettap4Ratio() {
				return tap4Ratio;
			}
		}	
