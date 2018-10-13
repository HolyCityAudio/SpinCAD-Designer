/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADPin.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class SpinCADPin implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6926516028763652131L;
	private int x_pos = -1;
	private int y_pos = -1;
	private SpinCADBlock block = null;
	private SpinCADBlock connectorBlock = null;
	private SpinCADPin connectorPin = null;	
	public enum pinType {AUDIO_IN, AUDIO_OUT, CONTROL_IN, CONTROL_OUT };
	public int numConnections = 0;

	// the register value is assigned during code generation
	private int Register = -1;

	private Color pinColor;
	private String name = "";
	private pinType type = null;

	/**
	 * @param b is block to which this pin belongs
	 * @param pinName is the name of the Pin, for display and reference
	 * @param pinType is audio/control input/output
	 * @param x is x position relative to left edge of block
	 * @param y is y position relative to top edge of block
	 */

	public SpinCADPin(SpinCADBlock b, String pinName, pinType pType, int x, int y) {
		block = b;
		name = pinName;
		type = pType;
		x_pos = x;
		y_pos = y;
	}

	public void setConnection(SpinCADBlock b, SpinCADPin p) {
		connectorBlock = b;
		connectorPin = p;
		numConnections++;
		p.numConnections++;
	}

	public void deletePinConnection() {
		connectorPin.numConnections--;
		if(connectorPin.numConnections == 0) {
			connectorPin.connectorPin = null;
			connectorPin.connectorBlock = null;
		}

		numConnections--;
		if(numConnections == 0) {
			connectorPin = null;
			connectorBlock = null;
		}
	}

	public boolean isConnected() {
		if (numConnections > 0) {
			return true;
		} else
			return false;
	}	public SpinCADBlock getBlockConnection() {
		return connectorBlock;
	}

	public SpinCADPin getPinConnection() {
		return connectorPin;
	}


	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		int size = 8;
		Ellipse2D boundingRect = new Ellipse2D.Double(block.getX() + x_pos - size/2, block.getY() + y_pos - size/2, size, size);
		g2.setColor(pinColor);
		g2.setStroke(new BasicStroke(2));
		g2.draw(boundingRect);
	}

	public pinType getType() {
		return type;
	}	

	public int getX() {
		return x_pos;
	}

	public void setX(int val) {
		x_pos = val;
	}

	public int getY() {
		return y_pos;
	}

	public void setColor(Color c) {
		pinColor = c;
	}

	public void setRegister(int r) {
		Register = r;
	}

	public int getRegister() {
		return Register;
	}

	public String getName() {
		return name;
	}	

	public boolean isInputPin() {
		if((type == pinType.AUDIO_IN) | (type == pinType.CONTROL_IN)) 
			return true;
		else
			return false;
	}

	public boolean isAudioInputPin() {
		if((type == pinType.AUDIO_IN)) 
			return true;
		else
			return false;
	}	

	public boolean isControlInputPin() {
		if((type == pinType.CONTROL_IN)) 
			return true;
		else
			return false;
	}

	public boolean isOutputPin() {
		if((type == pinType.AUDIO_OUT) | (type == pinType.CONTROL_OUT)) 
			return true;
		else
			return false;
	}

	public boolean isAudioOutputPin() {
		if((type == pinType.AUDIO_OUT)) 
			return true;
		else
			return false;
	}	

	public boolean isControlOutputPin() {
		if((type == pinType.CONTROL_OUT)) 
			return true;
		else
			return false;
	}

}

