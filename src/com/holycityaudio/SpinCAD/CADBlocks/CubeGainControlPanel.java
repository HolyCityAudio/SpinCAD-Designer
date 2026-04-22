/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
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

package com.holycityaudio.SpinCAD.CADBlocks;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.SpinCADFrame;

public class CubeGainControlPanel implements ChangeListener {

	private JSlider wavefoldingSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
	private JLabel wavefoldingLabel = new JLabel();
	private JDialog frame;

	private CubeGainCADBlock block;

	public CubeGainControlPanel(CubeGainCADBlock cubeGainCADBlock) {
		this.block = cubeGainCADBlock;
		wavefoldingSlider.addChangeListener(this);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JDialog(SpinCADFrame.getInstance(), "Cubed");
				block.controlPanelFrame = frame;
				frame.setResizable(false);
				frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));

				wavefoldingSlider.setMajorTickSpacing(25);
				wavefoldingSlider.setPaintTicks(true);
				wavefoldingSlider.setPaintLabels(true);

				frame.add(wavefoldingLabel);
				frame.add(wavefoldingSlider);

				wavefoldingSlider.setValue((int) Math.round(block.getWavefolding() * 100));
				updateLabel();

				frame.setLocationRelativeTo(SpinCADFrame.getInstance());
				frame.setVisible(true);
				frame.pack();
			}
		});
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		if (e.getSource() == wavefoldingSlider) {
			block.setWavefolding(wavefoldingSlider.getValue() / 100.0);
			updateLabel();
		}
	}

	private void updateLabel() {
		wavefoldingLabel.setText(String.format("  Wavefolding: %d%%", wavefoldingSlider.getValue()));
	}
}
