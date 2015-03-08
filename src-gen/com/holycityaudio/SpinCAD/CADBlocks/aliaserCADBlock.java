/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * aliaserCADBlock.java
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
	
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
 		import com.holycityaudio.SpinCAD.ControlPanel.aliaserControlPanel;
		
		public class aliaserCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private aliaserControlPanel cp = null;
			
			private double inputGain = 0.5;
			private double ripLevel = 0.5;
			private int flopper;
			private int lastval;

			public aliaserCADBlock(int x, int y) {
				super(x, y);
				setName("Aliaser_Zipper");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Audio_Input");
				addOutputPin(this, "Audio_Output");
				addControlInputPin(this, "Crush");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new aliaserControlPanel(this);
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
			sp = this.getPin("Audio_Input").getPinConnection();
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
			if(this.getPin("Input").isConnected() == true) {
			flopper = sfxb.allocateReg();
			lastval = sfxb.allocateReg();
			sfxb.readRegister(flopper, 1.0);
			sfxb.scaleOffset(1.0, -0.015);
			if(this.getPin("Crush").isConnected() == true) {
			sfxb.skip(GEZ, 4);
			sfxb.readRegister(input, inputGain);
			sfxb.writeRegister(lastval, 0);
			sfxb.readRegister(crusher, 0.6);
			sfxb.scaleOffset(1.0, 0);
			} else {
			sfxb.skip(GEZ, 3);
			sfxb.readRegister(input, inputGain);
			sfxb.writeRegister(lastval, 0);
			sfxb.scaleOffset(0.0, ripLevel);
			}
			
			sfxb.writeRegister(flopper, 0);
			this.getPin("Audio_Output").setRegister(lastval);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setinputGain(double __param) {
				inputGain = __param;	
			}
			
			public double getinputGain() {
				return inputGain;
			}
			public void setripLevel(double __param) {
				ripLevel = __param;	
			}
			
			public double getripLevel() {
				return ripLevel;
			}
		}	
