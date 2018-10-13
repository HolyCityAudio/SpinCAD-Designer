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
package org.andrewkilpatrick.elmGen;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.andrewkilpatrick.elmGen.instructions.Absa;
import org.andrewkilpatrick.elmGen.instructions.And;
import org.andrewkilpatrick.elmGen.instructions.ChorusReadDelay;
import org.andrewkilpatrick.elmGen.instructions.ChorusReadValue;
import org.andrewkilpatrick.elmGen.instructions.ChorusScaleOffset;
import org.andrewkilpatrick.elmGen.instructions.Clear;
import org.andrewkilpatrick.elmGen.instructions.Comment;
import org.andrewkilpatrick.elmGen.instructions.Exp;
import org.andrewkilpatrick.elmGen.instructions.Instruction;
import org.andrewkilpatrick.elmGen.instructions.Jam;
import org.andrewkilpatrick.elmGen.instructions.LoadAccumulator;
import org.andrewkilpatrick.elmGen.instructions.LoadRampLFO;
import org.andrewkilpatrick.elmGen.instructions.LoadSinLFO;
import org.andrewkilpatrick.elmGen.instructions.Log;
import org.andrewkilpatrick.elmGen.instructions.Maxx;
import org.andrewkilpatrick.elmGen.instructions.Mulx;
import org.andrewkilpatrick.elmGen.instructions.Not;
import org.andrewkilpatrick.elmGen.instructions.Or;
import org.andrewkilpatrick.elmGen.instructions.ReadDelay;
import org.andrewkilpatrick.elmGen.instructions.ReadDelayPointer;
import org.andrewkilpatrick.elmGen.instructions.ReadRegister;
import org.andrewkilpatrick.elmGen.instructions.ReadRegisterFilter;
import org.andrewkilpatrick.elmGen.instructions.ScaleOffset;
import org.andrewkilpatrick.elmGen.instructions.Skip;
import org.andrewkilpatrick.elmGen.instructions.WriteAllpass;
import org.andrewkilpatrick.elmGen.instructions.WriteDelay;
import org.andrewkilpatrick.elmGen.instructions.WriteRegister;
import org.andrewkilpatrick.elmGen.instructions.WriteRegisterHighshelf;
import org.andrewkilpatrick.elmGen.instructions.WriteRegisterLowshelf;
import org.andrewkilpatrick.elmGen.instructions.Xor;

/**
 * This class represents a program on the DSP. Effect programs should subclass
 * this class and call the appropriate methods to set up and test the program.
 * 
 * @author andrew
 */
public class ElmProgram implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3718907348318020286L;
	protected String name;
	private List<MemSegment> memoryMap;
	private List<Instruction> instList;
	private int nComments = 0;

	public static final int MAX_DELAY_MEM = 32767;
	public static final int MAX_CODE_LEN = 128;
	public static int SAMPLERATE = 32768;

	public static final int SIN0_RATE = 0x00;
	public static final int SIN0_RANGE = 0x01;
	public static final int SIN1_RATE = 0x02;
	public static final int SIN1_RANGE = 0x03;
	public static final int RMP0_RATE = 0x04;
	public static final int RMP0_RANGE = 0x05;
	public static final int RMP1_RATE = 0x06;
	public static final int RMP1_RANGE = 0x07;
	public static final int POT0 = 0x10;
	public static final int POT1 = 0x11;
	public static final int POT2 = 0x12;
	public static final int ADCL = 0x14;
	public static final int ADCR = 0x15;
	public static final int DACL = 0x16;
	public static final int DACR = 0x17;
	public static final int ADDR_PTR = 0x18;
	public static final int REG0 = 0x20;
	public static final int REG1 = 0x21;
	public static final int REG2 = 0x22;
	public static final int REG3 = 0x23;
	public static final int REG4 = 0x24;
	public static final int REG5 = 0x25;
	public static final int REG6 = 0x26;
	public static final int REG7 = 0x27;
	public static final int REG8 = 0x28;
	public static final int REG9 = 0x29;
	public static final int REG10 = 0x2a;
	public static final int REG11 = 0x2b;
	public static final int REG12 = 0x2c;
	public static final int REG13 = 0x2d;
	public static final int REG14 = 0x2e;
	public static final int REG15 = 0x2f;
	public static final int REG16 = 0x30;
	public static final int REG17 = 0x31;
	public static final int REG18 = 0x32;
	public static final int REG19 = 0x33;
	public static final int REG20 = 0x34;
	public static final int REG21 = 0x35;
	public static final int REG22 = 0x36;
	public static final int REG23 = 0x37;
	public static final int REG24 = 0x38;
	public static final int REG25 = 0x39;
	public static final int REG26 = 0x3a;
	public static final int REG27 = 0x3b;
	public static final int REG28 = 0x3c;
	public static final int REG29 = 0x3d;
	public static final int REG30 = 0x3e;
	public static final int REG31 = 0x3f;

	// GSW - I am adding code to track allocation of the SIN and RAMP LFOs
	// Otherwise it can be hard to tell which block is using what
	// counter needs to be cleared prior to a generateCode() and will increment
	// every time a WLDR or WLDS is called (once per block, since with conditional
	// paths it's conceivable that more than one reference might exist in one block
	// and still be OK

	public int usedSINLFO0 = 0;
	public int usedSINLFO1 = 0;
	public int usedRMPLFO0 = 0;
	public int usedRMPLFO1 = 0;

	// skip flags
	// GSW - I changed all register and flag names back to Spin definitions
	// although upper case.  This allows direct use of the parsed string
	// from a Spin ASM file, after conversion to upper case.
	// It's also less confusing as all documentation uses Spin names.

	public static final int NEG = 0x01;
	public static final int GEZ = 0x02;
	public static final int ZRO = 0x04;
	public static final int ZRC = 0x08;
	public static final int RUN = 0x10;

	// chorus flags
	public static final int SIN0 = 0;
	public static final int SIN1 = 1;
	// these are correct sir!
	public static final int RMP0 = 2;
	public static final int RMP1 = 3;
	public static final int COS0 = 4;
	public static final int COS1 = 5;
	public static final int SIN = 0x00;
	public static final int COS = 0x01;
	public static final int REG = 0x02;
	public static final int COMPC = 0x04;
	public static final int COMPA = 0x08;
	public static final int RPTR2 = 0x10;
	public static final int NA = 0x20;

	/**
	 * Creates a new ElmProgram with a blank memory map and instruction list.
	 * The default samplerate is 32768Hz.
	 * 
	 * @param name
	 *            the program name
	 */
	public ElmProgram(String name) {
		this.name = name;
		memoryMap = new LinkedList<MemSegment>();
		instList = new LinkedList<Instruction>();
		nComments = 0;
	}

	public static void setSamplerate(int samplerate) {
		if (samplerate < 32000 || samplerate > 48000) {
			throw new ElmProgramException(
					"samplerate must be: 32000 to 48000Hz");
		}
		ElmProgram.SAMPLERATE = samplerate;
	}

	public static int getSamplerate() {
		return ElmProgram.SAMPLERATE;
	}

	/**
	 * Gets the name of the program.
	 * 
	 * @return the program name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Allocates a segment of delay memory.
	 * 
	 * @param name
	 *            the segment name
	 * @param size
	 *            the number of samples of memory to allocate
	 */
	public void allocDelayMem(String name, int size) {
		String name2 = name.toUpperCase();
		for (int i = 0; i < memoryMap.size(); i++) {
			if (memoryMap.get(i).getName().equals(name2)) {
				throw new ElmProgramException(
						"attempt to allocate memory segment "
								+ "with name that is already used: " + name2);
			}
		}
		int offset = getDelayMemAllocated() + memoryMap.size();
		if (size < 1) {
			throw new ElmProgramException("attempt to allocate memory segment "
					+ "with size < 1");
		}
		if ((offset + size) > MAX_DELAY_MEM) {
			// throw new
			// ElmProgramException("not enough space to allocate memory segment "
			// +
			// "of size: " + size + " space available: " +
			// (MAX_DELAY_MEM - getDelayMemAllocated()));
			// DEBUG should actually throw exception and catch it farther up
			// yeah that would be great actually since it throws this error for every memory block!
			//			JFrame frame = new JFrame();
			//			JOptionPane.showMessageDialog(frame, "Not enough memory!\n"
			//					+ "Adjust delays and chorus blocks to reduce memory.",
			//					"Out of Memory", JOptionPane.OK_OPTION);

		}
		memoryMap.add(new MemSegment(name2, size, offset));
	}

	/**
	 * Gets a delay memory segment by name.
	 * 
	 * @param name
	 *            the segment name
	 * @return the MemSegment referenced
	 */
	protected MemSegment getDelayMemByName(String name) {
		String name2 = name.toUpperCase();
		for (int i = 0; i < memoryMap.size(); i++) {
			MemSegment seg = memoryMap.get(i);
			if (seg.getName().equals(name2)) {
				return seg;
			}
		}
		throw new ElmProgramException("segment not found: " + name2);
	}

	/**
	 * Gets the total amount of delay memory allocated in samples.
	 * 
	 * @return the number of samples of delay memory allocated
	 */
	public int getDelayMemAllocated() {
		int alloc = 0;
		for (int i = 0; i < memoryMap.size(); i++) {
			alloc += memoryMap.get(i).getLength();
		}
		return alloc;
	}

	/**
	 * Gets the total amount of delay memory in the system.
	 * 
	 * @return the total amount of delay memory in the system
	 */
	public int getMaxDelayMem() {
		return MAX_DELAY_MEM;
	}

	/**
	 * Gets the memory map as a String.
	 * 
	 * @return the memory map as a String
	 */
	public String getMemoryMap() {
		String str = "Memory Map:\n";
		for (int i = 0; i < memoryMap.size(); i++) {
			str += memoryMap.get(i).toString() + "\n";
		}
		return str;
	}

	/**
	 * Checks the current code length.
	 * 
	 * @throws ElmProgramException
	 *             if the code length is max.
	 */
	public void checkCodeLen() {
		if ((getCodeLen() - getNumComments()) >= MAX_CODE_LEN) {

			// throw new ElmProgramException("max program length reached: " +
			// MAX_CODE_LEN);
			JFrame frame = new JFrame();
//			JOptionPane.showMessageDialog(frame, "Too many instructions!\n"
//					+ "Please remove a block.",
//					"Ran out of instructions!", JOptionPane.OK_OPTION);
		}
	}

	/**
	 * Returns the number of instructions in the program.
	 * 
	 * @return the number of instructions in the program
	 */
	public int getCodeLen() {
		return instList.size();
	}

	public int getNumComments() {
		return nComments;
	}




	/**
	 * Gets the maximum number of instructions supported.
	 * 
	 * @return the maximum number of instructions supported
	 */
	public int getMaxCodeLen() {
		return MAX_CODE_LEN;
	}

	/**
	 * Gets an instruction by number.
	 * 
	 * @param num
	 *            the Instruction index
	 * @return the Instruction
	 */
	public Instruction getInstruction(int num) {
		return instList.get(num);
	}

	/**
	 * Generates the hex output from the current instruction list.
	 * 
	 * @return an int[] containing the hex words for the program
	 */
	public int[] generateHex() {
		int hex[] = new int[instList.size()];
		for (int i = 0, j = 0; i < instList.size(); i++) {
			int ii = instList.get(i).getHexWord();
			if(ii != -1) {
				hex[j] = ii; 
//				System.out.printf("%08x\n", hex[j]);
				j++;
			}
		}
		return hex;
	}

	/**
	 * Gets machine code strings.
	 * 
	 * @return a String[] with the machine codes as Strings
	 */
	public String[] getMachineCodeStrings() {
		String str[] = new String[instList.size()];
		for (int i = 0; i < instList.size(); i++) {
			str[i] = String.format("%08X", instList.get(i).getHexWord());
		}
		return str;
	}

	/**
	 * Gets machine code listing.
	 * 
	 * @return a printable machine code listing
	 */
	public String getProgramListing() {
		String str = "Program: " + name + "\n";
		for (int i = 0; i < instList.size(); i++) {
			Instruction inst = instList.get(i);
			str += String.format("%08X", inst.getHexWord()) + " - ";
			str += inst.getInstructionString() + "\n";
		}
		return str;
	}

	public String getProgramListing(int mode) {
		if (mode == 1) {
			String str = "; ----------------------------\n";
			for (int i = 0; i < instList.size(); i++) {
				Instruction inst = instList.get(i);
				// str += String.format("%08X", inst.getHexWord()) + " - ";
				str += inst.getInstructionString(1) + "\n";
			}
			return str;
		} else
			return "Error! Invalid mode.";
	}

	/*
	 * INSTRUCTIONS
	 */
	/**
	 * Scales the contents of ACC and adds an offset.
	 * 
	 * @param scale
	 *            the scale factor
	 * @param offset
	 *            the amount to add to the ACC after scaling
	 */
	public void scaleOffset(double scale, double offset) {
		//checkCodeLen();
		instList.add(new ScaleOffset(scale, offset));
	}

	/**
	 * ANDs the ACC with a mask.
	 * 
	 * @param mask
	 *            the mask to AND the ACC with
	 */
	public void and(int mask) {
		//checkCodeLen();
		instList.add(new And(mask));
	}

	/**
	 * ORs the ACC with a mask.
	 * 
	 * @param mask
	 *            the mask to OR the ACC with
	 */
	public void or(int mask) {
		//checkCodeLen();
		instList.add(new Or(mask));
	}

	/**
	 * XORs the ACC with a mask.
	 * 
	 * @param mask
	 *            the mask to XOR the ACC with
	 */
	public void xor(int mask) {
		//checkCodeLen();
		instList.add(new Xor(mask));
	}

	/**
	 * Multiplies the base 2 absolute value of the ACC with the scale value and
	 * then adds an offset.
	 * 
	 * @param scale
	 *            the amount to scale the result by
	 * @param offset
	 *            the amount of offset to add to the scaled result
	 */
	public void log(double scale, double offset) {
		//checkCodeLen();
		instList.add(new Log(scale, offset));
	}

	/**
	 * Scales 2^ACC by scale and adds offset.
	 * 
	 * @param scale
	 *            the amount to scale the result by
	 * @param offset
	 *            the amount of offset to add to the scaled result
	 */
	public void exp(double scale, double offset) {
		//checkCodeLen();
		instList.add(new Exp(scale, offset));
	}

	/**
	 * Skips instructions conditionally.
	 * 
	 * @param flags
	 *            the condition flags
	 * @param nskip
	 *            the number of instructions to skip if the conditions are met
	 */
	public void skip(int flags, int nskip) {
		//checkCodeLen();
		instList.add(new Skip(flags, nskip));
	}

	/**
	 * Reads a register, scales it by scale and adds it to the existing contents
	 * of ACC.
	 * 
	 * @param addr
	 *            the register address
	 * @param scale
	 *            the amount to scale the register by before adding it to the
	 *            ACC
	 */
	public void readRegister(int addr, double scale) {
		//checkCodeLen();
		instList.add(new ReadRegister(addr, scale));
	}

	/**
	 * Writes the current value of the ACC to a register. Then scales the
	 * current ACC value by scale.
	 * 
	 * @param addr
	 *            the register address to write to
	 * @param scale
	 *            the amount to scale the ACC by after writing the register
	 */
	public void writeRegister(int addr, double scale) {
		//checkCodeLen();
		instList.add(new WriteRegister(addr, scale));
	}

	/**
	 * Compares the absolute value of ACC with the absolute value of a register
	 * scaled by scale. The larger absolute value is written into the ACC.
	 * 
	 * @param addr
	 *            the register to use for comparison
	 * @param scale
	 *            the amount to scale the register value by before comparison
	 */
	public void maxx(int addr, double scale) {
		//checkCodeLen();
		instList.add(new Maxx(addr, scale));
	}

	/**
	 * Multiplies the current ACC value by the contents of the register.
	 * 
	 * @param addr
	 *            the address of the register to use for multiplication
	 */
	public void mulx(int addr) {
		//checkCodeLen();
		instList.add(new Mulx(addr));
	}

	/**
	 * Subtracts the value of the register at addr from the current ACC value,
	 * then scales the result by scale and finally adds the value of the
	 * register at addr to the result.
	 * 
	 * @param addr
	 *            the register to use
	 * @param scale
	 *            the amount to scale the result before adding the register
	 */
	public void readRegisterFilter(int addr, double scale) {
		//checkCodeLen();
		instList.add(new ReadRegisterFilter(addr, scale));
	}

	/**
	 * Stores the current ACC value in the register at addr. Then subtracts the
	 * current ACC value from the previous ACC value. Scales the result by scale
	 * and the previous ACC value is added to the result.
	 * 
	 * @param addr
	 *            the register to use
	 * @param scale
	 *            the amount to scale the result before adding PACC
	 */
	public void writeRegisterLowshelf(int addr, double scale) {
		//checkCodeLen();
		instList.add(new WriteRegisterLowshelf(addr, scale));
	}

	/**
	 * Stores the current ACC value in the register at addr. Then adds the
	 * current ACC value from the previous ACC value. Scales the result by scale
	 * and the previous ACC value is added to the result.
	 * 
	 * @param addr
	 *            the register to use
	 * @param scale
	 *            the amount to scale the result before adding PACC
	 */
	public void writeRegisterHighshelf(int addr, double scale) {
		//checkCodeLen();
		instList.add(new WriteRegisterHighshelf(addr, scale));
	}

	/**
	 * Reads a value from the delay memory, scales it by scale and then adds it
	 * to the current value of the ACC.
	 * 
	 * @param addr
	 *            the delay memory offset
	 * @param scale
	 *            the amount to scale by before adding to the ACC
	 */
	public void readDelay(int addr, double scale) {
		//checkCodeLen();
		instList.add(new ReadDelay(addr, scale));
	}

	/**
	 * Reads a value from the delay memory, scales it by scale and then adds it
	 * to the current value of the ACC.
	 * 
	 * @param memName
	 *            the delay memory segment name
	 * @param offset
	 *            the read position (0.0 to 1.0) within the memory segment
	 * @param scale
	 *            the amount to scale by before adding to the ACC
	 */
	public void readDelay(String memName, double offset, double scale) {
		//checkCodeLen();
		if (offset < 0.0 || offset > 1.0) {
			throw new ElmProgramException("offset out of range: " + offset
					+ " - valid range: 0.0 to 1.0");
		}

		MemSegment seg = getDelayMemByName(memName);
		int addr = seg.getStart();
		if (offset == 1.0) {
			addr = seg.getEnd();
		} else if (offset > 0.0) {
			addr += Math.round((double) (seg.getLength() - 1) * offset);
		}
		instList.add(new ReadDelay(addr, scale));
	}

	/**
	 * Reads from the delay memory based on the address in the indirect address
	 * pointer register. Scales the result by scale and adds to the ACC.
	 * 
	 * @param scale
	 *            the amount to scale by before adding to the ACC
	 */
	public void readDelayPointer(double scale) {
		//checkCodeLen();
		instList.add(new ReadDelayPointer(scale));
	}

	/**
	 * Writes the current ACC value to the delay memory. Then scales the ACC by
	 * scale.
	 * 
	 * @param addr
	 *            the delay memory offset
	 * @param scale
	 *            the amount to scale the ACC by after writing to delay memory
	 */
	public void writeDelay(int addr, double scale) {
		//checkCodeLen();
		instList.add(new WriteDelay(addr, scale));
	}

	/**
	 * Writes the current ACC value to the delay memory. Then scales the ACC by
	 * scale.
	 * 
	 * @param memName
	 *            the delay memory segment name
	 * @param offset
	 *            the read position (0.0 to 1.0) within the memory segment
	 * @param scale
	 *            the amount to scale the ACC by after writing to the delay
	 *            memory
	 */
	public void writeDelay(String memName, double offset, double scale) {
		//checkCodeLen();
		if (offset < 0.0 || offset > 1.0) {
			throw new ElmProgramException("offset out of range: " + offset
					+ " - valid range: 0.0 to 1.0");
		}

		MemSegment seg = getDelayMemByName(memName);
		int addr = seg.getStart();
		if (offset == 1.0) {
			addr = seg.getEnd();
		} else if (offset > 0.0) {
			addr += Math.round((double) (seg.getLength() - 1) * offset);
		}
		instList.add(new WriteDelay(addr, scale));
	}

	/**
	 * Writes the current ACC value in delay memory at addr. Then scales the ACC
	 * by scale. Finally, adds the contents of the previous delay memory read to
	 * the ACC.
	 * 
	 * @param addr
	 *            the offset in delay memory
	 * @param scale
	 *            the amount to scale the ACC by after writing to the delay
	 *            memory
	 */
	public void writeAllpass(int addr, double scale) {
		//checkCodeLen();
		instList.add(new WriteAllpass(addr, scale));
	}

	/**
	 * Writes the current ACC value in delay memory at addr. Then scales the ACC
	 * by scale. Finally, adds the contents of the previous delay memory read to
	 * the ACC.
	 * 
	 * @param memName
	 *            the delay memory segment name
	 * @param offset
	 *            the read position (0.0 to 1.0) within the memory segment
	 * @param scale
	 *            the amount to scale the ACC by after writing to the delay
	 *            memory
	 */
	public void writeAllpass(String memName, double offset, double scale) {
		//checkCodeLen();
		if (offset < 0.0 || offset > 1.0) {
			throw new ElmProgramException("offset out of range: " + offset
					+ " - valid range: 0.0 to 1.0");
		}

		MemSegment seg = getDelayMemByName(memName);
		int addr = seg.getStart();
		if (offset == 1.0) {
			addr = seg.getEnd();
		} else if (offset > 0.0) {
			addr += Math.round((double) (seg.getLength() - 1) * offset);
		}
		instList.add(new WriteAllpass(addr, scale));
	}

	/**
	 * Loads one of the SIN LFOs with frequency and amplitude settings.
	 * 
	 * @param lfo
	 *            the SIN LFO (0 or 1)
	 * @param freq
	 *            the frequency setting (0.0 to 20.33 in Hz)
	 * @param amp
	 *            the amplitude
	 */
	public void loadSinLFO(int lfo, double freq, double amp) {
		//checkCodeLen();
		incrementLFOUsed(lfo);
		instList.add(new LoadSinLFO(lfo, freq, amp));
	}

	/**
	 * Loads one of the SIN LFOs with frequency and amplitude settings.
	 * 
	 * @param lfo
	 *            the SIN LFO (0 or 1)
	 * @param freq
	 *            the frequency setting (register value)
	 * @param amp
	 *            the amplitude
	 */
	public void loadSinLFO(int lfo, int freq, int amp) {
		//checkCodeLen();
		incrementLFOUsed(lfo);
		instList.add(new LoadSinLFO(lfo, freq, amp));
	}

	/**
	 * Loads one of the RAMP LFOs with frequency and amplitude settings.
	 * 
	 * @param lfo
	 *            the RAM LFO (0 or 1) - // GSW actually this should be 2 or 3
	 * @param freq
	 *            the frequency setting
	 * @param amp
	 *            the amplitude setting
	 */
	public void loadRampLFO(int lfo, int freq, int amp) {
		//checkCodeLen();
		incrementLFOUsed(RMP0 + lfo);
		instList.add(new LoadRampLFO(lfo, freq, amp));
	}

	/**
	 * Resets one of the RAMP LFOs to the starting position.
	 * 
	 * @param lfo
//	 *            the RAMP LFO (0 or 1) - // GSW actually this should be 2 or 3
	 */
	public void jam(int lfo) {
		//checkCodeLen();
		instList.add(new Jam(lfo));
	}

	/**
	 * Reads from the delay memory with the read pointer value modulated by the
	 * selected LFO.
	 * 
	 * @param lfo
	 *            the LFO to use
	 * @param flags
	 *            the flags
	 * @param addr
	 *            the base offset in delay memory
	 */
	public void chorusReadDelay(int lfo, int flags, int addr) {
		//checkCodeLen();
		instList.add(new ChorusReadDelay(lfo, flags, addr));
	}

	/**
	 * Reads from the delay memory with the read pointer value modulated by the
	 * selected LFO.
	 * 
	 * @param lfo
	 *            the LFO to use
	 * @param flags
	 *            the flags
	 * @param addr
	 *            the base offset in delay memory
	 */
	public void comment(String s) {
		nComments = nComments + 1;
		instList.add(new Comment(s));
	}

	/**
	 * Reads from the delay memory with the read pointer value modulated by the
	 * selected LFO.
	 * 
	 * @param lfo
	 *            the LFO to use
	 * @param flags
	 *            the flags
	 * @param memName
	 *            the delay memory segment name
	 * @param offset
	 *            the base read position - absolute, in samples
	 */
	public void chorusReadDelay(int lfo, int flags, String memName, int offset) {
		//checkCodeLen();
		MemSegment seg = getDelayMemByName(memName);
		int ofst = offset;
		if (ofst < 0 || ofst > seg.getLength()) {
			throw new ElmProgramException("offset out of range: " + ofst
					+ " - valid range: 0 to " + seg.getLength());
		}
		int addr = seg.getStart() + ofst;
		instList.add(new ChorusReadDelay(lfo, flags, addr));
	}

	/**
	 * Scales the contents of the ACC and adds an offset. The scale coefficient
	 * is driven by the chosen LFO.
	 * 
	 * @param lfo
	 *            the LFO to use
	 * @param flags
	 *            the flags
	 * @param offset
	 *            the base offset in delay memory
	 */
	public void chorusScaleOffset(int lfo, int flags, double offset) {
		//checkCodeLen();
		instList.add(new ChorusScaleOffset(lfo, flags, offset));
	}

	/**
	 * Reads the current LFO value into the ACC.
	 * 
	 * @param lfo
	 *            the LFO to use
	 */
	public void chorusReadValue(int lfo) {
		//checkCodeLen();
		instList.add(new ChorusReadValue(lfo));
	}

	/**
	 * Clears the ACC.
	 */
	public void clear() {
		//checkCodeLen();
		instList.add(new Clear());
	}

	/**
	 * Negates all bit positions in the ACC.
	 */
	public void not() {
		//checkCodeLen();
		instList.add(new Not());
	}

	/**
	 * Makes the ACC value positive.
	 */
	public void absa() {
		//checkCodeLen();
		instList.add(new Absa());
	}

	/**
	 * Loads the ACC with the value of the register at addr.
	 * 
	 * @param addr
	 *            the register to load
	 */
	public void loadAccumulator(int addr) {
		checkCodeLen();
		instList.add(new LoadAccumulator(addr));
	}

	// GSW - added to keep track of LFO reference/allocation so that we can be aware
	// when multiple blocks reference the same LFO, which is usually not what we are looking
	// for

	public void clearLFOUsedCounts() {
		usedSINLFO0 = 0;
		usedSINLFO1 = 0;
		usedRMPLFO0 = 0;
		usedRMPLFO1 = 0;	
	}

	public void incrementLFOUsed(int LFO) {
		if(LFO == SIN0) {
			usedSINLFO0 ++;
		} else if(LFO == SIN1) {
			usedSINLFO1 ++;
		} else if(LFO == RMP0) {
			usedRMPLFO0 ++;
		} else if(LFO == RMP1) {
			usedRMPLFO1 ++;
		}
	}

	public int getLFOUsed(int LFO) {
		int result = 0;
		if(LFO == SIN0) {
			result = usedSINLFO0;
		} else if(LFO == SIN1) {
			result = usedSINLFO1;
		} else if(LFO == RMP0) {
			result = usedRMPLFO0;
		} else if(LFO == RMP1) {
			result = usedRMPLFO1;
		}
		return result;
	}
}
