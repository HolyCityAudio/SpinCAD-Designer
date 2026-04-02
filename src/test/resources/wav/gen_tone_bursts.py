"""Generate a WAV file with 440 Hz tone bursts at increasing levels.

Bursts go from -60 dB to 0 dB in 10 dB steps.
Each burst is 500 ms with 1.0 second silence between them.
"""

import struct
import math

SAMPLE_RATE = 32768
FREQ = 440.0
BURST_DURATION = 0.5    # seconds
SILENCE_DURATION = 0.1  # seconds
BITS_PER_SAMPLE = 16
NUM_CHANNELS = 2

burst_samples = int(SAMPLE_RATE * BURST_DURATION)
silence_samples = int(SAMPLE_RATE * SILENCE_DURATION)

# Levels from -60 dB to 0 dB in 10 dB steps
levels_db = list(range(-60, 1, 10))  # -60, -50, -40, -30, -20, -10, 0

# Short fade in/out to avoid clicks (5 ms)
fade_samples = int(SAMPLE_RATE * 0.005)

samples = []

# Lead-in silence
samples.extend([0] * silence_samples)

for db in levels_db:
    amplitude = 10.0 ** (db / 20.0)

    # Generate burst with fade in/out
    for i in range(burst_samples):
        val = amplitude * math.sin(2.0 * math.pi * FREQ * i / SAMPLE_RATE)

        # Fade in
        if i < fade_samples:
            val *= i / fade_samples
        # Fade out
        elif i > burst_samples - fade_samples:
            val *= (burst_samples - i) / fade_samples

        # Convert to 16-bit integer
        int_val = max(-32768, min(32767, int(val * 32767.0)))
        samples.append(int_val)

    # Silence between bursts
    samples.extend([0] * silence_samples)

# Write WAV file
output_path = "compressor_test_tone_bursts.wav"
num_samples = len(samples)
data_size = num_samples * NUM_CHANNELS * (BITS_PER_SAMPLE // 8)
byte_rate = SAMPLE_RATE * NUM_CHANNELS * (BITS_PER_SAMPLE // 8)
block_align = NUM_CHANNELS * (BITS_PER_SAMPLE // 8)

with open(output_path, "wb") as f:
    # RIFF header
    f.write(b"RIFF")
    f.write(struct.pack("<I", 36 + data_size))
    f.write(b"WAVE")
    # fmt chunk
    f.write(b"fmt ")
    f.write(struct.pack("<I", 16))              # chunk size
    f.write(struct.pack("<H", 1))               # PCM
    f.write(struct.pack("<H", NUM_CHANNELS))
    f.write(struct.pack("<I", SAMPLE_RATE))
    f.write(struct.pack("<I", byte_rate))
    f.write(struct.pack("<H", block_align))
    f.write(struct.pack("<H", BITS_PER_SAMPLE))
    # data chunk
    f.write(b"data")
    f.write(struct.pack("<I", data_size))
    for s in samples:
        f.write(struct.pack("<h", s))  # left
        f.write(struct.pack("<h", s))  # right (same signal)

total_duration = (len(levels_db) * (BURST_DURATION + SILENCE_DURATION) + SILENCE_DURATION)
print(f"Generated {output_path}")
print(f"  Sample rate: {SAMPLE_RATE} Hz")
print(f"  Duration: {total_duration:.1f} seconds")
print(f"  Levels: {', '.join(f'{db} dB' for db in levels_db)}")
print(f"  Burst: {BURST_DURATION*1000:.0f} ms, Silence: {SILENCE_DURATION*1000:.0f} ms")
