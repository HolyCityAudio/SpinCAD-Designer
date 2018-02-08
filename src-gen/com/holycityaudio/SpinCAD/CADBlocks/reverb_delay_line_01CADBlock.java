/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_delay_line_01CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.reverb_delay_line_01ControlPanel;
		
	    @SuppressWarnings("unused")
	    public class reverb_delay_line_01CADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private reverb_delay_line_01ControlPanel cp = null;
			
			private double gain = 0.5;
			private double delayLength = 2500;
			private double krt = 0.5;
			private double lpdf = 0.12;
			private double ap1Length = 231;
			private double ap1kap = 0.5;
			private double ap2Length = 231;
			private double ap2kap = 0.5;
			private double KRS = -1;
			private int dlOut;
			private int lp1;
			private int hp1;

			public reverb_delay_line_01CADBlock(int x, int y) {
				super(x, y);
				setName("Reverb_Delay_Line_01");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addInputPin(this, "Feedback Input");
				addOutputPin(this, "Delay Line Out");
				addOutputPin(this, "Low Pass Out");
			// if any control panel elements declared, set hasControlPanel to true
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
						cp = new reverb_delay_line_01ControlPanel(this);
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
			sp = this.getPin("Input").getPinConnection();
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			sp = this.getPin("Feedback Input").getPinConnection();
			int fbInput = -1;
			if(sp != null) {
				fbInput = sp.getRegister();
			}
			
			// finally, generate the instructions
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("lap1a", ap1Length); 
			sfxb.FXallocDelayMem("lap1b", ap2Length); 
			sfxb.FXallocDelayMem("delay", delayLength); 
			if(this.getPin("Input").isConnected() == true) {
			dlOut = sfxb.allocateReg();
			lp1 = sfxb.allocateReg();
			hp1 = sfxb.allocateReg();
			if(this.getPin("Feedback Input").isConnected() == true) {
			sfxb.readRegister(fbInput, krt);
			}
			
			sfxb.readRegisterFilter(lp1, lpdf);
			sfxb.writeRegisterLowshelf(lp1, KRS);
			sfxb.FXreadDelay("lap1a#", 0, ap1kap);
			sfxb.FXwriteAllpass("lap1a", 0, -ap1kap);
			sfxb.FXreadDelay("lap1b#", 0, ap2kap);
			sfxb.FXwriteAllpass("lap1b", 0, -ap2kap);
			sfxb.readRegister(input, 1);
			sfxb.FXwriteDelay("delay", 0, 0.0);
			sfxb.FXreadDelay("delay#", 0, 1.0);
			sfxb.writeRegister(dlOut, 0);
			this.getPin("Delay Line Out").setRegister(dlOut);
			this.getPin("Low Pass Out").setRegister(lp1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setdelayLength(double __param) {
				delayLength = __param;	
			}
			
			public double getdelayLength() {
				return delayLength;
			}
			public void setkrt(double __param) {
				krt = __param;	
			}
			
			public double getkrt() {
				return krt;
			}
			public void setlpdf(double __param) {
				lpdf = __param;	
			}
			
			public double getlpdf() {
				return lpdf;
			}
			public void setap1Length(double __param) {
				ap1Length = __param;	
			}
			
			public double getap1Length() {
				return ap1Length;
			}
			public void setap1kap(double __param) {
				ap1kap = __param;	
			}
			
			public double getap1kap() {
				return ap1kap;
			}
			public void setap2Length(double __param) {
				ap2Length = __param;	
			}
			
			public double getap2Length() {
				return ap2Length;
			}
			public void setap2kap(double __param) {
				ap2kap = __param;	
			}
			
			public double getap2kap() {
				return ap2kap;
			}
		}	
