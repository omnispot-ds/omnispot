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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kesdip.designer.properties.FileChooserPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class FlashComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/flash_cs3.png");
	
	private static final long serialVersionUID = 1L;

	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the source property value. */
	public static final String SOURCE_PROP = "Flash.SourceProp";

	/* STATE */
	private String source;
	
	public FlashComponent() {
		source = "";
	}

	protected Element serialize(Document doc, boolean isPublish) {
		Element flashElement = doc.createElement("bean");
		flashElement.setAttribute("class", "com.kesdip.player.components.FlashComponent");
		super.serialize(doc, flashElement);
		Element sourcePropElement = DOMHelpers.addProperty(doc, flashElement, "source");
		Resource resource = new Resource(source, "");
		Element resourceElement = resource.serialize(doc, isPublish);
		sourcePropElement.appendChild(resourceElement);
		return flashElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		super.deserialize(doc, componentNode);
		Node propNode = DOMHelpers.getPropertyNode(componentNode, "source");
		NodeList children = propNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("bean")) {
				Resource r = new Resource("", "");
				r.deserialize(doc, child);
				setPropertyValue(SOURCE_PROP, r.getResource());
			}
		}
	}
	
	public void save(IMemento memento) {
		super.save(memento);
		memento.putString(TAG_SOURCE, source);
	}
	
	public void load(IMemento memento) {
		super.load(memento);
		source = memento.getString(TAG_SOURCE);
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof FlashComponent);
		assert(source.equals(((FlashComponent) other).source));
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new FileChooserPropertyDescriptor(SOURCE_PROP, "Source")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				public String isValid(Object value) {
					// No validation for the url.
					return null;
				}
			});
		}
	} // static

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
		if (SOURCE_PROP.equals(propertyId))
			return source;
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (SOURCE_PROP.equals(propertyId)) {
			String oldValue = source;
			source = (String) value;
			firePropertyChange(SOURCE_PROP, oldValue, source);
		} else
			super.setPropertyValue(propertyId, value);
	}
	
	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}
	
	public ModelElement deepCopy() {
		FlashComponent retVal = new FlashComponent();
		retVal.deepCopy(this);
		retVal.source = this.source;
		return retVal;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "Flash(" + source + ")";
	}

}
