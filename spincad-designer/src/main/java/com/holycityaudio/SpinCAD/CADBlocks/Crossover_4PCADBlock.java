/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Crossover_4PCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Crossover_4PControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Crossover_4PCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Crossover_4PControlPanel cp = null;
			
			private double freq = 0.15;
			private int output;
			private int flt_SV1;
			private int flt_SV2;
			private int flt_SV3;
			private int lo_sig;
			private int hi_sig;

			public Crossover_4PCADBlock(int x, int y) {
				super(x, y);
				setName("Crossover 4P");					
			setBorderColor(new Color(0x24f26f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Frequency");
				addOutputPin(this, "Low Output");
				addOutputPin(this, "High Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Crossover_4PControlPanel(this);
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
			flt_SV1 = sfxb.allocateReg();
			flt_SV2 = sfxb.allocateReg();
			flt_SV3 = sfxb.allocateReg();
			lo_sig = sfxb.allocateReg();
			hi_sig = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, 0.25);
			sfxb.readRegister(flt_SV1, -2.00);
			sfxb.readRegister(flt_SV2, -2.00);
			sfxb.readRegister(flt_SV3, -1.00);
			sfxb.readRegister(lo_sig, -0.25);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(hi_sig, 0.5);
			sfxb.mulx(freqControl);
			sfxb.readRegister(flt_SV1, 1);
			sfxb.writeRegister(flt_SV1, 1);
			sfxb.mulx(freqControl);
			sfxb.readRegister(flt_SV2, 1);
			sfxb.writeRegister(flt_SV2, 1);
			sfxb.mulx(freqControl);
			sfxb.readRegister(flt_SV3, 1);
			sfxb.writeRegister(flt_SV3, 1);
			sfxb.mulx(freqControl);
			sfxb.readRegister(lo_sig, 1);
			sfxb.writeRegister(lo_sig, 0);
			this.getPin("Low Output").setRegister(lo_sig);
			this.getPin("High Output").setRegister(hi_sig);
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
