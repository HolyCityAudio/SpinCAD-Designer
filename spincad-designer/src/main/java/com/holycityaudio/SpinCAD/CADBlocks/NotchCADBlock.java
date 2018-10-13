/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * NotchCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.NotchControlPanel;
		
	    @SuppressWarnings("unused")
	    public class NotchCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private NotchControlPanel cp = null;
			
			private double sqrt2 = 1.4142135623730;
			private double f1scale = 0.43859416;
			private int lp_dly;
			private int bp_dly;
			private int f1;
			private int q1;
			private int hp;
			private int notch;
			private int p0fil;
			private int p1fil;

			public NotchCADBlock(int x, int y) {
				super(x, y);
				setName("Notch");					
			setBorderColor(new Color(0x24f26f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output_Notch");
				addOutputPin(this, "Output_Bandpass");
				addControlInputPin(this, "Freqeuncy");
				addControlInputPin(this, "Resonance");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new NotchControlPanel(this);
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
			sp = this.getPin("Freqeuncy").getPinConnection();
			int freq = -1;
			if(sp != null) {
				freq = sp.getRegister();
			}
			sp = this.getPin("Resonance").getPinConnection();
			int res = -1;
			if(sp != null) {
				res = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input").isConnected() == true) {
			lp_dly = sfxb.allocateReg();
			bp_dly = sfxb.allocateReg();
			f1 = sfxb.allocateReg();
			q1 = sfxb.allocateReg();
			hp = sfxb.allocateReg();
			notch = sfxb.allocateReg();
			p0fil = sfxb.allocateReg();
			p1fil = sfxb.allocateReg();
			sfxb.readRegister(freq, 1);
			sfxb.scaleOffset(0.2, 0);
			sfxb.readRegisterFilter(p0fil, 0.01);
			sfxb.writeRegister(p0fil, 1);
			sfxb.writeRegister(f1, 0);
			sfxb.readRegister(res, 1);
			sfxb.scaleOffset(1, -1);
			sfxb.absa();
			sfxb.readRegisterFilter(p1fil, 0.01);
			sfxb.writeRegister(p1fil, 1);
			sfxb.writeRegister(q1, 0);
			sfxb.loadAccumulator(bp_dly);
			sfxb.mulx(f1);
			sfxb.readRegister(lp_dly, 1.0);
			sfxb.writeRegister(lp_dly, -1.0);
			sfxb.readRegister(input, 1);
			sfxb.writeRegister(hp, 1);
			sfxb.loadAccumulator(bp_dly);
			sfxb.mulx(q1);
			sfxb.scaleOffset(sqrt2, 0);
			sfxb.scaleOffset(-sqrt2, 0);
			sfxb.readRegister(hp, 1);
			sfxb.writeRegister(hp, 1);
			sfxb.mulx(f1);
			sfxb.scaleOffset(f1scale, 0);
			sfxb.readRegister(bp_dly, 1);
			sfxb.writeRegister(bp_dly, 0);
			sfxb.loadAccumulator(lp_dly);
			sfxb.readRegister(hp, 1);
			sfxb.writeRegister(notch, 1);
			this.getPin("Output_Bandpass").setRegister(bp_dly);
			this.getPin("Output_Notch").setRegister(notch);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
