/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * sixtapControlPanel.java
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
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.Box;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import java.awt.Dimension;
import java.text.DecimalFormat;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.spinCADControlPanel;
import com.holycityaudio.SpinCAD.CADBlocks.sixtapCADBlock;

public class sixtapControlPanel extends spinCADControlPanel {
	private JFrame frame;

	private sixtapCADBlock gCB;
	// declare the controls
	JSlider inputGainSlider;
	JLabel  inputGainLabel;	
	JSlider delayLengthSlider;
	JLabel  delayLengthLabel;	
	JSlider tap1RatioSlider;
	JLabel  tap1RatioLabel;	
	JSlider tap2RatioSlider;
	JLabel  tap2RatioLabel;	
	JSlider tap3RatioSlider;
	JLabel  tap3RatioLabel;	
	JSlider tap4RatioSlider;
	JLabel  tap4RatioLabel;	
	JSlider tap5RatioSlider;
	JLabel  tap5RatioLabel;	
	JSlider tap6RatioSlider;
	JLabel  tap6RatioLabel;	
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

public sixtapControlPanel(sixtapCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Six_Tap");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			inputGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.getinputGain() * 1000.0));
				inputGainSlider.addChangeListener(new sixtapListener());
				inputGainLabel = new JLabel();
				updateinputGainLabel();
				
				Border inputGainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel inputGaininnerPanel = new JPanel();
					
				inputGaininnerPanel.setLayout(new BoxLayout(inputGaininnerPanel, BoxLayout.Y_AXIS));
				inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				inputGaininnerPanel.add(inputGainLabel);
				inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				inputGaininnerPanel.add(inputGainSlider);		
				inputGaininnerPanel.setBorder(inputGainborder);
			
				frame.add(inputGaininnerPanel);
			
			delayLengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (32767 * 1), (int) (gCB.getdelayLength() * 1));
				delayLengthSlider.addChangeListener(new sixtapListener());
				delayLengthLabel = new JLabel();
				updatedelayLengthLabel();
				
				Border delayLengthborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel delayLengthinnerPanel = new JPanel();
					
				delayLengthinnerPanel.setLayout(new BoxLayout(delayLengthinnerPanel, BoxLayout.Y_AXIS));
				delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				delayLengthinnerPanel.add(delayLengthLabel);
				delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				delayLengthinnerPanel.add(delayLengthSlider);		
				delayLengthinnerPanel.setBorder(delayLengthborder);
			
				frame.add(delayLengthinnerPanel);
			
			tap1RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap1Ratio() * 1000.0));
				tap1RatioSlider.addChangeListener(new sixtapListener());
				tap1RatioLabel = new JLabel();
				updatetap1RatioLabel();
				
				Border tap1Ratioborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap1RatioinnerPanel = new JPanel();
					
				tap1RatioinnerPanel.setLayout(new BoxLayout(tap1RatioinnerPanel, BoxLayout.Y_AXIS));
				tap1RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap1RatioinnerPanel.add(tap1RatioLabel);
				tap1RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap1RatioinnerPanel.add(tap1RatioSlider);		
				tap1RatioinnerPanel.setBorder(tap1Ratioborder);
			
				frame.add(tap1RatioinnerPanel);
			
			tap2RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap2Ratio() * 1000.0));
				tap2RatioSlider.addChangeListener(new sixtapListener());
				tap2RatioLabel = new JLabel();
				updatetap2RatioLabel();
				
				Border tap2Ratioborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap2RatioinnerPanel = new JPanel();
					
				tap2RatioinnerPanel.setLayout(new BoxLayout(tap2RatioinnerPanel, BoxLayout.Y_AXIS));
				tap2RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap2RatioinnerPanel.add(tap2RatioLabel);
				tap2RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap2RatioinnerPanel.add(tap2RatioSlider);		
				tap2RatioinnerPanel.setBorder(tap2Ratioborder);
			
				frame.add(tap2RatioinnerPanel);
			
			tap3RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap3Ratio() * 1000.0));
				tap3RatioSlider.addChangeListener(new sixtapListener());
				tap3RatioLabel = new JLabel();
				updatetap3RatioLabel();
				
				Border tap3Ratioborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap3RatioinnerPanel = new JPanel();
					
				tap3RatioinnerPanel.setLayout(new BoxLayout(tap3RatioinnerPanel, BoxLayout.Y_AXIS));
				tap3RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap3RatioinnerPanel.add(tap3RatioLabel);
				tap3RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap3RatioinnerPanel.add(tap3RatioSlider);		
				tap3RatioinnerPanel.setBorder(tap3Ratioborder);
			
				frame.add(tap3RatioinnerPanel);
			
			tap4RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap4Ratio() * 1000.0));
				tap4RatioSlider.addChangeListener(new sixtapListener());
				tap4RatioLabel = new JLabel();
				updatetap4RatioLabel();
				
				Border tap4Ratioborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap4RatioinnerPanel = new JPanel();
					
				tap4RatioinnerPanel.setLayout(new BoxLayout(tap4RatioinnerPanel, BoxLayout.Y_AXIS));
				tap4RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap4RatioinnerPanel.add(tap4RatioLabel);
				tap4RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap4RatioinnerPanel.add(tap4RatioSlider);		
				tap4RatioinnerPanel.setBorder(tap4Ratioborder);
			
				frame.add(tap4RatioinnerPanel);
			
			tap5RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap5Ratio() * 1000.0));
				tap5RatioSlider.addChangeListener(new sixtapListener());
				tap5RatioLabel = new JLabel();
				updatetap5RatioLabel();
				
				Border tap5Ratioborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap5RatioinnerPanel = new JPanel();
					
				tap5RatioinnerPanel.setLayout(new BoxLayout(tap5RatioinnerPanel, BoxLayout.Y_AXIS));
				tap5RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap5RatioinnerPanel.add(tap5RatioLabel);
				tap5RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap5RatioinnerPanel.add(tap5RatioSlider);		
				tap5RatioinnerPanel.setBorder(tap5Ratioborder);
			
				frame.add(tap5RatioinnerPanel);
			
			tap6RatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap6Ratio() * 1000.0));
				tap6RatioSlider.addChangeListener(new sixtapListener());
				tap6RatioLabel = new JLabel();
				updatetap6RatioLabel();
				
				Border tap6Ratioborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap6RatioinnerPanel = new JPanel();
					
				tap6RatioinnerPanel.setLayout(new BoxLayout(tap6RatioinnerPanel, BoxLayout.Y_AXIS));
				tap6RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap6RatioinnerPanel.add(tap6RatioLabel);
				tap6RatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap6RatioinnerPanel.add(tap6RatioSlider);		
				tap6RatioinnerPanel.setBorder(tap6Ratioborder);
			
				frame.add(tap6RatioinnerPanel);
			
			tap1GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap1Gain() * 1000.0));
				tap1GainSlider.addChangeListener(new sixtapListener());
				tap1GainLabel = new JLabel();
				updatetap1GainLabel();
				
				Border tap1Gainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap1GaininnerPanel = new JPanel();
					
				tap1GaininnerPanel.setLayout(new BoxLayout(tap1GaininnerPanel, BoxLayout.Y_AXIS));
				tap1GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap1GaininnerPanel.add(tap1GainLabel);
				tap1GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap1GaininnerPanel.add(tap1GainSlider);		
				tap1GaininnerPanel.setBorder(tap1Gainborder);
			
				frame.add(tap1GaininnerPanel);
			
			tap2GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap2Gain() * 1000.0));
				tap2GainSlider.addChangeListener(new sixtapListener());
				tap2GainLabel = new JLabel();
				updatetap2GainLabel();
				
				Border tap2Gainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap2GaininnerPanel = new JPanel();
					
				tap2GaininnerPanel.setLayout(new BoxLayout(tap2GaininnerPanel, BoxLayout.Y_AXIS));
				tap2GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap2GaininnerPanel.add(tap2GainLabel);
				tap2GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap2GaininnerPanel.add(tap2GainSlider);		
				tap2GaininnerPanel.setBorder(tap2Gainborder);
			
				frame.add(tap2GaininnerPanel);
			
			tap3GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap3Gain() * 1000.0));
				tap3GainSlider.addChangeListener(new sixtapListener());
				tap3GainLabel = new JLabel();
				updatetap3GainLabel();
				
				Border tap3Gainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap3GaininnerPanel = new JPanel();
					
				tap3GaininnerPanel.setLayout(new BoxLayout(tap3GaininnerPanel, BoxLayout.Y_AXIS));
				tap3GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap3GaininnerPanel.add(tap3GainLabel);
				tap3GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap3GaininnerPanel.add(tap3GainSlider);		
				tap3GaininnerPanel.setBorder(tap3Gainborder);
			
				frame.add(tap3GaininnerPanel);
			
			tap4GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap4Gain() * 1000.0));
				tap4GainSlider.addChangeListener(new sixtapListener());
				tap4GainLabel = new JLabel();
				updatetap4GainLabel();
				
				Border tap4Gainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap4GaininnerPanel = new JPanel();
					
				tap4GaininnerPanel.setLayout(new BoxLayout(tap4GaininnerPanel, BoxLayout.Y_AXIS));
				tap4GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap4GaininnerPanel.add(tap4GainLabel);
				tap4GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap4GaininnerPanel.add(tap4GainSlider);		
				tap4GaininnerPanel.setBorder(tap4Gainborder);
			
				frame.add(tap4GaininnerPanel);
			
			tap5GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap5Gain() * 1000.0));
				tap5GainSlider.addChangeListener(new sixtapListener());
				tap5GainLabel = new JLabel();
				updatetap5GainLabel();
				
				Border tap5Gainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap5GaininnerPanel = new JPanel();
					
				tap5GaininnerPanel.setLayout(new BoxLayout(tap5GaininnerPanel, BoxLayout.Y_AXIS));
				tap5GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap5GaininnerPanel.add(tap5GainLabel);
				tap5GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap5GaininnerPanel.add(tap5GainSlider);		
				tap5GaininnerPanel.setBorder(tap5Gainborder);
			
				frame.add(tap5GaininnerPanel);
			
			tap6GainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.gettap6Gain() * 1000.0));
				tap6GainSlider.addChangeListener(new sixtapListener());
				tap6GainLabel = new JLabel();
				updatetap6GainLabel();
				
				Border tap6Gainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel tap6GaininnerPanel = new JPanel();
					
				tap6GaininnerPanel.setLayout(new BoxLayout(tap6GaininnerPanel, BoxLayout.Y_AXIS));
				tap6GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap6GaininnerPanel.add(tap6GainLabel);
				tap6GaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				tap6GaininnerPanel.add(tap6GainSlider);		
				tap6GaininnerPanel.setBorder(tap6Gainborder);
			
				frame.add(tap6GaininnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setAlwaysOnTop(true);
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class sixtapListener implements ChangeListener { 
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
			if(ce.getSource() == tap2RatioSlider) {
			gCB.settap2Ratio((double) (tap2RatioSlider.getValue()/1000.0));
				updatetap2RatioLabel();
			}
			if(ce.getSource() == tap3RatioSlider) {
			gCB.settap3Ratio((double) (tap3RatioSlider.getValue()/1000.0));
				updatetap3RatioLabel();
			}
			if(ce.getSource() == tap4RatioSlider) {
			gCB.settap4Ratio((double) (tap4RatioSlider.getValue()/1000.0));
				updatetap4RatioLabel();
			}
			if(ce.getSource() == tap5RatioSlider) {
			gCB.settap5Ratio((double) (tap5RatioSlider.getValue()/1000.0));
				updatetap5RatioLabel();
			}
			if(ce.getSource() == tap6RatioSlider) {
			gCB.settap6Ratio((double) (tap6RatioSlider.getValue()/1000.0));
				updatetap6RatioLabel();
			}
			if(ce.getSource() == tap1GainSlider) {
			gCB.settap1Gain((double) (tap1GainSlider.getValue()/1000.0));
				updatetap1GainLabel();
			}
			if(ce.getSource() == tap2GainSlider) {
			gCB.settap2Gain((double) (tap2GainSlider.getValue()/1000.0));
				updatetap2GainLabel();
			}
			if(ce.getSource() == tap3GainSlider) {
			gCB.settap3Gain((double) (tap3GainSlider.getValue()/1000.0));
				updatetap3GainLabel();
			}
			if(ce.getSource() == tap4GainSlider) {
			gCB.settap4Gain((double) (tap4GainSlider.getValue()/1000.0));
				updatetap4GainLabel();
			}
			if(ce.getSource() == tap5GainSlider) {
			gCB.settap5Gain((double) (tap5GainSlider.getValue()/1000.0));
				updatetap5GainLabel();
			}
			if(ce.getSource() == tap6GainSlider) {
			gCB.settap6Gain((double) (tap6GainSlider.getValue()/1000.0));
				updatetap6GainLabel();
			}
			}
		}

		// add item listener 
		class sixtapItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class sixtapActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainLabel() {
		inputGainLabel.setText("Input Gain " + String.format("%4.2f", gCB.getinputGain()));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthLabel.setText("Delay Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/gCB.getSamplerate()));		
		}		
		private void updatetap1RatioLabel() {
		tap1RatioLabel.setText("Tap 1 Time " + String.format("%4.2f", gCB.gettap1Ratio()));		
		}		
		private void updatetap2RatioLabel() {
		tap2RatioLabel.setText("Tap 2 Time " + String.format("%4.2f", gCB.gettap2Ratio()));		
		}		
		private void updatetap3RatioLabel() {
		tap3RatioLabel.setText("Tap 3 Time " + String.format("%4.2f", gCB.gettap3Ratio()));		
		}		
		private void updatetap4RatioLabel() {
		tap4RatioLabel.setText("Tap 4 Time " + String.format("%4.2f", gCB.gettap4Ratio()));		
		}		
		private void updatetap5RatioLabel() {
		tap5RatioLabel.setText("Tap 5 Time " + String.format("%4.2f", gCB.gettap5Ratio()));		
		}		
		private void updatetap6RatioLabel() {
		tap6RatioLabel.setText("Tap_6_Time " + String.format("%4.2f", gCB.gettap6Ratio()));		
		}		
		private void updatetap1GainLabel() {
		tap1GainLabel.setText("Tap_1_Gain " + String.format("%4.2f", gCB.gettap1Gain()));		
		}		
		private void updatetap2GainLabel() {
		tap2GainLabel.setText("Tap_2_Gain " + String.format("%4.2f", gCB.gettap2Gain()));		
		}		
		private void updatetap3GainLabel() {
		tap3GainLabel.setText("Tap_3_Gain " + String.format("%4.2f", gCB.gettap3Gain()));		
		}		
		private void updatetap4GainLabel() {
		tap4GainLabel.setText("Tap_4_Gain " + String.format("%4.2f", gCB.gettap4Gain()));		
		}		
		private void updatetap5GainLabel() {
		tap5GainLabel.setText("Tap_5_Gain " + String.format("%4.2f", gCB.gettap5Gain()));		
		}		
		private void updatetap6GainLabel() {
		tap6GainLabel.setText("Tap_6_Gain " + String.format("%4.2f", gCB.gettap6Gain()));		
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
