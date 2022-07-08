/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChirpCADBlock.java
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
 		import com.holycityaudio.SpinCAD.ControlPanel.ChirpControlPanel;
		
	    @SuppressWarnings("unused")
	    public class ChirpCADBlock extends SpinCADBlock {
			private static final long serialVersionUID = 1L;
			private ChirpControlPanel cp = null;
			
			private double gain = 0.5;
			private double nAPs = 4;
			private double stretch = 4;
			private double kiap = 0.5;
			private int output1;

			public ChirpCADBlock(int x, int y) {
				super(x, y);
				setName("Chirp");					
			setBorderColor(new Color(0x7100fc));
				// Iterate through pin definitions and allocate or assign as needed
				addInputPin(this, "Input");
				addOutputPin(this, "Output");
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
						cp = new ChirpControlPanel(this);
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
			output1 = sfxb.allocateReg();
			if(this.getPin("Input").isConnected() == true) {
			if(nAPs > 29) {
			sfxb.FXallocDelayMem("iap30", stretch); 
			}
			
			if(nAPs > 28) {
			sfxb.FXallocDelayMem("iap29", stretch); 
			}
			
			if(nAPs > 27) {
			sfxb.FXallocDelayMem("iap28", stretch); 
			}
			
			if(nAPs > 26) {
			sfxb.FXallocDelayMem("iap27", stretch); 
			}
			
			if(nAPs > 25) {
			sfxb.FXallocDelayMem("iap26", stretch); 
			}
			
			if(nAPs > 24) {
			sfxb.FXallocDelayMem("iap25", stretch); 
			}
			
			if(nAPs > 23) {
			sfxb.FXallocDelayMem("iap24", stretch); 
			}
			
			if(nAPs > 22) {
			sfxb.FXallocDelayMem("iap23", stretch); 
			}
			
			if(nAPs > 21) {
			sfxb.FXallocDelayMem("iap22", stretch); 
			}
			
			if(nAPs > 20) {
			sfxb.FXallocDelayMem("iap21", stretch); 
			}
			
			if(nAPs > 19) {
			sfxb.FXallocDelayMem("iap20", stretch); 
			}
			
			if(nAPs > 18) {
			sfxb.FXallocDelayMem("iap19", stretch); 
			}
			
			if(nAPs > 17) {
			sfxb.FXallocDelayMem("iap18", stretch); 
			}
			
			if(nAPs > 16) {
			sfxb.FXallocDelayMem("iap17", stretch); 
			}
			
			if(nAPs > 15) {
			sfxb.FXallocDelayMem("iap16", stretch); 
			}
			
			if(nAPs > 14) {
			sfxb.FXallocDelayMem("iap15", stretch); 
			}
			
			if(nAPs > 13) {
			sfxb.FXallocDelayMem("iap14", stretch); 
			}
			
			if(nAPs > 12) {
			sfxb.FXallocDelayMem("iap13", stretch); 
			}
			
			if(nAPs > 11) {
			sfxb.FXallocDelayMem("iap12", stretch); 
			}
			
			if(nAPs > 10) {
			sfxb.FXallocDelayMem("iap11", stretch); 
			}
			
			if(nAPs > 9) {
			sfxb.FXallocDelayMem("iap10", stretch); 
			}
			
			if(nAPs > 8) {
			sfxb.FXallocDelayMem("iap9", stretch); 
			}
			
			if(nAPs > 7) {
			sfxb.FXallocDelayMem("iap8", stretch); 
			}
			
			if(nAPs > 6) {
			sfxb.FXallocDelayMem("iap7", stretch); 
			}
			
			if(nAPs > 5) {
			sfxb.FXallocDelayMem("iap6", stretch); 
			}
			
			if(nAPs > 4) {
			sfxb.FXallocDelayMem("iap5", stretch); 
			}
			
			if(nAPs > 3) {
			sfxb.FXallocDelayMem("iap1", stretch); 
			}
			
			if(nAPs > 2) {
			sfxb.FXallocDelayMem("iap2", stretch); 
			}
			
			if(nAPs > 1) {
			sfxb.FXallocDelayMem("iap3", stretch); 
			}
			
			sfxb.FXallocDelayMem("iap4", stretch); 
			sfxb.readRegister(input, gain);
			if(nAPs > 29) {
			sfxb.FXreadDelay("iap30#", 0, kiap);
			sfxb.FXwriteAllpass("iap30", 0, -kiap);
			}
			
			if(nAPs > 28) {
			sfxb.FXreadDelay("iap29#", 0, kiap);
			sfxb.FXwriteAllpass("iap29", 0, -kiap);
			}
			
			if(nAPs > 27) {
			sfxb.FXreadDelay("iap28#", 0, kiap);
			sfxb.FXwriteAllpass("iap28", 0, -kiap);
			}
			
			if(nAPs > 26) {
			sfxb.FXreadDelay("iap27#", 0, kiap);
			sfxb.FXwriteAllpass("iap27", 0, -kiap);
			}
			
			if(nAPs > 25) {
			sfxb.FXreadDelay("iap26#", 0, kiap);
			sfxb.FXwriteAllpass("iap26", 0, -kiap);
			}
			
			if(nAPs > 24) {
			sfxb.FXreadDelay("iap25#", 0, kiap);
			sfxb.FXwriteAllpass("iap25", 0, -kiap);
			}
			
			if(nAPs > 23) {
			sfxb.FXreadDelay("iap24#", 0, kiap);
			sfxb.FXwriteAllpass("iap24", 0, -kiap);
			}
			
			if(nAPs > 22) {
			sfxb.FXreadDelay("iap23#", 0, kiap);
			sfxb.FXwriteAllpass("iap23", 0, -kiap);
			}
			
			if(nAPs > 21) {
			sfxb.FXreadDelay("iap22#", 0, kiap);
			sfxb.FXwriteAllpass("iap22", 0, -kiap);
			}
			
			if(nAPs > 20) {
			sfxb.FXreadDelay("iap21#", 0, kiap);
			sfxb.FXwriteAllpass("iap21", 0, -kiap);
			}
			
			if(nAPs > 19) {
			sfxb.FXreadDelay("iap20#", 0, kiap);
			sfxb.FXwriteAllpass("iap20", 0, -kiap);
			}
			
			if(nAPs > 18) {
			sfxb.FXreadDelay("iap19#", 0, kiap);
			sfxb.FXwriteAllpass("iap19", 0, -kiap);
			}
			
			if(nAPs > 17) {
			sfxb.FXreadDelay("iap18#", 0, kiap);
			sfxb.FXwriteAllpass("iap18", 0, -kiap);
			}
			
			if(nAPs > 16) {
			sfxb.FXreadDelay("iap17#", 0, kiap);
			sfxb.FXwriteAllpass("iap17", 0, -kiap);
			}
			
			if(nAPs > 15) {
			sfxb.FXreadDelay("iap16#", 0, kiap);
			sfxb.FXwriteAllpass("iap16", 0, -kiap);
			}
			
			if(nAPs > 14) {
			sfxb.FXreadDelay("iap15#", 0, kiap);
			sfxb.FXwriteAllpass("iap15", 0, -kiap);
			}
			
			if(nAPs > 13) {
			sfxb.FXreadDelay("iap14#", 0, kiap);
			sfxb.FXwriteAllpass("iap14", 0, -kiap);
			}
			
			if(nAPs > 12) {
			sfxb.FXreadDelay("iap13#", 0, kiap);
			sfxb.FXwriteAllpass("iap13", 0, -kiap);
			}
			
			if(nAPs > 11) {
			sfxb.FXreadDelay("iap12#", 0, kiap);
			sfxb.FXwriteAllpass("iap12", 0, -kiap);
			}
			
			if(nAPs > 10) {
			sfxb.FXreadDelay("iap11#", 0, kiap);
			sfxb.FXwriteAllpass("iap11", 0, -kiap);
			}
			
			if(nAPs > 9) {
			sfxb.FXreadDelay("iap10#", 0, kiap);
			sfxb.FXwriteAllpass("iap10", 0, -kiap);
			}
			
			if(nAPs > 8) {
			sfxb.FXreadDelay("iap9#", 0, kiap);
			sfxb.FXwriteAllpass("iap9", 0, -kiap);
			}
			
			if(nAPs > 7) {
			sfxb.FXreadDelay("iap8#", 0, kiap);
			sfxb.FXwriteAllpass("iap8", 0, -kiap);
			}
			
			if(nAPs > 6) {
			sfxb.FXreadDelay("iap7#", 0, kiap);
			sfxb.FXwriteAllpass("iap7", 0, -kiap);
			}
			
			if(nAPs > 5) {
			sfxb.FXreadDelay("iap6#", 0, kiap);
			sfxb.FXwriteAllpass("iap6", 0, -kiap);
			}
			
			if(nAPs > 4) {
			sfxb.FXreadDelay("iap5#", 0, kiap);
			sfxb.FXwriteAllpass("iap5", 0, -kiap);
			}
			
			if(nAPs > 3) {
			sfxb.FXreadDelay("iap1#", 0, kiap);
			sfxb.FXwriteAllpass("iap1", 0, -kiap);
			}
			
			if(nAPs > 2) {
			sfxb.FXreadDelay("iap2#", 0, kiap);
			sfxb.FXwriteAllpass("iap2", 0, -kiap);
			}
			
			if(nAPs > 1) {
			sfxb.FXreadDelay("iap3#", 0, kiap);
			sfxb.FXwriteAllpass("iap3", 0, -kiap);
			}
			
			sfxb.FXreadDelay("iap4#", 0, kiap);
			sfxb.FXwriteAllpass("iap4", 0, -kiap);
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
			public void setnAPs(double __param) {
				nAPs = __param;	
			}
			
			public double getnAPs() {
				return nAPs;
			}
			public void setstretch(double __param) {
				stretch = __param;	
			}
			
			public double getstretch() {
				return stretch;
			}
			public void setkiap(double __param) {
				kiap = __param;	
			}
			
			public double getkiap() {
				return kiap;
			}
		}	
