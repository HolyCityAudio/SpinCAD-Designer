package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;

public class ModulationCADBlock extends SpinCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8225832881225754622L;

	public ModulationCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		addInputPin(this);
		addOutputPin(this);
		setBorderColor(Color.cyan);
	}
}
