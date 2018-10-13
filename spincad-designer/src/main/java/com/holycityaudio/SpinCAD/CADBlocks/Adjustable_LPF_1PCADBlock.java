/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Adjustable_LPF_1PCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Adjustable_LPF_1PControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Adjustable_LPF_1PCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Adjustable_LPF_1PControlPanel cp = null;
			
			private double attackFreq = 0.00015;
			private double decayFreq = 0.00015;
			private int output;
			private int lpf1;
			private int lpf2;
			private int rectified;

			public Adjustable_LPF_1PCADBlock(int x, int y) {
				super(x, y);
				setName("Adjustable LPF");					
			setBorderColor(new Color(0x02f27f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Attack");
				addControlInputPin(this, "Decay");
				addControlInputPin(this, "Sensitivity");
				addControlOutputPin(this, "Fast Output");
				addControlOutputPin(this, "Slow Output");
				addControlOutputPin(this, "Max Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Adjustable_LPF_1PControlPanel(this);
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
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, 1);
			sfxb.absa();
			sfxb.writeRegister(rectified, 0);
			if(this.getPin("Attack").isConnected() == true) {
			sfxb.readRegister(rectified, attackFreq);
			sfxb.readRegister(lpf1, -attackFreq);
			sfxb.mulx(attackControl);
			sfxb.readRegister(lpf1, 1.0);
			} else {
			sfxb.readRegister(rectified, 1.0);
			sfxb.readRegisterFilter(lpf1, attackFreq);
			}
			
			sfxb.writeRegister(lpf1, 0);
			if(this.getPin("Decay").isConnected() == true) {
			sfxb.readRegister(rectified, decayFreq);
			sfxb.readRegister(lpf2, -decayFreq);
			sfxb.mulx(decayControl);
			sfxb.readRegister(lpf2, 1.0);
			} else {
			sfxb.readRegister(rectified, 1.0);
			sfxb.readRegisterFilter(lpf2, decayFreq);
			}
			
			sfxb.writeRegister(lpf2, 1);
			sfxb.maxx(lpf1, 1.0);
			sfxb.writeRegister(output, 0);
			this.getPin("Max Output").setRegister(output);
			this.getPin("Fast Output").setRegister(lpf1);
			this.getPin("Slow Output").setRegister(lpf2);
			}
			

			}
			
			// create setters and getter for control panel variables
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
		}	
