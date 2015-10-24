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
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import org.andrewkilpatrick.elmGen.Debug;
import org.andrewkilpatrick.elmGen.ElmProgram;
import org.andrewkilpatrick.elmGen.simulator.AudioFileReader;
import org.andrewkilpatrick.elmGen.simulator.SpinSimulator;

import com.holycityaudio.SpinCAD.SpinCADSimulator.simControlToolBar;
import com.holycityaudio.SpinCAD.CADBlocks.FBInputCADBlock;
import com.holycityaudio.SpinCAD.CADBlocks.FBOutputCADBlock;

import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.Toolkit;

public class SpinCADFrame extends JFrame {
	/**
	 * 
	 */

	int buildNum = 959;
	// Swing things
	private JPanel contentPane;
	//=========================================================================================
	// pb shows instructions, registers, and RAM used.  It also shows allocation state of LFOs
	private final ModelResourcesToolBar pb = new ModelResourcesToolBar();
	// etb is used to show the pin name when you hover
	public final EditResourcesToolBar etb = new EditResourcesToolBar();

	private final JPanel controlPanels = new JPanel();
	// 
	// topPanel holds bankPanel and simPanel
	private final JPanel topPanel = new JPanel();
	private final JPanel bankPanel = new bankPanel();

	//=============================================================
	// Simulator display and control items
	SpinCADSimulator simX = new SpinCADSimulator(this);
	private final simControlToolBar sctb = simX.sctb;
	private final JPanel simPanel = new JPanel();

	// BANK ========================================================
	// stuff to do with working on a bank of 8 vs. just one patch
	// may remove bank mode variable, as will probably always be in bank mode
	boolean bankMode = true;
	static int bankIndex = 0;

	private static SpinCADBank eeprom = new SpinCADBank();

	// modelSave is used to undo deletes
	ByteArrayOutputStream modelSave;
	private int canUndo = 0;

	// ------------------------------------------------------------
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
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

		final SpinCADPanel panel = new SpinCADPanel(this);
		panel.setBackground(SystemColor.inactiveCaption);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
				} finally {
				}
			}
		});

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
		//		simControlToolBar);	// start up with bank panel hidden
		bankPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.green),
				bankPanel.getBorder()));

		topPanel.add(bankPanel, BorderLayout.NORTH);
		//----------------------------------------

		sctb.setFloatable(false);
		sctb.setBorder(border);
		simPanel.setLayout(new BoxLayout(simPanel, BoxLayout.Y_AXIS));
		simPanel.add(sctb);
		simPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.blue),
				simPanel.getBorder()));
		topPanel.add(simPanel, BorderLayout.NORTH);

		contentPane.add(topPanel, BorderLayout.NORTH);

		contentPane.add(simX.levelMonitor, BorderLayout.WEST);

		simPanel.add(simX.loggerPanel);
		simX.loggerPanel.setVisible(false);

		// controlPanels.setFloatable(false);
		contentPane.add(controlPanels, BorderLayout.EAST);
		controlPanels.setLayout(new BoxLayout(controlPanels, BoxLayout.Y_AXIS));
		// Then on your component(s)
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

				if (getModel().getChanged() == true) {
					int dialogButton = JOptionPane.YES_NO_OPTION;
					int dialogResult = JOptionPane.showConfirmDialog(panel,
							"You have unsaved changes!  Continue?", "Warning!",
							dialogButton);
					if (dialogResult == JOptionPane.NO_OPTION) {
						return;
					}
				}
				eeprom.bank[bankIndex] = new SpinCADPatch();
				//				bankPanel.setVisible(false);
				updateFrameTitle();
				repaint();
			}
		});
		mnFileMenu.add(mntmNewPatch);

		JMenuItem mntmOpenPatch = new JMenuItem("Open Patch");
		mntmOpenPatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f = new SpinCADFile();
				if (eeprom.bank[bankIndex].patchModel.getChanged() == true) {
					int dialogResult = yesNoBox(panel, "Warning!",
							"You have unsaved changes!  Continue?");
					if (dialogResult == 0) {
						getModel().newModel();
						repaint();
					}
					else {
						eeprom.bank[bankIndex] = f.fileOpenPatch();
						eeprom.bank[bankIndex].patchModel.getIndexFB();
						eeprom.bank[bankIndex].patchModel.setChanged(false);						
						eeprom.bank[bankIndex].patchModel.presetIndexFB();
						eeprom.changed = true;
						updateAll();
					}
				}
				eeprom.bank[bankIndex] = f.fileOpenPatch();
				eeprom.bank[bankIndex].patchModel.getIndexFB();
				eeprom.bank[bankIndex].patchModel.setChanged(false);						
				eeprom.bank[bankIndex].patchModel.presetIndexFB();
				eeprom.changed = true;
				updateAll();
			}
		});

		mnFileMenu.add(mntmOpenPatch);

		JMenuItem mntmSavePatch = new JMenuItem("Save Patch");
		mntmSavePatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(eeprom.bank[bankIndex].patchFileName != "Untitled") {
					try {
						SpinCADFile f = new SpinCADFile();
						f.fileSavePatchAs(eeprom.bank[bankIndex]);
						eeprom.bank[bankIndex].patchModel.setChanged(false);
						updateAll();
					} finally {
					}

				} else {
					SpinCADFile f = new SpinCADFile();
					f.fileSavePatchAs(eeprom.bank[bankIndex]);
					eeprom.bank[bankIndex].patchModel.setChanged(false);
					updateAll();
				}
			}
		});
		mntmSavePatch.setAccelerator(KeyStroke.getKeyStroke("ctrl s"));
		mnFileMenu.add(mntmSavePatch);

		JMenuItem mntmSavePatchAs = new JMenuItem("Save Patch As");
		mntmSavePatchAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.bank[bankIndex].cb.setVersion("Patch saved from SpinCAD Designer version" + buildNum);
				SpinCADFile f = new SpinCADFile();
				f.fileSavePatchAs(eeprom.bank[bankIndex]);
				eeprom.bank[bankIndex].patchModel.changed = false;
			}
		});
		mnFileMenu.add(mntmSavePatchAs);

		JMenuItem mntmSaveAsm = new JMenuItem("Save Patch as ASM");
		mntmSaveAsm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getModel().sortAlignGen();
				SpinCADFile f = new SpinCADFile();
				f.fileSaveAsm(eeprom.bank[bankIndex]);
			}
		});
		mnFileMenu.add(mntmSaveAsm);
		
		// XXX DEBUG
		JMenuItem mntmCopyToClipboard = new JMenuItem("Copy ASM to Clipboard");
		mntmCopyToClipboard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.bank[bankIndex].patchModel.sortAlignGen();
				StringSelection stringSelection = new StringSelection (eeprom.bank[bankIndex].cb.getComments() + SpinCADModel.getRenderBlock().getProgramListing(1));
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
			}
		});
		mnFileMenu.add(mntmCopyToClipboard);

		JMenuItem mntmInfo = new JMenuItem("Patch Information");
		mntmInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				commentBlockPatch cbp = new commentBlockPatch(eeprom.bank[bankIndex]);
				cbp.cbPnl.show();
			}
		});
		mnFileMenu.add(mntmInfo);
		
		mnFileMenu.addSeparator();

		// Bank File operations
		JMenuItem mntmNewBank = new JMenuItem("New Bank");
		mntmNewBank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (getModel().getChanged() == true) {
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
				updateFrameTitle();
				getModel().newModel();
				eeprom = new SpinCADBank();
				repaint();
			}
		});

		mnFileMenu.add(mntmNewBank);

		JMenuItem mntmOpenBank = new JMenuItem("Open Bank");
		mntmOpenBank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SpinCADFile f= new SpinCADFile();
				if (getModel().getChanged() == true) {
					int dialogResult = yesNoBox(panel, "Warning!",
							"You have unsaved changes!  Continue?");
					if (dialogResult == 0) {
						getModel().newModel();
						repaint();
					}
					eeprom = f.fileOpenBank();
					updateAll();
				}
				eeprom = f.fileOpenBank();
				updateAll();
			}
		});

		mnFileMenu.add(mntmOpenBank);

		JMenuItem mntmSaveBank = new JMenuItem("Save Bank");
		mntmSaveBank.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File fileToBeSaved = new File(eeprom.bankFileName);
				SpinCADFile f = new SpinCADFile();
				f.fileSaveBankAs(eeprom);
				eeprom.bankFileName = fileToBeSaved.getName();
				updateAll();
			}
		});

		mnFileMenu.add(mntmSaveBank);

		JMenuItem mntmSaveBankAs = new JMenuItem("Save Bank As");
		mntmSaveBankAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				eeprom.cb.setVersion("Bank saved from SpinCAD Designer version" + buildNum);
				SpinCADFile f = new SpinCADFile();
				f.fileSaveBankAs(eeprom);
				eeprom.changed = false;
			}
		});

		mnFileMenu.add(mntmSaveBankAs);

		JMenuItem mntmSaveHex = new JMenuItem("Export Bank to Hex");
		mntmSaveHex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				getModel().sortAlignGen();
				SpinCADFile f = new SpinCADFile();
				f.fileSaveHex(eeprom);
			}
		});

		mnFileMenu.add(mntmSaveHex);

		JMenuItem mntmBankInfo = new JMenuItem("Bank Information");
		mntmBankInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				commentBlockBank cbb = new commentBlockBank(eeprom);
				cbb.cbPnl.show();
			}
		});
		mnFileMenu.add(mntmBankInfo);
		
		mnFileMenu.addSeparator();



		JMenuItem mntmBatch = new JMenuItem("Batch Convert");
		// XXX 	    SpinCADFile.fileBatch(panel, mntmBatch);
		mnFileMenu.add(mntmBatch);


		mnFileMenu.addSeparator();



		mnFileMenu.addSeparator();

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (eeprom.bank[bankIndex].patchModel.getChanged() == true) {
					int dialogResult = yesNoBox(panel, "Warning!", 
							"You have unsaved changes!  Save first?");				
					if (dialogResult == JOptionPane.YES_OPTION) {
						File fileToBeSaved = new File(eeprom.bank[bankIndex].patchFileName);
						if (fileToBeSaved.exists()) {
							String filePath = fileToBeSaved.getPath();
							SpinCADFile f = new SpinCADFile();
							f.fileSave(eeprom.bank[bankIndex]);
						} else {
							SpinCADFile f = new SpinCADFile();
							f.fileSavePatchAs(eeprom.bank[bankIndex]);
						}
					}
					System.exit(0);
				}
			}
		});
		mnFileMenu.add(mntmExit);

		JMenu mn_edit = new JMenu("Edit");
		menuBar.add(mn_edit);

		final JMenuItem mntm_Undo = new JMenuItem("Undo");
		mntm_Undo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				undo();
			}
		});
		mn_edit.add(mntm_Undo);

		JMenu mn_io_mix = new JMenu("Loop");
		menuBar.add(mn_io_mix);

		final JMenuItem mntm_AddFB = new JMenuItem("Add");
		mntm_AddFB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int i =  getModel().getIndexFB();
				FBInputCADBlock pcB = new FBInputCADBlock(50, 100, i);
				dropBlock(panel, pcB);

				FBOutputCADBlock pcB1 = new FBOutputCADBlock(50, 300, i);
				dropBlock(panel, pcB1);
				getModel().setIndexFB(i + 1);
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
					MessageBox("Simulator File Error", "Make sure that your simulator source\n"
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
						MessageBox("Simulator Debug File Error", "Uhmmmm....");
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
					openWebpage(new URL("http://holycityaudio.com/spincad-designer-2/spincad-designer-help-pages/"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		});
		mnHelp.add(mntmHelp);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				MessageBox("About SpinCAD Designer", "Version 0.97 Build " + buildNum + "\n"
						+ "Copyright 2015 Gary Worsham, Holy City Audio\n" + 
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
				String patchName = bankIndex + " [" + eeprom.bank[bankIndex].patchFileName + (eeprom.bank[bankIndex].patchModel.changed ? " * ]" : "]");
				setTitle("SpinCAD Designer - Bank [" + bankName + " Patch " + patchName);			
			}
		});
	}

	void fileBatch(final SpinCADPanel panel, JMenuItem mntmFile) {
		mntmFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// Create a file chooser
				/*				String savedPath = prefs.get("MRUFolder", "");

				final JFileChooser fc = new JFileChooser(savedPath);
				fc.setDialogTitle("Choose files to convert...");
				fc.setMultiSelectionEnabled(true);

				final String newline = "\n";
				// In response to a button click:
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"SpinCAD Patch Files", "spcd");
				fc.setFileFilter(filter);

				int returnVal = fc.showOpenDialog(SpinCADFrame.this);

				// returnVal is from the first file open dialog
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					// now ask user to enter converted file destination
					/* XXX debug
					savedPath = prefs.get("MRUSpnFolder", "");

					final JFileChooser fc2 = new JFileChooser(savedPath); 
					fc2.setDialogTitle("Choose destination...");
					fc2.setMultiSelectionEnabled(false);
					fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					fc2.setAcceptAllFileFilterUsed(false);
					// In response to a button click:
					filter = new FileNameExtensionFilter("Spin ASM Files", "spn");
					fc2.setFileFilter(filter);

					int retVal2 = fc2.showSaveDialog(SpinCADFrame.this);

					if(retVal2 == JFileChooser.APPROVE_OPTION) {
						File myFile = fc2.getSelectedFile();
						if(myFile.isDirectory() == true) {
							saveMRUSpnFolder(myFile.getAbsolutePath());
						}

						int index = 0;
						int failed = 0;

						File files[] = fc.getSelectedFiles();
						// This is where a real application would open the file.
						saveMRUPatchFolder(files[0].getPath());
						while(index < files.length) {
							System.out.println("Opening: " + files[index].getName() + "."
									+ newline);
							try {
								String filePath = files[index].getPath();
								eeprom.bank[bankIndex] = SpinCADFile.fileReadPatch(filePath );
								//cb.line2text.setText()
								getModel().getIndexFB();
								getModel().setChanged(false);						
								getModel().sortAlignGen();

								spcFileName = files[index].getName();
								String spnPath  = prefs.get("MRUSpnFolder", "") + "/" + spcFileName + ".spn";
								// XXX debug
								//								SpinCADFile.fileSaveAsm(cb, SpinCADModel.getRenderBlock().getProgramListing(1), spnPath.replace(".spcd.spn",  ".spn"));
								updateFrameTitle();
							} catch (Exception e) {	// thrown over in SpinCADFile.java
								spcFileName = "Untitled";
								//						e.printStackTrace();
								failed++;
								MessageBox("File convert failed! " + spcFileName, spcFileName + " may be from\nan incompatible version of \nSpinCAD Designer.");
							}
							index++;
						}
						MessageBox("Conversion completed", (index - failed) + " files were converted.\n" + failed + " files failed.");
					} else {
						System.out.println("Open command cancelled by user."
								+ newline);
					}
				}
				 */
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
		getModel().addBlock(b);
		getModel().setChanged(true);
		p.unselectAll(this);
		p.dropBlockPanel(b);
	}

	public SpinCADModel getModel() {
		return eeprom.bank[bankIndex].patchModel;
	}

	private void setModel(SpinCADModel m) {
		eeprom.bank[bankIndex].patchModel = m;
	}

	public void saveModel() {
		try { 
			modelSave = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(modelSave); 
			oos.writeObject(getModel()); 
			oos.flush(); 
			oos.close(); 
		} 
		catch(Exception e) { 
			System.out.println("Exception during serialization: " + e); 
		} 
		canUndo = 1;
	}

	public void undo() {
		if(canUndo == 1) {
			try { 
				ByteArrayInputStream bais = new ByteArrayInputStream(modelSave.toByteArray());
				ObjectInputStream is = new ObjectInputStream(bais);
				setModel((SpinCADModel) is.readObject());
				is.close(); 
				contentPane.repaint();
				// System.out.println("m: " + m); 
			} 
			catch(Exception e) { 
				System.out.println("Exception during deserialization: " + 
						e); 
				System.exit(0); 
			} 
			canUndo = 0;		
		}
	}

	// Swing dialog boxes.

	public void MessageBox(String title, String message) {
		JFrame frame = new JFrame();
		JOptionPane.showMessageDialog(frame,
				message, title,
				JOptionPane.DEFAULT_OPTION);
	}

	static int yesNoBox(JPanel panel, String title, String question) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int dialogResult = JOptionPane.showConfirmDialog(panel,
				question,
				title, dialogButton);
		return dialogResult;
	}

	// ====== COMMENT BLOCK PATCH ==================================================
	class commentBlockPatch {
		commentBlockPanel cbPnl;

		public commentBlockPatch(SpinCADPatch patch) {
			cbPnl = new commentBlockPanel(patch.cb);
		}
	}

	// ======================================================================================================
	class commentBlockBank {
		commentBlockPanel cbPnl;

		public commentBlockBank(SpinCADBank bank) {
			cbPnl = new commentBlockPanel(bank.cb);
		}
	}

	private class commentBlockPanel extends JFrame implements WindowListener {
		JFrame commentFrame = new JFrame("Patch Information");
		
		JTextField fileNameText;
		JTextField versionText;
		JTextField line0text;
		JTextField line1text;
		JTextField line2text;
		JTextField line3text;
		JTextField line4text;

		SpinCADCommentBlock spcb;

		public commentBlockPanel(SpinCADCommentBlock cb) {

			this.spcb = cb;
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
			// TODO Auto-generated method stub
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO Auto-generated method stub
			spcb.line[0] = line0text.getText();	
			spcb.line[1] = line1text.getText();	
			spcb.line[2] = line2text.getText();	
			spcb.line[3] = line3text.getText();	
			spcb.line[4] = line4text.getText();	
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub

		}	
	}

	// ======================================================================================================
	class bankPanel extends JPanel implements ActionListener {

		/**
		 * 
		 */
		final JButton btnPatch0 = new JButton("Patch 0");
		final JButton btnPatch1 = new JButton("Patch 1");
		final JButton btnPatch2 = new JButton("Patch 2");
		final JButton btnPatch3 = new JButton("Patch 3");
		final JButton btnPatch4 = new JButton("Patch 4");
		final JButton btnPatch5 = new JButton("Patch 5");
		final JButton btnPatch6 = new JButton("Patch 6");
		final JButton btnPatch7 = new JButton("Patch 7");

		public bankPanel() {
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

			// previously loaded patch into the slot
			if(eeprom.bank[bankIndex] != null) {
			}
			// have not yet loaded a patch into this slot
			else {
				eeprom.bank[bankIndex] = new SpinCADPatch();
				eeprom.bank[bankIndex].patchModel.newModel();
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
		updateFrameTitle();
		pb.update(eeprom.bank[bankIndex].patchModel);
		contentPane.repaint();	
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
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
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
