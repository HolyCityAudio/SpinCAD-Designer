/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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
package org.andrewkilpatrick.elmGen.simulator;

import java.util.List;
import java.io.IOException;
import java.util.LinkedList;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JPanel;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.instructions.Instruction;
import org.andrewkilpatrick.elmGen.util.Util;

// GSW added extension to thread class, not 100% certain it was needed
public class SpinSimulator extends Thread {
	enum Port {
		ADC, DAC, SCOPE1, SCOPE2
	};

	ElmProgram prog = null;
	SimulatorState state;
	int pot0 = 0;
	int pot1 = 0;
	int pot2 = 0;
	boolean runSimulator = true;
	boolean loopMode = true;
	String inputFilename = null;
	String outputFilename = null;
	LinkedList<AudioSink> audioSinks = null;
	public LevelLogger scope, levelL = null;

	/**
	 * Creates a simulator.
	 * 
	 * @param prog
	 *            the SpinProgram to simulate
	 * @param inputFilename
	 *            the filename to read
	 * @param outputFilename
	 *            the filename to write or null if the sound card should be used
	 * @param pot0
	 *            the initial state of pot 0
	 * @param pot1
	 *            the initial state of pot 1
	 * @param pot2
	 *            the initial state of pot 2
	 */
	public SpinSimulator(ElmProgram prog, String inputFilename,
			String outputFilename, double pot0, double pot1, double pot2) {
		state = new SimulatorState();
		this.prog = prog;
		this.inputFilename = inputFilename;
		this.outputFilename = outputFilename;
		this.pot0 = Util.doubleToReg(pot0);
		this.pot1 = Util.doubleToReg(pot1);
		this.pot2 = Util.doubleToReg(pot2);
		audioSinks = new LinkedList<AudioSink>();
//		System.out.println("SpinSimulator - starting up simulator for: " + prog.getName());
	}

	// GSW new class for integration into SpinCAD Designer
	public SpinSimulator() {
		state = new SimulatorState();

	}

	/**
	 * Runs the simulator.
	 */
	public void run() {
		try {
			AudioSource input = new AudioFileReader(inputFilename, loopMode);
			// audio card output
			if (outputFilename == null) {
				audioSinks.add(new AudioCardOutput());
			}
			// file writer output
			else {
				audioSinks.add(new AudioFileWriter(outputFilename));
			}
			// TODO debug GSW trying to limit data generation
			simulate(0, input, audioSinks);
			// simulate(65536, input, audioSinks);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		System.out.println("Simulation ended.");
	}

	/**
	 * Process audio through the emulator.
	 * 
	 * @param tailout
	 *            the samples to process after the file ends
	 * @param input
	 *            the audio input
	 * @param sinks
	 *            the audio sinks
	 * @throws IOException
	 *             if there is an I/O error
	 */
	// GSW added List, to help deal with comments
	private List<Instruction> simList = null;

	public void simulate(int tailout, AudioSource input,
			LinkedList<AudioSink> sinks) throws IOException {

		long time = System.currentTimeMillis();
		int totalSamps = 0;
		// GSW added this stuff to insert comments per SpinCADBlock into ASM
		// copy instList to simList, deleting comments
		simList = new LinkedList<Instruction>();
		for (int i = 0; i < prog.getCodeLen(); i++) {
			// skip comments
			if (prog.getInstruction(i).getInstructionString().startsWith(";-") == false) {
				simList.add(prog.getInstruction(i));
			}
		}

		// process file
		int inBuf[] = new int[8192];
		int outBuf[] = new int[8192];
		System.out.println("processing file");
		while (runSimulator) {
			int ret = input.read(inBuf);
			if (ret < 1) {
				runSimulator = false;
			} else {
				for (int i = 0; i < audioSinks.size(); i++) {
					audioSinks.get(i).writeAdc(inBuf, ret);
				}

				for (int i = 0; i < ret; i += 2) {
					// for(int i = 0; i < 20; i += 2) {
					state.setRegVal(ElmProgram.ADCL, inBuf[i]);
					state.setRegVal(ElmProgram.ADCR, inBuf[i + 1]);
					state.setRegVal(ElmProgram.POT0, pot0);
					state.setRegVal(ElmProgram.POT1, pot1);
					state.setRegVal(ElmProgram.POT2, pot2);
					processSample();
					outBuf[i] = state.getRegVal(ElmProgram.DACL);
					outBuf[i + 1] = state.getRegVal(ElmProgram.DACR);
					// uncomment these two lines to bypass the effect to check
					// the audio I/O
					// outBuf[i] = inBuf[i];
					// outBuf[i + 1] = inBuf[i + 1];
				}

				// write all the outputs - only one can block
				for (int i = 0; i < audioSinks.size(); i++) {
					audioSinks.get(i).writeDac(outBuf, ret);
				}
				totalSamps += outBuf.length / 2;
			}
		}

		// process tailout
		System.out.println("tailing out: " + tailout + " samples");
		long tailoutSamps = 0;
		while (tailoutSamps < tailout) {
			for (int i = 0; i < outBuf.length; i += 2) {
				state.setRegVal(ElmProgram.ADCL, 0);
				state.setRegVal(ElmProgram.ADCR, 0);
				state.setRegVal(ElmProgram.POT0, pot0);
				state.setRegVal(ElmProgram.POT1, pot1);
				state.setRegVal(ElmProgram.POT2, pot2);
				processSample();
				outBuf[i] = state.getRegVal(ElmProgram.DACL);
				outBuf[i + 1] = state.getRegVal(ElmProgram.DACR);
			}

			// write all the outputs - only one can block
			for (int i = 0; i < audioSinks.size(); i++) {
				audioSinks.get(i).writeDac(outBuf, outBuf.length);
			}
			tailoutSamps += outBuf.length / 2;
			totalSamps += outBuf.length / 2;
		}

		input.close();
		for (int i = 0; i < audioSinks.size(); i++) {
			audioSinks.get(i).close();
		}
		long totalTime = System.currentTimeMillis() - time;
		int procRate = (int) (totalSamps / ((totalTime / 1000) + 1));
		System.out.println("processed: " + totalSamps + " samples in: "
				+ totalTime + " ms - " + "process rate: " + procRate
				+ " samples/sec");
	}

	/**
	 * Stops the simulator.
	 */
	public void stopSimulator() {
		runSimulator = false;
	}

	/**
	 * Runs the program across one sample.
	 */
	public void processSample() {
		// GSW changed this line to integrate into SpinCAD Designer
		int codeLen = simList.size(); // this includes comments
		state.resetPC();
		while (state.getPC() < codeLen) {
			Instruction inst = null;
			inst = simList.get(state.getPC());
			inst.simulate(state);
			state.incrementPC();
		}
		state.sampleIncrement();
	}

	/**
	 * Sets the value of a control pot.
	 * 
	 * @param pot
	 *            the pot number (0, 1 or 2)
	 * @param val
	 *            the new value (0.0 to 1.0)
	 */
	public void setPot(int pot, double val) {
		double value = val;
		if (value < 0.0) {
			value = 0.0;
		}
		if (pot == 0) {
			pot0 = Util.doubleToReg(value);
		} else if (pot == 1) {
			pot1 = Util.doubleToReg(value);
		} else if (pot == 2) {
			pot2 = Util.doubleToReg(value);
		} else {
			System.err.println("pot out of range: " + pot);
			return;
		}
	}

	public double getPot(int pot) {
		if (pot == 0) {
			return Util.regToDouble(pot0);
		} else if (pot == 1) {
			return Util.regToDouble(pot1);
		} else if (pot == 2) {
			return Util.regToDouble(pot2);
		} else {
			System.err.println("pot out of range: " + pot);
			return 0.0;
		}
	}

	public void showInteractiveControls() {
		// GSW changed this part to integrate into SpinCAD Designer
		new SimControls(this);
	}

	public boolean getLoopMode() {
		return loopMode;
	}

	public void setLoopMode(boolean loopMode) {
		this.loopMode = loopMode;
	}

	public void showLevelLogger() {
		audioSinks.add(new LevelLogger());
		System.out.print(audioSinks);
	}

	// GSW added this part to integrate into SpinCAD Designer
	public void showLevelLogger(JPanel p) {
		levelL = new LevelLogger(p);
		audioSinks.add(levelL);
//		System.out.println(audioSinks);
	}

	// GSW added this part to integrate into SpinCAD Designer
	public void showScope(JPanel p) {
		scope = new LevelLogger(p);
		scope.setLogMode(0);
		scope.windowRatio = 8;
		audioSinks.add(scope);
//		System.out.println(audioSinks);
	}

	public void showLevelMeter() {
		audioSinks.add(new LevelMeter());
	}

	public void showLevelMeter(JPanel p) {
		LevelMeter meter1 = new LevelMeter(p);
		audioSinks.add(meter1);
	}
}
