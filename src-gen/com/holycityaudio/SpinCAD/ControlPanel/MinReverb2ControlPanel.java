/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * MinReverb2ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.MinReverb2CADBlock;

@SuppressWarnings("unused")
public class MinReverb2ControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private MinReverb2CADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider kiapSlider;
	JLabel  kiapLabel;	
	JSlider ap1lengthSlider;
	JLabel  ap1lengthLabel;	
	JSlider ap2lengthSlider;
	JLabel  ap2lengthLabel;	
	JSlider ap3lengthSlider;
	JLabel  ap3lengthLabel;	
	JSlider ap4lengthSlider;
	JLabel  ap4lengthLabel;	
	JSlider klapSlider;
	JLabel  klapLabel;	
	JSlider lap1lengthSlider;
	JLabel  lap1lengthLabel;	
	JSlider del1lengthSlider;
	JLabel  del1lengthLabel;	
	JSlider lap2lengthSlider;
	JLabel  lap2lengthLabel;	
	JSlider del2lengthSlider;
	JLabel  del2lengthLabel;	

public MinReverb2ControlPanel(MinReverb2CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Small Reverb");
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
						gainSlider.addChangeListener(new MinReverb2Listener());
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
					kiapSlider = new JSlider(JSlider.HORIZONTAL, (int)(-0.90 * 100.0),(int) (0.90 * 100.0), (int) (gCB.getkiap() * 100.0));
						kiapSlider.addChangeListener(new MinReverb2Listener());
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
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap1lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap1length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap1lengthSlider.addChangeListener(new MinReverb2Listener());
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
					ap2lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap2length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap2lengthSlider.addChangeListener(new MinReverb2Listener());
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
					ap3lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap3length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap3lengthSlider.addChangeListener(new MinReverb2Listener());
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
					ap4lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(20 * 1),(int) (2000 * 1), (int) (gCB.getap4length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap4lengthSlider.addChangeListener(new MinReverb2Listener());
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
					klapSlider = new JSlider(JSlider.HORIZONTAL, (int)(-0.90 * 100.0),(int) (0.90 * 100.0), (int) (gCB.getklap() * 100.0));
						klapSlider.addChangeListener(new MinReverb2Listener());
						klapLabel = new JLabel();
						Border klapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						klapLabel.setBorder(klapBorder1);
						updateklapLabel();
						
						Border klapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel klapinnerPanel = new JPanel();
							
						klapinnerPanel.setLayout(new BoxLayout(klapinnerPanel, BoxLayout.Y_AXIS));
						klapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						klapinnerPanel.add(klapLabel);
						klapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						klapinnerPanel.add(klapSlider);		
						klapinnerPanel.setBorder(klapborder2);
			
						frame.add(klapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					lap1lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(3500 * 1),(int) (5000 * 1), (int) (gCB.getlap1length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						lap1lengthSlider.addChangeListener(new MinReverb2Listener());
						lap1lengthLabel = new JLabel();
						Border lap1lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						lap1lengthLabel.setBorder(lap1lengthBorder1);
						updatelap1lengthLabel();
						
						Border lap1lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel lap1lengthinnerPanel = new JPanel();
							
						lap1lengthinnerPanel.setLayout(new BoxLayout(lap1lengthinnerPanel, BoxLayout.Y_AXIS));
						lap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						lap1lengthinnerPanel.add(lap1lengthLabel);
						lap1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						lap1lengthinnerPanel.add(lap1lengthSlider);		
						lap1lengthinnerPanel.setBorder(lap1lengthborder2);
			
						frame.add(lap1lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					del1lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(5000 * 1),(int) (9000 * 1), (int) (gCB.getdel1length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						del1lengthSlider.addChangeListener(new MinReverb2Listener());
						del1lengthLabel = new JLabel();
						Border del1lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						del1lengthLabel.setBorder(del1lengthBorder1);
						updatedel1lengthLabel();
						
						Border del1lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel del1lengthinnerPanel = new JPanel();
							
						del1lengthinnerPanel.setLayout(new BoxLayout(del1lengthinnerPanel, BoxLayout.Y_AXIS));
						del1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						del1lengthinnerPanel.add(del1lengthLabel);
						del1lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						del1lengthinnerPanel.add(del1lengthSlider);		
						del1lengthinnerPanel.setBorder(del1lengthborder2);
			
						frame.add(del1lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					lap2lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(3500 * 1),(int) (5000 * 1), (int) (gCB.getlap2length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						lap2lengthSlider.addChangeListener(new MinReverb2Listener());
						lap2lengthLabel = new JLabel();
						Border lap2lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						lap2lengthLabel.setBorder(lap2lengthBorder1);
						updatelap2lengthLabel();
						
						Border lap2lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel lap2lengthinnerPanel = new JPanel();
							
						lap2lengthinnerPanel.setLayout(new BoxLayout(lap2lengthinnerPanel, BoxLayout.Y_AXIS));
						lap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						lap2lengthinnerPanel.add(lap2lengthLabel);
						lap2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						lap2lengthinnerPanel.add(lap2lengthSlider);		
						lap2lengthinnerPanel.setBorder(lap2lengthborder2);
			
						frame.add(lap2lengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					del2lengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(5000 * 1),(int) (9000 * 1), (int) (gCB.getdel2length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						del2lengthSlider.addChangeListener(new MinReverb2Listener());
						del2lengthLabel = new JLabel();
						Border del2lengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						del2lengthLabel.setBorder(del2lengthBorder1);
						updatedel2lengthLabel();
						
						Border del2lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel del2lengthinnerPanel = new JPanel();
							
						del2lengthinnerPanel.setLayout(new BoxLayout(del2lengthinnerPanel, BoxLayout.Y_AXIS));
						del2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						del2lengthinnerPanel.add(del2lengthLabel);
						del2lengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						del2lengthinnerPanel.add(del2lengthSlider);		
						del2lengthinnerPanel.setBorder(del2lengthborder2);
			
						frame.add(del2lengthinnerPanel);
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
		class MinReverb2Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
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
			if(ce.getSource() == klapSlider) {
			gCB.setklap((double) (klapSlider.getValue()/100.0));
				updateklapLabel();
			}
			if(ce.getSource() == lap1lengthSlider) {
			gCB.setlap1length((double) (lap1lengthSlider.getValue()/1));			    					
				updatelap1lengthLabel();
			}
			if(ce.getSource() == del1lengthSlider) {
			gCB.setdel1length((double) (del1lengthSlider.getValue()/1));			    					
				updatedel1lengthLabel();
			}
			if(ce.getSource() == lap2lengthSlider) {
			gCB.setlap2length((double) (lap2lengthSlider.getValue()/1));			    					
				updatelap2lengthLabel();
			}
			if(ce.getSource() == del2lengthSlider) {
			gCB.setdel2length((double) (del2lengthSlider.getValue()/1));			    					
				updatedel2lengthLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class MinReverb2ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class MinReverb2ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatekiapLabel() {
		kiapLabel.setText("Input All Pass: " + String.format("%4.2f", gCB.getkiap()));		
		}		
		private void updateap1lengthLabel() {
		ap1lengthLabel.setText("Input AP1 (msec) " + String.format("%4.1f", (1000 * gCB.getap1length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap2lengthLabel() {
		ap2lengthLabel.setText("Input AP2 (msec) " + String.format("%4.1f", (1000 * gCB.getap2length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap3lengthLabel() {
		ap3lengthLabel.setText("Input AP3 (msec) " + String.format("%4.1f", (1000 * gCB.getap3length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap4lengthLabel() {
		ap4lengthLabel.setText("Input AP4 (msec) " + String.format("%4.1f", (1000 * gCB.getap4length())/ElmProgram.getSamplerate()));		
		}		
		private void updateklapLabel() {
		klapLabel.setText("Loop All Pass " + String.format("%4.2f", gCB.getklap()));		
		}		
		private void updatelap1lengthLabel() {
		lap1lengthLabel.setText("Loop AP1 (msec) " + String.format("%4.0f", (1000 * gCB.getlap1length())/ElmProgram.getSamplerate()));		
		}		
		private void updatedel1lengthLabel() {
		del1lengthLabel.setText("Loop Delay 1 (msec) " + String.format("%4.0f", (1000 * gCB.getdel1length())/ElmProgram.getSamplerate()));		
		}		
		private void updatelap2lengthLabel() {
		lap2lengthLabel.setText("Loop AP2 (msec) " + String.format("%4.0f", (1000 * gCB.getlap2length())/ElmProgram.getSamplerate()));		
		}		
		private void updatedel2lengthLabel() {
		del2lengthLabel.setText("Loop Delay 2 (msec) " + String.format("%4.0f", (1000 * gCB.getdel2length())/ElmProgram.getSamplerate()));		
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
