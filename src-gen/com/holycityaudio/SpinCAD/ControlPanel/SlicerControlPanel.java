/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * SlicerControlPanel.java
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
import com.holycityaudio.SpinCAD.CADBlocks.SlicerCADBlock;

@SuppressWarnings("unused")
public class SlicerControlPanel extends spinCADControlPanel {
	private JFrame frame;
	private SlicerCADBlock gCB;
	// declare the controls
	JSlider sliceSlider;
	JLabel  sliceLabel;	
	private JComboBox <String> controlRangeComboBox; 

public SlicerControlPanel(SlicerCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JFrame();
				frame.setTitle("Slicer");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					sliceSlider = new JSlider(JSlider.HORIZONTAL, (int)(0.0 * 100.0),(int) (0.95 * 100.0), (int) (gCB.getslice() * 100.0));
						sliceSlider.addChangeListener(new SlicerListener());
						sliceLabel = new JLabel();
						Border sliceBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						sliceLabel.setBorder(sliceBorder1);
						updatesliceLabel();
						
						Border sliceborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel sliceinnerPanel = new JPanel();
							
						sliceinnerPanel.setLayout(new BoxLayout(sliceinnerPanel, BoxLayout.Y_AXIS));
						sliceinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						sliceinnerPanel.add(sliceLabel);
						sliceinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));			
						sliceinnerPanel.add(sliceSlider);		
						sliceinnerPanel.setBorder(sliceborder2);
			
						frame.add(sliceinnerPanel);
				controlRangeComboBox = new JComboBox <String> ();
				controlRangeComboBox.addItem("0 -> +1");
				controlRangeComboBox.addItem("-1 -> +1");
				controlRangeComboBox.setSelectedIndex(gCB.getcontrolRange());
				frame.add(Box.createRigidArea(new Dimension(5,8)));			
				frame.getContentPane().add(controlRangeComboBox);
				controlRangeComboBox.addActionListener(new SlicerActionListener());
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
		class SlicerListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == sliceSlider) {
			gCB.setslice((double) (sliceSlider.getValue()/100.0));
				updatesliceLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class SlicerItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class SlicerActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			if(arg0.getSource() == controlRangeComboBox) {
				gCB.setcontrolRange((controlRangeComboBox.getSelectedIndex()));
			}
			}
		}
		private void updatesliceLabel() {
		sliceLabel.setText("Slice Level " + String.format("%4.2f", gCB.getslice()));		
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
