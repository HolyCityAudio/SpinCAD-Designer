/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * InstructionTestCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.InstructionTestControlPanel;
		
	    @SuppressWarnings("unused")
	    public class InstructionTestCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private InstructionTestControlPanel cp = null;
			
			private double factor = 0.56789;
			private double s_dot_ten = 0.998;
			private int dacl;
			private int dacr;
			private int input;
			private int Output;
			private double kq0 = 0.43598;
			private double one = 1.0;
			private double two = 1.999;

			public InstructionTestCADBlock(int x, int y) {
				super(x, y);
				setName("Instruction_Test");					
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input_Left");
				addInputPin(this, "Input_Right");
				addOutputPin(this, "Audio_Output_1");
				addOutputPin(this, "Audio_Output_2");
				addControlInputPin(this, "Reverb");
				addControlInputPin(this, "Phase_Rate");
				addControlInputPin(this, "Phase_Width");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new InstructionTestControlPanel(this);
					}
				}
			}
			
			public void clearCP() {
				cp = null;
			}	
				
			public void generateCode(SpinFXBlock sfxb) {
	
			// Iterate through mem and equ statements, allocate accordingly

			int del = 250;
			
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
			sp = this.getPin("Reverb").getPinConnection();
			int pot0 = -1;
			if(sp != null) {
				pot0 = sp.getRegister();
			}
			sp = this.getPin("Phase_Rate").getPinConnection();
			int pot1 = -1;
			if(sp != null) {
				pot1 = sp.getRegister();
			}
			sp = this.getPin("Phase_Width").getPinConnection();
			int pot2 = -1;
			if(sp != null) {
				pot2 = sp.getRegister();
			}
			
			// finally, generate the instructions
			sfxb.FXallocDelayMem("buffer", 1000); 
			sfxb.FXallocDelayMem("delay1", 6000); 
			sfxb.FXallocDelayMem("delay2", 2000); 
			sfxb.FXallocDelayMem("delay3", 3000); 
			sfxb.FXallocDelayMem("delay_line", 5000); 
			sfxb.FXallocDelayMem("ap1", 500); 
			sfxb.FXallocDelayMem("fdell", 1500); 
			if(this.getPin("Input_Left").isConnected() == true) {
			sfxb.readRegisterFilter(input, kq0);
			sfxb.readRegisterFilter(input, -kq0);
			sfxb.readRegisterFilter(adcl, 0.00001);
			sfxb.skip(GEZ, 1);
			sfxb.jam(RMP1);
			sfxb.skip(ZRC, 4);
			sfxb.jam(RMP1);
			sfxb.jam(RMP0);
			sfxb.absa();
			sfxb.not();
			sfxb.skip(RUN, 4);
			sfxb.not();
			sfxb.absa();
			sfxb.skip(NEG, 2);
			sfxb.jam(RMP1);
			sfxb.clear();
			sfxb.FXwriteAllpass("delay_line", 0, 1.0);
			sfxb.FXwriteAllpass("delay_line+", (int)(75 * 1.0), factor);
			sfxb.FXwriteAllpass("delay2+", del, 0.3123);
			sfxb.FXwriteAllpass("delay2#", 0, factor);
			sfxb.FXwriteAllpass("delay1^+", (int)(375 * 1.0), 0.33215);
			sfxb.FXwriteAllpass("delay1^+", del, 0.33215);
			sfxb.FXwriteAllpass("delay1^", 0, factor);
			sfxb.FXwriteAllpass("delay3^-", (int)(450 * 1.0), 0.34445533);
			sfxb.FXwriteAllpass("delay3^-", del, 0.34445533);
			sfxb.FXwriteAllpass("delay3#-", (int)(450 * 1.0), 0.44553378);
			sfxb.FXwriteDelay("buffer+", del, -1.08);
			sfxb.FXwriteDelay("buffer^+", del, 0.78);
			sfxb.FXwriteDelay("buffer^-", del, 0.36);
			sfxb.FXwriteDelay("buffer#-", del, 0.91);
			sfxb.FXwriteDelay("buffer+", (int)(50 * 1.0), -1.0);
			sfxb.FXwriteDelay("buffer^+", (int)(25 * 1.0), 1.0);
			sfxb.FXwriteDelay("buffer^+", (int)(250 * 1.0), 0.77);
			sfxb.FXwriteDelay("buffer^-", (int)(75 * 1.0), 0.9);
			sfxb.FXwriteDelay("buffer#-", (int)(350 * 1.0), 0.89);
			sfxb.FXwriteDelay("buffer#-", (int)(35 * 1.0), 0.99);
			sfxb.FXwriteDelay("buffer#", 0, 0.96);
			sfxb.FXwriteDelay("buffer^", 0, 0.85);
			sfxb.FXwriteDelay("buffer", 0, 0.5);
			sfxb.FXreadDelay("buffer+", del, -1.08);
			sfxb.FXreadDelay("buffer^+", del, 0.78);
			sfxb.FXreadDelay("buffer^-", del, 0.36);
			sfxb.FXreadDelay("buffer#-", del, 0.91);
			sfxb.FXreadDelay("buffer+", (int)(50 * 1.0), -1.0);
			sfxb.FXreadDelay("buffer^+", (int)(25 * 1.0), 1.0);
			sfxb.FXreadDelay("buffer^+", (int)(250 * 1.0), 0.77);
			sfxb.FXreadDelay("buffer^-", (int)(75 * 1.0), 0.9);
			sfxb.FXreadDelay("buffer#-", (int)(350 * 1.0), 0.89);
			sfxb.FXreadDelay("buffer#-", (int)(35 * 1.0), 0.99);
			sfxb.FXreadDelay("buffer#", 0, 0.96);
			sfxb.FXreadDelay("buffer^", 0, 0.85);
			sfxb.FXreadDelay("buffer", 0, 0.5);
			sfxb.and(0b1010101);
			sfxb.and(0b10001001_1001000_00000001);
			sfxb.and(0b1001_1100_1011_0110);
			sfxb.and(0x55AA);
			sfxb.or(0b101101);
			sfxb.or(0b1000110011_01_01);
			sfxb.or(0b10001001_1001000_00000001);
			sfxb.or(0xFEEEEE);
			sfxb.or(0x15);
			sfxb.or(0xAA55);
			sfxb.or(0x55AA);
			sfxb.xor(0b101010101);
			sfxb.xor(0b1000110011_01_01);
			sfxb.xor(0x15);
			sfxb.xor(0xAA55);
			sfxb.xor(0x55AA);
			dacl = sfxb.allocateReg();
			dacr = sfxb.allocateReg();
			if(this.getPin("Reverb").isConnected() == true) {
			sfxb.and(0xFEEEEE);
			sfxb.and(0x15);
			sfxb.and(0xAA55);
			} else {
			sfxb.xor(0b10001100_10101010_01011110);
			sfxb.xor(0b10001001_10010010_00000001);
			sfxb.xor(0xFEEEEE);
			}
			
			if(this.getPin("Phase_Rate").isConnected() == true) {
			sfxb.readDelayPointer(1.0);
			sfxb.readDelayPointer(-2.0);
			} else {
			sfxb.readDelayPointer(factor);
			}
			
			sfxb.readDelayPointer(1.0);
			sfxb.readDelayPointer(-2.0);
			sfxb.readDelayPointer(factor);
			sfxb.FXchorusReadDelay(SIN0, REG | COMPC, "ap1+", 50);
			sfxb.FXchorusReadDelay(SIN0, REG | COMPC, "ap1+", 50);
			sfxb.FXchorusReadDelay(SIN0, REG, "ap1+", 51);
			sfxb.FXchorusReadDelay(RMP0, REG|COMPC, "fdell#", 0);
			input = sfxb.allocateReg();
			Output = sfxb.allocateReg();
			sfxb.jam(RMP0);
			sfxb.jam(RMP1);
			sfxb.clear();
			sfxb.not();
			sfxb.absa();
			sfxb.log(one, s_dot_ten);
			sfxb.log(1, 0.99);
			sfxb.log(1.0, 0.799);
			sfxb.log(-1.0, -1.0);
			sfxb.exp(1.0, s_dot_ten);
			sfxb.chorusReadValue(RMP0);
			sfxb.chorusReadValue(RMP1);
			sfxb.loadSinLFO((int) SIN0,(int) 30, (int) 50);
			sfxb.loadSinLFO((int) SIN1,(int) 134, (int) 4096);
			sfxb.loadRampLFO((int) 0, (int) 30, (int) 512);
			sfxb.loadRampLFO((int) 1, (int) 134, (int) 4096);
			sfxb.readRegister(input, kq0);
			sfxb.readRegister(input, -kq0);
			sfxb.readRegister(input, 0.45);
			sfxb.readRegister(input, 1);
			sfxb.readRegister(input, 0);
			sfxb.readRegister(input, -2);
			sfxb.readRegister(input, 1.45);
			sfxb.readRegister(45, -2.0);
			sfxb.writeRegister(input, kq0);
			sfxb.writeRegister(input, -kq0);
			sfxb.writeRegister(input, 1);
			sfxb.maxx(input, kq0);
			sfxb.maxx(34, -kq0);
			sfxb.maxx(adcr, 0.45);
			sfxb.writeRegisterLowshelf(input, kq0);
			sfxb.writeRegisterLowshelf(34, -kq0);
			sfxb.writeRegisterLowshelf(input, 0.45);
			sfxb.writeRegisterHighshelf(input, -2);
			sfxb.writeRegisterHighshelf(input, 0.45);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
