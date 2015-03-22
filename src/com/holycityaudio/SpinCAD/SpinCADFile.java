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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SpinCADFile {

	public SpinCADFile() {
		// Auto-generated constructor stub
	}

	public static void fileSave(SpinCADModel m, String fileName) {
		try { 
			FileOutputStream fos = new FileOutputStream(fileName); 
			ObjectOutputStream oos = new ObjectOutputStream(fos); 
			oos.writeObject(m); 
			oos.flush(); 
			oos.close(); 
		} 
		catch(Exception e) { 
			System.out.println("Exception during serialization: " + e); 
			//		System.exit(0); 
		} 
	}

	public static void fileSaveAsm(String codeListing, String fileName) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

		String[] words = codeListing.split("\n");
		for (String word: words) {
			writer.write(word);
			writer.newLine();
		}
		writer.close();
	}

	public static SpinCADModel fileRead(SpinCADModel m, String fileName) throws IOException, ClassNotFoundException {
		// Object deserialization 
		FileInputStream fis = new FileInputStream(fileName); 
		ObjectInputStream ois = new ObjectInputStream(fis); 
		m = (SpinCADModel)ois.readObject(); 
		ois.close(); 
		// System.out.println("m: " + m); 
		return m;
	} 	
}

