/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ExpControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.ExpCADBlock;

@SuppressWarnings("unused")
public class ExpControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private ExpCADBlock gCB;
	// declare the controls
	JSlider multiplierSlider;
	JLabel  multiplierLabel;	
	JSlider exp_offsetSlider;
	JLabel  exp_offsetLabel;	

public ExpControlPanel(ExpCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Exp");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					multiplierSlider = new JSlider(JSlider.HORIZONTAL, (int)(-1.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.getmultiplier() * 1000.0));
						multiplierSlider.addChangeListener(new ExpListener());
						multiplierLabel = new JLabel();
						Border multiplierBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						multiplierLabel.setBorder(multiplierBorder1);
						updatemultiplierLabel();
						
						Border multiplierborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel multiplierinnerPanel = new JPanel();
							
						multiplierinnerPanel.setLayout(new BoxLayout(multiplierinnerPanel, BoxLayout.Y_AXIS));
						multiplierinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						multiplierinnerPanel.add(multiplierLabel);
						multiplierinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						multiplierinnerPanel.add(multiplierSlider);		
						multiplierinnerPanel.setBorder(multiplierborder2);
			
						frame.add(multiplierinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					exp_offsetSlider = new JSlider(JSlider.HORIZONTAL, (int)(-1.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.getexp_offset() * 1000.0));
						exp_offsetSlider.addChangeListener(new ExpListener());
						exp_offsetLabel = new JLabel();
						Border exp_offsetBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						exp_offsetLabel.setBorder(exp_offsetBorder1);
						updateexp_offsetLabel();
						
						Border exp_offsetborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel exp_offsetinnerPanel = new JPanel();
							
						exp_offsetinnerPanel.setLayout(new BoxLayout(exp_offsetinnerPanel, BoxLayout.Y_AXIS));
						exp_offsetinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						exp_offsetinnerPanel.add(exp_offsetLabel);
						exp_offsetinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						exp_offsetinnerPanel.add(exp_offsetSlider);		
						exp_offsetinnerPanel.setBorder(exp_offsetborder2);
			
						frame.add(exp_offsetinnerPanel);
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
		class ExpListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == multiplierSlider) {
			gCB.setmultiplier((double) (multiplierSlider.getValue()/1000.0));
				updatemultiplierLabel();
			}
			if(ce.getSource() == exp_offsetSlider) {
			gCB.setexp_offset((double) (exp_offsetSlider.getValue()/1000.0));
				updateexp_offsetLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class ExpItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ExpActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatemultiplierLabel() {
		multiplierLabel.setText("Exp_Multiplier " + String.format("%4.3f", gCB.getmultiplier()));		
		}		
		private void updateexp_offsetLabel() {
		exp_offsetLabel.setText("Exp_Offset " + String.format("%4.3f", gCB.getexp_offset()));		
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
