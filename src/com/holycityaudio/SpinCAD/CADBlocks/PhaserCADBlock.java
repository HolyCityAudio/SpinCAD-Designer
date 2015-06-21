/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
 * 
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 */

package com.holycityaudio.SpinCAD.CADBlocks;
import java.awt.Color;

import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class PhaserCADBlock extends SpinCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 343880108475812086L;
	int temp, temp1, phase, stages, controlMode;
	PhaserControlPanel cP = null; 

	public PhaserCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		stages = 4;
		controlMode = 0; 	// default to LFO mode
		setupControls();
		addInputPin(this, "Audio Input");
		addOutputPin(this, "Mix Out");
		addOutputPin(this, "Wet Out");
		setBorderColor(Color.cyan);
		setName("Phaser");
		// add a listener up to the JPanel to redraw this block when the control mode is changed.
	}

	public void setupControls() {
		removeAllControlInputs();
		addControlInputPin(this, "LFO Speed");
		addControlInputPin(this, "LFO Width");			
		addControlInputPin(this, "Phase");
		if(controlMode == 0) {
		} else if (controlMode == 1) {
		}  else if (controlMode == 2) {
		}
	}

	public void generateCode(SpinFXBlock sfxb) {
		//				equ	mono	reg0
		int input = -1;
		int wet = -1;
		SpinCADPin p = this.getPin("Audio Input").getPinConnection();
		sfxb.comment(getName());


		if(p != null) {
			input = p.getRegister();
			//				equ	phase	reg5
			//				equ	pout	reg6
			int POUT = sfxb.allocateReg();
			//				equ	p1	reg7

			int p1 = sfxb.allocateReg();
			//				equ	p2	reg8
			int p2 = sfxb.allocateReg();

			int p3 = 0;
			int p4 = 0;
			if(stages > 1) {
				p3 = sfxb.allocateReg();
				p4 = sfxb.allocateReg();
			}

			int p5 = 0;
			int p6 = 0;
			if(stages > 2) {
				p5 = sfxb.allocateReg();
				p6 = sfxb.allocateReg();
			}

			int p7 = 0;
			int p8 = 0;
			if(stages > 3) {
				p7 = sfxb.allocateReg();
				p8 = sfxb.allocateReg();
			}

			int p9 = 0;
			int p10 = 0;
			if(stages > 4) {
				p9 = sfxb.allocateReg();
				p10 = sfxb.allocateReg();
			}

			temp = sfxb.allocateReg();
			temp1 = sfxb.allocateReg();
			wet = sfxb.allocateReg();

			int BYPASS = -1;

			if (controlMode == 0) {			
				BYPASS = sfxb.allocateReg();
				phase = sfxb.allocateReg();

				sfxb.skip(RUN, 1);
				sfxb.loadSinLFO(1, 0, 32767);

				p = this.getPin("LFO Width").getPinConnection();
				int depth = -1;
				if(p != null) {
					depth = p.getRegister();
					sfxb.readRegister(depth, 1.0);
				}
				else {
					depth = sfxb.allocateReg();		// shortcut, could optimize later
					sfxb.scaleOffset(0.0,  0.5);	// default width in case no pin connected
					sfxb.writeAllpass(depth, 0.0);
				}

				sfxb.readRegister(BYPASS, 0.9);
				sfxb.writeRegister(BYPASS, 0);

				p = this.getPin("LFO Speed").getPinConnection();
				int speed = -1;
				if(p != null) {
					speed = p.getRegister();
					sfxb.readRegister(speed, 1.0);
					sfxb.mulx(speed);
					sfxb.scaleOffset(0.83, 0.002);
				}
				else {
					sfxb.scaleOffset(0.0,  0.5);	// default speed in case no pin connected
				}

				sfxb.writeRegister(SIN1_RATE, 0);

				sfxb.chorusReadValue(SIN1);
				sfxb.scaleOffset(0.5, 0.5);
				sfxb.log(0.5, 0);
				sfxb.exp(1,0);
				sfxb.scaleOffset(1.0, -0.5);
				sfxb.scaleOffset(1.999, 0);
				sfxb.mulx(depth);
				//					sof	0.1,0.85
				sfxb.scaleOffset(0.15 , 0.83);
				//					wrax	phase,0		;phase variable ranges 0.8 to 0.95
				sfxb.writeRegister(phase, 0);
			}

			// beginning of phase shifter proper
			sfxb.readRegister(p1, 1);
			sfxb.writeRegister(temp, 1);
			sfxb.mulx(getControlReg(1));
			sfxb.readRegister(input, 1.0/64);
			sfxb.writeRegister(p1, -1);
			sfxb.mulx(getControlReg(1));

			PhaseShiftStage(sfxb ,p2, 1);

			if(stages > 1) {
				PhaseShiftStage(sfxb ,p3, 2);
				PhaseShiftStage(sfxb ,p4, 2);
			}
			if (stages > 2) {
				PhaseShiftStage(sfxb, p5, 3);
				PhaseShiftStage(sfxb, p6, 3);
			}
			if(stages > 3) {
				PhaseShiftStage(sfxb, p7, 4);
				PhaseShiftStage(sfxb, p8, 5);
			}
			if(stages > 4) {
				PhaseShiftStage(sfxb, p9, 5);
				PhaseShiftStage(sfxb, p10, 5);
			}
			sfxb.readRegister(temp, 1);

			//					sof	-2,0
			sfxb.scaleOffset(-2.0, 0.0);
			//					sof	-2,0
			sfxb.scaleOffset(-2.0, 0.0);
			//					sof	-2,0
			sfxb.scaleOffset(-2.0, 0.0);
			//					sof	-2,0
			sfxb.scaleOffset(-2.0, 0.0);
			//					sof	-2,0
			sfxb.scaleOffset(-2.0, 0.0);
			//					sof	-2,0	;output of phase shifter in acc
			sfxb.scaleOffset(-2.0, 0.0);

			sfxb.writeRegister(wet, 1.0);
			//					mulx	bypass
			if(controlMode == 0) {
				sfxb.mulx(BYPASS);
			}
			//					rdax	mono,1
			sfxb.readRegister(input, 1);
			//					wrax	pout,1
			sfxb.writeRegister(POUT, 0);

			// last instruction clears accumulator
			p = this.getPin("Mix Out");
			p.setRegister(POUT);
			p = this.getPin("Wet Out");
			p.setRegister(wet);
		}
		System.out.println("Phaser code gen!"); 
	}

	private void PhaseShiftStage(SpinFXBlock sfxb, int delay, int control) {
		int controlNum = getControlReg(control);
		//					rdax	temp,1
		sfxb.readRegister(temp, 1);
		//					wrax	temp1,0
		sfxb.writeRegister(temp1, 0);
		//					rdax	p6,1
		sfxb.readRegister(delay, 1);
		//					wrax	temp,1
		sfxb.writeRegister(temp, 1);
		//					mulx	phase
		sfxb.mulx(controlNum);
		//					rdax	temp1,1
		sfxb.readRegister(temp1, 1);
		//					wrax	p6,-1
		sfxb.writeRegister(delay, -1);
		//					mulx	phase
		sfxb.mulx(controlNum);
	}

	private int getControlReg(int stg) {
		if(controlMode == 0) {	// LFO
			return phase;
		} else if (controlMode == 1) {
			SpinCADPin p = this.getPin("Phase").getPinConnection();
			if(p != null) {
				return p.getRegister();
			}
		} 
		return -1;
	}

	public void editBlock(){
		if(cP == null) {
			cP = new PhaserControlPanel(this);
		}
	}

	public int getStages() {
		return stages;
	}

	public void setStages(int stages) {
		this.stages = stages;
	}

	public void setControlMode(int i) {
		controlMode = i;		
	}

	public int getControlMode() {
		return controlMode;
	}

	public void clearCP() {
		cP = null;		
	}
}
