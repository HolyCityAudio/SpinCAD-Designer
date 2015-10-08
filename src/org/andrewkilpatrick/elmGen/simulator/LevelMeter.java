/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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
package org.andrewkilpatrick.elmGen.simulator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.andrewkilpatrick.elmGen.simulator.LevelLogger.LoggerPanel;
import org.andrewkilpatrick.elmGen.util.Util;


public class LevelMeter implements AudioSink {
	JFrame frame;
	JPanel panel;
	int windowCount = 0;
	double maxL = 0.0;
	double maxR = 0.0;
	
	public LevelMeter() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					frame = new JFrame("ElmGen - Level Meter");
					panel = new JPanel();
					panel.setPreferredSize(new Dimension(200, 200));
					frame.getContentPane().add(panel);
					frame.pack();
					frame.setVisible(true);			
				}
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	public LevelMeter(final JPanel p) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
//				panel = new JPanel(p);
				p.setPreferredSize(new Dimension(80, 400));
				p.setVisible(true);			
			}
		});
//		delay = new AudioDelay();
	}

	public void close() {
		
	}

	public void writeAdc(int[] buf, int len) {
		
	}
	
	public void writeDac(int[] buf, int len) {
		for(int i = 0; i < len; i += 2) {
			double left = Math.abs(Util.regToDouble(buf[i]));
			double right = Math.abs(Util.regToDouble(buf[i + 1]));
			
			if(left > maxL) {
				maxL = left;
			}
			if(right > maxR) {
				maxR = right;
			}
			
			windowCount ++;
			if(windowCount == 4096) {
				updateLevels();
				windowCount = 0;
//				maxL = 0.0;
//				maxR = 0.0;
			}
			maxL -= 0.0001;
			maxR -= 0.0001;
		}
	}

	private void updateLevels() {
		Graphics2D g2 = (Graphics2D) panel.getGraphics();
		if(g2 == null) return;
		
		int heightL = (int)(maxL * (double)panel.getHeight());
		
		double db = sampleToDB(maxL);
		System.out.println("dB: " + db);
		
//		System.out.println("height: " + heightL);

		g2.setColor(Color.BLACK);
		g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
		g2.setColor(Color.GREEN);
		g2.fillRect(0, panel.getHeight() - heightL, 100, heightL);
		
//		System.out.println("ding!");
	}
	
	private double sampleToDB(double sampleLevel) {
		return 20 * Math.log10(sampleLevel);
	}
}
