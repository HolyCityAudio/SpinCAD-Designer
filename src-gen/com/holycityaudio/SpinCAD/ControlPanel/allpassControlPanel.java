/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * allpassControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.allpassCADBlock;

@SuppressWarnings("unused")
public class allpassControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private allpassCADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider ap1lengthSlider;
	JLabel  ap1lengthLabel;	
	JSlider ap2lengthSlider;
	JLabel  ap2lengthLabel;	
	JSlider ap3lengthSlider;
	JLabel  ap3lengthLabel;	
	JSlider ap4lengthSlider;
	JLabel  ap4lengthLabel;	
	JSlider kiapSlider;
	JLabel  kiapLabel;	

public allpassControlPanel(allpassCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Allpass");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
					// dB level slider goes in steps of 1 dB
						gainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain())));
						gainSlider.addChangeListener(new allpassListener());
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
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap1lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (4095 * 1), (int) (gCB.getap1length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap1lengthSlider.addChangeListener(new allpassListener());
						ap1lengthLabel = new JLabel();
						Border ap1lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap1lengthLabel.setBorder(ap1lengthBorder1);
						updateap1lengthLabel();
						
						Border ap1lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap1lengthinnerPanel = new JPanel();
							
						ap1lengthinnerPanel.setLayout(new BoxLayout(ap1lengthinnerPanel, BoxLayout.Y_AXIS));
						ap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap1lengthinnerPanel.add(ap1lengthLabel);
						ap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap1lengthinnerPanel.add(ap1lengthSlider);		
						ap1lengthinnerPanel.setBorder(ap1lengthborder2);
			
						frame.add(ap1lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap2lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (4095 * 1), (int) (gCB.getap2length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap2lengthSlider.addChangeListener(new allpassListener());
						ap2lengthLabel = new JLabel();
						Border ap2lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap2lengthLabel.setBorder(ap2lengthBorder1);
						updateap2lengthLabel();
						
						Border ap2lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap2lengthinnerPanel = new JPanel();
							
						ap2lengthinnerPanel.setLayout(new BoxLayout(ap2lengthinnerPanel, BoxLayout.Y_AXIS));
						ap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap2lengthinnerPanel.add(ap2lengthLabel);
						ap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap2lengthinnerPanel.add(ap2lengthSlider);		
						ap2lengthinnerPanel.setBorder(ap2lengthborder2);
			
						frame.add(ap2lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap3lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (4095 * 1), (int) (gCB.getap3length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap3lengthSlider.addChangeListener(new allpassListener());
						ap3lengthLabel = new JLabel();
						Border ap3lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap3lengthLabel.setBorder(ap3lengthBorder1);
						updateap3lengthLabel();
						
						Border ap3lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap3lengthinnerPanel = new JPanel();
							
						ap3lengthinnerPanel.setLayout(new BoxLayout(ap3lengthinnerPanel, BoxLayout.Y_AXIS));
						ap3lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap3lengthinnerPanel.add(ap3lengthLabel);
						ap3lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap3lengthinnerPanel.add(ap3lengthSlider);		
						ap3lengthinnerPanel.setBorder(ap3lengthborder2);
			
						frame.add(ap3lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap4lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (4095 * 1), (int) (gCB.getap4length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap4lengthSlider.addChangeListener(new allpassListener());
						ap4lengthLabel = new JLabel();
						Border ap4lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap4lengthLabel.setBorder(ap4lengthBorder1);
						updateap4lengthLabel();
						
						Border ap4lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap4lengthinnerPanel = new JPanel();
							
						ap4lengthinnerPanel.setLayout(new BoxLayout(ap4lengthinnerPanel, BoxLayout.Y_AXIS));
						ap4lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap4lengthinnerPanel.add(ap4lengthLabel);
						ap4lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap4lengthinnerPanel.add(ap4lengthSlider);		
						ap4lengthinnerPanel.setBorder(ap4lengthborder2);
			
						frame.add(ap4lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kiapSlider = new JSlider(JSlider.HORIZONTAL, (int)(-0.98 * 100.0),(int) (0.98 * 100.0), (int) (gCB.getkiap() * 100.0));
						kiapSlider.addChangeListener(new allpassListener());
						kiapLabel = new JLabel();
						Border kiapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kiapLabel.setBorder(kiapBorder1);
						updatekiapLabel();
						
						Border kiapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kiapinnerPanel = new JPanel();
							
						kiapinnerPanel.setLayout(new BoxLayout(kiapinnerPanel, BoxLayout.Y_AXIS));
						kiapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kiapinnerPanel.add(kiapLabel);
						kiapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kiapinnerPanel.add(kiapSlider);		
						kiapinnerPanel.setBorder(kiapborder2);
			
						frame.add(kiapinnerPanel);
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
		class allpassListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == ap1lengthSlider) {
			gCB.setap1length((double) (ap1lengthSlider.getValue()/1));			    					
				updateap1lengthLabel();
			}
			if(ce.getSource() == ap2lengthSlider) {
			gCB.setap2length((double) (ap2lengthSlider.getValue()/1));			    					
				updateap2lengthLabel();
			}
			if(ce.getSource() == ap3lengthSlider) {
			gCB.setap3length((double) (ap3lengthSlider.getValue()/1));			    					
				updateap3lengthLabel();
			}
			if(ce.getSource() == ap4lengthSlider) {
			gCB.setap4length((double) (ap4lengthSlider.getValue()/1));			    					
				updateap4lengthLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class allpassItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class allpassActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updateap1lengthLabel() {
		ap1lengthLabel.setText("AP1 length " + String.format("%4.0f", (1000 * gCB.getap1length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap2lengthLabel() {
		ap2lengthLabel.setText("AP2 length " + String.format("%4.0f", (1000 * gCB.getap2length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap3lengthLabel() {
		ap3lengthLabel.setText("AP3 length " + String.format("%4.0f", (1000 * gCB.getap3length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap4lengthLabel() {
		ap4lengthLabel.setText("AP4 length " + String.format("%4.0f", (1000 * gCB.getap4length())/ElmProgram.getSamplerate()));		
		}		
		private void updatekiapLabel() {
		kiapLabel.setText("All Pass " + String.format("%4.2f", gCB.getkiap()));		
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
