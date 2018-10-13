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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class PowerControlControlPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4717076120154216169L;
	private JSlider powerSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 2);
	private JLabel powerLabel = new JLabel("Hi");
	private JCheckBox invert = new JCheckBox();
	private JCheckBox flip = new JCheckBox();
	private PowerPanel graph = new PowerPanel();

	private PowerControlCADBlock pC;

	public PowerControlControlPanel(PowerControlCADBlock powerControlCADBlock) {
		this.pC = powerControlCADBlock;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("Power");
				setResizable(false);

				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				pccItemListener pccIL = new pccItemListener();
				pccChangeListener pccCL = new pccChangeListener();

				invert.setText("Invert");
				invert.addItemListener(pccIL);
				flip.setText("Flip");
				flip.addItemListener(pccIL);
				graph.setBackground(Color.BLACK);
				graph.setPreferredSize(new Dimension(150, 120));
				//	    graph.setBorder(BorderFactory.createEmptyBorder(0,10,10,10)); 

				powerSlider.addChangeListener(pccCL);

				add(powerLabel);
				add(powerSlider);
				add(invert);
				add(flip);
				add(graph);

				powerSlider.setValue((int) Math.round(pC.getPower()));
				powerLabel.setText(String.format("%2.2f", pC.getPower()));
				invert.setSelected(pC.getInvert());
				flip.setSelected(pC.getFlip());
				setLocation(pC.getX() + 200, pC.getY() + 150);
				setAlwaysOnTop(true);
				setVisible(true);
				pack();
//				graph.repaint();
			}
		});
	}

	class pccItemListener implements ItemListener { 
		public void itemStateChanged(ItemEvent arg0) {
			Object source = arg0.getItemSelectable();

			if (source == flip) {
		        if (arg0.getStateChange() == ItemEvent.DESELECTED) {
					pC.setFlip(false);
				}
				else
					pC.setFlip(true);	    		
			} else if (source == invert) {
		        if (arg0.getStateChange() == ItemEvent.DESELECTED) {
					pC.setInvert(false);
				}
				else
					pC.setInvert(true);	    		
			} 		
			graph.repaint();
		}
	}

	class pccChangeListener implements ChangeListener { 
		public void stateChanged(ChangeEvent e) {
			if (e.getSource() == powerSlider) {
				pC.setPower((double) powerSlider.getValue());
				powerLabel.setText(String.format("%2.2f", pC.getPower()));
			}
			graph.repaint();
		}	
	}

	class PowerPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7344139509606778116L;

		public PowerPanel() {
			super();		
		}

		protected void paintComponent(Graphics g2) {
			super.paintComponent(g2);
			double i;

			double x_scale = getWidth();
			double y_scale = getHeight();
			double p = pC.getPower();
			g2.setColor(Color.BLACK);
			g2.fillRect(0, 0, getWidth(), getHeight());

			for(i = 0; i < 1.0; i += 0.01) {
				double j;
				double ii;

				if(pC.getFlip() == true) {
					j = 1.0 - i;
				}
				else {
					j = i;
				}

				if(pC.getInvert() == true) {
					ii = Math.pow(j, p);
				}
				else {
					ii = 1.0 - Math.pow(j, p);
				}

				g2.setColor(Color.CYAN);
				g2.drawRect((int) (i * x_scale),(int) (ii * y_scale),1,1);
			}
			//			repaint();
		}

	}
}