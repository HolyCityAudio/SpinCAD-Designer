/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * Copyright (C) 2015 - Gary Worsham
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
 */

package com.holycityaudio.SpinCAD;

import java.awt.event.MouseEvent;
import javax.swing.JSlider;

/**
 * JSlider subclass supporting Ctrl+drag for fine adjustment.
 * Hold Ctrl while dragging to move at 1/10th normal speed.
 */
public class FineControlSlider extends JSlider {

	private boolean fineMode = false;
	private int dragStartX;
	private int dragStartValue;
	private static final double FINE_SCALE = 0.1;

	public FineControlSlider(int orientation, int min, int max, int value) {
		super(orientation, min, max, value);
		enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK | java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK);
	}

	@Override
	protected void processMouseEvent(MouseEvent e) {
		if (e.getID() == MouseEvent.MOUSE_PRESSED && e.isControlDown()) {
			fineMode = true;
			dragStartX = e.getX();
			dragStartValue = getValue();
			e.consume();
			return;
		}
		if (e.getID() == MouseEvent.MOUSE_RELEASED && fineMode) {
			fineMode = false;
			e.consume();
			return;
		}
		super.processMouseEvent(e);
	}

	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
		if (fineMode && e.getID() == MouseEvent.MOUSE_DRAGGED) {
			int range = getMaximum() - getMinimum();
			int trackWidth = Math.max(getWidth() - 20, 1);
			double pixelsPerUnit = (double) trackWidth / range;
			int dx = e.getX() - dragStartX;
			int delta = (int) (dx / pixelsPerUnit * FINE_SCALE);
			int newVal = Math.max(getMinimum(), Math.min(getMaximum(), dragStartValue + delta));
			setValue(newVal);
			e.consume();
			return;
		}
		super.processMouseMotionEvent(e);
	}
}
