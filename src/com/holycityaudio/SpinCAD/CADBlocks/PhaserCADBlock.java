package com.holycityaudio.SpinCAD.CADBlocks;
import com.holycityaudio.SpinCAD.SpinCADPin;
import com.holycityaudio.SpinCAD.SpinFXBlock;

public class PhaserCADBlock extends ModulationCADBlock{
	/**
	 * 
	 */
	private static final long serialVersionUID = 343880108475812086L;
	int TEMP, TEMP1, PHASE;


	public PhaserCADBlock(int x, int y) {
		super(x, y);
		addControlInputPin(this, "LFO Speed");
		addControlInputPin(this, "LFO Width");
		// TODO Auto-generated constructor stub
		setName("Phaser");
	}

	public void generateCode(SpinFXBlock sfxb) {

		//				equ	mono	reg0
		int MONO = -1;
		int Control1 = -1;
		SpinCADPin p = this.getPin("Audio Input 1").getPinConnection();
		sfxb.comment(getName());

		if(p != null) {
			MONO = p.getRegister();
			//				equ	phase	reg5
			PHASE = sfxb.allocateReg();
			//				equ	pout	reg6
			int POUT = sfxb.allocateReg();
			//				equ	p1	reg7
			int P1 = sfxb.allocateReg();
			//				equ	p2	reg8
			int P2 = sfxb.allocateReg();
			//				equ	p3	reg9
			int P3 = sfxb.allocateReg();
			//				equ	p4	reg10
			int P4 = sfxb.allocateReg();
			//				equ	p5	reg11
			int P5 = sfxb.allocateReg();
			//				equ	p6	reg12
			int P6 = sfxb.allocateReg();
			//				equ	p7	reg13
			int P7 = sfxb.allocateReg();
			//				equ	p8	reg14
			int P8 = sfxb.allocateReg();
			//				equ	temp	reg15
			TEMP = sfxb.allocateReg();
			//				equ	temp1	reg16
			TEMP1 = sfxb.allocateReg();
			//				equ	bypass	reg17
			int BYPASS = sfxb.allocateReg();

			//		skp	run,endclr
			sfxb.skip(RUN, 1);
			//		wlds	sin1,0,32767
			sfxb.loadSinLFO(1, 0, 32767);
			// 		endclr:

			//					;GA_DEMO	Phase shifter

			//					;Pot0 = Reverb level
			//					;Pot1 = Phase rate
			//					;pot2 = Sweep width




			//					;Do phase shifter from sin1:

			//					rdax	pot2,1
			p = this.getPin("Control Input 1").getPinConnection();
			int depth = -1;
			if(p != null) {
				depth = p.getRegister();
			}
				
			sfxb.readRegister(depth, 1.0);
			//					rdax	bypass,0.9
			sfxb.readRegister(BYPASS, 0.9);
			//					wrax	bypass,0
			sfxb.writeRegister(BYPASS, 0);

			p = this.getPin("Control Input 2").getPinConnection();
			int speed = -1;
			if(p != null) {
				speed = p.getRegister();
			}
			//					rdax	pot1,1
			sfxb.readRegister(speed, 1.0);
			//					mulx	pot1
			sfxb.mulx(speed);
			//					sof	0.2,0.02
			sfxb.scaleOffset(0.2, 0.02);
			//					wrax	sin1_rate,0
			sfxb.writeRegister(SIN1_RATE, 0);

			//					cho	rdal,sin1		;read sin1 as +/-1
			sfxb.chorusReadValue(SIN1);
			//					sof	0.5,0.5		;make positive only sin ranges 0 to 1
			sfxb.scaleOffset(0.5, 0.5);
			//					log	0.5,0
			sfxb.log(0.5, 0);
			//					exp	1,0		;square root function
			sfxb.exp(1,0);
			//					sof	1,-0.5		;make +/-0.5
			sfxb.scaleOffset(1.0, -0.5);
			//					sof	1.999,0		;make +/-1 again
			sfxb.scaleOffset(1.999, 0);
			//					mulx	pot2		;pot2 controls width and mix
			sfxb.mulx(depth);
			//					sof	0.1,0.85
			sfxb.scaleOffset(0.1 , 0.85);
			//					wrax	phase,0		;phase variable ranges 0.8 to 0.95
			sfxb.writeRegister(PHASE, 0);

			//					rdax	p1,1
			sfxb.readRegister(P1, 1);
			//					wrax	temp,1
			sfxb.writeRegister(TEMP, 1);
			//					mulx	phase
			sfxb.mulx(PHASE);
			//					rdax	mono,1/64	;input to phase shift network
			sfxb.readRegister(MONO, 1.0/64);
			//					wrax	p1,-1
			sfxb.writeRegister(P1, -1);
			//					mulx	phase
			sfxb.mulx(PHASE);

			PhaseShiftStage(sfxb ,P2);
			PhaseShiftStage(sfxb ,P3);
			PhaseShiftStage(sfxb ,P4);
			PhaseShiftStage(sfxb ,P5);
			PhaseShiftStage(sfxb ,P6);
			PhaseShiftStage(sfxb ,P7);
			PhaseShiftStage(sfxb ,P8);

			sfxb.readRegister(TEMP, 1);

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

			//					mulx	bypass
			sfxb.mulx(BYPASS);
			//					rdax	mono,1
			sfxb.readRegister(MONO, 1);
			//					wrax	pout,1
			sfxb.writeRegister(POUT, 1);

			// last instruction clears accumulator
			p = this.getPin("Audio Output 1");
			p.setRegister(POUT);
		}
		System.out.println("Phaser code gen!");
	}

	private void PhaseShiftStage(SpinFXBlock sfxb, int register) {
		//					rdax	temp,1
		sfxb.readRegister(TEMP, 1);
		//					wrax	temp1,0
		sfxb.writeRegister(TEMP1, 0);
		//					rdax	p6,1
		sfxb.readRegister(register, 1);
		//					wrax	temp,1
		sfxb.writeRegister(TEMP, 1);
		//					mulx	phase
		sfxb.mulx(PHASE);
		//					rdax	temp1,1
		sfxb.readRegister(TEMP1, 1);
		//					wrax	p6,-1
		sfxb.writeRegister(register, -1);
		//					mulx	phase
		sfxb.mulx(PHASE);
	}

}
