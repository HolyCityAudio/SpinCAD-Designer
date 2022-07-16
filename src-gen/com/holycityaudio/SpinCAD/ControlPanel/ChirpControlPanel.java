/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ChirpControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.ChirpCADBlock;

@SuppressWarnings("unused")
public class ChirpControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private ChirpCADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider nAPsSlider;
	JLabel  nAPsLabel;	
	JSlider stretchSlider;
	JLabel  stretchLabel;	
	JSlider kiapSlider;
	JLabel  kiapLabel;	

public ChirpControlPanel(ChirpCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Chirp");
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
						gainSlider.addChangeListener(new ChirpListener());
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
					nAPsSlider = new JSlider(JSlider.HORIZONTAL, (int)(2 * 1.0),(int) (30 * 1.0), (int) (gCB.getnAPs() * 1.0));
						nAPsSlider.addChangeListener(new ChirpListener());
						nAPsLabel = new JLabel();
						Border nAPsBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						nAPsLabel.setBorder(nAPsBorder1);
						updatenAPsLabel();
						
						Border nAPsborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel nAPsinnerPanel = new JPanel();
							
						nAPsinnerPanel.setLayout(new BoxLayout(nAPsinnerPanel, BoxLayout.Y_AXIS));
						nAPsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						nAPsinnerPanel.add(nAPsLabel);
						nAPsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						nAPsinnerPanel.add(nAPsSlider);		
						nAPsinnerPanel.setBorder(nAPsborder2);
			
						frame.add(nAPsinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					stretchSlider = new JSlider(JSlider.HORIZONTAL, (int)(1 * 1.0),(int) (50 * 1.0), (int) (gCB.getstretch() * 1.0));
						stretchSlider.addChangeListener(new ChirpListener());
						stretchLabel = new JLabel();
						Border stretchBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						stretchLabel.setBorder(stretchBorder1);
						updatestretchLabel();
						
						Border stretchborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel stretchinnerPanel = new JPanel();
							
						stretchinnerPanel.setLayout(new BoxLayout(stretchinnerPanel, BoxLayout.Y_AXIS));
						stretchinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						stretchinnerPanel.add(stretchLabel);
						stretchinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						stretchinnerPanel.add(stretchSlider);		
						stretchinnerPanel.setBorder(stretchborder2);
			
						frame.add(stretchinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kiapSlider = new JSlider(JSlider.HORIZONTAL, (int)(-0.98 * 100.0),(int) (0.98 * 100.0), (int) (gCB.getkiap() * 100.0));
						kiapSlider.addChangeListener(new ChirpListener());
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
		class ChirpListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == nAPsSlider) {
			gCB.setnAPs((double) (nAPsSlider.getValue()/1.0));
				updatenAPsLabel();
			}
			if(ce.getSource() == stretchSlider) {
			gCB.setstretch((double) (stretchSlider.getValue()/1.0));
				updatestretchLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class ChirpItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ChirpActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatenAPsLabel() {
		nAPsLabel.setText("Stages " + String.format("%4.1f", gCB.getnAPs()));		
		}		
		private void updatestretchLabel() {
		stretchLabel.setText("Stretch " + String.format("%4.1f", gCB.getstretch()));		
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
