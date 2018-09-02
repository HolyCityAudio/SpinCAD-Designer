/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADControlPanel.java
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

// I'm not exactly sure what this is doing here...
// Must have been a good idea at one time 8^)
package com.holycityaudio.SpinCAD;

import org.andrewkilpatrick.elmGen.ElmProgram;
import java.io.Serializable;

public class spinCADControlPanel implements Serializable {
	
	protected double coeffToLFORate(double rate) {
		 return (ElmProgram.getSamplerate() * rate) / (2 * Math.PI * Math.pow(2.0, 17));
	}
}
