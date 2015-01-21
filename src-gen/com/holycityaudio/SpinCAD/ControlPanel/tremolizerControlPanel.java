/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * tremolizerControlPanel.java
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
		
		import com.holycityaudio.SpinCAD.CADBlocks.tremolizerCADBlock;

		public class tremolizerControlPanel {
		private JFrame frame;

		private tremolizerCADBlock gCB;
		// declare the controls
			JSlider depthSlider;
			JLabel  depthLabel;	

		public tremolizerControlPanel(tremolizerCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Tremolizer");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			depthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.5 * 100.0),(int) (0.999 * 100.0), (int) (gCB.getdepth() * 100.0));
			depthSlider.addChangeListener(new tremolizerSliderListener());
			depthLabel = new JLabel();
			updatedepthLabel();
			frame.getContentPane().add(depthLabel);
			frame.getContentPane().add(depthSlider);		
				
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
		class tremolizerSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == depthSlider) {
				gCB.setdepth((double) (depthSlider.getValue()/100.0));
				updatedepthLabel();
			}
			}
		}
		// add item listener for Bool (CheckbBox) 
		class tremolizerItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			
		}
	}

		private void updatedepthLabel() {
		depthLabel.setText("Depth " + String.format("%4.2f", gCB.getdepth()));		
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
