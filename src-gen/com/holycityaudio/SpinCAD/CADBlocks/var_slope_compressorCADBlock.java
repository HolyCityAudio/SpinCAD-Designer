/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * var_slope_compressorCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.var_slope_compressorControlPanel;
		
	    @SuppressWarnings("unused")
	    public class var_slope_compressorCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private var_slope_compressorControlPanel cp = null;
			
			private int sigin;
			private int avg;
			private int gain;
			private int output;
			private int gainOut;
			private double inGain = 1.0;
			private double avgTime = 0.001;
			private double knee = 0.0625;
			private double ratio = 2.0;
			private double negSixteenth = -0.0625;
			private double thresh = 0.25;
			private double negOne = -1.0;
			private double makeupGain = 1.5;

			public var_slope_compressorCADBlock(int x, int y) {
				super(x, y);
				setName("Var_Slope_Compressor");					
			setBorderColor(new Color(0x009595));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Threshold");
				addControlInputPin(this, "Ratio");
				addControlInputPin(this, "Attack");
				addOutputPin(this, "Audio_Output");
				addControlOutputPin(this, "Gain Reduction");
			// if any control panel elements declared, set hasControlPanel to true
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
						cp = new var_slope_compressorControlPanel(this);
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
			sp = this.getPin("Threshold").getPinConnection();
			int threshIn = -1;
			if(sp != null) {
				threshIn = sp.getRegister();
			}
			sp = this.getPin("Ratio").getPinConnection();
			int ratioIn = -1;
			if(sp != null) {
				ratioIn = sp.getRegister();
			}
			sp = this.getPin("Attack").getPinConnection();
			int attackIn = -1;
			if(sp != null) {
				attackIn = sp.getRegister();
			}
			
			// finally, generate the instructions
			sigin = sfxb.allocateReg();
			avg = sfxb.allocateReg();
			gain = sfxb.allocateReg();
			output = sfxb.allocateReg();
			gainOut = sfxb.allocateReg();
			double slope = ratio * negSixteenth;
			double negThresh = negOne * thresh;
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, inGain);
			sfxb.writeRegister(sigin, 1);
			sfxb.mulx(sigin);
			if(this.getPin("Attack").isConnected() == true) {
			sfxb.readRegister(avg, -1);
			sfxb.mulx(attackIn);
			sfxb.readRegister(avg, 1);
			} else {
			sfxb.readRegisterFilter(avg, avgTime);
			}
			
			sfxb.writeRegister(avg, 0);
			if(this.getPin("Threshold").isConnected() == true) {
			sfxb.readRegister(threshIn, knee);
			} else {
			sfxb.scaleOffset(0, knee);
			}
			
			sfxb.readRegister(avg, 1);
			sfxb.log(slope, negThresh);
			sfxb.exp(1, 0);
			sfxb.writeRegister(gain, 0);
			if(this.getPin("Ratio").isConnected() == true) {
			sfxb.readRegister(gain, 1);
			sfxb.scaleOffset(-1, 0.999);
			sfxb.mulx(ratioIn);
			sfxb.scaleOffset(-1, 0.999);
			sfxb.writeRegister(gain, 0);
			}
			
			sfxb.readRegister(gain, 1);
			sfxb.writeRegister(gainOut, 0);
			sfxb.readRegister(gain, 1);
			sfxb.mulx(input);
			sfxb.scaleOffset(makeupGain, 0);
			sfxb.writeRegister(output, 0);
			this.getPin("Audio_Output").setRegister(output);
			this.getPin("Gain Reduction").setRegister(gainOut);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setinGain(double __param) {
				inGain = __param;	
			}
			
			public double getinGain() {
				return inGain;
			}
			public void setavgTime(double __param) {
				avgTime = __param;	
			}
			
			public double getavgTime() {
				return avgTime;
			}
			public void setknee(double __param) {
				knee = __param;	
			}
			
			public double getknee() {
				return knee;
			}
			public void setratio(double __param) {
				ratio = __param;	
			}
			
			public double getratio() {
				return ratio;
			}
			public void setthresh(double __param) {
				thresh = __param;	
			}
			
			public double getthresh() {
				return thresh;
			}
			public void setmakeupGain(double __param) {
				makeupGain = __param;	
			}
			
			public double getmakeupGain() {
				return makeupGain;
			}
		}	
