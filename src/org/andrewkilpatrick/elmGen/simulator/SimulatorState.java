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
package org.andrewkilpatrick.elmGen.simulator;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class SimulatorState {
	private int pc = 0;
	private Reg acc;
	private Reg pacc;
	private Reg lr;
	private Reg regs[];
	private int delay[];
	private int delayp;
	private SinLFO sinLFO[];
	private RampLFO rampLFO[];
	private boolean firstRun = true;
	String debugFilename = "simulator-debug.txt";

	
	public SimulatorState(String Filename) {
		debugFilename = Filename;
		simulatorInit();
	}
	
	public SimulatorState() {
		simulatorInit();
	}
	
	public void simulatorInit() {
		acc = new Reg(0);
		pacc = new Reg(0);
		lr = new Reg(0);
		regs = new Reg[64];
		delay = new int[32768];
		sinLFO = new SinLFO[2];
		rampLFO = new RampLFO[2];
		
		try {
			FileOutputStream fos = new FileOutputStream(debugFilename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 

		
		for(int i = 0; i < regs.length; i ++) {
			regs[i] = new Reg(0);
		}
		
		for(int i = 0; i < delay.length; i ++) {
			delay[i] = 0;
		}
		
		for(int i = 0; i < sinLFO.length; i ++) {
			sinLFO[i] = new SinLFO(this, i);
		}
		
		for(int i = 0; i < rampLFO.length; i ++) {
			rampLFO[i] = new RampLFO(this, i);
		}
		
	}
	
	/**
	 * Increments the state of all simulator parts
	 * after each instruction is run.
	 */
	public void sampleIncrement() {
		firstRun = false;
		delayp--;
		if(delayp == -32768) {
			delayp = 0;
		}
		pacc.setValue(acc.getValue());
		acc.clear();
		for(int i = 0; i < sinLFO.length; i ++) {
			sinLFO[i].increment();
		}
		for(int i = 0; i < rampLFO.length; i ++) {
			rampLFO[i].increment();
		}
	}
	
	public void resetPC() {
		pc = 0;
	}
	
	public int getPC() {
		return pc;
	}
	
	public void incrementPC() {
		pc ++;
	}
	
	public void skipInst(int num) {
		pc += num;
	}

	public boolean isFirstRun() {
		return firstRun;
	}
	
	public int getACCVal() {
		return acc.getValue();
	}
	
	public Reg getACC() {
		return acc;
	}

	public void setACCVal(int val) {
		acc.setValue(val);
	}

	public Reg getPACC() {
		return pacc;
	}

	public int getPACCVal() {
		return pacc.getValue();
	}
	
	public int getRegVal(int reg) {
		if(reg < 0 || reg > regs.length - 1) {
			throw new IllegalArgumentException("reg out of range: " + reg);
		}
		return regs[reg].getValue();
	}
	
	public void setRegVal(int reg, int value) {
		if(reg < 0 || reg > regs.length - 1) {
			throw new IllegalArgumentException("reg out of range: " + reg);
		}
		regs[reg].setValue(value);
	}
	
	public int getDelayVal(int offset) {
		if(offset < 0 || offset > delay.length - 1) {
			throw new IllegalArgumentException("delay offset out of range: " + offset);
		}
//		int val = delay[(offset + delayp) & 0x7fff];  // use this line to bypass the compressor
		int index = (offset + delayp) & 0x7fff;
		int val = (int)DelayCompressor.decompress(delay[index]);

		lr.setValue(val);
		return val;
	}
	
	public void setDelayVal(int offset, int value) {
		if(offset < 0 || offset > delay.length - 1) {
			throw new IllegalArgumentException("delay offset out of range: " + offset);
		}
//		delay[(offset + delayp) & 0x7fff] = value;  // use this line to bypass the compressor	
		int index = (offset + delayp) & 0x7fff;

		delay[index] = (int)DelayCompressor.compress(value);		
	}

	public int getLRVal() {
		return lr.getValue();
	}	
	
	public void jamRampLFO(int lfo) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("ramp lfo is out of range: " + lfo);
		}
		rampLFO[lfo].jam();
	}
	
	public void jamSinLFO(int lfo) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("sin lfo is out of range: " + lfo);
		}
		sinLFO[lfo].jam();
	}
	
	public int getRampLFOVal(int lfo) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("ramp lfo is out of range: " + lfo);
		}
		return rampLFO[lfo].getValue();
	}

	public int getRampXfadeVal(int lfo) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("ramp lfo is out of range: " + lfo);
		}
		return rampLFO[lfo].getXfade();
	}
	
	public int getRampLFORptr2Val(int lfo) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("ramp lfo is out of range: " + lfo);
		}
		return rampLFO[lfo].getRptr2Value();
	}
	
	public int getRampLFOAmp(int lfo) {
		if(lfo < 0 || lfo > 1) {
			throw new IllegalArgumentException("ramp lfo is out of range: " + lfo);
		}
		return rampLFO[lfo].getAmp();		
	}
	
	public int getSinLFOVal(int lfo) {
		if(lfo < 0 || lfo > 3) {
			throw new IllegalArgumentException("sin lfo is out of range: " + lfo);
		}
		// sin
		if(lfo < 2) {
			return sinLFO[lfo].getSinValue();
		}
		// cos
		return sinLFO[lfo - 2].getCosValue();
	}
}
