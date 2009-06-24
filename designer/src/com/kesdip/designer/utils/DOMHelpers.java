package com.kesdip.designer.utils;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMHelpers {
	public static void addProperty(Document doc, Element parent, String name,
			String value) {
		Element propertyElement = doc.createElement("property");
		propertyElement.setAttribute("name", name);
		propertyElement.setAttribute("value", value);
		parent.appendChild(propertyElement);
	}

	public static Element addProperty(Document doc, Element parent, String name) {
		Element propertyElement = doc.createElement("property");
		propertyElement.setAttribute("name", name);
		parent.appendChild(propertyElement);
		return propertyElement;
	}

	public static String getSimpleProperty(Node parent, String name) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals("property")
					&& child.getAttributes().getNamedItem("name")
							.getNodeValue().equals(name)) {
				return child.getAttributes().getNamedItem("value")
						.getNodeValue();
			}
		}

		return null;
	}

	/**
	 * Creates a new <code>./property[name='propertyName']/map/entry[key='key' && value='value']</code>. If the sequence already exists, it
	 * is not re-created.
	 * 
	 * @param parent
	 *            the parent node
	 * @param propertyName
	 *            the name of the property to which the map belongs
	 * @param key
	 *            the key value
	 * @param value
	 *            the entry's value
	 * @return Element the created "entry"
	 */
	public static Element addMapValue(Element parent, String propertyName,
			String key, String value) {
		Element mapNode = getMapNode(parent, propertyName, true);
		Element entryElement = mapNode.getOwnerDocument()
				.createElement("entry");
		entryElement.setAttribute("key", key);
		entryElement.setAttribute("value", value);
		mapNode.appendChild(entryElement);
		return entryElement;
	}

	/**
	 * Returns the value for XPath <code>./map/entry[key='keyValue']</code>.
	 * 
	 * @param parent
	 *            the current node
	 * @param propertyName
	 *            the name of the property containing the map
	 * @param keyValue
	 *            the value of the key
	 * @return String the value or <code>null</code>
	 */
	public static String getMapValue(Node parent, String propertyName,
			String keyValue) {
		Node map = getMapNode(parent, propertyName, false);
		if (map == null) {
			return null;
		}
		NodeList entries = map.getChildNodes();
		// locate "entry"
		for (int i = 0; i < entries.getLength(); i++) {
			Node entry = entries.item(i);
			if (entry.getNodeType() == Node.ELEMENT_NODE
					&& entry.getAttributes().getNamedItem("key").getNodeValue()
							.equals(keyValue)) {
				return entry.getAttributes().getNamedItem("value")
						.getNodeValue();
			}
		}

		return null;
	}

	public static Node getPropertyNode(Node parent, String name) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals("property")
					&& child.getAttributes().getNamedItem("name")
							.getNodeValue().equals(name)) {
				return child;
			}
		}

		return null;
	}

	/**
	 * Returns the "map" node which is located under the "parent".
	 * 
	 * @param parent
	 *            the parent node
	 * @param propertyName
	 *            the name of the property containing the map
	 * @param createIfNotPresent
	 *            if <code>true</code>, the node will be created if it does not
	 *            exist
	 * @return Node the node or <code>null</code>
	 */
	public static Element getMapNode(Node parent, String propertyName,
			boolean createIfNotPresent) {
		Node mapNode = null;
		// locate property
		Node propertyNode = getPropertyNode(parent, propertyName);
		if (propertyNode == null && !createIfNotPresent) {
			return null;
		} else if (propertyNode == null && createIfNotPresent) {
			propertyNode = addProperty(parent.getOwnerDocument(),
					(Element) parent, propertyName);
		}
		NodeList children = propertyNode.getChildNodes();
		// locate "map"
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals("map")) {
				mapNode = child;
				break;
			}
		}
		if (mapNode == null && createIfNotPresent) {
			Document doc = parent.getOwnerDocument();
			mapNode = doc.createElement("map");
			propertyNode.appendChild(mapNode);
		}
		return (Element) mapNode;
	}

	public static Date getDateProperty(Node parent, String name) {
		return getDateChild(getPropertyNode(parent, name));
	}

	public static Date getDateChild(Node propertyNode) {
		NodeList children = propertyNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals("bean")
					&& child.getAttributes().getNamedItem("class")
							.getNodeValue().equals("java.util.Date")) {
				NodeList beanChildren = child.getChildNodes();
				for (int j = 0; j < beanChildren.getLength(); j++) {
					Node beanChild = beanChildren.item(j);
					if (beanChild.getNodeType() == Node.ELEMENT_NODE
							&& beanChild.getNodeName()
									.equals("constructor-arg")
							&& beanChild.getAttributes().getNamedItem("type")
									.getNodeValue().equals("long")) {
						String dateValue = beanChild.getAttributes()
								.getNamedItem("value").getNodeValue();
						return new Date(Long.parseLong(dateValue));
					}
				}
			}
		}

		return null;
	}

	public static Color getColorProperty(Node parent, String name) {
		return getColorChild(getPropertyNode(parent, name));
	}

	public static Color getColorChild(Node propertyNode) {
		NodeList children = propertyNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals("bean")
					&& child.getAttributes().getNamedItem("class")
							.getNodeValue().equals("java.awt.Color")) {
				NodeList beanChildren = child.getChildNodes();
				boolean redFound = false;
				int red = 0;
				boolean greenFound = false;
				int green = 0;
				boolean blueFound = false;
				int blue = 0;
				for (int j = 0; j < beanChildren.getLength(); j++) {
					Node beanChild = beanChildren.item(j);
					if (beanChild.getNodeType() == Node.ELEMENT_NODE
							&& beanChild.getNodeName()
									.equals("constructor-arg")
							&& beanChild.getAttributes().getNamedItem("type")
									.getNodeValue().equals("int")) {
						String value = beanChild.getAttributes().getNamedItem(
								"value").getNodeValue();
						int intValue = Integer.parseInt(value);
						if (!redFound) {
							red = intValue;
							redFound = true;
						} else if (!greenFound) {
							green = intValue;
							greenFound = true;
						} else if (!blueFound) {
							blue = intValue;
							blueFound = true;
							return new Color(red, green, blue);
						}
					}
				}
			}
		}

		return null;
	}

	public static Font getFontProperty(Node parent, String name) {
		return getFontChild(getPropertyNode(parent, name));
	}

	public static Font getFontChild(Node propertyNode) {
		NodeList children = propertyNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals("bean")
					&& child.getAttributes().getNamedItem("class")
							.getNodeValue().equals("java.awt.Font")) {
				NodeList beanChildren = child.getChildNodes();
				boolean familyFound = false;
				String family = "";
				boolean styleFound = false;
				int style = 0;
				boolean sizeFound = false;
				int size = 0;
				for (int j = 0; j < beanChildren.getLength(); j++) {
					Node beanChild = beanChildren.item(j);
					if (beanChild.getNodeType() == Node.ELEMENT_NODE
							&& beanChild.getNodeName()
									.equals("constructor-arg")
							&& beanChild.getAttributes().getNamedItem("type")
									.getNodeValue().equals("java.lang.String")) {
						String value = beanChild.getAttributes().getNamedItem(
								"value").getNodeValue();
						if (!familyFound) {
							family = value;
							familyFound = true;
						} else {
							throw new RuntimeException(
									"Unexpected second string found "
											+ "in font constructor.");
						}
					} else if (beanChild.getNodeType() == Node.ELEMENT_NODE
							&& beanChild.getNodeName()
									.equals("constructor-arg")
							&& beanChild.getAttributes().getNamedItem("type")
									.getNodeValue().equals("int")) {
						String value = beanChild.getAttributes().getNamedItem(
								"value").getNodeValue();
						int intValue = Integer.parseInt(value);
						if (!styleFound) {
							style = intValue;
							styleFound = true;
						} else if (!sizeFound) {
							size = intValue;
							sizeFound = true;
							if (!familyFound)
								throw new RuntimeException(
										"Malformed font constuctor.");
							return new Font(family, style, size);
						}
					}
				}
			}
		}

		return null;
	}

	public static interface INodeListVisitor {
		void visitListItem(Document doc, Node listItem);
	}

	public static void applyToListProperty(Document doc, Node parent,
			String name, String listItemTag, INodeListVisitor visitor) {
		Node propNode = DOMHelpers.getPropertyNode(parent, name);
		NodeList children = propNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& child.getNodeName().equals("list")) {
				NodeList listChildren = child.getChildNodes();
				for (int j = 0; j < listChildren.getLength(); j++) {
					Node listChild = listChildren.item(j);
					if (listChild.getNodeType() == Node.ELEMENT_NODE
							&& listChild.getNodeName().equals(listItemTag)) {
						visitor.visitListItem(doc, listChild);
					}
				}
			}
		}
	}

	public static boolean checkAttribute(Node n, String name, String value) {
		if (n.getAttributes().getNamedItem(name) == null)
			return false;
		String actual = n.getAttributes().getNamedItem(name).getNodeValue();
		if (value == null)
			return false;
		return value.equals(actual);
	}
}
