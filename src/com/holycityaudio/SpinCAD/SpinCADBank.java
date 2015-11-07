package com.holycityaudio.SpinCAD;

import java.io.Serializable;

public class SpinCADBank implements Serializable {
	Boolean changed = false;
	String bankFileName = "Untitled";
	SpinCADPatch[] patch = new SpinCADPatch[8];
	SpinCADCommentBlock cb = new SpinCADCommentBlock();
	// in the case of loading a hew file to this patch position
	
	private static final long serialVersionUID = -8461234577905967897L;

	SpinCADBank() {
		cb.line[0] = "Bank";
		cb.line[1] = "";
		cb.line[2] = "";

		for (int i = 0; i < 8; i++) {
			patch[i] = new SpinCADPatch();			
		}
	}
}
