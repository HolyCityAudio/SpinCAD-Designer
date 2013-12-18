/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick
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
package org.andrewkilpatrick.elmGen.util;

public class Util {
	public static void printBuffer(byte buf[]) {
		for(int i = 0; i < buf.length; i ++) {
			System.out.print(String.format("%02X ", (buf[i] & 0xff)));
			if((i & 0x07) == 0x07) {
				System.out.println("");
			}
		}
	}
	
	/**
	 * Converts a register value to a double. Valid range is -0x800000 to 0x7fffff.
	 * 
	 * @param val the value from -0x800000 to 0x7fffff
	 * @return the double from -1.0 to +0.999...
	 */
	public static double regToDouble(int val) {
		int value = val;
		if(value > 0x7fffff) {
			value = 0x7fffff;
		}
		if(value < -0x800000) {
			value = -0x800000;
		}
		return (double)value / 8388608.0;
	}
	
	public static int regToInt(int val) {
		int value = val;
		if(value > 0x7fffff) {
			value = 0x7fffff;
		}
		if(value < -0x800000) {
			value = -0x800000;
		}
		return value;
	}
	
	/**
	 * Converts a double to a 24 bit register value. Valid range is -2.0 to +1.999...
	 * 
	 * @param val the value from -2.0 to +1.999...
	 * @return the register value from -0x800000 to 0x7fffff
	 */
	public static int doubleToScale(double val) {
		int temp = (int)(val * (double)0x400000);
		if(temp > 0x7fffff) {
			temp = 0x7fffff;
		}
		if(temp < -0x800000) {
			temp = -0x800000;
		}
		return temp;
	}
	
	/**
	 * Converts a double to a 24 bit register value. Valid range is -1.0 to +0.999...
	 * 
	 * @param val the value from -1.0 to +0.999...
	 * @return the register value from -0x800000 to 0x7fffff
	 */
	public static int doubleToReg(double val) {
		int temp = (int)(val * (double)0x800000);
		if(temp > 0x7fffff) {
			temp = 0x7fffff;
		}
		if(temp < -0x800000) {
			temp = -0x800000;
		}
		return temp;
	}
}
