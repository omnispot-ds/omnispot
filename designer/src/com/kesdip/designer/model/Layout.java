package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.quartz.CronExpression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kesdip.designer.properties.CronPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class Layout extends ModelElement {

	private static final long serialVersionUID = -8369517837436215055L;

	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/display.png");
	
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
	/** Property ID to use when changing the grid visibility. */
	public static final String SHOW_GRID_PROP = "Layout.ShowGrid";
	/** Property ID to use when changing the snap to geometry setting. */
	public static final String SNAP_TO_GEOMETRY_PROP = "Layout.SnapToGeometry";

	/* STATE */
	private String name;
	private String cronExpression;
	private int duration;
	private List<ModelElement> regionList;
	private boolean showGrid;
	private boolean snapToGeometry;
	protected LayoutRuler leftRuler, topRuler;
	
	public Layout() {
		name = "New Layout";
		cronExpression = "";
		duration = 0;
		regionList = new ArrayList<ModelElement>();
		showGrid = false;
		snapToGeometry = false;
		leftRuler = new LayoutRuler(false);
		topRuler = new LayoutRuler(true);
	}
	
	protected Element serialize(Document doc, int layoutCount, boolean isPublish) {
		Element layoutElement = doc.createElement("bean");
		layoutElement.setAttribute("class", "com.kesdip.player.DeploymentLayout");
		DOMHelpers.addProperty(doc, layoutElement, "name", name);
		DOMHelpers.addProperty(doc, layoutElement, "showGrid",
				showGrid ? "true" : "false");
		DOMHelpers.addProperty(doc, layoutElement, "snapToGeometry",
				snapToGeometry ? "true" : "false");
		if (cronExpression != null && cronExpression.length() != 0)
			DOMHelpers.addProperty(doc, layoutElement, "cronExpression", cronExpression);
		if (duration != 0)
			DOMHelpers.addProperty(doc, layoutElement, "duration", String.valueOf(duration));
		Element regionsElement = DOMHelpers.addProperty(doc, layoutElement, "contentRoots");
		Element listElement = doc.createElement("list");
		regionsElement.appendChild(listElement);
		int counter = 1;
		for (ModelElement e : regionList) {
			Region r = (Region) e;
			Element regionElement = r.serialize(doc, layoutCount, counter++, isPublish);
			listElement.appendChild(regionElement);
		}
		
		return layoutElement;
	}
	
	protected void deserialize(Document doc, Node layoutNode) {
		setPropertyValue(NAME_PROP, DOMHelpers.getSimpleProperty(layoutNode, "name"));
		setPropertyValue(SHOW_GRID_PROP,
				DOMHelpers.getSimpleProperty(layoutNode, "showGrid"));
		setPropertyValue(SNAP_TO_GEOMETRY_PROP,
				DOMHelpers.getSimpleProperty(layoutNode, "snapToGeometry"));
		String cronExpression = DOMHelpers.getSimpleProperty(layoutNode, "cronExpression");
		if (cronExpression != null)
			setPropertyValue(CRON_EXPRESSION_PROP, cronExpression);
		String duration = DOMHelpers.getSimpleProperty(layoutNode, "duration");
		if (duration != null)
			setPropertyValue(DURATION_PROP, duration);
		final List<ModelElement> newRegionList = new ArrayList<ModelElement>();
		DOMHelpers.applyToListProperty(doc, layoutNode, "contentRoots", "ref",
				new DOMHelpers.INodeListVisitor() {
			@Override
			public void visitListItem(Document doc, Node listItem) {
				Region newRegion = new Region();
				newRegion.deserialize(doc, listItem);
				newRegion.setParent(Layout.this);
				newRegionList.add(newRegion);
			}
		});
		regionList = newRegionList;
	}
	
	public void save(IMemento memento) {
		memento.putString(TAG_NAME, name);
		memento.putString(TAG_CRON_EXPRESSION, cronExpression);
		memento.putInteger(TAG_DURATION, duration);
		for (ModelElement e : regionList) {
			Region region = (Region) e;
			IMemento child = memento.createChild(TAG_REGION);
			region.save(child);
		}
		memento.putBoolean(TAG_GRID, showGrid);
		memento.putBoolean(TAG_SNAP_GEOM, snapToGeometry);
	}
	
	public void load(IMemento memento) {
		name = memento.getString(TAG_NAME);
		cronExpression = memento.getString(TAG_CRON_EXPRESSION);
		duration = memento.getInteger(TAG_DURATION);
		regionList.clear();
		IMemento[] children = memento.getChildren(TAG_REGION);
		for (IMemento child : children) {
			Region r = new Region();
			r.load(child);
		}
		showGrid = memento.getBoolean(TAG_GRID);
		snapToGeometry = memento.getBoolean(TAG_SNAP_GEOM);
	}
	
	public void checkEquivalence(Layout other) {
		assert(name.equals(other.name));
		assert(cronExpression.equals(other.cronExpression));
		assert(duration == other.duration);
		for (int i = 0; i < regionList.size(); i++) {
			Region thisRegion = (Region) regionList.get(i);
			Region otherRegion = (Region) other.regionList.get(i);
			thisRegion.checkEquivalence(otherRegion);
		}
		assert(showGrid == other.showGrid);
		assert(snapToGeometry == other.snapToGeometry);
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
				new CronPropertyDescriptor(CRON_EXPRESSION_PROP, "Cron Expression"),
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
			} else if (descriptors[i].getId().equals(CRON_EXPRESSION_PROP)) {
					((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
						public String isValid(Object value) {
							if ("".equals(value))
								return null;
							if (!CronExpression.isValidExpression((String) value))
								return "'" + value + "' is not a valid cron expression";
							return null;
						}
					});
			} else {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						return null;
					}
				});
			}
		}
	} // static

	public ModelElement deepCopy() {
		Layout retVal = new Layout();
		retVal.name = this.name;
		retVal.duration = this.duration;
		retVal.cronExpression = this.cronExpression;
		for (ModelElement srcr : this.regionList) {
			Region r = (Region) srcr.deepCopy();
			retVal.regionList.add(r);
		}
		retVal.showGrid = showGrid;
		retVal.snapToGeometry = snapToGeometry;
		return retVal;
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
		else if (SHOW_GRID_PROP.equals(propertyId))
			return showGrid;
		else if (SNAP_TO_GEOMETRY_PROP.equals(propertyId))
			return snapToGeometry;
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
		} else if (SHOW_GRID_PROP.equals(propertyId)) {
			if (value instanceof String) {
				// We are being deserialized
				String oldValue = showGrid ? "true" : "false";
				showGrid = value.equals("true");
				firePropertyChange(SHOW_GRID_PROP, oldValue, value);
				return;
			}
			Boolean oldValue = showGrid;
			showGrid = ((Boolean) value).booleanValue();
			firePropertyChange(SHOW_GRID_PROP, oldValue, showGrid);
		} else if (SNAP_TO_GEOMETRY_PROP.equals(propertyId)) {
			if (value instanceof String) {
				// We are being deserialized
				String oldValue = snapToGeometry ? "true" : "false";
				snapToGeometry = value.equals("true");
				firePropertyChange(SNAP_TO_GEOMETRY_PROP, oldValue, value);
				return;
			}
			Boolean oldValue = snapToGeometry;
			snapToGeometry = ((Boolean) value).booleanValue();
			firePropertyChange(SNAP_TO_GEOMETRY_PROP, oldValue, SNAP_TO_GEOMETRY_PROP);
		} else
			super.setPropertyValue(propertyId, value);
	}

	@Override
	public void add(ModelElement child) {
		if (child != null && child instanceof Region && regionList.add(child)) {
			child.setParent(this);
			firePropertyChange(REGION_ADDED_PROP, null, child);
		}
	}

	@Override
	public boolean removeChild(ModelElement child) {
		if (child != null && child instanceof Region && regionList.remove(child)) {
			child.setParent(null);
			firePropertyChange(REGION_REMOVED_PROP, null, child);
			return true;
		}
		return false;
	}

	@Override
	public List<ModelElement> getChildren() {
		return regionList;
	}

	@Override
	public void insertChildAt(int index, ModelElement child) {
		if (child != null && child instanceof Region) {
			child.setParent(this);
			regionList.add(index, child);
			firePropertyChange(REGION_ADDED_PROP, null, child);
		}
	}

	@Override
	public boolean isFirstChild(ModelElement child) {
		return regionList.indexOf(child) == 0;
	}

	@Override
	public boolean isLastChild(ModelElement child) {
		return regionList.indexOf(child) == regionList.size() - 1 &&
				regionList.size() != 0;
	}

	@Override
	public boolean moveChildDown(ModelElement child) {
		int index = regionList.indexOf(child);
		if (index == -1 || index == regionList.size() - 1)
			return false;
		if (!regionList.remove(child))
			return false;
		regionList.add(index + 1, child);
		firePropertyChange(CHILD_MOVE_DOWN, null, child);
		return true;
	}

	@Override
	public boolean moveChildUp(ModelElement child) {
		int index = regionList.indexOf(child);
		if (index < 1)
			return false;
		if (!regionList.remove(child))
			return false;
		regionList.add(index - 1, child);
		firePropertyChange(CHILD_MOVE_UP, null, child);
		return true;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}

	@Override
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isShowGrid() {
		return showGrid;
	}

	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}

	public boolean isSnapToGeometry() {
		return snapToGeometry;
	}

	public void setSnapToGeometry(boolean snapToGeometry) {
		this.snapToGeometry = snapToGeometry;
	}

	public LayoutRuler getRuler(int orientation) {
		LayoutRuler result = null;
		switch (orientation) {
			case PositionConstants.NORTH :
				result = topRuler;
				break;
			case PositionConstants.WEST :
				result = leftRuler;
				break;
		}
		return result;
	}

	@Override
	public void resizeBy(double x, double y) {
		for (ModelElement r : regionList)
			r.resizeBy(x, y);
	}
	
}
