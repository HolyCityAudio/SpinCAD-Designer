/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ga_demo_phaserCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ga_demo_phaserControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ga_demo_phaserCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ga_demo_phaserControlPanel cp = null;
			
			private int mono;
			private int apout;
			private int lp1;
			private int lp2;
			private int revout;
			private int phase;
			private int pout;
			private int p1;
			private int p2;
			private int p3;
			private int p4;
			private int p5;
			private int p6;
			private int p7;
			private int p8;
			private int temp;
			private int temp1;
			private int bypass;
			private int output0;
			private int output1;
			private double kap = 0.6;
			private double krt = 0.55;
			private double krf = 0.5;
			private double krs = -0.6;

			public ga_demo_phaserCADBlock(int x, int y) {
				super(x, y);
				setName("Phaser");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Reverb_Level");
				addControlInputPin(this, "Phase_Rate");
				addControlInputPin(this, "Phase_Width");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ga_demo_phaserControlPanel(this);
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
			sp = this.getPin("Reverb_Level").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Phase_Rate").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Phase_Width").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.FXallocDelayMem("ap1", 334); 
			sfxb.FXallocDelayMem("ap2", 556); 
			sfxb.FXallocDelayMem("ap3", 871); 
			sfxb.FXallocDelayMem("lap1a", 808); 
			sfxb.FXallocDelayMem("lap1b", 1934); 
			sfxb.FXallocDelayMem("d1", 2489); 
			sfxb.FXallocDelayMem("lap2a", 1016); 
			sfxb.FXallocDelayMem("lap2b", 1787); 
			sfxb.FXallocDelayMem("d2", 2287); 
			mono = sfxb.allocateReg();
			apout = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			revout = sfxb.allocateReg();
			phase = sfxb.allocateReg();
			pout = sfxb.allocateReg();
			p1 = sfxb.allocateReg();
			p2 = sfxb.allocateReg();
			p3 = sfxb.allocateReg();
			p4 = sfxb.allocateReg();
			p5 = sfxb.allocateReg();
			p6 = sfxb.allocateReg();
			p7 = sfxb.allocateReg();
			p8 = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			temp1 = sfxb.allocateReg();
			bypass = sfxb.allocateReg();
			output0 = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			sfxb.skip(RUN, 4);
			sfxb.writeRegister(lp1, 0);
			sfxb.writeRegister(lp2, 0);
			sfxb.loadSinLFO((int) SIN0,(int) 12, (int) 100);
			sfxb.loadSinLFO((int) SIN1,(int) 0, (int) 32767);
			sfxb.readRegister(adcl, 0.5);
			sfxb.readRegister(adcr, 0.5);
			sfxb.writeRegister(mono, 0);
			sfxb.readRegister(pout, 0.5);
			sfxb.FXreadDelay("ap1#", 0, kap);
			sfxb.FXwriteAllpass("ap1", 0, -kap);
			sfxb.FXreadDelay("ap2#", 0, kap);
			sfxb.FXwriteAllpass("ap2", 0, -kap);
			sfxb.FXreadDelay("ap3#", 0, kap);
			sfxb.FXwriteAllpass("ap3", 0, -kap);
			sfxb.writeRegister(apout, 0);
			sfxb.FXreadDelay("d2#", 0, krt);
			sfxb.readRegister(apout, 1);
			sfxb.FXreadDelay("lap1a#", 0, kap);
			sfxb.FXwriteAllpass("lap1a", 0, -kap);
			sfxb.FXreadDelay("lap1b#", 0, kap);
			sfxb.FXwriteAllpass("lap1b", 0, -kap);
			sfxb.readRegisterFilter(lp1, krf);
			sfxb.writeRegisterLowshelf(lp1, krs);
			sfxb.FXwriteDelay("d1", 0, 0);
			sfxb.FXreadDelay("d1#", 0, krt);
			sfxb.readRegister(apout, 1);
			sfxb.FXreadDelay("lap2a#", 0, kap);
			sfxb.FXwriteAllpass("lap2a", 0, -kap);
			sfxb.FXreadDelay("lap2b#", 0, kap);
			sfxb.FXwriteAllpass("lap2b", 0, -kap);
			sfxb.readRegisterFilter(lp2, krf);
			sfxb.writeRegisterLowshelf(lp2, krs);
			sfxb.FXwriteDelay("d2", 0, 1.99);
			if(this.getPin("Reverb_Level").isConnected() == true) {
			sfxb.FXreadDelay("d1", 0, 1.99);
			sfxb.mulx(input0);
			sfxb.mulx(input0);
			} else {
			sfxb.FXreadDelay("d1", 0, 0.75);
			}
			
			sfxb.writeRegister(revout, 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap1b+", 100);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap1b+", 101);
			sfxb.FXwriteDelay("lap1b+", (int)(200 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap2b+", 100);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap2b+", 101);
			sfxb.FXwriteDelay("lap2b+", (int)(200 * 1.0), 0);
			if(this.getPin("Phase_Width").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			sfxb.readRegister(bypass, 0.9);
			} else {
			sfxb.scaleOffset(0.0, 0.5);
			}
			
			sfxb.writeRegister(bypass, 0);
			if(this.getPin("Phase_Rate").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			sfxb.mulx(input1);
			sfxb.scaleOffset(0.2, 0.02);
			} else {
			sfxb.scaleOffset(0.0, 0.1);
			}
			
			sfxb.writeRegister(SIN1_RATE, 0);
			sfxb.chorusReadValue(SIN1);
			sfxb.scaleOffset(0.5, 0.5);
			sfxb.log(0.5, 0);
			sfxb.exp(1, 0);
			sfxb.scaleOffset(1, -0.5);
			if(this.getPin("Phase_Width").isConnected() == true) {
			sfxb.scaleOffset(1.999, 0);
			sfxb.mulx(input2);
			} else {
			sfxb.scaleOffset(0.0, 0.75);
			}
			
			sfxb.scaleOffset(0.1, 0.85);
			sfxb.writeRegister(phase, 0);
			sfxb.readRegister(p1, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(phase);
			sfxb.readRegister(mono, 0.015625);
			sfxb.writeRegister(p1, -1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp1, 0);
			sfxb.readRegister(p2, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp1, 1);
			sfxb.writeRegister(p2, -1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp1, 0);
			sfxb.readRegister(p3, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp1, 1);
			sfxb.writeRegister(p3, -1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp1, 0);
			sfxb.readRegister(p4, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp1, 1);
			sfxb.writeRegister(p4, -1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp1, 0);
			sfxb.readRegister(p5, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp1, 1);
			sfxb.writeRegister(p5, -1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp1, 0);
			sfxb.readRegister(p6, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp1, 1);
			sfxb.writeRegister(p6, -1);
			sfxb.mulx(phase);
			sfxb.readRegister(temp, 1);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.mulx(bypass);
			sfxb.readRegister(mono, 1);
			sfxb.writeRegister(pout, 1);
			sfxb.readRegister(revout, 1);
			sfxb.scaleOffset(1, 0.02);
			sfxb.writeRegister(output0, 1);
			sfxb.scaleOffset(1, -0.04);
			sfxb.writeRegister(output1, 0);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
