/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * VeeCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.VeeControlPanel;
		
	    @SuppressWarnings("unused")
	    public class VeeCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private VeeControlPanel cp = null;
			
			private int output1;
			private int output2;

			public VeeCADBlock(int x, int y) {
				super(x, y);
				setName("Vee");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Input");
				addControlOutputPin(this, "Output 1");
				addControlOutputPin(this, "Output 2");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new VeeControlPanel(this);
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
			if(this.getPin("Input").isConnected() == true) {
			if(this.getPin("Output 1").isConnected() == true) {
			output1 = sfxb.allocateReg();
			sfxb.readRegister(input, 1.0);
			sfxb.scaleOffset(-2.0, 0.99902);
			sfxb.skip(GEZ, 1);
			sfxb.clear();
			sfxb.writeRegister(output1, 0);
			}
			
			if(this.getPin("Output 2").isConnected() == true) {
			output2 = sfxb.allocateReg();
			sfxb.scaleOffset(0, 0.99902);
			sfxb.readRegister(input, -0.99902);
			sfxb.scaleOffset(-1.99804, 0.99902);
			sfxb.skip(GEZ, 1);
			sfxb.clear();
			sfxb.writeRegister(output2, 0);
			}
			
			this.getPin("Output 1").setRegister(output1);
			this.getPin("Output 2").setRegister(output2);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
