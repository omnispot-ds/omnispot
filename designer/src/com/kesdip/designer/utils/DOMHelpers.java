package com.kesdip.designer.utils;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMHelpers {
	public static  void addProperty(Document doc, Element parent,
			String name, String value) {
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
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("property") &&
					child.getAttributes().getNamedItem("name").
						getNodeValue().equals(name)) {
				return child.getAttributes().getNamedItem("value").getNodeValue();
			}
		}
		
		return null;
	}
	
	public static Node getPropertyNode(Node parent, String name) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("property") &&
					child.getAttributes().getNamedItem("name").
						getNodeValue().equals(name)) {
				return child;
			}
		}
		
		return null;
	}
	
	public static Date getDateProperty(Node parent, String name) {
		return getDateChild(getPropertyNode(parent, name));
	}
	
	public static Date getDateChild(Node propertyNode) {
		NodeList children = propertyNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("bean") &&
					child.getAttributes().getNamedItem("class").
						getNodeValue().equals("java.util.Date")) {
				NodeList beanChildren = child.getChildNodes();
				for (int j = 0; j < beanChildren.getLength(); j++) {
					Node beanChild = beanChildren.item(j);
					if (beanChild.getNodeType() == Node.ELEMENT_NODE &&
							beanChild.getNodeName().equals("constructor-arg") &&
							beanChild.getAttributes().getNamedItem("type").
								getNodeValue().equals("long")) {
						String dateValue = beanChild.getAttributes().
							getNamedItem("value").getNodeValue();
						return new Date(Long.parseLong(dateValue));
					}
				}
			}
		}
		
		return null;
	}
	
	public static interface INodeListVisitor {
		void visitListItem(Document doc, Node listItem);
	}
	
	public static void applyToListProperty(Document doc, Node parent, String name,
			String listItemTag, INodeListVisitor visitor) {
		Node propNode = DOMHelpers.getPropertyNode(parent, name);
		NodeList children = propNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE &&
					child.getNodeName().equals("list")) {
				NodeList listChildren = child.getChildNodes();
				for (int j = 0; j < listChildren.getLength(); j++) {
					Node listChild = listChildren.item(j);
					if (listChild.getNodeType() == Node.ELEMENT_NODE &&
							listChild.getNodeName().equals(listItemTag)) {
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
