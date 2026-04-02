# Wave Shaper Blocks Reference

These blocks apply nonlinear waveshaping to audio signals: soft clipping,
hard clipping, fuzz, sample-rate reduction, and bit crushing. Each plot
shows a 440 Hz sine wave at three input levels (0 dB, -6 dB, -18 dB).

---

## Cube

Applies a cubic waveshaping function to the input signal. The cubic
transfer curve produces soft clipping that adds odd harmonics (3rd, 5th, etc.)
while preserving the signal's zero crossings. There is no control panel.

| Pin | Type | Description |
|-----|------|-------------|
| Audio Input 1 | Audio In | Audio signal |
| Audio Output 1 | Audio Out | Cubed output |

Implements: `output = input^3`

At low levels the effect is subtle; at higher levels the signal is
compressed toward the peaks, producing warm saturation.

![Cube](images/waveshaper-cubegain.png)

---

## Distortion

Hard-clips the audio signal using the FV-1's saturation behavior.
Cascaded SOF instructions with a coefficient of -2.0 drive the signal
into clipping, producing aggressive square-wave-like distortion rich
in odd harmonics. There is no control panel.

| Pin | Type | Description |
|-----|------|-------------|
| Audio Input 1 | Audio In | Audio signal |
| Audio Output 1 | Audio Out | Distorted output |

![Distortion](images/waveshaper-distortion.png)

---

## Overdrive

A multi-stage overdrive with adjustable gain and drive depth. Uses
cascaded SOF-based clipping stages with post-filtering to tame
high-frequency harshness.

| Pin | Type | Description |
|-----|------|-------------|
| Audio Input 1 | Audio In | Audio signal |
| Drive | Control In | Drive amount (overrides gain knob) |
| Audio Output 1 | Audio Out | Overdriven output |

**Control panel parameters:**

| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Stages | 1-3 | 2 | Number of clipping stages |
| Gain | 0-1 | 0.25 | Input drive level |
| Output Gain | 0-1 | 0.3 | Output level |

When the Drive control pin is connected, the input is multiplied by
the control value instead of the fixed gain setting.

![Overdrive](images/waveshaper-overdrive.png)

---

## Octave Fuzz

Full-wave rectifies the input signal to produce an octave-up effect
combined with fuzz distortion. The rectification doubles the fundamental
frequency, creating an aggressive octave-up tone.

| Pin | Type | Description |
|-----|------|-------------|
| Input | Audio In | Audio signal |
| Audio_Output | Audio Out | Fuzzed octave-up output |

There is no control panel.

![Octave Fuzz](images/waveshaper-octavefuzz.png)

---

## T/X

Divides a fixed value by the input signal magnitude, producing an
inverse/reciprocal waveshaper. At low input levels the output is large
(clipped), and at high input levels the output approaches zero. This
creates an unusual compression/expansion characteristic.

| Pin | Type | Description |
|-----|------|-------------|
| Input | Audio In | Audio signal |
| Audio_Output | Audio Out | T/X shaped output |

There is no control panel.

![T/X](images/waveshaper-toverx.png)

---

## Aliaser

Reduces the effective sample rate of the audio signal by sample-and-hold
decimation, producing aliasing artifacts that add metallic, lo-fi character.
Two outputs are provided: a smoothed version and the raw decimated signal.

| Pin | Type | Description |
|-----|------|-------------|
| Input | Audio In | Audio signal |
| Rip | Control In | Decimation amount (0 = subtle, 1 = extreme) |
| Smooth | Audio Out | Filtered decimated output |
| Raw | Audio Out | Raw decimated output |

There is no control panel; the Rip control input sets the effect depth.

![Aliaser](images/waveshaper-aliaser.png)

---

## Quantizer

Reduces the bit depth of the audio signal, producing stepped quantization
noise characteristic of lo-fi digital audio. When the control input is
connected, the bit depth varies dynamically with the control signal.

| Pin | Type | Description |
|-----|------|-------------|
| Audio Input 1 | Audio In | Audio signal |
| Control Input 1 | Control In | Dynamic bit depth control |
| Audio Output 1 | Audio Out | Quantized output |

**Control panel parameters:**

| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Bits | 1-20 | 3 | Number of bits to keep |

Lower bit values produce more aggressive quantization. When the control
input is connected, the number of quantization levels varies between
the panel setting and a coarser resolution based on the control value.

![Quantizer](images/waveshaper-quantizer.png)
