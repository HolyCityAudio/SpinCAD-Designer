/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADFile.java
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

package com.holycityaudio.SpinCAD;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class SpinCADFile {
	// PREFERENCES =====================================================
	// following things are saved in the SpinCAD preferences
	private Preferences prefs;
	private RecentFileList recentBankFileList = null;
	private RecentFileList recentPatchFileList = null;
	// this next one is specific to file open, needs to be here for MRU file list operations
	private JFileChooser fc;

	public SpinCADFile() {
		// create a Preferences instance (somewhere later in the code)
		prefs = Preferences.userNodeForPackage(this.getClass());
	}

	public void fileSave(SpinCADPatch m) {
		File fileToBeSaved = new File(prefs.get("MRUPatchFolder",  "") + "/" + m.patchFileName);
		String filePath = fileToBeSaved.getPath();
		loadRecentPatchFileList();
		
		FileOutputStream fos;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filePath);
			oos = new ObjectOutputStream(fos); 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		try {
			oos.writeObject(m);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 	
		try {
			oos.flush();
			oos.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			saveMRUPatchFolder(filePath);
			recentPatchFileList.add(fileToBeSaved);		
			saveRecentPatchFileList();
		}
	}

	public void fileSave(SpinCADBank b) {
		File fileToBeSaved = new File(prefs.get("MRUBankFolder",  "") + "/" + b.bankFileName);
		String filePath = fileToBeSaved.getPath();
		loadRecentBankFileList();

		try { 
			FileOutputStream fos = new FileOutputStream(filePath); 
			ObjectOutputStream oos = new ObjectOutputStream(fos); 

			oos.writeObject(b); 	

			oos.flush(); 
			oos.close(); 
		} 
		catch(Exception e) { 
			System.out.println("Exception during serialization: " + e); 
			//		System.exit(0); 
		} 
		finally {
			saveMRUBankFolder(filePath);
			recentBankFileList.add(fileToBeSaved);		
			saveRecentBankFileList();
		}
	}

	public SpinCADPatch fileReadPatch(String fileName) throws IOException, ClassNotFoundException {
		// Object deserialization 
		FileInputStream fis = new FileInputStream(fileName); 
		ObjectInputStream ois = new ObjectInputStream(fis); 
		SpinCADPatch p  = new SpinCADPatch();

		p = (SpinCADPatch) ois.readObject();

		ois.close(); 
		return p;
	} 

	public SpinCADPatch fileOpenPatch() {

		loadRecentPatchFileList();
		final String newline = "\n";
		SpinCADPatch p = new SpinCADPatch();

		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"SpinCAD Files", "spcd");
		fc.setFileFilter(filter);
		fc.setAccessory(recentPatchFileList);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(new JFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();

			System.out.println("Opening: " + file.getName() + "."
					+ newline);
			try {
				String filePath = file.getPath();
				p = fileReadPatch(filePath);
				saveMRUPatchFolder(filePath);
				recentPatchFileList.add(file);
				String fileName = file.getName();
				p.patchFileName = fileName;
				p.cb.setFileName(fileName);
			} catch (Exception e) {	
				e.printStackTrace();
				// XXX				MessageBox("File open failed!", "This spcd file may be from\nan incompatible version of \nSpinCAD Designer.");
			} finally {
			}
		} else {
			System.out.println("Open command cancelled by user."
					+ newline);
		}
		saveRecentPatchFileList();
		return p;
	}

	public SpinCADBank fileReadBank(File fileName) throws IOException, ClassNotFoundException {
		// Object deserialization 
		FileInputStream fis = new FileInputStream(fileName); 
		ObjectInputStream ois = new ObjectInputStream(fis); 
		SpinCADBank b  = (SpinCADBank)ois.readObject();
		ois.close(); 
		return b;
	} 	

	public SpinCADBank fileOpenBank() {
		loadRecentBankFileList();
		SpinCADBank b = null;

		final String newline = "\n";
		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"SpinCAD Bank Files", "spbk");
		fc.setFileFilter(filter);
		fc.setAccessory(recentBankFileList);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(new JFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			// This is where a real application would open the file.
			System.out.println("Opening: " + file.getName() + "."
					+ newline);
			try {
				// first, open bank, then open patch 0
				b = fileReadBank(file);
				String filePath = file.getName();
				saveMRUBankFolder(filePath);
				recentBankFileList.add(file);
				// XXX debug looks like we kinda have the same info in 2 places
				b.bankFileName = filePath;
				b.cb.setFileName(filePath);
			} catch (Exception e) {	// thrown over in SpinCADFile.java
				e.printStackTrace();
				//				MessageBox("File open failed!", "This spbk file may be from\nan incompatible version of \nSpinCAD Designer.");
			}
		} else {
			System.out.println("Open command cancelled by user."
					+ newline);
		}
		saveRecentBankFileList();
		return b;
	}

	public void fileSaveAsm(SpinCADPatch p, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

		writer.write("; " + p.cb.fileName);
		writer.newLine();
		writer.write("; " + p.cb.version);
		writer.newLine();
		writer.write("; " + p.cb.line[0]);
		writer.newLine();
		writer.write("; " + p.cb.line[1]);
		writer.newLine();
		writer.write("; " + p.cb.line[2]);
		writer.newLine();
		writer.write("; " + p.cb.line[3]);
		writer.newLine();
		writer.write("; " + p.cb.line[4]);
		writer.newLine();
		writer.write("; " + p.cb.line[5]);
		writer.newLine();

		String codeListing = p.patchModel.getRenderBlock().getProgramListing(1);
		String[] words = codeListing.split("\n");
		for (String word: words) {
			writer.write(word);
			writer.newLine();
		}
		writer.close();
	}

	public void fileSaveHex(int[] codeListing, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
		int i = -1;
		String outputString = new String();
		for(int ii = 0, index = 0; ii < 128; ii++) {
			if(ii < codeListing.length) {
				i = codeListing[ii];
			} else {
				i = 0;
			}
			if(i != -1) {
				if(i == 0) { 	// do NOP conversion
					i = 0x11;
				}
				outputString = String.format("04%04X00%08X", index, i);
				long message = Long.parseLong(outputString, 16);
				int checksum = 0;
				for(int iii = 0; iii < 8; iii++) {
					checksum = checksum + (int) (message & 0xff);
					message = message >> 8;
				}
				checksum = ((~checksum) & 0xff) + 1;
				writer.write(":" + outputString + String.format("%02X",  checksum));
				writer.newLine();		
				index += 4;
			}
		}
		writer.write(":00000001FF\n");
		writer.close();
	}

	public void fileSavePatchAs(SpinCADPatch p) {
		// Create a file chooser
		String savedPath = prefs.get("MRUPatchFolder", "");
		final JFileChooser fc = new JFileChooser(savedPath);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"SpinCAD Files", "spcd");
		fc.setFileFilter(filter);
		fc.setSelectedFile(new File(p.patchFileName));
		int returnVal = fc.showSaveDialog(new JFrame());
		// need to process user canceling box right here
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			// In response to a button click:
			File fileToBeSaved = fc.getSelectedFile();

			if (!fileToBeSaved.getAbsolutePath().endsWith(".spcd")) {
				fileToBeSaved = new File(fc.getSelectedFile() + ".spcd");
			}
			int n = JOptionPane.YES_OPTION;
			if (fileToBeSaved.exists()) {
				JFrame frame = new JFrame();
				n = JOptionPane.showConfirmDialog(frame,
						"Would you like to overwrite it?", "File already exists!",
						JOptionPane.YES_NO_OPTION);
			}
			if (n == JOptionPane.YES_OPTION) {
				try {
					String filePath = fileToBeSaved.getPath();
					String fileName = fileToBeSaved.getName();
					p.patchFileName = fileName;
					fileSave(p);
					recentPatchFileList.add(fileToBeSaved);
					saveMRUPatchFolder(filePath);
				} finally {
				}
			}
		}
	}

	public void fileSaveBankAs(SpinCADBank b) {
		// Create a file chooser
		String savedPath = prefs.get("MRUBankFolder", "");
		final JFileChooser fc = new JFileChooser(savedPath);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"SpinCAD Bank Files", "spbk");
		fc.setFileFilter(filter);
		fc.setSelectedFile(new File(b.bankFileName));
		int returnVal = fc.showSaveDialog(new JFrame());
		// need to process user canceling box right here
		if (returnVal == JFileChooser.APPROVE_OPTION) {

			// In response to a button click:
			File fileToBeSaved = fc.getSelectedFile();

			if (!fc.getSelectedFile().getAbsolutePath().endsWith(".spbk")) {
				fileToBeSaved = new File(fc.getSelectedFile() + ".spbk");
			}
			b.bankFileName = fileToBeSaved.getName();

			int n = JOptionPane.YES_OPTION;
			if (fileToBeSaved.exists()) {
				JFrame frame = new JFrame();
				n = JOptionPane.showConfirmDialog(frame,
						"Would you like to overwrite it?", "File already exists!",
						JOptionPane.YES_NO_OPTION);
				if (n == JOptionPane.YES_OPTION) {
					try {
						fileSave(b);
					} finally {
						recentBankFileList.add(fileToBeSaved);
						saveMRUBankFolder(fileToBeSaved.getPath());
					}
				}
			}
			else {
				fileSave(b);
			}
		}
	}

	public void fileSaveAsm(SpinCADPatch patch) {
		// Create a file chooser
		String savedPath = prefs.get("MRUSpnFolder", "");

		final JFileChooser fc = new JFileChooser(savedPath);
		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Spin ASM Files", "spn");
		fc.setFileFilter(filter);
		// XXX DEBUG
		//		fc.showSaveDialog(model);
		File fileToBeSaved = fc.getSelectedFile();

		if (!fc.getSelectedFile().getAbsolutePath().endsWith(".spn")) {
			fileToBeSaved = new File(fc.getSelectedFile() + ".spn");
		}
		int n = JOptionPane.YES_OPTION;
		if (fileToBeSaved.exists()) {
			JFrame frame1 = new JFrame();
			n = JOptionPane.showConfirmDialog(frame1,
					"Would you like to overwrite it?", "File already exists!",
					JOptionPane.YES_NO_OPTION);
		}
		if (n == JOptionPane.YES_OPTION) {
			String filePath;
			try {
				filePath = fileToBeSaved.getPath();
				fileToBeSaved.delete();
			} finally {
			}

			try {
				fileSaveAsm(patch, filePath);
			} catch (IOException e) {
				JOptionPane.showOptionDialog(null,
						"File save error!", "Error",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);
				e.printStackTrace();
			}

			saveMRUSpnFolder(filePath);
		}
	}

	public void fileSaveHex(SpinCADModel model) {
		// Create a file chooser
		String savedPath = prefs.get("MRUHexFolder", "");

		final JFileChooser fc = new JFileChooser(savedPath);
		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Hex Files", "hex");
		fc.setFileFilter(filter);
		// debug
		//		fc.showSaveDialog(model);
		File fileToBeSaved = fc.getSelectedFile();

		if (!fc.getSelectedFile().getAbsolutePath().endsWith(".hex")) {
			fileToBeSaved = new File(fc.getSelectedFile() + ".hex");
		}
		int n = JOptionPane.YES_OPTION;
		if (fileToBeSaved.exists()) {
			JFrame frame1 = new JFrame();
			n = JOptionPane.showConfirmDialog(frame1,
					"Would you like to overwrite it?", "File already exists!",
					JOptionPane.YES_NO_OPTION);
		}
		if (n == JOptionPane.YES_OPTION) {
			String filePath;
			try {
				filePath = fileToBeSaved.getPath();
				fileToBeSaved.delete();
			} finally {
			}
			try {
				fileSaveHex(SpinCADModel.getRenderBlock().generateHex(), filePath);
			} catch (IOException e) {
				JOptionPane.showOptionDialog(null,
						"File save error!", "Error",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, null, null);

				e.printStackTrace();
			}
			saveMRUHexFolder(filePath);
		}
	}
	
	//====================================================
	// most-recently used file and folder methods
	
	private void saveMRUBankFolder(String path) {
		Path pathE = Paths.get(path);

		String pathS = pathE.getParent().toString();
		String nameS = pathE.getFileName().toString();

		prefs.put("MRUBankFolder", pathS);
		prefs.put("MRUBankFileName", nameS);
	}

	private void saveMRUPatchFolder(String path) {
		Path pathE = Paths.get(path);

		String pathS = pathE.getParent().toString();
		String nameS = pathE.getFileName().toString();

		prefs.put("MRUPatchFolder", pathS);
		prefs.put("MRUPatchFileName", nameS);
	}

	private void saveMRUSpnFolder(String path) {
		Path pathE = Paths.get(path);
		prefs.put("MRUSpnFolder", pathE.toString());
	}

	private void saveMRUHexFolder(String path) {
		Path pathE = Paths.get(path);
		prefs.put("MRUHexFolder", pathE.toString());
	}

//========================================================================	
// recent file lists
	
	private void saveRecentPatchFileList() {
		StringBuilder sb = new StringBuilder(128);
		if(recentPatchFileList != null) {
			int k = recentPatchFileList.listModel.getSize() - 1;
			for (int index = 0; index <= k; index++) {
				File file = recentPatchFileList.listModel.getElementAt(k - index);
				if (sb.length() > 0) {
					sb.append(File.pathSeparator);
				}
				sb.append(file.getPath());
			}
			Preferences p = Preferences.userNodeForPackage(RecentFileList.class);
			p.put("RecentPatchFileList.fileList", sb.toString());
		}
	}

	private void loadRecentPatchFileList() {
		Preferences p = Preferences.userNodeForPackage(RecentFileList.class);
		String listOfFiles = p.get("RecentPatchFileList.fileList", null);
		if (fc == null) {
			String savedPath = prefs.get("MRUPatchFolder", "");
			File MRUPatchFolder = new File(savedPath);
			fc = new JFileChooser(MRUPatchFolder);
			recentPatchFileList = new RecentFileList(fc);
			if (listOfFiles != null) {
				String[] files = listOfFiles.split(File.pathSeparator);
				for (String fileRef : files) {
					File file = new File(fileRef);
					if (file.exists()) {
						recentPatchFileList.listModel.add(file);
					}
				}
			}
			fc.setAccessory(recentPatchFileList);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
	}

	private void saveRecentBankFileList() {
		StringBuilder sb = new StringBuilder(128);
		if(recentBankFileList != null) {
			int k = recentBankFileList.listModel.getSize() - 1;
			for (int index = 0; index <= k; index++) {
				File file = recentBankFileList.listModel.getElementAt(k - index);
				if (sb.length() > 0) {
					sb.append(File.pathSeparator);
				}
				sb.append(file.getPath());
			}
			Preferences p = Preferences.userNodeForPackage(RecentFileList.class);
			p.put("RecentBankFileList.fileList", sb.toString());
		}
	}

	private void loadRecentBankFileList() {
		Preferences p = Preferences.userNodeForPackage(RecentFileList.class);
		String listOfFiles = p.get("RecentBankFileList.fileList", null);
		if (fc == null) {
			String savedPath = prefs.get("MRUBankFolder", "");
			File MRUBankFolder = new File(savedPath);
			fc = new JFileChooser(MRUBankFolder);
			recentBankFileList = new RecentFileList(fc);
			if (listOfFiles != null) {
				String[] files = listOfFiles.split(File.pathSeparator);
				for (String fileRef : files) {
					File file = new File(fileRef);
					if (file.exists()) {
						recentBankFileList.listModel.add(file);
					}
				}
			}
			fc.setAccessory(recentBankFileList);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
	}
}

