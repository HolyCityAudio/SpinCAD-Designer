/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * control_smootherControlPanel.java
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
		
		import com.holycityaudio.SpinCAD.CADBlocks.control_smootherCADBlock;

		public class control_smootherControlPanel {
		private JFrame frame;

		private control_smootherCADBlock gCB;
		// declare the controls
			JSlider filtSlider;
			JLabel  filtLabel;	

		public control_smootherControlPanel(control_smootherCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Smoother");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			filtSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(0.51) * 100.0),(int) (Math.log10(15.00) * 100.0), (int) (Math.log10(gCB.getfilt()) * 100));
			filtSlider.addChangeListener(new control_smootherSliderListener());
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
		class control_smootherSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == filtSlider) {
				gCB.setfilt((double) (filtSlider.getValue()/100.0));
				updatefiltLabel();
			}
			}
		}
		// add item listener for Bool (CheckbBox) 
		class control_smootherItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			
		}
	}

		private void updatefiltLabel() {
		filtLabel.setText("Filter " + String.format("%4.5f", Math.pow(10.0, gCB.getfilt())) + " Hz");		
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
