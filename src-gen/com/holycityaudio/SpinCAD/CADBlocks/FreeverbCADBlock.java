/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * FreeverbCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.FreeverbControlPanel;
		
	    @SuppressWarnings("unused")
	    public class FreeverbCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private FreeverbControlPanel cp = null;
			
			private double gain = 0.5;
			private double krt = 0.42;
			private double damping = 0.5;
			private double allpassamt = 0.5;
			private int outputL;
			private int outputR;
			private int filt1;
			private int filt2;
			private int filt3;
			private int filt4;
			private int filt5;
			private int filt6;
			private int filt7;
			private int filt8;
			private int mono;
			private int combsum;

			public FreeverbCADBlock(int x, int y) {
				super(x, y);
				setName("Freeverb");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_L");
				addInputPin(this, "Input_R");
				addOutputPin(this, "OutputL");
				addOutputPin(this, "OutputR");
				addControlInputPin(this, "Reverb_Time");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new FreeverbControlPanel(this);
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
			
			// finally, generate the instructions
			if(this.getPin("Input_L").isConnected() == true) {
			sfxb.FXallocDelayMem("comb1", 1116); 
			sfxb.FXallocDelayMem("comb2", 1188); 
			sfxb.FXallocDelayMem("comb3", 1277); 
			sfxb.FXallocDelayMem("comb4", 1356); 
			sfxb.FXallocDelayMem("comb5", 1422); 
			sfxb.FXallocDelayMem("comb6", 1491); 
			sfxb.FXallocDelayMem("comb7", 1557); 
			sfxb.FXallocDelayMem("comb8", 1617); 
			sfxb.FXallocDelayMem("ap1L", 556); 
			sfxb.FXallocDelayMem("ap2L", 441); 
			sfxb.FXallocDelayMem("ap3L", 341); 
			sfxb.FXallocDelayMem("ap4L", 225); 
			sfxb.FXallocDelayMem("ap1R", 579); 
			sfxb.FXallocDelayMem("ap2R", 464); 
			sfxb.FXallocDelayMem("ap3R", 364); 
			sfxb.FXallocDelayMem("ap4R", 248); 
			outputL = sfxb.allocateReg();
			outputR = sfxb.allocateReg();
			filt1 = sfxb.allocateReg();
			filt2 = sfxb.allocateReg();
			filt3 = sfxb.allocateReg();
			filt4 = sfxb.allocateReg();
			filt5 = sfxb.allocateReg();
			filt6 = sfxb.allocateReg();
			filt7 = sfxb.allocateReg();
			filt8 = sfxb.allocateReg();
			mono = sfxb.allocateReg();
			combsum = sfxb.allocateReg();
			sfxb.readRegister(input, gain);
			if(this.getPin("Input_R").isConnected() == true) {
			sfxb.readRegister(inputR, gain);
			}
			
			sfxb.writeRegister(mono, 0);
			sfxb.FXreadDelay("comb1#", 0, 1.0);
			sfxb.readRegisterFilter(filt1, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt1, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt1, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb1", 0, 0);
			sfxb.FXreadDelay("comb2#", 0, 1.0);
			sfxb.readRegisterFilter(filt2, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt2, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt2, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb2", 0, 0);
			sfxb.FXreadDelay("comb3#", 0, 1.0);
			sfxb.readRegisterFilter(filt3, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt3, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt3, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb3", 0, 0);
			sfxb.FXreadDelay("comb4#", 0, 1.0);
			sfxb.readRegisterFilter(filt4, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt4, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt4, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb4", 0, 0);
			sfxb.FXreadDelay("comb5#", 0, 1.0);
			sfxb.readRegisterFilter(filt5, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt5, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt5, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb5", 0, 0);
			sfxb.FXreadDelay("comb6#", 0, 1.0);
			sfxb.readRegisterFilter(filt6, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt6, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt6, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb6", 0, 0);
			sfxb.FXreadDelay("comb7#", 0, 1.0);
			sfxb.readRegisterFilter(filt7, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt7, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt7, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb7", 0, 0);
			sfxb.FXreadDelay("comb8#", 0, 1.0);
			sfxb.readRegisterFilter(filt8, damping);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.writeRegister(filt8, 1.0);
			sfxb.mulx(input0);
			} else {
			sfxb.writeRegister(filt8, krt);
			}
			
			sfxb.readRegister(mono, 1.0);
			sfxb.FXwriteDelay("comb8", 0, 0);
			sfxb.FXreadDelay("comb1#", 0, 0.2);
			sfxb.FXreadDelay("comb2#", 0, 0.2);
			sfxb.FXreadDelay("comb3#", 0, 0.2);
			sfxb.FXreadDelay("comb4#", 0, 0.2);
			sfxb.FXreadDelay("comb5#", 0, 0.2);
			sfxb.FXreadDelay("comb6#", 0, 0.2);
			sfxb.FXreadDelay("comb7#", 0, 0.2);
			sfxb.FXreadDelay("comb8#", 0, 0.2);
			sfxb.writeRegister(combsum, 1.0);
			sfxb.FXreadDelay("ap1L#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap1L", 0, allpassamt);
			sfxb.FXreadDelay("ap2L#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap2L", 0, allpassamt);
			sfxb.FXreadDelay("ap3L#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap3L", 0, allpassamt);
			sfxb.FXreadDelay("ap4L#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap4L", 0, allpassamt);
			sfxb.writeRegister(outputL, 0);
			sfxb.readRegister(combsum, 1.0);
			sfxb.FXreadDelay("ap1R#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap1R", 0, allpassamt);
			sfxb.FXreadDelay("ap2R#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap2R", 0, allpassamt);
			sfxb.FXreadDelay("ap3R#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap3R", 0, allpassamt);
			sfxb.FXreadDelay("ap4R#", 0, -allpassamt);
			sfxb.FXwriteAllpass("ap4R", 0, allpassamt);
			sfxb.writeRegister(outputR, 0);
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
			public void setdamping(double __param) {
				damping = __param;	
			}
			
			public double getdamping() {
				return damping;
			}
		}	
