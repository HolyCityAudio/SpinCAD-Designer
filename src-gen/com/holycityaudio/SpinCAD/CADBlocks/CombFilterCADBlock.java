/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * CombFilterCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.CombFilterControlPanel;
		
	    @SuppressWarnings("unused")
	    public class CombFilterCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private CombFilterControlPanel cp = null;
			
			private double gain = 0.5;
			private double delayLength = 1116;
			private double feedback = 0.7;
			private double damping = 0.5;
			private int output1;
			private int filtReg;

			public CombFilterCADBlock(int x, int y) {
				super(x, y);
				setName("Comb_Filter");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Feedback");
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
						cp = new CombFilterControlPanel(this);
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
			int fbIn = -1;
			if(sp != null) {
				fbIn = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input").isConnected() == true) {
			output1 = sfxb.allocateReg();
			filtReg = sfxb.allocateReg();
			sfxb.FXallocDelayMem("combDelay", delayLength); 
			sfxb.FXreadDelay("combDelay#", 0, 1.0);
			sfxb.writeRegister(output1, 1.0);
			sfxb.readRegisterFilter(filtReg, damping);
			if(this.getPin("Feedback").isConnected() == true) {
			sfxb.writeRegister(filtReg, 1.0);
			sfxb.mulx(fbIn);
			} else {
			sfxb.writeRegister(filtReg, feedback);
			}
			
			sfxb.readRegister(input, gain);
			sfxb.FXwriteDelay("combDelay", 0, 0);
			this.getPin("Output").setRegister(output1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setdelayLength(double __param) {
				delayLength = __param;	
			}
			
			public double getdelayLength() {
				return delayLength;
			}
			public void setfeedback(double __param) {
				feedback = __param;	
			}
			
			public double getfeedback() {
				return feedback;
			}
			public void setdamping(double __param) {
				damping = __param;	
			}
			
			public double getdamping() {
				return damping;
			}
		}	
