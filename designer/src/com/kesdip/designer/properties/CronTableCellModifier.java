package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

public class CronTableCellModifier implements ICellModifier {
	
	private GenericCallback callback;
	
	public CronTableCellModifier(GenericCallback callback) {
		this.callback = callback;
	}

	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		return element.toString();
	}

	@Override
	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem) element;
		if (value != null)
			item.setText(0, value.toString());
		if (callback != null) {
			int oldValue = item.getData() == null || item.getData() instanceof String ? -1 : (Integer) item.getData();
			// Don't need to check for NumberFormatException here because the CellEditor's validator
			// has already validated the value.
			int newValue = value == null ? -1 : Integer.parseInt(value.toString());
			callback.doCallback(oldValue, newValue);
		}
	}
	
	

}
