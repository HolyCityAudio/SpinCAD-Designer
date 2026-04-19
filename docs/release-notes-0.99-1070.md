# SpinCAD Designer 0.99-1070 Release Notes

## New Blocks

- **Oil Can Delay** -- Delay with synced LFO modulation, pitch-constant modulation, ratio selector, dB feedback slider, and optional feedback input
- **Single Delay** -- Single-tap delay derived from the ThreeTap pattern

## Block Changes

- **RMS Limiter** -- Simplified: removed Input Gain and diagnostic outputs; detection now at full scale; sidechain input is optional; added Makeup Gain slider
- **Lim/Exp** -- Added Makeup Gain slider
- **Resonator** -- Reworked Q range to 10-50; added QFACTOR slider mode
- **Reverse Delay** -- Added memory mode selector and feedback input
- **Multi-tap delays** -- Added subdivision snap feature to delay time sliders; dB sliders also snap to subdivisions

## Bug Fixes

- Fixed LOG simulator returning wrong value for zero input
- Fixed subdivision snap incorrectly snapping existing slider values on panel open
- Fixed subdivision slider tick display causing panel cutoff

## UI Changes

- Moved Special menu to appear after Instructions and before Simulator
- Renamed I/O-Mix menu to Mixers/Gain; renamed Pots menu to I/O-Pots
- Moved Root block to Control menu
- Converted all hand-written control panels from JFrame to JDialog (fixes hidden-behind-window issues)

## File Format

- Patch files (.spcdj) now include a build number; loading a file saved by a newer version shows a warning

## Infrastructure

- Added security scanning (deserialization/reflection vulnerability fixes)
- Added LICENSE (GPLv3), CONTRIBUTING.md, CODE_OF_CONDUCT.md

## Documentation

- Added tutorials: Using SpinCAD Designer, Using Control Signals, Managing Headroom
- Added block reference pages: Special Blocks, Simulator
- Added example patches: Arpeggiator, Resonator, Tremolo
- Reorganized docs to match new menu structure with alphabetized block sections and index tables
