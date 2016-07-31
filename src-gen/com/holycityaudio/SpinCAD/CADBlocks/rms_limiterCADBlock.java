/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rms_limiterCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.rms_limiterControlPanel;
		
	    @SuppressWarnings("unused")
	    public class rms_limiterCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private rms_limiterControlPanel cp = null;
			
			private int sigin;
			private int avg;
			private int rms;
			private int output;
			private int square;
			private int logPin;
			private double inGain = 0.1;
			private double filt = 0.1;

			public rms_limiterCADBlock(int x, int y) {
				super(x, y);
				setName("RMS_Limiter");					
			setBorderColor(new Color(0x009595));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Side Chain");
				addOutputPin(this, "Output");
				addControlOutputPin(this, "RMS");
				addControlOutputPin(this, "Square");
				addControlOutputPin(this, "Log");
				addControlOutputPin(this, "Avg");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new rms_limiterControlPanel(this);
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
			sp = this.getPin("Side Chain").getPinConnection();
			int sidechain = -1;
			if(sp != null) {
				sidechain = sp.getRegister();
			}
			
			// finally, generate the instructions
			sigin = sfxb.allocateReg();
			avg = sfxb.allocateReg();
			rms = sfxb.allocateReg();
			output = sfxb.allocateReg();
			square = sfxb.allocateReg();
			logPin = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, inGain);
			sfxb.writeRegister(sigin, 1);
			sfxb.mulx(sigin);
			sfxb.writeRegister(square, 1.0);
			sfxb.readRegisterFilter(avg, filt);
			sfxb.writeRegister(avg, 1);
			sfxb.log(-0.5, -0.125);
			sfxb.writeRegister(logPin, 1.0);
			sfxb.exp(1, 0);
			sfxb.writeRegister(rms, 1);
			sfxb.mulx(sidechain);
			sfxb.scaleOffset(1.5, 0);
			sfxb.writeRegister(output, 0);
			this.getPin("Output").setRegister(output);
			this.getPin("RMS").setRegister(rms);
			this.getPin("Square").setRegister(square);
			this.getPin("Log").setRegister(logPin);
			this.getPin("Avg").setRegister(avg);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setinGain(double __param) {
				inGain = __param;	
			}
			
			public double getinGain() {
				return inGain;
			}
		}	
