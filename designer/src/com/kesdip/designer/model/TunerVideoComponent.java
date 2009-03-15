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

import com.kesdip.designer.utils.DOMHelpers;

public class TunerVideoComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/wireless.png");

	private static final long serialVersionUID = 1L;

	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the device property value. */
	public static final String DEVICE_PROP = "Tuner.DeviceProp";
	/** Property ID to use for the channel property value. */
	public static final String CHANNEL_PROP = "Tuner.ChannelProp";
	/** Property ID to use for the input property value. */
	public static final String INPUT_PROP = "Tuner.InputProp";

	/* STATE */
	private String device;
	private int channel;
	private int input;
	
	public TunerVideoComponent() {
		device = "";
		channel = 0;
		input = 0;
	}
	
	protected Element serialize(Document doc, boolean isPublish) {
		Element videoElement = doc.createElement("bean");
		videoElement.setAttribute("class", "com.kesdip.player.components.TunerVideo");
		super.serialize(doc, videoElement);
		DOMHelpers.addProperty(doc, videoElement, "device", device);
		DOMHelpers.addProperty(doc, videoElement, "channel", String.valueOf(channel));
		DOMHelpers.addProperty(doc, videoElement, "input", String.valueOf(input));
		return videoElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		setPropertyValue(DEVICE_PROP, DOMHelpers.getSimpleProperty(componentNode, "device"));
		setPropertyValue(CHANNEL_PROP, DOMHelpers.getSimpleProperty(componentNode, "channel"));
		setPropertyValue(INPUT_PROP, DOMHelpers.getSimpleProperty(componentNode, "input"));
		super.deserialize(doc, componentNode);
	}
	
	public void save(IMemento memento) {
		super.save(memento);
		memento.putString(TAG_DEVICE, device);
		memento.putInteger(TAG_CHANNEL, channel);
		memento.putInteger(TAG_INPUT, input);
	}
	
	public void load(IMemento memento) {
		super.load(memento);
		device = memento.getString(TAG_DEVICE);
		channel = memento.getInteger(TAG_CHANNEL);
		input = memento.getInteger(TAG_INPUT);
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof TunerVideoComponent);
		assert(device.equals(((TunerVideoComponent) other).device));
		assert(channel == ((TunerVideoComponent) other).channel);
		assert(input == ((TunerVideoComponent) other).input);
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new TextPropertyDescriptor(DEVICE_PROP, "Device"),
				new TextPropertyDescriptor(CHANNEL_PROP, "Channel"),
				new TextPropertyDescriptor(INPUT_PROP, "Input Pin")
		};
		// use a custom cell editor validator for the array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
			if (CHANNEL_PROP.equals(descriptors[i].getId()) ||
					INPUT_PROP.equals(descriptors[i].getId())) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						try {
							Integer.parseInt((String) value);
						} catch (NumberFormatException e) {
							return "'" + value + "' is not a valid integer.";
						}
						return null;
					}
				});
			} else {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						// No validation for the device
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
		if (CHANNEL_PROP.equals(propertyId))
			return String.valueOf(channel);
		else if (INPUT_PROP.equals(propertyId))
			return String.valueOf(input);
		else if (DEVICE_PROP.equals(propertyId))
			return device;
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (CHANNEL_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(channel);
			channel = Integer.parseInt((String) value);
			firePropertyChange(CHANNEL_PROP, oldValue, value);
		} else if (INPUT_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(input);
			input = Integer.parseInt((String) value);
			firePropertyChange(INPUT_PROP, oldValue, value);
		} else if (DEVICE_PROP.equals(propertyId)) {
			String oldValue = device;
			device = (String) value;
			firePropertyChange(DEVICE_PROP, oldValue, device);
		} else
			super.setPropertyValue(propertyId, value);
	}

	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}
	
	@Override
	public ModelElement deepCopy() {
		TunerVideoComponent retVal = new TunerVideoComponent();
		retVal.deepCopy(this);
		retVal.device = this.device;
		retVal.channel = this.channel;
		retVal.input = this.input;
		return retVal;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "TunerVideo: (" + device + "," + channel + "," + input + ")";
	}


}
