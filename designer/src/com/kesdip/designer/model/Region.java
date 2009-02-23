package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kesdip.designer.utils.DOMHelpers;

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
	private List<ComponentModelElement> contents;
	
	public Region() {
		name = "New Region";
		isTransparent = false;
		contents = new ArrayList<ComponentModelElement>();
	}
	
	@Override
	protected Element serialize(Document doc) {
		throw new RuntimeException("Normal component serialization called for a region.");
	}
	
	protected Element serialize(Document doc, int layoutNumber, int regionNumber) {
		Element regionElement = doc.createElement("bean");
		regionElement.setAttribute("id", "frame" + layoutNumber + "_" + regionNumber);
		regionElement.setAttribute("class", "com.kesdip.player.components.RootContainer");
		super.serialize(doc, regionElement);
		DOMHelpers.addProperty(doc, regionElement, "name", name);
		DOMHelpers.addProperty(doc, regionElement, "isTransparent",
				isTransparent ? "true" : "false");
		Element contentsElement = DOMHelpers.addProperty(doc, regionElement, "contents");
		Element listElement = doc.createElement("list");
		contentsElement.appendChild(listElement);
		for (ComponentModelElement component : contents) {
			Element componentElement = component.serialize(doc);
			listElement.appendChild(componentElement);
		}
		
		doc.getDocumentElement().appendChild(regionElement);
		
		Element refElement = doc.createElement("ref");
		refElement.setAttribute("bean", "frame" + layoutNumber + "_" + regionNumber);

		return refElement;
	}
	
	protected void deserialize(Document doc, Node refNode) {
		String beanID = refNode.getAttributes().getNamedItem("bean").getNodeValue();
		
		final List<ComponentModelElement> newContents =
			new ArrayList<ComponentModelElement>();
		NodeList nl = doc.getDocumentElement().getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node beanNode = nl.item(i);
			if (beanNode.getNodeType() == Node.ELEMENT_NODE &&
					beanNode.getNodeName().equals("bean") &&
					DOMHelpers.checkAttribute(beanNode, "id", beanID)) {
				setPropertyValue(NAME_PROP,
						DOMHelpers.getSimpleProperty(beanNode, "name"));
				setPropertyValue(TRANSPARENT_PROP,
						DOMHelpers.getSimpleProperty(beanNode, "isTransparent"));
				super.deserialize(doc, beanNode);
				DOMHelpers.applyToListProperty(doc, beanNode, "contents", "bean",
						new DOMHelpers.INodeListVisitor() {
					@Override
					public void visitListItem(Document doc, Node listItem) {
						String className = listItem.getAttributes().
							getNamedItem("class").getNodeValue();
						ComponentModelElement component;
						if ("com.kesdip.player.components.Ticker".equals(className)) {
							component = new TickerComponent();
						} else if ("com.kesdip.player.components.Video".equals(className)) {
							component = new VideoComponent();
						} else if ("com.kesdip.player.components.Image".equals(className)) {
							component = new ImageComponent();
						} else if ("com.kesdip.player.components.FlashComponent".equals(className)) {
							component = new FlashComponent();
						} else if ("com.kesdip.player.components.weather.FlashWeatherComponent".equals(className)) {
							component = new FlashWeatherComponent();
						} else {
							throw new RuntimeException("Unexpected class name: " + className);
						}
						component.deserialize(doc, listItem);
						newContents.add(component);
					}
				});
				break;
			}
		}
		contents = newContents;
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		if (!(other instanceof Region))
			throw new RuntimeException("A region can only be equivalent to a region.");
		assert(name.equals(((Region) other).name));
		assert(isTransparent == ((Region) other).isTransparent);
		assert(getLocation().equals(other.getLocation()));
		assert(getSize().equals(other.getSize()));
		for (int i = 0; i < contents.size(); i++) {
			ComponentModelElement component = contents.get(i);
			ComponentModelElement otherComponent = ((Region) other).contents.get(i);
			component.checkEquivalence(otherComponent);
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
				new TextPropertyDescriptor(TRANSPARENT_PROP, "Transparent")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getId().equals(TRANSPARENT_PROP)) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						String v = (String) value;
						if (v == null)
							return null;
						if (!v.equals("true") && !v.equals("false"))
							return "Only true or false values are allowed. " +
									"Invalid value: " + v;
						return null;
					}
				});
			} else {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						// No validation for the name.
						return null;
					}
				});
			}
		}
	} // static

	/** 
	 * Add a component to this region.
	 * @param s a non-null component instance
	 * @return true, iff the component was added, false otherwise
	 */
	public boolean addComponent(ComponentModelElement s) {
		if (s != null && contents.add(s)) {
			s.setDeployment(deployment);
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
			s.setDeployment(null);
			firePropertyChange(COMPONENT_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}
	
	public void relocateChildren(Point moveBy) {
		for (ComponentModelElement e : contents) {
			e.setPropertyValue(ComponentModelElement.LOCATION_PROP,
					e.location.getCopy().translate(moveBy));
		}
	}
	
	public ModelElement deepCopy() {
		Region retVal = new Region();
		deepCopy(retVal);
		retVal.name = this.name;
		retVal.isTransparent = this.isTransparent;
		retVal.deployment = null;
		for (ComponentModelElement srce : this.contents) {
			ComponentModelElement e = (ComponentModelElement) srce.deepCopy();
			retVal.contents.add(e);
		}
		return retVal;
	}

	private Deployment deployment;
	
	public void setDeployment(Deployment deployment) {
		this.deployment = deployment;
		for (ComponentModelElement r : contents)
			r.setDeployment(deployment);
	}

	public Deployment getDeployment() {
		return deployment;
	}
	
	public ModelElement removeChild(ModelElement child) {
		if (child instanceof ComponentModelElement) {
			if (removeComponent((ComponentModelElement) child))
				return this;
			return null;
		}
		
		for (ComponentModelElement r : contents) {
			ModelElement e = r.removeChild(child);
			if (e != null)
				return e;
		}
		
		return null;
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
			return isTransparent ? "true" : "false";
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
			String oldValue = isTransparent ? "true" : "false";
			isTransparent = "true".equals(value);
			firePropertyChange(TRANSPARENT_PROP, oldValue, isTransparent ? "true" : "false");
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
