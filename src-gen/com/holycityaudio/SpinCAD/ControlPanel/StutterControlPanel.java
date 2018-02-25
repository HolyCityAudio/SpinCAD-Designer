/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * StutterControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.StutterCADBlock;

@SuppressWarnings("unused")
public class StutterControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private StutterCADBlock gCB;
	// declare the controls
	JSlider delayLengthSlider;
	JLabel  delayLengthLabel;	
	JSlider fadeTimeFiltSlider;
	JLabel  fadeTimeFiltLabel;	

public StutterControlPanel(StutterCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Stutter");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					delayLengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(32 * 1),(int) (32767 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						delayLengthSlider.addChangeListener(new StutterListener());
						delayLengthLabel = new JLabel();
						Border delayLengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						delayLengthLabel.setBorder(delayLengthBorder1);
						updatedelayLengthLabel();
						
						Border delayLengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel delayLengthinnerPanel = new JPanel();
							
						delayLengthinnerPanel.setLayout(new BoxLayout(delayLengthinnerPanel, BoxLayout.Y_AXIS));
						delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						delayLengthinnerPanel.add(delayLengthLabel);
						delayLengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						delayLengthinnerPanel.add(delayLengthSlider);		
						delayLengthinnerPanel.setBorder(delayLengthborder2);
			
						frame.add(delayLengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					fadeTimeFiltSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1.0),(int) (50 * 1.0), (int) SpinCADBlock.filtToTime(gCB.getfadeTimeFilt() * 1.0));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						fadeTimeFiltSlider.addChangeListener(new StutterListener());
						fadeTimeFiltLabel = new JLabel();
						Border fadeTimeFiltBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						fadeTimeFiltLabel.setBorder(fadeTimeFiltBorder1);
						updatefadeTimeFiltLabel();
						
						Border fadeTimeFiltborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel fadeTimeFiltinnerPanel = new JPanel();
							
						fadeTimeFiltinnerPanel.setLayout(new BoxLayout(fadeTimeFiltinnerPanel, BoxLayout.Y_AXIS));
						fadeTimeFiltinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						fadeTimeFiltinnerPanel.add(fadeTimeFiltLabel);
						fadeTimeFiltinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						fadeTimeFiltinnerPanel.add(fadeTimeFiltSlider);		
						fadeTimeFiltinnerPanel.setBorder(fadeTimeFiltborder2);
			
						frame.add(fadeTimeFiltinnerPanel);
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
		class StutterListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));			    					
				updatedelayLengthLabel();
			}
			if(ce.getSource() == fadeTimeFiltSlider) {
			gCB.setfadeTimeFilt((double) SpinCADBlock.timeToFilt(fadeTimeFiltSlider.getValue()/1.0));
				updatefadeTimeFiltLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class StutterItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class StutterActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatedelayLengthLabel() {
		delayLengthLabel.setText("Delay Time (ms):  " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));		
		}		
		private void updatefadeTimeFiltLabel() {
		fadeTimeFiltLabel.setText("Fade Time (ms):  " + String.format("%4.0f", SpinCADBlock.filtToTime(gCB.getfadeTimeFilt())) + " ms");		
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
