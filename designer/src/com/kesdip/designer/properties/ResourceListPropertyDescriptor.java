package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.kesdip.designer.constenum.ResourceListCellEditorTypes;

/**
 * Property descriptor for the resource list field. It can be parameterized as
 * to which specific {@link ResourceListCellEditor} instance to create.
 * 
 * @author gerogias
 */
public class ResourceListPropertyDescriptor extends PropertyDescriptor {

	/**
	 * The cell editor type to create.
	 */
	private int cellEditorType = ResourceListCellEditorTypes.GENERIC_RESOURCE_LIST_EDITOR;

	public ResourceListPropertyDescriptor(Object id, String displayName,
			int cellEditorType) {
		super(id, displayName);
		this.cellEditorType = cellEditorType;
	}

	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		switch (cellEditorType) {
		case ResourceListCellEditorTypes.VIDEO_RESOURCE_LIST_EDITOR:
			return new VideoResourceListCellEditor(parent, SWT.NONE);
		default:
			return new ResourceListCellEditor(parent, SWT.NONE);
		}
	}

}
