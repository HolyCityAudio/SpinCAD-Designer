/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * act_xoverCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.act_xoverControlPanel;
		
	    @SuppressWarnings("unused")
	    public class act_xoverCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private act_xoverControlPanel cp = null;
			
			private int lf1a;
			private int lf1b;
			private int lf2a;
			private int lf2b;
			private int hf1a;
			private int hf1b;
			private int hf2a;
			private int hf2b;
			private int temp;
			private int delout;
			private int eqin;
			private int b1a;
			private int b1b;
			private int b2a;
			private int b2b;
			private int b3a;
			private int b3b;
			private int b4a;
			private int b4b;
			private int b5a;
			private int b5b;
			private int b6a;
			private int b6b;
			private int b7a;
			private int b7b;
			private int loext;
			private int output1;
			private int output2;
			private double kfl = 0.48;
			private double kql = 0.5;
			private double kfh = 0.6;
			private double kqh = 0.85;
			private double kflext = 0.01;
			private double kshext = -0.5;
			private double ampl = 1.0;
			private double amph = 1.0;
			private double kf1 = 0;
			private double kq1 = 0;
			private double kp1 = 0;
			private double kg1 = 0;
			private double kf2 = 0;
			private double kq2 = 0;
			private double kp2 = 0;
			private double kg2 = 0;
			private double kf3 = 0;
			private double kq3 = 0;
			private double kp3 = 0;
			private double kg3 = 0;
			private double kf4 = 0;
			private double kq4 = 0;
			private double kp4 = 0;
			private double kg4 = 0;
			private double kf5 = 0;
			private double kq5 = 0;
			private double kp5 = 0;
			private double kg5 = 0;
			private double kf6 = 0;
			private double kq6 = 0;
			private double kp6 = 0;
			private double kg6 = 0;
			private double kf7 = 0;
			private double kq7 = 0;
			private double kp7 = 0;
			private double kg7 = 0;

			public act_xoverCADBlock(int x, int y) {
				super(x, y);
				setName("L-R Crossover");					
			setBorderColor(new Color(0x24f26f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Output_L");
				addOutputPin(this, "Output_R");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new act_xoverControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly

			int del = 0;
			
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
			
			// finally, generate the instructions
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.FXallocDelayMem("del1", 1000); 
			sfxb.FXallocDelayMem("del2", 1000); 
			lf1a = sfxb.allocateReg();
			lf1b = sfxb.allocateReg();
			lf2a = sfxb.allocateReg();
			lf2b = sfxb.allocateReg();
			hf1a = sfxb.allocateReg();
			hf1b = sfxb.allocateReg();
			hf2a = sfxb.allocateReg();
			hf2b = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			delout = sfxb.allocateReg();
			eqin = sfxb.allocateReg();
			b1a = sfxb.allocateReg();
			b1b = sfxb.allocateReg();
			b2a = sfxb.allocateReg();
			b2b = sfxb.allocateReg();
			b3a = sfxb.allocateReg();
			b3b = sfxb.allocateReg();
			b4a = sfxb.allocateReg();
			b4b = sfxb.allocateReg();
			b5a = sfxb.allocateReg();
			b5b = sfxb.allocateReg();
			b6a = sfxb.allocateReg();
			b6b = sfxb.allocateReg();
			b7a = sfxb.allocateReg();
			b7b = sfxb.allocateReg();
			loext = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			sfxb.readRegister(adcr, 0.5);
			sfxb.readRegister(adcl, 0.5);
			sfxb.writeRegister(eqin, 0);
			sfxb.readRegister(eqin, kg1);
			sfxb.readRegister(b1b, -kf1);
			sfxb.readRegister(b1a, 1);
			sfxb.writeRegister(temp, kq1);
			sfxb.readRegister(eqin, kg1);
			sfxb.writeRegister(b1a, 0);
			sfxb.readRegister(temp, kf1);
			sfxb.readRegister(b1b, 1);
			sfxb.writeRegister(b1b, 0);
			sfxb.readRegister(eqin, 1);
			sfxb.readRegister(temp, kp1);
			sfxb.writeRegister(eqin, kg2);
			sfxb.readRegister(b2b, -kf2);
			sfxb.readRegister(b2a, 1);
			sfxb.writeRegister(temp, kq2);
			sfxb.readRegister(eqin, kg2);
			sfxb.writeRegister(b2a, 0);
			sfxb.readRegister(temp, kf2);
			sfxb.readRegister(b2b, 1);
			sfxb.writeRegister(b2b, 0);
			sfxb.readRegister(eqin, 1);
			sfxb.readRegister(temp, kp2);
			sfxb.writeRegister(eqin, kg3);
			sfxb.readRegister(b3b, -kf3);
			sfxb.readRegister(b3a, 1);
			sfxb.writeRegister(temp, kq3);
			sfxb.readRegister(eqin, kg3);
			sfxb.writeRegister(b3a, 0);
			sfxb.readRegister(temp, kf3);
			sfxb.readRegister(b3b, 1);
			sfxb.writeRegister(b3b, 0);
			sfxb.readRegister(eqin, 1);
			sfxb.readRegister(temp, kp3);
			sfxb.writeRegister(eqin, kg4);
			sfxb.readRegister(b4b, -kf4);
			sfxb.readRegister(b4a, 1);
			sfxb.writeRegister(temp, kq4);
			sfxb.readRegister(eqin, kg4);
			sfxb.writeRegister(b4a, 0);
			sfxb.readRegister(temp, kf4);
			sfxb.readRegister(b4b, 1);
			sfxb.writeRegister(b4b, 0);
			sfxb.readRegister(eqin, 1);
			sfxb.readRegister(temp, kp4);
			sfxb.writeRegister(eqin, kg5);
			sfxb.readRegister(b5b, -kf5);
			sfxb.readRegister(b5a, 1);
			sfxb.writeRegister(temp, kq5);
			sfxb.readRegister(eqin, kg5);
			sfxb.writeRegister(b5a, 0);
			sfxb.readRegister(temp, kf5);
			sfxb.readRegister(b5b, 1);
			sfxb.writeRegister(b5b, 0);
			sfxb.readRegister(eqin, 1);
			sfxb.readRegister(temp, kp5);
			sfxb.writeRegister(eqin, kg6);
			sfxb.readRegister(b6b, -kf6);
			sfxb.readRegister(b6a, 1);
			sfxb.writeRegister(temp, kq6);
			sfxb.readRegister(eqin, kg6);
			sfxb.writeRegister(b6a, 0);
			sfxb.readRegister(temp, kf6);
			sfxb.readRegister(b6b, 1);
			sfxb.writeRegister(b6b, 0);
			sfxb.readRegister(eqin, 1);
			sfxb.readRegister(temp, kp6);
			sfxb.writeRegister(eqin, kg7);
			sfxb.readRegister(b7b, -kf7);
			sfxb.readRegister(b7a, 1);
			sfxb.writeRegister(temp, kq7);
			sfxb.readRegister(eqin, kg7);
			sfxb.writeRegister(b7a, 0);
			sfxb.readRegister(temp, kf7);
			sfxb.readRegister(b7b, 1);
			sfxb.writeRegister(b7b, 0);
			sfxb.readRegister(eqin, 1);
			sfxb.readRegister(temp, kp7);
			sfxb.writeRegister(eqin, 1);
			sfxb.FXwriteDelay("del1", 0, 1);
			sfxb.FXreadDelay("del1", 0, -1);
			sfxb.FXwriteDelay("del2", 0, 0);
			sfxb.FXreadDelay("del1+", del, 1);
			sfxb.FXreadDelay("del2+", del, 1);
			sfxb.writeRegister(delout, 0);
			sfxb.readRegister(lf1a, kfl);
			sfxb.readRegister(lf1b, 1);
			sfxb.writeRegister(lf1b, -kfl);
			sfxb.readRegister(lf1a, kql);
			sfxb.readRegister(eqin, 0.05);
			sfxb.writeRegister(lf1a, 0);
			sfxb.readRegister(lf2a, kfl);
			sfxb.readRegister(lf2b, 1);
			sfxb.writeRegister(lf2b, -kfl);
			sfxb.readRegister(lf2a, kql);
			sfxb.readRegister(lf1b, 1);
			sfxb.writeRegister(lf2a, 0);
			sfxb.readRegister(hf1a, kfh);
			sfxb.readRegister(hf1b, 1);
			sfxb.writeRegister(hf1b, 1);
			sfxb.readRegister(delout, 0.25);
			sfxb.readRegister(hf1a, kqh);
			sfxb.writeRegister(delout, 1);
			sfxb.scaleOffset(-kfh, 0);
			sfxb.readRegister(hf1a, 1);
			sfxb.writeRegister(hf1a, 0);
			sfxb.readRegister(hf2a, kfh);
			sfxb.readRegister(hf2b, 1);
			sfxb.writeRegister(hf2b, 1);
			sfxb.readRegister(delout, 0.3);
			sfxb.readRegister(hf2a, kqh);
			sfxb.writeRegister(delout, 1);
			sfxb.scaleOffset(-kfh, 0);
			sfxb.readRegister(hf2a, 1);
			sfxb.writeRegister(hf2a, 0);
			sfxb.readRegister(lf2b, -2);
			sfxb.readRegisterFilter(loext, kflext);
			sfxb.writeRegisterLowshelf(loext, kshext);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(ampl, 0);
			sfxb.writeRegister(output1, 0);
			sfxb.readRegister(delout, -2);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(amph, 0);
			sfxb.writeRegister(output2, 0);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
