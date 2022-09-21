/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * New_EnvelopeCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.New_EnvelopeControlPanel;
		
	    @SuppressWarnings("unused")
	    public class New_EnvelopeCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private New_EnvelopeControlPanel cp = null;
			
			private double threshhold = 0.002;
			private double attackFreq = 0.00015;
			private double decayFreq = 0.00015;
			private double postFreq = 0.00015;
			private int output;
			private int lpf1;
			private int lpf2;
			private int rectified;
			private int avg;
			private int lavg;
			private int temp;
			private int ffil;

			public New_EnvelopeCADBlock(int x, int y) {
				super(x, y);
				setName("Pluck Detector");					
			setBorderColor(new Color(0x02f27f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Attack");
				addControlInputPin(this, "Decay");
				addControlInputPin(this, "Sensitivity");
				addControlOutputPin(this, "Trigger Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new New_EnvelopeControlPanel(this);
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
			sp = this.getPin("Attack").getPinConnection();
			int attackControl = -1;
			if(sp != null) {
				attackControl = sp.getRegister();
			}
			sp = this.getPin("Decay").getPinConnection();
			int decayControl = -1;
			if(sp != null) {
				decayControl = sp.getRegister();
			}
			sp = this.getPin("Sensitivity").getPinConnection();
			int sens = -1;
			if(sp != null) {
				sens = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			lpf1 = sfxb.allocateReg();
			lpf2 = sfxb.allocateReg();
			rectified = sfxb.allocateReg();
			avg = sfxb.allocateReg();
			lavg = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			ffil = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, 1);
			sfxb.absa();
			sfxb.readRegisterFilter(avg, attackFreq);
			sfxb.writeRegister(avg, 0);
			sfxb.readRegister(lavg, 0.001);
			sfxb.scaleOffset(-0.01, 0);
			sfxb.readRegister(lavg, 1);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(avg, 1);
			sfxb.maxx(temp, 1);
			sfxb.writeRegister(lavg, 1);
			sfxb.scaleOffset(1, 0.002);
			sfxb.log(1, 0);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(avg, 1);
			sfxb.log(1, 0);
			sfxb.readRegister(temp, -1);
			if(this.getPin("Sensitivity").isConnected() == true) {
			sfxb.mulx(sens);
			}
			
			sfxb.exp(1, 0);
			sfxb.readRegisterFilter(ffil, postFreq);
			sfxb.writeRegister(ffil, 0);
			if(this.getPin("Attack").isConnected() == true) {
			sfxb.readRegister(input, attackFreq);
			sfxb.readRegister(lpf1, -attackFreq);
			sfxb.mulx(attackControl);
			sfxb.readRegister(lpf1, 1.0);
			} else {
			sfxb.readRegister(input, 1.0);
			sfxb.readRegisterFilter(lpf1, attackFreq);
			}
			
			sfxb.writeRegister(lpf1, 0);
			if(this.getPin("Decay").isConnected() == true) {
			sfxb.readRegister(input, decayFreq);
			sfxb.readRegister(lpf2, -decayFreq);
			sfxb.mulx(decayControl);
			sfxb.readRegister(lpf2, 1.0);
			} else {
			sfxb.readRegister(input, 1.0);
			sfxb.readRegisterFilter(lpf2, decayFreq);
			}
			
			this.getPin("Output").setRegister(ffil);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setthreshhold(double __param) {
				threshhold = __param;	
			}
			
			public double getthreshhold() {
				return threshhold;
			}
			public void setattackFreq(double __param) {
				attackFreq = __param;	
			}
			
			public double getattackFreq() {
				return attackFreq;
			}
			public void setdecayFreq(double __param) {
				decayFreq = __param;	
			}
			
			public double getdecayFreq() {
				return decayFreq;
			}
			public void setpostFreq(double __param) {
				postFreq = __param;	
			}
			
			public double getpostFreq() {
				return postFreq;
			}
		}	
