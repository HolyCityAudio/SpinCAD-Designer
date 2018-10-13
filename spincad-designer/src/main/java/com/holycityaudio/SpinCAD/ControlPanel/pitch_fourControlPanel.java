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

import org.andrewkilpatrick.elmGen.ElmProgram;
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
import com.holycityaudio.SpinCAD.CADBlocks.pitch_fourCADBlock;

@SuppressWarnings("unused")
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

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch1Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch1() * 1.0));
						pitch1Slider.addChangeListener(new pitch_fourListener());
						pitch1Label = new JLabel();
						Border pitch1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch1Label.setBorder(pitch1Border1);
						updatepitch1Label();
						
						Border pitch1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch1innerPanel = new JPanel();
							
						pitch1innerPanel.setLayout(new BoxLayout(pitch1innerPanel, BoxLayout.Y_AXIS));
						pitch1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch1innerPanel.add(pitch1Label);
						pitch1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch1innerPanel.add(pitch1Slider);		
						pitch1innerPanel.setBorder(pitch1border2);
			
						frame.add(pitch1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch2Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch2() * 1.0));
						pitch2Slider.addChangeListener(new pitch_fourListener());
						pitch2Label = new JLabel();
						Border pitch2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch2Label.setBorder(pitch2Border1);
						updatepitch2Label();
						
						Border pitch2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch2innerPanel = new JPanel();
							
						pitch2innerPanel.setLayout(new BoxLayout(pitch2innerPanel, BoxLayout.Y_AXIS));
						pitch2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch2innerPanel.add(pitch2Label);
						pitch2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch2innerPanel.add(pitch2Slider);		
						pitch2innerPanel.setBorder(pitch2border2);
			
						frame.add(pitch2innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch3Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch3() * 1.0));
						pitch3Slider.addChangeListener(new pitch_fourListener());
						pitch3Label = new JLabel();
						Border pitch3Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch3Label.setBorder(pitch3Border1);
						updatepitch3Label();
						
						Border pitch3border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch3innerPanel = new JPanel();
							
						pitch3innerPanel.setLayout(new BoxLayout(pitch3innerPanel, BoxLayout.Y_AXIS));
						pitch3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch3innerPanel.add(pitch3Label);
						pitch3innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch3innerPanel.add(pitch3Slider);		
						pitch3innerPanel.setBorder(pitch3border2);
			
						frame.add(pitch3innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitch4Slider = new JSlider(JSlider.HORIZONTAL, (int)(-12 * 1.0),(int) (17 * 1.0), (int) (gCB.getpitch4() * 1.0));
						pitch4Slider.addChangeListener(new pitch_fourListener());
						pitch4Label = new JLabel();
						Border pitch4Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitch4Label.setBorder(pitch4Border1);
						updatepitch4Label();
						
						Border pitch4border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitch4innerPanel = new JPanel();
							
						pitch4innerPanel.setLayout(new BoxLayout(pitch4innerPanel, BoxLayout.Y_AXIS));
						pitch4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch4innerPanel.add(pitch4Label);
						pitch4innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitch4innerPanel.add(pitch4Slider);		
						pitch4innerPanel.setBorder(pitch4border2);
			
						frame.add(pitch4innerPanel);
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new pitch_fourActionListener());
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
		class pitch_fourListener implements ChangeListener { 
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

		// add item state changed listener for Checkbox
		class pitch_fourItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class pitch_fourActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatepitch1Label() {
		pitch1Label.setText("Pitch 1 " + String.format("%4.0f", gCB.getpitch1()));		
		}		
		private void updatepitch2Label() {
		pitch2Label.setText("Pitch 2 " + String.format("%4.0f", gCB.getpitch2()));		
		}		
		private void updatepitch3Label() {
		pitch3Label.setText("Pitch 3 " + String.format("%4.0f", gCB.getpitch3()));		
		}		
		private void updatepitch4Label() {
		pitch4Label.setText("Pitch 4 " + String.format("%4.0f", gCB.getpitch4()));		
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
