/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ga_demo_flangerCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ga_demo_flangerControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ga_demo_flangerCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ga_demo_flangerControlPanel cp = null;
			
			private int mono;
			private int flaout;
			private int fbk;
			private int flamix;
			private int tri;
			private int fhp;
			private int output;
			private int servo;
			private double fbkmax = 0.9;

			public ga_demo_flangerCADBlock(int x, int y) {
				super(x, y);
				setName("Flanger");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addOutputPin(this, "Audio_Output_1");
				addControlOutputPin(this, "Triangle_LFO");
				addControlOutputPin(this, "Servo");
				addControlInputPin(this, "Flange_Rate");
				addControlInputPin(this, "Effect_Level_Feedback");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ga_demo_flangerControlPanel(this);
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
			sp = this.getPin("Input_Left").getPinConnection();
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			sp = this.getPin("Flange_Rate").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Effect_Level_Feedback").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.FXallocDelayMem("fladel", 512); 
			mono = sfxb.allocateReg();
			flaout = sfxb.allocateReg();
			fbk = sfxb.allocateReg();
			flamix = sfxb.allocateReg();
			tri = sfxb.allocateReg();
			fhp = sfxb.allocateReg();
			output = sfxb.allocateReg();
			servo = sfxb.allocateReg();
			sfxb.skip(RUN, 2);
			sfxb.loadRampLFO((int) 0, (int) 10, (int) 4096);
			sfxb.loadRampLFO((int) 1, (int) 0, (int) 512);
			sfxb.readRegister(adcl, 1.0);
			sfxb.writeRegister(mono, 0.5);
			sfxb.readRegister(flaout, fbkmax);
			sfxb.mulx(fbk);
			sfxb.readRegister(mono, 1);
			sfxb.FXwriteDelay("fladel", 0, 0);
			if(this.getPin("Effect_Level_Feedback").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			sfxb.scaleOffset(1.99, 0);
			} else {
			sfxb.scaleOffset(0.0, 0.9990);
			}
			
			sfxb.writeRegister(flamix, 0);
			if(this.getPin("Effect_Level_Feedback").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			sfxb.scaleOffset(1, -1);
			sfxb.scaleOffset(1.999, 0);
			sfxb.scaleOffset(1, 0.999);
			} else {
			sfxb.clear();
			}
			
			sfxb.writeRegister(fbk, 0);
			if(this.getPin("Flange_Rate").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			sfxb.mulx(input1);
			sfxb.mulx(input1);
			sfxb.scaleOffset(0.4, 0.01);
			} else {
			sfxb.scaleOffset(0.0, 0.2);
			}
			
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.chorusReadValue(RMP0);
			sfxb.scaleOffset(1, -0.25);
			sfxb.absa();
			sfxb.writeRegister(tri, 0);
			sfxb.chorusReadValue(RMP1);
			sfxb.writeRegister(servo, 1.0);
			sfxb.readRegister(tri, -0.16);
			sfxb.scaleOffset(0.25, 0);
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.FXchorusReadDelay(RMP1, REG|COMPC, "fladel", 0);
			sfxb.FXchorusReadDelay(RMP1, 0, "fladel+", 1);
			sfxb.writeRegister(flaout, 0);
			sfxb.readRegister(flaout, 1);
			sfxb.mulx(flamix);
			sfxb.readRegister(mono, 1);
			sfxb.writeRegister(output, 0);
			this.getPin("Audio_Output_1").setRegister(output);
			this.getPin("Triangle_LFO").setRegister(tri);
			this.getPin("Servo").setRegister(servo);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
