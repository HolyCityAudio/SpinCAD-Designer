/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * TripleTapCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.TripleTapControlPanel;
		
	    @SuppressWarnings("unused")
	    public class TripleTapCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private TripleTapControlPanel cp = null;
			
			private double inputGain = 1.0;
			private double fbkGain = 0.5;
			private double delayLength = 32767;
			private double tap1Ratio = 0.85;
			private double tap2Ratio = 0.60;
			private double tap3Ratio = 0.45;
			private double delayOffset = -1;
			private int output1;
			private int output2;
			private int output3;

			public TripleTapCADBlock(int x, int y) {
				super(x, y);
				setName("ThreeTap");					
			setBorderColor(new Color(0x6060c4));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Feedback");
				addOutputPin(this, "Tap 1 Out");
				addOutputPin(this, "Tap 2 Out");
				addOutputPin(this, "Tap 3 Out");
				addControlInputPin(this, "Delay Time 1");
				addControlInputPin(this, "Delay Time 2");
				addControlInputPin(this, "Delay Time 3");
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
						cp = new TripleTapControlPanel(this);
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
			sp = this.getPin("Delay Time 1").getPinConnection();
			int cIn1 = -1;
			if(sp != null) {
				cIn1 = sp.getRegister();
			}
			sp = this.getPin("Delay Time 2").getPinConnection();
			int cIn2 = -1;
			if(sp != null) {
				cIn2 = sp.getRegister();
			}
			sp = this.getPin("Delay Time 3").getPinConnection();
			int cIn3 = -1;
			if(sp != null) {
				cIn3 = sp.getRegister();
			}
			sp = this.getPin("Feedback Gain").getPinConnection();
			int fbk = -1;
			if(sp != null) {
				fbk = sp.getRegister();
			}
			
			// finally, generate the instructions
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("threeTap", delayLength); 
			if(this.getPin("Input").isConnected() == true) {
			if(this.getPin("Feedback").isConnected() == true) {
			sfxb.readRegister(feedback, fbkGain);
			if(this.getPin("Feedback Gain").isConnected() == true) {
			sfxb.mulx(fbk);
			}
			
			}
			
			sfxb.readRegister(adcl, inputGain);
			sfxb.FXwriteDelay("threeTap", 0, 0.0);
			if(this.getPin("Tap 1 Out").isConnected() == true) {
			output1 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			if(this.getPin("Delay Time 1").isConnected() == true) {
			sfxb.mulx(cIn1);
			}
			
			sfxb.scaleOffset((0.95 * tap1Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap1Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output1, 0.0);
			this.getPin("Tap 1 Out").setRegister(output1);
			}
			
			if(this.getPin("Tap 2 Out").isConnected() == true) {
			output2 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			if(this.getPin("Delay Time 2").isConnected() == true) {
			sfxb.mulx(cIn2);
			}
			
			sfxb.scaleOffset((0.95 * tap2Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap2Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output2, 0.0);
			this.getPin("Tap 2 Out").setRegister(output2);
			}
			
			if(this.getPin("Tap 3 Out").isConnected() == true) {
			output3 = sfxb.allocateReg();
			sfxb.clear();
			sfxb.or(0x7FFF00);
			if(this.getPin("Delay Time 3").isConnected() == true) {
			sfxb.mulx(cIn3);
			}
			
			sfxb.scaleOffset((0.95 * tap3Ratio * delayLength)/32768.0, (delayOffset + (0.05 * tap3Ratio * delayLength))/32768.0);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output3, 0.0);
			this.getPin("Tap 3 Out").setRegister(output3);
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
		}	
