/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADFrame.java
 * Copyright (C) 2013 - 2026 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2026.  Look for GSW in code.
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
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
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
import com.holycityaudio.SpinCAD.CADBlocks.InputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.OutputCADBlock;

public class SpinCADFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static SpinCADFrame instance;

	public static SpinCADFrame getInstance() {
		return instance;
	}


	int buildNum = 1069;

	// Swing things
	private JPanel contentPane;
	private JTextArea asmTextArea;
	private JScrollPane asmScrollPane;
	private JSplitPane mainSplitPane;
	private JSplitPane rightSplitPane;  // ASM on top, docked VU meter on bottom
	private JPanel vuDockPanel;         // placeholder for docked VU meter
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
	private SampleRateComboBox sampleRateDialog = null;

	// BANK ========================================================
	// stuff to do with working on a bank of 8 vs. just one patch
	// may remove bank mode variable, as will probably always be in bank mode
	boolean bankMode = true;
	int bankIndex = 0;

	// eeprom is where ALL the data for 8 patches is stored
	SpinCADBank eeprom = new SpinCADBank();
	private commentBlockPanel patchInfoPanel = null;
	private java.awt.Point patchInfoLocation = null;

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
	public static void logCrash(String context, Throwable e) {
		try {
			java.io.File logFile = new java.io.File(System.getProperty("user.home"), "spincad-crash.log");
			java.io.PrintWriter pw = new java.io.PrintWriter(
				new java.io.FileWriter(logFile, true));
			pw.println("=== " + new java.util.Date() + " === " + context + " ===");
			e.printStackTrace(pw);
			pw.println();
			pw.close();
			System.err.println("Crash logged to: " + logFile.getAbsolutePath());
		} catch (Exception ex) {
			// last resort
		}
		System.err.println(context + ": " + e);
		e.printStackTrace();
	}

	public static void main(String[] args) {
		// Install global handler so uncaught exceptions are always visible
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logCrash("Uncaught exception on thread " + t.getName(), e);
			}
		});
		// Shutdown hook to detect unexpected JVM exits
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("=== JVM SHUTDOWN HOOK FIRED ===");
			logCrash("JVM SHUTDOWN", new Exception("Shutdown hook - trace of all threads"));
			// Dump all thread stack traces
			try {
				java.io.File logFile = new java.io.File(System.getProperty("user.home"), "spincad-crash.log");
				java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(logFile, true));
				pw.println("=== Thread dump at shutdown ===");
				for (java.util.Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
					pw.println("Thread: " + entry.getKey().getName() + " state=" + entry.getKey().getState());
					for (StackTraceElement ste : entry.getValue()) {
						pw.println("  at " + ste);
					}
				}
				pw.close();
			} catch (Exception ex) { ex.printStackTrace(); }
		}));
		checkJavaVersion();
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

	private static void checkJavaVersion() {
		String version = System.getProperty("java.version");
		if (version == null) {
			return;
		}
		// Java 8 and earlier use "1.x" format; Java 9+ use "9", "10", etc.
		int major;
		try {
			if (version.startsWith("1.")) {
				major = Integer.parseInt(version.substring(2, 3));
			} else {
				int dot = version.indexOf('.');
				major = Integer.parseInt(dot > 0 ? version.substring(0, dot) : version);
			}
		} catch (NumberFormatException e) {
			return; // can't parse, let it proceed
		}
		if (major < 8) {
			System.err.println("ERROR: SpinCAD Designer requires Java 8 or newer.");
			System.err.println("You are running Java " + version + ".");
			System.err.println("Please install JDK 8 or newer from: https://adoptium.net/temurin/releases");
			javax.swing.JOptionPane.showMessageDialog(null,
				"SpinCAD Designer requires Java 8 or newer.\n"
				+ "You are running Java " + version + ".\n\n"
				+ "Please install JDK 8 or newer and try again.\n"
				+ "Download from: https://adoptium.net/temurin/releases",
				"Unsupported Java Version",
				javax.swing.JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}


	/**
	 * Create the frame.
	 */

	@SuppressWarnings("unused")
	public SpinCADFrame() {
		instance = this;
		// setTitle("SpinCAD Designer - Untitled");
		updateFrameTitle();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		java.net.URL iconURL = getClass().getResource("icon_256_x_256.png");
		if (iconURL != null) {
			setIconImage(new javax.swing.ImageIcon(iconURL).getImage());
		}
		restoreWindowBounds();
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

		// ASM listing panel
		asmTextArea = new JTextArea();
		asmTextArea.setEditable(false);
		asmTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
		asmScrollPane = new JScrollPane(asmTextArea);

		// VU meter dock placeholder (hidden until a VU meter is docked)
		vuDockPanel = new JPanel(new java.awt.BorderLayout());
		vuDockPanel.setVisible(false);

		// Right column: ASM on top, docked VU meter on bottom
		rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, asmScrollPane, vuDockPanel);
		rightSplitPane.setResizeWeight(0.7);
		rightSplitPane.setDividerSize(0);  // hidden until VU meter is docked

		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, rightSplitPane);
		mainSplitPane.setResizeWeight(0.85);
		contentPane.add(mainSplitPane, BorderLayout.CENTER);

		// Show or hide ASM panel based on preference
		SpinCADFile asmPref = new SpinCADFile(this);
		asmScrollPane.setVisible(asmPref.getShowSpinAsm());
		mainSplitPane.setDividerSize(asmPref.getShowSpinAsm() ? 6 : 0);

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

		// simPanel: sctb on top, display panel below
		simPanel.setLayout(new BorderLayout());
		simPanel.add(simX.sctb, BorderLayout.NORTH);

		// Y-axis label cards: swap between scope (amplitude) and logger (dB) labels
		org.andrewkilpatrick.elmGen.simulator.LevelLogger.AmplitudeLabelPanel ampLabels =
				new org.andrewkilpatrick.elmGen.simulator.LevelLogger.AmplitudeLabelPanel();
		simX.ampLabelPanel = ampLabels;
		org.andrewkilpatrick.elmGen.simulator.LevelLogger.LoggerLabelPanel dbLabels =
				new org.andrewkilpatrick.elmGen.simulator.LevelLogger.LoggerLabelPanel();
		simX.labelCards.add(ampLabels, "scope");
		simX.labelCards.add(dbLabels, "logger");
		simX.labelCardLayout.show(simX.labelCards, "scope");

		// Display: label column on the left (labels + Lin/dB button), scope column on right
		JPanel leftColumn = new JPanel(new BorderLayout());
		leftColumn.add(simX.labelCards, BorderLayout.CENTER);
		leftColumn.add(simX.btnLinDb, BorderLayout.SOUTH);

		JPanel displayPanel = new JPanel(new BorderLayout());
		displayPanel.add(leftColumn, BorderLayout.WEST);

		JPanel scopeColumn = new JPanel();
		scopeColumn.setLayout(new BoxLayout(scopeColumn, BoxLayout.Y_AXIS));
		scopeColumn.add(simX.scopePanel);
		simX.stb.setFloatable(false);
		scopeColumn.add(simX.stb);
		displayPanel.add(scopeColumn, BorderLayout.CENTER);

		displayPanel.setVisible(false);   // hidden until simulator runs
		simPanel.add(displayPanel, BorderLayout.CENTER);
		simX.displayColumn = displayPanel;

		topPanel.add(simPanel, BorderLayout.NORTH);

		contentPane.add(topPanel, BorderLayout.NORTH);

		// controlPanels.setFloatable(false);
		contentPane.add(controlPanels, BorderLayout.EAST);
		controlPanels.setLayout(new BoxLayout(controlPanels, BoxLayout.Y_AXIS));
		
		// initialize file paths in preferences
		SpinCADFile fsp = new SpinCADFile(SpinCADFrame.this);
		fsp.init_prefs();
			
		// ; ==================== menu bar and items ==========

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFileMenu = new JMenu("File");
		mnFileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
		menuBar.add(mnFileMenu);

		// Patch File operations

		JMenuItem mntmNewPatch = new JMenuItem("New Patch");
		mntmNewPatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(SpinCADFrame.this,
							"You have unsaved changes!  Continue?", "Warning!",
							dialogButton);
					if (dialogResult == JOptionPane.NO_OPTION) {
						return;
					}
				}
				eeprom.patch[bankIndex] = new SpinCADPatch();
				addDefaultBlocksIfEnabled();
				updateAll();;
			}
		});
		mntmNewPatch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmNewPatch);

		JMenuItem mntmOpenPatch = new JMenuItem("Open Patch");
		mntmOpenPatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(SpinCADFrame.this, "Warning!",
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
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(SpinCADFrame.this, "Warning!",
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
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
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
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
				f.fileSavePatchAs(eeprom.patch[bankIndex]);
				updateAll(false);
			}
		});
		mntmSavePatchAs.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmSavePatchAs);

		JMenuItem mntmInfo = new JMenuItem("Patch Information");
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (patchInfoPanel != null && patchInfoPanel.commentFrame.isDisplayable()) {
					patchInfoPanel.update(eeprom.patch[bankIndex].cb);
					patchInfoPanel.commentFrame.toFront();
				} else {
					patchInfoPanel = new commentBlockPanel(eeprom.patch[bankIndex].cb, "Patch Information");
					patchInfoPanel.show();
				}
			}
		});
		mntmInfo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		mnFileMenu.add(mntmInfo);

		mnFileMenu.addSeparator();

		JMenuItem mntmSaveAsm = new JMenuItem("Save Patch as ASM");
		mntmSaveAsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.patch[bankIndex].patchModel.sortAlignGen(true);
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
				f.fileSaveAsm(eeprom.patch[bankIndex]);
			}
		});
		mnFileMenu.add(mntmSaveAsm);

		JMenuItem mntmCopyToClipboard = new JMenuItem("Copy ASM to Clipboard");
		mntmCopyToClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.patch[bankIndex].patchModel.sortAlignGen(true);
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
					int dialogResult = JOptionPane.showConfirmDialog(SpinCADFrame.this,
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
				SpinCADFile f= new SpinCADFile(SpinCADFrame.this);
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(SpinCADFrame.this, "Warning!",
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
					SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
					f.fileSaveBank(eeprom);
				} else {
					SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
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
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
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
				for (int i = 0; i < 8; i++) {
					if (!eeprom.patch[i].isHexFile) {
						eeprom.patch[i].patchModel.sortAlignGen(true);
					}
				}
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
				f.fileSaveHex(eeprom);
			}
		});

		mnFileMenu.add(mntmSaveHex);

		JMenuItem mntmSavePrj = new JMenuItem("Export Bank to Spin Project");
		mntmSavePrj.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < 8; i++) {
					if (!eeprom.patch[i].isHexFile) {
						eeprom.patch[i].patchModel.sortAlignGen(true);
					}
				}
				SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
				f.fileSaveSpj(eeprom);
			}
		});

		mnFileMenu.add(mntmSavePrj);

		mnFileMenu.addSeparator();

		JMenuItem mntmPreferences = new JMenuItem("Preferences...");
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				PreferencesDialog dlg = new PreferencesDialog(SpinCADFrame.this);
				dlg.setVisible(true);
				updateAll();
			}
		});
		mnFileMenu.add(mntmPreferences);

		mnFileMenu.addSeparator();

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (eeprom.patch[bankIndex].getChanged() == true) {
					int dialogResult = SpinCADDialogs.yesNoBox(SpinCADFrame.this, "Warning!", 
							"You have unsaved changes!  Save first?");				
					if (dialogResult == JOptionPane.YES_OPTION) {
						File fileToBeSaved = new File(eeprom.patch[bankIndex].patchFileName);
						if (fileToBeSaved.exists()) {
							String filePath = fileToBeSaved.getPath();
							SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
							f.fileSavePatch(eeprom.patch[bankIndex]);
						} else {
							SpinCADFile f = new SpinCADFile(SpinCADFrame.this);
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

		JMenu mn_special = new JMenu("Special");
		menuBar.add(mn_special);

		final JMenuItem mntm_AddFB = new JMenuItem("Feedback Loop");
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
		mn_special.add(mntm_AddFB);

		final JMenuItem mntm_AddScopeProbe = new JMenuItem("Scope Probe");
		mntm_AddScopeProbe.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				com.holycityaudio.SpinCAD.CADBlocks.ScopeProbeCADBlock probe =
						new com.holycityaudio.SpinCAD.CADBlocks.ScopeProbeCADBlock(50, 200);
				dropBlock(panel, probe);
			}
		});
		mn_special.add(mntm_AddScopeProbe);

		final JMenuItem mntm_AddVUMeter = new JMenuItem("VU Meter");
		mntm_AddVUMeter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				com.holycityaudio.SpinCAD.CADBlocks.VUMeterCADBlock vu =
						new com.holycityaudio.SpinCAD.CADBlocks.VUMeterCADBlock(50, 200);
				dropBlock(panel, vu);
			}
		});
		mn_special.add(mntm_AddVUMeter);

		// most of the menu is generated right here.
		// standardmenu is generated by the spincadmenu DSL
		new standardMenu(this, panel, menuBar);

		// Restore display preference at startup
		java.util.prefs.Preferences simPrefs = java.util.prefs.Preferences.userNodeForPackage(SpinCADSimulator.class);
		simX.displayIsVisible = simPrefs.getBoolean("ENABLE_DISPLAY", false);

		final JMenu mnSimulator = new JMenu("Simulator");
		menuBar.add(mnSimulator);

		JMenuItem mntmSimOptions = new JMenuItem("Simulator Options...");
		mntmSimOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SimulatorOptionsDialog dlg = new SimulatorOptionsDialog(SpinCADFrame.this, simX);
				dlg.setVisible(true);
			}
		});
		mnSimulator.add(mntmSimOptions);

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
				SpinCADDialogs.MessageBox(SpinCADFrame.this, "About SpinCAD Designer", "Version 0.99 Build " + buildNum + "\n"
						+ "Copyright 2013 - 2026 Gary Worsham, Holy City Audio\n" +
						" This program is distributed in the hope that it will be useful," +
						"\nbut WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
						"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.");
			}
		});
		mnHelp.add(mntmAbout);

		// Auto-reload last file if preference is enabled
		autoReloadLastFile();
	}

	private void updateAsmPanel() {
		if (!asmScrollPane.isVisible()) {
			return;
		}
		try {
			SpinCADPatch patch = eeprom.patch[bankIndex];
			if (patch.patchModel.blockList.size() > 0) {
				patch.patchModel.sortAlignGen(true);
				SpinFXBlock rb = patch.patchModel.getRenderBlock();
				if (rb != null) {
					asmTextArea.setText(rb.getProgramListing(1));
					asmTextArea.setCaretPosition(0);
				}
				// Restore un-optimized state so simulator probe registers stay valid
				if (patch.patchModel.hasScopeProbe()) {
					patch.patchModel.sortAlignGen();
				}
			} else {
				asmTextArea.setText("");
			}
		} catch (Exception e) {
			asmTextArea.setText("; (unable to generate listing)");
		}
	}

	public void setAsmPanelVisible(boolean visible) {
		rightSplitPane.setVisible(visible || vuDockPanel.isVisible());
		asmScrollPane.setVisible(visible);
		mainSplitPane.setDividerSize((visible || vuDockPanel.isVisible()) ? 6 : 0);
		if (visible) {
			mainSplitPane.setDividerLocation(0.85);
			updateAsmPanel();
		}
		mainSplitPane.revalidate();
		mainSplitPane.repaint();
	}

	/** DockHost implementation: dock VU meter panel below ASM listing. */
	public void dockVUMeter(JPanel panel) {
		vuDockPanel.removeAll();
		vuDockPanel.add(panel, java.awt.BorderLayout.CENTER);
		vuDockPanel.setVisible(true);
		rightSplitPane.setDividerSize(6);
		rightSplitPane.setVisible(true);
		mainSplitPane.setDividerSize(6);
		// VU meter gets 30% of the window height
		rightSplitPane.setDividerLocation(0.7);
		rightSplitPane.revalidate();
		mainSplitPane.revalidate();
		mainSplitPane.repaint();
	}

	/** DockHost implementation: remove VU meter from dock. */
	public void undockVUMeter() {
		vuDockPanel.removeAll();
		vuDockPanel.setVisible(false);
		rightSplitPane.setDividerSize(0);
		if (!asmScrollPane.isVisible()) {
			rightSplitPane.setVisible(false);
			mainSplitPane.setDividerSize(0);
		}
		rightSplitPane.revalidate();
		mainSplitPane.revalidate();
		mainSplitPane.repaint();
	}

	private void addDefaultBlocksIfEnabled() {
		SpinCADFile f = new SpinCADFile(this);
		if (f.getAddDefaultBlocks()) {
			java.awt.Rectangle vis = panel.getVisibleRect();
			int cx = vis.x + vis.width / 2;
			int topY = vis.y + 30;
			int bottomY = vis.y + vis.height - 80;
			eeprom.patch[bankIndex].patchModel.addBlock(new InputCADBlock(cx, topY));
			eeprom.patch[bankIndex].patchModel.addBlock(new OutputCADBlock(cx, bottomY));
		}
	}

	private void autoReloadLastFile() {
		SpinCADFile f = new SpinCADFile(this);
		if (!f.getAutoReloadLastFile()) {
			return;
		}
		String lastType = f.getLastFileType();
		String lastPath = f.getLastFilePath();
		if (lastPath.isEmpty()) {
			return;
		}
		File lastFile = new File(lastPath);
		if (!lastFile.exists()) {
			return;
		}
		try {
			if ("bank".equals(lastType)) {
				SpinCADBank b = f.fileReadBank(lastFile);
				if (b != null) {
					eeprom = b;
					eeprom.changed = false;
					bankIndex = 0;
					simX.updateSliders(eeprom.patch[bankIndex]);
					updateAll();
				}
			} else if ("patch".equals(lastType)) {
				SpinCADPatch p = f.fileReadPatch(lastPath);
				if (p != null) {
					p.patchFileName = lastFile.getName();
					eeprom.patch[bankIndex] = p;
					eeprom.patch[bankIndex].setChanged(false);
					simX.updateSliders(eeprom.patch[bankIndex]);
					updateAll();
				}
			}
		} catch (Exception e) {
			System.out.println("Auto-reload failed: " + e.getMessage());
		}
	}

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
				int confirm = JOptionPane.showOptionDialog(SpinCADFrame.this,
						"Do you wish to exit SpinCAD?", "Exit Confirmation",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				if (confirm == JOptionPane.YES_OPTION) {
					saveWindowBounds();
					System.exit(0);
				}
			}
		};
		return exitListener;
	}

	private void saveWindowBounds() {
		java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(SpinCADFrame.class);
		java.awt.Rectangle b = getBounds();
		prefs.putInt("window_x", b.x);
		prefs.putInt("window_y", b.y);
		prefs.putInt("window_w", b.width);
		prefs.putInt("window_h", b.height);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		prefs.putInt("screen_w", screen.width);
		prefs.putInt("screen_h", screen.height);
	}

	private void restoreWindowBounds() {
		java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(SpinCADFrame.class);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int savedScreenW = prefs.getInt("screen_w", -1);
		int savedScreenH = prefs.getInt("screen_h", -1);
		if (savedScreenW != screen.width || savedScreenH != screen.height || savedScreenW == -1) {
			// Screen dimensions changed or first run — go full screen
			setBounds(0, 0, screen.width, screen.height);
		} else {
			int x = prefs.getInt("window_x", 100);
			int y = prefs.getInt("window_y", 100);
			int w = prefs.getInt("window_w", 800);
			int h = prefs.getInt("window_h", 600);
			setBounds(x, y, w, h);
		}
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
		catch(Throwable e) {
			logCrash("saveModel", e);
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
		catch(Throwable e) {
			logCrash("saveModelToPasteBuffer", e);
		}
		canUndo = 1;
	}

	public void delete() {
		try {
			saveModel();
			SpinCADBlock block;

			// If deleting a feedback block, also select its partner
			for (SpinCADBlock b : getPatch().patchModel.blockList) {
				if (b.selected) {
					if (b instanceof FBInputCADBlock || b instanceof FBOutputCADBlock) {
						for (SpinCADBlock partner : getPatch().patchModel.blockList) {
							if (partner != b && partner.getIndex() == b.getIndex()) {
								if ((b instanceof FBInputCADBlock && partner instanceof FBOutputCADBlock) ||
									(b instanceof FBOutputCADBlock && partner instanceof FBInputCADBlock)) {
									partner.selected = true;
								}
							}
						}
					}
				}
			}

			Iterator<SpinCADBlock> itr = getPatch().patchModel.blockList.iterator();
			while(itr.hasNext()) {
				block = itr.next();
				if(block.selected == true) {
					block.deleteControlPanel();
					deleteBlockConnection(getPatch().patchModel, block);
					itr.remove();
				}
			}
			updateAll();
		} catch (Throwable e) {
			logCrash("delete", e);
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
			catch(Throwable e) {
				logCrash("undo", e);
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
		catch(Throwable e) {
			logCrash("paste", e);
			return;
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
					if (patchInfoLocation != null) {
						commentFrame.setLocation(patchInfoLocation);
					} else {
						commentFrame.setLocation(200, 150);
					}
					commentFrame.setResizable(false);
					commentFrame.setVisible(true);
				}
			});
			commentFrame.addWindowListener(this);
		}

		public void show() {
			commentFrame.setAlwaysOnTop(true);
			commentFrame.pack();
			if (patchInfoLocation != null) {
				commentFrame.setLocation(patchInfoLocation);
			} else {
				commentFrame.setLocation(200, 150);
			}
			commentFrame.setResizable(false);
			commentFrame.setVisible(true);
		}

		public void update(SpinCADCommentBlock cb) {
			// save current edits to old comment block before switching
			if (spcb != null) {
				spcb.line[0] = line0text.getText();
				spcb.line[1] = line1text.getText();
				spcb.line[2] = line2text.getText();
				spcb.line[3] = line3text.getText();
				spcb.line[4] = line4text.getText();
			}
			this.spcb = cb;
			fileNameText.setText(cb.fileName);
			versionText.setText(cb.version);
			line0text.setText(cb.line[0]);
			line1text.setText(cb.line[1]);
			line2text.setText(cb.line[2]);
			line3text.setText(cb.line[3]);
			line4text.setText(cb.line[4]);
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
			if (patchInfoPanel == this) {
				patchInfoLocation = commentFrame.getLocation();
				patchInfoPanel = null;
			}
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
			if (patchInfoPanel != null && patchInfoPanel.commentFrame.isDisplayable()) {
				patchInfoPanel.update(eeprom.patch[bankIndex].cb);
			}
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
		final JLabel statusMessage = new JLabel("");

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
			setLayout(new java.awt.BorderLayout());
			JPanel pinPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
			pinPanel.setPreferredSize(new java.awt.Dimension(200, 20));
			pinPanel.add(pinName);
			add(pinPanel, java.awt.BorderLayout.WEST);

			JPanel statusPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
			statusMessage.setForeground(java.awt.Color.RED);
			statusPanel.add(statusMessage);
			add(statusPanel, java.awt.BorderLayout.CENTER);

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
		updateAsmPanel();
		contentPane.repaint();
	}

	public void updateAll(boolean isChanged) {
		eeprom.patch[bankIndex].setChanged(isChanged);
		updateAll();
	}

	// ===================================================
	// == Sample rate combo box
	public class SampleRateComboBox extends JDialog {
		private static final long serialVersionUID = 1L;
		JComboBox<Object> rateList = null;

		public SampleRateComboBox(JFrame owner) {
			super(owner, "Sample Rate", false);
			String[] rateStrings = { "32768", "44100", "48000" };
			rateList = new JComboBox<Object>(rateStrings);

			if (ElmProgram.SAMPLERATE == 44100) {
				rateList.setSelectedIndex(1);
			} else if (ElmProgram.SAMPLERATE == 48000) {
				rateList.setSelectedIndex(2);
			} else {
				rateList.setSelectedIndex(0);
			}

			rateList.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JComboBox<String> cb = ((JComboBox<String>) e.getSource());
					String rate = (String) cb.getSelectedItem();
					if ("32768".equals(rate)) {
						ElmProgram.setSamplerate(32768);
					} else if ("44100".equals(rate)) {
						ElmProgram.setSamplerate(44100);
					} else if ("48000".equals(rate)) {
						ElmProgram.setSamplerate(48000);
					}
				}
			});

			JPanel content = new JPanel(new BorderLayout());
			content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			content.add(rateList, BorderLayout.PAGE_START);
			setContentPane(content);
			pack();
			setResizable(false);
			setLocationRelativeTo(owner);
			setVisible(true);
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
