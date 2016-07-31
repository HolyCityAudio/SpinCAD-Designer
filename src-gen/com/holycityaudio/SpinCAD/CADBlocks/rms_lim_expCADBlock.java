/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rms_lim_expCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.rms_lim_expControlPanel;
		
	    @SuppressWarnings("unused")
	    public class rms_lim_expCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private rms_lim_expControlPanel cp = null;
			
			private int sigin;
			private int avg;
			private int gain;
			private int expavg;
			private int output;

			public rms_lim_expCADBlock(int x, int y) {
				super(x, y);
				setName("RMS_Lim_Exp");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addOutputPin(this, "Audio_Output");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new rms_lim_expControlPanel(this);
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
			
			// finally, generate the instructions
			sigin = sfxb.allocateReg();
			avg = sfxb.allocateReg();
			gain = sfxb.allocateReg();
			expavg = sfxb.allocateReg();
			output = sfxb.allocateReg();
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.readRegister(adcl, 1.0);
			sfxb.writeRegister(sigin, 1);
			sfxb.mulx(sigin);
			sfxb.readRegisterFilter(avg, 0.001);
			sfxb.writeRegister(avg, 1);
			sfxb.log(-0.5, -0.125);
			sfxb.exp(1, 0);
			sfxb.writeRegister(gain, 0);
			sfxb.readRegister(sigin, 1);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.absa();
			sfxb.readRegisterFilter(expavg, 0.001);
			sfxb.writeRegister(expavg, 1);
			sfxb.log(0.5, 0);
			sfxb.exp(1, 0);
			sfxb.mulx(gain);
			sfxb.writeRegister(gain, 1);
			sfxb.mulx(adcl);
			sfxb.scaleOffset(1.5, 0);
			sfxb.writeRegister(output, 0);
			this.getPin("Audio_Output").setRegister(output);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
