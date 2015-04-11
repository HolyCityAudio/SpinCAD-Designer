/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rom_rev2ControlPanel.java
 * Copyright (C) 2015 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick 
 * 
 *   This program is free software: you can redistribute it and/or modify 
 *   it under the terms of the GNU General Public License as published by 
 *   the Free Software Foundation, either version 3 of the License, or 
 *   (at your option) any later version. 
 * 
 *   This program is distributed in the hope that it will be useful, 
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 *   GNU General Public License for more details. 
 * 
 *   You should have received a copy of the GNU General Public License 
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>. 
 *     
 */ 
		package com.holycityaudio.SpinCAD.ControlPanel;
		import javax.swing.JFrame;
		import javax.swing.SwingUtilities;
		import javax.swing.event.ChangeEvent;
		import javax.swing.event.ChangeListener;
		import java.awt.event.ActionEvent;
		import java.awt.event.WindowEvent;
		import java.awt.event.WindowListener;
		import java.awt.event.ItemEvent;
		import javax.swing.BoxLayout;
		import javax.swing.JSlider;
		import javax.swing.JSpinner;
		import javax.swing.JLabel;
		import javax.swing.JCheckBox;
		import javax.swing.JComboBox;
		import javax.swing.Box;
		import java.awt.Dimension;
		import com.holycityaudio.SpinCAD.spinCADControlPanel;
		import com.holycityaudio.SpinCAD.CADBlocks.rom_rev2CADBlock;

		public class rom_rev2ControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private rom_rev2CADBlock gCB;
		// declare the controls
			JSlider gainSlider;
			JLabel  gainLabel;	
			JSlider revTimeMaxSlider;
			JLabel  revTimeMaxLabel;	
			JSlider kapiSlider;
			JLabel  kapiLabel;	
			JSlider kapd1Slider;
			JLabel  kapd1Label;	
			JSlider kapd2Slider;
			JLabel  kapd2Label;	
			JSlider kflSlider;
			JLabel  kflLabel;	
			JSlider kfhSlider;
			JLabel  kfhLabel;	
			JSlider memscaleSlider;
			JLabel  memscaleLabel;	

		public rom_rev2ControlPanel(rom_rev2CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("rom_rev2");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			// dB level slider goes in steps of 1 dB
				gainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0.0), (int) (20 * Math.log10(gCB.getgain())));
				gainSlider.addChangeListener(new rom_rev2SliderListener());
				gainLabel = new JLabel();
				updategainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(gainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(gainSlider);		
			
			revTimeMaxSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.9 * 100.0), (int) (gCB.getrevTimeMax() * 100.0));
				revTimeMaxSlider.addChangeListener(new rom_rev2SliderListener());
				revTimeMaxLabel = new JLabel();
				updaterevTimeMaxLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(revTimeMaxLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(revTimeMaxSlider);		
			
			kapiSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapi() * 100.0));
				kapiSlider.addChangeListener(new rom_rev2SliderListener());
				kapiLabel = new JLabel();
				updatekapiLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapiLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapiSlider);		
			
			kapd1Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapd1() * 100.0));
				kapd1Slider.addChangeListener(new rom_rev2SliderListener());
				kapd1Label = new JLabel();
				updatekapd1Label();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapd1Label);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapd1Slider);		
			
			kapd2Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapd2() * 100.0));
				kapd2Slider.addChangeListener(new rom_rev2SliderListener());
				kapd2Label = new JLabel();
				updatekapd2Label();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapd2Label);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapd2Slider);		
			
			//				kflSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(500) * 100.0),(int) (Math.log10(5000) * 100.0), (int) (Math.log10(gCB.getkfl()) * 100));
							kflSlider = gCB.LogFilterSlider(500,5000,gCB.getkfl());
				kflSlider.addChangeListener(new rom_rev2SliderListener());
				kflLabel = new JLabel();
				updatekflLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kflLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kflSlider);		
			
			//				kfhSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(40) * 100.0),(int) (Math.log10(1000) * 100.0), (int) (Math.log10(gCB.getkfh()) * 100));
							kfhSlider = gCB.LogFilterSlider(40,1000,gCB.getkfh());
				kfhSlider.addChangeListener(new rom_rev2SliderListener());
				kfhLabel = new JLabel();
				updatekfhLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kfhLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kfhSlider);		
			
			memscaleSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.5 * 10.0),(int) (1.0 * 10.0), (int) (gCB.getmemscale() * 10.0));
				memscaleSlider.addChangeListener(new rom_rev2SliderListener());
				memscaleLabel = new JLabel();
				updatememscaleLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(memscaleLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(memscaleSlider);		
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setAlwaysOnTop(true);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders 
		class rom_rev2SliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));
				updategainLabel();
			}
			if(ce.getSource() == revTimeMaxSlider) {
			gCB.setrevTimeMax((double) (revTimeMaxSlider.getValue()/100.0));
				updaterevTimeMaxLabel();
			}
			if(ce.getSource() == kapiSlider) {
			gCB.setkapi((double) (kapiSlider.getValue()/100.0));
				updatekapiLabel();
			}
			if(ce.getSource() == kapd1Slider) {
			gCB.setkapd1((double) (kapd1Slider.getValue()/100.0));
				updatekapd1Label();
			}
			if(ce.getSource() == kapd2Slider) {
			gCB.setkapd2((double) (kapd2Slider.getValue()/100.0));
				updatekapd2Label();
			}
			if(ce.getSource() == kflSlider) {
			gCB.setkfl((double) gCB.freqToFilt(gCB.sliderToLogval((int)(kflSlider.getValue()), 100.0)));
				updatekflLabel();
			}
			if(ce.getSource() == kfhSlider) {
			gCB.setkfh((double) gCB.freqToFilt(gCB.sliderToLogval((int)(kfhSlider.getValue()), 100.0)));
				updatekfhLabel();
			}
			if(ce.getSource() == memscaleSlider) {
			gCB.setmemscale((double) (memscaleSlider.getValue()/10.0));
				updatememscaleLabel();
			}
			}
		}

		// add item listener 
		class rom_rev2ItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class rom_rev2ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updaterevTimeMaxLabel() {
		revTimeMaxLabel.setText("Reverb_Time " + String.format("%4.2f", gCB.getrevTimeMax()));		
		}		
		private void updatekapiLabel() {
		kapiLabel.setText("Input_All_Pass_Gain " + String.format("%4.2f", gCB.getkapi()));		
		}		
		private void updatekapd1Label() {
		kapd1Label.setText("Delay_All_Pass_1_Gain " + String.format("%4.2f", gCB.getkapd1()));		
		}		
		private void updatekapd2Label() {
		kapd2Label.setText("Delay_All_Pass_2_Gain " + String.format("%4.2f", gCB.getkapd2()));		
		}		
		private void updatekflLabel() {
		//				kflLabel.setText("HF damping freq 1:" + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
						kflLabel.setText("Low_Pass " + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
		}		
		private void updatekfhLabel() {
		//				kflLabel.setText("HF damping freq 1:" + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
						kfhLabel.setText("High_Pass " + String.format("%4.1f", gCB.filtToFreq(gCB.getkfh())) + " Hz");		
		}		
		private void updatememscaleLabel() {
		memscaleLabel.setText("Delay_Scale " + String.format("%4.1f", gCB.getmemscale()));		
		}		
		
		class MyWindowListener implements WindowListener
		{
		@Override
			public void windowActivated(WindowEvent arg0) {
			}

		@Override
			public void windowClosed(WindowEvent arg0) {
			}

		@Override
			public void windowClosing(WindowEvent arg0) {
				gCB.clearCP();
			}

		@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
		}

		@Override
		public void windowIconified(WindowEvent arg0) {

		}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
		}
		
	}
