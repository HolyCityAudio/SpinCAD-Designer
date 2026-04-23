# Reverb Blocks

These blocks implement various reverb algorithms for the FV-1, ranging from simple allpass chains to full plate and hall reverbs. Each plot shows the impulse response envelope at three reverb-time settings (short, medium, long) with control pins disconnected.

### Block Index

|                                                               |                                                                 |                                                     |
| ------------------------------------------------------------- | --------------------------------------------------------------- | --------------------------------------------------- |
| [Adjustable Reverb](reverb-blocks.md#adjustable-reverb)       | [Allpass](reverb-blocks.md#allpass)                             | [Ambience](reverb-blocks.md#ambience)               |
| [Chirp](reverb-blocks.md#chirp)                               | [Dattorro Plate Reverb](reverb-blocks.md#dattorro-plate-reverb) | [Freeverb](reverb-blocks.md#freeverb)               |
| [Hall Reverb](reverb-blocks.md#hall-reverb)                   | [Min Reverb](reverb-blocks.md#min-reverb)                       | [Reverb Designer](reverb-blocks.md#reverb-designer) |
| [ROM Reverb 1](reverb-blocks.md#rom-reverb-1)                 | [ROM Reverb 2](reverb-blocks.md#rom-reverb-2)                   | [Room Reverb](reverb-blocks.md#room-reverb)         |
| [Small Reverb (Stereo)](reverb-blocks.md#small-reverb-stereo) | [Spring Reverb](reverb-blocks.md#spring-reverb)                 |                                                     |

***

## Adjustable Reverb

A configurable reverb with input allpass diffusers and delay loops with low-pass and high-pass filtering in the feedback path. LFO modulation can be applied to the delay taps for chorus-like movement.

| Pin           | Type       | Description         |
| ------------- | ---------- | ------------------- |
| Input         | Audio In   | Mono audio input    |
| Output\_Left  | Audio Out  | Left reverb output  |
| Output\_Right | Audio Out  | Right reverb output |
| Reverb\_Time  | Control In | Reverb decay time   |
| Filter        | Control In | Filter frequency    |

**Control panel parameters:**

| Parameter       | Range | Default | Description                               |
| --------------- | ----- | ------- | ----------------------------------------- |
| Gain            | 0-1   | 0.5     | Input gain                                |
| Input AP Coeff  | 0-1   | 0.5     | Input allpass coefficient                 |
| Num Delay Loops | 1-4   | 3       | Number of feedback delay loops            |
| Loop AP Coeff   | 0-1   | 0.6     | Loop allpass coefficient (controls decay) |
| LF Filter       | 0-1   | 0.4     | Low-frequency filter coefficient          |
| HF Filter       | 0-1   | 0.01    | High-frequency filter coefficient         |

**Built-in control processing:** The Reverb Time control input is internally remapped via Scale/Offset to the range 0.30–0.85, preventing the feedback from reaching unity (which would cause runaway oscillation) or dropping to zero.

![Adjustable Reverb impulse response](.gitbook/assets/reverb-reverb.png)

***

## Allpass

A chain of up to four allpass filters with configurable delay lengths. Produces a diffused, smeared version of the input without explicit feedback loops. Useful as a building block or for subtle ambience.

| Pin    | Type      | Description     |
| ------ | --------- | --------------- |
| Input  | Audio In  | Audio input     |
| Output | Audio Out | Diffused output |

**Control panel parameters:**

| Parameter  | Range   | Default | Description                 |
| ---------- | ------- | ------- | --------------------------- |
| Gain       | 0-1     | 0.5     | Input gain                  |
| AP1 Length | samples | 125     | First allpass delay length  |
| AP2 Length | samples | 250     | Second allpass delay length |
| AP3 Length | samples | 750     | Third allpass delay length  |
| AP4 Length | samples | 1500    | Fourth allpass delay length |
| AP Coeff   | 0-1     | 0.5     | Allpass coefficient         |

![Allpass impulse response (Short)](.gitbook/assets/reverb-allpass-short.png) ![Allpass impulse response (Medium)](.gitbook/assets/reverb-allpass-medium.png) ![Allpass impulse response (Long)](.gitbook/assets/reverb-allpass-long.png)

***

## Ambience

Based on the Spin Semiconductor P16\_V\_Ambience program. Uses multi-tapped delay lines with allpass diffusion and exponential decay to create early reflections. Produces a short, natural-sounding room ambience rather than a long reverb tail.

| Pin            | Type       | Description                       |
| -------------- | ---------- | --------------------------------- |
| Audio Input    | Audio In   | Mono audio input                  |
| Audio Output L | Audio Out  | Left ambience output              |
| Audio Output R | Audio Out  | Right ambience output             |
| Tone           | Control In | Brightness (0 = dark, 1 = bright) |
| Decay          | Control In | Decay time (0 = short, 1 = long)  |

**Control panel parameters:**

| Parameter   | Range        | Default | Description                      |
| ----------- | ------------ | ------- | -------------------------------- |
| Tone        | 0-1          | 0.5     | Tone control                     |
| Decay       | 0-1          | 0.5     | Decay amount                     |
| Filter Freq | 2000-8000 Hz | 4000    | Low-pass filter corner frequency |

**Built-in control processing:** The Decay control input is internally scaled to the range 0.20–0.85 (read at 65% gain, then offset by 0.2). This prevents the feedback from reaching unity or dropping too low.

![Ambience impulse response](.gitbook/assets/reverb-ambience.png)

***

## Chirp

A cascade of up to 30 identical allpass filters, each with a delay of _stretch_ samples. The allpass coefficient (which can be negative) controls how much different frequencies are delayed relative to each other. The result is a dispersive impulse response where energy at the resonance frequency arrives much later than at other frequencies — the characteristic "chirp" sound of spring reverbs and metallic resonators.

Long chains of allpass filters are commonly found in spring reverb emulations, where they model the frequency-dependent propagation velocity of the spring coil. The FV-1's 128-instruction limit constrains the chain to about 30 stages (60 instructions for RDA/WRAP pairs), which is not enough for a convincing spring emulation but is sufficient to demonstrate the dispersive effect.

| Pin    | Type      | Description    |
| ------ | --------- | -------------- |
| Input  | Audio In  | Audio input    |
| Output | Audio Out | Chirped output |

**Control panel parameters:**

| Parameter  | Range         | Default | Description                      |
| ---------- | ------------- | ------- | -------------------------------- |
| Input Gain | -18 to 0 dB   | -6 dB   | Input gain                       |
| Stages     | 2-30          | 4       | Number of allpass stages         |
| Stretch    | 1-50          | 20      | Delay length per stage (samples) |
| All Pass   | -0.98 to 0.98 | 0.5     | Allpass coefficient              |

### Effect of AP coefficient

Each AP coefficient setting produces a distinct impulse response and frequency dispersion pattern. At AP=0 the impulse passes through unchanged. As |AP| increases, different frequency bands spread over time — this is the characteristic "chirp" sound.

![Chirp AP=-0.75](.gitbook/assets/reverb-chirp-neg0.75.png) ![Chirp spectrogram AP=-0.75](.gitbook/assets/reverb-chirp-spec-neg0.75.png)

![Chirp AP=-0.50](.gitbook/assets/reverb-chirp-neg0.50.png) ![Chirp spectrogram AP=-0.50](.gitbook/assets/reverb-chirp-spec-neg0.50.png)

![Chirp AP=0.00](.gitbook/assets/reverb-chirp-0.00.png) ![Chirp spectrogram AP=0.00](.gitbook/assets/reverb-chirp-spec-0.00.png)

![Chirp AP=0.50](.gitbook/assets/reverb-chirp-0.50.png) ![Chirp spectrogram AP=0.50](.gitbook/assets/reverb-chirp-spec-0.50.png)

![Chirp AP=0.75](.gitbook/assets/reverb-chirp-0.75.png) ![Chirp spectrogram AP=0.75](.gitbook/assets/reverb-chirp-spec-0.75.png)

### Group delay vs. stretch and AP coefficient

With 30 stages (the block maximum), the group delay at the resonance frequency scales linearly with the stretch parameter. The sign of the AP coefficient determines the resonance frequency: positive AP peaks at f<sub>s</sub>/D, negative AP peaks at f<sub>s</sub>/(2D), where D = stretch and f<sub>s</sub> = 32768 Hz.

The theoretical group delay at the resonance frequency is:

> τ = N · D · (1 + |k|) / (1 − |k|) / f<sub>s</sub>

where N = number of stages, D = stretch, k = AP coefficient.

| Stretch | AP    | Group Delay | Resonance Freq |
| ------- | ----- | ----------- | -------------- |
| 5       | +0.65 | 21.4 ms     | 6504 Hz        |
| 5       | -0.65 | 21.6 ms     | 3276 Hz        |
| 10      | +0.65 | 42.8 ms     | 3252 Hz        |
| 10      | -0.65 | 43.3 ms     | 1640 Hz        |
| 20      | +0.65 | 86.6 ms     | 1640 Hz        |
| 20      | -0.65 | 86.5 ms     | 820 Hz         |

**Impulse spectrograms** (0.5 ms temporal resolution, 30 stages) show the dispersive chirp pattern. The annotation in each spectrogram shows the measured peak group delay and resonance frequency.

**Stretch = 5:**

![Impulse spectrogram stretch=5 AP=+0.65](.gitbook/assets/chirp-delay-impulse-s5_appos065.png) ![Impulse spectrogram stretch=5 AP=-0.65](.gitbook/assets/chirp-delay-impulse-s5_apneg065.png)

**Stretch = 10:**

![Impulse spectrogram stretch=10 AP=+0.65](.gitbook/assets/chirp-delay-impulse-s10_appos065.png) ![Impulse spectrogram stretch=10 AP=-0.65](.gitbook/assets/chirp-delay-impulse-s10_apneg065.png)

**Stretch = 20:**

![Impulse spectrogram stretch=20 AP=+0.65](.gitbook/assets/chirp-delay-impulse-s20_appos065.png) ![Impulse spectrogram stretch=20 AP=-0.65](.gitbook/assets/chirp-delay-impulse-s20_apneg065.png)

### Tone burst response

A 100 ms sine burst (with 5-cycle linear fade-in and fade-out) at the resonance frequency confirms the group delay measurement. The output onset is shifted by the measured group delay relative to the input.

**Stretch = 5:**

![Tone burst stretch=5 AP=+0.65](.gitbook/assets/chirp-delay-burst-s5_appos065.png) ![Tone burst stretch=5 AP=-0.65](.gitbook/assets/chirp-delay-burst-s5_apneg065.png)

**Stretch = 10:**

![Tone burst stretch=10 AP=+0.65](.gitbook/assets/chirp-delay-burst-s10_appos065.png) ![Tone burst stretch=10 AP=-0.65](.gitbook/assets/chirp-delay-burst-s10_apneg065.png)

**Stretch = 20:**

![Tone burst stretch=20 AP=+0.65](.gitbook/assets/chirp-delay-burst-s20_appos065.png) ![Tone burst stretch=20 AP=-0.65](.gitbook/assets/chirp-delay-burst-s20_apneg065.png)

### Waveform closeups

Ten-cycle closeups of sine and square waves at the lowest resonance frequency (stretch = 20) show how the allpass cascade disperses harmonics. The sine wave passes through with only a gain change, while the square wave's harmonics are spread in time, producing a visible chirp-like smearing of the sharp edges.

**AP = +0.65 (resonance at 1640 Hz):**

![Sine closeup AP=+0.65](.gitbook/assets/chirp-delay-sine-s20_appos065.png) ![Square closeup AP=+0.65](.gitbook/assets/chirp-delay-square-s20_appos065.png)

**AP = −0.65 (resonance at 820 Hz):**

![Sine closeup AP=-0.65](.gitbook/assets/chirp-delay-sine-s20_apneg065.png) ![Square closeup AP=-0.65](.gitbook/assets/chirp-delay-square-s20_apneg065.png)

***

## Dattorro Plate Reverb

An implementation of the Jon Dattorro plate reverb from "Effect Design" (JAES, 1997). Features four input diffusers feeding a cross-coupled modulated tank with multi-tap stereo output. Produces a dense, smooth plate-style reverb.

| Pin            | Type       | Description            |
| -------------- | ---------- | ---------------------- |
| Audio Input L  | Audio In   | Left audio input       |
| Audio Input R  | Audio In   | Right audio input      |
| Audio Output L | Audio Out  | Left reverb output     |
| Audio Output R | Audio Out  | Right reverb output    |
| Reverb Time    | Control In | Decay time             |
| HF Loss        | Control In | High-frequency damping |

**Control panel parameters:**

| Parameter | Range       | Default | Description            |
| --------- | ----------- | ------- | ---------------------- |
| Gain      | -24 to 0 dB | -6 dB   | Input gain             |
| Decay     | 0.1-0.95    | 0.5     | Tank decay coefficient |
| Damping   | 0-0.95      | 0.5     | HF damping in tank     |
| Bandwidth | 0.1-0.7     | 0.32    | Input bandwidth filter |

**Built-in control processing:** The Reverb Time control internally derives a secondary "decay diffusion 2" coefficient that is clamped to the range 0.25–0.50 via conditional logic. This prevents the cross-coupled tank from becoming unstable at extreme decay settings.

![Dattorro Plate Reverb (Short)](.gitbook/assets/reverb-dattorro-short.png) ![Dattorro Plate Reverb (Medium)](.gitbook/assets/reverb-dattorro-medium.png) ![Dattorro Plate Reverb (Long)](.gitbook/assets/reverb-dattorro-long.png)

***

## Freeverb

An FV-1 implementation of the Freeverb algorithm (Jezar). Uses eight parallel comb filters summed into four cascaded allpass filters, with separate left and right output paths for stereo spread.

| Pin          | Type       | Description         |
| ------------ | ---------- | ------------------- |
| Input\_L     | Audio In   | Left audio input    |
| Input\_R     | Audio In   | Right audio input   |
| OutputL      | Audio Out  | Left reverb output  |
| OutputR      | Audio Out  | Right reverb output |
| Reverb\_Time | Control In | Reverb decay time   |

**Control panel parameters:**

| Parameter   | Range | Default | Description                        |
| ----------- | ----- | ------- | ---------------------------------- |
| Gain        | 0-1   | 0.5     | Input gain                         |
| Reverb Time | 0-1   | 0.42    | Comb filter feedback (decay time)  |
| Damping     | 0-1   | 0.5     | High-frequency damping in feedback |

![Freeverb impulse response](.gitbook/assets/reverb-freeverb.png)

***

## Hall Reverb

A hall-style reverb with pre-delay, allpass diffusion, and cross-coupled delay loops with low-pass filtering for HF decay. The pre-delay separates the direct sound from the reverb onset, simulating a large acoustic space.

| Pin          | Type       | Description         |
| ------------ | ---------- | ------------------- |
| Input        | Audio In   | Mono audio input    |
| OutputL      | Audio Out  | Left reverb output  |
| OutputR      | Audio Out  | Right reverb output |
| Pre\_Delay   | Control In | Pre-delay time      |
| Reverb\_Time | Control In | Reverb decay time   |
| HF\_Loss     | Control In | High-frequency loss |

**Control panel parameters:**

| Parameter   | Range | Default | Description                           |
| ----------- | ----- | ------- | ------------------------------------- |
| Gain        | 0-1   | 0.5     | Input gain                            |
| Reverb Time | 0-1   | 0.5     | Feedback coefficient (decay time)     |
| HF Damping  | 0-1   | 0.02    | High-frequency damping                |
| Input AP    | 0-1   | 0.5     | Input allpass / pre-delay coefficient |
| Delay AP    | 0-1   | 0.5     | Delay loop allpass coefficient        |

![Hall Reverb impulse response](.gitbook/assets/reverb-hall.png)

**Pre-delay effect:**

![Hall Reverb pre-delay comparison](.gitbook/assets/reverb-hall-predelay.png)

**Built-in control processing:** The Pre-Delay control is read at 10% gain internally, limiting the effective pre-delay range. Reverb Time and HF Loss are applied directly as multipliers in the feedback loop.

***

## Min Reverb

A minimal reverb based on the Spin Semiconductor "minimum reverb" example. Uses four input allpass diffusers feeding two cross-coupled delay loops. Small code footprint but limited control -- reverb time is set via the control input pin only.

| Pin            | Type       | Description               |
| -------------- | ---------- | ------------------------- |
| Audio Input 1  | Audio In   | Audio input (auto-named)  |
| Audio Output 1 | Audio Out  | Audio output (auto-named) |
| Reverb Time    | Control In | Reverb decay time         |

No control panel parameters (reverb time is controlled exclusively by the control input pin).

![Min Reverb impulse response](.gitbook/assets/reverb-minreverb.png)

***

## Reverb Designer

A highly configurable reverb with selectable topology (two-loop, Dattorro, or ring FDN), size presets, optional shimmer, LFO modulation, and pre-delay. Control inputs can be assigned to any parameter. This is the most flexible reverb block available.

| Pin        | Type       | Description           |
| ---------- | ---------- | --------------------- |
| Audio In   | Audio In   | Left/mono audio input |
| Audio In 2 | Audio In   | Right audio input     |
| Out L      | Audio Out  | Left reverb output    |
| Out R      | Audio Out  | Right reverb output   |
| Ctrl 1     | Control In | Assignable control 1  |
| Ctrl 2     | Control In | Assignable control 2  |
| Ctrl 3     | Control In | Assignable control 3  |
| Ctrl 4     | Control In | Assignable control 4  |

**Control panel parameters:**

| Parameter   | Range                          | Default  | Description              |
| ----------- | ------------------------------ | -------- | ------------------------ |
| Topology    | Two-Loop / Dattorro / Ring FDN | Dattorro | Reverb algorithm         |
| Size        | Small / Medium / Large         | Medium   | Delay memory size preset |
| Reverb Time | 0-1                            | 0.5      | Decay time               |
| HF Damping  | 0-1                            | 0.3      | High-frequency damping   |
| LF Damping  | 0-1                            | 0.1      | Low-frequency damping    |
| Dry/Wet     | 0-1                            | 0.5      | Mix ratio                |
| Shimmer     | Off / Input / Input+Feedback   | Off      | Pitch-shifted feedback   |
| LFO Depth   | None / Subtle / Wide           | Subtle   | Modulation depth         |
| Pre-Delay   | on/off                         | off      | Enable pre-delay         |

![Reverb Designer impulse response](.gitbook/assets/reverb-reverbdesigner.png)

***

## ROM Reverb 1

A stereo reverb based on the FV-1 ROM programs. Uses input allpass diffusers, multiple delay loops with allpass feedback, and separate low-frequency and high-frequency response controls.

| Pin           | Type       | Description             |
| ------------- | ---------- | ----------------------- |
| Input\_Left   | Audio In   | Left audio input        |
| Input\_Right  | Audio In   | Right audio input       |
| Output\_Left  | Audio Out  | Left reverb output      |
| Output\_Right | Audio Out  | Right reverb output     |
| Reverb\_Time  | Control In | Reverb decay time       |
| Low\_Freq     | Control In | Low-frequency response  |
| High\_Freq    | Control In | High-frequency response |

**Control panel parameters:**

| Parameter  | Range | Default | Description               |
| ---------- | ----- | ------- | ------------------------- |
| Gain       | 0-1   | 0.5     | Input gain                |
| Input AP   | 0-1   | 0.5     | Input allpass coefficient |
| Num Delays | 1-4   | 3       | Number of delay loops     |
| Delay AP   | 0-1   | 0.6     | Delay allpass coefficient |
| LF Filter  | 0-1   | 0.4     | Low-frequency filter      |
| HF Filter  | 0-1   | 0.01    | High-frequency filter     |

**Built-in control processing:** The Reverb Time control is internally remapped via Scale/Offset before being used as a feedback multiplier. Low Freq and High Freq controls are applied as direct multipliers on their respective filter paths.

***

## ROM Reverb 2

A mono-output reverb with configurable delay lengths and memory scaling. Uses a denser topology with four allpass-delay pairs and separate low/high frequency response controls.

| Pin         | Type       | Description             |
| ----------- | ---------- | ----------------------- |
| Input       | Audio In   | Mono audio input        |
| Output      | Audio Out  | Mono reverb output      |
| Reverb Time | Control In | Maximum reverb time     |
| LF Response | Control In | Low-frequency response  |
| HF Response | Control In | High-frequency response |

**Control panel parameters:**

| Parameter    | Range | Default | Description                      |
| ------------ | ----- | ------- | -------------------------------- |
| Gain         | 0-1   | 0.5     | Input gain                       |
| Rev Time Max | 0-1   | 0.6     | Maximum reverb time coefficient  |
| Input AP     | 0-1   | 0.6     | Input allpass coefficient        |
| Delay AP 1   | 0-1   | 0.6     | First delay allpass coefficient  |
| Delay AP 2   | 0-1   | 0.5     | Second delay allpass coefficient |
| LF Filter    | 0-1   | 0.4     | Low-frequency filter             |
| HF Filter    | 0-1   | 0.01    | High-frequency filter            |

**Built-in control processing:** The Reverb Time control is internally remapped via Scale/Offset (0.9, 0.1) to the range 0.10–1.0. LF Response and HF Response controls are applied as direct multipliers on their respective filter outputs.

![ROM Reverb 2 impulse response](.gitbook/assets/reverb-rom_rev2.png)

***

## Room Reverb

A room-style reverb with pre-delay. Similar architecture to the Hall Reverb but with shorter delay lengths and different allpass tuning to simulate a smaller acoustic space.

| Pin          | Type       | Description         |
| ------------ | ---------- | ------------------- |
| Input        | Audio In   | Mono audio input    |
| OutputL      | Audio Out  | Left reverb output  |
| OutputR      | Audio Out  | Right reverb output |
| Pre\_Delay   | Control In | Pre-delay time      |
| Reverb\_Time | Control In | Reverb decay time   |
| HF\_Loss     | Control In | High-frequency loss |

**Control panel parameters:**

| Parameter   | Range | Default | Description                           |
| ----------- | ----- | ------- | ------------------------------------- |
| Gain        | 0-1   | 0.5     | Input gain                            |
| Reverb Time | 0-1   | 0.5     | Feedback coefficient (decay time)     |
| HF Damping  | 0-1   | 0.02    | High-frequency damping                |
| Input AP    | 0-1   | 0.5     | Input allpass / pre-delay coefficient |
| Delay AP    | 0-1   | 0.5     | Delay loop allpass coefficient        |

![Room Reverb impulse response](.gitbook/assets/reverb-room.png)

**Pre-delay effect:**

![Room Reverb pre-delay comparison](.gitbook/assets/reverb-room-predelay.png)

**Built-in control processing:** The Reverb Time and HF Loss controls are applied as direct multipliers in the feedback loop. No internal range limiting is applied beyond the feedback topology itself.

***

## Small Reverb (Stereo)

A stereo version of the minimum reverb with configurable allpass and delay lengths. Provides more control than Min Reverb while keeping a moderate instruction count.

| Pin           | Type       | Description         |
| ------------- | ---------- | ------------------- |
| Input\_Left   | Audio In   | Left audio input    |
| Input\_Right  | Audio In   | Right audio input   |
| Output\_Left  | Audio Out  | Left reverb output  |
| Output\_Right | Audio Out  | Right reverb output |
| Reverb\_Time  | Control In | Reverb decay time   |

**Control panel parameters:**

| Parameter        | Range   | Default | Description                               |
| ---------------- | ------- | ------- | ----------------------------------------- |
| Gain             | 0-1     | 0.5     | Input gain                                |
| Input AP         | 0-1     | 0.5     | Input allpass coefficient                 |
| Loop AP          | 0-1     | 0.6     | Loop allpass coefficient (controls decay) |
| AP/Delay lengths | samples | various | Individual allpass and delay lengths      |

![Small Reverb impulse response](.gitbook/assets/reverb-minreverb2.png)

***

## Spring Reverb

Simulates a mechanical spring reverb tank. Uses short allpass chains with negative coefficients to create the characteristic spring "boing" and metallic coloration.

| Pin          | Type       | Description            |
| ------------ | ---------- | ---------------------- |
| Input\_L     | Audio In   | Left audio input       |
| Input\_R     | Audio In   | Right audio input      |
| OutputL      | Audio Out  | Left reverb output     |
| OutputR      | Audio Out  | Right reverb output    |
| Reverb\_Time | Control In | Reverb decay time      |
| Damping      | Control In | High-frequency damping |

**Control panel parameters:**

| Parameter   | Range | Default | Description                       |
| ----------- | ----- | ------- | --------------------------------- |
| Gain        | 0-1   | 0.5     | Input gain                        |
| Reverb Time | 0-1   | 0.85    | Feedback coefficient (decay time) |
| Damping     | 0-1   | 0.55    | Spring resonance / damping        |

**Built-in control processing:** The Reverb Time and Damping controls are applied as direct multipliers in the feedback loop — no additional taper or range limiting.

![Spring Reverb impulse response](.gitbook/assets/reverb-spring.png)
