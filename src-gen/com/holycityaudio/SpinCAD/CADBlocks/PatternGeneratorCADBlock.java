/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * PatternGeneratorCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.PatternGeneratorControlPanel;
		
	    @SuppressWarnings("unused")
	    public class PatternGeneratorCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private PatternGeneratorControlPanel cp = null;
			
			private int output;
			private int gate;
			private int counter;
			private int hold;
			private int rangeVal;
			private double threshold = 0.25;
			private double step1 = 0.5;
			private double step2 = 0.5;
			private double step3 = 0.5;
			private double step4 = 0.5;
			private double step5 = 0.5;
			private double step6 = 0.5;
			private double step7 = 0.5;
			private double step8 = 0.5;

			public PatternGeneratorCADBlock(int x, int y) {
				super(x, y);
				setName("PatternGenerator");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addControlInputPin(this, "Trigger");
				addControlInputPin(this, "Range");
				addControlOutputPin(this, "Level");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
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
						cp = new PatternGeneratorControlPanel(this);
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
			sp = this.getPin("Trigger").getPinConnection();
			int trigger = -1;
			if(sp != null) {
				trigger = sp.getRegister();
			}
			sp = this.getPin("Range").getPinConnection();
			int range = -1;
			if(sp != null) {
				range = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			gate = sfxb.allocateReg();
			counter = sfxb.allocateReg();
			hold = sfxb.allocateReg();
			rangeVal = sfxb.allocateReg();
			if(this.getPin("Range").isConnected() == true) {
			sfxb.loadAccumulator(range);
			sfxb.writeRegister(rangeVal, 0);
			} else {
			sfxb.scaleOffset(0.0, 0.9990234375);
			sfxb.writeRegister(rangeVal, 0);
			}
			
			if(this.getPin("Trigger").isConnected() == true) {
			sfxb.loadAccumulator(trigger);
			sfxb.scaleOffset(1.0, -threshold);
			sfxb.skip(NEG, 44);
			sfxb.readRegister(gate, -1.0);
			sfxb.skip(NEG, 39);
			sfxb.loadAccumulator(counter);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.skip(NEG, 14);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.skip(NEG, 14);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.skip(NEG, 14);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.skip(NEG, 14);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.skip(NEG, 14);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.skip(NEG, 14);
			sfxb.scaleOffset(1.0, -0.125);
			sfxb.skip(NEG, 14);
			sfxb.scaleOffset(0.0, step8);
			sfxb.skip(RUN, 13);
			sfxb.scaleOffset(0.0, step1);
			sfxb.skip(RUN, 11);
			sfxb.scaleOffset(0.0, step2);
			sfxb.skip(RUN, 9);
			sfxb.scaleOffset(0.0, step3);
			sfxb.skip(RUN, 7);
			sfxb.scaleOffset(0.0, step4);
			sfxb.skip(RUN, 5);
			sfxb.scaleOffset(0.0, step5);
			sfxb.skip(RUN, 3);
			sfxb.scaleOffset(0.0, step6);
			sfxb.skip(RUN, 1);
			sfxb.scaleOffset(0.0, step7);
			sfxb.writeRegister(hold, 0);
			sfxb.readRegister(counter, 1.0);
			sfxb.scaleOffset(1.0, 0.125);
			sfxb.writeRegister(counter, 1.0);
			sfxb.scaleOffset(1.0, -0.25);
			sfxb.readRegister(rangeVal, -0.749);
			sfxb.skip(NEG, 2);
			sfxb.clear();
			sfxb.writeRegister(counter, 0);
			sfxb.scaleOffset(0.0, 0.9990234375);
			sfxb.writeRegister(gate, 0);
			sfxb.skip(RUN, 2);
			sfxb.clear();
			sfxb.writeRegister(gate, 0);
			sfxb.readRegister(hold, 1.0);
			sfxb.writeRegister(output, 0);
			this.getPin("Level").setRegister(output);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setthreshold(double __param) {
				threshold = __param;	
			}
			
			public double getthreshold() {
				return threshold;
			}
			public void setstep1(double __param) {
				step1 = __param;	
			}
			
			public double getstep1() {
				return step1;
			}
			public void setstep2(double __param) {
				step2 = __param;	
			}
			
			public double getstep2() {
				return step2;
			}
			public void setstep3(double __param) {
				step3 = __param;	
			}
			
			public double getstep3() {
				return step3;
			}
			public void setstep4(double __param) {
				step4 = __param;	
			}
			
			public double getstep4() {
				return step4;
			}
			public void setstep5(double __param) {
				step5 = __param;	
			}
			
			public double getstep5() {
				return step5;
			}
			public void setstep6(double __param) {
				step6 = __param;	
			}
			
			public double getstep6() {
				return step6;
			}
			public void setstep7(double __param) {
				step7 = __param;	
			}
			
			public double getstep7() {
				return step7;
			}
			public void setstep8(double __param) {
				step8 = __param;	
			}
			
			public double getstep8() {
				return step8;
			}
		}	
