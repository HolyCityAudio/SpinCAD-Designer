/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * RatioCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.RatioControlPanel;
		
	    @SuppressWarnings("unused")
	    public class RatioCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private RatioControlPanel cp = null;
			
			private int fullRange;
			private int ratioOut;
			private double invRatio = 5;
			private double scale = 0.2;
			private double number1 = 1.0;
			private double offsetVal = 0.9;
			private double logOffset = 0.4;

			public RatioCADBlock(int x, int y) {
				super(x, y);
				setName("Ratio");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Input");
				addControlOutputPin(this, "FullRange");
				addControlOutputPin(this, "Ratio");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new RatioControlPanel(this);
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
			
			// finally, generate the instructions
			fullRange = sfxb.allocateReg();
			ratioOut = sfxb.allocateReg();
			double offsetVal = number1 / invRatio; 
			double scale = number1 - offsetVal;
			double logOffset = -Math.log(invRatio)/(16 * Math.log(2));
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, 1);
			sfxb.scaleOffset(scale, offsetVal);
			sfxb.writeRegister(fullRange, 1);
			sfxb.log(-1.0, logOffset);
			sfxb.exp(1.0, 0);
			sfxb.writeRegister(ratioOut, 0);
			this.getPin("FullRange").setRegister(fullRange);
			this.getPin("Ratio").setRegister(ratioOut);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setinvRatio(double __param) {
				invRatio = __param;	
			}
			
			public double getinvRatio() {
				return invRatio;
			}
		}	
