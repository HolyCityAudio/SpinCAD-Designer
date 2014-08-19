package com.holycityaudio.SpinCAD;

import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

interface gwSliderSpinnerListener {
	public void sliderChanged();
	public void spinnerChanged();
}

// this is the initiating class

public class SpinSliderSpinner extends JPanel {
	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;
	JSlider filtSlider;
	JLabel  filtLabel;	
	JSpinner filtSpinner;

	public SpinSliderSpinner(final String label) {
		final JPanel internal = new JPanel();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				internal.setLayout(new BoxLayout(internal, BoxLayout.Y_AXIS));

				JPanel topLine = new JPanel();
				topLine.setLayout(new BoxLayout(topLine, BoxLayout.X_AXIS));

				// 				SpinnerNumberModel filtSpinnerNumberModel = new SpinnerNumberModel(gCB.filtToFreq(gCB.getfilt()) * 100, 0.51, 10000.00, 0.01);
				SpinnerNumberModel filtSpinnerNumberModel = new SpinnerNumberModel();

				filtSpinner = new JSpinner(filtSpinnerNumberModel);

				JSpinner.NumberEditor editor = (JSpinner.NumberEditor)filtSpinner.getEditor();  
				filtLabel = new JLabel(label);
				topLine.add(filtLabel);
				topLine.setVisible(true);

				internal.add(Box.createRigidArea(new Dimension(5,4)));			

				DecimalFormat format = editor.getFormat();  
				format.setMinimumFractionDigits(2);  
				format.setMaximumFractionDigits(2);  
				editor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);  
				Dimension d = filtSpinner.getPreferredSize();  
				d.width = 55;  
				filtSpinner.setPreferredSize(d);  

				//				updatefiltSpinner();
				topLine.add(filtSpinner);
				filtSpinner.addChangeListener(new SliderSpinnerListener());

				internal.add(topLine);
				internal.add(Box.createRigidArea(new Dimension(5,5)));			

				//				filtSlider = new JSlider(JSlider.HORIZONTAL, (int)(-29),(int) (100), gCB.logvalToSlider(gCB.filtToFreq(gCB.getfilt()), 100.0));
				filtSlider = new JSlider(JSlider.HORIZONTAL, (int)(-29),(int) (100), 50);
				filtSlider.addChangeListener(new SliderSpinnerListener());
				internal.add(filtSlider);		
				internal.add(Box.createRigidArea(new Dimension(5,4)));			

				internal.setVisible(true);		
			}
		});
		this.add(internal);
	}

	private void updateLabel() {
		filtLabel.setText(String.format(" Frequency (Hz) "));		
	}

	private void updateSpinner() {
		filtSpinner.setValue(25);
	}

	private void updateSlider() {
		filtSlider.setValue((int) (10 * Math.log10(3000)));
	}

	// add change listener for Sliders 
	class SliderSpinnerListener implements ChangeListener { 
		public void stateChanged(ChangeEvent ce) {
			if(ce.getSource() == filtSlider) {
				updateSlider();
				updateSpinner();
			} else if(ce.getSource() == filtSpinner) {
				updateSlider();
				updateSpinner();
			} 
		}
	}

	ArrayList<gwSliderSpinnerListener> listeners = new ArrayList<gwSliderSpinnerListener>();

    public void addListener(gwSliderSpinnerListener toAdd) {
        listeners.add(toAdd);
    }
}

class Responder implements gwSliderSpinnerListener {
    @Override
	public void sliderChanged() {
    	System.out.println("Slider...");
    }    	
	public void spinnerChanged() {
    	System.out.println("Spinner...");
    }    	
}
