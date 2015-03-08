/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Test_chorusCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Test_chorusControlPanel;
		
		public class Test_chorusCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private Test_chorusControlPanel cp = null;
			
			private int dacl;
			private int dacr;

			public Test_chorusCADBlock(int x, int y) {
				super(x, y);
				setName("Test_Chorus");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Test_chorusControlPanel(this);
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
			sp = this.getPin("Input_Left").getPinConnection();
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("delayl", 512); 
			dacl = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) 5, (int) 1024);
			sfxb.loadAccumulator(adcl);
			sfxb.FXwriteDelay("delayl", 0, 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "delayl^", 0);
			sfxb.FXchorusReadDelay(SIN0, SIN, "delayl^+", 1);
			sfxb.writeRegister(dacl, 0);
			sfxb.chorusReadValue(SIN0);
			sfxb.writeRegister(dacr, 0);

			}
			
			// create setters and getter for control panel variables
		}	
