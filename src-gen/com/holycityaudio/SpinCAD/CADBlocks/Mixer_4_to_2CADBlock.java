/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Mixer_4_to_2CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Mixer_4_to_2ControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Mixer_4_to_2CADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Mixer_4_to_2ControlPanel cp = null;
			
			private double gain1 = 1.0;
			private double gain2 = 1.0;
			private double gain3 = 1.0;
			private double gain4 = 1.0;
			private int output1;
			private int output2;

			public Mixer_4_to_2CADBlock(int x, int y) {
				super(x, y);
				setName("Mixer 4:2");					
			setBorderColor(new Color(0x2468f2));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input 1");
				addInputPin(this, "Input 2");
				addInputPin(this, "Input 3");
				addInputPin(this, "Input 4");
				addOutputPin(this, "Output1");
				addOutputPin(this, "Output2");
				addControlInputPin(this, "Level 1");
				addControlInputPin(this, "Level 2");
				addControlInputPin(this, "Level 3");
				addControlInputPin(this, "Level 4");
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
						cp = new Mixer_4_to_2ControlPanel(this);
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
			sp = this.getPin("Input 4").getPinConnection();
			int input4 = -1;
			if(sp != null) {
				input4 = sp.getRegister();
			}
			sp = this.getPin("Level 1").getPinConnection();
			int level_1 = -1;
			if(sp != null) {
				level_1 = sp.getRegister();
			}
			sp = this.getPin("Level 2").getPinConnection();
			int level_2 = -1;
			if(sp != null) {
				level_2 = sp.getRegister();
			}
			sp = this.getPin("Level 3").getPinConnection();
			int level_3 = -1;
			if(sp != null) {
				level_3 = sp.getRegister();
			}
			sp = this.getPin("Level 4").getPinConnection();
			int level_4 = -1;
			if(sp != null) {
				level_4 = sp.getRegister();
			}
			
			// finally, generate the instructions
			output1 = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(input1, gain1);
			if(this.getPin("Level 1").isConnected() == true) {
			sfxb.mulx(level_1);
			}
			
			sfxb.writeRegister(output1, 0.0);
			}
			
			if(this.getPin("Input 2").isConnected() == true) {
			sfxb.readRegister(input2, gain2);
			if(this.getPin("Level 2").isConnected() == true) {
			sfxb.mulx(level_2);
			}
			
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(output1, 1.0);
			}
			
			sfxb.writeRegister(output1, 0.0);
			}
			
			if(this.getPin("Input 3").isConnected() == true) {
			sfxb.readRegister(input3, gain3);
			if(this.getPin("Level 3").isConnected() == true) {
			sfxb.mulx(level_3);
			}
			
			sfxb.writeRegister(output2, 0.0);
			}
			
			if(this.getPin("Input 4").isConnected() == true) {
			sfxb.readRegister(input4, gain4);
			if(this.getPin("Level 4").isConnected() == true) {
			sfxb.mulx(level_4);
			}
			
			if(this.getPin("Input 3").isConnected() == true) {
			sfxb.readRegister(output2, 1.0);
			}
			
			sfxb.writeRegister(output2, 0.0);
			}
			
			this.getPin("Output1").setRegister(output1);
			this.getPin("Output2").setRegister(output2);

			}
			
			// create setters and getter for control panel variables
			public void setgain1(double __param) {
				gain1 = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain1() {
				return gain1;
			}
			public void setgain2(double __param) {
				gain2 = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain2() {
				return gain2;
			}
			public void setgain3(double __param) {
				gain3 = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain3() {
				return gain3;
			}
			public void setgain4(double __param) {
				gain4 = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain4() {
				return gain4;
			}
		}	
