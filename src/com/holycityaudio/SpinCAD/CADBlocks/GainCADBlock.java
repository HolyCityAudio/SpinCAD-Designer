package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;

public class GainCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4770958229056391233L;
	double leftGain = 1.0;
	double rightGain = 1.0;
	
	int leftGainControl = -1;
	int rightGainControl = -1;
	
	public GainCADBlock(int x, int y) {
		super(x, y);
		addInputPin(this);
		addOutputPin(this);
		setBorderColor(Color.RED);
	}
	
}
