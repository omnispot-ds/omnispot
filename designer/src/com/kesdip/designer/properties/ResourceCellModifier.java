package com.kesdip.designer.properties;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

import com.kesdip.designer.constenum.ResourceListColumnNames;
import com.kesdip.designer.model.Resource;

public class ResourceCellModifier implements ICellModifier {
	private ResourceListDialog dialog;

	public ResourceCellModifier(ResourceListDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public boolean canModify(Object element, String property) {
		return true;
	}

	@Override
	public Object getValue(Object element, String property) {
		Resource resource = (Resource) element;
		if (ResourceListColumnNames.RESOURCE.equals(property)) {
			return resource.getResource();
		} else if (ResourceListColumnNames.CRON_EXPRESSION.equals(property)) {
			return resource.getCronExpression();
		} else {
			throw new IllegalArgumentException("Unexpected property "
					+ property);
		}
	}

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
		} else if (ResourceListColumnNames.CRON_EXPRESSION.equals(property)) {
			if (value == null) {
				value = "";
			}
			item.setText(1, (String) value);
			resource.setCronExpression((String) value);
		} else {
			throw new IllegalArgumentException("Unexpected property "
					+ property);
		}
		dialog.updateButtons();
	}

}
