/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * pitch_fourCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.pitch_fourControlPanel;
		
	    @SuppressWarnings("unused")
	    public class pitch_fourCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private pitch_fourControlPanel cp = null;
			
			private double pitch1 = -12;
			private double pitch2 = -7;
			private double pitch3 = 7;
			private double pitch4 = 12;
			private double lfoSel = 0;
			private int pitchout;

			public pitch_fourCADBlock(int x, int y) {
				super(x, y);
				setName("Pitch_Four");					
			setBorderColor(new Color(0x00fc82));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Pitch_Out");
				addControlInputPin(this, "Pitch_Select");
			// if any control panel elements declared, set hasControlPanel to true
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
						cp = new pitch_fourControlPanel(this);
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
			sp = this.getPin("Pitch_Select").getPinConnection();
			int select = -1;
			if(sp != null) {
				select = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Input").isConnected() == true) {
			pitchout = sfxb.allocateReg();
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("delayd", 4096); 
			sfxb.FXallocDelayMem("temp", 1); 
			sfxb.skip(RUN, 2);
			sfxb.loadRampLFO((int) lfoSel, (int) 16384, (int) 4096);
			sfxb.readRegister(RMP0_RATE, 1.0);
			if(this.getPin("Pitch_Select").isConnected() == true) {
			sfxb.loadAccumulator(select);
			sfxb.and(0b01100000_00000000_00000000);
			if(lfoSel == 0) {
			sfxb.skip(ZRO, 13);
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.skip(ZRO, 8);
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.skip(ZRO, 3);
			double shift4 = 0.0;
			if(pitch4 > 0) {
				shift4 = (16384.0 * Math.pow(2.0, (pitch4/12.0) - 1))/32768.0;
			}
			else
			{
				shift4 = (-32.0 * Math.pow(2.0, (-pitch4/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift4);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.skip(RUN, 8);
			double shift1 = 0.0;
			if(pitch3 > 0) {
				shift1 = (16384.0 * Math.pow(2.0, (pitch3/12.0) - 1))/32768.0;
			}
			else
			{
				shift1 = (-32.0 * Math.pow(2.0, (-pitch3/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift1);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.skip(RUN, 5);
			double shift2 = 0.0;
			if(pitch2 > 0) {
				shift2 = (16384.0 * Math.pow(2.0, (pitch2/12.0) - 1))/32768.0;
			}
			else
			{
				shift2 = (-32.0 * Math.pow(2.0, (-pitch2/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift2);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.skip(RUN, 2);
			double shift3 = 0.0;
			if(pitch1 > 0) {
				shift3 = (16384.0 * Math.pow(2.0, (pitch1/12.0) - 1))/32768.0;
			}
			else
			{
				shift3 = (-32.0 * Math.pow(2.0, (-pitch1/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift3);
			sfxb.writeRegister(RMP0_RATE, 0);
			sfxb.loadAccumulator(input);
			sfxb.FXwriteDelay("delayd", 0, 0);
			sfxb.FXchorusReadDelay(RMP0, REG|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP0, 0, "delayd+", 1);
			sfxb.FXwriteDelay("temp", 0, 0);
			sfxb.FXchorusReadDelay(RMP0, RPTR2|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP0, RPTR2, "delayd+", 1);
			sfxb.chorusScaleOffset(RMP0, NA|COMPC, 0);
			sfxb.FXchorusReadDelay(RMP0, NA, "temp", 0);
			} else {
			sfxb.skip(ZRO, 13);
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.skip(ZRO, 8);
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.skip(ZRO, 3);
			double shift4 = 0.0;
			if(pitch4 > 0) {
				shift4 = (16384.0 * Math.pow(2.0, (pitch4/12.0) - 1))/32768.0;
			}
			else
			{
				shift4 = (-32.0 * Math.pow(2.0, (-pitch4/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift4);
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.skip(RUN, 8);
			double shift1 = 0.0;
			if(pitch3 > 0) {
				shift1 = (16384.0 * Math.pow(2.0, (pitch3/12.0) - 1))/32768.0;
			}
			else
			{
				shift1 = (-32.0 * Math.pow(2.0, (-pitch3/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift1);
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.skip(RUN, 5);
			double shift2 = 0.0;
			if(pitch2 > 0) {
				shift2 = (16384.0 * Math.pow(2.0, (pitch2/12.0) - 1))/32768.0;
			}
			else
			{
				shift2 = (-32.0 * Math.pow(2.0, (-pitch2/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift2);
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.skip(RUN, 2);
			double shift3 = 0.0;
			if(pitch1 > 0) {
				shift3 = (16384.0 * Math.pow(2.0, (pitch1/12.0) - 1))/32768.0;
			}
			else
			{
				shift3 = (-32.0 * Math.pow(2.0, (-pitch1/12.0) - 1))/32768.0;
			}
			sfxb.scaleOffset(0.0, shift3);
			sfxb.writeRegister(RMP1_RATE, 0);
			sfxb.loadAccumulator(input);
			sfxb.FXwriteDelay("delayd", 0, 0);
			sfxb.FXchorusReadDelay(RMP1, REG|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP1, 0, "delayd+", 1);
			sfxb.FXwriteDelay("temp", 0, 0);
			sfxb.FXchorusReadDelay(RMP1, RPTR2|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP1, RPTR2, "delayd+", 1);
			sfxb.chorusScaleOffset(RMP1, NA|COMPC, 0);
			sfxb.FXchorusReadDelay(RMP1, NA, "temp", 0);
			}
			
			sfxb.writeRegister(pitchout, 0);
			}
			
			}
			
			this.getPin("Pitch_Out").setRegister(pitchout);

			}
			
			// create setters and getter for control panel variables
			public void setpitch1(double __param) {
				pitch1 = __param;	
			}
			
			public double getpitch1() {
				return pitch1;
			}
			public void setpitch2(double __param) {
				pitch2 = __param;	
			}
			
			public double getpitch2() {
				return pitch2;
			}
			public void setpitch3(double __param) {
				pitch3 = __param;	
			}
			
			public double getpitch3() {
				return pitch3;
			}
			public void setpitch4(double __param) {
				pitch4 = __param;	
			}
			
			public double getpitch4() {
				return pitch4;
			}
			public void setlfoSel(int __param) {
				lfoSel = (double) __param;	
			}
			
			public int getlfoSel() {
				return (int) lfoSel;
			}
		}	
