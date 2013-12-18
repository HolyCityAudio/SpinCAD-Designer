	package com.holycityaudio.SpinCAD;
	import com.holycityaudio.SpinCAD.SpinCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.VolumeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer2_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer3_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.crossfadeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.BitCrusherCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.CubeGainCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.DistortionCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OctaveCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OverdriveCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ToverXCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.InstructionTestCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rms_lim_expCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rms_limiterCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.soft_knee_limiterCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.slow_gearCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LPF1PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LPFCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.BPFCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.HPFCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.HPF2PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SVF2PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.BiQuadCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LPF4PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OneBandEQCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SixBandEQCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SingleDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.StraightDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PingPongCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MultiTapCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MN3011CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.DiscoReverbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.gated_verbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.dance_ir_fla_lCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.very_tight_plate_verbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.GtrReverbChorusCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rom_rev1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.shimmer_verbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MinReverbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ChorusCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RichChorusCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ChorusPresetCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.FlangerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ModDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PhaserCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Test_chorusCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.TremoloCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RingModCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PitchUpCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PitchUpDownCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ga_demo_chorusCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ga_demo_echo_repeatCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ga_demo_flangerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ga_demo_phaserCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ga_demo_vibratoCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ga_demo_wahCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot0CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SinCosLFOCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RampLFOCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.EnvelopeControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PowerControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ClipControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.InvertControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ScaleOffsetControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ExponentialControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ControlMixerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.TapTempoCADBlock;

	import java.awt.event.ActionEvent;
	import java.awt.event.ActionListener;

	import javax.swing.JMenu;
	import javax.swing.JMenuBar;
	import javax.swing.JMenuItem;

	public class standardMenu {

//	private static final long serialVersionUID = 1L;

	public standardMenu(final SpinCADFrame f, final SpinCADPanel panel, JMenuBar menuBar) {

	JMenu mn_io_mix = new JMenu("I/O - Mix");
	menuBar.add(mn_io_mix);
	
	final JMenuItem mntm_Input = new JMenuItem("Input");
	mntm_Input.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new InputCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Input);
		
	final JMenuItem mntm_Output = new JMenuItem("Output");
	mntm_Output.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new OutputCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Output);
		
	final JMenuItem mntm_Volume = new JMenuItem("Volume");
	mntm_Volume.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new VolumeCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Volume);
		
	final JMenuItem mntm_Mixer2_1 = new JMenuItem("2 >1 Mixer");
	mntm_Mixer2_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer2_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer2_1);
		
	final JMenuItem mntm_Mixer3_1 = new JMenuItem("3 >1 Mixer");
	mntm_Mixer3_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer3_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer3_1);
		
	final JMenuItem mntm_crossfade = new JMenuItem("Crossfade");
	mntm_crossfade.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new crossfadeCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_crossfade);
		
	JMenu mn_waveshaper = new JMenu("Wave Shaper");
	menuBar.add(mn_waveshaper);
	
	final JMenuItem mntm_BitCrusher = new JMenuItem("Bit Crusher");
	mntm_BitCrusher.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new BitCrusherCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_BitCrusher);
		
	final JMenuItem mntm_CubeGain = new JMenuItem("Cube");
	mntm_CubeGain.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new CubeGainCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_CubeGain);
		
	final JMenuItem mntm_Distortion = new JMenuItem("Distortion");
	mntm_Distortion.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new DistortionCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_Distortion);
		
	final JMenuItem mntm_Octave = new JMenuItem("Octave");
	mntm_Octave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new OctaveCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_Octave);
		
	final JMenuItem mntm_Overdrive = new JMenuItem("Overdrive");
	mntm_Overdrive.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new OverdriveCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_Overdrive);
		
	final JMenuItem mntm_ToverX = new JMenuItem("T/X");
	mntm_ToverX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ToverXCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_ToverX);
		
	final JMenuItem mntm_InstructionTest = new JMenuItem("Test");
	mntm_InstructionTest.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new InstructionTestCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_InstructionTest);
		
	JMenu mn_dynamics = new JMenu("Dynamics");
	menuBar.add(mn_dynamics);
	
	final JMenuItem mntm_rms_lim_exp = new JMenuItem("RMS Lim/Exp");
	mntm_rms_lim_exp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new rms_lim_expCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_dynamics.add(mntm_rms_lim_exp);
		
	final JMenuItem mntm_rms_limiter = new JMenuItem("RMS Limiter");
	mntm_rms_limiter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new rms_limiterCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_dynamics.add(mntm_rms_limiter);
		
	final JMenuItem mntm_soft_knee_limiter = new JMenuItem("Soft Knee Limiter");
	mntm_soft_knee_limiter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new soft_knee_limiterCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_dynamics.add(mntm_soft_knee_limiter);
		
	final JMenuItem mntm_slow_gear = new JMenuItem("Slow Gear");
	mntm_slow_gear.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new slow_gearCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_dynamics.add(mntm_slow_gear);
		
	JMenu mn_filters = new JMenu("Filters");
	menuBar.add(mn_filters);
	
	final JMenuItem mntm_LPF1P = new JMenuItem("1P Lowpass");
	mntm_LPF1P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LPF1PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_LPF1P);
		
	final JMenuItem mntm_LPF = new JMenuItem("2P LPF Fixed");
	mntm_LPF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LPFCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_LPF);
		
	final JMenuItem mntm_BPF = new JMenuItem("2P BPF Fixed");
	mntm_BPF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new BPFCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_BPF);
		
	final JMenuItem mntm_HPF = new JMenuItem("2P HPF Fixed");
	mntm_HPF.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new HPFCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_HPF);
		
	final JMenuItem mntm_HPF2P = new JMenuItem("2P Highpass");
	mntm_HPF2P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new HPF2PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_HPF2P);
		
	final JMenuItem mntm_SVF2P = new JMenuItem("2P State Variable");
	mntm_SVF2P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SVF2PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_SVF2P);
		
	final JMenuItem mntm_BiQuad = new JMenuItem("Biquad");
	mntm_BiQuad.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new BiQuadCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_BiQuad);
		
	final JMenuItem mntm_LPF4P = new JMenuItem("4P Lowpass");
	mntm_LPF4P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LPF4PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_LPF4P);
		
	final JMenuItem mntm_OneBandEQ = new JMenuItem("1-Band EQ");
	mntm_OneBandEQ.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new OneBandEQCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_OneBandEQ);
		
	final JMenuItem mntm_SixBandEQ = new JMenuItem("6-Band EQ");
	mntm_SixBandEQ.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SixBandEQCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_SixBandEQ);
		
	JMenu mn_delay = new JMenu("Delay");
	menuBar.add(mn_delay);
	
	final JMenuItem mntm_SingleDelay = new JMenuItem("Single Delay");
	mntm_SingleDelay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SingleDelayCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_SingleDelay);
		
	final JMenuItem mntm_StraightDelay = new JMenuItem("Straight Delay");
	mntm_StraightDelay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new StraightDelayCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_StraightDelay);
		
	final JMenuItem mntm_PingPong = new JMenuItem("Ping Pong");
	mntm_PingPong.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new PingPongCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_PingPong);
		
	final JMenuItem mntm_MultiTap = new JMenuItem("8-Tap");
	mntm_MultiTap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MultiTapCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_MultiTap);
		
	final JMenuItem mntm_MN3011 = new JMenuItem("MN3011");
	mntm_MN3011.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MN3011CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_MN3011);
		
	JMenu mn_reverb = new JMenu("Reverb");
	menuBar.add(mn_reverb);
	
	final JMenuItem mntm_DiscoReverb = new JMenuItem("Disco Reverb");
	mntm_DiscoReverb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new DiscoReverbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_DiscoReverb);
		
	final JMenuItem mntm_gated_verb = new JMenuItem("Gated Reverb");
	mntm_gated_verb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new gated_verbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_gated_verb);
		
	final JMenuItem mntm_dance_ir_fla_l = new JMenuItem("Infinite Rev/Flange");
	mntm_dance_ir_fla_l.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new dance_ir_fla_lCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_dance_ir_fla_l);
		
	final JMenuItem mntm_very_tight_plate_verb = new JMenuItem("Plate Verb 3");
	mntm_very_tight_plate_verb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new very_tight_plate_verbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_very_tight_plate_verb);
		
	final JMenuItem mntm_GtrReverbChorus = new JMenuItem("Reverb/Chorus");
	mntm_GtrReverbChorus.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new GtrReverbChorusCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_GtrReverbChorus);
		
	final JMenuItem mntm_rom_rev1 = new JMenuItem("ROM Reverb 1");
	mntm_rom_rev1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new rom_rev1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_rom_rev1);
		
	final JMenuItem mntm_shimmer_verb = new JMenuItem("Shimmer Reverb");
	mntm_shimmer_verb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new shimmer_verbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_shimmer_verb);
		
	final JMenuItem mntm_MinReverb = new JMenuItem("Small Reverb");
	mntm_MinReverb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MinReverbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_MinReverb);
		
	JMenu mn_modulation = new JMenu("Modulation");
	menuBar.add(mn_modulation);
	
	final JMenuItem mntm_Chorus = new JMenuItem("Chorus");
	mntm_Chorus.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ChorusCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_Chorus);
		
	final JMenuItem mntm_RichChorus = new JMenuItem("Rich Chorus");
	mntm_RichChorus.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new RichChorusCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_RichChorus);
		
	final JMenuItem mntm_ChorusPreset = new JMenuItem("Preset Chorus");
	mntm_ChorusPreset.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ChorusPresetCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_ChorusPreset);
		
	final JMenuItem mntm_Flanger = new JMenuItem("Flanger");
	mntm_Flanger.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new FlangerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_Flanger);
		
	final JMenuItem mntm_ModDelay = new JMenuItem("Mod Delay");
	mntm_ModDelay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ModDelayCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_ModDelay);
		
	final JMenuItem mntm_Phaser = new JMenuItem("Phaser");
	mntm_Phaser.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new PhaserCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_Phaser);
		
	final JMenuItem mntm_Test_chorus = new JMenuItem("Test Chorus");
	mntm_Test_chorus.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Test_chorusCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_Test_chorus);
		
	final JMenuItem mntm_Tremolo = new JMenuItem("Tremolo");
	mntm_Tremolo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new TremoloCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_Tremolo);
		
	final JMenuItem mntm_RingMod = new JMenuItem("Ring Modulator");
	mntm_RingMod.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new RingModCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_RingMod);
		
	JMenu mn_pitch = new JMenu("Pitch");
	menuBar.add(mn_pitch);
	
	final JMenuItem mntm_PitchUp = new JMenuItem("Pitch Up");
	mntm_PitchUp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new PitchUpCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_PitchUp);
		
	final JMenuItem mntm_PitchUpDown = new JMenuItem("Pitch Up/Down");
	mntm_PitchUpDown.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new PitchUpDownCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_PitchUpDown);
		
	JMenu mn_guitar = new JMenu("Guitar");
	menuBar.add(mn_guitar);
	
	final JMenuItem mntm_ga_demo_chorus = new JMenuItem("GA Chorus");
	mntm_ga_demo_chorus.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ga_demo_chorusCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_guitar.add(mntm_ga_demo_chorus);
		
	final JMenuItem mntm_ga_demo_echo_repeat = new JMenuItem("GA Echo Repeat");
	mntm_ga_demo_echo_repeat.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ga_demo_echo_repeatCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_guitar.add(mntm_ga_demo_echo_repeat);
		
	final JMenuItem mntm_ga_demo_flanger = new JMenuItem("GA Flanger");
	mntm_ga_demo_flanger.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ga_demo_flangerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_guitar.add(mntm_ga_demo_flanger);
		
	final JMenuItem mntm_ga_demo_phaser = new JMenuItem("GA Phaser");
	mntm_ga_demo_phaser.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ga_demo_phaserCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_guitar.add(mntm_ga_demo_phaser);
		
	final JMenuItem mntm_ga_demo_vibrato = new JMenuItem("GA Vibrato");
	mntm_ga_demo_vibrato.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ga_demo_vibratoCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_guitar.add(mntm_ga_demo_vibrato);
		
	final JMenuItem mntm_ga_demo_wah = new JMenuItem("GA Wah");
	mntm_ga_demo_wah.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ga_demo_wahCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_guitar.add(mntm_ga_demo_wah);
		
	JMenu mn_pots = new JMenu("Pots");
	menuBar.add(mn_pots);
	
	final JMenuItem mntm_Pot0 = new JMenuItem("Pot 0");
	mntm_Pot0.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Pot0CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pots.add(mntm_Pot0);
		
	final JMenuItem mntm_Pot1 = new JMenuItem("Pot 1");
	mntm_Pot1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Pot1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pots.add(mntm_Pot1);
		
	final JMenuItem mntm_Pot2 = new JMenuItem("Pot 2");
	mntm_Pot2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Pot2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pots.add(mntm_Pot2);
		
	JMenu mn_control = new JMenu("Control");
	menuBar.add(mn_control);
	
	final JMenuItem mntm_SinCosLFO = new JMenuItem("Sin/Cos LFO");
	mntm_SinCosLFO.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SinCosLFOCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_SinCosLFO);
		
	final JMenuItem mntm_RampLFO = new JMenuItem("Ramp LFO");
	mntm_RampLFO.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new RampLFOCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_RampLFO);
		
	final JMenuItem mntm_EnvelopeControl = new JMenuItem("Envelope");
	mntm_EnvelopeControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new EnvelopeControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_EnvelopeControl);
		
	final JMenuItem mntm_PowerControl = new JMenuItem("Power");
	mntm_PowerControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new PowerControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_PowerControl);
		
	final JMenuItem mntm_ClipControl = new JMenuItem("Clip");
	mntm_ClipControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ClipControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ClipControl);
		
	final JMenuItem mntm_InvertControl = new JMenuItem("Invert");
	mntm_InvertControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new InvertControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_InvertControl);
		
	final JMenuItem mntm_ScaleOffsetControl = new JMenuItem("Scale/Offset");
	mntm_ScaleOffsetControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ScaleOffsetControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ScaleOffsetControl);
		
	final JMenuItem mntm_ExponentialControl = new JMenuItem("Log");
	mntm_ExponentialControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ExponentialControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ExponentialControl);
		
	final JMenuItem mntm_ControlMixer = new JMenuItem("Control Mixer");
	mntm_ControlMixer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ControlMixerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ControlMixer);
		
	final JMenuItem mntm_TapTempo = new JMenuItem("Tap Tempo");
	mntm_TapTempo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new TapTempoCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_TapTempo);
		
	}
}
