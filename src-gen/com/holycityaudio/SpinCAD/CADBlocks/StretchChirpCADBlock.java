/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * StretchChirpCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.StretchChirpControlPanel;
		
	    @SuppressWarnings("unused")
	    public class StretchChirpCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private StretchChirpControlPanel cp = null;
			
			private double gain = 0.5;
			private double stretch = 4;
			private double ap01 = 0.5;
			private int output1;

			public StretchChirpCADBlock(int x, int y) {
				super(x, y);
				setName("StretchChirp");					
			setBorderColor(new Color(0x7108fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new StretchChirpControlPanel(this);
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
			sfxb.FXallocDelayMem("stretchBuffer", stretch); 
			if(this.getPin("Input").isConnected() == true) {
			output1 = sfxb.allocateReg();
			sfxb.writeRegister(output1, 0);
			this.getPin("Output").setRegister(output1);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setgain(double __param) {
				gain = Math.pow(10.0, __param/20.0);	
			}
			
			public double getgain() {
				return gain;
			}
			public void setstretch(double __param) {
				stretch = __param;	
			}
			
			public double getstretch() {
				return stretch;
			}
			public void setap01(double __param) {
				ap01 = __param;	
			}
			
			public double getap01() {
				return ap01;
			}
		}	
