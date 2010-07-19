package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kesdip.designer.constenum.ResourceListCellEditorTypes;
import com.kesdip.designer.properties.CheckboxPropertyDescriptor;
import com.kesdip.designer.properties.ResourceListPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class VideoComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/clapperboard.png");

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
	/** Property ID to use for the name property value. */
	public static final String VIDEO_PROP = "Video.VideosProp";
	/** Property ID to use for the repeat property value. */
	public static final String REPEAT_PROP = "Video.RepeatProp";
	/** Property ID to use when a video is added to this video component. */
	public static final String VIDEO_ADDED_PROP = "Video.VideoAdded";
	/** Property ID to use when a video is removed from this video component. */
	public static final String VIDEO_REMOVED_PROP = "Video.VideoRemoved";
	/** Player Implementation (vlc,mplayer) */
	public static final String VIDEO_PROVIDER_PROP = "Video.VideoProvider";
	/** Playback quality (MPlayer only) */
	public static final String VIDEO_QUALITY_PROP = "Video.QualityProp";
	/** Extra arguments */
	public static final String EXTRA_ARGS_PROP = "Video.ExtraArgsProp";

	private static final String STRING_VLC = "VLC";

	private static final String STRING_MPLAYER = "MPlayer";

	private static final String STRING_HIGH_QUALITY = "High";

	private static final String STRING_NORMAL_QUALITY = "Normal";

	/* STATE */
	private List<Resource> videos;
	private boolean repeat;
	private String provider;
	private String quality;
	private String extraArgs;

	public VideoComponent() {
		videos = new ArrayList<Resource>();
		repeat = false;
		provider = STRING_MPLAYER;
		quality = STRING_NORMAL_QUALITY;
		extraArgs = "";
	}

	protected Element serialize(Document doc, boolean isPublish) {
		Element videoElement = doc.createElement("bean");

		if (provider.equals(STRING_VLC)) {
			videoElement.setAttribute("class",
					"com.kesdip.player.components.Video");
		} else if (provider.equals(STRING_MPLAYER)) {
			videoElement.setAttribute("class",
					"com.kesdip.player.components.media.FileVideo");
		}

		super.serialize(doc, videoElement);
		DOMHelpers.addProperty(doc, videoElement, "repeat", repeat ? "true"
				: "false");
		// do not serialize quality if not for MPlayer
		if (provider.equals(STRING_MPLAYER)) {
			DOMHelpers.addProperty(doc, videoElement, "quality", quality
					.equals(STRING_HIGH_QUALITY) ? "high" : "normal");
		}
		DOMHelpers.addProperty(doc, videoElement, "extraArgs", extraArgs);
		Element contentPropElement = DOMHelpers.addProperty(doc, videoElement,
				"contents");
		Element listElement = doc.createElement("list");
		contentPropElement.appendChild(listElement);
		for (Resource r : videos) {
			Element resourceElement = r.serialize(doc, isPublish);
			listElement.appendChild(resourceElement);
		}
		return videoElement;
	}

	protected void deserialize(Document doc, Node componentNode) {
		setPropertyValue(REPEAT_PROP, DOMHelpers.getSimpleProperty(
				componentNode, "repeat"));
		String qualityValue = DOMHelpers.getSimpleProperty(componentNode,
				"quality");
		if (qualityValue == null) {
			qualityValue = STRING_NORMAL_QUALITY;
		}
		setPropertyValue(
				VIDEO_QUALITY_PROP,
				"high".equalsIgnoreCase(qualityValue) ? getQualityType(STRING_HIGH_QUALITY)
						: getQualityType(STRING_NORMAL_QUALITY));
		setPropertyValue(EXTRA_ARGS_PROP, DOMHelpers.getSimpleProperty(
				componentNode, "extraArgs"));
		String className = componentNode.getAttributes().getNamedItem("class")
				.getNodeValue();
		if (className.equals("com.kesdip.player.components.media.FileVideo")) {
			setPropertyValue(VIDEO_PROVIDER_PROP,
					getProviderType(STRING_MPLAYER));
		} else if (className.equals("com.kesdip.player.components.Video")) {
			setPropertyValue(VIDEO_PROVIDER_PROP, getProviderType(STRING_VLC));
		}
		super.deserialize(doc, componentNode);
		final List<Resource> newVideos = new ArrayList<Resource>();
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
						newVideos.add(r);
					}
				});
		videos = newVideos;
	}

	public void save(IMemento memento) {
		super.save(memento);
		memento.putBoolean(TAG_REPEAT, repeat);
		memento.putString(TAG_VIDEO_QUALITY, quality);
		memento.putString(TAG_VIDEO_PROVIDER, provider);
		memento.putString(TAG_EXTRA_ARGS, extraArgs);
		/*
		 * Do not save resources. for (Resource r : videos) { IMemento child =
		 * memento.createChild(TAG_RESOURCE); r.save(child); }
		 */
	}

	public void load(IMemento memento) {
		super.load(memento);
		repeat = memento.getBoolean(TAG_REPEAT);
		quality = memento.getString(TAG_VIDEO_QUALITY);
		provider = memento.getString(TAG_VIDEO_PROVIDER);
		extraArgs = memento.getString(TAG_EXTRA_ARGS);
		IMemento[] children = memento.getChildren(TAG_RESOURCE);
		for (IMemento child : children) {
			Resource r = new Resource("", "");
			r.load(child);
			videos.add(r);
		}
	}

	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert (other instanceof VideoComponent);
		assert (repeat == ((VideoComponent) other).repeat);
		assert (quality.equals(((VideoComponent) other).quality));
		assert (provider.equals(((VideoComponent) other).provider));
		assert (extraArgs.equals(((VideoComponent) other).extraArgs));
		for (int i = 0; i < videos.size(); i++) {
			Resource resource = videos.get(i);
			Resource otherResource = ((VideoComponent) other).videos.get(i);
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
				new ComboBoxPropertyDescriptor(VIDEO_PROVIDER_PROP, "Provider",
						new String[] { STRING_MPLAYER, STRING_VLC }),
				new ComboBoxPropertyDescriptor(VIDEO_QUALITY_PROP,
						"Quality (MPlayer only)", new String[] {
								STRING_NORMAL_QUALITY, STRING_HIGH_QUALITY }),
				new TextPropertyDescriptor(EXTRA_ARGS_PROP, "Extra Arguments"),				
				new ResourceListPropertyDescriptor(VIDEO_PROP, "Videos",
						ResourceListCellEditorTypes.VIDEO_RESOURCE_LIST_EDITOR),
				new CheckboxPropertyDescriptor(REPEAT_PROP, "Repeat") };
		// use a custom cell editor validator for the array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
			((PropertyDescriptor) descriptors[i])
					.setValidator(new ICellEditorValidator() {
						public String isValid(Object value) {
							// No validation for the videos.
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
		if (VIDEO_PROP.equals(propertyId)) {
			return videos;
		} else if (REPEAT_PROP.equals(propertyId)) {
			return repeat;
		} else if (VIDEO_QUALITY_PROP.equals(propertyId)) {
			return getQualityType(quality);
		} else if (VIDEO_PROVIDER_PROP.equals(propertyId)) {
			return getProviderType(provider);
		} else if (EXTRA_ARGS_PROP.equals(propertyId)) {
			return extraArgs;
		} else {
			return super.getPropertyValue(propertyId);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (VIDEO_PROP.equals(propertyId)) {
			List<Resource> oldValue = videos;
			videos = (List<Resource>) value;
			firePropertyChange(VIDEO_PROP, oldValue, videos);
		} else if (REPEAT_PROP.equals(propertyId)) {
			if (value instanceof String) {
				// We are being deserialized
				String oldValue = repeat ? "true" : "false";
				repeat = value.equals("true");
				firePropertyChange(REPEAT_PROP, oldValue, value);
				return;
			}
			Boolean oldValue = repeat;
			repeat = ((Boolean) value).booleanValue();
			firePropertyChange(REPEAT_PROP, oldValue, repeat);
		} else if (VIDEO_QUALITY_PROP.equals(propertyId)) {
			if (value == null) {
				value = 0;
			}
			int oldValue = getQualityType(quality);
			int v = ((Integer) value).intValue();
			if (v == 0) {
				quality = STRING_NORMAL_QUALITY;
			} else if (v == 1) {
				quality = STRING_HIGH_QUALITY;
			} else {
				throw new RuntimeException("Unexpected provider type.");
			}
			firePropertyChange(VIDEO_QUALITY_PROP, oldValue, quality);
		} else if (VIDEO_PROVIDER_PROP.equals(propertyId)) {
			if (value == null) {
				value = 0;
			}
			int oldValue = getProviderType(provider);
			int v = ((Integer) value).intValue();
			if (v == 0) {
				provider = STRING_MPLAYER;
			} else if (v == 1) {
				provider = STRING_VLC;
			} else {
				throw new RuntimeException("Unexpected provider type.");
			}
			firePropertyChange(VIDEO_PROVIDER_PROP, oldValue, provider);
		} else if (EXTRA_ARGS_PROP.equals(propertyId)) {
			String oldValue = extraArgs;
			extraArgs = value.toString();
			firePropertyChange(EXTRA_ARGS_PROP, oldValue, extraArgs);
		} else
			super.setPropertyValue(propertyId, value);
	}

	private int getProviderType(String t) {
		if (t.equals(STRING_MPLAYER)) {
			return 0;
		} else if (t.equals(STRING_VLC)) {
			return 1;
		} else
			throw new RuntimeException("Unknown provider type.");
	}

	private int getQualityType(String t) {
		if (t.equals(STRING_HIGH_QUALITY)) {
			return 1;
		} else if (t.equals(STRING_NORMAL_QUALITY)) {
			return 0;
		} else
			throw new RuntimeException("Unknown quality type.");
	}

	/**
	 * Add a video to this video component.
	 * 
	 * @param v
	 *            a non-null video instance
	 * @return true, iff the video was added, false otherwise
	 */
	public boolean addVideo(Resource v) {
		if (v != null && videos.add(v)) {
			firePropertyChange(VIDEO_ADDED_PROP, null, v);
			return true;
		}
		return false;
	}

	/**
	 * Return a List of videos in this component. The returned List should not
	 * be modified.
	 */
	public List<Resource> getVideos() {
		return videos;
	}

	/**
	 * Remove a video from this video component.
	 * 
	 * @param v
	 *            a non-null video instance;
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
		retVal.deepCopy(this);
		retVal.repeat = this.repeat;
		retVal.quality = this.quality;
		retVal.provider = this.provider;
		retVal.extraArgs = this.extraArgs;
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
		boolean first = true;
		StringBuilder sb = new StringBuilder("[");
		for (Resource r : videos) {
			if (first) {
				first = false;
			} else {
				sb.append(",");
			}
			sb.append(r.toString());
		}
		sb.append("]");
		return "Video: " + sb.toString();
	}

}
