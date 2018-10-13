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

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class OscillatorCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7695385445625428287L;
	/**
	 * 
	 */
	private double lfo = 0.02;

	public OscillatorCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlInputPin(this, "LFO Speed");
//debug, width control not working, probably due to output register setting when control connected
//		addControlInputPin(this, "LFO Width");
		addControlOutputPin(this, "Sine Out");
		addControlOutputPin(this, "Cosine Out");
		setName("Oscillator");
	}

	public void editBlock(){
		new OscillatorControlPanel(this);
	}

	public void generateCode(SpinFXBlock b) {

		int Control1 = -1;
		int Control2 = -1;

		SpinCADPin p = this.getPin("LFO Speed").getPinConnection();
//		SpinCADPin p2 = this.getPin("LFO Width").getPinConnection();

		if (p != null)
			Control1 = p.getRegister();

		//if (p2 != null)
		//	Control2 = p2.getRegister();

		//		;POT0 : Control frequency 
		//		; 
		//		equ s reg0
		int s = b.allocateReg();
		//		equ c reg1
		int c = b.allocateReg();
		b.comment(getName());

		//		;Then initialize the oscillator by setting one to xero and the other to -1 
		//		;we do this just once, during the first cycle of operation 

		//		skp run,endset ;do not execute if already running 
		b.skip(RUN, 3);
		//		wrax s,0 ;set s to 0, (acc should be zero) 
		b.writeRegister(s, 0.0);

		//		sof 0,-1 ;set accum to -1
		b.scaleOffset(0, -1);
		//		wrax c,0 ;write to c 
		if(Control2 != -1) {
			int c_output = b.allocateReg();
			b.writeRegister(c, 1.0);
			b.mulx(Control2);
			b.writeRegister(c_output,  0.0);			
		}
		else {
			b.writeRegister(c, 0);
		}
		//		endset: ;jump-to label 

		//		;Now do the LFO, using pot0 as a control for frequency 

		//		rdax s,0.02 ;read the s register, change this value between  0.001 and 1.0
		b.readRegister(s, lfo);
		//		mulx pot0 ;multiply by pot value 
		if(Control1 != -1)
			b.mulx(Control1);
		//		rdax c,1 ;read the c register
		b.readRegister(c, 1);
		//		wrax c,-0.02 ;integrate the c value, this value MUST be the negative of
		b.writeRegister(c,  -lfo);
		//		;what ever you set the value in 'rdax s,X' to above 
		//		mulx pot0 ;multiply by pot value
		if(Control1 != -1)
			b.mulx(Control1);
		//		rdax s,1 ;read s reg
		b.readRegister(s, 1);
		//		wrax s,1 ;integrate the s value 
		if(Control2 != -1) {
			int s_output = b.allocateReg();
			b.writeRegister(s, 1.0);
			b.mulx(Control2);
			b.writeRegister(s_output,  0.0);
			
		}
		else {
			b.writeRegister(s, 0);
		}
		
		
		//		;Either the s or c register will be producing s waveforms (just shifted in 
		//		;phase), so either can be used as a modulation source. The maximum 
		//		;frequency of this LFO is Fs/2pi, which should be high enough! 

		this.getPin("Sine Out").setRegister(s);
		this.getPin("Cosine Out").setRegister(c);
	}

	//====================================================
	public double getLFO() {
		return lfo;
	}

	public void setLFO(double d) {
		lfo = d;
	}
}
