package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class ComboBoxPropertyDescriptor extends PropertyDescriptor {
	private String[] elements;

	public ComboBoxPropertyDescriptor(Object id, String displayName, String[] elements) {
		super(id, displayName);
		this.elements = elements;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor(org.eclipse.swt.widgets.Composite)
	 */
	public CellEditor createPropertyEditor(Composite parent) {
		ComboBoxViewerCellEditor editor = new ComboBoxViewerCellEditor(parent);
		editor.setContenProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object input) {
				return elements;
			}
			
			@Override
			public void inputChanged(Viewer viewer, Object oldValue, Object newValue) {
				// nothing to do
			}
			
			@Override
			public void dispose() {
				// nothing to do
			}
		});
		if (getValidator() != null)
			editor.setValidator(getValidator());
		return editor;
	}

}
