/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import com.holycityaudio.SpinCAD.spinCADControlPanel;

public class SinCosLFOAControlPanel extends spinCADControlPanel implements ChangeListener, ActionListener, ItemListener {

	private JSlider lfoWidthSlider = new JSlider(JSlider.HORIZONTAL, 0, 32767, 8192);
	private JLabel lfoWidthLabel = new JLabel("LFO Rate");

	private JSlider lfoRateSlider = new JSlider(JSlider.HORIZONTAL, 0, 511, 200);
	private JLabel lfoRateLabel = new JLabel("LFO Rate");
	
	private LFORadioButtons rb;
	
	private JLabel outputRangeLabel = new JLabel("Output Range");
	private JComboBox<String> outputRange;

	private JFrame frame;
	private SinCosLFOACADBlock pC;

	private String listOptions[] = {
			"-1.0 -> 1.0",
			" 0.0 -> 1.0"
	};

	public SinCosLFOAControlPanel(SinCosLFOACADBlock sinCosLFOCADBlock) {
		lfoWidthSlider.addChangeListener(this);
		lfoRateSlider.addChangeListener(this);
		pC = sinCosLFOCADBlock;
		rb  = new LFORadioButtons();

		
		outputRange = new JComboBox<String>(listOptions);
		outputRange.addActionListener(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("LFO");
				frame.setTitle("Sin/Cos LFO");
				frame.setResizable(false);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				frame.setLocation(new Point(pC.getX() + 200, pC.getY() + 150));

				//				    graph.setBorder(BorderFactory.createEmptyBorder(0,10,10,10)); 

				lfoRateSlider.setMajorTickSpacing(25);

				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.add(lfoRateLabel);
				frame.add(lfoRateSlider);

				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.add(lfoWidthLabel);
				frame.add(lfoWidthSlider);
				
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.add(rb);
								
				frame.add(Box.createRigidArea(new Dimension(5,4)));			
				frame.add(outputRangeLabel);
				outputRange.setSelectedIndex(pC.getRange());
				frame.add(outputRange);

				lfoRateSlider.setValue(pC.getLFORate());
				updateLfoRateLabel();
				lfoWidthSlider.setValue(pC.getLFOWidth());
				updateLfoWidthLabel();

				frame.setVisible(true);
				frame.pack();
				frame.setAlwaysOnTop(true);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == outputRange) {
	        JComboBox<?> cb = (JComboBox<?>)arg0.getSource();
	        String range = (String)cb.getSelectedItem();
	        if (range == listOptions[0]) {
	        	pC.setRange(0);
	        } else if (range == listOptions[1]) {
	        	pC.setRange(1);
	        }
		}
	}

	public void itemStateChanged(ItemEvent arg0) {
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == lfoRateSlider) {
			pC.setLFORate( lfoRateSlider.getValue());
			updateLfoRateLabel();
		}
		else if (e.getSource() == lfoWidthSlider) {
			pC.setLFOWidth(lfoWidthSlider.getValue());
			updateLfoWidthLabel();
		}
	}	

	private void updateLfoRateLabel() {
		lfoRateLabel.setText(String.format("%2.1f Hz", coeffToLFORate(pC.getLFORate())));
	}

	private void updateLfoWidthLabel() {
		lfoWidthLabel.setText(String.format("%2d", pC.getLFOWidth()));	
	}

	class LFORadioButtons extends JPanel implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -507133930408340822L;
		JRadioButton lfo0 = new JRadioButton("LFO 0");
		JRadioButton lfo1 = new JRadioButton("LFO 1");

		public LFORadioButtons() {
			super(new BorderLayout());

			lfo0.setActionCommand("LFO 0");
			lfo1.setActionCommand("LFO 1");

			if(pC.getLFOSel() == 0)
				lfo0.setSelected(true);
			else if(pC.getLFOSel() == 1)
				lfo1.setSelected(true);

			//Group the radio buttons.
			ButtonGroup group = new ButtonGroup();
			group.add(lfo0);
			group.add(lfo1);

			//Register a listener for the radio buttons.
			lfo0.addActionListener(this);
			lfo1.addActionListener(this);

			JPanel radioPanel = new JPanel(new GridLayout(1, 2));
			radioPanel.add(lfo0);
			radioPanel.add(lfo1);
			lfo1.setAlignmentY(SwingConstants.CENTER);
			add(radioPanel, BorderLayout.LINE_START);
		}
		@Override

		public void actionPerformed(ActionEvent arg0) {
			if(lfo0.isSelected()) {
				pC.setLFOSel(0);
			} 
			else if(lfo1.isSelected()) {
				pC.setLFOSel(1);
			} 
			pC.setName();
		}
	}
}