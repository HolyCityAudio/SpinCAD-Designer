/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ga_demo_flangerCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ga_demo_flangerControlPanel;
		
		public class ga_demo_flangerCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private ga_demo_flangerControlPanel cp = null;
			
			private int mono;
			private int apout;
			private int lp1;
			private int lp2;
			private int revout;
			private int flaout;
			private int fbk;
			private int flamix;
			private int tri;
			private int fhp;
			private int dacl;
			private int dacr;
			private double kap = 0.6;
			private double krt = 0.55;
			private double krf = 0.5;
			private double krs = -0.6;
			private double fbkmax = 0.9;

			public ga_demo_flangerCADBlock(int x, int y) {
				super(x, y);
				setName("Flanger");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Reverb_Level");
				addControlInputPin(this, "Flange_Rate");
				addControlInputPin(this, "Effect_Level_Feedback");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ga_demo_flangerControlPanel(this);
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
			
			sp = this.getPin("Flange_Rate").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			
			sp = this.getPin("Effect_Level_Feedback").getPinConnection();
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
			sfxb.FXallocDelayMem("fladel", 1000); 
			mono = sfxb.allocateReg();
			apout = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			revout = sfxb.allocateReg();
			flaout = sfxb.allocateReg();
			fbk = sfxb.allocateReg();
			flamix = sfxb.allocateReg();
			tri = sfxb.allocateReg();
			fhp = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			sfxb.skip(RUN, 6);
			sfxb.writeRegister(lp1, 0);
			sfxb.writeRegister(lp2, 0);
			sfxb.writeRegister(fhp, 0);
			sfxb.loadSinLFO(SIN0, 12, 100);
			sfxb.loadRampLFO(0, 0, 4096);
			sfxb.loadRampLFO(0, 0, 512);
			sfxb.readRegister(adcl, 0.5);
			sfxb.readRegister(adcr, 0.5);
			sfxb.writeRegister(mono, 0.5);
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
			sfxb.FXwriteDelay("lap1b+", 200, 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap2b+", 100);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap2b+", 101);
			sfxb.FXwriteDelay("lap2b+", 200, 0);
			sfxb.readRegister(flaout, fbkmax);
			sfxb.mulx(fbk);
			sfxb.readRegister(mono, 1);
			sfxb.FXwriteDelay("fladel", 0, 0);
			if(this.getPin("Effect_Level_Feedback").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			sfxb.scaleOffset(1.99, 0);
			} else {
			sfxb.scaleOffset(0.0, 0.5);
			}
			
			sfxb.writeRegister(flamix, 0);
			if(this.getPin("Effect_Level_Feedback").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			sfxb.scaleOffset(1, -1);
			sfxb.scaleOffset(1.999, 0);
			sfxb.scaleOffset(1, 0.999);
			} else {
			sfxb.clear();
			}
			
			sfxb.writeRegister(fbk, 0);
			if(this.getPin("Flange_Rate").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			sfxb.mulx(input1);
			sfxb.mulx(input1);
			sfxb.scaleOffset(0.4, 0.01);
			} else {
			sfxb.scaleOffset(0.0, 0.2);
			}
			
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.chorusReadValue(RMP0);
			sfxb.scaleOffset(1, -0.25);
			sfxb.absa();
			sfxb.writeRegister(tri, 0);
			sfxb.chorusReadValue(RMP1);
			sfxb.readRegister(tri, -0.06);
			sfxb.scaleOffset(0.5, 0);
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.FXchorusReadDelay(RMP1, REG|COMPC, "fladel", 0);
			sfxb.FXchorusReadDelay(RMP1, 0, "fladel+", 1);
			sfxb.readRegisterFilter(fhp, 0.02);
			sfxb.writeRegisterHighshelf(fhp, -1);
			sfxb.writeRegister(flaout, 0);
			sfxb.readRegister(flaout, 1);
			sfxb.mulx(flamix);
			sfxb.readRegister(mono, 1);
			sfxb.readRegister(revout, 1);
			sfxb.scaleOffset(1, 0.02);
			sfxb.writeRegister(dacl, 1);
			sfxb.scaleOffset(1, -0.04);
			sfxb.writeRegister(dacr, 0);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
