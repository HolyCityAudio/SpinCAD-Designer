/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_plateCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.reverb_plateControlPanel;
		
	    @SuppressWarnings("unused")
	    public class reverb_plateCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private reverb_plateControlPanel cp = null;
			
			private double gain = 0.5;
			private double krt = 0.5;
			private double kfh = 0.02;
			private double kfl = 0.8;
			private double kapi = 0.5;
			private double kap = 0.5;
			private int temp;
			private int krtreg;
			private int ksh;
			private int ksl;
			private int lap;
			private int output;
			private int hp1;
			private int hp2;
			private int hp3;
			private int hp4;
			private int lp1;
			private int lp2;
			private int lp3;
			private int lp4;
			private int lup;
			private double rate1 = 20;
			private double rate2 = 20;

			public reverb_plateCADBlock(int x, int y) {
				super(x, y);
				setName("Reverb_Plate");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Reverb_Time");
				addControlInputPin(this, "LF_Loss");
				addControlInputPin(this, "HF_Loss");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
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
						cp = new reverb_plateControlPanel(this);
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
			sp = this.getPin("Reverb_Time").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("LF_Loss").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("HF_Loss").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input").isConnected() == true) {
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("api1l", 224); 
			sfxb.FXallocDelayMem("api2l", 430); 
			sfxb.FXallocDelayMem("api3l", 856); 
			sfxb.FXallocDelayMem("api4l", 1089); 
			sfxb.FXallocDelayMem("apd1", 2301); 
			sfxb.FXallocDelayMem("apd2", 2902); 
			sfxb.FXallocDelayMem("apd3", 3171); 
			sfxb.FXallocDelayMem("apd4", 2401); 
			sfxb.FXallocDelayMem("del1", 3620); 
			sfxb.FXallocDelayMem("del2", 4591); 
			sfxb.FXallocDelayMem("del3", 4387); 
			sfxb.FXallocDelayMem("del4", 3679); 
			temp = sfxb.allocateReg();
			krtreg = sfxb.allocateReg();
			ksh = sfxb.allocateReg();
			ksl = sfxb.allocateReg();
			lap = sfxb.allocateReg();
			output = sfxb.allocateReg();
			hp1 = sfxb.allocateReg();
			hp2 = sfxb.allocateReg();
			hp3 = sfxb.allocateReg();
			hp4 = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			lp3 = sfxb.allocateReg();
			lp4 = sfxb.allocateReg();
			lup = sfxb.allocateReg();
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.readRegister(input1, krt);
			sfxb.log(0.5, 0);
			sfxb.exp(1, 0);
			sfxb.scaleOffset(0.6, 0.3);
			} else {
			sfxb.scaleOffset(0, krt);
			}
			
			sfxb.writeRegister(krtreg, 0);
			if(this.getPin("LF_Loss").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			} else {
			sfxb.scaleOffset(0, 0.5);
			}
			
			sfxb.scaleOffset(1, -0.999);
			sfxb.writeRegister(ksh, 0);
			if(this.getPin("HF_Loss").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			} else {
			sfxb.scaleOffset(0, 0.5);
			}
			
			sfxb.scaleOffset(1, -0.999);
			sfxb.writeRegister(ksl, 0);
			sfxb.readRegister(input, gain);
			sfxb.FXreadDelay("api1l#", 0, kapi);
			sfxb.FXwriteAllpass("api1l", 0, -kapi);
			sfxb.FXreadDelay("api2l#", 0, kapi);
			sfxb.FXwriteAllpass("api2l", 0, -kapi);
			sfxb.FXreadDelay("api3l#", 0, kapi);
			sfxb.FXwriteAllpass("api3l", 0, -kapi);
			sfxb.FXreadDelay("api4l#", 0, kapi);
			sfxb.FXwriteAllpass("api4l", 0, -kapi);
			sfxb.writeRegister(lap, 0);
			sfxb.readRegister(lup, 1);
			sfxb.FXreadDelay("apd1#", 0, kap);
			sfxb.FXwriteAllpass("apd1", 0, -kap);
			sfxb.FXwriteDelay("del1", 0, 0);
			sfxb.FXreadDelay("del1#", 0, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(hp1, kfh);
			sfxb.writeRegisterLowshelf(hp1, -1);
			sfxb.mulx(ksh);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lp1, kfl);
			sfxb.writeRegisterHighshelf(lp1, -1);
			sfxb.mulx(ksl);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(krtreg);
			sfxb.readRegister(lap, 1);
			sfxb.FXreadDelay("apd2#", 0, kap);
			sfxb.FXwriteAllpass("apd2", 0, -kap);
			sfxb.FXwriteDelay("del2", 0, 0);
			sfxb.FXreadDelay("del2#", 0, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(hp2, kfh);
			sfxb.writeRegisterLowshelf(hp2, -1);
			sfxb.mulx(ksh);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lp2, kfl);
			sfxb.writeRegisterHighshelf(lp2, -1);
			sfxb.mulx(ksl);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(krtreg);
			sfxb.readRegister(lap, 1);
			sfxb.FXreadDelay("apd4#", 0, kap);
			sfxb.FXwriteAllpass("apd4", 0, -kap);
			sfxb.FXwriteDelay("del4", 0, 0);
			sfxb.FXreadDelay("del4#", 0, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(hp4, kfh);
			sfxb.writeRegisterLowshelf(hp4, -1);
			sfxb.mulx(ksh);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lp4, kfl);
			sfxb.writeRegisterHighshelf(lp4, -1);
			sfxb.mulx(ksl);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(krtreg);
			sfxb.writeRegister(lup, 0);
			sfxb.FXreadDelay("del1+", (int)(201 * 1.0), 0.6);
			sfxb.FXreadDelay("del2+", (int)(1345 * 1.0), 0.5);
			sfxb.FXreadDelay("del3+", (int)(897 * 1.0), 0.6);
			sfxb.FXreadDelay("del4+", (int)(1780 * 1.0), 0.5);
			sfxb.writeRegister(output, 0);
			this.getPin("Output").setRegister(output);
			sfxb.skip(RUN, 2);
			sfxb.loadSinLFO((int) 0,(int) rate1, (int) 37);
			sfxb.loadSinLFO((int) 1,(int) rate2, (int) 33);
			sfxb.FXchorusReadDelay(SIN0, REG|SIN|COMPC, "apd1+", 40);
			sfxb.FXchorusReadDelay(SIN0, SIN, "apd1+", 41);
			sfxb.FXwriteDelay("apd1+", (int)(80 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, COS|COMPC, "apd2+", 40);
			sfxb.FXchorusReadDelay(SIN0, COS, "apd2+", 41);
			sfxb.FXwriteDelay("apd2+", (int)(80 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN1, REG|SIN|COMPC, "apd3+", 40);
			sfxb.FXchorusReadDelay(SIN0, SIN, "apd3+", 41);
			sfxb.FXwriteDelay("apd3+", (int)(80 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN1, COS|COMPC, "apd4+", 40);
			sfxb.FXchorusReadDelay(SIN0, COS, "apd4+", 41);
			sfxb.FXwriteDelay("apd4+", (int)(80 * 1.0), 0);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setkrt(double __param) {
				krt = __param;	
			}
			
			public double getkrt() {
				return krt;
			}
			public void setkfh(double __param) {
				kfh = __param;	
			}
			
			public double getkfh() {
				return kfh;
			}
			public void setkfl(double __param) {
				kfl = __param;	
			}
			
			public double getkfl() {
				return kfl;
			}
			public void setkapi(double __param) {
				kapi = __param;	
			}
			
			public double getkapi() {
				return kapi;
			}
			public void setkap(double __param) {
				kap = __param;	
			}
			
			public double getkap() {
				return kap;
			}
			public void setrate1(double __param) {
				rate1 = __param;	
			}
			
			public double getrate1() {
				return rate1;
			}
			public void setrate2(double __param) {
				rate2 = __param;	
			}
			
			public double getrate2() {
				return rate2;
			}
		}	
