/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ControlMixer_3_to_1CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ControlMixer_3_to_1ControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ControlMixer_3_to_1CADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ControlMixer_3_to_1ControlPanel cp = null;
			
			private double gain1 = 1.0;
			private double gain2 = 1.0;
			private double gain3 = 1.0;
			private int output;

			public ControlMixer_3_to_1CADBlock(int x, int y) {
				super(x, y);
				setName("Mixer 3:1");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Input 1");
				addControlInputPin(this, "Input 2");
				addControlInputPin(this, "Input 3");
				addControlOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ControlMixer_3_to_1ControlPanel(this);
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
			sp = this.getPin("Input 1").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Input 2").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			sp = this.getPin("Input 3").getPinConnection();
			int input3 = -1;
			if(sp != null) {
				input3 = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(input1, gain1);
			sfxb.writeRegister(output, 0.0);
			}
			
			if(this.getPin("Input 2").isConnected() == true) {
			sfxb.readRegister(input2, gain2);
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(output, 1.0);
			}
			
			sfxb.writeRegister(output, 0.0);
			}
			
			if(this.getPin("Input 3").isConnected() == true) {
			sfxb.readRegister(input3, gain3);
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(output, 1.0);
			} else {
			if(this.getPin("Input 2").isConnected() == true) {
			sfxb.readRegister(output, 1.0);
			}
			
			sfxb.writeRegister(output, 0.0);
			}
			
			}
			
			this.getPin("Output").setRegister(output);

			}
			
			// create setters and getter for control panel variables
			public void setgain1(double __param) {
				gain1 = __param;	
			}
			
			public double getgain1() {
				return gain1;
			}
			public void setgain2(double __param) {
				gain2 = __param;	
			}
			
			public double getgain2() {
				return gain2;
			}
			public void setgain3(double __param) {
				gain3 = __param;	
			}
			
			public double getgain3() {
				return gain3;
			}
		}	
