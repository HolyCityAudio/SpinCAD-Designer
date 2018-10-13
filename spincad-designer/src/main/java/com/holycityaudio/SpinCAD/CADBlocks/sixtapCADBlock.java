/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * sixtapCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.sixtapControlPanel;
		
	    @SuppressWarnings("unused")
	    public class sixtapCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private sixtapControlPanel cp = null;
			
			private double inputGain = 0.45;
			private double fbkGain = 0.5;
			private double delayLength = 32767;
			private double tap1Ratio = 0.10;
			private double tap2Ratio = 0.20;
			private double tap3Ratio = 0.30;
			private double tap4Ratio = 0.4;
			private double tap5Ratio = 0.5;
			private double tap6Ratio = 0.6;
			private double tap1Gain = 0.5;
			private double tap2Gain = 0.5;
			private double tap3Gain = 0.5;
			private double tap4Gain = 0.5;
			private double tap5Gain = 0.5;
			private double tap6Gain = 0.8;
			private double delayOffset = -1;
			private int mix1;
			private int mix2;
			private int max;
			private int output5;

			public sixtapCADBlock(int x, int y) {
				super(x, y);
				setName("Six Tap");					
			setBorderColor(new Color(0x6060c4));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Feedback In");
				addOutputPin(this, "Mix L Out");
				addOutputPin(this, "Mix R Out");
				addOutputPin(this, "Delay_Out_End");
				addControlInputPin(this, "Delay_Time_1");
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
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new sixtapControlPanel(this);
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
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			sp = this.getPin("Feedback In").getPinConnection();
			int feedback = -1;
			if(sp != null) {
				feedback = sp.getRegister();
			}
			sp = this.getPin("Delay_Time_1").getPinConnection();
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
			sfxb.FXallocDelayMem("delay", delayLength); 
			if(this.getPin("Input").isConnected() == true) {
			if(this.getPin("Feedback In").isConnected() == true) {
			sfxb.readRegister(feedback, fbkGain);
			if(this.getPin("Feedback Gain").isConnected() == true) {
			sfxb.mulx(fbk);
			}
			
			}
			
			sfxb.readRegister(input, inputGain);
			sfxb.FXwriteDelay("delay", 0, 0.0);
			if(this.getPin("Delay_Time_1").isConnected() == true) {
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
			sfxb.scaleOffset((0.95 * tap2Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap2Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap2Gain);
			sfxb.readRegister(mix1, 1.0);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap3Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap3Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap3Gain);
			sfxb.readRegister(mix1, 1.0);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap4Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap4Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap4Gain);
			sfxb.readRegister(mix1, 1.0);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap5Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap5Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap5Gain);
			sfxb.readRegister(mix1, 1.0);
			sfxb.writeRegister(mix1, 0.0);
			sfxb.readRegister(max, 1.0);
			sfxb.scaleOffset((0.95 * tap6Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap6Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(tap6Gain);
			sfxb.writeRegister(mix2, 0.0);
			} else {
			ratio = (int) (tap1Ratio * delayLength);
			sfxb.FXreadDelay("delay+", ratio, tap1Gain);
			ratio = (int) (tap2Ratio * delayLength);
			sfxb.FXreadDelay("delay+", ratio, tap2Gain);
			ratio = (int) (tap3Ratio * delayLength);
			sfxb.FXreadDelay("delay+", ratio, tap3Gain);
			sfxb.writeRegister(mix1, 0.0);
			ratio = (int) (tap4Ratio * delayLength);
			sfxb.FXreadDelay("delay+", ratio, tap4Gain);
			ratio = (int) (tap5Ratio * delayLength);
			sfxb.FXreadDelay("delay+", ratio, tap5Gain);
			ratio = (int) (tap6Ratio * delayLength);
			sfxb.FXreadDelay("delay+", ratio, tap6Gain);
			sfxb.writeRegister(mix2, 0.0);
			}
			
			output5 = sfxb.allocateReg();
			sfxb.FXreadDelay("delay#", 0, 1.0);
			sfxb.writeRegister(output5, 0.0);
			this.getPin("Delay_Out_End").setRegister(output5);
			}
			
			this.getPin("Mix L Out").setRegister(mix1);
			this.getPin("Mix R Out").setRegister(mix2);

			}
			
			// create setters and getter for control panel variables
			public void setinputGain(double __param) {
				inputGain = __param;	
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
			public void settap5Ratio(double __param) {
				tap5Ratio = __param;	
			}
			
			public double gettap5Ratio() {
				return tap5Ratio;
			}
			public void settap6Ratio(double __param) {
				tap6Ratio = __param;	
			}
			
			public double gettap6Ratio() {
				return tap6Ratio;
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
		}	
