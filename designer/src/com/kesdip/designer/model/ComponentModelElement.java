package com.kesdip.designer.model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kesdip.designer.properties.CheckboxPropertyDescriptor;
import com.kesdip.designer.properties.DimensionPropertySource;
import com.kesdip.designer.properties.LocationPropertySource;
import com.kesdip.designer.utils.DOMHelpers;

/**
 * Abstract prototype of an Component Element. Has a size (width and height), a
 * location (x and y position). Use subclasses to instantiate a specific
 * component element - e.g. an image component or a video component.
 * 
 * @author Pafsanias Ftakas
 */
public abstract class ComponentModelElement extends ModelElement {
	private static final long serialVersionUID = 5594247627329684698L;

	/**
	 * A static array of property descriptors. There is one IPropertyDescriptor
	 * entry per editable property.
	 * 
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID for the Height property value. */
	public static final String HEIGHT_PROP = "Component.Height";
	/** Property ID for the Width property value. */
	public static final String WIDTH_PROP = "Component.Width";
	/** Property ID for the X property value. */
	public static final String XPOS_PROP = "Component.xPos";
	/** Property ID for the Y property value. */
	public static final String YPOS_PROP = "Component.yPos";
	/** Property ID for the background color property value. */
	public static final String BACK_COLOR_PROP = "Component.backgroundColor";
	/** Property ID to use when the location of this shape is modified. */
	public static final String LOCATION_PROP = "Component.Location";
	/** Property ID to use then the size of this shape is modified. */
	public static final String SIZE_PROP = "Component.Size";
	/** Property ID to use for the lockSizeAndPosition property. */
	public static final String LOCK_SIZE_AND_POSITION_PROP = "Component.LockSizeAndPosition";

	/*
	 * Initializes the property descriptors array.
	 * 
	 * @see #getPropertyDescriptors()
	 * 
	 * @see #getPropertyValue(Object)
	 * 
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] {
				new PropertyDescriptor(LOCATION_PROP, "Location"),
				new PropertyDescriptor(SIZE_PROP, "Size"),
				new ColorPropertyDescriptor(BACK_COLOR_PROP, "Background Color"),
				new CheckboxPropertyDescriptor(LOCK_SIZE_AND_POSITION_PROP,
						"Lock Size & Location") };
		// use a custom cell editor validator for all five array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Real Estate");
			if (descriptors[i].getId().equals(BACK_COLOR_PROP)) {
				((PropertyDescriptor) descriptors[i])
						.setValidator(new ICellEditorValidator() {
							public String isValid(Object value) {
								// All colors are valid as background colors.
								return null;
							}
						});
			} else {
				((PropertyDescriptor) descriptors[i])
						.setValidator(new ICellEditorValidator() {
							public String isValid(Object value) {
								// Validation is performed at the children
								// property
								// source implementations.
								return null;
							}
						});
			}
		}
	} // static

	/** Location of this shape. */
	protected Point location = new Point(0, 0);
	protected LocationPropertySource locationPropertySource = new LocationPropertySource(
			location, this);
	/** Size of this shape. */
	protected Dimension size = new Dimension(50, 50);
	protected DimensionPropertySource dimensionPropertySource = new DimensionPropertySource(
			size, this);
	protected Color backgroundColor = Color.BLACK;
	protected boolean locked = false;
	protected LayoutGuide verticalGuide, horizontalGuide;

	abstract Element serialize(Document doc, boolean isPublish);

	protected void serialize(Document doc, Element componentElement) {
		serialize(doc, componentElement, true);
	}

	protected void serialize(Document doc, Element componentElement,
			boolean includeBackgroundColor) {
		DOMHelpers.addProperty(doc, componentElement, "x", String
				.valueOf(location.x));
		DOMHelpers.addProperty(doc, componentElement, "y", String
				.valueOf(location.y));
		DOMHelpers.addProperty(doc, componentElement, "width", String
				.valueOf(size.width));
		DOMHelpers.addProperty(doc, componentElement, "height", String
				.valueOf(size.height));
		DOMHelpers.addProperty(doc, componentElement, "locked", locked ? "true"
				: "false");
		if (includeBackgroundColor) {
			Element backColorPropElement = DOMHelpers.addProperty(doc,
					componentElement, "backgroundColor");
			Element backColorElement = doc.createElement("bean");
			backColorElement.setAttribute("class", "java.awt.Color");
			backColorPropElement.appendChild(backColorElement);
			Element constructorArg = doc.createElement("constructor-arg");
			constructorArg.setAttribute("type", "int");
			constructorArg.setAttribute("value", String.valueOf(backgroundColor
					.getRed()));
			backColorElement.appendChild(constructorArg);
			constructorArg = doc.createElement("constructor-arg");
			constructorArg.setAttribute("type", "int");
			constructorArg.setAttribute("value", String.valueOf(backgroundColor
					.getGreen()));
			backColorElement.appendChild(constructorArg);
			constructorArg = doc.createElement("constructor-arg");
			constructorArg.setAttribute("type", "int");
			constructorArg.setAttribute("value", String.valueOf(backgroundColor
					.getBlue()));
			backColorElement.appendChild(constructorArg);
		}
	}

	protected void deserialize(Document doc, Node componentNode) {
		setPropertyValue(XPOS_PROP, DOMHelpers.getSimpleProperty(componentNode,
				"x"));
		setPropertyValue(YPOS_PROP, DOMHelpers.getSimpleProperty(componentNode,
				"y"));
		setPropertyValue(WIDTH_PROP, DOMHelpers.getSimpleProperty(
				componentNode, "width"));
		setPropertyValue(HEIGHT_PROP, DOMHelpers.getSimpleProperty(
				componentNode, "height"));
		setPropertyValue(LOCK_SIZE_AND_POSITION_PROP, DOMHelpers
				.getSimpleProperty(componentNode, "locked"));
		if (DOMHelpers.getPropertyNode(componentNode, "backgroundColor") != null) {
			Color bc = DOMHelpers.getColorProperty(componentNode,
					"backgroundColor");
			setPropertyValue(BACK_COLOR_PROP, new RGB(bc.getRed(), bc
					.getGreen(), bc.getBlue()));
		}
	}

	public void save(IMemento memento) {
		memento.putInteger(TAG_X, location.x);
		memento.putInteger(TAG_Y, location.y);
		memento.putInteger(TAG_WIDTH, size.width);
		memento.putInteger(TAG_HEIGHT, size.height);
		memento.putBoolean(TAG_LOCKED, locked);
		memento.putInteger(TAG_BACK_RED, backgroundColor.getRed());
		memento.putInteger(TAG_BACK_GREEN, backgroundColor.getGreen());
		memento.putInteger(TAG_BACK_BLUE, backgroundColor.getBlue());
	}

	public void load(IMemento memento) {
		location = new Point(memento.getInteger(TAG_X), memento
				.getInteger(TAG_Y));
		size = new Dimension(memento.getInteger(TAG_WIDTH), memento
				.getInteger(TAG_HEIGHT));
		locked = memento.getBoolean(TAG_LOCKED);
		backgroundColor = new Color(memento.getInteger(TAG_BACK_RED), memento
				.getInteger(TAG_BACK_GREEN), memento.getInteger(TAG_BACK_BLUE));
	}

	void checkEquivalence(ComponentModelElement other) {
		assert (location.equals(other.location));
		assert (size.equals(other.size));
		assert (locked == other.locked);
		if (backgroundColor == null)
			assert (other.backgroundColor == null);
		else
			assert (backgroundColor.equals(other.backgroundColor));
	}

	/**
	 * Return the Location of this shape.
	 * 
	 * @return a non-null location instance
	 */
	public Point getLocation() {
		return location.getCopy();
	}

	/**
	 * Returns an array of IPropertyDescriptors for this shape.
	 * <p>
	 * The returned array is used to fill the property view, when the edit-part
	 * corresponding to this model element is selected.
	 * </p>
	 * 
	 * @see #descriptors
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/**
	 * Return the property value for the given propertyId, or null.
	 * <p>
	 * The property view uses the IDs from the IPropertyDescriptors array to
	 * obtain the value of the corresponding properties.
	 * </p>
	 * 
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public Object getPropertyValue(Object propertyId) {
		if (XPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.x);
		}
		if (YPOS_PROP.equals(propertyId)) {
			return Integer.toString(location.y);
		}
		if (HEIGHT_PROP.equals(propertyId)) {
			return Integer.toString(size.height);
		}
		if (WIDTH_PROP.equals(propertyId)) {
			return Integer.toString(size.width);
		}
		if (BACK_COLOR_PROP.equals(propertyId)) {
			if (backgroundColor == null)
				return new RGB(0, 0, 0);

			RGB v = new RGB(backgroundColor.getRed(), backgroundColor
					.getGreen(), backgroundColor.getBlue());
			return v;
		}
		if (LOCATION_PROP.equals(propertyId)) {
			return locationPropertySource;
		}
		if (SIZE_PROP.equals(propertyId)) {
			return dimensionPropertySource;
		}
		if (LOCK_SIZE_AND_POSITION_PROP.equals(propertyId)) {
			return locked;
		}
		return super.getPropertyValue(propertyId);
	}

	/**
	 * Return the Size of this shape.
	 * 
	 * @return a non-null Dimension instance
	 */
	public Dimension getSize() {
		return size.getCopy();
	}

	/**
	 * Container subclasses should move their children recursively by the
	 * specified amount.
	 * 
	 * @param moveBy
	 *            The amount to move by in the x and y axis.s
	 */
	public abstract void relocateChildren(Point moveBy);

	/**
	 * Set the Location of this shape.
	 * 
	 * @param newLocation
	 *            a non-null Point instance
	 * @throws IllegalArgumentException
	 *             if the parameter is null
	 */
	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException();
		}

		if (locked)
			return;

		Point moveBy = newLocation.getCopy().translate(location.getNegated());
		relocateChildren(moveBy);

		location.setLocation(newLocation);
		firePropertyChange(LOCATION_PROP, null, location);
	}

	/**
	 * Set the property value for the given property id. If no matching id is
	 * found, the call is forwarded to the superclass.
	 * <p>
	 * The property view uses the IDs from the IPropertyDescriptors array to set
	 * the values of the corresponding properties.
	 * </p>
	 * 
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		if (XPOS_PROP.equals(propertyId)) {
			if (locked)
				return;
			int x = Integer.parseInt((String) value);
			setLocation(new Point(x, location.y));
		} else if (YPOS_PROP.equals(propertyId)) {
			if (locked)
				return;
			int y = Integer.parseInt((String) value);
			setLocation(new Point(location.x, y));
		} else if (HEIGHT_PROP.equals(propertyId)) {
			if (locked)
				return;
			int height = Integer.parseInt((String) value);
			setSize(new Dimension(size.width, height));
		} else if (WIDTH_PROP.equals(propertyId)) {
			if (locked)
				return;
			int width = Integer.parseInt((String) value);
			setSize(new Dimension(width, size.height));
		} else if (LOCK_SIZE_AND_POSITION_PROP.equals(propertyId)) {
			if (value instanceof String) {
				// We are being deserialized
				String oldValue = locked ? "true" : "false";
				locked = value.equals("true");
				firePropertyChange(LOCK_SIZE_AND_POSITION_PROP, oldValue, value);
				return;
			}
			Boolean oldValue = locked;
			locked = ((Boolean) value).booleanValue();
			firePropertyChange(LOCK_SIZE_AND_POSITION_PROP, oldValue, locked);
		} else if (BACK_COLOR_PROP.equals(propertyId)) {
			RGB oldValue = backgroundColor == null ? new RGB(0, 0, 0)
					: new RGB(backgroundColor.getRed(), backgroundColor
							.getGreen(), backgroundColor.getBlue());
			RGB rgbValue = (RGB) value;
			backgroundColor = new Color(rgbValue.red, rgbValue.green,
					rgbValue.blue);
			firePropertyChange(BACK_COLOR_PROP, oldValue, value);
		} else if (LOCATION_PROP.equals(propertyId)) {
			setLocation((Point) value);
		} else if (SIZE_PROP.equals(propertyId)) {
			setSize((Dimension) value);
		} else {
			super.setPropertyValue(propertyId, value);
		}
	}

	/**
	 * Set the Size of this shape. Will not modify the size if newSize is null.
	 * 
	 * @param newSize
	 *            a non-null Dimension instance or null
	 */
	public void setSize(Dimension newSize) {
		if (locked)
			return;

		if (newSize != null) {
			size.setSize(newSize);
			firePropertyChange(SIZE_PROP, null, size);
		}
	}

	public void deepCopy(ComponentModelElement cme) {
		backgroundColor = new Color(cme.backgroundColor.getRGB());
		location = new Point(cme.location);
		size = new Dimension(cme.size);
	}

	@Override
	public void add(ModelElement child) {
		// Container subclasses should override
	}

	@Override
	public boolean removeChild(ModelElement child) {
		// Container subclasses should override
		return false;
	}

	@Override
	public List<ModelElement> getChildren() {
		// Container subclasses should override
		return new ArrayList<ModelElement>();
	}

	@Override
	public void insertChildAt(int index, ModelElement child) {
		// Container subclasses should override
	}

	@Override
	public boolean isFirstChild(ModelElement child) {
		// Container subclasses should override
		return false;
	}

	@Override
	public boolean isLastChild(ModelElement child) {
		// Container subclasses should override
		return false;
	}

	@Override
	public boolean moveChildDown(ModelElement child) {
		// Container subclasses should override
		return false;
	}

	@Override
	public boolean moveChildUp(ModelElement child) {
		// Container subclasses should override
		return false;
	}

	@Override
	public void resizeBy(double x, double y) {
		location.x = (int) (location.x * x);
		location.y = (int) (location.y * y);
		size.width = (int) (size.width * x);
		size.height = (int) (size.height * y);
	}

	public LayoutGuide getVerticalGuide() {
		return verticalGuide;
	}

	public void setVerticalGuide(LayoutGuide verticalGuide) {
		this.verticalGuide = verticalGuide;
	}

	public LayoutGuide getHorizontalGuide() {
		return horizontalGuide;
	}

	public void setHorizontalGuide(LayoutGuide horizontalGuide) {
		this.horizontalGuide = horizontalGuide;
	}

	/**
	 * Test if the point is contained inside this region. Delegates to
	 * {@link #containsPoint(Point, boolean)} with a <code>true</code> flag.
	 * 
	 * @param point
	 *            the point to check
	 * @return boolean <code>true</code> if the point is contained inside the
	 *         region
	 */
	public boolean containsPoint(Point point) {
		return containsPoint(point, true);
	}

	/**
	 * Test if the point is contained inside this region. The test is for a
	 * "proper" difference, i.e. the edge of the region is considered outside.
	 * 
	 * @param point
	 *            the point to check
	 * @param translate
	 *            if <code>true</code> the point is first translated to the
	 *            element's coordinate system
	 * @return boolean <code>true</code> if the point is contained inside the
	 *         region
	 */
	public boolean containsPoint(Point point, boolean translate) {
		// translate if necessary
		Point newPoint = translate ? point.translate(location) : point;

		// it is contained if it is between upper left and lower right corners
		Point lowerRight = getLowerRight();

		return ((newPoint.x < lowerRight.x) && (newPoint.y < lowerRight.y)
				&& (newPoint.x > location.x) && (newPoint.y > location.y));
	}

	/**
	 * @return Point the lower right point depending on location and dimension
	 */
	public Point getLowerRight() {
		return getLocation().translate(getSize());
	}
}
