/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverb_delay_line_01ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.reverb_delay_line_01CADBlock;

@SuppressWarnings("unused")
public class reverb_delay_line_01ControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private reverb_delay_line_01CADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider delayLengthSlider;
	JLabel  delayLengthLabel;	
	JSlider krtSlider;
	JLabel  krtLabel;	
	JSlider lpdfSlider;
	JLabel  lpdfLabel;	
	JSlider ap1LengthSlider;
	JLabel  ap1LengthLabel;	
	JSlider ap1kapSlider;
	JLabel  ap1kapLabel;	
	JSlider ap2LengthSlider;
	JLabel  ap2LengthLabel;	
	JSlider ap2kapSlider;
	JLabel  ap2kapLabel;	

public reverb_delay_line_01ControlPanel(reverb_delay_line_01CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Reverb_Delay_Line_01");
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
						gainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-24),(int) (0), (int) (20 * Math.log10(gCB.getgain())));
						gainSlider.addChangeListener(new reverb_delay_line_01Listener());
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
					delayLengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (5000 * 1), (int) (gCB.getdelayLength() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						delayLengthSlider.addChangeListener(new reverb_delay_line_01Listener());
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
					krtSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkrt() * 100.0));
						krtSlider.addChangeListener(new reverb_delay_line_01Listener());
						krtLabel = new JLabel();
						Border krtBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						krtLabel.setBorder(krtBorder1);
						updatekrtLabel();
						
						Border krtborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel krtinnerPanel = new JPanel();
							
						krtinnerPanel.setLayout(new BoxLayout(krtinnerPanel, BoxLayout.Y_AXIS));
						krtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						krtinnerPanel.add(krtLabel);
						krtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						krtinnerPanel.add(krtSlider);		
						krtinnerPanel.setBorder(krtborder2);
			
						frame.add(krtinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						lpdfSlider = SpinCADBlock.LogSlider(500,2500,gCB.getlpdf(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						lpdfSlider.addChangeListener(new reverb_delay_line_01Listener());
						lpdfLabel = new JLabel();
						Border lpdfBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						lpdfLabel.setBorder(lpdfBorder1);
						updatelpdfLabel();
						
						Border lpdfborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel lpdfinnerPanel = new JPanel();
							
						lpdfinnerPanel.setLayout(new BoxLayout(lpdfinnerPanel, BoxLayout.Y_AXIS));
						lpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						lpdfinnerPanel.add(lpdfLabel);
						lpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						lpdfinnerPanel.add(lpdfSlider);		
						lpdfinnerPanel.setBorder(lpdfborder2);
			
						frame.add(lpdfinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap1LengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (1500 * 1), (int) (gCB.getap1Length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap1LengthSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap1LengthLabel = new JLabel();
						Border ap1LengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap1LengthLabel.setBorder(ap1LengthBorder1);
						updateap1LengthLabel();
						
						Border ap1Lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap1LengthinnerPanel = new JPanel();
							
						ap1LengthinnerPanel.setLayout(new BoxLayout(ap1LengthinnerPanel, BoxLayout.Y_AXIS));
						ap1LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap1LengthinnerPanel.add(ap1LengthLabel);
						ap1LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap1LengthinnerPanel.add(ap1LengthSlider);		
						ap1LengthinnerPanel.setBorder(ap1Lengthborder2);
			
						frame.add(ap1LengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap1kapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getap1kap() * 100.0));
						ap1kapSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap1kapLabel = new JLabel();
						Border ap1kapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap1kapLabel.setBorder(ap1kapBorder1);
						updateap1kapLabel();
						
						Border ap1kapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap1kapinnerPanel = new JPanel();
							
						ap1kapinnerPanel.setLayout(new BoxLayout(ap1kapinnerPanel, BoxLayout.Y_AXIS));
						ap1kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap1kapinnerPanel.add(ap1kapLabel);
						ap1kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap1kapinnerPanel.add(ap1kapSlider);		
						ap1kapinnerPanel.setBorder(ap1kapborder2);
			
						frame.add(ap1kapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap2LengthSlider = new JSlider(JSlider.HORIZONTAL, (int)(0 * 1),(int) (1500 * 1), (int) (gCB.getap2Length() * 1));
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						ap2LengthSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap2LengthLabel = new JLabel();
						Border ap2LengthBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap2LengthLabel.setBorder(ap2LengthBorder1);
						updateap2LengthLabel();
						
						Border ap2Lengthborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap2LengthinnerPanel = new JPanel();
							
						ap2LengthinnerPanel.setLayout(new BoxLayout(ap2LengthinnerPanel, BoxLayout.Y_AXIS));
						ap2LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap2LengthinnerPanel.add(ap2LengthLabel);
						ap2LengthinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap2LengthinnerPanel.add(ap2LengthSlider);		
						ap2LengthinnerPanel.setBorder(ap2Lengthborder2);
			
						frame.add(ap2LengthinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					ap2kapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getap2kap() * 100.0));
						ap2kapSlider.addChangeListener(new reverb_delay_line_01Listener());
						ap2kapLabel = new JLabel();
						Border ap2kapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						ap2kapLabel.setBorder(ap2kapBorder1);
						updateap2kapLabel();
						
						Border ap2kapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel ap2kapinnerPanel = new JPanel();
							
						ap2kapinnerPanel.setLayout(new BoxLayout(ap2kapinnerPanel, BoxLayout.Y_AXIS));
						ap2kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap2kapinnerPanel.add(ap2kapLabel);
						ap2kapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						ap2kapinnerPanel.add(ap2kapSlider);		
						ap2kapinnerPanel.setBorder(ap2kapborder2);
			
						frame.add(ap2kapinnerPanel);
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
		class reverb_delay_line_01Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == delayLengthSlider) {
			gCB.setdelayLength((double) (delayLengthSlider.getValue()/1));			    					
				updatedelayLengthLabel();
			}
			if(ce.getSource() == krtSlider) {
			gCB.setkrt((double) (krtSlider.getValue()/100.0));
				updatekrtLabel();
			}
			if(ce.getSource() == lpdfSlider) {
			gCB.setlpdf((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(lpdfSlider.getValue()), 100.0)));
				updatelpdfLabel();
			}
			if(ce.getSource() == ap1LengthSlider) {
			gCB.setap1Length((double) (ap1LengthSlider.getValue()/1));			    					
				updateap1LengthLabel();
			}
			if(ce.getSource() == ap1kapSlider) {
			gCB.setap1kap((double) (ap1kapSlider.getValue()/100.0));
				updateap1kapLabel();
			}
			if(ce.getSource() == ap2LengthSlider) {
			gCB.setap2Length((double) (ap2LengthSlider.getValue()/1));			    					
				updateap2LengthLabel();
			}
			if(ce.getSource() == ap2kapSlider) {
			gCB.setap2kap((double) (ap2kapSlider.getValue()/100.0));
				updateap2kapLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class reverb_delay_line_01ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class reverb_delay_line_01ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatedelayLengthLabel() {
		delayLengthLabel.setText("Delay Line Time " + String.format("%4.0f", (1000 * gCB.getdelayLength())/ElmProgram.getSamplerate()));		
		}		
		private void updatekrtLabel() {
		krtLabel.setText("Reverb Time Coefficient " + String.format("%4.2f", gCB.getkrt()));		
		}		
		private void updatelpdfLabel() {
		lpdfLabel.setText("Damping Freq Hi " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getlpdf())) + " Hz");		
		}		
		private void updateap1LengthLabel() {
		ap1LengthLabel.setText("Allpass #1 Time " + String.format("%4.0f", (1000 * gCB.getap1Length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap1kapLabel() {
		ap1kapLabel.setText("All-Pass #1 Coefficient " + String.format("%4.2f", gCB.getap1kap()));		
		}		
		private void updateap2LengthLabel() {
		ap2LengthLabel.setText("Allpass #2 Time " + String.format("%4.0f", (1000 * gCB.getap2Length())/ElmProgram.getSamplerate()));		
		}		
		private void updateap2kapLabel() {
		ap2kapLabel.setText("All-Pass #2 Coefficient " + String.format("%4.2f", gCB.getap2kap()));		
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
