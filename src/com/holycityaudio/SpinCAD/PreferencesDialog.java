/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * PreferencesDialog.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
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
 */

package com.holycityaudio.SpinCAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class PreferencesDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JCheckBox autoReloadCheckBox;
	private final JCheckBox addDefaultBlocksCheckBox;
	private final SpinCADFile spinCADFile;

	public PreferencesDialog(Frame owner) {
		super(owner, "Preferences", true);
		spinCADFile = new SpinCADFile(owner);

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Startup section
		JPanel startupPanel = new JPanel();
		startupPanel.setLayout(new BoxLayout(startupPanel, BoxLayout.Y_AXIS));
		startupPanel.setBorder(BorderFactory.createTitledBorder("Startup"));

		autoReloadCheckBox = new JCheckBox("Auto-reload last patch or bank on startup");
		autoReloadCheckBox.setSelected(spinCADFile.getAutoReloadLastFile());
		startupPanel.add(autoReloadCheckBox);

		contentPanel.add(startupPanel);

		// New Patch section
		JPanel newPatchPanel = new JPanel();
		newPatchPanel.setLayout(new BoxLayout(newPatchPanel, BoxLayout.Y_AXIS));
		newPatchPanel.setBorder(BorderFactory.createTitledBorder("New Patch"));

		addDefaultBlocksCheckBox = new JCheckBox("Add Input and Output blocks to new patch");
		addDefaultBlocksCheckBox.setSelected(spinCADFile.getAddDefaultBlocks());
		newPatchPanel.add(addDefaultBlocksCheckBox);

		contentPanel.add(newPatchPanel);

		// Button panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");

		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				spinCADFile.setAutoReloadLastFile(autoReloadCheckBox.isSelected());
				spinCADFile.setAddDefaultBlocks(addDefaultBlocksCheckBox.isSelected());
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);

		getRootPane().setDefaultButton(okButton);
		pack();
		setLocationRelativeTo(owner);
	}
}
