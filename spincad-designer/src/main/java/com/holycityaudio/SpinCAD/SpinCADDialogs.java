package com.holycityaudio.SpinCAD;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SpinCADDialogs {
	
	// Swing dialog boxes.

	public static void MessageBox(String title, String message) {
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame,
				message, title,
				JOptionPane.DEFAULT_OPTION);
	}

	public static int yesNoBox(JPanel panel, String title, String question) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(panel,
				question,
				title, dialogButton);
		return dialogResult;
	}
}
