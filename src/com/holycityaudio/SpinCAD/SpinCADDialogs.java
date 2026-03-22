package com.holycityaudio.SpinCAD;

import java.awt.Component;
import javax.swing.JOptionPane;

public class SpinCADDialogs {

	// Swing dialog boxes.
	// All methods accept a Component parent so Swing can position the dialog
	// on the same monitor as the application window.  The static no-parent
	// overloads are kept for backward compatibility but should not be used
	// for new code.

	/** Show a message dialog anchored to the given parent component. */
	public static void MessageBox(Component parent, String title, String message) {
		JOptionPane.showMessageDialog(parent, message, title,
				JOptionPane.DEFAULT_OPTION);
	}

	/** @deprecated Pass a parent component so dialogs appear on the right monitor. */
	@Deprecated
	public static void MessageBox(String title, String message) {
		// Use null — at least avoids creating a stray JFrame on the primary screen.
		// Callers should be migrated to MessageBox(parent, title, message).
		JOptionPane.showMessageDialog(null, message, title,
				JOptionPane.DEFAULT_OPTION);
	}

	/** Show a yes/no confirm dialog anchored to the given parent component. */
	public static int yesNoBox(Component parent, String title, String question) {
		return JOptionPane.showConfirmDialog(parent, question, title,
				JOptionPane.YES_NO_OPTION);
	}

	/** @deprecated Pass a parent component so dialogs appear on the right monitor. */
	@Deprecated
	public static int yesNoBox(javax.swing.JPanel panel, String title, String question) {
		return JOptionPane.showConfirmDialog(panel, question, title,
				JOptionPane.YES_NO_OPTION);
	}
}
