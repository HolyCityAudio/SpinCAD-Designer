/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * noise_amzCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.noise_amzControlPanel;
		
	    @SuppressWarnings("unused")
	    public class noise_amzCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private noise_amzControlPanel cp = null;
			
			private double gain = 1.0;
			private double controlRange = 0;
			private int LFSR;
			private int TEMP;
			private int output1;

			public noise_amzCADBlock(int x, int y) {
				super(x, y);
				setName("Noise_AMZ");					
				// Iterate through pin definitions and allocate or assign as needed
				addOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new noise_amzControlPanel(this);
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
			LFSR = sfxb.allocateReg();
			TEMP = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			sfxb.skip(RUN, 2);
			sfxb.scaleOffset(0, 0.666);
			sfxb.writeRegister(LFSR, 0);
			sfxb.loadAccumulator(LFSR);
			sfxb.and(0x000001);
			sfxb.writeRegister(TEMP, 0);
			sfxb.readRegister(LFSR, 0.5);
			sfxb.and(0x7FFFFF);
			sfxb.writeRegister(LFSR, 0);
			sfxb.loadAccumulator(TEMP);
			sfxb.skip(ZRO, 4);
			sfxb.clear();
			sfxb.loadAccumulator(LFSR);
			sfxb.xor(0xD80000);
			sfxb.writeRegister(LFSR, 0);
			sfxb.loadAccumulator(LFSR);
			if(controlRange == 0) {
			sfxb.absa();
			}
			
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setcontrolRange(int __param) {
				controlRange = (double) __param;	
			}
			
			public int getcontrolRange() {
				return (int) controlRange;
			}
		}	
