package com.holycityaudio.SpinCAD;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

public class RecentFileList extends JPanel {

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
		            @SuppressWarnings("unused")
//					int index = list.locationToIndex(evt.getPoint())
					File file = list.getSelectedValue();
		            System.out.println(file.getName());
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

