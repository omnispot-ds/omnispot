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

import com.kesdip.designer.properties.ResourcePropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class ClockComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/clock.png");
	
	private static final long serialVersionUID = 1L;
	
	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the image property value. */
	public static final String IMAGE_PROP = "Clock.ImageProp";

	/* STATE */
	private Resource image;
	
	public ClockComponent() {
		image = null;
	}

	@Override
	protected Element serialize(Document doc, boolean isPublish) {
		Element imageElement = doc.createElement("bean");
		imageElement.setAttribute("class", "com.kesdip.player.components.Clock");
		super.serialize(doc, imageElement);
		//Element contentPropElement = DOMHelpers.addProperty(doc, imageElement, "imageResource");
		//Element resourceElement = image.serialize(doc, isPublish);
		//contentPropElement.appendChild(resourceElement);
		return imageElement;
	}
	
	@Override
	protected void deserialize(Document doc, Node componentNode) {
		super.deserialize(doc, componentNode);
		Node contentPropNode = DOMHelpers.getPropertyNode(componentNode, "imageResource");
//		image = new Resource("", "");
//		image.deserialize(doc, contentPropNode);
	}
	
	public void save(IMemento memento) {
		super.save(memento);
		/*
		 * Do not save resources.
		for (Resource r : images) {
			IMemento child = memento.createChild(TAG_RESOURCE);
			r.save(child);
		}
		 */
	}
	
	public void load(IMemento memento) {
		super.load(memento);
		IMemento child = memento.getChild(TAG_RESOURCE);
		image = new Resource("", "");
		image.load(child);
	}
		
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof ClockComponent);
		Resource resource = image;
		Resource otherResource = ((ClockComponent) other).image;
		resource.checkEquivalence(otherResource);
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new ResourcePropertyDescriptor(IMAGE_PROP, "Image"),
		};
		// use a custom cell editor validator for the array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				public String isValid(Object value) {
					// No validation for the images or duration.
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
		if (IMAGE_PROP.equals(propertyId))
			return image;
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (IMAGE_PROP.equals(propertyId)) {
			Resource oldValue = image;
			image = (Resource) value;
			firePropertyChange(IMAGE_PROP, oldValue, image);
		} else
			super.setPropertyValue(propertyId, value);
	}
	
	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}
	
	public ModelElement deepCopy() {
		ClockComponent retVal = new ClockComponent();
		retVal.deepCopy(this);
		//retVal.image = Resource.deepCopy(image);
		return retVal;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		if (image != null)
			sb.append(image.toString());
		sb.append("]");
		return "Clock: " + sb.toString();
	}

}
