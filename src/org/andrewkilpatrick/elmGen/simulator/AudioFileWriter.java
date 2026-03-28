package org.andrewkilpatrick.elmGen.simulator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.andrewkilpatrick.elmGen.ElmProgram;

/**
 * Writes simulator DAC output to a WAV file.
 * Collects all samples in memory and writes the file on close().
 */
public class AudioFileWriter implements AudioSink {
    private final String filename;
    private final ByteArrayOutputStream dacBytes = new ByteArrayOutputStream();

    public AudioFileWriter(String filename) {
        this.filename = filename;
    }

    @Override
    public void writeAdc(int[] buf, int len) {
        // not used for output
    }

    @Override
    public void writeDac(int[] buf, int len) {
        // buf contains interleaved L/R samples as 24-bit signed ints (shifted left 8 from 16-bit)
        // Convert back to 16-bit little-endian PCM
        for (int i = 0; i < len; i++) {
            short sample = (short) (buf[i] >> 8);
            dacBytes.write(sample & 0xff);
            dacBytes.write((sample >> 8) & 0xff);
        }
    }

    @Override
    public void close() {
        try {
            byte[] data = dacBytes.toByteArray();
            AudioFormat format = new AudioFormat(
                    ElmProgram.SAMPLERATE, 16, 2, true, false);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            long frameCount = data.length / format.getFrameSize();
            AudioInputStream ais = new AudioInputStream(bais, format, frameCount);
            AudioSystem.write(ais, AudioFileFormat.Type.WAVE, new File(filename));
            ais.close();
        } catch (IOException e) {
            System.err.println("AudioFileWriter: failed to write " + filename + ": " + e);
            e.printStackTrace();
        }
    }
}
