package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kesdip.designer.model.Resource;

public class ResourceListLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Resource r = (Resource) element;
		switch (columnIndex) {
		case 0: return r.getResource();
		case 1: return r.isFullscreen() ? "X" : "";
		case 2: return r.getCronExpression();
		default: throw new RuntimeException("Unexpected column: " + columnIndex);
		}
	}

}
