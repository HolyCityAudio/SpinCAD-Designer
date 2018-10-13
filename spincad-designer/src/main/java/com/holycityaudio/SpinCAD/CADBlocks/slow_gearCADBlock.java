/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * slow_gearCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.slow_gearControlPanel;
		
	    @SuppressWarnings("unused")
	    public class slow_gearCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private slow_gearControlPanel cp = null;
			
			private int in;
			private int envlop;
			private int peak;
			private int trig;
			private int ramp;
			private int attack;
			private int slope;
			private int dacl;
			private int debug;
			private int debug2;
			private double thresh = 0.4;

			public slow_gearCADBlock(int x, int y) {
				super(x, y);
				setName("Slow_gear");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Ramp");
				addOutputPin(this, "Debug");
				addOutputPin(this, "Debug2");
				addControlInputPin(this, "Attack");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new slow_gearControlPanel(this);
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
			sp = this.getPin("Attack").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			
			// finally, generate the instructions
			in = sfxb.allocateReg();
			envlop = sfxb.allocateReg();
			peak = sfxb.allocateReg();
			trig = sfxb.allocateReg();
			ramp = sfxb.allocateReg();
			attack = sfxb.allocateReg();
			slope = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			debug = sfxb.allocateReg();
			debug2 = sfxb.allocateReg();
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.FXallocDelayMem("delay", 256); 
			sfxb.readRegister(adcl, 1);
			sfxb.FXwriteDelay("delay", 0, 1);
			sfxb.maxx(envlop, 0.99999);
			sfxb.writeRegister(envlop, 1);
			sfxb.readRegisterFilter(peak, 0.0001);
			sfxb.writeRegisterHighshelf(peak, -1);
			sfxb.skip(GEZ, 1);
			sfxb.clear();
			sfxb.log(1.999, 0.5);
			sfxb.writeRegister(debug, 1.0);
			sfxb.writeRegister(trig, 0);
			sfxb.scaleOffset(0, thresh);
			sfxb.readRegister(trig, 1);
			sfxb.writeRegister(debug2, 1.0);
			sfxb.skip(NEG, 6);
			sfxb.clear();
			sfxb.writeRegister(ramp, 0);
			sfxb.writeRegister(envlop, 0);
			sfxb.writeRegister(peak, 0);
			sfxb.readRegister(attack, 1);
			sfxb.writeRegister(slope, 0);
			if(this.getPin("Attack").isConnected() == true) {
			sfxb.readRegister(input0, 1);
			} else {
			sfxb.scaleOffset(0.0, 0.5);
			}
			
			sfxb.log(-1, 0);
			sfxb.scaleOffset(1, 0.01);
			sfxb.scaleOffset(0.003, 0);
			sfxb.writeRegister(attack, 0);
			sfxb.clear();
			sfxb.readRegister(slope, 1);
			sfxb.readRegister(ramp, 1);
			sfxb.skip(GEZ, 1);
			sfxb.clear();
			sfxb.writeRegister(ramp, 0);
			sfxb.FXreadDelay("delay+", (int)(20 * 1.0), 1);
			sfxb.mulx(ramp);
			sfxb.mulx(ramp);
			sfxb.writeRegister(dacl, 0);
			}
			
			this.getPin("Audio_Output_1").setRegister(dacl);
			this.getPin("Debug").setRegister(debug);
			this.getPin("Debug2").setRegister(debug2);
			this.getPin("Ramp").setRegister(ramp);

			}
			
			// create setters and getter for control panel variables
			public void setthresh(double __param) {
				thresh = Math.pow(10.0, __param/20.0);	
			}
			
			public double getthresh() {
				return thresh;
			}
		}	
