/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * rom_rev2ControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.rom_rev2CADBlock;

@SuppressWarnings("unused")
public class rom_rev2ControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private rom_rev2CADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider revTimeMaxSlider;
	JLabel  revTimeMaxLabel;	
	JSlider kapiSlider;
	JLabel  kapiLabel;	
	JSlider kapd1Slider;
	JLabel  kapd1Label;	
	JSlider kapd2Slider;
	JLabel  kapd2Label;	
	JSlider kflSlider;
	JLabel  kflLabel;	
	JSlider kfhSlider;
	JLabel  kfhLabel;	
	JSlider memscaleSlider;
	JLabel  memscaleLabel;	

public rom_rev2ControlPanel(rom_rev2CADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("ROM Reverb 2");
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
						gainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0.0), (int) (20 * Math.log10(gCB.getgain())));
						gainSlider.addChangeListener(new rom_rev2Listener());
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
					revTimeMaxSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.9 * 100.0), (int) (gCB.getrevTimeMax() * 100.0));
						revTimeMaxSlider.addChangeListener(new rom_rev2Listener());
						revTimeMaxLabel = new JLabel();
						Border revTimeMaxBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						revTimeMaxLabel.setBorder(revTimeMaxBorder1);
						updaterevTimeMaxLabel();
						
						Border revTimeMaxborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel revTimeMaxinnerPanel = new JPanel();
							
						revTimeMaxinnerPanel.setLayout(new BoxLayout(revTimeMaxinnerPanel, BoxLayout.Y_AXIS));
						revTimeMaxinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						revTimeMaxinnerPanel.add(revTimeMaxLabel);
						revTimeMaxinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						revTimeMaxinnerPanel.add(revTimeMaxSlider);		
						revTimeMaxinnerPanel.setBorder(revTimeMaxborder2);
			
						frame.add(revTimeMaxinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kapiSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapi() * 100.0));
						kapiSlider.addChangeListener(new rom_rev2Listener());
						kapiLabel = new JLabel();
						Border kapiBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kapiLabel.setBorder(kapiBorder1);
						updatekapiLabel();
						
						Border kapiborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kapiinnerPanel = new JPanel();
							
						kapiinnerPanel.setLayout(new BoxLayout(kapiinnerPanel, BoxLayout.Y_AXIS));
						kapiinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kapiinnerPanel.add(kapiLabel);
						kapiinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kapiinnerPanel.add(kapiSlider);		
						kapiinnerPanel.setBorder(kapiborder2);
			
						frame.add(kapiinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kapd1Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapd1() * 100.0));
						kapd1Slider.addChangeListener(new rom_rev2Listener());
						kapd1Label = new JLabel();
						Border kapd1Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kapd1Label.setBorder(kapd1Border1);
						updatekapd1Label();
						
						Border kapd1border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kapd1innerPanel = new JPanel();
							
						kapd1innerPanel.setLayout(new BoxLayout(kapd1innerPanel, BoxLayout.Y_AXIS));
						kapd1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kapd1innerPanel.add(kapd1Label);
						kapd1innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kapd1innerPanel.add(kapd1Slider);		
						kapd1innerPanel.setBorder(kapd1border2);
			
						frame.add(kapd1innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					kapd2Slider = new JSlider(JSlider.HORIZONTAL, (int)(0.3 * 100.0),(int) (0.8 * 100.0), (int) (gCB.getkapd2() * 100.0));
						kapd2Slider.addChangeListener(new rom_rev2Listener());
						kapd2Label = new JLabel();
						Border kapd2Border1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kapd2Label.setBorder(kapd2Border1);
						updatekapd2Label();
						
						Border kapd2border2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kapd2innerPanel = new JPanel();
							
						kapd2innerPanel.setLayout(new BoxLayout(kapd2innerPanel, BoxLayout.Y_AXIS));
						kapd2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kapd2innerPanel.add(kapd2Label);
						kapd2innerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kapd2innerPanel.add(kapd2Slider);		
						kapd2innerPanel.setBorder(kapd2border2);
			
						frame.add(kapd2innerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						kflSlider = SpinCADBlock.LogSlider(500,5000,gCB.getkfl(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						kflSlider.addChangeListener(new rom_rev2Listener());
						kflLabel = new JLabel();
						Border kflBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kflLabel.setBorder(kflBorder1);
						updatekflLabel();
						
						Border kflborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kflinnerPanel = new JPanel();
							
						kflinnerPanel.setLayout(new BoxLayout(kflinnerPanel, BoxLayout.Y_AXIS));
						kflinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kflinnerPanel.add(kflLabel);
						kflinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kflinnerPanel.add(kflSlider);		
						kflinnerPanel.setBorder(kflborder2);
			
						frame.add(kflinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						kfhSlider = SpinCADBlock.LogSlider(40,1000,gCB.getkfh(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						kfhSlider.addChangeListener(new rom_rev2Listener());
						kfhLabel = new JLabel();
						Border kfhBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						kfhLabel.setBorder(kfhBorder1);
						updatekfhLabel();
						
						Border kfhborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel kfhinnerPanel = new JPanel();
							
						kfhinnerPanel.setLayout(new BoxLayout(kfhinnerPanel, BoxLayout.Y_AXIS));
						kfhinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kfhinnerPanel.add(kfhLabel);
						kfhinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						kfhinnerPanel.add(kfhSlider);		
						kfhinnerPanel.setBorder(kfhborder2);
			
						frame.add(kfhinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					memscaleSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.5 * 10.0),(int) (1.0 * 10.0), (int) (gCB.getmemscale() * 10.0));
						memscaleSlider.addChangeListener(new rom_rev2Listener());
						memscaleLabel = new JLabel();
						Border memscaleBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						memscaleLabel.setBorder(memscaleBorder1);
						updatememscaleLabel();
						
						Border memscaleborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel memscaleinnerPanel = new JPanel();
							
						memscaleinnerPanel.setLayout(new BoxLayout(memscaleinnerPanel, BoxLayout.Y_AXIS));
						memscaleinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						memscaleinnerPanel.add(memscaleLabel);
						memscaleinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						memscaleinnerPanel.add(memscaleSlider);		
						memscaleinnerPanel.setBorder(memscaleborder2);
			
						frame.add(memscaleinnerPanel);
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
		class rom_rev2Listener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == revTimeMaxSlider) {
			gCB.setrevTimeMax((double) (revTimeMaxSlider.getValue()/100.0));
				updaterevTimeMaxLabel();
			}
			if(ce.getSource() == kapiSlider) {
			gCB.setkapi((double) (kapiSlider.getValue()/100.0));
				updatekapiLabel();
			}
			if(ce.getSource() == kapd1Slider) {
			gCB.setkapd1((double) (kapd1Slider.getValue()/100.0));
				updatekapd1Label();
			}
			if(ce.getSource() == kapd2Slider) {
			gCB.setkapd2((double) (kapd2Slider.getValue()/100.0));
				updatekapd2Label();
			}
			if(ce.getSource() == kflSlider) {
			gCB.setkfl((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kflSlider.getValue()), 100.0)));
				updatekflLabel();
			}
			if(ce.getSource() == kfhSlider) {
			gCB.setkfh((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kfhSlider.getValue()), 100.0)));
				updatekfhLabel();
			}
			if(ce.getSource() == memscaleSlider) {
			gCB.setmemscale((double) (memscaleSlider.getValue()/10.0));
				updatememscaleLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class rom_rev2ItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class rom_rev2ActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updategainLabel() {
		gainLabel.setText("Input Gain " + String.format("%4.1f dB", (20 * Math.log10(gCB.getgain()))));		
		}		
		private void updaterevTimeMaxLabel() {
		revTimeMaxLabel.setText("Reverb Time " + String.format("%4.2f", gCB.getrevTimeMax()));		
		}		
		private void updatekapiLabel() {
		kapiLabel.setText("Input AP Gain " + String.format("%4.2f", gCB.getkapi()));		
		}		
		private void updatekapd1Label() {
		kapd1Label.setText("Delay AP 1 Gain " + String.format("%4.2f", gCB.getkapd1()));		
		}		
		private void updatekapd2Label() {
		kapd2Label.setText("Delay AP 2 Gain " + String.format("%4.2f", gCB.getkapd2()));		
		}		
		private void updatekflLabel() {
		kflLabel.setText("Low Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfl())) + " Hz");		
		}		
		private void updatekfhLabel() {
		kfhLabel.setText("High Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfh())) + " Hz");		
		}		
		private void updatememscaleLabel() {
		memscaleLabel.setText("Delay Scale " + String.format("%4.1f", gCB.getmemscale()));		
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
