/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Peak_EnvelopeCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Peak_EnvelopeControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Peak_EnvelopeCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Peak_EnvelopeControlPanel cp = null;
			
			private double attackFreq = 0.00015;
			private double decayFreq = 0.00015;
			private int env;

			public Peak_EnvelopeCADBlock(int x, int y) {
				super(x, y);
				setName("Peak/Envelope");					
			setBorderColor(new Color(0x02f27f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Sensitivity");
				addControlOutputPin(this, "Envelope");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Peak_EnvelopeControlPanel(this);
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
			sp = this.getPin("Sensitivity").getPinConnection();
			int sens = -1;
			if(sp != null) {
				sens = sp.getRegister();
			}
			
			// finally, generate the instructions
			env = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, 1);
			sfxb.absa();
			if(this.getPin("Sensitivity").isConnected() == true) {
			sfxb.mulx(sens);
			}
			
			sfxb.readRegister(env, -1);
			sfxb.skip(NEG, 3);
			sfxb.readRegister(env, 1);
			sfxb.writeRegister(env, 0);
			sfxb.skip(ZRO, 3);
			sfxb.clear();
			sfxb.readRegisterFilter(env, decayFreq);
			sfxb.writeRegisterLowshelf(env, 0);
			this.getPin("Envelope").setRegister(env);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setattackFreq(double __param) {
				attackFreq = __param;	
			}
			
			public double getattackFreq() {
				return attackFreq;
			}
			public void setdecayFreq(double __param) {
				decayFreq = __param;	
			}
			
			public double getdecayFreq() {
				return decayFreq;
			}
		}	
