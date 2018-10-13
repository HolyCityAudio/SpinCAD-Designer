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

public class InputCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4680315672929089295L;

	public InputCADBlock(int x, int y) {
		super(x, y);
		addOutputPin(this, "Output 1");
		addOutputPin(this, "Output 2");
		setName("Input");
	}
	
	public void generateCode(SpinFXBlock eP) {
//		System.out.println("Input codegen!");
		SpinCADPin p = this.getPin("Output 1");
		p.setRegister(ADCL);
		p = this.getPin("Output 2");
		p.setRegister(ADCR);
		eP.comment("Input");
	}
}
