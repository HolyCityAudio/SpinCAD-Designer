/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rom_rev2CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.rom_rev2ControlPanel;
		
	    @SuppressWarnings("unused")
	    public class rom_rev2CADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private rom_rev2ControlPanel cp = null;
			
			private double gain = 0.5;
			private double revTimeMax = 0.6;
			private double kapi = 0.6;
			private double kapd1 = 0.6;
			private double kapd2 = 0.5;
			private double kfl = 0.4;
			private double kfh = 0.01;
			private double memscale = 0.98;
			private double ap1length = 1251;
			private double ap1blength = 1551;
			private double ap2length = 943;
			private double ap2blength = 1343;
			private double ap3length = 1282;
			private double ap3blength = 1381;
			private double ap4length = 1174;
			private double ap4blength = 1382;
			private double delay1length = 2859;
			private double delay2length = 3145;
			private double delay3length = 2476;
			private double delay4length = 3568;
			private double ldellength = 3000;
			private int output;
			private int rt;
			private int hfResp;
			private int lfResp;
			private int ap1Out;
			private int loFilt1;
			private int hiFilt1;
			private int temp;
			private int loFilt2;
			private int hiFilt2;
			private int loFilt3;
			private int hiFilt3;
			private int loFilt4;
			private int hiFilt4;

			public rom_rev2CADBlock(int x, int y) {
				super(x, y);
				setName("ROM Reverb 2");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Reverb Time");
				addControlInputPin(this, "LF Response");
				addControlInputPin(this, "HF Response");
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
						cp = new rom_rev2ControlPanel(this);
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
			sp = this.getPin("Reverb Time").getPinConnection();
			int revTime = -1;
			if(sp != null) {
				revTime = sp.getRegister();
			}
			sp = this.getPin("LF Response").getPinConnection();
			int lfRespInput = -1;
			if(sp != null) {
				lfRespInput = sp.getRegister();
			}
			sp = this.getPin("HF Response").getPinConnection();
			int hfRespInput = -1;
			if(sp != null) {
				hfRespInput = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input").isConnected() == true) {
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("lap1", 156); 
			sfxb.FXallocDelayMem("lap2", 223); 
			sfxb.FXallocDelayMem("lap3", 332); 
			sfxb.FXallocDelayMem("lap4", 548); 
			double ap1scaled = memscale * ap1length;
			sfxb.FXallocDelayMem("ap1", ap1scaled); 
			double ap1bscaled = memscale * ap1blength;
			sfxb.FXallocDelayMem("ap1b", ap1bscaled); 
			double ap2scaled = memscale * ap2length;
			sfxb.FXallocDelayMem("ap2", ap2scaled); 
			double ap2bscaled = memscale * ap2blength;
			sfxb.FXallocDelayMem("ap2b", ap2bscaled); 
			double ap3scaled = memscale * ap3length;
			sfxb.FXallocDelayMem("ap3", ap2scaled); 
			double ap3bscaled = memscale * ap3blength;
			sfxb.FXallocDelayMem("ap3b", ap3bscaled); 
			double ap4scaled = memscale * ap4length;
			sfxb.FXallocDelayMem("ap4", ap4scaled); 
			double ap4bscaled = memscale * ap4blength;
			sfxb.FXallocDelayMem("ap4b", ap4bscaled); 
			double delay1scaled = memscale * delay1length;
			sfxb.FXallocDelayMem("del1", delay1scaled); 
			double delay2scaled = memscale * delay2length;
			sfxb.FXallocDelayMem("del2", delay2scaled); 
			double delay3scaled = memscale * delay3length;
			sfxb.FXallocDelayMem("del3", delay3scaled); 
			double delay4scaled = memscale * delay4length;
			sfxb.FXallocDelayMem("del4", delay3scaled); 
			double ldelscaled = memscale * ldellength;
			sfxb.FXallocDelayMem("ldel", ldelscaled); 
			output = sfxb.allocateReg();
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) 12, (int) 160);
			sfxb.FXchorusReadDelay(0, 6, "ap1+", 50);
			sfxb.FXchorusReadDelay(SIN0, 0, "ap1+", 51);
			sfxb.FXwriteDelay("ap1+", (int)(100 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, 7, "ap3+", 50);
			sfxb.FXchorusReadDelay(SIN0, 01, "ap3+", 51);
			sfxb.FXwriteDelay("ap3+", (int)(100 * 1.0), 0);
			rt = sfxb.allocateReg();
			if(this.getPin("Reverb Time").isConnected() == true) {
			sfxb.readRegister(revTime, revTimeMax);
			sfxb.scaleOffset(0.9, 0.1);
			} else {
			sfxb.scaleOffset(0.0, revTimeMax);
			}
			
			sfxb.writeRegister(rt, 0);
			hfResp = sfxb.allocateReg();
			if(this.getPin("LF Response").isConnected() == true) {
			sfxb.readRegister(lfRespInput, 1.0);
			sfxb.scaleOffset(0.8, -0.8);
			} else {
			sfxb.scaleOffset(0.0, -0.6);
			}
			
			sfxb.writeRegister(hfResp, 0);
			lfResp = sfxb.allocateReg();
			if(this.getPin("HF Response").isConnected() == true) {
			sfxb.readRegister(hfRespInput, 1.0);
			sfxb.scaleOffset(0.8, -0.8);
			} else {
			sfxb.scaleOffset(0.0, -0.6);
			}
			
			sfxb.writeRegister(lfResp, 0);
			ap1Out = sfxb.allocateReg();
			sfxb.readRegister(input, gain);
			sfxb.FXreadDelay("lap1#", 0, -kapi);
			sfxb.FXwriteAllpass("lap1", 0, kapi);
			sfxb.FXreadDelay("lap2#", 0, -kapi);
			sfxb.FXwriteAllpass("lap2", 0, kapi);
			sfxb.FXwriteDelay("ldel", 0, 1.0);
			sfxb.FXreadDelay("lap3#", 0, -kapi);
			sfxb.FXwriteAllpass("lap3", 0, kapi);
			sfxb.FXreadDelay("lap4#", 0, -kapi);
			sfxb.FXwriteAllpass("lap4", 0, kapi);
			sfxb.writeRegister(ap1Out, 0);
			loFilt1 = sfxb.allocateReg();
			hiFilt1 = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			sfxb.FXreadDelay("del4#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.readRegister(ap1Out, 1.0);
			sfxb.FXreadDelay("ap1#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap1", 0, kapd1);
			sfxb.FXreadDelay("ap1b#", 0, -kapd2);
			sfxb.FXwriteAllpass("ap1b", 0, kapd2);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(loFilt1, kfl);
			sfxb.writeRegisterHighshelf(loFilt1, -1.0);
			sfxb.mulx(lfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hiFilt1, kfh);
			sfxb.writeRegisterLowshelf(hiFilt1, -1.0);
			sfxb.mulx(hfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del1", 0, 0.0);
			loFilt2 = sfxb.allocateReg();
			hiFilt2 = sfxb.allocateReg();
			sfxb.FXreadDelay("del1#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.readRegister(ap1Out, 1.0);
			sfxb.FXreadDelay("ap2#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap2", 0, kapd1);
			sfxb.FXreadDelay("ap2b#", 0, -kapd2);
			sfxb.FXwriteAllpass("ap2b", 0, kapd2);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(loFilt2, kfl);
			sfxb.writeRegisterHighshelf(loFilt2, -1.0);
			sfxb.mulx(lfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hiFilt2, kfh);
			sfxb.writeRegisterLowshelf(hiFilt2, -1.0);
			sfxb.mulx(hfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del2", 0, 0.0);
			loFilt3 = sfxb.allocateReg();
			hiFilt3 = sfxb.allocateReg();
			sfxb.FXreadDelay("del2#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.readRegister(ap1Out, 1.0);
			sfxb.FXreadDelay("ap3#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap3", 0, kapd1);
			sfxb.FXreadDelay("ap3b#", 0, -kapd2);
			sfxb.FXwriteAllpass("ap3b", 0, kapd2);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(loFilt3, kfl);
			sfxb.writeRegisterHighshelf(loFilt3, -1.0);
			sfxb.mulx(lfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hiFilt3, kfh);
			sfxb.writeRegisterLowshelf(hiFilt3, -1.0);
			sfxb.mulx(hfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del3", 0, 0.0);
			loFilt4 = sfxb.allocateReg();
			hiFilt4 = sfxb.allocateReg();
			sfxb.FXreadDelay("del3#", 0, 1.0);
			sfxb.mulx(rt);
			sfxb.readRegister(ap1Out, 1.0);
			sfxb.FXreadDelay("ap4#", 0, -kapd1);
			sfxb.FXwriteAllpass("ap4", 0, kapd1);
			sfxb.FXreadDelay("ap4b#", 0, -kapd2);
			sfxb.FXwriteAllpass("ap4b", 0, kapd2);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(loFilt4, kfl);
			sfxb.writeRegisterHighshelf(loFilt4, -1.0);
			sfxb.mulx(lfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.writeRegister(temp, 1.0);
			sfxb.readRegisterFilter(hiFilt4, kfh);
			sfxb.writeRegisterLowshelf(hiFilt4, -1.0);
			sfxb.mulx(hfResp);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("del4", 0, 0.0);
			sfxb.FXreadDelay("del1+", (int)(2630 * memscale), 0.8);
			sfxb.FXreadDelay("del2+", (int)(1943 * memscale), 0.7);
			sfxb.FXreadDelay("del3+", (int)(1200 * memscale), 0.6);
			sfxb.FXreadDelay("del4+", (int)(2106 * memscale), 0.5);
			sfxb.FXreadDelay("ldel+", (int)(180 * memscale), 0.8);
			sfxb.FXreadDelay("ldel+", (int)(1194 * memscale), 0.7);
			sfxb.FXreadDelay("ldel+", (int)(2567 * memscale), 0.6);
			sfxb.FXreadDelay("ldel+", (int)(2945 * memscale), 0.5);
			sfxb.writeRegister(output, 0.0);
			this.getPin("Output").setRegister(output);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setrevTimeMax(double __param) {
				revTimeMax = __param;	
			}
			
			public double getrevTimeMax() {
				return revTimeMax;
			}
			public void setkapi(double __param) {
				kapi = __param;	
			}
			
			public double getkapi() {
				return kapi;
			}
			public void setkapd1(double __param) {
				kapd1 = __param;	
			}
			
			public double getkapd1() {
				return kapd1;
			}
			public void setkapd2(double __param) {
				kapd2 = __param;	
			}
			
			public double getkapd2() {
				return kapd2;
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
			public void setmemscale(double __param) {
				memscale = __param;	
			}
			
			public double getmemscale() {
				return memscale;
			}
		}	
