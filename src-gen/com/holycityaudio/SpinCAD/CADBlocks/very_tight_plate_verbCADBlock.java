/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * very_tight_plate_verbCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.very_tight_plate_verbControlPanel;
		
	    @SuppressWarnings("unused")
	    public class very_tight_plate_verbCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private very_tight_plate_verbControlPanel cp = null;
			
			private int temp;
			private int krt;
			private int ksh;
			private int ksl;
			private int lap;
			private int rap;
			private int hp1;
			private int hp2;
			private int hp3;
			private int hp4;
			private int lp1;
			private int lp2;
			private int lp3;
			private int lp4;
			private int lup;
			private int dacl;
			private int dacr;
			private double kapi = 0.65;
			private double kap = 0.65;
			private double kfl = 0.8;
			private double kfh = 0.02;

			public very_tight_plate_verbCADBlock(int x, int y) {
				super(x, y);
				setName("Plate_Verb_3");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Output 1");
				addOutputPin(this, "Output 2");
				addControlInputPin(this, "Reverb_Time");
				addControlInputPin(this, "LF_Loss");
				addControlInputPin(this, "HF_Loss");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new very_tight_plate_verbControlPanel(this);
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
			sp = this.getPin("Reverb_Time").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("LF_Loss").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("HF_Loss").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("api1l", 123); 
			sfxb.FXallocDelayMem("api2l", 264); 
			sfxb.FXallocDelayMem("api3l", 606); 
			sfxb.FXallocDelayMem("api4l", 768); 
			sfxb.FXallocDelayMem("api1r", 163); 
			sfxb.FXallocDelayMem("api2r", 322); 
			sfxb.FXallocDelayMem("api3r", 591); 
			sfxb.FXallocDelayMem("api4r", 778); 
			sfxb.FXallocDelayMem("apd1", 1201); 
			sfxb.FXallocDelayMem("apd2", 782); 
			sfxb.FXallocDelayMem("apd3", 1071); 
			sfxb.FXallocDelayMem("apd4", 947); 
			sfxb.FXallocDelayMem("del1", 1190); 
			sfxb.FXallocDelayMem("del2", 1091); 
			sfxb.FXallocDelayMem("del3", 1287); 
			sfxb.FXallocDelayMem("del4", 1379); 
			temp = sfxb.allocateReg();
			krt = sfxb.allocateReg();
			ksh = sfxb.allocateReg();
			ksl = sfxb.allocateReg();
			lap = sfxb.allocateReg();
			rap = sfxb.allocateReg();
			hp1 = sfxb.allocateReg();
			hp2 = sfxb.allocateReg();
			hp3 = sfxb.allocateReg();
			hp4 = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			lp2 = sfxb.allocateReg();
			lp3 = sfxb.allocateReg();
			lp4 = sfxb.allocateReg();
			lup = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			if(this.getPin("Input_Left").isConnected() == true) {
			if(input0 != -1) {
				System.out.println("Pin is Connected! " + "Reverb_Time"); 
				sfxb.readRegister(input0, 1.0);
			}
			else
			{
				System.out.println("Assigning default value! " + "0.5"); 
				sfxb.scaleOffset(0.0, 0.5);
			}
			sfxb.log(0.5, 0);
			sfxb.exp(1, 0);
			sfxb.scaleOffset(0.53, 0.4);
			sfxb.writeRegister(krt, 0);
			if(input1 != -1) {
				System.out.println("Pin is Connected! " + "LF_Loss"); 
				sfxb.readRegister(input1, 1.0);
			}
			else
			{
				System.out.println("Assigning default value! " + "0.2"); 
				sfxb.scaleOffset(0.0, 0.2);
			}
			sfxb.scaleOffset(1, -0.999);
			sfxb.writeRegister(ksh, 0);
			if(input2 != -1) {
				System.out.println("Pin is Connected! " + "HF_Loss"); 
				sfxb.readRegister(input2, 1.0);
			}
			else
			{
				System.out.println("Assigning default value! " + "0.7"); 
				sfxb.scaleOffset(0.0, 0.7);
			}
			sfxb.scaleOffset(1, -0.999);
			sfxb.writeRegister(ksl, 0);
			sfxb.readRegister(adcl, 0.25);
			sfxb.FXreadDelay("api1l#", 0, kapi);
			sfxb.FXwriteAllpass("api1l", 0, -kapi);
			sfxb.FXreadDelay("api2l#", 0, kapi);
			sfxb.FXwriteAllpass("api2l", 0, -kapi);
			sfxb.FXreadDelay("api3l#", 0, kapi);
			sfxb.FXwriteAllpass("api3l", 0, -kapi);
			sfxb.FXreadDelay("api4l#", 0, kapi);
			sfxb.FXwriteAllpass("api4l", 0, -kapi);
			sfxb.writeRegister(lap, 0);
			sfxb.readRegister(adcr, 0.25);
			sfxb.FXreadDelay("api1r#", 0, kapi);
			sfxb.FXwriteAllpass("api1r", 0, -kapi);
			sfxb.FXreadDelay("api2r#", 0, kapi);
			sfxb.FXwriteAllpass("api2r", 0, -kapi);
			sfxb.FXreadDelay("api3r#", 0, kapi);
			sfxb.FXwriteAllpass("api3r", 0, -kapi);
			sfxb.FXreadDelay("api4r#", 0, kapi);
			sfxb.FXwriteAllpass("api4r", 0, -kapi);
			sfxb.writeRegister(rap, 1);
			sfxb.readRegister(lup, 1);
			sfxb.FXreadDelay("apd1#", 0, kap);
			sfxb.FXwriteAllpass("apd1", 0, -kap);
			sfxb.FXwriteDelay("del1", 0, 0);
			sfxb.FXreadDelay("del1#", 0, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(hp1, kfh);
			sfxb.writeRegisterLowshelf(hp1, -1);
			sfxb.mulx(ksh);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lp1, kfl);
			sfxb.writeRegisterHighshelf(lp1, -1);
			sfxb.mulx(ksl);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(krt);
			sfxb.readRegister(lap, 1);
			sfxb.FXreadDelay("apd2#", 0, kap);
			sfxb.FXwriteAllpass("apd2", 0, -kap);
			sfxb.FXwriteDelay("del2", 0, 0);
			sfxb.FXreadDelay("del2#", 0, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(hp2, kfh);
			sfxb.writeRegisterLowshelf(hp2, -1);
			sfxb.mulx(ksh);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lp2, kfl);
			sfxb.writeRegisterHighshelf(lp2, -1);
			sfxb.mulx(ksl);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(krt);
			sfxb.readRegister(rap, 1);
			sfxb.FXreadDelay("apd3#", 0, kap);
			sfxb.FXwriteAllpass("apd3", 0, -kap);
			sfxb.FXwriteDelay("del3", 0, 0);
			sfxb.FXreadDelay("del3#", 0, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(hp3, kfh);
			sfxb.writeRegisterLowshelf(hp3, -1);
			sfxb.mulx(ksh);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lp3, kfl);
			sfxb.writeRegisterHighshelf(lp3, -1);
			sfxb.mulx(ksl);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(krt);
			sfxb.readRegister(lap, 1);
			sfxb.FXreadDelay("apd4#", 0, kap);
			sfxb.FXwriteAllpass("apd4", 0, -kap);
			sfxb.FXwriteDelay("del4", 0, 0);
			sfxb.FXreadDelay("del4#", 0, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(hp4, kfh);
			sfxb.writeRegisterLowshelf(hp4, -1);
			sfxb.mulx(ksh);
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.readRegisterFilter(lp4, kfl);
			sfxb.writeRegisterHighshelf(lp4, -1);
			sfxb.mulx(ksl);
			sfxb.readRegister(temp, 1);
			sfxb.mulx(krt);
			sfxb.writeRegister(lup, 0);
			sfxb.FXreadDelay("del1+", (int)(201 * 1.0), 0.8);
			sfxb.FXreadDelay("del2+", (int)(145 * 1.0), 0.7);
			sfxb.FXreadDelay("del3+", (int)(697 * 1.0), 0.6);
			sfxb.FXreadDelay("del4+", (int)(580 * 1.0), 0.5);
			sfxb.writeRegister(dacl, 0);
			sfxb.FXreadDelay("del1+", (int)(697 * 1.0), 0.8);
			sfxb.FXreadDelay("del2+", (int)(845 * 1.0), 0.7);
			sfxb.FXreadDelay("del3+", (int)(487 * 1.0), 0.6);
			sfxb.FXreadDelay("del4+", (int)(780 * 1.0), 0.5);
			sfxb.writeRegister(dacr, 0);
			sfxb.skip(RUN, 2);
			sfxb.loadSinLFO((int) 0,(int) 12, (int) 37);
			sfxb.loadSinLFO((int) 1,(int) 15, (int) 33);
			sfxb.FXchorusReadDelay(SIN0, REG|SIN|COMPC, "apd1+", 40);
			sfxb.FXchorusReadDelay(SIN0, SIN, "apd1+", 41);
			sfxb.FXwriteDelay("apd1+", (int)(80 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN0, COS|COMPC, "apd2+", 40);
			sfxb.FXchorusReadDelay(SIN0, COS, "apd2+", 41);
			sfxb.FXwriteDelay("apd2+", (int)(80 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN1, REG|SIN|COMPC, "apd3+", 40);
			sfxb.FXchorusReadDelay(SIN0, SIN, "apd3+", 41);
			sfxb.FXwriteDelay("apd3+", (int)(80 * 1.0), 0);
			sfxb.FXchorusReadDelay(SIN1, COS|COMPC, "apd4+", 40);
			sfxb.FXchorusReadDelay(SIN0, COS, "apd4+", 41);
			sfxb.FXwriteDelay("apd4+", (int)(80 * 1.0), 0);
			}
			
			this.getPin("Output 1").setRegister(dacl);
			this.getPin("Output 2").setRegister(dacr);

			}
			
			// create setters and getter for control panel variables
		}	
