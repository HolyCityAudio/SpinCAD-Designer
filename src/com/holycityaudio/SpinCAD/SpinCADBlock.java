/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADBlock.java
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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

public class SpinCADBlock extends SpinFXBlock {

	/**
	 * SpinCADBlock class extends the idea of a functional block
	 * to a graphical idea with inputs and outputs
	 * 
	 * @param val the value from -0x800000 to 0x7fffff
	 * @return the double from -1.0 to +0.999...
	 */

	private static final long serialVersionUID = -3067055375662565795L;

	// used to sort the blocks, assigned during addBlock
	int blockNum = -1;

	// these values are to be used for register addresses?
	int nInputs = 0;
	int nOutputs = 0;
	int nControlInputs = 0;
	int nControlOutputs = 0;
	
	// index currently used for resolving feedback loops
	int index = 1;

	public ArrayList<SpinCADPin> pinList = new ArrayList<SpinCADPin>();

	// position and size of the enclosing rectangle
	int x_pos = 0;
	int y_pos = 0;
	int width;
	int height = 40;

	private String name = null;
	spinCADControlPanel scCP = null;
	protected boolean hasControlPanel = false;	// used to determine whether to offer a control panel

	Color borderColor = Color.green;

	/**
	 * SpinCADBlock class extends the idea of a functional block
	 * to a graphical idea with inputs and outputs
	 * 
	 * @param x the x-location on the screen of the block's upper left corner
	 * @param y the y-location on the screen of the block's upper left corner
	 */

	public SpinCADBlock(int x, int y) {
		super("SpinCADBlock");
		CADBlockInit(x, y);
//		setNumBlocks(getNumBlocks() + 1);
	}

	/**
	 * SpinCADBlock constructor
	 * 
	 * @param x the x-location on the screen of the block's upper left corner
	 * @param y the y-location on the screen of the block's upper left corner
	 * @param border color of the border
	 * @param connector - color f the little connector circles
	 * 
	 */

	public SpinCADBlock(int x, int y, Color border, Color connector) {
		super("SpinCADBlock");
		CADBlockInit(x, y);
		borderColor = border;
	}

	/**
	 * CADBlockInit
	 * Initializes a generic block
	 *  
	 * @param x the x-location on the screen of the block's upper left corner
	 * @param y the y-location on the screen of the block's upper left corner
	 * 
	 */

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
	
	public void removeAllControlInputs() {
		nControlInputs = 0;
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
		FontMetrics metrics = g2.getFontMetrics(f);
		int adv = metrics.stringWidth(name);

		// width = (Math.max(nInputs, nOutputs) + 1) * 15  + adv;
		// height = (Math.max(nControlInputs, nControlOutputs) + 3) * 15;
		width = adv + 20;
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
		if (hasControlPanel == true) {
			g2.setColor(Color.WHITE);
			g2.setStroke(new BasicStroke(6));
		} else {
			g2.setColor(borderColor);
			g2.setStroke(new BasicStroke(4));			
		}
		
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
	 * Returns the x,y associated with the current block's origin
	 * 
	 * @return Point
	 */
	public Point getXY() {
		Point pt = new Point();
		pt.setLocation(getX(), getY());
		return pt;
	}

	/**
	 * Template generateCode() method for all blocks
	 * Every real block needs to override this.
	 * 
	 */
	public void generateCode(SpinFXBlock sfxb) {
		System.out.println("Empty!!!!");
	}

	/**
	 * Template editBlock() method for all blocks.
	 * Every real block with a control panel needs to override this.
	 * 
	 */
	public void editBlock() {
		// this is over ridden for each actual block
		System.out.println("Unimplemented edit block for " + name);
	}	

	/**
	 * Set the current block's number (order within the model)
	 * @param number the index to give to the current block being created.
	 */
	public void setBlockNum(int number) {
		blockNum = number;
	}

	/**
	 * Get the current block's number (order within the model)
	 * @return number the index of the current block
	 * 
	 */
	public int getBlockNum() {
		return blockNum;
	}

	/**
	 * Get the current block's name
	 * @return n the current block's name
	 * 
	 */
	public void setName(String n) {
		name = n;
	}

	/**
	 * @return the name of the current block
	 * 
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the x position of the current block (Integer)
	 * 
	 */

	public int getX() {
		return x_pos;
	}

	/**
	 * @return the y position of the current block (Integer)
	 * 
	 */

	public int getY() {
		return y_pos;
	}
	/**
	 * @return the x position of the current block (Integer)
	 * 
	 */

	public void setIndex(int val) {
		index = val;
	}

	/**
	 * @return the y position of the current block (Integer)
	 * 
	 */

	public int getIndex() {
		return index;
	}

	/**
	 * @param c Set the Color of the current block's border
	 * 
	 */
	
	public void setBorderColor(Color c) {
		borderColor = c;
	}
	
	public boolean hasControlPanel() {
		return hasControlPanel;
	}
	// below are functions to translate parameters between CADBlocks and control panels
	
	public double freqToFilt(double freq) {
		return (Math.sin(2 * Math.PI * freq/getSamplerate()));
	}
	
	public double filtToFreq(double filt) {
		return getSamplerate() * Math.asin(filt)/(2 * Math.PI);
	}
	
	public int logvalToSlider(double value, double multiplier) {
		return (int) (multiplier * Math.log10(value));
	}
	
	public double sliderToLogval(int pos, double multiplier) {
		return Math.pow(10.0, pos/multiplier);
	}
}

