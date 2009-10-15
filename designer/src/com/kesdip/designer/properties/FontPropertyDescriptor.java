package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class FontPropertyDescriptor extends PropertyDescriptor {
	
	public FontPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}
	
	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		return new FontCellEditor(parent, SWT.NONE);
	}
	
}
