/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * expanderCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.expanderControlPanel;
		
	    @SuppressWarnings("unused")
	    public class expanderCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private expanderControlPanel cp = null;
			
			private double ripLow = 0.002;
			private double ripHigh = 0.1;
			private int old;
			private int new_val;
			private int fptr;
			private int temp2;
			private int output;
			private int temp;
			private int gate;

			public expanderCADBlock(int x, int y) {
				super(x, y);
				setName("Expander");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Smooth");
				addOutputPin(this, "Raw");
				addControlInputPin(this, "Crush");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new expanderControlPanel(this);
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
			sp = this.getPin("Crush").getPinConnection();
			int crusher = -1;
			if(sp != null) {
				crusher = sp.getRegister();
			}
			
			// finally, generate the instructions
			old = sfxb.allocateReg();
			new_val = sfxb.allocateReg();
			fptr = sfxb.allocateReg();
			temp2 = sfxb.allocateReg();
			output = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			temp = sfxb.allocateReg();
			gate = sfxb.allocateReg();
			sfxb.writeRegister(temp, 0.02);
			sfxb.absa();
			sfxb.readRegister(gate, 0.9998);
			sfxb.writeRegister(gate, 1.999);
			sfxb.mulx(temp);
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
