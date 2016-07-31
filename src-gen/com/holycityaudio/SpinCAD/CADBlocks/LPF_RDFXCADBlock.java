/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * LPF_RDFXCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.LPF_RDFXControlPanel;
		
	    @SuppressWarnings("unused")
	    public class LPF_RDFXCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private LPF_RDFXControlPanel cp = null;
			
			private double freq = 0.15;
			private int output;
			private int lpf1;

			public LPF_RDFXCADBlock(int x, int y) {
				super(x, y);
				setName("LPF 1P");					
			setBorderColor(new Color(0x24f26f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Frequency");
				addOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new LPF_RDFXControlPanel(this);
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
			sp = this.getPin("Frequency").getPinConnection();
			int freqControl = -1;
			if(sp != null) {
				freqControl = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			lpf1 = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.readRegister(input, freq);
			sfxb.readRegister(lpf1, -freq);
			sfxb.mulx(freqControl);
			sfxb.readRegister(lpf1, 1.0);
			} else {
			sfxb.readRegister(input, 1.0);
			sfxb.readRegisterFilter(lpf1, freq);
			}
			
			sfxb.writeRegister(lpf1, 0);
			this.getPin("Output").setRegister(lpf1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setfreq(double __param) {
				freq = __param;	
			}
			
			public double getfreq() {
				return freq;
			}
		}	
