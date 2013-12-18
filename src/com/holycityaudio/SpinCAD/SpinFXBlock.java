package com.holycityaudio.SpinCAD;
import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.MemSegment;

public class SpinFXBlock extends ElmProgram {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static int numBlocks = 0;
	private static int numRegs = 0;		// allocation scheme for internal registers REG0 through REG31

	public SpinFXBlock(String nameBlock) {
		super(nameBlock);
		setNumBlocks(getNumBlocks() + 1);
		setNumRegs(REG0);
		//			System.out.printf("SpinFXBlock - numBlocks = %d\n\n", numBlocks);	
	}

	public int allocateReg() {
		//		  System.out.println("SpinFXBlock allocateReg");
		int retval = getNumRegs();
		setNumRegs(getNumRegs() + 1);
		return retval;
	}

	public void FXallocDelayMem(String memName, int size) {
		//		  System.out.println("SpinFXBlock FXallocDelayMem " + memName + numBlocks);
		allocDelayMem(memName + getNumBlocks(), size);
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

	public static int getNumRegs() {
		return numRegs;
	}

	public static void setNumRegs(int numRegs) {
		SpinFXBlock.numRegs = numRegs;
	}

	public static int getNumBlocks() {
		return numBlocks;
	}

	public static void setNumBlocks(int numBlocks) {
		SpinFXBlock.numBlocks = numBlocks;
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