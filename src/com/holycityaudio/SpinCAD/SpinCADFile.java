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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import org.apache.commons.io.FilenameUtils;

public class SpinCADFile {
	// PREFERENCES =====================================================
	// following things are saved in the SpinCAD preferences
	private Preferences prefs;
	private RecentFileList recentBankFileList = null;
	private RecentFileList recentPatchFileList = null;
	private RecentFileList recentHexFileList = null;
	// this next one is specific to file open, needs to be here for MRU file list operations
	private JFileChooser fc;

	public SpinCADFile() {
		// create a Preferences instance (somewhere later in the code)
		prefs = Preferences.userNodeForPackage(this.getClass());
	}

	private void init_pref(String prefName, String initValue){
		String value = prefs.get(prefName,  "");
		if (value == "") {
			prefs.put(prefName, initValue);
		}
	}
	
	public void init_prefs() {
    	Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
	
		init_pref("MRUPatchFolder", s);
		init_pref("MRUBankFolder", s);
		init_pref("MRUSpnFolder", s);
		init_pref("MRUSpjFolder", s);
		init_pref("MRUHexFolder", s);
		init_pref("RecentPatchFileList.fileList", "");
		init_pref("RecentBankFileList.fileList", "");
		init_pref("RecentHexFileList.fileList", "");
	}
	
	public void fileSavePatch(SpinCADPatch m) {
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
			// XXX debug this is currently triggered when a block control panel is open and you save the patch
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

	public void fileSaveBank(SpinCADBank b) {
		
		String Folder = prefs.get("MRUBankFolder",  "");
		
		File fileToBeSaved = new File( Folder + "/" + b.bankFileName);
		System.out.println("fileToBeSaved " + fileToBeSaved);

		String filePath = fileToBeSaved.getPath();
		System.out.println("filepath " + filePath);
		
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
			if(recentBankFileList != null) {
				recentBankFileList.add(fileToBeSaved);
			}
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

	// backwards compatibility with SpinCAD 952 patch file serialization
	public SpinCADPatch fileReadPatch952(String fileName) throws IOException, ClassNotFoundException {
		// Object deserialization 
		SpinCADPatch p = new SpinCADPatch();
		p.patchFileName = fileName;

		FileInputStream fis = new FileInputStream(fileName); 
		ObjectInputStream ois = new ObjectInputStream(fis); 

		p.cb.line[0] = (String)ois.readObject();
		p.cb.line[1] = (String)ois.readObject();
		p.cb.line[2] = (String)ois.readObject();
		p.cb.line[3] = (String)ois.readObject();
		p.cb.line[4] = (String)ois.readObject();
		p.patchModel = (SpinCADModel)ois.readObject(); 
		ois.close(); 
		return p;
	} 

	// backwards compatibility with SpinCAD 952 patch file serialization
	public SpinCADPatch fileReadHex(String fileName) throws IOException, ClassNotFoundException {
		SpinCADPatch p = new SpinCADPatch();
		p.patchFileName = fileName;
		File file = new File(fileName);
		int nComments = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			char c = 'c';
			int nBytes= 0;
			int nLines = 0;
			int address = 0;
			int recordType = 0;
			int data = 0;
			for(String line; (line = br.readLine()) != null; ) {
				c = line.charAt(0);
				switch (c) {
				case ':':
					// process 64 lines of hex file from beginning
					if(nLines < 128) {
						//						System.out.println("================================ " + nLines);
						//						System.out.println(line);
						String byteString = line.substring(1, 3);
						nBytes = Integer.parseInt(byteString, 16);
						//						System.out.println(byteString + " Bytes= " + nBytes);
						//						System.out.println("Address: " + line.substring(3,7));
						address =  Integer.parseInt(line.substring(3,7), 16);
						//						System.out.printf("Address: %x\n", address);
						//						System.out.println("recordType: " + line.substring(7,9));
						recordType= Integer.parseInt(line.substring(7,9), 16);
						//						System.out.println("recordType: " + recordType);
						data = (int) Long.parseLong(line.substring(9,9 + (2 * nBytes)), 16);
						//						System.out.println(line.substring(9,9 + (2 * nBytes)));
						//						System.out.printf("data: %x\n", data);		
						p.hexFile[nLines]= data;
					}
					nLines++;
					break;
				case ';':
					// process up to 5 comment lines
					if(nComments < 5) {
						p.cb.line[nComments] = line;
						nComments ++;
					}
					break;
				default:
					break;
					// process the line.
				}
			}
			// line is not visible here.
		}
		p.isHexFile = true;
		return p;
	} 

	public SpinCADPatch fileOpenHex() {

		loadRecentHexFileList();
		final String newline = "\n";
		SpinCADPatch p = new SpinCADPatch();

		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Spin Hex Files", "hex");
		fc.setFileFilter(filter);
		fc.setAccessory(recentHexFileList);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(new JFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			String filePath = null;
			File file = fc.getSelectedFile();

			System.out.println("Opening: " + file.getName() + "."
					+ newline);
			filePath = file.getPath();
			try {
				p = fileReadHex(filePath);
			} catch (Exception e) {	
				e.printStackTrace();
				SpinCADDialogs.MessageBox("Hex File open failed!", "That's not supposed to happen!");
			} finally {
				saveMRUHexFolder(filePath);
				recentHexFileList.add(file);
				String fileName = file.getName();
				p.patchFileName = fileName;
				p.cb.setFileName(fileName);
				p.isHexFile = true;
				p.setChanged(false);
			}
			saveRecentHexFileList();
			return p;
		} else {
			System.out.println("Open command cancelled by user."
					+ newline);
			return null;
		}

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
			String filePath = null;
			File file = fc.getSelectedFile();

			System.out.println("Opening: " + file.getName() + "."
					+ newline);
			filePath = file.getPath();
			try {
				p = fileReadPatch(filePath);
			} catch (Exception e) {	
				e.printStackTrace();
				try {
					System.out.println("Trying version 952 format..." + newline);					
					p = fileReadPatch952(filePath);
				} catch (Exception exc) {	
					exc.printStackTrace();
				}
				SpinCADDialogs.MessageBox("File open failed!", "This spcd file may be from\nan incompatible version of \nSpinCAD Designer.");
			} finally {
				saveMRUPatchFolder(filePath);
				recentPatchFileList.add(file);
				String fileName = file.getName();
				p.patchFileName = fileName;
				p.cb.setFileName(fileName);
			}
			saveRecentPatchFileList();
			return p;
		} else {
			System.out.println("Open command cancelled by user."
					+ newline);
			return null;
		}
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
		File file = null;

		final String newline = "\n";
		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"SpinCAD Bank Files", "spbk");
		System.out.println("\nfilter=" + filter);
		fc.setFileFilter(filter);
		fc.setAccessory(recentBankFileList);
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnVal = fc.showOpenDialog(new JFrame());
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			// This is where a real application would open the file.
			System.out.println("Opening: " + file.getName() + "."
					+ newline);
			try {
				// first, open bank, then open patch 0
				b = fileReadBank(file);
			} catch (Exception e) {	// thrown over in SpinCADFile.java
				e.printStackTrace();
				//				MessageBox("File open failed!", "This spbk file may be from\nan incompatible version of \nSpinCAD Designer.");
			}
			String filePath = file.getPath();
			String fileName = file.getName();
			saveMRUBankFolder(filePath);
			recentBankFileList.add(file);
			// XXX debug looks like we kinda have the same info in 2 places
			b.bankFileName = fileName;
			b.cb.setFileName(fileName);
			b.changed = false;
			saveRecentBankFileList();
			return b;
		} else {
			System.out.println("Open command cancelled by user."
					+ newline);
			return null;
		}
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
					saveMRUPatchFolder(filePath);

					String fileName = fileToBeSaved.getName();
					p.patchFileName = fileName;

					fileSavePatch(p);
					recentPatchFileList.add(fileToBeSaved);

				} catch (Exception e) {	// thrown over in SpinCADFile.java
					e.printStackTrace();
					SpinCADDialogs.MessageBox("File save failed!", "look at stack trace for info");
				}
				finally {
					p.setChanged(false);
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
						fileSaveBank(b);
					} finally {
					}
				}
			}
			else {
				fileSaveBank(b);
			}
			b.changed = false;
			if(recentBankFileList != null){
				recentBankFileList.add(fileToBeSaved);
			}
			saveMRUBankFolder(fileToBeSaved.getPath());
			b.bankFileName = fileToBeSaved.getName();
		}
	}

	// File Save Asm =============================================

	public void fileSaveAsm(SpinCADPatch patch) {
		// Create a file chooser
		String savedPath = prefs.get("MRUSpnFolder", "");

		final JFileChooser fc = new JFileChooser(savedPath);
		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Spin ASM Files", "spn");
		fc.setFileFilter(filter);
		// XXX DEBUG
		fc.showSaveDialog(new JFrame());
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
			String filePath = fileToBeSaved.getPath();
			fileToBeSaved.delete();

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

	public void fileSaveAsm(SpinCADPatch p, String fileName) throws IOException {

		// automatically overwrite existing ASM files
		File fileToBeSaved = new File(fileName);

		if (fileToBeSaved.exists()) {
			fileToBeSaved.delete();
		}

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

		String codeListing = p.patchModel.getRenderBlock().getProgramListing(1);
		String[] words = codeListing.split("\n");
		for (String word: words) {
			writer.write(word);
			writer.newLine();
		}
		writer.close();
	}

	// File Save Hex =============================================

	public void fileSaveHex(SpinCADBank bank) {
		// Create a file chooser
		String savedPath = prefs.get("MRUHexFolder", "");

		final JFileChooser fc = new JFileChooser(savedPath);
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Hex Files", "hex");
		fc.setFileFilter(filter);
		fc.showSaveDialog(new JFrame());
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
			for(int i = 0; i < 8; i++) {
				try {
					if(bank.patch[i].isHexFile) {
						fileSaveHex(i, bank.patch[i].hexFile, filePath);						
					}
					else {
						fileSaveHex(i, bank.patch[i].patchModel.getRenderBlock().generateHex(), filePath);										
					}
				} catch (IOException e) {
					JOptionPane.showOptionDialog(null,
							"File save error!", "Error",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, null, null);

					e.printStackTrace();
				}
			}
			saveMRUHexFolder(filePath);
		}
	}

	public void fileSaveSpj(SpinCADBank bank) {
		// Create a file chooser
		String savedPath = prefs.get("MRUSpjFolder", "");
		String[] spnFileNames = new String[8];

		final JFileChooser fc = new JFileChooser(savedPath);
		// In response to a button click:
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"Spin Project Files", "spj");
		fc.setFileFilter(filter);
		// XXX debug
		fc.showSaveDialog(new JFrame());
		File fileToBeSaved = fc.getSelectedFile();

		if (!fc.getSelectedFile().getAbsolutePath().endsWith(".spj")) {
			fileToBeSaved = new File(fc.getSelectedFile() + ".spj");
		}
		int n = JOptionPane.YES_OPTION;
		if (fileToBeSaved.exists()) {
			JFrame frame1 = new JFrame();
			n = JOptionPane.showConfirmDialog(frame1,
					"Would you like to overwrite it?", "File already exists!",
					JOptionPane.YES_NO_OPTION);
		}
		if (n == JOptionPane.YES_OPTION) {
			// filePath points at the desired Spj file
			String filePath = fileToBeSaved.getPath();
			String folder = fileToBeSaved.getParent().toString();

			// export the individual SPN files
			for(int i = 0; i < 8; i++) {
				try {
					String asmFileNameRoot =  FilenameUtils.removeExtension(bank.patch[i].patchFileName);
					String asmFileName = folder + "\\" +  asmFileNameRoot + ".spn";
					if(bank.patch[i].patchFileName != "Untitled") {
						fileSaveAsm(bank.patch[i], asmFileName);
						spnFileNames[i] = asmFileName;				
					}
				} catch (IOException e) {
					JOptionPane.showOptionDialog(null,
							"File save error!", "Error",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, null, null);

					e.printStackTrace();
				} finally {
				}
			}

			// now create the Spin Project file
			fileToBeSaved.delete();
			BufferedWriter writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(fileToBeSaved, true));
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			try {
				writer.write("NUMDOCS:8");
				writer.newLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			for(int i = 0; i < 8; i++) {
				try {
					if(bank.patch[i].patchFileName != "Untitled") {
						writer.write(spnFileNames[i] + ",1");
					}
					else {
						writer.write(",0");						
					}
					writer.newLine();				
				} catch (IOException e) {
					JOptionPane.showOptionDialog(null,
							"File save error!\n" + filePath, "Error",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, null, null);

					e.printStackTrace();
				}
			}
			// write the build flags
			try {
				writer.write(",1,1,1");
				writer.newLine();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			saveMRUSpjFolder(filePath);
		}
	}

	public void fileSaveHex(int patchIndex, int[] codeListing, String fileName) throws IOException {
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
				outputString = String.format("04%04X00%08X", (patchIndex * 0x200) + index, i);
				//				outputString = String.format("04%04X00%08X", index, i);
				long message = Long.parseLong(outputString, 16);
				int checksum = 0;
				for(int iii = 0; iii < 8; iii++) {
					checksum = checksum + (int) (message & 0xff);
					message = message >> 8;
				}
				checksum = (((~checksum) & 0xff) + 1) & 0xff;
				writer.write(":" + outputString + String.format("%02X",  checksum));
				writer.newLine();		
				index += 4;
			}
		}
		if(patchIndex == 7) {
			writer.write(":00000001FF\n");	
		}
		writer.close();
	}


	//====================================================
	// most-recently used file and folder methods

	private void saveMRUBankFolder(String path) {
		Path pathE = Paths.get(path);

		String pathS = pathE.getParent().toString();
		String nameS = pathE.getFileName().toString();

		prefs.put("MRUBankFolder", pathS);
	}

	private void saveMRUPatchFolder(String path) {
		Path pathE = Paths.get(path);

		String pathS = pathE.getParent().toString();
		String nameS = pathE.getFileName().toString();

		prefs.put("MRUPatchFolder", pathS);
	}

	private void saveMRUSpnFolder(String path) {
		Path pathE = Paths.get(path);
		prefs.put("MRUSpnFolder", pathE.toString());
	}

	private void saveMRUHexFolder(String path) {
		Path pathE = Paths.get(path);
		String pathS = pathE.getParent().toString();
		prefs.put("MRUHexFolder", pathS);
	}


	private void saveMRUSpjFolder(String path) {
		Path pathE = Paths.get(path);
		prefs.put("MRUSpjFolder", pathE.toString());
	}

	//========================================================================	
	// recent file lists

	private void saveRecentPatchFileList() {
		StringBuilder sb = new StringBuilder(128);
		if(recentPatchFileList != null) {
			int k = Math.min(20,recentPatchFileList.listModel.getSize() - 1);
			for (int index = 0; index <= k; index++) {
				File file = recentPatchFileList.listModel.getElementAt(k - index);
				if (sb.length() > 0) {
					sb.append(File.pathSeparator);
				}
				String fp = file.getPath();
				//				System.out.println(fp + " Path Length = " + fp.length());
				sb.append(file.getPath());
				//				System.out.println("RUFL Length = " + sb.length());
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
			int k = Math.min(20, recentBankFileList.listModel.getSize() - 1);
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
		System.out.println(listOfFiles);
		System.out.print(listOfFiles);
		
		if (listOfFiles != null ) {

			Integer listLength = listOfFiles.length();

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

	private void saveRecentHexFileList() {
		StringBuilder sb = new StringBuilder(128);
		if(recentHexFileList != null) {
			int k = Math.min(20, recentHexFileList.listModel.getSize() - 1);
			for (int index = 0; index <= k; index++) {
				File file = recentHexFileList.listModel.getElementAt(k - index);
				if (sb.length() > 0) {
					sb.append(File.pathSeparator);
				}
				sb.append(file.getPath());
			}
			Preferences p = Preferences.userNodeForPackage(RecentFileList.class);
			p.put("RecentHexFileList.fileList", sb.toString());
		}
	}

	private void loadRecentHexFileList() {
		Preferences p = Preferences.userNodeForPackage(RecentFileList.class);
		String listOfFiles = p.get("RecentHexFileList.fileList", null);
		if (fc == null) {
			String savedPath = prefs.get("MRUHexFolder", "");
			File MRUHexFolder = new File(savedPath);
			fc = new JFileChooser(MRUHexFolder);
			recentHexFileList = new RecentFileList(fc);
			if (listOfFiles != null) {
				String[] files = listOfFiles.split(File.pathSeparator);
				for (String fileRef : files) {
					File file = new File(fileRef);
					if (file.exists()) {
						recentHexFileList.listModel.add(file);
					}
				}
			}
			fc.setAccessory(recentHexFileList);
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		}
	}

}

