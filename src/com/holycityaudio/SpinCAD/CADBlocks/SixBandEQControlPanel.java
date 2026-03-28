/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2013 - 2014 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2014.  Look for GSW in code.
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

package com.holycityaudio.SpinCAD.CADBlocks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.holycityaudio.SpinCAD.FineControlSlider;
import com.holycityaudio.SpinCAD.SpinCADFrame;

public class SixBandEQControlPanel extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = 6306397702386815750L;

	JSlider eqSlider0;
	JSlider eqSlider1;
	JSlider eqSlider2;
	JSlider eqSlider3;
	JSlider eqSlider4;
	JSlider eqSlider5;
	JSlider qSlider;

	JTextField eqField0;
	JTextField eqField1;
	JTextField eqField2;
	JTextField eqField3;
	JTextField eqField4;
	JTextField eqField5;
	JTextField qField;

	private SixBandEQCADBlock filter;

	public SixBandEQControlPanel(SixBandEQCADBlock b) {
		this.filter = b;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setTitle("6-Band EQ");
				setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

				SixBandChangeListener sixCL = new SixBandChangeListener();

				eqSlider0 = new FineControlSlider(JSlider.HORIZONTAL, -100, 199, 0);
				eqSlider0.addChangeListener(sixCL);
				eqSlider1 = new FineControlSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider1.addChangeListener(sixCL);
				eqSlider2 = new FineControlSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider2.addChangeListener(sixCL);
				eqSlider3 = new FineControlSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider3.addChangeListener(sixCL);
				eqSlider4 = new FineControlSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider4.addChangeListener(sixCL);
				eqSlider5 = new FineControlSlider(JSlider.HORIZONTAL,  -100, 199, 0);
				eqSlider5.addChangeListener(sixCL);

				qSlider = new FineControlSlider(JSlider.HORIZONTAL, 100, 400, 100);
				qSlider.addChangeListener(sixCL);

				eqField0 = new JTextField();
				eqField0.setHorizontalAlignment(JTextField.CENTER);
				eqField0.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							double val = Double.parseDouble(eqField0.getText().replaceAll("[^0-9.\\-]", ""));
							val = Math.max(-1.0, Math.min(1.99, val));
							filter.seteqLevel(0, val);
							eqSlider0.setValue((int) Math.round(val * 100.0));
							updateEqField0();
						} catch (NumberFormatException ex) {
							updateEqField0();
						}
					}
				});

				eqField1 = new JTextField();
				eqField1.setHorizontalAlignment(JTextField.CENTER);
				eqField1.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							double val = Double.parseDouble(eqField1.getText().replaceAll("[^0-9.\\-]", ""));
							val = Math.max(-1.0, Math.min(1.99, val));
							filter.seteqLevel(1, val);
							eqSlider1.setValue((int) Math.round(val * 100.0));
							updateEqField1();
						} catch (NumberFormatException ex) {
							updateEqField1();
						}
					}
				});

				eqField2 = new JTextField();
				eqField2.setHorizontalAlignment(JTextField.CENTER);
				eqField2.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							double val = Double.parseDouble(eqField2.getText().replaceAll("[^0-9.\\-]", ""));
							val = Math.max(-1.0, Math.min(1.99, val));
							filter.seteqLevel(2, val);
							eqSlider2.setValue((int) Math.round(val * 100.0));
							updateEqField2();
						} catch (NumberFormatException ex) {
							updateEqField2();
						}
					}
				});

				eqField3 = new JTextField();
				eqField3.setHorizontalAlignment(JTextField.CENTER);
				eqField3.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							double val = Double.parseDouble(eqField3.getText().replaceAll("[^0-9.\\-]", ""));
							val = Math.max(-1.0, Math.min(1.99, val));
							filter.seteqLevel(3, val);
							eqSlider3.setValue((int) Math.round(val * 100.0));
							updateEqField3();
						} catch (NumberFormatException ex) {
							updateEqField3();
						}
					}
				});

				eqField4 = new JTextField();
				eqField4.setHorizontalAlignment(JTextField.CENTER);
				eqField4.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							double val = Double.parseDouble(eqField4.getText().replaceAll("[^0-9.\\-]", ""));
							val = Math.max(-1.0, Math.min(1.99, val));
							filter.seteqLevel(4, val);
							eqSlider4.setValue((int) Math.round(val * 100.0));
							updateEqField4();
						} catch (NumberFormatException ex) {
							updateEqField4();
						}
					}
				});

				eqField5 = new JTextField();
				eqField5.setHorizontalAlignment(JTextField.CENTER);
				eqField5.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							double val = Double.parseDouble(eqField5.getText().replaceAll("[^0-9.\\-]", ""));
							val = Math.max(-1.0, Math.min(1.99, val));
							filter.seteqLevel(5, val);
							eqSlider5.setValue((int) Math.round(val * 100.0));
							updateEqField5();
						} catch (NumberFormatException ex) {
							updateEqField5();
						}
					}
				});

				qField = new JTextField();
				qField.setHorizontalAlignment(JTextField.CENTER);
				qField.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							double val = Double.parseDouble(qField.getText().replaceAll("[^0-9.\\-]", ""));
							val = Math.max(1.0, Math.min(4.0, val));
							filter.setqLevel(val);
							qSlider.setValue((int) Math.round(val * 100.0));
							updateQField();
						} catch (NumberFormatException ex) {
							updateQField();
						}
					}
				});

				getContentPane().add(eqField0);
				getContentPane().add(eqSlider0);
				getContentPane().add(eqField1);
				getContentPane().add(eqSlider1);
				getContentPane().add(eqField2);
				getContentPane().add(eqSlider2);
				getContentPane().add(eqField3);
				getContentPane().add(eqSlider3);
				getContentPane().add(eqField4);
				getContentPane().add(eqSlider4);
				getContentPane().add(eqField5);
				getContentPane().add(eqSlider5);

				getContentPane().add(qField);
				getContentPane().add(qSlider);


				eqSlider0.setValue((int) Math.round(((filter.geteqLevel(0)) * 100.0)));
				updateEqField0();

				eqSlider1.setValue((int) Math.round((filter.geteqLevel(1) * 100.0)));
				updateEqField1();

				eqSlider2.setValue((int) Math.round((filter.geteqLevel(2) * 100.0)));
				updateEqField2();

				eqSlider3.setValue((int) Math.round((filter.geteqLevel(3) * 100.0)));
				updateEqField3();

				eqSlider4.setValue((int) Math.round((filter.geteqLevel(4) * 100.0)));
				updateEqField4();

				eqSlider5.setValue((int) Math.round((filter.geteqLevel(5) * 100.0)));
				updateEqField5();

				qSlider.setValue((int) Math.round((filter.getQLevel() * 100.0)));
				updateQField();

				setVisible(true);
				setLocationRelativeTo(SpinCADFrame.getInstance());
				pack();
				setResizable(false);
				setAlwaysOnTop(true);
			}
		});
	}

	private void updateEqField0() {
		eqField0.setText("80 Hz level " + String.format("%2.2f", filter.geteqLevel(0)));
	}

	private void updateEqField1() {
		eqField1.setText("160 Hz level " + String.format("%2.2f", filter.geteqLevel(1)));
	}

	private void updateEqField2() {
		eqField2.setText("320 Hz level " + String.format("%2.2f", filter.geteqLevel(2)));
	}

	private void updateEqField3() {
		eqField3.setText("640 Hz level " + String.format("%2.2f", filter.geteqLevel(3)));
	}

	private void updateEqField4() {
		eqField4.setText("1280 Hz level " + String.format("%2.2f", filter.geteqLevel(4)));
	}

	private void updateEqField5() {
		eqField5.setText("2560 Hz level " + String.format("%2.2f", filter.geteqLevel(5)));
	}

	private void updateQField() {
		qField.setText("Resonance " + String.format("%2.1f", filter.getQLevel()));
	}

	class SixBandChangeListener implements ChangeListener {
		public void stateChanged(ChangeEvent ce) {
			if (ce.getSource() == eqSlider0) {
				filter.seteqLevel(0, (double) eqSlider0.getValue() / 100.0);
				updateEqField0();
			} else if (ce.getSource() == eqSlider1) {
				filter.seteqLevel(1, (double) eqSlider1.getValue() / 100.0);
				updateEqField1();
			} else if (ce.getSource() == eqSlider2) {
				filter.seteqLevel(2, (double) eqSlider2.getValue() / 100.0);
				updateEqField2();
			} else if (ce.getSource() == eqSlider3) {
				filter.seteqLevel(3, (double) eqSlider3.getValue() / 100.0);
				updateEqField3();
			} else if (ce.getSource() == eqSlider4) {
				filter.seteqLevel(4, (double) eqSlider4.getValue() / 100.0);
				updateEqField4();
			} else if (ce.getSource() == eqSlider5) {
				filter.seteqLevel(5, (double) eqSlider5.getValue() / 100.0);
				updateEqField5();
			} else if (ce.getSource() == qSlider) {
				filter.setqLevel((double) qSlider.getValue() / 100.0);
				updateQField();
			}
		}
	}
}