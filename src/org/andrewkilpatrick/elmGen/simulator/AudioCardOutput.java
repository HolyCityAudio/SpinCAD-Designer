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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.andrewkilpatrick.elmGen.ElmProgram;

/**
 * Audio output sink that writes processed samples to the sound card.
 *
 * GSW 2026-03-22: Extracted from elmGen-0.5.jar and fixed for MacOS.
 *
 * Problems fixed:
 *  1. Original used default buffer size — too small on MacOS CoreAudio.
 *  2. Blocking line.write() can hang forever on MacOS if CoreAudio stalls.
 *     Now writes only as much as line.available() allows, with a running
 *     flag check between chunks so stopSimulator() can break the loop.
 *  3. close() called drain() which blocks if the line is stuck.
 *     Now calls stop/flush/close for immediate teardown.
 *  4. Pre-allocates the byte conversion buffer to reduce GC pressure
 *     in the real-time audio path.
 */
public class AudioCardOutput implements AudioSink {
	private SourceDataLine line = null;
	private volatile boolean running = true;
	private byte[] outBuf;  // pre-allocated conversion buffer

	public AudioCardOutput() throws LineUnavailableException {
		AudioFormat format = new AudioFormat(
				(float) ElmProgram.SAMPLERATE,  // sample rate
				16,                              // bits per sample
				2,                               // channels (stereo)
				true,                            // signed
				false);                          // little-endian

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine) AudioSystem.getLine(info);

		// Explicit buffer: 0.5 seconds of stereo 16-bit audio.
		int bytesPerFrame = 4;  // 2 channels × 2 bytes (16-bit)
		int bufferFrames = ElmProgram.SAMPLERATE / 2;  // 0.5 seconds
		int bufferBytes = bufferFrames * bytesPerFrame;
		line.open(format, bufferBytes);

		// Pre-allocate for the max batch size used by SpinSimulator (8192 samples)
		outBuf = new byte[8192 * 2];

		line.start();
	}

	@Override
	public void writeAdc(int[] buf, int len) {
		// no-op: audio card output only handles DAC writes
	}

	@Override
	public void writeDac(int[] buf, int len) {
		if (!running || !line.isOpen()) return;
		if (len < 1 || len > buf.length) return;

		// Grow conversion buffer if needed (unlikely — batch size is usually 8192)
		int byteLen = len * 2;
		if (byteLen > outBuf.length) {
			outBuf = new byte[byteLen];
		}

		// Convert int samples (FV-1 24-bit fixed point) to 16-bit little-endian bytes
		int pos = 0;
		for (int i = 0; i < len; i++) {
			// Extract bits 8-23 as a 16-bit sample (little-endian: low byte first)
			outBuf[pos++] = (byte) ((buf[i] & 0xFF00) >> 8);
			outBuf[pos++] = (byte) ((buf[i] & 0xFF0000) >> 16);
		}

		// Write in chunks sized to line.available() so we never block
		// indefinitely in line.write(). Check 'running' between chunks
		// so that close() can break us out promptly.
		int written = 0;
		while (written < pos && running) {
			int avail = line.available();
			if (avail <= 0) {
				try { Thread.sleep(1); } catch (InterruptedException e) { return; }
				continue;
			}
			int toWrite = Math.min(pos - written, avail);
			int n = line.write(outBuf, written, toWrite);
			if (n <= 0) break;
			written += n;
		}
	}

	@Override
	public void close() {
		running = false;
		if (line != null) {
			// stop + flush gives immediate teardown — drain() would block
			// if the line is stuck, which is the whole problem on MacOS.
			line.stop();
			line.flush();
			line.close();
		}
	}
}
