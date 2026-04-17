# Contributing to SpinCAD Designer

Thank you for your interest in SpinCAD Designer! This is a long-running open source project for designing audio DSP programs for the Spin FV-1 chip, and contributions — however small — are genuinely appreciated.

A few things to know going in: this project is maintained by one person on a sporadic basis. Response times on issues and pull requests may be slow. Please be patient.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Project Overview](#project-overview)
- [Getting Started](#getting-started)
- [How to Contribute](#how-to-contribute)
- [Pull Request Process](#pull-request-process)
- [Style Guidelines](#style-guidelines)
- [Reporting Bugs](#reporting-bugs)
- [Requesting Features](#requesting-features)

---

## Code of Conduct

This project follows the [Contributor Covenant Code of Conduct](https://www.contributor-covenant.org/version/2/1/code_of_conduct/). By participating, you agree to uphold it. Please report unacceptable behavior to the project maintainer via a GitHub issue marked `[conduct]`.

---

## Project Overview

SpinCAD Designer is a graphical editor for building FV-1 DSP programs by connecting audio processing modules. It is written primarily in **Java** and uses **Eclipse** as its development environment.

The companion project **SpinCAD Builder** (located in a separate repository) generates the Java module code from a domain-specific language, and is written in **Xtext** and **Xtend**.

---

## Getting Started

### Prerequisites

- **Eclipse IDE for Java and DSL Developers, 2022** (recommended)
  - Download from [eclipse.org](https://www.eclipse.org/downloads/packages/)
  - The "DSL Developers" package includes Xtext/Xtend support needed for SpinCAD Builder
- Java 8 or later (Java 8 is the minimum supported version)
- Git

### Setting Up the Project

1. Fork the repository on GitHub: [HolyCityAudio/SpinCAD-Designer](https://github.com/HolyCityAudio/SpinCAD-Designer)
2. Clone your fork locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/SpinCAD-Designer.git
   ```
3. Import the project into Eclipse:
   - **File → Import → Existing Projects into Workspace**
   - Select the cloned directory
4. Resolve any missing dependencies via the Eclipse project setup (check `.classpath` and the project's README for any additional jars)
5. For **SpinCAD Builder**, clone and import that project separately and follow its own setup instructions

---

## How to Contribute

### Reporting Bugs

Please open an issue at the [issue tracker](https://github.com/HolyCityAudio/SpinCAD-Designer/issues). Include:

- A clear description of the problem
- Steps to reproduce it
- What you expected to happen vs. what actually happened
- Your OS and Java version
- Any relevant error output from the Eclipse console

### Requesting Features

Open an issue describing the feature and the use case it would address. Please check existing issues first to avoid duplicates.

### Contributing New Modules (Most Welcome)

**The most valuable contributions are new DSP modules.** SpinCAD Designer is built around a library of processing blocks (filters, oscillators, delay effects, etc.) that users connect visually to build FV-1 programs. If you have knowledge of DSP algorithms and want to implement something that doesn't already exist in the module library, that is exactly the kind of contribution most welcome here.

Before building a new module, **check the existing module list** in the application and in the source to confirm it doesn't already exist or substantially overlap with something already there. Open an issue describing what you plan to build — this avoids duplicated effort and gives a chance to discuss the design before you invest time in it.

There are two ways to build a new module:

**Option 1: SpinCAD Builder (recommended for most contributors)**

SpinCAD Builder is a domain-specific language that lets you write annotated Spin ASM files and automatically generates the Java source code for the module and its control panel. You don't need to know Java — if you know Spin ASM, you can contribute a module this way.

The workflow is:
1. Write a `.spincad` file using the SpinCAD Builder DSL — essentially annotated Spin ASM with special `@` directives for things like input/output pins and GUI controls
2. Save it in the SpinCAD Builder-enabled Eclipse instance, which triggers automatic Java code generation
3. The generated Java integrates into SpinCAD Designer as a new block

To get started with SpinCAD Builder:
- Repo: [SpinCAD-Builder on GitHub](https://github.com/HolyCityAudio/SpinCAD-Builder)
- Overview: [How SpinCAD Builder works](https://holy-city-audio.gitbook.io/spincad-designer/beneath-the-hood/how-spincad-builder-works)
- Worked example: [Analyzing a simple SpinCAD Builder file](https://holy-city-audio.gitbook.io/spincad-designer/beneath-the-hood/analyzing-a-simple-spincad-builder-file)

**Option 2: Direct Java via ElmGen**

SpinCAD Designer uses Andrew Kilpatrick's [ElmGen](https://github.com/hires/ElmGen) library, which represents Spin ASM instructions as Java method calls. This is what makes the real-time FV-1 simulator and the object-oriented module structure possible — each block is a Java object whose `generate` method emits ElmGen calls that ultimately produce Spin ASM. Note that the version of ElmGen inside SpinCAD Designer has been significantly extended beyond the original and was not forked via GitHub, so the two have diverged.

Writing a module this way still requires knowledge of Spin ASM; ElmGen is a 1-to-1 mapping of the instruction set into Java, not an abstraction above it. The main reason to go this route rather than SpinCAD Builder is to work around current limitations of the Builder DSL — some of which are documented in the [issue tracker](https://github.com/HolyCityAudio/SpinCAD-Designer/issues). Look at a simple existing module in the source for reference, and using Claude with the existing source code as context is a practical way to get oriented.

### Contributing Bug Fixes and Other Code

1. Check the [issue tracker](https://github.com/HolyCityAudio/SpinCAD-Designer/issues) for open issues
2. Comment on the issue to let others know you're working on it
3. For anything beyond a small bug fix, **open an issue first** before investing significant effort
4. Fork the repo, create a branch off `master`, make your changes, and submit a pull request

Surprise feature contributions are welcome — if you see something that would genuinely improve the tool, go for it and make the case in the PR.

---

## Pull Request Process

1. **Target branch:** Submit all PRs against `master`
2. **Branch naming:** Use descriptive branch names, e.g. `fix/lfo-output-clipping` or `feature/new-delay-module`
3. **Keep PRs focused:** One bug fix or feature per PR whenever possible
4. **Describe your changes:** Fill out the PR description explaining what changed and why
5. **CI must pass:** GitHub Actions runs automated checks via Gradle on all PRs. The build system was set up with Claude's help — if you're unsure how to run it locally, asking Claude (with the project's `build.gradle` as context) is a reasonable approach
6. **Be patient:** This project is maintained by one person on a part-time basis. Reviews may take time

---

## Style Guidelines

- Follow the existing code conventions you see in the project (indentation, naming, structure)
- Keep changes consistent with the surrounding code style rather than reformatting unrelated lines
- The UI is built with **Java Swing** — any UI changes should stay consistent with the existing Swing patterns in the codebase, and must remain compatible with Java 8
- For SpinCAD Builder contributions, follow Xtend/Xtext conventions consistent with the existing DSL grammar and generator code

---

## A Note on AI-Assisted Development

Recent development on this project has made use of AI tools (specifically Claude). AI-assisted contributions are welcome, but please review generated code carefully before submitting — you are responsible for what you submit regardless of how it was written.

---

## Questions?

Open an issue on the [issue tracker](https://github.com/HolyCityAudio/SpinCAD-Designer/issues) and tag it with `question`.
