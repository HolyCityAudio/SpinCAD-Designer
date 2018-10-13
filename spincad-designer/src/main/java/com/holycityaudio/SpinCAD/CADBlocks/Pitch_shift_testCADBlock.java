/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Pitch_shift_testCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.Pitch_shift_testControlPanel;
		
	    @SuppressWarnings("unused")
	    public class Pitch_shift_testCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private Pitch_shift_testControlPanel cp = null;
			
			private double pitchCoeff = 8192;
			private double controlRange = 0;
			private double lfoSel = 0;
			private double lfoWidth = 0;
			private double length = 1;
			private int pitch;
			private double sixteen = 32768;
			private double two = 2.0;

			public Pitch_shift_testCADBlock(int x, int y) {
				super(x, y);
				setName("Pitch Shift");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Pitch Out");
				addControlInputPin(this, "Pitch Control");
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
						cp = new Pitch_shift_testControlPanel(this);
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
			sp = this.getPin("Pitch Control").getPinConnection();
			int pitchControl = -1;
			if(sp != null) {
				pitchControl = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(lfoWidth == 0) {
			length = 4096;
			}
			
			if(lfoWidth == 1) {
			length = 2048;
			}
			
			if(lfoWidth == 2) {
			length = 1024;
			}
			
			if(lfoWidth == 3) {
			length = 512;
			}
			
			if(this.getPin("Input").isConnected() == true) {
			pitch = sfxb.allocateReg();
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("delayd", length); 
			sfxb.FXallocDelayMem("temp", 1); 
			double scaledPitch = pitchCoeff / sixteen;
			if(lfoSel == 0) {
			sfxb.skip(RUN, 1);
			sfxb.loadRampLFO((int) 0, (int) pitchCoeff, (int) length);
			} else {
			sfxb.skip(RUN, 1);
			sfxb.loadRampLFO((int) 1, (int) pitchCoeff, (int) length);
			}
			
			sfxb.loadAccumulator(input);
			sfxb.FXwriteDelay("delayd", 0, 0);
			double halfPitch = scaledPitch / two;
			if(this.getPin("Pitch Control").isConnected() == true) {
			if(controlRange == 1) {
			sfxb.readRegister(pitchControl, 1.0);
			sfxb.scaleOffset(1.0, -0.5);
			sfxb.skip(GEZ, 1);
			sfxb.scaleOffset(0.5, 0);
			} else {
			sfxb.readRegister(pitchControl, scaledPitch);
			}
			
			if(lfoSel == 0) {
			sfxb.writeRegister(RMP0_RATE, 0);
			}
			
			if(lfoSel == 1) {
			sfxb.writeRegister(RMP1_RATE, 0);
			}
			
			}
			
			if(lfoSel == 0) {
			sfxb.FXchorusReadDelay(RMP0, REG|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP0, 0, "delayd+", 1);
			sfxb.FXwriteDelay("temp", 0, 0);
			sfxb.FXchorusReadDelay(RMP0, RPTR2|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP0, RPTR2, "delayd+", 1);
			sfxb.chorusScaleOffset(RMP0, NA|COMPC, 0);
			sfxb.FXchorusReadDelay(RMP0, NA, "temp", 0);
			} else {
			sfxb.FXchorusReadDelay(RMP1, REG|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP1, 0, "delayd+", 1);
			sfxb.FXwriteDelay("temp", 0, 0);
			sfxb.FXchorusReadDelay(RMP1, RPTR2|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP1, RPTR2, "delayd+", 1);
			sfxb.chorusScaleOffset(RMP1, NA|COMPC, 0);
			sfxb.FXchorusReadDelay(RMP1, NA, "temp", 0);
			}
			
			sfxb.writeRegister(pitch, 0);
			this.getPin("Pitch Out").setRegister(pitch);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setpitchCoeff(double __param) {
				pitchCoeff = __param;	
			}
			
			public double getpitchCoeff() {
				return pitchCoeff;
			}
			public void setcontrolRange(int __param) {
				controlRange = (double) __param;	
			}
			
			public int getcontrolRange() {
				return (int) controlRange;
			}
			public void setlfoSel(int __param) {
				lfoSel = (double) __param;	
			}
			
			public int getlfoSel() {
				return (int) lfoSel;
			}
			public void setlfoWidth(int __param) {
				lfoWidth = (double) __param;	
			}
			
			public int getlfoWidth() {
				return (int) lfoWidth;
			}
		}	
