/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * SVF_2P_adjustableCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.SVF_2P_adjustableControlPanel;
		
	    @SuppressWarnings("unused")
	    public class SVF_2P_adjustableCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private SVF_2P_adjustableControlPanel cp = null;
			
			private double freq = 0.15;
			private double qMax = 50;
			private double qMin = 1;
			private int output;
			private int z1;
			private int z2;
			private int lpf;
			private int hpf;
			private int brf;
			private double number1 = 1.0;
			private int temp;
			private int scaledQ;

			public SVF_2P_adjustableCADBlock(int x, int y) {
				super(x, y);
				setName("SVF 2 Pole");					
			setBorderColor(new Color(0x24f26f));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addControlInputPin(this, "Frequency");
				addControlInputPin(this, "Q");
				addOutputPin(this, "Low Pass Output");
				addOutputPin(this, "Band Pass Output");
				addOutputPin(this, "Notch Output");
				addOutputPin(this, "High Pass Output");
			// if any control panel elements declared, set hasControlPanel to true
						hasControlPanel = true;
						hasControlPanel = true;
						hasControlPanel = true;
						}
		
			// In the event there are parameters editable by control panel
			public void editBlock(){ 
				if(cp == null) {
					if(hasControlPanel == true) {
						cp = new SVF_2P_adjustableControlPanel(this);
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
			int freqControl = -1;
			if(sp != null) {
				freqControl = sp.getRegister();
			}
			sp = this.getPin("Q").getPinConnection();
			int qControl = -1;
			if(sp != null) {
				qControl = sp.getRegister();
			}
			
			// finally, generate the instructions
			output = sfxb.allocateReg();
			z1 = sfxb.allocateReg();
			z2 = sfxb.allocateReg();
			lpf = sfxb.allocateReg();
			hpf = sfxb.allocateReg();
			brf = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			sfxb.clear();
			sfxb.readRegister(z1, freq);
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.mulx(freqControl);
			}
			
			sfxb.readRegister(z2, 1);
			sfxb.writeRegister(lpf, 1);
			sfxb.writeRegister(z2, -1);
			if(this.getPin("Q").isConnected() == true) {
			sfxb.writeRegister(temp, 0);
			double y = number1 / qMin;
			double x1 = number1 / qMax;
			double q = x1 - y;
			temp = sfxb.allocateReg();
			scaledQ = sfxb.allocateReg();
			sfxb.readRegister(qControl, 1);
			sfxb.scaleOffset(-q, -y);
			sfxb.writeRegister(scaledQ, 0);
			sfxb.readRegister(z1, 1);
			sfxb.mulx(scaledQ);
			sfxb.readRegister(temp, 1);
			} else {
			double q = number1 / qMax;
			sfxb.readRegister(z1, -q);
			}
			
			sfxb.readRegister(input, 1);
			sfxb.writeRegister(hpf, freq);
			if(this.getPin("Frequency").isConnected() == true) {
			sfxb.mulx(freqControl);
			}
			
			sfxb.readRegister(z1, 1);
			sfxb.writeRegister(z1, 0);
			if(this.getPin("Notch Output").isConnected() == true) {
			this.getPin("Notch Output").setRegister(brf);
			sfxb.readRegister(lpf, 1);
			sfxb.readRegister(hpf, 1);
			sfxb.writeRegister(brf, 0);
			}
			
			this.getPin("Low Pass Output").setRegister(lpf);
			this.getPin("Band Pass Output").setRegister(z1);
			this.getPin("High Pass Output").setRegister(hpf);
			}
			

			}
			
			// create setters and getter for control panel variables
			public void setfreq(double __param) {
				freq = __param;	
			}
			
			public double getfreq() {
				return freq;
			}
			public void setqMax(double __param) {
				qMax = __param;	
			}
			
			public double getqMax() {
				return qMax;
			}
			public void setqMin(double __param) {
				qMin = __param;	
			}
			
			public double getqMin() {
				return qMin;
			}
		}	
