/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ToverXCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ToverXControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ToverXCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ToverXControlPanel cp = null;
			
			private int tovrx;
			private int lf1;
			private double threshold = -0.1875;
			private double filterFactor = 0.75;
			private double nStages = 1;
			private int lf2;
			private int lf3;
			private int lf4;

			public ToverXCADBlock(int x, int y) {
				super(x, y);
				setName("ToverX");					
			setBorderColor(new Color(0xff0000));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Audio_Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ToverXControlPanel(this);
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
			tovrx = sfxb.allocateReg();
			lf1 = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(adcl, 1);
			sfxb.log(-1, threshold);
			sfxb.exp(1, 0);
			sfxb.writeRegister(tovrx, 1);
			sfxb.mulx(tovrx);
			sfxb.readRegister(tovrx, -2);
			sfxb.mulx(adcl);
			if(nStages == 1) {
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(lf1, 0);
			} else {
			sfxb.readRegister(lf1, filterFactor);
			sfxb.writeRegister(lf1, 1);
			}
			
			if(nStages > 1) {
			lf2 = sfxb.allocateReg();
			sfxb.log(-1, threshold);
			sfxb.exp(1, 0);
			sfxb.writeRegister(tovrx, 1);
			sfxb.mulx(tovrx);
			sfxb.readRegister(tovrx, -2);
			sfxb.mulx(lf1);
			}
			
			if(nStages == 2) {
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(lf2, 0);
			} else {
			if(nStages > 2) {
			sfxb.readRegister(lf2, filterFactor);
			sfxb.writeRegister(lf2, 1);
			}
			
			}
			
			if(nStages > 2) {
			lf3 = sfxb.allocateReg();
			sfxb.log(-1, threshold);
			sfxb.exp(1, 0);
			sfxb.writeRegister(tovrx, 1);
			sfxb.mulx(tovrx);
			sfxb.readRegister(tovrx, -2);
			sfxb.mulx(lf2);
			}
			
			if(nStages == 3) {
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(lf3, 0);
			} else {
			if(nStages == 4) {
			sfxb.readRegister(lf3, filterFactor);
			sfxb.writeRegister(lf3, 1);
			}
			
			}
			
			if(nStages > 3) {
			lf4 = sfxb.allocateReg();
			sfxb.log(-1, threshold);
			sfxb.exp(1, 0);
			sfxb.writeRegister(tovrx, 1);
			sfxb.mulx(tovrx);
			sfxb.readRegister(tovrx, -2);
			sfxb.mulx(lf3);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(1.999, 0);
			sfxb.writeRegister(lf4, 0);
			}
			
			if(nStages == 1) {
			this.getPin("Audio_Output").setRegister(lf1);
			}
			
			if(nStages == 2) {
			this.getPin("Audio_Output").setRegister(lf2);
			}
			
			if(nStages == 3) {
			this.getPin("Audio_Output").setRegister(lf3);
			}
			
			if(nStages == 4) {
			this.getPin("Audio_Output").setRegister(lf4);
			}
			
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setnStages(double __param) {
				nStages = __param;	
			}
			
			public double getnStages() {
				return nStages;
			}
		}	
