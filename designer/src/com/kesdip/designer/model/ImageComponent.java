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

import com.kesdip.designer.constenum.ResourceListCellEditorTypes;
import com.kesdip.designer.properties.ResourceListPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class ImageComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/camera.png");

	private static final long serialVersionUID = 1L;

	/**
	 * A static array of property descriptors. There is one IPropertyDescriptor
	 * entry per editable property.
	 * 
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the images property value. */
	public static final String IMAGE_PROP = "Image.ImagesProp";
	/** Property ID to use for the duration property value. */
	public static final String DURATION_PROP = "Layout.DurationProp";
	/** Property ID to use when an image is added to this image component. */
	public static final String IMAGE_ADDED_PROP = "Image.ImageAdded";
	/** Property ID to use when an image is removed from this image component. */
	public static final String IMAGE_REMOVED_PROP = "Image.ImageRemoved";

	/* STATE */
	private List<Resource> images;
	private int duration;

	public ImageComponent() {
		images = new ArrayList<Resource>();
		duration = 0;
	}

	@Override
	protected Element serialize(Document doc, boolean isPublish) {
		Element imageElement = doc.createElement("bean");
		imageElement
				.setAttribute("class", "com.kesdip.player.components.Image");
		DOMHelpers.addProperty(doc, imageElement, "duration", String
				.valueOf(duration));
		super.serialize(doc, imageElement);
		Element contentPropElement = DOMHelpers.addProperty(doc, imageElement,
				"contents");
		Element listElement = doc.createElement("list");
		contentPropElement.appendChild(listElement);
		for (Resource r : images) {
			Element resourceElement = r.serialize(doc, isPublish);
			listElement.appendChild(resourceElement);
		}
		return imageElement;
	}

	@Override
	protected void deserialize(Document doc, Node componentNode) {
		setPropertyValue(DURATION_PROP, DOMHelpers.getSimpleProperty(
				componentNode, "duration"));
		super.deserialize(doc, componentNode);
		final List<Resource> newImages = new ArrayList<Resource>();
		DOMHelpers.applyToListProperty(doc, componentNode, "contents", "bean",
				new DOMHelpers.INodeListVisitor() {
					@Override
					public void visitListItem(Document doc, Node listItem) {
						if (!DOMHelpers.checkAttribute(listItem, "class",
								"com.kesdip.player.components.Resource")) {
							throw new RuntimeException(
									"Unexpected resource class: "
											+ listItem.getAttributes()
													.getNamedItem("class")
													.getNodeValue());
						}
						Resource r = new Resource("", "");
						r.deserialize(doc, listItem);
						newImages.add(r);
					}
				});
		images = newImages;
	}

	public void save(IMemento memento) {
		super.save(memento);
		memento.putInteger(TAG_DURATION, duration);
		/*
		 * Do not save resources. for (Resource r : images) { IMemento child =
		 * memento.createChild(TAG_RESOURCE); r.save(child); }
		 */
	}

	public void load(IMemento memento) {
		super.load(memento);
		duration = memento.getInteger(TAG_DURATION);
		IMemento[] children = memento.getChildren(TAG_RESOURCE);
		for (IMemento child : children) {
			Resource r = new Resource("", "");
			r.load(child);
			images.add(r);
		}
	}

	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert (other instanceof ImageComponent);
		assert (duration == ((ImageComponent) other).duration);
		for (int i = 0; i < images.size(); i++) {
			Resource resource = images.get(i);
			Resource otherResource = ((ImageComponent) other).images.get(i);
			resource.checkEquivalence(otherResource);
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
				new ResourceListPropertyDescriptor(
						IMAGE_PROP,
						"Images",
						ResourceListCellEditorTypes.GENERIC_RESOURCE_LIST_EDITOR),
				new TextPropertyDescriptor(DURATION_PROP, "Duration") };
		// use a custom cell editor validator for the array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
			((PropertyDescriptor) descriptors[i])
					.setValidator(new ICellEditorValidator() {
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
			return images;
		else if (DURATION_PROP.equals(propertyId))
			return String.valueOf(duration);
		else
			return super.getPropertyValue(propertyId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (IMAGE_PROP.equals(propertyId)) {
			List<Resource> oldValue = images;
			images = (List<Resource>) value;
			firePropertyChange(IMAGE_PROP, oldValue, images);
		} else if (DURATION_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(duration);
			duration = Integer.parseInt((String) value);
			firePropertyChange(DURATION_PROP, oldValue, value);
		} else
			super.setPropertyValue(propertyId, value);
	}

	/**
	 * Add an image to this image component.
	 * 
	 * @param i
	 *            a non-null image instance
	 * @return true, iff the image was added, false otherwise
	 */
	public boolean addImage(Resource i) {
		if (i != null && images.add(i)) {
			firePropertyChange(IMAGE_ADDED_PROP, null, i);
			return true;
		}
		return false;
	}

	/**
	 * Return a List of images in this component. The returned List should not
	 * be modified.
	 */
	public List<Resource> getImages() {
		return images;
	}

	/**
	 * Remove an image from this image component.
	 * 
	 * @param i
	 *            a non-null image instance;
	 * @return true, iff the image was removed, false otherwise
	 */
	public boolean removeImage(Resource i) {
		if (i != null && images.remove(i)) {
			firePropertyChange(IMAGE_REMOVED_PROP, null, i);
			return true;
		}
		return false;
	}

	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}

	public ModelElement deepCopy() {
		ImageComponent retVal = new ImageComponent();
		retVal.deepCopy(this);
		retVal.duration = this.duration;
		for (Resource r : images) {
			retVal.images.add(Resource.deepCopy(r));
		}
		return retVal;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}

	public String toString() {
		boolean first = true;
		StringBuilder sb = new StringBuilder("[");
		for (Resource r : images) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(r.toString());
		}
		sb.append("]");
		return "Image: " + sb.toString();
	}

}
