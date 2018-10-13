/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * New_OscillatorCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.New_OscillatorControlPanel;
		
	    @SuppressWarnings("unused")
	    public class New_OscillatorCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private New_OscillatorControlPanel cp = null;
			
			private double freqVar = 0.15;
			private int s;
			private int c;
			private int sineOutput;
			private int sqrOutput;

			public New_OscillatorCADBlock(int x, int y) {
				super(x, y);
				setName("Oscillator II");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Frequency");
				addControlInputPin(this, "Width");
				addControlOutputPin(this, "Sine Output");
				addControlOutputPin(this, "Square Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new New_OscillatorControlPanel(this);
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
			sp = this.getPin("Frequency").getPinConnection();
			int freq = -1;
			if(sp != null) {
				freq = sp.getRegister();
			}
			sp = this.getPin("Width").getPinConnection();
			int width = -1;
			if(sp != null) {
				width = sp.getRegister();
			}
			
			// finally, generate the instructions
			s = sfxb.allocateReg();
			c = sfxb.allocateReg();
			sineOutput = sfxb.allocateReg();
			sfxb.skip(RUN, 3);
			sfxb.scaleOffset(0, 0.5);
			sfxb.writeRegister(s, 0);
			sfxb.writeRegister(c, 0);
			sfxb.readRegister(c, freqVar);
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.mulx(freq);
			}
			
			sfxb.readRegister(s, 1);
			sfxb.writeRegister(s, -freqVar);
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.mulx(freq);
			}
			
			sfxb.readRegister(c, 1);
			sfxb.writeRegister(c, 1);
			if(this.getPin("Width").isConnected() == true) {
			sfxb.mulx(width);
			}
			
			if(this.getPin("Square Output").isConnected() == true) {
			sfxb.writeRegister(sineOutput, 1);
			sqrOutput = sfxb.allocateReg();
			sfxb.skip(NEG, 2);
			sfxb.scaleOffset(0, 0.5);
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(0, -0.5);
			if(this.getPin("Width").isConnected() == true) {
			sfxb.mulx(width);
			}
			
			sfxb.writeRegister(sqrOutput, 0);
			this.getPin("Square Output").setRegister(sqrOutput);
			} else {
			sfxb.writeRegister(sineOutput, 0);
			}
			
			this.getPin("Sine Output").setRegister(sineOutput);

			}
			
			// create setters and getter for control panel variables
			public void setfreqVar(double __param) {
				freqVar = __param;	
			}
			
			public double getfreqVar() {
				return freqVar;
			}
		}	
