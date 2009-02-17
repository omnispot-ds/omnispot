package com.kesdip.designer.view;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.kesdip.designer.model.Layout;

public class DeploymentLabelProvider extends LabelProvider
		implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex > 1)
			return null;
		
		Layout layout = (Layout) element;
		return layout.getIcon();
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex > 1)
			return null;
		
		Layout layout = (Layout) element;
		return layout.toString();
	}

}
