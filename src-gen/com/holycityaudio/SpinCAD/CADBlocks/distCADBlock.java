/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * distCADBlock.java
 * Copyright (C)2013 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
 * 
 *   This program is free software: you can redistribute it and/or modify 
 *   it under the terms of the GNU General Public License as published by 
 *   the Free Software Foundation, either version 3 of the License, or 
 *   (at your option) any later version. 
 * 
 *   This program is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *   GNU General Public License for more details. 
 * 
 *   You should have received a copy of the GNU General Public License 
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *     
 */ 
		package com.holycityaudio.SpinCAD.CADBlocks;
	
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
		
		public class distCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private int mono;
			private int abs;
			private int sign;
			private int output1;
			private int output2;

			public distCADBlock(int x, int y) {
				super(x, y);
				setName("Distortion");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
			}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
	//			new distControlPanel(this);
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly
			mono = sfxb.allocateReg();
			abs = sfxb.allocateReg();
			sign = sfxb.allocateReg();
			output1 = sfxb.allocateReg();
			output2 = sfxb.allocateReg();
			
			sfxb.comment(getName());
			
			SpinCADPin sp = null;
					
			// Iterate through pin definitions and connect or assign as needed
			sp = this.getPin("Input_Left").getPinConnection();
			int adcl = -1;
			if(sp != null) {
				adcl = sp.getRegister();
			}
			
			sp = this.getPin("Input_Right").getPinConnection();
			int adcr = -1;
			if(sp != null) {
				adcr = sp.getRegister();
			}
			
			this.getPin("Audio_Output_1").setRegister(output1);
			this.getPin("Audio_Output_2").setRegister(output2);
			// finally, generate the instructions
			if(this.getPin("Input_Left").getPinConnection() != null) {
			//			System.out.println("IsPinConnected! " + "Input_Left"); 
			sfxb.scaleOffset(0, -1);
			sfxb.writeRegister(sign, 0);
			sfxb.readRegister(adcl, 0.5);
			sfxb.readRegister(adcr, 0.5);
			sfxb.writeRegister(mono, 1);
			sfxb.skip(NEG, 2);
			sfxb.scaleOffset(0, 0.999);
			sfxb.writeRegister(sign, 0);
			sfxb.loadAccumulator(mono);
			sfxb.absa();
			sfxb.writeRegister(abs, 1);
			sfxb.scaleOffset(1, -0.125);
			sfxb.skip(NEG, 5);
			sfxb.loadAccumulator(abs);
			sfxb.log(-1, -0.375);
			sfxb.exp(1, 0);
			sfxb.scaleOffset(-1, 0.25);
			sfxb.skip(RUN, 1);
			sfxb.scaleOffset(1, 0.125);
			sfxb.mulx(sign);
			sfxb.scaleOffset(-2, 0);
			sfxb.scaleOffset(-2, 0);
			sfxb.writeRegister(output1, 1);
			sfxb.writeRegister(output2, 0);
			}
			

			// Say 'bye-bye'
			// System.out.println("Distortion" + " code gen!");
			}
			
			// create setters and getter for control panel variables
		}	
