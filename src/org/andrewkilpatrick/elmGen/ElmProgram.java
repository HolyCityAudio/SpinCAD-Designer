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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashMap;
import java.util.Map;

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
			System.err.println("Warning: program length (" + (getCodeLen() - getNumComments())
					+ ") exceeds maximum (" + MAX_CODE_LEN + ")");
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

	public void replaceInstruction(int num, Instruction inst) {
		instList.set(num, inst);
	}

	public void insertInstruction(int num, Instruction inst) {
		instList.add(num, inst);
	}

	public void removeInstruction(int num) {
		instList.remove(num);
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
		int i;
		if (mode == 1) {
			String str = "; ----------------------------\n";
			for (i = 0; i < instList.size(); i++) {
				Instruction inst = instList.get(i);
				// str += String.format("%08X", inst.getHexWord()) + " - ";
				str += inst.getInstructionString(1) + "\n";
			}
			return str;
		}
		else
			return "Error! Invalid mode.";
	}
	
// ============================================================
//  Drop-in replacement for optimizeProgram() in ElmProgram.java
//
//  Also requires one private helper method: countRegisterReads()
//
//  Assumptions (consistent with the existing codebase):
//    - WriteRegister has: getAddr() -> int,  getScale() -> double
//    - ReadRegister  has: getAddr() -> int,  getScale() -> double
//    - ScaleOffset   has: getScale() -> double, getOffset() -> double
//      (used to emit SOF for Case 3)
//    - Comment is the only non-executable instruction type (already
//      handled by the existing code)
//
//  If your instruction classes use different getter names, adjust
//  the four calls marked "*** ADJUST IF NEEDED ***" below.
// ============================================================

/**
 * Optimizes the instruction list by collapsing redundant
 * WriteRegister / ReadRegister pairs produced by the block
 * connection architecture.
 *
 * Three cases are handled:
 *
 *   Case 1  –  wrax reg, 0.0  /  rdax reg, 1.0
 *              AND reg not referenced anywhere else
 *              → both instructions deleted, register freed
 *              Saves: 2 instructions, 1 register
 *
 *   Case 2  –  wrax reg, 0.0  /  rdax reg, 1.0
 *              AND reg IS referenced elsewhere
 *              → collapsed to: wrax reg, 1.0
 *              Saves: 1 instruction
 *
 *   Case 3  –  wrax reg, 0.0  /  rdax reg, <gain>  (gain ≠ 1.0)
 *              AND reg not referenced anywhere else
 *              → replaced by: sof <gain>, 0.0
 *              Saves: 1 instruction, 1 register
 *
 * Comments interspersed between the two instructions are
 * preserved and re-inserted after the replacement instruction.
 *
 * @return the program name (matches original contract)
 */
public String optimizeProgram() {

    // ----------------------------------------------------------
    // Phase 1: count how many times each register address is
    // *read* (i.e. used as a source) across the whole program.
    // WriteRegister is a write, so it does not count here.
    // ----------------------------------------------------------
    Map<Integer, Integer> readCount = countRegisterReads();

    // ----------------------------------------------------------
    // Phase 2: single forward pass – find adjacent wrax/rdax
    // pairs (with optional comments in between) and apply the
    // appropriate case.
    // ----------------------------------------------------------
    List<Instruction> optList = new LinkedList<>();
    int savedInstructions = 0;
    int savedRegisters    = 0;

    int i = 0;
    while (i < instList.size()) {
        Instruction inst = instList.get(i);

        // Only interested in WriteRegister as the first instruction
        if (!(inst instanceof WriteRegister)) {
            optList.add(inst);
            i++;
            continue;
        }

        WriteRegister wrax = (WriteRegister) inst;

        // *** ADJUST IF NEEDED *** – getter names for WriteRegister
        int    wraxAddr  = wrax.getAddr();
        double wraxScale = wrax.getScale();

        // We only optimise the pattern where wrax zeroes the ACC
        if (wraxScale != 0.0) {
            optList.add(inst);
            i++;
            continue;
        }

        // Scan forward past any Comment instructions, collecting them
        List<Instruction> interveningComments = new LinkedList<>();
        int j = i + 1;
        while (j < instList.size() && instList.get(j) instanceof Comment) {
            interveningComments.add(instList.get(j));
            j++;
        }

        // Check whether the next non-comment instruction is ReadRegister
        if (j >= instList.size() || !(instList.get(j) instanceof ReadRegister)) {
            optList.add(inst);
            i++;
            continue;
        }

        ReadRegister rdax = (ReadRegister) instList.get(j);

        // *** ADJUST IF NEEDED *** – getter names for ReadRegister
        int    rdaxAddr  = rdax.getAddr();
        double rdaxScale = rdax.getScale();

        // Registers must match
        if (rdaxAddr != wraxAddr) {
            optList.add(inst);
            i++;
            continue;
        }

        // How many times is this register read across the whole program?
        // The rdax we are about to remove counts as one read, so if
        // readCount == 1 the register is not needed anywhere else.
        int reads = readCount.getOrDefault(wraxAddr, 0);
        boolean usedElsewhere = (reads > 1);

        // -------------------------------------------------------
        // Apply the matching case
        // -------------------------------------------------------
        if (rdaxScale == 1.0 && !usedElsewhere) {
            // Case 1: both instructions gone, register freed
            // Comments between the pair are preserved as block separators
            savedInstructions += 2;
            savedRegisters    += 1;
            optList.addAll(interveningComments);

        } else if (rdaxScale == 1.0) {
            // Case 2: collapse to wrax reg, 1.0
            WriteRegister merged = new WriteRegister(wraxAddr, 1.0); // *** ADJUST IF NEEDED ***
            optList.add(merged);
            optList.addAll(interveningComments);
            savedInstructions += 1;

        } else if (!usedElsewhere) {
            // Case 3: replace pair with sof <gain>, 0.0
            ScaleOffset sof = new ScaleOffset(rdaxScale, 0.0); // *** ADJUST IF NEEDED ***
            optList.add(sof);
            optList.addAll(interveningComments);
            savedInstructions += 1;
            savedRegisters    += 1;

        } else {
            // No optimisation applies – keep both instructions as-is
            optList.add(inst);
            optList.addAll(interveningComments);
            optList.add(rdax);
        }

        // Advance past both matched instructions (and the comments)
        i = j + 1;
    }

    System.out.println("Optimization complete: saved " + savedInstructions
            + " instruction(s) and " + savedRegisters + " register(s).");

    // Replace the instruction list in-place
    instList.clear();
    instList.addAll(optList);
 // Recount comments since instList was rebuilt
    nComments = 0;
    for (Instruction inst : instList) {
        if (inst instanceof Comment) {
            nComments++;
        }
    }
    return name;
}

// ============================================================
//  Helper: count how many instructions READ each register
//  address.  Only instructions that use a register as a
//  *source* are counted (ReadRegister, ReadRegisterFilter,
//  Maxx, Mulx).  WriteRegister is a write, not a read.
// ============================================================
private Map<Integer, Integer> countRegisterReads() {
    Map<Integer, Integer> counts = new HashMap<>();

    for (Instruction inst : instList) {

        if (inst instanceof ReadRegister) {
            // *** ADJUST IF NEEDED ***
            int addr = ((ReadRegister) inst).getAddr();
            counts.merge(addr, 1, Integer::sum);

        } else if (inst instanceof ReadRegisterFilter) {
            // rdfx also reads the register
            // *** ADJUST IF NEEDED ***
            int addr = ((ReadRegisterFilter) inst).getAddr();
            counts.merge(addr, 1, Integer::sum);

        } else if (inst instanceof Maxx) {
            // maxx reads the register for comparison
            // *** ADJUST IF NEEDED ***
            int addr = ((Maxx) inst).getAddr();
            counts.merge(addr, 1, Integer::sum);

        } else if (inst instanceof Mulx) {
            // mulx reads the register
            // *** ADJUST IF NEEDED ***
            int addr = ((Mulx) inst).getAddr();
            counts.merge(addr, 1, Integer::sum);
        }
        // WriteRegister, WriteRegisterLowshelf, WriteRegisterHighshelf
        // are writes only – do not count them here.
        // All other instructions (delays, LFOs, SOF, etc.) don't
        // reference the general-purpose register file.
    }
    return counts;
}

// ============================================================
//  Second-pass output-register optimizer.
//
//  Handles non-adjacent WRAX REGx / RDAX REGx patterns where
//  the register is only used to shuttle a value to DAC outputs.
//
//  Case 4 – WRAX REGx,0.0 → N × (RDAX REGx,1.0 / WRAX DACy,0.0)
//           REGx not used elsewhere, all gains 1.0
//           → Replace WRAX REGx with chain of WRAX DACy.
//           Saves: N+1 instructions, 1 register
//
//  Case 5 – RDAX REGx,1.0 / WRAX DACy,0.0 in output section
//           REGx IS used elsewhere (e.g. filter state via RDFX)
//           Only applies when WRAX REGx has scale=1.0 (ACC unchanged).
//           → Insert WRAX DACy,1.0 after the WRAX REGx,1.0
//           Saves: 1 instruction per output pair
//
//  Case 6 – WRAX REGx,0.0 → N × (RDAX REGx,g_i / WRAX DACy,0.0)
//           REGx not used elsewhere, gains differ
//           → Replace WRAX REGx,0.0 with WRAX REGx,g1, then
//             chain WRAX DACy with ratio-scaled gains.
//           Saves: N instructions
// ============================================================
public void optimizeOutputRegisters() {

    Map<Integer, Integer> readCount = countRegisterReads();
    final int USER_REG_MIN = REG0;  // 0x20

    // ----------------------------------------------------------
    // Phase 1: Find all "output pairs" in the instruction list.
    // An output pair is: RDAX REGx, gain / WRAX DACy, 0.0
    // where DACy is DACL or DACR and REGx is a user register.
    // ----------------------------------------------------------
    // Each entry: [rdaxIdx, wraxDacIdx, regAddr, dacAddr]
    List<int[]> pairInfo = new ArrayList<>();
    List<Double> pairGains = new ArrayList<>();

    for (int i = 0; i < instList.size(); i++) {
        if (!(instList.get(i) instanceof ReadRegister)) continue;
        ReadRegister rdax = (ReadRegister) instList.get(i);
        if (rdax.getAddr() < USER_REG_MIN) continue;

        // Skip comments to find next non-comment instruction
        int j = i + 1;
        while (j < instList.size() && instList.get(j) instanceof Comment) j++;
        if (j >= instList.size()) continue;
        if (!(instList.get(j) instanceof WriteRegister)) continue;

        WriteRegister wraxDac = (WriteRegister) instList.get(j);
        if ((wraxDac.getAddr() != DACL && wraxDac.getAddr() != DACR)
                || wraxDac.getScale() != 0.0) {
            continue;
        }

        pairInfo.add(new int[]{i, j, rdax.getAddr(), wraxDac.getAddr()});
        pairGains.add(rdax.getScale());
    }

    if (pairInfo.isEmpty()) {
        return;
    }

    // ----------------------------------------------------------
    // Phase 2: Group output pairs by source register.
    // ----------------------------------------------------------
    Map<Integer, List<Integer>> groups = new LinkedHashMap<>();
    for (int idx = 0; idx < pairInfo.size(); idx++) {
        int regAddr = pairInfo.get(idx)[2];
        groups.computeIfAbsent(regAddr, k -> new ArrayList<>()).add(idx);
    }

    // ----------------------------------------------------------
    // Phase 3: For each register group, determine which case
    // applies and record the transformation.
    // ----------------------------------------------------------
    Set<Integer> removeSet = new TreeSet<>();
    Map<Integer, Instruction> replaceMap = new HashMap<>();
    Map<Integer, List<Instruction>> insertMap = new HashMap<>();

    int savedInst = 0;
    int savedRegs = 0;

    for (Map.Entry<Integer, List<Integer>> entry : groups.entrySet()) {
        int reg = entry.getKey();
        List<Integer> pis = entry.getValue();

        int totalReads = readCount.getOrDefault(reg, 0);
        boolean onlyInOutput = (totalReads == pis.size());

        // Find the last WRAX to this register before the first output pair
        int firstOPIdx = pairInfo.get(pis.get(0))[0];
        int srcWrIdx = -1;
        double srcWrScale = 0.0;
        for (int i = firstOPIdx - 1; i >= 0; i--) {
            if (instList.get(i) instanceof WriteRegister) {
                WriteRegister w = (WriteRegister) instList.get(i);
                if (w.getAddr() == reg) {
                    srcWrIdx = i;
                    srcWrScale = w.getScale();
                    break;
                }
            }
        }
        if (srcWrIdx < 0) continue;  // couldn't find the source write

        // ----- All reads in output, WRAX has scale 0.0 -----
        if (onlyInOutput && srcWrScale == 0.0) {
            boolean allGainsOne = true;
            for (int pi : pis) {
                if (pairGains.get(pi) != 1.0) { allGainsOne = false; break; }
            }

            if (allGainsOne) {
                // === Case 4: same register, all gains 1.0, not used elsewhere ===
                // Replace WRAX REGx,0.0 with chain:  WRAX DAC_0,1.0 / ... / WRAX DAC_N,0.0
                int n = pis.size();
                replaceMap.put(srcWrIdx,
                    new WriteRegister(pairInfo.get(pis.get(0))[3], n > 1 ? 1.0 : 0.0));

                if (n > 1) {
                    List<Instruction> extra = new ArrayList<>();
                    for (int k = 1; k < n; k++) {
                        double s = (k < n - 1) ? 1.0 : 0.0;
                        extra.add(new WriteRegister(pairInfo.get(pis.get(k))[3], s));
                    }
                    insertMap.put(srcWrIdx, extra);
                }

                for (int pi : pis) {
                    removeSet.add(pairInfo.get(pi)[0]);  // RDAX
                    removeSet.add(pairInfo.get(pi)[1]);  // WRAX DACy
                }

                savedInst += n + 1;
                savedRegs += 1;

            } else {
                // === Case 6: same register, different gains, not used elsewhere ===
                // Replace WRAX REGx,0.0 with WRAX REGx,g1 then chain of WRAX DACy
                double g0 = pairGains.get(pis.get(0));
                replaceMap.put(srcWrIdx, new WriteRegister(reg, g0));

                List<Instruction> chain = new ArrayList<>();
                double accScale = g0;  // ACC = V × g0 after the modified WRAX REGx
                for (int k = 0; k < pis.size(); k++) {
                    double s;
                    if (k < pis.size() - 1) {
                        double nextG = pairGains.get(pis.get(k + 1));
                        s = nextG / accScale;
                        accScale *= s;
                    } else {
                        s = 0.0;
                    }
                    chain.add(new WriteRegister(pairInfo.get(pis.get(k))[3], s));
                }
                insertMap.put(srcWrIdx, chain);

                for (int pi : pis) {
                    removeSet.add(pairInfo.get(pi)[0]);
                    removeSet.add(pairInfo.get(pi)[1]);
                }

                savedInst += pis.size();
            }

        // ----- Register used elsewhere, WRAX scale is 1.0 -----
        } else if (!onlyInOutput && srcWrScale == 1.0) {
            // === Case 5: insert WRAX DACy,1.0 after the WRAX REGx,1.0 ===
            // ACC after WRAX REGx,1.0 still holds the value, so we can
            // piggyback DAC writes directly.
            boolean allGainsOne = true;
            for (int pi : pis) {
                if (pairGains.get(pi) != 1.0) { allGainsOne = false; break; }
            }
            if (!allGainsOne) continue;

            List<Instruction> inserts = insertMap.getOrDefault(srcWrIdx, new ArrayList<>());
            for (int pi : pis) {
                inserts.add(new WriteRegister(pairInfo.get(pi)[3], 1.0));
                removeSet.add(pairInfo.get(pi)[0]);
                removeSet.add(pairInfo.get(pi)[1]);
            }
            insertMap.put(srcWrIdx, inserts);

            savedInst += pis.size();
        }
        // All other combinations: leave as-is
    }

    if (removeSet.isEmpty() && replaceMap.isEmpty()) {
        return;
    }

    // ----------------------------------------------------------
    // Phase 4: Rebuild the instruction list with transformations.
    // ----------------------------------------------------------
    List<Instruction> newList = new LinkedList<>();
    for (int i = 0; i < instList.size(); i++) {
        if (removeSet.contains(i)) continue;

        newList.add(replaceMap.containsKey(i) ? replaceMap.get(i) : instList.get(i));

        if (insertMap.containsKey(i)) {
            newList.addAll(insertMap.get(i));
        }
    }

    instList.clear();
    instList.addAll(newList);

    // Recount comments since instList was rebuilt
    nComments = 0;
    for (Instruction inst : instList) {
        if (inst instanceof Comment) nComments++;
    }

    System.out.println("Output register optimization complete: saved "
        + savedInst + " instruction(s) and " + savedRegs + " register(s).");
}

//============================================================
//Add compactRegisters() and its helper collectRegisterAddresses()
//to ElmProgram.java, after the optimizeProgram() method.
//
//Call it immediately after optimizeProgram() in your pipeline:
//
//  program.optimizeProgram();
//  program.compactRegisters();
//
//Required imports (add to ElmProgram.java if not present):
//  import java.util.HashMap;
//  import java.util.Map;
//  import java.util.TreeSet;
//============================================================

/**
* Compacts the user-register address space after optimization.
*
* The FV-1 supports REG0 (0x20) through REG31 (0x3F) as
* general-purpose registers.  After the instruction optimizer
* removes redundant wrax/rdax pairs, gaps appear in the
* allocated register sequence (e.g. 0x20, 0x22, 0x25 with
* 0x21, 0x23, 0x24 now unused).
*
* This method:
*   1. Scans every instruction that references a user register
*      and collects the set of addresses actually still in use.
*   2. Builds a remapping table: lowest used address → REG0,
*      next → REG1, etc., preserving relative order.
*   3. Rewrites every register-referencing instruction in place
*      using the new addresses.
*   4. Prints a summary to System.err reporting:
*        - how many registers were compacted away
*        - the new highest register number
*        - an ERROR if the new highest register still exceeds REG31
*
* Hardware-mapped addresses (LFO regs 0x00–0x07, POT/ADC/DAC
* 0x10–0x18) are never remapped.
*/
public void compactRegisters() {

// ----------------------------------------------------------
// Constants (already defined in ElmProgram, repeated here
// for clarity – the method can reference the class fields
// directly, these comments are just documentation).
//   REG0  = 0x20  (first user register)
//   REG31 = 0x3F  (last legal user register on FV-1)
// ----------------------------------------------------------
final int USER_REG_MIN = REG0;   // 0x20 = 32
final int USER_REG_MAX = 0x3F;   // 63  (REG31, the legal ceiling)

// ----------------------------------------------------------
// Phase 1: Collect every user-register address still
// referenced anywhere in the instruction list.
// Uses a TreeSet so we get them in ascending order.
// ----------------------------------------------------------
TreeSet<Integer> usedAddresses = collectRegisterAddresses(USER_REG_MIN, USER_REG_MAX);

if (usedAddresses.isEmpty()) {
    System.err.println("compactRegisters: no user registers found, nothing to compact.");
    return;
}

// ----------------------------------------------------------
// Phase 2: Build old-address → new-address remapping table.
// Assign contiguous addresses starting at REG0.
// ----------------------------------------------------------
Map<Integer, Integer> remap = new HashMap<>();
int next = USER_REG_MIN;
for (int oldAddr : usedAddresses) {
    remap.put(oldAddr, next);
    next++;
}

int originalHighest = usedAddresses.last();
int compactedHighest = next - 1;   // highest address after remapping
int holesRemoved = usedAddresses.size() == (originalHighest - USER_REG_MIN + 1)
        ? 0
        : (originalHighest - USER_REG_MIN + 1) - usedAddresses.size();

// ----------------------------------------------------------
// Phase 3: Rewrite every instruction that holds a user-
// register address, replacing old address with new address.
//
// Each instruction type that references a register must be
// handled.  We replace the whole instruction object because
// the instruction classes are (presumably) immutable value
// types with no setAddr() mutator.  If your classes DO have
// a setAddr() mutator, you can simplify each branch to just
// call inst.setAddr(newAddr) instead of creating a new object.
//
// *** ADJUST IF NEEDED *** if your constructor signatures differ.
// ----------------------------------------------------------
for (int i = 0; i < instList.size(); i++) {
    Instruction inst = instList.get(i);

    if (inst instanceof WriteRegister) {
        WriteRegister w = (WriteRegister) inst;
        int newAddr = remap.getOrDefault(w.getAddr(), w.getAddr());
        if (newAddr != w.getAddr()) {
            instList.set(i, new WriteRegister(newAddr, w.getScale()));
        }

    } else if (inst instanceof WriteRegisterLowshelf) {
        WriteRegisterLowshelf w = (WriteRegisterLowshelf) inst;
        int newAddr = remap.getOrDefault(w.getAddr(), w.getAddr());
        if (newAddr != w.getAddr()) {
            instList.set(i, new WriteRegisterLowshelf(newAddr, w.getScale()));
        }

    } else if (inst instanceof WriteRegisterHighshelf) {
        WriteRegisterHighshelf w = (WriteRegisterHighshelf) inst;
        int newAddr = remap.getOrDefault(w.getAddr(), w.getAddr());
        if (newAddr != w.getAddr()) {
            instList.set(i, new WriteRegisterHighshelf(newAddr, w.getScale()));
        }

    } else if (inst instanceof ReadRegister) {
        ReadRegister r = (ReadRegister) inst;
        int newAddr = remap.getOrDefault(r.getAddr(), r.getAddr());
        if (newAddr != r.getAddr()) {
            instList.set(i, new ReadRegister(newAddr, r.getScale()));
        }

    } else if (inst instanceof ReadRegisterFilter) {
        ReadRegisterFilter r = (ReadRegisterFilter) inst;
        int newAddr = remap.getOrDefault(r.getAddr(), r.getAddr());
        if (newAddr != r.getAddr()) {
            instList.set(i, new ReadRegisterFilter(newAddr, r.getScale()));
        }

    } else if (inst instanceof Maxx) {
        Maxx m = (Maxx) inst;
        int newAddr = remap.getOrDefault(m.getAddr(), m.getAddr());
        if (newAddr != m.getAddr()) {
            instList.set(i, new Maxx(newAddr, m.getScale()));
        }

    } else if (inst instanceof Mulx) {
        Mulx m = (Mulx) inst;
        int newAddr = remap.getOrDefault(m.getAddr(), m.getAddr());
        if (newAddr != m.getAddr()) {
            instList.set(i, new Mulx(newAddr));
        }
    }
    // LoadAccumulator also reads a register
    else if (inst instanceof LoadAccumulator) {
        LoadAccumulator la = (LoadAccumulator) inst;
        int newAddr = remap.getOrDefault(la.getAddr(), la.getAddr());
        if (newAddr != la.getAddr()) {
            instList.set(i, new LoadAccumulator(newAddr));
        }
    }
    // All other instruction types (delays, LFOs, SOF, Skip, etc.)
    // do not reference user registers – leave them untouched.
}

// ----------------------------------------------------------
// Phase 4: Report results.
// ----------------------------------------------------------
int originalRegCount = originalHighest - USER_REG_MIN + 1;
int compactedRegCount = compactedHighest - USER_REG_MIN + 1;

System.err.println("----------------------------------------");
System.err.println("Register compaction report:");
System.err.println("  Registers before compaction : "
        + originalRegCount
        + "  (highest was REG" + (originalHighest - USER_REG_MIN) + " / 0x"
        + Integer.toHexString(originalHighest) + ")");
System.err.println("  Holes removed               : " + holesRemoved);
System.err.println("  Registers after compaction  : "
        + compactedRegCount
        + "  (highest is now REG" + (compactedHighest - USER_REG_MIN) + " / 0x"
        + Integer.toHexString(compactedHighest) + ")");

if (compactedHighest > USER_REG_MAX) {
    // Still over the hardware limit even after compaction.
    // Express the overage in user-friendly REGn terms.
    int overBy = compactedHighest - USER_REG_MAX;
    System.err.println("  ERROR: register count still exceeds FV-1 limit!");
    System.err.println("         Maximum legal register is REG31 (0x3F).");
    System.err.println("         Highest used after compaction is REG"
            + (compactedHighest - USER_REG_MIN)
            + " -- over by " + overBy
            + " register" + (overBy == 1 ? "" : "s") + ".");
    System.err.println("         This patch will NOT assemble for a real FV-1.");
} else {
    System.err.println("  OK: all registers fit within REG0–REG31.");
}
System.err.println("----------------------------------------");
// Hook for subclasses to update their register allocator state
onRegistersCompacted(usedAddresses.isEmpty() ? USER_REG_MIN : (compactedHighest + 1));    
}

//============================================================
//Helper: scan instList and return a sorted set of all
//user-register addresses in the range [minAddr, maxAddr]
//that are actually referenced by at least one instruction.
//============================================================
private TreeSet<Integer> collectRegisterAddresses(int minAddr, int maxAddr) {
TreeSet<Integer> found = new TreeSet<>();

for (Instruction inst : instList) {
    int addr = -1;

    if      (inst instanceof WriteRegister)      addr = ((WriteRegister)      inst).getAddr();
    else if (inst instanceof WriteRegisterLowshelf)  addr = ((WriteRegisterLowshelf)  inst).getAddr();
    else if (inst instanceof WriteRegisterHighshelf) addr = ((WriteRegisterHighshelf) inst).getAddr();
    else if (inst instanceof ReadRegister)       addr = ((ReadRegister)        inst).getAddr();
    else if (inst instanceof ReadRegisterFilter) addr = ((ReadRegisterFilter)  inst).getAddr();
    else if (inst instanceof Maxx)               addr = ((Maxx)                inst).getAddr();
    else if (inst instanceof Mulx)               addr = ((Mulx)                inst).getAddr();
    else if (inst instanceof LoadAccumulator)    addr = ((LoadAccumulator)     inst).getAddr();

    if (addr >= minAddr && addr <= maxAddr) {
        found.add(addr);
    }
}
return found;
}

/**
 * Called at the end of compactRegisters() with the new next-free
 * register address. Subclasses that maintain their own allocator
 * high-water mark should override this to update it.
 */
protected void onRegistersCompacted(int newNextFreeReg) {
    // default: no-op
}

//============================================================

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
		if (scale == 1.0 && offset == 0.0) return;
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
