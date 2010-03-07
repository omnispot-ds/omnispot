package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kesdip.designer.properties.CheckboxPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class Region extends ComponentModelElement {

	private static final long serialVersionUID = 8142189250205205732L;

	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/film_folder.png");

	/**
	 * A static array of property descriptors. There is one IPropertyDescriptor
	 * entry per editable property.
	 * 
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the name property value. */
	public static final String NAME_PROP = "Region.NameProp";
	/** Property ID to use for the transparent property value. */
	public static final String TRANSPARENT_PROP = "Region.TransparentProp";
	/** Property ID to use when a component is added to this region. */
	public static final String COMPONENT_ADDED_PROP = "Region.ComponentAdded";
	/** Property ID to use when a component is removed from this region. */
	public static final String COMPONENT_REMOVED_PROP = "Region.ComponentRemoved";

	/* STATE */
	private String name;
	private boolean isTransparent;
	private List<ModelElement> contents;

	public Region() {
		name = "New Region";
		isTransparent = false;
		contents = new ArrayList<ModelElement>();
	}

	@Override
	protected Element serialize(Document doc, boolean isPublish) {
		throw new RuntimeException(
				"Normal component serialization called for a region.");
	}

	protected Element serialize(Document doc, int layoutNumber,
			int regionNumber, boolean isPublish) {
		Element regionElement = doc.createElement("bean");
		regionElement.setAttribute("id", "frame" + layoutNumber + "_"
				+ regionNumber);
		regionElement.setAttribute("class",
				"com.kesdip.player.components.RootContainer");
		super.serialize(doc, regionElement, !isTransparent);
		DOMHelpers.addProperty(doc, regionElement, "name", name);
		DOMHelpers.addProperty(doc, regionElement, "isTransparent",
				isTransparent ? "true" : "false");
		Element contentsElement = DOMHelpers.addProperty(doc, regionElement,
				"contents");
		Element listElement = doc.createElement("list");
		contentsElement.appendChild(listElement);
		for (ModelElement element : contents) {
			ComponentModelElement component = (ComponentModelElement) element;
			Element componentElement = component.serialize(doc, isPublish);
			listElement.appendChild(componentElement);
		}

		doc.getDocumentElement().appendChild(regionElement);

		Element refElement = doc.createElement("ref");
		refElement.setAttribute("bean", "frame" + layoutNumber + "_"
				+ regionNumber);

		return refElement;
	}

	protected void deserialize(Document doc, Node refNode) {
		String beanID = refNode.getAttributes().getNamedItem("bean")
				.getNodeValue();

		final List<ModelElement> newContents = new ArrayList<ModelElement>();
		NodeList nl = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node beanNode = nl.item(i);
			if (beanNode.getNodeType() == Node.ELEMENT_NODE
					&& beanNode.getNodeName().equals("bean")
					&& DOMHelpers.checkAttribute(beanNode, "id", beanID)) {
				setPropertyValue(NAME_PROP, DOMHelpers.getSimpleProperty(
						beanNode, "name"));
				setPropertyValue(TRANSPARENT_PROP, DOMHelpers
						.getSimpleProperty(beanNode, "isTransparent"));
				super.deserialize(doc, beanNode);
				DOMHelpers.applyToListProperty(doc, beanNode, "contents",
						"bean", new DOMHelpers.INodeListVisitor() {
							@Override
							public void visitListItem(Document doc,
									Node listItem) {
								String className = listItem.getAttributes()
										.getNamedItem("class").getNodeValue();
								ComponentModelElement component;
								if ("com.kesdip.player.components.Ticker"
										.equals(className)) {
									component = new TickerComponent();
								} else if ("com.kesdip.player.components.Video"
										.equals(className) || "com.kesdip.player.components.media.FileVideo"
										.equals(className)) {
									component = new VideoComponent();
								} else if ("com.kesdip.player.components.TunerVideo"
										.equals(className)|| "com.kesdip.player.components.media.TunerVideo"
										.equals(className)) {
									component = new TunerVideoComponent();
								} else if ("com.kesdip.player.components.Image"
										.equals(className)) {
									component = new ImageComponent();
								} else if ("com.kesdip.player.components.clock.Clock"
										.equals(className)) {
									component = new ClockComponent();
								} else if ("com.kesdip.player.components.FlashComponent"
										.equals(className)) {
									component = new FlashComponent();
								} else if ("com.kesdip.player.components.weather.FlashWeatherComponent"
										.equals(className)) {
									component = new FlashWeatherComponent();
								} else {
									throw new RuntimeException(
											"Unexpected class name: "
													+ className);
								}
								component.deserialize(doc, listItem);
								component.setParent(Region.this);
								newContents.add(component);
							}
						});
				break;
			}
		}
		contents = newContents;
	}

	public void save(IMemento memento) {
		super.save(memento);
		memento.putString(TAG_NAME, name);
		memento.putBoolean(TAG_IS_TRANSPARENT, isTransparent);
		for (ModelElement e : contents) {
			ComponentModelElement element = (ComponentModelElement) e;
			IMemento child = memento.createChild(TAG_COMPONENT);
			if (element instanceof VideoComponent)
				child.putString(TAG_COMPONENT_TYPE, TYPE_VIDEO);
			else if (element instanceof TunerVideoComponent)
				child.putString(TAG_COMPONENT_TYPE, TYPE_TUNER_VIDEO);
			else if (element instanceof TickerComponent)
				child.putString(TAG_COMPONENT_TYPE, TYPE_TICKER);
			else if (element instanceof ImageComponent)
				child.putString(TAG_COMPONENT_TYPE, TYPE_IMAGE);
			else if (element instanceof ClockComponent)
				child.putString(TAG_COMPONENT_TYPE, TYPE_CLOCK);
			else if (element instanceof FlashComponent)
				child.putString(TAG_COMPONENT_TYPE, TYPE_FLASH);
			else if (element instanceof FlashWeatherComponent)
				child.putString(TAG_COMPONENT_TYPE, TYPE_WEATHER);
			else
				throw new RuntimeException("Unexpected component type: "
						+ element.getClass().getName());
			element.save(child);
		}
	}

	public void load(IMemento memento) {
		super.load(memento);
		name = memento.getString(TAG_NAME);
		isTransparent = memento.getBoolean(TAG_IS_TRANSPARENT);
		IMemento[] children = memento.getChildren(TAG_COMPONENT);
		for (IMemento child : children) {
			String type = child.getString(TAG_COMPONENT_TYPE);
			if (TYPE_VIDEO.equals(type)) {
				VideoComponent v = new VideoComponent();
				v.load(child);
			} else if (TYPE_VIDEO.equals(type)) {
				TunerVideoComponent v = new TunerVideoComponent();
				v.load(child);
			} else if (TYPE_TICKER.equals(type)) {
				TickerComponent t = new TickerComponent();
				t.load(child);
			} else if (TYPE_IMAGE.equals(type)) {
				ImageComponent i = new ImageComponent();
				i.load(child);
			} else if (TYPE_CLOCK.equals(type)) {
				ClockComponent i = new ClockComponent();
				i.load(child);
			} else if (TYPE_FLASH.equals(type)) {
				FlashComponent f = new FlashComponent();
				f.load(child);
			} else if (TYPE_WEATHER.equals(type)) {
				FlashWeatherComponent w = new FlashWeatherComponent();
				w.load(child);
			} else {
				throw new RuntimeException("Unexpected component type: " + type);
			}
		}
	}

	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		if (!(other instanceof Region))
			throw new RuntimeException(
					"A region can only be equivalent to a region.");
		assert (name.equals(((Region) other).name));
		assert (isTransparent == ((Region) other).isTransparent);
		assert (getLocation().equals(other.getLocation()));
		assert (getSize().equals(other.getSize()));
		for (int i = 0; i < contents.size(); i++) {
			ComponentModelElement component = (ComponentModelElement) contents
					.get(i);
			ComponentModelElement otherComponent = (ComponentModelElement) ((Region) other).contents
					.get(i);
			component.checkEquivalence(otherComponent);
		}
	}

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
				new TextPropertyDescriptor(NAME_PROP, "Name"),
				new CheckboxPropertyDescriptor(TRANSPARENT_PROP, "Transparent") };
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
			((PropertyDescriptor) descriptors[i])
					.setValidator(new ICellEditorValidator() {
						public String isValid(Object value) {
							// No validation needed.
							return null;
						}
					});
		}
	} // static

	public void relocateChildren(Point moveBy) {
		for (ModelElement elem : contents) {
			ComponentModelElement e = (ComponentModelElement) elem;
			e.setPropertyValue(ComponentModelElement.LOCATION_PROP, e.location
					.getCopy().translate(moveBy));
		}
	}

	public ModelElement deepCopy() {
		Region retVal = new Region();
		retVal.deepCopy(this);
		retVal.name = this.name;
		retVal.isTransparent = this.isTransparent;
		for (ModelElement srce : this.contents) {
			ComponentModelElement e = (ComponentModelElement) srce.deepCopy();
			retVal.contents.add(e);
		}
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
		else if (TRANSPARENT_PROP.equals(propertyId))
			return isTransparent;
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (NAME_PROP.equals(propertyId)) {
			String oldValue = name;
			name = (String) value;
			firePropertyChange(NAME_PROP, oldValue, name);
		} else if (TRANSPARENT_PROP.equals(propertyId)) {
			if (value instanceof String) {
				// We are being deserialized
				String oldValue = isTransparent ? "true" : "false";
				isTransparent = value.equals("true");
				firePropertyChange(TRANSPARENT_PROP, oldValue, value);
				return;
			}
			Boolean oldValue = isTransparent;
			isTransparent = ((Boolean) value).booleanValue();
			firePropertyChange(TRANSPARENT_PROP, oldValue, isTransparent);
		} else
			super.setPropertyValue(propertyId, value);
	}

	@Override
	public void add(ModelElement child) {
		if (child != null && child instanceof ComponentModelElement
				&& contents.add(child)) {
			child.setParent(this);
			firePropertyChange(COMPONENT_ADDED_PROP, null, child);
		}
	}

	@Override
	public boolean removeChild(ModelElement child) {
		if (child != null && child instanceof ComponentModelElement
				&& contents.remove(child)) {
			child.setParent(null);
			firePropertyChange(COMPONENT_REMOVED_PROP, null, child);
			return true;
		}
		return false;
	}

	@Override
	public List<ModelElement> getChildren() {
		return contents;
	}

	@Override
	public void insertChildAt(int index, ModelElement child) {
		if (child != null && child instanceof ComponentModelElement) {
			child.setParent(this);
			contents.add(index, child);
			firePropertyChange(COMPONENT_ADDED_PROP, null, child);
		}
	}

	@Override
	public boolean isFirstChild(ModelElement child) {
		return contents.indexOf(child) == 0;
	}

	@Override
	public boolean isLastChild(ModelElement child) {
		return contents.indexOf(child) == contents.size() - 1
				&& contents.size() != 0;
	}

	@Override
	public boolean moveChildDown(ModelElement child) {
		int index = contents.indexOf(child);
		if (index == -1 || index == contents.size() - 1)
			return false;
		if (!contents.remove(child))
			return false;
		contents.add(index + 1, child);
		firePropertyChange(CHILD_MOVE_DOWN, null, child);
		return true;
	}

	@Override
	public boolean moveChildUp(ModelElement child) {
		int index = contents.indexOf(child);
		if (index < 1)
			return false;
		if (!contents.remove(child))
			return false;
		contents.add(index - 1, child);
		firePropertyChange(CHILD_MOVE_UP, null, child);
		return true;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}

	public String toString() {
		return name;
	}

	@Override
	public void resizeBy(double x, double y) {
		super.resizeBy(x, y);
		for (ModelElement c : contents)
			c.resizeBy(x, y);
	}

	/**
	 * Changes the coordinates of the element to make it appear within the area
	 * of the region, if necessary. Calls
	 * {@link #bringWithinBounds(ComponentModelElement, int, boolean)} with a
	 * <code>true</code> flag.
	 * 
	 * @param element
	 *            the element to move
	 * @param pixels
	 *            the number of pixels the point should be distanced from the
	 *            edge if moved
	 */
	public void bringWithinBounds(ComponentModelElement element, int pixels) {
		bringWithinBounds(element, pixels, true);
	}

	/**
	 * Changes the coordinates of the element to make it appear within the area
	 * of the region, if necessary.
	 * 
	 * @param element
	 *            the element to move
	 * @param pixels
	 *            the number of pixels the point should be distanced from the
	 *            edge if moved inside the area
	 * @param translate
	 *            if the point should be translated first to this element's
	 *            coordinates
	 */
	public void bringWithinBounds(ComponentModelElement element, int pixels,
			boolean translate) {
		// first translate to this coordinate system, if necessary
		Point point = translate ? element.getLocation().translate(location)
				: element.getLocation();
		// do nothing if already visible
		if (containsPoint(point, false)) {
			return;
		}
		Point lowerRight = getLowerRight();
		// different treatment depending on which edge it is closer
		int newX = 0;
		if (point.x > location.x && point.x < lowerRight.x) {
			// x is in bounds
			newX = point.x;
		} else if (point.x < location.x) {
			// x is to the left
			newX = location.x + pixels;
		} else {
			// x is to the right
			newX = lowerRight.x - pixels;
		}
		int newY = 0;
		if (point.y > location.y && point.y < lowerRight.y) {
			// y is in bounds
			newY = point.y;
		} else if (point.y < location.y) {
			// y is above
			newY = location.y + pixels;
		} else {
			// y is below
			newY = lowerRight.y - pixels;
		}
		// set the new location
		point.setLocation(newX, newY);
		element.setLocation(point);
	}
}
