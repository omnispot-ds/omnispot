package com.kesdip.designer.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * This class is a HACK. It is designed to facilitate the display of data in columns instead of rows.
 * It accomplishes this by using TableViewer with hack-facilitating subclasses of CellModifier, LabelProvider 
 * and IStructuredContentProvider all contained herein.
 * 
 * @author n.giamouris
 *
 */
public class MinuteTableHelper {
	
	private TableViewer tableViewer;
	private CronDatum cronDatum;
	private CronModel cronModel;
	
	private int visibleColumns;
	private final static int maxColumns = 20;
	private final static int targetColumnWidth = 40;
	
	
	public MinuteTableHelper(TableViewer tableViewer, CronModel cronModel) {
		this.tableViewer = tableViewer;
		this.cronModel = cronModel;
		this.cronDatum = cronModel.getCronMinute();
		
		Table table = tableViewer.getTable();
		
		// keep track of how many columns are currently displaying
		visibleColumns = 0;

		// hide any existing columns
		for (int i = 0 ; i < table.getColumnCount() ; i++) {
			TableColumn col = table.getColumn(i);
			col.setAlignment(SWT.CENTER);
			col.setWidth(0);
			col.setResizable(false);
		}
		
		// create additional columns to reach the desired maxColumns
		int diff = maxColumns - table.getColumnCount();
		for (; diff > 0 ; diff--) {
			TableColumn col = new TableColumn(table, SWT.CENTER);
			col.setWidth(0);
			col.setResizable(false);
		}
		
		// set the column properties or the table will refuse to display anything!
		// the column properties will also be used to identify which element in the table 
		// is being edited.
		String[] colProperties = new String[maxColumns];
		for (int i = 0 ; i < maxColumns ; i++) {
			colProperties[i] = Integer.toString(i);
		}
		tableViewer.setColumnProperties(colProperties);
		
		tableViewer.setLabelProvider(new CronMinuteLabelProvider());
		
		// dummy content provider
		tableViewer.setContentProvider(new CronMinuteContentProvider());

		// dummy input
		tableViewer.setInput(new Object());
		
		// Prepare the cell editors
		ValidatingTextCellEditor minuteTextCellEditor = new ValidatingTextCellEditor(tableViewer.getTable(), SWT.NONE);
		minuteTextCellEditor.setValidator(new CronRangeValidator(0, 59));
		//minuteTextCellEditor.addListener(SWT.FocusIn, new ICellEditorListener() {
			
		//})
		CellEditor[] editors = new CellEditor[maxColumns];
		for (int i = 0 ; i < maxColumns ; i++) {
			editors[i] = minuteTextCellEditor;
		}
		tableViewer.setCellEditors(editors);
		
		tableViewer.setCellModifier(new CronMinuteCellModifier());
	}
	
	public void refresh() {
		Table table = tableViewer.getTable();
		
		Set<Integer> cronData = cronDatum.getCronData();
		int diff = visibleColumns - cronData.size();
		
		// more visible columns than required
		if (diff > 0) {
			
			for (int i = visibleColumns - 1 ; i > cronData.size() - 1 ; i--) {
				table.getColumn(i).setWidth(0);
			}
		}
		// less visible columns than required
		else if (diff < 0) {
			for (int i = visibleColumns ; i < cronData.size() ; i++) {
				table.getColumn(i).setWidth(targetColumnWidth);
			}
		}
		
		tableViewer.refresh(true);
		
		visibleColumns = cronData.size();
		
	}
	
	public void addMinute() {
		Table table = tableViewer.getTable();
		addingMinute = true;
		table.getColumn(visibleColumns).setWidth(targetColumnWidth);
		table.getItem(0).setText(visibleColumns, "new");
		tableViewer.editElement(cronDatum.getCronData(), visibleColumns);
		visibleColumns++;
	}
	
	public void removeSelectedMinute() {
		cronDatum.remove(lastSelectedMinute);
		refresh();
	}
	
	private int lastSelectedMinute = -1;
	private boolean addingMinute;
	
	class CronMinuteCellModifier implements ICellModifier {

		@Override
		public boolean canModify(Object element, String property) {
			return element instanceof Set;
		}

		@Override
		public Object getValue(Object element, String property) {
			if (addingMinute)
				return "new";
			List<Integer> lst = new ArrayList<Integer>(cronDatum.getCronData());
			// NumberFormatException should never happen here
			int idx = Integer.parseInt(property);
			lastSelectedMinute = lst.get(idx);
			return Integer.toString(lastSelectedMinute);
		}

		@Override
		public void modify(Object element, String property, Object value) {
			if (value == null) {
				addingMinute = false;
				refresh();
				return;
			}
			List<Integer> lst = new ArrayList<Integer>(cronDatum.getCronData());
			int idx = Integer.parseInt(property);
			if (idx < lst.size()) {
				int oldValue = lst.get(idx);
				cronDatum.remove(oldValue);
			} else {
				addingMinute = false;
			}
			int newValue = Integer.parseInt(value.toString());
			cronDatum.add(newValue);
			tableViewer.refresh(true);
		}
		
	}
	
	class CronMinuteLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof Set) {
				List<Integer> lst = new ArrayList<Integer>(cronDatum.getCronData());
				if (columnIndex <= lst.size() - 1) {
					return lst.get(columnIndex).toString();
				} else {
					return "";
				}
			} else {
				String[] vals = (String[]) element;
				if (columnIndex <= vals.length - 1) {
					return vals[columnIndex];
				} else {
					return "";
				}
			}
		}
		
		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

	}
	
	class CronMinuteContentProvider implements IStructuredContentProvider {
		
		@Override
		public void dispose() { }
		
		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) { }
		
		@Override
		public Object[] getElements(Object inputElement) { 
			if (cronModel.getCronHour() == null || cronModel.getCronMinute() == null) {
				return new Object[]{};
			}
			Set<Integer> cronHours = cronModel.getCronHour().getCronData();
			Set<Integer> cronMinutes = cronDatum.getCronData();
			Object[] retVal = new Object[cronHours.size() + 1];
			retVal[0] = cronMinutes;
			int i = 1;
			for (int hour : cronHours) {
				String hr = hour > 9 ? Integer.toString(hour) : "0" + hour;
				String[] times = new String[cronMinutes.size()];
				int j = 0;
				for (int minute : cronMinutes) {
					String min = minute > 9 ? Integer.toString(minute) : "0" + minute;
					times[j] = hr + ":" + min;
					j++;
				}
				retVal[i] = times;
				i++;
			}
			return retVal; 
		}
	}
	
}
