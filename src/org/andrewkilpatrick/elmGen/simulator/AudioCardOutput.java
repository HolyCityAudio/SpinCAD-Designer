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
 * Uses a dedicated writer thread so that the simulation thread is never
 * blocked by CoreAudio on macOS.  A small queue of byte buffers sits
 * between the simulation (producer) and the audio card (consumer).
 */
public class AudioCardOutput implements AudioSink {
	private SourceDataLine line = null;
	private volatile boolean running = true;

	// Queue between simulation thread (producer) and audio writer thread (consumer).
	// Capacity of 4 buffers gives ~0.5s of headroom before the simulation blocks.
	private final ArrayBlockingQueue<byte[]> queue = new ArrayBlockingQueue<>(4);
	private Thread writerThread;

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
		line.start();

		// Start the dedicated audio writer thread
		writerThread = new Thread(this::writerLoop, "AudioCardWriter");
		writerThread.setDaemon(true);
		writerThread.start();
	}

	/** Writer thread: drains the queue into the SourceDataLine. */
	private void writerLoop() {
		while (running) {
			try {
				byte[] buf = queue.poll(50, java.util.concurrent.TimeUnit.MILLISECONDS);
				if (buf == null) continue;  // timeout, check running flag

				int written = 0;
				while (written < buf.length && running) {
					int n = line.write(buf, written, buf.length - written);
					if (n <= 0) break;
					written += n;
				}
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	@Override
	public void writeAdc(int[] buf, int len) {
		// no-op: audio card output only handles DAC writes
	}

	@Override
	public void writeDac(int[] buf, int len) {
		if (!running) return;
		if (len < 1 || len > buf.length) return;

		// Convert int samples (FV-1 24-bit fixed point) to 16-bit little-endian bytes
		byte[] outBuf = new byte[len * 2];
		int pos = 0;
		for (int i = 0; i < len; i++) {
			// Extract bits 8-23 as a 16-bit sample (little-endian: low byte first)
			outBuf[pos++] = (byte) ((buf[i] & 0xFF00) >> 8);
			outBuf[pos++] = (byte) ((buf[i] & 0xFF0000) >> 16);
		}

		// Enqueue for the writer thread.  Block when the queue is full so the
		// simulation runs at the audio sample rate instead of racing ahead.
		// Use a timeout loop so we can still exit promptly when stopped.
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
