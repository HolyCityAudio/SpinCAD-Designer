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

		import java.awt.Color;
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
 		import com.holycityaudio.SpinCAD.ControlPanel.rom_rev1ControlPanel;
		
	    @SuppressWarnings("unused")
	    public class rom_rev1CADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private rom_rev1ControlPanel cp = null;
			
			private double gain = 0.5;
			private double kiap = 0.5;
			private double nDLs = 3;
			private double kapd1 = 0.6;
			private double kapd2 = 0.5;
			private double kfl = 0.4;
			private double kfh = 0.01;
			private int lpf1;
			private int hpf1;
			private int lpf2;
			private int hpf2;
			private int lpf3;
			private int hpf3;
			private int lpf4;
			private int hpf4;
			private int temp;
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
						hasControlPanel = true;
						hasControlPanel = true;
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
			if(nDLs > 3) {
			sfxb.FXallocDelayMem("del1", 5859); 
			lpf1 = sfxb.allocateReg();
			hpf1 = sfxb.allocateReg();
			}
			
			if(nDLs > 2) {
			lpf2 = sfxb.allocateReg();
			hpf2 = sfxb.allocateReg();
			sfxb.FXallocDelayMem("del2", 4145); 
			}
			
			if(nDLs > 1) {
			sfxb.FXallocDelayMem("del3", 3476); 
			lpf3 = sfxb.allocateReg();
			hpf3 = sfxb.allocateReg();
			}
			
			sfxb.FXallocDelayMem("del4", 4568); 
			lpf4 = sfxb.allocateReg();
			hpf4 = sfxb.allocateReg();
			temp = sfxb.allocateReg();
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
			sfxb.FXwriteDelay("ap1+", (int)(100 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, 7, "ap3+", 50);
			sfxb.FXchorusReadDelay(SIN0, 1, "ap3+", 51);
			sfxb.FXwriteDelay("ap3+", (int)(100 * 1.0), 0);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.readRegister(input0, 1.0);
			sfxb.scaleOffset(0.8, 0.1);
			} else {
			sfxb.scaleOffset(0.0, 0.76);
			}
			
			sfxb.writeRegister(rt, 0);
			sfxb.readRegister(input1, 1.0);
			sfxb.scaleOffset(0.8, -0.8);
			sfxb.writeRegister(hf, 0);
			sfxb.readRegister(input2, 1.0);
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
			if(nDLs > 3) {
			sfxb.mulx(rt);
			sfxb.readRegister(lapout, 1.0);
			sfxb.FXreadDelay("ap1#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap1", 0, kapd1);
			sfxb.FXreadDelay("ap1b#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap1b", 0, kapd1);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf1, kfl);
			sfxb.writeRegisterHighshelf(lpf1, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf1, kfh);
			sfxb.writeRegisterLowshelf(hpf1, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del1", 0, 0.0);
			sfxb.FXreadDelay("del1#", 0, 1.0);
			}
			
			if(nDLs > 2) {
			sfxb.mulx(rt);
			sfxb.FXreadDelay("ap2#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap2", 0, kapd1);
			sfxb.FXreadDelay("ap2b#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap2b", 0, kapd1);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf2, kfl);
			sfxb.writeRegisterHighshelf(lpf2, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf2, kfh);
			sfxb.writeRegisterLowshelf(hpf2, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del2", 0, 0.0);
			sfxb.FXreadDelay("del2#", 0, 1.0);
			}
			
			if(nDLs > 1) {
			sfxb.mulx(rt);
			sfxb.readRegister(lapout, 1.0);
			sfxb.FXreadDelay("ap3#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap3", 0, kapd1);
			sfxb.FXreadDelay("ap3b#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap3b", 0, kapd1);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf3, kfl);
			sfxb.writeRegisterHighshelf(lpf3, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf3, kfh);
			sfxb.writeRegisterLowshelf(hpf3, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del3", 0, 0.0);
			sfxb.FXreadDelay("del3#", 0, 1.0);
			}
			
			sfxb.mulx(rt);
			sfxb.FXreadDelay("ap4#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap4", 0, kapd1);
			sfxb.FXreadDelay("ap4b#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap4b", 0, kapd1);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(lpf4, kfl);
			sfxb.writeRegisterHighshelf(lpf4, -1.0);
			sfxb.mulx(hf);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hpf4, kfh);
			sfxb.writeRegisterLowshelf(hpf4, -1.0);
			sfxb.mulx(lf);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del4", 0, 0.0);
			if(nDLs > 3) {
			sfxb.FXreadDelay("del1+", (int)(2630 * 1.0), 1.5);
			}
			
			if(nDLs > 2) {
			sfxb.FXreadDelay("del2+", (int)(1943 * 1.0), 1.2);
			}
			
			if(nDLs > 1) {
			sfxb.FXreadDelay("del3+", (int)(3200 * 1.0), 1.0);
			}
			
			sfxb.FXreadDelay("del4+", (int)(4016 * 1.0), 0.8);
			sfxb.writeRegister(dacl, 0.0);
			}
			
			if(this.getPin("Output_Right").isConnected() == true) {
			if(nDLs > 3) {
			sfxb.FXreadDelay("del1+", (int)(2420 * 1.0), 1.0);
			}
			
			if(nDLs > 2) {
			sfxb.FXreadDelay("del2+", (int)(2631 * 1.0), 0.8);
			}
			
			if(nDLs > 1) {
			sfxb.FXreadDelay("del3+", (int)(1163 * 1.0), 1.5);
			}
			
			sfxb.FXreadDelay("del4+", (int)(3330 * 1.0), 1.2);
			sfxb.writeRegister(dacr, 0.0);
			}
			
			this.getPin("Output_Left").setRegister(dacl);
			this.getPin("Output_Right").setRegister(dacr);

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
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
			public void setnDLs(double __param) {
				nDLs = __param;	
			}
			
			public double getnDLs() {
				return nDLs;
			}
			public void setkapd1(double __param) {
				kapd1 = __param;	
			}
			
			public double getkapd1() {
				return kapd1;
			}
			public void setkfl(double __param) {
				kfl = __param;	
			}
			
			public double getkfl() {
				return kfl;
			}
			public void setkfh(double __param) {
				kfh = __param;	
			}
			
			public double getkfh() {
				return kfh;
			}
		}	
