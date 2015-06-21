/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADBlock.java
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

package com.holycityaudio.SpinCAD;

import java.awt.Color;

public class SpinCADBank {

	/**
	 * SpinCADBlock class extends the idea of a functional block
	 * to a graphical idea with inputs and outputs
	 * 
	 */
	
	String bankName = "Empty Bank";
	String[] bankFileName;
	SpinCADModel[] bankModel;
	
	private static final long serialVersionUID = -3067235375662565795L;

	int index = 0;


	/**
	 * SpinCADBlock class extends the idea of a functional block
	 * to a graphical idea with inputs and outputs
	 * 
	 * @param x the x-location on the screen of the block's upper left corner
	 * @param y the y-location on the screen of the block's upper left corner
	 */

	public SpinCADBank(int x, int y) {
		bankFileName = new String[8];
		bankModel = new SpinCADModel[8];
	}

	/**
	 * SpinCADBlock constructor
	 * 
	 * @param x the x-location on the screen of the block's upper left corner
	 * @param y the y-location on the screen of the block's upper left corner
	 * @param border color of the border
	 * @param connector - color f the little connector circles
	 * 
	 */
}