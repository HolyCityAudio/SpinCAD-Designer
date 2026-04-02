/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * peak_compressorCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.peak_compressorControlPanel;
		
	    @SuppressWarnings("unused")
	    public class peak_compressorCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private peak_compressorControlPanel cp = null;
			
			private int sigin;
			private int env;
			private int gain;
			private int output;
			private int gainOut;
			private double inGain = 1.0;
			private double attTime = 0.01;
			private double relTime = 0.001;
			private double ratio = 4.0;
			private double one = 1.0;
			private double threshDb = -25.0;
			private double negLogScale = -96.33;
			private double makeupDb = 0.0;
			private double logScale = 96.33;
			private double trim = 1.0;

			public peak_compressorCADBlock(int x, int y) {
				super(x, y);
				setName("Peak_Compressor");					
			setBorderColor(new Color(0x009595));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
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
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new peak_compressorControlPanel(this);
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
			sigin = sfxb.allocateReg();
			env = sfxb.allocateReg();
			gain = sfxb.allocateReg();
			output = sfxb.allocateReg();
			gainOut = sfxb.allocateReg();
			double invRatio = one / ratio; 
			double ratioFactor = invRatio - one;
			double negThreshAcc = threshDb / negLogScale; 
			double makeupAcc = makeupDb / logScale; 
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, inGain);
			sfxb.writeRegister(sigin, 0);
			sfxb.readRegister(sigin, 1);
			sfxb.absa();
			sfxb.readRegister(env, -1);
			sfxb.skip(NEG, 2);
			sfxb.scaleOffset(attTime, 0);
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(relTime, 0);
			sfxb.readRegister(env, 1);
			sfxb.writeRegister(env, 1);
			sfxb.log(1, 0);
			sfxb.scaleOffset(1, negThreshAcc);
			sfxb.skip(GEZ, 2);
			sfxb.scaleOffset(0, 0);
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(ratioFactor, 0);
			sfxb.scaleOffset(1, makeupAcc);
			sfxb.readRegisterFilter(gain, attTime);
			sfxb.writeRegister(gain, 1);
			sfxb.exp(1, 0);
			sfxb.mulx(sigin);
			sfxb.scaleOffset(trim, 0);
			sfxb.writeRegister(output, 0);
			sfxb.readRegister(gain, 1);
			sfxb.readRegisterFilter(gainOut, 0.001);
			sfxb.writeRegister(gainOut, 0);
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
			public void setattTime(double __param) {
				attTime = __param;	
			}
			
			public double getattTime() {
				return attTime;
			}
			public void setrelTime(double __param) {
				relTime = __param;	
			}
			
			public double getrelTime() {
				return relTime;
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
