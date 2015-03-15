	package com.holycityaudio.SpinCAD;
	import com.holycityaudio.SpinCAD.SpinCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.VolumeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.GainBoostCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer2_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer_2_to_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer2_1x2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer3_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer4_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.crossfadeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.aliaser_02CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.BitCrusherCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.CubeGainCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.DistortionCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OctaveCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OverdriveCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ToverXCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.noise_block24CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.noise_amzCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rms_lim_expCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rms_limiterCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.soft_knee_limiterCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LPF1PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Shelving_lowpassCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.HPF1PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Shelving_HipassCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.HPF2PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SVF2PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LPF4PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.NotchCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OneBandEQCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SixBandEQCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SingleDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ServoDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.TripleTapCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MN3011aCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.eighttapCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.tentapCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.gated_verbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rom_rev1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rom_rev2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.reverb_plateCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MinReverbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.reverbACADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.shimmer_verbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ChorusCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.FlangerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ModDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PhaserCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RingModCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PitchShiftFixedCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.pitchupdownCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.pitch_fourCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.pitchoffsetCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot0CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ConstantCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SinCosLFOCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RampLFOCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OscillatorCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SampleHoldCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PatternGeneratorCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.tremolizerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.EnvelopeControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PowerControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RootCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.AbsaCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ExpCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LogCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ScaleOffsetControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ControlMixerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.control_smootherACADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ClipControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.InvertControlCADBlock;
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
		
	final JMenuItem mntm_GainBoost = new JMenuItem("Gain Boost");
	mntm_GainBoost.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new GainBoostCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_GainBoost);
		
	final JMenuItem mntm_Mixer2_1 = new JMenuItem("2:1 Mixer");
	mntm_Mixer2_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer2_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer2_1);
		
	final JMenuItem mntm_Mixer_2_to_1 = new JMenuItem("2:1 Mixer test");
	mntm_Mixer_2_to_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer_2_to_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer_2_to_1);
		
	final JMenuItem mntm_Mixer2_1x2 = new JMenuItem("2:1 (x2) Mixer");
	mntm_Mixer2_1x2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer2_1x2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer2_1x2);
		
	final JMenuItem mntm_Mixer3_1 = new JMenuItem("3:1 Mixer");
	mntm_Mixer3_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer3_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer3_1);
		
	final JMenuItem mntm_Mixer4_1 = new JMenuItem("4:1 Mixer");
	mntm_Mixer4_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer4_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer4_1);
		
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
	
	final JMenuItem mntm_aliaser_02 = new JMenuItem("Aliaser");
	mntm_aliaser_02.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new aliaser_02CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_aliaser_02);
		
	final JMenuItem mntm_BitCrusher = new JMenuItem("Quantizer");
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
		
	final JMenuItem mntm_noise_block24 = new JMenuItem("Noise");
	mntm_noise_block24.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new noise_block24CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_noise_block24);
		
	final JMenuItem mntm_noise_amz = new JMenuItem("Noise AMZ");
	mntm_noise_amz.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new noise_amzCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_noise_amz);
		
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
		
	final JMenuItem mntm_Shelving_lowpass = new JMenuItem("Shelving Lowpass");
	mntm_Shelving_lowpass.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Shelving_lowpassCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_Shelving_lowpass);
		
	final JMenuItem mntm_HPF1P = new JMenuItem("1P Hipass");
	mntm_HPF1P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new HPF1PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_HPF1P);
		
	final JMenuItem mntm_Shelving_Hipass = new JMenuItem("Shelving Hipass");
	mntm_Shelving_Hipass.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Shelving_HipassCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_Shelving_Hipass);
		
	final JMenuItem mntm_HPF2P = new JMenuItem("2P/4P Highpass");
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
		
	final JMenuItem mntm_LPF4P = new JMenuItem("2P/4P Lowpass");
	mntm_LPF4P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LPF4PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_LPF4P);
		
	final JMenuItem mntm_Notch = new JMenuItem("Notch");
	mntm_Notch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new NotchCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_Notch);
		
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
		
	final JMenuItem mntm_ServoDelay = new JMenuItem("Servo Delay");
	mntm_ServoDelay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ServoDelayCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_ServoDelay);
		
	final JMenuItem mntm_TripleTap = new JMenuItem("Triple-tap");
	mntm_TripleTap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new TripleTapCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_TripleTap);
		
	final JMenuItem mntm_MN3011a = new JMenuItem("MN3011");
	mntm_MN3011a.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MN3011aCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_MN3011a);
		
	final JMenuItem mntm_eighttap = new JMenuItem("8-Tap");
	mntm_eighttap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new eighttapCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_eighttap);
		
	final JMenuItem mntm_tentap = new JMenuItem("10-Tap Stereo");
	mntm_tentap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new tentapCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_tentap);
		
	JMenu mn_reverb = new JMenu("Reverb");
	menuBar.add(mn_reverb);
	
	final JMenuItem mntm_gated_verb = new JMenuItem("Gated Reverb");
	mntm_gated_verb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new gated_verbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_gated_verb);
		
	final JMenuItem mntm_rom_rev1 = new JMenuItem("ROM Reverb 1");
	mntm_rom_rev1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new rom_rev1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_rom_rev1);
		
	final JMenuItem mntm_rom_rev2 = new JMenuItem("ROM Reverb 2");
	mntm_rom_rev2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new rom_rev2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_rom_rev2);
		
	final JMenuItem mntm_reverb_plate = new JMenuItem("Plate Reverb");
	mntm_reverb_plate.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new reverb_plateCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_reverb_plate);
		
	final JMenuItem mntm_MinReverb = new JMenuItem("Small Reverb");
	mntm_MinReverb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MinReverbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_MinReverb);
		
	final JMenuItem mntm_reverbA = new JMenuItem("Adjustable Reverb");
	mntm_reverbA.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new reverbACADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_reverbA);
		
	final JMenuItem mntm_shimmer_verb = new JMenuItem("Shimmer Reverb");
	mntm_shimmer_verb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new shimmer_verbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_shimmer_verb);
		
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
	
	final JMenuItem mntm_PitchShiftFixed = new JMenuItem("Pitch Shift Fixed");
	mntm_PitchShiftFixed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new PitchShiftFixedCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_PitchShiftFixed);
		
	final JMenuItem mntm_pitchupdown = new JMenuItem("Pitch Up/Down");
	mntm_pitchupdown.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new pitchupdownCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_pitchupdown);
		
	final JMenuItem mntm_pitch_four = new JMenuItem("Pitch Four");
	mntm_pitch_four.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new pitch_fourCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_pitch_four);
		
	final JMenuItem mntm_pitchoffset = new JMenuItem("Pitch Offset");
	mntm_pitchoffset.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new pitchoffsetCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_pitchoffset);
		
	JMenu mn_control = new JMenu("Control");
	menuBar.add(mn_control);
	
	final JMenuItem mntm_Pot0 = new JMenuItem("Pot 0");
	mntm_Pot0.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Pot0CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Pot0);
		
	final JMenuItem mntm_Pot1 = new JMenuItem("Pot 1");
	mntm_Pot1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Pot1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Pot1);
		
	final JMenuItem mntm_Pot2 = new JMenuItem("Pot 2");
	mntm_Pot2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Pot2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Pot2);
		
	final JMenuItem mntm_Constant = new JMenuItem("Constant");
	mntm_Constant.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ConstantCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Constant);
		
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
		
	final JMenuItem mntm_Oscillator = new JMenuItem("Oscillator");
	mntm_Oscillator.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new OscillatorCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Oscillator);
		
	final JMenuItem mntm_SampleHold = new JMenuItem("Sample/Hold");
	mntm_SampleHold.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SampleHoldCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_SampleHold);
		
	final JMenuItem mntm_PatternGenerator = new JMenuItem("Pattern Gen");
	mntm_PatternGenerator.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new PatternGeneratorCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_PatternGenerator);
		
	final JMenuItem mntm_tremolizer = new JMenuItem("Tremolizer");
	mntm_tremolizer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new tremolizerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_tremolizer);
		
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
		
	final JMenuItem mntm_Root = new JMenuItem("Root");
	mntm_Root.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new RootCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Root);
		
	final JMenuItem mntm_Absa = new JMenuItem("Absolute Value");
	mntm_Absa.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new AbsaCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Absa);
		
	final JMenuItem mntm_Exp = new JMenuItem("Exp");
	mntm_Exp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ExpCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Exp);
		
	final JMenuItem mntm_Log = new JMenuItem("Log");
	mntm_Log.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LogCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Log);
		
	final JMenuItem mntm_ScaleOffsetControl = new JMenuItem("Scale/Offset");
	mntm_ScaleOffsetControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ScaleOffsetControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ScaleOffsetControl);
		
	final JMenuItem mntm_ControlMixer = new JMenuItem("Control Mixer");
	mntm_ControlMixer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ControlMixerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ControlMixer);
		
	final JMenuItem mntm_control_smootherA = new JMenuItem("Smoother");
	mntm_control_smootherA.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new control_smootherACADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_control_smootherA);
		
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
