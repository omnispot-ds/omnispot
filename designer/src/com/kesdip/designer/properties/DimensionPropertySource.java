package com.kesdip.designer.properties;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class DimensionPropertySource implements IPropertySource {
	private static final String HEIGHT_PROP = "dimension.height_prop";
	private static final String WIDTH_PROP = "dimension.width_prop";
	
	private static IPropertyDescriptor[] descriptors;
	
	static {
		descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(HEIGHT_PROP, "Height"),
				new TextPropertyDescriptor(WIDTH_PROP, "Width")
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
	
	private Dimension dimension;
	
	public DimensionPropertySource(Dimension dimension) {
		this.dimension = dimension;
	}
	
	@Override
	public Object getEditableValue() {
		return dimension;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (HEIGHT_PROP.equals(id)) {
			return Integer.toString(dimension.height);
		} else if (WIDTH_PROP.equals(id)) {
			return Integer.toString(dimension.width);
		} else {
			return null;
		}
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (HEIGHT_PROP.equals(id)) {
			return true;
		} else if (WIDTH_PROP.equals(id)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void resetPropertyValue(Object id) {
		if (HEIGHT_PROP.equals(id)) {
			dimension.height = 0;
		} else if (WIDTH_PROP.equals(id)) {
			dimension.width = 0;
		}
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (HEIGHT_PROP.equals(id)) {
			dimension.height = Integer.parseInt((String) value);
		} else if (WIDTH_PROP.equals(id)) {
			dimension.width = Integer.parseInt((String) value);
		}
	}

}
