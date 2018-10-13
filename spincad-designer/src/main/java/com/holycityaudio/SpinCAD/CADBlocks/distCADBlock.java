/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * distCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.distControlPanel;
		
	    @SuppressWarnings("unused")
	    public class distCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private distControlPanel cp = null;
			
			private int mono;
			private int abs;
			private int sign;
			private int output;

			public distCADBlock(int x, int y) {
				super(x, y);
				setName("Distortion");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Audio_Input");
				addOutputPin(this, "Audio_Output");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new distControlPanel(this);
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
			sp = this.getPin("Audio_Input").getPinConnection();
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			
			// finally, generate the instructions
			mono = sfxb.allocateReg();
			abs = sfxb.allocateReg();
			sign = sfxb.allocateReg();
			output = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.scaleOffset(0, -1);
			sfxb.writeRegister(sign, 0);
			sfxb.readRegister(adcl, 1.0);
			sfxb.writeRegister(mono, 1);
			sfxb.skip(NEG, 2);
			sfxb.scaleOffset(0, 0.999);
			sfxb.writeRegister(sign, 0);
			sfxb.loadAccumulator(mono);
			sfxb.absa();
			sfxb.writeRegister(abs, 1);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 5);
			sfxb.loadAccumulator(abs);
			sfxb.log(-1, -0.375);
			sfxb.exp(1, 0);
			sfxb.scaleOffset(-1, 0.25);
			sfxb.skip(RUN, 1);
			sfxb.scaleOffset(1, 0.125);
			sfxb.mulx(sign);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.writeRegister(output, 0);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
