/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChirpCADBlock.java
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
	
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
 		import com.holycityaudio.SpinCAD.ControlPanel.ChirpControlPanel;
		
		public class ChirpCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private ChirpControlPanel cp = null;
			
			private double gain = 0.5;
			private double nAPs = 4;
			private double stretch = 4;
			private double kiap = 0.5;
			private int output1;

			public ChirpCADBlock(int x, int y) {
				super(x, y);
				setName("Chirp");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ChirpControlPanel(this);
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
			
			// finally, generate the instructions
			output1 = sfxb.allocateReg();
			if(nAPs > 3) {
			sfxb.FXallocDelayMem("iap1", stretch); 
			}
			
			if(nAPs > 2) {
			sfxb.FXallocDelayMem("iap2", stretch); 
			}
			
			if(nAPs > 1) {
			sfxb.FXallocDelayMem("iap3", stretch); 
			}
			
			sfxb.FXallocDelayMem("iap4", stretch); 
			sfxb.readRegister(input, gain);
			if(nAPs > 3) {
			sfxb.FXreadDelay("iap1#", 0, kiap);
			sfxb.FXwriteAllpass("iap1", 0, -kiap);
			}
			
			if(nAPs > 2) {
			sfxb.FXreadDelay("iap2#", 0, kiap);
			sfxb.FXwriteAllpass("iap2", 0, -kiap);
			}
			
			if(nAPs > 1) {
			sfxb.FXreadDelay("iap3#", 0, kiap);
			sfxb.FXwriteAllpass("iap3", 0, -kiap);
			}
			
			sfxb.FXreadDelay("iap4#", 0, kiap);
			sfxb.FXwriteAllpass("iap4", 0, -kiap);
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setnAPs(double __param) {
				nAPs = __param;	
			}
			
			public double getnAPs() {
				return nAPs;
			}
			public void setstretch(double __param) {
				stretch = __param;	
			}
			
			public double getstretch() {
				return stretch;
			}
			public void setkiap(double __param) {
				kiap = __param;	
			}
			
			public double getkiap() {
				return kiap;
			}
		}	
