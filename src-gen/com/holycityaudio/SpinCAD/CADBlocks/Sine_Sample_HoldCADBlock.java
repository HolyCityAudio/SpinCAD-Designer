/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Sine_Sample_HoldCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Sine_Sample_HoldControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Sine_Sample_HoldCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Sine_Sample_HoldControlPanel cp = null;
			
			private double rateMax = 511;
			private double number6554000 = 6554000.0;
			private int output1;
			private int output2;
			private int output3;
			private int output4;
			private int sinreg;
			private int cosreg;
			private int testpoint;
			private double rate = 50;
			private double lfoSel = 0;

			public Sine_Sample_HoldCADBlock(int x, int y) {
				super(x, y);
				setName("4-Phase Sample/Hold");					
			setBorderColor(new Color(0xe2fe24));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Source");
				addControlInputPin(this, "LFO Rate");
				addControlOutputPin(this, "S/H Output 1");
				addControlOutputPin(this, "S/H Output 2");
				addControlOutputPin(this, "S/H Output 3");
				addControlOutputPin(this, "S/H Output 4");
				addControlOutputPin(this, "Test Point");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Sine_Sample_HoldControlPanel(this);
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
			sp = this.getPin("Source").getPinConnection();
			int input1 = -1;
			if(sp != null) {
				input1 = sp.getRegister();
			}
			sp = this.getPin("LFO Rate").getPinConnection();
			int rateReg = -1;
			if(sp != null) {
				rateReg = sp.getRegister();
			}
			
			// finally, generate the instructions
			output1 = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			output3 = sfxb.allocateReg();
			output4 = sfxb.allocateReg();
			sinreg = sfxb.allocateReg();
			cosreg = sfxb.allocateReg();
			testpoint = sfxb.allocateReg();
			if(lfoSel == 0) {
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) rate, (int) 32000);
			} else {
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN1,(int) rate, (int) 32000);
			}
			
			if(this.getPin("LFO Rate").isConnected() == true) {
			double temp1 = rate / rateMax;
			sfxb.readRegister(rateReg, temp1);
			if(lfoSel == 0) {
			sfxb.writeRegister(SIN0_RATE, 0);
			} else {
			sfxb.writeRegister(SIN1_RATE, 0);
			}
			
			}
			
			if(lfoSel == 0) {
			sfxb.loadAccumulator(sinreg);
			sfxb.chorusReadValue(SIN0);
			sfxb.skip(ZRC, 3);
			sfxb.writeRegister(testpoint, 1);
			sfxb.writeRegister(sinreg, 0);
			sfxb.skip(ZRO, 9);
			sfxb.skip(NEG, 4);
			sfxb.writeRegister(sinreg, 0);
			sfxb.loadAccumulator(input1);
			sfxb.writeRegister(output1, 0);
			sfxb.skip(ZRO, 17);
			sfxb.writeRegister(sinreg, 0);
			sfxb.loadAccumulator(input1);
			sfxb.writeRegister(output3, 0);
			sfxb.skip(ZRO, 13);
			sfxb.loadAccumulator(cosreg);
			sfxb.chorusReadValue(8);
			sfxb.skip(ZRC, 2);
			sfxb.writeRegister(cosreg, 0);
			sfxb.skip(ZRO, 8);
			sfxb.skip(NEG, 4);
			sfxb.writeRegister(cosreg, 0);
			sfxb.loadAccumulator(input1);
			sfxb.writeRegister(output2, 0);
			sfxb.skip(ZRO, 3);
			sfxb.writeRegister(cosreg, 0);
			sfxb.loadAccumulator(input1);
			sfxb.writeRegister(output4, 0);
			}
			
			this.getPin("S/H Output 1").setRegister(output1);
			this.getPin("S/H Output 2").setRegister(output2);
			this.getPin("S/H Output 3").setRegister(output3);
			this.getPin("S/H Output 4").setRegister(output4);
			this.getPin("Test Point").setRegister(testpoint);

			}
			
			// create setters and getter for control panel variables
			public void setrate(double __param) {
				rate = __param;	
			}
			
			public double getrate() {
				return rate;
			}
			public void setlfoSel(int __param) {
				lfoSel = (double) __param;	
			}
			
			public int getlfoSel() {
				return (int) lfoSel;
			}
		}	
