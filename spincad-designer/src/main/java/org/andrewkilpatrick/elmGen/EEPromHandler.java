/* ElmGen - DSP Development Tool
 * Copyright (C)2011 - Andrew Kilpatrick
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
package org.andrewkilpatrick.elmGen;

import java.io.*;

/**
 * This class manages the assembling and writing of program banks to the EEPROM
 * for the hardware FV-1 DSP. Banks are loaded with ElmPrograms and and then
 * either individual or all banks can be written to the EEPROM. This class uses
 * the EEPromProgrammer class to interface with the programming hardware.
 * 
 * @author andrew
 */
public class EEPromHandler {
	int banks[][];
	EEPromProgrammer programmer;

	/**
	 * Creates a new EEPROM file. An EEPROM can store up to 8 programs.
	 * 
	 * @param programmerPort the serial port name of the programmer (COM3, /dev/usbserial, etc.)
	 */
	public EEPromHandler(String programmerPort) {
		banks = new int[8][128];
		for(int i = 0; i < 8; i ++) {
			for(int j = 0; j < 128; j ++) {
				banks[i][j] = 0;
			}
		}
		programmer = new EEPromProgrammer(programmerPort);
	}
	
	/**
	 * Fills a code bank with a program.
	 * 
	 * @param prog the ElmProgram to load
	 * @param bank the program bank (0-7)
	 */
	public void fillBank(ElmProgram prog, int bank) {
		if(bank < 0 || bank > 7) {
			throw new IllegalArgumentException("invalid bank: " + bank);
		}
		
		// TODO
/*		byte code[] = prog.generateHex();
		if(code.length > 128) {
			throw new ElmProgramException("code too long: " + code.length);
		}
		for(int i = 0; i < 128; i ++) {
			banks[bank][i] = 0;
		}
		for(int i = 0; i < code.length; i ++) {
			banks[bank][i] = code[i];
		}
		System.out.println("filled bank: " + bank + " with: " + 
				code.length + " words");
*/	}
	
	/**
	 * Writes the EEPROM with one of the banks.
	 * 
	 * @param bank the bank to write (0-7)
	 * @throws IOException if there was an error
	 */
	public void writeBank(int bank) throws IOException {
		if(bank < 0 || bank > 7) {
			throw new IllegalArgumentException("bank out of range: " + 
					bank + " - must be 0 to 7");
		}
		programmer.programBank(bank, this);
		System.out.println("DONE writing bank: " + bank + "!");
	}

	/**
	 * Writes the EEPROM with one of the banks.
	 * 
	 * @param bank the bank to write (0-7)
	 * @throws IOException if there was an error
	 */
	public void writeBankToFile(int bank, String fileName) throws IOException {
		if(bank < 0 || bank > 7) {
			throw new IllegalArgumentException("bank out of range: " + 
					bank + " - must be 0 to 7");
		}
		int i;
		File outputfile = new File(fileName);
		for(i = 0; i < 128; i++)
		{
			System.out.printf("i: %d data: %x %b", i, banks[bank][i], banks[bank][i]);
		}
		System.out.println("DONE writing bank to file: " + bank + "!" + fileName);
	}
	
	/**
	 * Writes all banks of the EEPROM.
	 * 
	 * @throws IOException if there was an error
	 */
	public void writeAllBanks() throws IOException {
		for(int i = 0; i < 8; i ++) {
			programmer.programBank(i, this);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("DONE writing all banks!");
	}
	
	/**
	 * Reads a byte from the EEPROM file.
	 * 
	 * @param bank the bank (0-7)
	 * @param offset the byte offset (0-511)
	 * @return the byte for this address
	 */
	public byte[] getBytes(int bank, int offset, int length) {		
		if(bank < 0 || bank > 7) {
			throw new IllegalArgumentException("invalid bank: " + bank);
		}
		if(offset < 0 || offset > 511) {
			throw new IllegalArgumentException("invalid offset: " + offset);
		}
		if(length < 1 || offset + length > 512) {
			throw new IllegalArgumentException("invalid length: " + length);
		}
		byte data[] = new byte[length];
		for(int i = 0; i < length; i ++) {
			int pos = (offset + i);
			data[i] = (byte)((banks[bank][pos >> 2] & 0xff000000) >> 24);
			if((pos & 0x03) == 1) {
				data[i] = (byte)((banks[bank][pos >> 2] & 0x00ff0000) >> 16);
			}
			if((pos & 0x03) == 2) {
				data[i] = (byte)((banks[bank][pos >> 2] & 0x0000ff00) >> 8);
			}
			if((pos & 0x03) == 3) {
				data[i] = (byte)(banks[bank][pos >> 2] & 0x000000ff);
			}
		}
		return data;
	}
}
