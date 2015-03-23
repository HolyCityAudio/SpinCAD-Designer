/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Shelving_HipassControlPanel.java
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
		import com.holycityaudio.SpinCAD.CADBlocks.Shelving_HipassCADBlock;

		public class Shelving_HipassControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private Shelving_HipassCADBlock gCB;
		// declare the controls
			JSlider freqSlider;
			JLabel  freqLabel;	
			JSlider shelfSlider;
			JLabel  shelfLabel;	

		public Shelving_HipassControlPanel(Shelving_HipassCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Shelving_Hipass");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			//				freqSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(80) * 100.0),(int) (Math.log10(2500) * 100.0), (int) (Math.log10(gCB.getfreq()) * 100));
							freqSlider = gCB.LogFilterSlider(80,2500,gCB.getfreq());
				freqSlider.addChangeListener(new Shelving_HipassSliderListener());
				freqLabel = new JLabel();
				updatefreqLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(freqLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(freqSlider);		
			
			// dB level slider goes in steps of 1 dB
				shelfSlider = new JSlider(JSlider.HORIZONTAL, (int)(-40),(int) (-3), (int) (20 * Math.log10(gCB.getshelf())));
				shelfSlider.addChangeListener(new Shelving_HipassSliderListener());
				shelfLabel = new JLabel();
				updateshelfLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(shelfLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(shelfSlider);		
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
		class Shelving_HipassSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqSlider) {
			gCB.setfreq((double) gCB.freqToFilt(gCB.sliderToLogval((int)(freqSlider.getValue()), 100.0)));
				updatefreqLabel();
			}
			if(ce.getSource() == shelfSlider) {
			gCB.setshelf((double) (shelfSlider.getValue()/1.0));
				updateshelfLabel();
			}
			}
		}

		// add item listener 
		class Shelving_HipassItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class Shelving_HipassActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatefreqLabel() {
		//				kflLabel.setText("HF damping freq 1:" + String.format("%4.1f", gCB.filtToFreq(gCB.getkfl())) + " Hz");		
						freqLabel.setText("Frequency " + String.format("%4.1f", gCB.filtToFreq(gCB.getfreq())) + " Hz");		
		}		
		private void updateshelfLabel() {
		shelfLabel.setText("Shelf " + String.format("%4.1f dB", (20 * Math.log10(gCB.getshelf()))));		
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
