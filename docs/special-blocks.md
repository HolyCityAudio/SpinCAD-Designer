# Special Blocks

The blocks in this section are available from the **Special** menu. Unlike normal signal-processing blocks, these are either structural (Feedback Loop) or simulation-only diagnostics (Scope Probe, VU Meter). They are all drawn with an orange border to distinguish them visually.

### Block Index

|                                                  |                                              |                                        |
| ------------------------------------------------ | -------------------------------------------- | -------------------------------------- |
| [Feedback Loop](special-blocks.md#feedback-loop) | [Scope Probe](special-blocks.md#scope-probe) | [VU Meter](special-blocks.md#vu-meter) |

***

## Feedback Loop

**Menu:** Special > Feedback Loop

<figure><img src=".gitbook/assets/image (22).png" alt=""><figcaption></figcaption></figure>

The Feedback Loop allows you to route a signal backward in the patch — from a block that appears later in the signal flow to one that appears earlier. SpinCAD Designer normally prohibits direct backward connections because they cannot be made to work reliably in a single-pass DSP program. The Feedback Loop solves this by introducing a one-sample delay between the two endpoints, which is the standard technique used in the FV-1 architecture.

Selecting **Feedback Loop** from the Special menu creates a matched pair of blocks: **FB In** and **FB Out**. They share an index number (0, 1, 2, ...) that links them together. Data flows from the FB Input block's input pin (top) to the FB Output block's output pin (bottom), delayed by one sample.

You can have more than one feedback loop in a patch; each pair is distinguished by its index. Deleting either block in a pair automatically deletes its partner.

| Block  | Pin             | Type      | Description                      |
| ------ | --------------- | --------- | -------------------------------- |
| FB In  | Feedback Input  | Audio In  | Signal to feed back              |
| FB Out | Feedback Output | Audio Out | Delayed copy of the FB In signal |

**Parameters (FB In):**

| Parameter | Range         | Default | Description                                                        |
| --------- | ------------- | ------- | ------------------------------------------------------------------ |
| Gain      | -1.90 to 1.90 | 1.00    | Gain applied to the signal before writing to the feedback register |

**Typical use:** Place the FB Output block before a delay or filter, and the FB Input block after it, connecting the processed output back to the input. This is essential for building effects that require recirculation, such as delay feedback or resonant structures.

***

## Scope Probe

**Menu:** Special > Scope Probe

<figure><img src=".gitbook/assets/image (23).png" alt=""><figcaption></figcaption></figure>

The Scope Probe is a simulation-only diagnostic block. It taps into up to two signals in your patch and displays them on the simulator's oscilloscope. It does not generate any FV-1 code, so it is safe to leave in your patch when exporting to ASM, Hex, or SPJ formats — it will be automatically stripped.

When a Scope Probe is present in the patch, the simulator toolbar shows probe buttons that let you trigger and view the captured waveforms.

| Pin     | Type     | Description              |
| ------- | -------- | ------------------------ |
| Scope 1 | Audio In | First signal to monitor  |
| Scope 2 | Audio In | Second signal to monitor |

***

## VU Meter

**Menu:** Special > VU Meter

<figure><img src=".gitbook/assets/image (24).png" alt=""><figcaption></figcaption></figure>

The VU Meter is a simulation-only two-channel level meter. Like the Scope Probe, it does not generate any FV-1 code and is automatically stripped when exporting. The meter display can be docked into the main SpinCAD window or floated as a separate window.

<figure><img src=".gitbook/assets/image (25).png" alt=""><figcaption></figcaption></figure>

| Pin  | Type     | Description                   |
| ---- | -------- | ----------------------------- |
| VU 1 | Audio In | Left channel signal to meter  |
| VU 2 | Audio In | Right channel signal to meter |

**Display modes** (selected by radio buttons on the meter):

| Mode   | Description                                                                                                      |
| ------ | ---------------------------------------------------------------------------------------------------------------- |
| VU     | 300 ms RMS integration on a dB scale (-60 to +3 dB) with green/yellow/red color coding and a peak hold indicator |
| Peak   | Fast-attack, slow-decay peak metering on a dB scale with peak hold indicator                                     |
| Linear | Instantaneous absolute value on a linear 0-1 scale with no ballistics or peak hold                               |
