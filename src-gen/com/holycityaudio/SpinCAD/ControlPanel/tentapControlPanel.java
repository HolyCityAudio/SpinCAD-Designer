/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * tentapControlPanel.java
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
		import com.holycityaudio.SpinCAD.CADBlocks.tentapCADBlock;

		public class tentapControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private tentapCADBlock gCB;
		// declare the controls
			JSlider inputGainSlider;
			JLabel  inputGainLabel;	
			JSlider delayLengthSlider;
			JLabel  delayLengthLabel;	
			JSlider tap1RatioSlider;
			JLabel  tap1RatioLabel;	
			JSlider tap1GainSlider;
			JLabel  tap1GainLabel;	
			JSlider tap2RatioSlider;
			JLabel  tap2RatioLabel;	
			JSlider tap2GainSlider;
			JLabel  tap2GainLabel;	
			JSlider tap3RatioSlider;
			JLabel  tap3RatioLabel;	
			JSlider tap3GainSlider;
			JLabel  tap3GainLabel;	
			JSlider tap4RatioSlider;
			JLabel  tap4RatioLabel;	
			JSlider tap4GainSlider;
			JLabel  tap4GainLabel;	
			JSlider tap5RatioSlider;
			JLabel  tap5RatioLabel;	
			JSlider tap5GainSlider;
			JLabel  tap5GainLabel;	
			JSlider tap6RatioSlider;
			JLabel  tap6RatioLabel;	
			JSlider tap6GainSlider;
			JLabel  tap6GainLabel;	
			JSlider tap7RatioSlider;
			JLabel  tap7RatioLabel;	
			JSlider tap7GainSlider;
			JLabel  tap7GainLabel;	
			JSlider tap8RatioSlider;
			JLabel  tap8RatioLabel;	
			JSlider tap8GainSlider;
			JLabel  tap8GainLabel;	
			JSlider tap9RatioSlider;
			JLabel  tap9RatioLabel;	
			JSlider tap9GainSlider;
			JLabel  tap9GainLabel;	
			JSlider tap10RatioSlider;
			JLabel  tap10RatioLabel;	
			JSlider tap10GainSlider;
			JLabel  tap10GainLabel;	

		public tentapControlPanel(tentapCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Ten_Tap");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			inputGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.getinputGain() * 1000.0));
				inputGainSlider.addChangeListener(new tentapSliderListener());
				inputGainLabel = new JLabel();
				updateinputGainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(inputGainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(inputGainSlider);		
			
			delayLengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (32767 * 1), (int) (gCB.getdelayLength() * 1));
				delayLengthSlider.addChangeListener(new tentapSliderListener());
				delayLengthLabel = new JLabel();
				updatedelayLengthLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(delayLengthLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(delayLengthSlider);		
			
			tap1RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap1Ratio() * 1000.0));
				tap1RatioSlider.addChangeListener(new tentapSliderListener());
				tap1RatioLabel = new JLabel();
				updatetap1RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1RatioSlider);		
			
			tap1GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap1Gain() * 1000.0));
				tap1GainSlider.addChangeListener(new tentapSliderListener());
				tap1GainLabel = new JLabel();
				updatetap1GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap1GainSlider);		
			
			tap2RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap2Ratio() * 1000.0));
				tap2RatioSlider.addChangeListener(new tentapSliderListener());
				tap2RatioLabel = new JLabel();
				updatetap2RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap2RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap2RatioSlider);		
			
			tap2GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap2Gain() * 1000.0));
				tap2GainSlider.addChangeListener(new tentapSliderListener());
				tap2GainLabel = new JLabel();
				updatetap2GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap2GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap2GainSlider);		
			
			tap3RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap3Ratio() * 1000.0));
				tap3RatioSlider.addChangeListener(new tentapSliderListener());
				tap3RatioLabel = new JLabel();
				updatetap3RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap3RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap3RatioSlider);		
			
			tap3GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap3Gain() * 1000.0));
				tap3GainSlider.addChangeListener(new tentapSliderListener());
				tap3GainLabel = new JLabel();
				updatetap3GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap3GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap3GainSlider);		
			
			tap4RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap4Ratio() * 1000.0));
				tap4RatioSlider.addChangeListener(new tentapSliderListener());
				tap4RatioLabel = new JLabel();
				updatetap4RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap4RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap4RatioSlider);		
			
			tap4GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap4Gain() * 1000.0));
				tap4GainSlider.addChangeListener(new tentapSliderListener());
				tap4GainLabel = new JLabel();
				updatetap4GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap4GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap4GainSlider);		
			
			tap5RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap5Ratio() * 1000.0));
				tap5RatioSlider.addChangeListener(new tentapSliderListener());
				tap5RatioLabel = new JLabel();
				updatetap5RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap5RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap5RatioSlider);		
			
			tap5GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap5Gain() * 1000.0));
				tap5GainSlider.addChangeListener(new tentapSliderListener());
				tap5GainLabel = new JLabel();
				updatetap5GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap5GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap5GainSlider);		
			
			tap6RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap6Ratio() * 1000.0));
				tap6RatioSlider.addChangeListener(new tentapSliderListener());
				tap6RatioLabel = new JLabel();
				updatetap6RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap6RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap6RatioSlider);		
			
			tap6GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap6Gain() * 1000.0));
				tap6GainSlider.addChangeListener(new tentapSliderListener());
				tap6GainLabel = new JLabel();
				updatetap6GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap6GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap6GainSlider);		
			
			tap7RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap7Ratio() * 1000.0));
				tap7RatioSlider.addChangeListener(new tentapSliderListener());
				tap7RatioLabel = new JLabel();
				updatetap7RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap7RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap7RatioSlider);		
			
			tap7GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap7Gain() * 1000.0));
				tap7GainSlider.addChangeListener(new tentapSliderListener());
				tap7GainLabel = new JLabel();
				updatetap7GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap7GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap7GainSlider);		
			
			tap8RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap8Ratio() * 1000.0));
				tap8RatioSlider.addChangeListener(new tentapSliderListener());
				tap8RatioLabel = new JLabel();
				updatetap8RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap8RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap8RatioSlider);		
			
			tap8GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap8Gain() * 1000.0));
				tap8GainSlider.addChangeListener(new tentapSliderListener());
				tap8GainLabel = new JLabel();
				updatetap8GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap8GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap8GainSlider);		
			
			tap9RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap9Ratio() * 1000.0));
				tap9RatioSlider.addChangeListener(new tentapSliderListener());
				tap9RatioLabel = new JLabel();
				updatetap9RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap9RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap9RatioSlider);		
			
			tap9GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap9Gain() * 1000.0));
				tap9GainSlider.addChangeListener(new tentapSliderListener());
				tap9GainLabel = new JLabel();
				updatetap9GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap9GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap9GainSlider);		
			
			tap10RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap10Ratio() * 1000.0));
				tap10RatioSlider.addChangeListener(new tentapSliderListener());
				tap10RatioLabel = new JLabel();
				updatetap10RatioLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap10RatioLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap10RatioSlider);		
			
			tap10GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap10Gain() * 1000.0));
				tap10GainSlider.addChangeListener(new tentapSliderListener());
				tap10GainLabel = new JLabel();
				updatetap10GainLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap10GainLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(tap10GainSlider);		
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
		class tentapSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
				gCB.setinputGain((double) (inputGainSlider.getValue()/1000.0));
				updateinputGainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
				gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));
				updatedelayLengthLabel();
			}
			if(ce.getSource() == tap1RatioSlider) {
				gCB.settap1Ratio((double) (tap1RatioSlider.getValue()/1000.0));
				updatetap1RatioLabel();
			}
			if(ce.getSource() == tap1GainSlider) {
				gCB.settap1Gain((double) (tap1GainSlider.getValue()/1000.0));
				updatetap1GainLabel();
			}
			if(ce.getSource() == tap2RatioSlider) {
				gCB.settap2Ratio((double) (tap2RatioSlider.getValue()/1000.0));
				updatetap2RatioLabel();
			}
			if(ce.getSource() == tap2GainSlider) {
				gCB.settap2Gain((double) (tap2GainSlider.getValue()/1000.0));
				updatetap2GainLabel();
			}
			if(ce.getSource() == tap3RatioSlider) {
				gCB.settap3Ratio((double) (tap3RatioSlider.getValue()/1000.0));
				updatetap3RatioLabel();
			}
			if(ce.getSource() == tap3GainSlider) {
				gCB.settap3Gain((double) (tap3GainSlider.getValue()/1000.0));
				updatetap3GainLabel();
			}
			if(ce.getSource() == tap4RatioSlider) {
				gCB.settap4Ratio((double) (tap4RatioSlider.getValue()/1000.0));
				updatetap4RatioLabel();
			}
			if(ce.getSource() == tap4GainSlider) {
				gCB.settap4Gain((double) (tap4GainSlider.getValue()/1000.0));
				updatetap4GainLabel();
			}
			if(ce.getSource() == tap5RatioSlider) {
				gCB.settap5Ratio((double) (tap5RatioSlider.getValue()/1000.0));
				updatetap5RatioLabel();
			}
			if(ce.getSource() == tap5GainSlider) {
				gCB.settap5Gain((double) (tap5GainSlider.getValue()/1000.0));
				updatetap5GainLabel();
			}
			if(ce.getSource() == tap6RatioSlider) {
				gCB.settap6Ratio((double) (tap6RatioSlider.getValue()/1000.0));
				updatetap6RatioLabel();
			}
			if(ce.getSource() == tap6GainSlider) {
				gCB.settap6Gain((double) (tap6GainSlider.getValue()/1000.0));
				updatetap6GainLabel();
			}
			if(ce.getSource() == tap7RatioSlider) {
				gCB.settap7Ratio((double) (tap7RatioSlider.getValue()/1000.0));
				updatetap7RatioLabel();
			}
			if(ce.getSource() == tap7GainSlider) {
				gCB.settap7Gain((double) (tap7GainSlider.getValue()/1000.0));
				updatetap7GainLabel();
			}
			if(ce.getSource() == tap8RatioSlider) {
				gCB.settap8Ratio((double) (tap8RatioSlider.getValue()/1000.0));
				updatetap8RatioLabel();
			}
			if(ce.getSource() == tap8GainSlider) {
				gCB.settap8Gain((double) (tap8GainSlider.getValue()/1000.0));
				updatetap8GainLabel();
			}
			if(ce.getSource() == tap9RatioSlider) {
				gCB.settap9Ratio((double) (tap9RatioSlider.getValue()/1000.0));
				updatetap9RatioLabel();
			}
			if(ce.getSource() == tap9GainSlider) {
				gCB.settap9Gain((double) (tap9GainSlider.getValue()/1000.0));
				updatetap9GainLabel();
			}
			if(ce.getSource() == tap10RatioSlider) {
				gCB.settap10Ratio((double) (tap10RatioSlider.getValue()/1000.0));
				updatetap10RatioLabel();
			}
			if(ce.getSource() == tap10GainSlider) {
				gCB.settap10Gain((double) (tap10GainSlider.getValue()/1000.0));
				updatetap10GainLabel();
			}
			}
		}

		// add item listener 
		class tentapItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class tentapActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainLabel() {
		inputGainLabel.setText("Input_Gain " + String.format("%4.2f", gCB.getinputGain()));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthLabel.setText("Delay_Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/gCB.getSamplerate()));		
		}		
		private void updatetap1RatioLabel() {
		tap1RatioLabel.setText("Tap_1_Time " + String.format("%4.2f", gCB.gettap1Ratio()));		
		}		
		private void updatetap1GainLabel() {
		tap1GainLabel.setText("Tap_1_Gain " + String.format("%4.2f", gCB.gettap1Gain()));		
		}		
		private void updatetap2RatioLabel() {
		tap2RatioLabel.setText("Tap_2_Time " + String.format("%4.2f", gCB.gettap2Ratio()));		
		}		
		private void updatetap2GainLabel() {
		tap2GainLabel.setText("Tap_2_Gain " + String.format("%4.2f", gCB.gettap2Gain()));		
		}		
		private void updatetap3RatioLabel() {
		tap3RatioLabel.setText("Tap_3_Time " + String.format("%4.2f", gCB.gettap3Ratio()));		
		}		
		private void updatetap3GainLabel() {
		tap3GainLabel.setText("Tap_3_Gain " + String.format("%4.2f", gCB.gettap3Gain()));		
		}		
		private void updatetap4RatioLabel() {
		tap4RatioLabel.setText("Tap_4_Time " + String.format("%4.2f", gCB.gettap4Ratio()));		
		}		
		private void updatetap4GainLabel() {
		tap4GainLabel.setText("Tap_4_Gain " + String.format("%4.2f", gCB.gettap4Gain()));		
		}		
		private void updatetap5RatioLabel() {
		tap5RatioLabel.setText("Tap_5_Time " + String.format("%4.2f", gCB.gettap5Ratio()));		
		}		
		private void updatetap5GainLabel() {
		tap5GainLabel.setText("Tap_5_Gain " + String.format("%4.2f", gCB.gettap5Gain()));		
		}		
		private void updatetap6RatioLabel() {
		tap6RatioLabel.setText("Tap_6_Time " + String.format("%4.2f", gCB.gettap6Ratio()));		
		}		
		private void updatetap6GainLabel() {
		tap6GainLabel.setText("Tap_6_Gain " + String.format("%4.2f", gCB.gettap6Gain()));		
		}		
		private void updatetap7RatioLabel() {
		tap7RatioLabel.setText("Tap_7_Time " + String.format("%4.2f", gCB.gettap7Ratio()));		
		}		
		private void updatetap7GainLabel() {
		tap7GainLabel.setText("Tap_7_Gain " + String.format("%4.2f", gCB.gettap7Gain()));		
		}		
		private void updatetap8RatioLabel() {
		tap8RatioLabel.setText("Tap_8_Time " + String.format("%4.2f", gCB.gettap8Ratio()));		
		}		
		private void updatetap8GainLabel() {
		tap8GainLabel.setText("Tap_8_Gain " + String.format("%4.2f", gCB.gettap8Gain()));		
		}		
		private void updatetap9RatioLabel() {
		tap9RatioLabel.setText("Tap_9_Time " + String.format("%4.2f", gCB.gettap9Ratio()));		
		}		
		private void updatetap9GainLabel() {
		tap9GainLabel.setText("Tap_9_Gain " + String.format("%4.2f", gCB.gettap9Gain()));		
		}		
		private void updatetap10RatioLabel() {
		tap10RatioLabel.setText("Tap_10_Time " + String.format("%4.2f", gCB.gettap10Ratio()));		
		}		
		private void updatetap10GainLabel() {
		tap10GainLabel.setText("Tap_10_Gain " + String.format("%4.2f", gCB.gettap10Gain()));		
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
