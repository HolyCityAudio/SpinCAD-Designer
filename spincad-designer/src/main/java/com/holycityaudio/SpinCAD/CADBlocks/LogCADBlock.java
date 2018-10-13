/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * LogCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.LogControlPanel;
		
	    @SuppressWarnings("unused")
	    public class LogCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private LogControlPanel cp = null;
			
			private double multiplier = 0.5;
			private double log_offset = 0.5;
			private int output1;

			public LogCADBlock(int x, int y) {
				super(x, y);
				setName("Log");					
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Control Input");
				addControlOutputPin(this, "Log Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new LogControlPanel(this);
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
			sp = this.getPin("Control Input").getPinConnection();
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			
			// finally, generate the instructions
			output1 = sfxb.allocateReg();
			if(this.getPin("Control Input").isConnected() == true) {
			sfxb.readRegister(input, 1);
			sfxb.log(multiplier, log_offset);
			sfxb.writeRegister(output1, 0);
			this.getPin("Log Output").setRegister(output1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setmultiplier(double __param) {
				multiplier = __param;	
			}
			
			public double getmultiplier() {
				return multiplier;
			}
			public void setlog_offset(double __param) {
				log_offset = __param;	
			}
			
			public double getlog_offset() {
				return log_offset;
			}
		}	
