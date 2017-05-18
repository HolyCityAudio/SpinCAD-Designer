/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChorusCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ChorusControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ChorusCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ChorusControlPanel cp = null;
			
			private double rateMax = 511;
			private double number6554000 = 6554000.0;
			private int output1;
			private double delayLength = 512;
			private double tap1Center = 0.5;
			private double rate = 20;
			private double width = 30;
			private double lfoSel = 0;

			public ChorusCADBlock(int x, int y) {
				super(x, y);
				setName("Chorus");					
			setBorderColor(new Color(0x24f2f2));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addControlInputPin(this, "LFO_Rate");
				addControlInputPin(this, "LFO_Width");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ChorusControlPanel(this);
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
			sp = this.getPin("LFO_Rate").getPinConnection();
			int rateIn = -1;
			if(sp != null) {
				rateIn = sp.getRegister();
			}
			sp = this.getPin("LFO_Width").getPinConnection();
			int widthIn = -1;
			if(sp != null) {
				widthIn = sp.getRegister();
			}
			
			// finally, generate the instructions
			output1 = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("delayl", delayLength); 
			if(lfoSel == 0) {
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) 50, (int) 64);
			} else {
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN1,(int) 50, (int) 64);
			}
			
			if(this.getPin("LFO_Width").isConnected() == true) {
			double x1 = delayLength * width;
			double x3 = x1 / number6554000;
			sfxb.readRegister(widthIn, x3);
			if(lfoSel == 0) {
			sfxb.writeRegister(SIN0_RANGE, 0);
			} else {
			sfxb.writeRegister(SIN1_RANGE, 0);
			}
			
			}
			
			if(this.getPin("LFO_Rate").isConnected() == true) {
			double temp1 = rate / rateMax;
			sfxb.readRegister(rateIn, temp1);
			if(lfoSel == 0) {
			sfxb.writeRegister(SIN0_RATE, 0);
			} else {
			sfxb.writeRegister(SIN1_RATE, 0);
			}
			
			}
			
			sfxb.loadAccumulator(input);
			sfxb.FXwriteDelay("delayl", 0, 0);
			{
				// careful to not put center point too close to the end or beginning
				int chorusCenter = (int) (delayOffset + (0.9 * tap1Center * delayLength) +  0.05 * delayLength); 
			// need to allow 4 phases of LFO
			sfxb.chorusReadDelay((int)lfoSel, SIN|REG|COMPC, chorusCenter );
			sfxb.chorusReadDelay((int)lfoSel, SIN, chorusCenter + 1);
			}
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setdelayLength(double __param) {
				delayLength = __param;	
			}
			
			public double getdelayLength() {
				return delayLength;
			}
			public void setrate(double __param) {
				rate = __param;	
			}
			
			public double getrate() {
				return rate;
			}
			public void setwidth(double __param) {
				width = __param;	
			}
			
			public double getwidth() {
				return width;
			}
			public void setlfoSel(int __param) {
				lfoSel = (double) __param;	
			}
			
			public int getlfoSel() {
				return (int) lfoSel;
			}
		}	
