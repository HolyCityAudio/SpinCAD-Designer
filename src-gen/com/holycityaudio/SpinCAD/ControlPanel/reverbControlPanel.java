/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * reverbControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.reverbCADBlock;

public class reverbControlPanel extends spinCADControlPanel {
	private JFrame frame;

	private reverbCADBlock gCB;
	// declare the controls
	JSlider gainSlider;
	JLabel  gainLabel;	
	JSlider kiapSlider;
	JLabel  kiapLabel;	
	JSlider nDLsSlider;
	JLabel  nDLsLabel;	
	JSlider klapSlider;
	JLabel  klapLabel;	
	JSlider kflSlider;
	JLabel  kflLabel;	
	JSlider kfhSlider;
	JLabel  kfhLabel;	

public reverbControlPanel(reverbCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Reverb");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					// dB level slider goes in steps of 1 dB
						gainSlider = new JSlider(JSlider.HORIZONTAL, (int)(-18),(int) (0), (int) (20 * Math.log10(gCB.getgain())));
						gainSlider.addChangeListener(new reverbListener());
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
					kiapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.25 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getkiap() * 100.0));
						kiapSlider.addChangeListener(new reverbListener());
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
					nDLsSlider = new JSlider(JSlider.HORIZONTAL, (int)(2 * 1.0),(int) (4 * 1.0), (int) (gCB.getnDLs() * 1.0));
						nDLsSlider.addChangeListener(new reverbListener());
						nDLsLabel = new JLabel();
						Border nDLsBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						nDLsLabel.setBorder(nDLsBorder1);
						updatenDLsLabel();
						
						Border nDLsborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel nDLsinnerPanel = new JPanel();
							
						nDLsinnerPanel.setLayout(new BoxLayout(nDLsinnerPanel, BoxLayout.Y_AXIS));
						nDLsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						nDLsinnerPanel.add(nDLsLabel);
						nDLsinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						nDLsinnerPanel.add(nDLsSlider);		
						nDLsinnerPanel.setBorder(nDLsborder2);
			
						frame.add(nDLsinnerPanel);
			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					klapSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.25 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getklap() * 100.0));
						klapSlider.addChangeListener(new reverbListener());
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
					kflSlider = SpinCADBlock.LogFilterSlider(500,5000,gCB.getkfl());
						kflSlider.addChangeListener(new reverbListener());
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
					kfhSlider = SpinCADBlock.LogFilterSlider(40,1000,gCB.getkfh());
						kfhSlider.addChangeListener(new reverbListener());
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
		class reverbListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == gainSlider) {
			gCB.setgain((double) (gainSlider.getValue()/1.0));			    					
				updategainLabel();
			}
			if(ce.getSource() == kiapSlider) {
			gCB.setkiap((double) (kiapSlider.getValue()/100.0));
				updatekiapLabel();
			}
			if(ce.getSource() == nDLsSlider) {
			gCB.setnDLs((double) (nDLsSlider.getValue()/1.0));
				updatenDLsLabel();
			}
			if(ce.getSource() == klapSlider) {
			gCB.setklap((double) (klapSlider.getValue()/100.0));
				updateklapLabel();
			}
			if(ce.getSource() == kflSlider) {
			gCB.setkfl((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kflSlider.getValue()), 100.0)));
				updatekflLabel();
			}
			if(ce.getSource() == kfhSlider) {
			gCB.setkfh((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(kfhSlider.getValue()), 100.0)));
				updatekfhLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class reverbItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class reverbActionListener implements java.awt.event.ActionListener { 
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
		private void updatenDLsLabel() {
		nDLsLabel.setText("Delay_Stages: " + String.format("%4.0f", gCB.getnDLs()));		
		}		
		private void updateklapLabel() {
		klapLabel.setText("Loop All Pass " + String.format("%4.2f", gCB.getklap()));		
		}		
		private void updatekflLabel() {
		kflLabel.setText("Low Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfl())) + " Hz");		
		}		
		private void updatekfhLabel() {
		kfhLabel.setText("High Pass " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getkfh())) + " Hz");		
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
