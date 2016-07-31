/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * pitchupdownCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.pitchupdownControlPanel;
		
	    @SuppressWarnings("unused")
	    public class pitchupdownCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private pitchupdownControlPanel cp = null;
			
			private int pitch1;
			private int pitch2;

			public pitchupdownCADBlock(int x, int y) {
				super(x, y);
				setName("Pitch_Up_Down");					
			setBorderColor(new Color(0x00fc82));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Pitch_Down_Out");
				addOutputPin(this, "Pitch_Up_Out");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new pitchupdownControlPanel(this);
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
			if(this.getPin("Input").isConnected() == true) {
			int	delayOffset = sfxb.getDelayMemAllocated() + 1;
			sfxb.FXallocDelayMem("delayd", 4096); 
			sfxb.FXallocDelayMem("temp", 1); 
			sfxb.skip(RUN, 2);
			sfxb.loadRampLFO((int) 1, (int) 16384, (int) 4096);
			sfxb.loadRampLFO((int) 0, (int) -8192, (int) 4096);
			sfxb.loadAccumulator(input);
			sfxb.FXwriteDelay("delayd", 0, 0);
			if(this.getPin("Pitch_Down_Out").isConnected() == true) {
			pitch1 = sfxb.allocateReg();
			sfxb.FXchorusReadDelay(RMP0, REG|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP0, 0, "delayd+", 1);
			sfxb.FXwriteDelay("temp", 0, 0);
			sfxb.FXchorusReadDelay(RMP0, RPTR2|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP0, RPTR2, "delayd+", 1);
			sfxb.chorusScaleOffset(RMP0, NA|COMPC, 0);
			sfxb.FXchorusReadDelay(RMP0, NA, "temp", 0);
			sfxb.writeRegister(pitch1, 0);
			}
			
			this.getPin("Pitch_Down_Out").setRegister(pitch1);
			if(this.getPin("Pitch_Up_Out").isConnected() == true) {
			pitch2 = sfxb.allocateReg();
			sfxb.FXchorusReadDelay(RMP1, REG|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP1, 0, "delayd+", 1);
			sfxb.FXwriteDelay("temp", 0, 0);
			sfxb.FXchorusReadDelay(RMP1, RPTR2|COMPC, "delayd", 0);
			sfxb.FXchorusReadDelay(RMP1, RPTR2, "delayd+", 1);
			sfxb.chorusScaleOffset(RMP1, NA|COMPC, 0);
			sfxb.FXchorusReadDelay(RMP1, NA, "temp", 0);
			sfxb.writeRegister(pitch2, 0);
			this.getPin("Pitch_Up_Out").setRegister(pitch2);
			}
			
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
