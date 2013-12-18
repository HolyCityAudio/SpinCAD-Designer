package com.holycityaudio.SpinCAD.CADBlocks;


public class RichChorusCADBlock extends ModulationCADBlock{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3895304944418909090L;

	public RichChorusCADBlock(int x, int y) {
		super(x, y);
		// TODO Auto-generated constructor stub
		setName("Rich Chorus");
	}
	
	public void generateCode() {

//cho	rda,sin0,sin|reg|compc,cdel+800
//cho	rda,sin0,sin,cdel+801
//wrax	c1,0
//cho	rda,sin0,sin|reg|compa,cdel+400
//cho	rda,sin0,sin|compa,cdel+401
		//wrax	c2,0
		//cho	rda,sin0,cos|reg|compc,cdel+1100
		//cho	rda,sin0,cos,cdel+1101
		//wrax	c3,0
		//cho	rda,sin0,cos|reg|compa,cdel+1400
		//cho	rda,sin0,cos|compc,cdel+1401
		//wrax	c4,0
	}
}
