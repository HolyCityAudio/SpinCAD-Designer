/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_plateControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.reverb_plateCADBlock;

public class reverb_plateControlPanel extends spinCADControlPanel {
	private JFrame frame;

	private reverb_plateCADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider rate1Slider;
	JLabel  rate1Label;	
	JSlider rate2Slider;
	JLabel  rate2Label;	

public reverb_plateControlPanel(reverb_plateCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Reverb_Plate");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			// dB level slider goes in steps of 1 dB
				gainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getgain())));
				gainSlider.addChangeListener(new reverb_plateListener());
				gainLabel = new JLabel();
				Border gainBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				gainLabel.setBorder(gainBorder1);
				updategainLabel();
				
				Border gainborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel gaininnerPanel = new JPanel();
					
				gaininnerPanel.setLayout(new BoxLayout(gaininnerPanel, BoxLayout.Y_AXIS));
				gaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				gaininnerPanel.add(gainLabel);
				gaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				gaininnerPanel.add(gainSlider);		
				gaininnerPanel.setBorder(gainborder2);
			
				frame.add(gaininnerPanel);
			
			rate1Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (51.0 * 100.0), (int) ((gCB.getrate1()) * 100.0));
				rate1Slider.addChangeListener(new reverb_plateListener());
				rate1Label = new JLabel();
				Border rate1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				rate1Label.setBorder(rate1Border1);
				updaterate1Label();
				
				Border rate1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel rate1innerPanel = new JPanel();
					
				rate1innerPanel.setLayout(new BoxLayout(rate1innerPanel, BoxLayout.Y_AXIS));
				rate1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				rate1innerPanel.add(rate1Label);
				rate1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				rate1innerPanel.add(rate1Slider);		
				rate1innerPanel.setBorder(rate1border2);
			
				frame.add(rate1innerPanel);
			
			rate2Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (51.0 * 100.0), (int) ((gCB.getrate2()) * 100.0));
				rate2Slider.addChangeListener(new reverb_plateListener());
				rate2Label = new JLabel();
				Border rate2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				rate2Label.setBorder(rate2Border1);
				updaterate2Label();
				
				Border rate2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel rate2innerPanel = new JPanel();
					
				rate2innerPanel.setLayout(new BoxLayout(rate2innerPanel, BoxLayout.Y_AXIS));
				rate2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				rate2innerPanel.add(rate2Label);
				rate2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				rate2innerPanel.add(rate2Slider);		
				rate2innerPanel.setBorder(rate2border2);
			
				frame.add(rate2innerPanel);
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
		class reverb_plateListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));
				updategainLabel();
			}
			if(ce.getSource() == rate1Slider) {
			gCB.setrate1((double) (rate1Slider.getValue()/100.0));
				updaterate1Label();
			}
			if(ce.getSource() == rate2Slider) {
			gCB.setrate2((double) (rate2Slider.getValue()/100.0));
				updaterate2Label();
			}
			}
		}

		// add item listener 
		class reverb_plateItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class reverb_plateActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updaterate1Label() {
		rate1Label.setText("LFO_Rate_1 " + String.format("%4.2f", coeffToLFORate(gCB.getrate1())));		
		}		
		private void updaterate2Label() {
		rate2Label.setText("LFO_Rate_2 " + String.format("%4.2f", coeffToLFORate(gCB.getrate2())));		
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
