/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADPanel.java
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
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import java.util.Iterator;

// =======================================================================================================
public class SpinCADPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private enum dragModes { NODRAG, DRAGMOVE, CONNECT };

	private SpinCADFrame f = null;
	// following 4 variables are for pin to pin connections
	private SpinCADBlock startBlock = null;
	private SpinCADBlock stopBlock = null;

	private Point startPoint;
	private Point stopPoint;
	private Point mouseAt;

	private SpinCADPin startPin;
	private SpinCADPin stopPin;

	public dragModes dm = dragModes.NODRAG;

	private Line2D dragLine = null;
	/*	
	public SpinCADPanel () {

	}
	 */
	public SpinCADPanel (final SpinCADFrame spdFrame) {
		f = spdFrame;

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseAt = e.getPoint();
				if(dm == dragModes.DRAGMOVE) {
					spdFrame.getModel();
					//					System.out.printf("Edit mode 3, drag mode 1, X: %d Y: %d\n", e.getX(), e.getY());
					moveBlock(SpinCADModel.getCurrentBlock(), (int) mouseAt.getX(), (int) mouseAt.getY());
					repaint();
				}
				else if(dm == dragModes.CONNECT) {
					//					System.out.printf("Edit mode 4, drag mode 2, X: %d y: %d\n", e.getX(), e.getY());
					stopPoint = getNearbyPoint();
					int startX = startBlock.getX() + startPin.getX();
					int startY = startBlock.getY() + startPin.getY();
					if (stopPoint != null)
						dragLine = new Line2D.Double(startX, startY, stopPoint.getX(), stopPoint.getY());
					else
						dragLine = new Line2D.Double(startX, startY, mouseAt.getX(), mouseAt.getY());
					repaint();
				}
				else if(dm == dragModes.NODRAG) {
					//					System.out.printf("Edit mode 4, drag mode 2, X: %d y: %d\n", e.getX(), e.getY());
					startPoint = getNearbyPoint();
					repaint();
				}
				Point point = getNearbyPoint();
				if(point != null) {		
					// we're near a pin, so iterate through the model and see which one it is
					// then show the pin name
					SpinCADBlock b = null;	
					Iterator<SpinCADBlock> itr = spdFrame.getModel().blockList.iterator();
					while(itr.hasNext()) {
						b = itr.next();
						Iterator<SpinCADPin> itrPin = b.pinList.iterator();
						SpinCADPin currentPin = null;
						while(itrPin.hasNext()) {
							currentPin = itrPin.next();
							// hit a block pin, so connect it
							// bug here somewhere
							if(hitPin(e, b, currentPin)) {
								f.etb.pinName.setText(currentPin.getName());
								return;
							}
						}
					}
				}
				else {
					// TODO this is supposed to allow deletion of a connection.
					// start looking for lines, if in delete mode
					// however for now, right-clicking on the destination pin is OK
					SpinCADBlock b = null;	
					Iterator<SpinCADBlock> itr = spdFrame.getModel().blockList.iterator();
					while(itr.hasNext()) {
						b = itr.next();
						Iterator<SpinCADPin> itrPin = b.pinList.iterator();
						SpinCADPin currentPin = null;
						while(itrPin.hasNext()) {
							currentPin = itrPin.next();
							// we're now iterating through each pin on each block.
							// see if a pin has a connector block (previous)
							// if so, get the coordinates of the end points and
							// distance of mouse point from the line.
							SpinCADPin otherPin = currentPin.getPinConnection();
							if(otherPin != null) {
								// TODO use getPinXY() here
								int x1 = currentPin.getX();
								int y1 = currentPin.getY();
								int x2 = otherPin.getX();
								int y2 = otherPin.getY();
								if(x2 != x1) {
									double slope = (double) (y2 - y1)/(x2 - x1);
									// System.out.println(slope);
								} else {

								}

							}
						}
					}
					f.etb.pinName.setText("");
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				// drop a line or block if you were dragging it
				if(dm == dragModes.DRAGMOVE) {
					spdFrame.getModel().setChanged(true);
					dm = dragModes.NODRAG;
					dragLine = null;
					return;
				}
				if(arg0.getButton() == 3) {
					if(dm == dragModes.CONNECT) {
						dm = dragModes.NODRAG;
						dragLine = null;
					}
					else {
						Point point = getNearbyPoint();
						if(point != null) {
							SpinCADBlock b = null;	
							Iterator<SpinCADBlock> itr = spdFrame.getModel().blockList.iterator();
							while(itr.hasNext()) {
								b = itr.next();
								Iterator<SpinCADPin> itrPin = b.pinList.iterator();
								SpinCADPin currentPin = null;
								while(itrPin.hasNext()) {
									currentPin = itrPin.next();
									// hit a block pin, so connect it
									// bug here somewhere
									if(hitPin(arg0, b, currentPin)) {
										currentPin.deletePinConnection();
										f.getModel().setChanged(true);
										f.updateFrameTitle();
										return;
									}
								}
							}
						}
					}
				}
				SpinCADBlock b = null;	
				Iterator<SpinCADBlock> itr = spdFrame.getModel().blockList.iterator();
				while(itr.hasNext()) {
					b = itr.next();
					// if we hit the block, we can either delete or drag it
					if (hitTarget(arg0, b) == true) {
						// System.out.println("Direct hit!");
						switch(dm) {
						case NODRAG:
							if(arg0.getButton() == 1) {	// left button
								spdFrame.getModel();
								SpinCADModel.setCurrentBlock(b);
								dm = dragModes.DRAGMOVE;
							}
							else if (arg0.getButton() == 3)	{	// right button
								doPop(arg0, b);
							}
						default:
							break;
						}
						repaint();
						return;
					}
					else {
						Iterator<SpinCADPin> itrPin = b.pinList.iterator();
						SpinCADPin currentPin = null;
						while(itrPin.hasNext()) {
							currentPin = itrPin.next();
							// hit a block pin, so connect it
							// bug here somewhere
							if(hitPin(arg0, b, currentPin)) {
								if(dm != dragModes.CONNECT) {
									System.out.println("Connect start!");
									dm = dragModes.CONNECT;	// now we're going to connect a wire
									startBlock = b;
									startPin = currentPin;
								}
								else {		// we already were dragging a wire, now place it
									if(startPin.isOutputPin() && currentPin.isInputPin()) {
										stopBlock = b;
										if(startBlock != stopBlock) {
											stopPin = currentPin;
											stopPin.setConnection(startBlock,  startPin);		
											// XXX debug set pin connections in both directions don't think this works
											// startPin.setConnection(stopBlock,  stopPin);
											System.out.println("Connect stop!");
											dm = dragModes.NODRAG;
											dragLine = null;
											startBlock = null;
											spdFrame.getModel().setChanged(true);
											spdFrame.getResourceToolbar().update();	// recalculate model resources toolbar
											f.updateFrameTitle();
											repaint();
										}
									}
									else if (startPin.isInputPin() && currentPin.isOutputPin()) {
										stopBlock = b;
										if(startBlock != stopBlock) {
											stopPin = currentPin;
											// XXX debug set pin connections in both directions don't think this works
											startPin.setConnection(stopBlock,  stopPin);	
											// stopPin.setConnection(startBlock,  startPin);		
											System.out.println("Connect stop!");
											dm = dragModes.NODRAG;
											dragLine = null;
											startBlock = null;
											spdFrame.getModel().setChanged(true);
											f.updateFrameTitle();
											spdFrame.getResourceToolbar().update();	// recalculate model resources toolbar
											repaint();
										}
									}
								}
								return;
							}
						}
					}
				}
			}
		});  
	}
	//=======================================================================================================
	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g; 
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		SpinCADBlock block;
		Iterator<SpinCADBlock> itr = f.getModel().blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			//			System.out.printf("X = %d y = %d\n", block.getX(), block.getY() );
			block.drawRect(g2);	   
			Iterator<SpinCADPin> itrPin = block.pinList.iterator();
			SpinCADPin currentPin;
			while(itrPin.hasNext()) {
				currentPin = itrPin.next();
				currentPin.setColor(Color.BLACK);
				if(currentPin.isControlOutputPin()) {
					currentPin.setX(block.width);		// deal with variable block width, only affects control outputs			
				}
				currentPin.paintComponent(g);								

				if(currentPin.getPinConnection() != null && currentPin.getBlockConnection() != null) {
					Point startPt = null;
					Point stopPt = null;
					//						System.out.println("Draw connection!");
					stopPin = currentPin.getPinConnection();
					stopBlock = currentPin.getBlockConnection();
					stopPt = stopBlock.getPinXY(stopPin);
					startPt = block.getPinXY(currentPin);					
					Line2D line = new Line2D.Double(startPt, stopPt);
					if(currentPin.getName().contains("Control")) {
						g2.setColor(Color.BLUE);						
					}
					else
						g2.setColor(Color.black);
					g2.setStroke(new BasicStroke(2));
					g2.draw(line);	
					currentPin = null;
				}
			}
		}		 
		if(dragLine != null) {
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.CYAN);
			g2.draw(dragLine);
		}
	}

	private static int RANGE = 5;

	protected Point getNearbyPoint() {
		SpinCADBlock block;
		Iterator<SpinCADBlock> itr = f.getModel().blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			Iterator<SpinCADPin> itrPin = block.pinList.iterator();
			SpinCADPin currentPin;
			while(itrPin.hasNext()) {
				currentPin = itrPin.next();
				if (Math.abs(block.getX() + currentPin.getX() - mouseAt.x) <= RANGE
						&& Math.abs(block.getY() + currentPin.getY() - mouseAt.y) <= RANGE) {
					Point point = new Point(block.getX() + currentPin.getX(), block.getY() + currentPin.getY()); 
					mouseAt = point;
					repaint();
					return point;
				}
			}
		}
		return null; 
	}

	public void deleteBlockConnection(SpinCADBlock b) {
		SpinCADBlock block;
		Iterator<SpinCADBlock> itr = f.getModel().blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			Iterator<SpinCADPin> itrPin = block.pinList.iterator();
			SpinCADPin currentPin;
			while(itrPin.hasNext()) {
				currentPin = itrPin.next();
				if(currentPin.getBlockConnection() == b) {
					currentPin.setConnection(null, null);
				}
			}
		}
	}

	public void moveBlock(SpinCADBlock block, int x, int y) {
		int OFFSET = 1;
		if ((block.x_pos !=x) || (block.y_pos !=y)) {
			repaint(block.x_pos,block.y_pos,block.width+OFFSET,block.height+OFFSET);
			block.x_pos=x - block.width/2;
			block.y_pos=y - block.height/2;
			repaint(block.x_pos,block.y_pos,block.width+OFFSET,block.height+OFFSET);
		} 
	}


	private boolean hitTarget(MouseEvent arg0, SpinCADBlock block) {

		//		System.out.printf("arg0.getX()= %d block.getX() = %d block.getWidth() = %d\n", arg0.getX(), block.getX(), block.getWidth());
		int blockCenterX = block.getX() + block.width/2;
		int blockCenterY = block.getY() + block.height/2;

		int deltaX = Math.abs(arg0.getX() - blockCenterX);
		int deltaY = Math.abs(arg0.getY() - blockCenterY);
		//		System.out.printf("deltaX = %d deltaY = %d\n", deltaX, deltaY);
		if(deltaX < (3 * block.width/8) && deltaY < (3 * block.height/8)) {
			return true;
		}
		else {
			return false;
		}
	}

	private boolean hitPin(MouseEvent arg0, SpinCADBlock b, SpinCADPin p) {

		Point pt = b.getPinXY(p);
		//		System.out.printf("arg0.getX()= %d block.getX() = %d block.getWidth() = %d\n", arg0.getX(), block.getX(), block.getWidth());
		int deltaX = Math.abs(arg0.getX() - (int) pt.getX());
		int deltaY = Math.abs(arg0.getY() - (int) pt.getY());
		//		System.out.printf("deltaX = %d deltaY = %d\n", deltaX, deltaY);
		if(deltaX < RANGE && deltaY < RANGE ) {
			return true;
		}
		else {
			return false;
		}
	}

	public void setDragMode(dragModes mode) {
		dm = mode;
	}

	public dragModes getDragMode () {
		return dm;
	}

	public void setDragModeDragMove() {
		dm = dragModes.DRAGMOVE;
	}

	// popup menu handling
	class PopUpDemo extends JPopupMenu {
		JMenuItem cPanel;
		JMenuItem del;
		public PopUpDemo(SpinCADBlock b){
			if(b.hasControlPanel()) {
				cPanel = new JMenuItem("Control Panel");
				add(cPanel);
				cPanel.addActionListener(new MenuActionListener(b));				
			}
			del = new JMenuItem("Delete");
			add(del);
			del.addActionListener(new MenuActionListener(b));
		}
	}

	private void doPop(MouseEvent e, SpinCADBlock b){
		PopUpDemo menu = new PopUpDemo(b);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	class MenuActionListener implements ActionListener {
		SpinCADBlock spcb = null;
		JFrame frame = null;

		public MenuActionListener(SpinCADBlock b) {
			spcb = b;
		}

		public void actionPerformed(ActionEvent e) {
			switch(e.getActionCommand()) {
			case "Control Panel":
				spcb.editBlock();
				break;
			case "Delete":
				// do a model save just before delete
				f.saveModel();
				deleteBlockConnection(spcb);
				f.getModel().blockList.remove(spcb);
				f.getModel().setChanged(true);
				f.getResourceToolbar().update();
				repaint();
				break;
			default: 
				break;
			}
			//			System.out.println("Selected: " + e.getActionCommand());
		}
	}
}


