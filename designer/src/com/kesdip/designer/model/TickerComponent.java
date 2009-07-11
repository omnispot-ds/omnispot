package com.kesdip.designer.model;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IMemento;
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
import com.kesdip.designer.utils.FontKludge;

public class TickerComponent extends ComponentModelElement {
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/ticker.png");
	
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
	/** Property ID to use for the show only titles property value. */
	public static final String SHOW_ONLY_TITLES_PROP = "Ticker.ShowOnlyTitlesProp";
	/** Property ID to use for the item separator property value. */
	public static final String ITEM_SEPARATOR_PROP = "Ticker.ItemSeparatorProp";
	/** Property ID to use for the after title property value. */
	public static final String AFTER_TITLE_PROP = "Ticker.AfterTitleProp";
	/** Property ID to use for refresh iterval property value. */
	public static final String REFRESH_INTERVAL_PROP = "Ticker.RefreshIntervalProp";

	/* STATE */
	private String type;
	private String url;
	private String string;
	private double speed;
	private Font font;
	private Color foregroundColor;
	private boolean isTransparent;
	private boolean showOnlyTitles;
	private String itemSeparator;
	private String afterTitle;
	private int refreshInterval;
	
	/* Transient state */
	private FontData fontData;
	
	public TickerComponent() {
		type = STRING_TICKER_TYPE;
		url = "";
		string = "Repeat this text forever!";
		speed = 160.0;
		font = new Font("Arial", Font.PLAIN, 24);
		foregroundColor = Color.WHITE;
		isTransparent = false;
		showOnlyTitles = true;
		itemSeparator = " - ";
		afterTitle = ": ";
		refreshInterval = 10;
		
		fontData = null;
	}
	
	public FontData getFontData() {
		return fontData;
	}

	protected Element serialize(Document doc, boolean isPublish) {
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
			DOMHelpers.addProperty(doc, tickerSourceElement, "showOnlyTitles", showOnlyTitles ? "true" : "false");
			DOMHelpers.addProperty(doc, tickerSourceElement, "itemSeparator", itemSeparator);
			DOMHelpers.addProperty(doc, tickerSourceElement, "afterTitle", afterTitle);
			DOMHelpers.addProperty(doc, tickerSourceElement, "refreshInterval", String.valueOf(refreshInterval));
		}
		tickerSourcePropElement.appendChild(tickerSourceElement);
		return tickerElement;
	}
	
	protected void deserialize(Document doc, Node componentNode) {
		Color oldBackgroundColor = backgroundColor;
		backgroundColor = null;
		super.deserialize(doc, componentNode);
		isTransparent = backgroundColor == null; // This is not stored in the XML
		backgroundColor = oldBackgroundColor;
		setPropertyValue(SPEED_PROP, DOMHelpers.getSimpleProperty(componentNode, "speed"));
		Color bc = DOMHelpers.getColorProperty(componentNode, "foregroundColor");
		setPropertyValue(FOREGROUND_COLOR_PROP,
				new RGB(bc.getRed(), bc.getGreen(), bc.getBlue()));
		setPropertyValue(FONT_PROP, assureFontExists(
				DOMHelpers.getFontProperty(componentNode, "font")));
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
					setPropertyValue(SHOW_ONLY_TITLES_PROP,
							DOMHelpers.getSimpleProperty(child, "showOnlyTitles"));
					setPropertyValue(ITEM_SEPARATOR_PROP,
							DOMHelpers.getSimpleProperty(child, "itemSeparator"));
					setPropertyValue(AFTER_TITLE_PROP,
							DOMHelpers.getSimpleProperty(child, "afterTitle"));
					setPropertyValue(REFRESH_INTERVAL_PROP,
							DOMHelpers.getSimpleProperty(child, "refreshInterval"));
				} else {
					throw new RuntimeException("Unexpected ticker source class: " + className);
				}
				break;
			}
		}
	}
	
	public void save(IMemento memento) {
		super.save(memento);
		memento.putString(TAG_TICKER_TYPE, type);
		memento.putString(TAG_TICKER_URL, url);
		memento.putString(TAG_TICKER_STRING, string);
		memento.putBoolean(TAG_TICKER_SHOW_ONLY_TITLES, showOnlyTitles);
		memento.putString(TAG_TICKER_ITEM_SEPARATOR, itemSeparator);
		memento.putString(TAG_TICKER_AFTER_TITLE, afterTitle);
		memento.putFloat(TAG_TICKER_SPEED, (float) speed);
		memento.putFloat(TAG_TICKER_REFRESH_ITERVAL, (float) refreshInterval);
		memento.putString(TAG_FONT_NAME, font.getFamily());
		memento.putInteger(TAG_FONT_STYLE, font.getStyle());
		memento.putInteger(TAG_FONT_SIZE, font.getSize());
		memento.putInteger(TAG_FRONT_RED, foregroundColor.getRed());
		memento.putInteger(TAG_FRONT_GREEN, foregroundColor.getGreen());
		memento.putInteger(TAG_FRONT_BLUE, foregroundColor.getBlue());
	}
	
	public void load(IMemento memento) {
		super.load(memento);
		type = memento.getString(TAG_TICKER_TYPE);
		url = memento.getString(TAG_TICKER_URL);
		string = memento.getString(TAG_TICKER_STRING);
		showOnlyTitles = memento.getBoolean(TAG_TICKER_SHOW_ONLY_TITLES);
		itemSeparator = memento.getString(TAG_TICKER_ITEM_SEPARATOR);
		afterTitle = memento.getString(TAG_TICKER_AFTER_TITLE);
		speed = memento.getFloat(TAG_TICKER_SPEED);
		refreshInterval = memento.getInteger(TAG_TICKER_REFRESH_ITERVAL);
		font = new Font(memento.getString(TAG_FONT_NAME),
				memento.getInteger(TAG_FONT_STYLE), memento.getInteger(TAG_FONT_SIZE));
		foregroundColor = new Color(memento.getInteger(TAG_FRONT_RED),
				memento.getInteger(TAG_FRONT_GREEN),
				memento.getInteger(TAG_FRONT_BLUE));
	}
	
	@Override
	void checkEquivalence(ComponentModelElement other) {
		super.checkEquivalence(other);
		assert(other instanceof TickerComponent);
		assert(type.equals(((TickerComponent) other).type));
		assert(url.equals(((TickerComponent) other).url));
		assert(string.equals(((TickerComponent) other).string));
		assert(showOnlyTitles == ((TickerComponent) other).showOnlyTitles);
		assert(itemSeparator.equals(((TickerComponent) other).itemSeparator));
		assert(afterTitle.equals(((TickerComponent) other).afterTitle));
		assert(speed == ((TickerComponent) other).speed);
		assert(refreshInterval == ((TickerComponent) other).refreshInterval);
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
				new CheckboxPropertyDescriptor(SHOW_ONLY_TITLES_PROP, "Show Only Titles"),
				new TextPropertyDescriptor(ITEM_SEPARATOR_PROP, "Item Separator"),
				new TextPropertyDescriptor(AFTER_TITLE_PROP, "After Title"),
				new TextPropertyDescriptor(SPEED_PROP, "Speed"),
				new ColorPropertyDescriptor(FOREGROUND_COLOR_PROP, "Foreground Color"),
				new FontPropertyDescriptor(FONT_PROP, "Font"),
				new CheckboxPropertyDescriptor(TRANSPARENT_PROP, "Transparent"),
				new TextPropertyDescriptor(REFRESH_INTERVAL_PROP, "Refresh Interval (in minutes)")
		};
		// use a custom cell editor validator for all three array entries
		for (int i = 0; i < descriptors.length; i++) {
			((PropertyDescriptor) descriptors[i]).setCategory("Behaviour");
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
			} else if (descriptors[i].getId().equals(REFRESH_INTERVAL_PROP)) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						try {
							Integer.parseInt((String) value);
						} catch (Exception e) {
							return "Invalid integer value: " + value;
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
		} else if (SHOW_ONLY_TITLES_PROP.equals(propertyId)) {
			return showOnlyTitles;
		} else if (ITEM_SEPARATOR_PROP.equals(propertyId)) {
			return itemSeparator;
		} else if (AFTER_TITLE_PROP.equals(propertyId)) {
			return afterTitle;
		} else if (REFRESH_INTERVAL_PROP.equals(propertyId)) {
			return String.valueOf(refreshInterval);
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
		} else if (SHOW_ONLY_TITLES_PROP.equals(propertyId)) {
			if (value instanceof String) {
				// We are being deserialized
				String oldValue = showOnlyTitles ? "true" : "false";
				showOnlyTitles = value.equals("true");
				firePropertyChange(SHOW_ONLY_TITLES_PROP, oldValue, value);
				return;
			}
			Boolean oldValue = showOnlyTitles;
			showOnlyTitles = ((Boolean) value).booleanValue();
			firePropertyChange(SHOW_ONLY_TITLES_PROP, oldValue, showOnlyTitles);
		} else if (ITEM_SEPARATOR_PROP.equals(propertyId)) {
			String oldValue = itemSeparator;
			itemSeparator = (String) value;
			firePropertyChange(ITEM_SEPARATOR_PROP, oldValue, itemSeparator);
		} else if (AFTER_TITLE_PROP.equals(propertyId)) {
			String oldValue = afterTitle;
			afterTitle = (String) value;
			firePropertyChange(AFTER_TITLE_PROP, oldValue, afterTitle);
		} else if (REFRESH_INTERVAL_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(refreshInterval);
			refreshInterval = value != null ? Integer.parseInt((String)value) : 10;
			firePropertyChange(REFRESH_INTERVAL_PROP, oldValue, refreshInterval);
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
			Font newValue;
			if (value instanceof FontKludge) {
				newValue = (FontKludge) value;
				fontData = ((FontKludge) newValue).getFontData();
			} else if (value instanceof org.eclipse.swt.graphics.Font) {
				org.eclipse.swt.graphics.Font f = (org.eclipse.swt.graphics.Font) value;
				fontData = f.getFontData()[0];
				int style = Font.PLAIN;
				if ((fontData.getStyle() & SWT.BOLD) != 0)
					style |= Font.BOLD;
				if ((fontData.getStyle() & SWT.ITALIC) != 0)
					style |= Font.ITALIC;
				newValue = new FontKludge(fontData.getName(), style, fontData.getHeight());
				assureFontExists(newValue);
			} else {
				newValue = (Font) value;
				fontData = null;
				assureFontExists(newValue);
			}
			Font oldValue = font;
			font = newValue;
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
		retVal.deepCopy(this);
		retVal.foregroundColor = new Color(this.foregroundColor.getRGB());
		retVal.font = new Font(this.font.getFamily(), this.font.getStyle(), this.font.getSize());
		retVal.speed = this.speed;
		retVal.type = this.type;
		retVal.string = this.string;
		retVal.showOnlyTitles = this.showOnlyTitles;
		retVal.itemSeparator = this.itemSeparator;
		retVal.afterTitle = this.afterTitle;
		retVal.url = this.url;
		retVal.isTransparent = this.isTransparent;
		retVal.refreshInterval = this.refreshInterval;
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
