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
import org.w3c.dom.NodeList;

import com.kesdip.designer.properties.FileChooserPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class FlashWeatherComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/cloud_sun.png");
	
	private static final long serialVersionUID = 1L;

	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	public static final String SOURCE_PROP = "Weather.SourceProp";
	/** Property ID to use for the script file property value. */
	public static final String SCRIPT_FILE_PROP = "Weather.ScriptFileProp";

	/* STATE */
	private String source;
	private String scriptFile;
	
	public FlashWeatherComponent() {
		source = "";
		scriptFile = "";
	}

	protected Element serialize(Document doc, boolean isPublish) {
		Element tickerElement = doc.createElement("bean");
		tickerElement.setAttribute("class", "com.kesdip.player.components.weather.FlashWeatherComponent");
		super.serialize(doc, tickerElement);
		Element sourcePropElement = DOMHelpers.addProperty(doc, tickerElement, "source");
		Resource r = new Resource(source, "");
		sourcePropElement.appendChild(r.serialize(doc, isPublish));
		Element weatherSourcePropElement = DOMHelpers.addProperty(
				doc, tickerElement, "weatherDataSource");
		Element weatherSourceElement = doc.createElement("bean");
		
		weatherSourceElement.setAttribute(
				"class", "com.kesdip.player.components.weather.ScriptedPushingDataSource");
		Element scriptFilePropElement = DOMHelpers.addProperty(doc, weatherSourceElement, "scriptFile");
		Resource resource = new Resource(scriptFile, "");
		Element resourceElement = resource.serialize(doc, isPublish);
		scriptFilePropElement.appendChild(resourceElement);
		
		weatherSourcePropElement.appendChild(weatherSourceElement);
		
		return tickerElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		super.deserialize(doc, componentNode);
		Node sourcePropNode = DOMHelpers.getPropertyNode(componentNode, "source");
		NodeList children = sourcePropNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("bean")) {
				Resource r = new Resource("", "");
				r.deserialize(doc, child);
				setPropertyValue(SOURCE_PROP, r.getResource());
			}
		}
		Node tickerSourcePropNode = DOMHelpers.getPropertyNode(componentNode, "weatherDataSource");
		children = tickerSourcePropNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("bean")) {
				String className = child.getAttributes().
						getNamedItem("class").getNodeValue();
				if ("com.kesdip.player.components.weather.ScriptedPushingDataSource".equals(className)) {
					Node scriptPropNode = DOMHelpers.getPropertyNode(child, "scriptFile");
					children = scriptPropNode.getChildNodes();
					for (int j = 0 ; j < children.getLength() ; j++) {
						child = children.item(j);
						if (child.getNodeType() == Node.ELEMENT_NODE && 
								child.getNodeName().equals("bean")) {
							Resource r = new Resource("","");
							r.deserialize(doc, child);
							setPropertyValue(SCRIPT_FILE_PROP, r.getResource());
						}
					}
				} else {
					throw new RuntimeException("Unexpected weather source class: " + className);
				}
				break;
			}
		}
	}
	
	public void save(IMemento memento) {
		super.save(memento);
		memento.putString(TAG_SOURCE, source);
		memento.putString(TAG_WEATHER_SCRIPT, scriptFile);
	}
	
	public void load(IMemento memento) {
		super.load(memento);
		source = memento.getString(TAG_SOURCE);
		scriptFile = memento.getString(TAG_WEATHER_SCRIPT);
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof FlashWeatherComponent);
		assert(source.equals(((FlashWeatherComponent) other).source));
		assert(scriptFile.equals(((FlashWeatherComponent) other).scriptFile));
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] {
				new FileChooserPropertyDescriptor(SOURCE_PROP, "Source"),
				new FileChooserPropertyDescriptor(SCRIPT_FILE_PROP, "Script File")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
			((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
				public String isValid(Object value) {
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
		else if (SCRIPT_FILE_PROP.equals(propertyId))
			return scriptFile;
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (SOURCE_PROP.equals(propertyId)) {
			String oldValue = source;
			source = (String) value;
			firePropertyChange(SOURCE_PROP, oldValue, source);
		} else if (SCRIPT_FILE_PROP.equals(propertyId)) {
			String oldValue = scriptFile;
			scriptFile = (String) value;
			firePropertyChange(SCRIPT_FILE_PROP, oldValue, scriptFile);
		} else
			super.setPropertyValue(propertyId, value);
	}
	
	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}
	
	public ModelElement deepCopy() {
		FlashWeatherComponent retVal = new FlashWeatherComponent();
		retVal.deepCopy(this);
		retVal.source = this.source;
		retVal.scriptFile = this.scriptFile;
		return retVal;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "FlashWeather(" + source + "," + scriptFile + ")";
	}

}
