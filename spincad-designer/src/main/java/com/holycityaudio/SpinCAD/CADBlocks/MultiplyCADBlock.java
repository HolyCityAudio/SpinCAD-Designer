/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * MultiplyCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.MultiplyControlPanel;
		
	    @SuppressWarnings("unused")
	    public class MultiplyCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private MultiplyControlPanel cp = null;
			
			private int output;

			public MultiplyCADBlock(int x, int y) {
				super(x, y);
				setName("Multiply");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Input 1");
				addControlInputPin(this, "Input 2");
				addControlOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new MultiplyControlPanel(this);
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
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			if(this.getPin("Input 1").isConnected() == true) {
			sfxb.readRegister(input1, 1.0);
			if(this.getPin("Input 2").isConnected() == true) {
			sfxb.mulx(input2);
			}
			
			sfxb.writeRegister(output, 0);
			this.getPin("Output").setRegister(output);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
