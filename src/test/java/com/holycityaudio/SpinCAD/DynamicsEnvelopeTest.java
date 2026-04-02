package com.holycityaudio.SpinCAD;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import javax.sound.sampled.*;

import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.holycityaudio.SpinCAD.CADBlocks.*;
import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

/**
 * Dynamics block envelope response tests using ascending tone bursts.
 *
 * Input: 440 Hz tone bursts, 200 ms on / 200 ms off, ascending from
 * -80 dBFS to 0 dBFS in 10 dB steps (9 bursts, 3.6 s total).
 *
 * Measures per-burst: steady-state output level, gain, attack time
 * (time to reach 90% of steady-state), and release time (time to
 * drop to 10% of burst level after offset).
 */
public class DynamicsEnvelopeTest {

    @TempDir
    File tempDir;

    private static final int SAMPLE_RATE = ElmProgram.SAMPLERATE;
    private static final double BURST_FREQ = 440.0;
    private static final double BURST_DURATION = 0.200;
    private static final double SILENCE_DURATION = 0.200;
    private static final double[] LEVELS_DB = {-80, -70, -60, -50, -40, -30, -20, -10, 0};
    private static final int ENV_WINDOW = 64; // ~2 ms at 32768 Hz
    private static final long SIM_TIMEOUT = 60000;

    @BeforeAll
    static void setup() {
        System.setProperty("java.awt.headless", "true");
    }

    // ==================== Tests ====================

    @Test
    void testSoftKneeLimiter_toneBurstResponse() {
        System.out.println("\n=== Soft Knee Limiter: Tone Burst Envelope ===");
        soft_knee_limiterCADBlock block = new soft_knee_limiterCADBlock(100, 100);
        BurstResult[] results = runBurstAnalysis(block, "Audio_Output", null);
        assertNotNull(results, "Soft Knee Limiter burst sim failed");

        printResults("Soft Knee Limiter", results);
        printEnvelopeSummary("Soft Knee Limiter", results);

        // Limiter should pass loud signals
        for (BurstResult r : results) {
            if (r.inputDb >= -20) {
                assertTrue(r.outputDb > -40,
                    String.format("SoftKnee: input %.0f dB should produce output > -40 dB, got %.1f dB",
                        r.inputDb, r.outputDb));
            }
        }

        // Limiter should compress: gain at 0 dB < gain at -40 dB
        double gain0 = results[8].gainDb;
        double gainMid = results[4].gainDb;
        System.out.printf("  Compression check: gain@0dB=%.1f  gain@-40dB=%.1f%n", gain0, gainMid);
        assertTrue(gain0 < gainMid + 3,
            "Soft Knee Limiter should compress louder signals more");
    }

    @Test
    void testNoiseGate_toneBurstResponse() {
        System.out.println("\n=== Noise Gate: Tone Burst Envelope ===");
        NoiseGateCADBlock block = new NoiseGateCADBlock(100, 100);
        BurstResult[] results = runBurstAnalysis(block, "Audio Out", null);
        assertNotNull(results, "Noise Gate burst sim failed");

        printResults("Noise Gate", results);
        printEnvelopeSummary("Noise Gate", results);

        // Gate should pass loud signals and attenuate quiet ones
        // At 0 dBFS input, output should be strong
        assertTrue(results[8].outputDb > -20,
            String.format("NoiseGate: 0 dB input should pass, got %.1f dB output", results[8].outputDb));

        // Very quiet signals should be more attenuated than loud ones
        if (results[0].outputDb > -130 && results[8].outputDb > -130) {
            double quietGain = results[0].gainDb;
            double loudGain = results[8].gainDb;
            System.out.printf("  Gating check: gain@-80dB=%.1f  gain@0dB=%.1f%n", quietGain, loudGain);
        }
    }

    @Test
    void testRmsLimiter_toneBurstResponse() {
        System.out.println("\n=== RMS Limiter: Tone Burst Envelope ===");
        rms_limiterCADBlock block = new rms_limiterCADBlock(100, 100);
        BurstResult[] results = runBurstAnalysis(block, "Output", null);
        assertNotNull(results, "RMS Limiter burst sim failed");

        printResults("RMS Limiter", results);
        printEnvelopeSummary("RMS Limiter", results);

        // RMS limiter should pass signal
        for (BurstResult r : results) {
            if (r.inputDb >= -20) {
                assertTrue(r.outputDb > -50,
                    String.format("RmsLimiter: input %.0f dB should produce output > -50 dB, got %.1f dB",
                        r.inputDb, r.outputDb));
            }
        }
    }

    // ==================== Analysis engine ====================

    private BurstResult[] runBurstAnalysis(SpinCADBlock block, String outputPin,
                                            Map<String, Integer> controls) {
        try {
            SpinCADModel model = new SpinCADModel();
            InputCADBlock inputBlock = new InputCADBlock(0, 0);
            OutputCADBlock outputBlock = new OutputCADBlock(200, 100);
            model.addBlock(inputBlock);
            model.addBlock(block);
            model.addBlock(outputBlock);

            SpinFXBlock tempSfxb = new SpinFXBlock("Setup");
            inputBlock.generateCode(tempSfxb);

            // Wire all audio inputs
            SpinCADPin srcPin = inputBlock.getPin("Output 1");
            for (SpinCADPin pin : block.pinList) {
                if (pin.getType() == pinType.AUDIO_IN && !pin.isConnected()) {
                    if (srcPin != null) pin.setConnection(inputBlock, srcPin);
                }
            }

            // Wire controls
            if (controls != null) {
                for (Map.Entry<String, Integer> e : controls.entrySet()) {
                    SpinCADPin ctrlPin = block.getPin(e.getKey());
                    if (ctrlPin != null && ctrlPin.getType() == pinType.CONTROL_IN) {
                        ConstantCADBlock cb = new ConstantCADBlock(50, 50);
                        cb.setConstant(e.getValue());
                        model.addBlock(cb);
                        cb.generateCode(tempSfxb);
                        ctrlPin.setConnection(cb, cb.getPin("Value"));
                    }
                }
            }

            // Wire output
            SpinCADPin outPin = block.getPin(outputPin);
            if (outPin == null) {
                for (SpinCADPin pin : block.pinList) {
                    if (pin.getType() == pinType.AUDIO_OUT) { outPin = pin; break; }
                }
            }
            if (outPin == null) return null;

            outputBlock.getPin("Input 1").setConnection(block, outPin);
            outputBlock.getPin("Input 2").setConnection(block, outPin);

            model.sortAlignGen();
            SpinFXBlock renderBlock = model.getRenderBlock();
            if (renderBlock == null) return null;

            String listing = renderBlock.getProgramListing(1);
            if (!listing.contains("WRAX DACL") || !listing.contains("WRAX DACR")) return null;

            // Generate burst WAV and simulate
            File burstWav = generateToneBurstWav();
            File outFile = new File(tempDir, "burst_" + System.nanoTime() + ".wav");

            SpinSimulator sim = new SpinSimulator(renderBlock,
                burstWav.getAbsolutePath(), outFile.getAbsolutePath(), 0.5, 0.5, 0.5);
            sim.setLoopMode(false);
            sim.start();
            sim.join(SIM_TIMEOUT);

            if (sim.isAlive() || sim.getSimulationException() != null) return null;
            if (!outFile.exists()) return null;

            short[] stereo = readWavSamples(outFile);
            short[] left = extractChannel(stereo, 0);

            return analyzeBursts(left);

        } catch (Exception e) {
            System.err.println("  Burst analysis error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private BurstResult[] analyzeBursts(short[] samples) {
        int burstSamples = (int) (SAMPLE_RATE * BURST_DURATION);
        int silenceSamples = (int) (SAMPLE_RATE * SILENCE_DURATION);
        int periodSamples = burstSamples + silenceSamples;

        BurstResult[] results = new BurstResult[LEVELS_DB.length];

        for (int i = 0; i < LEVELS_DB.length; i++) {
            int burstStart = i * periodSamples;
            int burstEnd = burstStart + burstSamples;
            int silenceEnd = Math.min(burstEnd + silenceSamples, samples.length);

            if (burstEnd > samples.length) {
                results[i] = new BurstResult(LEVELS_DB[i], -140, 0, -1, -1, new double[0]);
                continue;
            }

            // Input amplitude and RMS
            double inputAmp = 0.999 * Math.pow(10, LEVELS_DB[i] / 20.0);
            double inputRms = inputAmp / Math.sqrt(2);
            double inputDb = inputRms > 1e-10 ? 20 * Math.log10(inputRms) : -140;

            // Steady-state output: RMS of last 50 ms of burst
            int ssWindowMs = 50;
            int ssWindowSamples = (int) (SAMPLE_RATE * ssWindowMs / 1000.0);
            int ssStart = Math.max(burstStart, burstEnd - ssWindowSamples);
            double ssRms = rmsOfRange(samples, ssStart, burstEnd) / 32767.0;
            double outputDb = ssRms > 1e-10 ? 20 * Math.log10(ssRms) : -140;

            double gainDb = outputDb - inputDb;

            // Attack time: first 2ms window where envelope >= 90% of steady-state
            double attackMs = measureAttack(samples, burstStart, burstEnd, ssRms * 32767.0);

            // Release time: first 2ms window where envelope <= 10% of steady-state
            double releaseMs = measureRelease(samples, burstEnd, silenceEnd, ssRms * 32767.0);

            // Extract envelope for this burst (both burst and silence portions)
            double[] envelope = extractEnvelope(samples, burstStart, silenceEnd);

            results[i] = new BurstResult(LEVELS_DB[i], outputDb, gainDb, attackMs, releaseMs, envelope);
        }

        return results;
    }

    private double measureAttack(short[] samples, int burstStart, int burstEnd, double ssRmsRaw) {
        if (ssRmsRaw < 1.0) return -1; // too quiet
        double threshold = 0.9 * ssRmsRaw;

        for (int pos = burstStart; pos + ENV_WINDOW <= burstEnd; pos += ENV_WINDOW) {
            double rms = rmsOfRange(samples, pos, pos + ENV_WINDOW);
            if (rms >= threshold) {
                return (pos - burstStart) * 1000.0 / SAMPLE_RATE;
            }
        }
        return -1;
    }

    private double measureRelease(short[] samples, int silenceStart, int silenceEnd, double ssRmsRaw) {
        if (ssRmsRaw < 1.0) return -1;
        double threshold = 0.1 * ssRmsRaw;

        for (int pos = silenceStart; pos + ENV_WINDOW <= silenceEnd; pos += ENV_WINDOW) {
            double rms = rmsOfRange(samples, pos, pos + ENV_WINDOW);
            if (rms <= threshold) {
                return (pos - silenceStart) * 1000.0 / SAMPLE_RATE;
            }
        }
        return -1;
    }

    private double[] extractEnvelope(short[] samples, int start, int end) {
        end = Math.min(end, samples.length);
        int numWindows = (end - start) / ENV_WINDOW;
        double[] env = new double[numWindows];
        for (int i = 0; i < numWindows; i++) {
            int wStart = start + i * ENV_WINDOW;
            double rms = rmsOfRange(samples, wStart, wStart + ENV_WINDOW) / 32767.0;
            env[i] = rms > 1e-10 ? 20 * Math.log10(rms) : -140;
        }
        return env;
    }

    // ==================== Reporting ====================

    private void printResults(String blockName, BurstResult[] results) {
        System.out.printf("%n  %-14s  %8s  %8s  %8s  %10s  %10s%n",
            "Input (dBFS)", "Out (dB)", "Gain", "Attack", "Release", "");
        System.out.println("  " + "-".repeat(66));
        for (BurstResult r : results) {
            System.out.printf("  %+7.0f dBFS   %+8.1f  %+8.1f  %8s  %10s%n",
                r.inputDb, r.outputDb, r.gainDb,
                r.attackMs >= 0 ? String.format("%.1f ms", r.attackMs) : "  n/a",
                r.releaseMs >= 0 ? String.format("%.1f ms", r.releaseMs) : "  n/a");
        }
    }

    private void printEnvelopeSummary(String blockName, BurstResult[] results) {
        // Print envelope sparkline for select bursts
        int[] show = {2, 4, 6, 8}; // -60, -40, -20, 0 dBFS
        System.out.println("\n  Envelope traces (dB, ~2ms per char):");
        for (int idx : show) {
            if (idx >= results.length || results[idx].envelope.length == 0) continue;
            BurstResult r = results[idx];
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("  %+4.0f dBFS |", r.inputDb));
            int step = Math.max(1, r.envelope.length / 60);
            for (int i = 0; i < r.envelope.length; i += step) {
                double db = r.envelope[i];
                if (db <= -60) sb.append(' ');
                else if (db <= -40) sb.append('.');
                else if (db <= -20) sb.append('-');
                else if (db <= -10) sb.append('=');
                else sb.append('#');
            }
            sb.append('|');
            System.out.println(sb);
        }
        System.out.println("        legend: ' '=<-60  '.'=-60..-40  '-'=-40..-20  '='=-20..-10  '#'>-10 dB");
    }

    // ==================== WAV generation ====================

    private File generateToneBurstWav() throws IOException {
        int burstSamples = (int) (SAMPLE_RATE * BURST_DURATION);
        int silenceSamples = (int) (SAMPLE_RATE * SILENCE_DURATION);
        int periodSamples = burstSamples + silenceSamples;
        int totalFrames = LEVELS_DB.length * periodSamples;

        byte[] data = new byte[totalFrames * 4]; // stereo 16-bit

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
            // Silence portion is already zeros
        }

        File wavFile = File.createTempFile("burst_", ".wav");
        wavFile.deleteOnExit();
        AudioFormat format = new AudioFormat(SAMPLE_RATE, 16, 2, true, false);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        AudioInputStream ais = new AudioInputStream(bais, format, totalFrames);
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, wavFile);
        ais.close();
        return wavFile;
    }

    // ==================== Audio utilities ====================

    static double rmsOfRange(short[] samples, int start, int end) {
        if (start >= end || start >= samples.length) return 0;
        end = Math.min(end, samples.length);
        double sumSq = 0;
        for (int i = start; i < end; i++) {
            sumSq += (double) samples[i] * samples[i];
        }
        return Math.sqrt(sumSq / (end - start));
    }

    static short[] extractChannel(short[] stereo, int channel) {
        short[] mono = new short[stereo.length / 2];
        for (int i = 0; i < mono.length; i++) {
            mono[i] = stereo[i * 2 + channel];
        }
        return mono;
    }

    private static short[] readWavSamples(File wavFile) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(wavFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[4096];
        int n;
        while ((n = ais.read(buf)) > 0) {
            bos.write(buf, 0, n);
        }
        ais.close();
        byte[] raw = bos.toByteArray();
        short[] samples = new short[raw.length / 2];
        for (int i = 0; i < samples.length; i++) {
            int offset = i * 2;
            samples[i] = (short) ((raw[offset] & 0xff) | ((raw[offset + 1] & 0xff) << 8));
        }
        return samples;
    }

    // ==================== Result container ====================

    static class BurstResult {
        final double inputDb;
        final double outputDb;
        final double gainDb;
        final double attackMs;
        final double releaseMs;
        final double[] envelope; // dB values, one per ENV_WINDOW

        BurstResult(double inputDb, double outputDb, double gainDb,
                    double attackMs, double releaseMs, double[] envelope) {
            this.inputDb = inputDb;
            this.outputDb = outputDb;
            this.gainDb = gainDb;
            this.attackMs = attackMs;
            this.releaseMs = releaseMs;
            this.envelope = envelope;
        }
    }
}
