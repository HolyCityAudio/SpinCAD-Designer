# SpinCAD Designer

Java Swing application for creating and simulating audio patches for the Spin FV-1 DSP chip.

## Build & Test

- **Build:** `./gradlew build`
- **Run tests:** `./gradlew test`
- **Java version:** 1.8 (source and target compatibility)
- **Dependencies:** local jars in `lib/`, JUnit 5 for tests
- Tests run headless (`-Djava.awt.headless=true`)

## Project Structure

- `src/` — Hand-written Java source (core framework, some CADBlocks, control panels)
- `src-gen/` — **Generated** Java source (CADBlocks and ControlPanels produced by SpinCAD Builder)
- `src/SpinCADBuilder/` — `.spincad` definition files that drive code generation
- `src/test/java/` — JUnit 5 tests
- `lib/` — Third-party jar dependencies
- `build.gradle` — Gradle build config (both `src/` and `src-gen/` are source roots)

## Code Generation

**NEVER edit files in `src-gen/` directly.** They are generated output and will be overwritten.

- `.spincad` files in `src/SpinCADBuilder/` define blocks (pins, controls, DSP logic)
- The SpinCAD Builder project (separate repo at `C:\Users\garyw\git\SpinCAD-Builder`) contains Xtext/Xtend generators that produce Java from `.spincad` files
- Generator source: `SpinCAD-Builder/com.holycityaudio.spincad/src/com/holycityaudio/spincad/generator/`
- When a change is needed for a generated block, edit the `.spincad` file in `src/SpinCADBuilder/`
- Some blocks exist only in `src/` (not generated) — those can be edited directly

## Key Packages

- `com.holycityaudio.spincad` — Core framework (SpinCADBlock, SpinCADModel, SpinCADPanel, etc.)
- `com.holycityaudio.spincad.CADBlocks` — Hand-written block implementations (in `src/`)
- `com.holycityaudio.SpinCAD.CADBlocks` — Generated block implementations (in `src-gen/`)
- `com.holycityaudio.spincad.ControlBlocks` — Control block implementations

## Testing

- `AllBlocksCodeGenTest` — Parameterized test that instantiates every CADBlock, wires inputs/outputs, runs code generation, and checks FV-1 resource limits
- `LegacyFileLoadTest` — Tests loading legacy patch files
- `BlockDiscovery` — Utility for discovering all block classes
