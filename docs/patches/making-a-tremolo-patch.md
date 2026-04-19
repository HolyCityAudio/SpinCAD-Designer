# Making a Tremolo Patch

{% embed url="https://www.youtube.com/watch?v=lV8yjU29DoA" %}

[Watch on YouTube](https://www.youtube.com/watch?v=lV8yjU29DoA)

## Overview

A really simple patch -- a tremolo -- that demonstrates two important control signal processing blocks.

## Key Components

**[Scale/Offset](../instructions-blocks.md) Block:** Lets you constrain the low and/or high range of a control to ensure precise adjustment of parameters.

**[Power](../control-blocks.md#power) Block:** Lets you shape a control so it comes in faster or slower relative to the rotation of the pot.

## Tremolizer Block Details

The [Tremolizer](../control-blocks.md#tremolizer) block requires explanation regarding its width control. When using the Sin/Cos LFO width control, adjustment affects the swing around the center point (0 when output range is −1.0 to 1.0; 0.5 when output range is 0.0 to 1.0).

The intended behavior is for the LFO signal, going from 0.0 to 1.0, to *reduce* the gain of the audio signal as LFO amplitude increases. This prevents complete signal loss when width reaches zero. The Tremolizer uses some clever internal scaling to achieve this effect automatically.

## Resources

- [Download the patch file (tremolo.spcd)](https://github.com/HolyCityAudio/SpinCAD-Designer/raw/master/patches/tremolo.spcd)
