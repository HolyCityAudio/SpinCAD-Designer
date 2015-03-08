/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverbControlPanel.java
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
		import com.holycityaudio.SpinCAD.CADBlocks.reverbCADBlock;

		public class reverbControlPanel extends spinCADControlPanel {
		private JFrame frame;

		private reverbCADBlock gCB;
		// declare the controls
			JSlider kiapSlider;
			JLabel  kiapLabel;	
			JSlider klapSlider;
			JLabel  klapLabel;	
			JSlider kflSlider;
			JLabel  kflLabel;	

		public reverbControlPanel(reverbCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Reverb");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			kiapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.25 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkiap() * 100.0));
				kiapSlider.addChangeListener(new reverbSliderListener());
				kiapLabel = new JLabel();
				updatekiapLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kiapLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kiapSlider);		
			
			klapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.25 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getklap() * 100.0));
				klapSlider.addChangeListener(new reverbSliderListener());
				klapLabel = new JLabel();
				updateklapLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(klapLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(klapSlider);		
			
			kflSlider = new JSlider(JSlider.HORIZONTAL, (int)(Math.log10(100) * 100.0),(int) (Math.log10(5000) * 100.0), (int) (Math.log10(gCB.getkfl()) * 100));
				kflSlider.addChangeListener(new reverbSliderListener());
				kflLabel = new JLabel();
				updatekflLabel();
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kflLabel);
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(kflSlider);		
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
		class reverbSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == kiapSlider) {
				gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			if(ce.getSource() == klapSlider) {
				gCB.setklap((double) (klapSlider.getValue()/100.0));
				updateklapLabel();
			}
			if(ce.getSource() == kflSlider) {
				gCB.setkfl((double) (kflSlider.getValue()/100.0));
				updatekflLabel();
			}
			}
		}

		// add item listener 
		class reverbItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class reverbActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatekiapLabel() {
		kiapLabel.setText("Input_All_Pass " + String.format("%4.2f", gCB.getkiap()));		
		}		
		private void updateklapLabel() {
		klapLabel.setText("Loop_All_Pass " + String.format("%4.2f", gCB.getklap()));		
		}		
		private void updatekflLabel() {
		kflLabel.setText("Low_Pass " + String.format("%4.2f", Math.pow(10.0, gCB.getkfl())) + " Hz");		
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
