# Control Blocks Reference

## Introduction to Control Signals

An audio signal in the FV-1 is something that you actually listen to (or could).
A **control signal** is a number which affects some other part of the algorithm,
usually changing at a rate much lower than audio signals. For example, a pot
control delivers a value which can be multiplied with an audio signal to
implement a volume control.

A voltage of 0 is interpreted numerically as 0, whereas a voltage of 3.3 volts
(the supply voltage) is returned as 0.999 (numerically very close to 1.0).

Virtually all control signals operate from **0 to 1**. An exception is the
SIN/COS LFO which naturally puts out a signal from -1.0 to 1.0.

The blocks in this section shape, scale, and transform control signals.
They are found in the **Controls** menu of SpinCAD Designer.

---

## Invert

**Menu:** Controls > Invert

This block inverts the range 0 -> 1 to 1 -> 0. There is no control panel.
It implements the FV-1 instruction:

    SOF -0.999, 0.999

which generates an output y from input x:

    y = -x + 1 = 1 - x

(The coefficients are as close to 1.0 as the FV-1's SOF instruction allows:
the multiplier C is S1.14 format and the offset D is S.10 format.)

| Pin | Type | Description |
|-----|------|-------------|
| Control Input 1 | Control In | 0-1 control signal |
| Control Output 1 | Control Out | Inverted signal (1 -> 0) |

![Invert transfer curve](images/control-invert.png)

**Transfer function:** output = 1 - input

Note that because the Invert block fades linearly from 0 to 1, at its
midpoint both the original and inverted signals will be 0.5. If used for
crossfading, this results in a perceived level drop of -3 dB due to our ears'
sensitivity being related to the added power of both signals. The **Crossfade**
block (under I/O-Mix) merges the Invert block's control signal inversion with
a 2-input mixer to handle this in one step.

---

## Power

**Menu:** Controls > Power

The Power block shapes control signals by raising them to an integer power.
For example, if you have a Pot going directly to a volume control, the Power
block lets you convert that linear taper into a more natural-feeling curve.

| Pin | Type | Description |
|-----|------|-------------|
| Control Input 1 | Control In | 0-1 control signal |
| Control Output 1 | Control Out | Shaped output |

**Parameters:**
| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Power | 1-5 | 3 | Exponent applied to input |
| Invert | on/off | off | Invert input before raising to power |
| Flip | on/off | off | Invert output after raising to power |

![Power transfer curves](images/control-power.png)

The four combinations of Invert and Flip produce different curve shapes:

| Invert | Flip | Equation | Description |
|--------|------|----------|-------------|
| off | off | y = x^p | Compressed toward zero; most change at high end |
| on | off | y = (1-x)^p | Same shape but input direction reversed |
| off | on | y = 1 - x^p | Output inverted; high at low input, drops at high end |
| on | on | y = 1 - (1-x)^p | S-curve feel; slow start, fast finish |

![Power: no invert, no flip](images/control-power-0.png)
![Power: invert only](images/control-power-1.png)
![Power: flip only](images/control-power-2.png)
![Power: invert + flip](images/control-power-3.png)

At power=2, a linear pot sweep becomes a quadratic taper. At power=5, most
of the output change happens in the upper quarter of the input range.

A power of 1 is possible but wastes instructions and a register since
the output equals the input.

**Typical use:** Convert a linear pot to an audio-taper or logarithmic-feel
response for volume, filter cutoff, or delay feedback controls. Two Power
blocks can be used in conjunction with the straight pot signal to fade 3
signals in one at a time with the rotation of a single pot.

**Companion:** The [Root](#root) block is the natural companion to Power --
where Power compresses the response toward zero (quadratic, cubic), Root
expands the response away from zero (square root, cube root). Use Power
when you want fine resolution at the top of a pot's travel and Root when
you want fine resolution at the bottom.

---

## Root

**Menu:** Controls > Root

The Root block shapes control signals by raising them to the reciprocal
of an integer power, producing curves like the square root (power=2),
cube root (power=3), and so on. Internally it is implemented as a LOG/EXP
pair: the LOG instruction divides the logarithm by N and the EXP
instruction converts back to linear, effectively computing
`input^(1/N)`.

| Pin | Type | Description |
|-----|------|-------------|
| Control Input 1 | Control In | 0–1 control signal |
| Control Output 1 | Control Out | Shaped output |

**Parameters:**

| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Root | integer | 2 | Root degree (2 = square root, 3 = cube root, etc.) |
| Invert | on/off | off | Negate and offset input before computing root |
| Flip | on/off | off | Negate and offset output after computing root |

The Invert option transforms the input via `x' = -x + 1.0` before the
root computation. The Flip option applies the same transformation to
the output. These are useful for inverting the shape of the curve.

**Behavior at zero:** Because the Root block uses LOG internally, inputs
at or very near zero produce extreme values (LOG of zero is negative
infinity). The FV-1 saturates this to its minimum value, causing a spike
near zero as shown in the plot. For practical use, ensure inputs stay
above approximately 0.01.

![Root](images/instructions-root.png)

**Typical use:** A square-root curve on a pot gives fine resolution at
the low end of the range -- ideal when the quiet or subtle part of a
parameter is where the interesting behavior lives (e.g. low feedback
amounts, shallow modulation depths, the start of a reverb time sweep).

**Companion:** The [Power](#power) block is the natural companion to Root --
where Root expands the response away from zero, Power compresses the
response toward zero.

---

## Two Stage

**Menu:** Controls > Two Stage

Given that the FV-1 only supports 3 control pots, once your patch goes over
the normal amount of complexity, it can be a challenge figuring out how to
control everything. The Two-Stage block takes a single 0-1 input (e.g., from
a pot) and generates two outputs that divide the pot travel in half.

| Pin | Type | Description |
|-----|------|-------------|
| Input | Control In | 0-1 control signal |
| Stage 1 | Control Out | Active in lower half of input (0 to 0.5) |
| Stage 2 | Control Out | Active in upper half of input (0.5 to 1.0) |

![Two Stage transfer curves](images/control-two-stage.png)

**Transfer function:**

As the input goes from 0 to 0.5:
- Stage 1 goes linearly from 0 to 1.0
- Stage 2 stays at 0

As the input goes from 0.5 to 1.0:
- Stage 1 stays at 1.0
- Stage 2 goes linearly from 0 to 1.0

**Typical use:** Use a single pot to sequence two parameters. For example:
0 to 0.5 turns up the LFO speed, 0.5 to 1.0 turns up the LFO width. You can
add Scale/Offset, Power, or other shaping blocks to either output to fine-tune
the response.

---

## Vee

**Menu:** Controls > Vee

The Vee block splits a single control input into two outputs whose shape
depends on which output pins are connected.

| Pin | Type | Description |
|-----|------|-------------|
| Input | Control In | 0-1 control signal |
| Output 1 | Control Out | See modes below |
| Output 2 | Control Out | See modes below |

### Both outputs connected — complementary half-ramps

When both outputs are wired, each covers half the input range with a linear
ramp and stays clamped at zero for the other half.

![Vee transfer curves (both outputs connected)](images/control-vee.png)

As the input goes from 0 to 0.5:
- Output 1 goes linearly from 1.0 to 0
- Output 2 stays at 0

As the input goes from 0.5 to 1.0:
- Output 1 stays at 0
- Output 2 goes linearly from 0 to 1.0

### Single output connected — full V-shape

When only one output is connected, it covers the full input range as a
symmetric V (or inverted V), implemented with the ABSA instruction.

![Vee transfer curves (single output connected)](images/control-vee-single.png)

- **Output 1 only:** V-shape — high at edges (1.0), zero at center (0.5)
- **Output 2 only:** Inverted V — zero at edges, high at center (1.0)

**Typical use:** Drive two effects with a single pot so one fades out as the
other fades in, with the crossover point at the pot's midpoint. For example,
pot 2 controls the FX blend through a Vee block: in the middle it is just dry,
all the way left brings in chorus, all the way right brings in flanger. The LFO
speed and width of the two modulation stages can be individually scaled with
separate Scale/Offset blocks.

---

## Ratio

**Menu:** Controls > Ratio

Sometimes you want a single pot to control two things where one goes up while
the other goes down, and you'd like the values to remain balanced over the
full sweep of the pot.

A classic example is a chorus with independent controls for LFO speed and
width. If you control both directly, the amount of detuning increases with
both LFO frequency *and* width. The Ratio block lets you make them track
together properly so that the amount of detuning remains fixed regardless
of speed.

| Pin | Type | Description |
|-----|------|-------------|
| Input | Control In | 0-1 control signal |
| FullRange | Control Out | Linear scaled output |
| Ratio | Control Out | Inverse proportional output |

**Parameters:**
| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Ratio | 2-100 | 5 | Compression ratio |

**Transfer function:**

The **FullRange** output is a linear ramp:

    y = (1 + (ratio-1) * x) / ratio

This starts at 1/ratio when x=0 and ramps linearly to 1 when x=1.

![Ratio FullRange output](images/control-ratio-fullrange.png)

The **Ratio** output is an inverse curve:

    y = 1 / (1 + (ratio-1) * x)

This starts at 1 when x=0 and decreases to 1/ratio when x=1.

![Ratio output](images/control-ratio-ratio.png)

**Key property:** Multiply these together, and the product is the constant
value 1/ratio across the entire input range. For each ratio setting, the
FullRange curve intersects the y-axis (x=0) at the same value as the Ratio
curve intersects the x=1 line.

![Ratio FullRange x Ratio product](images/control-ratio-product.png)

This is implemented on the FV-1 using the LOG and EXP instructions, which
compute the inverse function in the logarithmic domain.

**Typical use:** Control a chorus LFO speed (FullRange) and width (Ratio)
from a single pot. As the speed increases, the width automatically decreases
to maintain consistent perceived detuning. Add a Multiply block with a second
pot to allow independent depth control that still tracks proportionally.

---

## Clip

**Menu:** Controls > Clip

The Clip block adds an adjustable amount of gain to a control signal with hard
clipping at the 0-1 boundaries. The way to think about this block is: "I want
the incoming control signal to be 0 (or 1) up to a specific point of the pot's
rotation, after which it ramps linearly to 1 (or 0)."

| Pin | Type | Description |
|-----|------|-------------|
| Control Input 1 | Control In | 0-1 control signal |
| Control Output 1 | Control Out | Amplified and clipped signal |

**Parameters:**
| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Gain | 1-10 | 3 | Amplification factor before clipping |
| Flip | on/off | off | Reverse input direction before clipping |
| Invert | on/off | off | Invert output after clipping |

![Clip transfer curves at different gains](images/control-clip.png)

The four combinations of Flip and Invert (shown here at gain=10):

| Flip | Invert | Equation | Description |
|------|--------|----------|-------------|
| off | off | 0 <= x <= 0.1: y = 10x; x > 0.1: y = 1.0 | Ramps up fast then clips high |
| on | off | x <= 0.9: y = 1.0; x > 0.9: y = 10(1-x) | Stays high, drops at the end |
| off | on | x <= 0.1: y = 1 - 10x; x > 0.1: y = 0 | Drops fast from 1, then stays at 0 |
| on | on | x <= 0.9: y = 0; x > 0.9: y = 10(x - 0.9) | Stays low, ramps up at the end |

![Clip: normal (no flip, no invert)](images/control-clip-0.png)
![Clip: flip only](images/control-clip-1.png)
![Clip: invert only](images/control-clip-2.png)
![Clip: flip + invert](images/control-clip-3.png)

**Typical use:** Smoothly "switch" between settings at either extreme end of
the pot travel. For example, in a delay with infinite feedback hold: over most
of the pot travel (0 to 0.9), the delay input volume is 1.0 and feedback is 0.
At the top of the pot (0.9 to 1.0), the input fades to 0 and feedback goes to
1.0, creating an infinite loop. Using Clip with high gain and Flip, the
transition happens over just the last 10% of pot travel.

---

## Half Wave

**Menu:** Controls > Half Wave

Half-wave rectifier: passes positive values unchanged, clamps negative values
to zero. Implemented using the FV-1's `SKP GEZ` (skip if greater than or equal
to zero) instruction followed by `CLR` (clear accumulator).

| Pin | Type | Description |
|-----|------|-------------|
| Input | Control In | Control signal (may include negative values) |
| Output | Control Out | max(0, input) |

![Half Wave DC transfer](images/control-halfwave.png)

**Transfer function:** output = max(0, input)

Since control signals from pots are typically 0-1, this block acts as a
pass-through for normal pot signals. Its primary use is in signal chains where
a control signal might go negative, for example after subtraction, mixing with
a bipolar LFO, or modulation.

Applied to a full-range sine wave (-1 to +1), the output retains only the
positive half-cycles:

![Half Wave on sine wave](images/control-halfwave-sine.png)

**Typical use:** Clamp the output of a mixer or difference block to prevent
negative control values from causing unexpected behavior in downstream blocks.

---

## Slicer

**Menu:** Controls > Slicer

The Slicer is a binary comparator that converts continuous control signals into
hard on/off switches. The most straightforward application is to get a square
wave from a sine wave.

Set the **Slice Level** at 50%, and the **Slicer Out** goes high when the
**Control In** is below this amount, and low when above. (This may seem
backwards, but that's how it works: output is high when input is *below*
the threshold.)

| Pin | Type | Description |
|-----|------|-------------|
| Control In | Control In | 0-1 control signal to compare |
| Slice Level | Control In | Optional: modulate threshold from another control |
| Slicer Out | Control Out | Binary output |

**Parameters:**
| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Slice Level | 0.0-0.95 | 0.5 | Comparator threshold |
| Control Range | 0->+1 / -1->+1 | 0->+1 | Output range selection |

**Output modes:**
- **0 -> +1**: output is 0 (low) or ~1.0 (high)
- **-1 -> +1**: output is -1.0 (low) or +1.0 (high)

![Slicer DC transfer at different thresholds](images/control-slicer.png)

**Pulse Width Modulation:** When a sine wave LFO feeds the Control In, the
Slicer produces a square wave. Varying the Slice Level changes the duty cycle,
creating classic PWM:

![Slicer PWM: slice level 25%](images/control-slicer-sine-0.png)
![Slicer PWM: slice level 50%](images/control-slicer-sine-1.png)
![Slicer PWM: slice level 75%](images/control-slicer-sine-2.png)

When a Pot is connected to the Slice Level input, varying the pot modulates
the pulse width over its full range. At pot=0, pulses stop entirely (slice
level is zero). To set a minimum pulse width, use a Scale/Offset block between
the pot and the Slice Level input.

**Typical use:** Feed an 8 Hz sine wave into the Slicer to generate a square
wave for tremolo effects. Follow with a **Smoother** block to add exponential
rise/fall times to the transitions, creating a variable-shape tremolo. Combine
with the **Tremolizer** block to control tremolo depth.

Another application: use the Slicer to drive a **Crossfade** block, creating an
instant switch between two effects (e.g., phaser and flanger) at the 50% point
of a pot. Combined with a **Vee** block on the same pot, you can independently
boost feedback for each effect on opposite sides of the crossover point.

---

## Tremolizer

**Menu:** Controls > Tremolizer

The Tremolizer takes an LFO signal (0 to 1) and converts it into a volume
envelope that *reduces* gain from 1.0. Unlike simply multiplying an LFO by
a width control (which gives you nothing out when the width is zero), the
Tremolizer uses the control signal's value above zero to reduce gain from
unity. So at zero depth, you get full signal; at maximum depth, the LFO
fully chops the signal.

| Pin | Type | Description |
|-----|------|-------------|
| LFO Input | Control In | 0-1 LFO signal |
| LFO Width | Control In | Optional: external width modulation |
| Control Output | Control Out | Volume envelope (1 minus scaled LFO) |

**Parameters:**
| Parameter | Range | Default | Description |
|-----------|-------|---------|-------------|
| Depth | 0.5-1.0 | 0.75 | Maximum depth of volume reduction |

**Transfer function:**

    output = 1 - (depth * input * width)

When the LFO Width pin is not connected:

    output = 1 - (depth * input)

At depth=1.0, the output goes from 1.0 (when LFO=0) down to 0.0 (when LFO=1),
giving "full chop" tremolo. At lower depth values, the output doesn't go as
low, producing a gentler volume modulation.

![Tremolizer DC transfer at different depths](images/control-tremolizer.png)

Applied to a 0-1 sine wave LFO, the Tremolizer produces an inverted volume
envelope:

![Tremolizer volume envelope from sine LFO](images/control-tremolizer-sine.png)

**Typical use:** Build a tremolo patch by connecting a Sine LFO (scaled 0 to 1)
to the Tremolizer's LFO Input, then connect the Tremolizer output to a Volume
control block. Use a Pot on the LFO Width input to control tremolo depth from
the front panel.

For a variable-shape tremolo, feed the LFO through a **Slicer** and then a
**Smoother** before the Tremolizer. The Smoother's corner frequency controls
the rise/fall time of the chopped waveform, giving everything from smooth sine
tremolo to hard square-wave chop. Notice that decreasing the Smoother frequency
also reduces the effective chop depth, since the LFO takes longer to reach
full excursion.

### Alternatives

If you use the width parameter input on the LFO block directly, it shrinks
about its midpoint, so with the width all the way down you get a 6 dB drop
in the output level. This is more subtle but closer to typical Fender guitar
amp tremolo sounds.

---

## Generating Updated Curves

The per-block SVG curves above are generated by running:

```
./gradlew test --tests "com.holycityaudio.SpinCAD.ControlBlockSweepTest"
```

Individual SVGs are written to the `docs/` directory.
