package com.holycityaudio.SpinCAD.test;

import java.awt.Dimension;
import java.awt.Point;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class SwingBugTest {

	static JSlider qSlider = null;
	static JLabel qLabel = null;

	public SwingBugTest() {
		qSlider = new JSlider(JSlider.HORIZONTAL, 10, 200, 50);
		qSlider.addChangeListener(new LPF1PChangeListener());
	}

	// ------------------------------------------------------------
	/**
	 * Launch the application.
	 */


	public static void main(String[] args) {

		new SwingBugTest();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				JFrame dspFrame = new JFrame();
				dspFrame.setVisible(true);
				dspFrame.setLayout(new BoxLayout(dspFrame.getContentPane(), BoxLayout.Y_AXIS));


				qLabel = new JLabel();
				qLabel.setAlignmentX(SwingConstants.LEFT);

				updateQLabel(40);

				dspFrame.getContentPane().add(qLabel);
				dspFrame.getContentPane().add(Box.createRigidArea(new Dimension(205,4)));			
				dspFrame.getContentPane().add(qSlider);

				dspFrame.setAlwaysOnTop(true);
				dspFrame.setVisible(true);
				dspFrame.setLocation(new Point(200, 150));
				dspFrame.pack();
				dspFrame.setResizable(true);
			}

			});		
		}

		class LPF1PChangeListener implements ChangeListener { 
			public void stateChanged(ChangeEvent ce) {
				if(ce.getSource() == qSlider) {
					int value = qSlider.getValue();
					updateQLabel(value);
				}
			}
		}

		public static void updateQLabel(int value) {
			qLabel.setText(" Resonance " + String.format("%4.1f",(100.0/value)));		
		}
	}

