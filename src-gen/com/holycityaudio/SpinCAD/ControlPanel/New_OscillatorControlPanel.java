/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * New_OscillatorControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.New_OscillatorCADBlock;

@SuppressWarnings("unused")
public class New_OscillatorControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private New_OscillatorCADBlock gCB;
	// declare the controls
	JSlider freqVarSlider;
	JLabel  freqVarLabel;	

public New_OscillatorControlPanel(New_OscillatorCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Oscillator II");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					//---------------------------------------------
					// LOGFREQ is used for single pole filters
					// multiplier is points per decade here
						freqVarSlider = SpinCADBlock.LogSlider(20,5000,gCB.getfreqVar(), "LOGFREQ", 100.0);
					//---------------------------------------------
					// LOGFREQ2 is used for 2-pole SVF
					// ---------------------------------------------						
						freqVarSlider.addChangeListener(new New_OscillatorListener());
						freqVarLabel = new JLabel();
						Border freqVarBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						freqVarLabel.setBorder(freqVarBorder1);
						updatefreqVarLabel();
						
						Border freqVarborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel freqVarinnerPanel = new JPanel();
							
						freqVarinnerPanel.setLayout(new BoxLayout(freqVarinnerPanel, BoxLayout.Y_AXIS));
						freqVarinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						freqVarinnerPanel.add(freqVarLabel);
						freqVarinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						freqVarinnerPanel.add(freqVarSlider);		
						freqVarinnerPanel.setBorder(freqVarborder2);
			
						frame.add(freqVarinnerPanel);
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
		class New_OscillatorListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == freqVarSlider) {
			gCB.setfreqVar((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(freqVarSlider.getValue()), 100.0)));
				updatefreqVarLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class New_OscillatorItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class New_OscillatorActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatefreqVarLabel() {
		freqVarLabel.setText("Frequency (Hz) " + String.format("%4.1f", SpinCADBlock.filtToFreq(gCB.getfreqVar())) + " Hz");		
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
