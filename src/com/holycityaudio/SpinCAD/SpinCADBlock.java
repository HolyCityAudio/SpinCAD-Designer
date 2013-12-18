/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADBlock.java
 * Copyright (C)2013 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick
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
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

public class SpinCADBlock extends SpinFXBlock {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3067055375662565795L;

	// used to sort the blocks, assigned during addBlock
	int blockNum = -1;

	// these values are to be used for register addresses?
	int nInputs = 0;
	int nOutputs = 0;
	int nControlInputs = 0;
	int nControlOutputs = 0;

	public ArrayList<SpinCADPin> pinList = new ArrayList<SpinCADPin>();

	// position and size of the enclosing rectangle
	int x_pos = 0;
	int y_pos = 0;
	int width = 80;
	int height = 40;

	private String name = null;
	spinCADControlPanel scCP = null;

	Color borderColor = Color.green;

	public SpinCADBlock(int x, int y) {
		super("SpinCADBLock");
		CADBlockInit(x, y);
		setNumBlocks(getNumBlocks() + 1);
	}

	public SpinCADBlock(int x, int y, Color border, Color connector) {
		super("SpinCADBLock");
		CADBlockInit(x, y);
		borderColor = border;
	}

	private void CADBlockInit(int x, int y) {
		nInputs = 0;
		nOutputs = 0;
		nControlInputs = 0;
		nControlOutputs = 0;
		x_pos = x;
		y_pos = y;	
		//		setLocation(x,y);
	}

	/**
	 * Add an audio input pin with an auto-generated name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 */

	public void addInputPin(SpinCADBlock b) {
		nInputs++;
		SpinCADPin pin = new SpinCADPin(b, "Audio Input " + nInputs, pinType.AUDIO_IN, nInputs * 20, 0);
		pinList.add(pin);
	}

	/**
	 * Add an audio input pin with specified name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 * @param s - the given pin name (a String)
	 */

	public void addInputPin(SpinCADBlock b, String s) {
		nInputs++;
		SpinCADPin pin = new SpinCADPin(b, s, pinType.AUDIO_IN, nInputs * 20, 0);
		pinList.add(pin);
	}

	/**
	 * Add an audio output pin with an auto-generated name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 */

	public void addOutputPin(SpinCADBlock b) {
		nOutputs++;
		SpinCADPin pin = new SpinCADPin(b, "Audio Output " + nOutputs, pinType.AUDIO_OUT, nOutputs * 20, height);
		pinList.add(pin);
	}

	/**
	 * Add an audio output pin with specified name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 * @param s - the given pin name (a String)
	 */

	public void addOutputPin(SpinCADBlock b, String s) {
		nOutputs++;
		SpinCADPin pin = new SpinCADPin(b, s, pinType.AUDIO_OUT, nOutputs * 20, height);
		pinList.add(pin);
	}

	/**
	 * Add a control input pin with an auto-generated name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 */

	public void addControlInputPin(SpinCADBlock b) {
		nControlInputs++;
		SpinCADPin pin = new SpinCADPin(b, "Control Input " + nControlInputs, pinType.CONTROL_IN, 0, nControlInputs * 10);
		pinList.add(pin);
	}

	/**
	 * Add a control input pin with the specified name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 * @param s - the given pin name (a String)
	 */

	public void addControlInputPin(SpinCADBlock b, String s) {
		nControlInputs++;
		SpinCADPin pin = new SpinCADPin(b, s, pinType.CONTROL_IN, 0, nControlInputs * 10);
		pinList.add(pin);
	}

	/**
	 * Add a control output pin with an auto-generated name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 */

	public void addControlOutputPin(SpinCADBlock b) {
		nControlOutputs++;
		SpinCADPin pin = new SpinCADPin(b, "Control Output " + nControlOutputs, pinType.CONTROL_OUT, width, nControlOutputs * 10);
		pinList.add(pin);
	}

	/**
	 * Add a control output pin with the specified name
	 * 
	 * @param b - SpinCADBlock reference to the desired block 
	 */

	public void addControlOutputPin(SpinCADBlock b, String s) {
		nControlOutputs++;
		SpinCADPin pin = new SpinCADPin(b, s, pinType.CONTROL_OUT, width, nControlOutputs * 10);
		pinList.add(pin);
	}

	/**
	 * Returns a handle to a pin with the specified name
	 * 
	 * @param name - the given pin name (a String)
	 * @return Pin - SpinCADPin - handle to the found pin or null if not found
	 */

	public SpinCADPin getPin(String name) {
		SpinCADPin nPin = new SpinCADPin(null, null, null, -1, -1);
		Iterator<SpinCADPin> itr = pinList.iterator();

		while(itr.hasNext()) {
			nPin = itr.next();
			String s = nPin.getName(); 
			if(s.contains(name)) {
				return nPin;
			}
		}
		System.out.println("Couldn't find " + name);
		return null;		
	}

	/**
	 * Sets the default size for all CAD Block rectangles
	 * 
	 * @param g2 - graphics context we be talking about
	 */

	public void sizeRect(Graphics2D g2) {
		Font f = new Font("Serif", Font.BOLD, 12);
		g2.setFont(f);
		//		FontMetrics metrics = g2.getFontMetrics(f);
		//		int adv = metrics.stringWidth(name);

		// width = (Math.max(nInputs, nOutputs) + 1) * 15  + adv;
		// height = (Math.max(nControlInputs, nControlOutputs) + 3) * 15;
		width = 80;
		height = 40;
	}

	/**
	 * Draws the CAD Block rectangle with the name inside
	 * 
	 * @param g2 - graphics context we be talking about
	 */

	public void drawRect(Graphics2D g2) {

		sizeRect(g2);
		//		Iterator<SpinCADPin> itr = pinList.iterator();
		//		SpinCADPin nPin = new SpinCADPin(null, null, -1, -1, null);
		//		while(itr.hasNext()) {
		//			nPin = itr.next();
		//			nPin.drawPin(g2);
		//		}

		//		Rectangle2D rectHandle = new Rectangle2D.Double(x_pos - 4 , y_pos - 4, 8, 8);
		//		g2.setColor(Color.BLACK);
		//		g2.setStroke(new BasicStroke(2));
		//		g2.draw(rectHandle);

		RoundRectangle2D rect = new RoundRectangle2D.Double(x_pos + 3, y_pos + 5, width - 6, height - 10, 5, 5);
		g2.setColor(borderColor);
		g2.setStroke(new BasicStroke(4));
		g2.draw(rect);
		GradientPaint redtowhite = new GradientPaint(x_pos, y_pos,borderColor, x_pos + width, y_pos + height, Color.WHITE);
		g2.setPaint(redtowhite);
		g2.fill(rect);

		Point pt = new Point(0,0);
		pt.setLocation(rect.getMinX() + 5, 5 + (rect.getMaxY() + rect.getMinY())/2);
		//		System.out.println("Text:" + pt.getX() + pt.getY());
		g2.setColor(Color.BLACK);
		g2.drawString(name,  (int)pt.getX(), (int)pt.getY());
	}

	/**
	 * Returns the X,Y coordinates of the current block's given pin
	 * 
	 * @param p - SpinCADPin in question
	 * @return Point
	 */

	public Point getPinXY(SpinCADPin p) {
		Point pt = new Point();
		pt.setLocation(getX() + p.getX(), getY() + p.getY());
		return pt;
	}

	/**
	 * Returns the X,y associated with the current block's origin
	 * 
	 * @return Point
	 */
	public Point getXY() {
		Point pt = new Point();
		pt.setLocation(getX(), getY());
		return pt;
	}

	public void generateCode(SpinFXBlock sfxb) {
		System.out.println("Empty!!!!");
	}

	public void editBlock() {
		// this is over ridden for each actual block
		System.out.println("Unimplemented edit block for " + name);
	}	
	public void setBlockNum(int number) {
		blockNum = number;
	}

	public int getBlockNum() {
		return blockNum;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void setName(String n) {
		name = n;
	}

	public String getName() {
		return name;
	}

	public int getX() {
		return x_pos;
	}

	public void setX(int x) {
		x_pos = x;
	}

	public int getY() {
		return y_pos;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color c) {
		borderColor = c;
	}
}

