package com.holycityaudio.SpinCAD;

import java.io.Serializable;

public class SpinCADBank implements Serializable {
	Boolean changed = false;
	String bankFileName = "Untitled";
	SpinCADPatch[] bank = new SpinCADPatch[8];
	SpinCADCommentBlock cb = new SpinCADCommentBlock();
	private static final long serialVersionUID = -8461234577905967897L;

	SpinCADBank() {
		cb.line[0] = "Bank";
		cb.line[1] = "Thank";
		cb.line[2] = "You";

		for (int i = 0; i < 8; i++) {
			bank[i] = new SpinCADPatch();			
		}
	}
}
