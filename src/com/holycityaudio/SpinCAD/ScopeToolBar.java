package com.holycityaudio.SpinCAD;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

// ======================================================================================================
// ================= used for the status toolbar and simulator start/stop
// button
public class ScopeToolBar extends JToolBar implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3040642773216953900L;
	final JLabel ch1_Vertical_Gain_Label = new JLabel(" Ch 1 Gain: ");
	
	String[] gainLabels = new String[] {"1x", "2x", "4x", "8x"};
	JComboBox<String> ch1_Vertical_Gain = new JComboBox<>(gainLabels);
	
	final JLabel ch2_Vertical_Gain_Label = new JLabel(" Ch 2 Gain: ");
	JComboBox<String> ch2_Vertical_Gain = new JComboBox<>(gainLabels);
	
	String[] timebaseLabels = new String[] {"32", "64", "128", "256"};
	final JLabel timebaseLabel = new JLabel(" Time Base: ");
	JComboBox<String> timebase = new JComboBox<>(gainLabels);
	
	String[] triggerModeLabels = new String[] {"Auto", "Normal", "Single"};
	final JLabel triggerModeLabel = new JLabel(" Trigger Mode: ");
	final JComboBox<String> triggerMode = new JComboBox(triggerModeLabels);
	
	class Task extends SwingWorker<Void, Void> {
		/*
		 * Main task. Executed in background thread.
		 */
		@Override
		public Void doInBackground() {
			// Sleep for at least one second to simulate "startup".
			try {
				Thread.sleep(200);
			} catch (InterruptedException ignore) {
			}
			done();
			return null;
		}

		/*
		 * Executed in event dispatch thread
		 */
		public void done() {
		}
	}

	// ==============================================================
	// == Resources toolbar
	public ScopeToolBar() {
		super();

		// Call setStringPainted now so that the progress bar height
		// stays the same whether or not the string is shown.

		ch1_Vertical_Gain.setToolTipText(" Ch 1 Gain ");
		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		ch1_Vertical_Gain.setBorder(border);

		ch2_Vertical_Gain.setToolTipText(" Ch 2 Gain ");
		ch2_Vertical_Gain.setBorder(border);
		triggerMode.add(new JLabel("Auto"));
		triggerMode.add(new JLabel("Normal"));
		
		add(ch1_Vertical_Gain_Label);
		add(ch1_Vertical_Gain);
		add(ch2_Vertical_Gain_Label);
		add(ch2_Vertical_Gain);

		add(timebaseLabel);
		add(timebase);
		add(triggerModeLabel);
		add(triggerMode);
	}

	/**
	 * Invoked when the user presses the start button.
	 */

	public void actionPerformed(ActionEvent evt) {
		// progressBar.setIndeterminate(true);

	}

	public void update(SpinCADPatch p) {
	}
}


