/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2026.  Look for GSW in code.
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

import java.util.concurrent.ArrayBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.andrewkilpatrick.elmGen.ElmProgram;

/**
 * Audio output sink that writes processed samples to the sound card.
 *
 * Opens the audio line at 44100 Hz (universally supported by all OS audio
 * subsystems) and resamples from the FV-1 native rate (32768 Hz) using
 * linear interpolation.  This avoids macOS CoreAudio deadlocks caused by
 * its sample-rate converter choking on the non-standard 32768 Hz rate.
 *
 * A dedicated writer thread keeps the simulation from blocking on I/O.
 */
public class AudioCardOutput implements AudioSink {
	private SourceDataLine line = null;
	private volatile boolean running = true;

	private static final int OUTPUT_RATE = 44100;

	// Resampler: input rate / output rate = advance per output sample
	private final double srcStep;
	private double srcPhase = 0.0;
	private int prevL = 0, prevR = 0;

	// Queue between simulation thread (producer) and audio writer thread (consumer).
	private final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(4);
	private Thread writerThread;

	public AudioCardOutput() throws LineUnavailableException {
		srcStep = (double) ElmProgram.SAMPLERATE / OUTPUT_RATE;

		AudioFormat format = new AudioFormat(
				(float) OUTPUT_RATE,             // 44100 Hz output
				16,                              // bits per sample
				2,                               // channels (stereo)
				true,                            // signed
				false);                          // little-endian

		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		line = (SourceDataLine) AudioSystem.getLine(info);

		// ~100ms of buffer at the output rate (enough to prevent underrun)
		int bytesPerFrame = 4;  // 2 channels × 2 bytes (16-bit)
		int bufferFrames = OUTPUT_RATE / 10;
		int bufferBytes = bufferFrames * bytesPerFrame;
		line.open(format, bufferBytes);
		line.start();

		// Start the dedicated audio writer thread
		writerThread = new Thread(this::writerLoop, "AudioCardWriter");
		writerThread.setDaemon(true);
		writerThread.start();
	}

	/** Writer thread: drains the queue into the SourceDataLine. */
	private void writerLoop() {
		try {
			while (running) {
				byte[] buf = queue.poll(50, java.util.concurrent.TimeUnit.MILLISECONDS);
				if (buf == null) continue;

				int written = 0;
				while (written < buf.length && running) {
					int chunk = Math.min(4096, buf.length - written);
					int n = line.write(buf, written, chunk);
					if (n <= 0) break;
					written += n;
				}
			}
		} catch (InterruptedException e) {
			// Thread interrupted during shutdown — normal exit
		} catch (Exception e) {
			System.err.println("AudioCardOutput: writer thread failed: " + e);
			e.printStackTrace();
		}
	}

	@Override
	public void writeAdc(int[] buf, int len) {
		// no-op: audio card output only handles DAC writes
	}

	@Override
	public void writeDac(int[] buf, int len) {
		if (!running) return;
		if (len < 2 || len > buf.length) return;

		int inputFrames = len / 2;  // stereo interleaved: 2 ints per frame

		// How many output frames we can produce from this input buffer.
		// Each output frame advances srcStep in the input.
		int outputFrames = (int) ((inputFrames - srcPhase) / srcStep);
		if (outputFrames < 0) outputFrames = 0;
		if (srcPhase + outputFrames * srcStep < inputFrames) {
			outputFrames++;
		}

		byte[] outBuf = new byte[outputFrames * 4];  // stereo 16-bit = 4 bytes/frame
		int pos = 0;

		for (int j = 0; j < outputFrames; j++) {
			double srcPos = srcPhase + j * srcStep;
			int idx = (int) srcPos;
			double frac = srcPos - idx;

			// Bounds check (should not happen, but guard against float drift)
			if (idx >= inputFrames) break;

			int sL0 = buf[idx * 2];
			int sR0 = buf[idx * 2 + 1];

			int sL1, sR1;
			int idx1 = idx + 1;
			if (idx1 < inputFrames) {
				sL1 = buf[idx1 * 2];
				sR1 = buf[idx1 * 2 + 1];
			} else {
				sL1 = sL0;
				sR1 = sR0;
			}

			// Linear interpolation
			int outL = (int) (sL0 + (sL1 - sL0) * frac);
			int outR = (int) (sR0 + (sR1 - sR0) * frac);

			// 16-bit little-endian (extract bits 8-23 of 24-bit fixed point)
			outBuf[pos++] = (byte) ((outL & 0xFF00) >> 8);
			outBuf[pos++] = (byte) ((outL & 0xFF0000) >> 16);
			outBuf[pos++] = (byte) ((outR & 0xFF00) >> 8);
			outBuf[pos++] = (byte) ((outR & 0xFF0000) >> 16);
		}

		// Advance resampler phase for next buffer
		srcPhase = srcPhase + outputFrames * srcStep - inputFrames;
		prevL = buf[(inputFrames - 1) * 2];
		prevR = buf[(inputFrames - 1) * 2 + 1];

		// Trim outBuf if the safety break fired
		if (pos < outBuf.length) {
			byte[] trimmed = new byte[pos];
			System.arraycopy(outBuf, 0, trimmed, 0, pos);
			outBuf = trimmed;
		}

		if (outBuf.length == 0) return;

		// Enqueue for the writer thread
		while (running) {
			try {
				if (queue.offer(outBuf, 100, java.util.concurrent.TimeUnit.MILLISECONDS)) break;
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	@Override
	public void close() {
		running = false;
		if (writerThread != null) {
			writerThread.interrupt();
			try { writerThread.join(2000); } catch (InterruptedException ignored) {}
		}
		if (line != null && line.isOpen()) {
			line.stop();
			line.flush();
			line.close();
		}
	}
}
