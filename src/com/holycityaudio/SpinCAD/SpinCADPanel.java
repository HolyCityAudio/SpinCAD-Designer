/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * SpinCADPanel.java
 * Copyright (C) 2013 - 2019 - Gary Worsham
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.ToolTipManager;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// =======================================================================================================
public class SpinCADPanel extends JPanel implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = 1L;
	public enum dragModes { NODRAG, DRAGMOVE, CONNECT, DRAGBOX, SELECTED };

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
	private JPanel drawingPane = null;

	//	private static String keys = null;

	//	private SpinCADModel pasteBuffer = new SpinCADModel();

	/**
	 * Check if connecting sourceBlock's output to destBlock's input would
	 * create a cycle. Returns true if destBlock already feeds into sourceBlock.
	 */
	private boolean wouldCreateCycle(SpinCADBlock sourceBlock, SpinCADBlock destBlock) {
		Set<SpinCADBlock> visited = new HashSet<>();
		return canReach(destBlock, sourceBlock, visited);
	}

	private boolean canReach(SpinCADBlock from, SpinCADBlock target, Set<SpinCADBlock> visited) {
		if (from == target) return true;
		if (!visited.add(from)) return false;
		for (SpinCADBlock b : f.getPatch().patchModel.blockList) {
			for (SpinCADPin pin : b.pinList) {
				if (pin.isInputPin() && pin.getBlockConnection() == from) {
					if (canReach(b, target, visited)) return true;
				}
			}
		}
		return false;
	}

	public SpinCADPanel (final SpinCADFrame spdFrame) {
		f = spdFrame;

		drawingPane = new DrawingPane();
		drawingPane.setPreferredSize(new Dimension(3840,2160));
		drawingPane.addMouseListener(this);
		drawingPane.addMouseMotionListener(this);

		ToolTipManager ttm = ToolTipManager.sharedInstance();
		ttm.setInitialDelay(0);
		ttm.setReshowDelay(0);

		JScrollPane scroller = new JScrollPane(drawingPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
				, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setLayout(new BorderLayout(0,0));
		add(scroller, BorderLayout.CENTER);
	}

	public class DrawingPane extends JPanel {

		//=======================================================================================================
		public void paintComponent( Graphics g ) {
			try {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D)g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				SpinCADBlock block;
				Iterator<SpinCADBlock> itr = f.getPatch().patchModel.blockList.iterator();
				while(itr.hasNext()) {
					block = itr.next();
					block.drawRect(g2);
					Iterator<SpinCADPin> itrPin = block.pinList.iterator();
					SpinCADPin currentPin;
					while(itrPin.hasNext()) {
						currentPin = itrPin.next();
						currentPin.setColor(Color.BLACK);
						if(currentPin.isControlOutputPin()) {
							currentPin.setX(block.width);
						}
						currentPin.paintComponent(g);

						if(currentPin.getPinConnection() != null && currentPin.getBlockConnection() != null) {
							Point startPt = null;
							Point stopPt = null;
							stopPin = currentPin.getPinConnection();
							stopBlock = currentPin.getBlockConnection();
							stopPt = stopBlock.getPinXY(stopPin);
							startPt = block.getPinXY(currentPin);
							Line2D line = new Line2D.Double(startPt, stopPt);
							if(currentPin.isMuted()) {
								g2.setColor(Color.RED);
							} else if(currentPin.getName().contains("Control")) {
								g2.setColor(Color.BLUE);
							} else {
								g2.setColor(Color.black);
							}
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
			} catch (Throwable e) {
				System.out.println("paintComponent CRASH: " + e);
				e.printStackTrace();
				SpinCADFrame.logCrash("paintComponent", e);
			}
		}
	}

	//=======================================================================================================
	public void syncMouse() {
		lastMouse = mouseAt;
	}

	public void nullMouse() {
		lastMouse = null;
	}

	int getMouseX() {
		return (int) mouseAt.getX();
	}

	int getMouseY() {
		return (int) mouseAt.getY();
	}

	void putMouseOnBlock(SpinCADBlock b) {
		Point p = new Point();
		p.setLocation(b.getX() + b.width/2, b.getY() + b.height/2);
		SwingUtilities.convertPointToScreen(p, this);
		moveMouse(p);
	}

	private void moveMouse(Point p) {
		try {
			Robot r = new Robot();
			r.mouseMove(p.x, p.y);
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}


	public void mouseDragged(MouseEvent e) {
		// don't bother with any mouse processing if this is a hex file
		if(f.getPatch().isHexFile) {
			return;
		}
		mouseAt = e.getPoint();
		if(dm == dragModes.DRAGBOX) {
			// draw a rectangle
			double ulhx = Math.min(startPoint.getX(), mouseAt.getX());
			double ulhy = Math.min(startPoint.getY(), mouseAt.getY());
			double widthX = Math.abs(mouseAt.getX() - startPoint.getX());
			double widthY = Math.abs(mouseAt.getY() - startPoint.getY());
			dragRect = new Rectangle2D.Double(ulhx, ulhy, widthX, widthY);
			repaint();
		}
	}

	public void mouseMoved(MouseEvent e) {
		// don't bother with any mouse processing if this is a hex file
		if(f.getPatch().isHexFile) {
			return;
		}
		mouseAt = e.getPoint();
		// in mode DRAGMOVE, we have selected one or more blocks and are dragging them to a new location.
		if(dm == dragModes.DRAGMOVE) {
			SpinCADBlock b = null;	
			Iterator<SpinCADBlock> itr = f.getPatch().patchModel.blockList.iterator();

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
		Point point = getNearbyPoint();
		if(point != null) {
			// we're near a pin, so iterate through the model and see which one it is
			// then show the pin name as a tooltip
			SpinCADBlock b = null;
			Iterator<SpinCADBlock> itr = f.getPatch().patchModel.blockList.iterator();
			while(itr.hasNext()) {
				b = itr.next();
				Iterator<SpinCADPin> itrPin = b.pinList.iterator();
				SpinCADPin currentPin = null;
				while(itrPin.hasNext()) {
					currentPin = itrPin.next();
					if(hitPin(e, b, currentPin)) {
						drawingPane.setToolTipText(currentPin.getName());
						return;
					}
				}
			}
		}
		drawingPane.setToolTipText(null);
	}
	
	private static int RANGE = 5;

	protected Point getNearbyPoint() {
		SpinCADBlock block;
		Iterator<SpinCADBlock> itr = f.getPatch().patchModel.blockList.iterator();
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

	public void setDragModeNoDrag() {
		dm = dragModes.NODRAG;
	}

	public boolean selectGroup(SpinCADFrame fr, Point start, Point end) {
		SpinCADBlock block;
		boolean retval = false;
		double x1 = Math.min(start.getX(), end.getX());
		double x2 = Math.max(start.getX(), end.getX());
		double y1 = Math.min(start.getY(), end.getY());
		double y2 = Math.max(start.getY(), end.getY());
		double targetX, targetY;

		Iterator<SpinCADBlock> itr = fr.getPatch().patchModel.blockList.iterator();
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

		Iterator<SpinCADBlock> itr = fr.getPatch().patchModel.blockList.iterator();
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

		Iterator<SpinCADBlock> itr = fr.getPatch().patchModel.blockList.iterator();
		while(itr.hasNext()) {
			block = itr.next();
			block.selected = false;				
		}
	}

	public void dropBlockPanel(SpinCADBlock b) {
		b.selected = true;
		if(b.x_pos == 0) {
			b.x_pos = 100;
		}
		if(b.y_pos == 0) {
			b.y_pos = 100;
		}
		lastMouse = new Point(b.x_pos + 20, b.y_pos + b.height/2);
		putMouseOnBlock(b);

		setDragModeDragMove();
		repaint();
	}

	class MenuActionListener implements ActionListener {
		SpinCADBlock spcb = null;
		JFrame frame = null;
		Iterator<SpinCADBlock> itr;

		public MenuActionListener(SpinCADBlock b) {
			spcb = b;
		}

		public void actionPerformed(ActionEvent e) {
			switch(e.getActionCommand()) {
			case "Control Panel":
				unselectAll(f);
				// If flagged as open, verify the window is actually still showing.
				if (spcb.controlPanelOpen) {
					if (spcb.controlPanelFrame != null && spcb.controlPanelFrame.isDisplayable()
							&& spcb.controlPanelFrame.isVisible()) {
						// Panel is still open — bring it to front
						spcb.controlPanelFrame.toFront();
						spcb.controlPanelFrame.repaint();
						break;
					}
					// Panel was closed or disposed — reset state
					spcb.controlPanelOpen = false;
					spcb.controlPanelFrame = null;
					spcb.clearCP();
				}
				// Open a new control panel
				spcb.controlPanelOpen = true;
				spcb.openControlPanel();
				// Position and attach cleanup listener via controlPanelFrame
				// (set in invokeLater by panels, so this must also be invokeLater)
				SwingUtilities.invokeLater(() -> {
					java.awt.Window cpf = spcb.controlPanelFrame;
					if (cpf != null && cpf.isDisplayable()) {
						spcb.positionControlPanel(cpf);
						cpf.addWindowListener(new WindowAdapter() {
							public void windowClosing(WindowEvent ev) {
								spcb.controlPanelOpen = false;
								spcb.controlPanelFrame = null;
								spcb.clearCP();
							}
							public void windowClosed(WindowEvent ev) {
								spcb.controlPanelOpen = false;
								spcb.controlPanelFrame = null;
								spcb.clearCP();
							}
						});
					}
				});
				break;
			case "Move":
				syncMouse();
				setDragMode(dragModes.DRAGMOVE);
				break;

			case "Delete":
				// do a model save just before delete
				f.delete();
				repaint();
				break;

			case "Copy":
				// save entire blockList
				f.saveModelToPasteBuffer();
				break;

			case "Paste":
				// do a model save just before Paste
				f.paste();
				repaint();
				break;

			default: 
				break;
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// don't bother with any mouse processing if this is a hex file
		if(f.getPatch().isHexFile) {
			return;
		}
		f.etb.statusMessage.setText("");
		boolean hitSomething = false;
		// drop a line or block if you were dragging it
		if(dm == dragModes.DRAGMOVE) {
			f.eeprom.changed = true;
			f.updateAll(true);
			unselectAll(f);
			dm = dragModes.NODRAG;
			dragLine = null;
			repaint();
			return;
		}
		else if (dm == dragModes.DRAGBOX) {
			if(arg0.getButton() == 1) {
				dm = dragModes.NODRAG;
				selectGroup(f, startPoint, mouseAt);
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
				// right clicked on pin, look to see if hit on/near pin
				Point point = getNearbyPoint();
				if(point != null) {
					SpinCADBlock b = null;
					Iterator<SpinCADBlock> itr = f.getPatch().patchModel.blockList.iterator();
					while(itr.hasNext()) {
						b = itr.next();
						Iterator<SpinCADPin> itrPin = b.pinList.iterator();
						SpinCADPin currentPin = null;
						while(itrPin.hasNext()) {
							currentPin = itrPin.next();
							if(hitPin(arg0, b, currentPin)) {
								if(currentPin.isInputPin()) {
									doPinPop(arg0, b, currentPin);
								}
								return;
							}
						}
					}
				}
			}
		}
		SpinCADBlock b = null;	
		Iterator<SpinCADBlock> itr = f.getPatch().patchModel.blockList.iterator();
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
							f.getPatch().patchModel.setCurrentBlock(b);
							b.selected = true;
							dm = dragModes.DRAGMOVE;	
							lastMouse = mouseAt;
						}
					}
					else if (arg0.getButton() == 3)	{	// right button
						if(areAnySelected(f) == true) {
							if (b.selected == false) {
								unselectAll(f);
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
									if (wouldCreateCycle(startBlock, b)) {
										JOptionPane.showMessageDialog(f,
											"Cannot create circular connection. Add a feedback loop instead.",
											"Circular Connection", JOptionPane.WARNING_MESSAGE);
										dm = dragModes.NODRAG;
										dragLine = null;
										startBlock = null;
										repaint();
										return;
									}
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
									f.getPatch().setChanged(true);
									f.updateAll();
									repaint();
									// made the connection, we can stop now
									return;
								}
							}
							else if (startPin.isInputPin() && currentPin.isOutputPin()) {
								stopBlock = b;
								if(startBlock != stopBlock) {
									if (wouldCreateCycle(b, startBlock)) {
										JOptionPane.showMessageDialog(f,
											"Cannot create circular connection. Add a feedback loop instead.",
											"Circular Connection", JOptionPane.WARNING_MESSAGE);
										dm = dragModes.NODRAG;
										dragLine = null;
										startBlock = null;
										repaint();
										return;
									}
									stopPin = currentPin;
									// XXX debug set pin connections in both directions don't think this works
									startPin.setConnection(stopBlock,  stopPin);
									// stopPin.setConnection(startBlock,  startPin);
									System.out.println("Connect stop!");
									dm = dragModes.NODRAG;
									dragLine = null;
									startBlock = null;
									f.getPatch().setChanged(true);
									f.updateAll();
									repaint();
									// made the connection, we can stop now
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
	
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// don't bother with any mouse processing if this is a hex file
		if(f.getPatch().isHexFile) {
			return;
		}
		if (dm == dragModes.DRAGBOX) {
			if(arg0.getButton() == 1) {
				dm = dragModes.NODRAG;
				selectGroup(f, startPoint, mouseAt);
			} 					
			dragLine = null;
			dragRect = null;
			repaint();
			return;
		}
	}
	
	private void doPop(MouseEvent e, SpinCADBlock b){
		PopUpMenu menu = new PopUpMenu(b);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}	
	
	private void doPinPop(MouseEvent e, SpinCADBlock b, SpinCADPin pin) {
		PinPopUpMenu menu = new PinPopUpMenu(b, pin);
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	class PinPopUpMenu extends JPopupMenu {
		private static final long serialVersionUID = 1L;

		public PinPopUpMenu(SpinCADBlock block, SpinCADPin pin) {
			JMenuItem deleteWire = new JMenuItem("Delete Wire");
			deleteWire.addActionListener(e -> {
				pin.deletePinConnection();
				pin.setMuted(false);
				f.getPatch().setChanged(true);
				if (f.simX.isSimRunning()) {
					f.etb.statusMessage.setText("Change will take place after simulator stops.");
				} else {
					f.updateFrameTitle();
				}
				repaint();
			});
			add(deleteWire);

			JCheckBoxMenuItem mutePin = new JCheckBoxMenuItem("Mute This Pin", pin.isMuted());
			mutePin.addActionListener(e -> {
				pin.setMuted(!pin.isMuted());
				if (f.simX.isSimRunning()) {
					f.etb.statusMessage.setText("Change will take place after simulator stops.");
				} else {
					f.updateAll(true);
				}
				repaint();
			});
			add(mutePin);
		}
	}

	// popup menu handling
	class PopUpMenu extends JPopupMenu {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3333357417403694456L;
		JMenuItem cPanel;
		JMenuItem mov;
		JMenuItem del;
		JMenuItem cpy;
		JMenuItem pst;

		public PopUpMenu(SpinCADBlock b){
			if(b.hasControlPanel()) {
				cPanel = new JMenuItem("Control Panel");
				add(cPanel);
				cPanel.addActionListener(new MenuActionListener(b));				
			}
			cpy = new JMenuItem("Copy");
			add(cpy);
			cpy.addActionListener(new MenuActionListener(b));

			pst = new JMenuItem("Paste");
			add(pst);
			pst.addActionListener(new MenuActionListener(b));

			mov = new JMenuItem("Move");
			add(mov);
			mov.addActionListener(new MenuActionListener(b));

			del = new JMenuItem("Delete");
			add(del);
			del.addActionListener(new MenuActionListener(b));
		}
	}
}