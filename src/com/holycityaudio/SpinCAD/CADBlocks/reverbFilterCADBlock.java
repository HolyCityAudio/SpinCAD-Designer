/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverbCADBlock.java
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

public class reverbFilterCADBlock extends reverbACADBlock {

	private static final long serialVersionUID = 1L;
	private reverbControlPanelA cp = null;

	private int temp;
	private int hpf4;
	private int lpf4;
	private int output;
	private int nAPs = 2;
	private int nDLs = 3;
	private double kiap = 0.5;
	private double klap = 0.5;

	private double kfh = freqToFilt(450);
	private double kfl = freqToFilt(2750);

	public reverbFilterCADBlock(int x, int y) {
		super(x, y);
		setName("Reverb Filter");	
		// Iterate through pin definitions and allocate or assign as needed
		// if any control panel elements declared, set hasControlPanel to true
		hasControlPanel = true;
	}

	// In the event there are parameters editable by control panel
	public void editBlock(){ 
		if(cp == null) {
			if(hasControlPanel == true) {
				cp = new reverbControlPanelA(this);
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

		temp = sfxb.allocateReg();
		output = sfxb.allocateReg();
		hpf4 = sfxb.allocateReg();
		lpf4 = sfxb.allocateReg();

		sfxb.readRegister(input, 1.0);

		sfxb.writeRegister(temp, 1);
		sfxb.readRegisterFilter(lpf4, kfl);
		sfxb.writeRegisterLowshelf(lpf4, -0.5);
		sfxb.readRegisterFilter(hpf4, kfh);
		sfxb.writeRegisterHighshelf(hpf4, -0.5);
		sfxb.writeRegister(output, 0);
		
		this.getPin("Output").setRegister(output);
		System.out.println("Reverb Filter code gen!");	
	}

	// create setters and getter for control panel variables
	public void setkiap(double __param) {
		kiap = __param;	
	}

	public double getkiap() {
		return kiap;
	}
	public void setklap(double __param) {
		klap = __param;	
	}

	public double getklap() {
		return klap;
	}
	public void setkfl(double __param) {
		kfl = __param;	
	}

	public double getkfl() {
		return kfl;
	}
	public void setkfh(double __param) {
		kfh = __param;	
	}

	public double getkfh() {
		return kfh;
	}

	public int getnAPs() {
		return nAPs;
	}

	public int getnDLs() {
		return nDLs;
	}

	public void setnAPs(int val) {
		nAPs = val;
	}

	public void setnDLs(int val) {
		nDLs = val;
	}
}	
