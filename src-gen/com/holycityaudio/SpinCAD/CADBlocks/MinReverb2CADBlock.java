/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * MinReverb2CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.MinReverb2ControlPanel;
		
	    @SuppressWarnings("unused")
	    public class MinReverb2CADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private MinReverb2ControlPanel cp = null;
			
			private double gain = 0.5;
			private double rtMax = 0.9;
			private double kiap = 0.5;
			private double ap1length = 125;
			private double ap2length = 250;
			private double ap3length = 750;
			private double ap4length = 1500;
			private double klap = 0.6;
			private double lap1length = 3800;
			private double del1length = 6250;
			private double lap2length = 4200;
			private double del2length = 7250;
			private int temp;
			private int iapout;
			private int outputL;
			private int outputR;
			private int hpf3;
			private int lpf3;
			private int krt;
			private int apout;

			public MinReverb2CADBlock(int x, int y) {
				super(x, y);
				setName("Small Reverb");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Output_Left");
				addOutputPin(this, "Output_Right");
				addControlInputPin(this, "Reverb_Time");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new MinReverb2ControlPanel(this);
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
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Input_Right").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			sp = this.getPin("Reverb_Time").getPinConnection();
			int revTime = -1;
			if(sp != null) {
				revTime = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input_Left").isConnected() == true) {
			temp = sfxb.allocateReg();
			iapout = sfxb.allocateReg();
			outputL = sfxb.allocateReg();
			outputR = sfxb.allocateReg();
			hpf3 = sfxb.allocateReg();
			lpf3 = sfxb.allocateReg();
			krt = sfxb.allocateReg();
			apout = sfxb.allocateReg();
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.readRegister(revTime, rtMax);
			} else {
			sfxb.scaleOffset(0, rtMax);
			}
			
			sfxb.writeRegister(krt, 0);
			sfxb.FXallocDelayMem("api1", ap1length); 
			sfxb.FXallocDelayMem("api2", ap2length); 
			sfxb.FXallocDelayMem("api3", ap3length); 
			sfxb.FXallocDelayMem("api4", ap4length); 
			sfxb.FXallocDelayMem("ap1", lap1length); 
			sfxb.FXallocDelayMem("del1", del1length); 
			sfxb.FXallocDelayMem("ap2", lap2length); 
			sfxb.FXallocDelayMem("del2", del2length); 
			sfxb.readRegister(input1, gain);
			if(this.getPin("Input_Right").isConnected() == true) {
			sfxb.readRegister(input2, gain);
			}
			
			sfxb.FXreadDelay("api1#", 0, kiap);
			sfxb.FXwriteAllpass("api1", 0, -kiap);
			sfxb.FXreadDelay("api2#", 0, kiap);
			sfxb.FXwriteAllpass("api2", 0, -kiap);
			sfxb.FXreadDelay("api3#", 0, kiap);
			sfxb.FXwriteAllpass("api3", 0, -kiap);
			sfxb.FXreadDelay("api4#", 0, kiap);
			sfxb.FXwriteAllpass("api4", 0, -kiap);
			sfxb.writeRegister(apout, 1);
			sfxb.FXreadDelay("del2#", 0, rtMax);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(revTime);
			}
			
			sfxb.FXreadDelay("ap1#", 0, -klap);
			sfxb.FXwriteAllpass("ap1", 0, klap);
			sfxb.FXwriteDelay("del1", 0, 1.99);
			sfxb.writeRegister(outputL, 0);
			sfxb.readRegister(apout, 1);
			sfxb.FXreadDelay("del1#", 0, rtMax);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.mulx(revTime);
			}
			
			sfxb.FXreadDelay("ap2#", 0, klap);
			sfxb.FXwriteAllpass("ap2", 0, -klap);
			sfxb.FXwriteDelay("del2", 0, 1.99);
			sfxb.writeRegister(outputR, 0);
			this.getPin("Output_Left").setRegister(outputL);
			this.getPin("Output_Right").setRegister(outputR);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setkiap(double __param) {
				kiap = __param;	
			}
			
			public double getkiap() {
				return kiap;
			}
			public void setap1length(double __param) {
				ap1length = __param;	
			}
			
			public double getap1length() {
				return ap1length;
			}
			public void setap2length(double __param) {
				ap2length = __param;	
			}
			
			public double getap2length() {
				return ap2length;
			}
			public void setap3length(double __param) {
				ap3length = __param;	
			}
			
			public double getap3length() {
				return ap3length;
			}
			public void setap4length(double __param) {
				ap4length = __param;	
			}
			
			public double getap4length() {
				return ap4length;
			}
			public void setklap(double __param) {
				klap = __param;	
			}
			
			public double getklap() {
				return klap;
			}
			public void setlap1length(double __param) {
				lap1length = __param;	
			}
			
			public double getlap1length() {
				return lap1length;
			}
			public void setdel1length(double __param) {
				del1length = __param;	
			}
			
			public double getdel1length() {
				return del1length;
			}
			public void setlap2length(double __param) {
				lap2length = __param;	
			}
			
			public double getlap2length() {
				return lap2length;
			}
			public void setdel2length(double __param) {
				del2length = __param;	
			}
			
			public double getdel2length() {
				return del2length;
			}
		}	
