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
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class PhaserCADBlock extends ModulationCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 343880108475812086L;
	int temp, temp1, phase, stages, controlMode;


	public PhaserCADBlock(int x, int y) {
		super(x, y);
		hasControlPanel = true;
		stages = 4;
		controlMode = 0; 	// default to LFO mode
		setupControls();
		addOutputPin(this, "Dry");
		setName("Phaser");
	}
	
	private void setupControls() {
		if(controlMode == 0) {
			addControlInputPin(this, "LFO Speed");
			addControlInputPin(this, "LFO Width");			
		} else if (controlMode == 1) {
			addControlInputPin(this, "Phase");
		}  else if (controlMode == 2) {
			for (int i = 0; i < stages; i++)
				addControlInputPin(this, "Phase " + (i + 1));
		}
	}

	public void generateCode(SpinFXBlock sfxb) {

		//				equ	mono	reg0
		int MONO = -1;
		int Control1 = -1;
		int dry = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		sfxb.comment(getName());


		if(p != null) {
			MONO = p.getRegister();
			//				equ	phase	reg5
			phase = sfxb.allocateReg();
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
			dry = sfxb.allocateReg();

			int BYPASS = -1;

			SpinCADPin phaseConnected = this.getPin("Phase").getPinConnection();
			if(phaseConnected != null) {
				phase = phaseConnected.getRegister();
			}
			else
			{			
				BYPASS = sfxb.allocateReg();

				sfxb.skip(RUN, 1);
				sfxb.loadSinLFO(1, 0, 32767);

				p = this.getPin("LFO Width").getPinConnection();
				int depth = -1;
				if(p != null) {
					depth = p.getRegister();
				}

				sfxb.readRegister(depth, 1.0);
				sfxb.readRegister(BYPASS, 0.9);
				sfxb.writeRegister(BYPASS, 0);

				p = this.getPin("LFO Speed").getPinConnection();
				int speed = -1;
				if(p != null) {
					speed = p.getRegister();
				}

				sfxb.readRegister(speed, 1.0);
				sfxb.mulx(speed);
				sfxb.scaleOffset(0.3, 0.02);
				sfxb.writeRegister(SIN1_RATE, 0);

				sfxb.chorusReadValue(SIN1);
				sfxb.scaleOffset(0.5, 0.5);
				sfxb.log(0.4, 0);
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
			sfxb.mulx(phase);
			sfxb.readRegister(MONO, 1.0/64);
			sfxb.writeRegister(p1, -1);
			sfxb.mulx(phase);

			PhaseShiftStage(sfxb ,p2);
			if(stages > 1) {
				PhaseShiftStage(sfxb ,p3);
				PhaseShiftStage(sfxb ,p4);
			}
			if (stages > 2) {
				PhaseShiftStage(sfxb ,p5);
				PhaseShiftStage(sfxb ,p6);
			}
			if(stages > 3) {
				PhaseShiftStage(sfxb ,p7);
				PhaseShiftStage(sfxb ,p8);
			}
			if(stages > 4) {
				PhaseShiftStage(sfxb ,p9);
				PhaseShiftStage(sfxb ,p10);
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

			sfxb.writeRegister(dry, 1.0);
			//					mulx	bypass
			if(phaseConnected == null) {
				sfxb.mulx(BYPASS);
			}
			//					rdax	mono,1
			sfxb.readRegister(MONO, 1);
			//					wrax	pout,1
			sfxb.writeRegister(POUT, 0);

			// last instruction clears accumulator
			p = this.getPin("Audio Output 1");
			p.setRegister(POUT);
			p = this.getPin("Dry");
			p.setRegister(dry);
		}
		System.out.println("Phaser code gen!"); 
	}

	private void PhaseShiftStage(SpinFXBlock sfxb, int register) {
		//					rdax	temp,1
		sfxb.readRegister(temp, 1);
		//					wrax	temp1,0
		sfxb.writeRegister(temp1, 0);
		//					rdax	p6,1
		sfxb.readRegister(register, 1);
		//					wrax	temp,1
		sfxb.writeRegister(temp, 1);
		//					mulx	phase
		sfxb.mulx(phase);
		//					rdax	temp1,1
		sfxb.readRegister(temp1, 1);
		//					wrax	p6,-1
		sfxb.writeRegister(register, -1);
		//					mulx	phase
		sfxb.mulx(phase);
	}
	
	public void editBlock(){
		new PhaserControlPanel(this);
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
}
