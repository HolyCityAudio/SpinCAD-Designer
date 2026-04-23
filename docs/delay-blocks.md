# Delay Blocks

These blocks implement various delay-based effects: multi-tap delays, drum echo, BBD emulation, reverse delay, and stutter/glitch effects. All delays use the FV-1's on-chip delay memory (32768 samples max at 32768 Hz sample rate, approximately 1 second).

### Block Index

|                                                              |                                                      |                                                |
| ------------------------------------------------------------ | ---------------------------------------------------- | ---------------------------------------------- |
| [Drum Delay](delay-blocks.md#drum-delay)                     | [Eight Tap Delay](delay-blocks.md#eight-tap-delay)   | [Long Delay](delay-blocks.md#long-delay)       |
| [MN3011 BBD Emulation](delay-blocks.md#mn3011-bbd-emulation) | [Reverse Delay](delay-blocks.md#reverse-delay)       | [Six Tap Delay](delay-blocks.md#six-tap-delay) |
| [Stutter](delay-blocks.md#stutter)                           | [Triple Tap Delay](delay-blocks.md#triple-tap-delay) |                                                |

***

## Drum Delay

A 4-tap drum echo emulation inspired by vintage tape echo units with multiple playback heads. The "Heads" control input selects how many taps are active (1-4). Tap ratios set each head's position along the delay buffer.

| Pin           | Type       | Description            |
| ------------- | ---------- | ---------------------- |
| Input         | Audio In   | Audio signal           |
| Feedback      | Audio In   | External feedback path |
| Tap 1 Out     | Audio Out  | Head 1 output          |
| Tap 2 Out     | Audio Out  | Head 2 output          |
| Tap 3 Out     | Audio Out  | Head 3 output          |
| Tap 4 Out     | Audio Out  | Head 4 output          |
| Delay Time    | Control In | Delay time control     |
| Heads         | Control In | Number of active heads |
| Feedback Gain | Control In | Feedback level control |

**Control panel parameters:**

| Parameter     | Range   | Default   | Description                         |
| ------------- | ------- | --------- | ----------------------------------- |
| Input Gain    | linear  | 1.0       | Input level                         |
| Feedback Gain | linear  | 0.5       | Feedback amount                     |
| Delay Length  | samples | 32767     | Delay buffer size                   |
| Tap 1-4 Ratio | 0-1     | 0.25-0.85 | Head positions as fraction of delay |

**Built-in control processing:** When the Delay Time pin is connected, the delay time is internally limited to a minimum of 5% of the buffer length per tap. This prevents the read pointer from reaching the write pointer, which would cause clicks or pass-through instead of delay.

***

## Eight Tap Delay

An 8-tap delay with independent tap ratios and gains. Taps 1-4 are mixed to output 1, taps 5-8 to output 2, with tap 8 available as a separate output for feedback.

| Pin           | Type       | Description                 |
| ------------- | ---------- | --------------------------- |
| Input         | Audio In   | Audio signal                |
| Feedback      | Audio In   | External feedback path      |
| Mix 1 Out     | Audio Out  | Mix of taps 1-4             |
| Mix 2 Out     | Audio Out  | Mix of taps 5-8             |
| Tap 8 Out     | Audio Out  | Tap 8 output (for feedback) |
| Delay Time 1  | Control In | Delay time control          |
| Feedback Gain | Control In | Feedback level control      |

**Control panel parameters:**

| Parameter     | Range   | Default | Description           |
| ------------- | ------- | ------- | --------------------- |
| Input Gain    | linear  | 1.0     | Input level           |
| Feedback Gain | linear  | 0.5     | Feedback amount       |
| Delay Length  | samples | 32767   | Delay buffer size     |
| Tap 1-8 Gain  | linear  | 0.5     | Individual tap levels |

***

## Long Delay

An extended delay that uses interleaved memory access to achieve delay times beyond the FV-1's single buffer limit. By writing and reading with a stride (interleave factor), the effective delay time is multiplied by that factor. Includes an optional low-pass filter to reduce aliasing artifacts from the interleaved read pattern.

| Pin           | Type       | Description            |
| ------------- | ---------- | ---------------------- |
| Audio Input   | Audio In   | Audio signal           |
| Feedback      | Audio In   | External feedback path |
| Feedback Gain | Control In | Feedback level control |
| Audio Output  | Audio Out  | Delayed audio output   |

**Control panel parameters:**

| Parameter      | Range  | Default | Description                                      |
| -------------- | ------ | ------- | ------------------------------------------------ |
| Interleave     | 2-16   | 8       | Memory interleave factor (multiplies delay time) |
| Feedback Level | dB     | -6 dB   | Feedback amount                                  |
| Input Gain     | dB     | 0 dB    | Input level                                      |
| Filter Enabled | on/off | on      | Low-pass filter on output                        |

Note: The interleaved memory technique trades bandwidth for delay length. Higher interleave factors produce longer delays but reduce the effective sample rate, rolling off high frequencies.  Due to aliasing you will hear some artifacts.

***

## MN3011 BBD Emulation

Emulates the tap timing of the Panasonic MN3011 bucket-brigade device, a classic analog delay IC with 6 taps at fixed ratios. The tap positions match the MN3011's actual tap spacing. All taps are summed into a single mix output, with tap 6 (end of line) available separately for feedback.

| Pin            | Type       | Description             |
| -------------- | ---------- | ----------------------- |
| Input          | Audio In   | Audio signal            |
| Feedback Input | Audio In   | External feedback path  |
| Mix Out        | Audio Out  | Sum of all taps         |
| Tap 6 Out      | Audio Out  | End-of-line tap output  |
| Delay Time     | Control In | Delay time (clock rate) |
| Feedback       | Control In | Feedback level control  |

**Control panel parameters:**

| Parameter     | Range   | Default | Description           |
| ------------- | ------- | ------- | --------------------- |
| Input Gain    | linear  | 0.5     | Input level           |
| Feedback Gain | linear  | 0.5     | Feedback amount       |
| Delay Length  | samples | 32767   | Delay buffer size     |
| Tap 1-6 Gain  | linear  | 0.5     | Individual tap levels |

The fixed tap ratios mirror the MN3011 datasheet: tap 1 = 11.9%, tap 2 = 19.9%, tap 3 = 35.9%, tap 4 = 51.9%, tap 5 = 83.8%, tap 6 = 100%.

**Built-in control processing:** The Delay Time and Feedback controls are applied directly via multiplication — no internal range limiting or taper shaping is applied.

***

## Reverse Delay

Plays back the delay buffer in reverse, creating a backwards echo effect. A ramp LFO sweeps a read pointer backward through the delay buffer, producing reversed audio at 1× speed. Two interleaved ramps, offset by half a cycle, are crossfaded to provide continuous output without clicks at the ramp reset points.

The Memory selector chooses between two modes: **Half** allocates 16384 samples of delay, freeing the other half for other blocks. **Full** allocates all 32768 samples and scales the ramp output by \~2× to sweep the entire buffer, doubling the reversed chunk length.  At the "Half" setting, it's pretty hard to discern the reverse behavior as the slices are pretty short.  This block is more subtle than you might expect from a commercial Reverse Delay.

| Pin           | Type       | Description            |
| ------------- | ---------- | ---------------------- |
| Input         | Audio In   | Audio signal           |
| Feedback      | Audio In   | External feedback path |
| Output        | Audio Out  | Reversed audio output  |
| Feedback Gain | Control In | Feedback level control |

**Control panel parameters:**

| Parameter     | Range       | Default | Description                              |
| ------------- | ----------- | ------- | ---------------------------------------- |
| Input Gain    | -12 to 0 dB | 0 dB    | Input level                              |
| Feedback Gain | -24 to 0 dB | -6 dB   | Feedback amount                          |
| Memory        | Half / Full | Half    | Delay memory allocation and chunk length |

***

## Six Tap Delay

A 6-tap stereo delay with independent tap ratios and gains. Taps are mixed into two stereo outputs (L/R), with the full delay endpoint available as a separate output for feedback routing.

| Pin             | Type       | Description              |
| --------------- | ---------- | ------------------------ |
| Input           | Audio In   | Audio signal             |
| Feedback In     | Audio In   | External feedback path   |
| Mix L Out       | Audio Out  | Left stereo mix of taps  |
| Mix R Out       | Audio Out  | Right stereo mix of taps |
| Delay\_Out\_End | Audio Out  | End-of-delay output      |
| Delay\_Time\_1  | Control In | Delay time control       |
| Feedback Gain   | Control In | Feedback level control   |

**Control panel parameters:**

| Parameter     | Range   | Default | Description                       |
| ------------- | ------- | ------- | --------------------------------- |
| Input Gain    | linear  | 0.45    | Input level                       |
| Feedback Gain | linear  | 0.5     | Feedback amount                   |
| Delay Length  | samples | 32767   | Delay buffer size                 |
| Tap 1-5 Ratio | 0-1     | 0.1-0.5 | Tap position as fraction of delay |
| Tap 1-6 Gain  | linear  | 0.5-0.8 | Individual tap levels             |

***

## Stutter

A glitch/stutter effect that lets you crossfade between the input and output of a delay delay with a 0-1 control signal.&#x20;

| Pin          | Type        | Description          |
| ------------ | ----------- | -------------------- |
| Input        | Audio In    | Audio signal         |
| Stutter      | Control In  | Stutter crossfade    |
| Output       | Audio Out   | Audio output         |
| Fade\_Filter | Control Out | Fade envelope output |

**Control panel parameters:**

| Parameter        | Range   | Default | Description                     |
| ---------------- | ------- | ------- | ------------------------------- |
| Delay Length     | samples | 32767   | Loop buffer size                |
| Fade Time Filter | linear  | 0.0015  | Crossfade smoothing coefficient |

**Built-in control processing:** The Stutter control input is offset by -0.5 internally before comparison, so a control value of 0.5 is the crossfade midpoint.

***

## Triple Tap Delay

A 3-tap delay with independent delay time controls for each tap. Unlike the six-tap and eight-tap blocks which use a single delay time with ratio offsets, the triple tap allows each tap to have its own delay time control input.

| Pin           | Type       | Description            |
| ------------- | ---------- | ---------------------- |
| Input         | Audio In   | Audio signal           |
| Feedback      | Audio In   | External feedback path |
| Tap 1 Out     | Audio Out  | Tap 1 output           |
| Tap 2 Out     | Audio Out  | Tap 2 output           |
| Tap 3 Out     | Audio Out  | Tap 3 output           |
| Delay Time 1  | Control In | Tap 1 delay time       |
| Delay Time 2  | Control In | Tap 2 delay time       |
| Delay Time 3  | Control In | Tap 3 delay time       |
| Feedback Gain | Control In | Feedback level control |

**Control panel parameters:**

| Parameter     | Range   | Default   | Description                        |
| ------------- | ------- | --------- | ---------------------------------- |
| Input Gain    | linear  | 1.0       | Input level                        |
| Feedback Gain | linear  | 0.5       | Feedback amount                    |
| Delay Length  | samples | 32767     | Delay buffer size                  |
| Tap 1-3 Ratio | 0-1     | 0.45-0.85 | Tap positions as fraction of delay |

**Built-in control processing:** When Delay Time pins are connected, the delay time for each tap is internally limited to a minimum of 5% of the buffer length (scaled by each tap's ratio). This prevents the read pointer from reaching the write pointer.
