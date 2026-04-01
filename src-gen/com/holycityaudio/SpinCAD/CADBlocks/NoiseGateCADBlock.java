/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * NoiseGateCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.NoiseGateControlPanel;
		
	    @SuppressWarnings("unused")
	    public class NoiseGateCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private NoiseGateControlPanel cp = null;
			
			private int output;
			private int gate;
			private double thresh = 0.02;

			public NoiseGateCADBlock(int x, int y) {
				super(x, y);
				setName("Noise Gate");					
			setBorderColor(new Color(0x009595));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Audio In");
				addOutputPin(this, "Audio Out");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new NoiseGateControlPanel(this);
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
			sp = this.getPin("Audio In").getPinConnection();
			int input = -1;
			if(sp != null) {
				input = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			gate = sfxb.allocateReg();
			if(this.getPin("Audio In").isConnected() == true) {
			sfxb.readRegister(input, thresh);
			sfxb.absa();
			sfxb.readRegister(gate, 0.9998);
			sfxb.writeRegister(gate, 1.999);
			sfxb.mulx(input);
			sfxb.writeRegister(output, 0);
			this.getPin("Audio Out").setRegister(output);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setthresh(double __param) {
				thresh = __param;	
			}
			
			public double getthresh() {
				return thresh;
			}
		}	
