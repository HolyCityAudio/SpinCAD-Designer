/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * control_smootherControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.control_smootherCADBlock;

@SuppressWarnings("unused")
public class control_smootherControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private control_smootherCADBlock gCB;
	// declare the controls
	JSlider filtSlider;
	JLabel  filtLabel;
	JSpinner  filtSpinner;	
	private boolean filtsilentGUIChange = false;	

public control_smootherControlPanel(control_smootherCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Smoother");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			// multiplier is points per decade here
				filtSlider = SpinCADBlock.LogSlider(0.51,15.00,gCB.getfilt(), "LOGFREQ", 100.0);
				filtSlider.addChangeListener(new control_smootherListener());
				
				filtLabel = new JLabel("Frequency (Hz)");
			
				SpinnerNumberModel filtSpinnerNumberModel = new SpinnerNumberModel(SpinCADBlock.filtToFreq(gCB.getfilt()) * 100, 0.51, 10000.00, 0.01);
			
				filtSpinner = new JSpinner(filtSpinnerNumberModel);
				JSpinner.NumberEditor filteditor = (JSpinner.NumberEditor)filtSpinner.getEditor();  
				DecimalFormat filtformat = filteditor.getFormat();  
			 			filtformat.setMinimumFractionDigits(2);  
				filtformat.setMaximumFractionDigits(2);  
				filteditor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
				Dimension filtd = filtSpinner.getPreferredSize();  
				filtd.width = 25;  
				filtSpinner.setPreferredSize(filtd);  
				
				updatefiltSpinner();
				filtSpinner.addChangeListener(new control_smootherListener());
				
				JPanel filttopLine = new JPanel();
				filttopLine.setLayout(new BoxLayout(filttopLine, BoxLayout.X_AXIS));
			
				filttopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				filttopLine.add(filtLabel);
				filttopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				filttopLine.add(filtSpinner);
				
				Border filtborder2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				filttopLine.setBorder(filtborder2);
			
				Border filtborder1 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel filtinnerPanel = new JPanel();
					
				filtinnerPanel.setLayout(new BoxLayout(filtinnerPanel, BoxLayout.Y_AXIS));
				filtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				filtinnerPanel.add(filttopLine);
				filtinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				filtinnerPanel.add(filtSlider);		
				filtinnerPanel.setBorder(filtborder1);
			
				frame.add(filtinnerPanel);
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
		class control_smootherListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(filtsilentGUIChange == true) 
				return;
			
			if(ce.getSource() == filtSlider) {
			gCB.setfilt((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(filtSlider.getValue()), 100.0)));
				updatefiltSpinner();
			}
			if(ce.getSource() == filtSpinner) {
			gCB.setfilt(SpinCADBlock.freqToFilt((double)(filtSpinner.getValue())));
				updatefiltSlider();
			}
			}
		}

		// add item state changed listener for Checkbox
		class control_smootherItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class control_smootherActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatefiltSpinner() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					filtsilentGUIChange = true;
		filtSpinner.setValue(SpinCADBlock.filtToFreq(gCB.getfilt()));
				}
				finally {
					filtsilentGUIChange = false;   	    	  
				}
			}
		});
		}	
		
		private void updatefiltSlider() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					filtsilentGUIChange = true;
		filtSlider.setValue((int) (100 * Math.log10(SpinCADBlock.filtToFreq(gCB.getfilt()))));		
				}
				finally {
					filtsilentGUIChange = false;   	    	  
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
