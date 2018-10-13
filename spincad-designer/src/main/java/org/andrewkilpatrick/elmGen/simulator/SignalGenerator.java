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

import java.io.IOException;

public class SignalGenerator implements AudioSource {
	double startFrequency;
	double endFrequency;
	double sweepSpeed;
	boolean logSweep;

	// running state
	long sin = 0;  // initial value
	long cos = -0x7fff00l;  // peak amplitude	
	int amp = 32700;
	int freq = 64000;
	
	
	/**
	 * Creates a new signal generator.
	 */
	public SignalGenerator() {
	
	}
	
	public double getStartFrequency() {
		return startFrequency;
	}

	public void setStartFrequency(double startFrequency) {
		this.startFrequency = startFrequency;
	}

	public double getEndFrequency() {
		return endFrequency;
	}

	public void setEndFrequency(double endFrequency) {
		this.endFrequency = endFrequency;
	}

	public double getSweepSpeed() {
		return sweepSpeed;
	}

	public void setSweepSpeed(double sweepSpeed) {
		this.sweepSpeed = sweepSpeed;
	}

	public boolean isLogSweep() {
		return logSweep;
	}

	public void setLogSweep(boolean logSweep) {
		this.logSweep = logSweep;
	}

	public void close() throws IOException { }

	public int read(int[] buf) throws IOException {
		
		long max = 0;
		
		for(int i = 0; i < buf.length; i += 2) {
			// calculate next value
			long acc = sin;			
			acc = (acc * freq) >> 17;
			acc = acc + cos;
			cos = acc;
			acc = -acc;
			acc = (acc * freq) >> 17;
			acc = acc + sin;
			sin = acc;
			
			long val = (sin * amp) >> 15;
			
			if(val > max) {
				max = val;
			}
			
			if(val < 0) {
				val = -(int)(0x7fffff - (val & 0x7fffff));
			}
			else {
				val = (int)(val & 0x7fffff);
			}
			buf[i] = (int)val;
			buf[i + 1] = (int)val;			
		}
		
		System.out.println("max: " + String.format("0x%08x", max));
		
		return buf.length;
	}
}