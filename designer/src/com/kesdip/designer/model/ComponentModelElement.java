package com.kesdip.designer.model;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kesdip.designer.utils.DOMHelpers;

/**
 * Abstract prototype of an Component Element.
 * Has a size (width and height), a location (x and y position). Use subclasses to
 * instantiate a specific component element - e.g. an image component or a video component.
 * 
 * @author Pafsanias Ftakas
 */
public abstract class ComponentModelElement extends ModelElement {
	private static final long serialVersionUID = 5594247627329684698L;
	
	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID for the Height property value. */
	public static final String HEIGHT_PROP = "AdElement.Height";
	/** Property ID for the Width property value. */
	public static final String WIDTH_PROP = "AdElement.Width";
	/** Property ID for the X property value.  */
	public static final String XPOS_PROP = "AdElement.xPos";
	/** Property ID for the Y property value.  */
	public static final String YPOS_PROP = "AdElement.yPos";
	/** Property ID for the Z property value. */
	public static final String ZPOS_PROP = "AdElement.zPos";
	/** Property ID to use when the location of this shape is modified. */
	public static final String LOCATION_PROP = "AdElement.Location";
	/** Property ID to use then the size of this shape is modified. */
	public static final String SIZE_PROP = "AdElement.Size";

	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(XPOS_PROP, "X"), // id and description pair
				new TextPropertyDescriptor(YPOS_PROP, "Y"),
				new TextPropertyDescriptor(WIDTH_PROP, "Width"),
				new TextPropertyDescriptor(HEIGHT_PROP, "Height"),
				new TextPropertyDescriptor(ZPOS_PROP, "ZOrder"),
		};
		// use a custom cell editor validator for all five array entries
		for (int i = 0; i < descriptors.length; i++) {
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
		}
	} // static

	/** Location of this shape. */
	private Point location = new Point(0, 0);
	/** Size of this shape. */
	private Dimension size = new Dimension(50, 50);
	private int zorder = 0;
	
	abstract Element serialize(Document doc);
	
	protected void serialize(Document doc, Element componentElement) {
		DOMHelpers.addProperty(doc, componentElement, "x",
				String.valueOf(location.x));
		DOMHelpers.addProperty(doc, componentElement, "y",
				String.valueOf(location.y));
		DOMHelpers.addProperty(doc, componentElement, "width",
				String.valueOf(size.width));
		DOMHelpers.addProperty(doc, componentElement, "height",
				String.valueOf(size.height));
		// TODO Do we need zorder? make sure all component model elements support it.
		// DOMHelpers.addProperty(doc, componentElement, "zOrder", String.valueOf(zorder));
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		setPropertyValue(XPOS_PROP, DOMHelpers.getSimpleProperty(componentNode, "x"));
		setPropertyValue(YPOS_PROP, DOMHelpers.getSimpleProperty(componentNode, "y"));
		setPropertyValue(WIDTH_PROP, DOMHelpers.getSimpleProperty(componentNode, "width"));
		setPropertyValue(HEIGHT_PROP, DOMHelpers.getSimpleProperty(componentNode, "height"));
		// TODO Do we need zorder? make sure all component model elements support it.
		// setPropertyValue(ZPOS_PROP, DOMHelpers.getSimpleProperty(componentNode, "zOrder"));
	}
	
	abstract void checkEquivalence(ComponentModelElement other);

	/**
	 * Return the Location of this shape.
	 * @return a non-null location instance
	 */
	public Point getLocation() {
		return location.getCopy();
	}

	/**
	 * Returns an array of IPropertyDescriptors for this shape.
	 * <p>The returned array is used to fill the property view, when the edit-part corresponding
	 * to this model element is selected.</p>
	 * @see #descriptors
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return descriptors;
	}

	/**
	 * Return the property value for the given propertyId, or null.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array 
	 * to obtain the value of the corresponding properties.</p>
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
		if (ZPOS_PROP.equals(propertyId)) {
			return Integer.toString(zorder);
		}
		return super.getPropertyValue(propertyId);
	}

	/**
	 * Return the Size of this shape.
	 * @return a non-null Dimension instance
	 */
	public Dimension getSize() {
		return size.getCopy();
	}

	/**
	 * Set the Location of this shape.
	 * @param newLocation a non-null Point instance
	 * @throws IllegalArgumentException if the parameter is null
	 */
	public void setLocation(Point newLocation) {
		if (newLocation == null) {
			throw new IllegalArgumentException();
		}
		location.setLocation(newLocation);
		firePropertyChange(LOCATION_PROP, null, location);
	}

	/**
	 * Set the property value for the given property id.
	 * If no matching id is found, the call is forwarded to the superclass.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array to set the values
	 * of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		if (XPOS_PROP.equals(propertyId)) {
			int x = Integer.parseInt((String) value);
			setLocation(new Point(x, location.y));
		} else if (YPOS_PROP.equals(propertyId)) {
			int y = Integer.parseInt((String) value);
			setLocation(new Point(location.x, y));
		} else if (HEIGHT_PROP.equals(propertyId)) {
			int height = Integer.parseInt((String) value);
			setSize(new Dimension(size.width, height));
		} else if (WIDTH_PROP.equals(propertyId)) {
			int width = Integer.parseInt((String) value);
			setSize(new Dimension(width, size.height));
		} else if (ZPOS_PROP.equals(propertyId)) {
			int zorder = Integer.parseInt((String) value);
			setZorder(zorder);
		} else {
			super.setPropertyValue(propertyId, value);
		}
	}

	/**
	 * Set the Size of this shape.
	 * Will not modify the size if newSize is null.
	 * @param newSize a non-null Dimension instance or null
	 */
	public void setSize(Dimension newSize) {
		if (newSize != null) {
			size.setSize(newSize);
			firePropertyChange(SIZE_PROP, null, size);
		}
	}
	
	public void setZorder(int zorder) {
		this.zorder = zorder;
		firePropertyChange(ZPOS_PROP, null, zorder);
	}


}
