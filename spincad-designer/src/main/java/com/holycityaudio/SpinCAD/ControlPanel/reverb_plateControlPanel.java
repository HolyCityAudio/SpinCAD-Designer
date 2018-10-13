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
import com.holycityaudio.SpinCAD.CADBlocks.reverb_plateCADBlock;

@SuppressWarnings("unused")
public class reverb_plateControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private reverb_plateCADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider krtSlider;
	JLabel  krtLabel;	
	JSlider hpdfSlider;
	JLabel  hpdfLabel;	
	JSlider inputkapSlider;
	JLabel  inputkapLabel;	
	JSlider dlkapSlider;
	JLabel  dlkapLabel;	
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
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					krtSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkrt() * 100.0));
						krtSlider.addChangeListener(new reverb_plateListener());
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
					hpdfSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.001 * 100.0),(int) (0.15 * 100.0), (int) (gCB.gethpdf() * 100.0));
						hpdfSlider.addChangeListener(new reverb_plateListener());
						hpdfLabel = new JLabel();
						Border hpdfBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						hpdfLabel.setBorder(hpdfBorder1);
						updatehpdfLabel();
						
						Border hpdfborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel hpdfinnerPanel = new JPanel();
							
						hpdfinnerPanel.setLayout(new BoxLayout(hpdfinnerPanel, BoxLayout.Y_AXIS));
						hpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						hpdfinnerPanel.add(hpdfLabel);
						hpdfinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						hpdfinnerPanel.add(hpdfSlider);		
						hpdfinnerPanel.setBorder(hpdfborder2);
			
						frame.add(hpdfinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					inputkapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getinputkap() * 100.0));
						inputkapSlider.addChangeListener(new reverb_plateListener());
						inputkapLabel = new JLabel();
						Border inputkapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						inputkapLabel.setBorder(inputkapBorder1);
						updateinputkapLabel();
						
						Border inputkapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel inputkapinnerPanel = new JPanel();
							
						inputkapinnerPanel.setLayout(new BoxLayout(inputkapinnerPanel, BoxLayout.Y_AXIS));
						inputkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						inputkapinnerPanel.add(inputkapLabel);
						inputkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						inputkapinnerPanel.add(inputkapSlider);		
						inputkapinnerPanel.setBorder(inputkapborder2);
			
						frame.add(inputkapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					dlkapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.05 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getdlkap() * 100.0));
						dlkapSlider.addChangeListener(new reverb_plateListener());
						dlkapLabel = new JLabel();
						Border dlkapBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						dlkapLabel.setBorder(dlkapBorder1);
						updatedlkapLabel();
						
						Border dlkapborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel dlkapinnerPanel = new JPanel();
							
						dlkapinnerPanel.setLayout(new BoxLayout(dlkapinnerPanel, BoxLayout.Y_AXIS));
						dlkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						dlkapinnerPanel.add(dlkapLabel);
						dlkapinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						dlkapinnerPanel.add(dlkapSlider);		
						dlkapinnerPanel.setBorder(dlkapborder2);
			
						frame.add(dlkapinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
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
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
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
			if(ce.getSource() == krtSlider) {
			gCB.setkrt((double) (krtSlider.getValue()/100.0));
				updatekrtLabel();
			}
			if(ce.getSource() == hpdfSlider) {
			gCB.sethpdf((double) (hpdfSlider.getValue()/100.0));
				updatehpdfLabel();
			}
			if(ce.getSource() == inputkapSlider) {
			gCB.setinputkap((double) (inputkapSlider.getValue()/100.0));
				updateinputkapLabel();
			}
			if(ce.getSource() == dlkapSlider) {
			gCB.setdlkap((double) (dlkapSlider.getValue()/100.0));
				updatedlkapLabel();
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

		// add item state changed listener for Checkbox
		class reverb_plateItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class reverb_plateActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input_Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updatekrtLabel() {
		krtLabel.setText("Reverb Time Coefficient " + String.format("%4.2f", gCB.getkrt()));		
		}		
		private void updatehpdfLabel() {
		hpdfLabel.setText("Low Freq Damping Frequency " + String.format("%4.2f", gCB.gethpdf()));		
		}		
		private void updateinputkapLabel() {
		inputkapLabel.setText("Input All-Pass Coefficient " + String.format("%4.2f", gCB.getinputkap()));		
		}		
		private void updatedlkapLabel() {
		dlkapLabel.setText("Delay All-Pass Coefficient " + String.format("%4.2f", gCB.getdlkap()));		
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
