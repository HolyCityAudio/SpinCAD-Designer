/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * SpringReverbCADBlock.java
 * Copyright (C) 2013 - 2026 - Gary Worsham 
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
 		import com.holycityaudio.SpinCAD.ControlPanel.SpringReverbControlPanel;
		
	    @SuppressWarnings("unused")
	    public class SpringReverbCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private SpringReverbControlPanel cp = null;
			
			private double gain = 0.5;
			private double krt = 0.85;
			private double krf = 0.55;
			private double klap = 0.6;
			private double kap0 = -0.70;
			private double kap1 = -0.65;
			private double kap2 = -0.60;
			private double kap3 = -0.50;
			private double baselen1 = 4;
			private double baselen2 = 7;
			private int outputL;
			private int outputR;
			private int lp1;
			private int lp2;
			private int temp;

			public SpringReverbCADBlock(int x, int y) {
				super(x, y);
				setName("Spring_Reverb");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_L");
				addInputPin(this, "Input_R");
				addOutputPin(this, "OutputL");
				addOutputPin(this, "OutputR");
				addControlInputPin(this, "Reverb_Time");
				addControlInputPin(this, "Damping");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new SpringReverbControlPanel(this);
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
			sp = this.getPin("Input_L").getPinConnection();
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			sp = this.getPin("Input_R").getPinConnection();
			int inputR = -1;
			if(sp != null) {
				inputR = sp.getRegister();
			}
			sp = this.getPin("Reverb_Time").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Damping").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input_L").isConnected() == true) {
			sfxb.FXallocDelayMem("lap1a", 404); 
			sfxb.FXallocDelayMem("lap1b", 967); 
			sfxb.FXallocDelayMem("d1", 1445); 
			sfxb.FXallocDelayMem("lap2a", 608); 
			sfxb.FXallocDelayMem("lap2b", 893); 
			sfxb.FXallocDelayMem("d2", 1013); 
			sfxb.FXallocDelayMem("ap1", baselen1); 
			sfxb.FXallocDelayMem("ap2", baselen1); 
			sfxb.FXallocDelayMem("ap3", baselen1); 
			sfxb.FXallocDelayMem("ap4", baselen1); 
			sfxb.FXallocDelayMem("ap5", baselen1); 
			sfxb.FXallocDelayMem("ap6", baselen1); 
			sfxb.FXallocDelayMem("ap7", baselen1); 
			sfxb.FXallocDelayMem("ap8", baselen1); 
			sfxb.FXallocDelayMem("ap9", baselen1); 
			sfxb.FXallocDelayMem("ap10", baselen2); 
			sfxb.FXallocDelayMem("ap11", baselen2); 
			sfxb.FXallocDelayMem("ap12", baselen2); 
			sfxb.FXallocDelayMem("ap13", baselen2); 
			outputL = sfxb.allocateReg();
			outputR = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) 15, (int) 40);
			sfxb.readRegister(input, gain);
			if(this.getPin("Input_R").isConnected() == true) {
			sfxb.readRegister(inputR, gain);
			}
			
			sfxb.writeRegister(temp, 0);
			sfxb.FXreadDelay("d1#", 0, krt);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(input0);
			}
			
			sfxb.readRegisterFilter(lp1, krf);
			sfxb.writeRegisterLowshelf(lp1, -1);
			if(this.getPin("Damping").isConnected() == true) {
			sfxb.mulx(input1);
			}
			
			sfxb.FXreadDelay("lap1a#", 0, klap);
			sfxb.FXwriteAllpass("lap1a", 0, -klap);
			sfxb.FXreadDelay("lap1b#", 0, klap);
			sfxb.FXwriteAllpass("lap1b", 0, -klap);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("d2", 0, 0);
			sfxb.FXreadDelay("d2#", 0, krt);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(input0);
			}
			
			sfxb.readRegisterFilter(lp2, krf);
			sfxb.writeRegisterLowshelf(lp2, -1);
			if(this.getPin("Damping").isConnected() == true) {
			sfxb.mulx(input1);
			}
			
			sfxb.FXreadDelay("lap2a#", 0, klap);
			sfxb.FXwriteAllpass("lap2a", 0, -klap);
			sfxb.FXreadDelay("lap2b#", 0, klap);
			sfxb.FXwriteAllpass("lap2b", 0, -klap);
			sfxb.readRegister(temp, 1.0);
			sfxb.FXwriteDelay("d1", 0, 0);
			sfxb.readRegister(lp1, 0.6);
			sfxb.readRegister(lp2, 0.6);
			sfxb.writeRegister(outputR, 1.0);
			sfxb.FXreadDelay("ap1#", 0, kap0);
			sfxb.FXwriteAllpass("ap1", 0, -kap0);
			sfxb.FXreadDelay("ap2#", 0, kap1);
			sfxb.FXwriteAllpass("ap2", 0, -kap1);
			sfxb.FXreadDelay("ap3#", 0, kap2);
			sfxb.FXwriteAllpass("ap3", 0, -kap2);
			sfxb.FXreadDelay("ap4#", 0, kap3);
			sfxb.FXwriteAllpass("ap4", 0, -kap3);
			sfxb.FXreadDelay("ap5#", 0, kap0);
			sfxb.FXwriteAllpass("ap5", 0, -kap0);
			sfxb.FXreadDelay("ap6#", 0, kap1);
			sfxb.FXwriteAllpass("ap6", 0, -kap1);
			sfxb.FXreadDelay("ap7#", 0, kap2);
			sfxb.FXwriteAllpass("ap7", 0, -kap2);
			sfxb.FXreadDelay("ap8#", 0, kap3);
			sfxb.FXwriteAllpass("ap8", 0, -kap3);
			sfxb.FXreadDelay("ap9#", 0, kap0);
			sfxb.FXwriteAllpass("ap9", 0, -kap0);
			sfxb.FXreadDelay("ap10#", 0, kap1);
			sfxb.FXwriteAllpass("ap10", 0, -kap1);
			sfxb.FXreadDelay("ap11#", 0, kap2);
			sfxb.FXwriteAllpass("ap11", 0, -kap2);
			sfxb.FXreadDelay("ap12#", 0, kap3);
			sfxb.FXwriteAllpass("ap12", 0, -kap3);
			sfxb.FXreadDelay("ap13#", 0, kap0);
			sfxb.FXwriteAllpass("ap13", 0, -kap0);
			sfxb.writeRegister(outputL, 0);
			sfxb.FXchorusReadDelay(SIN0, SIN|REG|COMPC, "lap1b+", 25);
			sfxb.FXchorusReadDelay(SIN0, SIN, "lap1b+", 26);
			sfxb.FXwriteDelay("lap1b+", (int)(50 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, COS|REG|COMPC, "lap2b+", 25);
			sfxb.FXchorusReadDelay(SIN0, COS, "lap2b+", 26);
			sfxb.FXwriteDelay("lap2b+", (int)(50 * 1.0), 0);
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
			public void setkrf(double __param) {
				krf = __param;	
			}
			
			public double getkrf() {
				return krf;
			}
		}	
