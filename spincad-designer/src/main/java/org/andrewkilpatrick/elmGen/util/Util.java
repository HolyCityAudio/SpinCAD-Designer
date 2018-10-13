/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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

	// GSW added for integration with SpinCAD Designer	
	/**
	 * Converts a register value to a double. Valid range is -0x800000 to 0x7fffff.
	 * 
	 * @param val the value from -0x800000 to 0x7fffff
	 * @return the same value, however limited to the above range.
	 */

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

	public static String getRegisterName(int addr) {
		switch (addr) {
		case 0:
			return "SIN0_RATE";
		case 1:
			return "SIN0_RANGE";
		case 2:
			return "SIN1_RATE";
		case 3:
			return "SIN1_RANGE";
		case 4:
			return "RMP0_RATE";
		case 5:
			return "RMP0_RANGE";
		case 6:
			return "RMP1_RATE";
		case 7:
			return "RMP1_RANGE";
		case 16:
			return "POT0";
		case 17:
			return "POT1";
		case 18:
			return "POT2";
		case 20:
			return "ADCL";
		case 21:
			return "ADCR";
		case 22:
			return "DACL";
		case 23:
			return "DACR";
		case 24:
			return "ADDR_PTR";
		case 32:
			return "REG0";
		case 33:
			return "REG1";
		case 34:
			return "REG2";
		case 35:
			return "REG3";
		case 36:
			return "REG4";
		case 37:
			return "REG5";
		case 38:
			return "REG6";
		case 39:
			return "REG7";
		case 40:
			return "REG8";
		case 41:
			return "REG9";
		case 42:
			return "REG10";
		case 43:
			return "REG11";
		case 44:
			return "REG12";
		case 45:
			return "REG13";
		case 46:
			return "REG14";
		case 47:
			return "REG15";
		case 48:
			return "REG16";
		case 49:
			return "REG17";
		case 50:
			return "REG18";
		case 51:
			return "REG19";
		case 52:
			return "REG20";
		case 53:
			return "REG21";
		case 54:
			return "REG22";
		case 55:
			return "REG23";
		case 56:
			return "REG24";
		case 57:
			return "REG25";
		case 58:
			return "REG26";
		case 59:
			return "REG27";
		case 60:
			return "REG28";
		case 61:
			return "REG29";
		case 62:
			return "REG30";
		case 63:
			return "REG31";
		default:
			return String.valueOf(addr);
		}
	}
	
	public static String removeComma(double number) {
		String s = Double.toString(number);
		return s.replace(",", ".");
	}
	
	public static String removeComma(String s) {
		return s.replace(",", ".");
	}
}
