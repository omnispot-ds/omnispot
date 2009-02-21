package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kesdip.designer.utils.DOMHelpers;

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
	
	protected Element serialize(Document doc, int layoutCount) {
		Element layoutElement = doc.createElement("bean");
		layoutElement.setAttribute("class", "com.kesdip.player.DeploymentLayout");
		DOMHelpers.addProperty(doc, layoutElement, "name", name);
		if (cronExpression != null && cronExpression.length() != 0)
			DOMHelpers.addProperty(doc, layoutElement, "cronExpression", cronExpression);
		if (duration != 0)
			DOMHelpers.addProperty(doc, layoutElement, "duration", String.valueOf(duration));
		Element regionsElement = DOMHelpers.addProperty(doc, layoutElement, "contentRoots");
		Element listElement = doc.createElement("list");
		regionsElement.appendChild(listElement);
		int counter = 1;
		for (Region r : regionList) {
			Element regionElement = r.serialize(doc, layoutCount, counter++);
			listElement.appendChild(regionElement);
		}
		
		return layoutElement;
	}
	
	protected void deserialize(Document doc, Node layoutNode) {
		setPropertyValue(NAME_PROP, DOMHelpers.getSimpleProperty(layoutNode, "name"));
		String cronExpression = DOMHelpers.getSimpleProperty(layoutNode, "cronExpression");
		if (cronExpression != null)
			setPropertyValue(CRON_EXPRESSION_PROP, cronExpression);
		String duration = DOMHelpers.getSimpleProperty(layoutNode, "duration");
		if (duration != null)
			setPropertyValue(DURATION_PROP, duration);
		final List<Region> newRegionList = new ArrayList<Region>();
		DOMHelpers.applyToListProperty(doc, layoutNode, "contentRoots", "ref",
				new DOMHelpers.INodeListVisitor() {
			@Override
			public void visitListItem(Document doc, Node listItem) {
				Region newRegion = new Region();
				newRegion.deserialize(doc, listItem);
				newRegionList.add(newRegion);
			}
		});
		regionList = newRegionList;
	}
	
	public void checkEquivalence(Layout other) {
		assert(name.equals(other.name));
		assert(cronExpression.equals(other.cronExpression));
		assert(duration == other.duration);
		for (int i = 0; i < regionList.size(); i++) {
			Region thisRegion = regionList.get(i);
			Region otherRegion = other.regionList.get(i);
			thisRegion.checkEquivalence(otherRegion);
		}
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
