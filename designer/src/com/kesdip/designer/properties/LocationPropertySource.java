package com.kesdip.designer.properties;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class LocationPropertySource implements IPropertySource {
	private static final String X_PROP = "location.x_prop";
	private static final String Y_PROP = "location.y_prop";
	
	private static IPropertyDescriptor[] descriptors;
	
	static {
		descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(X_PROP, "X"),
				new TextPropertyDescriptor(Y_PROP, "Y")
		};
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				@Override
				public String isValid(Object value) {
					try {
						Integer.parseInt(value.toString());
						return null;
					} catch (Exception e) {
						return "Unable to convert value: " +
							value.toString() + " to an integer.";
					}
				}
			});
		}
	}
	
	private Point location;
	
	public LocationPropertySource(Point location) {
		this.location = location;
	}
	
	@Override
	public Object getEditableValue() {
		return location;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (X_PROP.equals(id)) {
			return Integer.toString(location.x);
		} else if (Y_PROP.equals(id)) {
			return Integer.toString(location.y);
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (X_PROP.equals(id)) {
			return true;
		} else if (Y_PROP.equals(id)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (X_PROP.equals(id)) {
			location.x = 0;
		} else if (Y_PROP.equals(id)) {
			location.y = 0;
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (X_PROP.equals(id)) {
			location.x = Integer.parseInt((String) value);
		} else if (Y_PROP.equals(id)) {
			location.y = Integer.parseInt((String) value);
		}
	}

}
