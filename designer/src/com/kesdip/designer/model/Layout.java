package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class Layout extends ModelElement {

	private static final long serialVersionUID = -8369517837436215055L;

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
	/** Property ID to use for the cron expression property value. */
	public static final String CRON_EXPRESSION_PROP = "Layout.CronExpressionProp";
	/** Property ID to use for the duration property value. */
	public static final String DURATION_PROP = "Layout.DurationProp";
	/** Property ID to use when a region is added to this layout. */
	public static final String REGION_ADDED_PROP = "Layout.RegionAdded";
	/** Property ID to use when a region is removed from this layout. */
	public static final String REGION_REMOVED_PROP = "Layout.RegionRemoved";

	/* STATE */
	private String name;
	private String cronExpression;
	private int duration;
	private List<Region> regionList;
	
	public Layout() {
		name = "New Layout";
		cronExpression = "";
		duration = 0;
		regionList = new ArrayList<Region>();
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(NAME_PROP, "Name"),
				new TextPropertyDescriptor(CRON_EXPRESSION_PROP, "Cron Expression"),
				new TextPropertyDescriptor(DURATION_PROP, "Duration")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getId().equals(DURATION_PROP)) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						int intValue = -1;
						try {
							intValue = Integer.parseInt((String) value);
						} catch (NumberFormatException exc) {
							return "Not a number";
						}
						return (intValue >= 0) ? null : "Value must be >=  0";
					}
				});
			} else {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						// TODO: We should run some validation, at least for the cron expression.
						return null;
					}
				});
			}
		}
	} // static

	/** 
	 * Add a region to this layout.
	 * @param s a non-null region instance
	 * @return true, iff the region was added, false otherwise
	 */
	public boolean addRegion(Region s) {
		if (s != null && regionList.add(s)) {
			firePropertyChange(REGION_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	/** Return a List of regions in this layout.  The returned List should not be modified. */
	public List<Region> getRegions() {
		return regionList;
	}

	/**
	 * Remove a region from this layout.
	 * @param s a non-null region instance;
	 * @return true, iff the region was removed, false otherwise
	 */
	public boolean removeRegion(Region s) {
		if (s != null && regionList.remove(s)) {
			firePropertyChange(REGION_REMOVED_PROP, null, s);
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
		else if (CRON_EXPRESSION_PROP.equals(propertyId))
			return cronExpression;
		else if (DURATION_PROP.equals(propertyId))
			return Integer.toString(duration);
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (NAME_PROP.equals(propertyId)) {
			String oldValue = name;
			name = (String) value;
			firePropertyChange(NAME_PROP, oldValue, name);
		} else if (CRON_EXPRESSION_PROP.equals(propertyId)) {
			String oldValue = cronExpression;
			cronExpression = (String) value;
			firePropertyChange(CRON_EXPRESSION_PROP, oldValue, cronExpression);
		} else if (DURATION_PROP.equals(propertyId)) {
			int oldValue = duration;
			duration = Integer.parseInt((String) value);
			firePropertyChange(DURATION_PROP, oldValue, duration);
		} else
			super.setPropertyValue(propertyId, value);
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}

	@Override
	public String toString() {
		return "Layout: " + name;
	}
	
	public String getName() {
		return name;
	}
}
