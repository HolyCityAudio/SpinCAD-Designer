/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * noise_block24CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.noise_block24ControlPanel;
		
		public class noise_block24CADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private noise_block24ControlPanel cp = null;
			
			private int lfsr;
			private int lfsr2;
			private int temp;
			private int output1;

			public noise_block24CADBlock(int x, int y) {
				super(x, y);
				setName("Noise_Block");	
				// Iterate through pin definitions and allocate or assign as needed
				addOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new noise_block24ControlPanel(this);
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
			lfsr = sfxb.allocateReg();
			lfsr2 = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			sfxb.skip(RUN, 3);
			sfxb.loadAccumulator(ADCL);
			sfxb.or(0x001000);
			sfxb.writeRegister(lfsr, 0);
			sfxb.loadAccumulator(lfsr);
			sfxb.and(0x00ffff);
			sfxb.writeRegister(lfsr2, 1.0);
			sfxb.readRegister(lfsr2, 0.5);
			sfxb.readRegister(lfsr2, 0.25);
			sfxb.readRegister(lfsr2, 0.0078125);
			sfxb.and(0x000001);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(lfsr, 0.5);
			sfxb.and(0x7fffff);
			sfxb.writeRegister(lfsr, 0);
			sfxb.loadAccumulator(temp);
			sfxb.skip(ZRO, 4);
			sfxb.scaleOffset(0, 0);
			sfxb.readRegister(lfsr, 1.0);
			sfxb.or(0x800000);
			sfxb.writeRegister(lfsr, 0);
			sfxb.loadAccumulator(lfsr);
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);

			}
			
			// create setters and getter for control panel variables
		}	
