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
	}
	
	public int read(int buf[]) throws IOException {
		byte inBuf[] = new byte[buf.length * 2];
		int ret = audioInputStream.read(inBuf);
		if(ret < 1 && loop) {
			File soundFile = new File(filename);
			try {
				audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			} catch (UnsupportedAudioFileException e) {
				throw new IOException("can't loop file: " + e.getMessage());
			}			
			ret = audioInputStream.read(inBuf);
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
