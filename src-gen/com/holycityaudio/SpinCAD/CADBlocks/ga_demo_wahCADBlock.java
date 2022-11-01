/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ga_demo_wahCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ga_demo_wahControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ga_demo_wahCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ga_demo_wahControlPanel cp = null;
			
			private int mono;
			private int apout;
			private int lp1;
			private int lp2;
			private int revout;
			private int wahout;
			private int wf1;
			private int wf2;
			private int wf;
			private int wq;
			private int temp;
			private int avg;
			private int lavg;
			private int ffil;
			private int bypass;
			private int output1;
			private int output2;
			private double kap = 0.6;
			private double krt = 0.55;
			private double krf = 0.5;
			private double krs = -0.6;

			public ga_demo_wahCADBlock(int x, int y) {
				super(x, y);
				setName("Wah");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Reverb_Level");
				addControlInputPin(this, "Wah_Sens");
				addControlInputPin(this, "Wah_Level_Q");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ga_demo_wahControlPanel(this);
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
			sp = this.getPin("Wah_Sens").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Wah_Level_Q").getPinConnection();
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
			mono = sfxb.allocateReg();
			apout = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			revout = sfxb.allocateReg();
			wahout = sfxb.allocateReg();
			wf1 = sfxb.allocateReg();
			wf2 = sfxb.allocateReg();
			wf = sfxb.allocateReg();
			wq = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			avg = sfxb.allocateReg();
			lavg = sfxb.allocateReg();
			ffil = sfxb.allocateReg();
			bypass = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.skip(RUN, 5);
			sfxb.writeRegister(lp1, 0);
			sfxb.writeRegister(lp2, 0);
			sfxb.writeRegister(wf1, 0);
			sfxb.writeRegister(wf2, 0);
			sfxb.loadSinLFO((int) SIN0,(int) 12, (int) 100);
			sfxb.readRegister(adcl, 0.5);
			sfxb.readRegister(adcr, 0.5);
			sfxb.writeRegister(mono, 0);
			sfxb.readRegister(wahout, 0.5);
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
			sfxb.FXreadDelay("d1", 0, 0.5);
			}
			
			sfxb.writeRegister(revout, 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap1b+", 100);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap1b+", 101);
			sfxb.FXwriteDelay("lap1b+", (int)(200 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap2b+", 100);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap2b+", 101);
			sfxb.FXwriteDelay("lap2b+", (int)(200 * 1.0), 0);
			sfxb.readRegister(mono, 1);
			sfxb.absa();
			sfxb.readRegisterFilter(avg, 0.01);
			sfxb.writeRegister(avg, 0);
			sfxb.readRegister(lavg, 0.001);
			sfxb.scaleOffset(-0.01, 0);
			sfxb.readRegister(lavg, 1);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(avg, 1);
			sfxb.maxx(temp, 1);
			sfxb.writeRegister(lavg, 1);
			sfxb.scaleOffset(1, 0.002);
			sfxb.log(1, 0);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(avg, 1);
			sfxb.log(1, 0);
			if(this.getPin("Wah_Sens").isConnected() == true) {
			sfxb.readRegister(temp, -1);
			sfxb.mulx(input1);
			} else {
			sfxb.readRegister(temp, -0.5);
			}
			
			sfxb.exp(1, 0);
			sfxb.readRegisterFilter(ffil, 0.0005);
			sfxb.writeRegister(ffil, 1);
			sfxb.scaleOffset(0.7, 0.02);
			sfxb.writeRegister(wf, 0);
			if(this.getPin("Wah_Level_Q").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			sfxb.scaleOffset(-0.2, 0.25);
			} else {
			sfxb.scaleOffset(0.0, 0.2);
			}
			
			sfxb.writeRegister(wq, 0);
			sfxb.readRegister(wf1, 1);
			sfxb.mulx(wf);
			sfxb.readRegister(wf2, 1);
			sfxb.writeRegister(wf2, -1);
			sfxb.readRegister(mono, 1);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(wf1, -1);
			sfxb.mulx(wq);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(wf);
			sfxb.readRegister(wf1, 1);
			sfxb.writeRegister(wf1, 0);
			if(this.getPin("Wah_Level_Q").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			} else {
			sfxb.scaleOffset(0.0, 0.8);
			}
			
			sfxb.readRegister(bypass, 0.9);
			sfxb.writeRegister(bypass, 0);
			sfxb.readRegister(mono, -1);
			sfxb.readRegister(wf2, 1);
			sfxb.mulx(bypass);
			sfxb.readRegister(mono, 1);
			sfxb.writeRegister(wahout, 1);
			sfxb.readRegister(revout, 1);
			sfxb.writeRegister(output1, 1);
			sfxb.writeRegister(output2, 0);
			this.getPin("Audio_Output_1").setRegister(output1);
			this.getPin("Audio_Output_2").setRegister(output2);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
