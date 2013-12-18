/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * slow_gearCADBlock.java
 * Copyright (C)2013 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
 * 
 *   This program is free software: you can redistribute it and/or modify 
 *   it under the terms of the GNU General Public License as published by 
 *   the Free Software Foundation, either version 3 of the License, or 
 *   (at your option) any later version. 
 * 
 *   This program is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
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
		
		public class slow_gearCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private int in;
			private int envlop;
			private int peak;
			private int trig;
			private int ramp;
			private int attack;
			private int slope;
			private int dacl;

			public slow_gearCADBlock(int x, int y) {
				super(x, y);
				setName("Slow_gear");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Ramp");
				addControlInputPin(this, "Attack");
			}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
	//			new slow_gearControlPanel(this);
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly
			in = sfxb.allocateReg();
			envlop = sfxb.allocateReg();
			peak = sfxb.allocateReg();
			trig = sfxb.allocateReg();
			ramp = sfxb.allocateReg();
			attack = sfxb.allocateReg();
			slope = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			sfxb.FXallocDelayMem("delay", 256); 
			
			sfxb.comment(getName());
			
			SpinCADPin sp = null;
					
			// Iterate through pin definitions and connect or assign as needed
			sp = this.getPin("Input_Left").getPinConnection();
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			
			this.getPin("Audio_Output_1").setRegister(dacl);
			this.getPin("Ramp").setRegister(ramp);
			sp = this.getPin("Attack").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input_Left").getPinConnection() != null) {
			//			System.out.println("IsPinConnected! " + "Input_Left"); 
			sfxb.readRegister(adcl, 1);
			sfxb.FXwriteDelay("delay", 0, 1);
			sfxb.writeRegister(in, 1);
			sfxb.maxx(envlop, 0.9999);
			sfxb.writeRegister(envlop, 1);
			sfxb.readRegisterFilter(peak, 0.0001);
			sfxb.writeRegisterHighshelf(peak, -1);
			sfxb.skip(GEZ, 1);
			sfxb.clear();
			sfxb.log(1.999, 0.5);
			sfxb.writeRegister(trig, 0);
			sfxb.scaleOffset(0, 0.4);
			sfxb.readRegister(trig, 1);
			sfxb.skip(NEG, 6);
			sfxb.clear();
			sfxb.writeRegister(ramp, 0);
			sfxb.writeRegister(envlop, 0);
			sfxb.writeRegister(peak, 0);
			sfxb.readRegister(attack, 1);
			sfxb.writeRegister(slope, 0);
			if(this.getPin("Attack").getPinConnection() != null) {
			//			System.out.println("IsPinConnected! " + "Attack"); 
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
			sfxb.FXreadDelay("delay+", 20, 1);
			sfxb.mulx(ramp);
			sfxb.mulx(ramp);
			sfxb.writeRegister(dacl, 0);
			}
			

			// Say 'bye-bye'
			// System.out.println("Slow_gear" + " code gen!");
			}
			
			// create setters and getter for control panel variables
		}	
