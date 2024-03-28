/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * RatioControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.RatioCADBlock;

@SuppressWarnings("unused")
public class RatioControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private RatioCADBlock gCB;
	// declare the controls
	JSlider invRatioSlider;
	JLabel  invRatioLabel;	

public RatioControlPanel(RatioCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Ratio");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					invRatioSlider = new JSlider(JSlider.HORIZONTAL, (int)(2.0 * 10.0),(int) (100.0 * 10.0), (int) (gCB.getinvRatio() * 10.0));
						invRatioSlider.addChangeListener(new RatioListener());
						invRatioLabel = new JLabel();
						Border invRatioBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						invRatioLabel.setBorder(invRatioBorder1);
						updateinvRatioLabel();
						
						Border invRatioborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel invRatioinnerPanel = new JPanel();
							
						invRatioinnerPanel.setLayout(new BoxLayout(invRatioinnerPanel, BoxLayout.Y_AXIS));
						invRatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						invRatioinnerPanel.add(invRatioLabel);
						invRatioinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						invRatioinnerPanel.add(invRatioSlider);		
						invRatioinnerPanel.setBorder(invRatioborder2);
			
						frame.add(invRatioinnerPanel);
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
		class RatioListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == invRatioSlider) {
			gCB.setinvRatio((double) (invRatioSlider.getValue()/10.0));
				updateinvRatioLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class RatioItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class RatioActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updateinvRatioLabel() {
		invRatioLabel.setText("Ratio " + String.format("%4.1f", gCB.getinvRatio()));		
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
