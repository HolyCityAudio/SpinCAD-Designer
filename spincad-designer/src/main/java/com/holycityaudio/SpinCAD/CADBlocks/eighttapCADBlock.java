/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * eighttapCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.eighttapControlPanel;
		
	    @SuppressWarnings("unused")
	    public class eighttapCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private eighttapControlPanel cp = null;
			
			private double inputGain = 1.0;
			private double fbkGain = 0.5;
			private double delayLength = 32767;
			private double tap1Ratio = 0.1250;
			private double tap1Gain = 0.5;
			private double tap2Ratio = 0.25;
			private double tap2Gain = 0.5;
			private double tap3Ratio = 0.375;
			private double tap3Gain = 0.5;
			private double tap4Ratio = 0.5;
			private double tap4Gain = 0.5;
			private double tap5Ratio = 0.625;
			private double tap5Gain = 0.5;
			private double tap6Ratio = 0.750;
			private double tap6Gain = 0.8;
			private double tap7Ratio = 0.875;
			private double tap7Gain = 0.5;
			private double tap8Ratio = 1.0;
			private double tap8Gain = 0.5;
			private double delayOffset = -1;
			private int mix1;
			private int mix2;
			private int max;
			private int tap8;

			public eighttapCADBlock(int x, int y) {
				super(x, y);
				setName("Eight Tap");					
			setBorderColor(new Color(0x6060c4));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Feedback");
				addOutputPin(this, "Mix 1 Out");
				addOutputPin(this, "Mix 2 Out");
				addOutputPin(this, "Tap 8 Out");
				addControlInputPin(this, "Delay Time 1");
				addControlInputPin(this, "Feedback Gain");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
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
						cp = new eighttapControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly

			int ratio = 1;
			
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
			sp = this.getPin("Delay Time 1").getPinConnection();
			int cIn1 = -1;
			if(sp != null) {
				cIn1 = sp.getRegister();
			}
			sp = this.getPin("Feedback Gain").getPinConnection();
			int fbk = -1;
			if(sp != null) {
				fbk = sp.getRegister();
			}
			
			// finally, generate the instructions
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("eight_tap", delayLength); 
			if(this.getPin("Input").isConnected() == true) {
			if(this.getPin("Feedback Gain").isConnected() == true) {
			sfxb.readRegister(feedback, fbkGain);
			if(this.getPin("Feedback").isConnected() == true) {
			sfxb.mulx(fbk);
			}
			
			}
			
			sfxb.readRegister(adcl, inputGain);
			sfxb.FXwriteDelay("eight_tap", 0, 0.0);
			if(this.getPin("Delay Time 1").isConnected() == true) {
			mix1 = sfxb.allocateReg();
			mix2 = sfxb.allocateReg();
			max = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			sfxb.mulx(cIn1);
			sfxb.writeRegister(max, 0.0);
			sfxb.scaleOffset((0.95 * tap1Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap1Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap1Gain);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap3Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap3Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap3Gain);
			sfxb.readRegister(mix1, 1.0);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap5Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap5Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap5Gain);
			sfxb.readRegister(mix1, 1.0);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap7Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap7Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap7Gain);
			sfxb.readRegister(mix1, 1.0);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap2Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap2Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap2Gain);
			sfxb.writeRegister(mix2, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap4Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap4Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap4Gain);
			sfxb.readRegister(mix2, 1.0);
			sfxb.writeRegister(mix2, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap6Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap6Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap6Gain);
			sfxb.readRegister(mix2, 1.0);
			sfxb.writeRegister(mix2, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap8Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap8Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap8Gain);
			sfxb.readRegister(mix2, 1.0);
			sfxb.writeRegister(mix2, 0.0);
			} else {
			ratio = (int) (tap1Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap1Gain);
			ratio = (int) (tap3Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap3Gain);
			ratio = (int) (tap5Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap5Gain);
			ratio = (int) (tap7Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap7Gain);
			sfxb.writeRegister(mix1, 0.0);
			ratio = (int) (tap2Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap2Gain);
			ratio = (int) (tap4Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap4Gain);
			ratio = (int) (tap6Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap6Gain);
			ratio = (int) (tap8Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, tap8Gain);
			sfxb.writeRegister(mix2, 0.0);
			}
			
			if(this.getPin("Tap 8 Out").isConnected() == true) {
			tap8 = sfxb.allocateReg();
			ratio = (int) (tap8Ratio * delayLength);
			sfxb.FXreadDelay("eight_tap+", ratio, 1.0);
			sfxb.writeRegister(tap8, 0);
			this.getPin("Tap 8 Out").setRegister(tap8);
			}
			
			this.getPin("Mix 1 Out").setRegister(mix1);
			this.getPin("Mix 2 Out").setRegister(mix2);
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
			public void settap1Gain(double __param) {
				tap1Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap1Gain() {
				return tap1Gain;
			}
			public void settap2Gain(double __param) {
				tap2Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap2Gain() {
				return tap2Gain;
			}
			public void settap3Gain(double __param) {
				tap3Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap3Gain() {
				return tap3Gain;
			}
			public void settap4Gain(double __param) {
				tap4Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap4Gain() {
				return tap4Gain;
			}
			public void settap5Gain(double __param) {
				tap5Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap5Gain() {
				return tap5Gain;
			}
			public void settap6Gain(double __param) {
				tap6Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap6Gain() {
				return tap6Gain;
			}
			public void settap7Gain(double __param) {
				tap7Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap7Gain() {
				return tap7Gain;
			}
			public void settap8Gain(double __param) {
				tap8Gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double gettap8Gain() {
				return tap8Gain;
			}
		}	
