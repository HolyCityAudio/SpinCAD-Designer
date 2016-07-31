/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ga_demo_echoCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ga_demo_echoControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ga_demo_echoCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ga_demo_echoControlPanel cp = null;
			
			private int mono;
			private int apout;
			private int lp1;
			private int lp2;
			private int revout;
			private int dout;
			private int dx;
			private int efil;
			private int dacl;
			private int dacr;
			private double kap = 0.6;
			private double krt = 0.55;
			private double krf = 0.5;
			private double krs = -0.6;
			private double fbk = 0;
			private double default_delay_time = 0.2;

			public ga_demo_echoCADBlock(int x, int y) {
				super(x, y);
				setName("Echo");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Reverb_Level");
				addControlInputPin(this, "Delay_Time");
				addControlInputPin(this, "Echo_Level");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ga_demo_echoControlPanel(this);
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
			sp = this.getPin("Delay_Time").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Echo_Level").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("edel", 20000); 
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
			dout = sfxb.allocateReg();
			dx = sfxb.allocateReg();
			efil = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.skip(RUN, 4);
			sfxb.writeRegister(lp1, 0);
			sfxb.writeRegister(lp2, 0);
			sfxb.writeRegister(efil, 0);
			sfxb.loadSinLFO((int) SIN0,(int) 12, (int) 100);
			sfxb.readRegister(adcl, 0.5);
			sfxb.readRegister(adcr, 0.5);
			sfxb.writeRegister(mono, 0);
			sfxb.readRegister(dout, fbk);
			sfxb.readRegister(mono, 1);
			sfxb.FXwriteDelay("edel", 0, 0);
			sfxb.readRegister(mono, 0.5);
			sfxb.readRegister(dx, 0.5);
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
			if(this.getPin("Delay_Time").isConnected() == true) {
			sfxb.readRegister(input1, 1);
			sfxb.and(0b01111110_00000000_00000000);
			sfxb.scaleOffset(0.55, 0.05);
			} else {
			sfxb.scaleOffset(0.0, default_delay_time);
			}
			
			sfxb.writeRegister(ADDR_PTR, 0);
			sfxb.readDelayPointer(1);
			sfxb.readRegisterFilter(efil, 0.3);
			sfxb.writeRegisterLowshelf(efil, -1);
			sfxb.writeRegister(dout, 1);
			if(this.getPin("Echo_Level").isConnected() == true) {
			sfxb.mulx(input2);
			sfxb.mulx(input2);
			} else {
			sfxb.scaleOffset(0.5, 0.0);
			}
			
			sfxb.writeRegister(dx, 1);
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
