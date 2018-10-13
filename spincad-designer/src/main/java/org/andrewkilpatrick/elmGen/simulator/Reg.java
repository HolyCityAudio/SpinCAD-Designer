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
package org.andrewkilpatrick.elmGen.simulator;

import java.io.Serializable;

import org.andrewkilpatrick.elmGen.util.Util;

/**
 * This class represents an internal 24 bit register.
 */
public class Reg implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5197716260964881922L;
	
	int value;

	/**
	 * Creates a new register.
	 */
	public Reg() {
		this.value = 0;
	}

	/**
	 * Creates a new register. The value is internally clamped to 24 bits.
	 * 
	 * @param val the initial value to load into the register
	 */
	public Reg(int val) {
		this.value = clamp(val);
	}
	
	/**
	 * Returns the value of the register.
	 * 
	 * @return the value
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * Sets the value of the register. The value is internally clamped to 24 bits.
	 * 
	 * @param newValue the value to set
	 */
	public void setValue(int newValue) {
		this.value = clamp(newValue);
	}
	
	/**
	 * Clears the ACC.
	 */
	public void clear() {
		value = 0;
	}
	
	/**
	 * Adds a term to the current value of the reg. The term and value are
	 * clamped to 24 bits.
	 * 
	 * @param term the term to add
	 */
	public void add(int term) {
		value = clamp(value + clamp(term));
	}

	/**
	 * Adds a term to the current value of the reg. The term and value are
	 * clamped to 24 bits. Valid range for the term is -1.0 to +0.999...
	 * 
	 * @param term the term to add
	 */
	public void add(double term) {
		value = clamp(value + Util.doubleToReg(term));
	}
	
	/**
	 * Subtracts the term from the current value of the reg. The term and value
	 * are clamped to 24 bits.
	 * 
	 * @param term the term to subtract from the reg
	 */
	public void subtract(int term) {
		value = clamp(value - clamp(term));
	}
	
	/**
	 * Multiplies the reg by a 16 bit coefficient. The coeff is clamped to
	 * 16 bits. The result is clamped to 24 bits.
	 * 
	 * @param coeff the coeff to multiply the reg by
	 */
	public void mult(int coeff) {
		int mul = coeff;
		// clamp to 16 bits
		if(mul > 0x7fff) {
			mul = 0x7ffff;
		}
		else if(mul < -0x8000) {
			mul = -0x8000;
		}
		value = clamp((int)(((long)value * (long)mul) >> 14));
	}

	/**
	 * Multiplies the reg by a double coefficient. The coeff is
	 * clamped to a range of: -2.0 to +1.99993896484. The result
	 * is clamped to 24 bits.
	 * 
	 * @param coeff the coeff to multiply the reg by
	 */
	public void scale(double coeff) {
		double mult = coeff;
		int multInt = Util.doubleToScale(mult) >> 8;
		value = clamp((int)(((long)value * (long)multInt) >> 14));
	}
	
	/**
	 * Makes the reg positive.
	 */
	public void abs() {
		value = Math.abs(value);
	}
	
	/**
	 * ANDs the reg with a mask.
	 * 
	 * @param mask the mask
	 */
	public void and(int mask) {
		int res = (value & mask);
		// XXX debug GSW
		value = extendNeg(res);
	}
	
	/**
	 * Negates all bits in the reg.
	 */
	public void not() {
		value = ~(value & 0xffffffff);
	}
	
	/**
	 * ORs the reg with a mask.
	 * 
	 * @param mask the mask
	 */
	public void or(int mask) {
		int res = (value | mask);
		value = extendNeg(res);
	}
	
	/**
	 * XORs the reg with a mask.
	 * 
	 * @param mask the mask
	 */
	
	// XXX debug GSW
	
	public void xor(int mask) {
		int res = value ^ mask;
		value = extendNeg(res);
	}
	
	/**
	 * Loads the reg with the abs value of val if it is larger than the 
	 * the abs value of reg.
	 * 
	 * @param testValue the value to compare to
	 */
	public void maxx(int testValue) {
		if(Math.abs(testValue) > Math.abs(value)) {
			value = Math.abs(testValue);
		}
		else {
			value = Math.abs(value);
		}
	}
	
	/**
	 * Clamps a 24 bit value.
	 * 
	 * @param clampVal the value to clamp
	 * @return the clamped value
	 */
	private static int clamp(int clampVal) {
		if(clampVal > 0x7fffff) {
			return 0x7fffff;
		}
		if(clampVal < -0x800000) {
			return -0x800000;
		}
		return clampVal;
	}
	
	
	/**
	 * Clamps a 24 bit value.
	 * 
	 * @param clampVal the value to clamp
	 * @return the clamped value
	 */
	private static int extendNeg(int val) {
		if((val & 0x80_0000) != 0) {
			return val | 0xff00_0000;
		} else {
			return val;
		}
	}
	
	/**
	 * Test main for Reg().
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		Reg acc = new Reg();
		
		acc.clear();
		showAcc(" - acc: ", acc);
		acc.or(0xFF_FFFE);
		showAcc(" - or: ", acc);
		acc.not();
		showAcc(" - not: ", acc);
		acc.and(0x01);
		showAcc(" - and: ", acc);
		
		acc.setValue(0x7A_AAAA);
		showAcc(" - acc: ", acc);
		acc.xor(0xFF_FFFF);
		showAcc(" - xor: ", acc);
		acc.and(0xFF_FFFF);
		showAcc(" - and: ", acc);
		acc.and(0xF_ffff);
		showAcc(" - and: ", acc);
		acc.and(0x0_ffff);
		showAcc(" - and: ", acc);
		acc.and(0x0_0fff);
		showAcc(" - and: ", acc);

		acc.and(0x0_00ff);
		showAcc(" - and: ", acc);

		acc.and(0x0_000f);
		showAcc(" - and: ", acc);

		System.out.println("");

		/*
		for(int i = -0x810000; i < 0x810000; i += 0x7fe0) {
			acc.setValue(i);
			acc.add(100);
			acc.mult(-0x8000);
			acc.scale(-1.0);
			acc.and(0xffffff);
			acc.or(0x000fff);
			acc.xor(0x435);
			acc.xor(0xD80000);
			acc.not();
			acc.abs();
			System.out.println("i: " + i + " - " + String.format("0x%08x", i) +
					" - acc: " + acc.getValue() + " - " + String.format("0x%08x", acc.getValue()));
		}
		*/
		
		for(double i = -2.1; i < 2.1; i += 0.01) {
//			int val = Util.doubleToScale(i);
//			System.out.println("i: " + i + " - val: " + val);
		}
		
		for(int i = -0x810000; i < 0x810000; i += 0x7fe0) {
//			double val = Util.regToDouble(i);
//			System.out.println("i: " + i + " - val: " + val);
		}		
	}

	static void showAcc(String label, Reg acc) {
		System.out.println(label + String.format("0x%08x", acc.getValue()) +  " - " + acc.getValue());
	}
}
