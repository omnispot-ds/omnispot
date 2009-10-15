package com.kesdip.designer.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.draw2d.geometry.Dimension;
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

import com.kesdip.designer.properties.DatePropertyDescriptor;
import com.kesdip.designer.properties.DimensionPropertySource;
import com.kesdip.designer.utils.DOMHelpers;

public class Deployment extends ModelElement {

	private static final long serialVersionUID = -2386076166432510134L;
	
	/** A 16x16 pictogram of an elliptical shape. */
	private static final Image IMAGE_ICON = createImage("icons/palette.png");

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
	/** Property ID to use for the size. */
	public static final String SIZE_PROP = "Deployment.Size";
	/** Property ID to use for the bit depth. */
	public static final String BIT_DEPTH_PROP = "Deployment.BitDepth";
	/** Property ID to use for the deployment ID. */
	public static final String ID_PROP = "Deployment.ID";
	/** Property ID to use for the start time. */
	public static final String START_TIME_PROP = "Deployment.StartTime";
	/** Property ID to use for the sleep interval. */
	public static final String SLEEP_INTERVAL_PROP = "Deployment.SleepInterval";

	/* STATE */
	private List<ModelElement> layoutList;
	private Dimension size = new Dimension(0, 0);
	private DimensionPropertySource dimensionPropertySource =
		new DimensionPropertySource(size, this);
	private int bit_depth;
	private String id;
	private Date startTime;
	private int sleepInterval = 50;
	
	/**
	 * Return the Size of this shape.
	 * @return a non-null Dimension instance
	 */
	public Dimension getSize() {
		return size.getCopy();
	}

	/*
	 * Initializes the property descriptors array.
	 * @see #getPropertyDescriptors()
	 * @see #getPropertyValue(Object)
	 * @see #setPropertyValue(Object, Object)
	 */
	static {
		descriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(SIZE_PROP, "Size"),
				new TextPropertyDescriptor(BIT_DEPTH_PROP, "Bit Depth"),
				new PropertyDescriptor(ID_PROP, "ID"),
				new DatePropertyDescriptor(START_TIME_PROP, "Start Time"),
				new TextPropertyDescriptor(SLEEP_INTERVAL_PROP, "Sleep Interval")
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
						// No validation for the time.
						return null;
					}
				});
			} else if (descriptors[i].getId().equals(BIT_DEPTH_PROP)) {
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
			} else if (descriptors[i].getId().equals(SLEEP_INTERVAL_PROP)) {
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
			} else {
				((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
					public String isValid(Object value) {
						// No validation for the size.
						return null;
					}
				});
			}
		}
	} // static
	
	public Deployment() {
		layoutList = new ArrayList<ModelElement>();
		id = UUID.randomUUID().toString();
		startTime = new Date();
	}
	
	public void serialize(OutputStream os, boolean isPublish) throws ParserConfigurationException,
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
				"width", String.valueOf(size.width));
		DOMHelpers.addProperty(doc, deploymentSettingsElement,
				"height", String.valueOf(size.height));
		DOMHelpers.addProperty(doc, deploymentSettingsElement,
				"bitDepth", String.valueOf(bit_depth));
		if (sleepInterval != 50)
			DOMHelpers.addProperty(doc, deploymentSettingsElement,
					"sleepInterval", String.valueOf(sleepInterval));
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
		for (ModelElement e : layoutList) {
			Layout l = (Layout) e;
			Element layoutNode = l.serialize(doc, count++, isPublish);
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
		final List<ModelElement> newLayoutList = new ArrayList<ModelElement>();
		NodeList nl = doc.getDocumentElement().getElementsByTagName("bean");
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if (DOMHelpers.checkAttribute(n, "id", "deploymentSettings")) {
				setPropertyValue(WIDTH_PROP, DOMHelpers.getSimpleProperty(n, "width"));
				setPropertyValue(HEIGHT_PROP, DOMHelpers.getSimpleProperty(n, "height"));
				setPropertyValue(BIT_DEPTH_PROP, DOMHelpers.getSimpleProperty(n, "bitDepth"));
				setPropertyValue(ID_PROP, DOMHelpers.getSimpleProperty(n, "id"));
				setPropertyValue(START_TIME_PROP, DOMHelpers.getDateProperty(n, "startTime"));
				String sleepIntervalString = DOMHelpers.getSimpleProperty(n, "sleepInterval");
				if (sleepIntervalString != null) {
					setPropertyValue(SLEEP_INTERVAL_PROP, sleepIntervalString);
				}
			} else if (DOMHelpers.checkAttribute(n, "id", "deploymentContents")) {
				DOMHelpers.applyToListProperty(doc, n, "layouts", "bean",
						new DOMHelpers.INodeListVisitor() {
					@Override
					public void visitListItem(Document doc, Node listItem) {
						Layout childLayout = new Layout();
						childLayout.deserialize(doc, listItem);
						childLayout.setParent(Deployment.this);
						newLayoutList.add(childLayout);
					}
				});
			}
		}
		layoutList = newLayoutList;
	}
	
	public void checkEquivalence(Deployment other) {
		assert(size.width == other.size.width);
		assert(size.height == other.size.height);
		assert(bit_depth == other.bit_depth);
		assert(id == other.id);
		assert(startTime == other.startTime);
		assert(sleepInterval == other.sleepInterval);
		for (int i = 0; i < layoutList.size(); i++) {
			Layout thisLayout = (Layout) layoutList.get(i);
			Layout otherLayout = (Layout) other.layoutList.get(i);
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
			return Integer.toString(size.height);
		} else if (WIDTH_PROP.equals(propertyId)) {
			return Integer.toString(size.width);
		} else if (SIZE_PROP.equals(propertyId)) {
			return dimensionPropertySource;
		} else if (BIT_DEPTH_PROP.equals(propertyId)) {
			return Integer.toString(bit_depth);
		} else if (ID_PROP.equals(propertyId)) {
			return id;
		} else if (START_TIME_PROP.equals(propertyId)) {
			return startTime;
		} else if (SLEEP_INTERVAL_PROP.equals(propertyId)) {
			return Integer.toString(sleepInterval);
		} else {
			return super.getPropertyValue(propertyId);
		}
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
			String oldValue = String.valueOf(size.height);
			size.height = Integer.parseInt((String) value);
			firePropertyChange(HEIGHT_PROP, oldValue, value);
		} else if (WIDTH_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(size.width);
			size.width = Integer.parseInt((String) value);
			firePropertyChange(WIDTH_PROP, oldValue, value);
		} else if (SIZE_PROP.equals(propertyId)) {
			setSize((Dimension) value);
		} else if (BIT_DEPTH_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(bit_depth);
			bit_depth = Integer.parseInt((String) value);
			firePropertyChange(BIT_DEPTH_PROP, oldValue, value);
		} else if (ID_PROP.equals(propertyId)) {
			String oldValue = id;
			id = (String) value;
			firePropertyChange(ID_PROP, oldValue, value);
		} else if (START_TIME_PROP.equals(propertyId)) {
			Date oldValue = startTime;
			startTime = (Date) value;
			firePropertyChange(START_TIME_PROP, oldValue, value);
		} else if (SLEEP_INTERVAL_PROP.equals(propertyId)) {
			String oldValue = String.valueOf(sleepInterval);
			sleepInterval = Integer.parseInt((String) value);
			firePropertyChange(SLEEP_INTERVAL_PROP, oldValue, value);
		} else {
			super.setPropertyValue(propertyId, value);
		}
	}
	
	private void setSize(Dimension d) {
		if (d != null) {
			Dimension oldSize = size.getCopy();
			size.setSize(d);
			firePropertyChange(SIZE_PROP, oldSize, d);
		}
	}

	@Override
	public ModelElement deepCopy() {
		Deployment retVal = new Deployment();
		retVal.size = this.size.getCopy();
		retVal.bit_depth = this.bit_depth;
		retVal.id = this.id;
		retVal.startTime = new Date(this.startTime.getTime());
		for (ModelElement srcl : this.layoutList) {
			Layout l = (Layout) srcl.deepCopy();
			retVal.layoutList.add(l);
		}
		return retVal;
	}
	
	@Override
	public void add(ModelElement child) {
		if (child != null && child instanceof Layout && layoutList.add(child)) {
			child.setParent(this);
			firePropertyChange(LAYOUT_ADDED_PROP, null, child);
		}
	}

	@Override
	public boolean removeChild(ModelElement child) {
		if (child != null && child instanceof Layout && layoutList.remove(child)) {
			child.setParent(null);
			firePropertyChange(LAYOUT_REMOVED_PROP, null, child);
			return true;
		}
		return false;
	}

	@Override
	public List<ModelElement> getChildren() {
		return layoutList;
	}

	@Override
	public void insertChildAt(int index, ModelElement child) {
		if (child != null && child instanceof Layout) {
			child.setParent(this);
			layoutList.add(index, child);
			firePropertyChange(LAYOUT_ADDED_PROP, null, child);
		}
	}

	@Override
	public boolean isFirstChild(ModelElement child) {
		return layoutList.indexOf(child) == 0;
	}

	@Override
	public boolean isLastChild(ModelElement child) {
		return layoutList.indexOf(child) == layoutList.size() - 1 &&
				layoutList.size() != 0;
	}

	@Override
	public boolean moveChildDown(ModelElement child) {
		int index = layoutList.indexOf(child);
		if (index == -1 || index == layoutList.size() - 1)
			return false;
		if (!layoutList.remove(child))
			return false;
		layoutList.add(index + 1, child);
		firePropertyChange(CHILD_MOVE_DOWN, null, child);
		return true;
	}

	@Override
	public boolean moveChildUp(ModelElement child) {
		int index = layoutList.indexOf(child);
		if (index < 1)
			return false;
		if (!layoutList.remove(child))
			return false;
		layoutList.add(index - 1, child);
		firePropertyChange(CHILD_MOVE_UP, null, child);
		return true;
	}

	@Override
	public Image getIcon() {
		return IMAGE_ICON;
	}
	
	public String toString() {
		return id;
	}

	@Override
	public void resizeBy(double x, double y) {
		size.width = (int) (size.width * x);
		size.height = (int) (size.height * y);
		for (ModelElement l : layoutList)
			l.resizeBy(x, y);
	}
	
}
