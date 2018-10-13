/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * AliaserCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.AliaserControlPanel;
		
	    @SuppressWarnings("unused")
	    public class AliaserCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private AliaserControlPanel cp = null;
			
			private double ripLow = 0.002;
			private double ripHigh = 0.1;
			private int old;
			private int new_val;
			private int fptr;
			private int temp;
			private int temp2;
			private int output;

			public AliaserCADBlock(int x, int y) {
				super(x, y);
				setName("Aliaser");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Smooth");
				addOutputPin(this, "Raw");
				addControlInputPin(this, "Rip");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new AliaserControlPanel(this);
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
			sp = this.getPin("Rip").getPinConnection();
			int ripper = -1;
			if(sp != null) {
				ripper = sp.getRegister();
			}
			
			// finally, generate the instructions
			old = sfxb.allocateReg();
			new_val = sfxb.allocateReg();
			fptr = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			temp2 = sfxb.allocateReg();
			output = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.skip(RUN, 4);
			sfxb.clear();
			sfxb.writeRegister(old, 0);
			sfxb.writeRegister(new_val, 0);
			sfxb.writeRegister(fptr, 0);
			sfxb.clear();
			if(this.getPin("Rip").isConnected() == true) {
			sfxb.readRegister(ripper, 1.0);
			double ripScale = ripHigh - ripLow;
			sfxb.scaleOffset(ripScale, ripLow);
			} else {
			sfxb.scaleOffset(0.0, ripHigh);
			}
			
			sfxb.readRegister(fptr, 1);
			sfxb.writeRegister(fptr, 1);
			sfxb.scaleOffset(1, -0.5);
			sfxb.skip(NEG, 5);
			sfxb.writeRegister(fptr, 0);
			sfxb.readRegister(new_val, 1);
			sfxb.writeRegister(old, 0);
			sfxb.readRegister(input, 1.0);
			sfxb.writeRegister(new_val, 0);
			sfxb.clear();
			if(this.getPin("Smooth").isConnected() == true) {
			sfxb.readRegister(new_val, 1.0);
			sfxb.mulx(fptr);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(fptr, -1.0);
			sfxb.scaleOffset(1.0, 0.5);
			sfxb.writeRegister(temp2, 0);
			sfxb.readRegister(old, 1);
			sfxb.mulx(temp2);
			sfxb.writeRegister(temp2, 1);
			sfxb.readRegister(temp2, 1);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp2, 1);
			sfxb.absa();
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(temp2, 1.0);
			sfxb.skip(GEZ, 4);
			sfxb.clear();
			sfxb.readRegister(temp, 1.0);
			sfxb.scaleOffset(-1.0, 0);
			sfxb.writeRegister(temp, 0);
			sfxb.clear();
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(output, 0.0);
			this.getPin("Smooth").setRegister(output);
			}
			
			this.getPin("Raw").setRegister(old);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setripLow(double __param) {
				ripLow = __param;	
			}
			
			public double getripLow() {
				return ripLow;
			}
			public void setripHigh(double __param) {
				ripHigh = __param;	
			}
			
			public double getripHigh() {
				return ripHigh;
			}
		}	
