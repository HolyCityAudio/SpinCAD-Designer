/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * StutterCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.StutterControlPanel;
		
	    @SuppressWarnings("unused")
	    public class StutterCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private StutterControlPanel cp = null;
			
			private int output;
			private double delayLength = 32767;
			private double fadeTimeFilt = 0.0015;
			private int fadeFilt;

			public StutterCADBlock(int x, int y) {
				super(x, y);
				setName("Stutter");					
			setBorderColor(new Color(0xf2c214));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Stutter");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new StutterControlPanel(this);
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
			sp = this.getPin("Stutter").getPinConnection();
			int stutter = -1;
			if(sp != null) {
				stutter = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("delayRam", delayLength); 
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, 1);
			sfxb.FXwriteDelay("delayRam", 0, 0);
			if(fadeTimeFilt == 0) {
			if(this.getPin("Stutter").isConnected() == true) {
			sfxb.readRegister(stutter, 1.0);
			sfxb.scaleOffset(1.0, -0.5);
			sfxb.skip(NEG, 3);
			sfxb.clear();
			sfxb.FXreadDelay("delayRam#", 0, 1.0);
			sfxb.skip(RUN, 2);
			sfxb.clear();
			sfxb.FXreadDelay("delayRam", 0, 1.0);
			sfxb.writeRegister(output, 0.0);
			} else {
			sfxb.FXreadDelay("delayRam#", 0, 1.0);
			sfxb.writeRegister(output, 0.0);
			}
			
			} else {
			fadeFilt = sfxb.allocateReg();
			sfxb.readRegister(stutter, 1.0);
			sfxb.scaleOffset(1.0, -0.5);
			sfxb.skip(NEG, 2);
			sfxb.clear();
			sfxb.skip(RUN, 1);
			sfxb.scaleOffset(0.0, 0.9990234375);
			sfxb.readRegisterFilter(fadeFilt, fadeTimeFilt);
			sfxb.writeRegister(fadeFilt, 0.0000000000);
			sfxb.FXreadDelay("delayRam", 0, -0.5000000000);
			sfxb.FXreadDelay("delayRam#", 0, 0.5000000000);
			sfxb.mulx(fadeFilt);
			sfxb.FXreadDelay("delayRam", 0, 0.5000000000);
			sfxb.writeRegister(output, 0.0000000000);
			}
			
			}
			
			this.getPin("Output").setRegister(output);

			}
			
			// create setters and getter for control panel variables
			public void setdelayLength(double __param) {
				delayLength = __param;	
			}
			
			public double getdelayLength() {
				return delayLength;
			}
			public void setfadeTimeFilt(double __param) {
				fadeTimeFilt = __param;	
			}
			
			public double getfadeTimeFilt() {
				return fadeTimeFilt;
			}
		}	
