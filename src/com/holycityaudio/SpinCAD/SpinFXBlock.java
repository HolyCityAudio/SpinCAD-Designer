/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADFXBlock.java
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

// SpinCADFXBlock.java is an important interface between SpinCAD and ElmGen
// as it allocates unique names to memory blocks even if the same block is
// used several times in a model.

package com.holycityaudio.SpinCAD;
import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.MemSegment;

public class SpinFXBlock extends ElmProgram {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numBlocks = 0;
	private int numRegs = 0;		// allocation scheme for internal registers REG0 through REG31

	public SpinFXBlock(String nameBlock) {
		super(nameBlock);
		setNumBlocks(getNumBlocks() + 1);
		setNumRegs(REG0);
//		System.out.printf("SpinFXBlock - numBlocks = %d\n", numBlocks);	
	}

	public void setName(String newName) {
		name = newName;
	}
	
	public int allocateReg() {
		//		  System.out.println("SpinFXBlock allocateReg");
		int retval = getNumRegs();
		setNumRegs(getNumRegs() + 1);
		return retval;
	}

	public void FXallocDelayMem(String memName, int size) {
//		System.out.println("SpinFXBlock FXallocDelayMem " + memName + numBlocks);
		allocDelayMem(memName + getNumBlocks(), size);
	}

	// TODO this overloaded function allows use of a double as the size parameter, to deal with
	// (temporary) shortcomings in the parsing of "equ" statements - can't make it create an int
	// at this time
	
	public void FXallocDelayMem(String memName, double size) {
//		System.out.println("SpinFXBlock FXallocDelayMem " + memName + numBlocks);
		allocDelayMem(memName + getNumBlocks(), (int) size);
	}
	
	public void FXreadDelay(String memName, int offset, double param) {
		readDelay(getAddrFromSpinMem(memName, offset), param);
	}  

	public void FXreadDelay(String memName, double offset, double param) {
		//		System.out.println("SpinFXBlock FXreadDelay " + memName + numBlocks);
		readDelay(memName + getNumBlocks(), offset, param);
	}  

	public void FXwriteDelay(String memName, int offset, double param) {
		writeDelay(getAddrFromSpinMem(memName, offset), param);
	}  

	// GSW added this for interface to SpinCAD Builder 11/23/2013
	public void FXwriteAllpass(String memName, int offset, double param) {
		writeAllpass(getAddrFromSpinMem(memName, offset), param);
	}  

	public void FXchorusReadDelay(int lfo, int regs, String memName, int offset) {
		chorusReadDelay(lfo, regs, getAddrFromSpinMem(memName, offset));
	}  

	public int getNumRegs() {
		return numRegs;
	}

	public void setNumRegs(int nRegs) {
		numRegs = nRegs;
	}

	public int getNumBlocks() {
//		System.out.println("getNumblocks = " + numBlocks);
		return numBlocks;
	}

	public void setNumBlocks(int num) {
		numBlocks = num;
//		System.out.println("setNumblocks = " + numBlocks);
	}

	int getAddrFromSpinMem(String memName, int offset) {
		
		int address = 0;

			String buffer = memName;
			if(buffer.contains("#")) {
				address = 2;
				buffer = buffer.replace("#", "");
			}
			else if (buffer.contains("^")) {
				address = 1;
				buffer = buffer.replace("^", "");			
			}
			if(buffer.endsWith("+")) {
				buffer = buffer.replace("+", "");
				buffer = buffer + getNumBlocks();
				MemSegment seg = getDelayMemByName(buffer);
				address = seg.getStart() + ((address * getDelayMemByName(buffer).getLength())/2 + offset);
			}
			else if(buffer.endsWith("-")) {
				buffer = buffer.replace("-", "");
				buffer = buffer + getNumBlocks();
				MemSegment seg = getDelayMemByName(buffer);
				address = seg.getStart() + ((address * getDelayMemByName(buffer).getLength())/2 - offset);
			}
			else {
				buffer = buffer + getNumBlocks();			
				MemSegment seg = getDelayMemByName(buffer);
				address = seg.getStart() + ((address * getDelayMemByName(buffer).getLength())/2);	
			}
			return address;
		}		
}