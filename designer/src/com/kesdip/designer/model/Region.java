package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class Region extends ComponentModelElement {

	private static final long serialVersionUID = 8142189250205205732L;

	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/alt_window_16.gif");

	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the name property value. */
	public static final String NAME_PROP = "Layout.NameProp";
	/** Property ID to use when a component is added to this region. */
	public static final String COMPONENT_ADDED_PROP = "Region.ComponentAdded";
	/** Property ID to use when a component is removed from this region. */
	public static final String COMPONENT_REMOVED_PROP = "Region.ComponentRemoved";

	/* STATE */
	private String name;
	private List<ComponentModelElement> contents;
	
	public Region() {
		name = "New Region";
		contents = new ArrayList<ComponentModelElement>();
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(NAME_PROP, "Name")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				public String isValid(Object value) {
					// No validation for the name.
					return null;
				}
			});
		}
	} // static

	/** 
	 * Add a component to this region.
	 * @param s a non-null component instance
	 * @return true, iff the component was added, false otherwise
	 */
	public boolean addComponent(ComponentModelElement s) {
		if (s != null && contents.add(s)) {
			firePropertyChange(COMPONENT_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	/** Return a List of components in this layout.  The returned List should not be modified. */
	public List<ComponentModelElement> getComponents() {
		return contents;
	}

	/**
	 * Remove a components from this region.
	 * @param s a non-null component instance;
	 * @return true, iff the component was removed, false otherwise
	 */
	public boolean removeComponent(ComponentModelElement s) {
		if (s != null && contents.remove(s)) {
			firePropertyChange(COMPONENT_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<IPropertyDescriptor> superList = new ArrayList<IPropertyDescriptor>(
				Arrays.asList(super.getPropertyDescriptors()));
		superList.addAll(Arrays.asList(descriptors));
		IPropertyDescriptor[] retVal = new IPropertyDescriptor[superList.size()];
		int counter = 0;
		for (IPropertyDescriptor pd : superList) {
			retVal[counter++] = pd;
		}
		return retVal;
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		if (NAME_PROP.equals(propertyId))
			return name;
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (NAME_PROP.equals(propertyId)) {
			String oldValue = name;
			name = (String) value;
			firePropertyChange(NAME_PROP, oldValue, name);
		} else
			super.setPropertyValue(propertyId, value);
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "Region";
	}
}
