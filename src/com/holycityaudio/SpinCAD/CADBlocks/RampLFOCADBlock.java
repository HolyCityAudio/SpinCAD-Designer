/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Copyright (C) 2013 - 2014 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code. 
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
import com.holycityaudio.SpinCAD.ControlBlocks.RampLFOControlPanel;

public class RampLFOCADBlock extends ControlCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7025985649946130854L;
	int lfoRate = 1;
	int lfoWidth = 1;
	int whichLFO = 0;		// 0 or 1
	boolean invert = false;
	boolean flip = false;
	int lfoWidths[] = { 512, 1024, 2048, 4096 };

	public RampLFOCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this, "Rate");			//	
		addControlOutputPin(this, "Ramp LFO");		//	Ramp 1
		addControlOutputPin(this, "Triangle LFO");	//	
		hasControlPanel = true;
		setName("Ramp LFO " + whichLFO);
	}

	public void generateCode(SpinFXBlock sfxb) {

		int ramp0 = sfxb.allocateReg();
		int triangle = -1;
		sfxb.comment(getName());

		sfxb.skip(RUN,1);
		sfxb.loadRampLFO(whichLFO, lfoRate, lfoWidths[lfoWidth]);
		
		SpinCADPin p = this.getPin("Rate");

		if(p.isConnected()) {
			int speedIn = p.getPinConnection().getRegister();			
			sfxb.readRegister(speedIn, (lfoRate/32767.0));	// scale pot by control panel rate setting
			if(whichLFO == 0) {
				sfxb.writeRegister(RMP0_RATE, 0.0);
			}
			else {
				sfxb.writeRegister(RMP1_RATE, 0.0);				
			}
		}

		if(whichLFO == 0) {
			sfxb.chorusReadValue(RMP0);
		} else {
			sfxb.chorusReadValue(RMP1);
		}
		if(this.getPin("Triangle LFO").isConnected()) {
			triangle = sfxb.allocateReg();
			sfxb.writeRegister(ramp0, 1.0);	
			sfxb.scaleOffset(1.999, -0.5 * lfoWidths[lfoWidth]/4096);
			sfxb.absa();
			sfxb.writeRegister(triangle, 0.0);	
			this.getPin("Triangle LFO").setRegister(triangle);
		} else {
			sfxb.writeRegister(ramp0, 0.0);	
		}
		this.getPin("Ramp LFO").setRegister(ramp0);
		System.out.println("Ramp LFO code gen!");
	}

	public void editBlock(){
		RampLFOControlPanel cp = new RampLFOControlPanel(this);
	}
	//====================================================

	public int getLFORate() {
		return lfoRate;
	}

	public void setLFORate(int r) {
		lfoRate = r;
	}

	public void setLFOWidth(int value) {
		lfoWidth = value;
	}
	public int getLFOWidth() {
		return lfoWidth;
	}

	public int getLFOSel() {
		return whichLFO;
	}

	public void setLFOSel(int i) {
		whichLFO = i;
	}
}
