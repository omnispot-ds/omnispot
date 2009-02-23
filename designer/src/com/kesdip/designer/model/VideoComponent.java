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

import com.kesdip.designer.properties.ResourceListPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class VideoComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/alt_window_16.gif");
	
	private static final long serialVersionUID = 1L;

	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the name property value. */
	public static final String VIDEO_PROP = "Video.VideosProp";
	/** Property ID to use for the repeat property value. */
	public static final String REPEAT_PROP = "Video.RepeatProp";
	/** Property ID to use when a video is added to this video component. */
	public static final String VIDEO_ADDED_PROP = "Video.VideoAdded";
	/** Property ID to use when a video is removed from this video component. */
	public static final String VIDEO_REMOVED_PROP = "Video.VideoRemoved";

	/* STATE */
	private List<Resource> videos;
	private boolean repeat;
	
	public VideoComponent() {
		videos = new ArrayList<Resource>();
		repeat = false;
	}

	protected Element serialize(Document doc) {
		Element videoElement = doc.createElement("bean");
		videoElement.setAttribute("class", "com.kesdip.player.components.Video");
		super.serialize(doc, videoElement);
		DOMHelpers.addProperty(doc, videoElement, "repeat", repeat ? "true" : "false");
		Element contentPropElement = DOMHelpers.addProperty(doc, videoElement, "contents");
		Element listElement = doc.createElement("list");
		contentPropElement.appendChild(listElement);
		for (Resource r : videos) {
			Element resourceElement = r.serialize(doc);
			listElement.appendChild(resourceElement);
		}
		return videoElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		setPropertyValue(REPEAT_PROP, DOMHelpers.getSimpleProperty(componentNode, "repeat"));
		super.deserialize(doc, componentNode);
		final List<Resource> newVideos = new ArrayList<Resource>();
		DOMHelpers.applyToListProperty(doc, componentNode, "contents", "bean",
				new DOMHelpers.INodeListVisitor() {
			@Override
			public void visitListItem(Document doc, Node listItem) {
				if (!DOMHelpers.checkAttribute(
						listItem, "class", "com.kesdip.player.components.Resource")) {
					throw new RuntimeException("Unexpected resource class: " + 
							listItem.getAttributes().getNamedItem("class").getNodeValue());
				}
				Resource r = new Resource("", "");
				r.deserialize(doc, listItem);
				newVideos.add(r);
			}
		});
		videos = newVideos;
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof VideoComponent);
		assert(repeat == ((VideoComponent) other).repeat);
		for (int i = 0; i < videos.size(); i++) {
			Resource resource = videos.get(i);
			Resource otherResource = ((VideoComponent) other).videos.get(i);
			resource.checkEquivalence(otherResource);
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
				new ResourceListPropertyDescriptor(VIDEO_PROP, "Videos"),
				new TextPropertyDescriptor(REPEAT_PROP, "Repeat")
		};
		// use a custom cell editor validator for the array entries
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getId().equals(REPEAT_PROP)) {
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
						// No validation for the videos.
						return null;
					}
				});
			}
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
		if (VIDEO_PROP.equals(propertyId))
			return videos;
		else if (REPEAT_PROP.equals(propertyId))
			return repeat ? "true" : "false";
		else
			return super.getPropertyValue(propertyId);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (VIDEO_PROP.equals(propertyId)) {
			List<Resource> oldValue = videos;
			videos = (List<Resource>) value;
			firePropertyChange(VIDEO_PROP, oldValue, videos);
		} else if (REPEAT_PROP.equals(propertyId)) {
			String oldValue = repeat ? "true" : "false";
			repeat = "true".equals(value);
			firePropertyChange(REPEAT_PROP, oldValue, repeat ? "true" : "false");
		} else
			super.setPropertyValue(propertyId, value);
	}

	/** 
	 * Add a video to this video component.
	 * @param v a non-null video instance
	 * @return true, iff the video was added, false otherwise
	 */
	public boolean addVideo(Resource v) {
		if (v != null && videos.add(v)) {
			firePropertyChange(VIDEO_ADDED_PROP, null, v);
			return true;
		}
		return false;
	}

	/** Return a List of videos in this component.  The returned List should not be modified. */
	public List<Resource> getVideos() {
		return videos;
	}

	/**
	 * Remove a video from this video component.
	 * @param v a non-null video instance;
	 * @return true, iff the video was removed, false otherwise
	 */
	public boolean removeVideo(Resource v) {
		if (v != null && videos.remove(v)) {
			firePropertyChange(VIDEO_REMOVED_PROP, null, v);
			return true;
		}
		return false;
	}
	
	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}
	
	public ModelElement deepCopy() {
		VideoComponent retVal = new VideoComponent();
		deepCopy(retVal);
		retVal.repeat = this.repeat;
		for (Resource r : videos) {
			retVal.videos.add(Resource.deepCopy(r));
		}
		return retVal;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "Video";
	}

}
