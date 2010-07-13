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

import com.kesdip.designer.properties.FontPropertyDescriptor;
import com.kesdip.designer.properties.ResourcePropertyDescriptor;
import com.kesdip.designer.utils.DOMHelpers;
import com.kesdip.designer.utils.FontKludge;

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

	private static final String FOREGROUND_COLOR_PROP = "Clock.ForegroundColorProp";
	private static final String DATE_FORMAT_PROP = "Clock.DateFormat";
	private static final String FONT_PROP = "Clock.FontProp";
	public static final String CLOCK_TYPE = "Clock.Type";
	public static final String STRING_ANALOG = "Analog";
	public static final String STRING_DIGITAL = "Digital";



	/* STATE */
	private Resource image;

	private String clockType = STRING_DIGITAL;

	private Color foregroundColor;

	private Font font;

	private String dateFormat;
	/* Transient state */
	private FontData fontData;

	public ClockComponent() {
		image = null;
		font = new Font("Arial", Font.PLAIN, 24);
		foregroundColor = Color.WHITE;
		dateFormat = "EEE, d MMM yyyy HH:mm:ss";
	}

	@Override
	protected Element serialize(Document doc, boolean isPublish) {
		Element clockElement = doc.createElement("bean");
		clockElement.setAttribute("class", "com.kesdip.player.components.clock.Clock");
		super.serialize(doc, clockElement);

		DOMHelpers.addProperty(doc, clockElement, "dateFormat", dateFormat);

		DOMHelpers.addProperty(doc, clockElement, "digital", clockType.equals(STRING_DIGITAL) ? "true"
				: "false");

		Element fontPropElement = DOMHelpers.addProperty(doc, clockElement, "font");
		Element fontElement = doc.createElement("bean");
		fontElement.setAttribute("class", "java.awt.Font");
		fontPropElement.appendChild(fontElement);
		Element constructorArg = doc.createElement("constructor-arg");
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

		Element foreColorPropelement = DOMHelpers.addProperty(
				doc, clockElement, "foregroundColor");
		Element foreColorElement = doc.createElement("bean");
		foreColorElement.setAttribute("class", "java.awt.Color");
		foreColorPropelement.appendChild(foreColorElement);
		constructorArg = doc.createElement("constructor-arg");
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



		//Element contentPropElement = DOMHelpers.addProperty(doc, imageElement, "imageResource");
		//Element resourceElement = image.serialize(doc, isPublish);
		//contentPropElement.appendChild(resourceElement);
		return clockElement;
	}

	@Override
	protected void deserialize(Document doc, Node componentNode) {
		super.deserialize(doc, componentNode);
		setPropertyValue(CLOCK_TYPE, DOMHelpers.getSimpleProperty(
				componentNode, "digital").equals("true")?1:0);

		setPropertyValue(DATE_FORMAT_PROP, DOMHelpers.getSimpleProperty(componentNode, "dateFormat"));
		Color bc = DOMHelpers.getColorProperty(componentNode, "foregroundColor");
		setPropertyValue(FONT_PROP, assureFontExists(
				DOMHelpers.getFontProperty(componentNode, "font")));
		setPropertyValue(FOREGROUND_COLOR_PROP,
				new RGB(bc.getRed(), bc.getGreen(), bc.getBlue()));

		//Node contentPropNode = DOMHelpers.getPropertyNode(componentNode, "imageResource");
		//		image = new Resource("", "");
		//		image.deserialize(doc, contentPropNode);
	}

	public void save(IMemento memento) {
		super.save(memento);
		memento.putString(TAG_CLOCK_TYPE, clockType);
		memento.putString(TAG_FONT_NAME, font.getFamily());
		memento.putInteger(TAG_FONT_STYLE, font.getStyle());
		memento.putInteger(TAG_FONT_SIZE, font.getSize());
		memento.putInteger(TAG_FRONT_RED, foregroundColor.getRed());
		memento.putInteger(TAG_FRONT_GREEN, foregroundColor.getGreen());
		memento.putInteger(TAG_FRONT_BLUE, foregroundColor.getBlue());
		memento.putString(TAG_CLOCK_DATE_FORMAT, dateFormat);

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
		clockType = memento.getString(TAG_CLOCK_TYPE);
		dateFormat = memento.getString(TAG_CLOCK_DATE_FORMAT);
		font = new Font(memento.getString(TAG_FONT_NAME),
				memento.getInteger(TAG_FONT_STYLE), memento.getInteger(TAG_FONT_SIZE));
		foregroundColor = new Color(memento.getInteger(TAG_FRONT_RED),
				memento.getInteger(TAG_FRONT_GREEN),
				memento.getInteger(TAG_FRONT_BLUE));
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
		assert(font.equals(((ClockComponent) other).font));
		assert(foregroundColor.equals(((ClockComponent) other).foregroundColor));
		assert(dateFormat.equals(((ClockComponent) other).dateFormat));
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
				new ComboBoxPropertyDescriptor(CLOCK_TYPE, "Type",
						new String[]{STRING_ANALOG,STRING_DIGITAL}),
						new ColorPropertyDescriptor(FOREGROUND_COLOR_PROP, "Foreground Color"),
						new TextPropertyDescriptor(DATE_FORMAT_PROP, "Date Format"),
						new FontPropertyDescriptor(FONT_PROP, "Font"),
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
		else if (CLOCK_TYPE.equals(propertyId))
			return getClockType(clockType);
		else if (FONT_PROP.equals(propertyId)) 
			return font;
		else if (FOREGROUND_COLOR_PROP.equals(propertyId))  
			return new RGB(
					foregroundColor.getRed(),
					foregroundColor.getGreen(),
					foregroundColor.getBlue());
		else if (DATE_FORMAT_PROP.equals(propertyId)) 
			return String.valueOf(dateFormat);
		else
			return super.getPropertyValue(propertyId);
	}

	private int getClockType(String t) {
		if (t.equals(STRING_ANALOG))
			return 0;
		else if (t.equals(STRING_DIGITAL))
			return 1;
		else
			throw new RuntimeException("Unknown clock type.");		
	}

	@Override
	public void setPropertyValue(Object propertyId, Object value) {
		if (IMAGE_PROP.equals(propertyId)) {
			Resource oldValue = image;
			image = (Resource) value;
			firePropertyChange(IMAGE_PROP, oldValue, image);
		} else if (CLOCK_TYPE.equals(propertyId)) {
			if (value == null)
				value = 0;
			int v = ((Integer) value).intValue();
			int oldValue = getClockType(clockType);
			if (v == 1)
				clockType = STRING_DIGITAL;
			else 
				clockType = STRING_ANALOG;
			firePropertyChange(CLOCK_TYPE, oldValue, clockType);

		} else if (DATE_FORMAT_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(dateFormat);
			dateFormat = ((String) value);
			firePropertyChange(DATE_FORMAT_PROP, oldValue, dateFormat);
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
		}else {
			super.setPropertyValue(propertyId, value);
		}
	}

	public void relocateChildren(Point moveBy) {
		// Intentionally empty. Component not a container.
	}

	public ModelElement deepCopy() {
		ClockComponent retVal = new ClockComponent();
		retVal.deepCopy(this);
		retVal.clockType = this.clockType;
		retVal.dateFormat = this.dateFormat;
		retVal.foregroundColor = new Color(this.foregroundColor.getRGB());
		retVal.font = new Font(this.font.getFamily(), this.font.getStyle(), this.font.getSize());
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
