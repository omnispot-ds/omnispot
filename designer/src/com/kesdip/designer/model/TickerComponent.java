package com.kesdip.designer.model;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kesdip.designer.properties.CheckboxPropertyDescriptor;
import com.kesdip.designer.properties.FontPropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;

public class TickerComponent extends ComponentModelElement {
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
	/** Property ID to use for the ticker source type property value. */
	public static final String TYPE_PROP = "Ticker.TickerTypeProp";
	public static final String STRING_TICKER_TYPE = "String Ticker Type";
	public static final String RSS_TICKER_TYPE = "RSS Ticker Type";
	/** Property ID to use for the string property value. */
	public static final String STRING_PROP = "Ticker.TickerStringProp";
	/** Property ID to use for the url property value. */
	public static final String URL_PROP = "Ticker.TickerURLProp";
	/** Property ID to use for the font property value. */
	public static final String FONT_PROP = "Ticker.FontProp";
	/** Property ID to use for the foreground color property value. */
	public static final String FOREGROUND_COLOR_PROP = "Ticker.ForegroundColorProp";
	/** Property ID to use for the speed property value. */
	public static final String SPEED_PROP = "Ticker.SpeedProp";
	/** Property ID to use for the transparent property value. */
	public static final String TRANSPARENT_PROP = "Ticker.TransparentProp";

	/* STATE */
	private String type;
	private String url;
	private String string;
	private double speed;
	private Font font;
	private Color foregroundColor;
	private boolean isTransparent;
	
	public TickerComponent() {
		type = STRING_TICKER_TYPE;
		url = "";
		string = "";
		speed = 2.0;
		font = new Font("Arial", Font.PLAIN, 24);
		foregroundColor = Color.BLACK;
	}

	protected Element serialize(Document doc) {
		Element tickerElement = doc.createElement("bean");
		tickerElement.setAttribute("class", "com.kesdip.player.components.Ticker");
		super.serialize(doc, tickerElement, !isTransparent);
		DOMHelpers.addProperty(doc, tickerElement, "speed", String.valueOf(speed));
		Element foreColorPropelement = DOMHelpers.addProperty(
				doc, tickerElement, "foregroundColor");
		Element foreColorElement = doc.createElement("bean");
		foreColorElement.setAttribute("class", "java.awt.Color");
		foreColorPropelement.appendChild(foreColorElement);
		Element constructorArg = doc.createElement("constructor-arg");
		constructorArg.setAttribute("type", "int");
		constructorArg.setAttribute("value", String.valueOf(foregroundColor.getRed()));
		foreColorElement.appendChild(constructorArg);
		constructorArg = doc.createElement("constructor-arg");
		constructorArg.setAttribute("type", "int");
		constructorArg.setAttribute("value", String.valueOf(foregroundColor.getGreen()));
		foreColorElement.appendChild(constructorArg);
		constructorArg = doc.createElement("constructor-arg");
		constructorArg.setAttribute("type", "int");
		constructorArg.setAttribute("value", String.valueOf(foregroundColor.getBlue()));
		foreColorElement.appendChild(constructorArg);
		Element fontPropElement = DOMHelpers.addProperty(doc, tickerElement, "font");
		Element fontElement = doc.createElement("bean");
		fontElement.setAttribute("class", "java.awt.Font");
		fontPropElement.appendChild(fontElement);
		constructorArg = doc.createElement("constructor-arg");
		constructorArg.setAttribute("type", "java.lang.String");
		constructorArg.setAttribute("value", font.getFamily());
		fontElement.appendChild(constructorArg);
		constructorArg = doc.createElement("constructor-arg");
		constructorArg.setAttribute("type", "int");
		constructorArg.setAttribute("value", String.valueOf(font.getStyle()));
		fontElement.appendChild(constructorArg);
		constructorArg = doc.createElement("constructor-arg");
		constructorArg.setAttribute("type", "int");
		constructorArg.setAttribute("value", String.valueOf(font.getSize()));
		fontElement.appendChild(constructorArg);
		Element tickerSourcePropElement = DOMHelpers.addProperty(
				doc, tickerElement, "tickerSource");
		Element tickerSourceElement = doc.createElement("bean");
		if (type.equals(STRING_TICKER_TYPE)) {
			tickerSourceElement.setAttribute(
					"class", "com.kesdip.player.components.ticker.StringTickerSource");
			DOMHelpers.addProperty(doc, tickerSourceElement, "src", string);
		} else { /* assuming type is RSS_TICKER_TYPE */
			tickerSourceElement.setAttribute(
					"class", "com.kesdip.player.components.ticker.RssTickerSource");
			DOMHelpers.addProperty(doc, tickerSourceElement, "rssUrl", url);
		}
		tickerSourcePropElement.appendChild(tickerSourceElement);
		return tickerElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		super.deserialize(doc, componentNode);
		isTransparent = backgroundColor == null; // This is not stored in the XML
		setPropertyValue(SPEED_PROP, DOMHelpers.getSimpleProperty(componentNode, "speed"));
		Color bc = DOMHelpers.getColorProperty(componentNode, "foregroundColor");
		setPropertyValue(FOREGROUND_COLOR_PROP,
				new RGB(bc.getRed(), bc.getGreen(), bc.getBlue()));
		setPropertyValue(FONT_PROP, DOMHelpers.getFontProperty(componentNode, "font"));
		Node tickerSourcePropNode = DOMHelpers.getPropertyNode(componentNode, "tickerSource");
		NodeList children = tickerSourcePropNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("bean")) {
				String className = child.getAttributes().
						getNamedItem("class").getNodeValue();
				if ("com.kesdip.player.components.ticker.StringTickerSource".equals(className)) {
					setPropertyValue(TYPE_PROP, getTickerType(STRING_TICKER_TYPE));
					setPropertyValue(STRING_PROP, DOMHelpers.getSimpleProperty(child, "src"));
				} else if ("com.kesdip.player.components.ticker.RssTickerSource".equals(className)) {
					setPropertyValue(TYPE_PROP, getTickerType(RSS_TICKER_TYPE));
					setPropertyValue(URL_PROP, DOMHelpers.getSimpleProperty(child, "rssUrl"));
				} else {
					throw new RuntimeException("Unexpected ticker source class: " + className);
				}
				break;
			}
		}
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof TickerComponent);
		assert(type.equals(((TickerComponent) other).type));
		assert(url.equals(((TickerComponent) other).url));
		assert(string.equals(((TickerComponent) other).string));
		assert(speed == ((TickerComponent) other).speed);
		assert(font.equals(((TickerComponent) other).font));
		assert(foregroundColor.equals(((TickerComponent) other).foregroundColor));
	}
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] {
				new ComboBoxPropertyDescriptor(TYPE_PROP, "Ticker Type",
						new String[] { STRING_TICKER_TYPE, RSS_TICKER_TYPE }),
				new TextPropertyDescriptor(URL_PROP, "RSS URL"),
				new TextPropertyDescriptor(STRING_PROP, "String Source"),
				new TextPropertyDescriptor(SPEED_PROP, "Speed"),
				new ColorPropertyDescriptor(FOREGROUND_COLOR_PROP, "Foreground Color"),
				new FontPropertyDescriptor(FONT_PROP, "Font"),
				new CheckboxPropertyDescriptor(TRANSPARENT_PROP, "Transparent")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getId().equals(SPEED_PROP)) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						try {
							Double.parseDouble((String) value);
						} catch (Exception e) {
							return "Invalid double value: " + value;
						}
						return null;
					}
				});
			} else {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
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
	
	private int getTickerType(String t) {
		if (t.equals(STRING_TICKER_TYPE))
			return 0;
		else if (t.equals(RSS_TICKER_TYPE))
			return 1;
		else
			throw new RuntimeException("Unknown ticker type.");		
	}

	@Override
	public Object getPropertyValue(Object propertyId) {
		if (URL_PROP.equals(propertyId)) {
			return url;
		} else if (STRING_PROP.equals(propertyId)) {
			return string;
		} else if (TYPE_PROP.equals(propertyId)) {
			return getTickerType(type);
		} else if (SPEED_PROP.equals(propertyId)) {
			return String.valueOf(speed);
		} else if (FONT_PROP.equals(propertyId)) {
			return font;
		} else if (FOREGROUND_COLOR_PROP.equals(propertyId)) {
			RGB v = new RGB(
					foregroundColor.getRed(),
					foregroundColor.getGreen(),
					foregroundColor.getBlue());
			return v;
		} else if (TRANSPARENT_PROP.equals(propertyId)){
			return isTransparent;
		} else
			return super.getPropertyValue(propertyId);
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (URL_PROP.equals(propertyId)) {
			String oldValue = url;
			url = (String) value;
			firePropertyChange(URL_PROP, oldValue, url);
		} else if (STRING_PROP.equals(propertyId)) {
			String oldValue = string;
			string = (String) value;
			firePropertyChange(STRING_PROP, oldValue, string);
		} else if (TYPE_PROP.equals(propertyId)) {
			int oldValue = getTickerType(type);
			int v = ((Integer) value).intValue();
			if (v == 0)
				type = STRING_TICKER_TYPE;
			else if (v == 1)
				type = RSS_TICKER_TYPE;
			else
				throw new RuntimeException("Unexpected ticker type.");
			firePropertyChange(TYPE_PROP, oldValue, value);
		} else if (SPEED_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(speed);
			speed = Double.parseDouble((String) value);
			firePropertyChange(SPEED_PROP, oldValue, value);
		} else if (FONT_PROP.equals(propertyId)) {
			Font oldValue = font;
			font = (Font) value;
			firePropertyChange(FONT_PROP, oldValue, font);
		} else if (FOREGROUND_COLOR_PROP.equals(propertyId)) {
			RGB oldValue = new RGB(
					foregroundColor.getRed(),
					foregroundColor.getGreen(),
					foregroundColor.getBlue());
			RGB rgbValue = (RGB) value;
			foregroundColor = new Color(rgbValue.red, rgbValue.green, rgbValue.blue);
			firePropertyChange(BACK_COLOR_PROP, oldValue, value);
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

	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}
	
	public ModelElement deepCopy() {
		TickerComponent retVal = new TickerComponent();
		deepCopy(retVal);
		retVal.foregroundColor = new Color(this.foregroundColor.getRGB());
		retVal.font = new Font(this.font.getFamily(), this.font.getStyle(), this.font.getSize());
		retVal.speed = this.speed;
		retVal.type = this.type;
		retVal.string = this.string;
		retVal.url = this.url;
		retVal.isTransparent = this.isTransparent;
		return retVal;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "Ticker(" + type + "," + url + "," + string + ")";
	}

}
