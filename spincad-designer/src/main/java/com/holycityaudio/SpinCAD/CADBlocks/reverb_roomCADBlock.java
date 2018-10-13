/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_roomCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.reverb_roomControlPanel;
		
	    @SuppressWarnings("unused")
	    public class reverb_roomCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private reverb_roomControlPanel cp = null;
			
			private double gain = 0.5;
			private double krt = 0.5;
			private double hpdf = 0.02;
			private double inputkap = 0.5;
			private double dlkap = 0.5;
			private double rate1 = 20;
			private int pout;
			private int apout;
			private int temp;
			private int outputL;
			private int outputR;
			private int kd;
			private int lp1;
			private int lp2;
			private int lp3;
			private int lp4;
			private int hp1;
			private int hp2;
			private int tlp;

			public reverb_roomCADBlock(int x, int y) {
				super(x, y);
				setName("Reverb_Room");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "OutputL");
				addOutputPin(this, "OutputR");
				addControlInputPin(this, "Pre_Delay");
				addControlInputPin(this, "Reverb_Time");
				addControlInputPin(this, "HF_Loss");
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
						cp = new reverb_roomControlPanel(this);
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
			sp = this.getPin("Pre_Delay").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Reverb_Time").getPinConnection();
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
			sfxb.FXallocDelayMem("pdel", 3276); 
			sfxb.FXallocDelayMem("tdel", 4000); 
			sfxb.FXallocDelayMem("ap1", 473); 
			sfxb.FXallocDelayMem("ap2", 536); 
			sfxb.FXallocDelayMem("ap3", 667); 
			sfxb.FXallocDelayMem("ap4", 791); 
			sfxb.FXallocDelayMem("tap1", 452); 
			sfxb.FXallocDelayMem("tap2", 561); 
			sfxb.FXallocDelayMem("lap1a", 878); 
			sfxb.FXallocDelayMem("lap1b", 1287); 
			sfxb.FXallocDelayMem("d1", 1536); 
			sfxb.FXallocDelayMem("lap2a", 968); 
			sfxb.FXallocDelayMem("lap2b", 1367); 
			sfxb.FXallocDelayMem("d2", 1891); 
			sfxb.FXallocDelayMem("lap3a", 678); 
			sfxb.FXallocDelayMem("lap3b", 1127); 
			sfxb.FXallocDelayMem("d3", 1936); 
			sfxb.FXallocDelayMem("lap4a", 1263); 
			sfxb.FXallocDelayMem("lap4b", 1198); 
			sfxb.FXallocDelayMem("d4", 1781); 
			pout = sfxb.allocateReg();
			apout = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			outputL = sfxb.allocateReg();
			outputR = sfxb.allocateReg();
			kd = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			lp3 = sfxb.allocateReg();
			lp4 = sfxb.allocateReg();
			hp1 = sfxb.allocateReg();
			hp2 = sfxb.allocateReg();
			tlp = sfxb.allocateReg();
			sfxb.skip(RUN, 6);
			sfxb.writeRegister(lp1, 0);
			sfxb.writeRegister(lp2, 0);
			sfxb.writeRegister(hp1, 0);
			sfxb.writeRegister(hp2, 0);
			sfxb.writeRegister(tlp, 0);
			sfxb.loadSinLFO((int) SIN0,(int) rate1, (int) 100);
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.readRegister(input0, 0.1);
			} else {
			sfxb.scaleOffset(0, 0.025);
			}
			
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readRegister(input, gain);
			sfxb.FXwriteDelay("pdel", 0, 0);
			sfxb.readDelayPointer(1);
			sfxb.FXwriteDelay("tdel", 0, 1);
			sfxb.FXreadDelay("ap1#", 0, inputkap);
			sfxb.FXwriteAllpass("ap1", 0, -inputkap);
			sfxb.FXreadDelay("ap2#", 0, inputkap);
			sfxb.FXwriteAllpass("ap2", 0, -inputkap);
			sfxb.FXreadDelay("ap3#", 0, inputkap);
			sfxb.FXwriteAllpass("ap3", 0, -inputkap);
			sfxb.FXreadDelay("ap4#", 0, inputkap);
			sfxb.FXwriteAllpass("ap4", 0, -inputkap);
			sfxb.writeRegister(apout, 0);
			sfxb.FXreadDelay("d4#", 0, krt);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(input1);
			}
			
			sfxb.readRegister(apout, 1);
			sfxb.FXreadDelay("lap1a#", 0, dlkap);
			sfxb.FXwriteAllpass("lap1a", 0, -dlkap);
			sfxb.FXreadDelay("lap1b#", 0, -dlkap);
			sfxb.FXwriteAllpass("lap1b", 0, dlkap);
			sfxb.readRegisterFilter(hp1, hpdf);
			sfxb.writeRegisterHighshelf(hp1, -0.5);
			sfxb.writeRegister(temp, -1);
			sfxb.readRegisterFilter(lp1, 0.5);
			sfxb.writeRegisterHighshelf(lp1, -1);
			if(this.getPin("HF_Loss").isConnected() == true) {
			sfxb.mulx(input2);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("d1", 0, 0);
			sfxb.FXreadDelay("d1#", 0, -krt);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(input1);
			}
			
			sfxb.readRegister(apout, 1);
			sfxb.FXreadDelay("lap2a#", 0, dlkap);
			sfxb.FXwriteAllpass("lap2a", 0, -dlkap);
			sfxb.FXreadDelay("lap2b#", 0, dlkap);
			sfxb.FXwriteAllpass("lap2b", 0, -dlkap);
			sfxb.readRegisterFilter(hp2, hpdf);
			sfxb.writeRegisterHighshelf(hp2, -0.5);
			sfxb.writeRegister(temp, -1);
			sfxb.readRegisterFilter(lp2, 0.5);
			sfxb.writeRegisterHighshelf(lp2, -1);
			if(this.getPin("HF_Loss").isConnected() == true) {
			sfxb.mulx(input2);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("d2", 0, 0);
			sfxb.FXreadDelay("d2#", 0, -1);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(input1);
			}
			
			sfxb.readRegister(apout, 1);
			sfxb.FXreadDelay("lap3a#", 0, dlkap);
			sfxb.FXwriteAllpass("lap3a", 0, -dlkap);
			sfxb.FXreadDelay("lap3b#", 0, dlkap);
			sfxb.FXwriteAllpass("lap3b", 0, -dlkap);
			sfxb.readRegisterFilter(hp2, 0.05);
			sfxb.writeRegisterHighshelf(hp2, -0.5);
			sfxb.writeRegister(temp, -1);
			sfxb.readRegisterFilter(lp3, 0.5);
			sfxb.writeRegisterHighshelf(lp3, -1);
			sfxb.mulx(kd);
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("d3", 0, 0);
			sfxb.FXreadDelay("d3#", 0, -1);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(input1);
			}
			
			sfxb.readRegister(apout, 1);
			sfxb.FXreadDelay("lap4a#", 0, dlkap);
			sfxb.FXwriteAllpass("lap4a", 0, -dlkap);
			sfxb.FXreadDelay("lap4b#", 0, dlkap);
			sfxb.FXwriteAllpass("lap4b", 0, -dlkap);
			sfxb.writeRegister(temp, -1);
			sfxb.readRegisterFilter(lp4, 0.5);
			sfxb.writeRegisterHighshelf(lp4, -1);
			sfxb.mulx(kd);
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("d4", 0, 0);
			sfxb.FXreadDelay("tdel+", (int)(100 * 1.0), 1);
			sfxb.FXreadDelay("tap1#", 0, 0.5);
			sfxb.FXwriteAllpass("tap1", 0, -0.5);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(tlp, 0.1);
			sfxb.writeRegisterHighshelf(tlp, -1);
			if(this.getPin("HF_Loss").isConnected() == true) {
			sfxb.mulx(input2);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("tdel+", (int)(101 * 1.0), 0);
			sfxb.FXreadDelay("tdel+", (int)(1000 * 1.0), 1);
			sfxb.FXreadDelay("tap2#", 0, 0.5);
			sfxb.FXwriteAllpass("tap2", 0, -0.5);
			sfxb.FXwriteDelay("tdel+", (int)(1001 * 1.0), 0);
			sfxb.FXreadDelay("tdel+", (int)(701 * 1.0), 0.7);
			sfxb.FXreadDelay("tdel+", (int)(956 * 1.0), 0.6);
			sfxb.FXreadDelay("tdel+", (int)(409 * 1.0), 0.5);
			sfxb.FXreadDelay("tdel+", (int)(1323 * 1.0), 0.4);
			if(this.getPin("OutputR").isConnected() == true) {
			sfxb.FXreadDelay("d1", 0, 1.5);
			} else {
			sfxb.FXreadDelay("d1#", 0, 0.7);
			sfxb.FXreadDelay("d2#", 0, 0.8);
			}
			
			sfxb.writeRegister(outputL, 0);
			if(this.getPin("OutputR").isConnected() == true) {
			sfxb.FXreadDelay("tdel+", (int)(582 * 1.0), 0.7);
			sfxb.FXreadDelay("tdel+", (int)(956 * 1.0), 0.6);
			sfxb.FXreadDelay("tdel+", (int)(1047 * 1.0), 0.5);
			sfxb.FXreadDelay("tdel+", (int)(1323 * 1.0), 0.4);
			sfxb.FXreadDelay("d3", 0, 1.5);
			sfxb.writeRegister(outputR, 0);
			}
			
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap1b+", 100);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap1b+", 101);
			sfxb.FXwriteDelay("lap1b+", (int)(200 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, COS|REG|COMPC, "lap3b+", 100);
			sfxb.FXchorusReadDelay(SIN0, COS, "lap3b+", 101);
			sfxb.FXwriteDelay("lap3b+", (int)(200 * 1.0), 0);
			this.getPin("OutputL").setRegister(outputL);
			this.getPin("OutputR").setRegister(outputR);
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
			public void sethpdf(double __param) {
				hpdf = __param;	
			}
			
			public double gethpdf() {
				return hpdf;
			}
			public void setinputkap(double __param) {
				inputkap = __param;	
			}
			
			public double getinputkap() {
				return inputkap;
			}
			public void setdlkap(double __param) {
				dlkap = __param;	
			}
			
			public double getdlkap() {
				return dlkap;
			}
			public void setrate1(double __param) {
				rate1 = __param;	
			}
			
			public double getrate1() {
				return rate1;
			}
		}	
