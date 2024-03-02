/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADFrame.java
 * Copyright (C) 2013 - 2015 - Gary Worsham
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

package com.holycityaudio.SpinCAD;

import java.awt.BorderLayout;

import javax.sound.sampled.UnsupportedAudioFileException;
// import javax.sound.sampled.spi.AudioFileReader;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.ActionListener;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.andrewkilpatrick.elmGen.Debug;
import org.andrewkilpatrick.elmGen.ElmProgram;

import com.holycityaudio.SpinCAD.CADBlocks.FBInputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.FBOutputCADBlock;

public class SpinCADFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */

	int buildNum = 1037;

	// Swing things
	private JPanel contentPane;
	//=====================s====================================================================
	// pb shows instructions, registers, and RAM used.  It also shows allocation state of LFOs
	private final ModelResourcesToolBar pb = new ModelResourcesToolBar();

	// etb is used to show the pin name when you hover
	public final EditResourcesToolBar etb = new EditResourcesToolBar();

	private final JPanel controlPanels = new JPanel();
	// 
	// topPanel holds bankPanel and simPanel
	private final JPanel topPanel = new JPanel();
	private final JPanel bankPanel = new bankPanel();
	private final SpinCADPanel panel = new SpinCADPanel(this);

	//=============================================================
	// Simulator display and control items
	SpinCADSimulator simX = new SpinCADSimulator(this, new SpinCADPatch());

	private final JPanel simPanel = new JPanel();

	// BANK ========================================================
	// stuff to do with working on a bank of 8 vs. just one patch
	// may remove bank mode variable, as will probably always be in bank mode
	boolean bankMode = true;
	int bankIndex = 0;

	// eeprom is where ALL the data for 8 patches is stored
	SpinCADBank eeprom = new SpinCADBank();

	// modelSave is used to undo deletes
	ByteArrayOutputStream modelSave;


	// pasteBuffer is used to paste after copying
	ByteArrayOutputStream pasteBuffer;
	// used in copy/paste
	SpinCADBlock blk = new SpinCADBlock(0,0);

	private int canUndo = 0;

	// ------------------------------------------------------------
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					SpinCADFrame dspFrame = new SpinCADFrame();
					dspFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */

	@SuppressWarnings("unused")
	public SpinCADFrame() {
		// setTitle("SpinCAD Designer - Untitled");
		updateFrameTitle();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		simX.updateSliders(eeprom.patch[0]);

		double time = SpinCADBlock.filtToTime(SpinCADBlock.timeToFilt(0.356));

		panel.setBackground(SystemColor.inactiveCaption);

		WindowListener exitListener = window();
		addWindowListener(exitListener);

		// ==========================================================================================
		// ======================= main panel
		// =========================================================


		contentPane = new JPanel();
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		setPreferredSize(new Dimension(450, 1200));

		contentPane.add(scrollPane, BorderLayout.CENTER);

		// =========================================================
		// ======================= toolbars ========================
		// =========================================================

		JPanel toolBarPanel = new JPanel();
		toolBarPanel.setLayout(new BoxLayout(toolBarPanel, BoxLayout.Y_AXIS));
		contentPane.add(toolBarPanel, BorderLayout.SOUTH);

		etb.setFloatable(false);
		toolBarPanel.add(etb, BorderLayout.SOUTH);

		pb.setFloatable(false);
		toolBarPanel.add(pb, BorderLayout.SOUTH);

		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

		//--------------------------------------
		// patch selector buttons in bank toolbar

		Border border = BorderFactory.createBevelBorder(BevelBorder.RAISED);

		bankPanel.setLayout(new GridLayout(1,8));
		bankPanel.setVisible(true);
		topPanel.add(bankPanel, BorderLayout.NORTH);

		//----------------------------------------

		simX.sctb.setFloatable(false);
		simX.sctb.setBorder(border);
		simPanel.setLayout(new BoxLayout(simPanel, BoxLayout.Y_AXIS));
		simPanel.add(simX.sctb);

		topPanel.add(simPanel, BorderLayout.NORTH);

		contentPane.add(topPanel, BorderLayout.NORTH);
		// level monitor is not currently used
		//		contentPane.add(simX.levelMonitor, BorderLayout.WEST);

		simPanel.add(simX.loggerPanel);
		simX.loggerPanel.setVisible(false);
		// re-enabling scope for the time being
		simPanel.add(simX.scopePanel);
		simX.scopePanel.setVisible(false);

		simPanel.add(simX.stb);
		simX.stb.setFloatable(false);
		simX.stb.setVisible(true);

		// controlPanels.setFloatable(false);
		contentPane.add(controlPanels, BorderLayout.EAST);
		controlPanels.setLayout(new BoxLayout(controlPanels, BoxLayout.Y_AXIS));
		
		// initialize file paths in preferences
		SpinCADFile fsp = new SpinCADFile();
		fsp.init_prefs();
		
		// ======================================================
		// ; ==================== menu bar and items ==========

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFileMenu = new JMenu("File");
		menuBar.add(mnFileMenu);

		// Patch File operations

		JMenuItem mntmNewPatch = new JMenuItem("New Patch");
		mntmNewPatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(panel,
							"You have unsaved changes!  Continue?", "Warning!",
							dialogButton);
					if (dialogResult == JOptionPane.NO_OPTION) {
						return;
					}
				}
				eeprom.patch[bankIndex] = new SpinCADPatch();
				//				bankPanel.setVisible(false);
				updateAll();
				// repaint();
			}
		});
		mntmNewPatch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmNewPatch);

		JMenuItem mntmOpenPatch = new JMenuItem("Open Patch");
		mntmOpenPatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f = new SpinCADFile();
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(panel, "Warning!",
							"You have unsaved changes!  Continue?");
					if (dialogResult == JOptionPane.NO_OPTION) {
						// eeprom.patch[bankIndex].patchModel.newModel();
						repaint();
						return;
					}
				}
				SpinCADPatch p = f.fileOpenPatch();
				if (p != null) {
					eeprom.patch[bankIndex] = p;
					eeprom.patch[bankIndex].patchModel.getIndexFB();
					eeprom.patch[bankIndex].patchModel.presetIndexFB();
					eeprom.patch[bankIndex].setChanged(false);						
					eeprom.changed = true;
					updateAll();
					repaint();
				}
			}
		});
		mntmOpenPatch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmOpenPatch);

		JMenuItem mntmOpenHex = new JMenuItem("Open Hex");
		mntmOpenHex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f = new SpinCADFile();
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(panel, "Warning!",
							"You have unsaved changes!  Continue?");
					if (dialogResult == JOptionPane.NO_OPTION) {
						//						eeprom.patch[bankIndex].patchModel.newModel();
						repaint();
						return;
					}
				} 
				SpinCADPatch p = f.fileOpenHex();
				if (p != null) {
					eeprom.patch[bankIndex] = p;
					eeprom.changed = true;
					updateAll();
					repaint();
				}
			}
		});
		mntmOpenHex.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmOpenHex);

		JMenuItem mntmSavePatch = new JMenuItem("Save Patch");
		mntmSavePatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f = new SpinCADFile();
				if(eeprom.patch[bankIndex].patchFileName != "Untitled") {
					f.fileSavePatch(eeprom.patch[bankIndex]);
				} else {
					f.fileSavePatchAs(eeprom.patch[bankIndex]);
				}
				updateAll(false);
			}
		});
		mntmSavePatch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmSavePatch);

		JMenuItem mntmSavePatchAs = new JMenuItem("Save Patch As");
		mntmSavePatchAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.patch[bankIndex].cb.setVersion("Patch saved from SpinCAD Designer version " + buildNum);
				SpinCADFile f = new SpinCADFile();
				f.fileSavePatchAs(eeprom.patch[bankIndex]);
				updateAll(false);
			}
		});
		mntmSavePatchAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmSavePatchAs);

		JMenuItem mntmInfo = new JMenuItem("Patch Information");
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				commentBlockPatch cbp = new commentBlockPatch(eeprom.patch[bankIndex]);
				cbp.cbPnl.show();
			}
		});
		mntmInfo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmInfo);

		mnFileMenu.addSeparator();

		JMenuItem mntmSaveAsm = new JMenuItem("Save Patch as ASM");
		mntmSaveAsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.patch[bankIndex].patchModel.sortAlignGen();
				SpinCADFile f = new SpinCADFile();
				f.fileSaveAsm(eeprom.patch[bankIndex]);
			}
		});
		mnFileMenu.add(mntmSaveAsm);

		JMenuItem mntmCopyToClipboard = new JMenuItem("Copy ASM to Clipboard");
		mntmCopyToClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.patch[bankIndex].patchModel.sortAlignGen();
				StringSelection stringSelection = new StringSelection (eeprom.patch[bankIndex].cb.getComments() + eeprom.patch[bankIndex].patchModel.getRenderBlock().getProgramListing(1));
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
			}
		});
		mnFileMenu.add(mntmCopyToClipboard);

		mnFileMenu.addSeparator();

		// Bank File operations
		JMenuItem mntmNewBank = new JMenuItem("New Bank");
		mntmNewBank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(panel,
							"You have unsaved changes!  Continue?", "Warning!",
							dialogButton);
					if (dialogResult == JOptionPane.NO_OPTION) {
						return;
					}
				}
				eeprom.bankFileName = "Untitled";
				bankPanel.setVisible(true);
				eeprom.patch[bankIndex].patchModel.newModel();
				eeprom = new SpinCADBank();
				updateAll();
			}
		});
		mntmNewBank.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));
		mnFileMenu.add(mntmNewBank);

		JMenuItem mntmOpenBank = new JMenuItem("Open Bank");
		mntmOpenBank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f= new SpinCADFile();
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(panel, "Warning!",
							"You have unsaved changes!  Continue?");
					if (dialogResult == JOptionPane.NO_OPTION) {
						return;
					}
				}
				SpinCADBank b = f.fileOpenBank();
				if (b != null) {
					eeprom = b;
					updateAll(false);
				}
			}
		});
		mntmOpenBank.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		mnFileMenu.add(mntmOpenBank);

		JMenuItem mntmSaveBank = new JMenuItem("Save Bank");
		mntmSaveBank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(eeprom.bankFileName != "Untitled") {
					SpinCADFile f = new SpinCADFile();
					f.fileSaveBank(eeprom);
				} else {
					SpinCADFile f = new SpinCADFile();
					f.fileSaveBankAs(eeprom);
				}
				updateAll(false);
				eeprom.changed = false;
			}
		});
		mntmSaveBank.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
		mnFileMenu.add(mntmSaveBank);

		JMenuItem mntmSaveBankAs = new JMenuItem("Save Bank As");
		mntmSaveBankAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.cb.setVersion("Bank saved from SpinCAD Designer version " + buildNum);
				SpinCADFile f = new SpinCADFile();
				f.fileSaveBankAs(eeprom);
				eeprom.changed = false;
				updateAll();
			}
		});
		mntmSaveBankAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		mnFileMenu.add(mntmSaveBankAs);

		JMenuItem mntmBankInfo = new JMenuItem("Bank Information");
		mntmBankInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				commentBlockBank cbb = new commentBlockBank(eeprom);
				cbb.cbPnl.show();
			}
		});
		mntmBankInfo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
		mnFileMenu.add(mntmBankInfo);

		mnFileMenu.addSeparator();

		JMenuItem mntmSaveHex = new JMenuItem("Export Bank to Hex");
		mntmSaveHex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f = new SpinCADFile();
				f.fileSaveHex(eeprom);
			}
		});

		mnFileMenu.add(mntmSaveHex);

		JMenuItem mntmSavePrj = new JMenuItem("Export Bank to Spin Project");
		mntmSavePrj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f = new SpinCADFile();
				f.fileSaveSpj(eeprom);
			}
		});

		mnFileMenu.add(mntmSavePrj);

		mnFileMenu.addSeparator();

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(panel, "Warning!", 
							"You have unsaved changes!  Save first?");				
					if (dialogResult == JOptionPane.YES_OPTION) {
						File fileToBeSaved = new File(eeprom.patch[bankIndex].patchFileName);
						if (fileToBeSaved.exists()) {
							String filePath = fileToBeSaved.getPath();
							SpinCADFile f = new SpinCADFile();
							f.fileSavePatch(eeprom.patch[bankIndex]);
						} else {
							SpinCADFile f = new SpinCADFile();
							f.fileSavePatchAs(eeprom.patch[bankIndex]);
						}
					}
					System.exit(0);
				}
			}
		});
		mnFileMenu.add(mntmExit);

		JMenu mn_edit = new JMenu("Edit");
		menuBar.add(mn_edit);

		final JMenuItem mntm_Copy = new JMenuItem("Copy");
		mntm_Copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveModelToPasteBuffer();
			}
		});
		mntm_Copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		mn_edit.add(mntm_Copy);

		final JMenuItem mntm_Paste = new JMenuItem("Paste");
		mntm_Paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				paste();
			}
		});
		mntm_Paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		mn_edit.add(mntm_Paste);

		final JMenuItem mntm_Cut = new JMenuItem("Cut");
		mntm_Cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delete();
				updateAll();
			}
		});
		mntm_Cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		mn_edit.add(mntm_Cut);

		final JMenuItem mntm_Undo = new JMenuItem("Undo");
		mntm_Undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undo();
				updateAll();
			}
		});
		mntm_Undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		mn_edit.add(mntm_Undo);

		JMenu mn_io_mix = new JMenu("Loop");
		menuBar.add(mn_io_mix);

		final JMenuItem mntm_AddFB = new JMenuItem("Add");
		mntm_AddFB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i =  eeprom.patch[bankIndex].patchModel.getIndexFB();
				FBInputCADBlock pcB = new FBInputCADBlock(50, 100, i);
				dropBlock(panel, pcB);

				FBOutputCADBlock pcB1 = new FBOutputCADBlock(50, 300, i);
				dropBlock(panel, pcB1);
				eeprom.patch[bankIndex].patchModel.setIndexFB(i + 1);
			}
		});
		mn_io_mix.add(mntm_AddFB);

		// most of the menu is generated right here.
		// standardmenu is generated by the spincadmenu DSL
		new standardMenu(this, panel, menuBar);

		final JMenu mnSimulator = new JMenu("Simulator");
		menuBar.add(mnSimulator);

		final JMenuItem mntmSimLogger = new JCheckBoxMenuItem("Enable Level Viewer");
		mntmSimLogger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(simX.loggerIsVisible == true) {
					simX.loggerIsVisible = false;
				}
				else {
					simX.loggerIsVisible = true;
				}
			}
		});
		mnSimulator.add(mntmSimLogger);

		final JMenuItem mntmSimScope = new JCheckBoxMenuItem("Enable Scope");
		mntmSimScope.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(simX.scopeIsVisible == true) {
					simX.scopeIsVisible = false;
				}
				else {
					simX.scopeIsVisible = true;
				}
			}
		});
		mnSimulator.add(mntmSimScope);

		mnSimulator.addSeparator();
		JMenuItem mntmSimSendToFile = new JRadioButtonMenuItem("Simulator->File");
		mntmSimSendToFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simX.setOutputFileMode(true);
				simX.sim.setLoopMode(false);
			}
		});
		mnSimulator.add(mntmSimSendToFile);

		JMenuItem mntmSimSendToSound = new JRadioButtonMenuItem("Simulator->Sound Card", true);
		mntmSimSendToSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simX.outputFile = null;
				simX.sim.setLoopMode(true);
			}
		});
		mnSimulator.add(mntmSimSendToSound);

		ButtonGroup bg = new ButtonGroup();
		bg.add(mntmSimSendToFile);
		bg.add(mntmSimSendToSound);
		mnSimulator.addSeparator();

		JMenuItem mntmSimOutFile = new JMenuItem("Set Simulator Output File");
		mntmSimOutFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				simX.setSimulatorOutputFile();
			}
		});
		mnSimulator.add(mntmSimOutFile);

		JMenuItem mntmSourceFile = new JMenuItem("Set Simulator Source file");
		mntmSourceFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					simX.getSimulatorFile();
				} catch (UnsupportedAudioFileException e) {
					SpinCADDialogs.MessageBox("Simulator File Error", "Make sure that your simulator source\n"
							+ "file is a stereo 16 bit WAV file sampled \nat 32768, 44100, or 48000 Hz.");
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		mnSimulator.add(mntmSourceFile);		

		mnSimulator.addSeparator();
		JMenuItem mntmSetSampleRate = new JMenuItem("Set Sample Rate");
		mntmSetSampleRate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SampleRateComboBox srCB = new SampleRateComboBox();
				srCB.setLocation(mnSimulator.getLocation());
			}
		});
		mnSimulator.add(mntmSetSampleRate);

		if(Debug.DEBUG == true) {
			mnSimulator.addSeparator();
			JMenuItem mntmDebugFile = new JMenuItem("Set Simulator Debug file");
			mntmDebugFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						simX.setSimulatorDebugFile();
					} catch (IOException e) {
						SpinCADDialogs.MessageBox("Simulator Debug File Error", "Uhmmmm....");
					}
				}
			});
			mnSimulator.add(mntmDebugFile);
		}

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelp = new JMenuItem("Help");
		mntmHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					openWebpage(new URL("https://holy-city-audio.gitbook.io/spincad-designer"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		mnHelp.add(mntmHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADDialogs.MessageBox("About SpinCAD Designer", "Version 0.98 Build " + buildNum + "\n"
						+ "Copyright 2024 Gary Worsham, Holy City Audio\n" + 
						" This program is distributed in the hope that it will be useful," +
						"\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\n" + 
						"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
			}
		});
		mnHelp.add(mntmAbout);
	}

	/**
	 * @param panel
	 * @param mntmExit
	 */

	void updateFrameTitle() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 	
				String bankName = eeprom.bankFileName + (eeprom.changed ? " * ]" : "]");
				String patchName = bankIndex + " [" + eeprom.patch[bankIndex].patchFileName + (eeprom.patch[bankIndex].getChanged() ? " * ]" : "]");
				setTitle("SpinCAD Designer - Bank [" + bankName + " Patch " + patchName);			
			}
		});
	}

	private WindowListener window() {
		WindowListener exitListener = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				int confirm = JOptionPane.showOptionDialog(null,
						"Do you wish to exit SpinCAD?", "Exit Confirmation",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (confirm == JOptionPane.YES_OPTION) {
					System.exit(0);
				}
			}
		};
		return exitListener;
	}

	public void dropBlock(SpinCADPanel p, SpinCADBlock b) {
		eeprom.patch[bankIndex].patchModel.addBlock(b);
		eeprom.patch[bankIndex].setChanged(true);
		p.unselectAll(this);
		p.dropBlockPanel(b);
	}

	public SpinCADPatch getPatch() {
		return eeprom.patch[bankIndex];
	}

	public void setPatch(SpinCADPatch p) {
		eeprom.patch[bankIndex] = p;
	}

	private void setModel(SpinCADModel m) {
		eeprom.patch[bankIndex].patchModel = m;
	}

	// edit functions cut/copy/paste/undo

	public void saveModel() {
		try { 
			modelSave = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(modelSave); 
			oos.writeObject(getPatch().patchModel); 
			oos.flush(); 
			oos.close(); 
		} 
		catch(Exception e) { 
			System.out.println("saveModel: Exception during serialization: " + e); 
		} 
		canUndo = 1;
	}

	public void saveModelToPasteBuffer() {
		try { 
			pasteBuffer = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(pasteBuffer); 
			oos.writeObject(getPatch().patchModel); 
			oos.flush(); 
			oos.close(); 
		} 
		catch(Exception e) { 
			System.out.println("saveModelToPasteBuffer: Exception during serialization: " + e); 
		} 
		canUndo = 1;
	}

	public void delete() {
		saveModel();
		SpinCADBlock block;

		Iterator<SpinCADBlock> itr = getPatch().patchModel.blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			if(block.selected == true) {
				// TODO need to think of a way to delete an open control panel
				//						if(block.editBlock != null)
				deleteBlockConnection(getPatch().patchModel, block);
				itr.remove();
			}
		}
	}

	public void undo() {
		if(canUndo == 1) {
			try { 
				ByteArrayInputStream bais = new ByteArrayInputStream(modelSave.toByteArray());
				ObjectInputStream is = new ObjectInputStream(bais);
				setModel((SpinCADModel) is.readObject());
				is.close(); 
				// System.out.println("m: " + m); 
			} 
			catch(Exception e) { 
				System.out.println("Undo: Exception during deserialization: " + 
						e); 
				System.exit(0); 
			} 
			canUndo = 0;		
		}
		contentPane.repaint();
	}

	public void paste() {
		// save the previous state of this patch so you can undo it if desired
		saveModel();
		panel.unselectAll(this);
		SpinCADModel copyBuffer = new SpinCADModel();

		try { 
			ByteArrayInputStream bais = new ByteArrayInputStream(pasteBuffer.toByteArray());
			ObjectInputStream is = new ObjectInputStream(bais);
			copyBuffer = ((SpinCADModel) is.readObject());
			is.close(); 
			// System.out.println("m: " + m); 
		} 
		catch(Exception e) { 
			System.out.println("paste: Exception during deserialization: " + 
					e); 
			System.exit(0); 
		} 
		Iterator<SpinCADBlock> itr = copyBuffer.blockList.iterator();
		SpinCADBlock b = new SpinCADBlock(0,0);
		// delete all pin connections from unselected blocks
		while(itr.hasNext()) {
			b = itr.next();
			if(b.selected == false) {
				deleteBlockConnection(copyBuffer, b);
			}
		}

		// now delete the unselected blocks themselves
		// or if they are selected, add them to the current patch's
		// block list
		itr = copyBuffer.blockList.iterator();
		while(itr.hasNext()) {
			b = itr.next();
			if(b.selected == false) {
				itr.remove();
			}
			else {
				eeprom.patch[bankIndex].patchModel.addBlock(b);	
			}
		}

		// to make sure that the mouse is on one of the new blocks
		// just pick the first one it finds that is selected
		itr = eeprom.patch[bankIndex].patchModel.blockList.iterator();
		while(itr.hasNext()) {
			b = itr.next();
			if(b.selected == true) {
				blk = b;
				break;
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 	
				// this gets the mouse sitting on the block!
				panel.nullMouse();
				panel.putMouseOnBlock(blk);	
				panel.setDragModeDragMove();
			}
		});


		repaint();
		canUndo = 1;
	}

	// deleteBlockConnection will delete all connections to a block as  
	// part of removing it from the model

	public void deleteBlockConnection(SpinCADModel m, SpinCADBlock b) {
		SpinCADBlock block;
		Iterator<SpinCADBlock> itr = m.blockList.iterator();
		// b is the block to delete
		// iterate through each block in the model
		while(itr.hasNext()) {
			block = itr.next();
			Iterator<SpinCADPin> itrPin = block.pinList.iterator();
			SpinCADPin currentPin;
			// iterate through each pin in each block
			while(itrPin.hasNext()) {
				currentPin = itrPin.next();
				// if the current pin's block connection is this block
				// then we should delete it
				if(currentPin.getBlockConnection() == b) {
					currentPin.deletePinConnection();
				}
			}
		}
	}

	// ====== COMMENT BLOCK PATCH ==================================================
	class commentBlockPatch {
		commentBlockPanel cbPnl;

		public commentBlockPatch(SpinCADPatch patch) {
			cbPnl = new commentBlockPanel(patch.cb, "Patch Information");
		}
	}

	// ======================================================================================================
	class commentBlockBank {
		commentBlockPanel cbPnl;

		public commentBlockBank(SpinCADBank bank) {
			cbPnl = new commentBlockPanel(bank.cb, "Bank Information");
		}
	}

	private class commentBlockPanel extends JFrame implements WindowListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7295329402087496031L;

		JFrame commentFrame = new JFrame();

		JTextField fileNameText;
		JTextField versionText;
		JTextField line0text;
		JTextField line1text;
		JTextField line2text;
		JTextField line3text;
		JTextField line4text;

		SpinCADCommentBlock spcb;

		public commentBlockPanel(SpinCADCommentBlock cb, String name) {

			this.spcb = cb;
			commentFrame.setTitle(name);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					commentFrame.setLayout(new BoxLayout(commentFrame.getContentPane(), BoxLayout.Y_AXIS));
					fileNameText = new JTextField(spcb.fileName, 64);
					versionText = new JTextField(spcb.version, 64);
					line0text = new JTextField(spcb.line[0], 64);
					line1text = new JTextField(spcb.line[1], 64);
					line2text = new JTextField(spcb.line[2], 64);
					line3text = new JTextField(spcb.line[3], 64);
					line4text = new JTextField(spcb.line[4], 64);

					fileNameText.setEditable(false);
					commentFrame.add(fileNameText);
					versionText.setEditable(false);
					commentFrame.add(versionText);

					commentFrame.add(line0text);
					commentFrame.add(line1text);
					commentFrame.add(line2text);
					commentFrame.add(line3text);
					commentFrame.add(line4text);	
					commentFrame.setAlwaysOnTop(true);
					commentFrame.pack();
					commentFrame.setLocation(200, 150);
					commentFrame.setResizable(false);
					commentFrame.setVisible(true);
				}
			});
			commentFrame.addWindowListener(this);
		}

		public void show() {
			commentFrame.setAlwaysOnTop(true);
			commentFrame.pack();
			commentFrame.setLocation(200, 150);
			commentFrame.setResizable(false);
			commentFrame.setVisible(true);
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// ---
			spcb.line[0] = line0text.getText();	
			spcb.line[1] = line1text.getText();	
			spcb.line[2] = line2text.getText();	
			spcb.line[3] = line3text.getText();	
			spcb.line[4] = line4text.getText();	
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {

		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {

		}

		@Override
		public void windowIconified(WindowEvent arg0) {

		}

		@Override
		public void windowOpened(WindowEvent arg0) {

		}	
	}

	// ======================================================================================================
	class bankPanel extends JPanel implements ActionListener {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4962440927175195441L;
		/**
		 * 
		 */

		final JButton[] btnPatch = new JButton[8];

		public bankPanel() {
			super();
			Dimension minButtonSize = new Dimension(100, 20);
			Dimension buttonSize = new Dimension(180, 20);

			for(int i = 0; i < 8; i++) {
				btnPatch[i] = new JButton("Patch " + i);
				btnPatch[i].setPreferredSize(buttonSize);
				btnPatch[i].setMinimumSize(minButtonSize);
				btnPatch[i].addActionListener(this);
				this.add(btnPatch[i]);
			}

		}

		public void actionPerformed(ActionEvent arg0) {

			Object source = arg0.getSource(); 

			for(int i = 0; i < 8; i++) {
				if (source == btnPatch[i]) {
					bankIndex = i;
					btnPatch[i].setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(Color.blue),
							null));
				}
				else {
					btnPatch[i].setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(Color.gray),
							null));
				}
			}

			// previously loaded patch into the slot
			if(eeprom.patch[bankIndex] != null) {
			}
			// have not yet loaded a patch into this slot
			else {
				eeprom.patch[bankIndex] = new SpinCADPatch();
				eeprom.patch[bankIndex].patchModel.newModel();
			}
			updateAll();
		}
	}


	// ================= EditResourcesToolbar
	// this shows the pin name as you hover over a pin

	public class EditResourcesToolBar extends JToolBar implements ActionListener {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7209732646570379290L;
		/**
		 * 
		 */

		final JLabel pinName = new JLabel("");

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

		public EditResourcesToolBar() {
			super();
			add(pinName);
			setVisible(true);
		}

		public void actionPerformed(ActionEvent evt) {
			System.out.println("EditResources toolbar Action Event ");
		}

		public void update() {
			ActionEvent evt = null;
			actionPerformed(evt);
		}
	}

	ModelResourcesToolBar getResourceToolbar() {
		return pb;
	}

	public void updateAll() {
		simX.updateSliders(eeprom.patch[bankIndex]);
		pb.update(eeprom.patch[bankIndex]);
		if(eeprom.patch[bankIndex].isHexFile == true) {
			simX.sctb.setVisible(false);
			etb.pinName.setText("Hex file: " + eeprom.patch[bankIndex].patchFileName);
			etb.pinName.setVisible(true);
		} else {
			simX.sctb.setVisible(true);
			etb.pinName.setText("");			
		}
		etb.update();
		updateFrameTitle();
		contentPane.repaint();	
	}

	public void updateAll(boolean isChanged) {
		eeprom.patch[bankIndex].setChanged(isChanged);
		updateAll();
	}

	// ===================================================
	// == Sample rate combo box
	public class SampleRateComboBox extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		/**
		 * 
		private static final long serialVersionUID = 1L;
		 */
		JComboBox<Object> rateList = null;

		public SampleRateComboBox() {
			super("Sample Rate");
			createAndShowGUI();
		}

		/** Listens to the combo box. */
		class SampleRateListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cb = ((JComboBox<String>) e.getSource());
				String rate = (String) cb.getSelectedItem();
				if (rate == "32768") {
					ElmProgram.setSamplerate(32768);
				} else if (rate == "44100") {
					ElmProgram.setSamplerate(44100);
				} else if (rate == "48000") {
					ElmProgram.setSamplerate(48000);
				}
			}
		}

		/**
		 * Create the GUI and show it. For thread safety, this method should be
		 * invoked from the event-dispatching thread.
		 */
		private void createAndShowGUI() {
			// Create and set up the window.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {

					// Create and set up the content pane.
					JPanel newContentPane = new JPanel(new BorderLayout());
					setContentPane(newContentPane);
					newContentPane.setOpaque(true); // content panes must be
					// opaque
					String[] rateStrings = { "32768", "44100", "48000" };

					// Create the combo box, select the item at index 4.
					// Indices start at 0, so 4 specifies the pig.
					rateList = new JComboBox<Object>(rateStrings);
					if (ElmProgram.SAMPLERATE == 44100) {
						rateList.setSelectedIndex(1);

					} else if (ElmProgram.SAMPLERATE == 48000) {
						rateList.setSelectedIndex(2);

					} else
						rateList.setSelectedIndex(0);
					rateList.addActionListener(new SampleRateListener());

					// Lay out the demo.
					newContentPane.add(rateList, BorderLayout.PAGE_START);
					newContentPane.setBorder(BorderFactory.createEmptyBorder(
							20, 20, 20, 20));

					// Display the window.
					pack();
					setVisible(true);
					setResizable(false);
				}
			});
		}
	}

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
