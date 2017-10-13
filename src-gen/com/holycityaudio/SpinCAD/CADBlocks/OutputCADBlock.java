/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * OutputCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.OutputControlPanel;
		
	    @SuppressWarnings("unused")
	    public class OutputCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private OutputControlPanel cp = null;
			
			boolean mono = false;
			boolean offset0 = false;
			private double gain1 = 1.0;
			private double gain2 = 1.0;

			public OutputCADBlock(int x, int y) {
				super(x, y);
				setName("Output");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input 1");
				addInputPin(this, "Input 2");
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
						cp = new OutputControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly
			//		mono = false;
			//		offset0 = false;

			
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
			
			// finally, generate the instructions
			if(mono == true) {
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(input1, gain1);
			}
			
			if(this.getPin("Input 2").isConnected() == true) {
			sfxb.readRegister(input2, gain2);
			}
			
			if(offset0 == true) {
			sfxb.scaleOffset(1.0, 0.02);
			}
			
			sfxb.writeRegister(DACL, 1);
			sfxb.writeRegister(DACR, 0);
			} else {
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(input1, gain1);
			if(offset0 == true) {
			sfxb.scaleOffset(1.0, 0.02);
			}
			
			sfxb.writeRegister(DACL, 0);
			}
			
			if(this.getPin("Input 2").isConnected() == true) {
			sfxb.readRegister(input2, gain2);
			if(offset0 == true) {
			sfxb.scaleOffset(1.0, 0.02);
			}
			
			sfxb.writeRegister(DACR, 0);
			}
			
			}
			

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
			public void setmono(boolean __param) {
				mono = __param;	
			}
			
			public boolean getmono() {
				return mono;
			}
			public void setoffset0(boolean __param) {
				offset0 = __param;	
			}
			
			public boolean getoffset0() {
				return offset0;
			}
		}	
