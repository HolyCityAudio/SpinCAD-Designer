/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * gated_verbCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.gated_verbControlPanel;
		
	    @SuppressWarnings("unused")
	    public class gated_verbCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private gated_verbControlPanel cp = null;
			
			private int kd;
			private int mono;
			private int krt;
			private int lf2;
			private int lf3;
			private int lf4;
			private int lf5;
			private int output1;
			private int output2;

			public gated_verbCADBlock(int x, int y) {
				super(x, y);
				setName("Gated_reverb");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Pre_delay");
				addControlInputPin(this, "Gate_Time");
				addControlInputPin(this, "Damping");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new gated_verbControlPanel(this);
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
			sp = this.getPin("Input_Right").getPinConnection();
			int adcr = -1;
			if(sp != null) {
				adcr = sp.getRegister();
			}
			sp = this.getPin("Pre_delay").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Gate_Time").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Damping").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("pdel", 3277); 
			sfxb.FXallocDelayMem("gdel", 22938); 
			sfxb.FXallocDelayMem("inap1", 127); 
			sfxb.FXallocDelayMem("inap2", 191); 
			sfxb.FXallocDelayMem("inap3", 374); 
			sfxb.FXallocDelayMem("inap4", 573); 
			sfxb.FXallocDelayMem("ap1", 454); 
			sfxb.FXallocDelayMem("ap3", 355); 
			sfxb.FXallocDelayMem("ap2", 307); 
			sfxb.FXallocDelayMem("ap4", 477); 
			sfxb.FXallocDelayMem("rdel", 1203); 
			sfxb.FXallocDelayMem("ldel", 1457); 
			kd = sfxb.allocateReg();
			mono = sfxb.allocateReg();
			krt = sfxb.allocateReg();
			lf2 = sfxb.allocateReg();
			lf3 = sfxb.allocateReg();
			lf4 = sfxb.allocateReg();
			lf5 = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.clear();
			sfxb.writeRegister(kd, 0);
			sfxb.skip(RUN, 4);
			sfxb.writeRegister(lf2, 0);
			sfxb.writeRegister(lf3, 0);
			sfxb.writeRegister(lf4, 0);
			sfxb.writeRegister(lf5, 0);
			sfxb.readRegister(input0, 0.1);
			sfxb.and(0b01111110_00000000_00000000);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readRegister(adcl, 0.25);
			sfxb.readRegister(adcr, 0.25);
			sfxb.FXwriteDelay("pdel", 0, 0);
			sfxb.readDelayPointer(1);
			sfxb.FXreadDelay("gdel+", (int)(2340 * 1.0), 0.2);
			sfxb.FXreadDelay("inap1#", 0, 0.5);
			sfxb.FXwriteAllpass("inap1", 0, -0.5);
			sfxb.FXreadDelay("inap2#", 0, -0.5);
			sfxb.FXwriteAllpass("inap2", 0, 0.5);
			sfxb.FXreadDelay("inap3#", 0, 0.5);
			sfxb.FXwriteAllpass("inap3", 0, -0.5);
			sfxb.FXreadDelay("inap4#", 0, -0.5);
			sfxb.FXwriteAllpass("inap4", 0, 0.5);
			sfxb.FXwriteDelay("gdel", 0, 0);
			sfxb.FXreadDelay("gdel+", (int)(2000 * 1.0), -1);
			sfxb.readRegisterFilter(lf2, 0.5);
			sfxb.writeRegisterHighshelf(lf2, -1);
			sfxb.mulx(kd);
			sfxb.FXreadDelay("gdel+", (int)(2000 * 1.0), 1);
			sfxb.FXwriteDelay("gdel+", (int)(2000 * 1.0), 0);
			sfxb.FXreadDelay("gdel+", (int)(4000 * 1.0), -1);
			sfxb.readRegisterFilter(lf3, 0.5);
			sfxb.writeRegisterHighshelf(lf3, -1);
			sfxb.mulx(kd);
			sfxb.FXreadDelay("gdel+", (int)(4000 * 1.0), 1);
			sfxb.FXwriteDelay("gdel+", (int)(4000 * 1.0), 0);
			sfxb.FXreadDelay("gdel+", (int)(8000 * 1.0), -1);
			sfxb.readRegisterFilter(lf4, 0.5);
			sfxb.writeRegisterHighshelf(lf4, -1);
			sfxb.mulx(kd);
			sfxb.FXreadDelay("gdel+", (int)(8000 * 1.0), 1);
			sfxb.FXwriteDelay("gdel+", (int)(8000 * 1.0), 0);
			sfxb.FXreadDelay("gdel+", (int)(14000 * 1.0), -1);
			sfxb.readRegisterFilter(lf5, 0.5);
			sfxb.writeRegisterHighshelf(lf5, -1);
			sfxb.mulx(kd);
			sfxb.FXreadDelay("gdel+", (int)(14000 * 1.0), 1);
			sfxb.FXwriteDelay("gdel+", (int)(14000 * 1.0), 0);
			sfxb.readRegister(input1, 1);
			sfxb.and(0b01111000_00000000_00000000);
			sfxb.skip(ZRO, 50);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 47);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 43);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 39);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 36);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 32);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 29);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 26);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 22);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 19);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 15);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 12);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 8);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 5);
			sfxb.scaleOffset(1, -0.5625);
			sfxb.skip(ZRO, 2);
			sfxb.clear();
			sfxb.FXreadDelay("gdel+", (int)(22937 * 1.0), 0.45);
			sfxb.FXreadDelay("gdel+", (int)(21879 * 1.0), 0.5);
			sfxb.FXreadDelay("gdel+", (int)(20502 * 1.0), 0.45);
			sfxb.FXreadDelay("gdel+", (int)(19770 * 1.0), 0.4);
			sfxb.FXreadDelay("gdel+", (int)(18723 * 1.0), 0.4);
			sfxb.FXreadDelay("gdel+", (int)(18143 * 1.0), 0.5);
			sfxb.FXreadDelay("gdel+", (int)(17134 * 1.0), 0.5);
			sfxb.FXreadDelay("gdel+", (int)(15832 * 1.0), 0.55);
			sfxb.FXreadDelay("gdel+", (int)(15045 * 1.0), 0.5);
			sfxb.FXreadDelay("gdel+", (int)(13881 * 1.0), 0.5);
			sfxb.FXreadDelay("gdel+", (int)(13056 * 1.0), 0.55);
			sfxb.FXreadDelay("gdel+", (int)(12196 * 1.0), 0.6);
			sfxb.FXreadDelay("gdel+", (int)(11397 * 1.0), 0.6);
			sfxb.FXreadDelay("gdel+", (int)(10008 * 1.0), 0.6);
			sfxb.FXreadDelay("gdel+", (int)(9023 * 1.0), 0.55);
			sfxb.FXreadDelay("gdel+", (int)(7856 * 1.0), 0.4);
			sfxb.FXreadDelay("gdel+", (int)(7087 * 1.0), 0.65);
			sfxb.FXreadDelay("gdel+", (int)(6243 * 1.0), 0.6);
			sfxb.FXreadDelay("gdel+", (int)(5194 * 1.0), 0.7);
			sfxb.FXreadDelay("gdel+", (int)(4023 * 1.0), 0.6);
			sfxb.FXreadDelay("gdel+", (int)(3101 * 1.0), 0.5);
			sfxb.FXreadDelay("gdel+", (int)(1885 * 1.0), 0.7);
			sfxb.FXreadDelay("gdel+", (int)(1156 * 1.0), 0.5);
			sfxb.FXreadDelay("gdel", 0, 0.5);
			sfxb.writeRegister(mono, 1);
			sfxb.FXreadDelay("ldel#", 0, -0.3);
			sfxb.FXreadDelay("ap1#", 0, 0.5);
			sfxb.FXwriteAllpass("ap1", 0, -0.5);
			sfxb.FXreadDelay("ap2#", 0, -0.5);
			sfxb.FXwriteAllpass("ap2", 0, 0.5);
			sfxb.FXwriteDelay("ldel", 0, 1);
			sfxb.writeRegister(output1, 0);
			sfxb.readRegister(mono, 1);
			sfxb.FXreadDelay("rdel#", 0, -0.3);
			sfxb.FXreadDelay("ap3#", 0, 0.5);
			sfxb.FXwriteAllpass("ap3", 0, -0.5);
			sfxb.FXreadDelay("ap4#", 0, -0.5);
			sfxb.FXwriteAllpass("ap4", 0, 0.5);
			sfxb.FXwriteDelay("rdel", 0, 1);
			sfxb.writeRegister(output2, 0);
			this.getPin("Audio_Output_1").setRegister(output1);
			this.getPin("Audio_Output_2").setRegister(output2);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
