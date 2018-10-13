/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * control_smootherControlPanel.java
 * Copyright (C)2013 - Gary Worsham 
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
package com.holycityaudio.SpinCAD.CADBlocks;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import com.holycityaudio.SpinCAD.SpinCADBlock;

public class control_smootherControlPanelA {
	private JFrame frame;

	private control_smootherACADBlock gCB;
	private boolean silentGUIChange = false;
	
	// declare the controls
	JSlider filtSlider;
	JLabel  filtLabel;	
	JSpinner filtSpinner;


	public control_smootherControlPanelA(control_smootherACADBlock genericCADBlock) {

		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Smoother");

				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				JPanel topLine = new JPanel();
				topLine.setLayout(new BoxLayout(topLine, BoxLayout.X_AXIS));
				// Label
				filtLabel = new JLabel("  Frequency (Hz)  ");
				topLine.add(filtLabel);

				// Spinner
				SpinnerNumberModel filtSpinnerNumberModel = new SpinnerNumberModel(SpinCADBlock.filtToFreq(gCB.getfilt()) * 100, 0.51, 10000.00, 0.01);
				filtSpinner = new JSpinner(filtSpinnerNumberModel);
				JSpinner.NumberEditor editor = (JSpinner.NumberEditor)filtSpinner.getEditor();  

				DecimalFormat format = editor.getFormat();  
				format.setMinimumFractionDigits(2);  
				format.setMaximumFractionDigits(2);  
				editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
				Dimension d = filtSpinner.getPreferredSize();  
				d.width = 55;  
				filtSpinner.setPreferredSize(d);  

				updatefiltSpinner();
				filtSpinner.addChangeListener(new control_smootherListener());
				topLine.add(filtSpinner);
				topLine.setVisible(true);

				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.getContentPane().add(topLine);

				frame.add(Box.createRigidArea(new Dimension(5,5)));			

				// JSlider value is converted to an exponent representing filter frequency, so 
				// -29 => 10^(-29/100) = 0.5129 Hz which determined is the lowest practical frequency possible
				// with the FV-1's coefficient resolution.
				// 100 => 10^(100/100) = 10 Hz.

				filtSlider = SpinCADBlock.LogSlider(0.5129,10.0,gCB.getfilt(), "LOGFREQ", 100.0);

				filtSlider.addChangeListener(new control_smootherListener());
				frame.getContentPane().add(filtSlider);		
				frame.add(Box.createRigidArea(new Dimension(5,4)));			

				frame.addWindowListener(new MyWindowListener());

				frame.setVisible(true);		
				frame.pack();
				frame.setResizable(false);

				frame.setLocation(gCB.getX() + 100, gCB.getY() + 100);
				frame.setAlwaysOnTop(true);
			}
		});
	}

	// add change listener for Sliders 
	class control_smootherListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(silentGUIChange == true) 
				return;
			if(ce.getSource() == filtSlider) {
				gCB.setfilt(SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval(filtSlider.getValue(), 100.0)));
				updatefiltSpinner();
			}
			if(ce.getSource() == filtSpinner) {
				gCB.setfilt(SpinCADBlock.freqToFilt((double) filtSpinner.getValue()));
				updatefiltSlider();
			}
		}
	}


	private void updatefiltSpinner() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					silentGUIChange = true;
					filtSpinner.setValue(SpinCADBlock.filtToFreq(gCB.getfilt()));
				}
				finally {
					silentGUIChange = false;   	    	  
				}
			}
		});
	}

	private void updatefiltSlider() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					silentGUIChange = true;
					filtSlider.setValue((int) (100 * Math.log10(SpinCADBlock.filtToFreq(gCB.getfilt()))));
				}
				finally {
					silentGUIChange = false;   	    	  
				}
			}
		});
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
