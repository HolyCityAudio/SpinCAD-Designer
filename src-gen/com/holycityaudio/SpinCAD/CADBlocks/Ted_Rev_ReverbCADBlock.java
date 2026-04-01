/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Ted_Rev_ReverbCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Ted_Rev_ReverbControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Ted_Rev_ReverbCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Ted_Rev_ReverbControlPanel cp = null;
			
			private double inputGainl = 1.0;
			private double inputGainr = 1.0;
			private double pre = 3000;
			private double decay = 18000;
			private int f1;
			private int f2;
			private int f3;
			private int f4;
			private int input0;
			private int input2;
			private int kfil;
			private int input1;
			private int outputl;
			private int outputr;

			public Ted_Rev_ReverbCADBlock(int x, int y) {
				super(x, y);
				setName("Ted_Rev_Reverb");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Output_Left");
				addOutputPin(this, "Output_Right");
				addControlInputPin(this, "Pre_Delay");
				addControlInputPin(this, "Decay_Time");
				addControlInputPin(this, "Damping");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Ted_Rev_ReverbControlPanel(this);
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
			int inputl = -1;
			if(sp != null) {
				inputl = sp.getRegister();
			}
			sp = this.getPin("Input_Right").getPinConnection();
			int inputr = -1;
			if(sp != null) {
				inputr = sp.getRegister();
			}
			sp = this.getPin("Pre_Delay").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Decay_Time").getPinConnection();
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
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("pdel", pre); 
			sfxb.FXallocDelayMem("rdel", decay); 
			sfxb.FXallocDelayMem("ap1", 1234); 
			sfxb.FXallocDelayMem("ap2", 957); 
			sfxb.FXallocDelayMem("ap3", 765); 
			sfxb.FXallocDelayMem("ap4", 321); 
			f1 = sfxb.allocateReg();
			f2 = sfxb.allocateReg();
			f3 = sfxb.allocateReg();
			f4 = sfxb.allocateReg();
			input0 = sfxb.allocateReg();
			input2 = sfxb.allocateReg();
			kfil = sfxb.allocateReg();
			input1 = sfxb.allocateReg();
			outputl = sfxb.allocateReg();
			outputr = sfxb.allocateReg();
			if(this.getPin("Input_Right").isConnected() == true) {
			sfxb.readRegister(inputr, inputGainr);
			}
			
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.readRegister(inputl, inputGainl);
			}
			
			sfxb.FXwriteDelay("pdel", 0, 0);
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.readRegister(input0, 0.3);
			}
			
			sfxb.readRegisterFilter(input2, 0.001);
			sfxb.writeRegister(input2, 1);
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1);
			sfxb.writeRegister(input0, 0);
			sfxb.FXwriteDelay("rdel", 0, 0);
			if(this.getPin("Decay_Time").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			sfxb.skip(ZRO, 13);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 16);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 18);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 20);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 22);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 24);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 26);
			sfxb.skip(RUN, 29);
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel+", (int)(14000 * 1.0), 0);
			sfxb.skip(ZRO, 27);
			}
			
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel+", (int)(12000 * 1.0), 0);
			sfxb.skip(ZRO, 23);
			}
			
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel+", (int)(10000 * 1.0), 0);
			sfxb.skip(ZRO, 19);
			}
			
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel+", (int)(8000 * 1.0), 0);
			sfxb.skip(ZRO, 15);
			}
			
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel+", (int)(6000 * 1.0), 0);
			sfxb.skip(ZRO, 11);
			}
			
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel+", (int)(4000 * 1.0), 0);
			sfxb.skip(ZRO, 7);
			}
			
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel+", (int)(2000 * 1.0), 0);
			sfxb.skip(ZRO, 3);
			}
			
			if(this.getPin("Pre_Delay").isConnected() == true) {
			sfxb.loadAccumulator(input0);
			sfxb.FXwriteDelay("rdel", 0, 0);
			}
			
			if(this.getPin("Damping").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			}
			
			sfxb.scaleOffset(0.8, 0.1);
			sfxb.writeRegister(kfil, 0);
			sfxb.FXreadDelay("rdel+", (int)(1 * 1.0), 0.05);
			sfxb.FXreadDelay("rdel+", (int)(303 * 1.0), -0.05);
			sfxb.FXreadDelay("rdel+", (int)(569 * 1.0), 0.06);
			sfxb.FXreadDelay("rdel+", (int)(911 * 1.0), 0.07);
			sfxb.FXreadDelay("rdel+", (int)(1256 * 1.0), -0.008);
			sfxb.FXreadDelay("rdel+", (int)(1478 * 1.0), 0.008);
			sfxb.FXreadDelay("rdel+", (int)(1818 * 1.0), -0.01);
			sfxb.FXreadDelay("rdel+", (int)(2089 * 1.0), 0.01);
			sfxb.FXreadDelay("rdel+", (int)(2358 * 1.0), -0.011);
			sfxb.FXreadDelay("rdel+", (int)(2710 * 1.0), 0.012);
			sfxb.FXreadDelay("rdel+", (int)(3018 * 1.0), 0.0135);
			sfxb.FXreadDelay("rdel+", (int)(3345 * 1.0), -0.012);
			sfxb.FXreadDelay("rdel+", (int)(3567 * 1.0), -0.015);
			sfxb.FXreadDelay("rdel+", (int)(3922 * 1.0), 0.02);
			sfxb.FXreadDelay("rdel+", (int)(4167 * 1.0), -0.02);
			sfxb.readRegister(f1, -1);
			sfxb.mulx(kfil);
			sfxb.readRegister(f1, 1);
			sfxb.writeRegister(f1, 1);
			sfxb.FXreadDelay("ap1#", 0, 0.5);
			sfxb.FXwriteAllpass("ap1", 0, -0.5);
			sfxb.FXreadDelay("rdel+", (int)(4522 * 1.0), -0.029);
			sfxb.FXreadDelay("rdel+", (int)(4754 * 1.0), 0.04);
			sfxb.FXreadDelay("rdel+", (int)(5156 * 1.0), -0.034);
			sfxb.FXreadDelay("rdel+", (int)(5342 * 1.0), -0.04);
			sfxb.FXreadDelay("rdel+", (int)(5657 * 1.0), 0.035);
			sfxb.FXreadDelay("rdel+", (int)(6008 * 1.0), 0.04);
			sfxb.FXreadDelay("rdel+", (int)(6283 * 1.0), -0.04);
			sfxb.FXreadDelay("rdel+", (int)(6623 * 1.0), -0.045);
			sfxb.FXreadDelay("rdel+", (int)(6845 * 1.0), -0.055);
			sfxb.FXreadDelay("rdel+", (int)(7219 * 1.0), 0.06);
			sfxb.FXreadDelay("rdel+", (int)(7487 * 1.0), -0.06);
			sfxb.FXreadDelay("rdel+", (int)(7832 * 1.0), -0.05);
			sfxb.FXreadDelay("rdel+", (int)(8065 * 1.0), 0.07);
			sfxb.readRegister(f2, -1);
			sfxb.mulx(kfil);
			sfxb.readRegister(f2, 1);
			sfxb.writeRegister(f2, 1);
			sfxb.FXreadDelay("ap2#", 0, 0.5);
			sfxb.FXwriteAllpass("ap2", 0, -0.5);
			sfxb.FXreadDelay("rdel+", (int)(8404 * 1.0), 0.08);
			sfxb.FXreadDelay("rdel+", (int)(8713 * 1.0), -0.07);
			sfxb.FXreadDelay("rdel+", (int)(8967 * 1.0), -0.08);
			sfxb.FXreadDelay("rdel+", (int)(9307 * 1.0), 0.08);
			sfxb.FXreadDelay("rdel+", (int)(9576 * 1.0), -0.09);
			sfxb.FXreadDelay("rdel+", (int)(9924 * 1.0), -0.09);
			sfxb.FXreadDelay("rdel+", (int)(10298 * 1.0), -0.11);
			sfxb.FXreadDelay("rdel+", (int)(10578 * 1.0), 0.1);
			sfxb.FXreadDelay("rdel+", (int)(10835 * 1.0), 0.12);
			sfxb.FXreadDelay("rdel+", (int)(11207 * 1.0), -0.1);
			sfxb.FXreadDelay("rdel+", (int)(11523 * 1.0), -0.14);
			sfxb.FXreadDelay("rdel+", (int)(11765 * 1.0), -0.18);
			sfxb.FXreadDelay("rdel+", (int)(12113 * 1.0), 0.16);
			sfxb.FXreadDelay("rdel+", (int)(12324 * 1.0), -0.13);
			sfxb.FXreadDelay("rdel+", (int)(12735 * 1.0), -0.17);
			sfxb.readRegister(f3, -1);
			sfxb.mulx(kfil);
			sfxb.readRegister(f3, 1);
			sfxb.writeRegister(f3, 1);
			sfxb.FXreadDelay("ap3#", 0, 0.5);
			sfxb.FXwriteAllpass("ap3", 0, -0.5);
			sfxb.FXreadDelay("rdel+", (int)(13003 * 1.0), 0.19);
			sfxb.FXreadDelay("rdel+", (int)(13267 * 1.0), -0.14);
			sfxb.FXreadDelay("rdel+", (int)(13610 * 1.0), 0.16);
			sfxb.FXreadDelay("rdel+", (int)(13945 * 1.0), -0.18);
			sfxb.FXreadDelay("rdel+", (int)(14130 * 1.0), 0.2);
			sfxb.FXreadDelay("rdel+", (int)(14550 * 1.0), -0.25);
			sfxb.FXreadDelay("rdel+", (int)(14800 * 1.0), -0.25);
			sfxb.readRegister(f4, -1);
			sfxb.mulx(kfil);
			sfxb.readRegister(f4, 1);
			sfxb.writeRegister(f4, 1);
			sfxb.FXreadDelay("ap4#", 0, 0.5);
			sfxb.FXwriteAllpass("ap4", 0, -0.5);
			sfxb.FXreadDelay("rdel+", (int)(16000 * 1.0), 1);
			this.getPin("Output_Left").setRegister(outputl);
			sfxb.writeRegister(outputl, 1);
			this.getPin("Output_Right").setRegister(outputr);
			sfxb.writeRegister(outputr, 0);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setinputGainl(double __param) {
				inputGainl = Math.pow(10.0, __param/20.0);	
			}
			
			public double getinputGainl() {
				return inputGainl;
			}
			public void setinputGainr(double __param) {
				inputGainr = Math.pow(10.0, __param/20.0);	
			}
			
			public double getinputGainr() {
				return inputGainr;
			}
			public void setpre(double __param) {
				pre = __param;	
			}
			
			public double getpre() {
				return pre;
			}
			public void setdecay(double __param) {
				decay = __param;	
			}
			
			public double getdecay() {
				return decay;
			}
		}	
