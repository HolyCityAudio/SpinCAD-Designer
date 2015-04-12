/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * aliaserControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.aliaserCADBlock;

public class aliaserControlPanel extends spinCADControlPanel {
	private JFrame frame;

	private aliaserCADBlock gCB;
	// declare the controls
	JSlider inputGainSlider;
	JLabel  inputGainLabel;	
	JSlider ripLevelSlider;
	JLabel  ripLevelLabel;	

public aliaserControlPanel(aliaserCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Aliaser_Zipper");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			
			inputGainSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (1.0 * 1000.0), (int) (gCB.getinputGain() * 1000.0));
				inputGainSlider.addChangeListener(new aliaserListener());
				inputGainLabel = new JLabel();
				updateinputGainLabel();
				
				Border inputGainborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel inputGaininnerPanel = new JPanel();
					
				inputGaininnerPanel.setLayout(new BoxLayout(inputGaininnerPanel, BoxLayout.Y_AXIS));
				inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				inputGaininnerPanel.add(inputGainLabel);
				inputGaininnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				inputGaininnerPanel.add(inputGainSlider);		
				inputGaininnerPanel.setBorder(inputGainborder);
			
				frame.add(inputGaininnerPanel);
			
			ripLevelSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 1000.0),(int) (0.6 * 1000.0), (int) (gCB.getripLevel() * 1000.0));
				ripLevelSlider.addChangeListener(new aliaserListener());
				ripLevelLabel = new JLabel();
				updateripLevelLabel();
				
				Border ripLevelborder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
				JPanel ripLevelinnerPanel = new JPanel();
					
				ripLevelinnerPanel.setLayout(new BoxLayout(ripLevelinnerPanel, BoxLayout.Y_AXIS));
				ripLevelinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				ripLevelinnerPanel.add(ripLevelLabel);
				ripLevelinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
				ripLevelinnerPanel.add(ripLevelSlider);		
				ripLevelinnerPanel.setBorder(ripLevelborder);
			
				frame.add(ripLevelinnerPanel);
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
		class aliaserListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == inputGainSlider) {
			gCB.setinputGain((double) (inputGainSlider.getValue()/1000.0));
				updateinputGainLabel();
			}
			if(ce.getSource() == ripLevelSlider) {
			gCB.setripLevel((double) (ripLevelSlider.getValue()/1000.0));
				updateripLevelLabel();
			}
			}
		}

		// add item listener 
		class aliaserItemListener implements java.awt.event.ItemListener { 
		public void stateChanged(ChangeEvent ce) {
			}
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
				// TODO Auto-generated method stub
			}
		}
		
		// add action listener 
		class aliaserActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinputGainLabel() {
		inputGainLabel.setText("Input_Gain " + String.format("%4.2f", gCB.getinputGain()));		
		}		
		private void updateripLevelLabel() {
		ripLevelLabel.setText("Rip_Level " + String.format("%4.2f", gCB.getripLevel()));		
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
