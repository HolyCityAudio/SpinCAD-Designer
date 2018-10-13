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

import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ConstantCADBlock extends ControlCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7025985649946130854L;
	int value = 1;
	double constant = 0.9990234375;

	public ConstantCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		addControlOutputPin(this, "Value");	//	
		setName();
	}

	public void setName() {
		setName("Constant");
	}

	public void generateCode(SpinFXBlock sfxb) {

		sfxb.comment(getName());
		int value = sfxb.allocateReg();

		sfxb.scaleOffset(0.0, constant);
		sfxb.writeRegister(value, 0.0);
		this.getPin("Value").setRegister(value);

		System.out.println("Constant code gen!");
	}

	public void editBlock(){
		new ConstantControlPanel(this);
	}
	//====================================================


	public void setConstant(int value) {
		constant = (double) value/1000;
	}
	
	public int getConstant() {
		return (int) (constant * 1000);
	}

}
