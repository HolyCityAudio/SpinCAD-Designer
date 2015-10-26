/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChorusQuadCADBlock.java
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
		import com.holycityaudio.SpinCAD.SpinCADProgram;
 		import com.holycityaudio.SpinCAD.ControlPanel.ChorusQuadControlPanel;
		
		public class ChorusQuadCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private ChorusQuadControlPanel cp = null;
			
			private double rateMax = 511;
			private double widthMax = 16384;
			private double delayLength = 512;
			private double tap1Center = 0.25;
			private double tap2Center = 0.33;
			private double tap3Center = 0.63;
			private double tap4Center = 0.75;
			private double rate = 20;
			private double width = 64;
			private double delayOffset = -1;
			private int output1;
			private int output2;
			private int output3;
			private int output4;

			public ChorusQuadCADBlock(int x, int y) {
				super(x, y);
				setName("Chorus");					
			setBorderColor(new Color(0x24f2f2));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output_1");
				addOutputPin(this, "Output_2");
				addOutputPin(this, "Output_3");
				addOutputPin(this, "Output_4");
				addControlInputPin(this, "LFO_Rate");
				addControlInputPin(this, "LFO_Width");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new ChorusQuadControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinCADProgram sfxb) {
	
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
			if(this.getPin("Input").isConnected() == true) {
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("delayl", delayLength); 
			sfxb.skip(RUN, 1);
			sfxb.loadSinLFO((int) SIN0,(int) 50, (int) 64);
			if(this.getPin("LFO_Width").isConnected() == true) {
			double temp = width / widthMax;
			sfxb.readRegister(widthIn, temp);
			sfxb.writeRegister(SIN0_RANGE, 0);
			}
			
			if(this.getPin("LFO_Rate").isConnected() == true) {
			double temp1 = rate / rateMax;
			sfxb.readRegister(rateIn, temp1);
			sfxb.writeRegister(SIN0_RATE, 0);
			}
			
			sfxb.loadAccumulator(input);
			sfxb.FXwriteDelay("delayl", 0, 0);
			if(this.getPin("Output_1").isConnected() == true) {
			output1 = sfxb.allocateReg();
			{
				int chorusCenter = (int) (delayOffset + (0.5 * tap1Center * delayLength) +  0.25 * delayLength); 
				sfxb.chorusReadDelay(0, SIN|REG|COMPC, chorusCenter );
				sfxb.chorusReadDelay(0, SIN, chorusCenter + 1);
			}
			this.getPin("Output_1").setRegister(output1);
			}
			
			if(this.getPin("Output_2").isConnected() == true) {
			output2 = sfxb.allocateReg();
			{
				int chorusCenter = (int) (delayOffset + (0.5 * tap2Center * delayLength) +  0.25 * delayLength); 
				sfxb.chorusReadDelay(0, SIN|REG|COMPC, chorusCenter );
				sfxb.chorusReadDelay(0, SIN, chorusCenter + 1);
			}
			this.getPin("Output_2").setRegister(output2);
			}
			
			if(this.getPin("Output_3").isConnected() == true) {
			output3 = sfxb.allocateReg();
			{
				int chorusCenter = (int) (delayOffset + (0.5 * tap3Center * delayLength) +  0.25 * delayLength); 
				sfxb.chorusReadDelay(0, SIN|REG|COMPC, chorusCenter );
				sfxb.chorusReadDelay(0, SIN, chorusCenter + 1);
			}
			this.getPin("Output_3").setRegister(output3);
			}
			
			if(this.getPin("Output_4").isConnected() == true) {
			output4 = sfxb.allocateReg();
			{
				int chorusCenter = (int) (delayOffset + (0.5 * tap4Center * delayLength) +  0.25 * delayLength); 
				sfxb.chorusReadDelay(0, SIN|REG|COMPC, chorusCenter );
				sfxb.chorusReadDelay(0, SIN, chorusCenter + 1);
			}
			this.getPin("Output_4").setRegister(output4);
			}
			
			sfxb.writeRegister(output1, 0);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setdelayLength(double __param) {
				delayLength = __param;	
			}
			
			public double getdelayLength() {
				return delayLength;
			}
			public void settap1Center(double __param) {
				tap1Center = __param;	
			}
			
			public double gettap1Center() {
				return tap1Center;
			}
			public void settap2Center(double __param) {
				tap2Center = __param;	
			}
			
			public double gettap2Center() {
				return tap2Center;
			}
			public void settap3Center(double __param) {
				tap3Center = __param;	
			}
			
			public double gettap3Center() {
				return tap3Center;
			}
			public void settap4Center(double __param) {
				tap4Center = __param;	
			}
			
			public double gettap4Center() {
				return tap4Center;
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
		}	
