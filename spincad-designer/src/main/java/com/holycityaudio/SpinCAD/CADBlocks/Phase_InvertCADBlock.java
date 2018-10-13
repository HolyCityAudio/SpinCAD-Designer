/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Phase_InvertCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Phase_InvertControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Phase_InvertCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Phase_InvertControlPanel cp = null;
			
			private int output;

			public Phase_InvertCADBlock(int x, int y) {
				super(x, y);
				setName("Phase_Invert");					
			setBorderColor(new Color(0x2468f2));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Phase_InvertControlPanel(this);
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
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, -1.0);
			sfxb.writeRegister(output, 0);
			this.getPin("Output").setRegister(output);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
