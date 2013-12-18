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

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;



public class MultiTapControlPanel extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6654326471917532290L;
	JSlider tapSlider0;
	JSlider tapSlider1;
	JSlider tapSlider2;
	JSlider tapSlider3;
	JSlider tapSlider4;
	JSlider tapSlider5;
	JSlider tapSlider6;
	JSlider tapSlider7;

	JSlider fbSlider;
	JSlider delayGainSlider;
	JSlider lengthSlider;

	JLabel tapLabel0;
	JLabel tapLabel1;
	JLabel tapLabel2;
	JLabel tapLabel3;
	JLabel tapLabel4;
	JLabel tapLabel5;
	JLabel tapLabel6;
	JLabel tapLabel7;

	JLabel fbLabel;
	JLabel delayGainLabel;
	JLabel lengthLabel;

	private MultiTapCADBlock Multi;

	public MultiTapControlPanel(final MultiTapCADBlock multiTapCADBlock) {
		this.Multi = multiTapCADBlock;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("MultiTap Block");
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
				setResizable(false);
				
				mtcpSliderListener mtcpSL = new mtcpSliderListener();

				tapSlider0 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider0.addChangeListener(mtcpSL);

				tapSlider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider1.addChangeListener(mtcpSL);

				tapSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider2.addChangeListener(mtcpSL);

				tapSlider3 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider3.addChangeListener(mtcpSL);

				tapSlider4 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider4.addChangeListener(mtcpSL);

				tapSlider5 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider5.addChangeListener(mtcpSL);

				tapSlider6 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider6.addChangeListener(mtcpSL);

				tapSlider7 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
				tapSlider7.addChangeListener(mtcpSL);

				fbSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 0);
				fbSlider.addChangeListener(mtcpSL);

				delayGainSlider = new JSlider(JSlider.HORIZONTAL, 0, 99, 0);
				delayGainSlider.addChangeListener(mtcpSL);

				lengthSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 0);
				lengthSlider.addChangeListener(mtcpSL);

				tapLabel0 = new JLabel();
				tapLabel1 = new JLabel();
				tapLabel2 = new JLabel();
				tapLabel3 = new JLabel();
				tapLabel4 = new JLabel();
				tapLabel5 = new JLabel();

				tapLabel6 = new JLabel();
				tapLabel7 = new JLabel();

				fbLabel = new JLabel();
				delayGainLabel = new JLabel();
				lengthLabel = new JLabel();

				getContentPane().add(tapLabel0);
				getContentPane().add(tapSlider0);

				getContentPane().add(tapLabel1);
				getContentPane().add(tapSlider1);

				getContentPane().add(tapLabel2);
				getContentPane().add(tapSlider2);

				getContentPane().add(tapLabel3);
				getContentPane().add(tapSlider3);

				getContentPane().add(tapLabel4);
				getContentPane().add(tapSlider4);

				getContentPane().add(tapLabel5);
				getContentPane().add(tapSlider5);

				getContentPane().add(tapLabel6);
				getContentPane().add(tapSlider6);

				getContentPane().add(tapLabel7);
				getContentPane().add(tapSlider7);

				getContentPane().add(fbLabel);
				getContentPane().add(fbSlider);
				getContentPane().add(delayGainLabel);
				getContentPane().add(delayGainSlider);

				getContentPane().add(lengthLabel);
				getContentPane().add(lengthSlider);

				tapSlider0.setValue((int)Math.round((multiTapCADBlock.getTapLevel(0) * 100.0)));
				tapSlider1.setValue((int)Math.round((multiTapCADBlock.getTapLevel(1) * 100.0)));
				tapSlider2.setValue((int)Math.round((multiTapCADBlock.getTapLevel(2) * 100.0)));
				tapSlider3.setValue((int)Math.round((multiTapCADBlock.getTapLevel(3) * 100.0)));
				tapSlider4.setValue((int)Math.round((multiTapCADBlock.getTapLevel(4) * 100.0)));
				tapSlider5.setValue((int)Math.round((multiTapCADBlock.getTapLevel(5) * 100.0)));

				tapSlider6.setValue((int)Math.round((multiTapCADBlock.getTapLevel(6) * 100.0)));
				tapSlider7.setValue((int)Math.round((multiTapCADBlock.getTapLevel(7) * 100.0)));

				fbSlider.setValue((int)Math.round((multiTapCADBlock.getfbLevel() * 100.0)));
				delayGainSlider.setValue((int)Math.round((multiTapCADBlock.getDelayGain() * 100.0)));
				lengthSlider.setValue((int)Math.round((multiTapCADBlock.getLength() * 1000.0)));

				setVisible(true);
				pack();
			}
		});

	}

	public MultiTapControlPanel(MultiTapCADBlock multiTapCADBlock, JPanel p) {
		this.Multi = multiTapCADBlock;
		this.setTitle("MultiTap Block");
		mtcpSliderListener mtcpSL = new mtcpSliderListener();

		tapSlider0 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider0.addChangeListener(mtcpSL);
		tapSlider0.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapSlider1 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider1.addChangeListener(mtcpSL);
		tapSlider1.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapSlider2 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider2.addChangeListener(mtcpSL);
		tapSlider2.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapSlider3 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider3.addChangeListener(mtcpSL);
		tapSlider3.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapSlider4 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider4.addChangeListener(mtcpSL);
		tapSlider4.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapSlider5 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider5.addChangeListener(mtcpSL);
		tapSlider5.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapSlider6 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider6.addChangeListener(mtcpSL);
		tapSlider6.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapSlider7 = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		tapSlider7.addChangeListener(mtcpSL);
		tapSlider7.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		fbSlider = new JSlider(JSlider.HORIZONTAL, 0, 40, 0);
		fbSlider.addChangeListener(mtcpSL);
		fbSlider.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		delayGainSlider = new JSlider(JSlider.HORIZONTAL, 0, 99, 0);
		delayGainSlider.addChangeListener(mtcpSL);
		delayGainSlider.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		lengthSlider = new JSlider(JSlider.HORIZONTAL, 0, 800, 0);
		lengthSlider.addChangeListener(mtcpSL);
		lengthSlider.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

		tapLabel0 = new JLabel();
		tapLabel1 = new JLabel();
		tapLabel2 = new JLabel();
		tapLabel3 = new JLabel();
		tapLabel4 = new JLabel();
		tapLabel5 = new JLabel();

		tapLabel6 = new JLabel();
		tapLabel7 = new JLabel();

		fbLabel = new JLabel();
		delayGainLabel = new JLabel();
		lengthLabel = new JLabel();

		p.add(new JLabel("Multi Tap Delay"));

		p.add(tapLabel0);
		p.add(tapSlider0);

		p.add(tapLabel1);
		p.add(tapSlider1);

		p.add(tapLabel2);
		p.add(tapSlider2);

		p.add(tapLabel3);
		p.add(tapSlider3);

		p.add(tapLabel4);
		p.add(tapSlider4);

		p.add(tapLabel5);
		p.add(tapSlider5);

		p.add(tapLabel6);
		p.add(tapSlider6);

		p.add(tapLabel7);
		p.add(tapSlider7);

		p.add(fbLabel);
		p.add(fbSlider);
		p.add(delayGainLabel);
		p.add(delayGainSlider);

		p.add(lengthLabel);
		p.add(lengthSlider);

		tapSlider0.setValue((int)Math.round((multiTapCADBlock.getTapLevel(0) * 100.0)));
		tapSlider1.setValue((int)Math.round((multiTapCADBlock.getTapLevel(1) * 100.0)));
		tapSlider2.setValue((int)Math.round((multiTapCADBlock.getTapLevel(2) * 100.0)));
		tapSlider3.setValue((int)Math.round((multiTapCADBlock.getTapLevel(3) * 100.0)));
		tapSlider4.setValue((int)Math.round((multiTapCADBlock.getTapLevel(4) * 100.0)));
		tapSlider5.setValue((int)Math.round((multiTapCADBlock.getTapLevel(5) * 100.0)));

		tapSlider6.setValue((int)Math.round((multiTapCADBlock.getTapLevel(6) * 100.0)));
		tapSlider7.setValue((int)Math.round((multiTapCADBlock.getTapLevel(7) * 100.0)));

		fbSlider.setValue((int)Math.round((multiTapCADBlock.getfbLevel() * 100.0)));
		delayGainSlider.setValue((int)Math.round((multiTapCADBlock.getDelayGain() * 100.0)));
		lengthSlider.setValue((int)Math.round((multiTapCADBlock.getLength() * 1000.0)));
	}

	class mtcpSliderListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == tapSlider0) {
				Multi.setTapLevel(0, (double)tapSlider0.getValue() / 100.0);
				tapLabel0.setText("Tap 1 level " + String.format("%2.2f", Multi.getTapLevel(0)));
			}
			else if(ce.getSource() == tapSlider1) {
				Multi.setTapLevel(1, (double)tapSlider1.getValue() / 100.0);
				tapLabel1.setText("Tap 2 level " + String.format("%2.2f", Multi.getTapLevel(1)));
			}
			else if(ce.getSource() == tapSlider2) {
				Multi.setTapLevel(2, (double)tapSlider2.getValue() / 100.0);
				tapLabel2.setText("Tap 3 level " + String.format("%2.2f", Multi.getTapLevel(2)));
			}
			else if(ce.getSource() == tapSlider3) {
				Multi.setTapLevel(3, (double)tapSlider3.getValue() / 100.0);
				tapLabel3.setText("Tap 4 level " + String.format("%2.2f", Multi.getTapLevel(3)));
			}
			else if(ce.getSource() == tapSlider4) {
				Multi.setTapLevel(4, (double)tapSlider4.getValue() / 100.0);
				tapLabel4.setText("Tap 5 level " + String.format("%2.2f", Multi.getTapLevel(4)));
			}
			else if(ce.getSource() == tapSlider5) {
				Multi.setTapLevel(5, (double)tapSlider5.getValue() / 100.0);
				tapLabel5.setText("Tap 6 level " + String.format("%2.2f", Multi.getTapLevel(5)));
			}
			else if(ce.getSource() == tapSlider6) {
				Multi.setTapLevel(6, (double)tapSlider6.getValue() / 100.0);
				tapLabel6.setText("Tap 7 level " + String.format("%2.2f", Multi.getTapLevel(6)));
			}
			else if(ce.getSource() == tapSlider7) {
				Multi.setTapLevel(7, (double)tapSlider7.getValue() / 100.0);
				tapLabel7.setText("Tap 8 level " + String.format("%2.2f", Multi.getTapLevel(7)));
			}
			else if(ce.getSource() == fbSlider) {
				Multi.setfbLevel((double)fbSlider.getValue() / 100.0);
				fbLabel.setText("Feedback level " + String.format("%2.2f", Multi.getfbLevel()));
			}
			else if(ce.getSource() == delayGainSlider) {
				Multi.setDelayGain((double)delayGainSlider.getValue() / 100.0);
				delayGainLabel.setText("Delay Gain " + String.format("%2.2f", Multi.getDelayGain()));
			}
			else if(ce.getSource() == lengthSlider) {
				Multi.setLength((double)lengthSlider.getValue() / 1000.0);
				lengthLabel.setText("Delay (sec) " + String.format("%1.3f", Multi.getLength()));
			}
		}
	}
}