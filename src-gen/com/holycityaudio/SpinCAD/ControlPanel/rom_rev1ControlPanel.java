/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rom_rev1ControlPanel.java
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
		import javax.swing.JLabel;
		import javax.swing.JCheckBox;
		import javax.swing.JComboBox;
		import javax.swing.Box;
		import java.awt.Dimension;
		import com.holycityaudio.SpinCAD.spinCADControlPanel;
		import com.holycityaudio.SpinCAD.CADBlocks.rom_rev1CADBlock;

		public class rom_rev1ControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private rom_rev1CADBlock gCB;
		// declare the controls
			JSlider gainSlider;
			JLabel  gainLabel;	
			JSlider kiapSlider;
			JLabel  kiapLabel;	
			JSlider nDLsSlider;
			JLabel  nDLsLabel;	
			JSlider kapd1Slider;
			JLabel  kapd1Label;	
			JSlider kflSlider;
			JLabel  kflLabel;	
			JSlider kfhSlider;
			JLabel  kfhLabel;	

		public rom_rev1ControlPanel(rom_rev1CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("ROM_Reverb_1");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			// dB level slider goes in steps of 1 dB
				gainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0.0), (int) (20 * Math.log10(gCB.getgain())));
				gainSlider.addChangeListener(new rom_rev1SliderListener());
				gainLabel = new JLabel();
				updategainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(gainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(gainSlider);		
			
			kiapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.25 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkiap() * 100.0));
				kiapSlider.addChangeListener(new rom_rev1SliderListener());
				kiapLabel = new JLabel();
				updatekiapLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kiapLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kiapSlider);		
			
			nDLsSlider = new JSlider(JSlider.HORIZONTAL, (int)(2 * 1.0),(int) (4 * 1.0), (int) (gCB.getnDLs() * 1.0));
				nDLsSlider.addChangeListener(new rom_rev1SliderListener());
				nDLsLabel = new JLabel();
				updatenDLsLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(nDLsLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(nDLsSlider);		
			
			kapd1Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapd1() * 100.0));
				kapd1Slider.addChangeListener(new rom_rev1SliderListener());
				kapd1Label = new JLabel();
				updatekapd1Label();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapd1Label);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kapd1Slider);		
			
			//				kflSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(500) * 100.0),(int) (Math.log10(5000) * 100.0), (int) (Math.log10(gCB.getkfl()) * 100));
							kflSlider = gCB.LogFilterSlider(500,5000,gCB.getkfl());
				kflSlider.addChangeListener(new rom_rev1SliderListener());
				kflLabel = new JLabel();
				updatekflLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kflLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kflSlider);		
			
			//				kfhSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(40) * 100.0),(int) (Math.log10(1000) * 100.0), (int) (Math.log10(gCB.getkfh()) * 100));
							kfhSlider = gCB.LogFilterSlider(40,1000,gCB.getkfh());
				kfhSlider.addChangeListener(new rom_rev1SliderListener());
				kfhLabel = new JLabel();
				updatekfhLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kfhLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kfhSlider);		
				frame.addWindowListener(new MyWindowListener());
				frame.setVisible(true);		
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setAlwaysOnTop(true);
			}
		});
		}

		// add change listener for Sliders 
		class rom_rev1SliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));
				updategainLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			if(ce.getSource() == nDLsSlider) {
			gCB.setnDLs((double) (nDLsSlider.getValue()/1.0));
				updatenDLsLabel();
			}
			if(ce.getSource() == kapd1Slider) {
			gCB.setkapd1((double) (kapd1Slider.getValue()/100.0));
				updatekapd1Label();
			}
			if(ce.getSource() == kflSlider) {
			gCB.setkfl((double) gCB.freqToFilt(gCB.sliderToLogval((int)(kflSlider.getValue()), 100.0)));
				updatekflLabel();
			}
			if(ce.getSource() == kfhSlider) {
			gCB.setkfh((double) gCB.freqToFilt(gCB.sliderToLogval((int)(kfhSlider.getValue()), 100.0)));
				updatekfhLabel();
			}
			}
		}

		// add item listener 
		class rom_rev1ItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class rom_rev1ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatekiapLabel() {
		kiapLabel.setText("Input_All_Pass " + String.format("%4.2f", gCB.getkiap()));		
		}		
		private void updatenDLsLabel() {
		nDLsLabel.setText("Delay_Stages " + String.format("%4.0f", gCB.getnDLs()));		
		}		
		private void updatekapd1Label() {
		kapd1Label.setText("Delay_All_Pass_1_Gain " + String.format("%4.2f", gCB.getkapd1()));		
		}		
		private void updatekflLabel() {
		//				kflLabel.setText("HF damping freq 1:" + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
						kflLabel.setText("Low_Pass " + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
		}		
		private void updatekfhLabel() {
		//				kflLabel.setText("HF damping freq 1:" + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
						kfhLabel.setText("High_Pass " + String.format("%4.1f", gCB.filtToFreq(gCB.getkfh())) + " Hz");		
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
