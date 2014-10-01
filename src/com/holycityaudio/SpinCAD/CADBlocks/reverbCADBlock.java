/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverbCADBlock.java
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
 		
		public class reverbCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private reverbControlPanel cp = null;
			
			private int temp;
			private int hpf1;
			private int hpf2;
			private int hpf3;
			private int hpf4;
			private int lpf1;
			private int lpf2;
			private int lpf3;
			private int lpf4;
			private int nAPs = 2;	
			private int nDLs = 1;
			private int rt;
			private int iapout;
			private int pdelo;
			private int output;
			private double kfh = 0.01;
			private double kfl = 0.4;
			private double kiap = 0.5;
			private double klap = 0.6;

			public reverbCADBlock(int x, int y) {
				super(x, y);
				setName("Reverb");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Pot0");
				addControlInputPin(this, "Pot1");
				addControlInputPin(this, "Pot2");
			// if any control panel elements declared, set hasControlPanel to true
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

			sfxb.comment(getName());
			
			SpinCADPin sp = null;
					
			// Iterate through pin definitions and connect or assign as needed
			sp = this.getPin("Input").getPinConnection();
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			
			sp = this.getPin("Pot0").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			
			sp = this.getPin("Pot1").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			
			sp = this.getPin("Pot2").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("pdel", 4600); 
			sfxb.FXallocDelayMem("iap1", 156); 
			sfxb.FXallocDelayMem("iap2", 223); 
			sfxb.FXallocDelayMem("iap3", 332); 
			sfxb.FXallocDelayMem("iap4", 448); 
			sfxb.FXallocDelayMem("ap1", 1251); 
			sfxb.FXallocDelayMem("ap1b", 1751); 
			sfxb.FXallocDelayMem("ap2", 1443); 
			sfxb.FXallocDelayMem("ap2b", 1343); 
			sfxb.FXallocDelayMem("ap3", 1582); 
			sfxb.FXallocDelayMem("ap3b", 1981); 
			sfxb.FXallocDelayMem("ap4", 1274); 
			sfxb.FXallocDelayMem("ap4b", 1382); 
			sfxb.FXallocDelayMem("del1", 3559); 
			sfxb.FXallocDelayMem("del2", 2945); 
			sfxb.FXallocDelayMem("del3", 3976); 
			sfxb.FXallocDelayMem("del4", 4445); 
			temp = sfxb.allocateReg();
			hpf1 = sfxb.allocateReg();
			hpf2 = sfxb.allocateReg();
			hpf3 = sfxb.allocateReg();
			hpf4 = sfxb.allocateReg();
			lpf1 = sfxb.allocateReg();
			lpf2 = sfxb.allocateReg();
			lpf3 = sfxb.allocateReg();
			lpf4 = sfxb.allocateReg();
			rt = sfxb.allocateReg();
			iapout = sfxb.allocateReg();
			pdelo = sfxb.allocateReg();
			output = sfxb.allocateReg();
			sfxb.readRegister(input0, 1);
			sfxb.scaleOffset(0.55, 0.3);
			sfxb.writeRegister(rt, 0);
			sfxb.skip(RUN, 1);
			sfxb.loadRampLFO(0, 0, 4096);
			sfxb.readRegister(input, 1.0);
			sfxb.FXwriteDelay("pdel", 0, 0);
			sfxb.FXchorusReadDelay(RMP0, REG|COMPC, "pdel", 0);
			sfxb.FXchorusReadDelay(RMP0, 0, "pdel+", 1);
			sfxb.writeRegister(pdelo, 0);
			sfxb.chorusReadValue(RMP0);
			sfxb.readRegister(input1, -0.5);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.readRegister(pdelo, 0.25);
			sfxb.FXreadDelay("iap1#", 0, kiap);
			sfxb.FXwriteAllpass("iap1", 0, -kiap);
			sfxb.FXreadDelay("iap2#", 0, kiap);
			sfxb.FXwriteAllpass("iap2", 0, -kiap);
			sfxb.FXreadDelay("iap3#", 0, kiap);
			sfxb.FXwriteAllpass("iap3", 0, -kiap);
			sfxb.FXreadDelay("iap4#", 0, kiap);
			sfxb.FXwriteAllpass("iap4", 0, -kiap);
			sfxb.writeRegister(iapout, 0);
			sfxb.FXreadDelay("del4#", 0, 1);
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
			sfxb.mulx(input2);
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del1", 0, 0);
			sfxb.FXreadDelay("del1#", 0, 1);
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
			sfxb.mulx(input2);
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del2", 0, 0);
			sfxb.FXreadDelay("del2#", 0, 1);
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
			sfxb.mulx(input2);
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del3", 0, 0);
			sfxb.FXreadDelay("del3#", 0, 1.0);
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
			sfxb.mulx(input2);
			sfxb.readRegister(temp, 1);
			sfxb.FXwriteDelay("del4", 0, 0);
			sfxb.FXreadDelay("del1", 0, 0.8);
			sfxb.FXreadDelay("del2+", 1876, 1.5);
			sfxb.FXreadDelay("del3+", 2093, 1.1);
			sfxb.FXreadDelay("del4+", 2793, 1);
			sfxb.writeRegister(output, 0);
			sfxb.FXreadDelay("del1", 0, 0.8);
			sfxb.FXreadDelay("del2+", 923, 1.5);
			sfxb.FXreadDelay("del3+", 1234, 1.1);
			sfxb.FXreadDelay("del4+", 2267, 1);
			sfxb.writeRegister(output, 0);
			sfxb.skip(RUN, 2);
			sfxb.loadSinLFO(SIN0, 45, 50);
			sfxb.loadSinLFO(SIN1, 53, 50);
			sfxb.FXchorusReadDelay(SIN0, REG|COMPC, "ap1+", 50);
			sfxb.FXchorusReadDelay(SIN0, 0, "ap1+", 51);
			sfxb.FXwriteDelay("ap1+", 100, 0);
			sfxb.FXchorusReadDelay(SIN0, COS|COMPC, "ap2+", 50);
			sfxb.FXchorusReadDelay(SIN0, COS, "ap2+", 51);
			sfxb.FXwriteDelay("ap2+", 100, 0);
			sfxb.FXchorusReadDelay(SIN1, REG|COMPC, "ap3+", 50);
			sfxb.FXchorusReadDelay(SIN1, 0, "ap3+", 51);
			sfxb.FXwriteDelay("ap3+", 100, 0);
			sfxb.FXchorusReadDelay(SIN1, COS|COMPC, "ap4+", 50);
			sfxb.FXchorusReadDelay(SIN1, COS, "ap4+", 51);
			sfxb.FXwriteDelay("ap4+", 100, 0);

			}
			
			// create setters and getter for control panel variables
			public void setkiap(double __param) {
				kiap = __param;	
			}
			
			public double getkiap() {
				return kiap;
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

			public int getnAPs() {
				return nAPs;
			}

			public int getnDLs() {
				return nDLs;
			}
			
			public void setnAPs(int val) {
				nAPs = val;
			}

			public void setnDLs(int val) {
				nDLs = val;
			}
		}	
