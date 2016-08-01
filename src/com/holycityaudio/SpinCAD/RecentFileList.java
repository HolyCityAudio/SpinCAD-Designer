/* SpinCAD Designer - DSP Development Tool for the Spin FV-1
 * RecentFileList.java
 * Copyright (C) 2015 - Gary Worsham
 * Based on ElmGen by Andrew Kilpatrick.  Modified by Gary Worsham 2013 - 2015.  
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
//import java.util.EventListener;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
//import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

public class RecentFileList extends JPanel {

	/**
	 * 
	 */

	private static final long serialVersionUID = -8939170769782253694L;


	private final JList<File> list;
	final FileListModel listModel;
	private final JFileChooser fileChooser;

	public RecentFileList(JFileChooser chooser) {
		fileChooser = chooser;
		listModel = new FileListModel();
		list = new JList<>(listModel);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new FileListCellRenderer());

		setLayout(new BorderLayout());
		add(new JScrollPane(list));
		// add double click handler here

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				//		        @SuppressWarnings("rawtypes")
				//				JList list = (JList)evt.getSource();
				if (evt.getClickCount() == 2) {

					// Double-click detected
					File file = list.getSelectedValue();
					fileChooser.setSelectedFile(file);
					fileChooser.approveSelection();
				} else if (evt.getClickCount() == 3) {
					// Triple-click detected
					int index = list.locationToIndex(evt.getPoint());
				}
			}
		});

		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					File file = list.getSelectedValue();
					// You might like to check to see if the file still exists...
					fileChooser.setSelectedFile(file);
				}
			}
		});
	}

	public void clearList() {
		listModel.clear();
	}

	public void add(File file) {
		listModel.add(file);
	}

	public class FileListModel extends AbstractListModel<File> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1058051753311359394L;
		private List<File> files;

		public FileListModel() {
			files = new ArrayList<>();
		}

		public void add(File file) {
			if (!files.contains(file)) {
				if (files.isEmpty()) {
					files.add(file);
				} else {
					files.add(0, file);
				}
				fireIntervalAdded(this, 0, 0);
			}
		}

		public void clear() {
			int size = files.size() - 1;
			if (size >= 0) {
				files.clear();
				fireIntervalRemoved(this, 0, size);
			}
		}

		@Override
		public int getSize() {
			return files.size();
		}

		@Override
		public File getElementAt(int index) {
			return files.get(index);
		}
	}

	public class FileListCellRenderer extends DefaultListCellRenderer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -8275640582829749182L;

		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof File) {
				File file = (File) value;
				Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
				setIcon(ico);
				setToolTipText(file.getParent());
				setText(file.getName());
			}
			return this;
		}

	}
}

