Here's another variation on the filter tremolo. We have both filter tremolo and volume modulation with LFO 90 degrees phase shifted.

This is similar to "harmonic tremolo" as found in some vintage tube amps.  Harmonic tremolos may use separate filters for the high and low sections, whereas this implementation uses the high and low outputs of a single state variable filter.  You could of course add a second filter so that the corner frequencies could be controlled independently.  Also I'm pretty sure that harmonic tremolo varies the high and low amplitude 180 degrees out of phase with each other.  This may be a better sound than having them 90 degrees apart.  Nice thing is that you can experiment with all sorts of different ways of doing it.

This is Spin ASM.  

```; Patch: C:\Users\User\Documents\SpinCAD Designer\trem square.spcd
; SpinCAD Designer version: 931
; Pot 0: LFO speed
; Pot 1: LFO "symmetry"
; Pot 2: trem depth
;
;
; ----------------------------
;------ Input
;------ Pot 0
;------ Pot 1
;------ Scale/Offset
RDAX POT0,1.0000000000
SOF 0.7200000000,0.2800000000
WRAX REG0,0.0000000000
;------ Scale/Offset
RDAX POT1,1.0000000000
SOF 0.7000000000,0.3000000000
WRAX REG1,0.0000000000
;------ Pot 2
;------ Power
RDAX POT2,1.0000000000
WRAX REG2,1.0000000000
MULX REG2
WRAX REG3,0.0000000000
;------ SVF 2P
SOF 0.0000000000,0.0000000000
RDAX ADCL,1.0000000000
RDAX REG6,-1.0000000000
RDAX REG5,-0.1428571429
WRAX REG4,0.2067361810
RDAX REG5,1.0000000000
WRAX REG5,0.2067361810
RDAX REG6,1.0000000000
WRAX REG6,0.0000000000
;------ LFO 0
SKP RUN ,1
WLDS 0,277,32767
RDAX REG0,0.5420743640
WRAX SIN0_RATE,0.0000000000
RDAX REG1,1.0000000000
WRAX SIN0_RANGE,0.0000000000
CHO RDAL,0
WRAX REG7,0.0000000000
CHO RDAL,8
WRAX REG8,0.0000000000
;------ Scale/Offset
RDAX REG8,1.0000000000
SOF 0.5000000000,0.5000000000
WRAX REG9,0.0000000000
;------ Crossfade
RDAX REG6,-1.0000000000
RDAX REG4,1.0000000000
MULX REG9
RDAX REG6,1.0000000000
WRAX REG10,0.0000000000
;------ Clip
RDAX REG7,1.0000000000
SOF -2.0000000000,0.0000000000
SOF -2.0000000000,0.0000000000
SOF -2.0000000000,0.0000000000
SOF -1.2500000000,0.0000000000
SOF -0.9990000000,0.9990000000
WRAX REG11,0.0000000000
;------ Scale/Offset
RDAX REG3,1.0000000000
SOF 0.7000000000,0.3000000000
WRAX REG12,0.0000000000
;------ Tremolizer
RDAX REG11,0.9900000000
MULX REG12
SOF -0.9990000000,0.9990000000
WRAX REG13,0.0000000000
;------ Volume
RDAX REG10,1.0000000000
MULX REG13
WRAX REG14,0.0000000000
;------ Output
RDAX REG14,1.0000000000
WRAX DACL,0.0000000000
RDAX REG14,1.0000000000
WRAX DACR,0.0000000000
