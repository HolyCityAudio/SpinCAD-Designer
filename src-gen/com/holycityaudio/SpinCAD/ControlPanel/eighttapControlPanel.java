/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * eighttapControlPanel.java
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
		import com.holycityaudio.SpinCAD.CADBlocks.eighttapCADBlock;

		public class eighttapControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private eighttapCADBlock gCB;
		// declare the controls
			JSlider inputGainSlider;
			JLabel  inputGainLabel;	
			JSlider fbkGainSlider;
			JLabel  fbkGainLabel;	
			JSlider delayLengthSlider;
			JLabel  delayLengthLabel;	
			JSlider tap1GainSlider;
			JLabel  tap1GainLabel;	
			JSlider tap2GainSlider;
			JLabel  tap2GainLabel;	
			JSlider tap3GainSlider;
			JLabel  tap3GainLabel;	
			JSlider tap4GainSlider;
			JLabel  tap4GainLabel;	
			JSlider tap5GainSlider;
			JLabel  tap5GainLabel;	
			JSlider tap6GainSlider;
			JLabel  tap6GainLabel;	
			JSlider tap7GainSlider;
			JLabel  tap7GainLabel;	
			JSlider tap8GainSlider;
			JLabel  tap8GainLabel;	

		public eighttapControlPanel(eighttapCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Eight_Tap");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			// dB level slider goes in steps of 1 dB
				inputGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getinputGain())));
				inputGainSlider.addChangeListener(new eighttapSliderListener());
				inputGainLabel = new JLabel();
				updateinputGainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(inputGainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(inputGainSlider);		
			
			// dB level slider goes in steps of 1 dB
				fbkGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getfbkGain())));
				fbkGainSlider.addChangeListener(new eighttapSliderListener());
				fbkGainLabel = new JLabel();
				updatefbkGainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(fbkGainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(fbkGainSlider);		
			
			delayLengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (32767 * 1), (int) (gCB.getdelayLength() * 1));
				delayLengthSlider.addChangeListener(new eighttapSliderListener());
				delayLengthLabel = new JLabel();
				updatedelayLengthLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(delayLengthLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(delayLengthSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap1GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap1Gain())));
				tap1GainSlider.addChangeListener(new eighttapSliderListener());
				tap1GainLabel = new JLabel();
				updatetap1GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1GainSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap2GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap2Gain())));
				tap2GainSlider.addChangeListener(new eighttapSliderListener());
				tap2GainLabel = new JLabel();
				updatetap2GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap2GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap2GainSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap3GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap3Gain())));
				tap3GainSlider.addChangeListener(new eighttapSliderListener());
				tap3GainLabel = new JLabel();
				updatetap3GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap3GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap3GainSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap4GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap4Gain())));
				tap4GainSlider.addChangeListener(new eighttapSliderListener());
				tap4GainLabel = new JLabel();
				updatetap4GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap4GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap4GainSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap5GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap5Gain())));
				tap5GainSlider.addChangeListener(new eighttapSliderListener());
				tap5GainLabel = new JLabel();
				updatetap5GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap5GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap5GainSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap6GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap6Gain())));
				tap6GainSlider.addChangeListener(new eighttapSliderListener());
				tap6GainLabel = new JLabel();
				updatetap6GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap6GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap6GainSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap7GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap7Gain())));
				tap7GainSlider.addChangeListener(new eighttapSliderListener());
				tap7GainLabel = new JLabel();
				updatetap7GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap7GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap7GainSlider);		
			
			// dB level slider goes in steps of 1 dB
				tap8GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.gettap8Gain())));
				tap8GainSlider.addChangeListener(new eighttapSliderListener());
				tap8GainLabel = new JLabel();
				updatetap8GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap8GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap8GainSlider);		
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
		class eighttapSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/1.0));
				updateinputGainLabel();
			}
			if(ce.getSource() == fbkGainSlider) {
			gCB.setfbkGain((double) (fbkGainSlider.getValue()/1.0));
				updatefbkGainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));
				updatedelayLengthLabel();
			}
			if(ce.getSource() == tap1GainSlider) {
			gCB.settap1Gain((double) (tap1GainSlider.getValue()/1.0));
				updatetap1GainLabel();
			}
			if(ce.getSource() == tap2GainSlider) {
			gCB.settap2Gain((double) (tap2GainSlider.getValue()/1.0));
				updatetap2GainLabel();
			}
			if(ce.getSource() == tap3GainSlider) {
			gCB.settap3Gain((double) (tap3GainSlider.getValue()/1.0));
				updatetap3GainLabel();
			}
			if(ce.getSource() == tap4GainSlider) {
			gCB.settap4Gain((double) (tap4GainSlider.getValue()/1.0));
				updatetap4GainLabel();
			}
			if(ce.getSource() == tap5GainSlider) {
			gCB.settap5Gain((double) (tap5GainSlider.getValue()/1.0));
				updatetap5GainLabel();
			}
			if(ce.getSource() == tap6GainSlider) {
			gCB.settap6Gain((double) (tap6GainSlider.getValue()/1.0));
				updatetap6GainLabel();
			}
			if(ce.getSource() == tap7GainSlider) {
			gCB.settap7Gain((double) (tap7GainSlider.getValue()/1.0));
				updatetap7GainLabel();
			}
			if(ce.getSource() == tap8GainSlider) {
			gCB.settap8Gain((double) (tap8GainSlider.getValue()/1.0));
				updatetap8GainLabel();
			}
			}
		}

		// add item listener 
		class eighttapItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class eighttapActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainLabel() {
		inputGainLabel.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getinputGain()))));		
		}		
		private void updatefbkGainLabel() {
		fbkGainLabel.setText("Feedback_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getfbkGain()))));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthLabel.setText("Delay_Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/gCB.getSamplerate()));		
		}		
		private void updatetap1GainLabel() {
		tap1GainLabel.setText("Tap_1_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap1Gain()))));		
		}		
		private void updatetap2GainLabel() {
		tap2GainLabel.setText("Tap_2_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap2Gain()))));		
		}		
		private void updatetap3GainLabel() {
		tap3GainLabel.setText("Tap_3_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap3Gain()))));		
		}		
		private void updatetap4GainLabel() {
		tap4GainLabel.setText("Tap_4_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap4Gain()))));		
		}		
		private void updatetap5GainLabel() {
		tap5GainLabel.setText("Tap_5_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap5Gain()))));		
		}		
		private void updatetap6GainLabel() {
		tap6GainLabel.setText("Tap_6_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap6Gain()))));		
		}		
		private void updatetap7GainLabel() {
		tap7GainLabel.setText("Tap_7_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap7Gain()))));		
		}		
		private void updatetap8GainLabel() {
		tap8GainLabel.setText("Tap_8_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.gettap8Gain()))));		
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
