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

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

import org.andrewkilpatrick.elmGen.ElmProgram;


public class AudioFileReader implements AudioSource {
	String filename;
	AudioInputStream audioInputStream;
	final boolean loop;
	final int frameSize;       // bytes per frame (channels × bytesPerSample)
	final long totalFrames;    // total frames in the file (-1 if unknown)
	long framesRead;           // frames consumed from current stream

	public AudioFileReader(String filename, boolean loop)
			throws UnsupportedAudioFileException, IOException {
		this.filename = filename;
		this.loop = loop;
		File soundFile = new File(filename);
		audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		AudioFormat audioFormat = audioInputStream.getFormat();
		if(audioFormat.getChannels() != 2) {
			throw new UnsupportedAudioFileException("the file must be 2 channels");
		}
		if(audioFormat.getSampleSizeInBits() != 16) {
			throw new UnsupportedAudioFileException("the file must be 16 bit");
		}
		if(audioFormat.getEncoding() != Encoding.PCM_SIGNED) {
			throw new UnsupportedAudioFileException("the file must be PCM signed");
		}
		if(audioFormat.getSampleRate() != ElmProgram.SAMPLERATE) {
			System.err.println("SAMPLE RATE WARNING: samples rates != " + ElmProgram.SAMPLERATE +
					" may produce wrong simulated results");
		}
		frameSize = audioFormat.getFrameSize();  // 4 for stereo 16-bit
		totalFrames = audioInputStream.getFrameLength();
		framesRead = 0;
		System.out.println("AudioFileReader: " + totalFrames + " frames");
	}

	/** Reopen the file for looping. */
	private void reopen() throws IOException {
		audioInputStream.close();
		File soundFile = new File(filename);
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (UnsupportedAudioFileException e) {
			System.out.println("AudioFileReader: loop reopen failed: " + e);
			throw new IOException("can't loop file: " + e.getMessage());
		}
		framesRead = 0;
	}

	public int read(int buf[]) throws IOException {
		byte inBuf[] = new byte[buf.length * 2];

		// If we know the file length, check whether we've consumed all frames.
		// On macOS, AudioInputStream.read() can block at EOF instead of
		// returning -1, so we proactively loop before that happens.
		if(totalFrames > 0 && framesRead >= totalFrames) {
			if(loop) {
				reopen();
			} else {
				return -1;
			}
		}

		int ret = audioInputStream.read(inBuf);

		// Fallback: also handle read() returning -1 (works on Windows/Linux)
		if(ret < 1 && loop) {
			reopen();
			ret = audioInputStream.read(inBuf);
		}

		if(ret < 1) return ret;

		// Track frames consumed so we can detect EOF proactively
		if(frameSize > 0) {
			framesRead += ret / frameSize;
		}

		int bufCount = 0;
		for(int i = 0; i < ret; i += 2) {
			buf[bufCount] = (short)((inBuf[i] & 0xff) | ((inBuf[i + 1] & 0xff) << 8)) << 8;
			bufCount ++;
		}
		return ret / 2;
	}
	
	public void close() throws IOException {
		audioInputStream.close();
	}
}
