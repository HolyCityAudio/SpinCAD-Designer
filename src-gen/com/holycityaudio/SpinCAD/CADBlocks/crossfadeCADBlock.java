/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * crossfadeCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.crossfadeControlPanel;
		
	    @SuppressWarnings("unused")
	    public class crossfadeCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private crossfadeControlPanel cp = null;
			
			private double gain1 = 0.5;
			private double gain2 = 0.5;
			private int output1;

			public crossfadeCADBlock(int x, int y) {
				super(x, y);
				setName("Crossfade");					
			setBorderColor(new Color(0x2468f2));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input 1");
				addInputPin(this, "Input 2");
				addOutputPin(this, "Audio Output");
				addControlInputPin(this, "Fade");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new crossfadeControlPanel(this);
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
			int inp1 = -1;
			if(sp != null) {
				inp1 = sp.getRegister();
			}
			sp = this.getPin("Input 2").getPinConnection();
			int inp2 = -1;
			if(sp != null) {
				inp2 = sp.getRegister();
			}
			sp = this.getPin("Fade").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			
			// finally, generate the instructions
			output1 = sfxb.allocateReg();
			if(this.getPin("Input 1").isConnected() == true) {
			if(this.getPin("Input 2").isConnected() == true) {
			sfxb.readRegister(inp1, -gain1);
			sfxb.readRegister(inp2, gain2);
			if(this.getPin("Fade").isConnected() == true) {
			sfxb.mulx(input0);
			}
			
			sfxb.readRegister(inp1, gain1);
			} else {
			sfxb.readRegister(inp1, gain1);
			if(this.getPin("Fade").isConnected() == true) {
			sfxb.mulx(input0);
			}
			
			}
			
			} else {
			sfxb.readRegister(inp2, gain2);
			if(this.getPin("Fade").isConnected() == true) {
			sfxb.mulx(input0);
			}
			
			}
			
			sfxb.writeRegister(output1, 0);
			this.getPin("Audio Output").setRegister(output1);

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
		}	
