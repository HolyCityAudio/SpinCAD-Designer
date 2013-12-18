package org.andrewkilpatrick.elmGen.instructions;

import org.andrewkilpatrick.elmGen.ElmProgram;

public class ChorusModeFlags {

	String readMode = "";

	public ChorusModeFlags() {
	}

	public String readMode(int flags) {

		if ((flags & ElmProgram.COS) != 0){
			readMode = "COS ";
		}
		if ((flags & ElmProgram.SIN) != 0){
			if(readMode != "")
				readMode = readMode + " | ";
			readMode = readMode + "SIN";
		}
		if ((flags & ElmProgram.REG) != 0){
			if(readMode != "")
				readMode = readMode + " | ";
			readMode = readMode + "REG";
		}
		if ((flags & ElmProgram.COMPC) != 0){
			if(readMode != "")
				readMode = readMode + " | ";
			readMode = readMode + "COMPC";
//			compc = true;
		}
		if ((flags & ElmProgram.NA) != 0){
			if(readMode != "")
				readMode = readMode + " | ";
			readMode = readMode + "NA";
//			na = true;
//			if(lfo == 0 || lfo == 1) {
//				throw new IllegalArgumentException("na cannot be used for SIN LFOs");
//			}
		}
		if ((flags & ElmProgram.COMPA) != 0){
			if(readMode != "")
				readMode = readMode + " | ";
			readMode = readMode + "COMPA";
//			compa = true;
		}
		if ((flags & ElmProgram.RPTR2) != 0){
			if(readMode != "")
				readMode = readMode + " | ";
			readMode = readMode + "RPTR2";
/*			rptr2 = true;
			if(lfo == 0 || lfo == 1) {
				throw new IllegalArgumentException("rptr2 cannot be used for SIN LFOs");
			}
*/
		}

		if(readMode == "" ) {
			readMode = "0";
		}
		return readMode;
	}
}
