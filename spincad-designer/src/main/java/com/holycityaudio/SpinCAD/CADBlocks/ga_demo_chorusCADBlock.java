/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ga_demo_chorusCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ga_demo_chorusControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ga_demo_chorusCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ga_demo_chorusControlPanel cp = null;
			
			private int mono;
			private int apout;
			private int lp1;
			private int lp2;
			private int revout;
			private int choout;
			private int output1;
			private int output2;
			private double kap = 0.6;
			private double krt = 0.55;
			private double krf = 0.5;
			private double krs = -0.6;

			public ga_demo_chorusCADBlock(int x, int y) {
				super(x, y);
				setName("GA_Demo_Chorus");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Reverb_Level");
				addControlInputPin(this, "Chorus_Rate");
				addControlInputPin(this, "Effect_Level");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ga_demo_chorusControlPanel(this);
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
			sp = this.getPin("Chorus_Rate").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Effect_Level").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("ap1", 334); 
			sfxb.FXallocDelayMem("ap2", 556); 
			sfxb.FXallocDelayMem("ap3", 871); 
			sfxb.FXallocDelayMem("lap1a", 808); 
			sfxb.FXallocDelayMem("lap1b", 1934); 
			sfxb.FXallocDelayMem("d1", 2489); 
			sfxb.FXallocDelayMem("lap2a", 1016); 
			sfxb.FXallocDelayMem("lap2b", 1787); 
			sfxb.FXallocDelayMem("d2", 2287); 
			sfxb.FXallocDelayMem("chodel", 5000); 
			mono = sfxb.allocateReg();
			apout = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			revout = sfxb.allocateReg();
			choout = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.skip(RUN, 4);
			sfxb.writeRegister(lp1, 0);
			sfxb.writeRegister(lp2, 0);
			sfxb.loadSinLFO((int) SIN0,(int) 12, (int) 100);
			sfxb.loadSinLFO((int) SIN1,(int) 0, (int) 800);
			sfxb.readRegister(adcl, 0.5);
			sfxb.readRegister(adcr, 0.5);
			sfxb.FXwriteDelay("chodel", 0, 1);
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
			sfxb.FXwriteDelay("lap1b+", (int)(200 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap2b+", 100);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap2b+", 101);
			sfxb.FXwriteDelay("lap2b+", (int)(200 * 1.0), 0);
			if(this.getPin("Chorus_Rate").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			sfxb.mulx(input1);
			sfxb.scaleOffset(0.02, 0.01);
			} else {
			sfxb.scaleOffset(0.0, 0.02);
			}
			
			sfxb.writeRegister(SIN1_RATE, 0);
			sfxb.FXchorusReadDelay(SIN1, SIN|REG|COMPC, "chodel+", 1400);
			sfxb.FXchorusReadDelay(SIN1, SIN, "chodel+", 1401);
			sfxb.FXchorusReadDelay(SIN1, COS|REG|COMPC, "chodel+", 1200);
			sfxb.FXchorusReadDelay(SIN1, COS, "chodel+", 1201);
			sfxb.FXchorusReadDelay(SIN1, SIN|REG|COMPA, "chodel+", 1600);
			sfxb.FXchorusReadDelay(SIN1, SIN|COMPC|COMPA, "chodel+", 1601);
			sfxb.FXchorusReadDelay(SIN1, COS|REG|COMPA, "chodel+", 900);
			sfxb.FXchorusReadDelay(SIN1, COS|COMPC|COMPA, "chodel+", 901);
			if(this.getPin("Effect_Level").isConnected() == true) {
			sfxb.mulx(input2);
			} else {
			sfxb.scaleOffset(0.5, 0.0);
			}
			
			sfxb.writeRegister(choout, 0);
			sfxb.readRegister(mono, 1);
			sfxb.readRegister(revout, 1);
			sfxb.readRegister(choout, 0.5);
			sfxb.scaleOffset(1, 0.02);
			sfxb.writeRegister(output1, 1);
			sfxb.scaleOffset(1, -0.04);
			sfxb.writeRegister(output2, 0);
			this.getPin("Audio_Output_1").setRegister(output1);
			this.getPin("Audio_Output_2").setRegister(output2);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
