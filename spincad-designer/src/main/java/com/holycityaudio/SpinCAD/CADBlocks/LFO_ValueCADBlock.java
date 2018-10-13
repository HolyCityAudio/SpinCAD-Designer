/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * LFO_ValueCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.LFO_ValueControlPanel;
		
	    @SuppressWarnings("unused")
	    public class LFO_ValueCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private LFO_ValueControlPanel cp = null;
			
			private double lfoSel = 0;
			private int output1;

			public LFO_ValueCADBlock(int x, int y) {
				super(x, y);
				setName("LFO Value");					
				// Iterate through pin definitions and allocate or assign as needed
				addControlOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new LFO_ValueControlPanel(this);
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
			
			// finally, generate the instructions
			if(this.getPin("Output").isConnected() == true) {
			output1 = sfxb.allocateReg();
			if(lfoSel == 0) {
			sfxb.chorusReadValue(0);
			}
			
			if(lfoSel == 1) {
			sfxb.chorusReadValue(8);
			}
			
			if(lfoSel == 2) {
			sfxb.chorusReadValue(1);
			}
			
			if(lfoSel == 3) {
			sfxb.chorusReadValue(9);
			}
			
			if(lfoSel == 4) {
			sfxb.chorusReadValue(2);
			}
			
			if(lfoSel == 5) {
			sfxb.chorusReadValue(3);
			}
			
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);
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
