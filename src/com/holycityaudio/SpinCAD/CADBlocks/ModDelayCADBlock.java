/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ModDelayCADBLock.java
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

import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ModDelayCADBlock extends ModulationCADBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = 695539935034103396L;
	int delayLength = 8192;

	public ModDelayCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		// editPanel.add();
		addControlInputPin(this,"Modulation In");
		setName("Mod Delay");
	}
	
	private void modDelay(SpinFXBlock sfxb, int chorusLength) {
		int input = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		if (p != null) {
			input = p.getRegister();
			int chorout = sfxb.allocateReg();
			
			int Control1 = -1;

			p = this.getPin("Control Input 1").getPinConnection();
			if (p != null) {
				Control1 = p.getRegister();
			}

//			;modulated delay for flanger etc
//			equ control pot0
//			mem moddel 8192
			sfxb.FXallocDelayMem("moddel", delayLength);
			sfxb.comment("Mod Delay");

//			;set up LFOs, only at start up
//			skp run, START
			sfxb.skip(RUN, 1);
//			wldr rmp0,0,4096      ;ramp0 used for modulation operations
			sfxb.loadRampLFO(0, 0, 4096);
//			START:

//			rdax adcl,1
			sfxb.readRegister(input, 1.0);
//			wra moddel,0      ;write to head of delay
			sfxb.FXwriteDelay("moddel", 0, 0);
//			cho rdal,rmp0      ;servo ramp0 to correct position using value in control
			sfxb.chorusReadValue(RMP0);
//			rdax control,-0.25    ;scaled so that 0 - 1 sweeps full range
			sfxb.readRegister(Control1,  -0.25);
//			wrax rmp0_rate,0
			sfxb.writeRegister(RMP0_RATE, 0);
//			cho rda,rmp0,reg|compc,moddel   ;read from delay
			sfxb.FXchorusReadDelay(RMP0, REG | COMPC, "moddel", 0);
//			cho rda,rmp0,0,moddel+1
			sfxb.FXchorusReadDelay(RMP0, 0, "moddel", 1);
//			wrax dacl,0
			sfxb.writeRegister(chorout, 0);
			this.getPin("Audio Output 1").setRegister(chorout);
			System.out.println("Mod Delay code gen!");
		}
	}

	public void generateCode(SpinFXBlock sfxb) {
		/**
		 * @param sfxb
		 *            is the handle of the calling program
		 * @param input
		 *            is the input register from a previously defined block
		 */
		modDelay(sfxb, delayLength);
	}

// control panel functions
	
	public void editBlock(){
		new ModDelayControlPanel(this);
	}

	public int getDelayLength () {
		return delayLength;
	}

	public void setDelayLength (int l) {
		delayLength = l;
	}
}
