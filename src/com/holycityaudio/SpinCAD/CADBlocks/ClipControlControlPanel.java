/* SpinCAD Designer - DSP Development Tool for the Spin FV-1 
 * Copyright (C) 2013 - 2014 - Gary Worsham 
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code. 
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

public class ClipControlControlPanel implements ChangeListener, ActionListener, ItemListener {

	private JSlider gainSlider = new JSlider(JSlider.HORIZONTAL, 100, 1000, 200);
	private JLabel gainLabel = new JLabel("Hi");
	private JCheckBox invert = new JCheckBox();
	private JCheckBox flip = new JCheckBox();
	private JFrame frame;
	private ClipPanel graph;

	private ClipControlCADBlock pC;

	public ClipControlControlPanel(ClipControlCADBlock clipControlCADBlock) {
		gainSlider.addChangeListener(this);
		flip.addItemListener(this);
		invert.addItemListener(this);
		this.pC = clipControlCADBlock;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Clip Control");
				frame.setTitle("Control Clipper");
				frame.setResizable(false);
				graph = new ClipPanel();
				frame.getContentPane().add(graph);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				invert.setText("Invert");
				flip.setText("Flip");
				graph.setBackground(Color.GREEN);
				graph.setPreferredSize(new Dimension(150,120));
				//				    graph.setBorder(BorderFactory.createEmptyBorder(0,10,10,10)); 

				gainSlider.setMajorTickSpacing(25);

				frame.add(gainLabel);
				frame.add(gainSlider);
				frame.add(invert);
				frame.add(flip);
				frame.add(graph);

				gainSlider.setValue((int) Math.round(pC.getGain() * 100));
				gainLabel.setText(String.format("%2.2f", pC.getGain()));
				invert.setSelected(pC.getInvert());
				flip.setSelected(pC.getFlip());

				frame.setLocation(pC.getX() + 200, pC.getY() + 150);
				frame.setAlwaysOnTop(true);	
				frame.setVisible(true);
				frame.pack();
				graph.repaint();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

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

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == gainSlider) {
			pC.setGain((double) gainSlider.getValue()/100.0);
			gainLabel.setText(String.format("%2.2f", pC.getGain()));
			graph.repaint();
		}
	}	

	class ClipPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7881835628314249883L;

		public ClipPanel() {

		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);       

			// Draw Text
			double i;
			double x_scale = getWidth() - 2;
			double x_offset = 1; //graph.getWidth();
			double y_scale = getHeight() - 2;
			double y_offset = 1;// graph.getHeight();

			double p = pC.getGain();
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());

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
					ii = Math.min(1.0, j * p);
				}
				else {
					ii = 1.0 - Math.min(1.0, j * p);
				}
				g.setColor(Color.CYAN);
				g.drawRect((int) ((i * x_scale) + x_offset),(int) ((ii * y_scale) + y_offset),1,1);
			}
		}  
	}
}