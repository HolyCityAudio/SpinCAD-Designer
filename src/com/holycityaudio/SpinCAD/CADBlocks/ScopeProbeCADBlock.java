package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class ScopeProbeCADBlock extends SpinCADBlock {

	private static final long serialVersionUID = 1L;

	private int scope1Reg = -1;
	private int scope2Reg = -1;

	public ScopeProbeCADBlock(int x, int y) {
		super(x, y);
		setBorderColor(new Color(0x00C8C8));  // cyan
		addInputPin(this, "Scope 1");
		addInputPin(this, "Scope 2");
		setName("Scope Probe");
	}

	public void generateCode(SpinFXBlock sfxb) {
		sfxb.comment("Scope Probe");
		scope1Reg = -1;
		scope2Reg = -1;

		SpinCADPin p1 = this.getPin("Scope 1");
		if (p1 != null && p1.isConnected()) {
			SpinCADPin conn = p1.getPinConnection();
			if (conn != null) {
				scope1Reg = conn.getRegister();
			}
		}

		SpinCADPin p2 = this.getPin("Scope 2");
		if (p2 != null && p2.isConnected()) {
			SpinCADPin conn = p2.getPinConnection();
			if (conn != null) {
				scope2Reg = conn.getRegister();
			}
		}
	}

	public int getScope1Reg() { return scope1Reg; }
	public int getScope2Reg() { return scope2Reg; }
}
