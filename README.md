SpinCAD-Designer
================

SpinCAD Designer is an open source Java project which allows creation of patches for the Spin FV-1 audio DSP chip.
It should be used with the Eclipse Java development package, or with the Eclipse DSL development package if you 
wish to explore SpinCAD-Builder.

Initially I developed SpinCAD Designer in Eclipse for Java, Juno and Kepler Versions.  Now, after transferring the code from an SVN account into Github, I have also moved to Eclipse Luna.  I think I'm using Mars now.  Time flies!

I use the Java and DSL (Doman Specific Language) version of the IDE.  SpinCAD Designer itself can be compiled strictly from the Java source supplied.

You'll also have to get the commons-io-2.4-bin.zip file here: http://commons.apache.org/proper/commons-io/download_io.cgi

And the elmgen-05.jar file here: https://github.com/HolyCityAudio/SpinCAD-Designer/tree/master/src/org/andrewkilpatrick/elmGen

The jar files should be extracted from the ZIPs, if necessary, and placed in a folder somewhere on your computer.  Then, add these files to the Build Path for this project.  At the same time, you'll need to delete the erroneous Build Path entries which loaded with the project.

Instructions on using this code with Eclipse can be found here: https://github.com/HolyCityAudio/SpinCAD-Designer/raw/master/Installing_and_configuring_Eclipse_to_compile_SpinCAD_Designer.zip
