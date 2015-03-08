/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Shelving_HipassCADBlock.java
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
	
		import com.holycityaudio.SpinCAD.SpinCADBlock;
		import com.holycityaudio.SpinCAD.SpinCADPin;
		import com.holycityaudio.SpinCAD.SpinFXBlock;
 		import com.holycityaudio.SpinCAD.ControlPanel.Shelving_HipassControlPanel;
		
		public class Shelving_HipassCADBlock extends SpinCADBlock {

			private static final long serialVersionUID = 1L;
			private Shelving_HipassControlPanel cp = null;
			
			private double freq = 0.15;
			private double shelf = 0.5;
			private int output;
			private int hpf1;
			private int filtReg;

			public Shelving_HipassCADBlock(int x, int y) {
				super(x, y);
				setName("Shelving_Hipass");	
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
				addOutputPin(this, "Filter");
				addControlInputPin(this, "Frequency");
				addControlInputPin(this, "Shelf");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new Shelving_HipassControlPanel(this);
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
			sp = this.getPin("Frequency").getPinConnection();
			int freqIn = -1;
			if(sp != null) {
				freqIn = sp.getRegister();
			}
			sp = this.getPin("Shelf").getPinConnection();
			int shelfIn = -1;
			if(sp != null) {
				shelfIn = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			hpf1 = sfxb.allocateReg();
			filtReg = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.readRegister(input, 1.0);
			sfxb.readRegisterFilter(hpf1, freq);
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.mulx(freqIn);
			}
			
			sfxb.writeRegisterHighshelf(hpf1, -shelf);
			if(this.getPin("Shelf").isConnected() == true) {
			sfxb.mulx(shelfIn);
			}
			
			sfxb.writeRegister(filtReg, 1.0);
			sfxb.readRegister(input, 1.0);
			sfxb.writeRegister(output, 0);
			this.getPin("Output").setRegister(output);
			this.getPin("Filter").setRegister(filtReg);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setfreq(double __param) {
				freq = __param;	
			}
			
			public double getfreq() {
				return freq;
			}
			public void setshelf(double __param) {
				shelf = __param;	
			}
			
			public double getshelf() {
				return shelf;
			}
		}	
