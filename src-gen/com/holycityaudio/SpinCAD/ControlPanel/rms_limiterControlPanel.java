/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rms_limiterControlPanel.java
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
		import java.awt.event.WindowEvent;
		import java.awt.event.WindowListener;
		import java.awt.event.ItemEvent;
		import javax.swing.BoxLayout;
		import javax.swing.JSlider;
		import javax.swing.JLabel;
		import javax.swing.JCheckBox;
		
		import com.holycityaudio.SpinCAD.CADBlocks.rms_limiterCADBlock;

		public class rms_limiterControlPanel {
		private JFrame frame;

		private rms_limiterCADBlock gCB;
		// declare the controls
			JSlider inGainSlider;
			JLabel  inGainLabel;	
			JSlider filtSlider;
			JLabel  filtLabel;	

		public rms_limiterControlPanel(rms_limiterCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("RMS_Limiter");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			inGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.1 * 100.0),(int) (1.0 * 100.0), (int) (gCB.getinGain() * 100.0));
			inGainSlider.addChangeListener(new rms_limiterSliderListener());
			inGainLabel = new JLabel();
			updateinGainLabel();
			frame.getContentPane().add(inGainLabel);
			frame.getContentPane().add(inGainSlider);		
			
			filtSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0001 * 10000.0),(int) (0.01 * 10000.0), (int) (gCB.getfilt() * 10000.0));
			filtSlider.addChangeListener(new rms_limiterSliderListener());
			filtLabel = new JLabel();
			updatefiltLabel();
			frame.getContentPane().add(filtLabel);
			frame.getContentPane().add(filtSlider);		
				
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
		class rms_limiterSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inGainSlider) {
				gCB.setinGain((double) (inGainSlider.getValue()/100.0));
				updateinGainLabel();
			}
			if(ce.getSource() == filtSlider) {
				gCB.setfilt((double) (filtSlider.getValue()/10000.0));
				updatefiltLabel();
			}
			}
		}
		// add item listener for Bool (CheckbBox) 
		class rms_limiterItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			
		}
	}

		private void updateinGainLabel() {
		inGainLabel.setText("Input_Gain " + String.format("%4.2f", gCB.getinGain()));		
		}		
		private void updatefiltLabel() {
		filtLabel.setText("Filter " + String.format("%4.4f", gCB.getfilt()));		
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
