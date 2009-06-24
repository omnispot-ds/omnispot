/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jun 22, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import com.kesdip.designer.constenum.ResourceListColumnNames;
import com.kesdip.designer.model.Resource;

/**
 * Cell modifier for the table of a video resource list.
 * 
 * @author gerogias
 */
public class VideoResourceCellModifier implements ICellModifier {

	/**
	 * The parent dialog.
	 */
	private VideoResourceListDialog dialog;

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 *            the parent dialog.
	 */
	public VideoResourceCellModifier(VideoResourceListDialog dialog) {
		this.dialog = dialog;
	}

	/**
	 * We can always modify the cells.
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
	 *      java.lang.String)
	 */
	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		Resource resource = (Resource) element;
		if (ResourceListColumnNames.RESOURCE.equals(property)) {
			return resource.getResource();
		} else if (ResourceListColumnNames.FULL_SCREEN.equals(property)) {
			return resource.isFullscreen();
		} else if (ResourceListColumnNames.CRON_EXPRESSION.equals(property)) {
			return resource.getCronExpression();
		} else {
			throw new IllegalArgumentException("Unexpected property "
					+ property);
		}
	}

	/**
	 * Modify the model object based on the incoming value.
	 * 
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	@Override
	public void modify(Object element, String property, Object value) {
		TableItem item = (TableItem) element;
		Resource resource = (Resource) item.getData();
		if (ResourceListColumnNames.RESOURCE.equals(property)) {
			if (value == null) {
				value = "";
			}
			item.setText(0, (String) value);
			resource.setResource((String) value);
		} else if (ResourceListColumnNames.FULL_SCREEN.equals(property)) {
			if (value == null) {
				value = Boolean.FALSE;
			}
			item.setText(1, ((Boolean) value) ? "X" : "");
			resource.setFullscreen((Boolean) value);
		} else if (ResourceListColumnNames.CRON_EXPRESSION.equals(property)) {
			if (value == null) {
				value = "";
			}
			item.setText(2, (String) value);
			resource.setCronExpression((String) value);
		} else {
			throw new IllegalArgumentException("Unexpected property "
					+ property);
		}
		dialog.updateButtons();
	}

}
