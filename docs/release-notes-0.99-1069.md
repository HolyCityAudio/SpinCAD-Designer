# SpinCAD Designer 0.99-1069 Release Notes

## New Feature

- **VU Meter block** -- Two-channel VU meter with dockable display and three meter modes

## Bug Fixes

- Fixed broken negative pitch shift formula
- Fixed LPF4P/HPF2P to use control panel frequency
- Fixed stale LPF4P test assertion (passband 0dB not -6dB)
- Fixed instruction plots (MAXX, Root, LOG/EXP)
- Fixed SVF frequency response measurement (H1 estimator)
- Linearized phaser LFO sweep, fixed slider crash on out-of-range params

## New Blocks

- **Crossfade Adj** -- Crossfade block with adjustable midpoint
- **Peak Compressor** -- Peak-detecting compressor with Faust-style threshold clamping
- **RMS Compressor** -- RMS-detecting compressor with Faust-style threshold clamping
- **Comb Filter** -- Comb filter
- **Resonator** -- Resonator filter
- **Long Delay** -- Interleaved delay for extended delay times
- **Ambience** -- Ambience reverb
- **Freeverb** -- Freeverb algorithm reverb
- **Plate Reverb** -- Dattorro plate reverb
- **Reverb Designer** -- Configurable reverb
- **Spring Reverb** -- Spring reverb simulation
- **Parker Spring Reverb** -- Parker spring reverb (experimental, may be removed)
- **Pitch Four** -- Four-voice pitch shifter (previously hidden)
- **Adj Change Detect** -- Adjustable change detector for control signals
- **Envelope Follower** -- Envelope follower (replaces deprecated Envelope and Envelope II)

## Block Changes

- **Bassman '59 EQ** -- Rewritten with exact Yeh/Smith tone stack model
- Standardized Q/resonance across all filter blocks
- Updated all gain sliders to 0.1 dB resolution (mixer, delay, reverb, pitch)

## Removed Blocks

- **Envelope** -- Deprecated, replaced by Envelope Follower
- **Envelope II** -- Deprecated, replaced by Envelope Follower

## Menu Reorganization

- All menus alphabetically sorted
- **Pots** split out as its own menu (was under Control)
- **Oscillators** split out as its own menu (LFO/oscillator blocks moved from Control)
- Fixed Vee duplicate fields
- Added default pots on new patch
