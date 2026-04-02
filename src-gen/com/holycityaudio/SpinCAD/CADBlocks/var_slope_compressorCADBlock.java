/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * var_slope_compressorCADBlock.java
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
			private double avgTime = 0.013333;
			private double relTime = 0.000447;
			private double knee = 0.0625;
			private double ratio = 4.0;
			private double one = 1.0;
			private double negHalf = -0.5;
			private double threshDb = -25.0;
			private double hundred = 100.0;
			private double threshScale = -0.125;
			private double makeupDb = 0.0;
			private double trim = 1.0;

			public var_slope_compressorCADBlock(int x, int y) {
				super(x, y);
				setName("Var_Slope_Compressor");					
			setBorderColor(new Color(0x009595));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Threshold");
				addControlInputPin(this, "Ratio");
				addOutputPin(this, "Audio_Output");
				addControlOutputPin(this, "Gain Reduction");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
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
			
			// finally, generate the instructions
			sigin = sfxb.allocateReg();
			avg = sfxb.allocateReg();
			gain = sfxb.allocateReg();
			output = sfxb.allocateReg();
			gainOut = sfxb.allocateReg();
			double invRatio = one / ratio; 
			double oneMinusInv = one - invRatio;
			double slope = negHalf * oneMinusInv;
			double thresh = threshDb / hundred; 
			double makeupZ = makeupDb / hundred; 
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, inGain);
			sfxb.writeRegister(sigin, 1);
			sfxb.mulx(sigin);
			sfxb.readRegister(avg, -1);
			sfxb.skip(NEG, 2);
			sfxb.scaleOffset(avgTime, 0);
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(relTime, 0);
			sfxb.readRegister(avg, 1);
			sfxb.writeRegister(avg, 0);
			sfxb.scaleOffset(0, knee);
			sfxb.readRegister(avg, 1);
			sfxb.log(slope, thresh);
			sfxb.scaleOffset(1, makeupZ);
			if(this.getPin("Threshold").isConnected() == true) {
			sfxb.readRegister(threshIn, threshScale);
			}
			
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
			sfxb.readRegisterFilter(gainOut, 0.001);
			sfxb.writeRegister(gainOut, 0);
			sfxb.readRegister(gain, 1);
			sfxb.mulx(input);
			sfxb.scaleOffset(trim, 0);
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
			public void setrelTime(double __param) {
				relTime = __param;	
			}
			
			public double getrelTime() {
				return relTime;
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
			public void setthreshDb(double __param) {
				threshDb = __param;	
			}
			
			public double getthreshDb() {
				return threshDb;
			}
			public void setmakeupDb(double __param) {
				makeupDb = __param;	
			}
			
			public double getmakeupDb() {
				return makeupDb;
			}
			public void settrim(double __param) {
				trim = __param;	
			}
			
			public double gettrim() {
				return trim;
			}
		}	
