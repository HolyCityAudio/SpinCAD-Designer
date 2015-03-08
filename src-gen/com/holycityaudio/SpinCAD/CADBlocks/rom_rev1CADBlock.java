/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rom_rev1CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.rom_rev1ControlPanel;
		
		public class rom_rev1CADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private rom_rev1ControlPanel cp = null;
			
			private double gain = 0.5;
			private double kiap = 0.5;
			private int temp;
			private int lpf1;
			private int lpf2;
			private int lpf3;
			private int lpf4;
			private int hpf1;
			private int hpf2;
			private int hpf3;
			private int hpf4;
			private int rt;
			private int hf;
			private int lf;
			private int lapout;
			private int dacl;
			private int rapout;
			private int dacr;

			public rom_rev1CADBlock(int x, int y) {
				super(x, y);
				setName("ROM_Reverb_1");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Output_Left");
				addOutputPin(this, "Output_Right");
				addControlInputPin(this, "Reverb_Time");
				addControlInputPin(this, "Low_Freq");
				addControlInputPin(this, "High_Freq");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new rom_rev1ControlPanel(this);
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
			sp = this.getPin("Reverb_Time").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Low_Freq").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("High_Freq").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input_Right").isConnected() == true) {
			sfxb.FXallocDelayMem("rap1", 186); 
			sfxb.FXallocDelayMem("rap2", 253); 
			sfxb.FXallocDelayMem("rap3", 302); 
			sfxb.FXallocDelayMem("rap4", 498); 
			}
			
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.FXallocDelayMem("lap1", 156); 
			sfxb.FXallocDelayMem("lap2", 223); 
			sfxb.FXallocDelayMem("lap3", 332); 
			sfxb.FXallocDelayMem("lap4", 548); 
			sfxb.FXallocDelayMem("ap1", 1251); 
			sfxb.FXallocDelayMem("ap1b", 1751); 
			sfxb.FXallocDelayMem("ap2", 1443); 
			sfxb.FXallocDelayMem("ap2b", 1343); 
			sfxb.FXallocDelayMem("ap3", 1582); 
			sfxb.FXallocDelayMem("ap3b", 1981); 
			sfxb.FXallocDelayMem("ap4", 1274); 
			sfxb.FXallocDelayMem("ap4b", 1382); 
			sfxb.FXallocDelayMem("del1", 5859); 
			sfxb.FXallocDelayMem("del2", 4145); 
			sfxb.FXallocDelayMem("del3", 3476); 
			sfxb.FXallocDelayMem("del4", 4568); 
			temp = sfxb.allocateReg();
			lpf1 = sfxb.allocateReg();
			lpf2 = sfxb.allocateReg();
			lpf3 = sfxb.allocateReg();
			lpf4 = sfxb.allocateReg();
			hpf1 = sfxb.allocateReg();
			hpf2 = sfxb.allocateReg();
			hpf3 = sfxb.allocateReg();
			hpf4 = sfxb.allocateReg();
			rt = sfxb.allocateReg();
			hf = sfxb.allocateReg();
			lf = sfxb.allocateReg();
			lapout = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			if(this.getPin("Output_Right").isConnected() == true) {
			rapout = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			}
			
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) 12, (int) 160);
			sfxb.FXchorusReadDelay(SIN0, 6, "ap1+", 50);
			sfxb.FXchorusReadDelay(SIN0, 0, "ap1+", 51);
			sfxb.FXwriteDelay("ap1+", 100, 0);
			sfxb.FXchorusReadDelay(SIN0, 7, "ap3+", 50);
			sfxb.FXchorusReadDelay(SIN0, 1, "ap3+", 51);
			sfxb.FXwriteDelay("ap3+", 100, 0);
			sfxb.readRegister(POT0, 1.0);
			sfxb.scaleOffset(0.8, 0.1);
			sfxb.writeRegister(rt, 0);
			sfxb.readRegister(POT1, 1.0);
			sfxb.scaleOffset(0.8, -0.8);
			sfxb.writeRegister(hf, 0);
			sfxb.readRegister(POT2, 1.0);
			sfxb.scaleOffset(0.8, -0.8);
			sfxb.writeRegister(lf, 0);
			sfxb.readRegister(adcl, gain);
			sfxb.FXreadDelay("lap1#", 0, -kiap);
			sfxb.FXwriteAllpass("lap1", 0, kiap);
			sfxb.FXreadDelay("lap2#", 0, -kiap);
			sfxb.FXwriteAllpass("lap2", 0, kiap);
			sfxb.FXreadDelay("lap3#", 0, -kiap);
			sfxb.FXwriteAllpass("lap3", 0, kiap);
			sfxb.FXreadDelay("lap4#", 0, -kiap);
			sfxb.FXwriteAllpass("lap4", 0, kiap);
			sfxb.writeRegister(lapout, 0);
			if(this.getPin("Input_Right").isConnected() == true) {
			sfxb.readRegister(adcr, gain);
			sfxb.FXreadDelay("rap1#", 0, -kiap);
			sfxb.FXwriteAllpass("rap1", 0, kiap);
			sfxb.FXreadDelay("rap2#", 0, -kiap);
			sfxb.FXwriteAllpass("rap2", 0, kiap);
			sfxb.FXreadDelay("rap3#", 0, -kiap);
			sfxb.FXwriteAllpass("rap3", 0, kiap);
			sfxb.FXreadDelay("rap4#", 0, -kiap);
			sfxb.FXwriteAllpass("rap4", 0, kiap);
			sfxb.writeRegister(rapout, 0);
			}
			
			sfxb.FXreadDelay("del4#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.readRegister(lapout, 1.0);
			sfxb.FXreadDelay("ap1#", 0, -0.6);
			sfxb.FXwriteAllpass("ap1", 0, 0.6);
			sfxb.FXreadDelay("ap1b#", 0, -0.6);
			sfxb.FXwriteAllpass("ap1b", 0, 0.6);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf1, 0.5);
			sfxb.writeRegisterHighshelf(lpf1, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf1, 0.05);
			sfxb.writeRegisterLowshelf(hpf1, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del1", 0, 0.0);
			sfxb.FXreadDelay("del1#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.FXreadDelay("ap2#", 0, -0.6);
			sfxb.FXwriteAllpass("ap2", 0, 0.6);
			sfxb.FXreadDelay("ap2b#", 0, -0.6);
			sfxb.FXwriteAllpass("ap2b", 0, 0.6);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf2, 0.5);
			sfxb.writeRegisterHighshelf(lpf2, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf2, 0.05);
			sfxb.writeRegisterLowshelf(hpf2, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del2", 0, 0.0);
			sfxb.FXreadDelay("del2#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.readRegister(rapout, 1.0);
			sfxb.FXreadDelay("ap3#", 0, -0.6);
			sfxb.FXwriteAllpass("ap3", 0, 0.6);
			sfxb.FXreadDelay("ap3b#", 0, -0.6);
			sfxb.FXwriteAllpass("ap3b", 0, 0.6);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf3, 0.5);
			sfxb.writeRegisterHighshelf(lpf3, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf3, 0.05);
			sfxb.writeRegisterLowshelf(hpf3, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del3", 0, 0.0);
			sfxb.FXreadDelay("del3#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.FXreadDelay("ap4#", 0, -0.6);
			sfxb.FXwriteAllpass("ap4", 0, 0.6);
			sfxb.FXreadDelay("ap4b#", 0, -0.6);
			sfxb.FXwriteAllpass("ap4b", 0, 0.6);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf4, 0.5);
			sfxb.writeRegisterHighshelf(lpf4, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf4, 0.05);
			sfxb.writeRegisterLowshelf(hpf4, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del4", 0, 0.0);
			sfxb.FXreadDelay("del1+", 2630, 1.5);
			sfxb.FXreadDelay("del2+", 1943, 1.2);
			sfxb.FXreadDelay("del3+", 3200, 1.0);
			sfxb.FXreadDelay("del4+", 4016, 0.8);
			sfxb.writeRegister(dacl, 0.0);
			}
			
			if(this.getPin("Output_Right").isConnected() == true) {
			sfxb.FXreadDelay("del3+", 1163, 1.5);
			sfxb.FXreadDelay("del4+", 3330, 1.2);
			sfxb.FXreadDelay("del1+", 2420, 1.0);
			sfxb.FXreadDelay("del2+", 2631, 0.8);
			sfxb.writeRegister(dacr, 0.0);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = __param;	
			}
			
			public double getgain() {
				return gain;
			}
			public void setkiap(double __param) {
				kiap = __param;	
			}
			
			public double getkiap() {
				return kiap;
			}
		}	
