	package com.holycityaudio.SpinCAD;
	import com.holycityaudio.SpinCAD.SpinCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.VolumeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.GainBoostCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Phase_InvertCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.crossfadeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.crossfade_2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.crossfade_3CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.pannerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer_2_to_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer_4_to_2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer_3_to_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Mixer_4_to_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.AliaserCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.CubeGainCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.DistortionCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.noise_amzCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OctaveCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OverdriveCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.QuantizerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ToverXCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rms_lim_expCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rms_limiterCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.soft_knee_limiterCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LPF_RDFXCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Shelving_lowpassCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.HPF_RDFXCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Shelving_HipassCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SVF2PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SVF_2P_adjustableCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LPF4PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.HPF2PCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.NotchCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OneBandEQCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SixBandEQCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.TripleTapCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MN3011aCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.DrumDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.sixtapCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.eighttapCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.StutterCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ReverseDelayCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.allpassCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ChirpCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MinReverbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MinReverb2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.reverb_roomCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.reverb_hallCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rom_rev1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.rom_rev2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.reverbCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ChorusCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ChorusQuadCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.FlangerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.servoCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PhaserCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RingModCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PitchShiftFixedCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pitch_shift_testCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.pitchupdownCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Glitch_shiftCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.pitchoffsetCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.pitchoffset1_2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot0CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Pot2CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.control_smootherCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SinCosLFOACADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LFO_ValueCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RampLFOCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.OscillatorCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.New_OscillatorCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SampleHoldCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.tremolizerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.InvertControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.PowerControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ClipControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.SlicerCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Two_StageCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.VeeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.EnvelopeControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.New_EnvelopeCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ControlMixer_2_to_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ControlMixer_3_to_1CADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.TapTempoCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ScaleOffsetControlCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.maxxCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.MultiplyCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.Half_WaveCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.AbsaCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ExpCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.LogCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.RootCADBlock;
	import com.holycityaudio.SpinCAD.CADBlocks.ConstantCADBlock;

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
		
	final JMenuItem mntm_Phase_Invert = new JMenuItem("Phase Invert");
	mntm_Phase_Invert.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Phase_InvertCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Phase_Invert);
		
	final JMenuItem mntm_crossfade = new JMenuItem("Crossfade");
	mntm_crossfade.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new crossfadeCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_crossfade);
		
	final JMenuItem mntm_crossfade_2 = new JMenuItem("Crossfade 2");
	mntm_crossfade_2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new crossfade_2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_crossfade_2);
		
	final JMenuItem mntm_crossfade_3 = new JMenuItem("Crossfade 3");
	mntm_crossfade_3.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new crossfade_3CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_crossfade_3);
		
	final JMenuItem mntm_panner = new JMenuItem("Panner");
	mntm_panner.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new pannerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_panner);
		
	final JMenuItem mntm_Mixer_2_to_1 = new JMenuItem("2:1 Mixer");
	mntm_Mixer_2_to_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer_2_to_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer_2_to_1);
		
	final JMenuItem mntm_Mixer_4_to_2 = new JMenuItem("2:1 (x2) Mixer");
	mntm_Mixer_4_to_2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer_4_to_2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer_4_to_2);
		
	final JMenuItem mntm_Mixer_3_to_1 = new JMenuItem("3:1 Mixer");
	mntm_Mixer_3_to_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer_3_to_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer_3_to_1);
		
	final JMenuItem mntm_Mixer_4_to_1 = new JMenuItem("4:1 Mixer");
	mntm_Mixer_4_to_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Mixer_4_to_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_io_mix.add(mntm_Mixer_4_to_1);
		
	JMenu mn_waveshaper = new JMenu("Wave Shaper");
	menuBar.add(mn_waveshaper);
	
	final JMenuItem mntm_Aliaser = new JMenuItem("Aliaser");
	mntm_Aliaser.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new AliaserCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_Aliaser);
		
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
		
	final JMenuItem mntm_noise_amz = new JMenuItem("Noise AMZ");
	mntm_noise_amz.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new noise_amzCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_noise_amz);
		
	final JMenuItem mntm_Octave = new JMenuItem("Octave Fuzz");
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
		
	final JMenuItem mntm_Quantizer = new JMenuItem("Quantizer");
	mntm_Quantizer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new QuantizerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_Quantizer);
		
	final JMenuItem mntm_ToverX = new JMenuItem("T/X");
	mntm_ToverX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ToverXCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_waveshaper.add(mntm_ToverX);
		
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
	
	final JMenuItem mntm_LPF_RDFX = new JMenuItem("1P Lowpass");
	mntm_LPF_RDFX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LPF_RDFXCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_LPF_RDFX);
		
	final JMenuItem mntm_Shelving_lowpass = new JMenuItem("Shelving Lowpass");
	mntm_Shelving_lowpass.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Shelving_lowpassCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_Shelving_lowpass);
		
	final JMenuItem mntm_HPF_RDFX = new JMenuItem("1P Hipass");
	mntm_HPF_RDFX.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new HPF_RDFXCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_HPF_RDFX);
		
	final JMenuItem mntm_Shelving_Hipass = new JMenuItem("Shelving Hipass");
	mntm_Shelving_Hipass.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Shelving_HipassCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_Shelving_Hipass);
		
	final JMenuItem mntm_SVF2P = new JMenuItem("2P SVF Fixed Q");
	mntm_SVF2P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SVF2PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_SVF2P);
		
	final JMenuItem mntm_SVF_2P_adjustable = new JMenuItem("2P SVF Adjustable");
	mntm_SVF_2P_adjustable.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SVF_2P_adjustableCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_SVF_2P_adjustable);
		
	final JMenuItem mntm_LPF4P = new JMenuItem("2P/4P Lowpass");
	mntm_LPF4P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LPF4PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_LPF4P);
		
	final JMenuItem mntm_HPF2P = new JMenuItem("2P/4P Highpass");
	mntm_HPF2P.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new HPF2PCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_filters.add(mntm_HPF2P);
		
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
	
	final JMenuItem mntm_TripleTap = new JMenuItem("ThreeTap");
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
		
	final JMenuItem mntm_DrumDelay = new JMenuItem("Drum Delay");
	mntm_DrumDelay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new DrumDelayCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_DrumDelay);
		
	final JMenuItem mntm_sixtap = new JMenuItem("6-Tap Stereo");
	mntm_sixtap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new sixtapCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_sixtap);
		
	final JMenuItem mntm_eighttap = new JMenuItem("8-Tap");
	mntm_eighttap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new eighttapCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_eighttap);
		
	final JMenuItem mntm_Stutter = new JMenuItem("Stutter");
	mntm_Stutter.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new StutterCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_Stutter);
		
	final JMenuItem mntm_ReverseDelay = new JMenuItem("Reverse");
	mntm_ReverseDelay.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ReverseDelayCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_delay.add(mntm_ReverseDelay);
		
	JMenu mn_reverb = new JMenu("Reverb");
	menuBar.add(mn_reverb);
	
	final JMenuItem mntm_allpass = new JMenuItem("Allpass");
	mntm_allpass.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new allpassCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_allpass);
		
	final JMenuItem mntm_Chirp = new JMenuItem("Chirp");
	mntm_Chirp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ChirpCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_Chirp);
		
	final JMenuItem mntm_MinReverb = new JMenuItem("Small Reverb");
	mntm_MinReverb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MinReverbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_MinReverb);
		
	final JMenuItem mntm_MinReverb2 = new JMenuItem("Small Reverb 2");
	mntm_MinReverb2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MinReverb2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_MinReverb2);
		
	final JMenuItem mntm_reverb_room = new JMenuItem("Room Reverb");
	mntm_reverb_room.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new reverb_roomCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_reverb_room);
		
	final JMenuItem mntm_reverb_hall = new JMenuItem("Hall Reverb");
	mntm_reverb_hall.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new reverb_hallCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_reverb_hall);
		
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
		
	final JMenuItem mntm_reverb = new JMenuItem("Adjustable Reverb");
	mntm_reverb.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new reverbCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_reverb.add(mntm_reverb);
		
	JMenu mn_modulation = new JMenu("Modulation");
	menuBar.add(mn_modulation);
	
	final JMenuItem mntm_Chorus = new JMenuItem("1-voice Chorus");
	mntm_Chorus.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ChorusCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_Chorus);
		
	final JMenuItem mntm_ChorusQuad = new JMenuItem("4-voice Chorus");
	mntm_ChorusQuad.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ChorusQuadCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_ChorusQuad);
		
	final JMenuItem mntm_Flanger = new JMenuItem("LFO Flanger");
	mntm_Flanger.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new FlangerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_Flanger);
		
	final JMenuItem mntm_servo = new JMenuItem("Servo Flanger");
	mntm_servo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new servoCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_modulation.add(mntm_servo);
		
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
		
	final JMenuItem mntm_Pitch_shift_test = new JMenuItem("Pitch Shift Adjustable");
	mntm_Pitch_shift_test.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Pitch_shift_testCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_Pitch_shift_test);
		
	final JMenuItem mntm_pitchupdown = new JMenuItem("Octave Up/Down");
	mntm_pitchupdown.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new pitchupdownCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_pitchupdown);
		
	final JMenuItem mntm_Glitch_shift = new JMenuItem("Glitch Shift Adjustable");
	mntm_Glitch_shift.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Glitch_shiftCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_Glitch_shift);
		
	final JMenuItem mntm_pitchoffset = new JMenuItem("Pitch Offset");
	mntm_pitchoffset.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new pitchoffsetCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_pitchoffset);
		
	final JMenuItem mntm_pitchoffset1_2 = new JMenuItem("Dual Output Pitch Offset");
	mntm_pitchoffset1_2.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new pitchoffset1_2CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_pitch.add(mntm_pitchoffset1_2);
		
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
		
	final JMenuItem mntm_control_smoother = new JMenuItem("Smoother");
	mntm_control_smoother.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new control_smootherCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_control_smoother);
		
	final JMenuItem mntm_SinCosLFOA = new JMenuItem("Sin/Cos LFO");
	mntm_SinCosLFOA.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SinCosLFOACADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_SinCosLFOA);
		
	final JMenuItem mntm_LFO_Value = new JMenuItem("LFO Value");
	mntm_LFO_Value.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LFO_ValueCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_LFO_Value);
		
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
		
	final JMenuItem mntm_New_Oscillator = new JMenuItem("New Oscillator");
	mntm_New_Oscillator.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new New_OscillatorCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_New_Oscillator);
		
	final JMenuItem mntm_SampleHold = new JMenuItem("Ramp Sample/Hold");
	mntm_SampleHold.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SampleHoldCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_SampleHold);
		
	final JMenuItem mntm_tremolizer = new JMenuItem("Tremolizer");
	mntm_tremolizer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new tremolizerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_tremolizer);
		
	final JMenuItem mntm_InvertControl = new JMenuItem("Invert");
	mntm_InvertControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new InvertControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_InvertControl);
		
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
		
	final JMenuItem mntm_Slicer = new JMenuItem("Slicer");
	mntm_Slicer.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new SlicerCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Slicer);
		
	final JMenuItem mntm_Two_Stage = new JMenuItem("Two Stage");
	mntm_Two_Stage.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Two_StageCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Two_Stage);
		
	final JMenuItem mntm_Vee = new JMenuItem("Vee");
	mntm_Vee.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new VeeCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_Vee);
		
	final JMenuItem mntm_EnvelopeControl = new JMenuItem("Envelope");
	mntm_EnvelopeControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new EnvelopeControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_EnvelopeControl);
		
	final JMenuItem mntm_New_Envelope = new JMenuItem("Envelope II");
	mntm_New_Envelope.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new New_EnvelopeCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_New_Envelope);
		
	final JMenuItem mntm_ControlMixer_2_to_1 = new JMenuItem("Mixer 2:1");
	mntm_ControlMixer_2_to_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ControlMixer_2_to_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ControlMixer_2_to_1);
		
	final JMenuItem mntm_ControlMixer_3_to_1 = new JMenuItem("Mixer 3:1");
	mntm_ControlMixer_3_to_1.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ControlMixer_3_to_1CADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_ControlMixer_3_to_1);
		
	final JMenuItem mntm_TapTempo = new JMenuItem("Tap Tempo");
	mntm_TapTempo.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new TapTempoCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_control.add(mntm_TapTempo);
		
	JMenu mn_instructions = new JMenu("Instructions");
	menuBar.add(mn_instructions);
	
	final JMenuItem mntm_ScaleOffsetControl = new JMenuItem("Scale/Offset");
	mntm_ScaleOffsetControl.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ScaleOffsetControlCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_ScaleOffsetControl);
		
	final JMenuItem mntm_maxx = new JMenuItem("Maximum");
	mntm_maxx.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new maxxCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_maxx);
		
	final JMenuItem mntm_Multiply = new JMenuItem("Multiply");
	mntm_Multiply.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new MultiplyCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_Multiply);
		
	final JMenuItem mntm_Half_Wave = new JMenuItem("Half Wave");
	mntm_Half_Wave.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new Half_WaveCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_Half_Wave);
		
	final JMenuItem mntm_Absa = new JMenuItem("Absolute Value");
	mntm_Absa.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new AbsaCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_Absa);
		
	final JMenuItem mntm_Exp = new JMenuItem("Exp");
	mntm_Exp.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ExpCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_Exp);
		
	final JMenuItem mntm_Log = new JMenuItem("Log");
	mntm_Log.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new LogCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_Log);
		
	final JMenuItem mntm_Root = new JMenuItem("Root");
	mntm_Root.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new RootCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_Root);
		
	final JMenuItem mntm_Constant = new JMenuItem("Constant");
	mntm_Constant.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			SpinCADBlock pcB = new ConstantCADBlock(50, 100);
			f.dropBlock(panel, pcB);
		}
	});
	mn_instructions.add(mntm_Constant);
		
	}
}
