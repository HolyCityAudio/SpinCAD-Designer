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


public class RichChorusCADBlock extends ModulationCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3895304944418909090L;

	public RichChorusCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		setName("Rich Chorus");
	}
	
	public void generateCode() {

//cho	rda,sin0,sin|reg|compc,cdel+800
//cho	rda,sin0,sin,cdel+801
//wrax	c1,0
//cho	rda,sin0,sin|reg|compa,cdel+400
//cho	rda,sin0,sin|compa,cdel+401
		//wrax	c2,0
		//cho	rda,sin0,cos|reg|compc,cdel+1100
		//cho	rda,sin0,cos,cdel+1101
		//wrax	c3,0
		//cho	rda,sin0,cos|reg|compa,cdel+1400
		//cho	rda,sin0,cos|compc,cdel+1401
		//wrax	c4,0
	}
}
