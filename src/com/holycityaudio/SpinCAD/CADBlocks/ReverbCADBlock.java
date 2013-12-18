package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;

public class ReverbCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2080033675300414067L;

	public ReverbCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		addInputPin(this);
		addOutputPin(this);
		setBorderColor(Color.green);
	}
}
