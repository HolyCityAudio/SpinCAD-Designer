/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * SampleHoldCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.SampleHoldControlPanel;
		
	    @SuppressWarnings("unused")
	    public class SampleHoldCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private SampleHoldControlPanel cp = null;
			
			private int output;
			private int hold;
			private double symmetry = 0.25;
			private double lfoSel = 0;

			public SampleHoldCADBlock(int x, int y) {
				super(x, y);
				setName("SampleHold");					
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Control_In");
				addControlInputPin(this, "Rate");
				addControlOutputPin(this, "Sample_Hold");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new SampleHoldControlPanel(this);
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
			sp = this.getPin("Control_In").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Rate").getPinConnection();
			int rate = -1;
			if(sp != null) {
				rate = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			hold = sfxb.allocateReg();
			if(this.getPin("Control_In").isConnected() == true) {
			if(this.getPin("Rate").isConnected() == true) {
			sfxb.skip(RUN, 1);
			sfxb.loadRampLFO((int) lfoSel, (int) 20, (int) 4096);
			sfxb.loadAccumulator(rate);
			sfxb.mulx(rate);
			sfxb.mulx(rate);
			sfxb.scaleOffset(0.5, 0.1);
			if(lfoSel == 0) {
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.chorusReadValue(RMP0);
			} else {
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.chorusReadValue(RMP1);
			}
			
			sfxb.scaleOffset(1.0, -symmetry);
			sfxb.skip(ZRC, 4);
			sfxb.clear();
			sfxb.readRegister(hold, 1);
			sfxb.writeRegister(output, 0);
			sfxb.skip(ZRO, 2);
			sfxb.loadAccumulator(input1);
			sfxb.writeRegister(hold, 0);
			this.getPin("Sample_Hold").setRegister(output);
			}
			
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setlfoSel(int __param) {
				lfoSel = (double) __param;	
			}
			
			public int getlfoSel() {
				return (int) lfoSel;
			}
		}	
