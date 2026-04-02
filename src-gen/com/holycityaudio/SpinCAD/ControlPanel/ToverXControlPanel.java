/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * ToverXControlPanel.java
 * Copyright (C) 2013 - 2026 - Gary Worsham 
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
import javax.swing.JDialog;
import com.holycityaudio.SpinCAD.SpinCADFrame;
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
import javax.swing.JTextField;
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
import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADBlock;
import com.holycityaudio.SpinCAD.spinCADControlPanel;
import com.holycityaudio.SpinCAD.CADBlocks.ToverXCADBlock;

@SuppressWarnings("unused")
public class ToverXControlPanel extends spinCADControlPanel {
	private JDialog frame;
	private ToverXCADBlock gCB;
	// declare the controls
	FineControlSlider nStagesSlider;
	JTextField  nStagesField;

public ToverXControlPanel(ToverXCADBlock genericCADBlock) {
		
		gCB = genericCADBlock;

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				frame = new JDialog(SpinCADFrame.getInstance(), "ToverX");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

			//
			// these functions translate between slider values, which have to be integers, to whatever in program value you wish.
			//
					nStagesSlider = new FineControlSlider(JSlider.HORIZONTAL, (int)(1 * 1.0),(int) (4 * 1.0), (int) (gCB.getnStages() * 1.0));
						nStagesSlider.addChangeListener(new ToverXListener());
						nStagesField = new JTextField();
						nStagesField.setHorizontalAlignment(JTextField.CENTER);
						Border nStagesBorder1 = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
						nStagesField.setBorder(nStagesBorder1);
						nStagesField.addActionListener(new java.awt.event.ActionListener() {
							@Override
							public void actionPerformed(java.awt.event.ActionEvent e) {
								try {
									double val = Double.parseDouble(nStagesField.getText().replaceAll("[^0-9.\\-]", ""));
						int sliderVal = (int) Math.round(val * 1.0);
						sliderVal = Math.max(nStagesSlider.getMinimum(), Math.min(nStagesSlider.getMaximum(), sliderVal));
						nStagesSlider.setValue(sliderVal);
						gCB.setnStages((double) sliderVal / 1.0);
									updatenStagesLabel();
								} catch (NumberFormatException ex) {
									updatenStagesLabel();
								}
							}
						});
						updatenStagesLabel();
			
						Border nStagesborder2 = BorderFactory.createBevelBorder(BevelBorder.RAISED);
						JPanel nStagesinnerPanel = new JPanel();
			
						nStagesinnerPanel.setLayout(new BoxLayout(nStagesinnerPanel, BoxLayout.Y_AXIS));
						nStagesinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						nStagesinnerPanel.add(nStagesField);
						nStagesinnerPanel.add(Box.createRigidArea(new Dimension(5,4)));
						nStagesinnerPanel.add(nStagesSlider);
						nStagesinnerPanel.setBorder(nStagesborder2);
			
						frame.add(nStagesinnerPanel);
				frame.addWindowListener(new MyWindowListener());
				frame.pack();
				frame.setResizable(false);
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);		
			}
		});
		}

		// add change listener for Sliders, Spinners 
		class ToverXListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == nStagesSlider) {
			gCB.setnStages((double) (nStagesSlider.getValue()/1.0));
				updatenStagesLabel();
			}
			}
		}

		// add item state changed listener for Checkbox
		class ToverXItemListener implements java.awt.event.ItemListener { 
			
		@Override
			public void itemStateChanged(ItemEvent arg0) {
			}
		}
		
		// add action listener for Combo Box
		class ToverXActionListener implements java.awt.event.ActionListener { 
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		}
		private void updatenStagesLabel() {
		nStagesField.setText("Stages " + String.format("%4.1f", gCB.getnStages()));		
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
