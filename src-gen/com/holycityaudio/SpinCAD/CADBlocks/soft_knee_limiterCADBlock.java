/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * soft_knee_limiterCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.soft_knee_limiterControlPanel;
		
	    @SuppressWarnings("unused")
	    public class soft_knee_limiterCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private soft_knee_limiterControlPanel cp = null;
			
			private int sigin;
			private int avg;
			private int gain;
			private int dacl;
			private int dacr;

			public soft_knee_limiterCADBlock(int x, int y) {
				super(x, y);
				setName("Soft_Knee_Limiter");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Audio_Output");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new soft_knee_limiterControlPanel(this);
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
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			
			// finally, generate the instructions
			sigin = sfxb.allocateReg();
			avg = sfxb.allocateReg();
			gain = sfxb.allocateReg();
			dacl = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(adcl, 1.0);
			sfxb.writeRegister(sigin, 1);
			sfxb.mulx(sigin);
			sfxb.readRegisterFilter(avg, 0.001);
			sfxb.writeRegister(avg, 0);
			sfxb.scaleOffset(0, 0.125);
			sfxb.readRegister(avg, 1);
			sfxb.log(-0.4, -0.25);
			sfxb.exp(1, 0);
			sfxb.writeRegister(gain, 1);
			sfxb.mulx(adcl);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(1.5, 0);
			sfxb.writeRegister(dacl, 0);
			this.getPin("Audio_Output").setRegister(dacl);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
