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
	double decayval = 0.999;
	int xPos = 0;
	int oldL = -96;
	int oldR = -96;
	// GSW added options for linear or log display within SpinCAD
	// scope variables
	// display height is 
	double refLeft = -0.5;
	double refRight = 0.5;
	double vDivLeft = 1.0;
	double vDivRight = 1.0;
	// channel gain
	private int scopeCh1Gain = 16;
	private int scopeCh2Gain = 16;

	//  GSW added trigger modes
	public enum triggerMode { AUTO, NORMAL, SINGLE };
	public triggerMode tm = triggerMode.AUTO;
	boolean triggered = false;

	// scope slope filter
	private double[] filter = new double[3];

	// not exactly sure if linear works!
	private int logMode = 1;	// 0 for integer, 1 for log
	double left = -1;
	double right = -1;
	int windowRatio = 512;	// update display every how many samples

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
		filter[0] = 0.0;
		filter[1] = 0.0;
		filter[2] = 0.0;
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
				maxL *= decayval;
				maxR *= decayval;
			}
			else if(logMode == 0)	// integer
			{
				left = (double)Util.regToInt(dbuf[i]);
				right = (double)Util.regToInt(dbuf[i + 1]);
				//				System.out.println(left);
			}	

			windowCount ++;
			if(windowCount >= windowRatio) {
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
			if(logMode == 1) {
				newL = (int)sampleToDB(maxL);
				newR = (int)sampleToDB(maxR);
			}
			else if (logMode == 0) {
				newL = (int) (((1 + refLeft) * 100) - ((int) left >> scopeCh1Gain));
				newR = (int) (((1 + refRight) * 100) - ((int) right >> scopeCh2Gain));
			}

			filter[2] = filter[1];
			filter[1] = filter[0];
			filter[0] = newL;

			double slope = filter[0] - filter[2];

			if(slope > 0.0) {
				triggered = true;
			}

			if(triggered && (tm == triggerMode.AUTO) || (tm == triggerMode.SINGLE)) {
				if(logMode == 1) {
					if(xPos < 1) {
						g2.setColor(Color.BLACK);
						g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());
					}
					g2.setColor(Color.MAGENTA);
					g2.drawLine(xPos, (oldL * -2), xPos + 1, -(newL * 2));
					g2.setColor(Color.CYAN);
					g2.drawLine(xPos, (oldR * -2), xPos + 1, -(newR * 2));
				}
				else if (logMode == 0) {
					// clear a vertical slice at the current x position
					g2.setColor(Color.BLACK);
					g2.fillRect(xPos+1, 0, 2, panel.getHeight());

					g2.setColor(Color.MAGENTA);
					g2.drawLine(xPos, oldL, xPos + 1, newL);

					g2.setColor(Color.CYAN);
					g2.drawLine(xPos, oldR, xPos + 1, newR);

				}
			}
			oldL = newL;
			oldR = newR;
			xPos ++;
			if(xPos == panel.getWidth()) {
				switch(tm) {

				case AUTO:
					xPos = 0;
					triggered = false;
					break;

				case SINGLE:
					triggered = false;
					break;		

				}
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

	public void setLogMode(int mode) {
		if ((logMode == 0) || (logMode == 1))
			logMode = mode;
	}

	public void setScopeCh1Gain(int gain) {
		scopeCh1Gain = gain;
	}

	public void setWindowRatio(int ratio) {
		windowRatio = ratio;
	}

	public void setScopeCh2Gain(int gain) {
		scopeCh2Gain = gain;
	}

	public double getSignalSlope(int channel) {
		return right;
	}

}
