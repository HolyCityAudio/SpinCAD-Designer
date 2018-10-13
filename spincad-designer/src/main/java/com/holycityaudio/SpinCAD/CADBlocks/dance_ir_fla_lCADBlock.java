/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * dance_ir_fla_lCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.dance_ir_fla_lControlPanel;
		
	    @SuppressWarnings("unused")
	    public class dance_ir_fla_lCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private dance_ir_fla_lControlPanel cp = null;
			
			private int krt;
			private int kin;
			private int kmix;
			private int lp1al;
			private int lp1bl;
			private int lp1ar;
			private int lp1br;
			private int lp2al;
			private int lp2bl;
			private int lp2ar;
			private int lp2br;
			private int stop;
			private int pbyp;
			private int fol;
			private int forx;
			private int rol;
			private int ror;
			private int kfl;
			private int temp;
			private int rmixl;
			private int rmixr;
			private int lbyp;
			private int dacl;
			private int dacr;
			private double kap = 0.6;
			private double kql = -0.4;

			public dance_ir_fla_lCADBlock(int x, int y) {
				super(x, y);
				setName("Infinite_Rev_Flange");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_L");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Reverb_Time");
				addControlInputPin(this, "Flange");
				addControlInputPin(this, "Low_Pass");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new dance_ir_fla_lControlPanel(this);
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
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			sp = this.getPin("Reverb_Time").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			sp = this.getPin("Flange").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("Low_Pass").getPinConnection();
			int input2 = -1;
			if(sp != null) {
				input2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			krt = sfxb.allocateReg();
			kin = sfxb.allocateReg();
			kmix = sfxb.allocateReg();
			lp1al = sfxb.allocateReg();
			lp1bl = sfxb.allocateReg();
			lp1ar = sfxb.allocateReg();
			lp1br = sfxb.allocateReg();
			lp2al = sfxb.allocateReg();
			lp2bl = sfxb.allocateReg();
			lp2ar = sfxb.allocateReg();
			lp2br = sfxb.allocateReg();
			stop = sfxb.allocateReg();
			pbyp = sfxb.allocateReg();
			fol = sfxb.allocateReg();
			forx = sfxb.allocateReg();
			rol = sfxb.allocateReg();
			ror = sfxb.allocateReg();
			kfl = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			rmixl = sfxb.allocateReg();
			rmixr = sfxb.allocateReg();
			lbyp = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			sfxb.FXallocDelayMem("ap1", 202); 
			sfxb.FXallocDelayMem("ap2", 541); 
			sfxb.FXallocDelayMem("dap1a", 2204); 
			sfxb.FXallocDelayMem("dap1b", 2701); 
			sfxb.FXallocDelayMem("del1", 4456); 
			sfxb.FXallocDelayMem("dap2a", 2532); 
			sfxb.FXallocDelayMem("dap2b", 2201); 
			sfxb.FXallocDelayMem("del2", 6325); 
			sfxb.FXallocDelayMem("fdell", 512); 
			sfxb.FXallocDelayMem("fdelr", 512); 
			if(this.getPin("Input_L").isConnected() == true) {
			sfxb.skip(RUN, 1);
			sfxb.loadRampLFO((int) 0, (int) 0, (int) 512);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.readRegister(input0, 1.999);
			} else {
			sfxb.scaleOffset(0.0, 0.8);
			}
			
			sfxb.writeRegister(kmix, 0);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.readRegister(input0, -1);
			} else {
			sfxb.scaleOffset(0.0, 0.8);
			}
			
			sfxb.scaleOffset(1, 0.999);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(kin, 0);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.readRegister(input0, 1);
			} else {
			sfxb.scaleOffset(0.0, 0.8);
			}
			
			sfxb.writeRegister(krt, 1);
			sfxb.scaleOffset(1, -0.5);
			sfxb.skip(GEZ, 2);
			sfxb.scaleOffset(0, 0.5);
			sfxb.writeRegister(krt, 0);
			if(this.getPin("Low_Pass").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			} else {
			sfxb.scaleOffset(0.0, 0.5);
			}
			
			sfxb.scaleOffset(0.35, -0.35);
			sfxb.exp(1, 0);
			sfxb.writeRegister(kfl, 0);
			if(this.getPin("Low_Pass").isConnected() == true) {
			sfxb.readRegister(input2, 1);
			} else {
			sfxb.scaleOffset(0.0, 0.5);
			}
			
			sfxb.scaleOffset(1, -0.999);
			sfxb.exp(1, 0);
			sfxb.writeRegister(lbyp, 0);
			sfxb.readRegister(adcl, 0.5);
			sfxb.mulx(kin);
			sfxb.FXreadDelay("ap1#", 0, kap);
			sfxb.FXwriteAllpass("ap1", 0, -kap);
			sfxb.FXreadDelay("ap2#", 0, kap);
			sfxb.FXwriteAllpass("ap2", 0, -kap);
			sfxb.writeRegister(temp, 0);
			sfxb.FXreadDelay("del2#", 0, 1);
			sfxb.mulx(krt);
			sfxb.readRegister(temp, 1);
			sfxb.FXreadDelay("dap1a#", 0, kap);
			sfxb.FXwriteAllpass("dap1a", 0, -kap);
			sfxb.FXreadDelay("dap1b#", 0, kap);
			sfxb.FXwriteAllpass("dap1b", 0, -kap);
			sfxb.FXwriteDelay("del1", 0, 0);
			sfxb.FXreadDelay("del1#", 0, 1);
			sfxb.mulx(krt);
			sfxb.readRegister(temp, 1);
			sfxb.FXreadDelay("dap2a#", 0, kap);
			sfxb.FXwriteAllpass("dap2a", 0, -kap);
			sfxb.FXreadDelay("dap2b#", 0, kap);
			sfxb.FXwriteAllpass("dap2b", 0, -kap);
			sfxb.FXwriteDelay("del2", 0, 0);
			sfxb.readRegister(adcl, -1);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.FXreadDelay("del1", 0, 1);
			sfxb.mulx(input0);
			} else {
			sfxb.FXreadDelay("del1", 0, 0.5);
			}
			
			sfxb.readRegister(adcl, 1);
			sfxb.FXwriteDelay("fdell", 0, 0);
			sfxb.readRegister(adcl, -1);
			if(this.getPin("Reverb_Time").isConnected() == true) {
			sfxb.FXreadDelay("del2", 0, 1);
			sfxb.mulx(input0);
			} else {
			sfxb.FXreadDelay("del1", 0, 0.5);
			}
			
			sfxb.readRegister(adcl, 1);
			sfxb.FXwriteDelay("fdelr", 0, 0);
			sfxb.FXchorusReadDelay(RMP0, REG|COMPC, "fdell", 0);
			sfxb.FXchorusReadDelay(RMP0, 0, "fdell+", 1);
			sfxb.scaleOffset(0.707, 0);
			sfxb.FXreadDelay("fdell", 0, 0.707);
			sfxb.writeRegister(fol, 0);
			sfxb.FXchorusReadDelay(RMP0, COMPC, "fdelr", 0);
			sfxb.FXchorusReadDelay(RMP0, 0, "fdelr+", 1);
			sfxb.scaleOffset(0.707, 0);
			sfxb.FXreadDelay("fdelr", 0, 0.707);
			sfxb.writeRegister(forx, 0);
			sfxb.chorusReadValue(RMP0);
			if(this.getPin("Flange").isConnected() == true) {
			sfxb.readRegister(input1, -0.03);
			} else {
			sfxb.scaleOffset(0.0, -0.01);
			}
			
			sfxb.writeRegister(RMP0_RATE, 1);
			sfxb.readRegister(lp1al, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp1bl, 1);
			sfxb.writeRegister(lp1bl, -1);
			sfxb.readRegister(lp1al, kql);
			sfxb.readRegister(fol, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp1al, 1);
			sfxb.writeRegister(lp1al, 0);
			sfxb.readRegister(lp2al, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp2bl, 1);
			sfxb.writeRegister(lp2bl, -1);
			sfxb.readRegister(lp2al, kql);
			sfxb.readRegister(lp1bl, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp2al, 1);
			sfxb.writeRegister(lp2al, 0);
			sfxb.readRegister(lp1ar, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp1br, 1);
			sfxb.writeRegister(lp1br, -1);
			sfxb.readRegister(lp1ar, kql);
			sfxb.readRegister(forx, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp1ar, 1);
			sfxb.writeRegister(lp1ar, 0);
			sfxb.readRegister(lp2ar, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp2br, 1);
			sfxb.writeRegister(lp2br, -1);
			sfxb.readRegister(lp2ar, kql);
			sfxb.readRegister(lp1br, 1);
			sfxb.mulx(kfl);
			sfxb.readRegister(lp2ar, 1);
			sfxb.writeRegister(lp2ar, 0);
			sfxb.readRegister(lp2bl, -1);
			sfxb.readRegister(fol, 1);
			sfxb.mulx(lbyp);
			sfxb.readRegister(lp2bl, 1);
			sfxb.writeRegister(dacl, 0);
			sfxb.readRegister(lp2br, -1);
			sfxb.readRegister(forx, 1);
			sfxb.mulx(lbyp);
			sfxb.readRegister(lp2br, 1);
			sfxb.writeRegister(dacr, 0);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
