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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.andrewkilpatrick.elmGen.util.Util;


public class LevelLogger implements AudioSink {
	JFrame frame;
// GSW changed from JPanel to LoggerPanel to integrate with SpinCAD Designer
	public LoggerPanel panel;
	int windowCount = 0;
	double maxL = 0.0;
	double maxR = 0.0;
	int xPos = 0;
	int oldL = -96;
	int oldR = -96;
// GSW added options for linear or log display within SpinCAD
// not exactly sure if linear works!
	int logMode = 1;	// 0 for integer, 1 for log
	double left = -1;
	double right = -1;

	AudioDelay delay;

	public LevelLogger() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				frame = new JFrame("SpinCAD Designer - Level Logger");
				frame.setLocation(300, 0);
				panel = new LoggerPanel();
				panel.getPanel().setPreferredSize(new Dimension(600, 200));
				frame.getContentPane().add(panel.getPanel());
				frame.pack();
				frame.setVisible(true);			
			}
		});
		delay = new AudioDelay();
	}

	public LevelLogger(final JPanel p) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				panel = new LoggerPanel(p);
				p.setPreferredSize(new Dimension(600, 200));
				p.setVisible(true);			
			}
		});
		delay = new AudioDelay();
	}

	public void close() {

	}

	public void writeAdc(int[] buf, int len) {

	}

	public void writeDac(int[] buf, int len) {
		int dbuf[] = delay.process(buf, 50000);
// GSW Added some logic to discern between linear and log mode.
		for(int i = 0; i < len; i += 2) {	
			if(logMode == 1) {
				left = Math.abs(Util.regToDouble(dbuf[i]));
				right = Math.abs(Util.regToDouble(dbuf[i + 1]));
				if(left > maxL) {
					maxL = left;
				}

				if(right > maxR) {
					maxR = right;
				}
				// envelope decay parameter
				maxL *= 0.99;
				maxR *= 0.99;
			}
			else if(logMode == 0)	// integer
			{
				left = (double)Util.regToInt(dbuf[i]);
				right = (double)Util.regToInt(dbuf[i + 1]);
			}	

			windowCount ++;
			if(windowCount == 512) {
				panel.updateLevels();
				windowCount = 0;
				//				maxL = 0.0;
				//				maxR = 0.0;
			}

		}
	}
// GSW Added LoggerPanel as a class
	public class LoggerPanel {

		JPanel panel;

		public LoggerPanel() {
			panel = new JPanel();
		}

		public Component getPanel() {
			return panel;
		}

		public LoggerPanel(JPanel p) {
			panel = p;
		}

		protected void updateLevels() {
			Graphics2D g2 = (Graphics2D) panel.getGraphics();
			if(g2 == null) return;

			int newL = -1;
			int newR = -1;
			if(xPos < 5) {
				g2.setColor(Color.BLACK);
				g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
			}
			if(logMode == 1) {
				newL = (int)sampleToDB(maxL);
				newR = (int)sampleToDB(maxR);
				g2.setColor(Color.MAGENTA);
				g2.drawLine(xPos, (oldL * -2), xPos + 1, -(newL * 2));
				g2.setColor(Color.CYAN);
				g2.drawLine(xPos, (oldR * -2), xPos + 1, -(newR * 2));
			}
			else if (logMode == 0) {
				newL = sampleToInt(left);
				newR = sampleToInt(right);
				g2.setColor(Color.MAGENTA);
				g2.drawLine(xPos, (oldL * 200), xPos + 1, newL * 200);
				g2.setColor(Color.CYAN);
				g2.drawLine(xPos, (oldR * -2), xPos + 1, -(newR * 2));
			}
			oldL = newL;
			oldR = newR;
			xPos ++;
			if(xPos == panel.getWidth()) {
				xPos = 0;
			}
		}
	}

	private double sampleToDB(double sampleLevel) {
		return 20 * Math.log10(sampleLevel);
	}
// GSW added SampleToInt for use with integer mode logger
// as before, not sure if it works!
	private int sampleToInt(double sampleLevel) {
		return (int)(sampleLevel * 16384);
	}
}
