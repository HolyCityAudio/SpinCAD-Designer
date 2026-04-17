package com.holycityaudio.SpinCAD;

import static com.holycityaudio.SpinCAD.PlotUtils.*;

import java.io.*;
import java.util.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;

/**
 * Generates stepped-tone-burst waveform plots for dynamics blocks.
 *
 * Input: 1 kHz tone bursts at -60 to 0 dBFS in 10 dB steps,
 * 200 ms on / 200 ms off. Output: stacked input/output PNG.
 */
public class DynamicsBurstDocTest {

    @TempDir
    File tempDir;

    private static final double BURST_FREQ = 1000.0;
    private static final double BURST_DURATION = 0.200;
    private static final double SILENCE_DURATION = 0.200;
    private static final double[] LEVELS_DB = {-60, -50, -40, -30, -20, -10, 0};
    private static final File DOCS_DIR = new File("docs/images");

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
        DOCS_DIR.mkdirs();
    }

    @Test
    void generateNoiseGateBurst() throws Exception {
        NoiseGateCADBlock block = new NoiseGateCADBlock(100, 100);
        block.setthresh(Math.pow(10, -40.0 / 20.0));
        generateBurstPlot(block, "Audio Out",
            "Noise Gate (thresh=-40 dB)",
            "dynamics-noisegate-burst.png");
    }

    @Test
    void generatePeakCompressorBurst() throws Exception {
        peak_compressorCADBlock block = new peak_compressorCADBlock(100, 100);
        block.setthreshDb(-25);
        block.setratio(8);
        block.setinGain(1);
        block.setmakeupDb(0);
        block.settrim(1);
        block.setattTime(0.01);
        block.setrelTime(0.001);
        generateBurstPlot(block, "Audio_Output",
            "Peak Compressor (8:1, thresh=-25 dB)",
            "dynamics-peak_compressor-burst.png");
    }

    @Test
    void generateRmsCompressorBurst() throws Exception {
        rms_compressorCADBlock block = new rms_compressorCADBlock(100, 100);
        block.setthreshDb(-25);
        block.setstrength(1.0);
        block.setinGain(1);
        block.setmakeupDb(0);
        block.settrim(1);
        block.setattTime(0.01);
        block.setrelTime(0.001);
        generateBurstPlot(block, "Audio_Output",
            "RMS Compressor (strength=1.0, thresh=-25 dB)",
            "dynamics-rms_compressor-burst.png");
    }

    @Test
    void generateRmsLimiterBurst() throws Exception {
        rms_limiterCADBlock block = new rms_limiterCADBlock(100, 100);
        block.setinGain(0.5);
        generateBurstPlot(block, "Output",
            "RMS Limiter",
            "dynamics-rms_limiter-burst.png");
    }

    @Test
    void generateSoftKneeLimiterBurst() throws Exception {
        soft_knee_limiterCADBlock block = new soft_knee_limiterCADBlock(100, 100);
        generateBurstPlot(block, "Audio_Output",
            "Soft Knee Limiter",
            "dynamics-soft_knee_limiter-burst.png");
    }

    // ==================== Core ====================

    private void generateBurstPlot(SpinCADBlock block, String outputPin,
            String title, String filename) throws Exception {
        File burstWav = generateSteppedBurstWav();

        short[] stereo = simulate(block, burstWav, null, outputPin, null, tempDir);
        if (stereo == null) {
            System.out.println("  SKIP " + filename + " (sim failed)");
            return;
        }

        short[] inputStereo = readWavSamples(burstWav);
        double[] input = toDouble(extractChannel(inputStereo, 0));
        double[] output = toDouble(extractChannel(stereo, 0));

        int decimation = 8;
        double[] inputDec = decimate(input, decimation);
        double[] outputDec = decimate(output, decimation);
        double[] timeMs = timeAxisMs(inputDec.length, SAMPLE_RATE / decimation);

        writeStackedWaveformPlot(new File(DOCS_DIR, filename), title,
            timeMs, inputDec, outputDec, "Input", "Output");
        System.out.println("  wrote " + filename);
    }

    private File generateSteppedBurstWav() throws IOException {
        int burstSamples = (int) (SAMPLE_RATE * BURST_DURATION);
        int silenceSamples = (int) (SAMPLE_RATE * SILENCE_DURATION);
        int periodSamples = burstSamples + silenceSamples;
        int totalFrames = LEVELS_DB.length * periodSamples;

        byte[] data = new byte[totalFrames * 4];
        for (int lvl = 0; lvl < LEVELS_DB.length; lvl++) {
            double amplitude = 0.999 * Math.pow(10, LEVELS_DB[lvl] / 20.0);
            int offset = lvl * periodSamples;
            for (int i = 0; i < burstSamples; i++) {
                double t = (double) i / SAMPLE_RATE;
                short sample = (short) (amplitude * 32767.0
                    * Math.sin(2.0 * Math.PI * BURST_FREQ * t));
                int byteOff = (offset + i) * 4;
                data[byteOff] = (byte) (sample & 0xff);
                data[byteOff + 1] = (byte) ((sample >> 8) & 0xff);
                data[byteOff + 2] = (byte) (sample & 0xff);
                data[byteOff + 3] = (byte) ((sample >> 8) & 0xff);
            }
        }

        File wavFile = File.createTempFile("stepped_burst_", ".wav");
        wavFile.deleteOnExit();
        javax.sound.sampled.AudioFormat format =
            new javax.sound.sampled.AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(data);
        javax.sound.sampled.AudioInputStream ais =
            new javax.sound.sampled.AudioInputStream(bais, format, totalFrames);
        javax.sound.sampled.AudioSystem.write(ais,
            javax.sound.sampled.AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }
}
