/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Pitch_shift_testControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.Pitch_shift_testCADBlock;

@SuppressWarnings("unused")
public class Pitch_shift_testControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private Pitch_shift_testCADBlock gCB;
	// declare the controls
	JSlider pitchCoeffSlider;
	JLabel  pitchCoeffLabel;	
	private JComboBox <String> controlRangeComboBox; 
	private JComboBox <String> lfoSelComboBox; 
	private JComboBox <String> lfoWidthComboBox; 

public Pitch_shift_testControlPanel(Pitch_shift_testCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Pitch Shift");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					pitchCoeffSlider = new JSlider(JSlider.HORIZONTAL, (int)(-16384 * 1.0),(int) (32767 * 1.0), (int) (gCB.getpitchCoeff() * 1.0));
						pitchCoeffSlider.addChangeListener(new Pitch_shift_testListener());
						pitchCoeffLabel = new JLabel();
						Border pitchCoeffBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						pitchCoeffLabel.setBorder(pitchCoeffBorder1);
						updatepitchCoeffLabel();
						
						Border pitchCoeffborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel pitchCoeffinnerPanel = new JPanel();
							
						pitchCoeffinnerPanel.setLayout(new BoxLayout(pitchCoeffinnerPanel, BoxLayout.Y_AXIS));
						pitchCoeffinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeffinnerPanel.add(pitchCoeffLabel);
						pitchCoeffinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						pitchCoeffinnerPanel.add(pitchCoeffSlider);		
						pitchCoeffinnerPanel.setBorder(pitchCoeffborder2);
			
						frame.add(pitchCoeffinnerPanel);
				controlRangeComboBox = new JComboBox <String> ();
				controlRangeComboBox.addItem("0 -> +1");
				controlRangeComboBox.addItem("-1 -> +1");
				controlRangeComboBox.setSelectedIndex(gCB.getcontrolRange());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(controlRangeComboBox);
				controlRangeComboBox.addActionListener(new Pitch_shift_testActionListener());
				lfoSelComboBox = new JComboBox <String> ();
				lfoSelComboBox.addItem("Ramp 0");
				lfoSelComboBox.addItem("Ramp 1");
				lfoSelComboBox.setSelectedIndex(gCB.getlfoSel());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoSelComboBox);
				lfoSelComboBox.addActionListener(new Pitch_shift_testActionListener());
				lfoWidthComboBox = new JComboBox <String> ();
				lfoWidthComboBox.addItem("4096");
				lfoWidthComboBox.addItem("2048");
			lfoWidthComboBox.addItem("1024");
			lfoWidthComboBox.addItem("512");
				lfoWidthComboBox.setSelectedIndex(gCB.getlfoWidth());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(lfoWidthComboBox);
				lfoWidthComboBox.addActionListener(new Pitch_shift_testActionListener());
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
		class Pitch_shift_testListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == pitchCoeffSlider) {
			gCB.setpitchCoeff((double) (pitchCoeffSlider.getValue()/1.0));
				updatepitchCoeffLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class Pitch_shift_testItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class Pitch_shift_testActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == controlRangeComboBox) {
				gCB.setcontrolRange((controlRangeComboBox.getSelectedIndex()));
			}
			if(arg0.getSource() == lfoSelComboBox) {
				gCB.setlfoSel((lfoSelComboBox.getSelectedIndex()));
			}
			if(arg0.getSource() == lfoWidthComboBox) {
				gCB.setlfoWidth((lfoWidthComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatepitchCoeffLabel() {
		pitchCoeffLabel.setText("Pitch Coefficient " + String.format("%4.0f", gCB.getpitchCoeff()));		
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
