package com.holycityaudio.SpinCAD;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

// ======================================================================================================
// ================= used for the status toolbar and simulator start/stop
// button
public class ModelResourcesToolBar extends JToolBar implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3040642773216953900L;
	final JProgressBar progressBar_2 = new JProgressBar();
	final JProgressBar progressBar_1 = new JProgressBar();
	final JProgressBar progressBar = new JProgressBar();
	final JTextField ramp0Bar = new JTextField("RMP 0", 6);
	final JTextField ramp1Bar = new JTextField("RMP 1", 6);
	final JTextField sine0Bar = new JTextField("SIN 0", 6);
	final JTextField sine1Bar = new JTextField("SIN 1", 6);
	private SpinCADModel model;

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
	public ModelResourcesToolBar() {
		super();

		// Call setStringPainted now so that the progress bar height
		// stays the same whether or not the string is shown.

		progressBar_2.setToolTipText("Code Length");
		progressBar_2.setMaximum(128);
		progressBar_2.setBackground(Color.CYAN);
		progressBar_2.setString("Instructions Used");
		progressBar_2.setStringPainted(true);
		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		progressBar_2.setBorder(border);

		progressBar.setToolTipText("Registers");
		progressBar.setMaximum(32);
		progressBar.setBackground(Color.CYAN);
		progressBar.setString("Registers Used");
		progressBar.setStringPainted(true);
		progressBar.setBorder(border);

		progressBar_1.setMaximum(32768);
		progressBar_1.setToolTipText("Delay RAM");
		progressBar_1.setBackground(Color.CYAN);
		progressBar_1.setString("Delay RAM Used");
		progressBar_1.setStringPainted(true);
		progressBar_1.setBorder(border);

		ramp0Bar.setHorizontalAlignment(JTextField.CENTER);
		ramp0Bar.setBackground(Color.GREEN);
		ramp0Bar.setForeground(Color.BLUE);

		ramp1Bar.setHorizontalAlignment(JTextField.CENTER);
		ramp1Bar.setBackground(Color.GREEN);
		ramp1Bar.setForeground(Color.BLUE);

		sine0Bar.setHorizontalAlignment(JTextField.CENTER);
		sine0Bar.setBackground(Color.GREEN);
		sine0Bar.setForeground(Color.BLUE);

		sine1Bar.setHorizontalAlignment(JTextField.CENTER);
		sine1Bar.setBackground(Color.GREEN);
		sine1Bar.setForeground(Color.BLUE);

		Dimension lfoBarDim = sine1Bar.getPreferredSize();

		ramp0Bar.setMaximumSize(lfoBarDim);
		ramp1Bar.setMaximumSize(lfoBarDim);
		sine0Bar.setMaximumSize(lfoBarDim);
		sine1Bar.setMaximumSize(lfoBarDim);

		add(progressBar_2);
		add(progressBar);
		add(progressBar_1);

		add(sine0Bar);
		add(sine1Bar);
		add(ramp0Bar);
		add(ramp1Bar);
	}

	/**
	 * Invoked when the user presses the start button.
	 */

	public void actionPerformed(ActionEvent evt) {
		// progressBar.setIndeterminate(true);
		int codeLength = model.sortAlignGen();
		System.out.println("Code: " + codeLength);

		if (codeLength < 80) {
			progressBar_2.setForeground(Color.green);
		} else if (codeLength < 105) {
			progressBar_2.setForeground(Color.yellow);
		} else if (codeLength <= 128) {
			progressBar_2.setForeground(Color.orange);
		} else {
			progressBar_2.setForeground(Color.red);
		}
		
		progressBar_2.setToolTipText("Code Length: " + codeLength);
		progressBar_2.setValue(codeLength);

		// getModel();
		int nRegs = model.getRenderBlock().getNumRegs() - 32;
		if (nRegs < 20) {
			progressBar.setForeground(Color.green);
		} else if (nRegs < 26) {
			progressBar.setForeground(Color.yellow);
		} else if (nRegs <= 32) {
			progressBar.setForeground(Color.orange);
		} else {
			progressBar.setForeground(Color.red);
		}
		progressBar.setToolTipText("Registers used: " + nRegs);
		progressBar.setValue(nRegs);

		// getModel();
		int ramUsed = model.getRenderBlock().getDelayMemAllocated();
		if (ramUsed < 20000) {
			progressBar_1.setForeground(Color.green);
		} else if (ramUsed < 26000) {
			progressBar_1.setForeground(Color.yellow);
		} else if (ramUsed <= 32768) {
			progressBar_1.setForeground(Color.orange);
		} else {
			progressBar_1.setForeground(Color.red);
		}

		progressBar_1.setToolTipText("RAM Used: " + ramUsed);
		progressBar_1.setValue(ramUsed);

		int rampLFO_0 = SpinCADModel.countLFOReferences(model,"LoadRampLFO(0");
		if(rampLFO_0 == 0) {
			ramp0Bar.setBackground(Color.GREEN);
			ramp0Bar.setForeground(Color.black);
		} else if(rampLFO_0 == 1) {
			ramp0Bar.setBackground(Color.YELLOW);
			ramp0Bar.setForeground(Color.black);
		} else { 
			ramp0Bar.setBackground(Color.RED);
			ramp0Bar.setForeground(Color.white);
		}

		int rampLFO_1 = SpinCADModel.countLFOReferences(model,"LoadRampLFO(1");
		if(rampLFO_1 == 0) {
			ramp1Bar.setBackground(Color.GREEN);
			ramp1Bar.setForeground(Color.black);
		} else if(rampLFO_1 == 1) {
			ramp1Bar.setBackground(Color.YELLOW);
			ramp1Bar.setForeground(Color.black);
		} else { 
			ramp1Bar.setBackground(Color.RED);
			ramp1Bar.setForeground(Color.white);
		}

		int sineLFO_0 = SpinCADModel.countLFOReferences(model,"LoadSinLFO(0");
		if(sineLFO_0 == 0) {
			sine0Bar.setBackground(Color.GREEN);
			sine0Bar.setForeground(Color.black);
		} else if(sineLFO_0 == 1) {
			sine0Bar.setBackground(Color.YELLOW);
			sine0Bar.setForeground(Color.black);
		} else { 
			sine0Bar.setBackground(Color.RED);
			sine0Bar.setForeground(Color.white);
		}

		int sineLFO_1 = SpinCADModel.countLFOReferences(model,"LoadSinLFO(1");
		if(sineLFO_1 == 0) {
			sine1Bar.setBackground(Color.GREEN);
			sine1Bar.setForeground(Color.black);
		} else if(sineLFO_1 == 1) {
			sine1Bar.setBackground(Color.YELLOW);
			sine1Bar.setForeground(Color.black);
		} else { 
			sine1Bar.setBackground(Color.RED);
			sine1Bar.setForeground(Color.white);
		}
	}

	public void update(SpinCADPatch p) {
		model = p.patchModel;
		if(p.isHexFile == false) {
			this.setVisible(true);
			ActionEvent evt = null;
			actionPerformed(evt);
		} else {
			this.setVisible(false);
		}
	}
}


