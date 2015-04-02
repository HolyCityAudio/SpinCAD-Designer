/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * pitch_fourControlPanel.java
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
		import com.holycityaudio.SpinCAD.CADBlocks.pitch_fourCADBlock;

		public class pitch_fourControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private pitch_fourCADBlock gCB;
		// declare the controls
			JSlider pitch1Slider;
			JLabel  pitch1Label;	
			JSlider pitch2Slider;
			JLabel  pitch2Label;	
			JSlider pitch3Slider;
			JLabel  pitch3Label;	
			JSlider pitch4Slider;
			JLabel  pitch4Label;	
			private JComboBox <String> lfoSelComboBox; 

		public pitch_fourControlPanel(pitch_fourCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Pitch_Four");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			pitch1Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch1() * 1.0));
				pitch1Slider.addChangeListener(new pitch_fourSliderListener());
				pitch1Label = new JLabel();
				updatepitch1Label();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch1Label);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch1Slider);		
			
			pitch2Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch2() * 1.0));
				pitch2Slider.addChangeListener(new pitch_fourSliderListener());
				pitch2Label = new JLabel();
				updatepitch2Label();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch2Label);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch2Slider);		
			
			pitch3Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch3() * 1.0));
				pitch3Slider.addChangeListener(new pitch_fourSliderListener());
				pitch3Label = new JLabel();
				updatepitch3Label();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch3Label);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch3Slider);		
			
			pitch4Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch4() * 1.0));
				pitch4Slider.addChangeListener(new pitch_fourSliderListener());
				pitch4Label = new JLabel();
				updatepitch4Label();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch4Label);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(pitch4Slider);		
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new pitch_fourActionListener());
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
		class pitch_fourSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == pitch1Slider) {
			gCB.setpitch1((double) (pitch1Slider.getValue()/1.0));
				updatepitch1Label();
			}
			if(ce.getSource() == pitch2Slider) {
			gCB.setpitch2((double) (pitch2Slider.getValue()/1.0));
				updatepitch2Label();
			}
			if(ce.getSource() == pitch3Slider) {
			gCB.setpitch3((double) (pitch3Slider.getValue()/1.0));
				updatepitch3Label();
			}
			if(ce.getSource() == pitch4Slider) {
			gCB.setpitch4((double) (pitch4Slider.getValue()/1.0));
				updatepitch4Label();
			}
			}
		}

		// add item listener 
		class pitch_fourItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class pitch_fourActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatepitch1Label() {
		pitch1Label.setText("Pitch_1 " + String.format("%4.0f", gCB.getpitch1()));		
		}		
		private void updatepitch2Label() {
		pitch2Label.setText("Pitch_2 " + String.format("%4.0f", gCB.getpitch2()));		
		}		
		private void updatepitch3Label() {
		pitch3Label.setText("Pitch_3 " + String.format("%4.0f", gCB.getpitch3()));		
		}		
		private void updatepitch4Label() {
		pitch4Label.setText("Pitch_4 " + String.format("%4.0f", gCB.getpitch4()));		
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
