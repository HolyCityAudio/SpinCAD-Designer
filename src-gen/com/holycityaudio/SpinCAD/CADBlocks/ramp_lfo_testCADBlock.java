/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ramp_lfo_testCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ramp_lfo_testControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ramp_lfo_testCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ramp_lfo_testControlPanel cp = null;
			
			private int ramp0;
			private int ramp1;

			public ramp_lfo_testCADBlock(int x, int y) {
				super(x, y);
				setName("Ramp_LFO_Test");					
				// Iterate through pin definitions and allocate or assign as needed
				addControlOutputPin(this, "Ramp0");
				addControlOutputPin(this, "Ramp1");
				addControlInputPin(this, "Ramp_0_Rate");
				addControlInputPin(this, "Ramp_1_Rate");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ramp_lfo_testControlPanel(this);
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
			sp = this.getPin("Ramp_0_Rate").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Ramp_1_Rate").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			
			// finally, generate the instructions
			ramp0 = sfxb.allocateReg();
			ramp1 = sfxb.allocateReg();
			sfxb.skip(RUN, 2);
			sfxb.loadRampLFO((int) 0, (int) 141, (int) 4096);
			sfxb.loadRampLFO((int) 1, (int) 221, (int) 4096);
			if(this.getPin("Ramp_0_Rate").isConnected() == true) {
			sfxb.readRegister(input0, 1);
			sfxb.writeRegister(RMP0_RATE, 0);
			}
			
			if(this.getPin("Ramp_1_Rate").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			sfxb.writeRegister(RMP1_RATE, 0);
			}
			
			sfxb.chorusReadValue(RMP0);
			sfxb.writeRegister(ramp0, 0);
			sfxb.chorusReadValue(RMP1);
			sfxb.writeRegister(ramp1, 0);
			this.getPin("Ramp0").setRegister(ramp0);
			this.getPin("Ramp1").setRegister(ramp1);

			}
			
			// create setters and getter for control panel variables
		}	
