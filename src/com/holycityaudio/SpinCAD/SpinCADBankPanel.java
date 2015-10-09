package com.holycityaudio.SpinCAD;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class SpinCADBankPanel extends JPanel implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4298199583629847984L;
		final JButton btnPatch0 = new JButton("Patch 0");
		final JButton btnPatch1 = new JButton("Patch 1");
		final JButton btnPatch2 = new JButton("Patch 2");
		final JButton btnPatch3 = new JButton("Patch 3");
		final JButton btnPatch4 = new JButton("Patch 4");
		final JButton btnPatch5 = new JButton("Patch 5");
		final JButton btnPatch6 = new JButton("Patch 6");
		final JButton btnPatch7 = new JButton("Patch 7");
		int bankIndex = 0;

		public SpinCADBankPanel() {
			super();
			Dimension minButtonSize = new Dimension(100, 20);
			Dimension buttonSize = new Dimension(180, 20);

			this.add(btnPatch0);
			btnPatch0.setPreferredSize(buttonSize);
			btnPatch0.setMinimumSize(minButtonSize);
			btnPatch0.addActionListener(this);

			this.add(btnPatch1);
			btnPatch1.setMinimumSize(minButtonSize);
			btnPatch1.setPreferredSize(buttonSize);
			btnPatch1.addActionListener(this);

			this.add(btnPatch2);
			btnPatch2.setMinimumSize(minButtonSize);
			btnPatch2.setPreferredSize(buttonSize);
			btnPatch2.addActionListener(this);

			this.add(btnPatch3);
			btnPatch3.setMinimumSize(minButtonSize);
			btnPatch3.setPreferredSize(buttonSize);
			btnPatch3.addActionListener(this);

			this.add(btnPatch4);
			btnPatch4.setMinimumSize(minButtonSize);
			btnPatch4.setPreferredSize(buttonSize);
			btnPatch4.addActionListener(this);

			this.add(btnPatch5);
			btnPatch5.setMinimumSize(minButtonSize);
			btnPatch5.setPreferredSize(buttonSize);
			btnPatch5.addActionListener(this);

			this.add(btnPatch6);
			btnPatch6.setMinimumSize(minButtonSize);
			btnPatch6.setPreferredSize(buttonSize);
			btnPatch6.addActionListener(this);

			this.add(btnPatch7);
			btnPatch7.setMinimumSize(minButtonSize);
			btnPatch7.setPreferredSize(buttonSize);
			btnPatch7.addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			if (arg0.getSource() == btnPatch0) {
				bankIndex = 0;
			}
			else if (arg0.getSource() == btnPatch1) {
				bankIndex = 1;
			}
			else if (arg0.getSource() == btnPatch2) {
				bankIndex = 2;
			}
			else if (arg0.getSource() == btnPatch3) {
				bankIndex = 3;
			}
			else if (arg0.getSource() == btnPatch4) {
				bankIndex = 4;
			}
			else if (arg0.getSource() == btnPatch5) {
				bankIndex = 5;
			}
			else if (arg0.getSource() == btnPatch6) {
				bankIndex = 6;
			}
			else if (arg0.getSource() == btnPatch7) {
				bankIndex = 7;
			}
//			model = bank[bankIndex];
//			contentPane.repaint();
			
			/*
			if(bank[bankIndex] != null) {
				model = bank[bankIndex];
				contentPane.repaint();
			}
			else {
				// fileNewPatch(panel, mntmNewFile);
				bank[bankIndex].newModel();
				contentPane.repaint();
			}
			*/
		}
	}
