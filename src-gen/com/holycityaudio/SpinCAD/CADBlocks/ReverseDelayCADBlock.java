/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ReverseDelayCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ReverseDelayControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ReverseDelayCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ReverseDelayControlPanel cp = null;
			
			private int output;
			private int output2;
			private int ramp;
			private int ramp2;
			private int xfade;

			public ReverseDelayCADBlock(int x, int y) {
				super(x, y);
				setName("Reverse Delay");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addOutputPin(this, "Output2");
				addControlOutputPin(this, "Ramp");
				addControlOutputPin(this, "Ramp2");
				addControlOutputPin(this, "Xfade");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ReverseDelayControlPanel(this);
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
			sfxb.FXallocDelayMem("delay", 32767); 
			output = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			ramp = sfxb.allocateReg();
			ramp2 = sfxb.allocateReg();
			xfade = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.skip(RUN, 3);
			sfxb.scaleOffset(0, -0.25);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.writeRegister(RMP0_RANGE, 0);
			sfxb.readRegister(input, 1.0);
			sfxb.FXwriteDelay("delay#", 0, 0.0);
			sfxb.chorusReadValue(RMP0);
			sfxb.writeRegister(ADDR_PTR, 1.0);
			sfxb.writeRegister(ramp, 1.0);
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.absa();
			sfxb.scaleOffset(-2.0, 0.25);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.scaleOffset(0.5, 0.5);
			sfxb.writeRegister(xfade, 0.0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output, 0.0);
			sfxb.readRegister(ramp, 1.0);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.writeRegister(ramp2, 1.0);
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(1.0, 0.325);
			sfxb.writeRegister(ADDR_PTR, 0.0);
			sfxb.readDelayPointer(1.0);
			sfxb.writeRegister(output2, 0.0);
			}
			
			this.getPin("Output").setRegister(output);
			this.getPin("Output2").setRegister(output2);
			this.getPin("Ramp").setRegister(ramp);
			this.getPin("Ramp2").setRegister(ramp2);
			this.getPin("Xfade").setRegister(xfade);

			}
			
			// create setters and getter for control panel variables
		}	
