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

import java.io.Serializable;

/**
 * This class represents a delay memory segment in the DSP.
 * 
 * @author andrew
 */
public class MemSegment implements Serializable {
	private static final long serialVersionUID = -8461943977923452457L;
	final String name;
	final int length;
	final int start;
	
	/**
	 * Creates a new memory segment.
	 * 
	 * @param name the segment name
	 * @param size the length in samples
	 * @param offset the sample offset
	 */
	public MemSegment(String name, int size, int offset) {
		this.name = name;
		this.length = size;
		this.start = offset;
		// System.out.println(name + " Size: " + size +  " Offset: " + offset);
	}

	public String getName() {
		return name;
	}
	
	public int getLength() {
		return length;
	}

	public int getStart() {
		return start;
	}
	
	public int getEnd() {
//		return start + length - 1;
		return start + length;  // SpinASM compatibility
	}
	
	public int getMiddle() {
		return (length - start) / 2;
	}
	
	public String toString() {
		return "MemSegment: " + name +
			" - start: " + start + String.format(" (0x%04X)", start) +
			" - end: " + getEnd() + String.format(" (0x%04X)", getEnd()) +
			" - length: " + length + String.format(" (0x%04X)", length);
	}
}
