/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * pitchoffsetCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.pitchoffsetControlPanel;
		
	    @SuppressWarnings("unused")
	    public class pitchoffsetCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private pitchoffsetControlPanel cp = null;
			
			private int sigs;
			private int sigc;
			private int lpf;
			private int hpf;
			private int sigfil;
			private int sinosc;
			private int cososc;
			private int temp;
			private int shift;
			private int potfil;
			private int output1;

			public pitchoffsetCADBlock(int x, int y) {
				super(x, y);
				setName("PitchOffset");					
			setBorderColor(new Color(0x00fc82));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Pitch_Offset");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new pitchoffsetControlPanel(this);
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
			sp = this.getPin("Pitch_Offset").getPinConnection();
			int input0 = -1;
			if(sp != null) {
				input0 = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("ap1", 1); 
			sfxb.FXallocDelayMem("ap2", 1); 
			sfxb.FXallocDelayMem("ap3", 1); 
			sfxb.FXallocDelayMem("ap4", 1); 
			sfxb.FXallocDelayMem("ap5", 1); 
			sfxb.FXallocDelayMem("ap6", 1); 
			sigs = sfxb.allocateReg();
			sigc = sfxb.allocateReg();
			lpf = sfxb.allocateReg();
			hpf = sfxb.allocateReg();
			sigfil = sfxb.allocateReg();
			sinosc = sfxb.allocateReg();
			cososc = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			shift = sfxb.allocateReg();
			potfil = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			sfxb.skip(RUN, 2);
			sfxb.or(0x7fffff);
			sfxb.writeRegister(cososc, 0);
			sfxb.readRegister(input0, 0.1);
			sfxb.readRegisterFilter(potfil, 0.001);
			sfxb.writeRegisterLowshelf(potfil, -1);
			sfxb.scaleOffset(1, -0.05);
			sfxb.writeRegister(shift, 0);
			sfxb.readRegister(adcl, 1.0);
			sfxb.readRegisterFilter(hpf, 0.04);
			sfxb.writeRegisterHighshelf(hpf, -1);
			sfxb.readRegisterFilter(lpf, 0.4);
			sfxb.writeRegisterLowshelf(lpf, -1);
			sfxb.writeRegister(sigfil, 0.0039);
			sfxb.FXreadDelay("ap1+", (int)(1 * 1.0), 0.995);
			sfxb.FXwriteAllpass("ap1", 0, -0.995);
			sfxb.FXreadDelay("ap2+", (int)(1 * 1.0), 0.937);
			sfxb.FXwriteAllpass("ap2", 0, -0.937);
			sfxb.FXreadDelay("ap3+", (int)(1 * 1.0), 0.61);
			sfxb.FXwriteAllpass("ap3", 0, -0.61);
			sfxb.writeRegister(sigs, 0);
			sfxb.readRegister(sigfil, 0.0039);
			sfxb.FXreadDelay("ap4+", (int)(1 * 1.0), 0.978);
			sfxb.FXwriteAllpass("ap4", 0, -0.978);
			sfxb.FXreadDelay("ap5+", (int)(1 * 1.0), 0.84);
			sfxb.FXwriteAllpass("ap5", 0, -0.84);
			sfxb.FXreadDelay("ap6+", (int)(1 * 1.0), 0);
			sfxb.FXwriteAllpass("ap6", 0, -0);
			sfxb.writeRegister(sigc, 0);
			sfxb.readRegister(cososc, 1);
			sfxb.mulx(shift);
			sfxb.readRegister(sinosc, 1);
			sfxb.writeRegister(sinosc, -1);
			sfxb.mulx(shift);
			sfxb.readRegister(cososc, 1);
			sfxb.writeRegister(cososc, 0);
			sfxb.readRegister(sigc, -2);
			sfxb.mulx(cososc);
			sfxb.writeRegister(temp, 0);
			sfxb.readRegister(sigs, -2);
			sfxb.mulx(sinosc);
			sfxb.readRegister(temp, 1);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);

			}
			
			// create setters and getter for control panel variables
		}	
