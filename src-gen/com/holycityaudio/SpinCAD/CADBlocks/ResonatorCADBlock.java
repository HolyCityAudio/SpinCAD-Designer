/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ResonatorCADBlock.java
 * Copyright (C) 2013 - 2026 - Gary Worsham 
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ResonatorControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ResonatorCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ResonatorControlPanel cp = null;
			
			private double freq = 0.2;
			private double reso = 0.01;
			private int output1;
			private int bp;
			private int temp;

			public ResonatorCADBlock(int x, int y) {
				super(x, y);
				setName("Resonator");					
			setBorderColor(new Color(0x24f224));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addControlInputPin(this, "Frequency");
				addControlInputPin(this, "Resonance");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ResonatorControlPanel(this);
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
			int freqIn = -1;
			if(sp != null) {
				freqIn = sp.getRegister();
			}
			sp = this.getPin("Resonance").getPinConnection();
			int resoIn = -1;
			if(sp != null) {
				resoIn = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input").isConnected() == true) {
			output1 = sfxb.allocateReg();
			bp = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			sfxb.loadAccumulator(output1);
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.mulx(freqIn);
			} else {
			sfxb.scaleOffset(freq, 0);
			}
			
			sfxb.readRegister(bp, 1.0);
			sfxb.writeRegister(bp, -1.0);
			sfxb.readRegister(input, 1.0);
			sfxb.writeRegister(temp, 0);
			sfxb.loadAccumulator(output1);
			if(this.getPin("Resonance").isConnected() == true) {
			sfxb.mulx(resoIn);
			} else {
			sfxb.scaleOffset(reso, 0);
			}
			
			sfxb.scaleOffset(-1.0, 0);
			sfxb.readRegister(temp, 1.0);
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.mulx(freqIn);
			} else {
			sfxb.scaleOffset(freq, 0);
			}
			
			sfxb.readRegister(output1, 1.0);
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setfreq(double __param) {
				freq = __param;	
			}
			
			public double getfreq() {
				return freq;
			}
			public void setreso(double __param) {
				reso = __param;	
			}
			
			public double getreso() {
				return reso;
			}
		}	
