/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jun 22, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.properties;

import java.io.File;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.kesdip.designer.constenum.ResourceListColumnNames;
import com.kesdip.designer.model.Resource;

/**
 * Dialog for a video resource list.
 * <p>
 * Differs from the default resource list in that it introduces a "fullscreen"
 * checkbox.
 * </p>
 * 
 * @author gerogias
 */
public class VideoResourceListDialog extends ResourceListDialog {

	/**
	 * The created instance.
	 */
	private TableViewer tableViewer = null;

	/**
	 * Constructor.
	 * 
	 * @param parentShell
	 */
	public VideoResourceListDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * @see com.kesdip.designer.properties.ResourceListDialog#createResourceTableViewer(org.eclipse.swt.widgets.Group,
	 *      org.eclipse.jface.viewers.ISelectionChangedListener, java.util.List)
	 */
	@Override
	protected TableViewer createResourceTableViewer(Group resourceListGroup,
			ISelectionChangedListener listener, List<Resource> tableContents) {
		tableViewer = new TableViewer(resourceListGroup, SWT.FULL_SELECTION
				| SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		Table t = tableViewer.getTable();
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		tableViewer.setContentProvider(new ResourceListContentProvider());
		tableViewer.setLabelProvider(new ResourceListLabelProvider());
		tableViewer.setColumnProperties(new String[] {
				ResourceListColumnNames.RESOURCE,
				ResourceListColumnNames.FULL_SCREEN,
				ResourceListColumnNames.CRON_EXPRESSION });
		tableViewer.setCellModifier(new VideoResourceCellModifier(this));
		ResourceFileCellEditor resourceCellEditor = new ResourceFileCellEditor(
				t, SWT.NONE);
		resourceCellEditor.setValidator(new ICellEditorValidator() {
			@Override
			public String isValid(Object value) {
				String v = (String) value;
				if (v == null || "".equals(v)) {
					return null;
				}
				try {
					File f = new File(v);
					if (f.exists()) {
						// valid resource
						return null;
					}
					return "Unable to locate resource at: " + v;
				} catch (Exception e) {
					return "Error while trying to access resource at: " + v
							+ " [" + e.getMessage() + "]";
				}
			}
		});
		CronCellEditor cronExpressionCellEditor = new CronCellEditor(t,
				SWT.NONE);
		/*
		 * cronExpressionCellEditor.setValidator(new ICellEditorValidator() {
		 * 
		 * @Override public String isValid(Object value) { String v = (String)
		 * value; if (v == null || "".equals(v)) return null; if
		 * (CronExpression.isValidExpression(v)) return null; else return
		 * "Invalid CRON expression: '" + v + "'."; } });
		 */
		tableViewer.setCellEditors(new CellEditor[] { resourceCellEditor,
				new CheckboxCellEditor(t), cronExpressionCellEditor });
		String[] columnNames = new String[] { "Resoure", "F-S", "Cron Expr." };
		int[] columnWidths = new int[] { 240, 30, 100 };
		int[] columnAlignments = new int[] { SWT.LEFT, SWT.CENTER, SWT.LEFT };
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(t, columnAlignments[i]);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
		}
		t.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableViewer.setInput(tableContents);
		tableViewer.addSelectionChangedListener(listener);
		// add a double click listener on the table
//		t.addListener(SWT.MouseDoubleClick, new FullScreenCellClickListener(
//				tableViewer));

		return tableViewer;
	}

	/**
	 * Listener for click events in the fullscreen cells.
	 * 
	 * @author gerogias
	 */
	static class FullScreenCellClickListener implements Listener {

		/**
		 * The created TableViewer instance.
		 */
		private TableViewer tableViewer = null;

		/**
		 * Constructor.
		 * 
		 * @param tableViewer
		 *            the parent dialog's table viewer
		 */
		FullScreenCellClickListener(TableViewer tableViewer) {
			this.tableViewer = tableViewer;
		}

		/**
		 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
		 */
		@Override
		public void handleEvent(Event event) {
			Point pt = new Point(event.x, event.y);
			TableItem item = tableViewer.getTable().getItem(pt);
			if (item == null) {
				return;
			}
			// toggle fullscreen
			Resource resource = (Resource) item.getData();
			resource.setFullscreen(!resource.isFullscreen());
			// update visual status
			item.setText(1, resource.isFullscreen() ? "X" : "");
		}
	}
}
