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
 * GSW 2026-03-22: Extracted from elmGen-0.5.jar and fixed buffer sizing.
 * The original code called line.open(format) with no buffer size, which
 * uses a platform-dependent default. On MacOS (CoreAudio) the default
 * buffer is very small, causing underruns and silent output after a few
 * seconds of playback. Specifying an explicit buffer (0.5 s) fixes this.
 */
public class AudioCardOutput implements AudioSink {
	SourceDataLine line = null;

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
		// The original code used the platform default, which is too small
		// on MacOS CoreAudio, leading to underruns and silent playback.
		int bytesPerFrame = 4;  // 2 channels × 2 bytes (16-bit)
		int bufferFrames = ElmProgram.SAMPLERATE / 2;  // 0.5 seconds
		int bufferBytes = bufferFrames * bytesPerFrame;
		line.open(format, bufferBytes);
		line.start();
	}

	@Override
	public void writeAdc(int[] buf, int len) {
		// no-op: audio card output only handles DAC writes
	}

	@Override
	public void writeDac(int[] buf, int len) {
		if (len < 1 || len > buf.length) {
			return;
		}
		// Convert int samples (FV-1 24-bit fixed point) to 16-bit little-endian bytes
		byte[] bytes = new byte[len * 2];
		int pos = 0;
		for (int i = 0; i < len; i++) {
			// Extract bits 8-23 as a 16-bit sample (little-endian: low byte first)
			bytes[pos++] = (byte) ((buf[i] & 0xFF00) >> 8);
			bytes[pos++] = (byte) ((buf[i] & 0xFF0000) >> 16);
		}
		line.write(bytes, 0, pos);
	}

	@Override
	public void close() {
		line.drain();
		line.close();
	}
}
