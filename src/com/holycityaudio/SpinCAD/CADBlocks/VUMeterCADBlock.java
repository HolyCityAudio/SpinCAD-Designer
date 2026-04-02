package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class VUMeterCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	private int vu1Reg = -1;
	private int vu2Reg = -1;

	public VUMeterCADBlock(int x, int y) {
		super(x, y);
		setBorderColor(new Color(0xFFA000));  // bright orange (Special block)
		addInputPin(this, "VU 1");
		addInputPin(this, "VU 2");
		setName("VU Meter");
	}

	public void generateCode(SpinFXBlock sfxb) {
		sfxb.comment("VU Meter");
		vu1Reg = -1;
		vu2Reg = -1;

		SpinCADPin p1 = this.getPin("VU 1");
		if (p1 != null && p1.isConnected()) {
			SpinCADPin conn = p1.getPinConnection();
			if (conn != null) {
				vu1Reg = conn.getRegister();
			}
		}

		SpinCADPin p2 = this.getPin("VU 2");
		if (p2 != null && p2.isConnected()) {
			SpinCADPin conn = p2.getPinConnection();
			if (conn != null) {
				vu2Reg = conn.getRegister();
			}
		}
	}

	public int getVU1Reg() { return vu1Reg; }
	public int getVU2Reg() { return vu2Reg; }
}
