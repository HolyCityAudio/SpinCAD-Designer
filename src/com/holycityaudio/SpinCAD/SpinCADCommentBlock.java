package com.holycityaudio.SpinCAD;

import java.io.Serializable;

public class SpinCADCommentBlock implements Serializable {
	private static final long serialVersionUID = -8461943972745967897L;

	public String fileName;
	public String version;
	public String[] line;

	SpinCADCommentBlock() {
		line = new String[6];
	}
	
	public void clearComments() {
		line[0] = "";
		line[1] = "";
		line[2] = "";
		line[3] = "";
		line[4] = "";
		line[5] = "";
	}
	
	public void setLine(int i, String s) {
		if((i >= 0) && (i < 6)) {
			line[i] = s;
		}
	}
	
	public String getLine(int i) {
		if((i >= 0) && (i < 6)) {
			return line[i];
		}
		else
			return null;
	}

	public void setFileName(String s) {
		
		fileName = s;
	}
	
	public String getFileName() {
		return fileName;
	}

	public void setVersion(String s) {
		version = s;
	}
	
	public String getVersion() {
		return version;
	}

	// for writing out to clipboard, etc.
	public String getComments() {
		return 	"; " + fileName + "\n" +
				"; " + version + "\n" +
				"; " + line[0] + "\n" +
				"; " + line[1] + "\n" +
				"; " + line[2] + "\n" +
				"; " + line[3] + "\n" +
				"; " + line[4] + "\n" +
				"; " + line[5] + "\n";

	}
}
