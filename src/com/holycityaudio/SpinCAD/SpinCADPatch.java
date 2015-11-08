package com.holycityaudio.SpinCAD;

import java.io.Serializable;

public class SpinCADPatch implements Serializable {
	String patchFileName;
	SpinCADModel patchModel = new SpinCADModel();
	SpinCADCommentBlock cb = new SpinCADCommentBlock();
	private static final long serialVersionUID = -846192537905967897L;
	private boolean changed = false;
	private double[] potVal = new double[3];
	int[] hexFile = new int[128];
	boolean isHexFile = false;
	
	SpinCADPatch() {
		patchModel = new SpinCADModel();
		patchFileName = "Untitled";
		cb.line[0] = "Pot 0: ";
		cb.line[1] = "Pot 1: ";
		cb.line[2] = "Pot 2: ";
		potVal[0] = 0.0;
		potVal[1] = 0.0;
		potVal[2] = 0.0;
	}

	void updateFileName(String n) {
		patchFileName = n;
	}
	
	public void setChanged(boolean b) {
		changed = b;
	}
	
	public boolean getChanged() {
		return changed;
	}
	
	public void setPotVal(int index, double newVal) {
		if(index >= 0 && index < 3) {
			potVal[index] = newVal;
		}
	}
	
	public double getPotVal(int index) {
		if(index >= 0 && index < 3) {
			return potVal[index];
		} else
			return -1;
	}
}
