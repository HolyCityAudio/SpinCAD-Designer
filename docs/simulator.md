# Simulator

SpinCAD Designer includes a built-in simulator that runs your patch against a
WAV file input, producing audio output through your sound card or to a file.
The simulator executes the same FV-1 instruction stream that would run on the
real chip, giving you a way to hear and measure your patch without hardware.  
The response of the simulator is slightly delayed from the audio.

## Starting and Stopping

Click **Start Simulation** in the toolbar to begin. The simulator reads the
source WAV file, processes it through your patch, and sends the result to the
configured output destination. Click the button again (now labeled **Stop
Simulator**) to stop.

If no source file has been set, you will be prompted to configure one in
Simulator Options.

## Pot Sliders

The three sliders in the simulator toolbar correspond to the FV-1's three
potentiometer inputs (Pot 0, Pot 1, Pot 2). Each slider ranges from 0 to 1
and can be adjusted in real time while the simulator is running. Any block in
your patch that reads a pot value will respond to the slider position, just as
it would respond to a physical potentiometer on real hardware.

## Display

When **Enable Display** is checked in Simulator Options, a waveform display
appears below the patch canvas while the simulator is running. The display has
two modes, toggled by the **Scope / Levels** button in the display toolbar.

### Scope Mode

The scope displays a triggered oscilloscope-style view of the simulator's
output waveforms. Channel 1 (green) and Channel 2 (yellow) show the left and
right DAC outputs.

**Toolbar controls in scope mode:**

| Control | Description |
|---------|-------------|
| Ch 1 / Ch 2 | Toggle each output channel trace on or off |
| Freeze | Pause the display at the end of the current sweep; audio continues |
| Ch 1 Gain / Ch 2 Gain | Vertical gain per channel: 1x, 2x, 4x, 8x, or 16x |
| Time/div (ms) | Horizontal timebase: 1, 2, 5, 10, 20, 50, 100, 200, or 500 ms per division |
| Lin / dB | Toggle the Y-axis scale between linear (0-1) and decibels |

If a **Scope Probe** block is present in the patch, two additional traces
appear: Probe 1 (cyan) and Probe 2 (magenta). These show the signal at
whatever point in the patch the probe inputs are connected, while Ch 1 and
Ch 2 always show the final DAC output. Each probe trace can be toggled
independently.  These are always represented using a 0-1 range regardless of the gain
settings of the Ch 1/Ch 2 inputs.

### Level Logger Mode

The level logger displays a scrolling dB-scale plot of signal levels over
time. It is useful for observing how levels change in response to pot
adjustments or input dynamics. The vertical gain, timebase, and Lin/dB
controls are hidden in this mode — the logger always displays in dB with a
fixed time scale.  Smoothing is applied, so abrupt level changes may not be 
shown accurately.

## Simulator Options

Open the Simulator Options dialog from the **Simulator** menu. Settings are
saved between sessions.

### Output Destination

| Mode | Description |
|------|-------------|
| Sound Card | Audio is played through the system audio output. The simulator always loops the source file continuously. |
| File | Output is written to a WAV file. By default the simulator runs as fast as possible (faster than real time) and stops at the end of the source file. |

### Source File

The input WAV file that the simulator processes. The file must be a **stereo
16-bit WAV** sampled at **32768, 44100, or 48000 Hz**. Click **Browse** to
select a file.

### Output File

The destination WAV file when running in File output mode. Click **Browse** to
select a file.

### Sample Rate

| Rate | Description |
|------|-------------|
| 32768 Hz | The FV-1 default. Uses an inexpensive watch crystal for the clock circuit. |
| 44100 Hz | Standard CD sample rate. Supported by the FV-1 with an appropriate clock circuit. |
| 48000 Hz | Professional audio sample rate. Supported by the FV-1 with an appropriate clock circuit. |

The sample rate selected here should match the clock rate of the target
hardware. It also affects the simulator's internal timing for delay lines,
LFOs, and filters.

### Checkboxes

| Option | Description |
|--------|-------------|
| Enable Display | Show the scope/logger display panel when the simulator is running. Leave unchecked if you do not need the visual display. |
| Loop Mode | Restart the source file from the beginning when it reaches the end. Always on in Sound Card mode. In File mode, only available when Simulate in Real Time is also enabled. |
| Simulate to File in Real Time | When writing to a file, run the simulator at real-time speed instead of as fast as possible. This also enables the Loop Mode option. |
