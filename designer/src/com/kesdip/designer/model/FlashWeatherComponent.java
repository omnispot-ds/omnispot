package com.kesdip.designer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kesdip.designer.utils.DOMHelpers;

public class FlashWeatherComponent extends ComponentModelElement {
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
	/** Property ID to use for the waether source type property value. */
	public static final String TYPE_PROP = "Weather.WeatherSourceTypeProp";
	public static final String URL_XML_WEATHER_TYPE = "URL XML Weather Source Type";
	public static final String RSS_WEATHER_TYPE = "RSS Weather Source Type";
	/** Property ID to use for the xml property value. */
	public static final String RSS_PROP = "Weather.WeatherRSSProp";
	/** Property ID to use for the url property value. */
	public static final String URL_PROP = "Weather.WeatherURLProp";

	/* STATE */
	private String type;
	private String url;
	private String rss;
	
	public FlashWeatherComponent() {
		type = URL_XML_WEATHER_TYPE;
		url = "";
		rss = "";
	}

	protected Element serialize(Document doc) {
		Element tickerElement = doc.createElement("bean");
		tickerElement.setAttribute("class", "com.kesdip.player.components.weather.FlashWeatherComponent");
		super.serialize(doc, tickerElement);
		Element weatherSourcePropElement = DOMHelpers.addProperty(
				doc, tickerElement, "tickerSource");
		Element weatherSourceElement = doc.createElement("bean");
		if (type.equals(URL_XML_WEATHER_TYPE)) {
			weatherSourceElement.setAttribute(
					"class", "com.kesdip.player.components.weather.URLXmlSource");
			DOMHelpers.addProperty(doc, weatherSourceElement, "url", url);
		} else { /* assuming type is RSS_WEATHER_TYPE */
			weatherSourceElement.setAttribute(
					"class", "com.kesdip.player.components.weather.RssWeatherSource");
			DOMHelpers.addProperty(doc, weatherSourceElement, "rssUrl", rss);
		}
		weatherSourcePropElement.appendChild(weatherSourceElement);
		return tickerElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		super.deserialize(doc, componentNode);
		Node tickerSourcePropNode = DOMHelpers.getPropertyNode(componentNode, "tickerSource");
		NodeList children = tickerSourcePropNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("bean")) {
				String className = child.getAttributes().
						getNamedItem("class").getNodeValue();
				if ("com.kesdip.player.components.weather.URLXmlSource".equals(className)) {
					setPropertyValue(TYPE_PROP, getWeatherSourceType(URL_XML_WEATHER_TYPE));
					setPropertyValue(URL_PROP, DOMHelpers.getSimpleProperty(child, "url"));
				} else if ("com.kesdip.player.components.weather.RssWeatherSource".equals(className)) {
					setPropertyValue(TYPE_PROP, getWeatherSourceType(RSS_WEATHER_TYPE));
					setPropertyValue(RSS_PROP, DOMHelpers.getSimpleProperty(child, "rssUrl"));
				} else {
					throw new RuntimeException("Unexpected weather source class: " + className);
				}
				break;
			}
		}
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof FlashWeatherComponent);
		assert(type.equals(((FlashWeatherComponent) other).type));
		assert(url.equals(((FlashWeatherComponent) other).url));
		assert(rss.equals(((FlashWeatherComponent) other).rss));
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] { 
				new ComboBoxPropertyDescriptor(TYPE_PROP, "Weather Source Type",
						new String[] { URL_XML_WEATHER_TYPE, RSS_WEATHER_TYPE }),
				new TextPropertyDescriptor(URL_PROP, "XML URL"),
				new TextPropertyDescriptor(RSS_PROP, "RSS URL")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
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

	private int getWeatherSourceType(String t) {
		if (t.equals(URL_XML_WEATHER_TYPE))
			return 0;
		else if (t.equals(RSS_WEATHER_TYPE))
			return 1;
		else
			throw new RuntimeException("Unknown weather source type.");		
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		if (URL_PROP.equals(propertyId))
			return url;
		else if (RSS_PROP.equals(propertyId))
			return rss;
		else if (TYPE_PROP.equals(propertyId))
			return getWeatherSourceType(type);
		else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (URL_PROP.equals(propertyId)) {
			String oldValue = url;
			url = (String) value;
			firePropertyChange(URL_PROP, oldValue, url);
		} else if (RSS_PROP.equals(propertyId)) {
			String oldValue = rss;
			rss = (String) value;
			firePropertyChange(RSS_PROP, oldValue, rss);
		} else if (TYPE_PROP.equals(propertyId)) {
			int oldValue = getWeatherSourceType(type);
			int v = ((Integer) value).intValue();
			if (v == 0)
				type = URL_XML_WEATHER_TYPE;
			else if (v == 1)
				type = RSS_WEATHER_TYPE;
			else
				throw new RuntimeException("Unexpected weather source type.");
			firePropertyChange(TYPE_PROP, oldValue, value);
		} else
			super.setPropertyValue(propertyId, value);
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "FlashWeather(" + type + "," + url + "," + rss + ")";
	}

}
