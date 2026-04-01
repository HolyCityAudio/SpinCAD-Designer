/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2026.  Look for GSW in code.
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

@SuppressWarnings("serial")
class PitchShiftFixedControlPanel implements ChangeListener {
	JSlider freqSlider = new FineControlSlider(JSlider.HORIZONTAL, -12, 19, 0);
	JSlider ampSlider = new FineControlSlider(JSlider.HORIZONTAL, 0, 3, 2);

	JTextField freqField = new JTextField();
	JTextField ampField = new JTextField();

	private JFrame frame;

	private PitchShiftFixedCADBlock pong;
	private static LFORadioButtons rb;

	public PitchShiftFixedControlPanel(PitchShiftFixedCADBlock pitchShiftFixedCADBlock) {

		freqSlider.addChangeListener(this);
		ampSlider.addChangeListener(this);

		freqField.setHorizontalAlignment(JTextField.CENTER);
		freqField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					int val = Integer.parseInt(freqField.getText().replaceAll("[^0-9.\\-]", "").split("\\.")[0]);
					val = Math.max(-12, Math.min(19, val));
					pong.setFreq(val);
					freqSlider.setValue(val);
					updateFreqLabel();
				} catch (NumberFormatException ex) {
					updateFreqLabel();
				}
			}
		});

		ampField.setHorizontalAlignment(JTextField.CENTER);
		ampField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					// User types a buffer depth value: 512, 1024, 2048, 4096
					int typed = Integer.parseInt(ampField.getText().replaceAll("[^0-9.\\-]", "").split("\\.")[0]);
					int idx;
					if (typed <= 512) idx = 0;
					else if (typed <= 1024) idx = 1;
					else if (typed <= 2048) idx = 2;
					else idx = 3;
					idx = Math.max(0, Math.min(3, idx));
					pong.setAmp(idx);
					ampSlider.setValue(idx);
					updateAmpLabel(idx);
				} catch (NumberFormatException ex) {
					updateAmpLabel(ampSlider.getValue());
				}
			}
		});

		pong = pitchShiftFixedCADBlock;
		rb  = new LFORadioButtons();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("Ramp LFO");
				pong.controlPanelFrame = frame;
				frame.setTitle("Pitch Shift Fixed");
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
				frame.setResizable(false);

				freqSlider.setMajorTickSpacing(1);
				freqSlider.setPaintTicks(true);

				frame.add(ampField);
				updateAmpLabel(2);
				frame.add(ampSlider);

				frame.add(freqField);
				updateFreqLabel();
				frame.add(freqSlider);
				frame.add(rb);
				int i = pong.getAmp();
				int j = 0;
				if(i == 512) {
					j = 0;
				}
				if(i == 1024) {
					j = 1;
				}
				if(i == 2048) {
					j = 2;
				}
				if(i == 4096) {
					j = 3;
				}

				ampSlider.setValue(j);
				freqSlider.setValue((pong.getFreq()));

				frame.setVisible(true);
				frame.pack();
				frame.setLocationRelativeTo(SpinCADFrame.getInstance());			}
		});
	}

	public void stateChanged(ChangeEvent ce) {
		if(ce.getSource() == ampSlider) {
			int i = ampSlider.getValue();
			pong.setAmp(i);
			updateAmpLabel(i);
		}
		else if(ce.getSource() == freqSlider) {
			pong.setFreq(freqSlider.getValue());
			updateFreqLabel();
		}
	}

	public void updateFreqLabel() {
		int semi = pong.getFreq();
		String sign = semi > 0 ? "+" : "";
		String interval = ArpeggiatorCADBlock.intervalName(semi);
		freqField.setText("Shift: " + sign + semi + " (" + interval + ")");
	}

	public void updateAmpLabel(int i) {
		String label = "";
		if(i == 0) {
			label = "512";
		}
		if(i == 1) {
			label = "1024";
		}
		if(i == 2) {
			label = "2048";
		}
		if(i == 3) {
			label = "4096";
		}
		ampField.setText("Buffer depth " + label);
	}

	private class LFORadioButtons extends JPanel implements ActionListener {
		JRadioButton lfo0 = new JRadioButton("LFO 0");
		JRadioButton lfo1 = new JRadioButton("LFO 1");

		public LFORadioButtons() {
			super(new BorderLayout());

			lfo0.setActionCommand("LFO 0");
			lfo1.setActionCommand("LFO 1");

			if(pong.getLFOSel() == 0)
				lfo0.setSelected(true);
			else if(pong.getLFOSel() == 1)
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
				pong.setLFOSel(0);
			}
			else if(lfo1.isSelected()) {
				pong.setLFOSel(1);
			}
		}
	}
}
