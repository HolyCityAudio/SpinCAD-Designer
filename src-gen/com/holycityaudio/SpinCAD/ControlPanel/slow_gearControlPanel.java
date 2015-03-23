/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * slow_gearControlPanel.java
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
		import com.holycityaudio.SpinCAD.CADBlocks.slow_gearCADBlock;

		public class slow_gearControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private slow_gearCADBlock gCB;
		// declare the controls
			JSlider threshSlider;
			JLabel  threshLabel;	

		public slow_gearControlPanel(slow_gearCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Slow_gear");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			// dB level slider goes in steps of 1 dB
				threshSlider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0.0), (int) (20 * Math.log10(gCB.getthresh())));
				threshSlider.addChangeListener(new slow_gearSliderListener());
				threshLabel = new JLabel();
				updatethreshLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(threshLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(threshSlider);		
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
		class slow_gearSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == threshSlider) {
			gCB.setthresh((double) (threshSlider.getValue()/1.0));
				updatethreshLabel();
			}
			}
		}

		// add item listener 
		class slow_gearItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class slow_gearActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatethreshLabel() {
		threshLabel.setText("Threshold " + String.format("%4.1f dB", (20 * Math.log10(gCB.getthresh()))));		
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
