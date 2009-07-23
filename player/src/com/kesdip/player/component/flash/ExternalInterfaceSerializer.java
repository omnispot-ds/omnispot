package com.kesdip.player.component.flash;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.InputSource;

public class ExternalInterfaceSerializer {
	
	private static DocumentFactory documentFactory = DocumentFactory.getInstance();
	
	/**
	 * Encodes a function call to be sent to flash.
	 * @param functionName The name of the function to call.
	 * @param arguments Zero or more parameters to pass to the ActionScript function.
	 * @return The XML String representation of the function call to pass to Flash.
	 */
	public static String encodeInvoke(String functionName, Object[] arguments) {
		Element invokeElement = DocumentFactory.getInstance().createElement("invoke");
		invokeElement.addAttribute("name", functionName);
		invokeElement.addAttribute("returntype", "xml");
		
		if (arguments != null && arguments.length > 0) {
			Element argsElement = invokeElement.addElement("arguments");
			for (Object argument : arguments) {
				argsElement.add(encodeElement(argument));
			}
		}
		
		return invokeElement.asXML();
	}
	
	/**
	 * Encodes a value to send to Flash as the result of a function call from Flash.
	 * @param value The value to encode.
	 * @return The XML String representation of the value.
	 */
	public static String encodeResult(Object value) {
		Element resultElement = encodeElement(value);
		return resultElement.toString();
	}
	
	/**
	 * Decodes a function call from Flash.
	 * @param xml The XML String representing the function call.
	 * @return An ExternalInterfaceCall object representing the function call.
	 */
	public static ExternalInterfaceCall decodeInvoke(String xml) {
		try {
			Element invokeElement = new SAXReader().read(new InputSource(new StringReader(xml))).getRootElement();
			String functionName = invokeElement.attributeValue("name");
			ExternalInterfaceCall call = new ExternalInterfaceCall(functionName);
			Element argsElement = invokeElement.element("arguments");
			Iterator<?> iter = argsElement.elementIterator();
			while (iter.hasNext()) {
				Element child = (Element) iter.next();
				call.addArgument(decodeElement(child));
			}
			return call;
		} catch (DocumentException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * Decodes the result of a function call to Flash.
	 * @param xml The XML String representing the result.
	 * @return A Java Object containing the result.
	 */
	public static Object DecodeResult(String xml) {
		try {
			Element resultElement = new SAXReader().read(new InputSource(new StringReader(xml))).getRootElement();
			return decodeElement(resultElement);
		} catch (DocumentException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private static Element encodeElement(Object value) {
		Element element = null;
		
		if (value == null) {
			element = documentFactory.createElement("null");
		} else if (value instanceof String) {
			element = documentFactory.createElement("string");
			element.setText(value.toString());
		} else if (value instanceof Boolean) {
			element = documentFactory.createElement(value.toString());
		} else if (value instanceof Short || value instanceof Double || value instanceof Integer || value instanceof Long) {
			element = documentFactory.createElement("number");
			element.setText(value.toString());
		} else if (value instanceof List<?>) {
			element = encodeArray((List<?>) value);
		} else if (value instanceof Map<?, ?>) {
			element = encodeObject((Map<?, ?>) value);
		} else {
			// null is the default when ActionScript can't serialize an object
			element = documentFactory.createElement("null");
		}
		
		return element;
	}
	
	private static Element encodeArray(List<?> array) {
		Element element = documentFactory.createElement("array");
		
		for (int i = 0 ; i < array.size() ; i++) {
			Element child = element.addElement("property");
			child.addAttribute("id", Integer.toString(i));
			child.add(encodeElement(array.get(i)));
		}
		return element;
	}
	
	private static Element encodeObject(Map<?, ?> map) {
		Element element = documentFactory.createElement("object");
		for (Entry<?, ?> entry : map.entrySet()) {
			Element child = element.addElement("property");
			child.addAttribute("id", entry.getKey().toString());
			child.add(encodeElement(entry.getValue()));
		}
		return element;
	}
	
	
	private static Object decodeElement(Element element) {
		String elementName = element.getName();
		if (elementName.equals("true")) {
			return true;
		} else if (elementName.equals("false")) {
			return false;
		} else if (elementName.equals("null")) {
			return null;
		} else if (elementName.equals("number")) {
			return Double.parseDouble(element.getText());
		} else if (elementName.equals("string")) {
			return element.getText();
		} else if (elementName.equals("array")) {
			return decodeArray(element);
		} else if (elementName.equals("object")) {
			return decodeObject(element);
		}
		
		throw new IllegalArgumentException("The input element does not conform to ExternalInterface's xml dialect");
	}
	
	private static List<Object> decodeArray(Element element) {
		List<Object> list = new ArrayList<Object>();
		Iterator<?> iter = element.elementIterator();
		while (iter.hasNext()) {
			Element child = (Element) iter.next();
			list.add(decodeElement(child));
		}
		return list;
	}
	
	private static Map<String, Object> decodeObject(Element element) {
		Map<String, Object> map = new HashMap<String, Object>();
		Iterator<?> iter = element.elementIterator();
		while (iter.hasNext()) {
			Element child = (Element) iter.next();
			String id = child.attributeValue("id");
			map.put(id, decodeElement(child));
		}
		return map;
	}
	
	

}
