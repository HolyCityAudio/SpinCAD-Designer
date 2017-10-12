/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverbCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.reverbControlPanel;
		
	    @SuppressWarnings("unused")
	    public class reverbCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private reverbControlPanel cp = null;
			
			boolean LFOAIs0 = false;
			boolean LFOBIs0 = false;
			boolean LFOAIs1 = false;
			boolean LFOBIs1 = false;
			private double gain = 0.5;
			private int hpf4;
			private int lpf4;
			private int temp;
			private int rt;
			private int iapout;
			private int outputL;
			private double nAPs = 4;
			private double kiap = 0.5;
			private double nDLs = 3;
			private double klap = 0.6;
			private double kfl = 0.4;
			private double kfh = 0.01;
			private double lfoSelA = 0;
			private double lfoSelB = 0;
			private double rate1 = 20;
			private double rate2 = 20;
			private int hpf1;
			private int lpf1;
			private int hpf2;
			private int lpf2;
			private int hpf3;
			private int lpf3;
			private int outputR;

			public reverbCADBlock(int x, int y) {
				super(x, y);
				setName("Reverb");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output_Left");
				addOutputPin(this, "Output_Right");
				addControlInputPin(this, "Reverb_Time");
				addControlInputPin(this, "Filter");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
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
						cp = new reverbControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly
			//		LFOAIs0 = false;
			//		LFOBIs0 = false;
			//		LFOAIs1 = false;
			//		LFOBIs1 = false;

			
			sfxb.comment(getName());
			
			SpinCADPin sp = null;
					
			// Iterate through pin definitions and connect or assign as needed
			sp = this.getPin("Input").getPinConnection();
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			sp = this.getPin("Reverb_Time").getPinConnection();
			int revTime = -1;
			if(sp != null) {
				revTime = sp.getRegister();
			}
			sp = this.getPin("Filter").getPinConnection();
			int filter = -1;
			if(sp != null) {
				filter = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("ap4", 1274); 
			sfxb.FXallocDelayMem("ap4b", 1382); 
			sfxb.FXallocDelayMem("del4", 4445); 
			hpf4 = sfxb.allocateReg();
			lpf4 = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			rt = sfxb.allocateReg();
			iapout = sfxb.allocateReg();
			outputL = sfxb.allocateReg();
			if(nAPs > 3) {
			sfxb.FXallocDelayMem("iap1", 156); 
			}
			
			if(nAPs > 2) {
			sfxb.FXallocDelayMem("iap2", 223); 
			}
			
			if(nAPs > 1) {
			sfxb.FXallocDelayMem("iap3", 332); 
			}
			
			sfxb.FXallocDelayMem("iap4", 448); 
			if(nDLs > 3) {
			sfxb.FXallocDelayMem("del1", 3559); 
			sfxb.FXallocDelayMem("ap1", 1251); 
			sfxb.FXallocDelayMem("ap1b", 1751); 
			hpf1 = sfxb.allocateReg();
			lpf1 = sfxb.allocateReg();
			}
			
			if(nDLs > 2) {
			sfxb.FXallocDelayMem("del2", 2945); 
			sfxb.FXallocDelayMem("ap2", 1443); 
			sfxb.FXallocDelayMem("ap2b", 1343); 
			hpf2 = sfxb.allocateReg();
			lpf2 = sfxb.allocateReg();
			}
			
			if(nDLs > 1) {
			sfxb.FXallocDelayMem("del3", 3976); 
			sfxb.FXallocDelayMem("ap3", 1582); 
			sfxb.FXallocDelayMem("ap3b", 1981); 
			hpf3 = sfxb.allocateReg();
			lpf3 = sfxb.allocateReg();
			}
			
			if(this.getPin("Input").isConnected() == true) {
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.readRegister(revTime, 1);
			sfxb.scaleOffset(0.55, 0.3);
			} else {
			sfxb.scaleOffset(0, 0.85);
			}
			
			sfxb.writeRegister(rt, 0);
			sfxb.readRegister(input, gain);
			if(nAPs > 3) {
			sfxb.FXreadDelay("iap1#", 0, kiap);
			sfxb.FXwriteAllpass("iap1", 0, -kiap);
			}
			
			if(nAPs > 2) {
			sfxb.FXreadDelay("iap2#", 0, kiap);
			sfxb.FXwriteAllpass("iap2", 0, -kiap);
			}
			
			if(nAPs > 1) {
			sfxb.FXreadDelay("iap3#", 0, kiap);
			sfxb.FXwriteAllpass("iap3", 0, -kiap);
			}
			
			sfxb.FXreadDelay("iap4#", 0, kiap);
			sfxb.FXwriteAllpass("iap4", 0, -kiap);
			sfxb.writeRegister(iapout, 0);
			sfxb.FXreadDelay("del4#", 0, 1);
			if(nDLs > 3) {
			sfxb.mulx(rt);
			sfxb.readRegister(iapout, 1);
			sfxb.FXreadDelay("ap1#", 0, klap);
			sfxb.FXwriteAllpass("ap1", 0, -klap);
			sfxb.FXreadDelay("ap1b#", 0, klap);
			sfxb.FXwriteAllpass("ap1b", 0, -klap);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lpf1, kfl);
			sfxb.writeRegisterLowshelf(lpf1, -1);
			sfxb.readRegisterFilter(hpf1, kfh);
			sfxb.writeRegisterHighshelf(hpf1, -1);
			sfxb.readRegister(temp, -1);
			if(this.getPin("Filter").isConnected() == true) {
			sfxb.mulx(filter);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del1", 0, 0);
			sfxb.FXreadDelay("del1#", 0, 1);
			}
			
			if(nDLs > 2) {
			sfxb.mulx(rt);
			sfxb.readRegister(iapout, 1);
			sfxb.FXreadDelay("ap2#", 0, klap);
			sfxb.FXwriteAllpass("ap2", 0, -klap);
			sfxb.FXreadDelay("ap2b#", 0, klap);
			sfxb.FXwriteAllpass("ap2b", 0, -klap);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lpf2, kfl);
			sfxb.writeRegisterLowshelf(lpf2, -1);
			sfxb.readRegisterFilter(hpf2, kfh);
			sfxb.writeRegisterHighshelf(hpf2, -1);
			sfxb.readRegister(temp, -1);
			if(this.getPin("Filter").isConnected() == true) {
			sfxb.mulx(filter);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del2", 0, 0);
			sfxb.FXreadDelay("del2#", 0, 1);
			}
			
			if(nDLs > 1) {
			sfxb.mulx(rt);
			sfxb.readRegister(iapout, 1);
			sfxb.FXreadDelay("ap3#", 0, klap);
			sfxb.FXwriteAllpass("ap3", 0, -klap);
			sfxb.FXreadDelay("ap3b#", 0, klap);
			sfxb.FXwriteAllpass("ap3b", 0, -klap);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lpf3, kfl);
			sfxb.writeRegisterLowshelf(lpf3, -1);
			sfxb.readRegisterFilter(hpf3, kfh);
			sfxb.writeRegisterHighshelf(hpf3, -1);
			sfxb.readRegister(temp, -1);
			if(this.getPin("Filter").isConnected() == true) {
			sfxb.mulx(filter);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del3", 0, 0);
			sfxb.FXreadDelay("del3#", 0, 1.0);
			}
			
			sfxb.mulx(rt);
			sfxb.readRegister(iapout, 1);
			sfxb.FXreadDelay("ap4#", 0, klap);
			sfxb.FXwriteAllpass("ap4", 0, -klap);
			sfxb.FXreadDelay("ap4b#", 0, klap);
			sfxb.FXwriteAllpass("ap4b", 0, -klap);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lpf4, kfl);
			sfxb.writeRegisterLowshelf(lpf4, -1);
			sfxb.readRegisterFilter(hpf4, kfh);
			sfxb.writeRegisterHighshelf(hpf4, -1);
			sfxb.readRegister(temp, -1);
			if(this.getPin("Filter").isConnected() == true) {
			sfxb.mulx(filter);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del4", 0, 0);
			if(nDLs > 3) {
			sfxb.FXreadDelay("del1", 0, 0.8);
			} else {
			sfxb.FXreadDelay("del4", 0, 0.8);
			}
			
			if(nDLs > 2) {
			sfxb.FXreadDelay("del2+", (int)(1876 * 1.0), 1.5);
			} else {
			sfxb.FXreadDelay("del4+", (int)(1876 * 1.0), 1.5);
			}
			
			if(nDLs > 1) {
			sfxb.FXreadDelay("del3+", (int)(2093 * 1.0), 1.1);
			} else {
			sfxb.FXreadDelay("del4+", (int)(2093 * 1.0), 1.1);
			}
			
			sfxb.FXreadDelay("del4+", (int)(2793 * 1.0), 1);
			sfxb.writeRegister(outputL, 0);
			this.getPin("Output_Left").setRegister(outputL);
			if(this.getPin("Output_Right").isConnected() == true) {
			outputR = sfxb.allocateReg();
			if(nDLs > 3) {
			sfxb.FXreadDelay("del1", 0, 0.8);
			} else {
			sfxb.FXreadDelay("del4", 0, 0.8);
			}
			
			if(nDLs > 2) {
			sfxb.FXreadDelay("del2+", (int)(923 * 1.0), 1.5);
			} else {
			sfxb.FXreadDelay("del4+", (int)(923 * 1.0), 1.5);
			}
			
			if(nDLs > 1) {
			sfxb.FXreadDelay("del3+", (int)(1234 * 1.0), 1.1);
			} else {
			sfxb.FXreadDelay("del4+", (int)(1234 * 1.0), 1.1);
			}
			
			sfxb.FXreadDelay("del4+", (int)(2267 * 1.0), 1);
			sfxb.writeRegister(outputR, 0);
			this.getPin("Output_Right").setRegister(outputR);
			}
			
			if(lfoSelA == 0) {
			LFOAIs0 = true;
			LFOAIs1 = false;
			} else {
			LFOAIs0 = false;
			LFOAIs1 = true;
			}
			
			if(lfoSelB == 0) {
			LFOBIs0 = true;
			LFOBIs1 = false;
			} else {
			LFOBIs0 = false;
			LFOBIs1 = true;
			}
			
			if(LFOAIs0 || LFOBIs0 == true) {
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) rate1, (int) 50);
			}
			
			if(LFOAIs1 || LFOBIs1 == true) {
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN1,(int) rate2, (int) 50);
			}
			
			if(nDLs > 3) {
			if(lfoSelA == 0) {
			sfxb.FXchorusReadDelay(SIN0, REG|COMPC, "ap1+", 50);
			sfxb.FXchorusReadDelay(SIN0, 0, "ap1+", 51);
			} else {
			sfxb.FXchorusReadDelay(SIN1, REG|COMPC, "ap1+", 50);
			sfxb.FXchorusReadDelay(SIN1, 0, "ap1+", 51);
			}
			
			sfxb.FXwriteDelay("ap1+", (int)(100 * 1.0), 0);
			}
			
			if(nDLs > 2) {
			if(lfoSelA == 0) {
			sfxb.FXchorusReadDelay(SIN0, COS|COMPC, "ap2+", 50);
			sfxb.FXchorusReadDelay(SIN0, COS, "ap2+", 51);
			} else {
			sfxb.FXchorusReadDelay(SIN1, COS|COMPC, "ap2+", 50);
			sfxb.FXchorusReadDelay(SIN1, COS, "ap2+", 51);
			}
			
			sfxb.FXwriteDelay("ap2+", (int)(100 * 1.0), 0);
			}
			
			if(nDLs > 1) {
			if(lfoSelB == 0) {
			sfxb.FXchorusReadDelay(SIN0, REG|COMPC, "ap3+", 50);
			sfxb.FXchorusReadDelay(SIN0, 0, "ap3+", 51);
			} else {
			sfxb.FXchorusReadDelay(SIN1, REG|COMPC, "ap3+", 50);
			sfxb.FXchorusReadDelay(SIN1, 0, "ap3+", 51);
			}
			
			sfxb.FXwriteDelay("ap3+", (int)(100 * 1.0), 0);
			}
			
			if(lfoSelB == 0) {
			sfxb.FXchorusReadDelay(SIN0, COS|COMPC, "ap4+", 50);
			sfxb.FXchorusReadDelay(SIN0, COS, "ap4+", 51);
			} else {
			sfxb.FXchorusReadDelay(SIN1, COS|COMPC, "ap4+", 50);
			sfxb.FXchorusReadDelay(SIN1, COS, "ap4+", 51);
			}
			
			sfxb.FXwriteDelay("ap4+", (int)(100 * 1.0), 0);
			}
			

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
			public void setklap(double __param) {
				klap = __param;	
			}
			
			public double getklap() {
				return klap;
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
			public void setlfoSelA(int __param) {
				lfoSelA = (double) __param;	
			}
			
			public int getlfoSelA() {
				return (int) lfoSelA;
			}
			public void setlfoSelB(int __param) {
				lfoSelB = (double) __param;	
			}
			
			public int getlfoSelB() {
				return (int) lfoSelB;
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
