/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * crossfade_2CADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.crossfade_2ControlPanel;
		
	    @SuppressWarnings("unused")
	    public class crossfade_2CADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private crossfade_2ControlPanel cp = null;
			
			private int output1;
			private int temp;

			public crossfade_2CADBlock(int x, int y) {
				super(x, y);
				setName("Crossfade 2");					
			setBorderColor(new Color(0xf2f224));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Audio In 1");
				addInputPin(this, "Audio In 2");
				addControlInputPin(this, "Control Input");
				addOutputPin(this, "Audio Output");
			// if any control panel elements declared, set hasControlPanel to true
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new crossfade_2ControlPanel(this);
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
			sp = this.getPin("Audio In 1").getPinConnection();
			int inputOne = -1;
			if(sp != null) {
				inputOne = sp.getRegister();
			}
			sp = this.getPin("Audio In 2").getPinConnection();
			int inputTwo = -1;
			if(sp != null) {
				inputTwo = sp.getRegister();
			}
			sp = this.getPin("Control Input").getPinConnection();
			int controlIn = -1;
			if(sp != null) {
				controlIn = sp.getRegister();
			}
			
			// finally, generate the instructions
			if(this.getPin("Control Input").isConnected() == true) {
			output1 = sfxb.allocateReg();
			temp = sfxb.allocateReg();
			if(this.getPin("Audio In 1").isConnected() == true) {
			sfxb.readRegister(controlIn, -1.0);
			sfxb.scaleOffset(-2.0, 0);
			sfxb.mulx(inputTwo);
			sfxb.writeRegister(temp, 0);
			}
			
			if(this.getPin("Audio In 2").isConnected() == true) {
			sfxb.readRegister(controlIn, 1.0);
			sfxb.scaleOffset(1.0, -1.0);
			sfxb.scaleOffset(-2.0, 0.0);
			sfxb.mulx(inputOne);
			}
			
			sfxb.readRegister(temp, 1);
			sfxb.writeRegister(output1, 0);
			this.getPin("Audio Output").setRegister(output1);
			}
			

			}
			
			// create setters and getter for control panel variables
		}	
