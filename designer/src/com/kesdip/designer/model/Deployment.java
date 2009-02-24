package com.kesdip.designer.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kesdip.designer.utils.DOMHelpers;
import com.kesdip.designer.utils.DesignerLog;

public class Deployment extends ModelElement {

	private static final long serialVersionUID = -2386076166432510134L;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/alt_window_16.gif");

	/** Property ID to use when a layout is added to this deployment. */
	public static final String LAYOUT_ADDED_PROP = "Deployment.LayoutAdded";
	/** Property ID to use when a layout is removed from this deployment. */
	public static final String LAYOUT_REMOVED_PROP = "Deployment.LayoutRemoved";

	/** 
	 * A static array of property descriptors.
	 * There is one IPropertyDescriptor entry per editable property.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	private static IPropertyDescriptor[] descriptors;
	/** Property ID to use for the width. */
	public static final String WIDTH_PROP = "Deployment.Width";
	/** Property ID to use for the height. */
	public static final String HEIGHT_PROP = "Deployment.Height";
	/** Property ID to use for the bit depth. */
	public static final String BIT_DEPTH_PROP = "Deployment.BitDepth";
	/** Property ID to use for the deployment ID. */
	public static final String ID_PROP = "Deployment.ID";
	/** Property ID to use for the start time. */
	public static final String START_TIME_PROP = "Deployment.StartTime";

	/* STATE */
	private List<Layout> layoutList;
	private int width;
	private int height;
	private int bit_depth;
	private String id;
	private Date startTime;
	
	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(WIDTH_PROP, "Width"),
				new TextPropertyDescriptor(HEIGHT_PROP, "Height"),
				new TextPropertyDescriptor(BIT_DEPTH_PROP, "Bit Depth"),
				new TextPropertyDescriptor(ID_PROP, "ID"),
				new TextPropertyDescriptor(START_TIME_PROP, "Start Time")
		};
		// use a custom cell editor validator for all array entries
		for (int i = 0; i < descriptors.length; i++) {
			if (descriptors[i].getId().equals(ID_PROP)) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						// No validation for the ID.
						return null;
					}
				});
			} else if (descriptors[i].getId().equals(START_TIME_PROP)) {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						try {
							sdf.parse((String) value);
						} catch (ParseException exc) {
							return "Not a valid date";
						}
						return null;
					}
				});
			} else {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						int intValue = -1;
						try {
							intValue = Integer.parseInt((String) value);
						} catch (NumberFormatException exc) {
							return "Not a number";
						}
						return (intValue >= 0) ? null : "Value must be >=  0";
					}
				});
			}
		}
	} // static
	
	public Deployment() {
		layoutList = new ArrayList<Layout>();
		id = "";
		startTime = new Date();
	}
	
	public void serialize(OutputStream os) throws ParserConfigurationException,
			TransformerException, IOException {
		DocumentBuilderFactory bFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = bFactory.newDocumentBuilder();
		Document doc = builder.newDocument();
		
		// beans element (root)
		Element beansElement = doc.createElement("beans");
		beansElement.setAttribute("xmlns", "http://www.springframework.org/schema/beans");
		beansElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		beansElement.setAttribute("xsi:schemaLocation",
				"http://www.springframework.org/schema/beans  " +
				"  http://www.springframework.org/schema/beans/spring-beans-2.5.xsd");
		doc.appendChild(beansElement);
		
		// deploymentSettings
		Element deploymentSettingsElement = doc.createElement("bean");
		deploymentSettingsElement.setAttribute("id", "deploymentSettings");
		deploymentSettingsElement.setAttribute("class",
				"com.kesdip.player.DeploymentSettings");
		DOMHelpers.addProperty(doc, deploymentSettingsElement,
				"width", String.valueOf(width));
		DOMHelpers.addProperty(doc, deploymentSettingsElement,
				"height", String.valueOf(height));
		DOMHelpers.addProperty(doc, deploymentSettingsElement,
				"bitDepth", String.valueOf(bit_depth));
		DOMHelpers.addProperty(doc, deploymentSettingsElement, "id", id);
		Element startTimeElement = DOMHelpers.addProperty(
				doc, deploymentSettingsElement, "startTime");
		Element dateElement = doc.createElement("bean");
		dateElement.setAttribute("class", "java.util.Date");
		Element constructorArgElement = doc.createElement("constructor-arg");
		constructorArgElement.setAttribute("type", "long");
		constructorArgElement.setAttribute("value", String.valueOf(startTime.getTime()));
		dateElement.appendChild(constructorArgElement);
		startTimeElement.appendChild(dateElement);
		beansElement.appendChild(deploymentSettingsElement);
		
		// deploymentContents
		Element deploymentContentsElement = doc.createElement("bean");
		deploymentContentsElement.setAttribute("id", "deploymentContents");
		deploymentContentsElement.setAttribute("class",
				"com.kesdip.player.DeploymentContents");
		Element propertyNode = DOMHelpers.addProperty(
				doc, deploymentContentsElement, "layouts");
		Element listNode = doc.createElement("list");
		propertyNode.appendChild(listNode);
		int count = 1;
		for (Layout l : layoutList) {
			Element layoutNode = l.serialize(doc, count++);
			listNode.appendChild(layoutNode);
		}
		beansElement.appendChild(deploymentContentsElement);
		
		// Now pass the doc through an identity transform to write out to the output stream.
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // pretty print
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(os);
		transformer.transform(source, result);
	}
	
	public void deserialize(InputStream is)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(is);
		final List<Layout> newLayoutList = new ArrayList<Layout>();
		NodeList nl = doc.getDocumentElement().getElementsByTagName("bean");
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (DOMHelpers.checkAttribute(n, "id", "deploymentSettings")) {
				setPropertyValue(WIDTH_PROP, DOMHelpers.getSimpleProperty(n, "width"));
				setPropertyValue(HEIGHT_PROP, DOMHelpers.getSimpleProperty(n, "height"));
				setPropertyValue(BIT_DEPTH_PROP, DOMHelpers.getSimpleProperty(n, "bitDepth"));
				setPropertyValue(ID_PROP, DOMHelpers.getSimpleProperty(n, "id"));
				setPropertyValue(START_TIME_PROP, sdf.format(DOMHelpers.getDateProperty(n, "startTime")));
			} else if (DOMHelpers.checkAttribute(n, "id", "deploymentContents")) {
				DOMHelpers.applyToListProperty(doc, n, "layouts", "bean",
						new DOMHelpers.INodeListVisitor() {
					@Override
					public void visitListItem(Document doc, Node listItem) {
						Layout childLayout = new Layout();
						childLayout.deserialize(doc, listItem);
						newLayoutList.add(childLayout);
					}
				});
			}
		}
		layoutList = newLayoutList;
	}
	
	public void checkEquivalence(Deployment other) {
		assert(width == other.width);
		assert(height == other.height);
		assert(bit_depth == other.bit_depth);
		assert(id == other.id);
		assert(startTime == other.startTime);
		for (int i = 0; i < layoutList.size(); i++) {
			Layout thisLayout = layoutList.get(i);
			Layout otherLayout = other.layoutList.get(i);
			thisLayout.checkEquivalence(otherLayout);
		}
	}
	
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

	/**
	 * Return the property value for the given propertyId, or null.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array 
	 * to obtain the value of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public Object getPropertyValue(Object propertyId) {
		if (HEIGHT_PROP.equals(propertyId)) {
			return Integer.toString(height);
		}
		if (WIDTH_PROP.equals(propertyId)) {
			return Integer.toString(width);
		}
		if (BIT_DEPTH_PROP.equals(propertyId)) {
			return Integer.toString(bit_depth);
		}
		if (ID_PROP.equals(propertyId)) {
			return id;
		}
		if (START_TIME_PROP.equals(propertyId)) {
			return sdf.format(startTime);
		}
		return super.getPropertyValue(propertyId);
	}

	/**
	 * Set the property value for the given property id.
	 * If no matching id is found, the call is forwarded to the superclass.
	 * <p>The property view uses the IDs from the IPropertyDescriptors array to set the values
	 * of the corresponding properties.</p>
	 * @see #descriptors
	 * @see #getPropertyDescriptors()
	 */
	public void setPropertyValue(Object propertyId, Object value) {
		if (HEIGHT_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(height);
			height = Integer.parseInt((String) value);
			firePropertyChange(HEIGHT_PROP, oldValue, value);
		} else if (WIDTH_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(width);
			width = Integer.parseInt((String) value);
			firePropertyChange(WIDTH_PROP, oldValue, value);
		} else if (BIT_DEPTH_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(bit_depth);
			bit_depth = Integer.parseInt((String) value);
			firePropertyChange(BIT_DEPTH_PROP, oldValue, value);
		} else if (ID_PROP.equals(propertyId)) {
			String oldValue = id;
			id = (String) value;
			firePropertyChange(ID_PROP, oldValue, value);
		} else if (START_TIME_PROP.equals(propertyId)) {
			try {
				String oldValue = sdf.format(startTime);
				startTime = sdf.parse((String) value);
				firePropertyChange(START_TIME_PROP, oldValue, value);
			} catch (ParseException e) {
				DesignerLog.logError("Unable to convert: " + value + " to a date.", e);
			}
		} else {
			super.setPropertyValue(propertyId, value);
		}
	}

	/** 
	 * Add a layout to this deployment.
	 * @param s a non-null layout instance
	 * @return true, iff the layout was added, false otherwise
	 */
	public boolean addLayout(Layout s) {
		if (s != null && layoutList.add(s)) {
			s.setDeployment(this);
			firePropertyChange(LAYOUT_ADDED_PROP, null, s);
			return true;
		}
		return false;
	}

	/** Return a List of layouts in this deployment.  The returned List should not be modified. */
	public List<Layout> getLayouts() {
		return layoutList;
	}

	/**
	 * Remove a layout from this deployment.
	 * @param s a non-null layout instance;
	 * @return true, iff the layout was removed, false otherwise
	 */
	public boolean removeLayout(Layout s) {
		if (s != null && layoutList.remove(s)) {
			s.setDeployment(null);
			firePropertyChange(LAYOUT_REMOVED_PROP, null, s);
			return true;
		}
		return false;
	}
	
	public ModelElement deepCopy() {
		Deployment retVal = new Deployment();
		retVal.height = this.height;
		retVal.width = this.width;
		retVal.bit_depth = this.bit_depth;
		retVal.id = this.id;
		retVal.startTime = new Date(this.startTime.getTime());
		for (Layout srcl : this.layoutList) {
			Layout l = (Layout) srcl.deepCopy();
			retVal.layoutList.add(l);
		}
		return retVal;
	}
	
	public void setDeployment(Deployment deployment) {
		// Intentionally empty.
	}

	public Deployment getDeployment() {
		return this;
	}
	
	public ModelElement removeChild(ModelElement child) {
		if (child instanceof Layout) {
			if (removeLayout((Layout) child))
				return this;
			return null;
		}
		
		for (Layout l : layoutList) {
			ModelElement e = l.removeChild(child);
			if (e != null)
				return e;
		}
		
		return null;
	}
	
	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return "Deployment: " + id;
	}
	
}
