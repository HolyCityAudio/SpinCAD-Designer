/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * New_EnvelopeControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.New_EnvelopeCADBlock;

@SuppressWarnings("unused")
public class New_EnvelopeControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private New_EnvelopeCADBlock gCB;
	// declare the controls
	JSlider threshholdSlider;
	JLabel  threshholdLabel;
	JSpinner  threshholdSpinner;	
	private boolean threshholdsilentGUIChange = false;	
	JSlider attackFreqSlider;
	JLabel  attackFreqLabel;
	JSpinner  attackFreqSpinner;	
	private boolean attackFreqsilentGUIChange = false;	
	JSlider decayFreqSlider;
	JLabel  decayFreqLabel;
	JSpinner  decayFreqSpinner;	
	private boolean decayFreqsilentGUIChange = false;	
	JSlider postFreqSlider;
	JLabel  postFreqLabel;
	JSpinner  postFreqSpinner;	
	private boolean postFreqsilentGUIChange = false;	

public New_EnvelopeControlPanel(New_EnvelopeCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Pluck Detector");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
				threshholdSlider.addChangeListener(new New_EnvelopeListener());
				
				threshholdLabel = new JLabel("Threshhold");
			
				SpinnerNumberModel threshholdSpinnerNumberModel = new SpinnerNumberModel(SpinCADBlock.filtToFreq(gCB.getthreshhold()) * 100, 0.51, 10000.00, 0.01);
			
				threshholdSpinner = new JSpinner(threshholdSpinnerNumberModel);
				JSpinner.NumberEditor threshholdeditor = (JSpinner.NumberEditor)threshholdSpinner.getEditor();  
				DecimalFormat threshholdformat = threshholdeditor.getFormat();  
			 			threshholdformat.setMinimumFractionDigits(2);  
				threshholdformat.setMaximumFractionDigits(2);  
				threshholdeditor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
				Dimension threshholdd = threshholdSpinner.getPreferredSize();  
				threshholdd.width = 25;  
				threshholdSpinner.setPreferredSize(threshholdd);  
				
				updatethreshholdSpinner();
				threshholdSpinner.addChangeListener(new New_EnvelopeListener());
				
				JPanel threshholdtopLine = new JPanel();
				threshholdtopLine.setLayout(new BoxLayout(threshholdtopLine, BoxLayout.X_AXIS));
			
				threshholdtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				threshholdtopLine.add(threshholdLabel);
				threshholdtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				threshholdtopLine.add(threshholdSpinner);
				
				Border threshholdborder2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				threshholdtopLine.setBorder(threshholdborder2);
			
				Border threshholdborder1 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel threshholdinnerPanel = new JPanel();
					
				threshholdinnerPanel.setLayout(new BoxLayout(threshholdinnerPanel, BoxLayout.Y_AXIS));
				threshholdinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				threshholdinnerPanel.add(threshholdtopLine);
				threshholdinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				threshholdinnerPanel.add(threshholdSlider);		
				threshholdinnerPanel.setBorder(threshholdborder1);
			
				frame.add(threshholdinnerPanel);
			
			// multiplier is points per decade here
				attackFreqSlider = SpinCADBlock.LogSlider(0.51,20,gCB.getattackFreq(), "LOGFREQ", 100.0);
				attackFreqSlider.addChangeListener(new New_EnvelopeListener());
				
				attackFreqLabel = new JLabel("Attack Freq");
			
				SpinnerNumberModel attackFreqSpinnerNumberModel = new SpinnerNumberModel(SpinCADBlock.filtToFreq(gCB.getattackFreq()) * 100, 0.51, 10000.00, 0.01);
			
				attackFreqSpinner = new JSpinner(attackFreqSpinnerNumberModel);
				JSpinner.NumberEditor attackFreqeditor = (JSpinner.NumberEditor)attackFreqSpinner.getEditor();  
				DecimalFormat attackFreqformat = attackFreqeditor.getFormat();  
			 			attackFreqformat.setMinimumFractionDigits(2);  
				attackFreqformat.setMaximumFractionDigits(2);  
				attackFreqeditor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
				Dimension attackFreqd = attackFreqSpinner.getPreferredSize();  
				attackFreqd.width = 25;  
				attackFreqSpinner.setPreferredSize(attackFreqd);  
				
				updateattackFreqSpinner();
				attackFreqSpinner.addChangeListener(new New_EnvelopeListener());
				
				JPanel attackFreqtopLine = new JPanel();
				attackFreqtopLine.setLayout(new BoxLayout(attackFreqtopLine, BoxLayout.X_AXIS));
			
				attackFreqtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				attackFreqtopLine.add(attackFreqLabel);
				attackFreqtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				attackFreqtopLine.add(attackFreqSpinner);
				
				Border attackFreqborder2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				attackFreqtopLine.setBorder(attackFreqborder2);
			
				Border attackFreqborder1 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel attackFreqinnerPanel = new JPanel();
					
				attackFreqinnerPanel.setLayout(new BoxLayout(attackFreqinnerPanel, BoxLayout.Y_AXIS));
				attackFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				attackFreqinnerPanel.add(attackFreqtopLine);
				attackFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				attackFreqinnerPanel.add(attackFreqSlider);		
				attackFreqinnerPanel.setBorder(attackFreqborder1);
			
				frame.add(attackFreqinnerPanel);
			
			// multiplier is points per decade here
				decayFreqSlider = SpinCADBlock.LogSlider(0.51,10,gCB.getdecayFreq(), "LOGFREQ", 100.0);
				decayFreqSlider.addChangeListener(new New_EnvelopeListener());
				
				decayFreqLabel = new JLabel("Decay Freq");
			
				SpinnerNumberModel decayFreqSpinnerNumberModel = new SpinnerNumberModel(SpinCADBlock.filtToFreq(gCB.getdecayFreq()) * 100, 0.51, 10000.00, 0.01);
			
				decayFreqSpinner = new JSpinner(decayFreqSpinnerNumberModel);
				JSpinner.NumberEditor decayFreqeditor = (JSpinner.NumberEditor)decayFreqSpinner.getEditor();  
				DecimalFormat decayFreqformat = decayFreqeditor.getFormat();  
			 			decayFreqformat.setMinimumFractionDigits(2);  
				decayFreqformat.setMaximumFractionDigits(2);  
				decayFreqeditor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
				Dimension decayFreqd = decayFreqSpinner.getPreferredSize();  
				decayFreqd.width = 25;  
				decayFreqSpinner.setPreferredSize(decayFreqd);  
				
				updatedecayFreqSpinner();
				decayFreqSpinner.addChangeListener(new New_EnvelopeListener());
				
				JPanel decayFreqtopLine = new JPanel();
				decayFreqtopLine.setLayout(new BoxLayout(decayFreqtopLine, BoxLayout.X_AXIS));
			
				decayFreqtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				decayFreqtopLine.add(decayFreqLabel);
				decayFreqtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				decayFreqtopLine.add(decayFreqSpinner);
				
				Border decayFreqborder2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				decayFreqtopLine.setBorder(decayFreqborder2);
			
				Border decayFreqborder1 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel decayFreqinnerPanel = new JPanel();
					
				decayFreqinnerPanel.setLayout(new BoxLayout(decayFreqinnerPanel, BoxLayout.Y_AXIS));
				decayFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				decayFreqinnerPanel.add(decayFreqtopLine);
				decayFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				decayFreqinnerPanel.add(decayFreqSlider);		
				decayFreqinnerPanel.setBorder(decayFreqborder1);
			
				frame.add(decayFreqinnerPanel);
			
			// multiplier is points per decade here
				postFreqSlider = SpinCADBlock.LogSlider(0.51,10,gCB.getpostFreq(), "LOGFREQ", 100.0);
				postFreqSlider.addChangeListener(new New_EnvelopeListener());
				
				postFreqLabel = new JLabel("Post freq");
			
				SpinnerNumberModel postFreqSpinnerNumberModel = new SpinnerNumberModel(SpinCADBlock.filtToFreq(gCB.getpostFreq()) * 100, 0.51, 10000.00, 0.01);
			
				postFreqSpinner = new JSpinner(postFreqSpinnerNumberModel);
				JSpinner.NumberEditor postFreqeditor = (JSpinner.NumberEditor)postFreqSpinner.getEditor();  
				DecimalFormat postFreqformat = postFreqeditor.getFormat();  
			 			postFreqformat.setMinimumFractionDigits(2);  
				postFreqformat.setMaximumFractionDigits(2);  
				postFreqeditor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
				Dimension postFreqd = postFreqSpinner.getPreferredSize();  
				postFreqd.width = 25;  
				postFreqSpinner.setPreferredSize(postFreqd);  
				
				updatepostFreqSpinner();
				postFreqSpinner.addChangeListener(new New_EnvelopeListener());
				
				JPanel postFreqtopLine = new JPanel();
				postFreqtopLine.setLayout(new BoxLayout(postFreqtopLine, BoxLayout.X_AXIS));
			
				postFreqtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				postFreqtopLine.add(postFreqLabel);
				postFreqtopLine.add(Box.createRigidArea(new Dimension(35,4)));			
				postFreqtopLine.add(postFreqSpinner);
				
				Border postFreqborder2 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
				postFreqtopLine.setBorder(postFreqborder2);
			
				Border postFreqborder1 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel postFreqinnerPanel = new JPanel();
					
				postFreqinnerPanel.setLayout(new BoxLayout(postFreqinnerPanel, BoxLayout.Y_AXIS));
				postFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				postFreqinnerPanel.add(postFreqtopLine);
				postFreqinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				postFreqinnerPanel.add(postFreqSlider);		
				postFreqinnerPanel.setBorder(postFreqborder1);
			
				frame.add(postFreqinnerPanel);
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
		class New_EnvelopeListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(threshholdsilentGUIChange == true) 
				return;
			
			if(ce.getSource() == threshholdSlider) {
			gCB.setthreshhold((double) (threshholdSlider.getValue()/100));
				updatethreshholdSpinner();
			}
			if(ce.getSource() == threshholdSpinner) {
			gCB.setthreshhold((double) (threshholdSlider.getValue()/100));
				updatethreshholdSlider();
			}
			if(attackFreqsilentGUIChange == true) 
				return;
			
			if(ce.getSource() == attackFreqSlider) {
			gCB.setattackFreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(attackFreqSlider.getValue()), 100.0)));
				updateattackFreqSpinner();
			}
			if(ce.getSource() == attackFreqSpinner) {
			gCB.setattackFreq(SpinCADBlock.freqToFilt((double)(attackFreqSpinner.getValue())));
				updateattackFreqSlider();
			}
			if(decayFreqsilentGUIChange == true) 
				return;
			
			if(ce.getSource() == decayFreqSlider) {
			gCB.setdecayFreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(decayFreqSlider.getValue()), 100.0)));
				updatedecayFreqSpinner();
			}
			if(ce.getSource() == decayFreqSpinner) {
			gCB.setdecayFreq(SpinCADBlock.freqToFilt((double)(decayFreqSpinner.getValue())));
				updatedecayFreqSlider();
			}
			if(postFreqsilentGUIChange == true) 
				return;
			
			if(ce.getSource() == postFreqSlider) {
			gCB.setpostFreq((double) SpinCADBlock.freqToFilt(SpinCADBlock.sliderToLogval((int)(postFreqSlider.getValue()), 100.0)));
				updatepostFreqSpinner();
			}
			if(ce.getSource() == postFreqSpinner) {
			gCB.setpostFreq(SpinCADBlock.freqToFilt((double)(postFreqSpinner.getValue())));
				updatepostFreqSlider();
			}
			}
		}

		// add item state changed listener for Checkbox
		class New_EnvelopeItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class New_EnvelopeActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatethreshholdSpinner() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					threshholdsilentGUIChange = true;
				}
				finally {
					threshholdsilentGUIChange = false;   	    	  
				}
			}
		});
		}	
		
		private void updatethreshholdSlider() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					threshholdsilentGUIChange = true;
				}
				finally {
					threshholdsilentGUIChange = false;   	    	  
				}
			}
		});
		}		
			
		private void updateattackFreqSpinner() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					attackFreqsilentGUIChange = true;
		attackFreqSpinner.setValue(SpinCADBlock.filtToFreq(gCB.getattackFreq()));
				}
				finally {
					attackFreqsilentGUIChange = false;   	    	  
				}
			}
		});
		}	
		
		private void updateattackFreqSlider() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					attackFreqsilentGUIChange = true;
		attackFreqSlider.setValue((int) (100 * Math.log10(SpinCADBlock.filtToFreq(gCB.getattackFreq()))));		
				}
				finally {
					attackFreqsilentGUIChange = false;   	    	  
				}
			}
		});
		}		
			
		private void updatedecayFreqSpinner() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					decayFreqsilentGUIChange = true;
		decayFreqSpinner.setValue(SpinCADBlock.filtToFreq(gCB.getdecayFreq()));
				}
				finally {
					decayFreqsilentGUIChange = false;   	    	  
				}
			}
		});
		}	
		
		private void updatedecayFreqSlider() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					decayFreqsilentGUIChange = true;
		decayFreqSlider.setValue((int) (100 * Math.log10(SpinCADBlock.filtToFreq(gCB.getdecayFreq()))));		
				}
				finally {
					decayFreqsilentGUIChange = false;   	    	  
				}
			}
		});
		}		
			
		private void updatepostFreqSpinner() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					postFreqsilentGUIChange = true;
		postFreqSpinner.setValue(SpinCADBlock.filtToFreq(gCB.getpostFreq()));
				}
				finally {
					postFreqsilentGUIChange = false;   	    	  
				}
			}
		});
		}	
		
		private void updatepostFreqSlider() {
			SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					postFreqsilentGUIChange = true;
		postFreqSlider.setValue((int) (100 * Math.log10(SpinCADBlock.filtToFreq(gCB.getpostFreq()))));		
				}
				finally {
					postFreqsilentGUIChange = false;   	    	  
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
