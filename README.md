SpinCAD-Designer
================

SpinCAD Designer is an open source Java project which allows creation and audio simulation of patches for the Spin FV-1 audio DSP chip.  It should be used with the Java Runtime Environment or Java Development Kit 1.8 or later.  It has been tested on Windows, macOS, and Linux.

New as of July 2022, I am developing video and written tutorials for SpinCAD to help you get the most out of it.
Please visit: [The SpinCAD Designer Gitbook site](https://holy-city-audio.gitbook.io/spincad-designer)

![SpinCAD Designer](/spincad.png)

If you simply want to use SpinCAD Designer, download the SpinCAD-whatever.jar file and run it on your computer.  You'll need JRE/JDK 1.8 or later.

Initially I developed SpinCAD Designer in Eclipse for Java, Juno and Kepler Versions.  Next, after transferring the code from an SVN account into Github, I have moved to Eclipse Luna.  Now I'm on the 2022 version (as of 2022).

I use the Java and DSL (Domain Specific Language using Xtext and Xtend) version of the IDE.  Xtext is used as a parser to read "SpinCAD Builder" files, while Xtext handles the Java code generation to turn these into SpinCAD Designer blocks.  SpinCAD Designer itself can be compiled strictly from the Java source supplied.

Instructions on using this code with Eclipse can be found here: https://github.com/HolyCityAudio/SpinCAD-Designer/raw/master/Installing_and_configuring_Eclipse_to_compile_SpinCAD_Designer.zip This is only necessary if you wish to fork this repository in order to change or enhance SpinCAD Designer's behavior.  This includes the addition of new functional blocks you have invented or found laying around somewhere.  

Warning: Adding new blocks using SpinCAD Builder is a fairly advanced topic.
