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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.holycityaudio.SpinCAD.SpinCADFrame.commentBlockBank;
import com.holycityaudio.SpinCAD.SpinCADFrame.commentBlockPatch;

public class SpinCADFile {

	public SpinCADFile() {
		// Auto-generated constructor stub
	}

	public static void fileSave(commentBlockPatch cb, SpinCADModel m, String fileName) {
			FileOutputStream fos;
			ObjectOutputStream oos = null;
			try {
				fos = new FileOutputStream(fileName);
				oos = new ObjectOutputStream(fos); 
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			// file name and SpinCAD Version are not saved with the file.
			//oos.writeObject((Object)cb.line1text.getText());
			//oos.writeObject((Object)cb.line2text.getText());
			patchSave(cb, m, oos);
			try {
				oos.flush();
				oos.close(); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
	
	private static void patchSave(commentBlockPatch cb, SpinCADModel m, ObjectOutputStream oops) {
		try {
			oops.writeObject((Object)cb.line3text.getText());
			oops.writeObject((Object)cb.line4text.getText());
			oops.writeObject((Object)cb.line5text.getText());
			oops.writeObject((Object)cb.line6text.getText());
			oops.writeObject((Object)cb.line7text.getText());
			oops.writeObject(m); 	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void fileSave(commentBlockBank cb, SpinCADBank b, String fileName) {
		try { 
			FileOutputStream fos = new FileOutputStream(fileName); 
			ObjectOutputStream oos = new ObjectOutputStream(fos); 
			// file name and SpinCAD Version are not saved with the file.
			//oos.writeObject((Object)cb.line1text.getText());
			//oos.writeObject((Object)cb.line2text.getText());
			oos.writeObject((Object)cb.line3text.getText());
			oos.writeObject((Object)cb.line4text.getText());
			oos.writeObject((Object)cb.line5text.getText());
			oos.writeObject((Object)cb.line6text.getText());
			oos.writeObject((Object)cb.line7text.getText());
			for(int i = 0; i < 8; i++ ) {
//				b.
//				patchSave(b.bank[i].comment, b.bank[i].patchModel, fileName);
			}
			oos.flush(); 
			oos.close(); 
		} 
		catch(Exception e) { 
			System.out.println("Exception during serialization: " + e); 
			//		System.exit(0); 
		} 
	}

	public static void fileSaveAsm(commentBlockPatch cb, String codeListing, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

		writer.write("; " + cb.line1text.getText());
		writer.newLine();
		writer.write("; " + cb.line2text.getText());
		writer.newLine();
		writer.write("; " + cb.line3text.getText());
		writer.newLine();
		writer.write("; " + cb.line4text.getText());
		writer.newLine();
		writer.write("; " + cb.line5text.getText());
		writer.newLine();
		writer.write("; " + cb.line6text.getText());
		writer.newLine();
		writer.write("; " + cb.line7text.getText());
		writer.newLine();

		String[] words = codeListing.split("\n");
		for (String word: words) {
			writer.write(word);
			writer.newLine();
		}
		writer.close();
	}

	public static void fileSaveHex(int[] codeListing, String fileName) throws IOException {
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

	public static SpinCADModel fileRead(commentBlockPatch cb, SpinCADModel m, String fileName) throws IOException, ClassNotFoundException {
		// Object deserialization 
		FileInputStream fis = new FileInputStream(fileName); 
		ObjectInputStream ois = new ObjectInputStream(fis); 
		cb.line1text.setText("Patch: " + fileName);
		// line 2 is set back in SpinCAD Frame - it's the SpinCAD Designer version
		// cb.line2text.setText((String)ois.readObject());
		cb.line3text.setText((String)ois.readObject());
		cb.line4text.setText((String)ois.readObject());
		cb.line5text.setText((String)ois.readObject());
		cb.line6text.setText((String)ois.readObject());
		cb.line7text.setText((String)ois.readObject());
		m = (SpinCADModel)ois.readObject(); 
		ois.close(); 
		// System.out.println("m: " + m); 
		return m;
	} 	
}

