/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * servoControlPanel.java
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
		import com.holycityaudio.SpinCAD.CADBlocks.servoCADBlock;

		public class servoControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private servoCADBlock gCB;
		// declare the controls
			JSlider inputGainSlider;
			JLabel  inputGainLabel;	
			JSlider fbkGainSlider;
			JLabel  fbkGainLabel;	
			JSlider servoGainSlider;
			JLabel  servoGainLabel;	
			JSlider freqSlider;
			JLabel  freqLabel;	
			JSlider tap1RatioSlider;
			JLabel  tap1RatioLabel;	
			private JComboBox <String> lfoSelComboBox; 

		public servoControlPanel(servoCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Servo_Flanger");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			// dB level slider goes in steps of 1 dB
				inputGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getinputGain())));
				inputGainSlider.addChangeListener(new servoSliderListener());
				inputGainLabel = new JLabel();
				updateinputGainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(inputGainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(inputGainSlider);		
			
			// dB level slider goes in steps of 1 dB
				fbkGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getfbkGain())));
				fbkGainSlider.addChangeListener(new servoSliderListener());
				fbkGainLabel = new JLabel();
				updatefbkGainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(fbkGainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(fbkGainSlider);		
			
			servoGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.49 * 100.0), (int) (gCB.getservoGain() * 100.0));
				servoGainSlider.addChangeListener(new servoSliderListener());
				servoGainLabel = new JLabel();
				updateservoGainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(servoGainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(servoGainSlider);		
			
			//				freqSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(500) * 100.0),(int) (Math.log10(7500) * 100.0), (int) (Math.log10(gCB.getfreq()) * 100));
							freqSlider = gCB.LogFilterSlider(500,7500,gCB.getfreq());
				freqSlider.addChangeListener(new servoSliderListener());
				freqLabel = new JLabel();
				updatefreqLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(freqLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(freqSlider);		
			
			tap1RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.001 * 1000.0),(int) (0.05 * 1000.0), (int) (gCB.gettap1Ratio() * 1000.0));
				tap1RatioSlider.addChangeListener(new servoSliderListener());
				tap1RatioLabel = new JLabel();
				updatetap1RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1RatioSlider);		
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new servoActionListener());
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
		class servoSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/1.0));
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/1.0));
				updatefbkGainLabel();
			}
			if(ce.getSource() == servoGainSlider) {
			gCB.setservoGain((double) (servoGainSlider.getValue()/100.0));
				updateservoGainLabel();
			}
			if(ce.getSource() == freqSlider) {
			gCB.setfreq((double) gCB.freqToFilt(gCB.sliderToLogval((int)(freqSlider.getValue()), 100.0)));
				updatefreqLabel();
			}
			if(ce.getSource() == tap1RatioSlider) {
			gCB.settap1Ratio((double) (tap1RatioSlider.getValue()/1000.0));
				updatetap1RatioLabel();
			}
			}
		}

		// add item listener 
		class servoItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class servoActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updateinputGainLabel() {
		inputGainLabel.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));		
		}		
		private void updatefbkGainLabel() {
		fbkGainLabel.setText("Feedback_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));		
		}		
		private void updateservoGainLabel() {
		servoGainLabel.setText("Servo_Gain " + String.format("%4.2f", gCB.getservoGain()));		
		}		
		private void updatefreqLabel() {
		//				kflLabel.setText("HF damping freq 1:" + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
						freqLabel.setText("Low_Pass " + String.format("%4.1f", gCB.filtToFreq(gCB.getfreq())) + " Hz");		
		}		
		private void updatetap1RatioLabel() {
		tap1RatioLabel.setText("Tap_1_Time " + String.format("%4.3f", gCB.gettap1Ratio()));		
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
