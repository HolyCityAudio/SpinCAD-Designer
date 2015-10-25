package com.holycityaudio.SpinCAD;

import java.io.Serializable;

public class SpinCADPatch implements Serializable {
	String patchFileName;
	SpinCADModel patchModel = new SpinCADModel();
	SpinCADCommentBlock cb = new SpinCADCommentBlock();
	private static final long serialVersionUID = -846192537905967897L;

	SpinCADPatch() {
		patchModel = new SpinCADModel();
		patchFileName = "Untitled";
		cb.line[0] = "Pot 0: ";
		cb.line[1] = "Pot 1: ";
		cb.line[2] = "Pot 2: ";
	}

	void updateFileName(String n) {
		patchFileName = n;
	}
}
