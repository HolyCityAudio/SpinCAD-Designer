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

import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.holycityaudio.SpinCAD.SpinCADPin.pinType;

import java.util.Iterator;

// =======================================================================================================
public class SpinCADPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private enum dragModes { NODRAG, DRAGMOVE, CONNECT, DRAGBOX, SELECTED };

	private SpinCADFrame f = null;
	// following 4 variables are for pin to pin connections
	private SpinCADBlock startBlock = null;
	private SpinCADBlock stopBlock = null;

	private Point startPoint;
	private Point stopPoint;
	private Point mouseAt;
	private Point lastMouse = null;

	private SpinCADPin startPin;
	private SpinCADPin stopPin;

	public dragModes dm = dragModes.NODRAG;

	private Line2D dragLine = null;
	private Rectangle2D dragRect = null;
	private static String keys = null;

	public SpinCADPanel (final SpinCADFrame spdFrame) {
		f = spdFrame;

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseAt = e.getPoint();
				// in mode DRAGMOVE, we have selected one or more blocks and are dragging them to a new location.
				if(dm == dragModes.DRAGMOVE) {
					spdFrame.getModel();
					SpinCADBlock b = null;	
					Iterator<SpinCADBlock> itr = spdFrame.getModel().blockList.iterator();
					
					if(lastMouse == null) {
						lastMouse = mouseAt;
					}

					while(itr.hasNext()) {
						b = itr.next();
						if(b.selected == true)
							moveBlock(b, b.getX() + (int) (mouseAt.getX() - lastMouse.getX()), b.getY() + (int) (mouseAt.getY() - lastMouse.getY()));
					}
					lastMouse = mouseAt;
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
				else if(dm == dragModes.DRAGBOX) {
					// draw a rectangle
					double ulhx = Math.min(startPoint.getX(), mouseAt.getX());
					double ulhy = Math.min(startPoint.getY(), mouseAt.getY());
					double widthX = Math.abs(mouseAt.getX() - startPoint.getX());
					double widthY = Math.abs(mouseAt.getY() - startPoint.getY());
					dragRect = new Rectangle2D.Double(ulhx, ulhy, widthX, widthY);
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
							if(hitPin(e, b, currentPin)) {
								f.etb.pinName.setText(currentPin.getName());
								return;
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
				boolean hitSomething = false;
				// drop a line or block if you were dragging it
				if(dm == dragModes.DRAGMOVE) {
					spdFrame.getModel().setChanged(true);
					unselectAll(spdFrame);
					dm = dragModes.NODRAG;
					dragLine = null;
					repaint();
					return;
				}
				else if (dm == dragModes.DRAGBOX) {
					if(arg0.getButton() == 1) {
						dm = dragModes.NODRAG;
						selectGroup(spdFrame, startPoint, mouseAt);
					} 
					else if (arg0.getButton() == 3) {
						dm = dragModes.NODRAG;
					}
					dragRect = null;
					repaint();
					return;
				}
				if(arg0.getButton() == 3) {		// right mouse button
					if(dm == dragModes.CONNECT) {
						dm = dragModes.NODRAG;
						dragLine = null;
					}
					else {
						// right clicked on pin, look to see if hit on/near pin and if so, delete connection.
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
						hitSomething = true;
						switch(dm) {
						case NODRAG:
							if(arg0.getButton() == 1) {	// left button
								int mode = arg0.getModifiers();
								if((mode & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
									if(b.selected == false) {
										b.selected = true;
									}
									else {
										b.selected = false;
									}
									repaint();
								}
								else {
									spdFrame.getModel();
									SpinCADModel.setCurrentBlock(b);
									b.selected = true;
									dm = dragModes.DRAGMOVE;	
									lastMouse = mouseAt;
								}
							}
							else if (arg0.getButton() == 3)	{	// right button
								if(areAnySelected(spdFrame) == true) {
									if (b.selected == false) {
										unselectAll(spdFrame);
										b.selected = true;
									}
								}
								else {
									b.selected = true;
								}
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
								hitSomething = true;
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
											// decrement count of pin which was connected
											SpinCADPin p = currentPin.getPinConnection();
											if(p != null) {
												currentPin.deletePinConnection();
											}
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
											return;
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
											return;
										}
									}
								}
							}
						}
					}
				}
				if((dm != dragModes.CONNECT) && (hitSomething == false)) {
					if(arg0.getButton() == 1) {
						dragLine = null;  // in case you were dragging a line at the time.
						dm = dragModes.DRAGBOX;	
						startPoint = mouseAt;
					}
					else if (arg0.getButton() == 3) {
						dm = dragModes.NODRAG;	
					}
				}
			}
		});  
	}

	//=======================================================================================================
	public void syncMouse() {
		lastMouse = mouseAt;
	}

	int getMouseX() {
		return (int) mouseAt.getX();
	}

	int getMouseY() {
		return (int) mouseAt.getY();
	}
	
	private void putMouseOnBlock(SpinCADBlock b) {
		Point p = new Point();
		p.setLocation(b.getX() + b.width/2, b.getY() + b.height/2);
		SwingUtilities.convertPointToScreen(p, this);
		moveMouse(p);
	}

	private void moveMouse(Point p) {
		GraphicsEnvironment ge = 
				GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		// Search the devices for the one that draws the specified point.
		for (GraphicsDevice device: gs) { 
			GraphicsConfiguration[] configurations =
					device.getConfigurations();
			for (GraphicsConfiguration config: configurations) {
				Rectangle bounds = config.getBounds();
				if(bounds.contains(p)) {
					// Set point to screen coordinates.
					Point b = bounds.getLocation(); 
					Point s = new Point(p.x - b.x, p.y - b.y);

					try {
						Robot r = new Robot(device);
						r.mouseMove(s.x, s.y);
					} catch (AWTException e) {
						e.printStackTrace();
					}

					return;
				}
			}
		}
		// Couldn't move to the point, it may be off screen.
		return;
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
		if(dragRect != null) {
			g2.setStroke(new BasicStroke(1));
			g2.setColor(Color.BLUE);
			g2.draw(dragRect);
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
		// b is the block to delete
		// iterate through each block in the model
		while(itr.hasNext()) {
			block = itr.next();
			Iterator<SpinCADPin> itrPin = block.pinList.iterator();
			SpinCADPin currentPin;
			// iterate through each pin in each block
			while(itrPin.hasNext()) {
				currentPin = itrPin.next();
				// if the current pin's block connection is this block
				// then we should delete it
				if(currentPin.getBlockConnection() == b) {
					currentPin.deletePinConnection();
				}
			}
		}
	}

	public void moveBlock(SpinCADBlock block, int x, int y) {
		int OFFSET = 1;
		if ((block.x_pos !=x) || (block.y_pos !=y)) {
			block.x_pos=x;
			block.y_pos=y;
			repaint(block.x_pos,block.y_pos,block.width+OFFSET,block.height+OFFSET);
		} 
	}

	private boolean hitTarget(MouseEvent arg0, SpinCADBlock block) {

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
		int deltaX = Math.abs(arg0.getX() - (int) pt.getX());
		int deltaY = Math.abs(arg0.getY() - (int) pt.getY());
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

	public boolean selectGroup(SpinCADFrame fr, Point start, Point end) {
		SpinCADBlock block;
		boolean retval = false;
		double x1 = Math.min(start.getX(), end.getX());
		double x2 = Math.max(start.getX(), end.getX());
		double y1 = Math.min(start.getY(), end.getY());
		double y2 = Math.max(start.getY(), end.getY());
		double targetX, targetY;

		Iterator<SpinCADBlock> itr = fr.getModel().blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			targetX = block.x_pos + block.width/2;
			targetY = block.y_pos + block.height/2;

			if(targetX >= x1 && targetX <= x2 && targetY >= y1 && targetY <= y2) {
				block.selected = true;
				retval = true;
			}
			else {
				block.selected = false;				
			}
		}
		return retval;
	}

	public boolean areAnySelected(SpinCADFrame fr) {
		SpinCADBlock block;
		boolean retval = false;

		Iterator<SpinCADBlock> itr = fr.getModel().blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			if(block.selected == true) {
				retval = true;				
			}
		}
		return retval;
	}

	public void unselectAll(SpinCADFrame fr) {
		SpinCADBlock block;

		Iterator<SpinCADBlock> itr = fr.getModel().blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			block.selected = false;				
		}
	}
	
	public void dropBlockPanel(SpinCADBlock b) {
		b.selected = true;
		b.x_pos = 100;
		b.y_pos = 100;
		lastMouse = new Point(100 + 20,100 + b.height/2);
		putMouseOnBlock(b);

		setDragModeDragMove();
		repaint();
	}

	// popup menu handling
	class PopUpDemo extends JPopupMenu {
		JMenuItem cPanel;
		JMenuItem mov;
		JMenuItem del;

		public PopUpDemo(SpinCADBlock b){
			if(b.hasControlPanel()) {
				cPanel = new JMenuItem("Control Panel");
				add(cPanel);
				cPanel.addActionListener(new MenuActionListener(b));				
			}
			mov = new JMenuItem("Move");
			add(mov);
			mov.addActionListener(new MenuActionListener(b));

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
				unselectAll(f);
				spcb.editBlock();
				break;
			case "Move":
				syncMouse();
				setDragMode(dragModes.DRAGMOVE);
				break;
			case "Delete":
				// do a model save just before delete
				f.saveModel();
				SpinCADBlock block;

				Iterator<SpinCADBlock> itr = f.getModel().blockList.iterator();
				while(itr.hasNext()) {
					block = itr.next();
					if(block.selected == true) {
						deleteBlockConnection(block);
						//
						//						f.getModel().blockList.remove(block);
						itr.remove();
					}
				}

				f.getModel().setChanged(true);
				f.updateFrameTitle();
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


