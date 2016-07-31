/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * PluckCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.PluckControlPanel;
		
	    @SuppressWarnings("unused")
	    public class PluckCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private PluckControlPanel cp = null;
			
			private double threshold = 0.015;
			private double pulseLevel = -0.9;
			private double pulseWidth = 3277;
			private int retrigger;
			private int pulsecount;
			private int pulsevalue;
			private int output;
			private double pulse_width = 155;
			private double retrigger_width = 1655;
			private double filt = 0.0018;
			private double pulseHeight = -0.55;
			private double vcffreq = 0.45;

			public PluckCADBlock(int x, int y) {
				super(x, y);
				setName("Pluck");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlOutputPin(this, "Pluck Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new PluckControlPanel(this);
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
			if(this.getPin("Input").isConnected() == true) {
			retrigger = sfxb.allocateReg();
			pulsecount = sfxb.allocateReg();
			pulsevalue = sfxb.allocateReg();
			output = sfxb.allocateReg();
			sfxb.readRegister(retrigger, -1.0);
			sfxb.skip(NEG, 11);
			sfxb.clear();
			sfxb.readRegister(input, 1.0);
			sfxb.absa();
			sfxb.scaleOffset(1.0, -threshold);
			sfxb.skip(NEG, 17);
			sfxb.clear();
			sfxb.or(0x1);
			sfxb.writeRegister(pulsecount, 1);
			sfxb.writeRegister(retrigger, 0);
			sfxb.scaleOffset(0, pulseHeight);
			sfxb.writeRegister(pulsevalue, 0);
			sfxb.clear();
			sfxb.or(0x1);
			sfxb.readRegister(retrigger, 1.0);
			sfxb.writeRegister(retrigger, 1.0);
			sfxb.skip(ZRO, 2);
			sfxb.loadAccumulator(retrigger);
			sfxb.skip(RUN, 2);
			sfxb.writeRegister(retrigger, 0);
			sfxb.skip(RUN, 2);
			sfxb.readRegister(pulsevalue, 1.0);
			sfxb.skip(RUN, 2);
			sfxb.readRegisterFilter(pulsevalue, filt);
			sfxb.writeRegisterLowshelf(pulsevalue, -1.0);
			sfxb.writeRegister(output, 0.0);
			this.getPin("Pluck Output").setRegister(output);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setthreshold(double __param) {
				threshold = __param;	
			}
			
			public double getthreshold() {
				return threshold;
			}
			public void setpulseLevel(double __param) {
				pulseLevel = __param;	
			}
			
			public double getpulseLevel() {
				return pulseLevel;
			}
			public void setpulseWidth(double __param) {
				pulseWidth = __param;	
			}
			
			public double getpulseWidth() {
				return pulseWidth;
			}
		}	
